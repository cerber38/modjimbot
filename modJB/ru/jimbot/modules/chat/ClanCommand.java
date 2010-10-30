

package ru.jimbot.modules.chat;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
  * Кланы.
  * @author fraer72
  */

public class ClanCommand {
private HashMap< String, Cmd > commands = new HashMap< String, Cmd >();
private HashMap<String, ClanMap> ClanMap;
private ConcurrentHashMap < String, Integer > c; // Запоминаем приглашение в клан
private CommandParser parser;
private ChatCommandProc cmd;
private int Order = 0;
private String CMD = "";
private long TIME = 5*60000;
private String Clan_Name = "";// Название кланна
private int Leader_ID = 0;// Ид лидера клана
private int Clan_Room = 0;// Ид комнаты клана
private String Clan_Info = "";// Информация о клане
// Для хранения полномочий
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
group.put("Advance", "Продвинутый член клана:\n1)Может звать в клан.\n2)Может менять инфо о клане.");

commands.put( "!аддклан", new Cmd( "!аддклан", "", 1 ) );
commands.put( "!кланлист", new Cmd( "!кланлист", "", 2 ) );// Вывод всех кланов
commands.put( "!топклан", new Cmd( "!топклан", "", 3 ) );// Вывод по популярности
commands.put( "!принять", new Cmd( "!принять", "$n", 4 ) );// Принять пользователя в клан
commands.put( "!рассмотреть", new Cmd( "!рассмотреть", "$n", 5 ) );// Рассмотеть заявку о вступлении
commands.put( "!изгнать", new Cmd( "!изгнать", "$n", 6 ) );// Удалить пользователя из клана
commands.put( "!покинуть", new Cmd( "!покинуть", "", 7 ) );// Покинуть свой клан
commands.put( "+кланбалл", new Cmd( "+кланбалл", "", 8 ) );// Повышение рейтинга клана
commands.put( "!листмембер", new Cmd( "!листмембер", "$n", 9 ) );// Состав клана
commands.put( "!аддгруппа", new Cmd( "!аддгруппа", "$n $c", 10 ) );// Добавить клан-полномочие члену клана
commands.put( "!делгруппа", new Cmd( "!делгруппа", "$n", 11 ) );// Забрать клан-полномочие члену клана
commands.put( "!листгруппа", new Cmd( "!листгруппа", "$n", 12 ) );// Список допустимых полномочий в клане
commands.put( "!делклан", new Cmd( "!делклан", "$n", 13 ) );// Удаление клана
commands.put( "!изинфо", new Cmd( "!изинфо", "$s", 14 ) );// Смена инфы о клане
commands.put( "!кланхелп", new Cmd( "!кланхелп", "", 15 ) );// Справка по клан-командам
}


/**
 * Добавление новой команды
 * @param name
 * @param c
 * @return - истина, если команда уже существует
 */
public boolean addCommand(String name, Cmd c)
{
boolean f = commands.containsKey(name);
commands.put(name, c);
return f;
}

/**
  * Справка по клан - командам
  */
public String ClanHelp()
{
String s = "";
    /*s += "!аддклан - Интерактивное создание клана.\n";*/
s += "!кланлист - Вывод всех кланов.\n";
s += "!топклан - Вывод всех кланов по рейтингу.\n";
s += "!принять <id> - Отправить заявку пользователю с предложением вступить в клан.\n";
s += "!рассмотреть - Расмотреть заявку.\n";
s += "!изгнать <id> - Изгнать пользователя из клана.\n";
s += "!покинуть - Покинуть свой клан.\n";
s += "+кланбалл - Поднять рейтинг своего клана.\n";
s += "!листмембер <id> - Список всех членов конкретного клана.\n";
s += "!аддгруппа <id> <group> - Перевести члену клана определенную группу.\n";
s += "!делгруппа <id> - Лишить текущей группы члена клана.\n";
s += "!листгруппа - Список допустимых Клан-групп.\n";
    /*s += "!делклан - Удалить клан.\n";*/
s += "!изинфо <text> - Изменить инфо клана.\n";
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
  * Вывод списка полномочий в клане
  */
public String ListGroup(){
String s="Доступные полномочия в клане:\n";
for( String f:group.keySet() )
{
s += f + " - " + group.get( f )+"\n";
}
return s;
}

/**
 * Проверка на наличие полномочия в списке
 * @param power
 * @return
 */
 public boolean TestGroup( String group )
 {
 return this.group.containsKey( group );
 }

/**
  * Проверка полномочий
  * @param uin
  * @param power
  * @return
  */
public boolean Group( String uin, String group )
{
Users u = cmd.srv.us.getUser( uin );
return u.clangroup.equals( group );
}



/**
 * Интерактивное создание клана :)
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
proc.mq.add( uin, "Привышен лимит максимального количества кланов в чате!" );
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
// Запишим лидеру номер его кланна
Users u = cmd.srv.us.getUser( Leader_ID );
u.clansman = cmd.srv.us.getMaxId();
u.clangroup = "Leader";
cmd.srv.us.updateUser( u );
// Создаем клан
Clan clan = new Clan();
clan.setId( cmd.srv.us.getMaxId() );
clan.setLeader( Leader_ID );
clan.setRoom( Clan_Room );
clan.setName( Clan_Name );
clan.setBall( 0 );
clan.setInfo( Clan_Info );
cmd.srv.us.CreateClan( clan );
// Создадим комнату кланна
Rooms room = new Rooms();
room.setId( Clan_Room );
room.setName( "Комната клана |" + Clan_Name + "|");
room.setTopic( "Комната клана |" + Clan_Name + "|, лидер клана |" + u.id + "|" + u.localnick );
room.setUser_id( cmd.srv.us.getClan( u.clansman  ).getId() );
cmd.srv.us.createRoom( room );
// Оповестим
Users uss = cmd.srv.us.getUser( uin );
proc.mq.add( uin, uss.localnick + " клан ''" + Clan_Name + "'' успешно создан!");
// Сбросим
Order = 0;
CMD = "";
/******************************************************/
}
}
catch (Exception ex)
{
ex.printStackTrace();
proc.mq.add(uin,"При создании кланна возникла ошибка!" +
"\nПовторите попытку :)");
Order = 0;
CMD = "";
}
}

/**
 * Добавление пользователя в клан :)
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
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
// Пользователь лидер?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
if(!Group( uin, "Advance" ))
{
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана или вы не в продвинутой клан-группе" );
return;
}
}
// Пользователь существует?
if( u.id == 0 )
{
proc.mq.add( uin, uss.localnick + " такого пользователя не существует" );
return;
}
// Пользователь в чате?
if( u.state != UserWork.STATE_CHAT )
{
proc.mq.add( uin, uss.localnick + " пользователь " + u.localnick + " не в чате." );
return;
}
////
if( u.clansman == 0 )
{
if ( TestInvitation( u.sn ) != 0 )
{
proc.mq.add( uin, uss.localnick + " пользователь " + u.localnick + " уже расматривает одну из заявок на вступление." );
return;
}else{
SetInvitation( u.sn, uss.clansman );
proc.mq.add( uin, uss.localnick + " ждите пока пользователь " + u.localnick + " расмотрит заявку." );
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn, u.localnick + " Вас приглашают в клан ''" + cmd.srv.us.getClan( uss.clansman ).getName() + "'' для " +
"рассмотрения вступления в этот клан наберите !рассмотреть" );
}
} else {
proc.mq.add( uin, uss.localnick + " пользователь " + u.localnick + " уже состоит в клане ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" );
}
////
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "При добавлении клан мембера возникла ошибка - " + ex.getMessage() );
}
}

/**
 * Добавить рейтинг своюму клану
 * @param proc
 * @param uin
 * @param v
 */

private void commandAddClanBall( IcqProtocol proc, String uin )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;    
Users uss = cmd.srv.us.getUser( uin );
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
if( uss.ball > cmd.psp.getIntProperty( "Clan.Ball_3" ) )
{
Clan clan = cmd.srv.us.getClan( uss.clansman );
clan.setBall( clan.getBall() + cmd.psp.getIntProperty( "Clan.Ball_2" ));
cmd.srv.us.saveClan( clan );
uss.ball -= cmd.psp.getIntProperty( "Clan.Ball_3" );
cmd.srv.us.updateUser( uss );
proc.mq.add( uin, uss.localnick + " рейтинг клана повышен на " + cmd.psp.getIntProperty( "Clan.Ball_2" ) + " у вас осталось " + uss.ball + " баллов");
}else{
proc.mq.add( uin, uss.localnick + " у вас не достаточно " + (uss.ball - cmd.psp.getIntProperty( "Clan.Ball_3" )) + " баллов" );
}
}

/**
 * Удаление пользователя из клана :)
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
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
// Пользователь лидер?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана" );
return;
}
if( u.id == cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " вы лидер своего клана как вы можите кинуть его:D" );
return;
}
if( u.id == 0 )
{
proc.mq.add( uin, uss.localnick + " такого пользователя не существует" );
return;
}
if( u.clansman == uss.clansman )
{
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess( u.basesn ).mq.add( u.sn, " Вы изгнаны из клана ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" );
}
Clan clan = cmd.srv.us.getClan( u.clansman );
clan.setBall( clan.getBall() - cmd.psp.getIntProperty( "Clan.Ball_1" ));
cmd.srv.us.saveClan( clan );
u.clansman = 0;
u.clangroup = "";
cmd.srv.us.updateUser( u );
proc.mq.add( uin, uss.localnick + " успешно изгнан из клан" );
}
else
{
proc.mq.add( uin, uss.localnick + " вы не можите изгнать пользователя " + u.localnick + ", он сотоит в вашем клане ''" + cmd.srv.us.getClan( u.clansman ).getName() + "''" );
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "При удалении клан мембера возникла ошибка - " + ex.getMessage() );
}
}

/**
 * Покинуть клан
 * @param proc
 * @param uin
 * @param v
 */

private void commandAbandonClan(IcqProtocol proc, String uin )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
try {
Users uss = cmd.srv.us.getUser( uin );
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
if( uss.clansman != 0 )
{
// Пользователь лидер?
if( uss.id == cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " вы лидер своего клана как вы можите кинуть его:D" );
return;
}
proc.mq.add( uin, uss.localnick + " вы успешно покинули клан ''" + cmd.srv.us.getClan( uss.clansman ).getName() + "''" );
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
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
}
}

/**
 * Интерактивное расмотрени заявки о всттуплении
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
proc.mq.add( uin, "Вас не приглашали не в один из кланов!" );
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
msg = mmsg.trim();
msg = msg.toLowerCase();
}
catch( NumberFormatException e )
{
proc.mq.add( uin, uss.localnick + " ответ должен быть ''Да'' или ''Нет'' " );
return;
}
if( !TestMsgInvitation( msg ) )
{
proc.mq.add( uin, uss.localnick + " ответ должен быть ''Да'' или ''Нет'' " );
return;
}
I = true;
ClanMap.remove( uin );
}
if( !I )
{
cmd.srv.getIcqProcess(uss.basesn).mq.add(uss.sn, uss.localnick + " приглошение в клан ''" + cmd.srv.us.getClan( TestInvitation( uin ) ).getName() + "''\n" +
"''Да'' - для вступления, ''Нет'' - отказаться." );
ClanMap.put( uin, new ClanMap( uin, mmsg, mmsg, v, 5*60000 ) );
return;
}
if( msg.equals( "да" ) )
{
uss.clansman = TestInvitation( uin );
uss.clangroup = "member";
cmd.srv.us.updateUser( uss );
Clan clan = cmd.srv.us.getClan( uss.clansman );
clan.setBall( clan.getBall() + cmd.psp.getIntProperty( "Clan.Ball_1" ));
cmd.srv.us.saveClan( clan );
proc.mq.add( uin, uss.localnick + " вы успешно вступили в клан ''" + cmd.srv.us.getClan( TestInvitation( uin ) ).getName() + "''" );
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn, "Пользователь " + uss.localnick + " вступил в ваш клан");
}
c.remove( uin );
return;
} else {
proc.mq.add( uin, uss.localnick + " вы отказались от вступления в клан ''" + cmd.srv.us.getClan( TestInvitation( uin ) ).getName() + "''" );
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess(u.basesn).mq.add(u.sn, "Пользователь " + uss.localnick + " отказался от вступления в ваш клан");
}
c.remove( uin );
return;
}
}


/**
 * Проверка, приглашали пользователя в клан или нет?
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
 * Проверка ответа при расмотрении заявки
 * @param msg
 * @return
 */

public boolean TestMsgInvitation( String msg )
{
return msg.equals( "да" ) || msg.equals( "нет" );
}

/**
 * Запоминание клана в который позвали
 * @param uin
 * @param ClanId
 */

private void SetInvitation( String uin, int ClanId )
{
c.put( uin, ClanId );
}

/**
 * Листинг всех кланов
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
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
}
}

/**
 * Листинг по рейтингу
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
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
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
proc.mq.add( uin, "Такого клана не существует." );
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
}
}

/**
 * Название кланна
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
proc.mq.add( uin, uss.localnick + " введите название кланна" );
return;
}
if( cmd.srv.us.testClanName( msg ) )
{
proc.mq.add( uin, uss.localnick + " уже есть такой клан с таким же названием\nВведите другое название." );
return;
}
if( msg.length() > cmd.psp.getIntProperty( "Clan.NameLenght" ) )
{
proc.mq.add( uin, uss.localnick + " название кланна не может привышать " + cmd.psp.getIntProperty( "Clan.NameLenght" ) + " символов" );
return;
}
NAME = true;
ClanMap.remove( uin );
}
if( !NAME )
{
CMD = mmsg;
proc.mq.add( uin, uss.localnick + " введите название кланна" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Clan_Name = msg;
Order = 1;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
// Сбросим
Order = 0;
CMD = "";
}
}

/**
 * Ид лидера кланна
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
proc.mq.add( uin, uss.localnick + " введите ид лидера кланна" );
return;
}
if( id == 0)
{
proc.mq.add( uin, uss.localnick + " такого пользователя не существует" );
return;
}
LEADER = true;
ClanMap.remove( uin );
}
if( !LEADER )
{
proc.mq.add( uin, uss.localnick + " введите ид лидера кланна" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Leader_ID = id;
Order = 2;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
// Сбросим
Order = 0;
CMD = "";
}
}

/**
 * Ид комнаты кланна
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
proc.mq.add( uin, uss.localnick + " введите ид комнаты кланна" );
return;
}
if( cmd.srv.us.checkRoom( room ) )
{
proc.mq.add(uin, uss.localnick + " такая комната уже существует, введите другую комнату");
return;
}
LEADER = true;
ClanMap.remove( uin );
}
if( !LEADER )
{
proc.mq.add( uin, uss.localnick + " введите ид комнаты кланна" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Clan_Room = room;
Order = 3;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
// Сбросим
Order = 0;
CMD = "";
}
}

/**
 *  Инфо о клане
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
proc.mq.add( uin, uss.localnick + " введите краткую информацию о клане" );
return;
}
if( cmd.radm.testMat1( cmd.radm.changeChar( msg ) ))
{
proc.mq.add( uin, uss.localnick + " в тексте мат" );
return;
}
if( msg.length() > cmd.psp.getIntProperty( "Clan.InfoLenght" ) )
{
proc.mq.add( uin, uss.localnick + " информация о кланне не может привышать " + cmd.psp.getIntProperty( "Clan.InfoLenght" ) + " символов" );
return;
}
INFO = true;
ClanMap.remove( uin );
}
if( !INFO )
{
proc.mq.add( uin, uss.localnick + " введите краткую информацию о клане" );
ClanMap.put( uin, new ClanMap( uin, CMD, mmsg, v, TIME ) );
return;
}
Clan_Info = msg;
Order = 4;
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add( uin, "Возникла ошибка - " + ex.getMessage() );
// Сбросим
Order = 0;
CMD = "";
}
}


/**
  * Присвоить пользователю clan-группы
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
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
// Пользователь лидер?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана" );
return;
}
if( u.id != id )
{
proc.mq.add( uin,"Пользователь не найден" );
return;
}
if( u.clangroup.trim().equalsIgnoreCase( s.trim() ) )
{
proc.mq.add( uin,"Пользователь уже имеет эту clan-группу" );
return;
}
if( !TestGroup( s ) )
{
proc.mq.add( uin, "Такой группы не существует" );
return;
}
if( u.clansman == uss.clansman )
{
u.clangroup = s;
cmd.srv.us.updateUser( u );
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess( u.basesn ).mq.add( u.sn, "В клане ''" + cmd.srv.us.getClan( u.clansman ).getName() + "'' " +
"тебя перевели в clan-группу: ''" + s + "''\nПодробнее !листгруппа");
}
proc.mq.add( uin, "Успешно выполненно" );
}else{
proc.mq.add( uin, "Пользователь состоит не в вашем клане" );
return;
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
  * Лешить пользователя полномочия
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
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
// Пользователь лидер?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана" );
return;
}
if( u.id != id )
{
proc.mq.add( uin,"Пользователь не найден" );
return;
}
if( u.clansman == uss.clansman )
{
if( u.state == UserWork.STATE_CHAT )
{
cmd.srv.getIcqProcess( u.basesn ).mq.add( u.sn, "В клане ''" + cmd.srv.us.getClan( u.clansman ).getName() + "'' " +
"тебя лишили clan-группы: ''" + u.clangroup + "''\nПодробнее !листгруппа");
}
u.clangroup = "member";
cmd.srv.us.updateUser( u );
proc.mq.add( uin, "Успешно выполненно" );
}else{
proc.mq.add( uin, "Пользователь состоит не в вашем клане" );
return;
}
}
catch ( Exception ex )
{
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
 * Удаление клана.
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
proc.mq.add(uin, "Такого клана не существует!");
return;
}
proc.mq.add(uin, uss.localnick + " клан ''" + cmd.srv.us.getClan( n ).getName() + "'' был успешно удален");
Rooms r = new Rooms();
r.setId( cmd.srv.us.getClan( n ).getRoom() );
cmd.srv.us.deleteRoom(r);
cmd.srv.us.ClanDelAllMember( n );
Clan cc = new Clan();
cc.setId( n );
cmd.srv.us.DeleteClan( cc );
}

/**
 * Смена инфы о клане
 * @param proc
 * @param uin
 * @param v
 */

public void commandSetInfoClan( IcqProtocol proc, String uin, Vector v )
{
if( !cmd.isChat( proc, uin ) && !cmd.psp.testAdmin( uin ) ) return;
String text = ( String )v.get( 0 );
Users uss = cmd.srv.us.getUser( uin );
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
// Пользователь лидер?
if( uss.id != cmd.srv.us.getClan( uss.clansman ).getLeader() )
{
if(!Group( uin, "Advance" ))
{
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана или вы не в продвинутой клан-группе" );
return;
}
}
if( text.equals( "" ) || text.equals( "" ) )
{
proc.mq.add( uin, uss.localnick + " инфа не может быть пустой" );
return;
}
if( text.length() > cmd.psp.getIntProperty( "Clan.InfoLenght" ) )
{
proc.mq.add( uin, uss.localnick + " информация о кланне не может привышать " + cmd.psp.getIntProperty( "Clan.InfoLenght" ) + " символов" );
return;
}
cmd.srv.us.getClan( uss.clansman ).setInfo( text );
cmd.srv.us.saveClan(cmd.srv.us.getClan( uss.clansman ));
proc.mq.add( uin, uss.localnick + " инфа успешно изменина" );
}

/*
 * Масив для хранения промежуточных данных
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
