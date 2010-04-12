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
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private HashMap<String, VotingMap> VotingMap;
private ConcurrentHashMap <String, Integer> Repetition;// Масив для хранения информации
private ConcurrentHashMap <String, String> Msg;// Масив для хранения сообщений пользователя
private CommandParser parser;
public ChatServer srv;
private long TIME_CMD = 0;// Время временной команды
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
public String R = "";//Причина

public Voting(ChatServer s)
{
parser = new CommandParser(commands);
srv = s;
TIME_CMD = ChatProps.getInstance(srv.getName()).getIntProperty("voting.time")*60000;
R = "По результатам голосования ты велетел(а) из чата на " + ChatProps.getInstance(srv.getName()).getIntProperty("voting.kick.time") + " мин. :)";
Repetition  = new ConcurrentHashMap ();
Msg = new ConcurrentHashMap ();
VotingMap = new HashMap<String, VotingMap>();
init();
start();
}

private void init()
{
commands.put("!голосование", new Cmd("!голосование","$n",1));/*Начать голосование,
где аргумент $n пользователь против котого будет голосование*/
}

/**
 * Добавление новой команды
 * @param name
 * @param c
 * @return - истина, если команда уже существует
 */
public boolean addCommand(String name, Cmd c)
{
boolean f = commands.containsKey(name);
commands.put(name, c);
return f;
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
if(ChatProps.getInstance(srv.getName()).getBooleanProperty("voting.on.off")){
Voting(proc, uin, parser.parseArgs(tmsg), mmsg);
} else proc.mq.add(uin, "Эта команда закрыта администрацией чата");
break;
default:
f = false;
}
return f;
}

    public void Voting(IcqProtocol proc, String uin, Vector v, String mmsg)
    {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin)) return;
    if(!((ChatCommandProc)srv.cmd).auth(proc,uin, "voting")) return;
    if(StartVoting){return;}// Если голосование уже идет.
    Vtime = System.currentTimeMillis();// Запустим таймер
    int id = (Integer)v.get(0);
    Users uss = srv.us.getUser(uin);// Пользователь
    moder = uss.id;
    ROOM_Voice = uss.room;// Запомним комнату где идет голосование
    ID_Voice = id;// Пользователь против которого голосуем
    Users u = srv.us.getUser(ID_Voice);
    StartVoting = true;// Включим голосование
    //Если простой пользователь уже использовал 2 голосования
    if ((!ChatProps.getInstance(srv.getName()).testAdmin(uss.sn)) && (!srv.us.getUserGroup(uss.id).equals("moder")) && (!srv.us.getUserGroup(uss.id).equals("admin")) && (srv.us.getCountgolosovanChange(uss.id) >= ChatProps.getInstance(srv.getName()).getIntProperty("voting.count")))
    {
    proc.mq.add(uin,uss.localnick + ", голосование не создано.\nВы можете только " + ChatProps.getInstance(srv.getName()).getIntProperty("voting.count") + " раза в сутки создавать голосование за КИК.\nВы исчерпали свой лимит. :-P");
    StartVoting = false;
    return;
    }
    // Если пользователь системный админ или имеет полномочие anti_kik?
    if ((ChatProps.getInstance(srv.getName()).testAdmin(u.sn)) || (((ChatCommandProc)srv.cmd).qauth(proc, u.sn, "anti_kik")))
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
    srv.us.db.event(uss.id, uin, "GOLOSOVAN", u.id, u.sn, "голосовал за кик");
    Enumeration <String> e = srv.cq.uq.keys();
    while (e.hasMoreElements())
    {
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    Repetition.put(us.sn, 2);
    Msg.put(us.sn, mmsg);
    if((us.state == UserWork.STATE_CHAT) && (us.room == ROOM_Voice));
    {
    if((us.id != u.id) && (us.room == ROOM_Voice))
    {
    srv.getIcqProcess(us.basesn).mq.add(us.sn,"Пользователем " + uss.localnick + "|" + uss.id + "| начато голосование за КИК пользователя - " + u.localnick + "|" + u.id + "|\n"+
    "Выпнуть его из чата на " + ChatProps.getInstance(srv.getName()).getIntProperty("voting.kick.time") + " минут, отправьте ''да'' или ''нет'' ? (без ковычек)\n" +
    "Отправьте ''0'' (цифру ноль), что бы воздержатся от ответа.\nЧисло голосующих ~ " + VotingUsersRoom() + " чел.\nВремя на голосование - " + ChatProps.getInstance(srv.getName()).getIntProperty("voting.time") + " мин.");
    VotingMap.put(us.sn, new VotingMap(us.sn,  Msg.get(us.sn), Msg.get(us.sn), parser.parseArgs(mmsg), TIME_CMD));
    VoicesOfUsers(srv.getIcqProcess(us.basesn), us.sn, parser.parseArgs(mmsg), Msg.get(us.sn));
    }
    }
    }
    srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + uss.localnick + "|" + uss.id + "| запустил против тебя голосование.\nЕсли большинство проголосует ЗА, тебя выкинет из чата на " + ChatProps.getInstance(srv.getName()).getIntProperty("voting.kick.time") + " минут.");
    VotingMap.remove(u.sn);
    Repetition.remove(u.sn);
    }


    private void VoicesOfUsers(IcqProtocol proc, String uin, Vector v, String mmsg) {
    Users uss = srv.us.getUser(uin);
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
    proc.mq.add(uin,uss.localnick + " ваш голос учтен :)");
    }
    if(voice.equals("нет"))
    {
    proc.mq.add(uin,uss.localnick + " ваш голос учтен :)" );
    NO += 1;
    ALL_Voice += 1;
    }
    if(voice.equals("0"))
    {
    proc.mq.add(uin,uss.localnick + " вы воздержались. Спасибо за внимание :)");
    ALL_Voice += 1;
    }
    Repetition.remove(uin);
    Msg.remove(uin);
    }

    public boolean VotingTime()// Время голосования. По умолчанию 2 минуты.
    {
    return (System.currentTimeMillis()-Vtime) > 60000 * ChatProps.getInstance(srv.getName()).getIntProperty("voting.time");
    }

    public boolean Test(String uin)
    {
    return (Repetition.get(uin) == 1);
    }



    public int VotingUsersRoom()
    {
    int c = 0;
    Enumeration <String> e2 = srv.cq.uq.keys();
    while(e2.hasMoreElements())
    {
    String i2 = e2.nextElement();
    Users us = srv.us.getUser(i2);
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
    return (voice.equals("да") || voice.equals("нет") || voice.equals("0"));//проверим
    }

    /*
     * Если все голоса данны то наградим киком
     */

    public void EndVoting()
    {
    Users u = srv.us.getUser(ID_Voice);
    //Если все возможные голаса данны
    if(ALL_Voice == VotingUsersRoom())
    {
    if(YES > NO){
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
    YES + "|" + NO + "\n" +u .localnick + " вылетел из чата на " + ChatProps.getInstance(srv.getName()).getIntProperty("voting.kick.time") + " минут", "", ROOM_Voice);
    //Дадим кик
    ((ChatCommandProc)srv.cmd).tkick(srv.getIcqProcess(u.basesn), u.sn, ChatProps.getInstance(srv.getName()).getIntProperty("voting.kick.time"), moder, R);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// Выключаем
    }
    else
    {
    srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " вам повезло, вы остаетесь в чате.");
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
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
    Users u = srv.us.getUser(ID_Voice);
    if(YES > NO){
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
    YES + "|" + NO + "\n" +u .localnick + " вылетел из чата на " + ChatProps.getInstance(srv.getName()).getIntProperty("voting.kick.time") + " минут", "", ROOM_Voice);
    //Дадим кик
    ((ChatCommandProc)srv.cmd).tkick(srv.getIcqProcess(u.basesn), u.sn, ChatProps.getInstance(srv.getName()).getIntProperty("voting.kick.time"), moder, R);
    //
    YES = 0;
    NO = 0;
    ALL_Voice = 0;
    StartVoting = false;// Выключаем
    }
    else
    {
    srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " вам повезло, вы остаетесь в чате.");
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
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