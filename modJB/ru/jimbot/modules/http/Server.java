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

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;

import org.garret.httpserver.JHttpServer;

import ru.jimbot.util.Log;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class Server {
    static JHttpServer server;
    
    public static void startServer(String args[]) throws Exception {
        File propsFile;
        String cfgFileName = System.getProperty("config");
        if (cfgFileName != null) {
            propsFile = new File(cfgFileName);
            if (!propsFile.exists()) {
                Log.error("Configuration file " + propsFile +
                                   " doesn't exit");
                System.exit(1);
            }
        } else {
            propsFile = new File(JHttpServer.JHTTP_SERVER_PROPS_FILE_NAME);
        }
        Properties serverProperties;
        if (propsFile.exists()) {
            serverProperties = new Properties();
            FileInputStream stream = new FileInputStream(propsFile);
            serverProperties.load(stream);
            stream.close();
        } else {
            serverProperties = System.getProperties();
        }
        Hashtable servletMapping = new Hashtable();
        for (int i = 0; i < args.length; i += 2) {
            String path = args[i];
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            servletMapping.put(path, args[i + 1]);
        }
        server = new JHttpServer(servletMapping, serverProperties);
        Log.info("Start http...");
        /*JOptionPane.showMessageDialog(null, "HTTP сервер запущен.", "Сообщение",
                                      JOptionPane.INFORMATION_MESSAGE);*/
    }

    public static void stopServer() {
        try {
            System.out.println("Shutdown server");
            server.shutdown();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
