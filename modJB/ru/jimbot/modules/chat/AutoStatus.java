/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import ru.jimbot.Manager;

/**
 * Авто смена x-статуса
 * @author fraer72
 *
 *   public static final int SEX = 35;
 *   public static final int RULOVE = 36;
 *   public static final int SMOKING = 37;
 *
 */


public class AutoStatus implements Runnable {
private int sleepAmount = 1000;
private Thread th;
private long time = System.currentTimeMillis();
private Random r = new Random();
private ChatServer srv;
private int id = 0;
private int number = 0;
private String text = "";
private Vector hist = new Vector();

public AutoStatus(ChatServer s) {
        srv = s;
}

private int getRND(int i)
{
return r.nextInt(i);
}

/**
 * Тест на повтор
 * @param id
 */

private boolean TestRepetition (int id){
    for(Enumeration e = hist.elements(); e.hasMoreElements();) {
    if((Integer)e.nextElement() == id ){
    setXStatus();
    time = System.currentTimeMillis();
    return false;
    }
    }
    return true;
}

private int Random_ID()
{
long i = srv.us.db.getLastIndex("xstatus");
    /*if(getRND((int)i)==0){
    return 1;
    }*/
return getRND((int)i);
}

private void setXStatus(){
id = Random_ID();// Случайный ид
if(TestRepetition(id)){
number = GetNumber(id);// Номер
text = GetText(id);// Текст
// Проверим номер
if(number < 1 || number > 37){
hist.add(id);
return; // Если номер не верный!
}
// Проверим текст
if(text.trim().equals("")){
hist.add(id);
return; // Если текст не верный!
}
ChatProps.getInstance(srv.getName()).setIntProperty( "icq.xstatus", number );
ChatProps.getInstance(srv.getName()).setStringProperty("icq.STATUS_MESSAGE2", text);
Manager.getInstance().getService(srv.getName()).getProps().save();
// Перебираем все уины
for(int uins = 0; uins < srv.con.uins.count(); uins++)
{
if(srv.con.uins.proc.get(uins).isOnLine())// Если номер онлайн, то
{
srv.con.uins.proc.get(uins).setXStatusNumber(number);// меняем статус.
}
}
hist.add(id);
}
}

public void start()
{
th = new Thread(this);
th.setPriority(Thread.NORM_PRIORITY);
th.start();
}

public synchronized void stop()
{
th = null;
notify();
}

public void run()
{
Thread me = Thread.currentThread();
while (th == me)
{
timeEvent();
try {
Thread.sleep(sleepAmount);
}
catch (InterruptedException e)
{
break;
}
}
th=null;
}

    private void timeEvent() {
       if((time - System.currentTimeMillis())>ChatProps.getInstance(srv.getName()).getIntProperty( "auto_status.time")){
        if(getCountStatus() == 0){
            time = System.currentTimeMillis();
           return; // Если нет статусов в БД
        }
           if(hist.size() >= getCountStatus()){
            hist.removeAllElements();
        }
        setXStatus();
         time = System.currentTimeMillis();
     }
    }

    public int getCountStatus()
    {
    String q = "SELECT count(*) FROM `xstatus` WHERE id";
    Vector<String[]> v = srv.us.db.getValues(q);
    return Integer.parseInt(v.get(0)[0]);
    }

    public String GetText(int id)
    {
    String texts = "";
    try {
    PreparedStatement pst =  (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from xstatus where id=" + id);
    ResultSet rs = pst.executeQuery();
    if(rs.next())
    {
    texts = rs.getString(3);
    }
    rs.close();
    pst.close();
    } catch (Exception ex) {}
    return texts;
    }

    public int GetNumber(int id)
    {
    int numbers = 0;
    try {
    PreparedStatement pst =  (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from xstatus where id=" + id);
    ResultSet rs = pst.executeQuery();
    if(rs.next())
    {
    numbers = rs.getInt(2);
    }
    rs.close();
    pst.close();
    } catch (Exception ex) {}
    return numbers;
    }

}
