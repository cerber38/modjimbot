/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;
import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author Fraer72
 * ������ � ���� :)
 */


public class frends {
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private CommandParser parser;
private ChatCommandProc cmd;
private Random r = new Random();
private String R0 = "����� ��������� ������ ������������ ";
private String R1 = "\n";
private String R2 = "��� �� ����������� ���� ������ ������ !������� <id>";


public frends(ChatCommandProc c)
{
parser = new CommandParser(commands);
cmd = c;
init();
}

private void init()
{
commands.put("!�������", new Cmd("!�������","$n",1));// �������� �����
commands.put("!������", new Cmd("!������","$n",2));// ������ ������ �� ���������� � ������
commands.put("!�����������", new Cmd("!�����������","$n",3));// ����������� ������
commands.put("!���������", new Cmd("!���������","$n",4));// ��������� ������
commands.put("!������", new Cmd("!������","",5));// ����� �������� ���� ������
commands.put("!�������", new Cmd("!�������","$n",6));// ����� ���� ������
}


public boolean commandFrends(IcqProtocol proc, String uin, String mmsg) {
String msg = mmsg.trim();
int tp = parser.parseCommand(msg);
int tst=0;
if(tp<0)
tst=0;
else
tst = tp;
boolean f = true;
switch (tst)
{
case 1:
commandDelFrends(proc, uin, parser.parseArgs(msg));
break;
case 2:
commandDemand(proc, uin, parser.parseArgs(msg));
break;
case 3:
commandConfirm(proc, uin, parser.parseArgs(msg));
break;
case 4:
commandNot_To_Confirm(proc, uin, parser.parseArgs(msg));
break;
case 5:
commandDemands (proc, uin);
break;
case 6:
commandAllFrends (proc, uin, parser.parseArgs(msg));
break;
default:
f = false;
}
return f;
}

/*
 * ����� ��� �������� ������
 * !������ <id>
 */

private void commandDemand(IcqProtocol proc, String uin, Vector v) {
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users us = cmd.srv.us.getUser(uin);
Users u = cmd.srv.us.getUser(id);
long z = cmd.srv.us.db.getLastIndex("demand");
int max_id = (int) z;
if(id==us.id){
proc.mq.add(uin,us.localnick +" ������ ��������� ������ ������ ����");
return;
}
if((getCountDemand(us.id, id) > 1 || getCountDemand(us.id, id) == 1))
{
proc.mq.add(uin,us.localnick + " �� ��� ������� ������ �� ������ � ������������� "
+ u.localnick + ", ����� � �����������");
return;
}
if((getCountDemand2(us.id, id) > 1 || getCountDemand2(us.id, id) == 1))
{
proc.mq.add(uin,us.localnick + " ������������ " + u.localnick + " ��� ������ ������ �� ������ � ����." +
"\n�������������� �������� !������ ��� �� ����������� �");
return;
}
if(getCountFrends(us.id, id) > 1 || getCountFrends(id, us.id) > 1)
{
proc.mq.add(uin,us.localnick + " ������������ " + u.localnick + " ��� ��������� � ���� � �������");
return;
}
DemandBd(max_id, us.id, id, "D"+id);
proc.mq.add(uin,us.localnick + " �� ������� ������� ������, ����� ����� ������������ "
+ u.localnick + " ��������� �");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " � ��� 1 ����� ������.");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"��� �������� ������ �������� ������ - "+ex.getMessage());
}
}

public void commandDemands(IcqProtocol proc, String uin)
{
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try {
Users us = cmd.srv.us.getUser(uin);
proc.mq.add(uin,commandListDemand(us.id));
} catch (Exception ex) {ex.printStackTrace();
proc.mq.add(uin,ex.getMessage());
}
}

public void commandAllFrends(IcqProtocol proc, String uin, Vector v)
{
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try {
int id = (Integer)v.get(0);
proc.mq.add(uin,commandListFrends(id));
} catch (Exception ex) {ex.printStackTrace();
proc.mq.add(uin,ex.getMessage());
}
}

/*
 * �������������� ������
 */

private void commandConfirm(IcqProtocol proc, String uin, Vector v) {
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = cmd.srv.us.getUser(id);
Users us = cmd.srv.us.getUser(uin);
long z = cmd.srv.us.db.getLastIndex("frends");
int max_id = (int) z;
if(getCountDemand2(us.id, id) == 0)
{
proc.mq.add(uin,us.localnick + " ����� ������ �� ����������.");
return;
}
FrendsBd(max_id, id, us.id, "F"+us.id);
FrendsBd((max_id+1), us.id, id, "F"+id);
cmd.srv.us.db.executeQuery("DELETE FROM demand WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,"������������ " + u.localnick + " ������� �������� � ������");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"������������ " + us.localnick + " ��������� ������ � ������� ���� � ���� � ������");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"��� ������������� ������ �������� ������ - "+ex.getMessage());
}
}

/*
 * ���������� ������
 */

private void commandNot_To_Confirm(IcqProtocol proc, String uin, Vector v) {
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = cmd.srv.us.getUser(id);
Users us = cmd.srv.us.getUser(uin);
if(getCountDemand2(us.id, id) == 0)
{
proc.mq.add(uin,us.localnick + " ����� ������ �� ����������.");
return;
}
cmd.srv.us.db.executeQuery("DELETE FROM demand WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,us.localnick + " ������ " + id + " ����������");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"���� ������ ������������ " + us.localnick + ", ������������ � ������, ���������");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"��� ���������� ������ �������� ������ - "+ex.getMessage());
}
}

/*
 * �������� �����
 */

private void commandDelFrends(IcqProtocol proc, String uin, Vector v) {
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = cmd.srv.us.getUser(id);
Users us = cmd.srv.us.getUser(uin);
if(u.id==0)
{
proc.mq.add(uin,us.localnick + " ������������ �� ������");
return;
}
if(getCountFrends(us.id, id) == 0)
{
proc.mq.add(uin,us.localnick + " ������������ " + u.localnick + " �� ��������� � ����� �������");
return;
}
cmd.srv.us.db.executeQuery("DELETE FROM frends WHERE frend_id=" + id + " and user_id="+us.id);
cmd.srv.us.db.executeQuery("DELETE FROM frends WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,"������������ " + u.localnick + " ������� ������ �� ������");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"������������ " + us.localnick + " ������ ���� �� ������");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"��� �������� ����� �������� ������ - "+ex.getMessage());
}
}

/*
 * ������� ������
 */
public String commandListFrends(int id) {
Users u = cmd.srv.us.getUser(id);
String list = "��� ������ ������������ " + u.localnick +
"��|���|�������\n";
try{
PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select frend_id from frends WHERE user_id=" + id);
ResultSet rs = pst.executeQuery();
while(rs.next())
{
list += "|" + cmd.srv.us.getUser(rs.getInt(1)).id + "|" + cmd.srv.us.getUser(rs.getInt(1)).localnick +  " � " + "|" + cmd.srv.us.getUser(rs.getInt(1)).ball + "|" + '\n';
}
rs.close();
pst.close();
}
catch (Exception ex)
{
ex.printStackTrace();
}
return list;
}

/*
 * ������� �������
 */
public String commandListDemand(int id) {
String list = "������:\nId(������)|Nick|\n";
try{
PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select user_id from demand WHERE frend_id=" + id);
ResultSet rs = pst.executeQuery();
while(rs.next())
{
list += "|" + cmd.srv.us.getUser(rs.getInt(1)).id + "|" + cmd.srv.us.getUser(rs.getInt(1)).localnick + '\n';
}
rs.close();
pst.close();
}
catch (Exception ex)
{
ex.printStackTrace();
}
list += "������� !����������� <id(������)> ��� �������������� ������";
list += "\n������� !��������� <id(������)> ��� �������������� ������";
return list;
}

//INSERT INTO `frends` VALUES (0, 0, 0, "")
public void FrendsBd(int id, int user_id, int frend_id, String type) {
try {
PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("insert into frends values(?, ?, ?, ?)");
pst.setInt(1,id);
pst.setInt(2,user_id);
pst.setInt(3,frend_id);
pst.setString(4,type);
pst.execute();
pst.close();
}
catch (Exception ex)
{
ex.printStackTrace();
}
}
        
//INSERT INTO `demand` VALUES (0, 0, "")
public void DemandBd(int id, int user_id, int frend_id, String type) {
try {
PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("insert into demand values(?, ?, ?, ?)");
pst.setInt(1,id);
pst.setInt(2,user_id);
pst.setInt(3,frend_id);
pst.setString(4,type);
pst.execute();
pst.close();
}
catch (Exception ex)
{
ex.printStackTrace();
}
}

/*
 * �������� ������������ ��� ����?
 */
public int getCountFrends(int id, int id2)
{
String q = "SELECT count(*) FROM `frends` WHERE user_id="+id+" and type='F"+id2+"'";
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * �������� �� ��������� ������
 */
public int getCountDemand(int id, int id2)
{
String q = "SELECT count(*) FROM `demand` WHERE user_id="+id+" and frend_id="+id2+" and type='D"+id2+"'";
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * �������� �� ��������� ������
 */
public int getCountDemand2(int id, int id2)
{
String q = "SELECT count(*) FROM `demand` WHERE user_id="+id2+" and frend_id="+id+" and type='D"+id+"'";
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * ������������ ���������� ������
 */
public int MaxFrends(int id)
{
String q = "SELECT count(*) frend_id FROM `frends` WHERE user_id="+id;
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * ����� ������� ���������� ������������ ���������� ������,
 * ��� ������� �������� �� ������ �������
 */
public String Random_Frends(int id)
{
String list = "";
try{
PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select frend_id from frends WHERE user_id=" + id);
ResultSet rs = pst.executeQuery();
while(rs.next()){list += rs.getInt(1)+",";}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();}
String frends = "";
if(list.equals("")){return frends;}//���� ��� ������
Users u = cmd.srv.us.getUser(id);
frends += "������ ������������:\n";
frends += "����� ������ |" + MaxFrends(u.id) + "|\n";
frends += "��|���|�������\n";
String[] IdsFrends = list.split(",");
List<String> List = Arrays.asList(IdsFrends);
Collections.shuffle(List);//������ ������ ������ � ��������� �������
for(int f = 0; f<(IdsFrends.length); f++)
{
if(f>5){return R0  + u.localnick + R1 + frends + R2;}//������� ����� ���������
int L = Integer.valueOf(List.get(f));
frends += "|" + cmd.srv.us.getUser(L).id + "|" + cmd.srv.us.getUser(L).localnick +  " � " + "|" + cmd.srv.us.getUser(L).ball + "|" + '\n';
}
return frends;
}
}