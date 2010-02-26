/*
 * Скрипт для удаления информации из бд, для админ-бота.
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
   cmd.addCommand("!удинф", new Cmd("!удинф","$n",39,name));
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
   int id = (Integer)v.get(0);
   //задаем sql запрос
   String sql = "DELETE FROM inforob WHERE id =" + id;
   //создаем
   cmd.srv.us.db.executeQuery(sql);
   //Оповещаем
   proc.mq.add(uin,"Информация под номером " + id + " успешно удалена");
   }
   catch (Exception ex) 
   {
   ex.printStackTrace();
   proc.mq.add(uin,"При удалении информации возникла ошибка: "+ex.getMessage());
}