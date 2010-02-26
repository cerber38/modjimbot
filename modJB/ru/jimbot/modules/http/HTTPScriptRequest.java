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

import ru.jimbot.modules.WorkScript;
import ru.jimbot.util.Log;

/**
 * @author Prolubnikov Dmitry
 *
 */
public class HTTPScriptRequest extends HttpServlet{

    @Override
    public void init() throws ServletException {
        Log.http("init script servlet");
    }
    
    @Override
    public void destroy() {
        Log.http("destroy script servlet");
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
            IOException, ServletException {
        response.setContentType("text/html");
        HttpConnection con = new HttpConnection(request, response);
        
        String name = con.getURI().split("/")[1];
        con = WorkScript.getInstance("").startHTTPScript(name, con);
        con.send();
    }

    
}
