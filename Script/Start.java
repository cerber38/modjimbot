/*
 * ������ ������ ����������� ��� ������ ����. ����� ����� ������� ������� ����������� ���
 * ������������� ������� ����������� ������� - ��� ������, ���������� �������� � �.�.
 */

/*
 * ��� ������ ������� ���������� ��������� ����������:
 * AbstractServer srv
 */


import ru.jimbot.modules.AbstractServer;
import ru.jimbot.util.*;
import ru.jimbot.modules.chat.*;
import ru.jimbot.modules.*;

Log.getLogger(srv.getName()).info("������ ����...");
out="Ok";

// ��������� ���������� � ������������ �������� ����
// ����� ������� � ���������� ��������� ������������� ��� ��������� ��������

((ChatCommandProc)srv.cmd).addCommand("!help", new Cmd("!help","",1));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 1));
((ChatCommandProc)srv.cmd).addCommand("!chat", new Cmd("!chat", "", 2));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "", 2));
((ChatCommandProc)srv.cmd).addCommand("!����", new Cmd("!����", "", 2));
((ChatCommandProc)srv.cmd).addCommand("!exit", new Cmd("!exit", "", 3));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����", "", 3));
((ChatCommandProc)srv.cmd).addCommand("!rules", new Cmd("!rules", "", 4));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 4));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "", 4));
((ChatCommandProc)srv.cmd).addCommand("!stat", new Cmd("!stat", "", 5));
((ChatCommandProc)srv.cmd).addCommand("!����", new Cmd("!����", "", 5));
((ChatCommandProc)srv.cmd).addCommand("!gofree", new Cmd("!gofree", "", 6));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����", "", 6));
((ChatCommandProc)srv.cmd).addCommand("!go", new Cmd("!go", "$n", 7));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "$n", 7));
((ChatCommandProc)srv.cmd).addCommand("!banlist", new Cmd("!banlist", "", 8));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 8));
((ChatCommandProc)srv.cmd).addCommand("!kicklist", new Cmd("!kicklist", "", 9));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 9));
((ChatCommandProc)srv.cmd).addCommand("!info", new Cmd("!info", "$c", 10));
((ChatCommandProc)srv.cmd).addCommand("!����", new Cmd("!����", "$c", 10));
((ChatCommandProc)srv.cmd).addCommand("!kick", new Cmd("!kick", "$c $n $s", 11));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "$c $n $s", 11));
((ChatCommandProc)srv.cmd).addCommand("!kickall", new Cmd("!kickall", "", 12));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 12));
((ChatCommandProc)srv.cmd).addCommand("!listauth", new Cmd("!listauth", "", 13));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 13));
((ChatCommandProc)srv.cmd).addCommand("!who", new Cmd("!who", "$n", 14));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "$n", 14));
((ChatCommandProc)srv.cmd).addCommand("!listgroup", new Cmd("!listgroup", "", 15));
((ChatCommandProc)srv.cmd).addCommand("!���������", new Cmd("!���������", "", 15));
((ChatCommandProc)srv.cmd).addCommand("!checkuser", new Cmd("!checkuser", "$n", 16));
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������", "$n", 16));
((ChatCommandProc)srv.cmd).addCommand("!setgroup", new Cmd("!setgroup", "$n $c", 17));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$n $c", 17));
((ChatCommandProc)srv.cmd).addCommand("!grant", new Cmd("!grant", "$n $c", 18));
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������", "$n $c", 18));
((ChatCommandProc)srv.cmd).addCommand("!revoke", new Cmd("!revoke", "$n $c $s", 19));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$n $c $s", 19));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "$c $s", 20));
((ChatCommandProc)srv.cmd).addCommand("!uban", new Cmd("!uban", "$c", 21));
((ChatCommandProc)srv.cmd).addCommand("!����", new Cmd("!����", "$c", 21));
((ChatCommandProc)srv.cmd).addCommand("!reg", new Cmd("!reg","$c $c",22));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "$c $c", 22));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "$c $c", 22));
((ChatCommandProc)srv.cmd).addCommand("+a", new Cmd("+a", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+�", new Cmd("+�", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+f", new Cmd("+f", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+�", new Cmd("+�", "", 23));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "", 23));
((ChatCommandProc)srv.cmd).addCommand("+p", new Cmd("+p", "$n $s", 24));
((ChatCommandProc)srv.cmd).addCommand("+�", new Cmd("+�", "$n $s", 24));
((ChatCommandProc)srv.cmd).addCommand("!��", new Cmd("!��", "$n $s", 24));
((ChatCommandProc)srv.cmd).addCommand("+pp", new Cmd("+pp", "$s", 25));
((ChatCommandProc)srv.cmd).addCommand("+��", new Cmd("+��", "$s", 25));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����", "$s", 25));
((ChatCommandProc)srv.cmd).addCommand("!settheme", new Cmd("!settheme", "$s", 26));
((ChatCommandProc)srv.cmd).addCommand("!����", new Cmd("!����", "$s", 26));
((ChatCommandProc)srv.cmd).addCommand("!getinfo", new Cmd("!getinfo", "$c", 27));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����", "$c", 27));
((ChatCommandProc)srv.cmd).addCommand("!room", new Cmd("!room", "$n $c", 28));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "$n $c", 28));
((ChatCommandProc)srv.cmd).addCommand("!�", new Cmd("!�", "$n $c", 28));
((ChatCommandProc)srv.cmd).addCommand("!kickhist", new Cmd("!kickhist", "", 29));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "", 29));
((ChatCommandProc)srv.cmd).addCommand("!adm", new Cmd("!adm", "$s", 30));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$s", 30));
((ChatCommandProc)srv.cmd).addCommand("!banhist", new Cmd("!banhist", "", 31));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "", 31));
((ChatCommandProc)srv.cmd).addCommand("+aa", new Cmd("+aa", "", 32));
((ChatCommandProc)srv.cmd).addCommand("+��", new Cmd("+��", "", 32));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���", "", 32));
((ChatCommandProc)srv.cmd).addCommand("!lroom", new Cmd("!lroom", "", 33));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "", 33));
((ChatCommandProc)srv.cmd).addCommand("!crroom", new Cmd("!crroom", "$n $s", 34));
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������", "$n $s", 34));
((ChatCommandProc)srv.cmd).addCommand("!chroom", new Cmd("!chroom", "$n $s", 35));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "$n $s", 35));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����", "$n $n", 36));
((ChatCommandProc)srv.cmd).addCommand("!���������", new Cmd("!���������", "", 37));
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������", "", 38));
        // TODO: 39 - �������
        // TODO: 40 - ������� � ����
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������", "$n $n $s", 41));
((ChatCommandProc)srv.cmd).addCommand("!���������", new Cmd("!���������","",42));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����", "$s", 43));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "", 44));
((ChatCommandProc)srv.cmd).addCommand("!chnick", new Cmd("!chnick","$n $c",45));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����","$n $c",45));
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������", "$n", 46));
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������", "$n", 47));
((ChatCommandProc)srv.cmd).addCommand("!setpass", new Cmd("!setpass", "$c", 48));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$c", 48));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "$c", 49));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$s", 50));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "$n $s", 51));
//((ChatCommandProc)srv.cmd).addCommand("!�������������", new Cmd("!�������������", "", 52));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$s", 53));
((ChatCommandProc)srv.cmd).addCommand("!����������", new Cmd("!����������", "", 54));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "$n", 55));
((ChatCommandProc)srv.cmd).addCommand("!������������", new Cmd("!������������", "$n $s", 56));
((ChatCommandProc)srv.cmd).addCommand("!�������������", new Cmd("!�������������", "$c $s", 57));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����", "$s", 58));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "", 59));
((Shop)srv.cmd.shop).addCommand("!�������", new Cmd("!�������","",1));
((Shop)srv.cmd.shop).addCommand("!����", new Cmd("!����","",2));
((AboutUser)srv.cmd.abv).addCommand("!������", new Cmd("!������","",1));
((AboutUser)srv.cmd.abv).addCommand("!������", new Cmd("!������","$n",2));
((ClanCommand)srv.cmd.clan).addCommand( "!�������", new Cmd( "!�������", "", 1 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!��������", new Cmd( "!��������", "", 2 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!�������", new Cmd( "!�������", "", 3 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!�������", new Cmd( "!�������", "$n", 4 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!�����������", new Cmd( "!�����������", "$n", 5 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!�������", new Cmd( "!�������", "$n", 6 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!��������", new Cmd( "!��������", "", 7 ) );
((ClanCommand)srv.cmd.clan).addCommand( "+��������", new Cmd( "+��������", "", 8 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!����������", new Cmd( "!����������", "$n", 9 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!���������", new Cmd( "!���������", "$n $c", 10 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!���������", new Cmd( "!���������", "$n", 11 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!����������", new Cmd( "!����������", "$n", 12 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!�������", new Cmd( "!�������", "$n", 13 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!������", new Cmd( "!������", "$s", 14 ) );
((ClanCommand)srv.cmd.clan).addCommand( "!��������", new Cmd( "!��������", "", 15 ) );
((Gift)srv.cmd.gift).addCommand("!�����", new Cmd("!�����","",1));
((Gift)srv.cmd.gift).addCommand("!����������", new Cmd("!����������","$c $n",2));
((Gift)srv.cmd.gift).addCommand("!����", new Cmd("!����","",3));
((Gift)srv.cmd.gift).addCommand("!��������", new Cmd("!��������","$n $n $s",4));
((Gift)srv.cmd.gift).addCommand("!����������", new Cmd("!����������","$n",5));
((Gift)srv.cmd.gift).addCommand("!����������", new Cmd("!����������","$n",6));
((frends)srv.cmd.frends).addCommand("!�������", new Cmd("!�������","$n",1));
((frends)srv.cmd.frends).addCommand("!������", new Cmd("!������","$n",2));
((frends)srv.cmd.frends).addCommand("!�����������", new Cmd("!�����������","$n",3));
((frends)srv.cmd.frends).addCommand("!���������", new Cmd("!���������","$n",4));
((frends)srv.cmd.frends).addCommand("!������", new Cmd("!������","",5));
((frends)srv.cmd.frends).addCommand("!�������", new Cmd("!�������","$n",6));
((ChatCommandProc)srv.cmd).addCommand("!�������", new Cmd("!�������", "$n $n", 60));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$n $n", 61));
((ChatCommandProc)srv.cmd).addCommand("!������", new Cmd("!������", "$n $n", 62));
((ChatCommandProc)srv.cmd).addCommand("!chstatus", new Cmd("!chstatus","$n $s",63));
((ChatCommandProc)srv.cmd).addCommand("!c�������", new Cmd("!��������","$n $s",63));
((ChatCommandProc)srv.cmd).addCommand("!�����", new Cmd("!�����","$n $n",64));
((ChatCommandProc)srv.cmd).addCommand("!����������", new Cmd("!����������","",65));
((ChatCommandProc)srv.cmd).addCommand("!����������", new Cmd("!����������","",66));
((ChatCommandProc)srv.cmd).addCommand("!��������", new Cmd("!��������","",67));
((Shop2)srv.cmd.shop2).addCommand("!���������", new Cmd("!���������","",1));
((Shop2)srv.cmd.shop2).addCommand("!������������", new Cmd("!������������","",2));
((Shop2)srv.cmd.shop2).addCommand("!�����", new Cmd("!�����","",3));
((Shop2)srv.cmd.shop2).addCommand("!���", new Cmd("!���","",4));
((Shop2)srv.cmd.shop2).addCommand("!��������", new Cmd("!��������","$c $n",5));
((Shop2)srv.cmd.shop2).addCommand("!��������", new Cmd("!��������","$c $n $s",6));
((ChatCommandProc)srv.cmd).addCommand("!���", new Cmd("!���","$n",68));









