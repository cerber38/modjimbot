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
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.jimbot.Manager;
import ru.jimbot.modules.MsgStatCounter;
import ru.jimbot.modules.chat.ChatCommandProc;
import ru.jimbot.modules.chat.ChatServer;
import ru.jimbot.modules.info.InfoServer;
import ru.jimbot.table.UserPreference;
import ru.jimbot.util.CreateService;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 * Главная страница бота, содержит ссылки на остальные разделы
 * @author Prolubnikov Dmitry, fraer72
 */
public class MainPage extends HttpServlet {
    /*Не авторизованные пользователи*/
    private ConcurrentHashMap <String, NoAuthorization> NoAuthorization = new ConcurrentHashMap<String, NoAuthorization>();
    /*root*/
    private String userID = "";// Ид сессии до авторизации
    private long dt = 0; // Время начала сеанса

    Class self;
    Class methodParamTypes[];

    
    @Override
    public void init() throws ServletException {
    	self = getClass();
        methodParamTypes = new Class[1];
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

    /**
     * Проверка сессии для root`a
     * @param id
     * @return
     */

    private boolean checkSession(String id){
    boolean f = (System.currentTimeMillis()-dt)<MainProps.getIntProperty("http.delay")*60000;
    dt = System.currentTimeMillis();
    return userID.equals(id) && f;
    }

    /**
     * Проверка имени сервиса
     * @param ServiceName
     * @return
     */


    private boolean testServiceName(String ServiceName) {
    return ServiceName.indexOf(" ") >= 0;
    }

    /**
     * Проверка сессия для пользователя
     * @param id
     * @param user
     * @return
     */
    private boolean checkSession_user(String id, String user){
    if(!MainProps.testUser(user)) return false;
    boolean f = (System.currentTimeMillis()-Manager.getInstance().getBeginning(user))<MainProps.getIntProperty("http.delay")*60000;
    Manager.getInstance().setBeginning(user, System.currentTimeMillis());
    return Manager.getInstance().getUid(user).equals(id) && f;
    }

    /**
     * текущеее время для записи
     * @return
     */
    public String timeIn(){
    return MainProps.currentData();
    }

    /**
     * Обработка введенных данных при авторизации
     * @param con
     * @throws Exception
     */
    public void login(HttpConnection con) throws Exception {
     String ip = con.get("ip");
     String name = con.get("name");
     String pass = con.get("password");
     if(!NoAuthorization.containsKey(ip)){
     SrvUtil.error(con, "Error. It is not defined ip the address");
     return;
     }
     NoAuthorization no = NoAuthorization.get(ip);
     Integer loginErrCount = no.loginErrCount;
     long lastErrLogin = no.lastErrLogin;
     if(loginErrCount > MainProps.getIntProperty("http.maxErrLogin"))
     if((System.currentTimeMillis()-lastErrLogin) < (60000*MainProps.getIntProperty("http.timeBlockLogin"))) return;
        if (SrvUtil.getAuth(name, pass) == 0) {
            no.loginErrCount++;
            no.lastErrLogin = System.currentTimeMillis();
            NoAuthorization.put(ip, no);
            if((System.currentTimeMillis()-lastErrLogin) > (60000*MainProps.getIntProperty("http.timeErrLogin"))) loginErrCount=0;
        	SrvUtil.error(con, "Incorrect password");
            return;
        } else if (SrvUtil.getAuth(name, pass) == 1) {// root
        NoAuthorization.remove(ip);
        String uid = "root_"+ SrvUtil.getSessionId();
        userID = uid;
        dt = System.currentTimeMillis();
        con.addPair("uid", userID);
        main_page(con);
        } else if (SrvUtil.getAuth(name, pass) == 2) {// user
        NoAuthorization.remove(ip);
        Manager.getInstance().changeUser_ipAndTime(name, ip, timeIn());
        MainProps.changeUser_ipAndTime(name, ip, timeIn());
        String uid = name + "_" +  SrvUtil.getSessionId();
        Manager.getInstance().setUid(name, uid);
        Manager.getInstance().setBeginning(name, System.currentTimeMillis());
        con.addPair("uid", uid);
        con.addPair("us", name);
        main_page_user(con);
        }
    }
    
    /**
     * Формирует форму для редактирования настроек бота
     * @param p
     * @return
     */
    private String prefToHtml(UserPreference[] p, boolean isUser) {
    	String s = "<TABLE>";
    	for(int i=0;i<p.length;i++){
    		if(p[i].getType()==UserPreference.CATEGORY_TYPE){
    			s += "<TR><TH ALIGN=LEFT><u>" + p[i].getDisplayedKey() + "</u></TD></TR>";
    		} else if(p[i].getType()==UserPreference.BOOLEAN_TYPE) {
                        if(isUser & !p[i].getTestUser()){
                        s += "<TR><TH ALIGN=LEFT>"+p[i].getDisplayedKey()+ "</TD> " +
    			"<TD><INPUT TYPE=CHECKBOX NAME=\"" + p[i].getKey() +  "\" onclick=\"return false;\"" +
    			"\" VALUE=\"true\" " + ((Boolean)p[i].getValue() ? "CHECKED" : "") + "></TD></TR>";
                        } else{
    			s += "<TR><TH ALIGN=LEFT>"+p[i].getDisplayedKey()+ "</TD> " +
    			"<TD><INPUT TYPE=CHECKBOX NAME=\"" + p[i].getKey() +
    			"\" VALUE=\"true\" " + ((Boolean)p[i].getValue() ? "CHECKED" : "") + "></TD></TR>";
                        }
    		} else{
                        if(isUser & !p[i].getTestUser()){
    			s += "<TR><TH ALIGN=LEFT>"+p[i].getDisplayedKey()+ "</TD> " +
    			"<TD><div class=\"field\"><INPUT readonly=\"readonly\" class=\"container\" size=\"70\" TYPE=text NAME=\"" + p[i].getKey() +
    			"\" VALUE=\"" + p[i].getValue() + "\"></div></TD></TR>";
                        }else{
    			s += "<TR><TH ALIGN=LEFT>"+p[i].getDisplayedKey()+ "</TD> " +
    			"<TD><div class=\"field\"><INPUT class=\"container\" size=\"70\" TYPE=text NAME=\"" + p[i].getKey() +
    			"\" VALUE=\"" + p[i].getValue() + "\"></div></TD></TR>";
                        }
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
    public void loginForm(HttpConnection con, String ip) throws IOException {
        con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                  "<H2>Вход в панель управления ботом</H2>" +
                  "<FORM METHOD=POST ACTION=\"" + con.getURI() + "\">" +
                  "<INPUT TYPE=hidden NAME=\"page\" VALUE=\"login\">" +
                  "<INPUT TYPE=hidden NAME=\"ip\" VALUE=\"" + ip + "\">" +
                  "<TABLE><TR><TH ALIGN=LEFT>User:</TD>" +
                  "<TD><INPUT TYPE=text NAME=\"name\" SIZE=32></TD></TR>" +
                  "<TR><TH ALIGN=LEFT>Password:</TD>" +
                  "<TD><INPUT TYPE=password NAME=\"password\" SIZE=32></TD></TR></TABLE><P>" +
                  "<INPUT TYPE=submit VALUE=\"Login\">" +
                  "<p><a href=\"http://www.jimbot.ru\">jimbot</a></p>" +
                  "</FORM></BODY></HTML>");
    }


    public void main_page_user(HttpConnection con) throws IOException {
        String uid = con.get("uid");
        String us = con.get("us");
        Integer a = 1;
        if(!checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        Manager.getInstance().setBeginning(us, System.currentTimeMillis());
        String services = Manager.getInstance().getServicesUser(us);// Доступные пользователю сервисы
        if(services.equals("") || services == null){
        SrvUtil.message(con, "У вас нет прав не на один сервис!");
        return;
        }
        String[] sn = services.split(";");
        String s = "<TABLE>";
        con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY);
        con.print("<b>Вы вошли как пользователь<b><br><br>");
        con.print("<b>Доступные вам сервисы:<b><br><br>");
        for(int i=0; i<sn.length; i++){
    	s += "<TR><TH ALIGN=LEFT>" + a + ") " + sn[i] + "</TD>";
    	s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=srvs_props&ns="+sn[i]+"\">Настройки сервиса</A></TD>";
        if(MainProps.getType(sn[i]).equalsIgnoreCase("Chat")){
        s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=srvs_other&ns="+sn[i]+"\">Другие настройки</A></TD>";
        } else {
        s += "<TD> </TD>";
        }
        if(MainProps.getType(sn[i]).equalsIgnoreCase("Chat")){
    	s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us +  "&page=srvs_messages&ns="+sn[i]+"\">messages.xml</A></TD>";
        } else {
        s += "<TD> </TD>";
        }
    		if(Manager.getInstance().getService(sn[i]) instanceof ChatServer){
    		    s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us +
    		        "&page=user_group_props&ns=" + sn[i] + "\">Полномочия</A></TD>";
    		} else
    		    s += "<TD> </TD>";

            		if(Manager.getInstance().getService(sn[i]).isRun){
                  s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us +
    			"&page=restart_service&ns="+sn[i]+"\">Restart service</A></TD>";
    		} else {
    			s += "<TD>Сервис не запущен!</TD>";
    		}
    		s += "</TR>";
                        a++;
    	}
    	s += "</TABLE>";
    	con.print(s);
        con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=exit\">" + "Exit</A>");
        con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=Tags\">" + "Tags</A>");
        con.print("</FONT></BODY></HTML>");
    }

    /**
     * Главная страница панели управления. Показывается после авторизации
     * @param con
     * @throws IOException
     */
    public void main_page(HttpConnection con) throws IOException {
    	int i = 1;
        String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	dt = System.currentTimeMillis();
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY);
        con.print("<p align=\"center\"><b><FONT COLOR=\"#006400\">" + MainProps.VERSION + "</FONT></b></p>");
        con.print("<hr><H2>Панель управления ботом</H2>");
        con.print("<b>Вы вошли как администратор!<b><br><br>");
    	con.print("<H3>Главное меню</H3>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=main_props\">" +
    			"Основные настройки</A><br>");
    	con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=srvs_manager\">" +
    			"Управление сервисами</A><br>");
        con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=users_manager\">" +
    	"Управление пользователями</A><br><br>");
    	String s = "<TABLE>";
    	for(String n:Manager.getInstance().getServiceNames()){
    		s += "<TR><TH ALIGN=LEFT>" + i + ") " + n + "</TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    			"&page=srvs_props&ns="+n+"\">Настройки сервиса</A></TD>";
            if(MainProps.getType(n).equalsIgnoreCase("Chat")){
            s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    			"&page=srvs_other&ns="+n+"\">Другие настройки</A></TD>";
            } else {
            s += "<TD> </TD>";
            }
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +  
				"&page=srvs_props_uin&ns="+n+"\">Настройки UIN</A></TD>";
            if(MainProps.getType(n).equalsIgnoreCase("Chat")){
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +
				"&page=srvs_messages&ns="+n+"\">messages.xml</A></TD>";
            } else {
            s += "<TD> </TD>";
            }
    		if(Manager.getInstance().getService(n) instanceof ChatServer){
    		    s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    		        "&page=user_group_props&ns=" + n + "\">Полномочия</A></TD>";
    		} else
    		    s += "<TD> </TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
			"&page=srvs_stats&ns="+n+"\">Статистика</A></TD>";
    		if(Manager.getInstance().getService(n).isRun){
                  s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +
    			"&page=restart_service&ns="+n+"\">Restart service</A></TD>";
    			s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
    				"&page=srvs_stop&ns="+n+"\">Stop service</A></TD>";
    		} else {
    			s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + 
				"&page=srvs_start&ns="+n+"\">Start service</A></TD>";
    		}
    		s += "</TR>";
                        i++;
    	}
    	s += "</TABLE>";
    	con.print(s);
        con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=Tags\">" + "Tags</A>");
    	con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=stop_bot\">" + "Отключить бота</A>");
    	con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=restart_bot\">" + "Перезапустить бота</A>");
        con.print("<br><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=exit\">" + "Exit</A>");
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
        if(!checkSession(uid)) {
            SrvUtil.error(con,"Ошибка авторизации!");
            return;
        }
        Manager.restart();
        printMsgRestart(con,"main_page", "Перезапуск бота...");
    }

        public void restart_service(HttpConnection con) throws IOException {
        String ns = con.get("ns");
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        if(us != null & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
        return;
        }
        Manager.getInstance().restartService(ns);
        String page = us == null ? "main_page" : "main_page_user";
        printMsgRestart_service(con,page, "Перезапуск сервиса \"" + ns.replace("&ns=", "") + "\" ...");
    }

        public void printMsgRestart_service(HttpConnection con, String pg, String msg) throws IOException {
        String ns = con.get("ns");
        String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        ns = ns==null ? "" : "&ns="+ns;
        con.print(SrvUtil.HTML_HEAD +
                "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                msg + " </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" +
    			pg + ns +"\">" + "Назад</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }


        public void printMsgRestart(HttpConnection con, String pg, String msg) throws IOException {
        String ns = con.get("ns");
        String uid = con.get("uid");
            	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
        ns = ns==null ? "" : "&ns="+ns;
        con.print(SrvUtil.HTML_HEAD +
                "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                msg + " </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=" +
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
        if(Manager.getInstance().getService(ns).getIcqProcess(0) == null){
        printMsg(con, "main_page", "Нет уинов или сервис не был запущен!");
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
    	printOkMsg_Start(con,"main_page");
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
    	printOkMsg_Stop(con,"main_page");
    }

    /**
     * Форма для редоктирования messages.xml
     * @param con
     * @throws IOException
     */

    public void srvs_messages(HttpConnection con) throws IOException {
    String uid = con.get("uid");
    String us = con.get("us");
    if(!checkSession(uid) & !checkSession_user(uid, us)) {
    SrvUtil.error(con,"Ошибка авторизации!");
    return;
    }
    String ns = con.get("ns"); // Имя сервиса
    if(!Manager.getInstance().getServiceNames().contains(ns)){
    SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    return;
    }
    if(us != null & Manager.getInstance().testServicesUser(us, ns)){
    printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
    return;
    }
    con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
    "<H2>Панель управления ботом</H2>" +
    "<H3>Редактирование Messages.xml</H3>");
    
    MessagesLoad msg = MessagesLoad.getInstance(ns);
    String s = "<TABLE>";
    for(Object m : msg.getKeys()){
    s += "<TR><TH ALIGN=LEFT>"+m+ "</TD> " +
    "<TD><div class=\"field\"><INPUT class=\"container\" size=\"70\" TYPE=text NAME=\"" + m +
    "\" VALUE=\"" + msg.getStringProperty((String)m).replace("\n", "<br>") + "\"></div></TD></TR>";
    }
    s += "</TABLE>";
    con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
    "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_messages_in\">" +
    "<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
    "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" +us + "\">" +
    "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
    s +
    "<P><INPUT TYPE=submit VALUE=\"Сохранить\">");
    String page = us == null || us.equals("null") ? "main_page" : "main_page_user";
    con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" + page + "\"></FORM>");
    con.print("</FONT></BODY></HTML>");
    }

    /**
     * Редактирование messages.xml
     * @param con
     * @throws IOException
     */

    public void srvs_messages_in(HttpConnection con) throws IOException {
    String uid = con.get("uid");
    String us = con.get("us");
    if(!checkSession(uid) & !checkSession_user(uid, us)) {
    SrvUtil.error(con,"Ошибка авторизации!");
    return;
    }
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
    MessagesLoad msg = MessagesLoad.getInstance(ns);
    for(Object m : msg.getKeys()){
    String s = SrvUtil.getStringVal(con, (String)m);
    s = s.replace("<br>", "\n");
    msg.setStringProperty((String)m, s);
    }
    msg.save();
    String page = us.equals("null") || us == null ? "main_page" : "main_page_user";
    printOkMsg(con,page);
    }

    /**
     * Управление пользователями - создание, удаление.
     * @param con
     * @throws IOException
     */
    public void users_manager(HttpConnection con) throws IOException {
    int i = 1;
    String uid = con.get("uid");
    if(!checkSession(uid)) {
    SrvUtil.error(con,"Ошибка авторизации!");
    return;
    }
    con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
    "<H2>Панель управления ботом</H2>" +
    "<H3>Управление пользователями</H3>");
    con.print("<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=users_create\">" +
    "Создать пользователя</A><br><br>");
    String s = "<TABLE>";
    	for(String n:Manager.getInstance().getUsersNames()){
    		s += "<TR><TH ALIGN=LEFT>"+ i + ") " +n+"</TD>";
   		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +
    			"&page=users_info&us="+n+"\"> Информация </A></TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +
    			"&page=users_change&us="+n+"\"> Изменить </A></TD>";
    		s += "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid +
    			"&page=users_delete&us="+n+"\"> Удалить </A></TD>";
    		s += "</TR>";
                i++;
    	}
    	s += "</TABLE><FORM>";
    	con.print(s);
    	con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=main_page\"></FORM>");
    	con.print("</FONT></BODY></HTML>");
    }


    /**
     * Удаление заданного пользователя
     * @param con
     * @throws IOException
     */
    public void users_delete(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String us = con.get("us"); // Имя пользователя
    	if(!Manager.getInstance().getUsersNames().contains(us)){
    		SrvUtil.error(con,"Такого пользователя не существует!");
    		return;
    	}
    	Manager.getInstance().delUsers(us);
    	MainProps.delUser(us);
    	MainProps.save();
    	printOkMsg(con,"users_manager");
    }

    /**
     * Информация о пользователе
     * @param con
     * @throws IOException
     */
    public void users_info(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	String us = con.get("us"); // Имя пользователя
            	if(!Manager.getInstance().getUsersNames().contains(us)){
    		SrvUtil.error(con,"Такого пользователя не существует!");
    		return;
    	}
        String [] Service = Manager.getInstance().getServicesUser(us).split(";");
        String ip = Manager.getInstance().getIpUser(us);
        String time = Manager.getInstance().getTimeUser(us);
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        "<H2>Панель управления ботом</H2><br>" +
        "<b>Информация о пользователе - " + us + "</b>");
        con.print("<br><br>В последний раз заходил: " + time + " , с ip: " + ip);
        con.print("<br><br>Права на сервисы:<br>");
        for(int i=0; i<Service.length; i++){
        con.print(Service[i] + "<br>");
        }
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=users_manager\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }

    /**
     * Страница создания нового пользователя.
     * @param con
     * @throws IOException
     */
    public void users_create(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Создание нового пользователя</H3>");
               String s = "<FORM METHOD=POST ACTION=\"" + con.getURI() + "\">" +
                "<INPUT TYPE=hidden NAME=\"page\" VALUE=\"users_create_in\">" +
            	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
            	"<TABLE>" +
                "<TR><TD>Login:</TD><TD><INPUT TYPE=text NAME=\"login\" size=\"20\"></TD></TR>" +
                "<TR><TD>Пароль:</TD><TD><INPUT TYPE=password NAME=\"pass0\" size=\"20\"></TD></TR>"+
                "<TR><TD>Повторите пароль:</TD><TD><INPUT TYPE=password NAME=\"pass1\" size=\"20\"></TD></TR></TABLE>";
                 s += "<b>Укажите права на сервисы:<b><br>";
                 for(String n:Manager.getInstance().getServiceNames()){
                 s += "<INPUT TYPE=checkbox NAME=\"" + n + "\" VALUE=\"" + n + "\">" + n + "<br>";
                 }
              	s += "<P><INPUT TYPE=submit VALUE=\"Создать\">";
    	s += "<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&page=users_manager\"></FORM>";
    	s += "</FONT></BODY></HTML>";
        con.print(s);
    }

    /**
     * Страница измененния данных пользователя.
     * @param con
     * @throws IOException
     */
    public void users_change(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
        String us = con.get("us"); // Имя пользователя
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Изменить данные пользователя - " + us + "</H3>");
        con.print("<P><INPUT TYPE=button VALUE=\"Изменить пароль\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=users_change_pass\">");
               String s = "<FORM METHOD=POST ACTION=\"" + con.getURI() + "\">" +
                "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" + us + "\">" +
                "<INPUT TYPE=hidden NAME=\"page\" VALUE=\"users_change_in\">" +
            	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">";
                 s += "<b>Укажите права на сервисы:<b><br>";
                 for(String n:Manager.getInstance().getServiceNames()){ 
                 s += "<INPUT TYPE=checkbox NAME=\"" + n + "\" VALUE=\"" + n + "\"" + (!Manager.getInstance().testServicesUser(us, n) ? "CHECKED" : "") + ">" + n + "<br>";
                 }
              	s += "<P><INPUT TYPE=submit VALUE=\"Изменить\">";
    	s += "<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid +  "&page=users_manager\"></FORM>";
    	s += "</FONT></BODY></HTML>";
        con.print(s);
    }


    /**
     * Страница измененния пароля пользователя.
     * @param con
     * @throws IOException
     */
    public void users_change_pass(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
        String us = con.get("us"); // Имя пользователя
    	con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Изменить данные пользователя - " + us + "</H3>");
               String s = "<FORM METHOD=POST ACTION=\"" + con.getURI() + "\">" +
                "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" + us + "\">" +
                "<INPUT TYPE=hidden NAME=\"page\" VALUE=\"users_change_pass_in\">" +
            	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
            	"<TABLE>" +
                "<TR><TD>Новый пароль:</TD><TD><INPUT TYPE=password NAME=\"pass0\" size=\"20\"></TD></TR>"+
                "<TR><TD>Повторите пароль:</TD><TD><INPUT TYPE=password NAME=\"pass1\" size=\"20\"></TD></TR></TABLE>";
              	s += "<P><INPUT TYPE=submit VALUE=\"Изменить\">";
    	s += "<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=users_change\"></FORM>";
    	s += "</FONT></BODY></HTML>";
        con.print(s);
    }

    /**
     * Обработка формы изменения пароля пользователя
     * @param con
     * @throws IOException
     */
    public void users_change_pass_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
        String us = con.get("us");
    	String pass0 = con.get("pass0");
        String pass1 = con.get("pass1");
    	if(!pass0.equalsIgnoreCase(pass1)){
    		printMsg_users(con,"users_create","Пароли не совпадают!");
    		return;
    	}
    	if(us.equals("")){
    		printMsg_users(con,"users_create","Пустое имя пользователя!");
    		return;
    	}
    	Manager.getInstance().changeUserPass(us, pass0);
    	MainProps.changeUserPass(us, pass0);
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }

    /**
     * Обработка формы создания нового пользователя
     * @param con
     * @throws IOException
     */
    public void users_create_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
        String service = "";
    	String login = con.get("login");
    	String pass0 = con.get("pass0");
        String pass1 = con.get("pass1");
    	if(!pass0.equalsIgnoreCase(pass1)){
    		printMsg_users(con,"users_create","Пароли не совпадают!");
    		return;
    	}
        for(String n:Manager.getInstance().getServiceNames()){
        String srv = con.get(n);
        if(service.equals(""))
        service += srv == null ? "" : srv;
        else
        service += srv == null ? "" : ";" + srv;
        }
    	if(login.equals("")){
    		printMsg_users(con,"users_create","Пустое имя пользователя!");
    		return;
    	}
    	if(Manager.getInstance().getUsersNames().contains(login)){
    		printMsg_users(con,"users_create","Такой пользователь уже существует!");
    		return;
    	}
    	Manager.getInstance().addUser("", login, pass0, service,"");
    	MainProps.addUser(login, pass0, service);
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }

  /**
     * Обработка формы изменения данных пользователя
     * @param con
     * @throws IOException
     */
    public void users_change_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
    	if(!checkSession(uid)) {
    		SrvUtil.error(con,"Ошибка авторизации!");
    		return;
    	}
        String service = "";
    	String us = con.get("us");
        for(String n:Manager.getInstance().getServiceNames()){
        String srv = con.get(n);
        if(service.equals(""))
        service += srv == null ? "" : srv;
        else
        service += srv == null ? "" : ";" + srv;
        }
    	if(us.equals("")){
    		printMsg_users(con,"users_create","Пустое имя пользователя!");
    		return;
    	}
    	Manager.getInstance().changeUserService(us, service);
    	MainProps.changeUserService(us, service);
    	MainProps.save();
    	printOkMsg(con,"main_page");
    }

    public void printMsg_users(HttpConnection con, String pg, String msg) throws IOException {
        String uid = con.get("uid");
        con.print(SrvUtil.HTML_HEAD +
                "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                msg + " </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=" +
    			pg +"\">" + "Назад</A><br>");
    	con.print("</FONT></BODY></HTML>");
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
            	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
            	"Имя сервиса: <INPUT TYPE=text NAME=\"ns\" size=\"40\"> <br>" +
            	"Тип сервиса:<br>" +
                "chat <input type=radio name=\"type\" value=\"chat\"><br> " +
            	"anek <input type=radio name=\"type\" value=\"anek\"><br>" +
                "info <input type=radio name=\"type\" value=\"info\">" +
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
        if(testServiceName(ns)){
            printMsg(con,"srvs_create","В имени сервиса не должно быть пробелов!");
            return;
        }
    	if(type==null){
    		printMsg(con,"srvs_create","Необходимо выбрать тип сервиса!");
    		return;
    	}
        MainProps.AddDirectory(ns, type);
        MainProps.AddLogProperties(ns);
        MainProps.uinsAddFile(ns);
        if(!type.equalsIgnoreCase("info"))MainProps.CopyingScript(ns, type);
        if(type.equalsIgnoreCase("Chat")) MainProps.CopyingMessagesXml(ns);
    	Manager.getInstance().addService(ns, type);
        Manager.getInstance().getService(ns).getProps().AddXmlConfig(ns);
    	MainProps.addService(ns, type);
    	MainProps.save();
        if(MainProps.getBooleanProperty("avto.db.on.off") & type.equalsIgnoreCase("Chat")) CreateService.initMySQLService(ns);
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
        con.print("<P><INPUT TYPE=button VALUE=\"Загрузить из txt файла\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&ns=" + ns + "&page=srvs_props_uin_add_txt\"></FORM>");
        String s = "<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_props_uin_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">";
        for(int i=0;i<Manager.getInstance().getService(ns).getProps().uinCount();i++){
        	s += "UIN" + i + ": " +
				"<INPUT TYPE=text NAME=\"uin_" + i + "\" VALUE=\"" + 
				Manager.getInstance().getService(ns).getProps().getUin(i)+ "\"> : " +
				"<INPUT TYPE=text NAME=\"pass_" + i + "\" VALUE=\"" + 
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
     * Добавление уинов из txt файла
     * @param con
     * @throws IOException
     */
    public void srvs_props_uin_add_txt(HttpConnection con) throws IOException {
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
        if(!MainProps.isUins2(ns)) MainProps.uinsAddFile(ns);
        if(MainProps.isUins(ns)){
        printMsg(con, "srvs_props_uin", "Нет уинов в txt файле!");
        return;
        }
        String[] uins = MainProps.loadUinList(ns);
        for(int i=0;i<uins.length;i++){
        String[] a = uins[i].split(";");
    	Manager.getInstance().getService(ns).getProps().addUin(a[0], a[1]);
        }
        MainProps.uinsRecreate(ns);
    	srvs_props_uin(con);
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
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
        if(us != null & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
        return;
        }
        boolean user = false;
        if(us != null) user = true;
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>Панель управления ботом</H2>" +
        	"<H3>Настройки сервиса " + ns + "</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_props_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
                "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" +us + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
        	prefToHtml(Manager.getInstance().getService(ns).getProps().getUserPreference(), user)+
          	"<P><INPUT TYPE=submit VALUE=\"Сохранить\">");
        String page = us == null ? "main_page" : "main_page_user";
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" + page + "\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }
    
    /**
     * Сохранение настроек заданного сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_props_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
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
        String page = us.equals("null") ? "main_page" : "main_page_user";
    	printOkMsg(con,page);
    }
    

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
                "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
                prefToHtml(MainProps.getUserPreference(), false)+
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
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
    	String ns = con.get("ns"); // Имя сервиса
    	if(!Manager.getInstance().getServiceNames().contains(ns)){
    		SrvUtil.error(con,"Отсутствует сервис с таким именем!");
    		return;
    	}
        if(us != null & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
        return;
        }
        boolean user = false;
        if(us != null) user = true;
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        	"<H2>Панель управления ботом</H2>" +
        	"<H3>Настройки сервиса " + ns + "</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
        	"\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"srvs_other_in\">" +
        	"<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" +ns + "\">" +
                "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" +us + "\">" +
        	"<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
        	prefToHtml(Manager.getInstance().getService(ns).getProps().OtherUserPreference(), user)+
          	"<P><INPUT TYPE=submit VALUE=\"Сохранить\">");
        String page = us==null ? "main_page" : "main_page_user";
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" + page + "\"></FORM>");
        con.print("</FONT></BODY></HTML>");

    }

    /**
     * Сохранение настроек заданного сервиса
     * @param con
     * @throws IOException
     */
    public void srvs_other_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
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
        String page = us.equals("null") ? "main_page" : "main_page_user";
    	printOkMsg(con,page);
    }

    /**
     * Форма для тегов в messages.xml
     * @param con
     * @throws IOException
     */

    public void Tags(HttpConnection con) throws IOException {
    String uid = con.get("uid");
    String us = con.get("us");
    if(!checkSession(uid) & !checkSession_user(uid, us)) {
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
    con.print("<P>%CAR% - авто.</P>");
    con.print("<P>%HOME% - дом.</P>");
    con.print("<P>%CLOTHING% - одежда.</P>");
    con.print("<P>%ANIMAL% - животное.</P>");
    con.print("<P>%CLAN% - вернет либо \"Лидер клана |clan_name|\", либо \"Состоит в клане |clan_name| \", либо \"В клане не состоит\".</P>");
    con.print("<P>%WEDDING% - вернет \"В браке с |nick|\", если пользователь в браке</P>");
    con.print("<P>%VIC_GAME_TIME_0% - время начала игры в викторине.</P>");
    con.print("<P>%VIC_GAME_TIME_1% - время конца игры в викторине.</P>");
    con.print("<P>%VIC_USERS_COUNT% - максимальное количество пользователей которое может играть в викторину.</P>");
    con.print("<P>%SOCIAL_STATUS% - социальный статус.</P>");
    con.print("<P>%NOTICE% - количество предупреждений.</P>");
    con.print("<P>%TEXT_IN% - рандомный текст при входе.</P>");
    con.print("<P>%TEXT_OUT% - рандомный текст при выходе..</P>");
    con.print("<b>exitChat:</b>");
    con.print("<P>%NICK% - ник пользователя.</P>");
    con.print("<P>%ID% - id пользователя.</P>");
    String page = us==null ? "main_page" : "main_page_user";
    con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onclick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" + page + "\"></FORM>");
    con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Страница настроек групп пользователей
     * @param con
     * @throws IOException
     */
    public void user_group_props(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        us = us == null ? "null" : us;
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
        return;
        }
        con.print(SrvUtil.HTML_HEAD + "<TITLE> "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H2>Панель управления ботом</H2>" +
                "<H3>Управление группами пользователей</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
                "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"user_group_props_add\">" +
                "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
                "<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" + ns + "\">" +
                "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" +us + "\">" +
                "Имя группы: <INPUT TYPE=text NAME=\"gr\" size=\"20\"> <br>" +
                "Символ группы: <INPUT TYPE=text NAME=\"gr_s\" size=\"20\"> <br>" +
                "<INPUT TYPE=submit VALUE=\"Создать новую группу\"></FORM>");
        String s = "<TABLE>";
        String[] gr = Manager.getInstance().getService(ns).getProps().getStringProperty("auth.groups").split(";");
        for(int i=0; i<gr.length; i++){
            String symbol = Manager.getInstance().getService(ns).getProps().getStringProperty("group.symbol_"+gr[i]);
            symbol = symbol == null ? "" : symbol;
            s += "<TR><TH ALIGN=LEFT>"+ symbol + "  " + gr[i]+"</TD>";
            
            s += i==0 ? "<TD><A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_group_symboll&ns="+ns+"&gr=" + gr[i] + "&us=" + us + "\">(Изменить символ)</A> " +
                "<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_group_symboll_del&ns="+ns+"&gr=" + gr[i] + "&us=" + us + "\">(Убрать символ)</A></TD> " : "<TD>" +
                "<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_group_symboll&ns="+ns+"&gr=" + gr[i] + "&us=" + us + "\">(Изменить символ)</A> " +
                "<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_group_symboll_del&ns="+ns+"&gr=" + gr[i] + "&us=" + us + "\">(Убрать символ)</A> " +
                "<A HREF=\"" + con.getURI() + "?uid=" + uid + "&page=user_group_props_del&ns="+ns+"&gr=" + gr[i] + "&us=" + us + "\">(Удалить группу)</A></TD>";
            s += "</TR>";
        }
        s += "</TABLE>";
        con.print(s);
        con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=user_auth_props&ns="+ns+"\">" +
                "Редактировать полномочия</A><br>");
        String page = us.equals("null") ? "main_page" : "main_page_user";
        con.print("<FORM><P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" + page + "\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }
    
    /**
     * Добавить новую группу пользователей
     * @param con
     * @throws IOException
     */
    public void user_group_props_add(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
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
        Manager.getInstance().getService(ns).getProps().setStringProperty("group.symbol_"+gr, con.get("gr_s"));
        Manager.getInstance().getService(ns).getProps().save();
        printOkMsg(con,"user_group_props");
    }
        
    public void user_auth_props(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
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
        "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" +us + "\">" +
        "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">";
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
        String page = us.equals("null") ? "main_page" : "main_page_user";
        con.print("<P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" + page + "\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }
    
    public void user_auth_props_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
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
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
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
        Manager.getInstance().getService(ns).getProps().setStringProperty("group.symbol_"+gr,"");
        Manager.getInstance().getService(ns).getProps().save();
        printOkMsg(con,"user_group_props");
    }

    public void user_group_symboll(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
        return;
        }
        String gr = con.get("gr");
        con.print(SrvUtil.HTML_HEAD + "<TITLE> "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
        "<H2>Панель управления ботом</H2>" +
        "<H3>Смена символа группы " + gr + "</H3>");
        con.print("<FORM METHOD=POST ACTION=\"" + con.getURI() +
                "\"><INPUT TYPE=hidden NAME=\"page\" VALUE=\"user_group_symboll_in\">" +
                "<INPUT TYPE=hidden NAME=\"uid\" VALUE=\"" + uid + "\">" +
                "<INPUT TYPE=hidden NAME=\"ns\" VALUE=\"" + ns + "\">" +
                "<INPUT TYPE=hidden NAME=\"us\" VALUE=\"" +us + "\">" +
                "<INPUT TYPE=hidden NAME=\"gr\" VALUE=\"" +gr + "\">" +
                "Символ группы: <INPUT TYPE=text NAME=\"gr_s\" size=\"20\"> <br>" +
                "<INPUT TYPE=submit VALUE=\"Сменить символ\"></FORM>");
        con.print("<FORM><P><INPUT TYPE=button VALUE=\"Назад\" onClick=location.href=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&ns=" + ns + "&page=user_group_props\"></FORM>");
        con.print("</FONT></BODY></HTML>");
    }

    /**
     * Сменить символ группы
     * @param con
     * @throws IOException
     */
    public void user_group_symboll_in(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
        return;
        }
        String gr = con.get("gr");
        String sym = con.get("gr_s");
        if(sym.equals("")){
            printMsg(con,"user_group_symboll","Пустой символ!");
            return;
        }
        Manager.getInstance().getService(ns).getProps().setStringProperty("group.symbol_"+gr,sym);
        Manager.getInstance().getService(ns).getProps().save();
        printOkMsg(con,"user_group_props");
    }

    /**
     * Убрать символ группы
     * @param con
     * @throws IOException
     */
    public void user_group_symboll_del(HttpConnection con) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns"); // Имя сервиса
        if(!Manager.getInstance().getServiceNames().contains(ns)){
            SrvUtil.error(con,"Отсутствует сервис с таким именем!");
            return;
        }
        if(!us.equals("null") & Manager.getInstance().testServicesUser(us, ns)){
        printMsg(con, "main_page_user", "У вас нет прав на данный сервис!");
        return;
        }
        String gr = con.get("gr");
        Manager.getInstance().getService(ns).getProps().setStringProperty("group.symbol_"+gr,"");
        Manager.getInstance().getService(ns).getProps().save();
        printOkMsg(con,"user_group_props");
    }
    
    public void printOkMsg(HttpConnection con,String pg) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns");
        ns = ns==null ? "" : "&ns="+ns;
    	con.print(SrvUtil.HTML_HEAD + "<meta http-equiv=\"Refresh\" content=\"3; url=" + 
    			con.getURI() + "?uid=" + uid + "&page="+ pg + ns + "&us=" + us + "\" />" +
                "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                "Данные успешно сохранены </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" +
    			pg + ns + "\">" + "Назад</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }

        public void printOkMsg_Start(HttpConnection con,String pg) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns");
        ns = ns==null ? "" : "&ns="+ns;
    	con.print(SrvUtil.HTML_HEAD + "<meta http-equiv=\"Refresh\" content=\"3; url=" +
    			con.getURI() + "?uid=" + uid + "&page="+ pg + ns + "\" />" +
                "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                "Сервис \"" + ns.replace("&ns=", "") + "\" успешно запущен! </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" +
    			pg + ns + "\">" + "Назад</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }

        public void printOkMsg_Stop(HttpConnection con,String pg) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns");
        ns = ns==null ? "" : "&ns="+ns;
    	con.print(SrvUtil.HTML_HEAD + "<meta http-equiv=\"Refresh\" content=\"3; url=" +
    			con.getURI() + "?uid=" + uid + "&page="+ pg + ns + "\" />" +
                "<TITLE>"+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                "Сервис \"" + ns.replace("&ns=", "") + "\" успешно остановлен! </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" +
    			pg + ns + "\">" + "Назад</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }
    
    public void printMsg(HttpConnection con, String pg, String msg) throws IOException {
    	String uid = con.get("uid");
        String us = con.get("us");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
        String ns = con.get("ns");
        ns = ns.equals("") ? "" : "&ns="+ns;
        con.print(SrvUtil.HTML_HEAD +
                "<TITLE>JimBot "+MainProps.VERSION+" </TITLE></HEAD>" + SrvUtil.BODY +
                "<H3><FONT COLOR=\"#004000\">" +
                msg + " </FONT></H3>");
    	con.print("<P><A HREF=\"" + con.getURI() + "?uid=" + uid + "&us=" + us + "&page=" +
    			pg + ns +"\">" + "Назад</A><br>");
    	con.print("</FONT></BODY></HTML>");
    }

    public void exit(HttpConnection con)throws IOException {
            String us = con.get("us");
            String uid = con.get("uid");
        if(!checkSession(uid) & !checkSession_user(uid, us)) {
        SrvUtil.error(con,"Ошибка авторизации!");
        return;
        }
            if(us == null)
                userID = "";
            else
            Manager.getInstance().setUid(us, "");
        con.print(SrvUtil.HTML_HEAD + "<TITLE>JimBot error</TITLE></HEAD><BODY>");
        con.print("<P><CENTER><A HREF=\"" + con.getURI() + "\">Login</A></CENTER>");
    	con.print("</BODY></HTML>");
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
        /*Провери ip в черном списке?*/
        if(MainProps.isIpIgnor(request.getRemoteAddr().replace("/", ""))) return;
    	response.setContentType("text/html; charset=\"utf-8\"");
    	Log.getDefault().http("HTTP LOG: " + request.getRemoteAddr()+"("+request.getRemoteHost()+") "+  			request.getQueryString());
    	HttpConnection con = new HttpConnection(request, response);
    	String page = request.getParameter("page");
        /*Новый не авторизованный пользователь*/
        if(!NoAuthorization.containsKey(request.getRemoteAddr())){
        NoAuthorization no = new NoAuthorization( 0, 0);
        NoAuthorization.put(request.getRemoteAddr(), no);
        }
    	if (page == null/*|| page.equals("exit")*/) {
    		loginForm(con, request.getRemoteAddr());
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

    class NoAuthorization
    {
    public long lastErrLogin = 0;
    public int loginErrCount = 0;

    NoAuthorization(long lastErrLogin, int loginErrCount)
    {
    this.lastErrLogin = lastErrLogin;
    this.loginErrCount = loginErrCount;
    }
    }

}
