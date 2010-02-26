

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author Fraer72
 * Информация о пользователе :)
 */
public class AboutUser {
private int order  = 0;
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private CommandParser parser;
private ChatCommandProc cmd;
private HashMap<String, InfoMap> InfoMap;
private String CMD = "";
private long TIME = 5*60000;
private String[][] chg = {{"y","у"},{"Y","у"},{"k","к"},{"K","к"},{"e","е"},
                            {"E","е"},{"h","н"},{"H","н"},{"r","г"},{"3","з"},{"x","х"},{"X","х"},
                            {"b","в"},{"B","в"},{"a","а"},{"A","а"},{"p","р"},{"P","р"},{"c","с"},
                            {"C","с"},{"6","б"}};


public AboutUser(ChatCommandProc c)
{
parser = new CommandParser(commands);
cmd = c;
InfoMap = new HashMap<String, InfoMap>();
init();
}


private void init()
{
commands.put("!данные", new Cmd("!данные","",1));
commands.put("!личное", new Cmd("!личное","$n",2));
}


public boolean commandAboutUser(IcqProtocol proc, String uin, String mmsg) {
String tmsg = mmsg.trim();
int tp = 0;
/////
if(InfoMap.containsKey(uin))
if(!InfoMap.get(uin).isExpire())
tp = parser.parseCommand(InfoMap.get(uin).getCmd());
else {
tp = parser.parseCommand(tmsg);
order = 0;
CMD = "";
InfoMap.remove(uin);
}else
tp = parser.parseCommand(tmsg);
/////
int tst=0;
if(tp<0)
tst=0;
else
tst = tp;
boolean f = true;
switch (tst)
{
case 1:
commandAboutUser(proc, uin, parser.parseArgs(tmsg), mmsg);
break;
case 2:
commandLichUser(proc, uin, parser.parseArgs(tmsg));
break;
default:
f = false;
}
return f;
}


private void commandLichUser(IcqProtocol proc, String uin, Vector v) {
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try
{
int id = (Integer)v.get(0);
Users u = cmd.srv.us.getUser(id);
String List = "Личная информация о пользователе |" + u.localnick + "|" + '\n';
List += "------\n";
List += "Дата регистрации - " + (u.data==0 ? "Не указанна" : (("|" + new Date(u.data)).toString() + "|")) + '\n';
List += "Группа - " + u.group + '\n';
List += "Рейтинг - " + u.ball + '\n';
List += "Имя - " + u.lname + '\n';
List += "Пол - " + u.homepage + '\n';
List += "Возраст - " + u.age + '\n';
List += "Город - " + u.city + '\n';
if( u.clansman != 0 )
{
List += (  u.id != cmd.srv.us.getClan( u.clansman ).getLeader() ? "Состаит в клане - ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" : ( "Лидер клана - ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" ) ) + '\n';
}
else
{
List += "В клане не состаит" + '\n';
}
List += "Правельных ответов в викторине - " + u.answer + '\n';
List += "------\n";
List += cmd.gift.commandListGiftUser_5(id);
List += cmd.frends.Random_Frends(id);
proc.mq.add(uin,List);
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При вызове личной информации возникла ошибка - "+ex.getMessage());
}
}
private void commandAboutUser(IcqProtocol proc, String uin, Vector v, String mmsg){
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try
{
if(order == 0)
{
NAME(proc, uin, parser.parseArgs(mmsg), mmsg);
}
if(order == 1)
{
AGE(proc, uin, parser.parseArgs(mmsg), mmsg);
}
if(order == 2)
{
FLOOR(proc, uin, parser.parseArgs(mmsg), mmsg);
}
if(order == 3)
{
CITY(proc, uin, parser.parseArgs(mmsg), mmsg);
}
if(order == 4)
{
proc.mq.add(uin,cmd.srv.us.getUser(uin).lname + " информация успешно заполнена," +
" просматреть ее ты можешь командой !личное <id>");
order = 0;
CMD = "";
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При заполнении информации возникла ошибка!" +
"\nВозможно вы неправельно заполняеете информацию\n" +
"\nПовторите попытку :)");
order = 0;
CMD = "";
}
}


private void NAME(IcqProtocol proc, String uin, Vector v, String mmsg)
{
Users uss = cmd.srv.us.getUser(uin);
String msg = "";
boolean NAME = false;
if(InfoMap.containsKey(uin))
{
try
{
msg = mmsg;
}
catch(NumberFormatException e)
{
proc.mq.add(uin,uss.localnick + " введите ваше имя");
return;
}
if (Name(TestMsg2(msg)).equals("errore"))
{
proc.mq.add(uin,"В имени запрещенный символы\nВведите правельно имя");
return;
}
if (testMat(changeChar(msg))){
proc.mq.add(uin,"В имени " + msg + " ''мат''\nВведите правельно имя");
return;
}
NAME = true;
InfoMap.remove(uin);
}
if(!NAME)
{
CMD = mmsg;
proc.mq.add(uin,"Введите ваше имя :)");
InfoMap.put(uin, new InfoMap(uin, CMD, mmsg,v, TIME));
return;
}
String Name = Name(TestMsg(msg));
if(Name.length()>cmd.psp.getIntProperty("about.user.long")){Name = Name.substring(0,cmd.psp.getIntProperty("about.user.long"));proc.mq.add(uin,"Имя было обрезанно: " + Name);}
uss.lname = Name;
cmd.srv.us.updateUser(uss);
order = 1;
}


private void AGE(IcqProtocol proc, String uin, Vector v, String mmsg)
{
Users uss = cmd.srv.us.getUser(uin);
int age = 0;
boolean AGE = false;
if(InfoMap.containsKey(uin))
{
try
{
age = Integer.parseInt(mmsg);
}
catch(NumberFormatException e)
{
proc.mq.add(uin,uss.lname + " введите ваш возраст");
return;
}
if(age < cmd.psp.getIntProperty("about.age.min") || age > cmd.psp.getIntProperty("about.age.max"))
{
proc.mq.add(uin,uss.lname + " неправельно указан возраст\n" +
"Он дожен быть не меньше ''" + cmd.psp.getIntProperty("about.age.min") + "'' и не больше ''" + cmd.psp.getIntProperty("about.age.max") + "'' лет");
return;
}
AGE = true;
InfoMap.remove(uin);
}
if(!AGE)
{
proc.mq.add(uin,"Отлично " + uss.lname + ", сколько тебе лет?");
InfoMap.put(uin, new InfoMap(uin, CMD, mmsg,v, TIME));
return;
}
uss.age = age;
cmd.srv.us.updateUser(uss);
order = 2;
}

private void FLOOR(IcqProtocol proc, String uin, Vector v, String mmsg)
{
Users uss = cmd.srv.us.getUser(uin);
String msg  = "";
boolean FLOOR = false;
if(InfoMap.containsKey(uin))
{
try
{
msg = mmsg;
}
catch(NumberFormatException e)
{
proc.mq.add(uin,uss.lname + " введите ваш пол");
return;
}
if (!TestFloor(msg))
{
proc.mq.add(uin,uss.lname + " пол должен быть ''ж'' или ''м''");
return;
}
FLOOR = true;
InfoMap.remove(uin);
}
if(!FLOOR)
{
proc.mq.add(uin,uss.lname + " возраст успешно указан, какой у тебя пол? 'м' или 'ж' ?");
InfoMap.put(uin, new InfoMap(uin, CMD, mmsg,v, TIME));
return;
}
uss.homepage = msg;
cmd.srv.us.updateUser(uss);
order = 3;
}

private void CITY(IcqProtocol proc, String uin, Vector v, String mmsg)
{
Users uss = cmd.srv.us.getUser(uin);
String msg = "";
boolean CITY = false;
if(InfoMap.containsKey(uin))
{
try
{
msg = mmsg;
}
catch(NumberFormatException e)
{
proc.mq.add(uin,uss.localnick + " введите ваш город");
return;
}
if (testMat(changeChar(msg))){
proc.mq.add(uin,uss.lname + "в городе " + msg + " ''мат''\nУкажите свой город правельно");
return;
}
if (City(TestMsg2(msg)).equals("errore"))
{
proc.mq.add(uin,"В городе запрещенный символы\nВведите правельно имя");
return;
}
CITY = true;
InfoMap.remove(uin);
}
if(!CITY)
{
proc.mq.add(uin,uss.lname + " прекрасно, пол указан, а теперь введите город где вы живете");
InfoMap.put(uin, new InfoMap(uin, CMD, mmsg,v, TIME));
return;
}
String Name = City(TestMsg(msg));
if(Name.length()>cmd.psp.getIntProperty("about.user.long")){Name = Name.substring(0,cmd.psp.getIntProperty("about.user.long"));proc.mq.add(uin,uss.lname + " длинное название города, обрезанно: " + Name);}
uss.city = Name;
cmd.srv.us.updateUser(uss);
order = 4;
}


public String Name(String name)
{
return name;
}

public String City(String city )
{
return city ;
}

public String TestMsg2(String Nick){
Nick = Nick.toLowerCase();
String H  = cmd.psp.getStringProperty("about.user.bad");
Nick = changeChar(Nick.toLowerCase());
for(int msg = 0;msg < H.length();msg++)
{
if(Nick.indexOf(H.charAt(msg))>=0)
{
Nick = "errore";
return Nick;//Если запрещенный символ
}
}
return Nick;
}


public String changeChar(String s)
{
for(int i=0;i<chg.length;i++){
s = s.replace(chg[i][0],chg[i][1]);
}
return s;
}

public boolean TestFloor(String msg)
{
String Floor = msg.toLowerCase();//опустим регистр
if(Floor.equals("м") || Floor.equals("ж"))//проверим
{
return true;//если пол указан верно
}
else
{
return false;//если пол указан неверно
}
}

public String TestMsg(String msg){
msg = msg.replace('\n',' ');
msg = msg.replace('\r',' ');
msg = msg.replace("0","");
msg = msg.replace("1","");
msg = msg.replace("2","");
msg = msg.replace("3","");
msg = msg.replace("4","");
msg = msg.replace("5","");
msg = msg.replace("6","");
msg = msg.replace("7","");
msg = msg.replace("8","");
msg = msg.replace("9","");
return msg;
}

public class InfoMap
{
	private String uin;
	private long vremia;
	private String msg;
	private String cmd;
	private Vector data;


	public InfoMap(String _uin, String _cmd, String _msg, Vector _data, long expire) {
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


    public boolean testMat(String msg){
    String[] s = msg.trim().split(" ");
    for(int i=0;i<s.length;i++){
    if(!test(s[i], ChatProps.getInstance(cmd.srv.getName()).getStringProperty("adm.noMatString").split(";"))){
    if(test(s[i], ChatProps.getInstance(cmd.srv.getName()).getStringProperty("adm.matString").split(";")))
    return true;
    }
    }
    return false;
    }

    public boolean test(String msg, String[] testStr)
    {
    for(int i=0;i<testStr.length;i++)
    {
    if(msg.toLowerCase().indexOf(testStr[i])>=0) return true;
    }
    return false;
    }



}
