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

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.Manager;
import ru.jimbot.Messages;
import ru.jimbot.modules.AbstractCommandProcessor;
import ru.jimbot.modules.AbstractServer;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandExtend;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.modules.FloodElement;
import ru.jimbot.modules.NewExtend;
import ru.jimbot.modules.WorkScript;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 * ��������� ������ ����
 * 
 * @author Prolubnikov Dmitry
 */
public class ChatCommandProc extends AbstractCommandProcessor {
    public IcqProtocol icq;
    private ScriptWork scr;
    public frends frends;
    public AboutUser abv;
    public ClanCommand clan;
    private Shop shop;
    public Gift gift;
    public ChatServer srv;
    public RobAdmin radm = null;
    public Voting voting_events = null;
    public MyQuiz Quiz = null;
    private ConcurrentHashMap <String,String> up; // ���������� ��������� ��������� ������
    private ConcurrentHashMap <String, KickInfo> statKick; // ����������� ���������� �����
    private ConcurrentHashMap <String, ModInfo> statMod; // ����������� ���������� �������� �����������
    private HashSet<String> warnFlag; // ���� �������������� � ��������
    public int state=0;
    private CommandParser parser;
    public ChatProps psp;
    private boolean firstStartMsg = false;
    private boolean firstStartScript = false;
    // �������� ��� �������� �����: ���������� ���������, ���������� ���������
    private ConcurrentHashMap <String, FloodElement> floodMap, floodMap2, floodNoReg;
    private HashMap<String, CommandExtend> comMap;
    private HashMap<String, NewExtend> comNew;
    // ��� �������� ��������� �������� �����������
    private HashMap<String, String> authObj = new HashMap<String, String>();
    // ��� �������� ��������� ������
    private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
    int a = 0;
    long ButilochkaTime = System.currentTimeMillis();


    class ModInfo
    {
    public int id=0;
    public String m = "";

    ModInfo(int id, String msg)
    {
    this.id = id;
    this.m = msg;
    }
    }
    
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
        comNew = new HashMap<String, NewExtend>();
        warnFlag = new HashSet<String>();
        statMod = new ConcurrentHashMap<String, ModInfo>();
        shop = new Shop(this);
        frends = new frends(this);
        clan = new ClanCommand(this);
        abv = new AboutUser(this);
        gift = new Gift(this);
        init();
    }
    
    /**
     * ������������� ������ ������ � ����������
     */
    private void init(){
    	authObj.put("pmsg","�������� ��������� ���������");
    	authObj.put("reg","����� ����");
    	authObj.put("invite","�������� �����������");
    	authObj.put("kickone","��� ������ ������������");
    	authObj.put("kickall","��� ���� �������������");
    	authObj.put("ban","�������� ������������");
    	authObj.put("settheme","���������� ���� � �������");
    	authObj.put("adminsay","������������� � �������");
    	authObj.put("adminstat","�������� ���������� �� ������");
    	authObj.put("info","�������� ���������� ���������� � �����");
    	authObj.put("exthelp","����������� ������");
    	authObj.put("authread","��������� ���� � �����������");
    	authObj.put("authwrite","��������� ���������� �������������");
    	authObj.put("whouser","�������� ���� � ����� ����� ������");
    	authObj.put("room","����� �������");
    	authObj.put("whoinv","������� !whoinvite");
    	authObj.put("kickhist","������� !kickhist");
    	authObj.put("chgkick","��������� ������� ����");
    	authObj.put("dblnick","��������� ����������� ���");
    	authObj.put("anyroom","������� � ����� �������");
    	authObj.put("wroom","��������� � �������� �������");
        authObj.put("zakhist", " 5 ���������� �������������");
        authObj.put("banroom", "��������� �������������");
        authObj.put("anti_ban", "���� ''ban''");
        authObj.put("anti_kik", "���� ''kick''");
        authObj.put("anti_banroom", "���� ''banroom''");
        authObj.put("anti_chnick", "���� ''chnick''");
        authObj.put("fraza","����������� ��������� ����� � ���� ���������");
        authObj.put("adm","�������� ������ ������� � online");
        authObj.put("chnick","������� ��������� ���� ������� ������������");
        authObj.put("invise", "����������� ���������");
        authObj.put("setpass", "���������� ������ � �������");
        authObj.put("admlist", "�������� ������ ��� ���������");
        authObj.put("robmsg", "����������� ��������� ����� ��� �����-����");
        authObj.put("moder_time", "��������� ���������� ����������");
        authObj.put("xst","�������� �-������ ����");
        authObj.put("restart","������������ ����");
        authObj.put("shophist","�������� ������� ������� � ��������");
        authObj.put("gift","���������/������� ������� � ������");
        authObj.put("status_user", "��������� ��������");
        authObj.put("ubanhist", "������� ��������");
        authObj.put("shophist", "������ ������� � ��������");
        authObj.put("invitation", "���������� ������������� � ���");
        authObj.put("allroom_message", "��������� �� ��� �������");
        authObj.put("setclan", "��������/�������� ������");
        authObj.put("deladmmsg", "�������� ��� ���������");
    	
       	commands.put("!help", new Cmd("!help","",1));
        commands.put("!�������", new Cmd("!�������", "", 1));
        commands.put("!������", new Cmd("!������", "", 1));
        commands.put("!�������", new Cmd("!�������", "", 1));
        commands.put("!chat", new Cmd("!chat", "", 2));
        commands.put("!���", new Cmd("!���", "", 2));
        commands.put("!����", new Cmd("!����", "", 2));
        commands.put("!exit", new Cmd("!exit", "", 3));
        commands.put("!�����", new Cmd("!�����", "", 3));
        commands.put("!rules", new Cmd("!rules", "", 4));
        commands.put("!�������", new Cmd("!�������", "", 4));
        commands.put("!������", new Cmd("!������", "", 4));
        commands.put("!stat", new Cmd("!stat", "", 5));
        commands.put("!����", new Cmd("!����", "", 5));
        commands.put("!gofree", new Cmd("!gofree", "", 6));
        commands.put("!�����", new Cmd("!�����", "", 6));
        commands.put("!go", new Cmd("!go", "$n", 7));
        commands.put("!���", new Cmd("!���", "$n", 7));
        commands.put("!banlist", new Cmd("!banlist", "", 8));
        commands.put("!�������", new Cmd("!�������", "", 8));
        commands.put("!kicklist", new Cmd("!kicklist", "", 9));
        commands.put("!�������", new Cmd("!�������", "", 9));
        commands.put("!info", new Cmd("!info", "$c", 10));
        commands.put("!����", new Cmd("!����", "$c", 10));
        commands.put("!kick", new Cmd("!kick", "$c $n $s", 11));
        commands.put("!���", new Cmd("!���", "$c $n $s", 11));
        commands.put("!kickall", new Cmd("!kickall", "", 12));
        commands.put("!�������", new Cmd("!�������", "", 12));
        commands.put("!listauth", new Cmd("!listauth", "", 13));
        commands.put("!�������", new Cmd("!�������", "", 13));
        commands.put("!who", new Cmd("!who", "$n", 14));
        commands.put("!���", new Cmd("!���", "$n", 14));
        commands.put("!listgroup", new Cmd("!listgroup", "", 15));
        commands.put("!���������", new Cmd("!���������", "", 15));
        commands.put("!checkuser", new Cmd("!checkuser", "$n", 16));
        commands.put("!��������", new Cmd("!��������", "$n", 16));
        commands.put("!setgroup", new Cmd("!setgroup", "$n $c", 17));
        commands.put("!������", new Cmd("!������", "$n $c", 17));
        commands.put("!grant", new Cmd("!grant", "$n $c", 18));
        commands.put("!��������", new Cmd("!��������", "$n $c", 18));
        commands.put("!revoke", new Cmd("!revoke", "$n $c $s", 19));
        commands.put("!������", new Cmd("!������", "$n $c $s", 19));
        commands.put("!ban", new Cmd("!ban", "$c $s", 20));
        commands.put("!���", new Cmd("!���", "$c $s", 20));
        commands.put("!uban", new Cmd("!uban", "$c", 21));
        commands.put("!����", new Cmd("!����", "$c", 21));
        commands.put("!reg", new Cmd("!reg","$c $c",22));
        commands.put("!���", new Cmd("!���", "$c $c", 22));
        commands.put("!���", new Cmd("!���", "$c $c", 22));
        commands.put("+a", new Cmd("+a", "", 23));
        commands.put("+�", new Cmd("+�", "", 23));
        commands.put("+f", new Cmd("+f", "", 23));
        commands.put("+�", new Cmd("+�", "", 23));
        commands.put("!���", new Cmd("!���", "", 23));
        commands.put("+p", new Cmd("+p", "$n $s", 24));
        commands.put("+�", new Cmd("+�", "$n $s", 24));
        commands.put("!��", new Cmd("!��", "$n $s", 24));
        commands.put("+pp", new Cmd("+pp", "$s", 25));
        commands.put("+��", new Cmd("+��", "$s", 25));
        commands.put("!�����", new Cmd("!�����", "$s", 25));
        commands.put("!settheme", new Cmd("!settheme", "$s", 26));
        commands.put("!����", new Cmd("!����", "$s", 26));
        commands.put("!getinfo", new Cmd("!getinfo", "$c", 27));
        commands.put("!�����", new Cmd("!�����", "$c", 27));
        commands.put("!room", new Cmd("!room", "$n $c", 28));
        commands.put("!�������", new Cmd("!�������", "$n $c", 28));
        commands.put("!�", new Cmd("!�", "$n $c", 28));
        commands.put("!kickhist", new Cmd("!kickhist", "", 29));
        commands.put("!������", new Cmd("!������", "", 29));
        commands.put("!adm", new Cmd("!adm", "$s", 30));
        commands.put("!������", new Cmd("!������", "$s", 30));
        commands.put("!banhist", new Cmd("!banhist", "", 31));
        commands.put("!������", new Cmd("!������", "", 31));
        commands.put("+aa", new Cmd("+aa", "", 32));
        commands.put("+��", new Cmd("+��", "", 32));
        commands.put("!���", new Cmd("!���", "", 32));
        commands.put("!lroom", new Cmd("!lroom", "", 33));
        commands.put("!�������", new Cmd("!�������", "", 33));
        commands.put("!crroom", new Cmd("!crroom", "$n $s", 34));
        commands.put("!��������", new Cmd("!��������", "$n $s", 34));
        commands.put("!chroom", new Cmd("!chroom", "$n $s", 35));
        commands.put("!�������", new Cmd("!�������", "$n $s", 35));
        commands.put("!�����", new Cmd("!�����", "$n $n", 36));
        commands.put("!���������", new Cmd("!���������", "", 37));
        commands.put("!��������", new Cmd("!��������", "", 38));
        // TODO: 39 - �������
        // TODO: 40 - ������� � ����
        commands.put("!��������", new Cmd("!��������", "$n $n $s", 41));
        commands.put("!���������", new Cmd("!���������","",42));
        commands.put("!�����", new Cmd("!�����", "$s", 43));
        commands.put("!������", new Cmd("!������", "", 44));
        commands.put("!chnick", new Cmd("!chnick","$n $c",45));
        commands.put("!�����", new Cmd("!�����","$n $c",45));
        commands.put("!��������", new Cmd("!��������", "$n", 46));
        commands.put("!��������", new Cmd("!��������", "$n", 47));
        commands.put("!setpass", new Cmd("!setpass", "$c", 48));
        commands.put("!������", new Cmd("!������", "$c", 48));
        commands.put("!�������", new Cmd("!�������", "$c", 49));
        commands.put("!������", new Cmd("!������", "$s", 50));
        commands.put("!�������", new Cmd("!�������", "$n $s", 51));
        commands.put("!�������������", new Cmd("!�������������", "", 52));
        commands.put("!������", new Cmd("!������", "$s", 53));
        commands.put("!����������", new Cmd("!����������", "", 54));
        commands.put("!�������", new Cmd("!�������", "$n", 55));
        commands.put("!������������", new Cmd("!������������", "$n $s", 56));
        commands.put("!�������������", new Cmd("!�������������", "$c $s", 57));
        commands.put("!�����", new Cmd("!�����", "$s", 58));
        commands.put("!������", new Cmd("!������", "", 59));
    	WorkScript.getInstance(srv.getName()).installAllChatCommandScripts(this);
    }
    
    /**
     * ������ ������ ���������� ��� ������
     * @return
     */
    public HashMap<String, String> getAuthObjects(){
    	return this.authObj;
    }
    
    /**
     * ������ ������ ������
     * @return
     */
    public HashMap<String, Cmd> getCommands(){
    	return this.commands;
    }
    
    /**
     * ���������� ������ ������� ����������
     * @param name
     * @param comment
     * @return - ������, ���� ����� ������ ��� ��� � �������
     */
    public boolean addAuth(String name, String comment){
    	boolean f = authObj.containsKey(name);
    	authObj.put(name, comment);
    	return f;
    }
    
    /**
     * ���������� ����� �������
     * @param name
     * @param c
     * @return - ������, ���� ������� ��� ����������
     */
    public boolean addCommand(String name, Cmd c) {
    	boolean f = commands.containsKey(name);
    	commands.put(name, c);
    	return f;
    }
    
    /**
     * ���������� ��������� �������
     * @return
     */
    public CommandParser getParser(){
    	return parser;
    }
    

private void firstScript(IcqProtocol proc)
{
try
{
if(!firstStartScript)
{  
scr = new ScriptWork(this);
scr.installAll(proc);
firstStartScript=true;
}
}
catch (Exception ex)
{
ex.printStackTrace();
}
}


private void firstMsg(IcqProtocol proc)
{
if(!firstStartMsg){
String[] s = srv.getProps().getAdmins();
for(int i=0;i<s.length;i++){
String k = "��� ������� - " + new Date(Time.getTimeStart());
String ss = k;
if(MainProps.checkNewVersion())
ss += "�������� ����� ������!\n" + MainProps.getNewVerDesc();
else
ss += "";
proc.mq.add(s[i], ss);
}
firstStartMsg=true;
}
}
    

    
    public AbstractServer getServer()
    {
    return srv;
    }
    
    /**
     * �������� ��������� ������� ������
     */
    public void parse(IcqProtocol proc, String uin, String mmsg) {
    if(Quiz == null)
    {
    Quiz = new MyQuiz(srv);
    Quiz.start();
    }
    if(voting_events == null)
    {
    voting_events = new Voting(this);
    voting_events.start();
    }
    if(radm == null)
    {
    radm = new RobAdmin(srv);
    radm.start();
    }
    firstMsg(proc);//������ ���������
    firstScript(proc);//������ ��������
    state++;
    /*psp.del_logs_time();*/
    Log.debug("CHAT: parse " + proc.baseUin + ", " + uin + ", " + mmsg); 
    if(psp.getBooleanProperty("chat.writeInMsgs"))
    //������� ������� ��������� ���������� � �� �� ����� ��� ��������� ������������
    if(mmsg.length()>1000){srv.us.db.log(0,uin,"IN",mmsg.substring(0, 1000),0);} else {srv.us.db.log(0,uin,"IN",mmsg,0);}
    String tmsg = mmsg.trim();
    if(tmsg.length()==0){Log.error("������ ��������� � ������� ������: " + uin + ">" + mmsg);return;}
    if(tmsg.charAt(0)=='!' || tmsg.charAt(0)=='+'){Log.info("CHAT COM_LOG: " + uin + ">>" + tmsg);}
    try {
    //banroom
    if (testClosed(uin)==0 & !srv.us.authorityCheck(uin,"room"))freedom(uin);
    //�������� �� ��������� ����������
    int moder = srv.us.getUser(uin).id;
    if (srv.us.getUserGroup(moder).equals("modertime") & testModer(uin)==0){ModerNoTime(proc, uin);}
    
    if(srv.us.testUser(uin))
    {
    if(isBan(uin)){Log.flood2("CHAT_BAN: " + uin + ">" + mmsg);return;}
    if(testKick(uin)>0){Log.info("CHAT_KICK: " + uin + ">" + mmsg);return;}
    if(srv.us.getUser(uin).state==UserWork.STATE_CHAT)
    goChat(proc, uin, mmsg, parser.parseArgs(mmsg));
    } 
    else
    {
    // ��� ������ �����
    // �������� �� ����
    if(floodNoReg.containsKey(uin)){
    FloodElement e = floodNoReg.get(uin);
    if(e.getDeltaTime()<(psp.getIntProperty("chat.floodTimeLimitNoReg")*1000))
    {
    e.addMsg(tmsg);
    floodNoReg.put(uin, e);
    Log.flood("FLOOD NO REG " + uin + "> " + tmsg);
    return; // ������� �����
    }
    if(e.isDoubleMsg(tmsg) && e.getCount()>3)
    {
    e.addMsg(tmsg);
    floodNoReg.put(uin, e);
    Log.flood("FLOOD NO REG " + uin + "> " + tmsg);
    return; // ������ ���������
    }
    e.addMsg(tmsg);
    floodNoReg.put(uin, e);
    } 
    else
    {
    FloodElement e = new FloodElement(psp.getIntProperty("chat.floodTimeLimitNoReg")*1000);
    floodNoReg.put(uin, e);
    }
    /*
     * �������� �������� �� �����?
     */
    if(psp.getBooleanProperty("chat.captcha")){

       /**
        *   ����� ��� ������ ��������� �� �����
        *   @author fraer72
        */

      // ���� ��� �� ������� - ������� �����������, ����� ������������ �������
      boolean captcha = false; // ������ ����� � ��������� ����� ������?
      if(srv.us.getUser(uin).state != UserWork.STATE_CAPTCHA && tmsg.charAt(0) != '!' && comNew.containsKey(uin))
      {
      if(comNew.get(uin).getMsg().equalsIgnoreCase(mmsg))
      {
      captcha = true;
      v = comNew.get(uin).getData();
      comNew.remove(uin);
      }
      else
      {
      proc.mq.add(uin,"�� ����������� �������� �� ����������� ������, ����������� ��� ���.:)");
      comNew.remove(uin);
      return;
      }
      }
      if(!captcha)
      {
      String s = getCaptcha();
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.0", new Object[] {s.split("=")[0],psp.getIntProperty("chat.floodTimeLimitNoReg")}));
      comNew.put(uin, new NewExtend(uin, mmsg, s.split("=")[1],v, 5*60000));
      return;
      }
      proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.1", new Object[] {psp.getStringProperty("chat.name"),psp.getIntProperty("chat.floodTimeLimitNoReg")}));
      Log.talk(uin + " Captcha user: " + mmsg);
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
 
            // �������� �� ����
            if(floodMap.containsKey(uin))
            {
            FloodElement e = floodMap.get(uin);
            e.addMsg(tmsg);
            floodMap.put(uin, e);
            } 
            else
            {
            FloodElement e = new FloodElement(psp.getIntProperty("chat.floodTimeLimit")*1000);
            e.addMsg(tmsg);
            floodMap.put(uin, e);
            }
            testFlood(proc,uin);
            mmsg = WorkScript.getInstance(srv.getName()).startMessagesScript(mmsg, srv);
            if(mmsg.equals("")) return; // ��������� ���� ������� � �������
            int tp = 0;
            if(comMap.containsKey(uin))
            if(!comMap.get(uin).isExpire())
            tp = parser.parseCommand(comMap.get(uin).getCmd());
            else
            {
            tp = parser.parseCommand(tmsg);
            comMap.remove(uin);
            }
            else
            tp = parser.parseCommand(tmsg);
            /********************************/
            if(comNew.containsKey(uin))
            if(!comNew.get(uin).isExpire())
            tp = parser.parseCommand(comNew.get(uin).getCmd());
            else
            {
            tp = parser.parseCommand(tmsg);
            comNew.remove(uin);
            }
            else
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
                goChat(proc, uin, mmsg, parser.parseArgs(mmsg));
                if(psp.getBooleanProperty("chat.getUserInfoOnChat"))
                proc.mq.add(uin, "", 1); //proc.recUserInfo(uin,"0");
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
            	//TODO �������� ������ ����������
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
                if(!auth(proc,uin, "moder_time")) return;
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
                ex.printStackTrace();
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
                ModerTime(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 37:
                if(!isChat(proc,uin) && !psp.testAdmin(uin)) break;
                if(!auth(proc,uin, "kickone")) return;
                proc.mq.add(uin, listModUsers());
                break;
           case 38:
                commandZakHist(proc, uin);
                break;
           case 39: // ��������� ������� ��������
            	String ret = WorkScript.getInstance(srv.getName()).startChatCommandScript(parser.parseCommand2(tmsg).script, tmsg, uin, proc, this);
            	if(!ret.equals("") && !ret.equals("ok")) proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.2"));
            	break;
           case 40: // ������� � ����
                String re = scr.startScript(parser.parseCommand2(tmsg).script, tmsg, uin, proc, this);
                if(!re.equals("") && !re.equals("ok")) proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.2"));
                break;
           case 41:
                BanRoom(proc, uin, parser.parseArgs(tmsg), mmsg);
                break;
           case 42:
                commandBytilochka(proc, uin, parser.parseArgs(tmsg), mmsg);
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
                default:
     //�������������� ������� �� ������ �������
     if(scr.commandExec(proc, uin, mmsg)) return;
     if(voting_events.commandVoting(proc, uin, mmsg)) return;
     if(shop.commandShop(proc, uin, mmsg)) return;
     if(frends.commandFrends(proc, uin, mmsg)) return;
     if(abv.commandAboutUser(proc, uin, mmsg)) return;
     if(gift.commandShop(proc, uin, mmsg)) return;
     if(clan.commandClan(proc, uin, mmsg)) return;
     if(srv.us.getUser(uin).state==UserWork.STATE_CHAT){
     //��������� ������������ � "!" � "+" �� ������� � ���
     try{
     if(mmsg.substring(0, 1).equals("!")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.3"));return;}
     } catch (Exception ex){ex.printStackTrace();}
     String s = "";
     if(mmsg.indexOf("/me")==0)
     s = mmsg.replaceFirst("/me", "*" + srv.us.getUser(uin).localnick);
     else
     s += srv.us.getUser(uin).localnick + psp.getStringProperty("chat.delimiter")+" " + mmsg;
     if(s.length()>psp.getIntProperty("chat.MaxMsgSize")){
     s = s.substring(0,psp.getIntProperty("chat.MaxMsgSize"));
     proc.mq.add(uin,"������� ������� ��������� ���� ��������: " + s);
     }
     s = s.replace('\n',' ');
     s = s.replace('\r',' ');
     Log.talk("CHAT: " + uin + "<" + srv.us.getUser(uin).id +"> ["+srv.us.getUser(uin).room +"]>>" + s);
     srv.us.db.log(srv.us.getUser(uin).id,uin,"OUT", s, srv.us.getUser(uin).room);
     srv.cq.addMsg(s, uin, srv.us.getUser(uin).room);
     if(psp.getBooleanProperty("adm.useAdmin")) radm.parse(proc,uin,s,srv.us.getUser(uin).room);
     if(psp.getBooleanProperty("vic.on.off")) Quiz.parse(uin,mmsg,srv.us.getUser(uin).room);
     } 
     else
     {
     if(srv.us.getUser(uin).state==UserWork.STATE_NO_CHAT){
     goChat(proc, uin, mmsg, parser.parseArgs(mmsg));
     return;
     }
     if(srv.us.getUser(uin).localnick==null || srv.us.getUser(uin).localnick.equals("") || srv.us.getUser(uin).state == UserWork.STATE_NO_REG) {
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.parse.4", new Object[] {psp.getStringProperty("chat.name"),psp.getIntProperty("chat.floodTimeLimitNoReg")}));
     return;
     }
     }
     }
     } catch (Exception ex) {ex.printStackTrace();}
     }
    
    /**
     * ������� ����
     */



    public String A(int room)
    {
    String Y = "";
    if(room == psp.getIntProperty("vic.room"))
    {
    Y += srv.us.AnswerUsersTop();
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.0") + "\n";
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.1") + "\n";
    }
    else
    {
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.0") + "\n";
    Y += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.1") + "\n";
    if(psp.getBooleanProperty("adm.useAdmin")){Y += "0 - " + radm.NICK + '\n';}
    }
    return Y;
    }

    public String AA(int room)
    {
    String Y = "";
    if(room == psp.getIntProperty("vic.room"))
    {
    Y += "";
    }
    else
    {
    if(psp.getBooleanProperty("adm.useAdmin")){Y += "0 - " + radm.NICK + '\n';}
    }
    return Y;
    }

    public int AllUsersRoom(int i)
    {
    int c = 0;
    Enumeration<String> e2 = srv.cq.uq.keys();
    while(e2.hasMoreElements())
    {
    String i2 = e2.nextElement();
    Users us = srv.us.getUser(i2);
    if(us.state==UserWork.STATE_CHAT)
    {
    if(us.room==i)
    {
    c++;
    }
    }
    }
    return (c-1);
    }


     /*
     * ����� ��������� ����� ��� ���������
     */
    public int n()
    {
    long i = srv.us.db.getLastIndex("butilochka");
    String o = "" + i;
    int p = Integer.valueOf(o);
    int i1 =  radm.getRND(p);
    return i1;
    }

     /**
     * !help
     */
    public void commandHelp(IcqProtocol proc, String uin)
    {
    if(srv.us.getUser(uin).id == 0){return;}
    String[] s = psp.loadText("./text/" + srv.getName() + "/help1.txt").split("<br>");
    for (int i = 0; i < s.length; i++)
    {
    proc.mq.add(uin, s[i]);
    }
    if (srv.us.authorityCheck(uin, "exthelp"))
    {
    s = psp.loadText("./text/" + srv.getName() + "/help2.txt").split("<br>");
    for (int i = 0; i < s.length; i++)
    {
    proc.mq.add(uin, s[i]);
    }
    }
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
     }catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
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
      }catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}        
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
    String s = (String)v.get(0);
    int id = 0;
    String L = "";
    String Ban = "";
    String No_chat = "";
    String In_chat = "";
    String Kick = "";
    String Closed = "";
    id = Integer.parseInt(s);    
    if(id==0){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.0"));return;}
    Users uss = srv.us.getUser(id);    
    try{
    L += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.1") + uss.localnick;
    L += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.2", new Object[] {(uss.data==0 ? "�� ��������" : (("|" + new Date(uss.data)).toString() + "|"))});
    L += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.3", new Object[] {uss.sn});
    L += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.4", new Object[] {uss.id});
    L += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.5", new Object[] {uss.group});
    L += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.6", new Object[] {uss.ball});
    L += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.7", new Object[] {uss.answer});
    if(state==-1){ Ban += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.8");}
    if(state==1){ No_chat += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.9");}
    if(state==2){ In_chat+= "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.10", new Object[] {testClosed(uss.sn)});}
    if(testClosed(uss.sn)>1){ Closed += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.11", new Object[] {testClosed(uss.sn)});}
    if(testKick(uss.sn)>1){ Kick += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.12", new Object[] {testKick(uss.sn)});}
    proc.mq.add(uin,L + Ban + No_chat + In_chat + Closed + Kick);
    }catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.13", new Object[] {ex.getMessage()}));}
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
    } catch (Exception ex){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err.1", new Object[] {ex.getMessage()}));return;}
    String i = srv.us.getUser(id).sn;
    if(testKick(i)>0 && !auth(proc,uin, "chgkick")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.0"));return;}
    if (psp.testAdmin(i) || qauth(proc, i, "anti_kik")) {proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.1"));return;}
    if(t==0)
    {
    tkick(proc, i, psp.getIntProperty("chat.defaultKickTime"), moder_id,"");
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.3", new Object[] {testKick(i)}));
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.4", new Object[] {srv.us.getUser(i).localnick,srv.us.getUser(i).id,t,r,srv.us.getUser(uin).localnick,srv.us.getUser(uin).id}), i, srv.us.getUser(i).room);
    } 
    else
    {
    if(r.equals("")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandKick.5"));return;}
    if(t>psp.getIntProperty("chat.maxKickTime"))
    t=psp.getIntProperty("chat.maxKickTime");
    tkick(proc, i, t, moder_id, r);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandInfo.3", new Object[] {t}));
    }
    } catch (Exception ex) {ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
    }
    
    /**
     * !who
     * @param proc
     * @param uin
     * @param v
     */
    public void commandWho(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "whouser")) return;
    try
    {
    int id = (Integer)v.get(0);
    proc.mq.add(uin, srv.us.getUserNicks(id));
    }
    catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
    }
    
    /**
     * !checkuser
     * @param proc
     * @param uin
     * @param v
     */
    public void commandCheckuser(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authread")) return;
    try{
    int id = (Integer)v.get(0);
    proc.mq.add(uin, srv.us.getUserAuthInfo(id));
    }
    catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
    }
    
    /**
     * !setgroup
     * @param proc
     * @param uin
     * @param v
     */
    public void commandSetgroup(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authwrite")) return;
    try
    {
    String s1 = (String)v.get(1);
    int id = (Integer)v.get(0);
    Users uss = srv.us.getUser(id);
    if(uss.id!=id){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.0"));return;}
    if(!testUserGroup(s1)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.1"));return;}
    uss.group = s1;
    boolean f = srv.us.setUserPropsValue(id, "group", s1) &&
    srv.us.setUserPropsValue(id, "grant", "") &&
    srv.us.setUserPropsValue(id, "revoke", "");
    srv.us.clearCashAuth(id);
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.2", new Object[] {s1}));
    if(f)
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.3"));
    else
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSetgroup.4"));
    } 
    catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
    }
    
    /**
     * !grant
     * @param proc
     * @param uin
     * @param v
     */
    public void commandGrant(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authwrite")) return;
    try{
    String s1 = (String)v.get(1);
    int id = (Integer)v.get(0);
    Users uss = srv.us.getUser(id);
    if(uss.id!=id){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.0"));return;}
    if(!testAuthObject(s1)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.1"));return;}
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.2", new Object[] {s1}));
    if(srv.us.grantUser(id, s1))
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.3"));
    else
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGrant.4"));
    }
    catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
    }
    
    /**
     * !revoke
     * @param proc
     * @param uin
     * @param v
     */
    public void commandRevoke(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "authwrite")) return;
    try{
    String s1 = (String)v.get(1);
    int id = (Integer)v.get(0);
    String r = (String)v.get(2);
    Users uss = srv.us.getUser(id);
    if(uss.id!=id){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.0"));return;}
    if(!testAuthObject(s1)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.1"));return;}
    if(r.equals("")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.2"));return;}
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.3", new Object[] {s1,r}));
    if(srv.us.revokeUser(id, s1))
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.4"));
    else
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRevoke.5"));
    }
    catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
    }
    
    /**
     * !ban
     * @param proc
     * @param uin
     * @param v
     */
    public void commandBan(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "ban")) return;
    try{
    String s = (String)v.get(0);
    String m = (String)v.get(1);
    String i="";
    if(s.length()>=6)
    {
    if(uin.equals(s)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.0"));return;}
    if(m.equals("")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.1"));return;}
    ban(proc, s, uin,m);
    } 
    else
    {
    int id = 0;
    try {
    id = Integer.parseInt(s);
    } catch(Exception ex) {proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.2"));return;}
    i = srv.us.getUser(id).sn;
    if(!i.equals(""))
    {
    if(uin.equals(i)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.3"));return;}
    if (psp.testAdmin(i) || qauth(proc, i, "anti_ban")) {proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.4"));return;}
    if(m.equals("")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.5"));return;}
    ban(proc, i, uin,m);
    }
    }   
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.6", new Object[] {i}));
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandBan.7", new Object[] {srv.us.getUser(i).localnick,srv.us.getUser(i).id,m,srv.us.getUser(uin).localnick,srv.us.getUser(uin).id}), i, srv.us.getUser(i).room);
    }
    catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
    }
    
    /**
     * !uban
     * @param proc
     * @param uin
     * @param v
     */
    public void commandUban(IcqProtocol proc, String uin, Vector v)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "ban")) return;
    try{
    String s = (String)v.get(0);
    String i="";
    if(s.length()>=6){
    uban(proc, s, uin);
    } 
    else
    {
    int id = 0;
    try {
    id = Integer.parseInt(s);
    } catch (Exception e) {proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandUban.0"));}
    i = srv.us.getUser(id).sn;
    if(!i.equals("")) uban(proc, i, uin);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandUban.1", new Object[] {i}));
    }            
    }
    catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));}
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
     if (lnick.equals("") || lnick.equals(" "))
     {
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.0"));
     return;
     }
     if(lnick.length()>maxNick)
     {
     lnick = lnick.substring(0,maxNick);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.1"));
     }
     if(!testNick(uin,lnick))
     {
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.2"));
     return;
     }
     lnick = lnick.replace('\n',' ');
     lnick = lnick.replace('\r',' ');
     if(psp.getBooleanProperty("chat.isUniqueNick") && !qauth(proc,uin,"dblnick") && !psp.testAdmin(uin))
     if(srv.us.isUsedNick(lnick))
     {
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.3"));
     return;
     }
            
    
     //����� ���� - ���� ��� � ����, ������ �� �����
     if(uss.state != UserWork.STATE_CAPTCHA  && uss.state != UserWork.STATE_NO_REG)
     {
     if(!auth(proc,uin, "reg")) return;
     if(uss.state!=UserWork.STATE_CHAT) return; // ������ ��� ���� � ����
     if(lnick.length()>maxNick)
     {
     lnick = lnick.substring(0,maxNick);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.4"));
     }
     if(srv.us.getCountNickChange(uss.id)>psp.getIntProperty("chat.maxNickChanged")){
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.5"));
     return;
     }
     if (oldNick.equals(lnick))
     {
     if (uss.state == UserWork.STATE_NO_CHAT) {
     proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.6", new Object[] {psp.getStringProperty("chat.name")}));
     return;
     }     
     if (uss.state == UserWork.STATE_CHAT)
     {
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.7"));
     }     
     return;
     }
     uss.localnick = lnick;
     Log.talk(uin + " update " + mmsg);
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.8"));
     srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.9", new Object[] {oldNick,lnick}), "", uss.room); //��������� ��� ����
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
     proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandReg.11"));
     uss.state = UserWork.STATE_NO_CHAT;
     uss.data = System.currentTimeMillis();
     srv.us.updateUser(uss);
     }
     catch (Exception ex)
     {
     ex.printStackTrace();
     Log.talk(uin + " Reg error: " + mmsg);
     //proc.mq.add(uin,"������");
     }

     //������� goChat
     goChat(proc, uin, mmsg, v);
     return;

     /*
     }        
     catch (Exception ex)
     {
     ex.printStackTrace();
     Log.talk(uin + " Reg error: " + mmsg);
     proc.mq.add(uin,"������");
     }
     */
     
     }


    /**
     * +a
     * @param proc
     * @param uin
     */
    public void commandA(IcqProtocol proc, String uin)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    int room = srv.us.getUser(uin).room;
    String s = Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.2", new Object[] {room,srv.us.getRoom(room).getName()});
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.3", new Object[] {srv.us.getRoom(room).getTopic()});
    s += A(room);
    Enumeration<String> e = srv.cq.uq.keys();
    int cnt=0;
    while(e.hasMoreElements()){
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if(us.state==UserWork.STATE_CHAT)
    {
    if(us.room==room)
    {
    cnt++;
    s += us.id + " - " + us.localnick + "|" + us.ball + "|" + (us.status.equals("") ? "" : ("|" + us.status + "|")) + '\n';
    }
    } 
    }
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.4", new Object[] {cnt});
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandA.5", new Object[] {"�"+Integer.toString(srv.us.count()) +"�"});
    proc.mq.add(uin, s);       
    }
    
    /**
     * +aa
     * @param proc
     * @param uin
     */
    public void commandAA(IcqProtocol proc, String uin)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    int room = srv.us.getUser(uin).room;
    String s = Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.0") + "\n";
    s += Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.1") + "\n";
    s += AA(room);
    int cnt=0;
    Enumeration<String> e = srv.cq.uq.keys();
    while(e.hasMoreElements())
    {
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if(us.state==UserWork.STATE_CHAT)
    {
    cnt++;
    s += us.id + " - " + us.localnick + " ["+ us.room + "] " + "[" + us.ball + "]" + (us.status.equals("") ? "" : ("[" + us.status + "]")) + '\n';
    }
    }
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.2", new Object[] {cnt});
    s += "\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAA.3", new Object[] {"�" + srv.us.statUsersCount()+ "�"});
    proc.mq.add(uin, s);
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
    if(txt.equals("")) {proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.0"));return;}
    Users uss = srv.us.getUser(no);
    if(uss == null){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.1"));return;}
    if(!srv.cq.testUser(uss.sn)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.2"));return;}
    if(txt.length()>psp.getIntProperty("chat.MaxMsgSize"))
    {
    txt = txt.substring(0,psp.getIntProperty("chat.MaxMsgSize"));
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.3", new Object[] {txt}));
    }
    //TODO: ������ ������ ������ ������������ ���������� null.... ��� ���������� ��� �������
        /*if(psp.getBooleanProperty("lichnoe.on.off"))
        {
        String s = psp.getStringProperty("chat.lichnoe");
        String[] ss = s.split(";");
        for (int i=0;i<ss.length;i++)
        {
        Users usss = srv.us.getUser(ss[i]);
        srv.getIcqProcess(usss.basesn).mq.add(usss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.4", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,uin,uss.localnick,uss.id,txt}));
        }
        }*/
    Log.talk("CHAT: " + uss.sn + ">>" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    srv.us.db.log(uss.id,uin,"LICH",">> " + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}),uss.room);
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    setPM(uss.sn, uin);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.6"));
    }
    catch (Exception ex){ex.printStackTrace();Log.talk(uin + " Private msg error: " + tmsg);proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.7"));}
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
    if(txt.equals("")) {proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.0"));return;}
    if(fsn.equals("")) {proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandPP.0"));return;}
    Users uss = srv.us.getUser(fsn);
    if(uss == null){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.1"));return;}
    if(!srv.cq.testUser(uss.sn)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.2"));return;}
    if(txt.length()>psp.getIntProperty("chat.MaxMsgSize"))
    {
    txt = txt.substring(0,psp.getIntProperty("chat.MaxMsgSize"));
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.3", new Object[] {txt}));
    }
    //TODO: ������ ������ ������ ������������ ���������� null.... ��� ���������� ��� �������
        /*if(psp.getBooleanProperty("lichnoe.on.off"))
        {
        String s = psp.getStringProperty("chat.lichnoe");
        String[] ss = s.split(";");
        for (int i=0;i<ss.length;i++)
        {
        Users usss = srv.us.getUser(ss[i]);
        srv.getIcqProcess(usss.basesn).mq.add(usss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.4", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,uin,uss.localnick,uss.id,txt}));
        }
        }*/
    Log.talk("CHAT: " + uss.sn + ">>" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    srv.us.db.log(uss.id,uin,"LICH",">> " + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}),uss.room);
    srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.5", new Object[] {srv.us.getUser(uin).localnick,srv.us.getUser(uin).id,txt}));
    setPM(uss.sn, uin);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.6"));
    }
    catch (Exception ex){ex.printStackTrace();Log.talk(uin + " Private msg error: " + tmsg);proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandP.7"));}
    }
    
    /**
     * !settheme
     * @param proc
     * @param uin
     * @param v
     */
    public void commandSettheme(IcqProtocol proc, String uin, Vector v)
    {
    if(!auth(proc,uin, "settheme")) return;
    String s = (String)v.get(0);
    Users uss = srv.us.getUser(uin);
    int room = srv.us.getUser(uin).room;
    Rooms r = srv.us.getRoom(room);
    r.setTopic(s);
    srv.us.saveRoom(r, "");
    Log.info("����������� ���� ������� " + room + ": " + s);
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandSettheme.0", new Object[] {uss.localnick,s}), "", room);
    }
    
    /**
     * !getinfo
     * @param proc
     * @param uin
     * @param v
     */
    public void commandGetinfo(IcqProtocol proc, String uin, Vector v)
    {
    if(!isAdmin(proc, uin)) return;
    try{
    String s = (String)v.get(0);
    s = srv.us.getUser(Integer.parseInt(s)).sn;
    proc.mq.add(s, "", 1);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGetinfo.0", new Object[] {s}));
    } catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandGetinfo.1"));}
    }
    
    /**
     * !room
     * @param proc
     * @param uin
     * @param v
     */
   public void commandRoom(IcqProtocol proc, String uin, Vector v)
   {
   if (!isChat(proc, uin))
   {
   return;
   }
   if (!auth(proc, uin, "room"))
   {
   return;
   }
   try
   {
   int i = (Integer) v.get(0);
   String pass = (String) v.get(1);
   Users uss = srv.us.getUser(uin);
   Users u = srv.us.getUser(i);
   if(i==psp.getIntProperty("room.tyrma") && !psp.testAdmin(uin)){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.0", new Object[] {uss.localnick}));return;}
   if (uss.room == i)
   {
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.1", new Object[] {uss.localnick}));
   return;
   } else if (!srv.us.getRoom(i).checkPass(pass) && !psp.testAdmin(uin))
   {
   if (srv.us.getCountpassChange(uss.id, u.id) >= 1) {
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.2", new Object[] {uss.localnick}));
   return;
   }
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.3", new Object[] {uss.localnick}));
   srv.us.db.event(uss.id, uin, "PASS", u.id, u.sn, "������� ������ ������");
   return;
   }
   if( srv.us.getRoom(i).getUser_id() != 0 && srv.us.getRoom(i).getUser_id() != uss.clansman && !psp.testAdmin(uin) )
   {
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.4", new Object[] {uss.localnick,srv.us.getRoom(i).getName()}));
   return;
   }
   if (qauth(proc, uin, "anyroom") || srv.us.checkRoom(i))
   {
   srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.5", new Object[] {uss.id,uss.localnick,srv.us.getRoom(i).getName()}), uin, uss.room);
   uss.room = i;
   srv.us.updateUser(uss);
   srv.cq.changeUserRoom(uin, i);
   srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.6", new Object[] {uss.id,uss.localnick,srv.us.getRoom(i).getName()}), uin, uss.room);
   proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.7", new Object[] {i,srv.us.getRoom(i).getName(),(srv.us.getRoom(i).getTopic().equals("") ? "" : ("\n����: " + srv.us.getRoom(i).getTopic())),"\n������������� � ������� - |" + AllUsersRoom(i) + "|"}));
   } 
   else
   {
   proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandRoom.8", new Object[] {uss.localnick}));
   }
   }
   catch (Exception ex)
   {
   ex.printStackTrace();
   proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
   }
   }

        
    /**
     * !kickhist
     * @param proc
     * @param uin
     */
    public void commandKickhist(IcqProtocol proc, String uin)
    {
    if(!auth(proc,uin, "kickhist")) return;
    try {
    proc.mq.add(uin,srv.us.getKickHist());
    } catch (Exception ex) {ex.printStackTrace();
    proc.mq.add(uin,ex.getMessage());            
    }
    }
    
    /**
     * !adm
     * @param proc
     * @param uin
     * @param v
     */
    public void commandAdm(IcqProtocol proc, String uin, Vector v)
    {
    if(srv.us.getUser(uin).id == 0){return;}
    try {
    String s = (String)v.get(0);
    Users uss = srv.us.getUser(uin);
    int id_2 = uss.id;
    if(s.equals("")){proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAdm.0"));return;}
    long t = System.currentTimeMillis();
    long z = srv.us.db.getLastIndex("admmsg");
    int id = (int) (z + 1)-1;
    srv.us.db.admmsg(id, id_2, s, t);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAdm.1"));
    }
    catch (Exception ex) {ex.printStackTrace();Log.talk("Error save msg: " + ex.getMessage());proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandAdm.2"));}
    }
    
    /**
     * !banhist
     * @param proc
     * @param uin
     */
    public void commandBanhist(IcqProtocol proc, String uin)
    {
    if(!auth(proc,uin, "ban")) return;
    try {
    proc.mq.add(uin,srv.us.getBanHist());
    }
    catch (Exception ex) {ex.printStackTrace();proc.mq.add(uin,ex.getMessage());}
    }
    
  /**
     * !lroom - ������� ������ ������������������ ������
     * @param proc
     * @param uin
     */
    public void commandLRoom(IcqProtocol proc, String uin)
    {
    /*
    if (!isChat(proc, uin) && !psp.testAdmin(uin))
    {
    return;
    }
    */
    String s = Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandLRoom.0") + "\n";
    Set<Integer> rid = srv.us.getRooms();
    Integer[] rooms = (Integer[]) rid.toArray(new Integer[0]);
    Arrays.sort(rooms);
    //TODO ������ ������ �� �������
    for (Integer i : rooms)
    {
    int cnt = 0;
    Enumeration<String> e = srv.cq.uq.keys();
    while (e.hasMoreElements())
    {
    String g = e.nextElement();
    Users us = srv.us.getUser(g);
    if (us.state == UserWork.STATE_CHAT && us.room == i)
    {
    cnt++;
    }
    }
    s += i + " - " + srv.us.getRoom(i).getName() + " �" + cnt + " ���.�" + "\n";
    }
    proc.mq.add(uin, s);
    }
    
    /**
     * !crroom - �������� ����� �������
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
     * !chroom - ��������� �������� �������
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
     * ��������, ���� �� �������� ��������� ��������� � �� ����
     */
    private String testPM(String sn){
        if(up.get(sn)==null)
            return "";
        else 
            return up.get(sn);
    }
    
    /**
     * ����������� ��������� ������ ��������� ���������
     */
    private void setPM(String sn, String from_sn){
        up.put(sn,from_sn);
    }
    
    /**
     * �������� ���� �� ������������
     */
    public boolean testNick(String sn, String nick){
        Users uss = srv.us.getUser(sn);
        if(psp.testAdmin(sn)) return true; // ������� ����� ����� ��� :)
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
     * ����� ������ �������� ����������
     */
    public String listAuthObjects(){
        String s="������� ����������:\n";
        for(String c:authObj.keySet()){
        	s += c + " - " + authObj.get(c)+"\n";
        }
        return s;
    }

    /**
     * �������� ������� �� ������� � ������
     */
    public boolean testAuthObject(String tst)
    {
    return authObj.containsKey(tst);
    }
    
    /**
     * ���� ����� ������?
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
     * �������� �����, ������ �� ��
     */
    public int testKick(String sn){
    	long tc = srv.us.getUser(sn).lastKick;
    	long t = System.currentTimeMillis();
    	return tc>t ? (int)(tc-t)/60000 : 0;
    }
    
    /**
     * ��� ����� �� �������
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
     * ������ ������ � ��������� ����
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
     * ������ ��������� ����� ������� ���� �����
     */
    public void parseInfo(Users u, int type){
    	switch(type) {
    	case 1: // �������� ����
    		Log.info("User: " + u.sn + ", " + u.nick);
    		Users uu = srv.us.getUser(u.sn);
    		uu.sn = u.sn;
    		uu.nick = u.nick;
    		uu.fname = u.fname;
    		//uu.lname = u.lname;
    		uu.email = u.email;
    		srv.us.updateUser(uu);
    		break;
    	default:
    	}
    }
    
    /**
     * �������� ��������
     * @param uin
     */
    public void testState(String uin){
        if (psp.testAdmin(uin))
        {
        return;//���� ���� ��� �����
        }
        long t = floodMap.get(uin).getDeltaTime();
        if(t>(psp.getIntProperty("chat.autoKickTimeWarn")*60000) &&
                !warnFlag.contains(uin)){
            Log.info("Warning to " + uin);

            srv.getIcqProcess(srv.us.getUser(uin).basesn).mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.testState.0"));
            warnFlag.add(uin);
        }
        if(t>(psp.getIntProperty("chat.autoKickTime")*60000)){
            Log.talk("Autokick to " + uin);
            warnFlag.remove(uin);
            kick(srv.getIcqProcess(srv.us.getUser(uin).basesn),uin);
        }
    }
    
    /**
     * ���� - ������� �����?
     * @param proc
     * @param uin
     * @return
     */
    public boolean isAdmin(IcqProtocol proc, String uin)
    {
    Users u = srv.us.getUser(uin);
    if(!psp.testAdmin(uin)){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.auth.0", new Object[] {u.localnick}));
    return false;
    }
    return true;
    }
    
    /**
     * �������� ����������
     * @param proc
     * @param uin
     * @param obj
     * @return
     */
    public boolean auth(IcqProtocol proc, String uin, String obj)
    {
    Users u = srv.us.getUser(uin);
    if(!srv.us.authorityCheck(uin, obj)){
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.auth.0", new Object[] {u.localnick}));
    return false;
    }
    return true;
    }
    
    /**
     * ����� �������� ����������. �� ������� ���������.
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
     * ���� � ���
     * @param proc
     * @param uin
     */
    public void goChat(IcqProtocol proc, String uin, String mmsg, Vector v) {
    Users uss = this.srv.us.getUser(uin);
    boolean f = false;
    int room = 0;
    boolean room_in_chat = false;
    if(uss.localnick==null || uss.localnick.equals("") || uss.state==UserWork.STATE_CAPTCHA)
    {
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.0", new Object[] {psp.getStringProperty("chat.name")}));
    return;
    }
    if (uss.state==UserWork.STATE_CHAT) return; //���� ��� � ����
    String pass = "";
    if (uss.state==UserWork.STATE_NO_CHAT)
    {
    if(comNew.containsKey(uin))
    {
    try
    {
    room = Integer.parseInt(mmsg);
    }
    catch(NumberFormatException e)
    {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.1", new Object[] {uss.localnick}) + "\n");
    commandLRoom(proc,uin);
    return;
    }

    if (room==psp.getIntProperty("room.tyrma") & !srv.us.authorityCheck(uin,"room") && !psp.testAdmin(uin))
    {
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.2", new Object[] {uss.localnick}));
    return;
    }
    
    if (testClosed(uin)>1 & !srv.us.authorityCheck(uin,"room") & room!=psp.getIntProperty("room.tyrma"))
    {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.3", new Object[] {uss.localnick,psp.getIntProperty("room.tyrma"),srv.us.getRoom(psp.getIntProperty("room.tyrma")).getName()}));
    return;
    }

    if( srv.us.getRoom( room ).getUser_id() != 0 && srv.us.getRoom( room ).getUser_id() != uss.clansman && !psp.testAdmin(uin) )
    {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.4", new Object[] {uss.localnick,srv.us.getRoom( room ).getName()}) );
    return;
    }

    if (!srv.us.getRoom(room).checkPass(pass) && !psp.testAdmin(uin))
    {
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.5", new Object[] {uss.localnick}));
    return;
    }
    
    if(!psp.testAdmin(uin)&& !srv.us.checkRoom(room))
    {
    proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.6", new Object[] {uss.localnick,room}));
    return;
    }
    room_in_chat = true;
    comNew.remove(uin);
    }
    //TODO ������ ������ ����� ������ � ���
    Set<Integer> rid = srv.us.getRooms();
    Integer[] rooms=(Integer[])rid.toArray(new Integer[0]);
    Arrays.sort(rooms);
    if(!room_in_chat)
    {
    String list = Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.7", new Object[] {psp.getStringProperty("chat.name")}) + "\n";
    for(Integer i:rooms)
    {
    int cnt=0;
    Enumeration<String> e = srv.cq.uq.keys();
    while(e.hasMoreElements())
    {
    String g = e.nextElement();
    Users us = srv.us.getUser(g);
    if(us.state==UserWork.STATE_CHAT && us.room==i)
    {
    cnt++;
    }
    }
    list += "[" + i + "] - " + srv.us.getRoom(i).getName() + " �" + cnt + "���.�\n";
    }
    proc.mq.add(uin, list + "\n----\n" + Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.8"));
    comNew.put(uin, new NewExtend(uin, mmsg, mmsg, v, 2*60000));
    return;
    }
    Log.info("Add contact " + uin);
    if(proc.isNoAuthUin(uin)) proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.9"), 2);
    proc.addContactList(uin);
    uss.state = UserWork.STATE_CHAT;
    uss.basesn = proc.baseUin;
    uss.room=room;
    srv.us.updateUser(uss);
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.10", new Object[] {uss.localnick}), uss.sn, uss.room);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.11", new Object[] {uss.localnick}));
    f = true;
    }
    if (uss.state==UserWork.STATE_OFFLINE)
    {
    uss.state = UserWork.STATE_CHAT;
    uss.room = room;
    uss.basesn = proc.baseUin;
    srv.us.updateUser(uss);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.11", new Object[] {uss.localnick}));
    if(psp.getBooleanProperty("chat.showChangeUserStatus"))
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.10", new Object[] {uss.localnick}), uss.sn, uss.room);
    }
    Log.talk(uss.localnick + " ����� � ���");
    srv.us.db.log(uss.id,uin,"STATE_IN",uss.localnick + " �����(�) � ���",uss.room);
    srv.us.db.event(uss.id, uin, "STATE_IN", 0, "", uss.localnick + " ����� � ���");
    srv.cq.addUser(uin,proc.baseUin, uss.room);
    if(f)
    {
    if(srv.us.getCurrUinUsers(uss.basesn)>psp.getIntProperty("chat.maxUserOnUin"))
    {
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.12"));
    String s = srv.us.getFreeUin();
    uss.basesn = s;
    srv.us.updateUser(uss);
    srv.cq.changeUser(uin, s);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandgoChat.11", new Object[] {s}));
    }
    }
    }
    
    
    /**
     * ����� �� ����
     * @param proc
     * @param uin
     */
    public void exitChat(IcqProtocol proc, String uin)
    {
    Users uss = srv.us.getUser(uin);
    if (uss.state==UserWork.STATE_CHAT || uss.state==UserWork.STATE_OFFLINE && !srv.us.authorityCheck(uss.id, "invisible"))
    {
    if(!psp.getBooleanProperty("chat.NoDelContactList"))
    {
    Log.info("Delete contact " + uin);
    proc.RemoveContactList(uin);
    }
    } else
    return; // ����� ��� � ���� - ���������� �������
    uss.state = UserWork.STATE_NO_CHAT;
    srv.us.updateUser(uss);
    Log.talk(uss.localnick + " ����(�) �� ����");
    srv.us.db.log(uss.id,uin,"STATE_OUT",uss.localnick + " ����(�) �� ����",uss.room);
    srv.us.db.event(uss.id, uin, "STATE_OUT", 0, "", uss.localnick + " ����(�) �� ����");
    srv.cq.addMsg(Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandexitChat.0", new Object[] {uss.localnick}), uss.sn, uss.room);
    proc.mq.add(uin,Messages.getInstance(srv.getName()).getString("ChatCommandProc.commandexitChat.1"));
    srv.cq.delUser(uin);
    }
    
    /**
     * ����� �������� ���� �����
     */
    public void changeBaseUin(String uin, String buin){
        Users u = srv.us.getUser(uin);
        u.basesn = buin;
        srv.us.updateUser(u);
        srv.cq.changeUser(uin, buin);
    }
    
    /**
     * ��������� �������� �� ������������ ������� �����. �������� ��� ��� �������������
     * @param proc
     * @param uin
     * @return ������, ���� ���� ������ �� ����
     */
    private boolean testFlood(IcqProtocol proc, String uin){
    	if(warnFlag.contains(uin)) warnFlag.remove(uin);
    	if(floodMap.containsKey(uin)){
    		if(floodMap.get(uin).getCount()>psp.getIntProperty("chat.floodCountLimit")){
    			akick(proc, uin);
    			return true;
    		}
    	}
    	if(floodMap2.containsKey(uin)) {
    		if(floodMap2.get(uin).getCount()>psp.getIntProperty("chat.floodCountLimit")){
    			akick(proc, uin);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * ��� � ������� � ���
     */
    public void lkick(IcqProtocol proc, String uin, String txt, int id) {
        kick(proc, uin);
        srv.us.db.log(srv.us.getUser(uin).id,uin,"KICK", txt,srv.us.getUser(uin).room);
        srv.us.db.event(srv.us.getUser(uin).id, uin, "KICK", id, "", txt);
    }
    
    /**
     * ��� � �������������� ������������ �������
     */
    public void akick(IcqProtocol proc, String uin, int user_id){
        int def = psp.getIntProperty("chat.defaultKickTime");
        int max = psp.getIntProperty("chat.maxKickTime");
        int i=def;
        if(statKick.containsKey(uin)){
        	int t = statKick.get(uin).len;
            i = t<max ? t*2 : def;
            i = i>max ? max : i;
        }
        tkick(proc, uin, i, user_id, "");
    }
    
    public void akick(IcqProtocol proc, String uin){
        akick(proc, uin, 0);
    }
    
    /**
     * ��� � ������������ �������
     */
    public void tkick(IcqProtocol proc, String uin, int t, int user_id, String r){
        Users uss = srv.us.getUser(uin);
        setKick(uin,t, user_id, r);
        Log.talk("kick user " + uin + " on " + t + " min.");
        if (srv.us.getUser(uin).state == UserWork.STATE_CHAT)
        if (psp.getBooleanProperty("chat.isShowKickReason")) {
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn,Messages.getInstance(srv.getName()).getString("ChatCommandProc.tkick.0", new Object[] {t,
        (user_id == 0 ? "" : srv.us.getUser(user_id).group),
        (user_id == 0 ? radm.NICK : srv.us.getUser(user_id).localnick)})
        + "\n"
        + (r.equals("") ? "" : (Messages.getInstance(srv.getName()).getString("ChatCommandProc.tkick.1", new Object[] {r}))));
        } 
        else
        {
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
        Log.talk("Kick user " + uin);
        
        if(srv.cq.testUser(uin)){
        proc.mq.add(uin, Messages.getInstance(srv.getName()).getString("ChatCommandProc.kick.0"));
        }
        exitChat(proc, uin);
    }
    
    public void kickAll(IcqProtocol proc, String uin) {
        Vector v = srv.us.getUsers(UserWork.STATE_CHAT);
        for(int i=0;i<v.size();i++){
            Users uss = (Users)v.get(i);
            if(!uss.sn.equalsIgnoreCase(uin)) kick(proc, uss.sn);
        }
        v = srv.us.getUsers(UserWork.STATE_OFFLINE);
        for(int i=0;i<v.size();i++){
            Users uss = (Users)v.get(i);
            uss.state=UserWork.STATE_NO_CHAT;
        }
    }
    
    public void ban(IcqProtocol proc, String uin, String adm_uin, String m) {
        Users uss = srv.us.getUser(uin);
        if(uss.state==UserWork.STATE_CHAT) kick(proc, uin);
        Log.talk("Ban user " + uin);
        srv.us.db.log(uss.id,uin,"BAN",m,uss.room);
        srv.us.db.event(uss.id, uin, "BAN", srv.us.getUser(adm_uin).id, adm_uin, m);
        uss.state=UserWork.STATE_BANNED;
        srv.us.updateUser(uss);
        // ������ �� ��
        Log.info("Delete contact " + uin);
        proc.RemoveContactList(uin);
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
            return false; //���� ��� ����� ������������
        }
    }
    
    public boolean isBan(String uin) {
        try{
            return (srv.us.getUser(uin).state==UserWork.STATE_BANNED);
        } catch (Exception ex) {
            return false; //���� ��� ����� ������������
        }
    }
    
    /**
     * ��������� ��������� � ����� - ������� ������ ��������� ������ ���� �����������
     */
    public void parseFloodNotice(String uin, String msg, IcqProtocol proc){
    	if(isBan(uin)) return;
        if(testKick(uin)>0) return;
        if(!srv.us.testUser(uin)) return; // ���� �� �������
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
     * ������� ������� �������������� ������ ��� ������ �� ����� ��� �����������
     * @return
     */
    public String getCaptcha(){
    	int i1 = radm.getRND(100);
    	int i2 = radm.getRND(15);
    	String s = intToString(i1) + " + " + intToString(i2) + "=" + (i1+i2);
    	return s;
    }
    
    /**
     * ����� ��������
     * @param i
     * @return
     */
    public String intToString(int k){
        String[] ss = {"����","����","���","���","������","����","�����","����","������","������","������",
                "�����������","����������","����������","������������","����������","�����������",
                "����������","������������","������������","��������","��������","�����","���������",
                "����������","���������","�����������","���������","���","������","������","���������",
                "�������","��������","�������","���������","���������","������"};
        String[] ss2 = {"����","���"};
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
                s += "������ ";
                break;
            case 2:
            case 3:
            case 4:
                s += "������ ";
                break;
            default:
                s += "����� ";
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

    //**************************************************************************
    // �������������� �������
    //**************************************************************************


       /**
       * ���������� ������������ �� �����
       * !�������� <id> <id> <id>
       * @author fraer72
       */

   public void BanRoom(IcqProtocol proc, String uin, Vector v, String mmsg)
        {
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "banroom")) return;
        int i1 = (Integer) v.get(0);
        int time = (Integer) v.get(1);
        String r = (String) v.get(2);
        long t = System.currentTimeMillis()+(time*60000);
        Users u = srv.us.getUser(i1);
        if (psp.testAdmin(u.sn) || qauth(proc, u.sn, "anti_banroom")) {proc.mq.add(uin, "�� �� ������ ������� ������������, �� ������� �����������");return;}
        if (u.id == 0)
        {
        proc.mq.add(uin, "������������ �� ������");
        return;
        }
        if (u.state != UserWork.STATE_CHAT)
        {
        proc.mq.add(uin, "����� ������������ ��� � ����.");
        return;
        }
        if (time == 0)
        {
        proc.mq.add(uin, "���������� ������� �����");
        return;
        }
        if (time > psp.getIntProperty("chat.defaultBanroomTime")) {
        proc.mq.add(uin, "������������ ����� �������� - " + psp.getIntProperty("chat.defaultBanroomTime") + " �����");
        return;
        }
        if (r.equals("") || r.equals(" "))
        {
        proc.mq.add(uin, "���������� �������� ������� ��������");
        return;
        }
        if (uin.equals(u.sn))
        {
        proc.mq.add(uin, "������ ��������� ������ ���� :)");
        return;
        }
        srv.cq.addMsg(u.localnick + "|" + u.id + "|" + " ������ � �������: " + srv.us.getRoom(psp.getIntProperty("room.tyrma")).getName() + "|" + psp.getIntProperty("room.tyrma") + "| �� " + time + " ����� �� �������: " + r + ", �������������: " + srv.us.getUser(uin).localnick, u.sn, u.room);
        String nick = u.localnick + "(���)";
        u.localnick = nick;
        srv.us.db.event(u.id, uin, "REG", 0, "", nick);
        u.room = psp.getIntProperty("room.tyrma");
        u.lastclosed = t;
        srv.us.revokeUser(u.id, "room");
        srv.us.updateUser(u);
        srv.cq.changeUserRoom(u.sn, psp.getIntProperty("room.tyrma"));
        srv.us.db.event(u.id, uin, "Banroom", 0, "", " ������ � �������: " + psp.getIntProperty("room.tyrma") + " �� " + time + " �����\n�������: " + r + ", �������������: " + srv.us.getUser(uin).localnick);
        srv.us.db.log(u.id, uin, "Banroom", u.localnick + "|" + u.id + "|" + " ������ � ������� " + srv.us.getRoom(psp.getIntProperty("room.tyrma")).getName() + "|" + psp.getIntProperty("room.tyrma") + "| �� " + time + " �����", u.room);
        srv.cq.addMsg(u.localnick + "|" + u.id + "|" + " ������ �� " + time + " ����� �� �������: " + r + ", �������������: " + srv.us.getUser(uin).localnick, u.sn, u.room);
        srv.getIcqProcess(u.basesn).mq.add(u.sn, "�� " + r + " �� ������ � ������� " + psp.getIntProperty("room.tyrma") + " �� " + time + " �����");
        proc.mq.add(uin, "������������ " + u.localnick + " ������� ������ � ������� " + psp.getIntProperty("room.tyrma"));
        }

       /**
       * ������ �������� �������������
       * !��������
       * @author ����
       */

       public void commandZakHist(IcqProtocol proc, String uin) {
       if (!isChat(proc, uin) && !psp.testAdmin(uin))
       {
       return;
       }
       if (!auth(proc, uin, "zakhist"))
       {
       return;
       }
       try
       {
       proc.mq.add(uin, "����� ������������� � ������: " + srv.us.statBanroomColCount() + srv.us.getZakHist());
       }
       catch (Exception ex)
       {
       ex.printStackTrace();
       proc.mq.add(uin, ex.getMessage());
       }
       }

       /**
       * ��������� ������ ������������ ��� ���
       */
        private void freedom(String uin)
        {
        Users u = srv.us.getUser(uin);
        if (u.state==srv.us.STATE_CHAT)
        {
        srv.cq.addMsg("������������ |" + u.id + "|" + u.localnick + " ��� ������� �� ����������.", "", psp.getIntProperty("room.tyrma"));
        srv.cq.addMsg("������������ |" + u.id + "|" + u.localnick + " ��� ������� �� ����������.", "", 0);
        }
        String nick = u.localnick.replace("(���)","");
        u.localnick = nick;
        srv.us.db.event(u.id, uin, "REG", 0, "", nick);
        srv.us.grantUser(u.id, "room");
        u.room=0;
        srv.cq.changeUserRoom(u.sn,0);
        srv.us.updateUser(u);
        srv.us.db.log(u.id,uin,"FREE","���(�) �������(�) �� ����������.",0);
        }

       /**
       * ��������� ������ ������������ ��� ���
       */

        private int testClosed(String sn)
        {
    	long tc = srv.us.getUser(sn).lastclosed;
    	long t = System.currentTimeMillis();
    	return tc>t ? (int)(tc-t)/60000 : 0;
        }

       /**
       * ��������� ����� �� ������������ �������� ����������
       */
        private int testModer(String sn)
        {
        //srv.us.updateUser(srv.us.getUser(uin);
    	long tc = srv.us.getUser(sn).lastMod;
    	long t = System.currentTimeMillis();
    	return tc>t ? (int)(tc-t)/60000 : 0;
        }

       /**
       * ����� �� ������������ �����
       * !����� <id> <id>
       * @author fraer72
       */
     public void ModerTime(IcqProtocol proc, String uin, Vector v, String mmsg)
        {
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
        if (!auth(proc, uin, "moder_time")) return;
        int i1 = (Integer) v.get(0);
        int time = (Integer) v.get(1);
        int day = time/**24*/;
        //int day = (int) (System.currentTimeMillis() + (1000 * 3600 * t));
        Users u = srv.us.getUser(i1);
        if (u.id == 0)
        {
        proc.mq.add(uin, "������������ �� ������");
        return;
        }
        if (u.state != UserWork.STATE_CHAT)
        {
        proc.mq.add(uin, "����� ������������ ��� � ����.");
        return;
        }
        if (time == 0)
        {
        proc.mq.add(uin, "���������� ������� �����");
        return;
        }
        setGrouptime(u.sn,day);
        srv.us.updateUser(u);
        srv.us.getUser(i1).group = "modertime";
        boolean moder = srv.us.setUserPropsValue(u.id, "group", "modertime") &&
        srv.us.setUserPropsValue(u.id, "grant", "") &&
        srv.us.setUserPropsValue(u.id, "revoke", "");
        srv.us.clearCashAuth(u.id);
        srv.getIcqProcess(u.basesn).mq.add(u.sn, "���� ��������� ����� ���� �� " + time + " (����)����");
        proc.mq.add(uin,"������������ " + u.localnick + " ������� ��������� ����� ���� �� " + time + " (����)����");
        }

       /**
        * @author mmaximm
        * ������������� ���� �������� ������
        * @param uin
        * @param day
        */
        public void setGrouptime(String uin, int day) {
        Users us = srv.us.getUser(uin);
        Date date;
        if (us.lastMod > System.currentTimeMillis()) {
        date = new Date(us.lastMod);
        } else {
        date = new Date();
        }
        date.setDate(date.getDate() + day);
        us.lastMod = date.getTime();
        srv.us.updateUser(us);
    }
       /**
       * ������ ���������� ��� ��������� ������� �������������
       * @author fraer72
       */
       private void ModerNoTime(IcqProtocol proc, String uin)
       {
       if (srv.us.getUser(uin).state == srv.us.STATE_CHAT)
       {
       srv.us.getUser(uin).group = "user";
       boolean moder = srv.us.setUserPropsValue(srv.us.getUser(uin).id, "group", "user") &&
       srv.us.setUserPropsValue(srv.us.getUser(uin).id, "grant", "") &&
       srv.us.setUserPropsValue(srv.us.getUser(uin).id, "revoke", "");
       srv.us.clearCashAuth(srv.us.getUser(uin).id);
       //srv.us.updateUser(srv.us.getUser(uin));
       proc.mq.add(uin,"����� ������ ������������� � ���� ���������");
       }
       }
       
     /**
     * ������ ������ c ������������ ��������� ����
     * @author fraer72
     */
        public String listModUsers(){
        String msg = "���c�� �������� �����������:\n";
        Integer i = 0;
        for(Users u:srv.us.getModList())
        {
        i = i + 1;
        msg += i + ") - |" + u.id + "|" + u.localnick + " - |�������� " + (new Date(u.lastKick)).toString() + ", �� " + (u.lastMod-System.currentTimeMillis())/(1000*3600*24) + " ����(�����)|\n";
        if(statMod.containsKey(u.sn))
        {
        msg += '\n';
        }
        }
        return msg;
        }




     /**
     * ���������
     * !���������
     */
    public void commandBytilochka(IcqProtocol proc, String uin, Vector v, String mmsg)
    {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))
    {
    return;
    }
    if((System.currentTimeMillis()-ButilochkaTime)<1000*psp.getIntProperty("time.igra.bytilochka")){proc.mq.add(uin, "������ ����� ��� �  " + psp.getIntProperty("time.igra.bytilochka") + " ������");return;}//�����
    try {
    int c = 0;
    String g = "";
    Users uss = srv.us.getUser(uin);
    Enumeration<String> e = srv.cq.uq.keys();
    while (e.hasMoreElements())
    {
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if (us.state == UserWork.STATE_CHAT)
    {
    if (us.room == uss.room) {
    g += us.id + ";";
    c++;
    }
    }
    }
    if(c==1){proc.mq.add(uin, "� ������� ��� �����, ������ ������");return;}//���� � ������� ��� �����
    String[] gg = g.split(";");
    int o = (int) ((Math.random() * gg.length));
    a = n();
    String A = srv.us.GetButilochka(a);
    Users u = srv.us.getUser(Integer.parseInt(gg[o]));
    if (uss.id == 0)
    {
    proc.mq.add(uin, "������������ �� ������");
    return;
    }
    if (uss.state != UserWork.STATE_CHAT)
    {
    proc.mq.add(uin, "����� ������������ ��� � ����.");
    return;
    }
    if (uss.room != psp.getIntProperty("room.igra.bytilochka"))
    {
    proc.mq.add(uin, "������ ����� ������ � " + psp.getIntProperty("room.igra.bytilochka") + " �������");
    return;
    }
    if (u.id == uss.id)
    {
    proc.mq.add(uin, uss.localnick + "|" + uss.id + "|" + " �� �� ������� ���");
    return;
    }
    // ��������� ���
    srv.cq.addMsg("���������>> ������������ " + uss.localnick + "|" + uss.id + "|" + " ������ " + A + " ������������(�) " + u.localnick + "|" + u.id + "|", uss.sn, uss.room);
    proc.mq.add(uin, "�� ������ " + A + " ������������(�) " + u.localnick + "|" + u.id + "|");
    ButilochkaTime = System.currentTimeMillis();
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    proc.mq.add(uin, "������ " + ex.getMessage());
    }
    }

    /**
     * ����� ��� ���������
     * !����� �����
     */
    public void commandFraza(IcqProtocol proc, String uin, Vector v)
    {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
    if(!auth(proc,uin, "fraza")) return;
    String sn = (String) v.get(0);
    int k = sn.length();
    if (sn.equals("") || sn.equals(" "))
    {
    proc.mq.add(uin, "�� ��� �� �� ������ ������ �����");
    return;
    }
    if (!(k > 50))
    {
    try{
    long z = srv.us.db.getLastIndex("butilochka");
    int p = (int) (z + 1)-1;
    srv.us.db.AddAButilochka(p, sn);
    proc.mq.add(uin, "����� ������� ���������� � ��\n���� � ��: "+p);
    }catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,"��� ���������� ����� �������� ������: " + ex.getMessage());}
    }
    else
    {
    proc.mq.add(uin, "������� ������� ����� (> 50). ����� �� ���������");
    }
    }

    public void commandAdmini(IcqProtocol proc, String uin) {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))
    {
    return;
    }
    if(!auth(proc,uin, "adm")) return;
    try
    {
    String list = "";
    Enumeration<String> e = srv.cq.uq.keys();
    while (e.hasMoreElements()) {
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if (us.state == UserWork.STATE_CHAT && !srv.us.getUserGroup(us.id).equals("user"))
    {
    list += "- " + us.id + " - " + us.localnick + " � |" + us.room + "| - [" + us.group + "]" + "\n";
    }
    }
    proc.mq.add(uin, "� ������ ������ � ���� online :\n��|���|�������\n" + list);
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    proc.mq.add(uin, "������ " + ex.getMessage());
    }
    }

    /**
    * !chnick
    */
    public void commandchnick(IcqProtocol proc, String uin, Vector v, String mmsg)
    {
    if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!auth(proc,uin, "chnick")) return;
    try{
	int i = (Integer)v.get(0);
    String nick = (String)v.get(1);
    int len = nick.length();
	Users u = srv.us.getUser(i);
    Users uss = srv.us.getUser(uin);
    if (psp.testAdmin(u.sn) || qauth(proc, u.sn, "anti_chnick")) {proc.mq.add(uin, "�� �� ������ ������� ��� ������������, �� ������� �����������");return;}
	if(u.id==0)
    {
	proc.mq.add(uin,"������������ �� ������");
	return;
	}
    if (uin.equals(u.sn))
    {
    proc.mq.add(uin, "��� ����� ������ ���� ��������� !��� <���>");
    return;
    }
    if (!(len>psp.getIntProperty("max.chnick")))
    {
	String oldNick = u.localnick;
	u.localnick=nick;
	srv.us.updateUser(u);
	srv.us.db.event(u.id, uin, "REG", 0, "", nick);
	if(u.state==UserWork.STATE_CHAT){
	srv.cq.addMsg("� ������������ " + oldNick + " ��� ������� �� " + nick+ " ������� ��� ������������ " +uss.localnick, "", u.room);
	}
	proc.mq.add(uin,"��� ������� �������");
    } else proc.mq.add(uin,"������� ������� ���(>"+ psp.getIntProperty("max.chnick")+" ). ��� �� ������.");
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    proc.mq.add(uin,"������ "+ex.getMessage());
    }
    }

     /**
     * �������� �������
     * !�������� <id>
     */

        public void commandOchkiplys(IcqProtocol proc, String uin, Vector v)
        {
        if (!isChat(proc, uin) && !psp.testAdmin(uin)) {
        return;
        }
        try
        {
        int i = (Integer) v.get(0);
        Users u = srv.us.getUser(i);
        Users us = srv.us.getUser(uin);
        if (u.id == 0)
        {
        proc.mq.add(uin, "������������ �� ������");
        return;
        }
        if (us.id == i)
        {
        proc.mq.add(uin, "�������� ������� ���� ���� ������");
        return;
        }
        if (srv.us.getCountgolosChange(us.id, u.id) >= 1)
        {
        proc.mq.add(uin, "�� ������ ������ ��� � ����� ���������� �� ������ � ���� �� ������������");
        return;
        }
        int reiting = u.ball + 1;
        u.ball = reiting;
        srv.us.updateUser(u);
        srv.us.db.event(us.id, uin, "GOLOS", u.id, u.sn, "��� ���");
        proc.mq.add(uin, "������� ������������ " + u.localnick + "|" + u.id + "| �������");
        srv.getIcqProcess(u.basesn).mq.add(u.sn, "������������ " + us.localnick + "|" + us.id + "| ������� ���� �������, �� ���������� " + u.ball + " ������");
        } 
        catch (Exception ex)
        {
        ex.printStackTrace();
        proc.mq.add(uin, "������ " + ex.getMessage());
        }
        }

     /**
     * �������� �������
     * !�������� <id>
     */

        public void commandOchkiminys(IcqProtocol proc, String uin, Vector v)
        {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))
        {
        return;
        }
        try
        {
        int i = (Integer) v.get(0);
        Users u = srv.us.getUser(i);
        Users us = srv.us.getUser(uin);
        if (u.id == 0)
        {
        proc.mq.add(uin, "������������ �� ������");
        return;
        }
        if (us.id == i)
        {
        proc.mq.add(uin, "�������� ������� ���� ���� ������");
        return;
        }
        if (srv.us.getCountgolosChange(us.id, u.id) >= 1)
        {
        proc.mq.add(uin, "�� ������ ������ ��� � ����� ���������� �� ������ � ���� �� ������������");
        return;
        }
        int reiting = u.ball - 1;
        u.ball = reiting;
        srv.us.updateUser(u);
        srv.us.db.event(us.id, uin, "GOLOS", u.id, u.sn, "���� ���");
        proc.mq.add(uin, "������� ������������ " + u.localnick + "|" + u.id + "| �������");
        srv.getIcqProcess(u.basesn).mq.add(u.sn, "������������ " + us.localnick + "|" + us.id + "| ������� ���� �������, �� ���������� " + u.ball + " ������");
        } 
        catch (Exception ex)
        {
        ex.printStackTrace();
        proc.mq.add(uin, "������ " + ex.getMessage());
        }
        }

       /**
       * ��������� ������ �� �������
       * !������ <pass>
       * @author jimbot
       */

       public void commandSetpass(IcqProtocol proc, String uin, Vector v)
        {
        if(!auth(proc,uin, "setpass")) return;
        String s = (String)v.get(0);
        int room = srv.us.getUser(uin).room;
        Rooms r = srv.us.getRoom(room);
        r.setPass(s);
        srv.us.saveRoom(r, s);
        Log.info("���������� ������ �� ������� " + room + ": " + s);
        proc.mq.add(uin,"������ "+s+" �� ������� ������� ����������.");
        }

       /**
       * ������ ����� ���������
       * !�������
       * @author fraer72
       */
       public void commandAdmList(IcqProtocol proc, String uin)
       {
       if (!isChat(proc, uin) && !psp.testAdmin(uin)) return;
       if(!auth(proc,uin, "admlist")) return;
       try
       {
       proc.mq.add(uin, srv.us.getVseAdmMsg());
       }catch (Exception ex){ex.printStackTrace();proc.mq.add(uin, "������ " + ex.getMessage());}
       }

    /**
     * ����� ��� ����� ����
     */
    public void commandRobMsg(IcqProtocol proc, String uin, Vector v)
    {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))return;
    if(!auth(proc,uin, "robmsg")) return;
    String sn = (String) v.get(0);
    int k = sn.length();
    if (sn.equals("") || sn.equals(" "))
    {
    proc.mq.add(uin, "�� ��� �� �� ������ ������ �����");
    return;
    }
    if (!(k > 250))
    {
    try{
    long z = srv.us.db.getLastIndex("robadmin");
    int p = (int) (z + 1)-1;
    radm.AddAdmin(p,sn);
    proc.mq.add(uin, "����� ������� ���������� � ��\n���� � ��: "+p);
    }catch (Exception ex){ex.printStackTrace();proc.mq.add(uin,"��� ���������� ����� �������� ������: " + ex.getMessage());}
    }
    else
    {
    proc.mq.add(uin, "������� ������� ����� (> 250). ����� �� ���������");
    }
    }


   public void commandXst(IcqProtocol proc, String uin, Vector v){
   if(!isChat(proc,uin) && !psp.testAdmin(uin)) return;
   if(!auth(proc,uin, "xst")) return;
   int nomer = (Integer)v.get(0);
   if (nomer == 0)
   {
   icq.ListStatus(proc, uin);
   return;
   }
   String text = (String)v.get(1);
   if(text.equals("") || text.equals(" "))
   {
   proc.mq.add(uin,"����� ������� �����������");
   return;
   }  
   psp.setIntProperty("icq.xstatus", nomer);
   psp.setStringProperty("icq.STATUS_MESSAGE2", text);
   Manager.getInstance().getService(srv.getName()).getProps().save();
   if (nomer >= 1 && nomer <= 34){
   try {
   for(int uins = 0; uins < srv.con.uins.count(); uins++)
   {
   srv.con.uins.proc.get(uins).setXStatus(nomer, text);
   }
   proc.mq.add(uin,"������ ���� ������ �������");
   } 
   catch (Exception ex)
   {
   ex.printStackTrace();
   proc.mq.add(uin,"��������� ������ ��� ����� �������");
   }
   }
   else
   {
   proc.mq.add(uin,"����� ������ ���� �� 1 �� 34");
   }
   }


  public void commandRestart(IcqProtocol proc, String uin, Vector v, String mmsg)
  {
  if (!isChat(proc, uin) && !psp.testAdmin(uin))
  {
  return;
  }
  if(!auth(proc,uin, "restart")) return;
  Manager.restart();
  }

    /**
    * ��������� �������
    * !������ <�����>
    * @author fraer72
    */
    public void commandStatus(IcqProtocol proc, String uin, Vector v) {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))
    {
    return;
    }
    if (!auth(proc, uin, "status_user"))
    {
    return;
    }
    try {
    String lstatus = (String) v.get(0);
    int len = lstatus.length();
    if (lstatus.equals(""))
    {
    Users uss = srv.us.getUser(uin);
    srv.cq.addMsg(uss.localnick + "|" + uss.id + "|" + " ����� ������", uss.sn, uss.room);
    Log.talk(uss.localnick + "|" + uss.id + "|" + " ����� ������");
    proc.mq.add(uin, "�� ������ ������");
    uss.status  = "";
    srv.us.updateUser(uss);
    return;
    }
    if (len > psp.getIntProperty("about.user.st"))
    {
    proc.mq.add(uin, "������� ������� ������ (> "+psp.getIntProperty("about.user.st") +"). ������ �� ������.");
    return;
    }
    Users uss = srv.us.getUser(uin);
    srv.cq.addMsg(uss.localnick + "|" + uss.id + "|" + " ������ ������ �� |" + lstatus + "|", uss.sn, uss.room);
    Log.talk(uss.localnick + "|" + uss.id + "|" + " ������ ������ �� |" + lstatus + "|");
    uss.status  = lstatus;
    srv.us.updateUser(uss);
    proc.mq.add(uin, "�� ������� ������ �� |" + lstatus + "|");
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    Log.talk("Error save msg: " + ex.getMessage());
    proc.mq.add(uin, "������ ��������� ���� " + ex.getMessage());
    }
    }

     /**
     * ����� ����������� �������������
     * !����������
     */
    public void commandUbanHist(IcqProtocol proc, String uin) {
    if (!isChat(proc, uin) && !psp.testAdmin(uin))
    {
    return;
    }
    if (!auth(proc, uin, "ubanhist"))
    {
    return;
    }
    try {
    proc.mq.add(uin, srv.us.getUbanHist());
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    proc.mq.add(uin, ex.getMessage());
    }
    }

       /**
       * �������� ������ ��� ������������ ����
       * !������� <id>
       * @author ����
       */

      public void commandDeleteRoom(IcqProtocol proc, String uin, Vector v)
        {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))
        {
        return;
        }
        if (!auth(proc, uin, "wroom"))
        {
        return;
        }
        int room = (Integer) v.get(0);
        Users uss = srv.us.getUser(uin);
        if (!srv.us.checkRoom(room))
        {
        proc.mq.add(uin, "����� ������� �� ����������!");
        return;
        }
        srv.cq.addMsg("���� ������� ������� " + srv.us.getRoom(room).getName() + "|" + room + "| ������������� " + uss.localnick + "|" + uss.id + "|", uin, uss.room);
        //srv.cq.addMsg("���� ������� ������� " + srv.us.getRoom(room).getName() + "|" + room + "| ������������� " + uss.localnick + "|" + uss.id + "|", uin, 0);
        proc.mq.add(uin, "������� " + room + " ���� ������� �������");
        Rooms r = new Rooms();
        r.setId(room);
        srv.us.deleteRoom(r);
        }

        /**
        *  !������������ <id <msg>
        *  ����������� � ��� �� ���
        *  @author fraer72
        */
        public void commandInvitation_ID(IcqProtocol proc, String uin, Vector v, String tmsg) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))
        {
        return;
        }
        if (!auth(proc, uin, "invitation"))
        {
        return;
        }
        try{
        int i = (Integer)v.get(0);
        String s = (String)v.get(1);
        Users us = srv.us.getUser(uin);
        Users uss = srv.us.getUser(i);
        if(uss.id == 0)
        {
        proc.mq.add(uin,"������ ������������ �� ����������");
        return;
        }
        if (uss.state==UserWork.STATE_CHAT)
        {
        proc.mq.add(uin,"������������ ��� ����� � ����!");
        return;
        }
        if (s.length() > psp.getIntProperty("chat.MaxMsgSize"))
        {
        s = s.substring(0, psp.getIntProperty("chat.MaxMsgSize"));
        proc.mq.add(uin, "������� ������� ��������� ���� ��������: " + s);
        }
        if(s.equals(""))
        {
        proc.mq.add(uin,srv.us.getUser(uin).localnick  + " ���������� �������� ���������!\n������: !������� <id> ����� � ���");
        return;
        }
        //TODO: ������ ������ ������ ������������ ���������� null.... ��� ���������� ��� �������
            /*if(psp.getBooleanProperty("Priglashenie.on.off"))
            {
            String ss = psp.getStringProperty("chat.Priglashenie");
            String[] sss = ss.split(";");
            for (int i1=0;i1<sss.length;i1++)
            {
            Users usss = srv.us.getUser(sss[i1]);
            srv.getIcqProcess(usss.basesn).mq.add(usss.sn,"������������ " + uss.localnick +  "|" + uss.id + "|" + " ����� � ��� " + srv.us.getUser(uin).localnick + "|" + srv.us.getUser(uin).id + "| "
            + "\n ���������: "+s);
            }
            }*/
        srv.us.db.event(uss.id, uin, "PRIG", us.id, us.sn, "������ � ���");
        srv.us.db.log(uss.id,uin,"PRIG",">> ��� ����� � ��� " + srv.us.getUser(uin).localnick + " |" + srv.us.getUser(uin).id + "|",uss.room);
        srv.getIcqProcess(uss.basesn).mq.add(uss.sn,"������������ " + srv.us.getUser(uin).localnick + " |" + srv.us.getUser(uin).id + "| ����� ��� � ��� " + " � ��� �� ���� ���������: " + s);
        proc.mq.add(uin,"�� ������� � ��� ������������ " + uss.localnick + " |" + uss.id + "|");
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        proc.mq.add(uin,"������ "+ex.getMessage());
        }
        }

        /*
         *  !������������� <uin> <msg>
         *  ����������� � ��� �� ����
         *  @author fraer72
         */
        public void commandInvitation_UIN(IcqProtocol proc, String uin, Vector v, String tmsg) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))
        {
        return;
        }
        if (!auth(proc, uin, "invitation"))
        {
        return;
        }
        try{
        String uins = (String)v.get(0);
        String s = (String)v.get(1);
        Users uss = srv.us.getUser(uins);
        Users us = srv.us.getUser(uin);
        if(s.equals(""))
        {
        proc.mq.add(uin,srv.us.getUser(uin).localnick  + " ���������� �������� ���������!\n������: !���������� <uin> ����� � ���");
        return;
        }
        if (radm.testMat1(radm.changeChar(s)))
        {
        proc.mq.add(uin,"� ��������� ''���''");
        return;
        }
        if (s.length() > psp.getIntProperty("chat.MaxMsgSize"))
        {
        s = s.substring(0, psp.getIntProperty("chat.MaxMsgSize"));
        proc.mq.add(uin, "������� ������� ��������� ���� ��������: " + s);
        }
        //TODO: ������ ������ ������ ������������ ���������� null.... ��� ���������� ��� �������
            /*if(psp.getBooleanProperty("Priglashenie.on.off"))
            {
            String ss = psp.getStringProperty("chat.Priglashenie");
            String[] sss = ss.split(";");
            for (int i=0;i<sss.length;i++)
            {
            Users usss = srv.us.getUser(sss[i]);
            srv.getIcqProcess(usss.basesn).mq.add(usss.sn,"���������� ����������� � ��� �� ��� " + uins+ " �� ������������ " + srv.us.getUser(uin).localnick + "|" + srv.us.getUser(uin).id + "| "
            + "\n ���������: "+s);
            }
            }*/
        srv.us.db.event(uss.id, uin, "PRIG", us.id, us.sn, "������ � ���");
        proc.mq.add(uins,"������������ " + srv.us.getUser(uin).localnick + " |" + srv.us.getUser(uin).id + "| ���������� ��� � ��� " + " � ��� �� ���� ���������: " + s);
        proc.mq.add(uin,"����������� �� ���: "+uins+"  ����������");
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        proc.mq.add(uin,"������ "+ex.getMessage());
        }
        }

        /*
         * �������� ��������� �� ��� �������
         * !����� <text>
         * @author Sushka
         */
        public void commandSend(IcqProtocol proc, String uin, Vector v) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))
        {
        return;
        }
        if (!auth(proc, uin, "allroom_message"))
        {
        return;
        }
        try {
        String smsg = (String) v.get(0);
        if (smsg.equals("") || smsg.equals(" "))
        {
        return;
        }
        if (radm.testMat1(radm.changeChar(smsg)))
        {
        proc.mq.add(uin,"� ��������� ''���''");
        return;
        }
        Set<Integer> rid = new HashSet();
        Enumeration<String> e = srv.cq.uq.keys();
        while (e.hasMoreElements())
        {
        String i = e.nextElement();
        Users us = srv.us.getUser(i);
        if (us.state == UserWork.STATE_CHAT)
        {
        rid.add(us.room);
        }
        }
        for (int i : rid)
        {
        srv.cq.addMsg("{����� ���������: " + smsg + "}", uin, i);
        }
        proc.mq.add(uin, "��������� ����������� �������");
        } 
        catch (Exception ex)
        {
        ex.printStackTrace();
        proc.mq.add(uin, "������ - " + ex.getMessage());
        }
        }

        /**
         * ������� ������ ��� ���������
         * @param proc
         * @param uin
         */

        public void commandDelAdmMsg(IcqProtocol proc, String uin) {
        if (!isChat(proc, uin) && !psp.testAdmin(uin))
        {
        return;
        }
        if (!auth(proc, uin, "deladmmsg"))
        {
        return;
        }
        srv.us.db.executeQuery( " TRUNCATE `admmsg` " );
        proc.mq.add(uin, "������� \"admmsg\" �������" );
        }

        }
