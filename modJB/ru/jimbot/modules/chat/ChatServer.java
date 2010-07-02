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
import ru.jimbot.util.Log;
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
    private ChatProps props = null;
    
    /** Creates a new instance of ChatServer */

    public ChatServer(String name) {
    	this.setName(name);
    	props = ChatProps.getInstance(name);
        props.load();
        cmd = new ChatCommandProc(this);
        con = new ChatConnection(this);
        con.server = MainProps.getServer();
        con.port = MainProps.getPort();
        con.proxy = MainProps.getProxy();
        cq = new ChatQueue(this);
        inq = new MsgInQueue(cmd);
    }
    
    public void start() {
        us = new UserWork(getName());
        String[] icq = new String[ChatProps.getInstance(this.getName()).uinCount()];
        String[] pass = new String[ChatProps.getInstance(this.getName()).uinCount()];
        for(int i=0;i<ChatProps.getInstance(this.getName()).uinCount();i++){
            icq[i] = ChatProps.getInstance(this.getName()).getUin(i);
            pass[i] = ChatProps.getInstance(this.getName()).getPass(i);
        }
        con.uins = new UINmanager(icq, pass, con,
                ChatProps.getInstance(this.getName()).getBooleanProperty("chat.IgnoreOfflineMsg"),
                ChatProps.getInstance(this.getName()), this.getName());
    	WorkScript.getInstance(getName()).startScript("start", "", this);
        if(!con.server.equals("")) {
            con.uins.start();
        }
         for(int i=0;i<con.uins.count();i++){
             inq.addReceiver((IcqProtocol)con.uins.proc.get(i));
         }
        inq.start();
        cq.start();
        isRun = true;
        if(ChatProps.getInstance(this.getName()).getBooleanProperty("vic.on.off")){
        Log.getLogger(getName()).talk("I start quiz for service - \"" + this.getName() + "\"");
        ((ChatCommandProc)this.cmd).Quiz.start();
        }
    }
    
    public void stop() {
        isRun = false;
        closeDB();
        us = null;
        inq.stop();
        cq.stop();   	
        if(!con.server.equals("")) con.uins.stop();
        /*Убьем остальные потоки*/
        if(((ChatCommandProc)this.cmd).Quiz != null) ((ChatCommandProc)this.cmd).Quiz.stop();
        if(((ChatCommandProc)this.cmd).radm != null) ((ChatCommandProc)this.cmd).radm.stop();
        if(((ChatCommandProc)this.cmd).xstatus != null) ((ChatCommandProc)this.cmd).xstatus.stop();
        WorkScript.getInstance(getName()).startScript("stop", "", this);
    }

        public void Errore_bd() {
        isRun = false;
        inq.stop();
        cq.stop();   	
        if(!con.server.equals("")) con.uins.stop();
        /*Убьем остальные потоки*/
        if(((ChatCommandProc)this.cmd).Quiz != null) ((ChatCommandProc)this.cmd).Quiz.stop();
        if(((ChatCommandProc)this.cmd).radm != null) ((ChatCommandProc)this.cmd).radm.stop();
        if(((ChatCommandProc)this.cmd).xstatus != null) ((ChatCommandProc)this.cmd).xstatus.stop();
        Log.getLogger(this.getName()).info("Остановка сервиса - \"" + this.getName() + "\"");
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
        if(con.uins == null) return null;
               if(con.uins.proc.get(baseUin) == null) return null;
   	 return con.uins.proc.get(baseUin);
    }
    
    public int getIneqSize(){
   	 return inq.size();
    }

public UserWork getUSW() {
   return us;
}


}
