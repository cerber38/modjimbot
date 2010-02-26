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
 * Голосование в чате, приз КИК :)
 */

public class Voting implements Runnable {
private HashMap commands = new HashMap();
private HashMap<String, VotingMap> VotingMap;
private ConcurrentHashMap <String, Integer> Repetition;// Масив для хранения информации
private ConcurrentHashMap <String, String> Msg;// Масив для хранения сообщений пользователя
private CommandParser parser;
private ChatCommandProc cmd;
private long TIME_CMD = 1*60000;// Время временной команды
public long Vtime = System.currentTimeMillis();//Время
private Thread x;// поток
private int sleepAmount = 1000;
public boolean StartVoting = false;// Включить/Выключить голосование
public int YES = 0;// ЗА
public int NO = 0;// ПРОТИВ
public int ALL_Voice = 0;// Общее количество возможных голосов
public int ID_Voice = 0;// Ид пользователя против которого идет голосование
public int ROOM_Voice = 0;// Комната для голосования
public int moder = 0;// Пользователь который начал голосование
public final String R = "По результатам голосования ты велетел(а) из чата на 30 мин. :)";//Причина

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
commands.put("!голосование", new Cmd("!голосование","$n",1));/*Начать голосование,
где аргумент $n пользователь против котого будет голосование*/
}


public boolean commandVoting(IcqProtocol proc, String uin, String mmsg) {
String tmsg = mmsg.trim();
int tp = 0;
//
if(VotingMap.containsKey(uin))
if(!VotingMap.get( uin ).isExpire())
{
VoicesOfUsers(proc, uin, parser.parseArgs(tmsg), mmsg);
return true;//при голосовании невыводим сообщения в поток
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
    if(StartVoting){return;}// Если голосование уже идет.
    Vtime = System.currentTimeMillis();// Запустим таймер
    int id = (Integer)v.get(0);
    Users uss = cmd.srv.us.getUser(uin);// Пользователь
    moder = uss.id;
    ROOM_Voice = uss.room;// Запомним комнату где идет голосование
    ID_Voice = id;// Пользователь против которого голосуем
    Users u = cmd.srv.us.getUser(ID_Voice);
    StartVoting = true;// Включим голосование
    //Если простой пользователь уже использовал 2 голосования
    if ((!cmd.psp.testAdmin(uss.sn)) && (!cmd.srv.us.getUserGroup(uss.id).equals("moder")) && (!cmd.srv.us.getUserGroup(uss.id).equals("admin")) && (cmd.srv.us.getCountgolosovanChange(uss.id) >= 2))
    {
    proc.mq.add(uin,uss.localnick + ", голосование не создано.\nВы можете только 2 раза в сутки создавать голосование за КИК.\nВы исчерпали свой лимит. :-P");
    StartVoting = false;
    return;
    }
    // Если пользователь системный админ или имеет полномочие anti_kik?
    if ((cmd.psp.testAdmin(u.sn)) || (cmd.qauth(proc, u.sn, "anti_kik")))
    {
    proc.mq.add(uin,uss.localnick + " вы не можете создать голосование против этого пользователя, он владеет иммунитетом");
    StartVoting = false;
    return;
    }
    // Если пользователь создает голосование против себя?
    if(id == uss.id)
    {
    proc.mq.add(uin,uss.localnick + " нельзя создавать голосование против себя.");
    StartVoting = false;
    return;
    }
    // Если пользователь незареган?
    if(u.id == 0)
    {
    proc.mq.add(uin,uss.localnick + ", голосование не создано.\nПользователь с таким ID не зарегистрирован в чате.");
    StartVoting = false;
    return;
    }
    // Если пользователь не в чате?
    if(u.state == 1)
    {
    proc.mq.add(uin,uss.localnick + ", голосование не создано, так как этот пользователь сейчас не в чате.");
    StartVoting = false;
    return;
    }
    // Если пользователь не в той же комнате?
    if(uss.room != u.room)
    {
    proc.mq.add(uin,uss.localnick + ", голосование не создано.\nВы должны находиться в одной комнате с пользователем, против которого создаете голосование.");
    StartVoting = false;
    return;
    }
    cmd.srv.us.db.event(uss.id, uin, "GOLOSOVAN", u.id, u.sn, "голосовал за кик");
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
    cmd.srv.getIcqProcess(us.basesn).mq.add(us.sn,"Пользователем " + uss.localnick + "|" + uss.id + "| начато голосование за КИК пользователя - " + u.localnick + "|" + u.id + "|n"+
    "Выпнуть его из чата на 30 минут, отправьте ''да'' или ''нет'' ? (без ковычек)n" +
    "Отправьте ''0'' (цифру ноль), что бы воздержатся от ответа.nЧисло голосующих ~ " + VotingUsersRoom() + " чел.\nВремя на голосование - 2 мин.");
    VotingMap.put(us.sn, new VotingMap(us.sn,  Msg.get(us.sn), Msg.get(us.sn), parser.parseArgs(mmsg), TIME_CMD));
    VoicesOfUsers(cmd.srv.getIcqProcess(us.basesn), us.sn, parser.parseArgs(mmsg), Msg.get(us.sn));
    }
    }
    }
    cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + uss.localnick + "|" + uss.id + "| запустил против тебя голосование.\nЕсли большинство проголосует ЗА, тебя выкинет из чата на 30 минут.");
    VotingMap.remove(u.sn);
    Repetition.remove(u.sn);
    }


    private void VoicesOfUsers(IcqProtocol proc, String uin, Vector v, String mmsg) {
    Users uss = cmd.srv.us.getUser(uin);
    Msg.put(uin, mmsg);
    String voice  = "";
    if (uss.state == UserWork.STATE_CHAT)
    {
    if(!Test(uin)){ Repetition.put(uin, 1); return;}// Исключим сразу второй заход
    if(VotingMap.containsKey(uin))
    {
    try{
    voice = Msg.get(uin);
    voice = voice.toLowerCase();//опустим регистр
    }
    /*Можно закоментить оповещение в исключениях, что бы незаставлять пользователей голосовать.*/
    catch(NumberFormatException e)
    {
    proc.mq.add(uin,uss.localnick + " вам необходимо проголосовать :)");
    return;
    }
    if ((!TestVoting(voice)) && (uss.room == ROOM_Voice))
    {
    proc.mq.add(uin,uss.localnick + " голос должен быть ''да'' или ''нет''n" +
    "Или отправьте ''0'' (цифру ноль), что бы воздержатся от ответа.");
    return;
    }
    VotingMap.remove(uin);
    }
    }
    if(voice.equals("да"))
    {
    YES += 1;
    ALL_Voice += 1;
    proc.mq.add(uin,uss.localnick + " ваш голос учтен :)n");
    }
    if(voice.equals("нет"))
    {
    proc.mq.add(uin,uss.localnick + " ваш голос учтен :)n" );
    NO += 1;
    ALL_Voice += 1;
    }
    if(voice.equals("0"))
    {
    proc.mq.add(uin,uss.localnick + " вы воздержались. Спасибо за внимание :)n");
    ALL_Voice += 1;
    }
    Repetition.remove(uin);
    Msg.remove(uin);
    }

    public boolean VotingTime()// Время голосования. По умолчанию 2 минуты.
    {
    return (System.currentTimeMillis()-Vtime)>120000;
    }

    public boolean Test(String uin)
    {
    if(Repetition.get(uin) == 1)
    {
    return true;// истина
    }
    else
    {
    return false;// лож
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
    if(voice.equals("да") || voice.equals("нет") || voice.equals("0"))//проверим
    {
    return true;//если голос указан верно
    }
    else
    {
    return false;//если голос указан неверно
    }
    }

    /*
     * Если все голоса данны то наградим киком
     */

    public void EndVoting()
    {
    Users u = cmd.srv.us.getUser(ID_Voice);
    //Если все возможные голаса данны
    if(ALL_Voice == VotingUsersRoom())
    {
    if(YES > NO){
    cmd.srv.cq.addMsg("Результаты голосования:nЗА|ПРОТИВn" +
    YES + "|" + NO + "\n" +u .localnick + " вылетел из чата на 30 минут", "", ROOM_Voice);
    //Дадим кик
    cmd.tkick(cmd.srv.getIcqProcess(u.basesn), u.sn, 30, moder, R);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// Выключаем
    }
    else
    {
    cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " вам повезло, вы остаетесь в чате.");
    cmd.srv.cq.addMsg("Результаты голосования:nЗА|ПРОТИВn" +
    YES + "|" + NO + "\n" +u .localnick + " остается в чате", "", ROOM_Voice);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// Выключаем
    }
    }
    }

     /*
     * Если все голосования вышло.
     */

    public void EndVotingTime()
    {
    Users u = cmd.srv.us.getUser(ID_Voice);
    if(YES > NO){
    cmd.srv.cq.addMsg("Результаты голосования:nЗА|ПРОТИВn" +
    YES + "|" + NO + "\n" +u .localnick + " вылетел из чата на 30 минут", "", ROOM_Voice);
    //Дадим кик
    cmd.tkick(cmd.srv.getIcqProcess(u.basesn), u.sn, 30, moder, R);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// Выключаем
    }
    else
    {
    cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " вам повезло, вы остаетесь в чате.");
    cmd.srv.cq.addMsg("Результаты голосования:nЗА|ПРОТИВn" +
    YES + "|" + NO + "\n" + u .localnick + " остается в чате", "", ROOM_Voice);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// Выключаем
    }
    }

    /**
     * Обработка событий по времени
     */
    private void timeEvent(){
    if(StartVoting)
    {
    if(VotingTime())
    {
    EndVotingTime();// Если время вышло
    }
    else
    {
    EndVoting();// Если все пользователи проголосовали
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
 * Наш массив для хранения промежучных данных.
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