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

import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import ru.jimbot.Manager;
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
    long cTime=System.currentTimeMillis(); //Время последнего сообщения, для определения паузы
    long stTime = 0; //Время последнего вывода статистики
    //public ChatServer srv;
    public ConcurrentLinkedQueue <MsgElement> mq;
    public ConcurrentHashMap <String,Integer> uins;
    ConcurrentHashMap <String,Integer> test1, test2;
    private String[][] chg = {{"y","у"},{"Y","у"},{"k","к"},{"K","к"},{"e","е"},
                            {"E","е"},{"h","н"},{"H","н"},{"r","г"},{"3","з"},{"x","х"},{"X","х"},
                            {"b","в"},{"B","в"},{"a","а"},{"A","а"},{"p","р"},{"P","р"},{"c","с"},
                            {"C","с"},{"6","б"}};
    private Random r = new Random();
    int a = 0;
    long times = System.currentTimeMillis();
    long TimesDelLog = System.currentTimeMillis();

   
    /** Creates a new instance of RobAdmin */
    public RobAdmin(ChatServer s) {
        srv = s;
        mq = new ConcurrentLinkedQueue();
        uins = new ConcurrentHashMap();
        uins.put("0",0);
        test1 = new ConcurrentHashMap();
        test2 = new ConcurrentHashMap();
    }
    

   
   
    /*
    * Вывод случайной фразы для админ-бота
    */
    public int n()
    {
    long i = srv.us.db.getLastIndex("robadmin");
    String o = "" + i;
    int p = Integer.valueOf(o);
    int i1 =  getRND(p);
    return i1;
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


    
    private void parse()
    {WorkScript.getInstance(srv.getName()).startAdminScript(this);}
    
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
        cTime = System.currentTimeMillis();
        String s = NICK + ChatProps.getInstance(srv.getName()).getStringProperty("chat.delimiter") + " " + m;
        Log.getLogger(srv.getName()).info(s);
        srv.us.db.log(0,"admin","OUT", s, room);
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
    
    /**
     * Проверка на наличие приветствия
     */
    public boolean testHi(String s){
        String t = "прив;прев;здоров;здрас;здрав;хай;хой;хелл;добр;даро";
        return test(s,t.split(";"));
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
            /*say("Ну вас нафиг... нашли дурака... работай тут, считай... дайте передохнуть хоть немного.", room);*/
            return;
        }
        stTime = System.currentTimeMillis();
        String s = "Чат запущен - " + new Date(Time.getTimeStart());
        s += "\nВремя работы - " + Time.getTime(Time.getUpTime());
        s += "\nЗа последние сутки:";
        s += "\nВсего зашло в чат - " + srv.us.statUsersCount();
        s += "\nОтправлено сообщений - " + srv.us.statMsgCount();
        s += "\nЗакрыто в тюрьме юзеров - " + srv.us.statBanroomCount();
        s += "\nКикнутых юзеров - " + srv.us.statKickUsersCount();
        s += "\nВсего киков - " + srv.us.statKickCount();
        s += "\nЗабанено юзеров - " + srv.us.statBanUsersCount();
        s += "\nСамые болтливые пользователи:\n" + srv.us.statUsersTop();
        s += "\nСамые рейтинговые пользователи:\n" + srv.us.BogachiUsersTop();
        say(s, room);
    }
    
    /**
     * Проверка на первышение интервала ожидания
     */
    public boolean testTime(){
        return (System.currentTimeMillis()-cTime)>ChatProps.getInstance(srv.getName()).getIntProperty("adm.sayAloneTime")*60000;
    }
    
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
     * Приветствие
     */
    public String getHi(String name){
        String[] s = {"Привет","Хай","Приветствую","Здравствуй","Здоров"};
        return s[getRND(s.length)] + " " + name + "!";
    }
    
    /**
     * Фразы про одиночество
     */
    public String getAlone(){
        String[] s = {
            /*"Здесь так тихо...",
            "Ну и чего все замолчали?",
            "Ну и молчите дальше, я тоже буду молчать :-\\",
            "Алле! тут есть кто-нибудь? А-а-а-а!!! Я что тут один?!"*/
        };
        return s[getRND(s.length)];
    }
    
    /**
     * Фразы при упоминении админа
     */
    public String getAdmin()
    {
    a = n();
    String A = GetAdmin(a);
    return A;
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
            /*DelLog();*/
            try {
                th.sleep(sleepAmount);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }


    ///////////////////////
    //ДОПОЛНИТЕЛЬНЫЕ КОМАНДЫ
    ////////////////////////

    private void DelLog(){
    if((System.currentTimeMillis()-TimesDelLog)> 5*60000){
        // Тут удаление
    TimesDelLog = System.currentTimeMillis();
    }
    }

   public String getBanroom_say()
   {
   String[] s = {" закрыт(а) в комнату "," пнут(а) в комнату "," улетел(а) в комнату "," заперт(а) в комнате "," мда... go в комнату "," не беси меня больше а пока поседи в "};
   return s[getRND(s.length)];
   }

   public String getBanroom_say2()
   {
   String[] s = {"меньше матом ори","не матерись","за мат","мат в чате запрещен","прочти !правила","за мат я караю строга"};
   return s[getRND(s.length)];
   }

   public String getBanroom_say1()
   {
   String[] s = {"много мата","сматерился","за мат","мат в чате запрещен","пусть читает !правила","за мат я караю строга"};
   return s[getRND(s.length)];
   }

    public void close(String uin)
	{
    Users u = srv.us.getUser(uin);
    int K = srv.getProps().getIntProperty("room.tyrma");
	// Время
	int time = (int) (5+(Math.random()*55));
    long t = System.currentTimeMillis()+(time*60000);
	u.lastclosed = t;
   	// Сменим ник
	String nick = u.localnick + "(Зек)";
    u.localnick = nick;
    srv.us.db.event(u.id, uin, "REG", 0, "", nick);
    // Оповести чат
	say(u.localnick + getBanroom_say() + "|" + srv.us.getRoom(K).getName() +
	"|, на " + time + " минут, " + getBanroom_say1(), u.room);
	// Комната
    u.room = K;
    srv.us.updateUser(u);
    srv.cq.changeUserRoom(u.sn, K);
    // Оповещаем целевую комнату
    say("У вас пополнение, неудачник:  " + u.localnick +
	" он(а) закрыт(а) на " + time + " минут", K);
    // Оповещаем юзера
    srv.getIcqProcess(srv.us.getUser(uin).basesn).mq.add(srv.us.getUser(uin).sn,"Ты закрыт(а) в комнату, на "
    + time + " минут, " + getBanroom_say2());
    // Лишаем юзера прав
    srv.us.revokeUser(srv.us.getUser(uin).id, "room");
	//srv.us.revokeUser(srv.us.getUser(uin).id, "reg");
    srv.us.revokeUser(srv.us.getUser(uin).id, "psmg");
	}

    public String GetAdmin(int id)
    {
    String s = "";
    try {
    PreparedStatement pst =  (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from robadmin where id=" + id);
    ResultSet rs = pst.executeQuery();
    if(rs.next())
    {
    s = rs.getString(2);
    }
    rs.close();
    pst.close();
    } catch (Exception ex) {}
    return s;
    }

   public void AddAdmin(int id, String msg) {
   try {
   PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into robadmin values(?, ?)");
   pst.setInt(1,id);
   pst.setString(2,msg);
   pst.execute();
   pst.close();
   }
   catch (Exception ex) {ex.printStackTrace();}
   }

}
