/**
 * JimBot - Java IM Bot
 * Copyright (C) 2006-2009 JimBot project
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.jimbot.modules.http;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.jimbot.Manager;
import ru.jimbot.modules.MsgStatCounter;
import ru.jimbot.modules.chat.ChatCommandProc;
import ru.jimbot.modules.chat.ChatServer;
import ru.jimbot.table.UserPreference;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 * ������� �������� ����, �������� ������ �� ��������� �������
 * @author Prolubnikov Dmitry
 */
public class MainPage extends HttpServlet {
	private String userID=""; // �� ������
	private long dt = 0; // ����� ������ ������
	Class self;
    Class methodParamTypes[];
    private long lastErrLogin = 0;
    private int loginErrCount = 0;
//    int objectCacheSizeLimit = 300000;

    @Override
    public void init() throws ServletException {
    	self = getClass();
        methodParamTypes = new Class[1];
        userID = SrvUtil.getSessionId();
        try {
			methodParamTypes[0] = Class.forName("ru.jimbot.modules.http.HttpConnection");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
            throw new ServletException(e.getMessage());
		}
        Log.http("init MainPage");
    }
    
    @Override
    public void destroy() {
        Log.http("destroy MainPage");
    }
    
    private boolean checkSession(String id){
    	boolean f = (System.currentTimeMillis()-dt)<MainProps.getIntProperty("http.delay")*60000;
    	dt = System.currentTimeMillis();
    	return id.equals(userID) && f;
    }
    
    /**
     * ��������� ��������� ������ ��� �����������
     * @param con
     * @throws Exception
     */
    public void login(HttpConnection con) throws Exception {
        if(loginErrCount>MainProps.getIntProperty("http.maxErrLogin"))
            if((System.currentTimeMillis()-lastErrLogin) < (60000*MainProps.getIntProperty("http.timeBlockLogin"))) return;
        String name = con.get("name");
        String pass = con.get("password");
        if (SrvUtil.getAuth(name, pass)!=1) {
            loginErrCount++;
            lastErrLogin = System.currentTimeMillis();
            if((System.currentTimeMillis()-lastErrLogin) > (60000*MainProps.getIntProperty("http.timeErrLogin"))) loginErrCount=0;
        	SrvUtil.error(con, "Incorrect password");
            return;
        }
        loginErrCount=0;
        userID = SrvUtil.getSessionId();
        dt = System.currentTimeMillis();
        con.addPair("uid", userID);
//        con.addPair("person", userID);
        main_page(con);
    }
    
    /**
     * ��������� ����� ��� �������������� �������� ����
     * @param p
     * @return
     */
    private String prefToHtml(UserPreference[] p) {
    	String s = "<TABLE>";
    	for(int i=0;i<p.length;i++){
    		if(p[i].getType()==UserPreference.CATEGORY_TYPE){
    			s += "<TR><TH ALIGN=LEFT><u>" + p[i].getDisplayedKey() + "</u></TD></TR>";
    		} else if(p[i].getType()==UserPreference.BOOLEAN_TYPE) {
    			s += "<TR><TH ALIGN=LEFT>"+p[i].getDisplayedKey()+ "</TD> " +
    			"<TD><INPUT TYPE=CHECKBOX NAME=\"" + p[i].getKey() +
    			"\" VALUE=\"true\" " + ((Boolean)p[i].getValue() ? "CHECKED" : "") + "></TD></TR>";
    		} else {
    			s += "<TR><TH ALIGN=LEFT>"+p[i].getDisplayedKey()+ "</TD> " +
    					"<TD><div class=\"field\"><INPUT class=\"container\" size=\"70\" TYPE=text NAME=\"" + p[i].getKey() +
    					"\" VALUE=\"" + p[i].getValue() + "\"></div></TD></TR>";
    		}
    	}
    	s += "</TABLE>";
    	return s;
    }
    
    /**
     * �������� �����������
     * @param con
     * @throws IOException
     */
    public void loginForm(HttpConnection con) throws IOException {
        con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                  "<H2>������ ���������� �����</H2>" +
                  "<b>���� ��� ������������:</b>" +
                  "<FORM METHOD=POST ACTION=\"" + con.getURI() +
                  "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"login\">" +
                  "<TABLE><TR><TH ALIGN=LEFT>User:</TD>" +
                  "<TD><INPUT TYPE=text NAME=\"name\" SIZE=32></TD></TR>" +
                  "<TR><TH ALIGN=LEFT>Password:</TD>" +
                  "<TD><INPUT TYPE=password NAME=\"password\" SIZE=32></TD></TR></TABLE><P>" +
                  "<INPUT TYPE=submit VALUE=\"Login\"></FORM></BODY></HTML>");
    }
    
    /**
     * ������� �������� ������ ����������. ������������ ����� �����������
     * @param con
     * @throws IOException
     */
    public void main_page(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	dt = System.currentTimeMillis();
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY);
        con.print("<b><FONT COLOR=\"#006400\">" + MainProps.VERSION + "</FONT></b>");
        con.print("<H2>������ ���������� �����</H2>");
        /*if(MainProps.getStringProperty("http.user").equals("admin") &&
        MainProps.getStringProperty("http.pass").equals("admin"))
        con.print("<H3><FONT COLOR=\"#FF0000\">� ����� ������������ ��� ����� ������ �������� " +
        "����������� ����� � ������ ��� ������� � ���� ��������! ������������� ����� �������� ����.</FONT></H3>");*/
    	if(MainProps.checkNewVersion()){
    	    con.print("<p>�� ����� <A HREF=\"http://jimbot.ru\">jimbot.ru</A> �������� ����� ������!<br>");
    	    con.print(MainProps.getNewVerDesc().replaceAll("\n", "<BR>"));
    	    con.print("</p>");
    	}
    	con.print("<H3>������� ����</H3>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=main_props\">" +
    			"�������� ���������</A><br>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_manager\">" +
    			"���������� ���������</A><br>");
    	String s = "<TABLE>";
    	for(String n:Manager.getInstance().getServiceNames()){
    		s += "<TR><TH ALIGN=LEFT>"+n+"</TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    			"&page=srvs_props&ns="+n+"\">��������� �������</A></TD>";
            s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +
    			"&page=srvs_other&ns="+n+"\">������ ���������</A></TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
				"&page=srvs_props_uin&ns="+n+"\">��������� UIN</A></TD>";
    		if(Manager.getInstance().getService(n) instanceof ChatServer){
    		    s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    		        "&page=user_group_props&ns=" + n + "\">����������</A></TD>";
    		} else
    		    s += "<TD> </TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
			"&page=srvs_stats&ns="+n+"\">����������</A></TD>";
    		if(Manager.getInstance().getService(n).isRun){
    			s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    				"&page=srvs_stop&ns="+n+"\">Stop</A></TD>";
    		} else {
    			s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
				"&page=srvs_start&ns="+n+"\">Start</A></TD>";
    		}
    		s += "</TR>";
    	}
    	s += "</TABLE>";
    	con.print(s);
        /*con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=help\">" + "������</A>");*/
    	con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=stop_bot\">" + "��������� ����</A>");
    	con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=restart_bot\">" + "������������� ����</A>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * ��������� ����
     * @param con
     * @throws IOException
     */
    public void stop_bot(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	Manager.getInstance().exit();
    	printOkMsg(con,"main_page");
    }
    
    public void restart_bot(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"������ �����������!");
            return;
        }
        Manager.restart();
        printMsg(con,"main_page", "���������� ����...");
    }
    
    /**
     * ����� ��������� ������ �������
     * @param con
     * @throws IOException
     */
    public void srvs_stats(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	con.print(SrvUtil.HTML_HEAD + "<meta http-equiv=\"Refresh\" content=\"3; url=" + 
    			con.getURI() + "?uid=" + uid + "&page=srvs_stats&ns="+ ns + "\" />" +
    			"<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3>���������� ������ " + ns + "</H3>");
    	con.print("������� �������� ���������: " + Manager.getInstance().getService(ns).getIneqSize() + "<br>");
    	con.print("������� ��������� ���������: <br>");
    	for(int i=0;i<Manager.getInstance().getService(ns).getProps().uinCount();i++){
    		con.print(">> " + Manager.getInstance().getService(ns).getProps().getUin(i) + 
    				(Manager.getInstance().getService(ns).getIcqProcess(i).isOnLine() ? "  [ ON]  " : "  [OFF]  ") +
    				Manager.getInstance().getService(ns).getIcqProcess(i).getOuteqSize() + 
    				", ������:" + Manager.getInstance().getService(ns).getIcqProcess(i).mq.getLostMsgCount() + "<br>");
    	}
    	con.print("<br>���������� �������� ��������� �� �������:<br>");
    	String s = "<TABLE BORDER=\"1\"><TR><TD>UIN</TD><TD>1 ������</TD><TD>5 �����</TD><TD>60 �����</TD><TD>24 ����</TD><TD>�����</TD></TR>";
    	int c = Manager.getInstance().getService(ns).getProps().uinCount();
    	for(int i=0;i<c;i++){
    		String u = Manager.getInstance().getService(ns).getProps().getUin(i);
    		s += "<TR><TD>" + u +
    			"</TD><TD>" + MsgStatCounter.getElement(u).getMsgCount(MsgStatCounter.M1) +
    			"</TD><TD>" + MsgStatCounter.getElement(u).getMsgCount(MsgStatCounter.M5) +
    			"</TD><TD>" + MsgStatCounter.getElement(u).getMsgCount(MsgStatCounter.M60) +
    			"</TD><TD>" + MsgStatCounter.getElement(u).getMsgCount(MsgStatCounter.H24) +
    			"</TD><TD>" + MsgStatCounter.getElement(u).getMsgCount(MsgStatCounter.ALL) +
    			"</TD></TR>";
    	}
    	s += "</TABLE><FORM>";
    	con.print(s);
    	con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * ������ �������
     * @param con
     * @throws IOException
     */
    public void srvs_start(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	Manager.getInstance().start(ns);
    	printOkMsg(con,"main_page");
//    	main_page(con);
    }
    
    public void srvs_stop(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	Manager.getInstance().stop(ns);
    	printOkMsg(con,"main_page");
//    	main_page(con);
    }
    
    
    /**
     * ���������� ��������� - ��������, ��������.
     * @param con
     * @throws IOException
     */
    public void srvs_manager(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>������ ���������� �����</H2>" +
                "<H3>���������� ���������</H3>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_create\">" +
    			"������� ����� ������</A><br><br>");
    	String s = "<TABLE>";
    	for(String n:Manager.getInstance().getServiceNames()){
    		s += "<TR><TH ALIGN=LEFT>"+n+"</TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    			"&page=srvs_delete&ns="+n+"\">(�������)</A></TD>";
    		s += "</TR>";
    	}
    	s += "</TABLE><FORM>";
    	con.print(s);
    	con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * �������� ��������� �������
     * @param con
     * @throws IOException
     */
    public void srvs_delete(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	Manager.getInstance().delService(ns);
    	MainProps.delService(ns);
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }
    
    /**
     * �������� �������� ������ �������.
     * @param con
     * @throws IOException
     */
    public void srvs_create(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>������ ���������� �����</H2>" +
                "<H3>�������� ������ �������</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
            	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_create_in\">" +
            	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
            	"��� �������: <INPUT TYPE=text NAME=\"ns\" size=\"40\"> <br>" +
            	"��� �������: chat <input type=radio name=\"type\" value=\"chat\"> " +
            	"anek <input type=radio name=\"type\" value=\"anek\">" +
              	"<P><INPUT TYPE=submit VALUE=\"���������\">");
    	con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * ��������� ����� �������� ������ �������
     * @param con
     * @throws IOException
     */
    public void srvs_create_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns");
    	String type = con.get("type");
    	if(ns.equals("")){
    		printMsg(con,"srvs_create","������ ��� �������!");
    		return;
    	}
    	if(Manager.getInstance().getServiceNames().contains(ns)){
    		printMsg(con,"srvs_create","������ � ����� ������ ��� ����������!");
    		return;
    	}
    	if(type==null){
    		printMsg(con,"srvs_create","���������� ������� ��� �������!");
    		return;
    	}
    	Manager.getInstance().addService(ns, type);
    	MainProps.addService(ns, type);
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }
    
    /**
     * �������� � ������ ��� ��������� �����
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>������ ���������� �����</H2>" +
        	"<H3>��������� UIN ��� ������� " + ns + "</H3>");
        con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_props_uin_add&ns="+ns+"\">" +
        	"�������� ����� UIN</A><br>");
        String s = "<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_props_uin_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">";
        for(int i=0;i<Manager.getInstance().getService(ns).getProps().uinCount();i++){
        	s += "UIN" + i + ": " +
				"<INPUT TYPE=text NAME=\"uin_" + i + "\" VALUE=\"" + 
				Manager.getInstance().getService(ns).getProps().getUin(i)+ "\"> : " +
				"<INPUT TYPE=text NAME=\"pass_" + i + "\" VALUE=\"" + 
//				Manager.getInstance().getService(ns).getProps().getPass(i)+
				"\"> " +
				"<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_props_uin_del&ns="+ns+"&cnt=" + i + "\">" +
				"�������</A><br>";
        }
        s += "<P><INPUT TYPE=submit VALUE=\"���������\">";
        con.print(s);
        con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }
    
    /**
     * ��������� ������ ����� �� �����
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	for(int i=0;i<Manager.getInstance().getService(ns).getProps().uinCount();i++){
    		if(!con.get("pass_"+i).equals(""))
    			Manager.getInstance().getService(ns).getProps().setUin(i, con.get("uin_"+i), con.get("pass_"+i));
    	}
    	Manager.getInstance().getService(ns).getProps().save();
    	printOkMsg(con,"main_page");
    }
    
    /**
     * ���������� ������ ����
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin_add(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	Manager.getInstance().getService(ns).getProps().addUin("111", "pass");
    	srvs_props_uin(con);
    }
    
    /**
     * �������� ����
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin_del(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	int i = Integer.parseInt(con.get("cnt"));
    	Manager.getInstance().getService(ns).getProps().delUin(i);
    	srvs_props_uin(con);
    }
    
    /**
     * �������� � ������ �������������� �������� ��������� �������.
     * ��� �������� ������ �� ���� �������.
     * @param con
     * @throws IOException
     */
    public void srvs_props(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>������ ���������� �����</H2>" +
        	"<H3>��������� ������� " + ns + "</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_props_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
        	prefToHtml(Manager.getInstance().getService(ns).getProps().getUserPreference())+
          	"<P><INPUT TYPE=submit VALUE=\"���������\">");
        con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }
    
    /**
     * ���������� �������� ��������� �������
     * @param con
     * @throws IOException
     */
    public void srvs_props_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	UserPreference[] p = Manager.getInstance().getService(ns).getProps().getUserPreference();
    	for(int i=0;i<p.length;i++){
    		if(p[i].getType()==UserPreference.BOOLEAN_TYPE){
    			boolean b = SrvUtil.getBoolVal(con, p[i].getKey());
    			if(b!=(Boolean)p[i].getValue()){
    				p[i].setValue(b);
    				Manager.getInstance().getService(ns).getProps().setBooleanProperty(p[i].getKey(), b);
    			}
    		} else if(p[i].getType()==UserPreference.INTEGER_TYPE){
    			int c = Integer.parseInt(SrvUtil.getStringVal(con, p[i].getKey()));
    			if(c!=(Integer)p[i].getValue()){
    				p[i].setValue(c);
    				Manager.getInstance().getService(ns).getProps().setIntProperty(p[i].getKey(), c);
    			}
    		} else if(p[i].getType()!=UserPreference.CATEGORY_TYPE){
    			String s = SrvUtil.getStringVal(con, p[i].getKey());
    			if(!s.equals((String)p[i].getValue())){
    				p[i].setValue(s);
    				Manager.getInstance().getService(ns).getProps().setStringProperty(p[i].getKey(), s);
    			}
    		}
    	}
    	Manager.getInstance().getService(ns).getProps().save();
    	printOkMsg(con,"main_page");
    }
    
//    public void log_view(HttpConnection con) throws IOException {
//    	String uid = con.get("uid");
//    	if(!checkSession(uid)) {
//    		SrvUtil.error(con,"������ �����������!");
//    		return;
//    	}
//    	dt = System.currentTimeMillis();
//    	
//    }
    
    /**
     * �������� � ������ �������������� �������� �������� ����
     * @param con
     * @throws IOException
     */
    public void main_props(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	dt = System.currentTimeMillis();
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>������ ���������� �����</H2>" +
                "<H3>�������� ��������� ����</H3>");
    	con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
                "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"main_props_in\">" +
                "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
                prefToHtml(MainProps.getUserPreference())+
                "<P><INPUT TYPE=submit VALUE=\"���������\">");
    	con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * ��������� � ���������� ��������
     * @param con
     * @throws IOException
     */
    public void main_props_in(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"������ �����������!");
            return;
        }
        UserPreference[] p = MainProps.getUserPreference();
    	for(int i=0;i<p.length;i++){
    		if(p[i].getType()==UserPreference.BOOLEAN_TYPE){
    			boolean b = SrvUtil.getBoolVal(con, p[i].getKey());
    			if(b!=(Boolean)p[i].getValue()){
    				p[i].setValue(b);
    				MainProps.setBooleanProperty(p[i].getKey(), b);
    			}
    		} else if(p[i].getType()==UserPreference.INTEGER_TYPE){
    			int c = Integer.parseInt(SrvUtil.getStringVal(con, p[i].getKey()));
    			if(c!=(Integer)p[i].getValue()){
    				p[i].setValue(c);
    				MainProps.setIntProperty(p[i].getKey(), c);
    			}
    		} else if(p[i].getType()!=UserPreference.CATEGORY_TYPE){
    			String s = SrvUtil.getStringVal(con, p[i].getKey());
    			if(!s.equals((String)p[i].getValue())){
    				p[i].setValue(s);
    				MainProps.setStringProperty(p[i].getKey(), s);
    			}
    		}
    	}
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }

    /**
     * �������� � ������ �������������� �������� ��������� �������.
     * ��� �������� ������ �� ���� �������.
     * @param con
     * @throws IOException
     */
    public void srvs_other(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>������ ���������� �����</H2>" +
        	"<H3>��������� ������� " + ns + "</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_other_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
        	prefToHtml(Manager.getInstance().getService(ns).getProps().OtherUserPreference())+
          	"<P><INPUT TYPE=submit VALUE=\"���������\">");
        con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }

    /**
     * ���������� �������� ��������� �������
     * @param con
     * @throws IOException
     */
    public void srvs_other_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"������ �����������!");
    		return;
    	}
    	String ns = con.get("ns"); // ��� �������
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"����������� ������ � ����� ������!");
    		return;
    	}
    	UserPreference[] p = Manager.getInstance().getService(ns).getProps().OtherUserPreference();
    	for(int i=0;i<p.length;i++){
    		if(p[i].getType()==UserPreference.BOOLEAN_TYPE){
    			boolean b = SrvUtil.getBoolVal(con, p[i].getKey());
    			if(b!=(Boolean)p[i].getValue()){
    				p[i].setValue(b);
    				Manager.getInstance().getService(ns).getProps().setBooleanProperty(p[i].getKey(), b);
    			}
    		} else if(p[i].getType()==UserPreference.INTEGER_TYPE){
    			int c = Integer.parseInt(SrvUtil.getStringVal(con, p[i].getKey()));
    			if(c!=(Integer)p[i].getValue()){
    				p[i].setValue(c);
    				Manager.getInstance().getService(ns).getProps().setIntProperty(p[i].getKey(), c);
    			}
    		} else if(p[i].getType()!=UserPreference.CATEGORY_TYPE){
    			String s = SrvUtil.getStringVal(con, p[i].getKey());
    			if(!s.equals((String)p[i].getValue())){
    				p[i].setValue(s);
    				Manager.getInstance().getService(ns).getProps().setStringProperty(p[i].getKey(), s);
    			}
    		}
    	}
    	Manager.getInstance().getService(ns).getProps().save();
    	printOkMsg(con,"main_page");
    }

    /*public void help(HttpConnection con) throws IOException {
    String uid = con.get("uid");
    if(!checkSession(uid)) {
    SrvUtil.error(con,"������ �����������!");
    return;
    }
    con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
    "<H2>������ ���������� �����</H2>" +
    "<H3>������ �� ������������:</H3>");
    con.print("<P>#NIK# - ��� ������������</P>");
    con.print("<P>#ID# - �� ������������</P>");
    con.print("<P>#UIN# - ��� ������������</P>");
    con.print("<P>#GROUP# - ������ ������������</P>");
    con.print("<P>#ROOM# - �������� ������� � ������� ����� ������������, ������������ ��� �����</P>");
    con.print("<P>#ROOMID# - �� ������� � ������� ����� ������������, ������������ ��� �����</P>");
    con.print("<P>#BALLID# - ���������� ������ ������������, ������������ ��� �����</P>");
    con.print("<P>#ROOM_IN# - �������� ������� � ������� ������� ������������</P>");
    con.print("<P>#ROOM_IN_ID# - �� ������� � ������� ������� ������������</P>");
    con.print("<P>#TOPIC# - ���� �������, ������������ ��� ����� � ���</P>");
    con.print("<FORM><P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    con.print("</FONT></BODY></HTML>");
    }*/
    
    /**
     * �������� �������� ����� �������������
     * @param con
     * @throws IOException
     */
    public void user_group_props(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"������ �����������!");
            return;
        }
        String ns = con.get("ns"); // ��� �������
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"����������� ������ � ����� ������!");
            return;
        }
        con.print(SrvUtil.HTML_HEAD + "<TITLE> "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>������ ���������� �����</H2>" +
                "<H3>���������� �������� �������������</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
                "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"user_group_props_add\">" +
                "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
                "<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" + ns + "\">" +
                "��� ������: <INPUT TYPE=text NAME=\"gr\" size=\"20\"> " +
                "<INPUT TYPE=submit VALUE=\"������� ����� ������\"></FORM>");
//        con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_group_props_add\">" +
//                "������� ����� ������</A><br><br>");
        String s = "<TABLE>";
        String[] gr = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups").split(";");
        for(int i=0; i<gr.length; i++){
            s += "<TR><TH ALIGN=LEFT>"+gr[i]+"</TD>";
            
            s += i==0 ? "" : "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
                "&page=user_group_props_del&ns="+ns+"&gr=" + gr[i] + "\">(�������)</A></TD>";
            s += "</TR>";
        }
        s += "</TABLE>";
        con.print(s);
        con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_auth_props&ns="+ns+"\">" +
                "������������� ����������</A><br>");
        con.print("<FORM><P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * �������� ����� ������ �������������
     * @param con
     * @throws IOException
     */
    public void user_group_props_add(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"������ �����������!");
            return;
        }
        String ns = con.get("ns"); // ��� �������
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"����������� ������ � ����� ������!");
            return;
        }
        String gr = con.get("gr");
        if(gr.equals("")){
            printMsg(con,"user_group_props","������ ��� ������!");
            return;
        }
        String[] s = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups").split(";");
        for(int i=0;i<s.length;i++){
            if(s[i].equalsIgnoreCase(gr)){
                printMsg(con,"user_group_props","������ � ����� ������ ��� ����������!");
                return;
            }
        }
        Manager.getInstance().getService(ns).getProps().setStringProperty("auth.groups", 
                Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups") + ";" + gr);
        Manager.getInstance().getService(ns).getProps().setStringProperty("auth.group_"+gr,"");
        Manager.getInstance().getService(ns).getProps().save();
        printOkMsg(con,"user_group_props");
    }
        
    public void user_auth_props(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"������ �����������!");
            return;
        }
        String ns = con.get("ns"); // ��� �������
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"����������� ������ � ����� ������!");
            return;
        }
        con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>������ ���������� �����</H2>" +
                "<H3>���������� ������������ �������������</H3>");
        String[] gr = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups").split(";");
        Set<String> au = ((ChatCommandProc)Manager.getInstance().getService(ns).cmd).getAuthObjects().keySet();
        Map m = ((ChatCommandProc)Manager.getInstance().getService(ns).cmd).getAuthObjects();
        HashSet[] grs = new HashSet[gr.length];
        for(int i=0; i<gr.length; i++){
            grs[i] = new HashSet<String>();
            try {
                String[] s = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.group_"+gr[i]).split(";");
                if(s.length>0)
                    for(int j=0;j<s.length; j++)
                        grs[i].add(s[j]);
            } catch (Exception ex) {}
        }
        String s = "<FORM METHOD=POST ACTION=\"" + con.getURI() +
        "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"user_auth_props_in\">" +
        "<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
        "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">";
        s += "<TABLE><tbody><TR style=\"background-color: rgb(217, 217, 200);\"><TH ALIGN=LEFT>";
        for(int i=0;i<gr.length;i++)
            s += "<TD><b><u>" + gr[i] + "</u></b></TD>";
        s += "</TR>";
        for(String ss:au){
            s += "<TR style=\"background-color: rgb(217, 217, 200);\" " +
            		"onmouseover=\"this.style.backgroundColor='#ecece4'\" " +
            		"onmouseout=\"this.style.backgroundColor='#d9d9c8'\">" +
            		"<TH ALIGN=LEFT>" + m.get(ss) + "  [" + ss + "]</TD>";
            for(int i=0; i<gr.length; i++){
                s += "<TD><INPUT TYPE=CHECKBOX NAME=\"" + gr[i] + "_" + ss +
                    "\" VALUE=\"true\" " + (grs[i].contains(ss) ? "CHECKED" : "") + "></TD>";
            }
            s += "</TR>";
        }
        s += "</tbody></TABLE><P><INPUT TYPE=submit VALUE=\"���������\">";
        con.print(s);
        con.print("<P><INPUT TYPE=button VALUE=\"�����\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }
    
    public void user_auth_props_in(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"������ �����������!");
            return;
        }
        String ns = con.get("ns"); // ��� �������
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"����������� ������ � ����� ������!");
            return;
        }
        String[] gr = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups").split(";");
        Set<String> au = ((ChatCommandProc)Manager.getInstance().getService(ns).cmd).getAuthObjects().keySet();
        HashSet[] grs = new HashSet[gr.length];
        for(int i=0; i<gr.length; i++){
            grs[i] = new HashSet<String>();
            try {
                String[] s = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.group_"+gr[i]).split(";");
                if(s.length>0)
                    for(int j=0;j<s.length; j++)
                        grs[i].add(s[j]);
            } catch (Exception ex) {}
        }
        for(int i=0; i<gr.length; i++){
            for(String s:au){
                boolean b = SrvUtil.getBoolVal(con, gr[i] + "_" + s);
                if(b && !grs[i].contains(s))
                    grs[i].add(s);
                else if(!b && grs[i].contains(s))
                    grs[i].remove(s);
            }
        }
        for(int i=0; i<gr.length; i++){
            String s = "";
            for(Object c:grs[i]){
                s += c.toString() + ";";
            }
            s = s.substring(0, s.length()-1);
            Manager.getInstance().getService(ns).getProps().setStringProperty("auth.group_"+gr[i], s);
        }
        Manager.getInstance().getService(ns).getProps().save();
        printOkMsg(con,"user_group_props");
    }
    
    /**
     * ������� ������ �������������
     * @param con
     * @throws IOException
     */
    public void user_group_props_del(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"������ �����������!");
            return;
        }
        String ns = con.get("ns"); // ��� �������
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"����������� ������ � ����� ������!");
            return;
        }
        String gr = con.get("gr");
        if(gr.equals("")){
            printMsg(con,"user_group_props","������ ��� ������!");
            return;
        }
        String s = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups");
        s = s.replace(";"+gr, "");
        Manager.getInstance().getService(ns).getProps().setStringProperty("auth.groups",s);
        Manager.getInstance().getService(ns).getProps().setStringProperty("auth.group_"+gr,"");
        Manager.getInstance().getService(ns).getProps().save();
        printOkMsg(con,"user_group_props");
    }
    
    public void printOkMsg(HttpConnection con,String pg) throws IOException {
        String ns = con.get("ns");
        ns = ns==null ? "" : "&ns="+ns;
    	con.print(SrvUtil.HTML_HEAD + "<meta http-equiv=\"Refresh\" content=\"3; url=" + 
    			con.getURI() + "?uid=" + userID + "&page="+ pg + ns + "\" />" +
                "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                "������ ������� ��������� </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + userID + "&page=" + 
    			pg + ns + "\">" + "�����</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    public void printMsg(HttpConnection con, String pg, String msg) throws IOException {
        String ns = con.get("ns");
        ns = ns.equals("") ? "" : "&ns="+ns;
        con.print(SrvUtil.HTML_HEAD +
                "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                msg + " </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + userID + "&page=" + 
    			pg + ns +"\">" + "�����</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {
    	doGetOrPost(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {
    	doGetOrPost(request, response);
    }
    
    void doGetOrPost(HttpServletRequest request, HttpServletResponse response) throws
    IOException, ServletException {
    	response.setContentType("text/html; charset=\"utf-8\"");
    	Log.http("HTTP LOG: " + request.getRemoteAddr()+"("+request.getRemoteHost()+") "+ 
    			request.getQueryString());
    	HttpConnection con = new HttpConnection(request, response);
    	String page = request.getParameter("page");
    	if (page == null) {
    		loginForm(con);
    	} else {
    		try {
    			Method method = self.getMethod(page, methodParamTypes);
    			Object methodParams[] = new Object[1];
    			methodParams[0] = con;
    			try {
    				method.invoke(this, methodParams);
    			} catch (InvocationTargetException x) {
    				Throwable tx = x.getTargetException();
    				tx.printStackTrace();
    				SrvUtil.error(con,
    						"Exception " + tx.getClass().getName() + ": " +
    						tx.getMessage());
    			} catch (Exception x) {
    				SrvUtil.error(con,
    						"Exception " + x.getClass().getName() + ": " +
    						x.getMessage());
    			}
    		} catch (NoSuchMethodException x) {
    			SrvUtil.error(con, "No such method: " + page);
    		}
    	}
    	con.send();
    }

}
