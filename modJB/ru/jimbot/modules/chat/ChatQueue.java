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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.jimbot.util.Log;

/**
 * Очередь сообщений чата
 * @author Prolubnikov Dmitry
 */
public class ChatQueue implements Runnable {
    private Thread th;
    private ChatServer srv;
    private ChatProps psp;
    // Обшая очередь сообщений
    private ConcurrentLinkedQueue <MsgElement> mq;
    // Очереди сообщений разбитые по комнатам. Очищаются после каждой итерации
    // обхода по юзерам
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<MsgElement>> mmq;
    // Список активных юзеров
    public ConcurrentHashMap <String,UinElement> uq;
    private int sleepAmount = 10000;
    private int counter=0; //счетчик пользователей
    public int msgCounter=1; //счетчик сообщений
    private boolean isNewMsg=true;
    
    /** Creates a new instance of ChatQueue */
    public ChatQueue(ChatServer s) {
        srv = s;
        psp = ChatProps.getInstance(srv.getName());
        sleepAmount = psp.getIntProperty("chat.pauseOut");
        mq = new ConcurrentLinkedQueue<MsgElement>();
        uq = new ConcurrentHashMap<String,UinElement>();
        mmq = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<MsgElement>>();
    }
    
    /**
     * Возвращает число активных юзеров
     */
    public int statUsers() {
        return uq.size();
    }
    
    /**
     * Длина очереди сообщений в чате
     */
    public int statQueue() {
        return mq.size();
    }
    
    /**
     * Общее число отправленных сообщений
     */
    public int statSend() {
        return msgCounter;
    }
    
    /**
     * Добавить сообщение в очередь
     */
    public void addMsg(String m, String user, int room) {
        Log.debug("CHAT: Add msg "+m);
        isNewMsg=true;
        mq.add(new MsgElement(m, msgCounter++, user, room));
    }
    
    /**
     * Добавить активного юзера
     */
    public void addUser(String uin, String buin, int room) {
        Log.debug("CHAT: Add user "+uin + ", " + buin);
        uq.put(uin,new UinElement(uin,buin,0,room));
    }
    
    /**
     * Изменить базовый уин
     */
    public void changeUser(String uin, String buin){
        UinElement u = uq.get(uin);
        u.baseUin = buin;
        uq.put(uin, u);
    }
    
    /**
     * Изменить комнату
     */
    public void changeUserRoom(String uin, int room){
        UinElement u = uq.get(uin);
        u.room = room;
        uq.put(uin, u);
    }
    
    /**
     * Удалить активного юзера
     */
    public void delUser(String uin){
        Log.debug("CHAT: Delete active user "+uin);
        uq.remove(uin);
    }
    
    /**
     * Проверка на наличие юзера в списке активных
     */
    public boolean testUser(String uin) {
        return uq.containsKey(uin);
    }
    
    /**
     * Формирование общего сообщения для юзера из списка еще непрочитанных сообщений
     */
    public synchronized String createMsg(String uin) {
    	String s = "";
    	UinElement u = uq.get(uin);
    	if(!mmq.containsKey(u.room)) return "";
    	if(mmq.get(u.room).isEmpty()) return "";
    	for(MsgElement m:mmq.get(u.room)){
    		if(m.countID>u.lastMsg){
    			if(!m.userSN.equalsIgnoreCase(uin) || !psp.getBooleanProperty("chat.ignoreMyMessage"))
    				s += m.msg + '\n';
    			u.lastMsg = m.countID;
    		}
    	}
    	// Дополнительная проверка, на случай если юзер вышел во время формирования сообщения
//    	((ChatCommandProc) srv.cmd).testState(uin);
    	if(uq.containsKey(uin)) uq.put(uin,u);
    	if(s.length()>1) return s.substring(0, s.length()-1);
    	return s;
    }
    
    public synchronized void send(){
    	try {
    		if (uq.size() == 0)
				return;
			if (mq.size() == 0)
				return;
			// Распихиваем все сообщения по очередям комнат
			while (!mq.isEmpty()) {
				MsgElement m = mq.poll();
				ConcurrentLinkedQueue<MsgElement> q;
				if (!mmq.containsKey(m.room))
					q = new ConcurrentLinkedQueue<MsgElement>();
				else
					q = mmq.get(m.room);
				q.add(m);
				mmq.put(m.room, q);
			}
			// Перебираем юзеров, формируем сообщения на отправку, если что-то
			// есть в очередях
			for (String c : uq.keySet()) {
				String s = createMsg(c);
				((ChatCommandProc) srv.cmd).testState(c);
				if (!s.equals("") && uq.containsKey(c))
					srv.getIcqProcess(uq.get(c).baseUin).mq.add(uq.get(c).uin,
							s);
			}
			// Очищаем очереди чата (все сообщения в них уже обработаны
			for (int room : mmq.keySet()) {
				if (!mmq.get(room).isEmpty())
					mmq.get(room).clear();
			}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
        
    public void start(){
        th = new Thread(this);
        th.setPriority(Thread.NORM_PRIORITY);
        th.start();
    }
    
    public synchronized void stop() {
        th = null;
        notify();
    }
    
    public void run() {
        Thread me = Thread.currentThread(); 
        while (th == me) {
            send();
            try {
                th.sleep(sleepAmount);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }
    
    /**
     * Элемент очереди сообщений
     * @author spec
     *
     */
    public class MsgElement {
        public String msg="";
        public int countID=0;
        public String userSN=""; // Отсекать посылку своих сообщений себе
        public int room=0;
        
        MsgElement(String s, int id, String user, int room) {
            msg = s;
            countID = id;
            userSN = user;
            this.room = room;
        }
    }
    
    /**
     * Элемент списка активных пользователей чата
     * @author spec
     *
     */
    public class UinElement {
        public String uin="";
        public String baseUin="";
        public int lastMsg=0;
        public int room = 0;
        
        UinElement(String u, String bu, int lm, int room) {
            uin = u;
            baseUin = bu;      
            lastMsg = lm;
            this.room = room;
        }
    }
}
