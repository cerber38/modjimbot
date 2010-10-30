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

/**
 * Элемент структуры для анализа возникновения флуда
 * 
 * @author Prolubnikov Dmitry
 * 
 */
public class FloodElement {
	private int count=0;
	private long time=0;
	private String lastMsg="";
	private long timeLimit = 0;
	
	/**
	 * Новый элемент анализа флуда.
	 * @param t - период, чаще которого нельзя слать одинаковые сообщения
	 */
	public FloodElement(long t){
		timeLimit = t;
	}
	
	/**
	 * Принято новое сообщение
	 * @param s - текст сообщения
	 * @return - число одинаковых сообщений с периодом меньше лимита 
	 */
	public int addMsg(String s){
		if(lastMsg.equalsIgnoreCase(s) && (System.currentTimeMillis()-time)<=timeLimit)
			count++;
		else
			count=0;
		time = System.currentTimeMillis();
		lastMsg = s;
		return count;
	}
	
	/**
	 * Время прошедшее с последнего сообщения
	 * @return
	 */
	public long getDeltaTime(){
		return System.currentTimeMillis()-time;
	}
	
	/**
	 * Число одинаковых сообщений с периодом меньше лимита
	 * @return
	 */
	public int getCount(){
		return count;
	}
	
	/**
	 * Время последнего сообщения
	 * @return
	 */
	public long getLastTime(){
		return time;
	}
	
	/**
	 * сообщение совпадает с предыдущим?
	 * @param s
	 * @return
	 */
	public boolean isDoubleMsg(String s){
		return s.equals(lastMsg);
	}
	
	/**
	 * Возвращает последнее отправленное сообщение
	 * @return
	 */
	public String getLastMsg(){
		return lastMsg;
	}
}
