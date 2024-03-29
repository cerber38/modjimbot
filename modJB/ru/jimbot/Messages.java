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
private Properties props = null;

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
            ChatServer srv = ( ChatServer ) Manager.getInstance().getService( ServiceName );
            String msg = getNewBundle().getString(key);
            msg = msg.replace("%NICK%", uss.localnick);// Ник пользователя который набрал команду
            msg = msg.replace("%ID%", Integer.toString(uss.id));// Ид пользователя который набрал команду
            msg = msg.replace("%ROOM_ID%", Integer.toString(room));// Ид комнаты куда переходим
            msg = msg.replace("%ROOM_NAME%", srv.us.getRoom(room).getName());// Название комнаты куда переходим
            msg = msg.replace("%ROOM_TOPIC%", (srv.us.getRoom(room).getTopic().equals("") ? "" : ("\nТема: " + srv.us.getRoom(room).getTopic())));// Тема комнаты куда переходим
            msg = msg.replace("%ROOM_USERS%", Integer.toString(((ChatCommandProc)srv.cmd).AllUsersRoom(room)));// Количество пользователей в комнате куда переходим
            String[] time = ChatProps.getInstance(ServiceName).getStringProperty("vic.game.time").split(";");
            msg = msg.replace("%VIC_GAME_TIME_0%", time[0]);
            msg = msg.replace("%VIC_GAME_TIME_1%", time[1]);
            msg = msg.replace("%VIC_USERS_COUNT%", Integer.toString(ChatProps.getInstance(ServiceName).getIntProperty("vic.users.cnt")));
            return msg;
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

        public synchronized String getString_goChat(String key, int room, Users uss) {
        try {
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
            msg = msg.replace("%CLAN%", getClan(uss) );// Клан пользователя
            msg = msg.replace("%WEDDING%", (uss.wedding == 0 ? "" : ("В браке с |" + srv.us.getUser(uss.wedding).id + "|" + srv.us.getUser(uss.wedding).localnick)) );// Брак пользователя
            msg = msg.replace("%ROOM_ID%", Integer.toString(room));// Ид комнаты в которую вошли
            msg = msg.replace("%ROOM_NAME%", srv.us.getRoom(room).getName());// Название комнаты в которую вошли
            msg = msg.replace("%ROOM_TOPIC%", (srv.us.getRoom(room).getTopic().equals("") ? "" : ("\nТема: " + srv.us.getRoom(room).getTopic())));// Тема комнаты в которую вошли
            Integer cnt_room = (((ChatCommandProc)srv.cmd).AllUsersRoom(room) == -1 ? 0 : (((ChatCommandProc)srv.cmd).AllUsersRoom(room)));
            msg = msg.replace("%ROOM_USERS%", Integer.toString(cnt_room));// Количество пользователей в комнате в которую вошли
            msg = msg.replace("%CHAT_NAME%", ChatProps.getInstance(ServiceName).getStringProperty("chat.name"));// Название чата
            msg = msg.replace("%ROOM_PRISON_ID%", Integer.toString(ChatProps.getInstance(ServiceName).getIntProperty("room.tyrma")));// Ид комнаты тюрьмы
            msg = msg.replace("%ROOM_PRISON_NAME%", srv.us.getRoom(ChatProps.getInstance(ServiceName).getIntProperty("room.tyrma")).getName());// Название комнаты тюрьмы
            String[] time = ChatProps.getInstance(ServiceName).getStringProperty("vic.game.time").split(";");
            msg = msg.replace("%VIC_GAME_TIME_0%", time[0]);
            msg = msg.replace("%VIC_GAME_TIME_1%", time[1]);
            msg = msg.replace("%VIC_USERS_COUNT%", Integer.toString(ChatProps.getInstance(ServiceName).getIntProperty("vic.users.cnt")));
            if(ChatProps.getInstance(ServiceName).getBooleanProperty("shop2.on.off")){
            msg = msg.replace("%CAR%", uss.car);
            msg = msg.replace("%HOME%", uss.home);
            msg = msg.replace("%CLOTHING%", uss.clothing);
            msg = msg.replace("%ANIMAL%", uss.animal);
            }
            if(ChatProps.getInstance(ServiceName).getBooleanProperty("social.status.on.off"))
            msg = msg.replace("%SOCIAL_STATUS%", srv.us.getStatus(uss.id));
            msg = msg.replace("%NOTICE%", Integer.toString(uss.notice));
            msg = msg.replace("%TEXT_IN%", srv.us.getTextInOut("in"));          
            return msg;
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
        /**
         * Вернет клан пользователя
         * @param uss
         * @return
         */


        private String getClan(Users uss){
        String clan = "";
        ChatServer srv = (ChatServer) Manager.getInstance().getService(ServiceName);
        String clan_symbol = "";
        if(srv.us.getClan(uss.clansman).getSymbol() == null) srv.us.getClan(uss.clansman).setSymbol("");
        clan_symbol = srv.us.getClan(uss.clansman).getSymbol().equals("") || !ChatProps.getInstance(ServiceName).getBooleanProperty("Clan.Symbol") ? "" : "(" + srv.us.getClan(uss.clansman).getSymbol() + ")";
        if(uss.clansman != 0)
        clan += (uss.id != srv.us.getClan(uss.clansman).getLeader() ? "Состаит в клане - ''" + srv.us.getClan(uss.clansman).getName() + " " + clan_symbol + "''" : ("Лидер клана - ''" + srv.us.getClan(uss.clansman).getName() + " " + clan_symbol + "''"));
        else
        clan += "В клане не состаит";
        return clan;
        }

         public synchronized String getString_exitChat(String key, Users uss) {
        try {
           ChatServer srv = ( ChatServer ) Manager.getInstance().getService( ServiceName );
            String msg = getNewBundle().getString(key);
            msg = msg.replace("%NICK%", uss.localnick);// Ник пользователя который набрал команду
            msg = msg.replace("%ID%", Integer.toString(uss.id));// Ид пользователя который набрал команду
            msg = msg.replace("%TEXT_OUT%", srv.us.getTextInOut("out"));
            return msg;
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
         }

    public synchronized String getString(String key) {
        try {
            return getNewBundle().getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    public synchronized String getString(String key, Object[] arg) {
        try {
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
            props.keys();
            return null;
        }
    }



}
