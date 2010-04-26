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
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.modules.MsgStatCounter;
import ru.jimbot.modules.chat.ChatCommandProc;
import ru.jimbot.modules.chat.ChatServer;
import ru.jimbot.table.UserPreference;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 * Главная страница бота, содержит ссылки на остальные разделы
 * @author Prolubnikov Dmitry
 */
public class MainPage extends HttpServlet {
	private String userID=""; // ИД сеанса
	private long dt = 0; // Время начала сеанса
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
        Log.getDefault().http("init MainPage");
    }
    
    @Override
    public void destroy() {
        Log.getDefault().http("destroy MainPage");
    }
    
    private boolean checkSession(String id){
    	boolean f = (System.currentTimeMillis()-dt)<MainProps.getIntProperty("http.delay")*60000;
    	dt = System.currentTimeMillis();
    	return id.equals(userID) && f;
    }
    
    /**
     * Обработка введенных данных при авторизации
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
     * Формирует форму для редактирования настроек бота
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
     * Страница авторизации
     * @param con
     * @throws IOException
     */
    public void loginForm(HttpConnection con) throws IOException {
        con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                  "<H2>Панель управления ботом</H2>" +
                  "<b>Вход для пользователя:</b>" +
                  "<FORM METHOD=POST ACTION=\"" + con.getURI() +
                  "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"login\">" +
                  "<TABLE><TR><TH ALIGN=LEFT>User:</TD>" +
                  "<TD><INPUT TYPE=text NAME=\"name\" SIZE=32></TD></TR>" +
                  "<TR><TH ALIGN=LEFT>Password:</TD>" +
                  "<TD><INPUT TYPE=password NAME=\"password\" SIZE=32></TD></TR></TABLE><P>" +
                  "<INPUT TYPE=submit VALUE=\"Login\"></FORM></BODY></HTML>");
    }
    
    /**
     * Главная страница панели управления. Показывается после авторизации
     * @param con
     * @throws IOException
     */
    public void main_page(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	dt = System.currentTimeMillis();
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY);
        con.print("<p align=\"center\"><b><FONT COLOR=\"#006400\">" + MainProps.VERSION + "</FONT></b></p>");
        con.print("<hr><H2>Панель управления ботом</H2>");
        /*if(MainProps.getStringProperty("http.user").equals("admin") &&
        MainProps.getStringProperty("http.pass").equals("admin"))
        con.print("<H3><FONT COLOR=\"#FF0000\">В целях безопасности как можно скорее измените " +
        "стандартный логин и пароль для доступа к этой странице! Рекомендуется также изменить порт.</FONT></H3>");*/
    	if(MainProps.checkNewVersion()){
    	    con.print("<p>На сайте <A HREF=\"http://jimbot.ru\">jimbot.ru</A> Доступна новая версия!<br>");
    	    con.print(MainProps.getNewVerDesc().replaceAll("\n", "<BR>"));
    	    con.print("</p>");
    	}
    	con.print("<H3>Главное меню</H3>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=main_props\">" +
    			"Основные настройки</A><br>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_manager\">" +
    			"Управление сервисами</A><br><br>");
    	String s = "<TABLE>";
    	for(String n:Manager.getInstance().getServiceNames()){
    		s += "<TR><TH ALIGN=LEFT>"+n+"</TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    			"&page=srvs_props&ns="+n+"\">Настройки сервиса</A></TD>";
            s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +
    			"&page=srvs_other&ns="+n+"\">Другие настройки</A></TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
				"&page=srvs_props_uin&ns="+n+"\">Настройки UIN</A></TD>";
    		if(Manager.getInstance().getService(n) instanceof ChatServer){
    		    s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    		        "&page=user_group_props&ns=" + n + "\">Полномочия</A></TD>";
    		} else
    		    s += "<TD> </TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
			"&page=srvs_stats&ns="+n+"\">Статистика</A></TD>";
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
        con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=Tags\">" + "Tags</A>");
    	con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=stop_bot\">" + "Отключить бота</A>");
    	con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=restart_bot\">" + "Перезапустить бота</A>");
        con.print("<hr><br><br>");
        con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Остановка бота
     * @param con
     * @throws IOException
     */
    public void stop_bot(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	Manager.getInstance().exit();
    	printOkMsg(con,"main_page");
    }
    
    public void restart_bot(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"Ошибка авторизации!");
            return;
        }
        Manager.restart();
        printMsgRestart(con,"main_page", "Перезапуск бота...");
    }


        public void printMsgRestart(HttpConnection con, String pg, String msg) throws IOException {
        String ns = con.get("ns");
        ns = ns==null ? "" : "&ns="+ns;
        con.print(SrvUtil.HTML_HEAD +
                "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                msg + " </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + userID + "&page=" +
    			pg + ns +"\">" + "Назад</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }

    /**
     * Вывод статитики работы сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_stats(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
    	con.print(SrvUtil.HTML_HEAD + "<meta http-equiv=\"Refresh\" content=\"3; url=" + 
    			con.getURI() + "?uid=" + uid + "&page=srvs_stats&ns="+ ns + "\" />" +
    			"<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3>Статистика работы " + ns + "</H3>");
    	con.print("Очередь входящих сообщений: " + Manager.getInstance().getService(ns).getIneqSize() + "<br>");
    	con.print("Очередь исходящих сообщений: <br>");
    	for(int i=0;i<Manager.getInstance().getService(ns).getProps().uinCount();i++){
    		con.print(">> " + Manager.getInstance().getService(ns).getProps().getUin(i) + 
    				(Manager.getInstance().getService(ns).getIcqProcess(i).isOnLine() ? "  [ ON]  " : "  [OFF]  ") +
    				Manager.getInstance().getService(ns).getIcqProcess(i).getOuteqSize() + 
    				", потери:" + Manager.getInstance().getService(ns).getIcqProcess(i).mq.getLostMsgCount() + "<br>");
    	}
    	con.print("<br>Статистика принятых сообщений по номерам:<br>");
    	String s = "<TABLE BORDER=\"1\"><TR><TD>UIN</TD><TD>1 минута</TD><TD>5 митут</TD><TD>60 минут</TD><TD>24 часа</TD><TD>Всего</TD></TR>";
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
    	con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Запуск сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_start(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
    	Manager.getInstance().start(ns);
    	printOkMsg(con,"main_page");
//    	main_page(con);
    }
    
    public void srvs_stop(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
    	Manager.getInstance().stop(ns);
    	printOkMsg(con,"main_page");
//    	main_page(con);
    }
    
    
    /**
     * Управление сервисами - создание, удаление.
     * @param con
     * @throws IOException
     */
    public void srvs_manager(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Управление сервисами</H3>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_create\">" +
    			"Создать новый сервис</A><br><br>");
    	String s = "<TABLE>";
    	for(String n:Manager.getInstance().getServiceNames()){
    		s += "<TR><TH ALIGN=LEFT>"+n+"</TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    			"&page=srvs_delete&ns="+n+"\">(Удалить)</A></TD>";
    		s += "</TR>";
    	}
    	s += "</TABLE><FORM>";
    	con.print(s);
    	con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Удаление заданного сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_delete(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
    	Manager.getInstance().delService(ns);
    	MainProps.delService(ns);
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }
    
    /**
     * Страница создания нового сервиса.
     * @param con
     * @throws IOException
     */
    public void srvs_create(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Создание нового сервиса</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
            	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_create_in\">" +
            	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
            	"Имя сервиса: <INPUT TYPE=text NAME=\"ns\" size=\"40\"> <br>" +
            	"Тип сервиса: chat <input type=radio name=\"type\" value=\"chat\"> " +
            	"anek <input type=radio name=\"type\" value=\"anek\">" +
              	"<P><INPUT TYPE=submit VALUE=\"Сохранить\">");
    	con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Обработка формы создания нового сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_create_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns");
    	String type = con.get("type");
    	if(ns.equals("")){
    		printMsg(con,"srvs_create","Пустое имя сервиса!");
    		return;
    	}
    	if(Manager.getInstance().getServiceNames().contains(ns)){
    		printMsg(con,"srvs_create","Сервис с таким именем уже существует!");
    		return;
    	}
    	if(type==null){
    		printMsg(con,"srvs_create","Необходимо выбрать тип сервиса!");
    		return;
    	}
        MainProps.AddDirectory(ns);
        MainProps.AddLogProperties(ns);
    	Manager.getInstance().addService(ns, type);
        Manager.getInstance().getService(ns).getProps().AddXmlConfig(ns);
    	MainProps.addService(ns, type);
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }
    
    /**
     * Страница с формой для настройки уинов
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>Панель управления ботом</H2>" +
        	"<H3>Настройки UIN для сервиса " + ns + "</H3>");
        con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_props_uin_add&ns="+ns+"\">" +
        	"Добавить новый UIN</A><br>");
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
				"Удалить</A><br>";
        }
        s += "<P><INPUT TYPE=submit VALUE=\"Сохранить\">";
        con.print(s);
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }
    
    /**
     * Обработка данных формы об уинах
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
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
     * Добавление нового уина
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin_add(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
    	Manager.getInstance().getService(ns).getProps().addUin("111", "pass");
    	srvs_props_uin(con);
    }
    
    /**
     * Удаление уина
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin_del(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
    	int i = Integer.parseInt(con.get("cnt"));
    	Manager.getInstance().getService(ns).getProps().delUin(i);
    	srvs_props_uin(con);
    }
    
    /**
     * Страница с формой редактирования настроек заданного сервиса.
     * Вид страницы зависи от типа сервиса.
     * @param con
     * @throws IOException
     */
    public void srvs_props(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>Панель управления ботом</H2>" +
        	"<H3>Настройки сервиса " + ns + "</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_props_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
        	prefToHtml(Manager.getInstance().getService(ns).getProps().getUserPreference())+
          	"<P><INPUT TYPE=submit VALUE=\"Сохранить\">");
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }
    
    /**
     * Сохранение настроек заданного сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_props_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
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
//    		SrvUtil.error(con,"Ошибка авторизации!");
//    		return;
//    	}
//    	dt = System.currentTimeMillis();
//    	
//    }
    
    /**
     * Страница с формой редактирования основных настроек бота
     * @param con
     * @throws IOException
     */
    public void main_props(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	dt = System.currentTimeMillis();
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Основные настройки бота</H3>");
    	con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
                "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"main_props_in\">" +
                "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
                prefToHtml(MainProps.getUserPreference())+
                "<P><INPUT TYPE=submit VALUE=\"Сохранить\">");
    	con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Обработка и сохранение настроек
     * @param con
     * @throws IOException
     */
    public void main_props_in(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"Ошибка авторизации!");
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
     * Страница с формой редактирования настроек заданного сервиса.
     * Вид страницы зависи от типа сервиса.
     * @param con
     * @throws IOException
     */
    public void srvs_other(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>Панель управления ботом</H2>" +
        	"<H3>Настройки сервиса " + ns + "</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_other_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
        	prefToHtml(Manager.getInstance().getService(ns).getProps().OtherUserPreference())+
          	"<P><INPUT TYPE=submit VALUE=\"Сохранить\">");
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }

    /**
     * Сохранение настроек заданного сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_other_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
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

    public void Tags(HttpConnection con) throws IOException {
    String uid = con.get("uid");
    if(!checkSession(uid)) {
    SrvUtil.error(con,"Ошибка авторизации!");
    return;
    }
    con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
    "<H3>Tags</H3>");
    con.print("<b>room:</b>");
    con.print("<P>%NICK% - ник пользователя.</P>");
    con.print("<P>%ID% - id пользователя.</P>");
    con.print("<P>%ROOM_ID% - id комнаты куда пользователь переходит.</P>");
    con.print("<P>%ROOM_NAME% - название комнаты куда пользователь переходит.</P>");
    con.print("<P>%ROOM_TOPIC% - тема комнаты куда пользователь переходит.</P>");
    con.print("<P>%ROOM_USERS% - количество пользователей в комнате куда пользователь переходит.</P>");
    con.print("<P>%VIC_GAME_TIME_0% - время начала игры в викторине.</P>");
    con.print("<P>%VIC_GAME_TIME_1% - время конца игры в викторине.</P>");
    con.print("<P>%VIC_USERS_COUNT% - максимальное количество пользователей которое может играть в викторину.</P>");
    con.print("<b>goChat:</b>");
    con.print("<P>%NICK% - ник пользователя.</P>");
    con.print("<P>%ID% - id пользователя.</P>");
    con.print("<P>%ROOM_ID% - id комнаты при входе.</P>");
    con.print("<P>%ROOM_NAME% - название комнаты при входе.</P>");
    con.print("<P>%ROOM_TOPIC% - тема комнаты при входе.</P>");
    con.print("<P>%ROOM_USERS% - количество пользователей в комнате при входе.</P>");
    con.print("<P>%ROOM_PRISON_ID% - id комнаты \"Тюрьмы\" в чате.</P>");
    con.print("<P>%ROOM_PRISON_NAME% - название комнаты \"Тюрьмы\" в чате.</P>");
    con.print("<P>%CHAT_NAME% - название чата.</P>");
    con.print("<P>%NEW_CHAT_UIN% - уин чата т.е. самый свободный при переводе.</P>");
    con.print("<P>%BALL% - рейтинг пользователя.</P>");
    con.print("<P>%DATA% - дата регистрации пользователя.</P>");
    con.print("<P>%GROUP% - группа пользователя.</P>");
    con.print("<P>%NAME% - имя пользователя.</P>");
    con.print("<P>%SEX% - пол пользователя.</P>");
    con.print("<P>%AGE% - возраст пользователя.</P>");
    con.print("<P>%CITY% - город пользователя.</P>");
    con.print("<P>%CLAN% - вернет либо \"Лидер клана |clan_name|\", либо \"Состоит в клане |clan_name| \", либо \"В клане не состоит\".</P>");
    con.print("<P>%WEDDING% - вернет \"В браке с |nick|\", если пользователь в браке</P>");
    con.print("<P>%VIC_GAME_TIME_0% - время начала игры в викторине.</P>");
    con.print("<P>%VIC_GAME_TIME_1% - время конца игры в викторине.</P>");
    con.print("<P>%VIC_USERS_COUNT% - максимальное количество пользователей которое может играть в викторину.</P>");
    con.print("<b>exitChat:</b>");
    con.print("<P>%NICK% - ник пользователя.</P>");
    con.print("<P>%ID% - id пользователя.</P>");
    con.print("<FORM><P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Страница настроек групп пользователей
     * @param con
     * @throws IOException
     */
    public void user_group_props(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"Ошибка авторизации!");
            return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        con.print(SrvUtil.HTML_HEAD + "<TITLE> "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Управление группами пользователей</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
                "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"user_group_props_add\">" +
                "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + userID + "\">" +
                "<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" + ns + "\">" +
                "Имя группы: <INPUT TYPE=text NAME=\"gr\" size=\"20\"> " +
                "<INPUT TYPE=submit VALUE=\"Создать новую группу\"></FORM>");
//        con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_group_props_add\">" +
//                "Создать новую группу</A><br><br>");
        String s = "<TABLE>";
        String[] gr = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups").split(";");
        for(int i=0; i<gr.length; i++){
            s += "<TR><TH ALIGN=LEFT>"+gr[i]+"</TD>";
            
            s += i==0 ? "" : "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
                "&page=user_group_props_del&ns="+ns+"&gr=" + gr[i] + "\">(Удалить)</A></TD>";
            s += "</TR>";
        }
        s += "</TABLE>";
        con.print(s);
        con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_auth_props&ns="+ns+"\">" +
                "Редактировать полномочия</A><br>");
        con.print("<FORM><P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Добавить новую группу пользователей
     * @param con
     * @throws IOException
     */
    public void user_group_props_add(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"Ошибка авторизации!");
            return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        String gr = con.get("gr");
        if(gr.equals("")){
            printMsg(con,"user_group_props","Пустое имя группы!");
            return;
        }
        String[] s = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups").split(";");
        for(int i=0;i<s.length;i++){
            if(s[i].equalsIgnoreCase(gr)){
                printMsg(con,"user_group_props","Группа с таким именем уже существует!");
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
            SrvUtil.error(con,"Ошибка авторизации!");
            return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Управление полномочиями пользователей</H3>");
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
        s += "</tbody></TABLE><P><INPUT TYPE=submit VALUE=\"Сохранить\">";
        con.print(s);
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }
    
    public void user_auth_props_in(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"Ошибка авторизации!");
            return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
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
     * Удалить группу пользователей
     * @param con
     * @throws IOException
     */
    public void user_group_props_del(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        if(!checkSession(uid)) {
            SrvUtil.error(con,"Ошибка авторизации!");
            return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        String gr = con.get("gr");
        if(gr.equals("")){
            printMsg(con,"user_group_props","Пустое имя группы!");
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
                "Данные успешно сохранены </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + userID + "&page=" + 
    			pg + ns + "\">" + "Назад</A><br>");
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
    			pg + ns +"\">" + "Назад</A><br>");
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
    	Log.getDefault().http("HTTP LOG: " + request.getRemoteAddr()+"("+request.getRemoteHost()+") "+
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
