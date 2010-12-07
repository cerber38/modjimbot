package ru.jimbot.modules.chat;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandExtend;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author fraer72
 * Голосование в чате, приз КИК :)
 */

public class Voting implements Runnable {
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private HashMap<String, CommandExtend> VotingMap;
private ConcurrentHashMap <String, Boolean> Repetition;
private ConcurrentHashMap <String, String> Msg;
private CommandParser parser;
private ChatServer srv;
private ChatProps psp;
private long time_cmd = 0;
private long time = System.currentTimeMillis();
private Thread thread;
private int sleepAmount = 1000;
private boolean startVoting = false;
private int yes = 0;
private int no = 0;
private int allVoice = 0;
private int idVoice = 0;
private int roomVoice = 0;
private int moder = 0;
private String r = "";

public Voting (ChatServer srv, ChatProps psp){
parser = new CommandParser(commands);
this.srv = srv;
this.psp = psp;
time_cmd = psp.getIntProperty("voting.time")*60000;
r = "По результатам голосования ты велетел(а) из чата на " + psp.getIntProperty("voting.kick.time") + " мин. :)";
Repetition  = new ConcurrentHashMap ();
Msg = new ConcurrentHashMap ();
VotingMap = new HashMap<String, CommandExtend>();
init();
start();
}

    private void init(){
    commands.put("!голосование", new Cmd("!голосование","$n",1));
    }


/**
 * Добавление новой команды
 * @param name
 * @param c
 * @return - истина, если команда уже существует
 */
public boolean addCommand(String name, Cmd c){
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
VoicesOfUsers(proc, uin, mmsg);
return true;//при голосовании невыводим сообщения в поток
}else{
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
if(psp.getBooleanProperty("voting.on.off")){
Voting(proc, uin, parser.parseArgs(tmsg), mmsg);
} else proc.mq.add(uin, "Эта команда закрыта администрацией чата");
break;
default:
f = false;
}
return f;
}

    public void Voting(IcqProtocol proc, String uin, Vector v, String mmsg){
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin)) return;
    if(!((ChatCommandProc)srv.cmd).auth(proc,uin, "voting")) return;
    if(startVoting){return;}// Если голосование уже идет.
    time = System.currentTimeMillis();// Запустим таймер
    int id = (Integer)v.get(0);
    Users uss = srv.us.getUser(uin);// Пользователь
    moder = uss.id;
    roomVoice = uss.room;// Запомним комнату где идет голосование
    idVoice = id;// Пользователь против которого голосуем
    Users u = srv.us.getUser(idVoice);
    startVoting = true;// Включим голосование
    //Если простой пользователь уже использовал 2 голосования
    if ((!psp.testAdmin(uss.sn)) && (!srv.us.getUserGroup(uss.id).equals("moder")) && (!srv.us.getUserGroup(uss.id).equals("admin")) && (srv.us.getCountgolosovanChange(uss.id) >= psp.getIntProperty("voting.count"))){
    proc.mq.add(uin,uss.localnick + ", голосование не создано.\nВы можете только " + psp.getIntProperty("voting.count") + " раза в сутки создавать голосование за КИК.\nВы исчерпали свой лимит. :-P");
    startVoting = false;
    return;
    }
    // Если пользователь системный админ или имеет полномочие anti_kik?
    if ((psp.testAdmin(u.sn)) || (((ChatCommandProc)srv.cmd).qauth(proc, u.sn, "anti_kik"))){
    proc.mq.add(uin,uss.localnick + " вы не можете создать голосование против этого пользователя, он владеет иммунитетом");
    startVoting = false;
    return;
    }
    // Если пользователь создает голосование против себя?
    if(id == uss.id){
    proc.mq.add(uin,uss.localnick + " нельзя создавать голосование против себя.");
    startVoting = false;
    return;
    }
    // Если пользователь незареган?
    if(u.id == 0){
    proc.mq.add(uin,uss.localnick + ", голосование не создано.\nПользователь с таким ID не зарегистрирован в чате.");
    startVoting = false;
    return;
    }
    // Если пользователь не в чате?
    if(u.state == 1){
    proc.mq.add(uin,uss.localnick + ", голосование не создано, так как этот пользователь сейчас не в чате.");
    startVoting = false;
    return;
    }
    // Если пользователь не в той же комнате?
    if(uss.room != u.room){
    proc.mq.add(uin,uss.localnick + ", голосование не создано.\nВы должны находиться в одной комнате с пользователем, против которого создаете голосование.");
    startVoting = false;
    return;
    }
    srv.us.db.event(uss.id, uin, "GOLOSOVAN", u.id, u.sn, "голосовал за кик");
    Enumeration <String> e = srv.cq.uq.keys();
    while (e.hasMoreElements())
    {
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    Repetition.put(us.sn, false);
    Msg.put(us.sn, mmsg);
    if((us.state == UserWork.STATE_CHAT) && (us.room == roomVoice)){
    if((us.id != u.id) && (us.room == roomVoice)){
    srv.getIcqProcess(us.basesn).mq.add(us.sn,"Пользователем " + uss.localnick + "|" + uss.id + "| начато голосование за КИК пользователя - " + u.localnick + "|" + u.id + "|\n"+
    "Выпнуть его из чата на " + psp.getIntProperty("voting.kick.time") + " минут, отправьте ''да'' или ''нет'' ? (без ковычек)\n" +
    "Отправьте ''0'' (цифру ноль), что бы воздержатся от ответа.\nЧисло голосующих ~ " + VotingUsersRoom() + " чел.\nВремя на голосование - " + psp.getIntProperty("voting.time") + " мин.");
    VotingMap.put(us.sn, new CommandExtend(us.sn,  Msg.get(us.sn), Msg.get(us.sn), parser.parseArgs(mmsg), time_cmd));
    VoicesOfUsers(srv.getIcqProcess(us.basesn), us.sn, Msg.get(us.sn));
    }
    }
    }
    srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + uss.localnick + "|" + uss.id + "| запустил против тебя голосование.\nЕсли большинство проголосует ЗА, тебя выкинет из чата на " + psp.getIntProperty("voting.kick.time") + " минут.");
    VotingMap.remove(u.sn);
    Repetition.remove(u.sn);
    }


    private void VoicesOfUsers(IcqProtocol proc, String uin, String mmsg) {
    Users uss = srv.us.getUser(uin);
    Msg.put(uin, mmsg);
    String voice  = "";
    if (uss.state == UserWork.STATE_CHAT){
    if(!Test(uin)){
    Repetition.put(uin, true);
    return;// Исключим сразу второй заход
    }
    if(VotingMap.containsKey(uin)){
    try{
    voice = Msg.get(uin);
    voice = voice.toLowerCase();//опустим регистр
    }catch(NumberFormatException e){
    proc.mq.add(uin,uss.localnick + " вам необходимо проголосовать :)");
    return;
    }
    if ((!TestVoting(voice)) && (uss.room == roomVoice)){
    proc.mq.add(uin,uss.localnick + " голос должен быть ''да'' или ''нет''n" +
    "Или отправьте ''0'' (цифру ноль), что бы воздержатся от ответа.");
    return;
    }
    VotingMap.remove(uin);
    }
    }
    if(voice.equals("да")){
    yes += 1;
    allVoice += 1;
    proc.mq.add(uin,uss.localnick + " ваш голос учтен :)");
    }
    if(voice.equals("нет")){
    proc.mq.add(uin,uss.localnick + " ваш голос учтен :)" );
    no += 1;
    allVoice += 1;
    }
    if(voice.equals("0")){
    proc.mq.add(uin,uss.localnick + " вы воздержались. Спасибо за внимание :)");
    allVoice += 1;
    }
    Repetition.remove(uin);
    Msg.remove(uin);
    }

    public boolean VotingTime(){
    return (System.currentTimeMillis()-time) > 60000 * psp.getIntProperty("voting.time");
    }

    public boolean Test(String uin){
    return Repetition.get(uin);
    }



    public int VotingUsersRoom() {
    int c = 0;
    Enumeration <String> e2 = srv.cq.uq.keys();
    while(e2.hasMoreElements()){
    String i2 = e2.nextElement();
    Users us = srv.us.getUser(i2);
    if(us.state==UserWork.STATE_CHAT){
    if(us.room == roomVoice){
    c++;
    }
    }
    }
    return (c-1);
    }

    public boolean TestVoting(String voice) {
    return (voice.equals("да") || voice.equals("нет") || voice.equals("0"));//проверим
    }

    /*
     * Если все голоса данны то наградим киком
     */

    public void EndVoting(){
    Users u = srv.us.getUser(idVoice);
    //Если все возможные голаса данны
    if(allVoice == VotingUsersRoom()){
    if(yes > no){
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
    yes + "|" + no + "\n" +u .localnick + " вылетел из чата на " + psp.getIntProperty("voting.kick.time") + " минут", "", roomVoice);
    //Дадим кик
    ((ChatCommandProc)srv.cmd).tkick(srv.getIcqProcess(u.basesn), u.sn, psp.getIntProperty("voting.kick.time"), moder,r);
    yes = 0;
    no = 0;
    allVoice = 0;
    startVoting = false;// Выключаем
    }else{
    srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " вам повезло, вы остаетесь в чате.");
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
    yes + "|" + no + "\n" +u .localnick + " остается в чате", "", roomVoice);
    yes = 0;
    no = 0;
    allVoice = 0;
    startVoting = false;// Выключаем
    }
    }
    }

     /*
     * Если все голосования вышло.
     */

    public void EndVotingTime()
    {
    Users u = srv.us.getUser(idVoice);
    if(yes > no){
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
    yes + "|" + no + "\n" +u .localnick + " вылетел из чата на " + psp.getIntProperty("voting.kick.time") + " минут", "", roomVoice);
    //Дадим кик
    ((ChatCommandProc)srv.cmd).tkick(srv.getIcqProcess(u.basesn), u.sn, psp.getIntProperty("voting.kick.time"), moder, r);
    //
    yes = 0;
    no = 0;
    allVoice = 0;
    startVoting = false;// Выключаем
    }else{
    srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " вам повезло, вы остаетесь в чате.");
    srv.cq.addMsg("Результаты голосования:\nЗА|ПРОТИВ\n" +
    yes + "|" + no + "\n" + u .localnick + " остается в чате", "", roomVoice);
    //
    yes = 0;
    no = 0;
    allVoice = 0;
    startVoting = false;// Выключаем
    }
    }

    /**
     * Обработка событий по времени
     */
    private void timeEvent(){
    if(startVoting){
    if(VotingTime()){
    EndVotingTime();// Если время вышло
    }else{EndVoting();// Если все пользователи проголосовали
    }
    }
    }



    public void start(){
    thread = new Thread(this);
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.start();
    }

    public synchronized void stop(){
    thread = null;
    notify();
    }

    public void run(){
    Thread me = Thread.currentThread();
    while (thread == me){
    timeEvent();
    try{
    thread.sleep(sleepAmount);
    }catch (InterruptedException e){
    break;
    }
    }
    thread = null;
    }




}