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

import ru.jimbot.db.DBObject;

/**
* Комната в чате
* 
* @author Prolubnikov Dmitry
*/
public class Rooms extends DBObject {
	private int id=0;
	private String name="";
	private String topic="";
	private int user_id=0;
	private String pass="";
	
	public Rooms(){
		
	}
	
	public Rooms(int _id, String _name, String _topic, int _user_id){
		id=_id;
		name=_name;
		topic=_topic;
		user_id=_user_id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public boolean checkPass(String p) {
		return pass.equals(p);
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@Override
	public String[] getFields() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
