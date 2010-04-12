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

package ru.jimbot.modules;

import java.util.Vector;

import ru.jimbot.db.DBAdaptor;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.modules.chat.UserWork;
/**
 *
 * @author Prolubnikov Dmitry
 */
public class AbstractServer {
    public AbstractCommandProcessor cmd;
    public boolean isRun = false;
    private String name = "";
    private AbstractProps props = null;
    // Для хранения промежуточных результатов между запусками скриптов бота
    public Vector res = new Vector();
    public boolean testBd = true;
    /** Creates a new instance of Service */
    public AbstractServer() {
    }
    
    public void start() {}
    
    public void stop() {}
    
    public void parseMsg(IcqProtocol proc, String uin, String msg) {
        cmd.parse(proc, uin, msg);
    }
    
    public void parseStatus(IcqProtocol proc, String uin, int status) {
        cmd.parseStatus(proc, uin, status);
    }
    
    public IcqProtocol getIcqProcess(int baseUin) {
        return null;
    }
    
    public void setName(String name){
    	this.name = name;
    }
    
    public String getName() {
    	return name;
    }
    
    public AbstractProps getProps() {
    	return props;
    }
    
    public DBAdaptor getDB(){
    	return null;
    }
    
    public int getIneqSize(){
    	return -1;
    }


   public UserWork getUSW() {
   return null;
}
}
