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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

import ru.jimbot.util.Log;

/**
 * Элемент для таблицы кешеа скрипта
 * @author Prolubnikov Dmitry
 *
 */
public class ScriptCashElement {
	private String fname="";
	private Date dt = new Date(0);
	private String script="";
	
	public ScriptCashElement(String fname){
		dt = new Date(new File(fname).lastModified());
		this.fname = fname;
		script = readScript(fname);
	}
	
	/**
	 * Возвращает текст скрипта, при необходимости заново перечитывает файл
	 * @return
	 */
	public String getScript(){
		long d = new File(fname).lastModified();
		if(d != dt.getTime()){
			script = readScript(fname);
			dt.setTime(d);
		}
		return script;
	}
	
    /**
     * Читаем текст макроса в текстовую переменную
     * @param fileName
     * @return
     */
    private String readScript(String fileName){
    	String s="";
    	try {
    		Log.info("Reading script: "+fileName);
    		BufferedReader in
    		   = new BufferedReader(new InputStreamReader(
    				   new FileInputStream(fileName),"windows-1251"));
    		while(in.ready())
    			s += in.readLine() + '\n';
    		in.close();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return s;
    }	
}
