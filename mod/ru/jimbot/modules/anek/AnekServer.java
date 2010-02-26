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

package ru.jimbot.modules.anek;

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
public class AnekServer extends AbstractServer{
    public AnekConnection con;
    public AnekWork an;
    public MsgInQueue inq;
    private AnekProps props = null;
    
    /** Creates a new instance of AnekServer */
    public AnekServer(String name) {
    	this.setName(name);
    	AnekProps.getInstance(name).load();
    	an = new AnekWork(this.getName());
        cmd = new AnekCommandProc(this);
        con = new AnekConnection(this);
        con.server = MainProps.getServer();
        con.port = MainProps.getPort();
        con.proxy = MainProps.getProxy();
        String[] icq = new String[AnekProps.getInstance(this.getName()).uinCount()];
        String[] pass = new String[AnekProps.getInstance(this.getName()).uinCount()];
        for(int i=0;i<AnekProps.getInstance(this.getName()).uinCount();i++){
            icq[i] = AnekProps.getInstance(this.getName()).getUin(i);
            pass[i] = AnekProps.getInstance(this.getName()).getPass(i);
        }
        con.uins = new UINmanager(icq, pass, con, true, 
                AnekProps.getInstance(this.getName()));
        inq = new MsgInQueue(cmd);
     }
    
     public void start(){
         con.uins.start();
         for(int i=0;i<con.uins.count();i++){
             inq.addReceiver((IcqProtocol)con.uins.proc.get(i));
         }
         inq.start();
         // Удалить из запуска инициализацию базы. Она должна проходить по мере необходимости.
         WorkScript.getInstance(getName()).startScript("start", "", this);
//         an.initDB();
         isRun=true;
     }
     
     public void stop(){
    	 WorkScript.getInstance(getName()).startScript("stop", "", this);
    	 inq.stop();
         con.uins.stop();
         closeDB();
         isRun=false;
     }
     
     public void closeDB(){
         an.closeDB();
     }
     
     public DBAdaptor getDB(){
    	 return an.db;
     }
     
     public AbstractProps getProps() {
    	 if(props==null)
    		 props = AnekProps.getInstance(this.getName());
     	return props;
     }    
     
     public int getIneqSize(){
    	 return inq.size();
     }
     
     public IcqProtocol getIcqProcess(int baseUin) {
    	 return con.uins.proc.get(baseUin);
     }
}
