import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Enumeration;
import ru.jimbot.*;
import ru.jimbot.util.*;
import java.util.Random;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.modules.chat.RobAdmin;
import ru.jimbot.modules.chat.MsgElement;
import ru.jimbot.modules.chat.ChatProps;
import ru.jimbot.modules.chat.ChatCommandProc;
import java.util.Random; 
import ru.jimbot.modules.chat.Users;
import java.sql.*;

    // RobAdmin adm;
    adm.NICK = "�����:D";
    adm.ALT_NICK="�����;admin";


    
     // ���� �� �����������
    public boolean testHi(String s)
	{
    String t = "����;����;������;�����;�����;���;���;����;����;����;�����";
    return adm.test(s,t.split(";"));
    }
    public String getHi(String name)
	{
    String[] s = {"������","���","�����������","����������","������","���... ��� ����� ��, ������"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // ���� �� ��������
    public boolean testBYE(String s)
	{
    String t = "����;������;���������;�����;�� ��������;��� ���";
    return adm.test(s,t.split(";"));
    }
    public String getBYE(String name)
	{
    String[] s = {"����","������","���������","�����","�� ��������","��� ���","����������","��� ���","���� ���"};
    return name + " " + s[adm.getRND(s.length)];
    }


    // ���� �� ������ (1)
    public boolean testQuestion1(String s)
	{
    String t = "�� �������;��� �������;�� �������;��� �������;��� �����������;�� �������;��� �������;��� ������";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion1(String name){
    String[] s = {"�� ��� � ���� �������...","������ ������ �����!","���� ����� �� ������.:)","������ �������� �������...","������ �������","���� ����???"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // ���� �� ������ (2)
    public boolean testQuestion2(String s)
	{
    String t = "��� ����;��� ���;��� ���;��� �����;��� ����� �������;��� ���������;��� ������;��� ��";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion2(String name)
	{
    String[] s = {"���� �� ������!","�������...","������������:)","���������!","����� ����, � ��???"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // ���� �� ������ (3)
    public boolean testQuestion3(String s)
	{
    String t = "������";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion3(String name)
	{
    String[] s = {"������ ���, ���������!","������!","�� ���� ��������, �� ������, �� ��� �����������!"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // ���� �� ������ (4)
    public boolean testQuestion4(String s)
    {
    String t = "?;�����;�����;���;����;����;����;���;���";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion4(String name)
    {
    String[] s = {"������ �������� �� �������!","����� ������ �����, ����� ������ �����","� ���������� �� ������"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // ���� �� ����������� ������
    public boolean testSKD(String s)
    {
    String t = "���;���;���;���;�����;����;���������;����;����;�������;����;���;����;����;����;����;����;����;����;������;������;����;����;����";
    return adm.test(s,t.split(";"));
    }
    public String getSKD(String name)
    {
    String[] s = {"��� �����!","� �� �� �����","�� �� �����!","�������� ����!!! ������ ��������!","��� ������ ������� ������?! ������","� �� ���� ������???"};
    return name + " " + s[adm.getRND(s.length)];
    }
	
    /*����������*/
    public int n_inf()
    {
    long i = adm.srv.us.db.getLastIndex("inforob");
    String o = "" + i;
    int p = Integer.valueOf(o);
    int i1 = adm.getRND(p);
    return i1;
    }

    public String GetInfo(int id)
    {
    String s = "";
    try {
    PreparedStatement pst = adm.srv.us.db.getDb().prepareStatement("select * from inforob where id=" + id);
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

	if(adm.srv.getProps().getBooleanProperty("adm.Informer")){
    Object times = Manager.getInstance().getData("times");
    if (times == null || times < System.currentTimeMillis())
    {
    Manager.getInstance().setData("times", System.currentTimeMillis() + adm.srv.getProps().getIntProperty("adm.Informer.time") *60000);// �������� ������ ����������
    Set rid = new HashSet();
    Enumeration e = adm.srv.cq.uq.keys();
    while(e.hasMoreElements())
    {
    String i = e.nextElement();
    Users us = adm.srv.us.getUser(i);
    if(us.state==adm.srv.us.STATE_CHAT)
    rid.add(us.room);
    }
    for (int i:rid)
    {
    adm.say(GetInfo(n_inf()), i); // ���������� ����� �� ��� �������
    }
    }
    }	
	
    if (adm.mq.isEmpty()) return;
    MsgElement ms = adm.mq.poll();
    if(adm.srv.getProps().getBooleanProperty("adm.useMatFilter") &&
    adm.testMat1(adm.changeChar(ms.msg))&& 
	ms.room != adm.srv.getProps().getIntProperty("room.tyrma") && 
	!adm.srv.getProps().testAdmin(ms.uin)) 
	{
    int i=0;
    if(!adm.uins.containsKey(ms.uin))
	{
    adm.uins.put(ms.uin,i);
    } 
	else 
	{
    i=adm.uins.get(ms.uin);
    i++;
    adm.uins.put(ms.uin,i);
    }
    
	if(i>=3) 
	{ 
	adm.close(ms.uin);
    }	
    return;
    }

    if(!adm.srv.getProps().getBooleanProperty("adm.useSayAdmin"))
    return;  
    if(((ChatCommandProc)adm.srv.cmd).Quiz.TestRoom(ms.room)){return;} 	
	
	// ���� �� �����������
    if(adm.testName(ms.msg) && testHi(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getHi(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
    // ���� �� ��������
    if(adm.testName(ms.msg) && testBYE(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getBYE(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
    
	// ���� �� ����������� ������
    if(adm.testName(ms.msg) && testSKD(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getSKD(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
	// ���� �� ������ (1)
	if(adm.testName(ms.msg) && testQuestion1(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion1(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
	// ���� �� ������ (2)
    if(adm.testName(ms.msg) && testQuestion2(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion2(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }

	// ���� �� ������ (3)
    if(adm.testName(ms.msg) && testQuestion3(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion3(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
    
	// ���� �� ������ (4)
    if(adm.testName(ms.msg) && testQuestion4(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion4(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
	// ���� �� ����
    if(adm.testName(ms.msg) && adm.testStat(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminstat")) return;
    adm.sayStat(ms.room);
    return;
    }
    
    // ���� �� ��������� � ������    
    if(adm.testName(ms.msg)){
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    if(adm.testFlood(ms.uin))
	{
    adm.lastCount++;
    if(adm.lastCount == (adm.srv.getProps().getIntProperty("adm.maxSayAdminCount")-1))
	{
    adm.say("�������... �� ������!", ms.room);
    } 
	else if(adm.lastCount >= adm.srv.getProps().getIntProperty("adm.maxSayAdminCount"))
	{
    ((ChatCommandProc)adm.srv.cmd).akick(ms.proc,ms.uin);
    adm.lastCount =0;
    } 
	else 
	{
    adm.say(adm.srv.us.getUser(ms.uin).localnick + " " + adm.getAdmin(), ms.room);
    }
    } 
	else 
	{
    adm.say(adm.srv.us.getUser(ms.uin).localnick + " " + adm.getAdmin(), ms.room);
    }
    }
    return;
