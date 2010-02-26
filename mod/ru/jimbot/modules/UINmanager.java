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

import ru.jimbot.protocol.IcqProtocol;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class UINmanager {
    AbstractConnection con;
    public Vector<String> icq = new Vector<String>(); // ScreenName
    private Vector<String> pass = new Vector<String>(); // Pass
    public Vector<IcqProtocol> proc = new Vector<IcqProtocol>(); // ссылка на процесс
    public boolean ignoreOfflineMsg=false;
    
    /** Creates a new instance of UINmanager */
    public UINmanager(String[] ic, String[] ps, AbstractConnection c, boolean ignore, AbstractProps props) {
        ignoreOfflineMsg = ignore;
        con = c;
        for(int i=0;i<ic.length;i++){
            icq.add(ic[i]);
            pass.add(ps[i]);
            IcqProtocol iprot = new IcqProtocol(props);
            iprot.screenName = ic[i];
            iprot.baseUin = ic[i];
            iprot.password = ps[i];
            iprot.server = con.server;
            iprot.port = con.port;
            iprot.proxyHost = con.proxy[0];
            iprot.useProxy = !con.proxy[0].equals("");
            try{
                iprot.proxyPort = Integer.parseInt(con.proxy[1]);
            } catch (Exception ex){
                iprot.proxyPort=0;
            }
            iprot.proxyUser = con.proxy[2];
            iprot.proxyPass = con.proxy[3];
            //!!! настройки прокси !!!
//            iprot.con = c;
            proc.add(iprot);
        }
    }
    
    public void start() {
        for(int i=0;i<count();i++){
            ((IcqProtocol)proc.get(i)).connect();
        }
    }
    
    public void stop() {
        for(int i=0;i<count();i++){
            ((IcqProtocol)proc.get(i)).disconnect();
        }        
    }
    
    public int count(){
        return icq.size();
    }
    
    public boolean getState(int i) {
        return ((IcqProtocol)proc.get(i)).isOnLine();
    }
    
    public String getUin(int i){
        return (String)icq.get(i);
    }
}
