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
import java.sql.Timestamp;
import java.util.Vector;

import ru.jimbot.db.DBAdaptor;
import ru.jimbot.db.DBObject;
import ru.jimbot.util.Log;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class DBChat extends DBAdaptor{
    //private String serviceName = "";
    
    /** Creates a new instance of DBChat */
    public DBChat(String name) throws Exception
    {
    serviceName = name;
    }
    
    public void createDB(){ }
    
    /**
     * Запись лога в БД
     */
    public void log(int user, String sn, String type, String msg, int room){
        if(!ChatProps.getInstance(serviceName).getBooleanProperty("chat.writeAllMsgs")) return;
        try{
            PreparedStatement pst = getDb().prepareStatement("insert into log values(null, ?, ?, ?, ?, ?, ?)");
            pst.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
            pst.setInt(2,user);
            pst.setString(3,sn);
            pst.setString(4,type);
            pst.setString(5,msg);
            pst.setInt(6, room);
            pst.execute();
            pst.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Запись события в БД
     */
    public void event(int user, String sn, String type, int user2, String sn2, String msg) {
        try {
            PreparedStatement pst = getDb().prepareStatement("insert into events values(null, ?, ?, ?, ?, ?, ?, ?)");
            pst.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
            pst.setInt(2,user);
            pst.setString(3,sn);
            pst.setString(4,type);
            pst.setInt(5,user2);
            pst.setString(6,sn2);            
            pst.setString(7,msg);
            pst.execute();
            pst.close();            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Поиск параметров пользователя
     */
    public boolean existUserProps(int user_id){
        boolean f = false;
        try{
        	Vector<String[]> v = this.getValues("select count(*) from user_props where user_id="+user_id);
        	if(Integer.parseInt(v.get(0)[0])>0) f = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return f;
    }
    
    /**
     * Возвращает параметры пользователя
     */
    public Vector<String[]> getUserProps(int user_id){
        Vector<String[]> v=new Vector<String[]>();
        try{
            v=getValues("select name, val from user_props where user_id="+user_id);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return v;
    }
    
    /**
     * Устанавливает параметр пользователя
     */
    public boolean setUserProps(int user_id, String name, String val){
        boolean f = false;
        try{
            executeQuery("delete from user_props where user_id="+user_id+" and name='"+name + "'");
            PreparedStatement pst = getDb().prepareStatement("insert into user_props values(?, ?, ?)");
            pst.setString(2, name);
            pst.setString(3, val);
            pst.setInt(1, user_id);
            pst.execute();
            pst.close();
            f = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return f;
    }


 /*
  * @USER
  */
    
    public DBObject getObject(String q){
        Users us = new Users();
        ResultSet rSet=null;
        Statement stmt=null;
        try{
        	stmt = getDb().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	Log.getLogger(serviceName).debug("EXEC: " + q);
        	rSet = stmt.executeQuery(q);
            rSet.next();
            us.id = rSet.getInt(1);
            us.sn = rSet.getString(2);
            us.nick = rSet.getString(3);
            us.localnick = rSet.getString(4);
            us.fname = rSet.getString(5);
            us.lname = rSet.getString(6);
            us.email = rSet.getString(7);
            us.city = rSet.getString(8);
            us.homepage = rSet.getString(9);
            us.gender = rSet.getInt(10);
            us.birthyear = rSet.getInt(11);
            us.birthmonth = rSet.getInt(12);
            us.birthday = rSet.getInt(13);
            us.age = rSet.getInt(14);
            us.country = rSet.getInt(15);
            us.language = rSet.getInt(16);
            us.state =  rSet.getInt(17);
            us.basesn = rSet.getString(18);
            us.createtime = rSet.getTimestamp(19).getTime();
            us.room = rSet.getInt(20);
            if(rSet.getLong(21)==0)us.lastKick = System.currentTimeMillis(); else us.lastKick = rSet.getTimestamp(21).getTime();
            if(rSet.getLong(22)==0)us.grouptime = System.currentTimeMillis(); else us.grouptime = rSet.getTimestamp(22).getTime();
            if(rSet.getLong(23)==0)us.data = System.currentTimeMillis(); else us.data = rSet.getTimestamp(23).getTime();
            if(rSet.getLong(24)==0)us.lastclosed = System.currentTimeMillis(); else us.lastclosed = rSet.getTimestamp(24).getTime();
            us.ball = rSet.getInt(25);
            us.answer = rSet.getInt(26);
            us.status =  rSet.getString(27);
            us.clansman = rSet.getInt(28);
            us.clangroup =  rSet.getString(29);
            us.wedding =  rSet.getInt(30);
            us.car =  rSet.getString(31);
            us.home =  rSet.getString(32);
            us.clothing =  rSet.getString(33);
            us.animal =  rSet.getString(34);
            } catch (Exception ex){
            ex.printStackTrace();
            } finally {
        	if(rSet!=null) try{rSet.close();} catch(Exception e) {};
        	if(stmt!=null) try{stmt.close();} catch(Exception e) {};
        }
        return us;
    }
    
    public Vector<Users> getObjectVector(String q){
        Vector<Users> v = new Vector<Users>();
        ResultSet rSet=null;
        Statement stmt=null;
        try{
        	stmt = getDb().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	Log.getLogger(serviceName).debug("EXEC: " + q);
        	rSet = stmt.executeQuery(q);
            while(rSet.next()) {
                Users us = new Users();
                us.id = rSet.getInt(1);
                us.sn = rSet.getString(2);
                us.nick = rSet.getString(3);
                us.localnick = rSet.getString(4);
                us.fname = rSet.getString(5);
                us.lname = rSet.getString(6);
                us.email = rSet.getString(7);
                us.city = rSet.getString(8);
                us.homepage = rSet.getString(9);
                us.gender = rSet.getInt(10);
                us.birthyear = rSet.getInt(11);
                us.birthmonth = rSet.getInt(12);
                us.birthday = rSet.getInt(13);
                us.age = rSet.getInt(14);
                us.country = rSet.getInt(15);
                us.language = rSet.getInt(16);
                us.state =  rSet.getInt(17);
                us.basesn = rSet.getString(18);
                us.createtime = rSet.getTimestamp(19).getTime();
                us.room = rSet.getInt(20);
                if(rSet.getLong(21)==0)us.lastKick = System.currentTimeMillis(); else us.lastKick = rSet.getTimestamp(21).getTime();
                if(rSet.getLong(22)==0)us.grouptime = System.currentTimeMillis(); else us.grouptime = rSet.getTimestamp(22).getTime();
                if(rSet.getLong(23)==0)us.data = System.currentTimeMillis(); else us.data = rSet.getTimestamp(23).getTime();
                if(rSet.getLong(24)==0)us.lastclosed = System.currentTimeMillis(); else us.lastclosed = rSet.getTimestamp(24).getTime();
                us.ball = rSet.getInt(25);
                us.answer = rSet.getInt(26);
                us.status =  rSet.getString(27);
                us.clansman = rSet.getInt(28);
                us.clangroup =  rSet.getString(29);
                us.wedding =  rSet.getInt(30);
                us.car =  rSet.getString(31);
                us.home =  rSet.getString(32);
                us.clothing =  rSet.getString(33);
                us.animal =  rSet.getString(34);
                v.addElement(us);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
        	if(rSet!=null) try{rSet.close();} catch(Exception e) {};
        	if(stmt!=null) try{stmt.close();} catch(Exception e) {};
        }
        return v;
        
    }
    
    public void insertObject(DBObject o){
        Users us = (Users)o;
        Log.getLogger(serviceName).debug("INSERT user id=" + us.id);
        try{
            PreparedStatement pst = getDb().prepareStatement("insert into users values (?," +
                    " ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setInt(1,us.id);
            pst.setString(2,us.sn);
            pst.setString(3,us.nick);
            pst.setString(4,us.localnick);
            pst.setString(5,us.fname);
            pst.setString(6,us.lname);
            pst.setString(7,us.email);
            pst.setString(8,us.city);
            pst.setString(9,us.homepage);
            pst.setInt(10,us.gender);
            pst.setInt(11,us.birthyear);
            pst.setInt(12,us.birthmonth);
            pst.setInt(13,us.birthday);
            pst.setInt(14,us.age);
            pst.setInt(15,us.country);
            pst.setInt(16,us.language);
            pst.setInt(17,us.state);
            pst.setString(18,us.basesn);
            pst.setTimestamp(19,new Timestamp(us.createtime));
            pst.setInt(20,us.room);
            pst.setTimestamp(21,new Timestamp(us.lastKick));
            pst.setTimestamp(22,new Timestamp(us.grouptime));
            pst.setTimestamp(23,new Timestamp(us.data));
            pst.setTimestamp(24,new Timestamp(us.lastclosed));
            pst.setInt(25,us.ball);
            pst.setInt(26,us.answer);
            pst.setString(27,us.status);
            pst.setInt(28,us.clansman);
            pst.setString(29,us.clangroup);
            pst.setInt(30,us.wedding);
            pst.setString(31,us.car);
            pst.setString(32,us.home);
            pst.setString(33,us.clothing);
            pst.setString(34,us.animal);
            pst.execute();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void updateObject(DBObject o){
        Users us = (Users)o;
        Log.getLogger(serviceName).debug("UPDATE user id=" + us.id);
        try{
            PreparedStatement pst = getDb().prepareStatement("update users set sn=?,nick=?," + 
                    "localnick=?,fname=?,lname=?,email=?,city=?,homepage=?,gender=?," +
                    "birthyear=?,birthmonth=?,birthday=?,age=?,country=?,language=?," +
                    "state=?,basesn=?,createtime=?,room=?, lastkick=?, grouptime=?, data=?," +
                    "lastclosed=?, ball=?, answer=?, status=?, clansman=?, clangroup=?, wedding=?," +
                    "car=?, home=?, clothing=?, animal=? where id=" + us.id);
            pst.setString(1,us.sn);
            pst.setString(2,us.nick);
            pst.setString(3,us.localnick);
            pst.setString(4,us.fname);
            pst.setString(5,us.lname);
            pst.setString(6,us.email);
            pst.setString(7,us.city);
            pst.setString(8,us.homepage);
            pst.setInt(9,us.gender);
            pst.setInt(10,us.birthyear);
            pst.setInt(11,us.birthmonth);
            pst.setInt(12,us.birthday);
            pst.setInt(13,us.age);
            pst.setInt(14,us.country);
            pst.setInt(15,us.language);
            pst.setInt(16,us.state);
            pst.setString(17,us.basesn);
            pst.setTimestamp(18,new Timestamp(us.createtime));
            pst.setInt(19,us.room);
            pst.setTimestamp(20,new Timestamp(us.lastKick));
            pst.setTimestamp(21,new Timestamp(us.grouptime));
            pst.setTimestamp(22,new Timestamp(us.data));
            pst.setTimestamp(23,new Timestamp(us.lastclosed));
            pst.setInt(24,us.ball);
            pst.setInt(25,us.answer);
            pst.setString(26,us.status);
            pst.setInt(27,us.clansman);
            pst.setString(28,us.clangroup);
            pst.setInt(29,us.wedding);
            pst.setString(30,us.car);
            pst.setString(31,us.home);
            pst.setString(32,us.clothing);
            pst.setString(33,us.animal);
            pst.execute();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }


         /*
         * !адмлист - список админ сообщений
         */
        public void admmsg(int id, int id_2, String msg, long time) {
        try {
        PreparedStatement pst = getDb().prepareStatement("insert into admmsg values(?, ?, ?, ?)");
        pst.setInt(1,id);
        pst.setInt(2,id_2);
        pst.setString(3,msg);
        pst.setTimestamp(4,new Timestamp(time));
        pst.execute();
        pst.close();
        }
        catch (Exception ex) {ex.printStackTrace();}
        }

        public void AddAButilochka(int id, String word) {
        try {
        PreparedStatement pst = (PreparedStatement) getDb().prepareStatement("insert into butilochka values(?, ?)");
        pst.setInt(1,id);
        pst.setString(2,word);
        pst.execute();
        pst.close();
        }
        catch (Exception ex) {ex.printStackTrace();}
        }


}
