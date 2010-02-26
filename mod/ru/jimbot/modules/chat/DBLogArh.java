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
import java.sql.Timestamp;
import java.util.Vector;

import ru.jimbot.db.DBAdaptor;
import ru.jimbot.db.DBObject;
import ru.jimbot.util.Log;

/**
 * Архив базы логов
 * @author Prolubnikov Dmitry
 */
public class DBLogArh extends DBAdaptor implements Runnable{
    private Thread th;
    static DBChat dbc;
    
    public DBLogArh() throws Exception {
//        this.DRIVER = "org.hsqldb.jdbcDriver";
//        this.URL = "jdbc:hsqldb:file:";
//        this.dbName = "db/log_arh";
//        this.openConnection();
    }

    public void createDB(){
//        Log.info("DB log_arh not found. Create new DB...");
//        try{
//            executeQuery("CREATE CACHED TABLE LOG(ID BIGINT, " +
//                    "TIME TIMESTAMP,USER_ID INTEGER, user_sn varchar(50), TYPE VARCHAR(10)," +
//                    "MSG LONGVARCHAR, room integer)");
////            commit();
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
    }
    
    public void setDB(DBChat _dbc){
        dbc = _dbc;
    }
    
    public void start(){
        th = new Thread(this);
        th.setPriority(Thread.MIN_PRIORITY);
        th.start();
    }
    
    public synchronized void stop() {
        th = null;
        try{
        	getDb().close();
            dbc=null;
        } catch (Exception ex){
            ex.printStackTrace();
        }        
        notify();
    }
    
    public void run() {
        Thread me = Thread.currentThread(); 
        while (th == me) {
            if(!step()) stop();
            try {
                th.sleep(500);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }
    
    private boolean step(){
        if(copyRecords(dbc)>10000) return true;
        return false;
    }
    
    public int copyRecords(DBChat dbc){
        //Выводим статистику о базах
        Vector v = dbc.getValues("select min(time) from LOG");
        Log.info("Запуск архивации базы сообщений пользователя");
        Log.info("Начало базы сообщений: " + ((String[])v.get(0))[0]);
        v = dbc.getValues("select count(*) from LOG");
        Log.info("Всего сообщений в базе: " + ((String[])v.get(0))[0]);
        v = this.getValues("select count(*) from LOG");
        Log.info("Всего сообщений в архиве: " + ((String[])v.get(0))[0]);
        v = dbc.getValues("select min(id) from LOG");
        long id = Long.parseLong(((String[])v.get(0))[0]);
        long last = System.currentTimeMillis() - 1000*3600*24;
        int i=0;
        try{
            PreparedStatement pst = dbc.getDb().prepareStatement("select * from LOG t where time<? and id<"+(id+20000) + " order by time");
            pst.setTimestamp(1,new Timestamp(last));
            ResultSet rs = pst.executeQuery();
            PreparedStatement pst1 = this.getDb().prepareStatement("insert into log values(?, ?, ?, ?, ?, ?, ?)");
            while(rs.next()){
                pst1.setLong(1, rs.getLong(1));
                pst1.setTimestamp(2,rs.getTimestamp(2));
                pst1.setInt(3,rs.getInt(3));
                pst1.setString(4,rs.getString(4));
                pst1.setString(5,rs.getString(5));
                pst1.setString(6,rs.getString(6));
                pst1.setInt(7,rs.getInt(7));
                pst1.execute();
                i++;
            }
            pst1.close();
            rs.close();
            pst.close();
            Log.info("Удаление записей");
            pst = dbc.getDb().prepareStatement("delete from LOG t where time<? and id<"+(id+20000));
            pst.setTimestamp(1,new Timestamp(last));
            pst.execute();
            pst.close();
            Log.info("Архивация завершена, всего записей перенесено: " + i);
            this.getDb().commit();
        } catch (Exception ex){
            ex.printStackTrace();
            Log.info("В результате выполнения призошла ошибка: " + ex.getMessage() + "\nПеренесено записей:" + i);
            stop();
        }
        return i;
        
    }
    
    public DBObject getObject(String q) {return null;}
    
    public Vector getObjectVector(String q) {return null;}
    
    public void insertObject(DBObject o) {}
    
    public void updateObject(DBObject o) {}
    
}
