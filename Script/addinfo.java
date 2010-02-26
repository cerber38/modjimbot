﻿/*
 * Скрипт для добавления информации в бд, для админ-бота.
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
   cmd.addCommand("!создинф", new Cmd("!создинф","$s",39,name));
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
   String s = (String)v.get(0);
   long z = cmd.srv.us.db.getLastIndex("inforob");
   int id = (int) z;
   //задаем sql запрос
   String sql = "INSERT INTO `inforob` VALUES ("+id+", '"+s+"');";
   //создаем
   cmd.srv.us.db.executeQuery(sql);
   //Оповещаем
   proc.mq.add(uin,"Информация успешно созданна\nВсего информаций в БД: " + id);
   }
   catch (Exception ex) 
   {
   ex.printStackTrace();
   proc.mq.add(uin,"При создании информации возникла ошибка: "+ex.getMessage());
}