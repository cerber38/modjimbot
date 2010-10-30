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

/**
 * Подсчет сообщений для статистики
 * @author Prolubnikov Dmitry
 *
 */
public class MsgStatCounter {
	public static final int M1 = 0;
	public static final int M5 = 1;
	public static final int M60 = 2;
	public static final int H24 = 3;
	public static final int ALL = 4;
	private static HashMap<String,MsgStatCounter> cnt = new HashMap<String,MsgStatCounter>();
	
    // Для обработки статистики (1min, 5min, 60min, 24h)
    private int msgCount[] = {0,0,0,0};
    private int msgCount2[] = {0,0,0,0};
    private long msgTime[] = {0,0,0,0};
    private int msgCountAll = 0;

    /**
     * Добавлено очередное сообщение
     */
    public synchronized void addMsgCount(){
    	msgCountAll++;
    	long c = System.currentTimeMillis();
    	if((c-msgTime[0])>60000){
    		msgCount[0] = msgCountAll-msgCount2[0];
    		msgCount2[0] = msgCountAll;
    		msgTime[0] = c;
    	}
    	if((c-msgTime[1])>300000){
    		msgCount[1] = msgCountAll-msgCount2[1];
    		msgCount2[1] = msgCountAll;
    		msgTime[1] = c;
    	}
    	if((c-msgTime[2])>3600000){
    		msgCount[2] = msgCountAll-msgCount2[2];
    		msgCount2[2] = msgCountAll;
    		msgTime[2] = c;
    	}
    	if((c-msgTime[3])>86400000){
    		msgCount[3] = msgCountAll-msgCount2[3];
    		msgCount2[3] = msgCountAll;
    		msgTime[3] = c;
    	}
    }

    /**
     * Возвращает подсчитанное число сообщений за заданный интервал.
     * @param i
     * @return
     */
    public synchronized int getMsgCount(int i){
    	if(i>3) return msgCountAll;
    	int c[] = {60000,300000,3600000,86400000};
    	if((System.currentTimeMillis()-msgTime[i])>c[i]){
    		msgCount[i] = msgCountAll - msgCount2[i];
    		msgCount2[i] = msgCountAll;
    		msgTime[i] = System.currentTimeMillis();
    	}
    	return msgCount[i];
    }
    
    public static MsgStatCounter getElement(String uin){
    	if(cnt.containsKey(uin))
    		return cnt.get(uin);
    	else {
    		cnt.put(uin, new MsgStatCounter());
    		return cnt.get(uin);
    	}
    }
}
