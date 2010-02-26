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

package ru.jimbot.modules.anek;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import ru.jimbot.db.DBAdaptor;
import ru.jimbot.db.DBObject;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class DBAneks extends DBAdaptor{
    
    /** Creates a new instance of DBAneks */
    public DBAneks() throws Exception  {
//        this.DRIVER = "org.hsqldb.jdbcDriver";
//        this.URL = "jdbc:hsqldb:file:";
//        this.dbName = "db/aneks";
    }
    
    public void createDB(){
//        if(!MainProps.isExtdb()){
//            Log.info("DB aneks not found. Create new DB...");
//            try{
//                executeQuery("CREATE CACHED TABLE ANEKS(ID INTEGER NOT NULL PRIMARY KEY,TEXT LONGVARCHAR)");
//                executeQuery("insert into aneks values (0, 'test')");
////                commit();
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
//        } else {
//            
//        }
    }
    
    public DBObject getObject(String q){
        Aneks an = new Aneks();
        Statement stm = null;
        ResultSet rs = null;
        try{
//            openQuery(q);
            stm = getDb().createStatement();
            rs = stm.executeQuery(q);
            rs.next();
            an.id = rs.getInt(1);
            an.text = rs.getString(2);
//            closeQuery();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
        	if(rs!=null) try{rs.close();}catch(Exception e){};
        	if(stm!=null) try{stm.close();}catch(Exception e){};
        }
        return an;
    }
    
    public Vector getObjectVector(String q){
        Vector v = new Vector();
        Statement stm = null;
        ResultSet rs = null;
        try{
//            openQuery(q);
            stm = getDb().createStatement();
            rs = stm.executeQuery(q);
            while(rs.next()) {
                Aneks an = new Aneks();
                an.id = rs.getInt(1);
                an.text = rs.getString(2);
                v.addElement(an);
            }
//            closeQuery();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
        	if(rs!=null) try{rs.close();}catch(Exception e){};
        	if(stm!=null) try{stm.close();}catch(Exception e){};
        }
        return v;
        
    }
    
    public void insertObject(DBObject o){
        Aneks an = (Aneks)o;
        try{
            PreparedStatement pst = getDb().prepareStatement("insert into aneks values (?, ?)");
            pst.setInt(1,an.id);
            pst.setString(2,an.text);
            pst.execute();
            pst.close();
//            commit();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
  
    public void updateObject(DBObject o){
        
    }
}
