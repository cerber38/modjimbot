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

package ru.jimbot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.chat.ChatCommandProc;
import ru.jimbot.modules.chat.ChatProps;
import ru.jimbot.modules.chat.ChatServer;
import ru.jimbot.modules.chat.Users;

/**
 * Класс управления локализованными текстовыми ресурсами
 * 
 * @author Prolubnikov Dmitriy
 * @author fraer72
 */
public class Messages {
private String Messages_FOLDER = "/msg/";
private String fileName = "";
private String ServiceName = null;
private static ConcurrentHashMap<String,Messages> instances = new ConcurrentHashMap<String,Messages>();
private static Messages mainInst = new Messages("");

    public Messages(String name)
    {
        ServiceName = name;
    }

     /**
     * Возвращает экземпляр Messages для нужного сервиса
     * @param name
     * @return
     */
    public static Messages getInstance(String name){
        if(name == null) return null;
        if(name.equals("")) return mainInst;
        if(!instances.containsKey(name)) instances.put(name, new Messages(name));
        return instances.get(name);
    }

    public  ResourceBundle getNewBundle(){
    	ResourceBundle bundle = null;
    	InputStream stream = null;
    	try {
            fileName = "./services/" + ServiceName + Messages_FOLDER + "messages.xml";
            stream =  new BufferedInputStream(new FileInputStream(new File(fileName)));
        if (stream != null) {
            BufferedInputStream bis = new BufferedInputStream(stream);
            bundle = new XMLResourceBundle(bis);
            bis.close();
        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return bundle;
    }


        public synchronized String getString_Room(String key, int room, Users uss) {
        try {
     //System.out.print("Messages >>> " + getNewBundle().getString(key));
            ChatServer srv = ( ChatServer ) Manager.getInstance().getService( ServiceName );
            String msg = getNewBundle().getString(key);
            msg = msg.replace("%NICK%", uss.localnick);// Ник пользователя который набрал команду
            msg = msg.replace("%ID%", Integer.toString(uss.id));// Ид пользователя который набрал команду
            msg = msg.replace("%ROOM_ID%", Integer.toString(room));// Ид комнаты куда переходим
            msg = msg.replace("%ROOM_NAME%", srv.us.getRoom(room).getName());// Название комнаты куда переходим
            msg = msg.replace("%ROOM_TOPIC%", (srv.us.getRoom(room).getTopic().equals("") ? "" : ("\nТема: " + srv.us.getRoom(room).getTopic())));// Тема комнаты куда переходим
            msg = msg.replace("%ROOM_USERS%", Integer.toString(((ChatCommandProc)srv.cmd).AllUsersRoom(room)));// Количество пользователей в комнате куда переходим
            return msg;
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

        public synchronized String getString_goChat(String key, int room, Users uss) {
        try {
     //System.out.print("Messages >>> " + getNewBundle().getString(key));
            ChatServer srv = ( ChatServer ) Manager.getInstance().getService( ServiceName );
            String msg = getNewBundle().getString(key);
            msg = msg.replace("%NICK%", uss.localnick);// Ник пользователя который набрал команду
            msg = msg.replace("%ID%", Integer.toString(uss.id));// Ид пользователя который набрал команду
            msg = msg.replace("%NEW_CHAT_UIN%", uss.basesn);// Уин на который перевели
            msg = msg.replace("%BALL%", Integer.toString(uss.ball));// Баллы пользователя который набрал команду
            msg = msg.replace("%DATA%", (uss.data==0 ? "Не указанна" : (("|" + new Date(uss.data)).toString() + "|")));// Дата регистрации
            msg = msg.replace("%GROUP%", uss.group);// Группа пользователя
            msg = msg.replace("%NAME%", uss.lname);// Имя пользователя
            msg = msg.replace("%SEX%", uss.homepage);// Пол пользователя
            msg = msg.replace("%AGE%", Integer.toString(uss.age));// Возраст пользователя
            msg = msg.replace("%CITY%", uss.city );// Город пользователя
            msg = msg.replace("%CLAN%", test_clan(uss) );// Клан пользователя
            msg = msg.replace("%WEDDING%", (uss.wedding == 0 ? "" : ("В браке с |" + srv.us.getUser(uss.wedding).id + "|" + srv.us.getUser(uss.wedding).localnick)) );// Брак пользователя
            msg = msg.replace("%ROOM_ID%", Integer.toString(room));// Ид комнаты в которую вошли
            msg = msg.replace("%ROOM_NAME%", srv.us.getRoom(room).getName());// Название комнаты в которую вошли
            msg = msg.replace("%ROOM_TOPIC%", (srv.us.getRoom(room).getTopic().equals("") ? "" : ("\nТема: " + srv.us.getRoom(room).getTopic())));// Тема комнаты в которую вошли
            msg = msg.replace("%ROOM_USERS%", Integer.toString(((ChatCommandProc)srv.cmd).AllUsersRoom(room)));// Количество пользователей в комнате в которую вошли
            msg = msg.replace("%CHAT_NAME%", ChatProps.getInstance(ServiceName).getStringProperty("chat.name"));// Название чата
            msg = msg.replace("%ROOM_PRISON_ID%", Integer.toString(ChatProps.getInstance(ServiceName).getIntProperty("room.tyrma")));// Ид комнаты тюрьмы
            msg = msg.replace("%ROOM_PRISON_NAME%", srv.us.getRoom(ChatProps.getInstance(ServiceName).getIntProperty("room.tyrma")).getName());// Название комнаты тюрьмы
            return msg;
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

        private String test_clan(Users uss){
        String clan = "";
        ChatServer srv = ( ChatServer ) Manager.getInstance().getService( ServiceName );
        if( uss.clansman != 0 )
        {
        clan += (  uss.id != srv.us.getClan( uss.clansman ).getLeader() ? "Состаит в клане - ''" + srv.us.getClan( uss.clansman ).getName() + "''" : ( "Лидер клана - ''" + srv.us.getClan( uss.clansman ).getName() + "''" ) );
        }
        else
        {
        clan += "В клане не состаит";
        }
        return clan;
        }

         public synchronized String getString_exitChat(String key, Users uss) {
        try {
     //System.out.print("Messages >>> " + getNewBundle().getString(key));
            String msg = getNewBundle().getString(key);
            msg = msg.replace("%NICK%", uss.localnick);// Ник пользователя который набрал команду
            msg = msg.replace("%ID%", Integer.toString(uss.id));// Ид пользователя который набрал команду
            return msg;
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
         }

    public synchronized String getString(String key) {
        try {
     //System.out.print("Messages >>> " + getNewBundle().getString(key));
            return getNewBundle().getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    public synchronized String getString(String key, Object[] arg) {
        try {
     //System.out.print("Messages >>> " + java.text.MessageFormat.format(getNewBundle().getString(key), arg));
            return java.text.MessageFormat.format(getNewBundle().getString(key), arg);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    public class XMLResourceBundle extends ResourceBundle {
        private Properties props;
        XMLResourceBundle(InputStream stream) throws IOException {
            props = new Properties();
            props.loadFromXML(stream);
        }
        protected Object handleGetObject(String key) {
            return props.getProperty(key);
        }
        /* (non-Javadoc)
         * @see java.util.ResourceBundle#getKeys()
         */
        @Override
        public Enumeration<String> getKeys() {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
