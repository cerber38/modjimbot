

package ru.jimbot.modules.chat;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.AboutExtend;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandExtend;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;

/**
  * Кланы.
  * @author fraer72
  */

public class ClanCommand {
private HashMap< String, Cmd > commands = new HashMap< String, Cmd >();
private ConcurrentHashMap < String, Integer > c;
private CommandParser parser;
private ChatServer srv;
private ChatProps psp;
private HashMap< String, String > group = new HashMap< String, String >();
private HashMap<String, AboutExtend> clanAbout;
private ConcurrentHashMap <String, ClanInfo> clanInfo;
private HashMap<String, CommandExtend> ComClan;

public ClanCommand(ChatServer srv, ChatProps psp){
parser = new CommandParser(commands);
this.srv = srv;
this.psp = psp;
clanAbout = new HashMap<String, AboutExtend>();
clanInfo = new ConcurrentHashMap<String, ClanInfo>();
ComClan = new HashMap<String, CommandExtend>();
c = new ConcurrentHashMap< String, Integer >();
init();
}

private void init(){
group.put("Advance", "Продвинутый член клана:\n1)Может звать в клан.\n2)Может менять инфо о клане.");

commands.put("!аддклан", new Cmd("!аддклан", "", 1));
commands.put("!кланлист", new Cmd("!кланлист", "", 2));
commands.put("!топклан", new Cmd("!топклан", "", 3));
commands.put("!принять", new Cmd("!принять", "$n", 4));
commands.put("!рассмотреть", new Cmd("!рассмотреть", "$n", 5));
commands.put("!изгнать", new Cmd("!изгнать", "$n", 6));
commands.put("!покинуть", new Cmd("!покинуть", "", 7));
commands.put("+кланбалл", new Cmd("+кланбалл", "", 8));
commands.put("!листмембер", new Cmd("!листмембер", "$n", 9));
commands.put("!аддгруппа", new Cmd("!аддгруппа", "$n $c", 10));
commands.put("!делгруппа", new Cmd("!делгруппа", "$n", 11));
commands.put("!листгруппа", new Cmd("!листгруппа", "$n", 12));
commands.put("!делклан", new Cmd("!делклан", "$n", 13));
commands.put("!изинфо", new Cmd("!изинфо", "$s", 14));
commands.put("!кланхелп", new Cmd("!кланхелп", "", 15));
commands.put("!изназвание", new Cmd("!изназвание", "$s", 16));
commands.put("!символ", new Cmd("!символ", "$s", 17));
}


/**
 * Добавление новой команды
 * @param name
 * @param c
 * @return - истина, если команда уже существует
 */
public boolean addCommand(String name, Cmd c){
boolean f = commands.containsKey(name);
commands.put(name, c);
return f;
}

/**
  * Справка по клан - командам
  */
public String ClanHelp(){
String s = "";
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
s += "!изинфо <text> - Изменить инфо клана.\n";
s += "!изназвание <text> - Изменить название клана.\n";
if(psp.getBooleanProperty("Clan.Symbol"))
s += "!символ <text> - Установить символ клана.\n";
return s;
}


public boolean commandClan(IcqProtocol proc, String uin, String mmsg) {
String tmsg = mmsg.trim();
if(clanAbout.containsKey(uin))
if(!clanAbout.get(uin).isExpire()){
commandAddClan(proc, uin, mmsg);
return true;
}else{
clanAbout.remove(uin);
clanInfo.remove(uin);
}
int tp = parser.parseCommand(tmsg);
int tst=0;
if(tp < 0)
tst=0;
else
tst = tp;
boolean f = true;
switch (tst){
case 1:
commandAddClan(proc, uin, mmsg);
break;
case 2:
commandListClan(proc, uin);
break;
case 3:
commandTopClan(proc, uin);
break;
case 4:
commandAddClansman(proc, uin, parser.parseArgs(tmsg));
break;
case 5:
commandIntroduction(proc, uin, parser.parseArgs(tmsg), mmsg);
break;
case 6:
commandDelClansman(proc, uin, parser.parseArgs(tmsg));
break;
case 7:
commandAbandonClan(proc, uin);
break;
case 8:
commandAddClanBall(proc, uin);
break;
case 9:
commandClanMemberList(proc, uin, parser.parseArgs(tmsg));
break;
case 10:
commandAddPower(proc, uin, parser.parseArgs(tmsg));
break;
case 11:
commandDelPower(proc, uin, parser.parseArgs(tmsg));
break;
case 12:
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) break;
proc.mq.add(uin, ListGroup());
break;
case 13:
commandDeleteClan(proc, uin, parser.parseArgs(tmsg));
break;
case 14:
commandSetInfoClan(proc, uin, parser.parseArgs(tmsg));
break;
case 15:
if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) break;
proc.mq.add(uin, ClanHelp());
break;
case 16:
commandSetNameClan(proc, uin, parser.parseArgs(tmsg));
break;
case 17:
if(psp.getBooleanProperty("Clan.Symbol"))
commandSetSymbolClan(proc, uin, parser.parseArgs(tmsg));
else proc.mq.add(uin, "Команда закрыта администрицией!");
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
String s = "Доступные полномочия в клане:\n";
for(String f:group.keySet())
s += f + " - " + group.get(f)+"\n";
return s;
}

/**
 * Проверка на наличие полномочия в списке
 * @param power
 * @return
 */
 public boolean TestGroup(String group){
 return this.group.containsKey(group);
 }

/**
  * Проверка полномочий
  * @param uin
  * @param power
  * @return
  */
public boolean Group(String uin, String group){
Users u = srv.us.getUser(uin);
return u.clangroup.equals(group);
}


     /**
      * Регулируем подачу вопроса
      * @param about
      * @param answer
      */

  private void setAnswer(AboutExtend about, boolean answer){
  about.setAnswer(answer);
  clanAbout.put(about.getUin(), about);
  }

     /**
      * На следующий вопрос
      * @param about
      */

  private void nextQuestion(AboutExtend about){
  about.setOrder();
  clanAbout.put(about.getUin(), about);
  }



/**
 * Интерактивное создание клана :)
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void commandAddClan(IcqProtocol proc, String uin, String mmsg){
if(!((ChatCommandProc)srv.cmd).isChat( proc, uin ) && !psp.testAdmin( uin )) return;
if(!((ChatCommandProc)srv.cmd).auth( proc, uin, "setclan" )) return;
if(((ChatCommandProc)srv.cmd).srv.us.getCountClan() == psp.getIntProperty("Clan.MaxCount")){
proc.mq.add(uin, "Привышен лимит максимального количества кланов в чате!");
return;
}
try{
if(!clanAbout.containsKey(uin)){
clanAbout.put(uin, new AboutExtend(uin, 5*60000, false));
}
AboutExtend about = clanAbout.get(uin);
switch (about.getOrder()){
case 0:
proc.mq.add(uin,srv.us.getUser(uin).localnick + " введите название клана.");
nextQuestion(about);
break;
case 1:
clanName(proc, uin, mmsg, about);
if(!about.getAnswer()){
proc.mq.add(uin,srv.us.getUser(uin).localnick + " введите ид лидера клана.");
setAnswer(about, true);
}
break;
case 2:
leader_ID(proc, uin, mmsg, about);
if(!about.getAnswer()){
proc.mq.add(uin,srv.us.getUser(uin).localnick + " введите ид комнаты клана.");
setAnswer(about, true);
}
break;
case 3:
clanRoom(proc, uin, mmsg, about);
if(!about.getAnswer()){
proc.mq.add(uin,srv.us.getUser(uin).lname + " введите информацию о клане.");
setAnswer(about, true);
}
break;
case 4:
clanInfo(proc, uin, mmsg, about);
ClanInfo cl = clanInfo.get(uin);
Users u = srv.us.getUser(cl.getLeader_id());
u.clansman = srv.us.getMaxId();
u.clangroup = "Leader";
srv.us.updateUser(u);
// Создаем клан
Clan clan = new Clan();
clan.setId(srv.us.getMaxId());
clan.setLeader( cl.getLeader_id() );
clan.setRoom(cl.getClanRoom());
clan.setName(cl.getClanName());
clan.setBall(0);
clan.setInfo(cl.getInfoName());
clan.setSymbol("");
srv.us.CreateClan( clan );
// Создадим комнату кланна
Rooms room = new Rooms();
room.setId(cl.getClanRoom());
room.setName("Комната клана |" + cl.getClanName() + "|");
room.setTopic( "Комната клана |" + cl.getClanName() + "|, лидер клана |" + u.id + "|" + u.localnick);
room.setUser_id(srv.us.getClan(u.clansman).getId());
srv.us.createRoom(room);
Users uss = srv.us.getUser( uin );
proc.mq.add(uin, uss.localnick + " клан ''" + cl.getClanName() + "'' успешно создан!");
clanAbout.remove(uin);
clanInfo.remove(uin);
break;
default:
}
}catch (Exception ex){
proc.mq.add(uin,"При создании кланна возникла ошибка!" +
"\nПовторите попытку :)");
}
}


/**
 * Название кланна
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */

private void clanName(IcqProtocol proc, String uin, String mmsg, AboutExtend about){
try
{
Users uss = srv.us.getUser(uin);
if(!((ChatCommandProc)srv.cmd).radm.testMat1(((ChatCommandProc)srv.cmd).radm.changeChar(mmsg))){
proc.mq.add(uin, uss.localnick + " в название клана мат");
return;
}
if(srv.us.testClanName(mmsg)){
proc.mq.add(uin, uss.localnick + " уже есть такой клан с таким же названием\nВведите другое название.");
return;
}
if(mmsg.length() > psp.getIntProperty("Clan.NameLenght")){
proc.mq.add(uin, uss.localnick + " название клана не может привышать " + psp.getIntProperty("Clan.NameLenght") + " символов");
return;
}
setAnswer(about, false);
nextQuestion(about);
ClanInfo cl = new ClanInfo(mmsg);
clanInfo.put(uin, cl);
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
 * Ид лидера кланна
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void leader_ID(IcqProtocol proc, String uin, String mmsg, AboutExtend about){
try{
Users uss = srv.us.getUser(uin);
int id = 0;
try{
id = Integer.parseInt(mmsg);
}catch(NumberFormatException e){
proc.mq.add(uin, uss.localnick + " введите ид лидера кланна");
return;
}
if(id == 0){
proc.mq.add(uin, uss.localnick + " такого пользователя не существует");
return;
}
setAnswer(about, false);
nextQuestion(about);
ClanInfo cl = clanInfo.get(uin);
cl.setLeader_id(id);
clanInfo.put(uin, cl);
}catch (Exception ex ){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
 * Ид комнаты кланна
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void clanRoom( IcqProtocol proc, String uin, String mmsg, AboutExtend about){
try{
Users uss = srv.us.getUser(uin);
int room = 0;
try{
room = Integer.parseInt(mmsg);
}catch(NumberFormatException e){
proc.mq.add( uin, uss.localnick + " введите ид комнаты кланна" );
return;
}
if(srv.us.checkRoom(room)){
proc.mq.add(uin, uss.localnick + " такая комната уже существует, введите другую комнату");
return;
}
setAnswer(about, false);
nextQuestion(about);
ClanInfo cl = clanInfo.get(uin);
cl.setClanRoom(room);
clanInfo.put(uin, cl);
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
 *  Инфо о клане
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void clanInfo(IcqProtocol proc, String uin, String mmsg, AboutExtend about){
try{
Users uss = srv.us.getUser(uin);
if(!((ChatCommandProc)srv.cmd).radm.testMat1(((ChatCommandProc)srv.cmd).radm.changeChar(mmsg))){
proc.mq.add(uin, uss.localnick + " в тексте мат");
return;
}
if(mmsg.length() > psp.getIntProperty("Clan.InfoLenght")){
proc.mq.add(uin, uss.localnick + " информация о кланне не может привышать " + psp.getIntProperty("Clan.InfoLenght") + " символов");
return;
}
setAnswer(about, false);
nextQuestion(about);
ClanInfo cl = clanInfo.get(uin);
cl.setInfoName(mmsg);
clanInfo.put(uin, cl);
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
 * Добавление пользователя в клан :)
 * @param proc
 * @param uin
 * @param v
 */

private void commandAddClansman(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
try {
int id = (Integer)v.get(0);
Users uss = srv.us.getUser(uin);
Users u = srv.us.getUser(id);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
// Пользователь лидер?
if(uss.id != srv.us.getClan(uss.clansman).getLeader() && !Group(uin, "Advance")){
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана или вы не в продвинутой клан-группе");
return;
}
// Пользователь существует?
if(u.id == 0){
proc.mq.add(uin, uss.localnick + " такого пользователя не существует");
return;
}
// Пользователь в чате?
if( u.state != UserWork.STATE_CHAT ){
proc.mq.add(uin, uss.localnick + " пользователь " + u.localnick + " не в чате.");
return;
}
if(u.clansman == 0){
if(TestInvitation( u.sn ) != 0){
proc.mq.add(uin, uss.localnick + " пользователь " + u.localnick + " уже расматривает одну из заявок на вступление.");
return;
}else{
SetInvitation(u.sn, uss.clansman);
proc.mq.add(uin, uss.localnick + " ждите пока пользователь " + u.localnick + " расмотрит заявку.");
srv.getIcqProcess(u.basesn).mq.add(u.sn, u.localnick + " Вас приглашают в клан ''" + srv.us.getClan(uss.clansman).getName() + "'' для " +
"рассмотрения вступления в этот клан наберите !рассмотреть");
}
}else{
proc.mq.add(uin, uss.localnick + " пользователь " + u.localnick + " уже состоит в клане ''" + srv.us.getClan(u.clansman).getName() + "''");
}
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin, "При добавлении клан мембера возникла ошибка - " + ex.getMessage());
}
}

/**
 * Добавить рейтинг своюму клану
 * @param proc
 * @param uin
 * @param v
 */

private void commandAddClanBall(IcqProtocol proc, String uin){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
Users uss = srv.us.getUser(uin);
// Пользователь состаит в клане?
if( uss.clansman == 0 )
{
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
if(uss.ball > psp.getIntProperty("Clan.Ball_3"))
{
Clan clan = srv.us.getClan(uss.clansman);
clan.setBall(clan.getBall() + psp.getIntProperty("Clan.Ball_2"));
srv.us.saveClan(clan);
uss.ball -= psp.getIntProperty("Clan.Ball_3");
srv.us.updateUser(uss);
proc.mq.add(uin, uss.localnick + " рейтинг клана повышен на " + psp.getIntProperty("Clan.Ball_2") + " у вас осталось " + uss.ball + " баллов");
}else{
proc.mq.add(uin, uss.localnick + " у вас не достаточно " + (uss.ball - psp.getIntProperty("Clan.Ball_3")) + " баллов");
}
}

/**
 * Удаление пользователя из клана :)
 * @param proc
 * @param uin
 * @param v
 */

private void commandDelClansman(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
try {
int id = (Integer)v.get(0);
Users uss = srv.us.getUser(uin);
Users u = srv.us.getUser(id);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add( uin, uss.localnick + " вы не являетесь членом не одного из кланов" );
return;
}
// Пользователь лидер?
if(uss.id != srv.us.getClan(uss.clansman).getLeader()){
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана" );
return;
}
if(u.id == srv.us.getClan(uss.clansman).getLeader()){
proc.mq.add( uin, uss.localnick + " вы лидер своего клана как вы можите кинуть его:D");
return;
}
if(u.id == 0){
proc.mq.add( uin, uss.localnick + " такого пользователя не существует" );
return;
}
if(u.clansman == uss.clansman){
if(u.state == UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn, " Вы изгнаны из клана ''" + srv.us.getClan(u.clansman).getName() + "''");
}
Clan clan = srv.us.getClan(u.clansman);
clan.setBall(clan.getBall() - psp.getIntProperty("Clan.Ball_1"));
srv.us.saveClan(clan);
u.clansman = 0;
u.clangroup = "";
srv.us.updateUser(u);
proc.mq.add(uin, uss.localnick + " успешно изгнан из клан");
}else{
proc.mq.add(uin, uss.localnick + " вы не можите изгнать пользователя " + u.localnick + ", он сотоит в вашем клане ''" + srv.us.getClan(u.clansman).getName() + "''");
}
}catch ( Exception ex ){
ex.printStackTrace();
proc.mq.add(uin, "При удалении клан мембера возникла ошибка - " + ex.getMessage());
}
}

/**
 * Покинуть клан
 * @param proc
 * @param uin
 * @param v
 */

private void commandAbandonClan(IcqProtocol proc, String uin ){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
try {
Users uss = srv.us.getUser(uin);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
if(uss.clansman != 0){
// Пользователь лидер?
if(uss.id == srv.us.getClan(uss.clansman).getLeader()){
proc.mq.add(uin, uss.localnick + " вы лидер своего клана как вы можите кинуть его:D");
return;
}
proc.mq.add(uin, uss.localnick + " вы успешно покинули клан ''" + srv.us.getClan(uss.clansman).getName() + "''");
Clan clan = srv.us.getClan(uss.clansman);
clan.setBall(clan.getBall() - psp.getIntProperty("Clan.Ball_1"));
srv.us.saveClan(clan);
uss.clansman = 0;
uss.clangroup = "";
srv.us.updateUser(uss);
}
}catch ( Exception ex ){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
 * Интерактивное расмотрени заявки о всттуплении
 * @param proc
 * @param uin
 * @param v
 * @param mmsg
 */
private void commandIntroduction (IcqProtocol proc, String uin, Vector v, String mmsg){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
if (TestInvitation(uin) == 0){
proc.mq.add(uin, "Вас не приглашали не в один из кланов!");
return;
}
Users uss = srv.us.getUser(uin);
Users u = srv.us.getUser(srv.us.getClan(TestInvitation(uin)).getLeader());
String msg = "";
boolean I = false;
if(ComClan.containsKey(uin)){
msg = msg.toLowerCase().trim();
if(!TestMsgInvitation(msg)){
proc.mq.add(uin, uss.localnick + " ответ должен быть ''Да'' или ''Нет'' ");
return;
}
I = true;
ComClan.remove(uin);
}
if(!I){
srv.getIcqProcess(uss.basesn).mq.add(uss.sn, uss.localnick + " приглошение в клан ''" + srv.us.getClan(TestInvitation(uin)).getName() + "''\n" +
"''Да'' - для вступления, ''Нет'' - отказаться." );
ComClan.put( uin, new CommandExtend( uin, mmsg, mmsg, v, 5*60000 ) );
return;
}
if( msg.equals( "да" ) ){
uss.clansman = TestInvitation(uin);
uss.clangroup = "member";
srv.us.updateUser(uss);
Clan clan = srv.us.getClan( uss.clansman );
clan.setBall(clan.getBall() + psp.getIntProperty("Clan.Ball_1"));
srv.us.saveClan(clan);
proc.mq.add(uin, uss.localnick + " вы успешно вступили в клан ''" + srv.us.getClan(TestInvitation(uin)).getName() + "''" );
if(u.state == UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn, "Пользователь " + uss.localnick + " вступил в ваш клан");
}
c.remove(uin);
return;
} else {
proc.mq.add(uin, uss.localnick + " вы отказались от вступления в клан ''" + srv.us.getClan(TestInvitation(uin)).getName() + "''" );
if(u.state == UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn, "Пользователь " + uss.localnick + " отказался от вступления в ваш клан");
}
c.remove( uin );
}
}


/**
 * Проверка, приглашали пользователя в клан или нет?
 * @param uin
 * @return
 */

private int TestInvitation(String uin){
Integer s = c.get(uin);
if(s == null)
return 0;
else
return s;
}

/**
 * Проверка ответа при расмотрении заявки
 * @param msg
 * @return
 */

public boolean TestMsgInvitation(String msg){
return msg.equals("да") || msg.equals( "нет");
}

/**
 * Запоминание клана в который позвали
 * @param uin
 * @param ClanId
 */

private void SetInvitation(String uin, int ClanId){
c.put(uin, ClanId);
}

/**
 * Листинг всех кланов
 * @param proc
 * @param uin
 */


public void commandListClan(IcqProtocol proc, String uin){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
try {
proc.mq.add(uin, srv.us.ListClan());
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

/**
 * Листинг по рейтингу
 * @param proc
 * @param uin
 */

public void commandTopClan(IcqProtocol proc, String uin){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
try {
proc.mq.add(uin, srv.us.ClanTop());
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}

public void commandClanMemberList(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat( proc, uin ) && !psp.testAdmin(uin)) return;
try {
int id = (Integer)v.get(0);
if(srv.us.CheckClan(id)){
proc.mq.add(uin, srv.us.ClanMemberList(id));
}else{
proc.mq.add(uin, "Такого клана не существует.");
}
}catch (Exception ex){
ex.printStackTrace();
proc.mq.add(uin, "Возникла ошибка - " + ex.getMessage());
}
}




/**
  * Присвоить пользователю clan-группы
  * @param proc
  * @param uin
  * @param v
  */
public void commandAddPower(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
try{
String s = (String)v.get(1);
int id = (Integer)v.get(0);
Users uss = srv.us.getUser(uin);
Users u = srv.us.getUser(id);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
// Пользователь лидер?
if(uss.id != srv.us.getClan(uss.clansman).getLeader()){
proc.mq.add(uin, uss.localnick + " вы не лидер своего клана");
return;
}
if(u.id != id){
proc.mq.add(uin,"Пользователь не найден");
return;
}
if(u.clangroup.trim().equalsIgnoreCase(s.trim())){
proc.mq.add(uin,"Пользователь уже имеет эту clan-группу");
return;
}
if(!TestGroup(s)){
proc.mq.add(uin, "Такой группы не существует");
return;
}
if(u.clansman == uss.clansman){
u.clangroup = s;
srv.us.updateUser(u);
if(u.state == UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn, "В клане ''" + srv.us.getClan(u.clansman).getName() + "'' " +
"тебя перевели в clan-группу: ''" + s + "''\nПодробнее !листгруппа");
}
proc.mq.add(uin, "Успешно выполненно");
}else{
proc.mq.add(uin, "Пользователь состоит не в вашем клане");
return;
}
}catch ( Exception ex ){
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
public void commandDelPower( IcqProtocol proc, String uin, Vector v ){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
try{
int id = (Integer)v.get(0);
Users uss = srv.us.getUser(uin);
Users u = srv.us.getUser(id);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
// Пользователь лидер?
if(uss.id != srv.us.getClan(uss.clansman).getLeader()){
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана");
return;
}
if(u.id != id){
proc.mq.add( uin,"Пользователь не найден");
return;
}
if(u.clansman == uss.clansman){
if(u.state == UserWork.STATE_CHAT){
srv.getIcqProcess(u.basesn).mq.add(u.sn, "В клане ''" + srv.us.getClan(u.clansman).getName() + "'' " +
"тебя лишили clan-группы: ''" + u.clangroup + "''\nПодробнее !листгруппа");
}
u.clangroup = "member";
srv.us.updateUser(u);
proc.mq.add(uin, "Успешно выполненно");
}else{
proc.mq.add(uin, "Пользователь состоит не в вашем клане");
return;
}
}catch (Exception ex){
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

public void commandDeleteClan(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
if(!((ChatCommandProc)srv.cmd).auth(proc, uin, "setclan" )) return;
int n = (Integer) v.get(0);
Users uss = srv.us.getUser(uin);
if (!srv.us.CheckClan(n)){
proc.mq.add(uin, "Такого клана не существует!");
return;
}
proc.mq.add(uin, uss.localnick + " клан ''" + srv.us.getClan(n).getName() + "'' был успешно удален");
Rooms r = new Rooms();
r.setId(srv.us.getClan(n).getRoom());
srv.us.deleteRoom(r);
srv.us.ClanDelAllMember(n);
Clan cc = new Clan();
cc.setId(n);
srv.us.DeleteClan(cc);
}

/**
 * Смена инфы о клане
 * @param proc
 * @param uin
 * @param v
 */

public void commandSetInfoClan(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
String text = (String)v.get(0);
Users uss = srv.us.getUser(uin);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
// Пользователь лидер?
if(uss.id != srv.us.getClan(uss.clansman).getLeader() && !Group( uin, "Advance" )){
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана или вы не в продвинутой клан-группе");
return;
}
if(text.equals("")){
proc.mq.add( uin, uss.localnick + " инфа не может быть пустой");
return;
}
if(text.length() > psp.getIntProperty( "Clan.InfoLenght" )){
proc.mq.add(uin, uss.localnick + " информация о кланне не может привышать " + psp.getIntProperty("Clan.InfoLenght") + " символов");
return;
}
srv.us.getClan(uss.clansman).setInfo(text);
srv.us.saveClan(srv.us.getClan(uss.clansman));
proc.mq.add(uin, uss.localnick + " инфа успешно изменина");
}


/**
 * Смена названия
 * @param proc
 * @param uin
 * @param v
 */

public void commandSetNameClan(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
String text = (String)v.get(0);
Users uss = srv.us.getUser(uin);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
// Пользователь лидер?
if(uss.id != srv.us.getClan(uss.clansman).getLeader() && !Group( uin, "Advance" )){
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана или вы не в продвинутой клан-группе");
return;
}
if(text.equals("")){
proc.mq.add( uin, uss.localnick + " название клана не может быть пустым");
return;
}
if(text.length() > psp.getIntProperty("Clan.NameLenght")){
proc.mq.add(uin, uss.localnick + " название клана не может привышать " + psp.getIntProperty("Clan.NameLenght") + " символов");
return;
}
srv.us.getClan(uss.clansman).setName(text);
srv.us.saveClan(srv.us.getClan(uss.clansman));
proc.mq.add(uin, uss.localnick + " название клана успешно изменино.");
}

/**
 * Смена названия
 * @param proc
 * @param uin
 * @param v
 */

public void commandSetSymbolClan(IcqProtocol proc, String uin, Vector v){
if(!((ChatCommandProc)srv.cmd).isChat(proc, uin) && !psp.testAdmin(uin)) return;
String text = (String)v.get(0);
Users uss = srv.us.getUser(uin);
// Пользователь состаит в клане?
if(uss.clansman == 0){
proc.mq.add(uin, uss.localnick + " вы не являетесь членом не одного из кланов");
return;
}
// Пользователь лидер?
if(uss.id != srv.us.getClan(uss.clansman).getLeader() && !Group( uin, "Advance" )){
proc.mq.add( uin, uss.localnick + " вы не лидер своего клана или вы не в продвинутой клан-группе");
return;
}
if(text.length() > psp.getIntProperty("Clan.SymbolLenght")){
proc.mq.add(uin, uss.localnick + " название клана не может привышать " + psp.getIntProperty("Clan.SymbolLenght") + " символов");
return;
}
srv.us.getClan(uss.clansman).setSymbol(text);
srv.us.saveClan(srv.us.getClan(uss.clansman));
if(text.equals(""))
proc.mq.add( uin, uss.localnick + " символ клана убран.");
else
proc.mq.add(uin, uss.localnick + " символ клана успешно установлен.");
}

    class ClanInfo {
    private int leader_id = 0;
    private int clan_room = 0;
    private String clan_name = "";
    private String clan_info = "";

    public ClanInfo(String clan_name){
    this.clan_name = clan_name;
    }


    public int getLeader_id(){
    return leader_id;
    }

    public void setLeader_id(int leader_id){
    this.leader_id = leader_id;
    }

    public int getClanRoom(){
    return clan_room;
    }

    public void setClanRoom(int clan_room){
    this.clan_room = clan_room;
    }

    public String getClanName(){
    return clan_name;
    }
    

    public String getInfoName(){
    return clan_info;
    }

    public void setInfoName(String clan_info){
    this.clan_info = clan_info;
    }

    }


}
