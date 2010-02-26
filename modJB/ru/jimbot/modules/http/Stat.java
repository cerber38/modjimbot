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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.jimbot.modules.anek.AnekServer;
import ru.jimbot.modules.chat.ChatServer;
import ru.jimbot.util.Log;

/**
 * Выводит статистику работы бота
 * @author Prolubnikov Dmitry
 */
public class Stat extends HttpServlet {
    private static ChatServer chat = null;
    private static AnekServer anek = null;
    private static long start = 0;

    public static void setAnek(AnekServer anek) {
        Stat.anek = anek;
        start = System.currentTimeMillis();
    }

    public static void setChat(ChatServer chat) {
        Stat.chat = chat;
        start = System.currentTimeMillis();
    }
    
    @Override
    public void init() throws ServletException {
        Log.http("init Stat");
    }
    
    @Override
    public void destroy() {
        Log.http("destroy Stat");
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {
        response.setContentType("text/html");
        HttpConnection con = new HttpConnection(request, response);
        con.print(SrvUtil.HTML_HEAD+"<TITLE>JimBot статистика</TITLE></HEAD>"+SrvUtil.BODY);
        con.print("<H1><FONT COLOR=\"#004000\">" +
                  SrvUtil.encodeHTML("Если вы видите это сообщение, значит HTTP сервер JimBot работает нормально :)") + 
                  "</FONT></H1>");
        con.print("</BODY></HTML>");
        con.send();
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {
//        response.setContentType("text/html");
//        HttpConnection con = new HttpConnection(request, response);
//        con.print(SrvUtil.HTML_HEAD+"<TITLE>Welcome to ChatBot HTTP Server</TITLE></HEAD>"+SrvUtil.BODY);
//        con.print("</BODY></HTML>");
//        con.send();
    }
}
