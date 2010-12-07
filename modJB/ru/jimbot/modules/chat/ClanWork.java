/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.util.Log;

/**
 * Работа с кланами
 *
 * @author fraer72
 */

public class ClanWork {
public DBChat db;
private ConcurrentHashMap < Integer, Clan > Cl = new ConcurrentHashMap < Integer, Clan >();// Кеш кланов

public ClanWork(DBChat _db){
db = _db;
}

/**
  * Есть такой клан?
  * @param id
  * @return
  */
public boolean CheckClan( int id ){
return Cl.containsKey( id );
}

/**
  * Заполняет кеш кланов из БД
  */

public void StartClanCash(){
ResultSet rst = null;
Statement stmt = null;
String q = "select id, leader_clan, room_clan, name_clan, ball_clan, info_clan, symbol_clan from clans";
try{
stmt = (Statement) db.getDb().createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY );
Log.getDefault().debug("EXEC: " + q);
rst = stmt.executeQuery(q);
while(rst.next()){
Clan c = new Clan();
c.setId( rst.getInt(1));
c.setLeader(rst.getInt(2));
c.setRoom(rst.getInt(3));
c.setName(rst.getString(4));
c.setBall(rst.getInt(5));
c.setInfo(rst.getString(6));
c.setSymbol(rst.getString(7));
Cl.put( c.getId(), c );
}
}catch ( Exception ex ){
ex.printStackTrace();
}
finally
{
if( rst != null ) try{rst.close();} catch( Exception e ) {};
if( stmt != null ) try{stmt.close();} catch( Exception e ) {};
}
}

/**
  * Возвращает заданный клан
  * @param id
  * @return
  */
public Clan getClan( int id ){
if( !Cl.containsKey( id ) ) return null;
return Cl.get(id);
}

/**
  * Создание нового клана
  * @param с
  * @return
  */
public boolean CreateClan( Clan c ){
String q = "insert into clans values(?,?,?,?,?,?,?)";
Log.getDefault().debug("INSERT clans id=" + c.getId());
boolean f = false;
try {
PreparedStatement pst = db.getDb().prepareStatement(q);
pst.setInt(1, c.getId());
pst.setInt(2, c.getLeader());
pst.setInt(3, c.getRoom());
pst.setString(4, c.getName());
pst.setInt(5, c.getBall());
pst.setString(6, c.getInfo());
pst.setString(7, c.getSymbol());
pst.execute();
pst.close();
Cl.put(c.getId(), c);
f = true;
}catch (Exception ex){
ex.printStackTrace();
}
return f;
}

/**
  * Обновление данных о клане
  * @param c
  * @return
  */

public boolean UpdateClan(Clan c){
String q = "update clans set leader_clan=?, room_clan=?, name_clan=?, ball_clan=?, info_clan=?, symbol_clan=? where id=?";
Log.getDefault().debug( "UPDATE clan id=" + c.getId() );
boolean f = false;
try {
PreparedStatement pst = db.getDb().prepareStatement( q );
pst.setInt(7, c.getId());
pst.setInt(1, c.getLeader());
pst.setInt(2, c.getRoom() );
pst.setString(3, c.getName());
pst.setInt(4, c.getBall() );
pst.setString(5, c.getInfo());
pst.setString(6, c.getSymbol());
pst.execute();
pst.close();
Cl.put(c.getId(), c);
f = true;
}catch ( Exception ex ){
ex.printStackTrace();
}
return f;
}

public Set<Integer> getClans(){
return Cl.keySet();
}

/**
  * Удаление клана
  * @param c
  * @return
  */

public boolean DeleteClan( Clan c ){
String q = "delete from clans where id=?";
Log.getDefault().debug("DELETE clan id=" + c.getId());
boolean f = false;
try{
PreparedStatement pst = db.getDb().prepareStatement(q);
pst.setInt(1, c.getId());
pst.execute();
pst.close();
Cl.remove (c.getId());
f = true;
}catch ( Exception ex ){
ex.printStackTrace();
}
return f;
}

/**
 * Получаем общее число кланов
 * @return
 */
public int getCountClan(){
String q = "SELECT count(*) FROM `clans` WHERE id";
Vector< String[] > v = db.getValues( q );
return Integer.parseInt( v.get( 0 )[ 0 ] );
}

/**
 * Получаем максимальный ид
 * @return
 */
public int getMaxId(){
return (int) db.getLastIndex("clans") == 0 ? 1 : (int) db.getLastIndex("clans");
}


/**
 * Проверка имени клана на уникальность
 * Вернет true если такое имя клана существует
 * @return
 */
public boolean TestClanName(String ClanName) {
try{
PreparedStatement pst = ( PreparedStatement ) db.getDb().prepareStatement("select name_clan from clans WHERE id");
ResultSet rs = pst.executeQuery();
while(rs.next()){
if( ClanName.equals(rs.getString(1))){
return true;    
}
}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
return false;
}



}
