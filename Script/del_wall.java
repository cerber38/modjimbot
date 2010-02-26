﻿/*
* Скрипт для очистки стены
* (с)fraer72
*/

import ru.jimbot.modules.AbstractServer;
import ru.jimbot.util.*;
import ru.jimbot.modules.chat.*;
import ru.jimbot.modules.*;
import ru.jimbot.Manager;
import java.sql.*;

//Установка скрипта как обработчика команды
if(in.equals("install"))
{
cmd.addCommand("!делстена", new Cmd("!делстена","",39,name));
cmd.addAuth("Wall","Стена :)");
out="Ok";
return;
}

// Проверим полномочия
if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
if(!cmd.auth(proc,uin, "Wall")) return;

try{
cmd.srv.us.db.executeQuery(" TRUNCATE `wall` ");
//Оповещаем
proc.mq.add(uin,"Стена очищена");
}   
catch (Exception ex) 
{
ex.printStackTrace();
proc.mq.add(uin,"ошибка "+ex.getMessage());
}
