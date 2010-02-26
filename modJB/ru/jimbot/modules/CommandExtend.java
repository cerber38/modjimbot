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

/**
 * Элемент для расширенной обработки команд 
 * (обработка ответов пользователя и запоминание результатов)
 * 
 * @author Prolubnikov Dmitry
 *
 */
public class CommandExtend {
	private String uin;
	private long time;
	private String msg;
	private String cmd;
	private Vector data;
	
	/**
	 * Создание нового элемента
	 * @param _uin - Уин пользователя
	 * @param _cmd - команда
	 * @param _msg - сообщение
	 * @param _data - промежуточные данные для работы команды
	 * @param expire - длительность ожидания ответа (мс)
	 */
	public CommandExtend(String _uin, String _cmd, String _msg, Vector _data, long expire) {
		time = System.currentTimeMillis() + expire;
		uin = _uin;
		cmd = _cmd;
		msg = _msg;
		data = _data;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public String getUin() {
		return uin;
	}
	
	public String getCmd() {
		return cmd;
	}
	
	public Vector getData() {
		return data;
	}
	
	public boolean isExpire() {
		return System.currentTimeMillis()>time;
	}
}
