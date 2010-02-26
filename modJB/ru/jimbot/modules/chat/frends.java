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
 * Друзья в чате :)
 */


public class frends {
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private CommandParser parser;
private ChatCommandProc cmd;
private Random r = new Random();
private String R0 = "Шесть случайных друзей пользователя ";
private String R1 = "\n";
private String R2 = "Что бы просматреть всех друзей набери !аллдруг <id>";


public frends(ChatCommandProc c)
{
parser = new CommandParser(commands);
cmd = c;
init();
}

private void init()
{
commands.put("!делдруг", new Cmd("!делдруг","$n",1));// удаление друга
commands.put("!заявка", new Cmd("!заявка","$n",2));// подать заявку на добавление в друзья
commands.put("!подтвердить", new Cmd("!подтвердить","$n",3));// подтвердить заявку
commands.put("!отклонить", new Cmd("!отклонить","$n",4));// отклонить заявку
commands.put("!заявки", new Cmd("!заявки","",5));// вывод листинга всех заявок
commands.put("!аллдруг", new Cmd("!аллдруг","$n",6));// вывод всех друзей
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
 * Метод для создания заявки
 * !заявка <id>
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
proc.mq.add(uin,us.localnick +" нельзя создавать заявку самому себе");
return;
}
if((getCountDemand(us.id, id) > 1 || getCountDemand(us.id, id) == 1))
{
proc.mq.add(uin,us.localnick + " вы уже создали заявку на дружбу с пользователем "
+ u.localnick + ", ждите её расмотрения");
return;
}
if((getCountDemand2(us.id, id) > 1 || getCountDemand2(us.id, id) == 1))
{
proc.mq.add(uin,us.localnick + " пользователь " + u.localnick + " уже создал заявку на дружбу с вами." +
"\nВоспользуйтесь командой !заявки что бы просматреть её");
return;
}
if(getCountFrends(us.id, id) > 1 || getCountFrends(id, us.id) > 1)
{
proc.mq.add(uin,us.localnick + " пользователь " + u.localnick + " уже находится у тебя в друзьях");
return;
}
DemandBd(max_id, us.id, id, "D"+id);
proc.mq.add(uin,us.localnick + " вы успешно создали заявку, ждите когда пользователь "
+ u.localnick + " расмотрит её");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " у вас 1 новая заявка.");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При создании заявки возникла ошибка - "+ex.getMessage());
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
 * Продтверждение заявок
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
proc.mq.add(uin,us.localnick + " такой заявки не существует.");
return;
}
FrendsBd(max_id, id, us.id, "F"+us.id);
FrendsBd((max_id+1), us.id, id, "F"+id);
cmd.srv.us.db.executeQuery("DELETE FROM demand WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,"Пользователь " + u.localnick + " успешно добавлен в друзья");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + us.localnick + " расмотрел заявку и добавил тебя к себе в друзья");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При подтверждении заявки возникла ошибка - "+ex.getMessage());
}
}

/*
 * Откланение заявок
 */

private void commandNot_To_Confirm(IcqProtocol proc, String uin, Vector v) {
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = cmd.srv.us.getUser(id);
Users us = cmd.srv.us.getUser(uin);
if(getCountDemand2(us.id, id) == 0)
{
proc.mq.add(uin,us.localnick + " такой заявки не существует.");
return;
}
cmd.srv.us.db.executeQuery("DELETE FROM demand WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,us.localnick + " заявка " + id + " откланенна");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"Твоя заявка полюзователю " + us.localnick + ", надобавление в друзбя, отклонена");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При отклонении заявки возникла ошибка - "+ex.getMessage());
}
}

/*
 * Удаление друга
 */

private void commandDelFrends(IcqProtocol proc, String uin, Vector v) {
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = cmd.srv.us.getUser(id);
Users us = cmd.srv.us.getUser(uin);
if(u.id==0)
{
proc.mq.add(uin,us.localnick + " пользователь не найден");
return;
}
if(getCountFrends(us.id, id) == 0)
{
proc.mq.add(uin,us.localnick + " пользователь " + u.localnick + " не находится в ваших друзьях");
return;
}
cmd.srv.us.db.executeQuery("DELETE FROM frends WHERE frend_id=" + id + " and user_id="+us.id);
cmd.srv.us.db.executeQuery("DELETE FROM frends WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,"Пользователь " + u.localnick + " успешно удален из друзей");
if(u.state==UserWork.STATE_CHAT){
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + us.localnick + " удалил тебя из друзей");
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При удалении друга возникла ошибка - "+ex.getMessage());
}
}

/*
 * Листинг друзей
 */
public String commandListFrends(int id) {
Users u = cmd.srv.us.getUser(id);
String list = "Все друзья пользователя " + u.localnick +
"Ид|Ник|Рейтинг\n";
try{
PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select frend_id from frends WHERE user_id=" + id);
ResultSet rs = pst.executeQuery();
while(rs.next())
{
list += "|" + cmd.srv.us.getUser(rs.getInt(1)).id + "|" + cmd.srv.us.getUser(rs.getInt(1)).localnick +  " » " + "|" + cmd.srv.us.getUser(rs.getInt(1)).ball + "|" + '\n';
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
 * Листинг заяваок
 */
public String commandListDemand(int id) {
String list = "Заявки:\nId(Заявки)|Nick|\n";
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
list += "Команда !подтвердить <id(Заявки)> для подтвержедения заявки";
list += "\nКоманда !отклонить <id(Заявки)> для подтвержедения заявки";
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
 * Проверка пользователь уже друг?
 */
public int getCountFrends(int id, int id2)
{
String q = "SELECT count(*) FROM `frends` WHERE user_id="+id+" and type='F"+id2+"'";
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * Проверка на повторную заявку
 */
public int getCountDemand(int id, int id2)
{
String q = "SELECT count(*) FROM `demand` WHERE user_id="+id+" and frend_id="+id2+" and type='D"+id2+"'";
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * Проверка на повторную заявку
 */
public int getCountDemand2(int id, int id2)
{
String q = "SELECT count(*) FROM `demand` WHERE user_id="+id2+" and frend_id="+id+" and type='D"+id+"'";
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * Максимальное количество друзей
 */
public int MaxFrends(int id)
{
String q = "SELECT count(*) frend_id FROM `frends` WHERE user_id="+id;
Vector<String[]> v = cmd.srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * Метод который показывает определенное количество друзей,
 * его следует вызывать из других методов
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
if(list.equals("")){return frends;}//Если нет друзей
Users u = cmd.srv.us.getUser(id);
frends += "Друзья пользователя:\n";
frends += "Всего друзей |" + MaxFrends(u.id) + "|\n";
frends += "Ид|Ник|Рейтинг\n";
String[] IdsFrends = list.split(",");
List<String> List = Arrays.asList(IdsFrends);
Collections.shuffle(List);//Тусуем список друзей в случайном порядке
for(int f = 0; f<(IdsFrends.length); f++)
{
if(f>5){return R0  + u.localnick + R1 + frends + R2;}//Выводим шесть случайных
int L = Integer.valueOf(List.get(f));
frends += "|" + cmd.srv.us.getUser(L).id + "|" + cmd.srv.us.getUser(L).localnick +  " » " + "|" + cmd.srv.us.getUser(L).ball + "|" + '\n';
}
return frends;
}
}