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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ru.jimbot.util.Log;

/**
* Работа с комнатами
* 
* @author Prolubnikov Dmitry
*/
public class RoomWork {
	public DBChat db;
	// Кеш комнат
	private ConcurrentHashMap <Integer,Rooms> rc = new ConcurrentHashMap <Integer,Rooms>();
	
	public RoomWork(DBChat _db){
		db = _db;
	}
	
	/**
	 * Есть такая комната?
	 * @param id
	 * @return
	 */
	public boolean checkRoom(int id) {
		return rc.containsKey(id);
	}
	
	/**
	 * Заполняет кеш комнат из БД
	 */
   
	public void fillCash() {
		ResultSet rst=null;
        Statement stmt=null;
        String q = "select id, name, topic, user_id, pass from rooms";
        try{
        	stmt = db.getDb().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	Log.debug("EXEC: " + q);
        	rst = stmt.executeQuery(q);
        	while(rst.next()){
        		Rooms r = new Rooms();
        		r.setId(rst.getInt(1));
        		r.setName(rst.getString(2));
        		r.setTopic(rst.getString(3));
        		r.setUser_id(rst.getInt(4));
        		r.setPass(rst.getString(5));
        		rc.put(r.getId(), r);
        	}
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
        	if(rst!=null) try{rst.close();} catch(Exception e) {};
        	if(stmt!=null) try{stmt.close();} catch(Exception e) {};
        }
	}
	
	/**
	 * Возвращает заданную комнату
	 * @param id
	 * @return
	 */
	public Rooms getRoom(int id){
		if(!rc.containsKey(id)) return null;
		return rc.get(id);
	}
	
	/**
	 * Создание новой комнаты
	 * @param r
	 * @return
	 */
	public boolean createRoom(Rooms r){
        String q = "insert into rooms values(?,?,?,?,?)";
        Log.debug("INSERT room id=" + r.getId());
        boolean f = false;
        try {
        	PreparedStatement pst = db.getDb().prepareStatement(q);
        	pst.setInt(1, r.getId());
        	pst.setString(2, r.getName());
        	pst.setString(3, r.getTopic());
        	pst.setString(4, "");
        	pst.setInt(5, r.getUser_id());
        	pst.execute();
        	pst.close();
        	rc.put(r.getId(), r);
        	f=true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return f;
	}
	
	/**
	 * Обновление данных о комнате
	 * @param r
	 * @return
	 */
	public boolean updateRoom(Rooms r, String pass) {
		String q = "update rooms set name=?, topic=?, user_id=?, pass=? where id=?";
		Log.debug("UPDATE rooms id=" + r.getId());
		boolean f = false;
		try {
			PreparedStatement pst = db.getDb().prepareStatement(q);
			pst.setInt(5, r.getId());
			pst.setString(1, r.getName());
        	pst.setString(2, r.getTopic());
        	pst.setString(4, pass);
        	pst.setInt(3, r.getUser_id());
        	pst.execute();
        	pst.close();
        	rc.put(r.getId(), r);
        	f=true;
		} catch (Exception ex){
            ex.printStackTrace();
        }
        return f;
	}
	
	public Set<Integer> getRooms() {
		return rc.keySet();
	}
        public boolean deleteRoom(Rooms r)
        {
        String q = "delete from rooms where id=?";
        Log.debug("DELETE room id=" + r.getId());
        boolean f = false;
        try{
        PreparedStatement pst = db.getDb().prepareStatement(q);
        pst.setInt(1, r.getId());
        pst.execute();
        pst.close();
        rc.remove(r.getId());
        f=true;
        }
        catch (Exception ex){
        ex.printStackTrace();
        }
        return f;
        }
}
