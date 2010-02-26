/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import com.mysql.jdbc.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.util.Log;

/**
 * ������ � �������
 *
 * @author fraer72
 */

public class ClanWork {
public DBChat db;
private ConcurrentHashMap < Integer, Clan > Cl = new ConcurrentHashMap < Integer, Clan >();// ��� ������

public ClanWork( DBChat _db )
{
db = _db;
}

/**
  * ���� ����� ����?
  * @param id
  * @return
  */
public boolean CheckClan( int id )
{
return Cl.containsKey( id );
}

/**
  * ��������� ��� ������ �� ��
  */

public void StartClanCash()
{
ResultSet rst = null;
Statement stmt = null;
String q = "select id, leader_clan, room_clan, name_clan, ball_clan, info_clan from clans";
try{
stmt = (Statement) db.getDb().createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY );
Log.debug("EXEC: " + q);
rst = stmt.executeQuery( q );
while(rst.next()){
Clan c = new Clan();
c.setId( rst.getInt( 1 ) );
c.setLeader( rst.getInt( 2 ) );
c.setRoom( rst.getInt( 3 ) );
c.setName( rst.getString( 4 ) );
c.setBall( rst.getInt( 5 ) );
c.setInfo( rst.getString( 6 ) );
Cl.put( c.getId(), c );
}
}
catch ( Exception ex )
{
ex.printStackTrace();
}
finally
{
if( rst != null ) try{rst.close();} catch( Exception e ) {};
if( stmt != null ) try{stmt.close();} catch( Exception e ) {};
}
}

/**
  * ���������� �������� ����
  * @param id
  * @return
  */
public Clan getClan( int id )
{
if( !Cl.containsKey( id ) ) return null;
return Cl.get(id);
}

/**
  * �������� ������ �����
  * @param �
  * @return
  */
public boolean CreateClan( Clan c ){
String q = "insert into clans values(?,?,?,?,?,?)";
Log.debug( "INSERT clans id=" + c.getId() );
boolean f = false;
try {
PreparedStatement pst = db.getDb().prepareStatement( q );
pst.setInt( 1, c.getId() );
pst.setInt( 2, c.getLeader() );
pst.setInt( 3, c.getRoom() );
pst.setString( 4, c.getName() );
pst.setInt( 5, c.getBall() );
pst.setString( 6, c.getInfo() );
pst.execute();
pst.close();
Cl.put( c.getId(), c );
f = true;
}
catch (Exception ex)
{
ex.printStackTrace();
}
return f;
}

/**
  * ���������� ������ � �����
  * @param c
  * @return
  */

public boolean UpdateClan(Clan c)
{
String q = "update clans set leader_clan=?, room_clan=?, name_clan=?, ball_clan=?, info_clan=? where id=?";
Log.debug( "UPDATE clan id=" + c.getId() );
boolean f = false;
try {
PreparedStatement pst = db.getDb().prepareStatement( q );
pst.setInt( 6, c.getId() );
pst.setInt( 1, c.getLeader() );
pst.setInt( 2, c.getRoom() );
pst.setString( 3, c.getName() );
pst.setInt( 4, c.getBall() );
pst.setString( 5, c.getInfo() );
pst.execute();
pst.close();
Cl.put( c.getId(), c );
f = true;
}
catch ( Exception ex )
{
ex.printStackTrace();
}
return f;
}

public Set<Integer> getClans()
{
return Cl.keySet();
}

/**
  * �������� �����
  * @param c
  * @return
  */

public boolean DeleteClan( Clan c )
{
String q = "delete from clans where id=?";
Log.debug( "DELETE clan id=" + c.getId() );
boolean f = false;
try{
PreparedStatement pst = db.getDb().prepareStatement( q );
pst.setInt( 1, c.getId() );
pst.execute();
pst.close();
Cl.remove ( c.getId() );
f = true;
}
catch ( Exception ex )
{
ex.printStackTrace();
}
return f;
}

/**
 * �������� ����� ����� ������
 * @return
 */
public int getCountClan()
{
String q = "SELECT count(*) FROM `clans` WHERE id";
Vector< String[] > v = db.getValues( q );
return Integer.parseInt( v.get( 0 )[ 0 ] );
}

/**
 * �������� ������������ ��
 * @return
 */
public int getMaxId()
{
long z = db.getLastIndex("clans");
int Id = (int) z;
// �� ����� ������ 0 �� :)
if( Id == 0 )
{
Id += 1;
}
return Id;
}


/**
 * �������� ����� ����� �� ������������
 * ������ true ���� ����� ��� ����� ����������
 * @return
 */
public boolean TestClanName( String ClanName ) {
try{
PreparedStatement pst = ( PreparedStatement ) db.getDb().prepareStatement( "select name_clan from clans WHERE id" );
ResultSet rs = pst.executeQuery();
while( rs.next() )
{
if( ClanName.equals( rs.getString( 1 ) ) )
{
return true;    
}
}
rs.close();
pst.close();
}
catch ( Exception ex )
{
ex.printStackTrace();
}
return false;
}



}
