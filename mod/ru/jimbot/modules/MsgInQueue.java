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

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.jimbot.modules.chat.Users;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;

/**
 * Очередь входящих сообщений
 * @author Prolubnikov Dmitry
 */
public class MsgInQueue implements Runnable {
    Vector<MsgReceiver> receivers; // список объектов, помещающих сообщения в эту очередь
    AbstractCommandProcessor cmd;
    ConcurrentLinkedQueue <MsgQueueElement> q;
    private Thread th;
    int sleepAmount = 100;
    public boolean ignoreOffline = true;
    // Пары уин - время последнего сообщения
    private HashMap<String,Long> flood = new HashMap<String,Long>();
        
    /** Creates a new instance of MsgInQueue */
    public MsgInQueue(AbstractCommandProcessor c) {
        cmd = c;
        receivers = new Vector<MsgReceiver>();
        q = new ConcurrentLinkedQueue<MsgQueueElement>();
    }
    
    /**
     * Проверка на флуд входящий сообщений. Слишком частые не должны попадать в очередь.
     * @param uin
     * @return
     */
    public boolean testFlood(String uin){
    	long i = cmd.getServer().getProps().getIntProperty("bot.pauseIn");
    	long c = System.currentTimeMillis();
    	if(flood.containsKey(uin)){
    		if((c-flood.get(uin))<i){
    			flood.put(uin, c);
    			return true;
    		} else {
    			flood.put(uin, c);
    			return false;
    		}
    	} else {
    		flood.put(uin, c);
			return false;
    	}
    }
    
//    private synchronized void addMsgCount(){
//    	msgCountAll++;
//    	long c = System.currentTimeMillis();
//    	if((c-msgTime[0])>60000)
//    		msgCount[0] = msgCountAll-msgCount[0];
//    	if((c-msgTime[1])>300000)
//    		msgCount[1] = msgCountAll-msgCount[1];
//    	if((c-msgTime[2])>3600000)
//    		msgCount[2] = msgCountAll-msgCount[2];
//    	if((c-msgTime[3])>86400000)
//    		msgCount[3] = msgCountAll-msgCount[3];
//    }
    
//    public synchronized int getMsgCount(int i){
//    	if(i>3) return msgCountAll;
//    	return msgCount[i];
//    }
    
    public void start(){
        th = new Thread(this,"msg_in");
        th.setPriority(Thread.NORM_PRIORITY);
        th.start();
    }
    
    public synchronized void stop() {
        th = null;
        receivers = new Vector<MsgReceiver>();
        notify();
    }
    
    private void parseMsg(){
        try{
            if(q.size()==0) return;
            MsgQueueElement m = q.poll();
            if(m.type==MsgQueueElement.TYPE_TEXT){
                cmd.parse(m.proc,m.sn,m.msg);
            } else if(m.type==MsgQueueElement.TYPE_STATUS){
                cmd.parseStatus(m.proc, m.sn, Integer.parseInt(m.msg));
            }else if(m.type==MsgQueueElement.TYPE_FLOOD_NOTICE){
            	cmd.parseFloodNotice(m.sn, m.msg, m.proc);
            } else {
//                Log.info(m.u.nick);
                cmd.parseInfo(m.u, m.info_type);
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void run() {
        Thread me = Thread.currentThread(); 
        while (th == me) {
            parseMsg();
            try {
                Thread.sleep(sleepAmount);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }
    
    public void addReceiver(IcqProtocol ip){
        receivers.add(new MsgReceiver(this,ip));
    }
    
    public void addMsg(IcqProtocol proc, String sn, String msg, boolean isOffline){
        if(isOffline && ignoreOffline) {
            Log.info("OFFLINE: " + sn + " - " + msg);
            return;
        }
        MsgStatCounter.getElement(proc.baseUin).addMsgCount();
        if(!testFlood(sn))
        	q.add(new MsgQueueElement(sn, msg, proc));
        else {
        	Log.flood("FLOOD from " + sn + ">> " + msg);
        	MsgQueueElement e = new MsgQueueElement(sn,msg,proc);
        	e.type = MsgQueueElement.TYPE_FLOOD_NOTICE;
        	q.add(e);
        }
        	
    }
    
    public void addStatus(IcqProtocol proc, String sn, String st){
//        Log.info("ADD status in queue");
        MsgQueueElement m = new MsgQueueElement(sn, st, proc);
        m.type = MsgQueueElement.TYPE_STATUS;
        q.add(m);
    }
    
    public void addInfo(Users u, int type){
        MsgQueueElement m = new MsgQueueElement(u, type);
        q.add(m);
    }
    
    public int size(){
    	return q.size();
    }
}
