/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandExtend;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author fraer72
 * Магазин чата, для смысла викторины)
 */

public class Shop {
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private HashMap<String, CommandExtend> ComShop;
private CommandParser parser;
private ChatServer srv;
private ChatProps psp;


public Shop(ChatServer srv, ChatProps psp){
parser = new CommandParser(commands);
this.srv = srv;
this.psp = psp;
ComShop = new HashMap<String, CommandExtend>();
init();
}



private void init(){
commands.put("!магазин", new Cmd("!магазин","",1));
commands.put("!учет", new Cmd("!учет","",2));
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
if(ComShop.containsKey(uin))
if(!ComShop.get(uin).isExpire())
tp = parser.parseCommand(ComShop.get(uin).getCmd());
else {
tp = parser.parseCommand(tmsg);
ComShop.remove(uin);
}else
tp = parser.parseCommand(tmsg);
int tst=0;
if(tp<0)
tst=0;
else
tst = tp;
boolean f = true;
switch (tst){
case 1:
CommandShop(proc, uin, parser.parseArgs(tmsg), mmsg);
break;
case 2:
commandShophist(proc, uin);
break;
default:
f = false;
}
return f;
}


    private void CommandShop(IcqProtocol proc, String uin, Vector v, String mmsg){
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    Users uss = srv.us.getUser(uin);
    int i = 0;
    boolean twoPart = false;
    if(ComShop.containsKey(uin))
    {
    try{
    i = Integer.parseInt(mmsg);
    }catch(NumberFormatException e){
    proc.mq.add(uin, "Укажите номер покупки\nДля выхода выберите |0|");
    return;
    }
    twoPart = true;
    ComShop.remove(uin);
    }
    if(!twoPart){
    Spisok(proc, uin);
    ComShop.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
    return;
    }
    switch(i){
        case 0:
        Exit(proc, uin);
        break;
        case 1:
        if(psp.getBooleanProperty("Spisok.Chnick.on.off")) Chnick(proc, uin);
        break;
        case 2:
        if(psp.getBooleanProperty("Spisok.Settheme.on.off")) Settheme(proc, uin);
        break;
        case 3:
        if(psp.getBooleanProperty("Spisok.Who.on.off")) Who(proc, uin);
        break;
        case 4:
        if(psp.getBooleanProperty("Spisok.Kickhist.on.off")) Kickhist(proc, uin);
        break;
        case 5:
        if(psp.getBooleanProperty("Spisok.Anyroom.on.off")) Anyroom(proc, uin);
        break;
        case 6:
        if(psp.getBooleanProperty("Spisok.Kickone.on.off")) Kickone(proc, uin);
        break;
        case 7:
        if(psp.getBooleanProperty("Spisok.Banroom.on.off")) Banroom(proc, uin);
        break;
        case 8:
        if(psp.getBooleanProperty("Spisok.Status.on.off")) Status(proc, uin);
        break;
        case 9:
        if(psp.getBooleanProperty("Spisok.Moder.on.off")) Moder(proc, uin);
        break;
        case 10:
        if(psp.getBooleanProperty("Spisok.Admin.on.off")) Admin(proc, uin);
        break;
        case 11:
        if(psp.getBooleanProperty("Spisok.Modertime.on.off")) ModerTime(proc, uin);
        break;
        case 12:
        if(psp.getBooleanProperty("Spisok.PersonalRoom.on.off")) PersonalRoom(proc, uin);
        break;
    }
    }


    public void commandShophist(IcqProtocol proc, String uin){
    if(!((ChatCommandProc)srv.cmd).auth(proc,uin, "shophist")) return;
    try {
    proc.mq.add(uin,getShop());
    } catch (Exception ex) {ex.printStackTrace();
    proc.mq.add(uin,ex.getMessage());
    }
    }



public void Spisok (IcqProtocol proc, String uin){
String Spisok = "";
Spisok += "Барыга8-): здравствуйте я могу вам предложить...";
Spisok += "\nПолномочия:";
if(psp.getBooleanProperty("Spisok.Chnick.on.off"))
Spisok += "\n|1| - Полномочие ''chnick'', цена - " + psp.getIntProperty("ball.grant.1") + " балов";
if(psp.getBooleanProperty("Spisok.Settheme.on.off"))
Spisok += "\n|2| - Полномочие ''settheme'', цена - " + psp.getIntProperty("ball.grant.2") + " балов";
if(psp.getBooleanProperty("Spisok.Who.on.off"))
Spisok += "\n|3| - Полномочие ''who'', цена - " + psp.getIntProperty("ball.grant.3") + " балов";
if(psp.getBooleanProperty("Spisok.Kickhist.on.off"))
Spisok += "\n|4| - Полномочие ''kickhist'', цена - " + psp.getIntProperty("ball.grant.4") + " балов";
if(psp.getBooleanProperty("Spisok.Anyroom.on.off"))
Spisok += "\n|5| - Полномочие ''anyroom'', цена - " + psp.getIntProperty("ball.grant.5") + " балов";
if(psp.getBooleanProperty("Spisok.Kickone.on.off"))
Spisok += "\n|6| - Полномочие ''kickone'', цена - " + psp.getIntProperty("ball.grant.6") + " балов";
if(psp.getBooleanProperty("Spisok.Banroom.on.off"))
Spisok += "\n|7| - Полномочие ''banroom'', цена - " + psp.getIntProperty("ball.grant.7") + " балов";
if(psp.getBooleanProperty("Spisok.Status.on.off"))
Spisok += "\n|8| - Полномочие ''status_user'', цена - " + psp.getIntProperty("ball.grant.10") + " ч$";
Spisok += "\nГруппы:";
if(psp.getBooleanProperty("Spisok.Moder.on.off"))
Spisok += "\n|9| - Группа ''moder'' цена - " + psp.getIntProperty("ball.grant.8") + " балов";
if(psp.getBooleanProperty("Spisok.Admin.on.off"))
Spisok += "\n|10| - Группа ''admin'', цена - " + psp.getIntProperty("ball.grant.9") + " балов";
if(psp.getBooleanProperty("Spisok.Modertime.on.off"))
Spisok += "\n|11| - Группа ''modertime'' на " + psp.getIntProperty("Spisok.Modertime.Day") +
" день(дней) , цена - " + psp.getIntProperty("ball.grant.11") + " балов";
Spisok += "\nДругое:";
Spisok += "\n|12| - Личная комната, цена - " + psp.getIntProperty("ball.grant.12") + " балов";
Spisok += "\nДля выхода из магазина выберете |0|";
Spisok += "\nВыберете цифру для покупки";
proc.mq.add(uin, Spisok);
}

public void Exit(IcqProtocol proc, String uin){
proc.mq.add(uin, "Вы вышли из магазина");return;
}

public void Chnick(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball < psp.getIntProperty("ball.grant.1")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " +psp.getIntProperty("ball.grant.1") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "chnick"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''chnick''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.1");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " балл(ов)");
}

public void Settheme(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.2")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.2") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "settheme"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''settheme''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.2");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
}

public void Who(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.3")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.3") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "whouser"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''whouser''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.3");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
}

public void Kickhist(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.4")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.4") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "kickhist"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''kickhist''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.4");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
}

public void Anyroom(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.5")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.5") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "anyroom"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''anyroom''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.5");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " балл(ов)");
}

public void Kickone(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.6")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.6") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "kickone"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''kickone''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.6");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
}

public void Banroom(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.7")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.7") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "banroom"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''banroom''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.7");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
}

public void Moder(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.8")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.8") + " балов");
return;
}
int id = srv.us.getUser(uin).id;
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.8");
srv.us.updateUser(srv.us.getUser(uin));
boolean k = srv.us.setUserPropsValue(id, "group", "moder") &&
srv.us.setUserPropsValue(id, "grant", "") &&
srv.us.setUserPropsValue(id, "revoke", "");
srv.us.clearCashAuth(id);
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) группу ''moder''");
if(k)
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
else
proc.mq.add(uin,"Произошла ошибка");
}

public void Admin(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.9")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.9") + " балов");
return;
}
int id = srv.us.getUser(uin).id;
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.9");
srv.us.updateUser(srv.us.getUser(uin));
boolean kk = srv.us.setUserPropsValue(id, "group", "admin") &&
srv.us.setUserPropsValue(id, "grant", "") &&
srv.us.setUserPropsValue(id, "revoke", "");
srv.us.clearCashAuth(id);
srv.us.db.event(srv.us.getUser(uin).id, "", "SHOP", 0, "", "Купил(а) группу ''admin''");
if(kk)
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
else
proc.mq.add(uin,"Произошла ошибка");
}

public void Status(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.10")){
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + psp.getIntProperty("ball.grant.10") + " балов");
return;
}
if(srv.us.grantUser(srv.us.getUser(uin).id, "status_user"))
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''status_user''");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.10");
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
}

public void ModerTime(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.11")){
proc.mq.add(uin, "Для получения этой группы Вам необходимо набрать " + psp.getIntProperty("ball.grant.11") + " балов");
return;
}
int id = srv.us.getUser(uin).id;
boolean kk = srv.us.setUserPropsValue(id, "group", "moder") &&
srv.us.setUserPropsValue(id, "grant", "") &&
srv.us.setUserPropsValue(id, "revoke", "");
srv.us.clearCashAuth(id);
((ChatCommandProc)srv.cmd).setGrouptime(uin, psp.getIntProperty("Spisok.Modertime.Day"));
if(kk)
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
else
proc.mq.add(uin,"Произошла ошибка");
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) группу ''moder'' на время");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.11");
srv.us.updateUser(srv.us.getUser(uin));
}

public void PersonalRoom(IcqProtocol proc, String uin){
if(srv.us.getUser(uin).ball<psp.getIntProperty("ball.grant.12")){
proc.mq.add(uin, "Для получения личной комнаты необходимо набрать " + psp.getIntProperty("ball.grant.12") + " балов");
return;
}
int room = (int)srv.us.db.getLastIndex("rooms");
Rooms r = srv.us.getRoom(room);
r.setName("Личная комната: " + srv.us.getUser(uin).localnick);
r.setTopic("Твоя личная комната :)");
r.setPersonal(room);
srv.us.createRoom(r);
srv.us.getUser(uin).personal_room = room;
srv.us.updateUser(srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, ваша личная комната - " + room
+ " \nУ вас осталось " + srv.us.getUser(uin).ball + " бал(ов)");
srv.us.db.event(srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил личную комнату");
srv.us.getUser(uin).ball=srv.us.getUser(uin).ball-psp.getIntProperty("ball.grant.12");
srv.us.updateUser(srv.us.getUser(uin));
}

     /**
     * Вывод истории покупок
     */

    public String getShop(){
    String s="Покупки в магазине:\n ";
    try{
    PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select time, user_id, user_id2, msg from events where type='SHOP' order by time desc");
    ResultSet rs = pst.executeQuery();
    for(int i = 1; i < 21; i++){
    if(rs.next()){
    s += i + ". - |" + rs.getTimestamp(1) + "|  Пользователь - |"+ srv.us.getUser(rs.getInt(2)).id
    + "|" + srv.us.getUser(rs.getInt(2)).localnick +  ", " + rs.getString(4)+'\n';
    }else{
    break;
    }
    }
    rs.close();
    pst.close();
    }catch (Exception ex){
    ex.printStackTrace();
    }
    return s;
    }

}