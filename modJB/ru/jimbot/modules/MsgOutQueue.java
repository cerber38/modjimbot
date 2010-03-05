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

import java.util.concurrent.ConcurrentLinkedQueue;

import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class MsgOutQueue implements Runnable {
    int counter=0;
    int maxCounter=144; //Период переподключения
    public IcqProtocol proc;
    private Thread th;
    int sleepAmount = 5000;
    ConcurrentLinkedQueue <Msg> q;
    private long stopCon = 0; // Время разрыва связи
    private int PAUSE_OUT, PAUSE_RESTART, MSG_OUT_LIMIT;
    private int p_restart = 30000;
    private int lostMsg = 0; // Счетчик пропущенных сообщений
    private long t = 0; // Время последнего отправленного сообщения
    
    /** Creates a new instance of MsgOutQueue */
    public MsgOutQueue(IcqProtocol pr, int pout, int prestart, int mlimit) {
        PAUSE_OUT = pout; PAUSE_RESTART=prestart; MSG_OUT_LIMIT=mlimit;
        sleepAmount = PAUSE_OUT; //Props.getIntProperty("bot.pauseOut");
        proc = pr;
        q = new ConcurrentLinkedQueue<Msg>();
    }    
    
    public void start(){
        th = new Thread(this,"msg_out");
        th.setPriority(Thread.NORM_PRIORITY);
        th.start();
    }
    
    public synchronized void stop() {
        th = null;
        notify();
    }
     
    public void add(String uin, String msg){
    add(uin, msg, 0);
    }



    public void add(String uin, String msg, int type) {
        if(q.size()>MSG_OUT_LIMIT /*Props.getIntProperty("bot.msgOutLimit")*/) {
        	Log.getLogger(proc.serviceName).info("OUT MESSAGE IS LOST: " + proc.baseUin + ">>" + uin + " : " + msg);
        	lostMsg++;
        	return;
        }
        if(type!=Msg.TYPE_MSG){
        	q.add(new Msg(uin, msg, type));
        	return;
        }
        int maxLenMsg = proc.getProps().getIntProperty("chat.MaxOutMsgSize");
        int maxCountMsg = proc.getProps().getIntProperty("chat.MaxOutMsgCount");
        int m = (msg.length()/maxLenMsg+1) > maxCountMsg ? maxCountMsg : (msg.length()/maxLenMsg+1);
        for(int i=0; i<m; i++){
        	if(((i+1)*maxLenMsg-1)<msg.length()){
        		if(i==(maxCountMsg-1))
        			q.add(new Msg(uin,msg.substring(i*maxLenMsg, (i+1)*maxLenMsg) + 
        					"\nЧасть сообщения была обрезана...", 0));
        		else
        			q.add(new Msg(uin,msg.substring(i*maxLenMsg, (i+1)*maxLenMsg), 0));
        	} else
        		q.add(new Msg(uin,msg.substring(i*maxLenMsg, msg.length()), 0));
        }
   }
    
    public int getLostMsgCount(){
    	return lostMsg;
    }
    
    private void send() {
    	if((System.currentTimeMillis()-t)<=sleepAmount) return;
    	t = System.currentTimeMillis();
    	try {
            Msg m;
            if(q.size()<=0) return;
            m = q.poll();
            switch (m.type){
            case Msg.TYPE_INFO:
            	proc.userInfoRequest(m.uin, m.text);
            	break;
            case Msg.TYPE_AUTH:
            	proc.authRequest(m.uin, m.text);
            	break;
            case Msg.TYPE_MSG:
            default:
            	proc.sendMsg(m.uin,m.text);
            }   		
    	} catch (Exception ex){
    		Log.getLogger(proc.serviceName).info("ERROR send msg over " + proc.baseUin);
    		stopCon=System.currentTimeMillis();
    		ex.printStackTrace();
    	}
    }
    
    public int size() {
        return q.size();
    }
    
    public void testOnline(){
        try{
            if(proc==null) {
                stopCon=0;
                return;
            }
            //TODO Как сообщить сюда о запуске процесса?
//            if(!proc.con.srv.isRun) {
//                stopCon=0;
//                return;
//            }
            if(proc.isOnLine()) {
                stopCon=0;
                counter=0;
                p_restart=30000;
                return;
            }
            if(stopCon>0) {
                if((System.currentTimeMillis()-stopCon)>=p_restart/*PAUSE_RESTART/*Props.getIntProperty("bot.pauseRestart")*/){
                    MainProps.nextServer();
                    proc.server = MainProps.getServer();
                    proc.port = MainProps.getPort();
                    Log.getLogger(proc.serviceName).info("Попытка нового подключения... " + proc.server + ":" + proc.port);
                    proc.reConnect();
                    stopCon=System.currentTimeMillis();
                    p_restart = (p_restart>=PAUSE_RESTART) ? PAUSE_RESTART : p_restart*2;
                }
            } else {
                Log.getLogger(proc.serviceName).info("Ожидание подключения...");
                stopCon = System.currentTimeMillis();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void run() {
        Thread me = Thread.currentThread(); 
        while (th == me) {
        		testOnline();
        		send();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }
    
    public class Msg{
    	public static final int TYPE_MSG = 0; // Обычное сообщение
    	public static final int TYPE_INFO = 1; // запрос инфы
    	public static final int TYPE_AUTH = 2; // запрос авторизации
        public String uin="";
        public String text = "";
        public int type = 0;
        
        public Msg(String _uin, String msg, int _type) {
            uin = _uin;
            text = msg;
            type = _type;
        }
     }
}
