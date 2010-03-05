/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import ru.jimbot.Manager;
import ru.jimbot.util.Log;

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
private ChatServer srv;
private int id = 0;
private int number = 0;
private String text = "";
private boolean testType = false;

public AutoStatus(ChatServer s) {
        srv = s;
}


private void setXStatus(){
Random_ID();// Случайны ид
number = GetNumber(id);// Номер
text = GetText(id);// Текст
// Проверим номер
if(number < 1 || number > 37){
Log.getLogger(srv.getName()).error("Не правельный номер статуса в автосмене: " + number);
return; // Если номер не верный!
}
// Проверим текст
if(text.trim().equals("")){
Log.getLogger(srv.getName()).error("Не правельный текст статуса в автосмене");
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
Log.getLogger(srv.getName()).talk("Change auto x-status number - " + number + " and text -  " + text);
setType(id);
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
        // Первый запуск? Если да то везде ставим type=0
        if(!testType){
            setTypeAll();
            Log.getLogger(srv.getName()).talk("Change auto x-status dump");
            testType = true;
        }
       if((System.currentTimeMillis() - time)>ChatProps.getInstance(srv.getName()).getIntProperty( "auto_status.time")*60000){
           if(getCountStatus() == 0){
            time = System.currentTimeMillis();
            Log.getLogger(srv.getName()).error("Нет статусов в бд для автосмены");
           return; // Если нет статусов в БД
        }
        // Если задействованы были все статусы
        if(getCountType() >= getCountStatus()){
          Log.getLogger(srv.getName()).talk("Change auto x-status dump");
          setTypeAll();
        }
        setXStatus();
         time = System.currentTimeMillis();
     }
    }

    /**
     * Получаем максимальный ид
     * @return
     */

    public int getCountStatus()
    {
    String q = "SELECT count(*) FROM `xstatus` WHERE id";
    Vector<String[]> v = srv.us.db.getValues(q);
    return Integer.parseInt(v.get(0)[0]);
    }

    /**
     * Получаем количество статусов которые уже были задействованы (Для исключения повторов)
     * @return
     */

    public int getCountType()
    {
    String q = "SELECT count(*) FROM `xstatus` WHERE type=1";
    Vector<String[]> v = srv.us.db.getValues(q);
    return Integer.parseInt(v.get(0)[0]);
    }

    /**
     * Метод устанавливает во всех полях type=0
     * Вызывается при старте, и когда все статусы были задействованы.
     * Служит для исключения повторов.
     */

    public void setTypeAll(){
    // формируем цикл
    for(int i = 0; i >= getCountStatus(); i++){
    srv.us.db.executeQuery("update xstatus set type=0 where id=" + i);
    }
    }

    /**
     * Ставим type=1 если статус был задействован
     * @param id
     */

    public void setType( int id ){
    srv.us.db.executeQuery("update xstatus set type=1 where id=" + id);
    }

    /**
     * Получаем текст статуса из БД
     * @param id
     * @return
     */

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

     /**
     * Получаем номер статуса из БД
     * @param id
     * @return
     */

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

/**
 * Получаем рандомный ид, из тех статусов где type=0
 * @return
 */

private void Random_ID()
{
    try {
    PreparedStatement pst =  (PreparedStatement) srv.us.db.getDb().prepareStatement("SELECT id FROM xstatus WHERE type=0 ORDER BY RAND( ) LIMIT 0 , 1");
    ResultSet rs = pst.executeQuery();
    if(rs.next())
    {
    id = rs.getInt(1);
    }
    rs.close();
    pst.close();
    } catch (Exception ex) {}
}

}
