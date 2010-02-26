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

package ru.jimbot.modules.chat;

import ru.jimbot.db.DBAdaptor;
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.modules.AbstractServer;
import ru.jimbot.modules.MsgInQueue;
import ru.jimbot.modules.UINmanager;
import ru.jimbot.modules.WorkScript;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.MainProps;


/**
 *
 * @author Prolubnikov Dmitry
 */
public class ChatServer extends AbstractServer{
    public ChatConnection con;
    public UserWork us;
    public ChatQueue cq;
    public MsgInQueue inq;
    //public Messages msg;
    private ChatProps props = null;
    
    /** Creates a new instance of ChatServer */

    public ChatServer(String name) {
    	this.setName(name);
    	ChatProps.getInstance(name).load();
        cmd = new ChatCommandProc(this);
//        us = new UserWork(name);
        con = new ChatConnection(this);
        //msg = new Messages(name);
        //msg = new Messages(this);
        con.server = MainProps.getServer();
        con.port = MainProps.getPort();
        con.proxy = MainProps.getProxy();
        String[] icq = new String[ChatProps.getInstance(this.getName()).uinCount()];
        String[] pass = new String[ChatProps.getInstance(this.getName()).uinCount()];
        for(int i=0;i<ChatProps.getInstance(this.getName()).uinCount();i++){
            icq[i] = ChatProps.getInstance(this.getName()).getUin(i);
            pass[i] = ChatProps.getInstance(this.getName()).getPass(i);
        }
        con.uins = new UINmanager(icq, pass, con, 
                ChatProps.getInstance(this.getName()).getBooleanProperty("chat.IgnoreOfflineMsg"), 
                ChatProps.getInstance(this.getName()));
        cq = new ChatQueue(this);
        cq.start();
        inq = new MsgInQueue(cmd);
    }
    
    public void start() {
        us = new UserWork(getName());
    	WorkScript.getInstance(getName()).startScript("start", "", this);
        if(!con.server.equals("")) {
            con.uins.start();
        }
         for(int i=0;i<con.uins.count();i++){
             inq.addReceiver((IcqProtocol)con.uins.proc.get(i));
         }
        inq.start();
        isRun = true;
    }
    
    public void stop() {
    	WorkScript.getInstance(getName()).startScript("stop", "", this);
        closeDB();
        if(!con.server.equals("")) con.uins.stop();
        isRun = false;
    }
    
    public void closeDB(){
        us.closeDB();
    }
    
    public IcqProtocol getIcqProcess(String baseUin) {
        if(!con.server.equals("")) {
            for(int i=0; i<con.uins.count();i++){
                if(con.uins.getUin(i).equalsIgnoreCase(baseUin)) 
                    return (IcqProtocol)con.uins.proc.get(i);
            }
        }
        return null;
    }
    
    public DBAdaptor getDB(){
    	return us.db;
    }
    
    public AbstractProps getProps() {
   	 if(props==null)
   		 props = ChatProps.getInstance(this.getName());
    	return props;
    }
    
    public IcqProtocol getIcqProcess(int baseUin) {
   	 return con.uins.proc.get(baseUin);
    }
    
    public int getIneqSize(){
   	 return inq.size();
    }

public UserWork getUSW() {
   return us;
}


}
