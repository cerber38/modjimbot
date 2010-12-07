/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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
private ChatServer srv;
private ChatProps psp;

public frends (ChatServer srv, ChatProps psp){
parser = new CommandParser(commands);
this.srv = srv;
this.psp = psp;
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
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users us = srv.us.getUser(uin);
Users u = srv.us.getUser(id);
if(id==us.id){
proc.mq.add(uin,us.localnick +" нельзя создавать заявку самому себе");
return;
}
if((getCountDemand(us.id, id) > 1 || getCountDemand(us.id, id) == 1)){
proc.mq.add(uin,us.localnick + " вы уже создали заявку на дружбу с пользователем "
+ u.localnick + ", ждите её расмотрения");
return;
}
if((getCountDemand2(us.id, id) > 1 || getCountDemand2(us.id, id) == 1)){
proc.mq.add(uin,us.localnick + " пользователь " + u.localnick + " уже создал заявку на дружбу с вами." +
"\nВоспользуйтесь командой !заявки что бы просматреть её");
return;
}
if(getCountFrends(us.id, id) >= 1 || getCountFrends(id, us.id) >= 1){
proc.mq.add(uin,us.localnick + " пользователь " + u.localnick + " уже находится у тебя в друзьях");
return;
}
DemandBd((int)srv.us.db.getLastIndex("demand"), us.id, id, "D"+id);
proc.mq.add(uin,us.localnick + " вы успешно создали заявку, ждите когда пользователь "
+ u.localnick + " расмотрит её");
if(u.state==UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " у вас 1 новая заявка.");
}
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin,"При создании заявки возникла ошибка - "+ex.getMessage());
}
}

private void commandDemands(IcqProtocol proc, String uin){
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
try {
Users us = srv.us.getUser(uin);
proc.mq.add(uin,commandListDemand(us.id));
} catch (Exception ex) {ex.printStackTrace();
proc.mq.add(uin,ex.getMessage());
}
}

private void commandAllFrends(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
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
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = srv.us.getUser(id);
Users us = srv.us.getUser(uin);
if(getCountDemand2(us.id, id) == 0){
proc.mq.add(uin,us.localnick + " такой заявки не существует.");
return;
}
FrendsBd((int)srv.us.db.getLastIndex("frends"), id, us.id, "F"+us.id);
FrendsBd(((int)srv.us.db.getLastIndex("frends")+1), us.id, id, "F"+id);
srv.us.db.executeQuery("DELETE FROM demand WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,"Пользователь " + u.localnick + " успешно добавлен в друзья");
if(u.state==UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + us.localnick + " расмотрел заявку и добавил тебя к себе в друзья");
}
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin,"При подтверждении заявки возникла ошибка - "+ex.getMessage());
}
}

/*
 * Откланение заявок
 */

private void commandNot_To_Confirm(IcqProtocol proc, String uin, Vector v) {
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = srv.us.getUser(id);
Users us = srv.us.getUser(uin);
if(getCountDemand2(us.id, id) == 0){
proc.mq.add(uin,us.localnick + " такой заявки не существует.");
return;
}
srv.us.db.executeQuery("DELETE FROM demand WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,us.localnick + " заявка " + id + " откланенна");
if(u.state==UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn,"Твоя заявка полюзователю " + us.localnick + ", надобавление в друзбя, отклонена");
}
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin,"При отклонении заявки возникла ошибка - "+ex.getMessage());
}
}

/*
 * Удаление друга
 */

private void commandDelFrends(IcqProtocol proc, String uin, Vector v) {
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users u = srv.us.getUser(id);
Users us = srv.us.getUser(uin);
if(u.id==0){
proc.mq.add(uin,us.localnick + " пользователь не найден");
return;
}
if(getCountFrends(us.id, id) == 0){
proc.mq.add(uin,us.localnick + " пользователь " + u.localnick + " не находится в ваших друзьях");
return;
}
srv.us.db.executeQuery("DELETE FROM frends WHERE frend_id=" + id + " and user_id="+us.id);
srv.us.db.executeQuery("DELETE FROM frends WHERE frend_id=" + us.id + " and user_id="+id);
proc.mq.add(uin,"Пользователь " + u.localnick + " успешно удален из друзей");
if(u.state==UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn,"Пользователь " + us.localnick + " удалил тебя из друзей");
}
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin,"При удалении друга возникла ошибка - "+ex.getMessage());
}
}

/*
 * Листинг друзей
 */
public String commandListFrends(int id) {
Users u = srv.us.getUser(id);
String list = "Все друзья пользователя " + u.localnick +
"Ид|Ник|Рейтинг\n";
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select frend_id from frends WHERE user_id=" + id);
ResultSet rs = pst.executeQuery();
while(rs.next()){
list += "|" + srv.us.getUser(rs.getInt(1)).id + "|" + srv.us.getUser(rs.getInt(1)).localnick +  " » " + "|" + srv.us.getUser(rs.getInt(1)).ball + "|" + '\n';
}
rs.close();
pst.close();
}catch (Exception ex){
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
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select user_id from demand WHERE frend_id=" + id);
ResultSet rs = pst.executeQuery();
while(rs.next()){
list += "|" + srv.us.getUser(rs.getInt(1)).id + "|" + srv.us.getUser(rs.getInt(1)).localnick + '\n';
}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
list += "Команда !подтвердить <id(Заявки)> для подтвержедения заявки";
list += "\nКоманда !отклонить <id(Заявки)> для подтвержедения заявки";
return list;
}


public void FrendsBd(int id, int user_id, int frend_id, String type) {
try {
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into frends values(?, ?, ?, ?)");
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
        
public void DemandBd(int id, int user_id, int frend_id, String type) {
try {
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into demand values(?, ?, ?, ?)");
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
public int getCountFrends(int id, int id2){
String q = "SELECT count(*) FROM `frends` WHERE user_id="+id+" and type='F"+id2+"'";
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * Проверка на повторную заявку
 */
public int getCountDemand(int id, int id2){
String q = "SELECT count(*) FROM `demand` WHERE user_id="+id+" and frend_id="+id2+" and type='D"+id2+"'";
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * Проверка на повторную заявку
 */
public int getCountDemand2(int id, int id2){
String q = "SELECT count(*) FROM `demand` WHERE user_id="+id2+" and frend_id="+id+" and type='D"+id+"'";
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/*
 * Максимальное количество друзей
 */
public int MaxFrends(int id){
String q = "SELECT count(*) frend_id FROM `frends` WHERE user_id="+id;
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/**
 * Рандомный вывод 6-ти друзей
 * @param id
 * @return
 */
public String Random_Frends(int id)
{
Users u = srv.us.getUser(id);
if(MaxFrends(u.id) == 0) return "Нет друзей";
String frends = "Шесть случайных друзей пользователя:\n";
frends += "Всего друзей |" + MaxFrends(u.id) + "|\n";
frends += "Ид|Ник|Рейтинг\n";
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select frend_id from frends WHERE user_id=" + id + " ORDER BY RAND( ) LIMIT 0 , 6");
ResultSet rs = pst.executeQuery();
while(rs.next()){
frends += "|" + srv.us.getUser(rs.getInt(1)).id + "|" + srv.us.getUser(rs.getInt(1)).localnick +  " » " + "|" + srv.us.getUser(rs.getInt(1)).ball + "|" + '\n';
}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
return frends + "\nЧто бы просмотреть всех друзей набери !аллдруг <id>";
}
}