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

import java.io.File;
import java.util.HashMap;

import ru.jimbot.util.Log;

/**
 * Кеш для макросов. Читает макрос при первом обращении. Наблюдает изменения файла.
 * При необходимости обновляет кеш.
 * 
 * @author Prolubnikov Dmitry
 *
 */
public class ScriptCash {
	private HashMap<String,ScriptCashElement> scr;
	
	public ScriptCash(){
		scr = new HashMap<String,ScriptCashElement>();
	}
	
	/**
	 * Возвращает текст скрипта из кеша, если он там есть.
	 * При необходимости обновляет и пополняет кеш.
	 * Возвращает "" если файл отсутствует.
	 * @param fname
	 * @return
	 */
	public String getScript(String fname){
		String s = "";
		try {
			if(!new File(fname).exists()){
				Log.error("Скрипт не найден: " + fname);
				return "";
			}
			if(scr.containsKey(fname)){
				s = scr.get(fname).getScript();
			} else {
				scr.put(fname, new ScriptCashElement(fname));
				s = scr.get(fname).getScript();
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return s;
	}
}
