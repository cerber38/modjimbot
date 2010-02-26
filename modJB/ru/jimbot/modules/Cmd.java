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
 * Хранит отдельную команду
 * @author Prolubnikov Dmitry
 *
 */
public class Cmd {
public int cmd = -1;
public String param = "";
public String cmd_name = "";
public String script = "";

	public Cmd(){
		
	}

	public Cmd(String name, String param, int id, String scr) {
		cmd_name = name;
		this.param = param;
		cmd = id;
		script = scr;
	}

	public Cmd(String name, String param, int id) {
		cmd_name = name;
		this.param = param;
		cmd = id;
	}

}
