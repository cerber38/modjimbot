/*
 * Скрипт для вывода листинга всей информации для админ бота из БД
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
   cmd.addCommand("!листинф", new Cmd("!листинф","",39,name));
   cmd.addAuth("infbot","Создании, удалении информации для админ бота");
   out="Ok";
   return;
}

// Проверим полномочия
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
if(!cmd.auth(proc,uin, "infbot")) return;

try
{
   //парсим
   Vector v = cmd.getParser().parseArgs(msg);
   String list = "Список информаций для админ-бота\nid|msg\n";
   //задаем sql запрос
   String sql = "select id, information from inforob";
   //создаем
   PreparedStatement pst = cmd.srv.us.db.getDb().prepareStatement(sql);
   ResultSet rs = pst.executeQuery();
   while(rs.next())   
   {
   list += rs.getInt(1) + " | " + rs.getString(2) + "\n";
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