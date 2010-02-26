﻿/**
*  Добавление записи на стену
*  (c) fraer72
*/

import ru.jimbot.modules.AbstractServer;
import ru.jimbot.util.*;
import ru.jimbot.modules.chat.*;
import ru.jimbot.modules.*;
import java.sql.*;

//Установка скрипта как обработчика команды
if(in.equals("install")){
  cmd.addCommand("!аддстена", new Cmd("!аддстена","$s",39,name));
  cmd.addAuth("Wall","Стена :)");
  out="Ok";
  return;
}

// Проверим полномочия
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
if(!cmd.auth(proc,uin, "Wall")) return;

try
{
   //парсим
   Vector v = cmd.getParser().parseArgs(msg);
   String text = (String)v.get(0);// Сообщение
   int MaxTxt = 100;// длина сообщения
   long time = System.currentTimeMillis();
   Users u = cmd.srv.us.getUser(uin);// Пользователь
   long z = cmd.srv.us.db.getLastIndex("wall");
   int id = (int) z;
   String q = "SELECT count(*) FROM `wall` WHERE user_id="+u.id+" and (to_days( now( ) ) - to_days( time )) <1";
   Vector v0 = cmd.srv.us.db.getValues(q);
   // Ограничение на количество записей на стену. (на системных админов недействует)
   if((Integer.parseInt(v0.get(0)[0])) > 4 && !cmd.psp.testAdmin(uin))
   {
   proc.mq.add(uin,u.localnick + " нельзя в день делать больше пяти записей на стену");
   return;
   }
   // Если пустое сообщение
   if(text.equals("") || text.equals(" "))
   {
   proc.mq.add(uin,u.localnick + " ну и что ж мы пишем пустое сообщение");
   return;
   }
   // Если в сообщении мат
   if (cmd.radm.testMat1(cmd.radm.changeChar(text)))
   {
   proc.mq.add(uin,u.localnick + " в сообщении ''МАТ''");
   return;
   }
   // Обрежим сообщение если оно большое
   if(text.length() > MaxTxt)
   {
   text = text.substring(0,MaxTxt);
   proc.mq.add(uin,"Предупреждение! Ваш сообщение слишком длинное и будет обрезано.");
   }
   // Сотрем все цифры :)
   for(int i = 0; i < 10; i++){text = text.replace(Integer.toString(i),"");}
   // Опустим регистр
   text = text.toLowerCase();
   //задаем sql запрос
   PreparedStatement pst = cmd.srv.us.db.getDb().prepareStatement("insert into wall values (?, ?, ?, ?)");
   pst.setInt(1, id);
   pst.setInt(2, u.id);
   pst.setString(3, text); 
   pst.setTimestamp(4,new Timestamp(time));
   pst.execute();
   pst.close();
   //Оповещаем
   proc.mq.add(uin,u.localnick + " ваше сообщение опубликовано на стену :)");
   }
   catch (Exception ex) 
   {
   ex.printStackTrace();
   proc.mq.add(uin,"При создании сообщения возникла ошибка: "+ex.getMessage());
}