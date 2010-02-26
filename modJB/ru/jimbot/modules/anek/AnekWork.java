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
import java.util.Random;
import java.util.Vector;


/**
 *
 * @author Prolubnikov Dmitry
 */
public class AnekWork {
    public int maxAnek=0;
    public DBAneks db;
    private Random r = new Random();
    private Vector<Integer> adsKey; //= new Vector<Integer>();
    private String serviceName = "";
    private String host, name, user, pass;
    
    /** Creates a new instance of AnekWork */
    public AnekWork(String name) {
    	serviceName = name;
    	host = AnekProps.getInstance(name).getStringProperty("db.host");
    	this.name = AnekProps.getInstance(name).getStringProperty("db.dbname");
    	user = AnekProps.getInstance(name).getStringProperty("db.user");
    	pass = AnekProps.getInstance(name).getStringProperty("db.pass");
    }
    
    public void initDB() {
        try{
            db = new DBAneks();
            db.openConnection(host, name, user, pass);
            maxAnek=count();
            readAdsKey();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void closeDB(){
        try{
            db.shutdown();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
//    public void commitdb(){
//        try{
//            db.commit();
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//    }
    
    /**
     * Обновление данных при изменении БД
     */
    public void refreshData(){
    	readAdsKey();
    	maxAnek=(int)db.getLastIndex("aneks");
    }
    
    /**
     * Читает ключи к активным рекламным объявлениям
     */
    private void readAdsKey(){
    	adsKey = new Vector<Integer>();
    	Statement stm = null;
    	ResultSet rs = null;
    	try {
    		stm = db.getDb().createStatement();
    		rs = stm.executeQuery("select id from ads where enable=1");
    		while(rs.next()){
    			adsKey.add(rs.getInt(1));
    		}
    	} catch (Exception ex){
    		ex.printStackTrace();
    	} finally {
    		if(rs!=null) try{rs.close();}catch(Exception e){};
    		if(stm!=null) try{stm.close();}catch(Exception e){};
    	}
    }
    
    public String getAds(int id){
    	String s = "";
    	if(testRnd(AnekProps.getInstance(serviceName).getIntProperty("bot.adsRate"))){
    		Statement stm = null;
    		ResultSet rs = null;
    		try {
    			stm = db.getDb().createStatement();
    			rs = stm.executeQuery("select txt from ads where id="+id);
    			rs.next();
    			s = rs.getString(1);
    			addLogAds(id);
    		} catch (Exception ex){
                ex.printStackTrace();
            } finally {
            	if(rs!=null) try{rs.close();}catch(Exception e){};
            	if(stm!=null) try{stm.close();}catch(Exception e){};
            }
    	}
    	return s;
    }
    
    public void addLogAds(int id){
    	try{
            PreparedStatement pst = db.getDb().prepareStatement("insert into ads_log values (?, ?, ?)");
            pst.setInt(1,id);
            pst.setTimestamp(2,null);
            pst.setString(3, "0");
            pst.execute();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * Статистика показа рекламы за сутки
     * @return
     */
    public String adsStat(){
    	String s = "Статистика показа рекламы:\n";
    	Statement stm = null;
		ResultSet rs = null;
		try {
			stm = db.getDb().createStatement();
			rs = stm.executeQuery("SELECT ads_id, count( ads_id ) cnt " +
					"FROM `ads_log` " +
					"WHERE (to_days( now( ) ) - to_days( time )) <=1 " +
					"GROUP BY ads_id ORDER BY ads_id");
			while(rs.next()){
				s += rs.getString(1) + " - " + rs.getString(2) + "\n";
			}
		} catch (Exception ex){
            ex.printStackTrace();
        } finally {
        	if(rs!=null) try{rs.close();}catch(Exception e){};
        	if(stm!=null) try{stm.close();}catch(Exception e){};
        }
        return s;
    }
    
    /**
     * Возвращает случайное объявление, или ""
     * @return
     */
    public String getAds(){
    	if(!AnekProps.getInstance(serviceName).getBooleanProperty("bot.useAds"))
    		return "";
    	String s = getAds(adsKey.get(r.nextInt(adsKey.size())));
    	if(s.equals(""))
    		return "";
    	else
    		return "\n***\n" + s;
    }
    
    /**
     * Событие с вероятностью 1/i
     */
    public boolean testRnd(int i){
        if(i<=1)
            return false;
        else
            return r.nextInt(i)==1;
    }
    
    public String getAnek(int id) {
        String s = "";
//    	Aneks a;
        if(id<1 || id>maxAnek) return "Нет такого анека";
        Statement stm = null;
		ResultSet rs = null;
		try {
			stm = db.getDb().createStatement();
			rs = stm.executeQuery("select text from aneks where id=" + id);
			while(rs.next()){
				s = rs.getString(1);
			}
		} catch (Exception ex){
            ex.printStackTrace();
        } finally {
        	if(rs!=null) try{rs.close();}catch(Exception e){};
        	if(stm!=null) try{stm.close();}catch(Exception e){};
        }
//        a = (Aneks)db.getObject("select * from aneks where id=" + id);
        return "Анекдот №" + id + "\n" + s + getAds();
    }
    
    public int count() {
        return maxAnek==0 ? (int)db.getLastIndex("aneks") : maxAnek;
    }
    
    public String getAnek(){
        Random r = new Random();
//        long i = db.getLastIndex("db.aneks");
        long i = (long)maxAnek;
        int t = r.nextInt((int)(i-2)) +1;
        return getAnek(t);
    }
    
    public void addAnek(String s) {
        int i = (int)db.getLastIndex("aneks");
        Aneks a = new Aneks(i,s);
        db.insertObject(a);
        maxAnek=(int)i;
    }
    
    /**
     * Добавление анекдота во временную таблицу
     * @param s
     */
    public void addTempAnek(String s, String uin){
    	try{
            PreparedStatement pst = db.getDb().prepareStatement("insert into aneks_tmp values (?, ?, ?)");
            pst.setString(1,null);
            pst.setString(2,s);
            pst.setString(3, uin);
            pst.execute();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
