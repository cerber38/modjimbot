/*
 * Данный скрипт запускается при старте бота. Здесь можно указать команды подключения или
 * инициализации внешних программных средств - баз данных, програмных оболочек и т.п.
 */

/*
 * При вызове скрипта определены следующие переменные:
 * AbstractServer srv
 */


import ru.jimbot.modules.AbstractServer;
import ru.jimbot.util.*;
import ru.jimbot.modules.chat.*;
import ru.jimbot.modules.*;

Log.getLogger(srv.getName()).info("Запуск бота...");
out="Ok";

// Добавляем псевдонимы к существующим командам бота
// Новые команды и полномочия добавятся автоматически при установке скриптов

((ChatCommandProc)srv.cmd).addCommand("!help", new Cmd("!help","",1));
((ChatCommandProc)srv.cmd).addCommand("!справка", new Cmd("!справка", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!помощь", new Cmd("!помощь", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!команды", new Cmd("!команды", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!команды", new Cmd("!команды", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!chat", new Cmd("!chat", "", 2));
((ChatCommandProc)srv.cmd).addCommand("!чат", new Cmd("!чат", "", 2));
((ChatCommandProc)srv.cmd).addCommand("!вход", new Cmd("!вход", "", 2));
((ChatCommandProc)srv.cmd).addCommand("!exit", new Cmd("!exit", "", 3));
((ChatCommandProc)srv.cmd).addCommand("!выход", new Cmd("!выход", "", 3));
((ChatCommandProc)srv.cmd).addCommand("!rules", new Cmd("!rules", "", 4));
((ChatCommandProc)srv.cmd).addCommand("!правила", new Cmd("!правила", "", 4));
((ChatCommandProc)srv.cmd).addCommand("!законы", new Cmd("!законы", "", 4));
((ChatCommandProc)srv.cmd).addCommand("!stat", new Cmd("!stat", "", 5));
((ChatCommandProc)srv.cmd).addCommand("!стат", new Cmd("!стат", "", 5));
((ChatCommandProc)srv.cmd).addCommand("!gofree", new Cmd("!gofree", "", 6));
((ChatCommandProc)srv.cmd).addCommand("!свюин", new Cmd("!свюин", "", 6));
((ChatCommandProc)srv.cmd).addCommand("!go", new Cmd("!go", "$n", 7));
((ChatCommandProc)srv.cmd).addCommand("!юин", new Cmd("!юин", "$n", 7));
((ChatCommandProc)srv.cmd).addCommand("!banlist", new Cmd("!banlist", "", 8));
((ChatCommandProc)srv.cmd).addCommand("!банлист", new Cmd("!банлист", "", 8));
((ChatCommandProc)srv.cmd).addCommand("!kicklist", new Cmd("!kicklist", "", 9));
((ChatCommandProc)srv.cmd).addCommand("!киклист", new Cmd("!киклист", "", 9));
((ChatCommandProc)srv.cmd).addCommand("!info", new Cmd("!info", "$c", 10));
((ChatCommandProc)srv.cmd).addCommand("!инфо", new Cmd("!инфо", "$c", 10));
((ChatCommandProc)srv.cmd).addCommand("!kick", new Cmd("!kick", "$c $n $s", 11));
((ChatCommandProc)srv.cmd).addCommand("!кик", new Cmd("!кик", "$c $n $s", 11));
((ChatCommandProc)srv.cmd).addCommand("!kickall", new Cmd("!kickall", "", 12));
((ChatCommandProc)srv.cmd).addCommand("!киквсех", new Cmd("!киквсех", "", 12));
((ChatCommandProc)srv.cmd).addCommand("!listauth", new Cmd("!listauth", "", 13));
((ChatCommandProc)srv.cmd).addCommand("!листаут", new Cmd("!листаут", "", 13));
((ChatCommandProc)srv.cmd).addCommand("!who", new Cmd("!who", "$n", 14));
((ChatCommandProc)srv.cmd).addCommand("!кто", new Cmd("!кто", "$n", 14));
((ChatCommandProc)srv.cmd).addCommand("!listgroup", new Cmd("!listgroup", "", 15));
((ChatCommandProc)srv.cmd).addCommand("!листгрупп", new Cmd("!листгрупп", "", 15));
((ChatCommandProc)srv.cmd).addCommand("!checkuser", new Cmd("!checkuser", "$n", 16));
((ChatCommandProc)srv.cmd).addCommand("!проверка", new Cmd("!проверка", "$n", 16));
((ChatCommandProc)srv.cmd).addCommand("!setgroup", new Cmd("!setgroup", "$n $c", 17));
((ChatCommandProc)srv.cmd).addCommand("!группа", new Cmd("!группа", "$n $c", 17));
((ChatCommandProc)srv.cmd).addCommand("!grant", new Cmd("!grant", "$n $c", 18));
((ChatCommandProc)srv.cmd).addCommand("!добавить", new Cmd("!добавить", "$n $c", 18));
((ChatCommandProc)srv.cmd).addCommand("!revoke", new Cmd("!revoke", "$n $c $s", 19));
((ChatCommandProc)srv.cmd).addCommand("!лишить", new Cmd("!лишить", "$n $c $s", 19));
((ChatCommandProc)srv.cmd).addCommand("!бан", new Cmd("!бан", "$c $s", 20));
((ChatCommandProc)srv.cmd).addCommand("!uban", new Cmd("!uban", "$c", 21));
((ChatCommandProc)srv.cmd).addCommand("!убан", new Cmd("!убан", "$c", 21));
((ChatCommandProc)srv.cmd).addCommand("!reg", new Cmd("!reg","$c $c",22));
((ChatCommandProc)srv.cmd).addCommand("!ник", new Cmd("!ник", "$c $c", 22));
((ChatCommandProc)srv.cmd).addCommand("!рег", new Cmd("!рег", "$c $c", 22));
((ChatCommandProc)srv.cmd).addCommand("+a", new Cmd("+a", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+а", new Cmd("+а", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+f", new Cmd("+f", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+ф", new Cmd("+ф", "", 23));
((ChatCommandProc)srv.cmd).addCommand("!тут", new Cmd("!тут", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+p", new Cmd("+p", "$n $s", 24));
((ChatCommandProc)srv.cmd).addCommand("+р", new Cmd("+р", "$n $s", 24));
((ChatCommandProc)srv.cmd).addCommand("!лс", new Cmd("!лс", "$n $s", 24));
((ChatCommandProc)srv.cmd).addCommand("+pp", new Cmd("+pp", "$s", 25));
((ChatCommandProc)srv.cmd).addCommand("+рр", new Cmd("+рр", "$s", 25));
((ChatCommandProc)srv.cmd).addCommand("!ответ", new Cmd("!ответ", "$s", 25));
((ChatCommandProc)srv.cmd).addCommand("!settheme", new Cmd("!settheme", "$s", 26));
((ChatCommandProc)srv.cmd).addCommand("!тема", new Cmd("!тема", "$s", 26));
((ChatCommandProc)srv.cmd).addCommand("!getinfo", new Cmd("!getinfo", "$c", 27));
((ChatCommandProc)srv.cmd).addCommand("!аська", new Cmd("!аська", "$c", 27));
((ChatCommandProc)srv.cmd).addCommand("!room", new Cmd("!room", "$n $c", 28));
((ChatCommandProc)srv.cmd).addCommand("!комната", new Cmd("!комната", "$n $c", 28));
((ChatCommandProc)srv.cmd).addCommand("!к", new Cmd("!к", "$n $c", 28));
((ChatCommandProc)srv.cmd).addCommand("!kickhist", new Cmd("!kickhist", "", 29));
((ChatCommandProc)srv.cmd).addCommand("!кикист", new Cmd("!кикист", "", 29));
((ChatCommandProc)srv.cmd).addCommand("!adm", new Cmd("!adm", "$s", 30));
((ChatCommandProc)srv.cmd).addCommand("!админу", new Cmd("!админу", "$s", 30));
((ChatCommandProc)srv.cmd).addCommand("!banhist", new Cmd("!banhist", "", 31));
((ChatCommandProc)srv.cmd).addCommand("!банист", new Cmd("!банист", "", 31));
((ChatCommandProc)srv.cmd).addCommand("+aa", new Cmd("+aa", "", 32));
((ChatCommandProc)srv.cmd).addCommand("+аа", new Cmd("+аа", "", 32));
((ChatCommandProc)srv.cmd).addCommand("!все", new Cmd("!все", "", 32));
((ChatCommandProc)srv.cmd).addCommand("!lroom", new Cmd("!lroom", "", 33));
((ChatCommandProc)srv.cmd).addCommand("!комнаты", new Cmd("!комнаты", "", 33));
((ChatCommandProc)srv.cmd).addCommand("!crroom", new Cmd("!crroom", "$n $s", 34));
((ChatCommandProc)srv.cmd).addCommand("!создкомн", new Cmd("!создкомн", "$n $s", 34));
((ChatCommandProc)srv.cmd).addCommand("!chroom", new Cmd("!chroom", "$n $s", 35));
((ChatCommandProc)srv.cmd).addCommand("!измкомн", new Cmd("!измкомн", "$n $s", 35));
((ChatCommandProc)srv.cmd).addCommand("!модер", new Cmd("!модер", "$n $n", 36));
((ChatCommandProc)srv.cmd).addCommand("!модерлист", new Cmd("!модерлист", "", 37));
((ChatCommandProc)srv.cmd).addCommand("!закрытые", new Cmd("!закрытые", "", 38));
        // TODO: 39 - скрипты
        // TODO: 40 - скрипты в базе
((ChatCommandProc)srv.cmd).addCommand("!запереть", new Cmd("!запереть", "$n $n $s", 41));
((ChatCommandProc)srv.cmd).addCommand("!бутылочка", new Cmd("!бутылочка","",42));
((ChatCommandProc)srv.cmd).addCommand("!фраза", new Cmd("!фраза", "$s", 43));
((ChatCommandProc)srv.cmd).addCommand("!админы", new Cmd("!админы", "", 44));
((ChatCommandProc)srv.cmd).addCommand("!chnick", new Cmd("!chnick","$n $c",45));
((ChatCommandProc)srv.cmd).addCommand("!смник", new Cmd("!смник","$n $c",45));
((ChatCommandProc)srv.cmd).addCommand("!повысить", new Cmd("!повысить", "$n", 46));
((ChatCommandProc)srv.cmd).addCommand("!понизить", new Cmd("!понизить", "$n", 47));
((ChatCommandProc)srv.cmd).addCommand("!setpass", new Cmd("!setpass", "$c", 48));
((ChatCommandProc)srv.cmd).addCommand("!пароль", new Cmd("!пароль", "$c", 48));
((ChatCommandProc)srv.cmd).addCommand("!адмлист", new Cmd("!адмлист", "$c", 49));
((ChatCommandProc)srv.cmd).addCommand("!робмсг", new Cmd("!робмсг", "$s", 50));
((ChatCommandProc)srv.cmd).addCommand("!хстатус", new Cmd("!хстатус", "$n $s", 51));
//((ChatCommandProc)srv.cmd).addCommand("!перезагрузить", new Cmd("!перезагрузить", "", 52));
((ChatCommandProc)srv.cmd).addCommand("!статус", new Cmd("!статус", "$s", 53));
((ChatCommandProc)srv.cmd).addCommand("!разбанлист", new Cmd("!разбанлист", "", 54));
((ChatCommandProc)srv.cmd).addCommand("!удалить", new Cmd("!удалить", "$n", 55));
((ChatCommandProc)srv.cmd).addCommand("!пригласитьид", new Cmd("!пригласитьид", "$n $s", 56));
((ChatCommandProc)srv.cmd).addCommand("!пригласитьуин", new Cmd("!пригласитьуин", "$c $s", 57));
((ChatCommandProc)srv.cmd).addCommand("!везде", new Cmd("!везде", "$s", 58));
((ChatCommandProc)srv.cmd).addCommand("!деладм", new Cmd("!деладм", "", 59));
((Shop)srv.cmd.shop).addCommand("!магазин", new Cmd("!магазин","",1));
((Shop)srv.cmd.shop).addCommand("!учет", new Cmd("!учет","",2));
((AboutUser)srv.cmd.abv).addCommand("!данные", new Cmd("!данные","",1));
((AboutUser)srv.cmd.abv).addCommand("!личное", new Cmd("!личное","$n",2));
((ClanCommand)srv.cmd.clan).addCommand( "!аддклан", new Cmd( "!аддклан", "", 1 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!кланлист", new Cmd( "!кланлист", "", 2 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!топклан", new Cmd( "!топклан", "", 3 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!принять", new Cmd( "!принять", "$n", 4 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!рассмотреть", new Cmd( "!рассмотреть", "$n", 5 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!изгнать", new Cmd( "!изгнать", "$n", 6 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!покинуть", new Cmd( "!покинуть", "", 7 ) );
((ClanCommand)srv.cmd.clan).addCommand( "+кланбалл", new Cmd( "+кланбалл", "", 8 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!листмембер", new Cmd( "!листмембер", "$n", 9 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!аддгруппа", new Cmd( "!аддгруппа", "$n $c", 10 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!делгруппа", new Cmd( "!делгруппа", "$n", 11 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!листгруппа", new Cmd( "!листгруппа", "$n", 12 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!делклан", new Cmd( "!делклан", "$n", 13 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!изинфо", new Cmd( "!изинфо", "$s", 14 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!кланхелп", new Cmd( "!кланхелп", "", 15 ) );
((Gift)srv.cmd.gift).addCommand("!ларек", new Cmd("!ларек","",1));
((Gift)srv.cmd.gift).addCommand("!добподарок", new Cmd("!добподарок","$c $n",2));
((Gift)srv.cmd.gift).addCommand("!вещи", new Cmd("!вещи","",3));
((Gift)srv.cmd.gift).addCommand("!подарить", new Cmd("!подарить","$n $n $s",4));
((Gift)srv.cmd.gift).addCommand("!всеподарки", new Cmd("!всеподарки","$n",5));
((Gift)srv.cmd.gift).addCommand("!делподарок", new Cmd("!делподарок","$n",6));
((frends)srv.cmd.frends).addCommand("!делдруг", new Cmd("!делдруг","$n",1));
((frends)srv.cmd.frends).addCommand("!заявка", new Cmd("!заявка","$n",2));
((frends)srv.cmd.frends).addCommand("!подтвердить", new Cmd("!подтвердить","$n",3));
((frends)srv.cmd.frends).addCommand("!отклонить", new Cmd("!отклонить","$n",4));
((frends)srv.cmd.frends).addCommand("!заявки", new Cmd("!заявки","",5));
((frends)srv.cmd.frends).addCommand("!аллдруг", new Cmd("!аллдруг","$n",6));
((ChatCommandProc)srv.cmd).addCommand("!свадьба", new Cmd("!свадьба", "$n $n", 60));
((ChatCommandProc)srv.cmd).addCommand("!развод", new Cmd("!развод", "$n $n", 61));
((ChatCommandProc)srv.cmd).addCommand("!отдать", new Cmd("!отдать", "$n $n", 62));
((ChatCommandProc)srv.cmd).addCommand("!chstatus", new Cmd("!chstatus","$n $s",63));
((ChatCommandProc)srv.cmd).addCommand("!cмстатус", new Cmd("!смстатус","$n $s",63));
((ChatCommandProc)srv.cmd).addCommand("!измид", new Cmd("!измид","$n $n",64));
((ChatCommandProc)srv.cmd).addCommand("!спрятаться", new Cmd("!спрятаться","",65));
((ChatCommandProc)srv.cmd).addCommand("!показаться", new Cmd("!показаться","",66));
((ChatCommandProc)srv.cmd).addCommand("!скрылись", new Cmd("!скрылись","",67));
((Shop2)srv.cmd.shop2).addCommand("!автосалон", new Cmd("!автосалон","",1));
((Shop2)srv.cmd.shop2).addCommand("!недвижимость", new Cmd("!недвижимость","",2));
((Shop2)srv.cmd.shop2).addCommand("!бутик", new Cmd("!бутик","",3));
((Shop2)srv.cmd.shop2).addCommand("!зоо", new Cmd("!зоо","",4));
((Shop2)srv.cmd.shop2).addCommand("!делтовар", new Cmd("!делтовар","$c $n",5));
((Shop2)srv.cmd.shop2).addCommand("!аддтовар", new Cmd("!аддтовар","$c $n $s",6));









