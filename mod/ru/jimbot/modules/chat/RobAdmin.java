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
 * ����������� ����� � ����
 *
 * @author Prolubnikov Dmitry
 */
public class RobAdmin implements Runnable {
    public String NICK = "�����";
    public String ALT_NICK="admin;�����";
    //����������� ������������ ������
    private String lastSN="";
    private long lastTime=0;
    public int lastCount = 0;
    private Thread th;
    public ChatServer srv;
    int sleepAmount = 1000;
    long cTime=System.currentTimeMillis(); //����� ���������� ���������, ��� ����������� �����
    long stTime = 0; //����� ���������� ������ ����������
    //public ChatServer srv;
    public ConcurrentLinkedQueue <MsgElement> mq;
    public ConcurrentHashMap <String,Integer> uins;
    ConcurrentHashMap <String,Integer> test1, test2;
    private String[][] chg = {{"y","�"},{"Y","�"},{"k","�"},{"K","�"},{"e","�"},
                            {"E","�"},{"h","�"},{"H","�"},{"r","�"},{"3","�"},{"x","�"},{"X","�"},
                            {"b","�"},{"B","�"},{"a","�"},{"A","�"},{"p","�"},{"P","�"},{"c","�"},
                            {"C","�"},{"6","�"}};
    private Random r = new Random();
    int a = 0;
    long times = System.currentTimeMillis();

   
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
    * ����� ��������� ����� ��� �����-����
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
     * ���������� � ������� ������ �������
     */
    public void parse(IcqProtocol proc, String uin, String msg, int room){
        cTime = System.currentTimeMillis();
        mq.add(new MsgElement(msg,uin,proc,room));
    }
    
    /**
     * ������ ������� ���� �� �������
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
     * ��������� ������� �� �������
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
        Log.info(s);
        srv.us.db.log(0,"admin","OUT", s, room);
        srv.cq.addMsg(s,"",room);
    }
    
    /**
     * �������� �� ���, ������� 1
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
     * �������� �� ���������� ���� � ���������
     */
    public boolean test(String msg, String[] testStr){
        for(int i=0;i<testStr.length;i++){
            if(msg.toLowerCase().indexOf(testStr[i])>=0) return true;
        }
        return false;        
    }
    
    /**
     * �������� �� ������� ����� ������
     */
    public boolean testName(String s){
        return test(s,ALT_NICK.split(";"));
    }
    
    /**
     * �������� �� ������� �����������
     */
    public boolean testHi(String s){
        String t = "����;����;������;�����;�����;���;���;����;����;����";
        return test(s,t.split(";"));
    }
    
    public boolean testStat(String s){
        String t = "stat;����";
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
     * ����� ���������� �� �������
     */
    public void sayStat(int room){
        long test = ChatProps.getInstance(srv.getName()).getIntProperty("adm.getStatTimeout")*60*1000;
        
        if((System.currentTimeMillis()-stTime)<test){
            /*say("�� ��� �����... ����� ������... ������� ���, ������... ����� ����������� ���� �������.", room);*/
            return;
        }
        stTime = System.currentTimeMillis();
        String s = "��� ������� - " + new Date(Time.getTimeStart());
        s += "\n����� ������ - " + Time.getTime(Time.getUpTime());
        s += "\n�� ��������� �����:";
        s += "\n����� ����� � ��� - " + srv.us.statUsersCount();
        s += "\n���������� ��������� - " + srv.us.statMsgCount();
        s += "\n������� � ������ ������ - " + srv.us.statBanroomCount();
        s += "\n�������� ������ - " + srv.us.statKickUsersCount();
        s += "\n����� ����� - " + srv.us.statKickCount();
        s += "\n�������� ������ - " + srv.us.statBanUsersCount();
        s += "\n����� ��������� ������������:\n" + srv.us.statUsersTop();
        s += "\n����� ����������� ������������:\n" + srv.us.BogachiUsersTop();
        say(s, room);
    }
    
    /**
     * �������� �� ���������� ��������� ��������
     */
    public boolean testTime(){
        return (System.currentTimeMillis()-cTime)>ChatProps.getInstance(srv.getName()).getIntProperty("adm.sayAloneTime")*60000;
    }
    
    public int getRND(int i){
        return r.nextInt(i);
    }
    
    /**
     * ������� � ������������ 1/i
     */
    public boolean testRnd(int i){
        if(i<=1)
            return false;
        else
            return r.nextInt(i)==1;
    }
    
    /**
     * �����������
     */
    public String getHi(String name){
        String[] s = {"������","���","�����������","����������","������"};
        return s[getRND(s.length)] + " " + name + "!";
    }
    
    /**
     * ����� ��� �����������
     */
    public String getAlone(){
        String[] s = {
            /*"����� ��� ����...",
            "�� � ���� ��� ���������?",
            "�� � ������� ������, � ���� ���� ������� :-\\",
            "����! ��� ���� ���-������? �-�-�-�!!! � ��� ��� ����?!"*/
        };
        return s[getRND(s.length)];
    }
    
    /**
     * ����� ��� ���������� ������
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
            try {
                th.sleep(sleepAmount);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }

    ///////////////////////
    //�������������� �������
    ////////////////////////

   public String getBanroom_say()
   {
   String[] s = {" ������(�) � ������� "," ����(�) � ������� "," ������(�) � ������� "," ������(�) � ������� "," ���... go � ������� "," �� ���� ���� ������ � ���� ������ � "};
   return s[getRND(s.length)];
   }

   public String getBanroom_say2()
   {
   String[] s = {"������ ����� ���","�� ��������","�� ���","��� � ���� ��������","������ !�������","�� ��� � ����� ������"};
   return s[getRND(s.length)];
   }

   public String getBanroom_say1()
   {
   String[] s = {"����� ����","����������","�� ���","��� � ���� ��������","����� ������ !�������","�� ��� � ����� ������"};
   return s[getRND(s.length)];
   }

    public void close(String uin)
	{
    Users u = srv.us.getUser(uin);
    int K = srv.getProps().getIntProperty("room.tyrma");
	// �����
	int time = (int) (5+(Math.random()*55));
    long t = System.currentTimeMillis()+(time*60000);
	u.lastclosed = t;
   	// ������ ���
	String nick = u.localnick + "(���)";
    u.localnick = nick;
    srv.us.db.event(u.id, uin, "REG", 0, "", nick);
    // �������� ���
	say(u.localnick + getBanroom_say() + "|" + srv.us.getRoom(K).getName() +
	"|, �� " + time + " �����, " + getBanroom_say1(), u.room);
	// �������
    u.room = K;
    srv.us.updateUser(u);
    srv.cq.changeUserRoom(u.sn, K);
    // ��������� ������� �������
    say("� ��� ����������, ���������:  " + u.localnick +
	" ��(�) ������(�) �� " + time + " �����", K);
    // ��������� �����
    srv.getIcqProcess(srv.us.getUser(uin).basesn).mq.add(srv.us.getUser(uin).sn,"�� ������(�) � �������, �� "
    + time + " �����, " + getBanroom_say2());
    // ������ ����� ����
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
