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
import ru.jimbot.modules.CommandParser;
import ru.jimbot.modules.ShopExtend;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author Fraer72
 * Магазин чата, для смысла викторины)
 */

public class Shop {
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
public HashMap<String, ShopExtend> ComShop;
private CommandParser parser;
private ChatCommandProc cmd;


public Shop(ChatCommandProc c)
{
parser = new CommandParser(commands);
cmd = c;
ComShop = new HashMap<String, ShopExtend>();
init();
}



private void init()
{
commands.put("!магазин", new Cmd("!магазин","",1));
commands.put("!учет", new Cmd("!учет","",2));
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
switch (tst)
{
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


    private void CommandShop(IcqProtocol proc, String uin, Vector v, String mmsg)
    {
    if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
    Users uss = cmd.srv.us.getUser(uin);
    int i = 0;
    boolean twoPart = false;
    if (uss.state == UserWork.STATE_CHAT)
    {
    if(ComShop.containsKey(uin))
    {
    if(ComShop.containsKey(uin))
    {
    try{
    i = Integer.parseInt(mmsg);
    }
    catch(NumberFormatException e)
    {
    proc.mq.add(uin, "Укажите номер покупки\nДля выхода выберите |0|");
    return;
    }
    twoPart = true;
    ComShop.remove(uin);
    }
    }
    if(!twoPart)
    {
    Spisok(proc, uin);
    ComShop.put(uin, new ShopExtend(uin, mmsg, mmsg,v, 2*60000));
    return;
    }
    if(i == 0){Exit(proc, uin);}
    if(i == 1 && cmd.psp.getBooleanProperty("Spisok.Chnick.on.off")){Chnick(proc, uin);}
    if(i == 2 && cmd.psp.getBooleanProperty("Spisok.Settheme.on.off")){Settheme(proc, uin);}
    if(i == 3 && cmd.psp.getBooleanProperty("Spisok.Who.on.off")){Who(proc, uin);}
    if(i == 4 && cmd.psp.getBooleanProperty("Spisok.Kickhist.on.off")){Kickhist(proc, uin);}
    if(i == 5 && cmd.psp.getBooleanProperty("Spisok.Anyroom.on.off")){Anyroom(proc, uin);}
    if(i == 6 && cmd.psp.getBooleanProperty("Spisok.Kickone.on.off")){Kickone(proc, uin);}
    if(i == 7 && cmd.psp.getBooleanProperty("Spisok.Banroom.on.off")){Banroom(proc, uin);}
    if(i == 8 && cmd.psp.getBooleanProperty("Spisok.Status.on.off")){Status(proc, uin);}
    if(i == 9 && cmd.psp.getBooleanProperty("Spisok.Moder.on.off")){Moder(proc, uin);}
    if(i == 10 && cmd.psp.getBooleanProperty("Spisok.Admin.on.off")){Admin(proc, uin);}
    if(i == 11 && cmd.psp.getBooleanProperty("Spisok.Modertime.on.off")){ModerTime(proc, uin);}
    }
    }


    public void commandShophist(IcqProtocol proc, String uin)
    {
    if(!cmd.auth(proc,uin, "shophist")) return;
    try {
    proc.mq.add(uin,getShop());
    } catch (Exception ex) {ex.printStackTrace();
    proc.mq.add(uin,ex.getMessage());
    }
    }



public void Spisok (IcqProtocol proc, String uin)
{
String Spisok = "";
Spisok += "Барыга8-): здравствуйте я могу вам предложить...";
Spisok += "\nПолномочия:";
if(cmd.psp.getBooleanProperty("Spisok.Chnick.on.off")){Spisok += "\n|1| - Полномочие ''chnick'', цена - " + cmd.psp.getIntProperty("ball.grant.1") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Settheme.on.off")){Spisok += "\n|2| - Полномочие ''settheme'', цена - " + cmd.psp.getIntProperty("ball.grant.2") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Who.on.off")){Spisok += "\n|3| - Полномочие ''who'', цена - " + cmd.psp.getIntProperty("ball.grant.3") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Kickhist.on.off")){Spisok += "\n|4| - Полномочие ''kickhist'', цена - " + cmd.psp.getIntProperty("ball.grant.4") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Anyroom.on.off")){Spisok += "\n|5| - Полномочие ''anyroom'', цена - " + cmd.psp.getIntProperty("ball.grant.5") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Kickone.on.off")){Spisok += "\n|6| - Полномочие ''kickone'', цена - " + cmd.psp.getIntProperty("ball.grant.6") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Banroom.on.off")){Spisok += "\n|7| - Полномочие ''banroom'', цена - " + cmd.psp.getIntProperty("ball.grant.7") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Status.on.off")){Spisok += "\n|8| - Полномочие ''status_user'', цена - " + cmd.psp.getIntProperty("ball.grant.10") + " ч$";}
Spisok += "\nГруппы:";
if(cmd.psp.getBooleanProperty("Spisok.Moder.on.off")){Spisok += "\n|9| - Группа ''moder'' цена - " + cmd.psp.getIntProperty("ball.grant.8") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Admin.on.off")){Spisok += "\n|10| - Группа ''admin'', цена - " + cmd.psp.getIntProperty("ball.grant.9") + " баллов";}
if(cmd.psp.getBooleanProperty("Spisok.Modertime.on.off")){Spisok += "\n|11| - Группа ''modertime'' на " + cmd.psp.getIntProperty("Spisok.Modertime.Day") +
" день(дней) , цена - " + cmd.psp.getIntProperty("ball.grant.11") + " баллов";}
Spisok += "\nДля выхода из магазина выберете |0|";
Spisok += "\nВыберете цифру для покупки";
proc.mq.add(uin, Spisok);
}

public void Exit(IcqProtocol proc, String uin)
{
proc.mq.add(uin, "Вы вышли из магазина");return;
}

public void Chnick(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.1"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.1") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "chnick"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''chnick''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.1");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void Settheme(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.2"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.2") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "settheme"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''settheme''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.2");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void Who(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.3"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.3") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "whouser"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''whouser''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.3");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void Kickhist(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.4"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.4") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "kickhist"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''kickhist''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.4");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void Anyroom(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.5"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.5") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "anyroom"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''anyroom''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.5");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void Kickone(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.6"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.6") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "kickone"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''kickone''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.6");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void Banroom(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.7"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.7") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "banroom"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''banroom''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.7");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void Moder(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.8"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.8") + " баллов");
return;
}
int id = cmd.srv.us.getUser(uin).id;
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.8");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
boolean k = cmd.srv.us.setUserPropsValue(id, "group", "moder") &&
cmd.srv.us.setUserPropsValue(id, "grant", "") &&
cmd.srv.us.setUserPropsValue(id, "revoke", "");
cmd.srv.us.clearCashAuth(id);
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) группу ''moder''");
if(k)
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
else
proc.mq.add(uin,"Произошла ошибка");
}

public void Admin(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.9"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.9") + " баллов");
return;
}
int id = cmd.srv.us.getUser(uin).id;
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.9");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
boolean kk = cmd.srv.us.setUserPropsValue(id, "group", "admin") &&
cmd.srv.us.setUserPropsValue(id, "grant", "") &&
cmd.srv.us.setUserPropsValue(id, "revoke", "");
cmd.srv.us.clearCashAuth(id);
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, "", "SHOP", 0, "", "Купил(а) группу ''admin''");
if(kk)
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
else
proc.mq.add(uin,"Произошла ошибка");
}

public void Status(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.10"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.10") + " баллов");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "status_user"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) полномочие ''status_user''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.10");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

public void ModerTime(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.11"))
{
proc.mq.add(uin, "Для получения этого полномочия Вам необходимо набрать " + cmd.psp.getIntProperty("ball.grant.11") + " баллов");
return;
}
int id = cmd.srv.us.getUser(uin).id;
boolean kk = cmd.srv.us.setUserPropsValue(id, "group", "moder") &&
cmd.srv.us.setUserPropsValue(id, "grant", "") &&
cmd.srv.us.setUserPropsValue(id, "revoke", "");
cmd.srv.us.clearCashAuth(id);
cmd.setGrouptime(uin, cmd.psp.getIntProperty("Spisok.Modertime.Day"));
if(kk)
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
else
proc.mq.add(uin,"Произошла ошибка");
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "Купил(а) группу ''moder'' на время");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.11");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"Запрос успешно выполне, у вас осталось " + cmd.srv.us.getUser(uin).ball + " балл(ов)");
}

     /**
     * Вывод истории покупок
     */
    public String getShop(){
    String s="Покупки в магазине:\n ";
    try{
    PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select time, user_id, user_id2, msg from events where type='SHOP' order by time desc");
    ResultSet rs = pst.executeQuery();
    for(int i = 1; i < 21; i++)
    {
    if(rs.next())
    {
    s += i + ". - |" + rs.getTimestamp(1) + "|  Пользователь - |"+ cmd.srv.us.getUser(rs.getInt(2)).id
    + "|" + cmd.srv.us.getUser(rs.getInt(2)).localnick +  ", " + rs.getString(4)+'\n';
    }
    else
    {
    break;
    }
    }
    rs.close();
    pst.close();
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    }
    return s;
    }

}