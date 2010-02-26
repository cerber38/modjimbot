

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
 * ���������� � ������������ :)
 */
public class AboutUser {
private int order  = 0;
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private CommandParser parser;
private ChatCommandProc cmd;
private HashMap<String, InfoMap> InfoMap;
private String CMD = "";
private long TIME = 5*60000;
private String[][] chg = {{"y","�"},{"Y","�"},{"k","�"},{"K","�"},{"e","�"},
                            {"E","�"},{"h","�"},{"H","�"},{"r","�"},{"3","�"},{"x","�"},{"X","�"},
                            {"b","�"},{"B","�"},{"a","�"},{"A","�"},{"p","�"},{"P","�"},{"c","�"},
                            {"C","�"},{"6","�"}};


public AboutUser(ChatCommandProc c)
{
parser = new CommandParser(commands);
cmd = c;
InfoMap = new HashMap<String, InfoMap>();
init();
}


private void init()
{
commands.put("!������", new Cmd("!������","",1));
commands.put("!������", new Cmd("!������","$n",2));
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
String List = "������ ���������� � ������������ |" + u.localnick + "|" + '\n';
List += "------\n";
List += "���� ����������� - " + (u.data==0 ? "�� ��������" : (("|" + new Date(u.data)).toString() + "|")) + '\n';
List += "������ - " + u.group + '\n';
List += "������� - " + u.ball + '\n';
List += "��� - " + u.lname + '\n';
List += "��� - " + u.homepage + '\n';
List += "������� - " + u.age + '\n';
List += "����� - " + u.city + '\n';
if( u.clansman != 0 )
{
List += (  u.id != cmd.srv.us.getClan( u.clansman ).getLeader() ? "������� � ����� - ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" : ( "����� ����� - ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" ) ) + '\n';
}
else
{
List += "� ����� �� �������" + '\n';
}
List += "���������� ������� � ��������� - " + u.answer + '\n';
List += "------\n";
List += cmd.gift.commandListGiftUser_5(id);
List += cmd.frends.Random_Frends(id);
proc.mq.add(uin,List);
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"��� ������ ������ ���������� �������� ������ - "+ex.getMessage());
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
proc.mq.add(uin,cmd.srv.us.getUser(uin).lname + " ���������� ������� ���������," +
" ����������� �� �� ������ �������� !������ <id>");
order = 0;
CMD = "";
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"��� ���������� ���������� �������� ������!" +
"\n�������� �� ����������� ����������� ����������\n" +
"\n��������� ������� :)");
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
proc.mq.add(uin,uss.localnick + " ������� ���� ���");
return;
}
if (Name(TestMsg2(msg)).equals("errore"))
{
proc.mq.add(uin,"� ����� ����������� �������\n������� ��������� ���");
return;
}
if (testMat(changeChar(msg))){
proc.mq.add(uin,"� ����� " + msg + " ''���''\n������� ��������� ���");
return;
}
NAME = true;
InfoMap.remove(uin);
}
if(!NAME)
{
CMD = mmsg;
proc.mq.add(uin,"������� ���� ��� :)");
InfoMap.put(uin, new InfoMap(uin, CMD, mmsg,v, TIME));
return;
}
String Name = Name(TestMsg(msg));
if(Name.length()>cmd.psp.getIntProperty("about.user.long")){Name = Name.substring(0,cmd.psp.getIntProperty("about.user.long"));proc.mq.add(uin,"��� ���� ���������: " + Name);}
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
proc.mq.add(uin,uss.lname + " ������� ��� �������");
return;
}
if(age < cmd.psp.getIntProperty("about.age.min") || age > cmd.psp.getIntProperty("about.age.max"))
{
proc.mq.add(uin,uss.lname + " ����������� ������ �������\n" +
"�� ����� ���� �� ������ ''" + cmd.psp.getIntProperty("about.age.min") + "'' � �� ������ ''" + cmd.psp.getIntProperty("about.age.max") + "'' ���");
return;
}
AGE = true;
InfoMap.remove(uin);
}
if(!AGE)
{
proc.mq.add(uin,"������� " + uss.lname + ", ������� ���� ���?");
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
proc.mq.add(uin,uss.lname + " ������� ��� ���");
return;
}
if (!TestFloor(msg))
{
proc.mq.add(uin,uss.lname + " ��� ������ ���� ''�'' ��� ''�''");
return;
}
FLOOR = true;
InfoMap.remove(uin);
}
if(!FLOOR)
{
proc.mq.add(uin,uss.lname + " ������� ������� ������, ����� � ���� ���? '�' ��� '�' ?");
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
proc.mq.add(uin,uss.localnick + " ������� ��� �����");
return;
}
if (testMat(changeChar(msg))){
proc.mq.add(uin,uss.lname + "� ������ " + msg + " ''���''\n������� ���� ����� ���������");
return;
}
if (City(TestMsg2(msg)).equals("errore"))
{
proc.mq.add(uin,"� ������ ����������� �������\n������� ��������� ���");
return;
}
CITY = true;
InfoMap.remove(uin);
}
if(!CITY)
{
proc.mq.add(uin,uss.lname + " ���������, ��� ������, � ������ ������� ����� ��� �� ������");
InfoMap.put(uin, new InfoMap(uin, CMD, mmsg,v, TIME));
return;
}
String Name = City(TestMsg(msg));
if(Name.length()>cmd.psp.getIntProperty("about.user.long")){Name = Name.substring(0,cmd.psp.getIntProperty("about.user.long"));proc.mq.add(uin,uss.lname + " ������� �������� ������, ���������: " + Name);}
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
return Nick;//���� ����������� ������
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
String Floor = msg.toLowerCase();//������� �������
if(Floor.equals("�") || Floor.equals("�"))//��������
{
return true;//���� ��� ������ �����
}
else
{
return false;//���� ��� ������ �������
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
