package ru.jimbot.modules.chat;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author fraer72
 * ����������� � ����, ���� ��� :)
 */

public class Voting implements Runnable {
private HashMap commands = new HashMap();
private HashMap<String, VotingMap> VotingMap;
private ConcurrentHashMap <String, Integer> Repetition;// ����� ��� �������� ����������
private ConcurrentHashMap <String, String> Msg;// ����� ��� �������� ��������� ������������
private CommandParser parser;
private ChatCommandProc cmd;
private long TIME_CMD = 1*60000;// ����� ��������� �������
public long Vtime = System.currentTimeMillis();//�����
private Thread x;// �����
private int sleepAmount = 1000;
public boolean StartVoting = false;// ��������/��������� �����������
public int YES = 0;// ��
public int NO = 0;// ������
public int ALL_Voice = 0;// ����� ���������� ��������� �������
public int ID_Voice = 0;// �� ������������ ������ �������� ���� �����������
public int ROOM_Voice = 0;// ������� ��� �����������
public int moder = 0;// ������������ ������� ����� �����������
public final String R = "�� ����������� ����������� �� �������(�) �� ���� �� 30 ���. :)";//�������

public Voting(ChatCommandProc c)
{
parser = new CommandParser(commands);
cmd = c;
Repetition  = new ConcurrentHashMap ();
Msg = new ConcurrentHashMap ();
VotingMap = new HashMap<String, VotingMap>();
init();
}

private void init()
{
commands.put("!�����������", new Cmd("!�����������","$n",1));/*������ �����������,
��� �������� $n ������������ ������ ������ ����� �����������*/
}


public boolean commandVoting(IcqProtocol proc, String uin, String mmsg) {
String tmsg = mmsg.trim();
int tp = 0;
//
if(VotingMap.containsKey(uin))
if(!VotingMap.get( uin ).isExpire())
{
VoicesOfUsers(proc, uin, parser.parseArgs(tmsg), mmsg);
return true;//��� ����������� ��������� ��������� � �����
}
else
{
tp = parser.parseCommand(tmsg);
VotingMap.remove(uin);
Repetition.remove(uin);
Msg.remove(uin);
}else
tp = parser.parseCommand(tmsg);
int tst=0;
if(tp<0)
tst=0;
else
tst = tp;
boolean f = true;
switch (tst)
{
case 1:
Voting(proc, uin, parser.parseArgs(tmsg), mmsg);
break;
default:
f = false;
}
return f;
}

    public void Voting(IcqProtocol proc, String uin, Vector v, String mmsg)
    {
    if(!cmd.isChat(proc,uin)) return;
    if(!cmd.auth(proc,uin, "voting")) return;
    if(StartVoting){return;}// ���� ����������� ��� ����.
    Vtime = System.currentTimeMillis();// �������� ������
    int id = (Integer)v.get(0);
    Users uss = cmd.srv.us.getUser(uin);// ������������
    moder = uss.id;
    ROOM_Voice = uss.room;// �������� ������� ��� ���� �����������
    ID_Voice = id;// ������������ ������ �������� ��������
    Users u = cmd.srv.us.getUser(ID_Voice);
    StartVoting = true;// ������� �����������
    //���� ������� ������������ ��� ����������� 2 �����������
    if ((!cmd.psp.testAdmin(uss.sn)) && (!cmd.srv.us.getUserGroup(uss.id).equals("moder")) && (!cmd.srv.us.getUserGroup(uss.id).equals("admin")) && (cmd.srv.us.getCountgolosovanChange(uss.id) >= 2))
    {
    proc.mq.add(uin,uss.localnick + ", ����������� �� �������.\n�� ������ ������ 2 ���� � ����� ��������� ����������� �� ���.\n�� ��������� ���� �����. :-P");
    StartVoting = false;
    return;
    }
    // ���� ������������ ��������� ����� ��� ����� ���������� anti_kik?
    if ((cmd.psp.testAdmin(u.sn)) || (cmd.qauth(proc, u.sn, "anti_kik")))
    {
    proc.mq.add(uin,uss.localnick + " �� �� ������ ������� ����������� ������ ����� ������������, �� ������� �����������");
    StartVoting = false;
    return;
    }
    // ���� ������������ ������� ����������� ������ ����?
    if(id == uss.id)
    {
    proc.mq.add(uin,uss.localnick + " ������ ��������� ����������� ������ ����.");
    StartVoting = false;
    return;
    }
    // ���� ������������ ���������?
    if(u.id == 0)
    {
    proc.mq.add(uin,uss.localnick + ", ����������� �� �������.\n������������ � ����� ID �� ��������������� � ����.");
    StartVoting = false;
    return;
    }
    // ���� ������������ �� � ����?
    if(u.state == 1)
    {
    proc.mq.add(uin,uss.localnick + ", ����������� �� �������, ��� ��� ���� ������������ ������ �� � ����.");
    StartVoting = false;
    return;
    }
    // ���� ������������ �� � ��� �� �������?
    if(uss.room != u.room)
    {
    proc.mq.add(uin,uss.localnick + ", ����������� �� �������.\n�� ������ ���������� � ����� ������� � �������������, ������ �������� �������� �����������.");
    StartVoting = false;
    return;
    }
    cmd.srv.us.db.event(uss.id, uin, "GOLOSOVAN", u.id, u.sn, "��������� �� ���");
    Enumeration <String> e = cmd.srv.cq.uq.keys();
    while (e.hasMoreElements())
    {
    String i = e.nextElement();
    Users us = cmd.srv.us.getUser(i);
    Repetition.put(us.sn, 2);
    Msg.put(us.sn, mmsg);
    if((us.state == UserWork.STATE_CHAT) && (us.room == ROOM_Voice));
    {
    if((us.id != u.id) && (us.room == ROOM_Voice))
    {
    cmd.srv.getIcqProcess(us.basesn).mq.add(us.sn,"������������� " + uss.localnick + "|" + uss.id + "| ������ ����������� �� ��� ������������ - " + u.localnick + "|" + u.id + "|n"+
    "������� ��� �� ���� �� 30 �����, ��������� ''��'' ��� ''���'' ? (��� �������)n" +
    "��������� ''0'' (����� ����), ��� �� ����������� �� ������.n����� ���������� ~ " + VotingUsersRoom() + " ���.\n����� �� ����������� - 2 ���.");
    VotingMap.put(us.sn, new VotingMap(us.sn,  Msg.get(us.sn), Msg.get(us.sn), parser.parseArgs(mmsg), TIME_CMD));
    VoicesOfUsers(cmd.srv.getIcqProcess(us.basesn), us.sn, parser.parseArgs(mmsg), Msg.get(us.sn));
    }
    }
    }
    cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"������������ " + uss.localnick + "|" + uss.id + "| �������� ������ ���� �����������.\n���� ����������� ����������� ��, ���� ������� �� ���� �� 30 �����.");
    VotingMap.remove(u.sn);
    Repetition.remove(u.sn);
    }


    private void VoicesOfUsers(IcqProtocol proc, String uin, Vector v, String mmsg) {
    Users uss = cmd.srv.us.getUser(uin);
    Msg.put(uin, mmsg);
    String voice  = "";
    if (uss.state == UserWork.STATE_CHAT)
    {
    if(!Test(uin)){ Repetition.put(uin, 1); return;}// �������� ����� ������ �����
    if(VotingMap.containsKey(uin))
    {
    try{
    voice = Msg.get(uin);
    voice = voice.toLowerCase();//������� �������
    }
    /*����� ����������� ���������� � �����������, ��� �� ������������ ������������� ����������.*/
    catch(NumberFormatException e)
    {
    proc.mq.add(uin,uss.localnick + " ��� ���������� ������������� :)");
    return;
    }
    if ((!TestVoting(voice)) && (uss.room == ROOM_Voice))
    {
    proc.mq.add(uin,uss.localnick + " ����� ������ ���� ''��'' ��� ''���''n" +
    "��� ��������� ''0'' (����� ����), ��� �� ����������� �� ������.");
    return;
    }
    VotingMap.remove(uin);
    }
    }
    if(voice.equals("��"))
    {
    YES += 1;
    ALL_Voice += 1;
    proc.mq.add(uin,uss.localnick + " ��� ����� ����� :)n");
    }
    if(voice.equals("���"))
    {
    proc.mq.add(uin,uss.localnick + " ��� ����� ����� :)n" );
    NO += 1;
    ALL_Voice += 1;
    }
    if(voice.equals("0"))
    {
    proc.mq.add(uin,uss.localnick + " �� ������������. ������� �� �������� :)n");
    ALL_Voice += 1;
    }
    Repetition.remove(uin);
    Msg.remove(uin);
    }

    public boolean VotingTime()// ����� �����������. �� ��������� 2 ������.
    {
    return (System.currentTimeMillis()-Vtime)>120000;
    }

    public boolean Test(String uin)
    {
    if(Repetition.get(uin) == 1)
    {
    return true;// ������
    }
    else
    {
    return false;// ���
    }
    }



    public int VotingUsersRoom()
    {
    int c = 0;
    Enumeration <String> e2 = cmd.srv.cq.uq.keys();
    while(e2.hasMoreElements())
    {
    String i2 = e2.nextElement();
    Users us = cmd.srv.us.getUser(i2);
    if(us.state==UserWork.STATE_CHAT)
    {
    if(us.room == ROOM_Voice)
    {
    c++;
    }
    }
    }
    return (c-1);
    }

    public boolean TestVoting(String voice)
    {
    if(voice.equals("��") || voice.equals("���") || voice.equals("0"))//��������
    {
    return true;//���� ����� ������ �����
    }
    else
    {
    return false;//���� ����� ������ �������
    }
    }

    /*
     * ���� ��� ������ ����� �� �������� �����
     */

    public void EndVoting()
    {
    Users u = cmd.srv.us.getUser(ID_Voice);
    //���� ��� ��������� ������ �����
    if(ALL_Voice == VotingUsersRoom())
    {
    if(YES > NO){
    cmd.srv.cq.addMsg("���������� �����������:n��|������n" +
    YES + "|" + NO + "\n" +u .localnick + " ������� �� ���� �� 30 �����", "", ROOM_Voice);
    //����� ���
    cmd.tkick(cmd.srv.getIcqProcess(u.basesn), u.sn, 30, moder, R);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// ���������
    }
    else
    {
    cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " ��� �������, �� ��������� � ����.");
    cmd.srv.cq.addMsg("���������� �����������:n��|������n" +
    YES + "|" + NO + "\n" +u .localnick + " �������� � ����", "", ROOM_Voice);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// ���������
    }
    }
    }

     /*
     * ���� ��� ����������� �����.
     */

    public void EndVotingTime()
    {
    Users u = cmd.srv.us.getUser(ID_Voice);
    if(YES > NO){
    cmd.srv.cq.addMsg("���������� �����������:n��|������n" +
    YES + "|" + NO + "\n" +u .localnick + " ������� �� ���� �� 30 �����", "", ROOM_Voice);
    //����� ���
    cmd.tkick(cmd.srv.getIcqProcess(u.basesn), u.sn, 30, moder, R);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// ���������
    }
    else
    {
    cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " ��� �������, �� ��������� � ����.");
    cmd.srv.cq.addMsg("���������� �����������:n��|������n" +
    YES + "|" + NO + "\n" + u .localnick + " �������� � ����", "", ROOM_Voice);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// ���������
    }
    }

    /**
     * ��������� ������� �� �������
     */
    private void timeEvent(){
    if(StartVoting)
    {
    if(VotingTime())
    {
    EndVotingTime();// ���� ����� �����
    }
    else
    {
    EndVoting();// ���� ��� ������������ �������������
    }
    }
    }



    public void start()
    {
    x = new Thread(this);
    x.setPriority(Thread.NORM_PRIORITY);
    x.start();
    }

    public synchronized void stop()
    {
    x = null;
    notify();
    }

    public void run()
    {
    Thread me = Thread.currentThread();
    while (x == me)
    {
    timeEvent();
    try
    {
    x.sleep(sleepAmount);
    }
    catch (InterruptedException e)
    {
    break;
    }
    }
    x = null;
    }


/*
 * ��� ������ ��� �������� ����������� ������.
 */

    public class VotingMap
    {
        private String uin;
        private long vremia;
        private String msg;
        private String cmd;
        private Vector data;


        public VotingMap(String _uin, String _cmd, String _msg, Vector _data, long expire) {
        vremia = System.currentTimeMillis() + expire;
        uin = _uin;
        cmd = _cmd;
        msg = _msg;
        data = _data;
        }

        public String getMsg(){return msg;}
        public String getUin() {return uin;}
        public String getCmd() {return cmd;}
        public Vector getData() {return data;}
    public boolean isExpire() {return System.currentTimeMillis()>vremia;}
}

}