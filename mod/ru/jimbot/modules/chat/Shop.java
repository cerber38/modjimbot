/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.modules.ShopExtend;
import ru.jimbot.protocol.IcqProtocol;

/**
 * @author Fraer72
 * ������� ����, ��� ������ ���������)
 */

public class Shop {
private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
private HashMap<String, ShopExtend> ComShop;
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
commands.put("!�������", new Cmd("!�������","",1));
commands.put("!����", new Cmd("!����","",2));
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
    proc.mq.add(uin, "������� ����� �������\n��� ������ �������� |0|");
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
Spisok += "������8-): ������������ � ���� ��� ����������...";
Spisok += "\n����������:";
if(cmd.psp.getBooleanProperty("Spisok.Chnick.on.off")){Spisok += "\n|1| - ���������� ''chnick'', ���� - " + cmd.psp.getIntProperty("ball.grant.1") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Settheme.on.off")){Spisok += "\n|2| - ���������� ''settheme'', ���� - " + cmd.psp.getIntProperty("ball.grant.2") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Who.on.off")){Spisok += "\n|3| - ���������� ''who'', ���� - " + cmd.psp.getIntProperty("ball.grant.3") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Kickhist.on.off")){Spisok += "\n|4| - ���������� ''kickhist'', ���� - " + cmd.psp.getIntProperty("ball.grant.4") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Anyroom.on.off")){Spisok += "\n|5| - ���������� ''anyroom'', ���� - " + cmd.psp.getIntProperty("ball.grant.5") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Kickone.on.off")){Spisok += "\n|6| - ���������� ''kickone'', ���� - " + cmd.psp.getIntProperty("ball.grant.6") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Banroom.on.off")){Spisok += "\n|7| - ���������� ''banroom'', ���� - " + cmd.psp.getIntProperty("ball.grant.7") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Status.on.off")){Spisok += "\n|8| - ���������� ''status_user'', ���� - " + cmd.psp.getIntProperty("ball.grant.10") + " �$";}
Spisok += "\n������:";
if(cmd.psp.getBooleanProperty("Spisok.Moder.on.off")){Spisok += "\n|9| - ������ ''moder'' ���� - " + cmd.psp.getIntProperty("ball.grant.8") + " ������";}
if(cmd.psp.getBooleanProperty("Spisok.Admin.on.off")){Spisok += "\n|10| - ������ ''admin'', ���� - " + cmd.psp.getIntProperty("ball.grant.9") + " ������";}
Spisok += "\n��� ������ �� �������� �������� |0|";
Spisok += "\n�������� ����� ��� �������";
proc.mq.add(uin, Spisok);
}

public void Exit(IcqProtocol proc, String uin)
{
proc.mq.add(uin, "�� ����� �� ��������");return;
}

public void Chnick(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.1"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.1") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "chnick"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''chnick''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.1");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

public void Settheme(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.2"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.2") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "settheme"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''settheme''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.2");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

public void Who(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.3"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.3") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "whouser"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''whouser''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.3");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

public void Kickhist(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.4"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.4") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "kickhist"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''kickhist''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.4");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

public void Anyroom(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.5"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.5") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "anyroom"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''anyroom''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.5");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

public void Kickone(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.6"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.6") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "kickone"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''kickone''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.6");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

public void Banroom(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.7"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.7") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "banroom"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''banroom''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.7");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

public void Moder(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.8"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.8") + " ������");
return;
}
int id = cmd.srv.us.getUser(uin).id;
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.8");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
boolean k = cmd.srv.us.setUserPropsValue(id, "group", "moder") &&
cmd.srv.us.setUserPropsValue(id, "grant", "") &&
cmd.srv.us.setUserPropsValue(id, "revoke", "");
cmd.srv.us.clearCashAuth(id);
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ������ ''moder''");
if(k)
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
else
proc.mq.add(uin,"��������� ������");
}

public void Admin(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.9"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.9") + " ������");
return;
}
int id = cmd.srv.us.getUser(uin).id;
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.9");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
boolean kk = cmd.srv.us.setUserPropsValue(id, "group", "admin") &&
cmd.srv.us.setUserPropsValue(id, "grant", "") &&
cmd.srv.us.setUserPropsValue(id, "revoke", "");
cmd.srv.us.clearCashAuth(id);
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, "", "SHOP", 0, "", "�����(�) ������ ''admin''");
if(kk)
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
else
proc.mq.add(uin,"��������� ������");
}

public void Status(IcqProtocol proc, String uin)
{
if(cmd.srv.us.getUser(uin).ball<cmd.psp.getIntProperty("ball.grant.10"))
{
proc.mq.add(uin, "��� ��������� ����� ���������� ��� ���������� ������� " + cmd.psp.getIntProperty("ball.grant.10") + " ������");
return;
}
if(cmd.srv.us.grantUser(cmd.srv.us.getUser(uin).id, "status_user"))
cmd.srv.us.db.event(cmd.srv.us.getUser(uin).id, uin, "SHOP", 0, "", "�����(�) ���������� ''status_user''");
cmd.srv.us.getUser(uin).ball=cmd.srv.us.getUser(uin).ball-cmd.psp.getIntProperty("ball.grant.10");
cmd.srv.us.updateUser(cmd.srv.us.getUser(uin));
proc.mq.add(uin,"������ ������� �������, � ��� �������� " + cmd.srv.us.getUser(uin).ball + " ����(��)");
}

     /**
     * ����� ������� �������
     */
    public String getShop(){
    String s="������� � ��������:\n ";
    try{
    PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select time, user_id, user_id2, msg from events where type='SHOP' order by time desc");
    ResultSet rs = pst.executeQuery();
    for(int i = 1; i < 21; i++)
    {
    if(rs.next())
    {
    s += i + ". - |" + rs.getTimestamp(1) + "|  ������������ - |"+ cmd.srv.us.getUser(rs.getInt(2)).id
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