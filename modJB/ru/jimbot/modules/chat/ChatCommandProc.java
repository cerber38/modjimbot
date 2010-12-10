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
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.Manager;
import ru.jimbot.Messages;
import ru.jimbot.modules.AboutExtend;
import ru.jimbot.modules.AbstractCommandProcessor;
import ru.jimbot.modules.AbstractServer;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandExtend;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.modules.FloodElement;
import ru.jimbot.modules.WorkScript;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 * Обработка команд чата
 * 
 * @author Prolubnikov Dmitry
 */
public class ChatCommandProc extends AbstractCommandProcessor {
    public IcqProtocol icq;
    private ScriptWork scr;
    public frends frends;
    public ClanCommand clan;
    public Shop shop;
    public Shop2 shop2;
    public Gift gift;
    public ChatServer srv;
    public Voting voting;
    public RobAdmin radm = null;
    public AutoStatus xstatus = null;
    public MyQuiz Quiz = null;
    private ConcurrentHashMap <String,String> up; // Запоминаем последний пришедший приват
    private ConcurrentHashMap <String, KickInfo> statKick; // Расширенная статистика киков
    private HashSet<String> warnFlag; // Флаг предупреждения о молчании
    private CommandParser parser;
    public ChatProps psp;
    private boolean firstStartMsg = false;
    private boolean firstStartScript = false;
    // Счетчики для контроля флуда: одинаковых сообщений, отброшеных сообщений
    private ConcurrentHashMap <String, FloodElement> floodMap, floodMap2, floodNoReg;
    private HashMap<String, CommandExtend> comMap;
    // Храним время для игры бутылочка
    private long VialTime = System.currentTimeMillis();
    // Для хранения доступных объектов авторизации
    private HashMap<String, String> authObj = new HashMap<String, String>();
    // Для хранения доступных команд
    private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();    
    private ConcurrentHashMap <Integer, WeddingInfo> WeddingInfo;// Расширенная свадьба
    private ConcurrentHashMap <String,Integer> Wedding_ID;// Хранит ид свадьбы
    private ConcurrentHashMap <String,Integer> Wedding_STATUS;// Хранит статус пользователей при свадьбе
    private HashMap<String, AboutExtend> About;// Храним данные при заполнении анкеты
    private ConcurrentHashMap <String,Integer> pr;
    private HashMap<String, antiadvertising> antiadvertising;

    class KickInfo {
        public int id=0;
        public int len=0;
        public int moder_id=0;
        public int count=0;
        public String reason = "";
        
        KickInfo(int id, int moder_id, String r, int len) {
            this.id = id;
            this.len = len;
            this.moder_id = moder_id;
            this.reason = r;
            count = 0;
        }
        
        public int inc() {return count++;}
    }
    
    /** Creates a new instance of ChatCommandProc */
    public ChatCommandProc(ChatServer s) {
    	parser = new CommandParser(commands);
        srv = s;
        psp = ChatProps.getInstance(srv.getName());
        up = new ConcurrentHashMap<String, String>();
        statKick = new ConcurrentHashMap<String, KickInfo>();
        floodMap = new ConcurrentHashMap <String, FloodElement>();
        floodMap2 = new ConcurrentHashMap <String, FloodElement>();
        floodNoReg = new ConcurrentHashMap <String, FloodElement>();
        comMap = new HashMap<String, CommandExtend>();
        warnFlag = new HashSet<String>();
        shop = new Shop(srv, psp);
        if(psp.getBooleanProperty("shop2.on.off"))
        shop2 = new Shop2(srv, psp);
        frends = new frends(srv, psp);
        clan = new ClanCommand(srv, psp);
        gift = new Gift(srv, psp);
        voting = new Voting(srv, psp);
        WeddingInfo = new ConcurrentHashMap<Integer, WeddingInfo>();
        Wedding_ID = new ConcurrentHashMap<String, Integer>();
        Wedding_STATUS = new ConcurrentHashMap<String, Integer>();
        antiadvertising = new HashMap<String, antiadvertising>();
        About = new HashMap<String, AboutExtend>();
        pr = new ConcurrentHashMap<String, Integer>();
        Quiz = new MyQuiz(s);
        radm = new RobAdmin(srv);
        xstatus = new AutoStatus(srv, psp);
        init();
    }
    
    /**
     * Инициализация списка команд и полномочий
     */
    private void init(){
    	authObj.put("pmsg","Отправка приватных сообщений");
    	authObj.put("reg","Смена ника");
    	authObj.put("kickone","Кик одного пользователя");
    	authObj.put("kickall","Кик всех пользователей");
    	authObj.put("ban","Забанить пользователя");
    	authObj.put("settheme","Установить тему в комнате");
    	authObj.put("adminsay","Разговаривать с админом");
    	authObj.put("adminstat","Получать статистику от админа");
    	authObj.put("info","Получать расшириную информацию о юзере");
    	authObj.put("exthelp","Расширенная помощь");
    	authObj.put("authread","Получение инфы о полномочиях");
    	authObj.put("authwrite","Изменение полномочий пользователей");
    	authObj.put("whouser","Просмотр инфы о смене ников юзером");
    	authObj.put("room","Смена комнаты");
    	authObj.put("whoinv","Команда !whoinvite");
    	authObj.put("kickhist","Команда !kickhist");
    	authObj.put("chgkick","Изменение времени кика");
    	authObj.put("dblnick","Разрешено дублировать ник");
    	authObj.put("anyroom","Переход в любую комнату");
    	authObj.put("wroom","Создавать и изменять комнаты");
        authObj.put("zakhist", " 5 закрываний пользователей");
        authObj.put("banroom", "Закрывать пользователей");
        authObj.put("anti_ban", "Анти ''ban''");
        authObj.put("anti_kik", "Анти ''kick''");
        authObj.put("anti_banroom", "Анти ''banroom''");
        authObj.put("anti_chnick", "Анти ''chnick''");
        authObj.put("fraza","Возможность добовлять фразы в игру бутылочка");
        authObj.put("admlist","Смотреть список админов в online");
        authObj.put("admalllist","Смотреть список админов");
        authObj.put("chnick","Команда изменения ника другого пользователя");
        authObj.put("setpass", "Установить пароль в комнате");
        authObj.put("admlist", "Смотреть список адм сообщений");
        authObj.put("robmsg", "Возможность добовлять фразы для админ-бота");
        authObj.put("group_time", "Назначать временную группу");
        authObj.put("xst","Изминить х-статус чата");
        authObj.put("restart","Перезагрузка сервиса");
        authObj.put("shophist","Смотреть историю покупок в магазине");
        authObj.put("gift","Добавлять/удалять подарки/товар в ларьке/магазине");
        authObj.put("status_user", "Установка статусов");
        authObj.put("ubanhist", "История разбанов");
        authObj.put("shophist", "Список пакупок в магазине");
        authObj.put("invitation", "Приглашать пользователей в чат");
        authObj.put("allroom_message", "Сообщение во все комнаты");
        authObj.put("setclan", "Создание/удаление кланов");
        authObj.put("deladmmsg", "Удаление адм сообщений");
        authObj.put("wedding", "Свадьба/Развод");
        authObj.put("chstatus", "Смена статуса другому пользователю");
        authObj.put("voting", "Возможность открывать голосование");
        authObj.put("chid", "Менять id другому пользователю");
        authObj.put("invise","Возможность прятаться");
        authObj.put("invisible","невидимый");
        authObj.put("listinvise","Возможность смотреть список скрытых пользователей");
        authObj.put("usermessages","Возможность отправлять сообщение пользователю");
        authObj.put("uchat","Затаскивать пользователя а чат");
        authObj.put("bot_messages","Отправить сообщение вместо бота");
        authObj.put("reg_user","Регистрировать пользователей");
        authObj.put("advertisement_work","Работа с рекламой");
        authObj.put("ballwork","Дать/Забрать балы");
        authObj.put("notice_work","Работа с предупреждениями");
        authObj.put("personal_room","Назначить/Убрать личную комнату пользователю");
    	
       	commands.put("!help", new Cmd("!help","",1));
        commands.put("!справка", new Cmd("!справка", "", 1));
        commands.put("!помощь", new Cmd("!помощь", "", 1));
        commands.put("!команды", new Cmd("!команды", "", 1));
        commands.put("!chat", new Cmd("!chat", "", 2));
        commands.put("!чат", new Cmd("!чат", "", 2));
        commands.put("!вход", new Cmd("!вход", "", 2));
        commands.put("!exit", new Cmd("!exit", "", 3));
        commands.put("!выход", new Cmd("!выход", "", 3));
        commands.put("!rules", new Cmd("!rules", "", 4));
        commands.put("!правила", new Cmd("!правила", "", 4));
        commands.put("!законы", new Cmd("!законы", "", 4));
        commands.put("!stat", new Cmd("!stat", "", 5));
        commands.put("!стат", new Cmd("!стат", "", 5));
        commands.put("!gofree", new Cmd("!gofree", "", 6));
        commands.put("!свюин", new Cmd("!свюин", "", 6));
        commands.put("!go", new Cmd("!go", "$n", 7));
        commands.put("!юин", new Cmd("!юин", "$n", 7));
        commands.put("!banlist", new Cmd("!banlist", "", 8));
        commands.put("!банлист", new Cmd("!банлист", "", 8));
        commands.put("!kicklist", new Cmd("!kicklist", "", 9));
        commands.put("!киклист", new Cmd("!киклист", "", 9));
        commands.put("!info", new Cmd("!info", "$c", 10));
        commands.put("!инфо", new Cmd("!инфо", "$c", 10));
        commands.put("!kick", new Cmd("!kick", "$c $n $s", 11));
        commands.put("!кик", new Cmd("!кик", "$c $n $s", 11));
        commands.put("!kickall", new Cmd("!kickall", "", 12));
        commands.put("!киквсех", new Cmd("!киквсех", "", 12));
        commands.put("!listauth", new Cmd("!listauth", "", 13));
        commands.put("!листаут", new Cmd("!листаут", "", 13));
        commands.put("!who", new Cmd("!who", "$n", 14));
        commands.put("!кто", new Cmd("!кто", "$n", 14));
        commands.put("!listgroup", new Cmd("!listgroup", "", 15));
        commands.put("!листгрупп", new Cmd("!листгрупп", "", 15));
        commands.put("!checkuser", new Cmd("!checkuser", "$n", 16));
        commands.put("!проверка", new Cmd("!проверка", "$n", 16));
        commands.put("!setgroup", new Cmd("!setgroup", "$n $c", 17));
        commands.put("!группа", new Cmd("!группа", "$n $c", 17));
        commands.put("!grant", new Cmd("!grant", "$n $c", 18));
        commands.put("!добавить", new Cmd("!добавить", "$n $c", 18));
        commands.put("!revoke", new Cmd("!revoke", "$n $c $s", 19));
        commands.put("!лишить", new Cmd("!лишить", "$n $c $s", 19));
        commands.put("!ban", new Cmd("!ban", "$c $s", 20));
        commands.put("!бан", new Cmd("!бан", "$c $s", 20));
        commands.put("!uban", new Cmd("!uban", "$c", 21));
        commands.put("!убан", new Cmd("!убан", "$c", 21));
        commands.put("!reg", new Cmd("!reg","$c $c",22));
        commands.put("!ник", new Cmd("!ник", "$c $c", 22));
        commands.put("!рег", new Cmd("!рег", "$c $c", 22));
        commands.put("+a", new Cmd("+a", "", 23));
        commands.put("+а", new Cmd("+а", "", 23));
        commands.put("+f", new Cmd("+f", "", 23));
        commands.put("+ф", new Cmd("+ф", "", 23));
        commands.put("!тут", new Cmd("!тут", "", 23));
        commands.put("+p", new Cmd("+p", "$n $s", 24));
        commands.put("+р", new Cmd("+р", "$n $s", 24));
        commands.put("!лс", new Cmd("!лс", "$n $s", 24));
        commands.put("+pp", new Cmd("+pp", "$s", 25));
        commands.put("+рр", new Cmd("+рр", "$s", 25));
        commands.put("!ответ", new Cmd("!ответ", "$s", 25));
        commands.put("!settheme", new Cmd("!settheme", "$s", 26));
        commands.put("!тема", new Cmd("!тема", "$s", 26));
        commands.put("!getinfo", new Cmd("!getinfo", "$c", 27));
        commands.put("!аська", new Cmd("!аська", "$c", 27));
        commands.put("!room", new Cmd("!room", "$n $c", 28));
        commands.put("!комната", new Cmd("!комната", "$n $c", 28));
        commands.put("!к", new Cmd("!к", "$n $c", 28));
        commands.put("!kickhist", new Cmd("!kickhist", "", 29));
        commands.put("!кикист", new Cmd("!кикист", "", 29));
        commands.put("!adm", new Cmd("!adm", "$s", 30));
        commands.put("!админу", new Cmd("!админу", "$s", 30));
        commands.put("!banhist", new Cmd("!banhist", "", 31));
        commands.put("!банист", new Cmd("!банист", "", 31));
        commands.put("+aa", new Cmd("+aa", "", 32));
        commands.put("+аа", new Cmd("+аа", "", 32));
        commands.put("!все", new Cmd("!все", "", 32));
        commands.put("!lroom", new Cmd("!lroom", "", 33));
        commands.put("!комнаты", new Cmd("!комнаты", "", 33));
        commands.put("!crroom", new Cmd("!crroom", "$n $s", 34));
        commands.put("!создкомн", new Cmd("!создкомн", "$n $s", 34));
        commands.put("!chroom", new Cmd("!chroom", "$n $s", 35));
        commands.put("!измкомн", new Cmd("!измкомн", "$n $s", 35));
        commands.put("!таймгруппа", new Cmd("!таймгруппа", "$n $n $s", 36));
        commands.put("!таймлист", new Cmd("!таймлист", "", 37));
        commands.put("!закрытые", new Cmd("!закрытые", "", 38));
        // TODO: 39 - скрипты
        // TODO: 40 - скрипты в базе
        commands.put("!запереть", new Cmd("!запереть", "$n $n $s", 41));
        commands.put("!бутылочка", new Cmd("!бутылочка","",42));
        commands.put("!фраза", new Cmd("!фраза", "$s", 43));
        commands.put("!админы", new Cmd("!админы", "", 44));
        commands.put("!chnick", new Cmd("!chnick","$n $c",45));
        commands.put("!смник", new Cmd("!смник","$n $c",45));
        commands.put("!повысить", new Cmd("!повысить", "$n", 46));
        commands.put("!понизить", new Cmd("!понизить", "$n", 47));
        commands.put("!setpass", new Cmd("!setpass", "$c", 48));
        commands.put("!пароль", new Cmd("!пароль", "$c", 48));
        commands.put("!адмлист", new Cmd("!адмлист", "$c", 49));
        commands.put("!робмсг", new Cmd("!робмсг", "$s", 50));
        commands.put("!хстатус", new Cmd("!хстатус", "$n $s", 51));
        commands.put("!перезагрузить", new Cmd("!перезагрузить", "", 52));
        commands.put("!статус", new Cmd("!статус", "$s", 53));
        commands.put("!разбанлист", new Cmd("!разбанлист", "", 54));
        commands.put("!удалить", new Cmd("!удалить", "$n", 55));
        commands.put("!пригласитьид", new Cmd("!пригласитьид", "$n $s", 56));
        commands.put("!пригласитьуин", new Cmd("!пригласитьуин", "$c $s", 57));
        commands.put("!везде", new Cmd("!везде", "$s", 58));
        commands.put("!деладм", new Cmd("!деладм", "", 59));
        commands.put("!свадьба", new Cmd("!свадьба", "$n $n", 60));
        commands.put("!развод", new Cmd("!развод", "$n $n", 61));
        commands.put("!отдать", new Cmd("!отдать", "$n $n", 62));
        commands.put("!chstatus", new Cmd("!chstatus","$n $s",63));
        commands.put("!смстатус", new Cmd("!смстатус","$n $s",63));
        commands.put("!измид", new Cmd("!измид","$n $n",64));
        commands.put("!спрятаться", new Cmd("!спрятаться","",65));
        commands.put("!показаться", new Cmd("!показаться","",66));
        commands.put("!скрылись", new Cmd("!скрылись","",67));
        commands.put("!уин", new Cmd("!уин","$n",68));
        commands.put("!личное", new Cmd("!личное","$n",69));
        commands.put("!данные", new Cmd("!данные","",70));
        commands.put("!юзеру", new Cmd("!юзеру","$c $s",71));
        commands.put("!затащить", new Cmd("!затащить", "$n", 72));
        commands.put("!бот", new Cmd("!бот", "$s", 73));
        commands.put("!регистрировать",new Cmd("!регистрировать","$c $c",74));
        commands.put("!казино",new Cmd("!казино","",75));
        commands.put("!рулетка",new Cmd("!рулетка","$n",76));
        commands.put("!аддреклама",new Cmd("!аддреклама","$s",77));
        commands.put("!делреклама",new Cmd("!делреклама","$n",78));
        commands.put("!листреклама",new Cmd("!листреклама","$n",79));
        commands.put("!датьбалы",new Cmd("!датьбалы","$n $n",80));
        commands.put("!забратьбалы",new Cmd("!забратьбалы","$n $n",81));
        commands.put("!зарплата",new Cmd("!зарплата","",82));
        commands.put("!предупреждение",new Cmd("!предупреждение","$n $s",83));
        commands.put("!нотист",new Cmd("!нотист","$n",84));
        commands.put("!число",new Cmd("!число","$n $n",85));
        commands.put("!всеадмины",new Cmd("!всеадмины","",86));
        commands.put("!лкомната",new Cmd("!лкомната","$n $n",87));
        commands.put("!рлкомната",new Cmd("!рлкомната","$n",88));
        commands.put("!пригласить",new Cmd("!пригласить","$n",89));
        WorkScript.getInstance(srv.getName()).installAllChatCommandScripts(this);
        commands.put("!оботе",new Cmd("!оботе","",90));
        commands.put("!about",new Cmd("!about","",90));
    }
    
    /**
     * Выдает список полномочий для работы
     * @return
     */
    public HashMap<String, String> getAuthObjects(){
    	return this.authObj;
    }
    
    /**
     * Выдает список команд
     * @return
     */
    public HashMap<String, Cmd> getCommands(){
    	return this.commands;
    }
    
    /**
     * Добавление нового объекта полномочий
     * @param name
     * @param comment
     * @return - истина, если такой объект уже был в таблице
     */
    public boolean addAuth(String name, String comment){
    	boolean f = authObj.containsKey(name);
    	authObj.put(name, comment);
    	return f;
    }
    
    /**
     * Добавление новой команды
     * @param name
     * @param c
     * @return - истина, если команда уже существует
     */
    public boolean addCommand(String name, Cmd c) {
    	boolean f = commands.containsKey(name);
    	commands.put(name, c);
    	return f;
    }

    /**
     * Возвращает экземпляр парсера
     * @return
     */
    public CommandParser getParser(){
    return parser;
    }

    /**
     * Устанавливаем скрипты в базе
     * @param proc
     */
    

    private void firstScript(IcqProtocol proc){
    try{
    if(!firstStartScript){
    scr = new ScriptWork(this);
    scr.installAll(proc);
    firstStartScript=true;
    }
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }
     /**
      * Сообщение системным админам о запуске сервиса
      * @param proc
      */


     private void firstMsg(IcqProtocol proc){
     if(!firstStartMsg){
     String[] s = srv.getProps().getAdmins();
     for(int i=0;i<s.length;i++){
     String k = "Сервис \"" + srv.getName() + "\" запущен - " + new Date(Time.getTimeStart());
     proc.mq.add(s[i], k);
     }
     firstStartMsg=true;
     }
     }
   
    public AbstractServer getServer(){
    return srv;
    }
    
    /**
     * Основная процедура парсера команд
     */
    public void parse(IcqProtocol proc, String uin, String mmsg) {
    firstMsg(proc);//Первое сообщение
    firstScript(proc);//Запуск скриптов
    Log.getLogger(srv.getName()).debug("CHAT: parse " + proc.baseUin + ", " + uin + ", " + mmsg);
    if(psp.getBooleanProperty("chat.writeInMsgs"))
    //Слишком длинные сообщения записывать в БД не нужно для избежания переполнений
    if(mmsg.length()>1000){
    srv.us.db.log(0,uin,"IN",mmsg.substring(0, 1000),0);
    } else {
    srv.us.db.log(0,uin,"IN",mmsg,0);
    }
    String tmsg = mmsg.trim();
    if(tmsg.length()==0){
    Log.getLogger(srv.getName()).error("Пустое сообщение в парсере команд: " + uin + ">" + mmsg);
    return;
    }
    if(tmsg.charAt(0)=='!' || tmsg.charAt(0)=='+'){
    Log.getLogger(srv.getName()).info("CHAT COM_LOG: " + uin + ">>" + tmsg);
    }
    try {
    //Проверка на временую группу
    if (srv.us.getUser(uin).country == 1 & testGroupTime(uin)==0){
    GroupNoTime(proc, uin);
    Users us = srv.us.getUser(uin);
    us.country = 0;
    srv.us.updateUser(us);
    }
    //banroom
    if (testClosed(uin)==0 & !srv.us.authorityCheck(uin,"room"))freedom(uin);
    //Notices
    if(!testNotices(uin, proc)) return;// Пользователь кикнут за лимит предупреждений
    if(srv.us.testUser(uin)){
    if(isBan(uin)){
    Log.getLogger(srv.getName()).flood2("CHAT_BAN: " + uin + ">" + mmsg);
    return;
    }
    if(testKick(uin)>0){
    Log.getLogger(srv.getName()).info("CHAT_KICK: " + uin + ">" + mmsg);
    return;
    }
    if(srv.us.getUser(uin).state==UserWork.STATE_CHAT)
    GoToChat(proc, uin, parser.parseArgs(tmsg), mmsg);
    } else{
    // Для нового юзера
    // Проверка на флуд
    if(floodNoReg.containsKey(uin)){
    FloodElement e = floodNoReg.get(uin);
    if(e.getDeltaTime()<(psp.getIntProperty("chat.floodTimeLimitNoReg")*1000)){
    e.addMsg(tmsg);
    floodNoReg.put(uin, e);
    Log.getLogger(srv.getName()).flood("FLOOD NO REG " + uin + "> " + tmsg);
    return; // Слишком часто
    }
    if(e.isDoubleMsg(tmsg) && e.getCount()>3){
    e.addMsg(tmsg);
    floodNoReg.put(uin, e);
    Log.getLogger(srv.getName()).flood("FLOOD NO REG " + uin + "> " + tmsg);
    return; // Повтор сообщений
    }
    e.addMsg(tmsg);
    floodNoReg.put(uin, e);
    } else{
    FloodElement e = new FloodElement(psp.getIntProperty("chat.floodTimeLimitNoReg")*1000);
    floodNoReg.put(uin, e);
    }
    /*
     * Проверим включена ли капча?
     */
    if(psp.getBooleanProperty("chat.captcha")){

       /**
        *   Капча при первом сообщение от юзера
        *   @author fraer72
        */

      // Если это не команда - выводим приветствие, иначе обрабатываем команду
      boolean captcha = false; // Второй заход в процедуру после ответа?
      if(srv.us.getUser(uin).state != UserWork.STATE_CAPTCHA && tmsg.charAt(0) != '!' && comMap.containsKey(uin)){
      if(comMap.get(uin).getMsg().equalsIgnoreCase(mmsg)){
      captcha = true;
      comMap.remove(uin);
      }else{
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.6"));
      comMap.remove(uin);
      return;
      }
      }
      if(!captcha){
      String s = getCaptcha();
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.0", new Object[] {s.split("=")[0],psp.getIntProperty("chat.floodTimeLimitNoReg")}));
      comMap.put(uin, new CommandExtend(uin, mmsg, s.split("=")[1],new Vector(), 5*60000));
      return;
      }
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.1", new Object[] {psp.getStringProperty("chat.name"),psp.getIntProperty("chat.floodTimeLimitNoReg")}));
      Log.getLogger(srv.getName()).talk(uin + " Captcha user: " + mmsg);
      proc.mq.add(uin, "", 1);      
      srv.us.getUser(uin).state = UserWork.STATE_CAPTCHA;
      return;
      }else{
      if(srv.us.getUser(uin).state == UserWork.STATE_NO_REG && tmsg.charAt(0) != '!'){
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.1", new Object[] {psp.getStringProperty("chat.name"),psp.getIntProperty("chat.floodTimeLimitNoReg")}));
      return;
      }
      }
      }
 
            // Проверка на флуд
            if(floodMap.containsKey(uin)){
            FloodElement e = floodMap.get(uin);
            e.addMsg(tmsg);
            floodMap.put(uin, e);
            } else{
            FloodElement e = new FloodElement(psp.getIntProperty("chat.floodTimeLimit")*1000);
            e.addMsg(tmsg);
            floodMap.put(uin, e);
            }
            if (testFlood(proc,uin)) return;// Если пользователь был кикнут за флуд

            if(srv.us.getUser(uin).state==UserWork.STATE_CHAT & psp.getBooleanProperty("messages.script.on.off"))
            mmsg = WorkScript.getInstance(srv.getName()).startMessagesScript(mmsg, srv, uin);
            if(srv.us.getUser(uin).state==UserWork.STATE_CHAT & psp.getBooleanProperty("antiadvertising.on.off") & !psp.testAdmin(uin))
            mmsg = antiadvertising(mmsg, uin);
            if(mmsg.equals("")) return; // Сообщение было удалено в скрипте или в анте рекламе
            int tp = 0;
            if(About.containsKey(uin))
            if(!About.get(uin).isExpire()){
            InteractiveQuestions(proc, uin, mmsg, About.get(uin).getCommand());
            return;
            } else
            About.remove(uin);
            if(comMap.containsKey(uin))
            if(!comMap.get(uin).isExpire())
            tp = parser.parseCommand(comMap.get(uin).getCmd());
            else{
            tp = parser.parseCommand(tmsg);
            comMap.remove(uin);
            }else
            tp = parser.parseCommand(tmsg);
            /********************************/
            if (comMap.containsKey("Wedding_"+uin)){
            if (!comMap.get("Wedding_"+uin).isExpire()){
            TestWedding(uin, mmsg, parser.parseArgs(mmsg));
            }else{
            tp = parser.parseCommand(tmsg); 
            comMap.remove("Wedding_"+uin);
            Wedding_ID.remove("Wedding_"+uin);
            Wedding_STATUS.remove("Wedding_"+uin);
            }
            }else 
            tp = parser.parseCommand(tmsg);
            /********************************/
            if (comMap.containsKey("goChat_"+uin)){
            if (!comMap.get("goChat_"+uin).isExpire()){
            tp = parser.parseCommand(comMap.get("goChat_"+uin).getCmd());
            }else{
            tp = parser.parseCommand(tmsg);
            comMap.remove("goChat_"+uin);
            Wedding_ID.remove("goChat_"+uin);
            Wedding_STATUS.remove("goChat_"+uin);
            }
            }else
            tp = parser.parseCommand(tmsg);
            /********************************/
            int tst=0;
            if(tp<0)
                tst=0;
            else
            	tst = tp;
            switch (tst){
           case 1:
                commandHelp(proc, uin);
                break;
           case 2:
                GoToChat(proc, uin, parser.parseArgs(tmsg), mmsg);
                if(psp.getBooleanProperty("chat.getUserInfoOnChat"))
                proc.mq.add(uin, "", 1);
                break;
           case 3:
                exitChat(proc, uin);
                break;
           case 4:
                if (!isChat(proc, uin) && !psp.testAdmin(uin)) {
                break;
                }
                proc.mq.add(uin, psp.loadText("./text/" + srv.getName() + "/rules.txt"));
                break;
           case 5:
            	if(!psp.testAdmin(uin)) break;
                proc.mq.add(uin,srv.us.getUinStat());
                break;
           case 6:
            	if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                commandGofree(proc, uin);
                break;
           case 7:
            	//TODO Выделить объект полномочий
            	if(!psp.testAdmin(uin)) break;
                commandGo(proc, uin, parser.parseArgs(tmsg));
                break;
           case 8:
                if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                if(!auth(proc,uin, "ban")) return;
                proc.mq.add(uin, srv.us.listUsers());
                break;
           case 9:
                if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                if(!auth(proc,uin, "kickone")) return;
                proc.mq.add(uin, listKickUsers());
                break;
           case 10:
                commandInfo(proc, uin, parser.parseArgs(tmsg));
                break;
           case 11:
                commandKick(proc, uin, parser.parseArgs(tmsg));
                break;
           case 12:
                if(!isChat(proc,uin)) break;
                if(!auth(proc,uin, "kickall")) return;
                try{
                kickAll(proc, uin);
                } catch (Exception ex) {
                Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
                }
                break;
           case 13:
                if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                if(!auth(proc,uin, "authread")) return;
                proc.mq.add(uin,listAuthObjects());
                break;
           case 14:
                commandWho(proc, uin, parser.parseArgs(tmsg));
                break;
           case 15:
                if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                if(!auth(proc,uin, "authread")) return;
                proc.mq.add(uin,psp.getStringProperty("auth.groups"));
                break;
           case 16:
                commandCheckuser(proc, uin, parser.parseArgs(tmsg));
                break;
           case 17:
                commandSetgroup(proc, uin, parser.parseArgs(tmsg));
                break;
           case 18:
                commandGrant(proc, uin, parser.parseArgs(tmsg));
                break;
           case 19:
                commandRevoke(proc, uin, parser.parseArgs(tmsg));
                break;
           case 20:
                commandBan(proc, uin, parser.parseArgs(tmsg));
                break;
           case 21:
                commandUban(proc, uin, parser.parseArgs(tmsg));
                break;
           case 22:
                commandReg(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 23:
                commandA(proc, uin);
                break;
           case 24:
                commandP(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 25:
                commandPP(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 26:
                commandSettheme(proc, uin, parser.parseArgs(tmsg));
                break;
           case 27:
                commandGetinfo(proc, uin, parser.parseArgs(tmsg));
                break;
           case 28:
                commandRoom(proc, uin, parser.parseArgs(tmsg));
                break;
           case 29:
                commandKickhist(proc, uin);
                break;
           case 30:
                commandAdm(proc, uin, parser.parseArgs(tmsg));
                break;
           case 31:
                commandBanhist(proc, uin);
                break;
           case 32:
                commandAA(proc, uin);
                break;
           case 33:
            	commandLRoom(proc,uin);
            	break;
           case 34:
                commandCrRoom(proc, uin, parser.parseArgs(tmsg));
                break;
           case 35:
                commandChRoom(proc, uin, parser.parseArgs(tmsg));
                break;
           case 36:
                GroupTime(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 37:
                if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                if(!auth(proc,uin, "group_time")) return;
                proc.mq.add(uin, srv.us.getGroupList());
                break;
           case 38:
                commandZakHist(proc, uin);
                break;
           case 39: // Обработка команды макросом
            	String ret = WorkScript.getInstance(srv.getName()).startChatCommandScript(parser.parseCommand2(tmsg).script, tmsg, uin, proc, this);
            	if(!ret.equals("") && !ret.equals("ok")) proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.2"));
            	break;
           case 40: // Скрипты в базе
                String re = scr.startScript(parser.parseCommand2(tmsg).script, tmsg, uin, proc, this);
                if(!re.equals("") && !re.equals("ok")) proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.2"));
                break;
           case 41:
                BanRoom(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 42:
                commandVial(proc, uin, parser.parseArgs(tmsg), mmsg);
               break;
           case 43:
                commandFraza(proc, uin, parser.parseArgs(tmsg));
               break;
           case 44:
                commandAdmini(proc, uin);
               break;
           case 45:
                commandchnick(proc, uin, parser.parseArgs(tmsg), mmsg);
                break; 
           case 46:
                commandOchkiplys(proc, uin, parser.parseArgs(tmsg));
                break;
           case 47:
                commandOchkiminys(proc, uin, parser.parseArgs(tmsg));
                break;
           case 48:
                commandSetpass(proc, uin, parser.parseArgs(tmsg));
                break;
           case 49:
                commandAdmList(proc, uin);
                break;
           case 50:
                commandRobMsg(proc, uin, parser.parseArgs(tmsg));
                break;
           case 51:
                commandXst(proc, uin, parser.parseArgs(tmsg));
                break;
           case 52:
                commandRestart(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 53:
                commandStatus(proc, uin, parser.parseArgs(tmsg));
                break;
           case 54:
                commandUbanHist(proc, uin);
                break;
           case 55:
                commandDeleteRoom(proc, uin, parser.parseArgs(tmsg));
                break;
           case 56:
                commandInvitation_ID(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 57:
                commandInvitation_UIN(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 58:
                commandSend(proc, uin, parser.parseArgs(tmsg));
                break;
           case 59:
                commandDelAdmMsg(proc, uin);
                break;
           case 60:
                commandWedding(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 61:
                commandDivorce(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 62:
                if (psp.getBooleanProperty("ball.on.off")) {
                PresendBall(proc, uin, parser.parseArgs(tmsg));
                } else proc.mq.add(uin, "Эта команда закрыта администрацией чата");
                break;
           case 63:
                ChengeUserStatus(proc, uin, parser.parseArgs(tmsg));
                break;
           case 64:
                commandchid(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 65:
                commandOnInvise(proc, uin);
                break;
           case 66:
                commandOffInvise(proc, uin);
                break;
           case 67:
                commandListInvise(proc, uin);
                break;
           case 68:
                commandGetUinUser(proc, uin, parser.parseArgs(tmsg));
                break;
           case 69:
                commandLichUser(proc, uin, parser.parseArgs(tmsg));
                break;
           case 70:
                if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                InteractiveQuestions(proc, uin, mmsg, false);
                break;
           case 71:
                getUserMessages(proc, uin, parser.parseArgs(tmsg));
                break;
           case 72:
                commandDragSomewhere(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 73:
                commandBotMessages(proc, uin, parser.parseArgs(tmsg));
                break;
           case 74:
                RegUser(proc, uin, parser.parseArgs(tmsg));
                break;
           case 75:
                if (psp.getBooleanProperty("casino.on.off")) {
                commandCasino(proc, uin);
                } else proc.mq.add(uin, "Эта команда закрыта администрацией чата");  
                break;
           case 76:
                if (psp.getBooleanProperty("russian.roulette.on.off")) {
                commandRussianRoulette(proc, uin, parser.parseArgs(tmsg), mmsg);
                } else proc.mq.add(uin, "Эта команда закрыта администрацией чата");
                break;
           case 77:
                setAdvertisement(proc, uin, parser.parseArgs(tmsg));
                break;
           case 78:
                delAdvertisement(proc, uin, parser.parseArgs(tmsg));
                break;
           case 79:
                listAdvertisement(proc, uin);
                break;
           case 80:
                getBall(proc, uin, parser.parseArgs(tmsg));
                break;
           case 81:
                nailBall(proc, uin, parser.parseArgs(tmsg));
                break;
           case 82:
                commandSalary(proc, uin);
                break;
           case 83:
                commandNotice(proc, uin, parser.parseArgs(tmsg));
                break;
           case 84:
                commandListNotice(proc, uin, parser.parseArgs(tmsg));
                break;
           case 85:
                if (psp.getBooleanProperty("number.on.off")) {
                commandGameNumber(proc, uin, parser.parseArgs(tmsg));
                } else proc.mq.add(uin, "Эта команда закрыта администрацией чата");
                break;
           case 86:
                commandAdminiAll(proc, uin);
                break;
           case 87:
                commandAppointPrivateRoom(proc, uin, parser.parseArgs(tmsg));
                break;
           case 88:
                commandCleanPrivateRoom(proc, uin, parser.parseArgs(tmsg));
                break;
           case 89:
                commandInvitationPrivateRoom(proc, uin, parser.parseArgs(tmsg));
                break;
           case 90:
                proc.mq.add(uin,MainProps.getAbout());
                break;
                default:
     //Дополнительные команды из других классов
     if(scr.commandExec(proc, uin, tmsg)) return;
     if(voting.commandVoting(proc, uin, tmsg)) return;
     if(shop.commandShop(proc, uin, tmsg)) return;
     if(psp.getBooleanProperty("shop2.on.off"))
     if(shop2.commandShop2(proc, uin, tmsg)) return;
     if(frends.commandFrends(proc, uin, tmsg)) return;
     if(gift.commandShop(proc, uin, tmsg)) return;
     if(clan.commandClan(proc, uin, tmsg)) return;
     if(srv.us.getUser(uin).state==UserWork.STATE_CHAT){
     //Сообщения начинающиеся с "!" и "+" не выводим в чат
     try{
     if(psp.getBooleanProperty("replace.nick.on.off")) mmsg = replaceNick(mmsg);
     if(mmsg.substring(0, 1).equals("!")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.3"));return;}
     } catch (Exception ex){
     Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
     }
     String s = "";
     if(mmsg.indexOf("/me")==0)
     s = mmsg.replaceFirst("/me", "*" + srv.us.getUser(uin).localnick);
     else
     if(psp.getBooleanProperty("id.on.off"))
     s += srv.us.getUser(uin).localnick + "|" + srv.us.getUser(uin).id + "|"  + psp.getStringProperty("chat.delimiter")+" " + mmsg;
     else
     s += srv.us.getUser(uin).localnick + psp.getStringProperty("chat.delimiter")+" " + mmsg;
     if(s.length()>psp.getIntProperty("chat.MaxMsgSize")){
     s = s.substring(0,psp.getIntProperty("chat.MaxMsgSize"));
     proc.mq.add(uin,"Слишком длинное сообщение было обрезано: " + s);
     }
     s = s.replace('\n',' ');
     s = s.replace('\r',' ');
     Log.getLogger(srv.getName()).info("CHAT: " + uin + "<" + srv.us.getUser(uin).id +"> ["+srv.us.getUser(uin).room +"]>>" + s);
     srv.us.db.log(srv.us.getUser(uin).id,uin,"OUT", s, srv.us.getUser(uin).room);
     srv.cq.addMsg(s + (psp.getBooleanProperty("divided.between.on.off") ? "\n" + psp.getStringProperty("divided.between") : ""), uin, srv.us.getUser(uin).room);
     if(psp.getBooleanProperty("adm.useAdmin")) radm.parse(proc,uin,s,srv.us.getUser(uin).room);
     if(psp.getBooleanProperty("vic.on.off")) Quiz.parse(uin,mmsg,srv.us.getUser(uin).room);
     }else{
     if(srv.us.getUser(uin).state==UserWork.STATE_NO_CHAT & !About.containsKey(uin)){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.5", new Object[] {psp.getStringProperty("chat.name")}));
     return;
     }
     if(srv.us.getUser(uin).localnick==null || srv.us.getUser(uin).localnick.equals("") || srv.us.getUser(uin).state == UserWork.STATE_NO_REG & !About.containsKey(uin)) {
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.4", new Object[] {psp.getStringProperty("chat.name"),psp.getIntProperty("chat.floodTimeLimitNoReg")}));
     return;
     }
     }
     }
     } catch (Exception ex) {
     ex.printStackTrace();
     Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
     }
     }
    
    /**
     * Замена id`a на ник пользователя
     * @author fraer72
     * @param msg
     * @v 0.3
     */

    private String replaceNick(String mmsg){
    String chars = "%";// Символ с идом
    boolean f = true;
    Set<String> nick = new HashSet();
    if(mmsg.indexOf(chars) == -1) return mmsg; // Нет символа в строке   
    StringTokenizer st = new StringTokenizer(mmsg, " ");
    while(st.hasMoreTokens()){// переберем все сообщение
    String token = st.nextToken();
    if(token.indexOf(chars) != -1) nick.add(token);
    }
    for (String p : nick){
    if(!MainProps.testInteger(p.replace(chars, ""))) f = false;// Если попалось не число
    else
    f = true;
    if(f){
    p = p.replace(chars, "");
    Integer id = Integer.parseInt(p);
    mmsg = mmsg.replace(chars + p, srv.us.getUser(id).localnick);
    }
    }
    return mmsg;
    }

    /**
     * Анти-Реклама
     * @author fraer72
     * @param msg
     * @v 0.7
     */

    private String antiadvertising(String msg, String uin){
    int maxCnt = psp.getIntProperty("antiadvertising.integer.cnt"); // Максимально число цифр в сообщении
    int Cnt = 0;
    String s = "";
    String[] number = psp.getStringProperty("antiadvertising.number").split(";");
    String delimiters = psp.getStringProperty("antiadvertising.delimiters");
    StringTokenizer st = new StringTokenizer(msg, delimiters);
    while(st.hasMoreTokens()){  // Перебираем сообщение
    s = st.nextToken();
    for (int i = 0 ;i < number.length; i++)
    if(s.toLowerCase().indexOf(number[i]) != -1 & s.toLowerCase().indexOf("%") == -1)
    Cnt++;
    }
    if(Cnt > maxCnt)
    msg = msg.replace(s, "*"); // Если выше максимального, закроем
    else if (psp.getBooleanProperty("antiadvertising.send.on.off") & Cnt != 0) {
    if(!antiadvertising.containsKey(uin))
        antiadvertising.put(uin, new antiadvertising(System.currentTimeMillis(), 0));
    if(antiadvertising.get(uin).testTime()){
        if(antiadvertising.get(uin).getCnt() == psp.getIntProperty("antiadvertising.messages.cnt")-1){
        // Частая отправка, закроем
        msg = msg.replace(s, "*");
        antiadvertising.remove(uin);
        }else
        antiadvertising.get(uin).setCnt();
    }else
        antiadvertising.remove(uin);
    }
    return msg;
    }

    class antiadvertising {

        private long time = 0;
        private int cnt = 0;

        public antiadvertising(long time, int cnt){
        this.time = time;
        this.cnt = cnt;
        }

        public boolean testTime(){
        return (System.currentTimeMillis()-time) < psp.getIntProperty("antiadvertising.time");
        }

        public void setCnt(){
        cnt++;
        }

        public int getCnt(){
        return cnt;
        }

    }



    /**
     * Вывод данных в зависимости от настроек для команды +а
     * @param room
     * @return
     */


    public String A(int room){
    String Y = "";
    if(!psp.getBooleanProperty("vic.on.off")){
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.0") + "\n";
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.1") + "\n";
    if(psp.getBooleanProperty("adm.useAdmin")){Y += "0 - " + radm.NICK + '\n';}
    return Y;
    }
    if(Quiz.TestRoom(room)){
    if(psp.getBooleanProperty("vic.time_game.on.off")){
    String [] test = psp.getStringProperty("vic.game.time").split(";");
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.6", new Object[] {test[0],test[1]});
    }
    Y += srv.us.AnswerUsersTop();
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.0") + "\n";
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.1") + "\n";
    }else{
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.0") + "\n";
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.1") + "\n";
    if(psp.getBooleanProperty("adm.useAdmin")){Y += "0 - " + radm.NICK + '\n';}
    }
    return Y;
    }

    /**
     * Вывод данных в зависимости от настроек для команды +аа
     * @param room
     * @return
     */

    public String AA(int room){
    String Y = "";
    if(!psp.getBooleanProperty("vic.on.off")){
    if(psp.getBooleanProperty("adm.useAdmin")){Y += "0 - " + radm.NICK + '\n';}
    return Y;
    }
    if(Quiz.TestRoom(room)){
    Y += "";
    }else{
    if(psp.getBooleanProperty("adm.useAdmin")){Y += "0 - " + radm.NICK + '\n';}
    }
    return Y;
    }

    /**
     * Вернет количество пользователей в конкретной комнате
     * @param room
     * @return
     */

    public int AllUsersRoom(int room){
    int c = 0;
    Enumeration<String> e = srv.cq.uq.keys();
    while(e.hasMoreElements()){
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if(us.state == UserWork.STATE_CHAT){
    if(us.room == room){
    c++;
    }
    }
    }
    return (c-1);
    }
    
    /**
     * Сообщение во все комнаты
     * @param msg
     */

    private void allRoomMsg(String uin, String msg){
    Set<Integer> rid = new HashSet();
    Enumeration<String> e = srv.cq.uq.keys();
    while (e.hasMoreElements()){
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if (us.state == UserWork.STATE_CHAT){
    rid.add(us.room);
    }
    }
    for (int i : rid){
    srv.cq.addMsg(msg, uin, i);
    }
    }

    /**
     * Команды чата
     */

     /**
       * @author HellFaust
       * !help (!справка) - Вывод списка команд
       */
    public void commandHelp(IcqProtocol proc, String uin){ 
    Users uss = srv.us.getUser(uin);
    String c = "Список доступных команд:\n";
    if (psp.testAdmin(uin)){
    c += srv.us.getAllCommand();
    }
    else
    {
    String g = "all";
    c += srv.us.getCommand(g);
    String [] s = srv.us.getAuth(uss.id).split(";");
    for(int i = 0; i < s.length; i++){
    c += srv.us.getCommand(s[i]);
    }
    }
    cutsend(proc, uin, c);
    }

    /**
     * Резка сообщений
     * @author jimbot
     * @param proc
     * @param uin
     * @param s
     */
    public void cutsend(IcqProtocol proc, String uin, String s) { 
    char[] c = s.toCharArray();
    s = "";
    for (int i = 0; i < c.length; i++){
    if( i == 1000 || i == 2000 || i == 3000 || i == 4000 ||  i == 5000 || i == 6000 ){
    proc.mq.add(uin, s+c[i]);
    s = "";
    }else s += c[i];
    }
    proc.mq.add(uin, s);
    }


    /**
     * !gofree
     * @param proc
     * @param uin
     */
    public void commandGofree(IcqProtocol proc, String uin){
     try{
     String s = srv.us.getFreeUin();
     changeBaseUin(uin,s);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGo.1", new Object[] {s}));
     }catch (Exception ex){
     Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
     }
     }
    
    /**
     * !go
     * @param proc
     * @param uin
     * @param v
     */
    public void commandGo(IcqProtocol proc, String uin, Vector v){
      try{
      int k = (Integer)v.get(0);
      if(k>=psp.uinCount() || k<0){
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGo.0"));
      return;
      }
      changeBaseUin(uin,psp.getUin(k));
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGo.1", new Object[] {psp.getUin(k)}));
      }catch (Exception ex){
      Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
      }
      }


    
    /**
     * !info
     * @param proc
     * @param uin
     * @param v
     */
    public void commandInfo(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "info"))return;
    try{
    String s = (String)v.get(0);
    Users uss = null;
    String info = "";
    if(s.length()>=6){
    uss = srv.us.getUser(s);
    }else{
    try{
    uss = srv.us.getUser(Integer.parseInt(s));
    }catch (Exception ex){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.0"));
    return;
    }
    }
    if(uss.id == 0){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.0"));
    return;
    }    
    info += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.1") + uss.localnick;
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.2", new Object[] {(uss.data==0 ? "Не указанна" : (("|" + new Date(uss.data)).toString() + "|"))});
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.3", new Object[] {uss.sn});
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.4", new Object[] {uss.id});
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.5", new Object[] {srv.us.getUserGroup(uss.id)});//TODO uss.group не всегда выводит истиную группу, лучше использовать getUserGroup()
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.6", new Object[] {uss.ball});
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.7", new Object[] {uss.answer});
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.14", new Object[] {uss.notice, psp.getIntProperty("notice.limit")});
    if(uss.state==-1)
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.8");
    if(uss.state==1)
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.9", new Object[] {srv.us.statVxodVixod("STATE_IN", uss.id)});
    if(uss.state==2)
    info+= "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.10", new Object[] {uss.room});
    if(testClosed(uss.sn)>1)
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.11", new Object[] {testClosed(uss.sn)});
    if(testKick(uss.sn)>1)
    info += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.12", new Object[] {testKick(uss.sn)});
    proc.mq.add(uin,info);
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.13", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !kick
     * @param proc
     * @param uin
     * @param v
     */
    public void commandKick(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "kickone")) return;
    try{
    int moder_id = srv.us.getUser(uin).id;
    String s = (String)v.get(0);
    int t = (Integer)v.get(1);
    String r = (String)v.get(2);
    int id=0;
    try{
    id = Integer.parseInt(s);
    } catch (Exception ex){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err.1", new Object[] {ex.getMessage()}));
    return;
    }
    String i = srv.us.getUser(id).sn;
    if(testKick(i)>0 && !auth(proc,uin, "chgkick")){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.0"));
    return;
    }
    if (psp.testAdmin(i) || qauth(proc, i, "anti_kik")) {
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.1"));
    return;
    }
    if(t==0){
    tkick(proc, i, psp.getIntProperty("chat.defaultKickTime"), moder_id,"");
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.3", new Object[] {testKick(i)}));
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.4", new Object[] {srv.us.getUser(i).localnick,srv.us.getUser(i).id,t,r,srv.us.getUser(uin).localnick,srv.us.getUser(uin).id}), i, srv.us.getUser(i).room);
    } else{
    if(r.equals("")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.5"));return;}
    if(t>psp.getIntProperty("chat.maxKickTime"))
    t=psp.getIntProperty("chat.maxKickTime");
    tkick(proc, i, t, moder_id, r);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.3", new Object[] {t}));
    }
    } catch (Exception ex) {
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !who
     * @param proc
     * @param uin
     * @param v
     */
    public void commandWho(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "whouser")) return;
    try{
    int id = (Integer)v.get(0);
    proc.mq.add(uin, srv.us.getUserNicks(id));
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !checkuser
     * @param proc
     * @param uin
     * @param v
     */
    public void commandCheckuser(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authread")) return;
    try{
    int id = (Integer)v.get(0);
    proc.mq.add(uin, srv.us.getUserAuthInfo(id));
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !setgroup
     * @param proc
     * @param uin
     * @param v
     */
    public void commandSetgroup(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authwrite")) return;
    try{
    String s1 = (String)v.get(1);
    int id = (Integer)v.get(0);
    Users uss = srv.us.getUser(id);
    if(uss.id!=id){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.0"));
    return;
    }
    if(!testUserGroup(s1)){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.1"));
    return;
    }
    uss.group = s1;
    uss.country = 0;
    srv.us.updateUser(uss);
    boolean f = srv.us.setUserPropsValue(id, "group", s1) &&
    srv.us.setUserPropsValue(id, "grant", "") &&
    srv.us.setUserPropsValue(id, "revoke", "");
    srv.us.clearCashAuth(id);
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.2", new Object[] {s1}));
    if(f)
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.3"));
    else
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.4"));
    } catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !grant
     * @param proc
     * @param uin
     * @param v
     */
    public void commandGrant(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authwrite")) return;
    try{
    String s1 = (String)v.get(1);
    int id = (Integer)v.get(0);
    Users uss = srv.us.getUser(id);
    if(uss.id!=id){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.0"));
    return;
    }
    if(!testAuthObject(s1)){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.1"));
    return;
    }
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.2", new Object[] {s1}));
    if(srv.us.grantUser(id, s1))
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.3"));
    else
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.4"));
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !revoke
     * @param proc
     * @param uin
     * @param v
     */
    public void commandRevoke(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authwrite")) return;
    try{
    String s1 = (String)v.get(1);
    int id = (Integer)v.get(0);
    String r = (String)v.get(2);
    Users uss = srv.us.getUser(id);
    if(uss.id!=id){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.0"));
    return;
    }
    if(!testAuthObject(s1)){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.1"));
    return;
    }
    if(r.equals("")){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.2"));
    return;
    }
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.3", new Object[] {s1,r}));
    if(srv.us.revokeUser(id, s1))
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.4"));
    else
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.5"));
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !ban
     * @param proc
     * @param uin
     * @param v
     */
    public void commandBan(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "ban")) return;
    try{
    String s = (String)v.get(0);
    String m = (String)v.get(1);
    String i="";
    if(s.length()>=6){
    if(uin.equals(s)){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.0"));
    return;
    }
    if(m.equals("")){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.1"));
    return;
    }
    ban(proc, s, uin,m);
    } else{
    int id = 0;
    try {
    id = Integer.parseInt(s);
    } catch(Exception ex) {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.2"));
    return;
    }
    i = srv.us.getUser(id).sn;
    if(!i.equals("")){
    if(uin.equals(i)){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.3"));
    return;
    }
    if (psp.testAdmin(i) || qauth(proc, i, "anti_ban")) {
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.4"));
    return;
    }
    if(m.equals("")){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.5"));
    return;
    }
    ban(proc, i, uin,m);
    }
    }   
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.6", new Object[] {i}));
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.7", new Object[] {srv.us.getUser(i).localnick,srv.us.getUser(i).id,m,srv.us.getUser(uin).localnick,srv.us.getUser(uin).id}), i, srv.us.getUser(i).room);
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    
    /**
     * !uban
     * @param proc
     * @param uin
     * @param v
     */
    public void commandUban(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "ban")) return;
    try{
    String s = (String)v.get(0);
    String i="";
    if(s.length()>=6){
    uban(proc, s, uin);
    }else{
    int id = 0;
    try {
    id = Integer.parseInt(s);
    } catch (Exception e) {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandUban.0"));
    }
    i = srv.us.getUser(id).sn;
    if(!i.equals("")) uban(proc, i, uin);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandUban.1", new Object[] {i}));
    }            
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
    }
    }
    

    /**
     * !reg
     * @param proc
     * @param uin
     * @param v
     * @param mmsg
     * @author fraer72
     */
    
    public void commandReg(IcqProtocol proc, String uin, Vector v, String mmsg){
     try{
     Users uss = srv.us.getUser(uin);
     int maxNick = psp.getIntProperty("chat.maxNickLenght");
     String lnick = (String)v.get(0);
     String oldNick = uss.localnick;
     if (lnick.equals("") || lnick.equals(" ")){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.0"));
     return;
     }
     if(lnick.length()>maxNick){
     lnick = lnick.substring(0,maxNick);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.1"));
     }
     if(!testNick(uin,lnick)){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.2"));
     return;
     }
     lnick = lnick.replace('\n',' ');
     lnick = lnick.replace('\r',' ');
     if(psp.getBooleanProperty("chat.isUniqueNick") && !qauth(proc,uin,"dblnick") && !psp.testAdmin(uin))
     if(srv.us.isUsedNick(lnick)){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.3"));
     return;
     }
            
    
     //смена ника - юзер уже в чате, пароль не нужен
     if(uss.state != UserWork.STATE_CAPTCHA  && uss.state != UserWork.STATE_NO_REG){
     if(!auth(proc,uin, "reg")) return;
     if(uss.state!=UserWork.STATE_CHAT) return; // Менять ник тока в чате
     if(lnick.length()>maxNick){
     lnick = lnick.substring(0,maxNick);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.4"));
     }
     if(srv.us.getCountNickChange(uss.id)>psp.getIntProperty("chat.maxNickChanged")){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.5"));
     return;
     }
     if (oldNick.equals(lnick)){
     if (uss.state == UserWork.STATE_NO_CHAT) {
     proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.6", new Object[] {psp.getStringProperty("chat.name")}));
     return;
     }     
     if (uss.state == UserWork.STATE_CHAT){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.7"));
     }     
     return;
     }
     uss.localnick = lnick;
     Log.getLogger(srv.getName()).talk(uin + " update " + mmsg);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.8"));
     srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.9", new Object[] {oldNick,lnick}), "", uss.room); //Сообщение для всех
     srv.us.db.log(uss.id,uin,"REG",lnick,uss.room);
     srv.us.db.event(uss.id, uin, "REG", 0, "", lnick);
     uss.basesn = proc.baseUin;
     srv.us.updateUser(uss);
     return;
     }
     if(!testNick(uin,lnick)){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.10"));
     return;
     }
     srv.us.getUser(uin).basesn = proc.baseUin;
     int id = srv.us.addUser(srv.us.getUser(uin));
     proc.mq.add(uin, "", 1);
     uss.localnick = lnick;
     uss.state = UserWork.STATE_NO_CHAT;
     uss.data = System.currentTimeMillis();
     srv.us.updateUser(uss);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.11"));
     allRoomMsg("", Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.13", new Object[] {uss.id,uss.localnick}));
     /*
      * Тут начинаем задавать интерактивные вопросы
      */
     if(psp.getBooleanProperty("Questionnaire.on.off")){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.12"));
     InteractiveQuestions(proc, uin, mmsg, true);
     }
     }catch (Exception ex){
     Log.getLogger(srv.getName()).error(uin + " Ошбка регистрации " + ex.getMessage());
     }
     }


    /**
     * +a
     * @param proc
     * @param uin
     */
    public void commandA(IcqProtocol proc, String uin){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    int room = srv.us.getUser(uin).room;
    String s = Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.2", new Object[] {room,srv.us.getRoom(room).getName()});
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.3", new Object[] {srv.us.getRoom(room).getTopic()});
    s += "\n" + A(room);
    Enumeration<String> e = srv.cq.uq.keys();
    int cnt=0;
    int[] user = new int[srv.cq.statUsers()];
    int j = 0;
    while(e.hasMoreElements()){
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if(us.state==UserWork.STATE_CHAT){
    user[j] = us.id;
    j++;
    }
    }
    Arrays.sort(user);
    for (int k = 0; k < user.length; k++) {
    Users us = srv.us.getUser(user[k]);
    if (!srv.us.authorityCheck(us.id, "invisible")) {
    if(us.room == room){
    cnt++;
    s += us.id + " - " + (srv.us.getClan(us.clansman).getSymbol().equals("") || !psp.getBooleanProperty("Clan.Symbol") ? "" : "[" + srv.us.getClan(us.clansman).getSymbol() + "] ")  +  us.localnick + (!psp.getBooleanProperty("symbols.on.off") ? "" : (srv.us.getSymbol(us.group))) + "|" + us.ball + "|" + (us.status.equals("") ? "" : ("|" + us.status + "|")) + '\n';
    }
    } else if (psp.testAdmin(uin)) {
    if(us.room == room){
    cnt++;
    s += us.id + " - " + (srv.us.getClan(us.clansman).getSymbol().equals("") || !psp.getBooleanProperty("Clan.Symbol") ? "" : "[" + srv.us.getClan(us.clansman).getSymbol() + "] ")  +  us.localnick + (!psp.getBooleanProperty("symbols.on.off") ? "" : (srv.us.getSymbol(us.group))) + "|" + us.ball + "|" + (us.status.equals("") ? "" : ("|" + us.status + "|")) + " - Скрывается " + '\n';
    }
    }
    }
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.4", new Object[] {cnt});
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.5", new Object[] {"«"+Integer.toString(srv.us.count()) +"»"});
    s += (srv.us.db.getAdvertisement().equals("") || !psp.getBooleanProperty("advertisement.on.off") ?  "" : ("\n\n" + Messages.getInstance(srv.getName()).getString("Advertisement.0", new Object[] {srv.us.db.getAdvertisement()})));
    proc.mq.add(uin, s);
    }
    
    /**
     * +aa
     * @param proc
     * @param uin
     */
    public void commandAA(IcqProtocol proc, String uin){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    int room = srv.us.getUser(uin).room;
    String s = Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.0") + "\n";
    s += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.1") + "\n";
    s += AA(room);
    int cnt = 0;
    int[] user = new int[srv.cq.statUsers()];
    int j = 0;
    Enumeration<String> e = srv.cq.uq.keys();
    while(e.hasMoreElements()){
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if(us.state==UserWork.STATE_CHAT){
    user[j] = us.id;
    j++;
    }
    }
    Arrays.sort(user);
    for (int k = 0; k < user.length; k++) {
    Users us = srv.us.getUser(user[k]);
    if (!srv.us.authorityCheck(us.id, "invisible")) {
    cnt++;
    s += us.id + " - " + (srv.us.getClan(us.clansman).getSymbol().equals("") || !psp.getBooleanProperty("Clan.Symbol") ? "" : "[" + srv.us.getClan(us.clansman).getSymbol() + "] ") + " " +  us.localnick + (!psp.getBooleanProperty("symbols.on.off") ? "" : (srv.us.getSymbol(us.group))) + " ["+ us.room + "] " + "[" + us.ball + "]" + (us.status.equals("") ? "" : ("[" + us.status + "]")) + '\n';
    } else if (psp.testAdmin(uin)) {
    cnt++;
    s += us.id + " - " + (srv.us.getClan(us.clansman).getSymbol().equals("") || !psp.getBooleanProperty("Clan.Symbol") ? "" : "[" + srv.us.getClan(us.clansman).getSymbol() + "] ") + " " +  us.localnick + (!psp.getBooleanProperty("symbols.on.off") ? "" : (srv.us.getSymbol(us.group))) + " ["+ us.room + "] " + "[" + us.ball + "]" + (us.status.equals("") ? "" : ("[" + us.status + "]")) + " - Скрывается " + '\n';
    }
    }
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.2", new Object[] {cnt});
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.3", new Object[] {"«" + srv.us.statUsersCount()+ "»"});
    s += (srv.us.db.getAdvertisement().equals("") || !psp.getBooleanProperty("advertisement.on.off") ?  "" : ("\n\n" + Messages.getInstance(srv.getName()).getString("Advertisement.0", new Object[] {srv.us.db.getAdvertisement()})));
    cutsend(proc, uin, s);
    }
    
    /**
     * +p
     * @param proc
     * @param uin
     * @param v
     * @param tmsg
     */
    public void commandP(IcqProtocol proc, String uin, Vector v, String tmsg){
    if(!isChat(proc,uin)) return;
    if(!auth(proc,uin, "pmsg")) return;
    try{
    int no = (Integer)v.get(0);
    String txt = (String)v.get(1);
    if(txt.equals("")) {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.0"));
    return;
    }
    if(psp.getBooleanProperty("antiadvertising.on.off") & psp.getBooleanProperty("antiadvertising.pm.on.off") & !psp.testAdmin(uin))
    txt = antiadvertising(txt, uin);
    Users uss = srv.us.getUser(no);
    if(uss == null){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.1"));
    return;
    }
    if(!srv.cq.testUser(uss.sn) || srv.us.authorityCheck(uss.id,"invisible")){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.2"));
    return;
    }
    if(txt.length()>psp.getIntProperty("chat.MaxMsgSize")){
    txt = txt.substring(0,psp.getIntProperty("chat.MaxMsgSize"));
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.3", new Object[] {txt}));
    }
    Log.getLogger(srv.getName()).talk("CHAT: " + uss.sn + ">>" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    srv.us.db.log(uss.id,uin,"LICH",">> " + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}),uss.room);
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    setPM(uss.sn, uin);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.6"));
    /*Оповещение админа*/
    if(psp.getBooleanProperty("lichnoe.on.off")){
    String[] admins = psp.getStringProperty("chat.lichnoe").split(";");
    for (int i=0 ;i<admins.length; i++){
    if(admins[i] == null || admins[i].equals("")){
    Log.getLogger(srv.getName()).error("В админке не указан(ны) уин(ы) для оповещения!!!");
    return;
    }
    Users usss = srv.us.getUser(admins[i]);
    srv.getIcqProcess(usss.basesn).mq.add(usss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.4", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,uin,uss.localnick,uss.id,txt}));
    }
    }
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.7"));
    }
    }
    
    /**
     * +pp
     * @param proc
     * @param uin
     * @param v
     * @param tmsg
     */
    public void commandPP(IcqProtocol proc, String uin, Vector v, String tmsg){
    if(!isChat(proc,uin)) return;
    if(!auth(proc,uin, "pmsg")) return;
    try{
    String txt = (String)v.get(0);
    String fsn = testPM(uin);
    if(txt.equals("")) {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.0"));
    return;
    }
    if(psp.getBooleanProperty("antiadvertising.on.off") & psp.getBooleanProperty("antiadvertising.pm.on.off") & !psp.testAdmin(uin))
    txt = antiadvertising(txt, uin);
    if(fsn.equals("")) {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandPP.0"));
    return;
    }
    Users uss = srv.us.getUser(fsn);
    if(uss == null){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.1"));
    return;
    }
    if(!srv.cq.testUser(uss.sn) || srv.us.authorityCheck(uss.id,"invisible")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.2"));return;}
    if(txt.length()>psp.getIntProperty("chat.MaxMsgSize")){
    txt = txt.substring(0,psp.getIntProperty("chat.MaxMsgSize"));
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.3", new Object[] {txt}));
    }
    Log.getLogger(srv.getName()).talk("CHAT: " + uss.sn + ">>" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    srv.us.db.log(uss.id,uin,"LICH",">> " + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}),uss.room);
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    setPM(uss.sn, uin);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.6"));
    /*Оповещение админа*/
    if(psp.getBooleanProperty("lichnoe.on.off")){
    String[] admins = psp.getStringProperty("chat.lichnoe").split(";");
    for (int i=0 ;i<admins.length; i++){
    if(admins[i] == null || admins[i].equals("")){
    Log.getLogger(srv.getName()).error("В админке не указан(ны) уин(ы) для оповещения!!!");
    return;
    }
    Users usss = srv.us.getUser(admins[i]);
    srv.getIcqProcess(usss.basesn).mq.add(usss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.4", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,uin,uss.localnick,uss.id,txt}));
    }
    }
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.7"));
    }
    }
    
    /**
     * !settheme
     * @param proc
     * @param uin
     * @param v
     */
    public void commandSettheme(IcqProtocol proc, String uin, Vector v){
    if(!auth(proc,uin, "settheme")) return;
    String s = (String)v.get(0);
    if(s.length() > psp.getIntProperty("theme.leght")){
    proc.mq.add(s, "Тема комнаты не может быть больше " + psp.getIntProperty("theme.leght") + " символов");
    return;
    }
    Users uss = srv.us.getUser(uin);
    int room = srv.us.getUser(uin).room;
    Rooms r = srv.us.getRoom(room);
    r.setTopic(s);
    srv.us.saveRoom(r, "");
    Log.getLogger(srv.getName()).info("Установлена тема комнаты " + room + ": " + s);
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSettheme.0", new Object[] {uss.localnick,s}), "", room);
    }
    
    /**
     * !getinfo
     * @param proc
     * @param uin
     * @param v
     */
    public void commandGetinfo(IcqProtocol proc, String uin, Vector v){
    if(!isAdmin(proc, uin)) return;
    try{
    String s = (String)v.get(0);
    s = srv.us.getUser(Integer.parseInt(s)).sn;
    proc.mq.add(s, "", 1);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGetinfo.0", new Object[] {s}));
    } catch (Exception ex){
    Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGetinfo.1"));
    }
    }
    
    /**
     * !room
     * @param proc
     * @param uin
     * @param v
     */
   public void commandRoom(IcqProtocol proc, String uin, Vector v){
   if (!isChat(proc, uin)) return;
   if (!auth(proc, uin, "room")) return;
   try{
   int i = (Integer) v.get(0);
   String pass = (String) v.get(1);
   Users uss = srv.us.getUser(uin);
   Users u = srv.us.getUser(i);
   if(i==psp.getIntProperty("room.tyrma") && !psp.testAdmin(uin)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.0", i, uss));return;}
   if (uss.room == i){
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.1", i, uss));
   return;
   } else if (!srv.us.getRoom(i).checkPass(pass) && !psp.testAdmin(uin))
   {
   if (srv.us.getCountpassChange(uss.id, u.id) >= 1) {
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.2", i, uss));
   return;
   }
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.3", i, uss));
   srv.us.db.event(uss.id, uin, "PASS", u.id, u.sn, "Пытался ввести пароль");
   return;
   }
   if(srv.us.getRoom(i).getUser_id() != 0 && srv.us.getRoom(i).getUser_id() != uss.clansman && !psp.testAdmin(uin) ){
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.4", i, uss));
   return;
   }
   if(psp.getBooleanProperty("vic.on.off") & psp.getBooleanProperty("vic.time_game.on.off") & Quiz.TestRoom(i) & !psp.testAdmin(uin) & !Quiz.testHours()){
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.9", i, uss));
   return;
   }
   if(psp.getBooleanProperty("vic.on.off") & Quiz.TestRoom(i) & !psp.testAdmin(uin) & Quiz.AllUsersRoom(i) >= psp.getIntProperty("vic.users.cnt")){
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.10", i, uss));
   return;
   }
   if(srv.us.getRoom(i).checkPersonal() & i != uss.personal_room & !testPrivateRoom(uin, i)){
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.11", i, uss));
   return;
   }
   if (qauth(proc, uin, "anyroom") || srv.us.checkRoom(i)){
   if (!srv.us.authorityCheck(uss.id,"invisible")){
   srv.cq.addMsg(Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.5", i, uss), uin, uss.room);
   }
   uss.room = i;
   srv.us.updateUser(uss);
   srv.cq.changeUserRoom(uin, i);
   if(pr.containsKey(uin) & testPrivateRoom(uin, i))
   pr.remove(uin);
   if (!srv.us.authorityCheck(uss.id,"invisible")){
   srv.cq.addMsg(Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.6", i, uss), uin, uss.room);
   }
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.7", i, uss));
   } else{
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_Room("ChatCommandProc.commandRoom.8", i, uss));
   }
   }catch (Exception ex){
   Log.getLogger(srv.getName()).error(Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
   }
   }

        
    /**
     * !kickhist
     * @param proc
     * @param uin
     */
    public void commandKickhist(IcqProtocol proc, String uin){
    if(!auth(proc,uin, "kickhist")) return;
    try {
    proc.mq.add(uin,srv.us.getKickHist());
    } catch (Exception ex) {
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }
    
    /**
     * !adm
     * @param proc
     * @param uin
     * @param v
     */
    public void commandAdm(IcqProtocol proc, String uin, Vector v){
    if(srv.us.getUser(uin).id == 0){return;}
    try {
    String s = (String)v.get(0);
    Users uss = srv.us.getUser(uin);
    int id_2 = uss.id;
    if(s.equals("")){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAdm.0"));
    return;
    }
    if(radm.testMat1(s)){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAdm.3"));
    return;        
    }
    long t = System.currentTimeMillis();
    srv.us.db.admmsg((int)srv.us.db.getLastIndex("admmsg"), id_2, s, t);
    /*Оповещение админа*/
    if(psp.getBooleanProperty("adm.on.off")){
    String[] admins = psp.getStringProperty("chat.adm").split(";");
    for (int i=0 ;i<admins.length; i++){
    if(admins[i] == null || admins[i].equals("")){
    Log.getLogger(srv.getName()).error("В админке не указан(ны) уин(ы) для оповещения!!!");
    return;
    }
    Users usss = srv.us.getUser(admins[i]);
    srv.getIcqProcess(usss.basesn).mq.add(usss.sn,"От " + (uss.localnick.equals("") ? "не зарегистрированного пользователя" : ("пользователя |" + uss.id + "|" + uss.localnick)) + " сообщение админу: " + s);
    }
    }
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAdm.1"));
    }catch (Exception ex) {
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAdm.2"));
    }
    }
    
    /**
     * !banhist
     * @param proc
     * @param uin
     */
    public void commandBanhist(IcqProtocol proc, String uin){
    if(!auth(proc,uin, "ban")) return;
    try {
    proc.mq.add(uin,srv.us.getBanHist());
    }catch (Exception ex) {
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }
    
  /**
     * !lroom - Выводит список зарегистрированных комнат
     * @param proc
     * @param uin
     */
    public void commandLRoom(IcqProtocol proc, String uin){
    String s = Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandLRoom.0") + "\n";
    Set<Integer> rid = srv.us.getRooms();
    Integer[] rooms = (Integer[]) rid.toArray(new Integer[0]);
    Arrays.sort(rooms);
    //TODO список комнат по порядку
    for (Integer i : rooms){
    int cnt = 0;
    Enumeration<String> e = srv.cq.uq.keys();
    while (e.hasMoreElements()){
    String g = e.nextElement();
    Users us = srv.us.getUser(g);
    if (us.state == UserWork.STATE_CHAT && us.room == i && !srv.us.authorityCheck(us.id, "invisible")){
    cnt++;
    }
    }
    s += i + " - " + srv.us.getRoom(i).getName() + " «" + cnt + " чел.»" + "\n";
    }
    s += (srv.us.db.getAdvertisement().equals("") || !psp.getBooleanProperty("advertisement.on.off") ?  "" : ("\n" + Messages.getInstance(srv.getName()).getString("Advertisement.0", new Object[] {srv.us.db.getAdvertisement()})));
    proc.mq.add(uin, s);
    }
    
    /**
     * !crroom - Создание новой комнаты
     * @param proc
     * @param uin
     * @param v
     */
    public void commandCrRoom(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "wroom")) return;
    int room = (Integer)v.get(0);
    String s = (String)v.get(1);
    if(srv.us.checkRoom(room)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandCrRoom.0"));return;}
    Rooms r = new Rooms();
    r.setId(room);
    r.setName(s);
    srv.us.createRoom(r);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandCrRoom.1", new Object[] {room}));
    }
    
    /**
     * !chroom - Изменение названия комнаты
     * @param proc
     * @param uin
     * @param v
     */
    public void commandChRoom(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "wroom")) return;
    int room = (Integer)v.get(0);
    String s = (String)v.get(1);
    if(!srv.us.checkRoom(room)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandChRoom.0"));return;}
    Rooms r = srv.us.getRoom(room);
    r.setName(s);
    srv.us.saveRoom(r,"");
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandChRoom.1", new Object[] {room}));
    }
        

    
    /**
     * Проверка, были ли входящие приватные сообщения и от кого
     */
    private String testPM(String sn){
        if(up.get(sn)==null)
            return "";
        else 
            return up.get(sn);
    }
    
    /**
     * Запоминание источника нового входящего сообщения
     */
    private void setPM(String sn, String from_sn){
        up.put(sn,from_sn);
    }
    
    /**
     * Проверка ника на правильность
     */
    public boolean testNick(String sn, String nick){
        Users uss = srv.us.getUser(sn);
        if(psp.testAdmin(sn)) return true; // Админам можно любой ник :)
        String[] ss = psp.getStringProperty("chat.badNicks").split(";");
        String nick1 = radm.changeChar(nick.toLowerCase());
        for(int i=0;i<ss.length;i++){
        	if(nick.toLowerCase().equals("fraer72")){
        		if(sn.equals("1765594"))
        			return true;
        		else 
        			return false;
        	}
            if(nick.toLowerCase().indexOf(ss[i])>=0 ||
                    nick1.toLowerCase().indexOf(ss[i])>=0) return false;
        }
        String s = psp.getStringProperty("chat.badSymNicks");
        String s1 = psp.getStringProperty("chat.goodSymNicks");
        if(s1.equals("")){
        	for(int i=0;i<s.length();i++){
        		if(nick.indexOf(s.charAt(i))>=0) return false;
            }
        } else {
        	for(int i=0;i<nick.length();i++){
        		if(s1.indexOf(nick.charAt(i))<0) return false;
            }
        }
        
        return true;
    }


    /**
     * Вывод списка объектов полномочий
     */
    public String listAuthObjects(){
        String s="Объекты полномочий:\n";
        for(String c:authObj.keySet()){
        	s += c + " - " + authObj.get(c)+"\n";
        }
        return s;
    }

    /**
     * Проверка объекта на наличие в списке
     */
    public boolean testAuthObject(String tst){
    return authObj.containsKey(tst);
    }
    
    /**
     * Есть такая группа?
     * @param tst
     * @return
     */
    public boolean testUserGroup(String tst){
        String[] ss = psp.getStringProperty("auth.groups").split(";");
        for(int i=0;i<ss.length;i++){
            if(tst.equals(ss[i])) return true;
        }
        return false;
    }
    
    /**
     * Проверка юзера, кикнут ли он
     */
    public int testKick(String sn){
    	long tc = srv.us.getUser(sn).lastKick;
    	long t = System.currentTimeMillis();
    	return tc>t ? (int)(tc-t)/60000 : 0;
    }
    
    /**
     * Кик юзера по времени
     */
    public void setKick(String sn, int min, int user_id, String r){
        Users u = srv.us.getUser(sn);
        if(statKick.containsKey(sn)){
            KickInfo ki = statKick.get(sn);
            ki.moder_id = user_id;
            ki.reason = r;
            ki.inc();
            statKick.put(sn, ki);
        } else {
            KickInfo ki = new KickInfo(u.id, user_id, r, min);
            statKick.put(sn, ki);
        }
        u.lastKick = System.currentTimeMillis() + min*60000;
        srv.us.updateUser(u);
    }
    
    /**
     * Список юзеров в состоянии кика
     */
    public String listKickUsers(){
        String r=Messages.getInstance(srv.getName()).getString("ChatCommandProc.listKickUsers.0") + "\n";
        r += Messages.getInstance(srv.getName()).getString("ChatCommandProc.listKickUsers.1") + "\n";
        for(Users u:srv.us.getKickList()){
        	
        	r += ">>" + u.id + "-" + u.localnick + "; [" + (new Date(u.lastKick)).toString() + "]; " +
        		(u.lastKick-System.currentTimeMillis())/60000 + "; ";
        	if(statKick.containsKey(u.sn)){
        		KickInfo ki = statKick.get(u.sn);
        		if(ki.moder_id==0)
        			r += "Admin-Bot";
        		else
        			r += ki.moder_id + "-" + srv.us.getUser(ki.moder_id).localnick;
        		r += "; " + ki.reason + "\n";
        	} else 
        		r += '\n';
        }
        return r;
    } 
        
    /**
     * Проверка молчунов
     * @param uin
     */
    public void testState(String uin){
        if (psp.testAdmin(uin))return;//если юзак сис админ
        long t = floodMap.get(uin).getDeltaTime();
        if(t>(psp.getIntProperty("chat.autoKickTimeWarn")*60000) &&
                !warnFlag.contains(uin)){
            Log.getLogger(srv.getName()).info("Warning to " + uin);

            srv.getIcqProcess(srv.us.getUser(uin).basesn).mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.testState.0"));
            warnFlag.add(uin);
        }
        if(t>(psp.getIntProperty("chat.autoKickTime")*60000)){
            Log.getLogger(srv.getName()).talk("Autokick to " + uin);
            warnFlag.remove(uin);
            kick(srv.getIcqProcess(srv.us.getUser(uin).basesn),uin);
        }
    }
    
    /**
     * Юзер - главный админ?
     * @param proc
     * @param uin
     * @return
     */
    public boolean isAdmin(IcqProtocol proc, String uin){
    Users u = srv.us.getUser(uin);
    if(!psp.testAdmin(uin)){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.auth.0", new Object[] {u.localnick}));
    return false;
    }
    return true;
    }
    
    /**
     * Проверка полномочий
     * @param proc
     * @param uin
     * @param obj
     * @return
     */
    public boolean auth(IcqProtocol proc, String uin, String obj){
    Users u = srv.us.getUser(uin);
    if(!srv.us.authorityCheck(uin, obj)){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.auth.0", new Object[] {u.localnick}));
    return false;
    }
    return true;
    }
    
    /**
     * Тихая проверка полномочий. Не выводит сообщений.
     * @param proc
     * @param uin
     * @param obj
     * @return
     */
    public boolean qauth(IcqProtocol proc, String uin, String obj){
        if(!srv.us.authorityCheck(uin, obj)){
            return false;
        }
        return true;
    }
    
    
    /**
     * Вход в чат c выбором комнаты
     * @param proc
     * @param uin
     * @param mmsg
     * @param v
     */
    public void goChat_Interactive (IcqProtocol proc, String uin, String mmsg, Vector v) {
    Users uss = this.srv.us.getUser(uin);
    boolean f = false;
    int room = 0;
    boolean room_in_chat = false;
    if(uss.localnick==null || uss.localnick.equals("") || uss.state==UserWork.STATE_CAPTCHA || uss.state==UserWork.STATE_NO_REG){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.0", room, uss));
    return;
    }
    if (uss.state==UserWork.STATE_CHAT) return; //Юзер уже в чате
    String pass = "";
    if (uss.state==UserWork.STATE_NO_CHAT){
    if(comMap.containsKey("goChat_"+uin)){
    try{
    room = Integer.parseInt(mmsg);
    }catch(NumberFormatException e){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.1", room, uss) + "\n");
    commandLRoom(proc,uin);
    return;
    }

    if (testClosed(uin)==0 & room==psp.getIntProperty("room.tyrma") && !psp.testAdmin(uin)){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.2", room, uss));
    return;
    }
    
    if (testClosed(uin)>1 & !srv.us.authorityCheck(uin,"room") & room!=psp.getIntProperty("room.tyrma")){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.3", room, uss));
    return;
    }

    if( srv.us.getRoom( room ).getUser_id() != 0 && srv.us.getRoom( room ).getUser_id() != uss.clansman && !psp.testAdmin(uin) ){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.4", room, uss));
    return;
    }

    if (!srv.us.getRoom(room).checkPass(pass) && !psp.testAdmin(uin)){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.5", room, uss));
    return;
    }

    if(psp.getBooleanProperty("vic.on.off"))
    if(psp.getBooleanProperty("vic.time_game.on.off") & Quiz.TestRoom(room) & !psp.testAdmin(uin) & !Quiz.testHours()){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.14", room, uss));
    return;
    }

    if(psp.getBooleanProperty("vic.on.off"))
    if(Quiz.TestRoom(room) & !psp.testAdmin(uin) & Quiz.AllUsersRoom(room) >= psp.getIntProperty("vic.users.cnt")){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.15", room, uss));
    return;
    }
  
    if(srv.us.getRoom(room).checkPersonal() & room != uss.personal_room & !testPrivateRoom(uin, room)){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.17", room, uss));
    return;
    }

    if(!psp.testAdmin(uin)&& !srv.us.checkRoom(room)){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.6", room, uss));
    return;
    }

    room_in_chat = true;
    comMap.remove("goChat_"+uin);
    }
    //TODO список комнат перед входом в чат
    Set<Integer> rid = srv.us.getRooms();
    Integer[] rooms=(Integer[])rid.toArray(new Integer[0]);
    Arrays.sort(rooms);
    if(!room_in_chat){
    String list = Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.7", room, uss) + "\n";
    for(Integer i:rooms){
    int cnt=0;
    Enumeration<String> e = srv.cq.uq.keys();
    while(e.hasMoreElements()){
    String g = e.nextElement();
    Users us = srv.us.getUser(g);
    if(us.state==UserWork.STATE_CHAT && us.room==i){
    cnt++;
    }
    }
    list += "[" + i + "] - " + srv.us.getRoom(i).getName() + " «" + cnt + "чел.»\n";
    }
    String Advertisement = (srv.us.db.getAdvertisement().equals("") || !psp.getBooleanProperty("advertisement.on.off") ?  "" : ("\n\n" + Messages.getInstance(srv.getName()).getString("Advertisement.0", new Object[] {srv.us.db.getAdvertisement()})));
    proc.mq.add(uin, list + "\n----\n" + Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.8", room, uss) + Advertisement);
    comMap.put("goChat_"+uin, new CommandExtend(uin, mmsg, mmsg, v, 2*60000));
    return;
    }
    if(proc.isNoAuthUin(uin)) proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.9", room, uss), 2);
    proc.addContactList(uin);
    uss.state = UserWork.STATE_CHAT;
    uss.basesn = proc.baseUin;
    uss.room=room;
    if(pr.containsKey(uin) & testPrivateRoom(uin, room))
    pr.remove(uin);
    srv.us.updateUser(uss);
    if (!srv.us.authorityCheck(uss.id,"invisible")){
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.10", room, uss), uss.sn, uss.room);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.11", room, uss));
    } else {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.16", room, uss));    
    }
    f = true;
    }
    Log.getLogger(srv.getName()).talk(uss.localnick + " Вошел в чат");
    srv.us.db.log(uss.id,uin,"STATE_IN",uss.localnick + " вошел(а) в чат",uss.room);
    srv.us.db.event(uss.id, uin, "STATE_IN", 0, "", uss.localnick + " Вошел в чат");
    srv.cq.addUser(uin,proc.baseUin, uss.room);
    if(f){
    if(srv.us.getCurrUinUsers(uss.basesn)>psp.getIntProperty("chat.maxUserOnUin"))
    {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.12", room, uss));
    String s = srv.us.getFreeUin();
    uss.basesn = s;
    srv.us.updateUser(uss);
    srv.cq.changeUser(uin, s);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.13", room, uss));
    }
    }
    }

     /**
      * Выбор входа
      * @param proc
      * @param uin
      * @param v
      * @param mmsg
      */

    public void GoToChat(IcqProtocol proc, String uin, Vector v, String mmsg){
    if(psp.getBooleanProperty("inchat.on.off")){
    goChat_Interactive(proc, uin, mmsg, v);
    }else{
    goChat_Usual(proc, uin);
    }
    }


    /**
     * Вход в чат
     * @param proc
     * @param uin
     */
    public void goChat_Usual(IcqProtocol proc, String uin) {
    Users uss = srv.us.getUser(uin);
    boolean f = false;
    if(uss.localnick==null || uss.localnick.equals("") || uss.state==UserWork.STATE_CAPTCHA || uss.state==UserWork.STATE_NO_REG){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.0", uss.room, uss));
    return;
    }
    if (uss.state==UserWork.STATE_CHAT) return; //Юзер уже в чате
    if (uss.state==UserWork.STATE_NO_CHAT) {
    if(proc.isNoAuthUin(uin)) proc.mq.add(uin, Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.9", uss.room, uss), 2);
    proc.addContactList(uin);
    if(psp.getBooleanProperty("vic.on.off"))
    if(psp.getBooleanProperty("vic.time_game.on.off") &  Quiz.TestRoom(uss.room) & !psp.testAdmin(uin) & !Quiz.testHours())
    uss.room = 0;
    if(psp.getBooleanProperty("vic.on.off"))
    if(Quiz.TestRoom(uss.room) & !psp.testAdmin(uin) & Quiz.AllUsersRoom(uss.room) > psp.getIntProperty("vic.users.cnt"))
    uss.room = 0;
    if(srv.us.getRoom(uss.room).checkPersonal() & uss.room != uss.personal_room)
    uss.room = 0;
    uss.state = UserWork.STATE_CHAT;
    uss.basesn = proc.baseUin;
    srv.us.updateUser(uss);
    if (!srv.us.authorityCheck(uss.id,"invisible")){
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.10", uss.room, uss), uss.sn, uss.room);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.11", uss.room, uss));
    } else {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.16", uss.room, uss));
    }
    f = true;
    }
    Log.getLogger(srv.getName()).talk(uss.localnick + " Вошел в чат");
    srv.us.db.log(uss.id,uin,"STATE_IN",uss.localnick + " вошел(а) в чат",uss.room);
    srv.us.db.event(uss.id, uin, "STATE_IN", 0, "", uss.localnick + " Вошел в чат");
    srv.cq.addUser(uin,proc.baseUin, uss.room);
    if(f){
    if(srv.us.getCurrUinUsers(uss.basesn)>psp.getIntProperty("chat.maxUserOnUin")){
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.12", uss.room, uss));
    String s = srv.us.getFreeUin();
    uss.basesn = s;
    srv.us.updateUser(uss);
    srv.cq.changeUser(uin, s);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_goChat("ChatCommandProc.commandgoChat.13", uss.room, uss));
    }
    }
    }


    /**
     * Выход из чата
     * @param proc
     * @param uin
     */
    public void exitChat(IcqProtocol proc, String uin){
    Users uss = srv.us.getUser(uin);
    if (uss.state != UserWork.STATE_CHAT) return; // Юзера нет в чате - игнорируем команду
    uss.state = UserWork.STATE_NO_CHAT;
    srv.us.updateUser(uss);
    Log.getLogger(srv.getName()).talk(uss.localnick + " Ушел(а) из чата");
    srv.us.db.log(uss.id,uin,"STATE_OUT",uss.localnick + " Ушел(а) из чата",uss.room);
    srv.us.db.event(uss.id, uin, "STATE_OUT", 0, "", uss.localnick + " Ушел(а) из чата");
    if (!srv.us.authorityCheck(uss.id,"invisible")){
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString_exitChat("ChatCommandProc.commandexitChat.0", uss), uss.sn, uss.room);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_exitChat("ChatCommandProc.commandexitChat.1", uss));
    }else{
        proc.mq.add(uin,Messages.getInstance(srv.getName()).getString_exitChat("ChatCommandProc.commandexitChat.2", uss));
    }
    srv.cq.delUser(uin);
    }
    
    /**
     * Смена базового уина юзера
     */
    public void changeBaseUin(String uin, String buin){
        Users u = srv.us.getUser(uin);
        u.basesn = buin;
        srv.us.updateUser(u);
        srv.cq.changeUser(uin, buin);
    }
    
    /**
     * Процедура проверки на срабатывание условий флуда. Включает кик при необходимости
     * @param proc
     * @param uin
     * @return истина, если юзер выпнут за флуд
     */
    private boolean testFlood(IcqProtocol proc, String uin){
    	if(warnFlag.contains(uin)) warnFlag.remove(uin);
    	if(floodMap.containsKey(uin)){
    		if(floodMap.get(uin).getCount()>psp.getIntProperty("chat.floodCountLimit")){
    			if(!psp.testAdmin(uin)) akick(proc, uin, "Кик за флуд");
    			return true;
    		}
    	}
    	if(floodMap2.containsKey(uin)) {
    		if(floodMap2.get(uin).getCount()>psp.getIntProperty("chat.floodCountLimit")){
    			if(!psp.testAdmin(uin)) akick(proc, uin, "Кик за флуд");
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * КИК с записью в лог
     */
    public void lkick(IcqProtocol proc, String uin, String txt, int id) {
        kick(proc, uin);
        srv.us.db.log(srv.us.getUser(uin).id,uin,"KICK", txt,srv.us.getUser(uin).room);
        srv.us.db.event(srv.us.getUser(uin).id, uin, "KICK", id, "", txt);
    }
    
    /**
     * КИК с автоматическим определением времени
     */
    public void akick(IcqProtocol proc, String uin, int user_id, String r){
        int def = psp.getIntProperty("chat.defaultKickTime");
        int max = psp.getIntProperty("chat.maxKickTime");
        int i=def;
        if(statKick.containsKey(uin)){
        	int t = statKick.get(uin).len;
            i = t<max ? t*2 : def;
            i = i>max ? max : i;
        }
        tkick(proc, uin, i, user_id, r);
    }
    
    public void akick(IcqProtocol proc, String uin, String r){
        akick(proc, uin, 0, r);
    }
    
    /**
     * КИК с выставлением времени
     */
    public void tkick(IcqProtocol proc, String uin, int t, int user_id, String r){
        Users uss = srv.us.getUser(uin);
        setKick(uin,t, user_id, r);
        if(psp.getBooleanProperty("minus.ball.kick.off"))
        uss.ball -= psp.getIntProperty("minus.ball.kick");
        srv.us.updateUser(uss);
        Log.getLogger(srv.getName()).talk("kick user " + uin + " on " + t + " min.");
        if (srv.us.getUser(uin).state == UserWork.STATE_CHAT)
        if (psp.getBooleanProperty("chat.isShowKickReason")) {
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.tkick.0", new Object[] {t,
        (user_id == 0 ? "" : srv.us.getUser(user_id).group),
        (user_id == 0 ? radm.NICK : srv.us.getUser(user_id).localnick)})
        + "\n"
        + (r.equals("") ? "" : (Messages.getInstance(srv.getName()).getString("ChatCommandProc.tkick.1", new Object[] {r}))));
        } else{
        proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.tkick.2", new Object[] {t}));
        }
        lkick(proc, uin, "kick user on " + t + " min. - " + r, user_id);
        }
    
    public void tkick(IcqProtocol proc, String uin, int t){
        tkick(proc, uin, t, 0, "");
    }
    
    public void kick(IcqProtocol proc, String uin) {
        Users uss = srv.us.getUser(uin);
        if(uss.state != UserWork.STATE_CHAT) return;
        Log.getLogger(srv.getName()).talk("Kick user " + uin);
        
        if(srv.cq.testUser(uin)){
        proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.kick.0"));
        }
        exitChat(proc, uin);
    }
    
    public void kickAll(IcqProtocol proc, String uin) {
        Vector a = srv.us.getUsers(UserWork.STATE_CHAT);
        for(int i=0;i<a.size();i++){
            Users uss = (Users)a.get(i);
            if(!uss.sn.equalsIgnoreCase(uin) || !psp.testAdmin(uss.sn)) kick(proc, uss.sn);
        }
    }
    
    public void ban(IcqProtocol proc, String uin, String adm_uin, String m) {
        Users uss = srv.us.getUser(uin);
        if(uss.state==UserWork.STATE_CHAT) kick(proc, uin);
        Log.getLogger(srv.getName()).talk("Ban user " + uin);
        srv.us.db.log(uss.id,uin,"BAN",m,uss.room);
        srv.us.db.event(uss.id, uin, "BAN", srv.us.getUser(adm_uin).id, adm_uin, m);
        uss.state=UserWork.STATE_BANNED;
        Users wedding = srv.us.getUser(uss.wedding);
        wedding.wedding = 0;
        uss.wedding = 0;
        srv.us.updateUser(wedding);
        srv.us.updateUser(uss);
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.ban.0") +
        (psp.getBooleanProperty("chat.isShowKickReason") ? ("\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.ban.1", new Object[] {m})) : ""));
    }
    
    public void uban(IcqProtocol proc, String uin, String adm_uin) {
        Users uss = srv.us.getUser(uin);
        if(uss.state!=UserWork.STATE_BANNED) return;
        srv.us.db.log(uss.id,uin,"UBAN","",uss.room);
        srv.us.db.event(uss.id, uin, "UBAN", srv.us.getUser(adm_uin).id, adm_uin, "");
        uss.state=UserWork.STATE_NO_CHAT;
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.uban.0"));
        srv.us.updateUser(uss);
    }
    

    
    public void addUser(String uin, IcqProtocol proc){
        if(!srv.us.testUser(uin)){
            srv.us.reqUserInfo(uin,proc);
        }
    }
    
    public boolean isChat(IcqProtocol proc,String uin) {
        try{
            if(srv.us.getUser(uin).state==UserWork.STATE_CHAT){
                return true;
            } else {
                if(!psp.testAdmin(uin)){
                proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.isChat.0"));
                }
                return false;
            }
        } catch (Exception ex){
            return false; //если это новый пользователь
        }
    }
    
    public boolean isBan(String uin) {
        try{
            return (srv.us.getUser(uin).state==UserWork.STATE_BANNED);
        } catch (Exception ex) {
            return false; //если это новый пользователь
        }
    }
    
    /**
     * Обработка сообщений о флуде - слишком частые сообщения должны быть блокированы
     */
    public void parseFloodNotice(String uin, String msg, IcqProtocol proc){
    	if(isBan(uin)) return;
        if(testKick(uin)>0) return;
        if(!srv.us.testUser(uin)) return; // Юзер не зареган
        proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.parseFloodNotice.0"));
    	if(floodMap2.containsKey(uin)){
    		FloodElement e = floodMap2.get(uin);
    		e.addMsg("1");
    		floodMap2.put(uin, e);
    	} else {
    		FloodElement e = new FloodElement(psp.getIntProperty("chat.floodTimeLimit")*1000);
    		e.addMsg("1");
    		floodMap2.put(uin, e);
    	}
    	testFlood(proc, uin);
    }
    
    /**
     * Создает простой арифметический пример для защиты от ботов при регистрации
     * @return
     */
    private String getCaptcha(){
    	int i1 = radm.getRND(100);
    	int i2 = radm.getRND(15);
    	String s = intToString(i1) + " + " + intToString(i2) + "=" + (i1+i2);
    	return s;
    }
    
    /**
     * Число прописью
     * @param i
     * @return
     */
    private String intToString(int k){
        String[] ss = {"ноль","один","два","три","четыре","пять","шесть","семь","восемь","девять","десять",
                "одиннадцать","двенадцать","тринадцать","четырнадцать","пятнадцать","шестнадцать",
                "семнадцать","восемнадцать","девятнадцать","двадцать","тридцать","сорок","пятьдесят",
                "шестьдесят","семьдесят","восемьдесят","девяносто","сто","двести","тристо","четыресто",
                "пятьсот","шестьсот","семьсот","восемьсот","девятьсот","тысяча"};
        String[] ss2 = {"одна","две"};
        String s = "";
        int c1 = k/1000;
        int c2 = k - c1*1000;
        int i1 = c1/100;
        int i2 = (c1 - i1*100)/10;
        int i3 = c1 - i1*100 - i2*10;
        if (i1>0) s += ss[i1+27] + " ";
        if (i2>1) s += ss[i2+18] + (i3>2 ? " " + ss[i3] : (i3>0 ? " " + ss2[i3-1] : "")) + " ";
        else if (i2==0 && i3 >0 && i3<3) s += (i3==1 ? ss2[0] : ss2[1]) + " ";
        else if (i2>0 || i3>0) s += ss[i3 + i2*10] + " ";
        if (c1>0) {
            switch (i3+(i2==1 ? 10 : 0)){
            case 1:
                s += "тысяча ";
                break;
            case 2:
            case 3:
            case 4:
                s += "тысячи ";
                break;
            default:
                s += "тысяч ";
            }
        }
        
        i1 = c2/100;
        i2 = (c2 - i1*100)/10;
        i3 = c2 - i1*100 - i2*10;
        if (i1>0) s += ss[i1+27] + " ";
        if (i2>1) s += ss[i2+18] + (i3>0 ? " " + ss[i3] : "") + " ";
        else if (i2>0 || i3>0) s += ss[i3 + i2*10] + " "; 
        
        if(k==0) s = ss[0] + " ";
        return s;
    }

       /**
       * Закрывание пользователя на время
       * !запереть <id> <id> <id>
       * @author fraer72
       */

   private void BanRoom(IcqProtocol proc, String uin, Vector v, String mmsg){
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "banroom")) return;
        int i1 = (Integer) v.get(0);
        int time = (Integer) v.get(1);
        String r = (String) v.get(2);
        long t = System.currentTimeMillis()+(time*60000);
        Users u = srv.us.getUser(i1);
        if (psp.testAdmin(u.sn) || qauth(proc, u.sn, "anti_banroom")) {
        proc.mq.add(uin, "Вы не можете закрыть пользователя, он владеет иммунитетом");
        return;
        }
        if (u.id == 0){
        proc.mq.add(uin, "Пользователь не найден");
        return;
        }
        if (u.state != UserWork.STATE_CHAT){
        proc.mq.add(uin, "Этого пользователя нет в чате.");
        return;
        }
        if (time == 0){
        proc.mq.add(uin, "Необходимо указать время");
        return;
        }
        if (time > psp.getIntProperty("chat.defaultBanroomTime")) {
        proc.mq.add(uin, "Максимальное время закрытия - " + psp.getIntProperty("chat.defaultBanroomTime") + " минут");
        return;
        }
        if (r.equals("") || r.equals(" ")){
        proc.mq.add(uin, "Необходимо добавить причину закрытия");
        return;
        }
        if (uin.equals(u.sn)){
        proc.mq.add(uin, "Нельзя закрывать самого себя :)");
        return;
        }
        srv.cq.addMsg(u.localnick + "|" + u.id + "|" + " заперт в комнате: " + srv.us.getRoom(psp.getIntProperty("room.tyrma")).getName() + "|" + psp.getIntProperty("room.tyrma") + "| на " + time + " минут по причине: " + r + ", пользователем: " + srv.us.getUser(uin).localnick, u.sn, u.room);
        String nick = u.localnick + Messages.getInstance(srv.getName()).getString("RobAdmin.close.0");
        if(testClosed(u.sn) == 0){
        u.localnick = nick;
        srv.us.db.event(u.id, uin, "REG", 0, "", nick);
        }
        u.room = psp.getIntProperty("room.tyrma");
        u.lastclosed = t;
        srv.us.revokeUser(u.id, "room");
        srv.us.updateUser(u);
        srv.cq.changeUserRoom(u.sn, psp.getIntProperty("room.tyrma"));
        srv.us.db.event(u.id, uin, "Banroom", 0, "", " заперт в комнате: " + psp.getIntProperty("room.tyrma") + " на " + time + " минут\nпричина: " + r + ", пользователем: " + srv.us.getUser(uin).localnick);
        srv.us.db.log(u.id, uin, "Banroom", u.localnick + "|" + u.id + "|" + " заперт в комнате " + srv.us.getRoom(psp.getIntProperty("room.tyrma")).getName() + "|" + psp.getIntProperty("room.tyrma") + "| на " + time + " минут", u.room);
        srv.cq.addMsg(u.localnick + "|" + u.id + "|" + " заперт на " + time + " минут по причине: " + r + ", пользователем: " + srv.us.getUser(uin).localnick, u.sn, u.room);
        srv.getIcqProcess(u.basesn).mq.add(u.sn, "За " + r + " ты заперт в комнате " + psp.getIntProperty("room.tyrma") + " на " + time + " минут");
        proc.mq.add(uin, "Пользователь " + u.localnick + " успешно заперт в комнате " + psp.getIntProperty("room.tyrma"));
        }

       /**
       * Список закрытых пользователей
       * !закрытые
       * @author Юрий
       */

       private void commandZakHist(IcqProtocol proc, String uin) {
       if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "zakhist")) return;
       try{
       proc.mq.add(uin, "Всего пользователей в тюрьме: " + srv.us.statBanroomColCount() + srv.us.getZakHist());
       }catch (Exception ex){
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
       * Проверяем закрыт пользователь или нет
       */

        private void freedom(String uin){
        Users u = srv.us.getUser(uin);
        if (u.state == UserWork.STATE_CHAT){
        srv.cq.addMsg("Пользователь |" + u.id + "|" + u.localnick + " был выпущен из заключения.", "", psp.getIntProperty("room.tyrma"));
        srv.cq.addMsg("Пользователь |" + u.id + "|" + u.localnick + " был выпущен из заключения.", "", 0);
        }
        String nick = u.localnick.replace(Messages.getInstance(srv.getName()).getString("RobAdmin.close.0"),"");
        u.localnick = nick;
        srv.us.db.event(u.id, uin, "REG", 0, "", nick);
        srv.us.grantUser(u.id, "room");
        u.room = 0;
        if (u.state == UserWork.STATE_CHAT) srv.cq.changeUserRoom(u.sn,0);
        srv.us.updateUser(u);
        srv.us.db.log(u.id,uin,"FREE","был(а) выпущен(а) из заключения.",0);
        }

       /**
       * Проверяем закрыт пользователь или нет
       */

        public int testClosed(String sn){
    	long tc = srv.us.getUser(sn).lastclosed;
    	long t = System.currentTimeMillis();
    	return tc>t ? (int)(tc-t)/60000 : 0;
        }

       /**
       * Проверяем имеет ли пользователь временые полномочия
       */
        private int testGroupTime(String sn){
    	long tc = srv.us.getUser(sn).grouptime;
    	long t = System.currentTimeMillis();
    	return tc>t ? (int)(tc-t)/60000 : 0;
        }

       /**
       * Группа на определенное время
       * !модер <id> <id>
       * @author fraer72
       */
     private void GroupTime(IcqProtocol proc, String uin, Vector v, String mmsg){
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "group_time")) return;
        int i1 = (Integer) v.get(0);
        int time = (Integer) v.get(1);
        String group = (String) v.get(2);
        int day = time;
        Users u = srv.us.getUser(i1);
        if (u.id == 0){
        proc.mq.add(uin, "Пользователь не найден");
        return;
        }
        if (u.state != UserWork.STATE_CHAT){
        proc.mq.add(uin, "Этого пользователя нет в чате.");
        return;
        }
        if (time == 0){
        proc.mq.add(uin, "Необходимо указать время");
        return;
        }
        if(group.equals("")){
        proc.mq.add(uin, "Необходимо ввести название группы");
        return;
        }
        if(!testUserGroup(group)){
        proc.mq.add(uin, "Такой группы не существует");
        return;
        }
        setGrouptime(u.sn,day);
        srv.us.getUser(i1).group = group;
        srv.us.getUser(i1).country = 1;
        boolean groupp = srv.us.setUserPropsValue(u.id, "group", group) &&
        srv.us.setUserPropsValue(u.id, "grant", "") &&
        srv.us.setUserPropsValue(u.id, "revoke", "");
        srv.us.clearCashAuth(u.id);
        srv.us.updateUser(u);
        if(groupp){
        srv.getIcqProcess(u.basesn).mq.add(u.sn, "Тебе назначена группа -  \"" + group + "\" на " + time + " (день)дней");
        proc.mq.add(uin,"Пользователю " + u.localnick + " успешно назначена группа -  \"" + group + "\" на " + time + " (день)дней");
        }else{
        proc.mq.add(uin, "Произошла ошибка");
        }
     }

       /**
        * @author mmaximm
        * Устанавливаем дату действия группы
        * @param uin
        * @param day
        */
        public void setGrouptime(String uin, int day) {
        Users us = srv.us.getUser(uin);
        Date date;
        date = new Date();
        date.setDate(date.getDate() + day);
        us.grouptime = date.getTime();
        srv.us.updateUser(us);
    }
       /**
       * Снятие полномочий при окрнчании времени 
       * @author fraer72
       */
       private void GroupNoTime(IcqProtocol proc, String uin){
       if (srv.us.getUser(uin).state == UserWork.STATE_CHAT){
       proc.mq.add(uin,"Твоё время в группы - \"" + srv.us.getUser(uin).group + "\" оконченно");
       srv.us.getUser(uin).group = "user";
       boolean group = srv.us.setUserPropsValue(srv.us.getUser(uin).id, "group", "user") &&
       srv.us.setUserPropsValue(srv.us.getUser(uin).id, "grant", "") &&
       srv.us.setUserPropsValue(srv.us.getUser(uin).id, "revoke", "");
       srv.us.clearCashAuth(srv.us.getUser(uin).id);
       }
       }
     
     /**
     * Бутылочка
     * !бутылочка
     */
    private void commandVial(IcqProtocol proc, String uin, Vector v, String mmsg){
    if (!isChat(proc, uin) && !psp.testAdmin(uin)){
    return;
    }
    if((System.currentTimeMillis()-VialTime)<1000*psp.getIntProperty("time.igra.bytilochka")){
    proc.mq.add(uin, "Играть можно раз в  " + psp.getIntProperty("time.igra.bytilochka") + " секунд");
    return;
    }
    try {
    int c = 0;
    Set<Integer> users = new HashSet();
    Users uss = srv.us.getUser(uin);
    Enumeration<String> e = srv.cq.uq.keys();
    while (e.hasMoreElements()){
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if (us.state == UserWork.STATE_CHAT){
    if (us.room == uss.room) {
    users.add(us.id);
    c++;
    }
    }
    }
    if(c == 1){
    proc.mq.add(uin, "В комнате нет людей, неским играть");
    return;
    }
    String getMessages = srv.us.getVial();
    Users u = srv.us.getUser((Integer)users.toArray()[radm.getRND(users.size())]);
    if (uss.room != psp.getIntProperty("room.igra.bytilochka")){
    proc.mq.add(uin, "Играть можно только в " + psp.getIntProperty("room.igra.bytilochka") + " комнате");
    return;
    }
    if (u.id == uss.id){
    proc.mq.add(uin, uss.localnick + "|" + uss.id + "|" + " ну ка крутани еще");
    return;
    }
    srv.cq.addMsg("БУТЫЛОЧКА>> Пользователь " + uss.localnick + "|" + uss.id + "|" + " должен " + getMessages + " пользователя(ю) " + u.localnick + "|" + u.id + "|", uss.sn, uss.room);
    proc.mq.add(uin, "Ты должен " + getMessages + " пользователя(ю) " + u.localnick + "|" + u.id + "|");
    VialTime = System.currentTimeMillis();
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }

    /**
     * Фразы для бутылочки
     * !фраза текст
     */
    private void commandFraza(IcqProtocol proc, String uin, Vector v){
    if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
    if(!auth(proc,uin, "fraza")) return;
    String sn = (String) v.get(0);
    int k = sn.length();
    if (sn.equals("") || sn.equals(" ")){
    proc.mq.add(uin, "Ну что же ты пишешь пустую фразу");
    return;
    }
    if (!(k > psp.getIntProperty("vial.fraza.leght"))){
    try{
    srv.us.db.AddAButilochka((int)srv.us.db.getLastIndex("butilochka"), sn);
    proc.mq.add(uin, "Фраза успешно добавленна в БД\nФраз в БД: " + (int)srv.us.db.getLastIndex("butilochka"));
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }else{
    proc.mq.add(uin, "Слишком длинная фраза (> " + psp.getIntProperty("vial.fraza.leght") + "). Фраза не сохранена");
    }
    }

    private void commandAdmini(IcqProtocol proc, String uin) {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
    if(!auth(proc,uin, "admlist")) return;
    try{
    String list = "";
    Enumeration<String> e = srv.cq.uq.keys();
    while (e.hasMoreElements()) {
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if (us.state == UserWork.STATE_CHAT && !srv.us.getUserGroup(us.id).equals("user")){
    list += "- " + us.id + " - " + us.localnick + " » |" + us.room + "| - [" + us.group + "]" + "\n";
    }
    }
    proc.mq.add(uin, "В данный момент в чате online :\nИд|Ник|Комната\n" + list);
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }

    private void commandAdminiAll(IcqProtocol proc, String uin) {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
    if(!auth(proc,uin, "admalllist")) return;
    try{
    proc.mq.add(uin, srv.us.allAdmins());
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }

    /**
     * Смена ника пользователю
     * @param proc
     * @param uin
     * @param v
     * @param mmsg
     */
    private void commandchnick(IcqProtocol proc, String uin, Vector v, String mmsg){
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "chnick")) return;
    try{
    int i = (Integer)v.get(0);
    String nick = (String)v.get(1);
    int len = nick.length();
    Users u = srv.us.getUser(i);
    Users uss = srv.us.getUser(uin);
    if (psp.testAdmin(u.sn) || qauth(proc, u.sn, "anti_chnick")) {
    proc.mq.add(uin, "Вы не можете сменить ник пользователя, он владеет иммунитетом");
    return;
    }
    if(u.id == 0){
    proc.mq.add(uin,"Пользователь не найден");
    return;
    }
    if (uin.equals(u.sn)){
    proc.mq.add(uin, "Для смены своего ника используй !рег <ник>");
    return;
    }
    if (!(len>psp.getIntProperty("max.chnick"))){
    String oldNick = u.localnick;
    u.localnick=nick;
    srv.us.updateUser(u);
    srv.us.db.event(u.id, uin, "REG", 0, "", nick);
    if(u.state == UserWork.STATE_CHAT){
    srv.cq.addMsg("У пользователя " + oldNick + " ник изменен на " + nick+ " изменил его пользователь " +uss.localnick, "", u.room);
    }
    proc.mq.add(uin,"Ник успешно изменен");
    } else proc.mq.add(uin,"Слишком длинный ник(>"+ psp.getIntProperty("max.chnick")+" ). Ник не изменён.");
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }

     /**
      * Повысить рейтинг пользователю
      * @param proc
      * @param uin
      * @param v
      */

    private void commandOchkiplys(IcqProtocol proc, String uin, Vector v){
    if(!isChat(proc, uin) && !psp.testAdmin(uin)) return;
    try{
    int i = (Integer) v.get(0);
    Users u = srv.us.getUser(i);
    Users us = srv.us.getUser(uin);
    if(u.id == 0){
    proc.mq.add(uin, "Пользователь не найден");
    return;
    }
    if(us.id == i){
    proc.mq.add(uin, "Повышать рейтинг саму себе нельзя");
    return;
    }
    if(srv.us.getCountgolosChange(us.id, u.id) >= 1){
    proc.mq.add(uin, "Вы можете только раз в сутки голосовать за одного и того же пользователя");
    return;
    }
    u.ball += psp.getIntProperty("ball.respekt");
    srv.us.updateUser(u);
    srv.us.db.event(us.id, uin, "GOLOS", u.id, u.sn, "дал бал");
    proc.mq.add(uin, "Рейтинг пользователя " + u.localnick + "|" + u.id + "| повышен");
    srv.getIcqProcess(u.basesn).mq.add(u.sn, "Пользователь " + us.localnick + "|" + us.id + "| повысил тебе рейтинг, он составляет " + u.ball + " баллов");
    } catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }

     /**
     * Понизить рейтинг
     * !понизить <id>
     */

        private void commandOchkiminys(IcqProtocol proc, String uin, Vector v){
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        try{
        int i = (Integer) v.get(0);
        Users u = srv.us.getUser(i);
        Users us = srv.us.getUser(uin);
        if (u.id == 0){
        proc.mq.add(uin, "Пользователь не найден");
        return;
        }
        if (us.id == i){
        proc.mq.add(uin, "Понижать рейтинг саму себе нельзя");
        return;
        }
        if (srv.us.getCountgolosChange(us.id, u.id) >= 1){
        proc.mq.add(uin, "Вы можете только раз в сутки голосовать за одного и того же пользователя");
        return;
        }
        u.ball -= psp.getIntProperty("ball.respekt");
        srv.us.updateUser(u);
        srv.us.db.event(us.id, uin, "GOLOS", u.id, u.sn, "снял бал");
        proc.mq.add(uin, "Рейтинг пользователя " + u.localnick + "|" + u.id + "| понижен");
        srv.getIcqProcess(u.basesn).mq.add(u.sn, "Пользователь " + us.localnick + "|" + us.id + "| понизил тебе рейтинг, он составляет " + u.ball + " баллов");
        } catch (Exception ex){
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }

       /**
       * Установка пароля на комнату
       * !пароль <pass>
       * @author jimbot
       */

       private void commandSetpass(IcqProtocol proc, String uin, Vector v){
        if(!auth(proc,uin, "setpass")) return;
        String s = (String)v.get(0);
        int room = srv.us.getUser(uin).room;
        Rooms r = srv.us.getRoom(room);
        r.setPass(s);
        srv.us.saveRoom(r, s);
        Log.getLogger(srv.getName()).info("Установлен пароль на комнату " + room + ": " + s);
        proc.mq.add(uin,"Пароль "+s+" на комнату успешно установлен.");
        }

       /**
       * Список админ сообщений
       * !адмлист
       * @author fraer72
       */
       private void commandAdmList(IcqProtocol proc, String uin){
       if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
       if(!auth(proc,uin, "admlist")) return;
       try{
       cutsend(proc, uin, srv.us.getVseAdmMsg());
       }catch (Exception ex){
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

    /**
     * Фразы для админ бота
     */
    private void commandRobMsg(IcqProtocol proc, String uin, Vector v){
    if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
    if(!auth(proc,uin, "robmsg")) return;
    String sn = (String) v.get(0);
    int k = sn.length();
    if (sn.equals("") || sn.equals(" ")){
    proc.mq.add(uin, "Ну что же ты пишешь пустую фразу");
    return;
    }
    if (!(k > psp.getIntProperty("rob.msg"))){
    try{
    radm.AddAdmin((int) srv.us.db.getLastIndex("robadmin"),sn);
    proc.mq.add(uin, "Фраза успешно добавленна в БД\nФраз в БД: " + (int)srv.us.db.getLastIndex("robadmin"));
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }else{
    proc.mq.add(uin, "Слишком длинная фраза (> " + psp.getIntProperty("rob.msg") + "). Фраза не сохранена");
    }
    }

    /**
     * Смена статуса чата
     * @param proc
     * @param uin
     * @param v
     */


   private void commandXst(IcqProtocol proc, String uin, Vector v){
   if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
   if(!auth(proc,uin, "xst")) return;
   if(xstatus.isStart()){
   proc.mq.add(uin,"Запущена авто смена xstatus`ов. Вы не можете сменить статус.");
   return;
   }
   int nomer = (Integer)v.get(0);
   if (nomer == 0){
   icq.ListStatus(proc, uin);
   return;
   }
   String text = (String)v.get(1);
   if(text.equals("") || text.equals(" ")){
   proc.mq.add(uin,"Текст статуса отсутствует");
   return;
   }  
   psp.setIntProperty("icq.xstatus", nomer);
   psp.setStringProperty("icq.STATUS_MESSAGE2", text);
   Manager.getInstance().getService(srv.getName()).getProps().save();
   if (nomer >= 1 && nomer <= 37){
   try {
   for(int uins = 0; uins < srv.con.uins.count(); uins++){
   srv.con.uins.proc.get(uins).setXStatus(nomer, text);
   }
   proc.mq.add(uin,"Статус чата изменён успешно");
   }catch (Exception ex){
   Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
   }
   }else{
   proc.mq.add(uin,"Числа должно быть от 1 до 34");
   }
   }

   /**
    * Перезапуск сервиса
    * @param proc
    * @param uin
    * @param v
    * @param mmsg
    */


  private void commandRestart(IcqProtocol proc, String uin, Vector v, String mmsg){
  if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
  if(!auth(proc,uin, "restart")) return;
  Manager.getInstance().restartService(srv.getName());
  }

    /**
    * Установка статуса
    * !статус <текст>
    * @author fraer72
    */
    private void commandStatus(IcqProtocol proc, String uin, Vector v) {
    if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
    if (!auth(proc, uin, "status_user")) return;
    try {
    String lstatus = (String) v.get(0);
    int len = lstatus.length();
    if (lstatus.equals("")){
    Users uss = srv.us.getUser(uin);
    srv.cq.addMsg(uss.localnick + "|" + uss.id + "|" + " убрал статус", uss.sn, uss.room);
    Log.getLogger(srv.getName()).talk(uss.localnick + "|" + uss.id + "|" + " убрал статус");
    proc.mq.add(uin, "Вы убрали статус");
    uss.status  = "";
    srv.us.updateUser(uss);
    return;
    }
    if (len > psp.getIntProperty("about.user.st")){
    proc.mq.add(uin, "Слишком длинный статус (> "+psp.getIntProperty("about.user.st") +"). Статус не изменён.");
    return;
    }
    Users uss = srv.us.getUser(uin);
    srv.cq.addMsg(uss.localnick + "|" + uss.id + "|" + " меняет статус на |" + lstatus + "|", uss.sn, uss.room);
    Log.getLogger(srv.getName()).talk(uss.localnick + "|" + uss.id + "|" + " меняет статус на |" + lstatus + "|");
    uss.status  = lstatus;
    srv.us.updateUser(uss);
    proc.mq.add(uin, "Вы сменили статус на |" + lstatus + "|");
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }

     /**
     * Показ разбаненных пользователей
     * !разбанлист
     */
    private void commandUbanHist(IcqProtocol proc, String uin) {
    if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
    if (!auth(proc, uin, "ubanhist")) return;
    try {
    proc.mq.add(uin, srv.us.getUbanHist());
    }catch (Exception ex){
    Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
    }
    }

       /**
       * Удаление комнат без перезагрузки чата
       * !удалить <id>
       * @author Юрий
       */

      private void commandDeleteRoom(IcqProtocol proc, String uin, Vector v){
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "wroom")) return;
        int room = (Integer) v.get(0);
        Users uss = srv.us.getUser(uin);
        if (!srv.us.checkRoom(room)){
        proc.mq.add(uin, "Такой комнаты не существует!");
        return;
        }
        srv.cq.addMsg("Была удалена комната " + srv.us.getRoom(room).getName() + "|" + room + "| пользователем " + uss.localnick + "|" + uss.id + "|", uin, uss.room);
        proc.mq.add(uin, "Комната " + room + " была успешно удалена");
        Rooms r = new Rooms();
        r.setId(room);
        srv.us.deleteRoom(r);
        }

        /**
        *  !пригласитьид <id <msg>
        *  Приглашение в чат по иду
        *  @author fraer72
        */
        private void commandInvitation_ID(IcqProtocol proc, String uin, Vector v, String tmsg) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "invitation")) return;
        try{
        int i = (Integer)v.get(0);
        String s = (String)v.get(1);
        Users us = srv.us.getUser(uin);
        Users uss = srv.us.getUser(i);
        if(psp.getBooleanProperty("antiadvertising.on.off") & psp.getBooleanProperty("antiadvertising.in.on.off") & !psp.testAdmin(uin))
        s = antiadvertising(s, uin);
        if(uss.id == 0){
        proc.mq.add(uin,"Такого пользователя не существует");
        return;
        }
        if (uss.state==UserWork.STATE_CHAT){
        proc.mq.add(uin,"Пользователь уже сидит в чате!");
        return;
        }
        if (s.length() > psp.getIntProperty("chat.MaxMsgSize")){
        s = s.substring(0, psp.getIntProperty("chat.MaxMsgSize"));
        proc.mq.add(uin, "Слишком длинное сообщение было обрезано: " + s);
        }
        if(s.equals("")){
        proc.mq.add(uin,srv.us.getUser(uin).localnick  + " Необходимо добавить сообщение!\nПример: !позвать <id> давай к нам");
        return;
        }
        srv.us.db.event(uss.id, uin, "PRIG", us.id, us.sn, "позвал в чат");
        srv.us.db.log(uss.id,uin,"PRIG",">> Вас зовут в чат " + srv.us.getUser(uin).localnick + " |" + srv.us.getUser(uin).id + "|",uss.room);
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn,"Пользователь " + srv.us.getUser(uin).localnick + " |" + srv.us.getUser(uin).id + "| зовет вас в чат " + " и вам от него сообщение: " + s);
        proc.mq.add(uin,"Вы позвали в чат пользователя " + uss.localnick + " |" + uss.id + "|");
        /*Оповещение админа*/
        if(psp.getBooleanProperty("Priglashenie.on.off")){
        String[] admins = psp.getStringProperty("chat.Priglashenie").split(";");
        for (int t=0 ;t<admins.length; t++){
        if(admins[t] == null || admins[t].equals("")){
        Log.getLogger(srv.getName()).error("В админке не указан(ны) уин(ы) для оповещения!!!");
        return;
        }
        Users usss = srv.us.getUser(admins[t]);
        srv.getIcqProcess(usss.basesn).mq.add(usss.sn,"Пользователя " + us.localnick +  "|" + us.id + "|" + " зовет в чат " + uss.id+ "|" + uss.localnick + "| "
        + "\n Сообщение: "+s);
        }
        }
        }catch (Exception ex){
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }

        /*
         *  !пригласитьуин <uin> <msg>
         *  Приглашение в чат по уину
         *  @author fraer72
         */
        private void commandInvitation_UIN(IcqProtocol proc, String uin, Vector v, String tmsg) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "invitation")) return;
        try{
        String uins = (String)v.get(0);
        String s = (String)v.get(1);
        Users uss = srv.us.getUser(uins);
        Users us = srv.us.getUser(uin);
        if(s.equals("")){
        proc.mq.add(uin,srv.us.getUser(uin).localnick  + " Необходимо добавить сообщение!\nПример: !пригласить <uin> давай к нам");
        return;
        }
        if(psp.getBooleanProperty("antiadvertising.on.off") & psp.getBooleanProperty("antiadvertising.in.on.off") & !psp.testAdmin(uin))
        s = antiadvertising(s, uin);
        if (radm.testMat1(radm.changeChar(s))){
        proc.mq.add(uin,"В сообщении ''МАТ''");
        return;
        }
        if (s.length() > psp.getIntProperty("chat.MaxMsgSize")){
        s = s.substring(0, psp.getIntProperty("chat.MaxMsgSize"));
        proc.mq.add(uin, "Слишком длинное сообщение было обрезано: " + s);
        }
        srv.us.db.event(uss.id, uin, "PRIG", us.id, us.sn, "позвал в чат");
        proc.mq.add(uins,"Пользователь " + srv.us.getUser(uin).localnick + " |" + srv.us.getUser(uin).id + "| приглашает вас в чат " + " и вам от него сообщение: " + s);
        proc.mq.add(uin,"Приглашение на уин: "+uins+"  отправлено");
        /*Оповещение админа*/
        if(psp.getBooleanProperty("Priglashenie.on.off")){
        String[] admins = psp.getStringProperty("chat.Priglashenie").split(";");
        for (int t=0 ;t<admins.length; t++){
        if(admins[t] == null || admins[t].equals("")){
        Log.getLogger(srv.getName()).error("В админке не указан(ны) уин(ы) для оповещения!!!");
        return;
        }
        Users usss = srv.us.getUser(admins[t]);
        srv.getIcqProcess(usss.basesn).mq.add(usss.sn,"Пользователя " + us.localnick +  "|" + us.id + "|" + " отправил приглашение на уин " + uins+ "| "
        + "\n Сообщение: "+s);
        }
        }
        }catch (Exception ex){
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }

        /**
         * Отправка сообщения во все комнаты
         * !везде <text>
         * @author Sushka
         */
        private void commandSend(IcqProtocol proc, String uin, Vector v) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
        if (!auth(proc, uin, "allroom_message"))return;
        try {
        String smsg = (String) v.get(0);
        if (smsg.equals("") || smsg.equals(" "))return;
        if (radm.testMat1(radm.changeChar(smsg))){
        proc.mq.add(uin,"В сообщении ''МАТ''");
        return;
        }
        allRoomMsg(uin, "System Messages: " + smsg);
        proc.mq.add(uin, "Сообщение отправленно успешно");
        } catch (Exception ex){
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }

        /**
         * Очистка списка адм сообщений
         * @param proc
         * @param uin
         */

        private void commandDelAdmMsg(IcqProtocol proc, String uin) {
        if(!isChat(proc, uin) && !psp.testAdmin(uin))return;
        if(!auth(proc, uin, "deladmmsg"))return;    
        srv.us.db.executeQuery( " TRUNCATE `admmsg` " );
        proc.mq.add(uin, "Таблицы \"admmsg\" очищена" );
        }

        /**
         * Свадьба
         * @author fraer72
         * @param proc
         * @param uin
         * @param v
         * @param mmsg
         */

        private void commandWedding(IcqProtocol proc, String uin, Vector v, String mmsg) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "wedding")) return;
        int bride = (Integer)v.get(0);
        int groom  = (Integer)v.get(1);
        Users u = srv.us.getUser(uin); // Который набрад команду
        Users us = srv.us.getUser(bride);// Невеста
        Users uss = srv.us.getUser(groom);// Жених
        if(u.room != psp.getIntProperty("wedding.room")){
        proc.mq.add(uin,"Проводить свадьбы можно тока в " + psp.getIntProperty("wedding.room") + " комнате");
        return;    
        }
        /*Тест невесты*/
        if(us.id == 0){
        proc.mq.add(uin,"Пользователя с id=" + bride + " не существует");
        return;
        }
        if (us.state!=UserWork.STATE_CHAT){
        proc.mq.add(uin,"Пользователь " + us.localnick + " не в чате!");
        return;
        }
        if(us.room != psp.getIntProperty("wedding.room")){
        proc.mq.add(uin,"Невеста должна присутствовать в данной комнате");
        return;    
        }
        if(us.wedding != 0){
        proc.mq.add(uin,"Невеста уже состоит в браке");
        return;      
        }
        /*Тест жениха*/
        if(uss.id == 0){
        proc.mq.add(uin,"Пользователя с id=" + groom + " не существует");
        return;
        }
        if (uss.state!=UserWork.STATE_CHAT){
        proc.mq.add(uin,"Пользователь " + uss.localnick + " не в чате!");
        return;
        }
        if(uss.room != psp.getIntProperty("wedding.room")){
        proc.mq.add(uin,"Жених должен присутствовать в данной комнате");
        return;    
        }
        if(uss.wedding != 0){
        proc.mq.add(uin,"Жених уже состоит в браке");
        return;
        }
        /*Тест на пол*/
        if(psp.getBooleanProperty("wedding.floor.on.off")){
        /*Невеста*/
        if(us.homepage.equalsIgnoreCase("")){
        proc.mq.add(uin,"Невеста ''" + us.localnick + "'' должна указать свой пол!");
        return;
        } else if (!us.homepage.equalsIgnoreCase("ж")){
        proc.mq.add(uin,"Невеста не женского пола. Однополые браки в чате запрещены!");
        return;
        }
        /*Жених*/
        if(uss.homepage.equalsIgnoreCase("")){
        proc.mq.add(uin,"Жених ''" + uss.localnick + "'' должен указать свой пол!");
        return;
        } else if (!uss.homepage.equalsIgnoreCase("м")){
        proc.mq.add(uin,"Жених не мужского пола. Однополые браки в чате запрещены!");
        return;
        }
        }
        // Запоминаем текушую свадьбу для дальнейших действий
        WeddingInfo sv = new WeddingInfo(u.id, us.id, uss.id, 0 , 0);
        int Wedding_id = sv.count;
        sv.Count();
        WeddingInfo.put(Wedding_id, sv);// Запомним свадьбу
        Wedding_ID.put("Wedding_"+us.sn, Wedding_id);// Запоминаем ид свадьбы для невесты
        Wedding_ID.put("Wedding_"+uss.sn, Wedding_id);// Запоминаем ид свадьбы для жениха
        // Запоминаем статус пользователя в свадьбе
        Wedding_STATUS.put("Wedding_"+us.sn, 0);
        Wedding_STATUS.put("Wedding_"+uss.sn, 1);
        // Вызываем метод для определения согласен пользователь или нет
        TestWedding(us.sn, mmsg, v);// Невесиа
        TestWedding(uss.sn, mmsg, v);// Жених
        }

        /**
         * Проверка согласия
         * @author fraer72
         * @param proc
         * @param uin
         * @param v
         * @param mmsg
         * @param - 0 - невеста, 1 - жених
         */

        private void TestWedding(String uin, String mmsg, Vector v){
        Users uss = srv.us.getUser(uin);
        String[] message0 = "согласна;согласен".split(";");
        String[] message1 = "женой;мужем".split(";");
        String msg = "";
        int id = 0;
        boolean Wedding = false;
        WeddingInfo sv = WeddingInfo.get(Wedding_ID.get("Wedding_"+uin));
        // Надо понять кто жених кто невеста в этой каше :D
        if(Wedding_STATUS.get("Wedding_"+uin) == 0){
        id = sv.bride;// ид невесты, если жених
        }else{
        id = sv.groom;// ид жениха, если невеста
        }
        Users u = srv.us.getUser(id);
        String cmd = "Wedding_"+uin;
        if(comMap.containsKey(cmd)){
        msg = mmsg.trim();
        msg = msg.toLowerCase();
        if(!TestMsgWedding(msg)){
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn, uss.localnick + " ответ должен быть ''да'' или ''нет'' " );
        return;
        }
        Wedding = true;
        comMap.remove(cmd);
        }
        if(!Wedding){
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn,uss.localnick + " Ты " + message0[Wedding_STATUS.get("Wedding_"+uin)] + " стать "
        + message1[Wedding_STATUS.get("Wedding_"+uin)] + " пользователя " + u.localnick + "\n" +
        " Ответ должен быть ''Да'' или ''Нет''");
        comMap.put(cmd, new CommandExtend(uin, cmd, cmd,v, 60000));
        return;
        }
        if(msg.equals("да")){
        srv.cq.addMsg(uss.localnick + " ты " + message0[Wedding_STATUS.get("Wedding_"+uin)] +
        " стать " +  message1[Wedding_STATUS.get("Wedding_"+uin)] + " пользователя " + u.localnick,
        uss.sn, psp.getIntProperty("wedding.room"));
        sv.answer += 1;
        sv.CountAnswer();
        WeddingInfo.put(Wedding_ID.get("Wedding_"+uin), sv);
        EndWedding(uin, Wedding_ID.get("Wedding_"+uin));
        }else{
        srv.cq.addMsg(uss.localnick + " ты " + message0[Wedding_STATUS.get("Wedding_"+uin)] +
        " стать " +  message1[Wedding_STATUS.get("Wedding_"+uin)] + " пользователя " + u.localnick,
        uss.sn, psp.getIntProperty("wedding.room"));
        sv.answer -= 1;
        sv.CountAnswer();
        WeddingInfo.put(Wedding_ID.get("Wedding_"+uin), sv);
        EndWedding(uin, Wedding_ID.get("Wedding_"+uin));
        }
        }
        
        /**
         * Если все успешно, то метод завершит процес свадьбы
         * @author fraer72
         * @param Wedding_id - ид свадьбы
         */

        private void EndWedding(String uin, int Wedding_id){
        // Очистим
        Wedding_ID.remove("Wedding_"+uin);
        Wedding_STATUS.remove("Wedding_"+uin);
        WeddingInfo sv = WeddingInfo.get(Wedding_id);
        if(sv.answer_cnt == 2){
        if(sv.answer == 2){// Если новобрачные согласны
        Users u = srv.us.getUser(sv.bride);//невеста
        Users us = srv.us.getUser(sv.groom);//жених
        srv.cq.addMsg("Пользователи " + u.localnick + " и " +
        us.localnick + " обвенчались. Бухаем*DRINK*", "", psp.getIntProperty("wedding.room"));
        u.wedding = sv.groom;
        us.wedding = sv.bride;
        srv.us.updateUser(u);
        srv.us.updateUser(us);
        WeddingInfo.remove(Wedding_id);
        }else{
        srv.cq.addMsg("Свадьба не состоялась", "", psp.getIntProperty("wedding.room"));
        WeddingInfo.remove(Wedding_id);
        }
        }else{
        return; // Если свадьба еще идет!
        }
        }

        /**
          * Проверка ответа
          * @param msg
          * @return
          */

        private boolean TestMsgWedding( String msg ){
        return ( msg.equals("да") || msg.equals("нет") );
        }

        /**
         * Клас для хранения информаии при свадьбе
         */

     class WeddingInfo {
        public int id = 0;
        public int bride = 0;
        public int groom = 0;
        public int count = 0;
        public int answer = 0;
        public int answer_cnt = 0;

        WeddingInfo(int id, int groom,  int bride, int answer, int answer_cnt) {
            this.id = id;
            this.bride = bride;
            this.groom = groom;
            this.answer = answer;
            this.answer_cnt = answer_cnt;
            count = 0;
        }

        public int Count() {return count++;}
        public int CountAnswer() {return answer_cnt++;}
    }

     /**
      * Развод
      * @author fraer72
      * @param proc
      * @param uin
      * @param v
      * @param mmsg
      */

  private void commandDivorce(IcqProtocol proc, String uin, Vector v, String mmsg) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
        if (!auth(proc, uin, "wedding"))return;
        int bride = (Integer)v.get(0);
        int groom  = (Integer)v.get(1);
        Users u = srv.us.getUser(uin); // Который набрад команду
        Users us = srv.us.getUser(bride);// Жена
        Users uss = srv.us.getUser(groom);// Муж
        if(u.room != psp.getIntProperty("wedding.room")){
        proc.mq.add(uin,"Проводить развод можно тока в " + psp.getIntProperty("wedding.room") + " комнате");
        return;
        }
        /*Тест жены*/
        if(us.id == 0){
        proc.mq.add(uin,"Пользователя с id=" + bride + " не существует");
        return;
        }
      /*if (us.state!=UserWork.STATE_CHAT){
      proc.mq.add(uin,"Пользователь " + us.localnick + " не в чате!");
      return;
      }*/
        if(us.room != psp.getIntProperty("wedding.room")){
        proc.mq.add(uin,"Жена должна присутствовать в данной комнате");
        return;
        }
        if(us.wedding == 0){
        proc.mq.add(uin,"Жена не находится в браке");
        return;
        }
        /*Тест мужа*/
        if(uss.id == 0){
        proc.mq.add(uin,"Пользователя с id=" + groom + " не существует");
        return;
        }
      /*if (uss.state!=UserWork.STATE_CHAT){
      proc.mq.add(uin,"Пользователь " + uss.localnick + " не в чате!");
      return;
      }*/
        if(uss.room != psp.getIntProperty("wedding.room")){
        proc.mq.add(uin,"Муж должен присутствовать в данной комнате");
        return;
        }
        if(uss.wedding == 0){
        proc.mq.add(uin,"Муж не находится в браке");
        return;
        }
        if(us.wedding != uss.id & uss.wedding != us.id){
        proc.mq.add(uin,"Данные пользователи не находятся в браке друг с другом.");
        return;
        }
        us.wedding = 0;
        uss.wedding = 0;
        srv.us.updateUser(us);
        srv.us.updateUser(uss);
        srv.cq.addMsg("Пользователи " + us.localnick + " и " +
        uss.localnick + " развелись!", "", psp.getIntProperty("wedding.room"));
        proc.mq.add(uin,"Успешно выполнено!");
  }

    /**
     * Метод задает итерактивные вопросы пользователю
     * @param proc
     * @param uin
     * @param mmsg
     */

  private void InteractiveQuestions(IcqProtocol proc, String uin, String mmsg, boolean commandReg){
        if(!About.containsKey(uin)){
        About.put(uin, new AboutExtend(uin, 5*60000, commandReg));
        }
        AboutExtend about = About.get(uin);
        switch (about.getOrder()){
        case 0:
        proc.mq.add(uin,"Введите ваше имя :)");
        nextQuestion(about);
        setAnswer(about, true);
        break;
        case 1:
        setName(proc, uin, mmsg, about);
        if(!about.getAnswer()){
        proc.mq.add(uin,"Отлично " + srv.us.getUser(uin).lname + ", сколько тебе лет?");
        setAnswer(about, true);
        return;
        }
        break;
        case 2:
        setAge(proc, uin, mmsg, about);
        if(!about.getAnswer()){
        proc.mq.add(uin,srv.us.getUser(uin).lname + " возраст успешно указан, какой у тебя пол? 'м' или 'ж' ?");
        setAnswer(about, true);
        return;
        }
        break;
        case 3:
        setSex(proc, uin, mmsg, about);
        if(!about.getAnswer()){
        proc.mq.add(uin,srv.us.getUser(uin).lname + " прекрасно, пол указан, а теперь введите город где вы живете");
        setAnswer(about, true);
        return;
        }
        break;
        case 4:
        setCity(proc, uin, mmsg, about);
        if(!commandReg)
        proc.mq.add(uin,srv.us.getUser(uin).lname + " информация успешно заполнена," +
        " просматреть ее ты можешь командой !личное <id>");
        else
        proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.14", new Object[] {srv.us.getUser(uin).lname, psp.getStringProperty("chat.name")}));
        About.remove(uin);
        break;
        default:
        }
  }

    /**
     * Сохраняем имя пользователя
     * @param proc
     * @param uin
     * @param mmsg
     * @param about
     */

  private void setName(IcqProtocol proc, String uin, String mmsg, AboutExtend about){
        Users uss = srv.us.getUser(uin);
        String msg = RepleceMsg(mmsg);
        if (InvalidCharacters(msg)){
        proc.mq.add(uin,"В имени запрещенный символы\nВведите правельно имя");
        return;
        }
        if (radm.testMat1(radm.changeChar(msg))){
        proc.mq.add(uin,"В имени " + msg + " ''мат''\nВведите правельно имя");
        return;
        }
        if(msg.length() > psp.getIntProperty("about.user.long")){
        msg = msg.substring(0,psp.getIntProperty("about.user.long"));
        proc.mq.add(uin,"Имя было обрезанно: " + msg);
        }
        uss.lname = msg;
        srv.us.updateUser(uss);
        setAnswer(about, false);
        nextQuestion(about);
  }

    /**
     * Сохраняем возраст
     * @param proc
     * @param uin
     * @param mmsg
     * @param about
     */

  private void setAge(IcqProtocol proc, String uin, String mmsg, AboutExtend about){
        Users uss = srv.us.getUser(uin);
        int age = 0;
        if(!MainProps.testInteger(mmsg)){
        proc.mq.add(uin,uss.lname + " введите ваш возраст");
        return;
        }
        age = Integer.parseInt(mmsg);
        if(age < psp.getIntProperty("about.age.min") || age > psp.getIntProperty("about.age.max")){
        proc.mq.add(uin,uss.lname + " неправельно указан возраст\n" +
        "Он дожен быть не меньше ''" + psp.getIntProperty("about.age.min") + "'' и не больше ''" + psp.getIntProperty("about.age.max") + "'' лет");
        return;
        }
        uss.age = age;
        srv.us.updateUser(uss);
        setAnswer(about, false);
        nextQuestion(about);
  }

    /**
     * Сохраняем пол
     * @param proc
     * @param uin
     * @param mmsg
     * @param about
     */

  private void setSex(IcqProtocol proc, String uin, String mmsg, AboutExtend about){
        Users uss = srv.us.getUser(uin);
        if (!testSex(mmsg)){
        proc.mq.add(uin,uss.lname + " пол должен быть ''ж'' или ''м''");
        return;
        }
        uss.homepage = mmsg;
        srv.us.updateUser(uss);
        setAnswer(about, false);
        nextQuestion(about);
  }

    /**
     * Сохраняем город
     * @param proc
     * @param uin
     * @param mmsg
     * @param about
     */

  private void setCity(IcqProtocol proc, String uin, String mmsg, AboutExtend about){
        Users uss = srv.us.getUser(uin);
        String msg = RepleceMsg(mmsg);
        if (radm.testMat1(radm.changeChar(msg))){
        proc.mq.add(uin,uss.lname + "в городе " + msg + " ''мат''\nУкажите свой город правельно");
        return;
        }
        if(msg.length()>psp.getIntProperty("about.user.long")){
        msg = msg.substring(0,psp.getIntProperty("about.user.long"));
        proc.mq.add(uin,uss.lname + " длинное название города, обрезанно: " + msg);
        }
        uss.city = msg;
        srv.us.updateUser(uss);
        setAnswer(about, false);
        nextQuestion(about);
  }

    /**
     * Проверка на запрещенный символы
     * @param msg
     * @return
     */


  private boolean InvalidCharacters(String msg){
        msg = msg.toLowerCase();
        String H  = psp.getStringProperty("about.user.bad");
        for(int i = 0;i < H.length();i++){
        if(msg.indexOf(H.charAt(i))>=0) return true;
        }
        return false;
  }

     /**
      * Проверка пола
      * @param msg
      * @return
      */

  private boolean testSex(String msg){
        String Floor = msg.toLowerCase();//опустим регистр
        return Floor.equals("м") || Floor.equals("ж");//проверим
  }

     /**
      * Стираем ненужные символы в целях анти-рекламы
      * @param msg
      * @return
      */

  private String RepleceMsg(String msg){
        msg = msg.replace('\n',' ');
        msg = msg.replace('\r',' ');
        for(int i = 0;i < 9;i++){
        msg = msg.replace(Integer.toString(i),"");
        }
        return msg;
  }

     /**
      * Регулируем подачу вопроса
      * @param about
      * @param answer
      */

  private void setAnswer(AboutExtend about, boolean answer){
        about.setAnswer(answer);
        About.put(about.getUin(), about);
  }

     /**
      * На следующий вопрос
      * @param about
      */

  private void nextQuestion(AboutExtend about){
        about.setOrder();
        About.put(about.getUin(), about);
  }

  /**
   * Передать балы
   * @author Юрий
   * @param proc
   * @param uin
   * @param v
   */
   private void PresendBall(IcqProtocol proc, String uin, Vector v){
   if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
   try{
   int s = (Integer)v.get(0);
   int t = (Integer)v.get(1);
   Users us = srv.us.getUser(s);
   Users uss = srv.us.getUser(uin);
   if(us.id==0){
   proc.mq.add(uin,"Пользователь не найден");
   return;
   }
   if(uss.ball<t){
   proc.mq.add(uin,"Вы не имеете такого количества баллов");
   return;
   }
   if(t<0){
   proc.mq.add(uin,"Вы не имеете такого количества баллов");
   return;
   }
   int uroven = us.ball+t;
   us.ball=uroven;
   srv.us.updateUser(us);
   int uroven2 = uss.ball-t;
   uss.ball=uroven2;
   srv.us.updateUser(uss);
   proc.mq.add(uin,"Вы отдали " + t + " баллов пользователю " + us.localnick + "|" + us.id + "|");
   srv.getIcqProcess(us.basesn).mq.add(us.sn,"Вам отдал " + t + " баллов пользователь " + uss.localnick + "|" + uss.id + "|");
   } catch (Exception ex) {
   Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
   }
   }

   /**
    * Смена статуса другому пользователю
    * @author fraer72
    * @param proc
    * @param uin
    * @param v
    */

   private void ChengeUserStatus(IcqProtocol proc, String uin, Vector v){
   if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
   if(!auth(proc,uin, "chstatus")) return;
   try{
   int i = (Integer)v.get(0);
   String lstatus = (String)v.get(1);
   int len = lstatus.length();
   Users uss = srv.us.getUser(i);
   Users u = srv.us.getUser(uin);
   if(i==0){
   proc.mq.add(uin,"Пользователь не найден");
   return;
   }
   if(lstatus.equals("")){
   uss.status  = "";
   srv.us.updateUser(uss);
   srv.cq.addMsg("У пользователя " + uss.localnick + "|" + uss.id + "|" + " убран статус, убрал пользователь "+u.localnick, uss.sn, uss.room);
   srv.getIcqProcess(uss.basesn).mq.add(uss.sn ,"У вас изменен статус на |" + lstatus + "|");
   proc.mq.add(uin,"Статус у пользователя " + uss.localnick + " успешно убран");
   return;
   }
   if (!(len>psp.getIntProperty("about.user.st"))){
   uss.status  = lstatus;
   srv.us.updateUser(uss);
   srv.cq.addMsg("У пользователя " + uss.localnick + "|" + uss.id + "|" + " изменен статус на |" + lstatus + "| изменил пользователь "+u.localnick, uss.sn, uss.room);
   srv.getIcqProcess(uss.basesn).mq.add(uss.sn ,"У вас изменен статус на |" + lstatus + "|");
   proc.mq.add(uin,"Статус у пользователя " + uss.localnick + " успешно убран");
   } else proc.mq.add(uin,"Слишком длинный статус (> " + psp.getIntProperty("about.user.st") + " ). Статус не изменён.");
   } catch (Exception ex) {
   Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
   }
   }

     /**
      * @author Юрий
      * Изменить ид пользователю
      * !измид <id> <id>
      * @param proc
      * @param uin
      * @param v
      * @param mmsg
      */
       private void commandchid(IcqProtocol proc, String uin, Vector v, String mmsg){
         if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
         if(!auth(proc,uin, "chid")) return;
       try{
          int i = (Integer)v.get(0);
          int newid = (Integer)v.get(1);
          Users u = srv.us.getUser(i);
          Users unew = srv.us.getUser(newid);
          int del = i;
          int clansmen = u.clansman;
          String clangroup = u.clangroup;
          Users delete = srv.us.getUser(del);
          if(u.id==0){
          proc.mq.add(uin,"Пользователь не найден!");
          return;
          }
          if(newid==0){
          proc.mq.add(uin,"Нельзя 0 ид");
          return;
          }
          if (srv.us.testUser(unew.sn)){
          proc.mq.add(uin,"Пользователь с ID " + newid + " уже существует! Попробуй другой ID");
          return;
          }
          srv.us.changeInfo(del, newid);
          u.id=newid;
          srv.us.updateUser(u);
          srv.us.deleteUser(delete.sn);
          PreparedStatement pst = srv.us.db.getDb().prepareStatement("update users set id=? where sn="+u.sn);
          pst.setInt(1,newid);
          pst.execute();
          pst.close();
          srv.us.changeClan(newid, clansmen, clangroup);
          boolean kk = srv.us.setUserPropsValue(u.id, "group", "user") &&
                  srv.us.setUserPropsValue(u.id, "grant", "") &&
                  srv.us.setUserPropsValue(u.id, "revoke", "");
                  srv.us.clearCashAuth(u.id);
          proc.mq.add(uin,"ID изменен");
       } catch (Exception ex){
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
           }

       /**
        * @author mmaximm
        * Спрятатся
        * @param proc
        * @param uin
        */

       private void commandOnInvise(IcqProtocol proc, String uin){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if(!auth(proc,uin, "invise")) return;
       try{
       Users u = srv.us.getUser(uin);
       srv.us.grantUser(u.id, "invisible");
       proc.mq.add(uin,"Вы спрятались");
       }catch (Exception ex){
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * @author mmaximm
        * Показаться
        * @param proc
        * @param uin
        */

       private void commandOffInvise(IcqProtocol proc, String uin){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if(!auth(proc,uin, "invise")) return;
       try{
       Users u = srv.us.getUser(uin);
       srv.us.revokeUser(u.id, "invisible");
       proc.mq.add(uin,"Вы показались");
       }catch (Exception ex){
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }
       
       /**
        * @author Юрий
        * Список скрытых пользователей
        * @param proc
        * @param uin
        */

       private void commandListInvise(IcqProtocol proc, String uin) {
       if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "listinvise")) return;
       try {
       String lst = "";
       Enumeration<String> e = srv.cq.uq.keys();
       while (e.hasMoreElements()) {
       String i = e.nextElement();
       Users us = srv.us.getUser(i);
       if ((srv.us.authorityCheck(us.id, "invisible")) && (us.state == UserWork.STATE_CHAT)) lst = lst + us.id + " ~ " + us.localnick + " ~ |" + us.room + "|" + "\n";
       }
       proc.mq.add(uin, "Скрываются:\nИд~Ник~Комната\n" + lst);
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Дать пользователю свой уин
        * @param proc
        * @param uin
        */

       private void commandGetUinUser(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       try{
       int id = (Integer)v.get(0);
       Users u = srv.us.getUser(id);
       Users uss = srv.us.getUser(uin);
       if(u.id == 0){
       proc.mq.add(uin,"Пользователь не найден!");
       return;
       }
       if(u.state != UserWork.STATE_CHAT){
       proc.mq.add(uin,"Пользователь не в чате!");
       return;
       }
       proc.mq.add(uin,"Uin отправлен");
       srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + uss.localnick + " дал вам свой уин, - " + uss.sn);
       }catch (Exception ex){
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }


       /**
         * Полная (личная) информация о пользователе
         * @param proc
         * @param uin
         * @param v
         */

       private void commandLichUser(IcqProtocol proc, String uin, Vector v) {
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       try{
       int id = (Integer)v.get(0);
       Users u = srv.us.getUser(id);
       String List = "Личная информация о пользователе |" + u.localnick + "|" + '\n';
       List += "------\n";
       List += "Дата регистрации - " + (u.data==0 ? "Не указанна" : (("|" + new Date(u.data)).toString() + "|")) + '\n';
       List += "Группа - " + u.group + '\n';
       List += "Рейтинг - " + u.ball + '\n';
       List += "Предупреждений - " + u.notice + ", лимит - " + psp.getIntProperty("notice.limit") + '\n';
       List += "Имя - " + (u.lname.equals("") || u.lname == null ? "Скрыто" : (u.lname)) + '\n';
       List += "Пол - " + (u.homepage.equals("") || u.homepage == null ? "Скрыто" : (u.homepage)) + '\n';
       List += "Возраст - " + (u.age == 0 ? "Скрыто" : (u.age)) + '\n';
       List += "Город - " + (u.city.equals("") || u.city == null ? "Скрыто" : (u.city)) + '\n';
       if(psp.getBooleanProperty("social.status.on.off"))
       List += "Социальный статус - " + srv.us.getStatus(id) + '\n';
       if(psp.getBooleanProperty("shop2.on.off")){
       List += "Дом - " + (u.home.equals("") || u.home == null ? "Бомж" : (u.home)) + '\n';
       List += "Машина - " + (u.car.equals("") || u.car == null ? "Пешеход" : (u.car)) + '\n';
       List += "Одежда - " + (u.clothing.equals("") || u.clothing == null ? "Голый" : (u.clothing)) + '\n';
       List += "Животное - " + (u.animal.equals("") || u.animal == null ? "Нету" : (u.animal)) + '\n';
       }
       String clan_symbol = srv.us.getClan(u.clansman).getSymbol().equals("") || !psp.getBooleanProperty("Clan.Symbol") ? "" : "(" + srv.us.getClan(u.clansman).getSymbol() + ")";
       if( u.clansman != 0 )      
       List += (  u.id != srv.us.getClan(u.clansman).getLeader() ? "Состоит в клане - ''" + srv.us.getClan( u.clansman ).getName() + " " + clan_symbol + "''" : ( "Лидер клана - ''" + srv.us.getClan(u.clansman).getName() + " " + clan_symbol + "''" ) ) + '\n';
       else List += "В клане не состоит" + '\n';
       List += "Правильных ответов в викторине - " + u.answer + '\n';
       List += (u.wedding == 0 ? "В браке не состоит" : ("В браке с |" + srv.us.getUser(u.wedding).id + "|" + srv.us.getUser(u.wedding).localnick))  + '\n';
       List += "------\n";
       List += gift.commandListGiftUser_5(id);
       List += frends.Random_Frends(id);
       proc.mq.add(uin,List);
       }catch (Exception ex){
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Отправить сообщение пользователю
        * @param proc
        * @param uin
        * @param v
        */

       private void getUserMessages(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "usermessages")) return;
       try{
       String user = (String)v.get(0);
       String messages = (String)v.get(1);
       Integer id = 0;
       if(user.length() >= 6){
       proc.mq.add(user,"Вам сообщение от администратора чата: " + messages);
       }else{
       if(!MainProps.testInteger(user)){
       proc.mq.add(uin,"Ошибка выполнения команды.");
       return;
       }
       id = Integer.parseInt(user);
       Users u = srv.us.getUser(id);
       srv.getIcqProcess(u.basesn).mq.add(u.sn, "Вам сообщение от администратора чата: " + messages);
       }
       proc.mq.add(uin,"Сообщение отправлено.");
       }catch (Exception ex){
       proc.mq.add(uin,"При отправке сообщения возникла ошибка - "+ex.getMessage());
       }
       }

        /**
         * Затащить в чат
         * @param proc
         * @param uin
         * @param v
         * @author mmaximm
         */
       private void commandDragSomewhere(IcqProtocol proc, String uin, Vector v, String mmsg) {
       if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "uchat")) return;
       try {
       int id = (Integer) v.get(0);
       if (id == 0) {
       proc.mq.add(uin, "Нет такого пользователя");
       return;
       }
       Users us = srv.us.getUser(id);
       if (us.id == 0) {
       proc.mq.add(uin, "Пользователь не найден");
       return;
       }
       if (us.state == UserWork.STATE_CHAT) {
       proc.mq.add(uin, "Пользователь уже в чате");
       return;
       }
       if (us.state == UserWork.STATE_BANNED) {
       proc.mq.add(uin, "Нельзя затащить забаненого пользователя");
       return;
       }
       goChat_Usual(srv.getIcqProcess(us.basesn), us.sn);
       if (floodMap.containsKey(us.sn)) {
       FloodElement e = floodMap.get(us.sn);
       e.addMsg("!chat");
       floodMap.put(us.sn, e);
       } else {
       FloodElement e = new FloodElement(psp.getIntProperty("chat.floodTimeLimit") * 1000);
       e.addMsg("!chat");
       floodMap.put(us.sn, e);
       }
       srv.getIcqProcess(us.basesn).mq.add(us.sn, "Вас затащили в чат!");
       proc.mq.add(uin, "Пользователь " + us.localnick + "|" + us.id + "| затащен в чат");
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

        /**
         * Отправить сообщение место бота
         * @param proc
         * @param uin
         * @param v
         */
       private void commandBotMessages(IcqProtocol proc, String uin, Vector v) {
       if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "bot_messages")) return;
       try {
       String msg = (String) v.get(0);
       Users uss = srv.us.getUser(uin);
       srv.cq.addMsg(radm.NICK + " " + psp.getStringProperty("chat.delimiter") + " " + msg, "", uss.room);
       proc.mq.add(uin, "Собщение отправлено");
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Русская рулетка
        * @param proc
        * @param uin
        * @param v
        * @param mmsg
        */

       private void commandRussianRoulette(IcqProtocol proc, String uin, Vector v, String mmsg) {
       if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
       try {
       int s = (Integer) v.get(0);
       int kick = radm.getRND(psp.getIntProperty("russian.roulette.kick.time"));
       int random_number = radm.getRND(4);
       Users uss = srv.us.getUser(uin);
       if (uss.room != psp.getIntProperty("russian.roulette.room")){
       proc.mq.add(uin, "Играть можно только в " + psp.getIntProperty("russian.roulette.room") + " комнате");
       return;
       }
       if (s > 3 || s < 1) {
       proc.mq.add(uin, "Русская рулетка: Число должно быть от 1 до 3...");
       return;
       }
       if(random_number == 0){
       random_number = 1;
       }
       if (s == random_number) {
       srv.cq.addMsg("Русская рулетка: числа совпали! " + uss.localnick + "|" + uss.id + "|" + " выпнут из чата на " + kick + " минут", "", uss.room);
       proc.mq.add(uin, "Русская рулетка: я загадала число: (" + random_number + ") , а у тебя выпало: (" + s + ") , числа совпали! ты неудачник!");
       tkick(proc, uin, kick, 0, "Проиграл в Русскую Рулетку");
       } else {
       proc.mq.add(uin, "Русская рулетка: а ты везунчик ;-)!");
       srv.cq.addMsg("Русская рулетка: числа не совпали!\nПользователь " + uss.localnick + "|" + uss.id + "|" + " везунчик ;-)" +
       " выигрывает " + psp.getIntProperty("russian.roulette.ball") + " бал(ов)", "", uss.room);
       uss.ball += psp.getIntProperty("russian.roulette.ball");
       srv.us.updateUser(uss);
       }
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Зарегистрировать пользователя
        * @param proc
        * @param uin
        * @param v
        */

       private void RegUser(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "reg_user")) return;
       try{
       String user = (String)v.get(0);
       String nick = (String)v.get(1);
       Users uss = srv.us.getUser(user);
       if(uss.id!=0){
       proc.mq.add(uin, "Юзер уже зареган в чате: "+uss.localnick+ "|" + uss.id + "|");
       return;
       }
       uss.state=UserWork.STATE_NO_CHAT;
       uss.basesn = proc.baseUin;
       uss.localnick = nick;
       uss.sn = user;
       int id = srv.us.addUser(uss);
       Log.getLogger(srv.getName()).talk(uin + " - Юзера насильно зарегали: " + user);
       srv.us.db.log(id,user,"REG",nick,uss.room);
       srv.us.db.event(id, user, "REG", 0, "", nick);
       proc.mq.add(user, "Теперь Вы пользователь нашего чата. Вход по команде !чат");
       proc.mq.add(uin, "Вы успешно зарегистрировали юзера");
       srv.cq.addMsg("Зарегистрировали пользователя " + uss.localnick + "|" + uss.id + "|", "", 0);
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Игра казино
        * @author HellFaust
        * @param proc
        * @param uin
        */

       private void commandCasino(IcqProtocol proc, String uin){
       if(!isChat(proc,uin)) return;
       try{
       Users uss = srv.us.getUser(uin);
       if(uss.room != psp.getIntProperty("casino.room")){
       proc.mq.add(uin,"Играть в казино можно только в " + psp.getIntProperty("casino.room") + " комнате");
       return;
       }
       if (uss.ball < psp.getIntProperty("casino.price")){
       proc.mq.add(uin,"Недостаточно баллов для игры. Необходимо " + psp.getIntProperty("casino.price") + ", а у Вас имеется " + uss.ball);
       return;
       }
       int number1 = (int) ((Math.random() * psp.getIntProperty("casino.amount")));
       int number2 = (int) ((Math.random() * psp.getIntProperty("casino.amount")));
       int number3 = (int) ((Math.random() * psp.getIntProperty("casino.amount")));
       int payment = uss.ball - psp.getIntProperty("casino.price");
       srv.cq.addMsg(uss.localnick + " потянул рычаг... Табло вращается... Вращается... Вращается... И выпадает комбинация -=" + number1 + "=" + number2 + "=" +  number3 + "=-", uss.sn, uss.room);
       proc.mq.add(uin,"Вы потянули рычаг... И выпадает комбинация -=" + number1 + "=" + number2 + "=" +  number3 + "=-");
       if(number1 != number2 && number1 != number3 && number2 != number3){
       uss.ball = payment;
       srv.cq.addMsg("Ни одного совподения... " + uss.localnick + " - неудачник!!!", uss.sn, uss.room);
       proc.mq.add(uin,"Ни одного совподения... Возможно в следующий раз повезет. За игру у Вас снято " + psp.getIntProperty("casino.price") + " балла(ов)");
       }
       if(number1 == number2 && number3 != number2){
       int win2 = payment + psp.getIntProperty("casino.win2");
       uss.ball = win2;
       srv.cq.addMsg("Два числа совпали... И " + uss.localnick + " выигрывает " + psp.getIntProperty("casino.win2") + " балла(ов)", uss.sn, uss.room);
       proc.mq.add(uin,"Два числа совпали... Вы выиграли " + psp.getIntProperty("casino.win2") + " балла(ов). За игру у Вас снято " +  psp.getIntProperty("casino.price") + " балла(ов)");
       }
       if(number2 == number3 && number1 != number2){
       int win2 = payment + psp.getIntProperty("casino.win2");
       uss.ball = win2;
       srv.cq.addMsg("Два числа совпали... И " + uss.localnick + " выигрывает " + psp.getIntProperty("casino.win2") + " балла(ов)", uss.sn, uss.room);
       proc.mq.add(uin,"Два числа совпали... Вы выиграли " + psp.getIntProperty("casino.win2") + " балла(ов). За игру у Вас снято " +  psp.getIntProperty("casino.price") + " балла(ов)");
       }
       if(number1 == number3 && number1 != number2){
       int win2 = payment + psp.getIntProperty("casino.win2");
       uss.ball = win2;
       srv.cq.addMsg("Два числа совпали... И " + uss.localnick + " выигрывает " + psp.getIntProperty("casino.win2") + " балла(ов)", uss.sn, uss.room);
       proc.mq.add(uin,"Два числа совпали... Вы выиграли " + psp.getIntProperty("casino.win2") + " балла(ов). За игру у Вас снято " +  psp.getIntProperty("casino.price") + " балла(ов)");
       }
       if(number1 == number2 && number3 == number2){
       int win3 = payment + psp.getIntProperty("casino.win3");
       uss.ball = win3;
       srv.cq.addMsg("Все 3 числа совпали... И " + uss.localnick + " выигрывает " + psp.getIntProperty("casino.win3") + " балла(ов)... Какой везунчик!!!", uss.sn, uss.room);
       proc.mq.add(uin,"Все 3 числа совпали... Вы выиграли " + psp.getIntProperty("casino.win3") + " балла(ов). За игру у Вас снято " + psp.getIntProperty("casino.price") + " балла(ов)");
       }
       uss.basesn = proc.baseUin;
       srv.us.updateUser(uss);
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Добавление рекламы
        * @param proc
        * @param uin
        * @param v
        */

       private void setAdvertisement(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "advertisement_work")) return;
       try{
       String text = (String)v.get(0);
       if(text.equals("")){
       proc.mq.add(uin,"Пустой текст рекламы");
       return;
       }
       srv.us.db.setAdvertisement((int)srv.us.db.getLastIndex("advertisement"), text);
       proc.mq.add(uin,"Реклама успешно добавлена");
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }
       
       /**
        * Удаление рекламы
        * @param proc
        * @param uin
        * @param v
        */

       private void delAdvertisement(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "advertisement_work")) return;
       try{
       int id = (Integer)v.get(0);
       if(srv.us.db.isAdvertisement(id) == 0){
       proc.mq.add(uin,"Рекламы под id`ом " + id + " не существует!");
       return;
       }
       srv.us.db.delAdvertisement(id);
       proc.mq.add(uin,"Реклама успешно удалена");
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }
       
       /**
        * Листинг рекламы
        * @param proc
        * @param uin
        */

       private void listAdvertisement(IcqProtocol proc, String uin){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "advertisement_work")) return;
       try{
       proc.mq.add(uin,"Листинг рекламы:\n" + srv.us.db.listAdvertisement());
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }
       
       /**
        * Дать балы пользователю
        * @param proc
        * @param uin
        * @param v
        */

      private void getBall(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "ballwork")) return;
       try{
       int id = (Integer)v.get(0);
       int ball = (Integer)v.get(1);
       Users u = srv.us.getUser(id);
       if(u.id == 0){
       proc.mq.add(uin,"Такого пользователя не существует");
       return;
       }
       u.ball += ball;
       srv.us.updateUser(u);
       proc.mq.add(uin,"Команда успешно выполнена");
       srv.getIcqProcess(u.basesn).mq.add(u.sn, "Вам перевели " + ball + " бал(ов)");
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Забрать балы у пользователя
        * @param proc
        * @param uin
        * @param v
        */

      private void nailBall(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "ballwork")) return;
       try{
       int id = (Integer)v.get(0);
       int ball = (Integer)v.get(1);
       Users u = srv.us.getUser(id);
       if(u.id == 0){
       proc.mq.add(uin,"Такого пользователя не существует");
       return;
       }
       if(u.ball <= 0){
       proc.mq.add(uin,"У пользователя и так нет балов");
       return;
       }
       u.ball -= ball;
       srv.us.updateUser(u);
       proc.mq.add(uin,"Команда успешно выполнена");
       srv.getIcqProcess(u.basesn).mq.add(u.sn, "У вас забрали " + ball + " бал(ов)");
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

      /**
       * Зарплата для администрации
       * @param proc
       * @param uin
       * @author Ar2r
       */

       private void commandSalary (IcqProtocol proc, String uin) {
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if(!psp.getBooleanProperty("salary.on.off")){
       proc.mq.add(uin,"Эта команда закрыта администрацией чата");
       return;
       }
       try {
       Users uss = srv.us.getUser(uin);
       if(srv.us.getSalary(uss.id)>=1) {
       proc.mq.add(uin,"Зарплату можно получить только раз в сутки");
       return;
       }
       if(uss.group.equals("moder") ) {
       uss.ball += psp.getIntProperty("salary.moder");
       srv.us.updateUser(uss);
       proc.mq.add(uin,"Возьмите чек и конвертик с вашей зарплатой в размере " + psp.getIntProperty("salary.moder") + " балов.\nПриходите завтра...");
       srv.us.db.event(uss.id, uin, "Salary", uss.id, uss.sn, "Зарплата полученна");
       } else if(uss.group.equals("admin")) {
       uss.ball += psp.getIntProperty("salary.admin");
       srv.us.updateUser(uss);
       proc.mq.add(uin,"Возьмите чек и конвертик с вашей зарплатой в размере " + psp.getIntProperty("salary.admin") + " балов.\nПриходите завтра...");
       srv.us.db.event(uss.id, uin, "Salary", uss.id, uss.sn, "Зарплата полученна");
       } else {
       proc.mq.add(uin, "Вам зарплата не положенна...");
       }
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }
       
       /**
        * Выписать предупреждение пользователю
        * @param proc
        * @param uin
        * @param v
        * @author fraer72
        */

       private void commandNotice(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "notice_work")) return;
       try{
       int id = (Integer)v.get(0);
       String notice = (String)v.get(1);
       Users uss = srv.us.getUser(uin);
       Users u = srv.us.getUser(id);
       if(u.id == 0) {
       proc.mq.add(uin,"Пользователя не существует!");
       return;
       }
       if(u.state != UserWork.STATE_CHAT) {
       proc.mq.add(uin,"Пользователя не в чате!");
       return;
       }
       if(psp.testAdmin(u.sn)) {
       proc.mq.add(uin,"Пользователь системный администратор, вы не можите выписать ему предупреждение!");
       return;
       }
       if(notice.equals("") || notice.equals(" ")) {
       proc.mq.add(uin,"Необходимо указать причину предупреждения!");
       return;
       }
       u.notice++;
       srv.us.updateUser(u);
       srv.us.db.setNotice((int)srv.us.db.getLastIndex("notice"), id, uss.id, notice);
       srv.us.db.event(u.id, uin, "Notices", 0, u.sn, "Выписано предупреждение, за - " + notice + ", модератором - |" + uss.localnick + "|");
       srv.us.db.log(u.id, uin, "Notices", "Выписано предупреждение, за - " + notice + ", модератором - |" + uss.localnick + "|", u.room);
       srv.getIcqProcess(u.basesn).mq.add(u.sn, "Вам выписано предупреждение! Причина - " + notice + (psp.getBooleanProperty("chat.isShowKickReason") ? (" Модератором - |" + uss.localnick + "|") : "") + 
       (u.notice >= psp.getIntProperty("notice.limit") ? ("") : "\nЗа " + psp.getIntProperty("notice.limit") + " предупреждения(ий) вы будите удалены из чата на " + psp.getIntProperty("notice.kick") + " минут"));
       proc.mq.add(uin,"Вы выписали предупреждение пользователю - " + u.localnick);
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Тест предупреждений
        * @param uin
        * @return
        * @author fraer72
        */

       private boolean testNotices(String uin, IcqProtocol proc){
       Users uss = srv.us.getUser(uin);
       if(uss.notice >= psp.getIntProperty("notice.limit")){
       uss.notice = 0;
       srv.us.updateUser(uss);
       tkick(proc, uin, psp.getIntProperty("notice.kick"), 0, "Лимит предупреждений.");
           return false;
       }
          return true;
       }

       /**
        * Листинг предупреждений пользователя
        * @param proc
        * @param uin
        * @param v
        * @author fraer72
        */

       private void commandListNotice(IcqProtocol proc, String uin, Vector v){
       if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
       if (!auth(proc, uin, "notice_work")) return;
       try{
       int id = (Integer)v.get(0);
       Users u = srv.us.getUser(id);
       if(u.id == 0) {
       proc.mq.add(uin,"Пользователя не существует!");
       return;
       }
       proc.mq.add(uin,srv.us.noticesUser(u.id));
       } catch (Exception ex) {
       Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
       }
       }

       /**
        * Игра "число"
        * @param proc
        * @param uin
        * @param v
        * @param mmsg
        */
        private void commandGameNumber(IcqProtocol proc, String uin, Vector v){
        if (!isChat(proc, uin)) return;
        try{
        int rate = (Integer)v.get(0);
        int s = (Integer)v.get(1);
        int number = radm.getRND(psp.getIntProperty("number.amount"));
        Users uss = srv.us.getUser(uin);
        if (uss.room != psp.getIntProperty("number.room")){
        proc.mq.add(uin, "Играть можно только в " + psp.getIntProperty("number.room") + " комнате");
        return;
        }
        if(uss.ball <= 0){
        proc.mq.add(uin,"Тебе нечего ставить, у тебя нет баллов");
        return;
        }
        if(s > psp.getIntProperty("number.amount") || s < 0){
        proc.mq.add(uin,"Число должно быть от 0 до " + psp.getIntProperty("number.amount"));
        return;
        }
        if(rate>psp.getIntProperty("number.max.rate") || rate<=0){
        proc.mq.add(uin,"Ставка не может быть больше " + psp.getIntProperty("number.max.rate") + " и меньше 1 =)");
        return;
        }
        if (s == number) {
        uss.ball += rate * psp.getIntProperty("number.increase");
        srv.us.updateUser(uss);
        proc.mq.add(uin,"Ты выиграл и твоя ставка увеличилась в" + psp.getIntProperty("number.increase") + " раза и получил выигрыш " + rate * psp.getIntProperty("number.increase") + " баллов, а выпало то " + number);
        srv.cq.addMsg("Пользователь" + uss.localnick + "|" + uss.id + "| выиграл " + rate * psp.getIntProperty("number.increase") + " баллов... а выпало то " + number, uss.sn, uss.room);
        } else {
        uss.ball -= rate;
        srv.us.updateUser(uss);
        proc.mq.add(uin,"Ты проиграл. Ты ставил " + rate + " баллов. И значит твой проигрыш " + rate + " баллов... а выпало то " + number);
        srv.cq.addMsg("Пользователь" + uss.localnick + "|" + uss.id + "| проиграл " + rate + " баллов... а выпало то " + number, uss.sn, uss.room);
        }
        } catch (Exception ex) {
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }

        /**
         * Назначить личную комнату
         * @param proc
         * @param uin
         * @param v
         */


        private void commandAppointPrivateRoom(IcqProtocol proc, String uin, Vector v){
        if (!isChat(proc, uin)) return;
        if (!auth(proc, uin, "personal_room")) return;
        try{
        int id = (Integer)v.get(0);
        int room = (Integer)v.get(1);
        Users u = srv.us.getUser(id);
        if(u.id == 0) {
        proc.mq.add(uin,"Пользователя не существует!");
        return;
        }
        if(room == 0){
        proc.mq.add(uin,"Комната под номером - " + room + " не может быть личной!");
        return;
        }
        if(srv.us.checkRoom(room)){
        proc.mq.add(uin,"Комната под номером - " + room + " уже создана!");
        return;
        }
        if(srv.us.getRoom(room).getPersonal() != 0){
        proc.mq.add(uin,"Комната под номером - " + room + " уже является личной комнотой пользователя: " +
        srv.us.getUser(srv.us.getRoom(room).getPersonal()));
        return;
        }
        Rooms r = srv.us.getRoom(room);
        r.setName("Комната пользователя: |" + u.id + "|" + u.localnick);
        r.setTopic("Твоя личная комната :)");
        r.setPersonal(room);
        srv.us.createRoom(r);
        u.personal_room = room;
        srv.us.updateUser(u);
        proc.mq.add(uin,"Команда успешно выполнена");
        if(u.state == UserWork.STATE_CHAT)
        srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " вам назначена личная комната - " + room);
        } catch (Exception ex) {
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }
        
        /**
         * Убрать личную комнату
         * @param proc
         * @param uin
         * @param v
         */

        private void commandCleanPrivateRoom(IcqProtocol proc, String uin, Vector v){
        if (!isChat(proc, uin)) return;
        if (!auth(proc, uin, "personal_room")) return;
        try{
        int id = (Integer)v.get(0);
        Users u = srv.us.getUser(id);
        if(u.id == 0) {
        proc.mq.add(uin,"Пользователя не существует!");
        return;
        }
        if(u.personal_room == 0){
        proc.mq.add(uin,"У пользователя нет личной комнаты!");
        return;
        }
        Rooms r = srv.us.getRoom(u.personal_room);
        srv.us.deleteRoom(r);
        u.personal_room = 0;
        srv.us.updateUser(u);
        proc.mq.add(uin,"Команда успешно выполнена");
        if(u.state == UserWork.STATE_CHAT)
        srv.getIcqProcess(u.basesn).mq.add(u.sn, " у вас убрана личная комната!");
        } catch (Exception ex) {
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }
        
        /**
         * Пригласить в свою личную комнату
         * @param proc
         * @param uin
         * @param v
         */

        private void commandInvitationPrivateRoom(IcqProtocol proc, String uin, Vector v){
        if (!isChat(proc, uin)) return;
        try{
        int id = (Integer)v.get(0);
        Users u = srv.us.getUser(id);
        Users uss = srv.us.getUser(uin);
        if(u.id == 0) {
        proc.mq.add(uin,"Пользователя не существует!");
        return;
        }
        if(u.state == UserWork.STATE_NO_CHAT) {
        proc.mq.add(uin,"Пользователь не в чате!");
        return;
        }
        if(uss.personal_room == 0){
        proc.mq.add(uin,"У вас нет личной комнаты!");
        return;
        }
        if(u.id == uss.id){
        proc.mq.add(uin,"Вы не можете пригласть себя!");
        return;
        }
        addPrivateRoom(u.sn, uss.personal_room);
        proc.mq.add(uin,"Команда успешно выполнена");
        } catch (Exception ex) {
        Log.getLogger(srv.getName()).error("Ошибка - " + ex.getMessage());
        }
        }

        /**
         * Запоминаем приглашенного
         * @param uin
         * @param room
         */


        private void addPrivateRoom(String uin, int room){
        if(!pr.containsKey(uin)){
        pr.put(uin, room);
        srv.getIcqProcess(srv.us.getUser(uin).basesn).mq.add(srv.us.getUser(uin).sn,
        srv.us.getUser(uin).localnick + " вас пргласили в комнату " + room);
        }
        }

        /**
         * Проверка приглашен пользователь в комнату или нет?
         * @param uin
         * @param room
         * @return
         */

        private boolean testPrivateRoom(String uin, int room){
        // если uin хозяин комнаты
        if(srv.us.getUser(uin).personal_room == room) return true;  
        if(!pr.containsKey(uin)) return false;
        if(pr.get(uin) == room)
            return true;
        else
            return false;
        }

  }
