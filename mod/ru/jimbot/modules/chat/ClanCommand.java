

package ru.jimbot.modules.chat;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
  * �����.
  * @author fraer72
  */

public class ClanCommand {
private HashMap< String, Cmd > commands = new HashMap< String, Cmd >();
private HashMap<String, ClanMap> ClanMap;
private ConcurrentHashMap < String, Integer > c; // ���������� ����������� � ����
private CommandParser parser;
private ChatCommandProc cmd;
private int Order = 0;
private String CMD = "";
private long TIME = 5*60000;
private String Clan_Name = "";// �������� ������
private int Leader_ID = 0;// �� ������ �����
private int Clan_Room = 0;// �� ������� �����
private String Clan_Info = "";// ���������� � �����
// ��� �������� ����������
private HashMap< String, String > group = new HashMap< String, String >();

public ClanCommand( ChatCommandProc c )
{
cmd = c;
parser = new CommandParser(commands);
ClanMap = new HashMap<String, ClanMap>();
this.c = new ConcurrentHashMap< String, Integer >();
init();
}

private void init()
{
group.put("Advance", "����������� ���� �����:\n1)����� ����� � ����.\n2)����� ������ ���� � �����.");

commands.put( "!�������", new Cmd( "!�������", "", 1 ) );
commands.put( "!��������", new Cmd( "!��������", "", 2 ) );// ����� ���� ������
commands.put( "!�������", new Cmd( "!�������", "", 3 ) );// ����� �� ������������
commands.put( "!�������", new Cmd( "!�������", "$n", 4 ) );// ������� ������������ � ����
commands.put( "!�����������", new Cmd( "!�����������", "$n", 5 ) );// ���������� ������ � ����������
commands.put( "!�������", new Cmd( "!�������", "$n", 6 ) );// ������� ������������ �� �����
commands.put( "!��������", new Cmd( "!��������", "", 7 ) );// �������� ���� ����
commands.put( "+��������", new Cmd( "+��������", "", 8 ) );// ��������� �������� �����
commands.put( "!����������", new Cmd( "!����������", "$n", 9 ) );// ������ �����
commands.put( "!���������", new Cmd( "!���������", "$n $c", 10 ) );// �������� ����-���������� ����� �����
commands.put( "!���������", new Cmd( "!���������", "$n", 11 ) );// ������� ����-���������� ����� �����
commands.put( "!����������", new Cmd( "!����������", "$n", 12 ) );// ������ ���������� ���������� � �����
commands.put( "!�������", new Cmd( "!�������", "$n", 13 ) );// �������� �����
commands.put( "!������", new Cmd( "!������", "$s", 14 ) );// ����� ���� � �����
commands.put( "!��������", new Cmd( "!��������", "", 15 ) );// ������� �� ����-��������
}

/**
  * ������� �� ���� - ��������
  */
public String ClanHelp()
{
String s = "";
s += "!������� - ������������� �������� �����.\n";
s += "!�������� - ����� ���� ������.\n";
s += "!������� - ����� ���� ������ �� ��������.\n";
s += "!������� <id> - ��������� ������ ������������ � ������������ �������� � ����.\n";
s += "!����������� - ���������� ������.\n";
s += "!������� <id> - ������� ������������ �� �����.\n";
s += "!�������� - �������� ���� ����.\n";
s += "+�������� - ������� ������� ������ �����.\n";
s += "!���������� <id> - ������ ���� ������ ����������� �����.\n";
s += "!��������� <id> <group> - ��������� ����� ����� ������������ ������.\n";
s += "!��������� <id> - ������ ������� ������ ����� �����.\n";
s += "!���������� - ������ ���������� ����-�����.\n";
s += "!������� - ������� ����.\n";
s += "!������ <text> - �������� ���� �����.\n";
return s;
}

public boolean commandClan( IcqProtocol proc, String uin, String mmsg ) {
String tmsg = mmsg.trim();
int tp = 0;
/////
if( ClanMap.containsKey( uin ) )
if( !ClanMap.get( uin ).isExpire() )
tp = parser.parseCommand( ClanMap.get( uin ).getCmd() );
else {
tp = parser.parseCommand( tmsg );
Order = 0;
CMD = "";
ClanMap.remove( uin );
}else
tp = parser.parseCommand( tmsg );
/////
int tst=0;
if( tp < 0 )
tst=0;
else
tst = tp;
boolean f = true;
switch ( tst )
{
case 1:
commandAddClan( proc, uin, mmsg );
break;
case 2:
commandListClan( proc, uin );
break;
case 3:
commandTopClan( proc, uin );
break;
case 4:
commandAddClansman( proc, uin, parser.parseArgs( tmsg ) );
break;
case 5:
commandIntroduction( proc, uin, parser.parseArgs( tmsg ), mmsg );
break;
case 6:
commandDelClansman( proc, uin, parser.parseArgs( tmsg ) );
break;
case 7:
commandAbandonClan( proc, uin );
break;
case 8:
commandAddClanBall( proc, uin );
break;
case 9:
commandClanMemberList( proc, uin, parser.parseArgs( tmsg ) );
break;
case 10:
commandAddPower( proc, uin, parser.parseArgs( tmsg ) );
break;
case 11:
commandDelPower( proc, uin, parser.parseArgs( tmsg ) );
break;
case 12:
if( !cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin) ) break;
proc.mq.add( uin, ListGroup() );
break;
case 13:
commandDeleteClan( proc, uin, parser.parseArgs( tmsg ) );
break;
case 14:
commandSetInfoClan( proc, uin, parser.parseArgs( tmsg ) );
break;
case 15:
if( !cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin) ) break;
proc.mq.add( uin, ClanHelp() );
break;
default:
f = false;
}
return f;
}


/**
  * ����� ������ ���������� � �����
  */
public String ListGroup(){
String s="��������� ���������� � �����:\n";
for( String f:group.keySet() )
{
s += f + " - " + group.get( f )+"\n";
}
return s;
}

/**
 * �������� �� ������� ���������� � ������
 * @param power
 * @return
 */
 public boolean TestGroup( String group )
 {
 return this.group.containsKey( group );
 }

/**
  * �������� ����������
  * @param uin
  * @param power
  * @return
  */
public boolean Group( String uin, String group )
{
Users u = cmd.srv.us.getUser( uin );
if( !u.clangroup.equals( group ) )
{
return false;
}
return true;
}



/**
 * ������������� �������� ����� :)
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void commandAddClan( IcqProtocol proc, String uin, String mmsg )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
if( !cmd.auth( proc, uin, "setclan" ) ) return;
if( cmd.srv.us.getCountClan() == cmd.psp.getIntProperty( "Clan.MaxCount" ) )
{
proc.mq.add( uin, "�������� ����� ������������� ���������� ������ � ����!" );
return;
}
try
{
if( Order == 0 )
{
Clan_Name( proc, uin, parser.parseArgs( mmsg ), mmsg );
}
if( Order == 1 )
{
Leader_ID( proc, uin, parser.parseArgs( mmsg ), mmsg );
}
if( Order == 2 )
{
Clan_Room( proc, uin, parser.parseArgs( mmsg ), mmsg );
}
if( Order == 3 )
{
Clan_Info( proc, uin, parser.parseArgs( mmsg ), mmsg );
}
if( Order == 4 )
{
/******************************************************/
// ������� ������ ����� ��� ������
Users u = cmd.srv.us.getUser( Leader_ID );
u.clansman = cmd.srv.us.getMaxId();
u.clangroup = "Leader";
cmd.srv.us.updateUser( u );
// ������� ����
Clan clan = new Clan();
clan.setId( cmd.srv.us.getMaxId() );
clan.setLeader( Leader_ID );
clan.setRoom( Clan_Room );
clan.setName( Clan_Name );
clan.setBall( 0 );
clan.setInfo( Clan_Info );
cmd.srv.us.CreateClan( clan );
// �������� ������� ������
Rooms room = new Rooms();
room.setId( Clan_Room );
room.setName( "������� ����� |" + Clan_Name + "|");
room.setTopic( "������� ����� |" + Clan_Name + "|, ����� ����� |" + u.id + "|" + u.localnick );
room.setUser_id( cmd.srv.us.getClan( u.clansman  ).getId() );
cmd.srv.us.createRoom( room );
// ���������
Users uss = cmd.srv.us.getUser( uin );
proc.mq.add( uin, uss.localnick + " ���� ''" + Clan_Name + "'' ������� ������!");
// �������
Order = 0;
CMD = "";
/******************************************************/
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"��� �������� ������ �������� ������!" +
"\n��������� ������� :)");
Order = 0;
CMD = "";
}
}

/**
 * ���������� ������������ � ���� :)
 * @param proc
 * @param uin
 * @param v
 */

private void commandAddClansman( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try {
int id = ( Integer )v.get( 0 );
Users uss = cmd.srv.us.getUser( uin );
Users u = cmd.srv.us.getUser( id );
// ������������ ������� � �����?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " �� �� ��������� ������ �� ������ �� ������" );
return;
}
// ������������ �����?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
if(!Group( uin, "Advance" ))
{
proc.mq.add( uin, uss.localnick + " �� �� ����� ������ ����� ��� �� �� � ����������� ����-������" );
return;
}
}
// ������������ ����������?
if( u.id == 0 )
{
proc.mq.add( uin, uss.localnick + " ������ ������������ ������������" );
return;
}
// ������������ � ����?
if( u.state != UserWork.STATE_CHAT )
{
proc.mq.add( uin, uss.localnick + " ������������ " + u.localnick + " �� � ����." );
return;
}
////
if( u.clansman == 0 )
{
if ( TestInvitation( u.sn ) != 0 )
{
proc.mq.add( uin, uss.localnick + " ������������ " + u.localnick + " ��� ������������ ���� �� ������ �� ����������." );
return;
}else{
SetInvitation( u.sn, uss.clansman );
proc.mq.add( uin, uss.localnick + " ����� ���� ������������ " + u.localnick + " ��������� ������." );
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn, u.localnick + " ��� ���������� � ���� ''" + cmd.srv.us.getClan( uss.clansman ).getName() + "'' ��� " +
"������������ ���������� � ���� ���� �������� !�����������" );
}
} else {
proc.mq.add( uin, uss.localnick + " ������������ " + u.localnick + " ��� ������� � ����� ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" );
}
////
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "��� ���������� ���� ������� �������� ������ - " + ex.getMessage() );
}
}

/**
 * �������� ������� ������ �����
 * @param proc
 * @param uin
 * @param v
 */

private void commandAddClanBall( IcqProtocol proc, String uin )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;    
Users uss = cmd.srv.us.getUser( uin );
// ������������ ������� � �����?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " �� �� ��������� ������ �� ������ �� ������" );
return;
}
if( uss.ball > cmd.psp.getIntProperty( "Clan.Ball_3" ) )
{
Clan clan = cmd.srv.us.getClan( uss.clansman );
clan.setBall( clan.getBall() + cmd.psp.getIntProperty( "Clan.Ball_2" ));
cmd.srv.us.saveClan( clan );
uss.ball -= cmd.psp.getIntProperty( "Clan.Ball_3" );
cmd.srv.us.updateUser( uss );
proc.mq.add( uin, uss.localnick + " ������� ����� ������� �� " + cmd.psp.getIntProperty( "Clan.Ball_2" ) + " � ��� �������� " + uss.ball + " ������");
}else{
proc.mq.add( uin, uss.localnick + " � ��� ������������ " + (uss.ball - cmd.psp.getIntProperty( "Clan.Ball_3" )) + " ������" );
}
}

/**
 * �������� ������������ �� ����� :)
 * @param proc
 * @param uin
 * @param v
 */

private void commandDelClansman( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try {
int id = ( Integer )v.get( 0 );
Users uss = cmd.srv.us.getUser( uin );
Users u = cmd.srv.us.getUser( id );
// ������������ ������� � �����?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " �� �� ��������� ������ �� ������ �� ������" );
return;
}
// ������������ �����?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " �� �� ����� ������ �����" );
return;
}
if( u.id == 0 )
{
proc.mq.add( uin, uss.localnick + " ������ ������������ ������������" );
return;
}
if( u.clansman == uss.clansman )
{
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess( u.basesn ).mq.add( u.sn, " �� ������� �� ����� ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" );
}
Clan clan = cmd.srv.us.getClan( u.clansman );
clan.setBall( clan.getBall() - cmd.psp.getIntProperty( "Clan.Ball_1" ));
cmd.srv.us.saveClan( clan );
u.clansman = 0;
u.clangroup = "";
cmd.srv.us.updateUser( u );
proc.mq.add( uin, uss.localnick + " ������� ������ �� ����" );
}
else
{
proc.mq.add( uin, uss.localnick + " �� �� ������ ������� ������������ " + u.localnick + ", �� ������ � ����� ����� ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" );
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "��� �������� ���� ������� �������� ������ - " + ex.getMessage() );
}
}

/**
 * �������� ����
 * @param proc
 * @param uin
 * @param v
 */

private void commandAbandonClan(IcqProtocol proc, String uin )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try {
Users uss = cmd.srv.us.getUser( uin );
// ������������ ������� � �����?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " �� �� ��������� ������ �� ������ �� ������" );
return;
}
if( uss.clansman != 0 )
{
// ������������ �����?
if( uss.id == cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " �� ����� ������ ����� ��� �� ������ ������ ���:D" );
return;
}
proc.mq.add( uin, uss.localnick + " �� ������� �������� ���� ''" + cmd.srv.us.getClan( uss.clansman ).getName() + "''" );
Clan clan = cmd.srv.us.getClan( uss.clansman );
clan.setBall( clan.getBall() - cmd.psp.getIntProperty( "Clan.Ball_1" ));
cmd.srv.us.saveClan( clan );
uss.clansman = 0;
uss.clangroup = "";
cmd.srv.us.updateUser( uss );
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
}
}

/**
 * ������������� ���������� ������ � �����������
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void commandIntroduction ( IcqProtocol proc, String uin, Vector v, String mmsg )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
if ( TestInvitation( uin ) == 0 )
{
proc.mq.add( uin, "��� �� ���������� �� � ���� �� ������!" );
return;
}
Users uss = cmd.srv.us.getUser( uin );
Users u = cmd.srv.us.getUser( cmd.srv.us.getClan( TestInvitation( uin ) ).getLeader() );
String msg = "";
boolean I = false;
if( ClanMap.containsKey( uin ) )
{
try
{
msg = mmsg;
msg.toLowerCase();
}
catch( NumberFormatException e )
{
proc.mq.add( uin, uss.localnick + " ����� ������ ���� ''��'' ��� ''���'' " );
return;
}
if( !TestMsgInvitation( msg ) )
{
proc.mq.add( uin, uss.localnick + " ����� ������ ���� ''��'' ��� ''���'' " );
return;
}
I = true;
ClanMap.remove( uin );
}
if( !I )
{
proc.mq.add( uin, uss.localnick + " ����������� � ���� ''" + cmd.srv.us.getClan( TestInvitation( uin ) ).getName() + "''\n" +
"''��'' - ��� ����������, ''���'' - ����������." );
ClanMap.put( uin, new ClanMap( uin, mmsg, mmsg, v, 5*60000 ) );
return;
}
if( msg.equals( "��" ) )
{
uss.clansman = TestInvitation( uin );
uss.clangroup = "member";
cmd.srv.us.updateUser( uss );
Clan clan = cmd.srv.us.getClan( uss.clansman );
clan.setBall( clan.getBall() + cmd.psp.getIntProperty( "Clan.Ball_1" ));
cmd.srv.us.saveClan( clan );
proc.mq.add( uin, uss.localnick + " �� ������� �������� � ���� ''" + cmd.srv.us.getClan( TestInvitation( uin ) ).getName() + "''" );
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn, "������������ " + uss.localnick + " ������� � ��� ����");
}
c.remove( uin );
return;
} else {
proc.mq.add( uin, uss.localnick + " �� ���������� �� ���������� � ���� ''" + cmd.srv.us.getClan( TestInvitation( uin ) ).getName() + "''" );
if( u.state == UserWork.STATE_CHAT )
{
proc.mq.add( u.sn, "������������ " + uss.localnick + " ��������� �� ���������� � ��� ����");
}
c.remove( uin );
return;
}
}


/**
 * ��������, ���������� ������������ � ���� ��� ���?
 * @param uin
 * @return
 */

private int TestInvitation( String uin )
{
Integer s = c.get( uin );
if( s == null )
return 0;
else
return s;
}

/**
 * �������� ������ ��� ����������� ������
 * @param msg
 * @return
 */

public boolean TestMsgInvitation( String msg )
{
if( msg.equals( "��" ) || msg.equals( "���" ) )
{
return true;
} else return false;
}

/**
 * ����������� ����� � ������� �������
 * @param uin
 * @param ClanId
 */

private void SetInvitation( String uin, int ClanId )
{
c.put( uin, ClanId );
}

/**
 * ������� ���� ������
 * @param proc
 * @param uin
 */


public void commandListClan( IcqProtocol proc, String uin )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try {
proc.mq.add( uin, cmd.srv.us.ListClan() );
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
}
}

/**
 * ������� �� ��������
 * @param proc
 * @param uin
 */

public void commandTopClan( IcqProtocol proc, String uin )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try {
proc.mq.add( uin, cmd.srv.us.ClanTop() );
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
}
}

public void commandClanMemberList( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try {
int id = ( Integer )v.get( 0 );
if( cmd.srv.us.CheckClan( id ) )
{
proc.mq.add( uin, cmd.srv.us.ClanMemberList( id ) );
}else{
proc.mq.add( uin, "������ ����� �� ����������." );
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
}
}

/**
 * �������� ������
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */

private void Clan_Name( IcqProtocol proc, String uin, Vector v, String mmsg )
{
try
{
Users uss = cmd.srv.us.getUser( uin );
String msg = "";
boolean NAME = false;
if( ClanMap.containsKey( uin ) )
{
try
{
msg = mmsg;
}
catch( NumberFormatException e )
{
proc.mq.add( uin, uss.localnick + " ������� �������� ������" );
return;
}
if( cmd.srv.us.testClanName( msg ) )
{
proc.mq.add( uin, uss.localnick + " ��� ���� ����� ���� � ����� �� ���������\n������� ������ ��������." );
return;
}
if( msg.length() > cmd.psp.getIntProperty( "Clan.NameLenght" ) )
{
proc.mq.add( uin, uss.localnick + " �������� ������ �� ����� ��������� " + cmd.psp.getIntProperty( "Clan.NameLenght" ) + " ��������" );
return;
}
NAME = true;
ClanMap.remove( uin );
}
if( !NAME )
{
CMD = mmsg;
proc.mq.add( uin, uss.localnick + " ������� �������� ������" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Clan_Name = msg;
Order = 1;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
// �������
Order = 0;
CMD = "";
}
}

/**
 * �� ������ ������
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void Leader_ID( IcqProtocol proc, String uin, Vector v, String mmsg )
{
try{
Users uss = cmd.srv.us.getUser( uin );
int id = 0;
boolean LEADER = false;
if( ClanMap.containsKey( uin ) )
{
try
{
id = Integer.parseInt( mmsg );
}
catch( NumberFormatException e )
{
proc.mq.add( uin, uss.localnick + " ������� �� ������ ������" );
return;
}
if( id == 0)
{
proc.mq.add( uin, uss.localnick + " ������ ������������ �� ����������" );
return;
}
LEADER = true;
ClanMap.remove( uin );
}
if( !LEADER )
{
proc.mq.add( uin, uss.localnick + " ������� �� ������ ������" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Leader_ID = id;
Order = 2;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
// �������
Order = 0;
CMD = "";
}
}

/**
 * �� ������� ������
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void Clan_Room( IcqProtocol proc, String uin, Vector v, String mmsg )
{
try{
Users uss = cmd.srv.us.getUser( uin );
int room = 0;
boolean LEADER = false;
if( ClanMap.containsKey( uin ) )
{
try
{
room = Integer.parseInt( mmsg );
}
catch( NumberFormatException e )
{
proc.mq.add( uin, uss.localnick + " ������� �� ������� ������" );
return;
}
if( cmd.srv.us.checkRoom( room ) )
{
proc.mq.add(uin, uss.localnick + " ����� ������� ��� ����������, ������� ������ �������");
return;
}
LEADER = true;
ClanMap.remove( uin );
}
if( !LEADER )
{
proc.mq.add( uin, uss.localnick + " ������� �� ������� ������" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Clan_Room = room;
Order = 3;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
// �������
Order = 0;
CMD = "";
}
}

/**
 *  ���� � �����
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void Clan_Info( IcqProtocol proc, String uin, Vector v, String mmsg )
{
try{
Users uss = cmd.srv.us.getUser( uin );
String msg = "";
boolean INFO = false;
if( ClanMap.containsKey( uin ) )
{
try
{
msg = mmsg;
}
catch( NumberFormatException e )
{
proc.mq.add( uin, uss.localnick + " ������� ������� ���������� � �����" );
return;
}
if( cmd.radm.testMat1( cmd.radm.changeChar( msg ) ))
{
proc.mq.add( uin, uss.localnick + " � ������ ���" );
return;
}
if( msg.length() > cmd.psp.getIntProperty( "Clan.InfoLenght" ) )
{
proc.mq.add( uin, uss.localnick + " ���������� � ������ �� ����� ��������� " + cmd.psp.getIntProperty( "Clan.InfoLenght" ) + " ��������" );
return;
}
INFO = true;
ClanMap.remove( uin );
}
if( !INFO )
{
proc.mq.add( uin, uss.localnick + " ������� ������� ���������� � �����" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Clan_Info = msg;
Order = 4;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "�������� ������ - " + ex.getMessage() );
// �������
Order = 0;
CMD = "";
}
}


/**
  * ��������� ������������ clan-������
  * @param proc
  * @param uin
  * @param v
  */
public void commandAddPower( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try{
String s = (String)v.get( 1 );
int id = (Integer)v.get( 0 );
Users uss = cmd.srv.us.getUser( uin );
Users u = cmd.srv.us.getUser( id );
// ������������ ������� � �����?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " �� �� ��������� ������ �� ������ �� ������" );
return;
}
// ������������ �����?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " �� �� ����� ������ �����" );
return;
}
if( u.id != id )
{
proc.mq.add( uin,"������������ �� ������" );
return;
}
if( u.clangroup.trim().equalsIgnoreCase( s.trim() ) )
{
proc.mq.add( uin,"������������ ��� ����� ��� clan-������" );
return;
}
if( !TestGroup( s ) )
{
proc.mq.add( uin, "����� ������ �� ����������" );
return;
}
if( u.clansman == uss.clansman )
{
u.clangroup = s;
cmd.srv.us.updateUser( u );
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess( u.basesn ).mq.add( u.sn, "� ����� ''" + cmd.srv.us.getClan( u.clansman ).getName() + "'' " +
"���� �������� � clan-������: ''" + s + "''\n��������� !����������");
}
proc.mq.add( uin, "������� ����������" );
}else{
proc.mq.add( uin, "������������ ������� �� � ����� �����" );
return;
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add(uin, "�������� ������ - " + ex.getMessage());
}
}

/**
  * ������ ������������ ����������
  * @param proc
  * @param uin
  * @param v
  */
public void commandDelPower( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try{
int id = (Integer)v.get( 0 );
Users uss = cmd.srv.us.getUser( uin );
Users u = cmd.srv.us.getUser( id );
// ������������ ������� � �����?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " �� �� ��������� ������ �� ������ �� ������" );
return;
}
// ������������ �����?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " �� �� ����� ������ �����" );
return;
}
if( u.id != id )
{
proc.mq.add( uin,"������������ �� ������" );
return;
}
if( u.clansman == uss.clansman )
{
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess( u.basesn ).mq.add( u.sn, "� ����� ''" + cmd.srv.us.getClan( u.clansman ).getName() + "'' " +
"���� ������ clan-������: ''" + u.clangroup + "''\n��������� !����������");
}
u.clangroup = "member";
cmd.srv.us.updateUser( u );
proc.mq.add( uin, "������� ����������" );
}else{
proc.mq.add( uin, "������������ ������� �� � ����� �����" );
return;
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add(uin, "�������� ������ - " + ex.getMessage());
}
}

/**
 * �������� �����.
 * @param proc
 * @param uin
 * @param v
 */

public void commandDeleteClan( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
if( !cmd.auth( proc, uin, "setclan" ) ) return;
int n = ( Integer ) v.get( 0 );
Users uss = cmd.srv.us.getUser( uin );
if ( !cmd.srv.us.CheckClan( n ) )
{
proc.mq.add(uin, "������ ����� �� ����������!");
return;
}
proc.mq.add(uin, uss.localnick + " ���� ''" + cmd.srv.us.getClan( n ).getName() + "'' ��� ������� ������");
cmd.srv.us.ClanDelAllMember( n );
Clan cc = new Clan();
cc.setId( n );
cmd.srv.us.DeleteClan( cc );
}

/**
 * ����� ���� � �����
 * @param proc
 * @param uin
 * @param v
 */

public void commandSetInfoClan( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
String text = ( String )v.get( 0 );
Users uss = cmd.srv.us.getUser( uin );
// ������������ ������� � �����?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " �� �� ��������� ������ �� ������ �� ������" );
return;
}
// ������������ �����?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
if(!Group( uin, "Advance" ))
{
proc.mq.add( uin, uss.localnick + " �� �� ����� ������ ����� ��� �� �� � ����������� ����-������" );
return;
}
}
if( text.equals( "" ) || text.equals( "" ) )
{
proc.mq.add( uin, uss.localnick + " ���� �� ����� ���� ������" );
return;
}
if( text.length() > cmd.psp.getIntProperty( "Clan.InfoLenght" ) )
{
proc.mq.add( uin, uss.localnick + " ���������� � ������ �� ����� ��������� " + cmd.psp.getIntProperty( "Clan.InfoLenght" ) + " ��������" );
return;
}
cmd.srv.us.getClan( uss.clansman ).setInfo( text );
proc.mq.add( uin, uss.localnick + " ���� ������� ��������" );
}

/*
 * ����� ��� �������� ������������� ������
 */

public class ClanMap
{
private String uin;
private long vremia;
private String msg;
private String cmd;
private Vector data;

public ClanMap(String _uin, String _cmd, String _msg, Vector _data, long expire) {
vremia = System.currentTimeMillis() + expire;
uin = _uin;
cmd = _cmd;
msg = _msg;
data = _data;
}

public String getMsg(){return msg;}
public String getUin() {return uin;}
public String getCmd() {return cmd;}
public Vector getData() {return data;}
public boolean isExpire() {return System.currentTimeMillis()>vremia;}
}
}
