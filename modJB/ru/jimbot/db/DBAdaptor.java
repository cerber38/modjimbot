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

package ru.jimbot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;

import ru.jimbot.util.Log;

/**
 * Базовый клас для работы с базой данных.
 * 
 * @author Prolubnikov Dmitry
 */
public abstract class DBAdaptor {
    private Connection db;
    private String host, name, user, pass;
    private long lastConnect = 0;
    
    /** Creates a new instance of DBAdaptor */
    public DBAdaptor() throws Exception {
    }
    
    public static Timestamp getTS(Timestamp t){
        return t.equals(new Timestamp(0)) ? null : t;
    }
    
    public boolean isClosed() {
    	if(db==null) return true;
    	try {
    		return db.isClosed();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return true;
    	}
    }
    
    /**
     * Закрываем соединение с БД
     */
    public void shutdown(){
            try {
                db.close();
            } catch (SQLException ex) {
            	ex.printStackTrace();
            }
    }
    
    public boolean openConnection(String host, String name, String user, String pass) {
        	while(!open(host, name, user, pass)){
//        		Log.info("Подключение к БД...");
        	}
        	return true;
    }
    
    //тут нада создать базу данных
    public abstract void createDB();
    
    public boolean open(String host, String name, String user, String pass) {
        boolean f=false;
        this.host = host; this.name = name; this.user = user; this.pass = pass;
        try {
            if (db != null) {
                if (!db.isClosed()) return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Исключаем слишком частые подключения к БД
        if((System.currentTimeMillis()-lastConnect)<30000)
            return false;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                db = DriverManager.getConnection("jdbc:mysql://" + host + "/" + name, user, pass);
            } catch (Exception ex) {
                ex.printStackTrace();
                f=false;
                lastConnect = System.currentTimeMillis();
                Log.talk("Ошибка подключения к базе данных!!!");
            }
        return f;
    }    
    
    public void executeQuery(String qry) {
    	Statement stmt = null;
        try {
            stmt = getDb().createStatement();
            Log.debug("EXEC: " + qry);
            stmt.execute(qry);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	if(stmt!=null) try{stmt.close();} catch(Exception e) {};
        }
    }
    
    public Vector<String[]> getValues(String query) {
        Vector<String[]> v = new Vector<String[]>();
        String[] ss;
        ResultSet rst=null;
        Statement stmt=null;
        try {
        	stmt = getDb().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	Log.debug("EXEC: " + query);
        	rst = stmt.executeQuery(query);
            while (!rst.isLast()) {
                ss = readNext(rst);
                if (ss != null) v.addElement(ss);
                else break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	if(rst!=null) try{rst.close();} catch(Exception e) {};
        	if(stmt!=null) try{stmt.close();} catch(Exception e) {};
        }
        return v;
    }

    public String[] readNext(ResultSet rSet) {
      String sTmp[]=null;
      int i;
      try {
        if (rSet.next()) {
          sTmp=new String [rSet.getMetaData().getColumnCount()];
          for (i = 1; i <= rSet.getMetaData().getColumnCount(); i++) {
            //if (i > 1) sTmp += '\t';
            sTmp[i-1] = rSet.getString(i);
          }
        } else sTmp=null;
      }
      catch(SQLException ex) {
        ex.printStackTrace();
        sTmp=null;
      }
      return sTmp;
    }

    public long getLastIndex(String tableName) {
        String s="";
        String q = "select max(id) as id from " + tableName;
        ResultSet rst=null;
        Statement stmt=null;
        try {
        	stmt = getDb().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	Log.debug("EXEC: " + q);
        	rst = stmt.executeQuery(q);
            s = readNext(rst)[0];
        } catch (Exception ex) {
            s="";
        } finally {
        	if(rst!=null) try{rst.close();} catch(Exception e) {};
        	if(stmt!=null) try{stmt.close();} catch(Exception e) {};
        }
        if (s==null) return 1;
        if (s.equals("")) return 1;
        if (Long.parseLong(s) <= 0) return 1;
        return Long.parseLong(s) + 1;
    }
    
    public abstract DBObject getObject(String q);
    
    public abstract Vector getObjectVector(String q);
    
    public abstract void insertObject(DBObject o);
    
    public abstract void updateObject(DBObject o);

    /**
     * Возвращает Connection текущей БД.
     * При необходимости производит подключение и инициализацию
     * @return
     * @throws SQLException 
     */
	public Connection getDb() throws SQLException {
		if(db.isClosed()){
			openConnection(host, name, user, pass);
		}
		return db;
	}
}
