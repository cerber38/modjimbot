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

import ru.jimbot.util.Time;
import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ru.jimbot.Messages;
import ru.jimbot.modules.WorkScript;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;

/**
 * Виртуальный админ в чате
 *
 * @author Prolubnikov Dmitry
 */
public class RobAdmin implements Runnable {
    public String NICK = "Админ";
    public String ALT_NICK="admin;админ";
    //Определение зафлуживания админа
    private String lastSN="";
    private long lastTime=0;
    public int lastCount = 0;
    private Thread th;
    public ChatServer srv;
    int sleepAmount = 1000;
    long cTime = System.currentTimeMillis(); //Время последнего сообщения, для определения паузы
    long stTime = 0; //Время последнего вывода статистики
    public ConcurrentLinkedQueue <MsgElement> mq;
    public ConcurrentHashMap <String,Integer> uins;
    private String[][] chg = {{"y","у"},{"Y","у"},{"k","к"},{"K","к"},{"e","е"},
                            {"E","е"},{"h","н"},{"H","н"},{"r","г"},{"3","з"},{"x","х"},{"X","х"},
                            {"b","в"},{"B","в"},{"a","а"},{"A","а"},{"p","р"},{"P","р"},{"c","с"},
                            {"C","с"},{"6","б"}};
    private Random r = new Random();
    int a = 0;
    long times = System.currentTimeMillis();
 
    /** Creates a new instance of RobAdmin */
    public RobAdmin(ChatServer s) {
        srv = s;
        mq = new ConcurrentLinkedQueue();
        uins = new ConcurrentHashMap();
        uins.put("0",0);
    }
    
    /**
     * Добавление в очередь нового задания
     */
    public void parse(IcqProtocol proc, String uin, String msg, int room){
        cTime = System.currentTimeMillis();
        mq.add(new MsgElement(msg,uin,proc,room));
    }
    
    /**
     * Замена похожих букв на русские
     */
    public String changeChar(String s){
        for(int i=0;i<chg.length;i++){
            s = s.replace(chg[i][0],chg[i][1]);
        }
        return s;
    }


    
    private void parse(){
    WorkScript.getInstance(srv.getName()).startAdminScript(this);
    }
    
    /**
     * Обработка событий по времени
     */
    private void timeEvent(){
        if(testTime()){
            cTime = System.currentTimeMillis();
            if(testRnd(ChatProps.getInstance(srv.getName()).getIntProperty("adm.sayAloneProbability"))){
                if(srv.cq.uq.size()<=0) return;
                say(getAlone(), 0);
            }
        }      
    }
    
    public void say(String m, int room){
        if(m.equals("")) return; // Если пустое сообщение
        cTime = System.currentTimeMillis();
        String s = NICK + ChatProps.getInstance(srv.getName()).getStringProperty("chat.delimiter") + " " + m;
        Log.getLogger(srv.getName()).info(s);
        srv.us.db.log(0,NICK,"OUT", s, room);
        srv.cq.addMsg(s,"",room);
    }
    
    /**
     * Проверка на мат, вариант 1
     */
    public boolean testMat1(String msg){
        String[] s = msg.trim().split(" ");
        for(int i=1;i<s.length;i++){
            if(!test(s[i], ChatProps.getInstance(srv.getName()).getStringProperty("adm.noMatString").split(";"))){
                if(test(s[i], ChatProps.getInstance(srv.getName()).getStringProperty("adm.matString").split(";")))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Проверка на совпадение слов в сообщении
     */
    public boolean test(String msg, String[] testStr){
        for(int i=0;i<testStr.length;i++){
            if(msg.toLowerCase().indexOf(testStr[i])>=0) return true;
        }
        return false;        
    }
    
    /**
     * Проверка на наличие имени админа
     */
    public boolean testName(String s){
        return test(s,ALT_NICK.split(";"));
    }
     
    public boolean testStat(String s){
        String t = "stat;стат";
        return test(s,t.split(";"));
    }
    
    public boolean testFlood(String sn){
        if(sn.equalsIgnoreCase(lastSN)){
            if((System.currentTimeMillis()-lastTime)<ChatProps.getInstance(srv.getName()).getIntProperty("adm.maxSayAdminTimeout")*60*1000){
                return true;
            } else {
                lastTime = System.currentTimeMillis();
                lastCount = 0;
                return false;
            }
        } else {
            lastSN = sn;
            lastTime = System.currentTimeMillis();
            lastCount = 0;
            return false;
        }
    }
    
    /**
     * Вывод статистики по запросу
     */
    public void sayStat(int room){
        long test = ChatProps.getInstance(srv.getName()).getIntProperty("adm.getStatTimeout")*60*1000;
        
        if((System.currentTimeMillis()-stTime)<test){
            say(Messages.getInstance(srv.getName()).getString("RobAdmin.stat.0"), room);
            return;
        }
        stTime = System.currentTimeMillis();
        String s = Messages.getInstance(srv.getName()).getString("RobAdmin.stat.1", new Object[] {
        ChatProps.getInstance(srv.getName()).getStringProperty("chat.name"),
        new Date(Time.getTimeStart()),
        Time.getTime(Time.getUpTime()),
        srv.us.statUsersCount(),
        srv.us.statMsgCount(),
        srv.us.statBanroomCount(),
        srv.us.statNoticesCount(),
        srv.us.statKickUsersCount(),
        srv.us.statKickCount(),
        srv.us.statBanUsersCount(),
        srv.us.statUsersTop(),
        srv.us.BogachiUsersTop()}) ;
        say(s, room);
    }
    
    /**
     * Проверка на первышение интервала ожидания
     */
    public boolean testTime(){
        return (System.currentTimeMillis()-cTime)>ChatProps.getInstance(srv.getName()).getIntProperty("adm.sayAloneTime")*60000;
    }

    /**
     * Вернет рандомное число
     * @param i
     * @return
     */
    
    public int getRND(int i){
        return r.nextInt(i);
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
    
    /**
     * Фразы про одиночество
     */
    public String getAlone(){
        String[] s = Messages.getInstance(srv.getName()).getString("RobAdmin.alone.0").split(";");
        return s[getRND(s.length)];
    }
    
    
    public void start(){
        th = new Thread(this);
        th.setPriority(Thread.NORM_PRIORITY);
        th.start();
    }
    
    public synchronized void stop() {
        th = null;
        notify();
    }
    
    public void run() {
        Thread me = Thread.currentThread(); 
        while (th == me) {
            parse();
            timeEvent();
            try {
                th.sleep(sleepAmount);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }

    /**
     * Рандомная враза о закрытии
     * @return
     */
    
   public String getBanroom_say_0(){
   String[] s = Messages.getInstance(srv.getName()).getString("RobAdmin.getBanroom_say.0").split(";");
   return s[getRND(s.length)];
   }

   /**
    * Рандомная причина
    * @return
    */

   public String getBanroom_say_1(){
   String[] s = Messages.getInstance(srv.getName()).getString("RobAdmin.getBanroom_say.1").split(";");
   return s[getRND(s.length)];
   }

   /**
    * Рандомная причина
    * @return
    */

   public String getBanroom_say_2(){
   String[] s = Messages.getInstance(srv.getName()).getString("RobAdmin.getBanroom_say.2").split(";");
   return s[getRND(s.length)];
   }

   /**
    * Метод закрывает нарушителя в тюрьму
    * @param uin
    */

    public void Close(String uin){
    Users u = srv.us.getUser(uin);
    int room = srv.getProps().getIntProperty("room.tyrma");
    if(((ChatCommandProc)srv.cmd).testClosed(u.sn) == 0){
    String nick = u.localnick + Messages.getInstance(srv.getName()).getString("RobAdmin.close.0");
    u.localnick = nick;
    srv.us.db.event(u.id, uin, "REG", 0, "", nick);
    }
    int time = (!ChatProps.getInstance(srv.getName()).getBooleanProperty("radm.close.random") ? (ChatProps.getInstance(srv.getName()).getIntProperty("radm.close.time")) : (getRND(ChatProps.getInstance(srv.getName()).getIntProperty("radm.close.time"))));
    if(time == 0) time = ChatProps.getInstance(srv.getName()).getIntProperty("chat.defaultKickTime");
    long t = System.currentTimeMillis()+(time*60000);
    say(Messages.getInstance(srv.getName()).getString("RobAdmin.close.1", new Object[] {u.localnick,getBanroom_say_0(),srv.us.getRoom(room).getName(),time,getBanroom_say_1()}), u.room);
    u.lastclosed = t;
    u.room = room;
    srv.us.updateUser(u);
    srv.cq.changeUserRoom(u.sn, room);
    say(Messages.getInstance(srv.getName()).getString("RobAdmin.close.2", new Object[] {u.localnick,time}), room);
    srv.getIcqProcess(srv.us.getUser(uin).basesn).mq.add(srv.us.getUser(uin).sn, Messages.getInstance(srv.getName()).getString("RobAdmin.close.3", new Object[] {time,getBanroom_say_2()}));
    srv.us.revokeUser(srv.us.getUser(uin).id, "room");
    }

    /**
     * Метод кикает нарушителя
     * @param proc
     * @param uin
     */

    public void Kick(IcqProtocol proc, String uin){
    Users u = srv.us.getUser(uin);
    say(Messages.getInstance(srv.getName()).getString("RobAdmin.kick.0", new Object[] {u.id,u.localnick}), u.room);
    ((ChatCommandProc)srv.cmd).akick(proc,uin, Messages.getInstance(srv.getName()).getString("RobAdmin.kick.1"));
    }
    
    /**
     * Предупреждение о мате
     * @param id
     * @param nick
     * @return
     */

    public String getWarning(Integer id, String nick){
    return Messages.getInstance(srv.getName()).getString("RobAdmin.warning.0", new Object[] {id,nick});
    }

    /**
     * Предупреждение о максимальном обращении
     * @param id
     * @param nick
     * @return
     */

    public String getWarning(){
    return Messages.getInstance(srv.getName()).getString("RobAdmin.warning.1");
    }

    /**
     * Вернет случайную фразу
     * @return
     */

    public String getAdmin(){
    String s = "";
    try {
    PreparedStatement pst =  (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from robadmin ORDER BY RAND( ) LIMIT 0 , 1");
    ResultSet rs = pst.executeQuery();
    if(rs.next()){
    s = rs.getString(2);
    }
    rs.close();
    pst.close();
    } catch (Exception ex) {
    ex.printStackTrace();
    }
    return s;
    }

    /**
     * Добавить фразу
     * @param id
     * @param msg
     */

   public void AddAdmin(int id, String msg) {
   try {
   PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into robadmin values(?, ?)");
   pst.setInt(1,id);
   pst.setString(2,msg);
   pst.execute();
   pst.close();
   }catch (Exception ex) {
   ex.printStackTrace();
   }
   }

   
}
