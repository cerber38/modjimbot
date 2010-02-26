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

package ru.jimbot.protocol;

import ru.jimbot.modules.MsgOutQueue;

/**
 * Реализация действий протокола
 *
 * @author Prolubnikov Dmitry
 */
public abstract class AbstractProtocol {
    /**
     * Определяем параметры для подключения
     */
    public String server="";
    public int port = 0;
    public boolean useProxy = false;
    public String proxyHost = "";
    public int proxyPort = 0;
    public String proxyUser = "";
    public String proxyPass = "";
    public String screenName = "";
    public String password = "";
    
//    public AbstractConnection con;
    public MsgOutQueue mq;
    public String baseUin="";
    private String basePass="";    
    
    protected ProtocolListener protList;
    
    /**
     * Основные методы
     */
    public void addListener(ProtocolListener p){
        protList = p;
    }
    
    public abstract void connect();
    public abstract void reConnect();
    public abstract void disconnect();
    public abstract void setStatus(int status);
    public abstract boolean isOnLine();
//    public abstract boolean isOnlineStatus(int i);
    public abstract void sendMsg(String sn, String msg);
    public abstract void getMsg(String sendSN, String recivSN, String msg, boolean isOffline);
    public abstract void getStatus(String sn, int status);
    public abstract void addContactList(String sn);
    public abstract void RemoveContactList(String sn);
}
