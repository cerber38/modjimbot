/*
* Скрипт для вывода сообщений на стене
* (с)fraer72
*/

import ru.jimbot.modules.AbstractServer;
import ru.jimbot.util.*;
import ru.jimbot.modules.chat.*;
import ru.jimbot.modules.*;
import ru.jimbot.Manager;
import java.sql.*;

//Установка скрипта как обработчика команды
if(in.equals("install")){
cmd.addCommand("!стена", new Cmd("!стена","",39,name));
cmd.addAuth("Wall","Стена :)");
out="Ok";
return;
}

// Проверим полномочия
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
if(!cmd.auth(proc,uin, "Wall")) return;

try
{
int N = 0;
String q = "SELECT count(*) txt FROM `wall` WHERE id";
Vector v = cmd.srv.us.db.getValues(q);
String list = "Сообщения на стене\n";
list += "Всего сообщений |" + Integer.parseInt(v.get(0)[0]) + "|\n";
list += "№|Id|Nick|Msg\n";
//задаем sql запрос
String sql = "select user_id, txt from wall ORDER BY `id` DESC LIMIT 0 , 10";//10 - это максимальное количество сообщений при вызове
//создаем
PreparedStatement pst = cmd.srv.us.db.getDb().prepareStatement(sql);
ResultSet rs = pst.executeQuery();
while(rs.next())
{
N++;
Users u = cmd.srv.us.getUser(rs.getInt(1));
list += N + ". - |" + u.id + "|" + u.localnick + " » "+ rs.getString(2) + '\n';
}
rs.close();
pst.close();
//Оповещаем
proc.mq.add(uin,list);
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При выводе всей информации возникла ошибка: "+ex.getMessage());
}