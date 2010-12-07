/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandExtend;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author Fraer72
 *
 * Подарки в чате :)
 */

public class Gift {
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private HashMap<String, CommandExtend> GiftMap;
private CommandParser parser;
private ChatServer srv;
private ChatProps psp;

public Gift(ChatServer srv, ChatProps psp){
parser = new CommandParser(commands);
this.srv = srv;
this.psp = psp;
GiftMap = new HashMap<String, CommandExtend>();
init();
}


private void init(){
commands.put("!ларек", new Cmd("!ларек","",1));
commands.put("!добподарок", new Cmd("!добподарок","$c $n",2));
commands.put("!вещи", new Cmd("!вещи","",3));
commands.put("!подарить", new Cmd("!подарить","$n $n $s",4));
commands.put("!всеподарки", new Cmd("!всеподарки","$n",5));
commands.put("!делподарок", new Cmd("!делподарок","$n",6));
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

public boolean commandShop(IcqProtocol proc, String uin, String mmsg) {
String tmsg = mmsg.trim();
int tp = 0;
if(GiftMap.containsKey(uin))
if(!GiftMap.get(uin).isExpire())
tp = parser.parseCommand(GiftMap.get(uin).getCmd());
else {
tp = parser.parseCommand(tmsg);
GiftMap.remove(uin);
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
TheListOfGifts(proc, uin, parser.parseArgs(tmsg), mmsg);
break;
case 2:
AddGifts(proc, uin, parser.parseArgs(tmsg));
break;
case 3:
ListThing(proc, uin);
break;
case 4:
AddGiftUser(proc, uin, parser.parseArgs(tmsg));
break;
case 5:
AllGifts(proc, uin, parser.parseArgs(tmsg));
break;
case 6:
DelGifts(proc, uin, parser.parseArgs(tmsg));
break;
default:
f = false;
}
return f;
}

    private void TheListOfGifts(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    Users uss = srv.us.getUser(uin);
    int i = 0;
    boolean GIFT = false;
    if (uss.state == UserWork.STATE_CHAT){
    if(GiftMap.containsKey(uin)){
    try{
    i = Integer.parseInt(mmsg);
    }catch(NumberFormatException e){
    proc.mq.add(uin,"Барыга8-): " + uss.localnick + " укажите номер покупки.\nДля выхода выберите |0|");
    return;
    }
    if(i != 0 && getCountGiftId(i) == 0){
    proc.mq.add(uin,"Барыга8-): " + uss.localnick + " нет такого падарка в продаже, выберете правельно.\nДля выхода выберите |0|");
    return;
    }
    if(uss.ball < (commandPrice(i))){
    proc.mq.add(uin,"Барыга8-): " + uss.localnick + " у вас нехватает " + ((commandPrice(i))-uss.ball) + " баллов для покупки");
    return;
    }
    GIFT = true;
    GiftMap.remove(uin);
    }
    if(!GIFT){
    proc.mq.add(uin,commandListGift());
    GiftMap.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
    return;
    }
    if(i == 0){
    proc.mq.add(uin,uss.localnick + " вы вышли из ларька");
    return;
    }
    ThingBd((int)srv.us.db.getLastIndex("thing"), uss.id, commandGift(i));
    uss.ball -= commandPrice(i);
    srv.us.updateUser(uss);
    proc.mq.add(uin,"Барыга8-): " + uss.localnick + " вы купили  - ''" + commandGift(i) + "''\n" +
    "У вас осталось " + uss.ball + " баллов.");
    }
    }


    private void ListThing(IcqProtocol proc, String uin) {
    try{
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    Users uss = srv.us.getUser(uin);
    proc.mq.add(uin, commandListThing(uss.id));
    }catch (Exception ex){
    ex.printStackTrace();
    proc.mq.add(uin,"При вызове листинга вещёй возникла ошибка - "+ex.getMessage());
    }
    }

    private void AddGifts(IcqProtocol proc, String uin, Vector v) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!((ChatCommandProc)srv.cmd).auth(proc,uin, "gift")) return;
    try{
    Users uss = srv.us.getUser(uin);
    String gift = (String)v.get(0);
    int price = (Integer)v.get(1);
    if(gift.equals("") || gift.equals(" "))
    {
    proc.mq.add(uin,uss.localnick + " вы неуказади название подарка для продажи");
    return;
    }
    if(price <= 0)
    {
    proc.mq.add(uin,uss.localnick + " цена не может быть '0' или меньше '0'");
    return;
    }
    if(((ChatCommandProc)srv.cmd).radm.testMat1(((ChatCommandProc)srv.cmd).radm.changeChar(gift))){
    proc.mq.add(uin,uss.localnick + " в названии подарка мат ''МАТ''");
    return;
    }
    if(gift.length() > 20){
    proc.mq.add(uin,uss.localnick + " название подарка слишком большое.");
    return;
    }
    GiftBd((int)srv.us.db.getLastIndex("gift"), gift, price);
    proc.mq.add(uin,uss.localnick + " вы успешно добавили новый подарок на витрину ларька.");
    }catch (Exception ex){
    ex.printStackTrace();
    proc.mq.add(uin,"При создании подарка возникла ошибка - "+ex.getMessage());
    }
    }


    private void AddGiftUser(IcqProtocol proc, String uin, Vector v) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    try{
    Users uss = srv.us.getUser(uin);
    long time = System.currentTimeMillis();
    int id_user = (Integer)v.get(0);
    int id_gift = (Integer)v.get(1);
    String text = (String)v.get(2);
    System.out.print((Integer)v.get(0) + " " + id_gift + " " + (String)v.get(2) + "\n");
    Users u = srv.us.getUser(id_user);
    if(id_user == uss.id){proc.mq.add(uin,uss.localnick + " вы не можите дарить подарки себе :)");return;}
    if(u == null){proc.mq.add(uin,uss.localnick + " такого пользователя не существует");return;}
    if(getCountThing(uss.id, id_gift) == 0){
    proc.mq.add(uin,uss.localnick + " и что вы собрались дарить, у вас нет такого подарка" +
    "\nЧто бы посматреть свои вещи набери !вещи");
    return;
    }
    if(text.equals("") || text.equals(" ")){
    proc.mq.add(uin,uss.localnick + " вы неуказали текст к подарку");
    return;
    }
    if(text.length() > 30){
    proc.mq.add(uin,uss.localnick + " текст к подарку сильно большой.");
    return;
    }
    if(((ChatCommandProc)srv.cmd).radm.testMat1(((ChatCommandProc)srv.cmd).radm.changeChar(text))){
    proc.mq.add(uin,uss.localnick + " в тексте к подарку мат ''МАТ''");
    return;
    }
    GiftUserBd((int)srv.us.db.getLastIndex("gift_user"), id_user, uss.id, commandThing(uss.id, id_gift), text, time);
    proc.mq.add(uin,uss.localnick + " вы успешно подарили пользователю " + u.localnick + " ''" + commandThing(uss.id, id_gift) + "''");
    srv.getIcqProcess(u.basesn).mq.add(u.sn,u.localnick + " пользователь " + uss.localnick + " подарил тебе ''" +
    commandThing(uss.id, id_gift) + "''\nСписок подаренных тебе подарков можно посмотреть командой !личное <id> или !всеподарки <id>");
    srv.cq.addMsg("Пользователь |" + uss.id + "|" + uss.localnick + " подарил пользователю |" +
    + u.id + "|" + u.localnick + "''" + commandThing(uss.id, id_gift) +  "''", "", u.room);
    srv.us.db.executeQuery("DELETE FROM thing WHERE id="+id_gift);
    }catch (Exception ex){
    ex.printStackTrace();
    proc.mq.add(uin,"Возникла ошибка - "+ex.getMessage());
    }
    }

    private void DelGifts(IcqProtocol proc, String uin, Vector v) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!((ChatCommandProc)srv.cmd).auth(proc,uin, "gift")) return;
    try{
    Users uss = srv.us.getUser(uin);
    int id = (Integer)v.get(0);
    if(getCountGiftId(id) == 0){
    proc.mq.add(uin,uss.localnick + " нет такого подарка");
    return;
    }    
    proc.mq.add(uin,uss.localnick + " подарок ''" + commandGift(id) + "'' успешно удален из списка в ларьке");
    srv.us.db.executeQuery("DELETE FROM gift WHERE id=" + id);
    }catch (Exception ex){
    ex.printStackTrace();
    proc.mq.add(uin,"При удалении подарка возникла ошибка - "+ex.getMessage());
    }
    }

    private void AllGifts(IcqProtocol proc, String uin, Vector v) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    try {
    int id = (Integer)v.get(0);
    proc.mq.add(uin,commandListGiftUser(id));
    } catch (Exception ex) {ex.printStackTrace();
    proc.mq.add(uin,ex.getMessage());
    }
    }

/*
 *
 * Настройки при продаже вещей
 *
 */

public String commandListGift() {
String list = "Барыга8-): здравствуйте я могу вам предложить...\n" +
"Номер|Название|Цена(баллов)\n";
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select id, gift, price from gift");
ResultSet rs = pst.executeQuery();
while(rs.next()){
list += "|" + rs.getInt(1) + "|" + rs.getString(2) +  " » " + rs.getInt(3) + "(баллов)" + '\n';
}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
list += "Для покупки выберети цифру\nДля выхода выберете |0|";
return list;
}

public String commandGift(int id) {
String gift = "";
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from gift where id=" + id);
ResultSet rs = pst.executeQuery();
if(rs.next()){gift += rs.getString(2);}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
return gift;
}

public int commandPrice(int id) {
int price = 0;
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from gift where id=" + id);
ResultSet rs = pst.executeQuery();
if(rs.next()){price += rs.getInt(3);}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
return price;
}

public int getCountGiftId(int id){
String q = "SELECT count(*) FROM `gift` WHERE id="+id;
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

public void GiftBd(int id, String gift, int price) {
try {
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into gift values(?, ?, ?)");
pst.setInt(1,id);
pst.setString(2,gift);
pst.setInt(3,price);
pst.execute();
pst.close();
}
catch (Exception ex)
{
ex.printStackTrace();
}
}

/*
 *
 * Настройки подарков
 *
 */

public String commandListThing(int id) {
String list = "Список ваших вещей:\n" +
"Id - Вещи|Название|\n";
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select id, thing from thing WHERE user_id="+id);
ResultSet rs = pst.executeQuery();
while(rs.next()){
list += "|" + rs.getInt(1) + "|" + rs.getString(2) + '\n';
}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
list += "Для того что бы сделать подарок наберите: !подарить <id-пользователя> <id-вещи>";
return list;
}

public String commandThing(int id, int id2) {
String thing = "";
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select user_id="+id+", thing from thing WHERE id=" + id2);
ResultSet rs = pst.executeQuery();
if(rs.next()){thing += rs.getString(2);}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
return thing;
}

public int getCountThing(int id, int id2){
String q = "SELECT count(*) FROM `thing` WHERE user_id="+id+" and id="+id2;
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

public void ThingBd(int id, int user_id, String thing) {
try {
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into thing values(?, ?, ?)");
pst.setInt(1,id);
pst.setInt(2,user_id);
pst.setString(3,thing);
pst.execute();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
}

/*
 *
 * Настройки подарков пользователя
 *
 */


public void GiftUserBd(int id, int user_id, int user_id2, String gift, String text, long time) {
try {
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into gift_user values(?, ?, ?, ?, ?, ?)");
pst.setInt(1,id);
pst.setInt(2,user_id);
pst.setInt(3,user_id2);
pst.setString(4,gift);
pst.setString(5,text);
pst.setTimestamp(6,new Timestamp(time));
pst.execute();
pst.close();
}
catch (Exception ex)
{
ex.printStackTrace();
}
}

public String commandListGiftUser(int id) {
String list = "Список всех подарков:\n" +
"№|Время|От кого|Подарок|Текст\n";
int i = 0;
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select time, user_id2, gift, text from gift_user WHERE user_id="+id);
ResultSet rs = pst.executeQuery();
while(rs.next()){
Users u = srv.us.getUser(rs.getInt(2));
i++;
list += i + ". - |"+rs.getTimestamp(1)+"| |" + u.id + "|" + u.localnick +  " » " + rs.getString(3) + " » " + rs.getString(4) + '\n';
}
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
return list;
}

public String commandListGiftUser_5(int id) {
Users us = srv.us.getUser(id);
String L = "";
int a = 0;
String list = "5-ка подарков пользователя\n" +
"Всего подарков:" + getCountGiftUser(us.id) + "\n" +
"№|От кого|Подарок|Текст\n";
try{
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select time, user_id2, gift, text from gift_user WHERE user_id="+id);
ResultSet rs = pst.executeQuery();
for(int i=1 ; i<6 ; i++){
if(rs.next()){
a++;
Users u = srv.us.getUser(rs.getInt(2));
L += a + ". - |" + u.id + "|" + u.localnick +  " » " + rs.getString(3) + " » " + rs.getString(4) + '\n';
} else {
break;
}
}
if(L.equals("")){
list = "";
return list;
}
list += L;
rs.close();
pst.close();
}catch (Exception ex){
ex.printStackTrace();
}
list += "!всеподарки <id> - что бы узнать все подарки пользователя\n------\n";
return list;
}

public int getCountGiftUser(int id){
String q = "SELECT count(*) FROM `gift_user` WHERE user_id="+id;
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}


}
