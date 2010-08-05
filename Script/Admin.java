import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Enumeration;
import ru.jimbot.*;
import ru.jimbot.util.*;
import java.util.Random;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.modules.chat.RobAdmin;
import ru.jimbot.modules.chat.MsgElement;
import ru.jimbot.modules.chat.ChatProps;
import ru.jimbot.modules.chat.ChatCommandProc;
import java.util.Random; 
import ru.jimbot.modules.chat.Users;
import java.sql.*;

    // RobAdmin adm;
    adm.NICK = adm.srv.getProps().getStringProperty("radm.nick");
    adm.ALT_NICK =adm.srv.getProps().getStringProperty("radm.nicks");


    
     // Тест на приветствие
    public boolean testHi(String s)
	{
    String t = "прив;прев;здоров;здрас;здрав;хай;хой;хелл;добр;даро;салям";
    return adm.test(s,t.split(";"));
    }
    public String getHi(String name)
	{
    String[] s = {"Привет","Хай","Приветствую","Здравствуй","Здоров","Ааа... Это снова ты, привет"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // Тест на прощание
    public boolean testBYE(String s)
	{
    String t = "пока;покеда;счастливо;удачи;до свидания;гуд бай";
    return adm.test(s,t.split(";"));
    }
    public String getBYE(String name)
	{
    String[] s = {"пока","покеда","счастливо","удачи","до свидания","гуд бай","ариведерчи","иди уже","вали уже"};
    return name + " " + s[adm.getRND(s.length)];
    }


    // Тест на вопрос (1)
    public boolean testQuestion1(String s)
	{
    String t = "чё делаешь;что делаешь;че делаешь;чем маешься;чем занимаешься;че творишь;что творишь;чем занята";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion1(String name){
    String[] s = {"Да вот в чате работаю...","Юзеров всяких кикаю!","Пока ничем не занята.:)","Хотела покушать сходить...","Админу помогаю","Тебя ебет???"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // Тест на вопрос (2)
    public boolean testQuestion2(String s)
	{
    String t = "как дела;как она;как оно;как жизнь;как жизнь молодая;как поживаешь;как житуха;как ты";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion2(String name)
	{
    String[] s = {"Пока не родила!","Неплохо...","Замечательно:)","Нормально!","Лучше всех, а ти???"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // Тест на вопрос (3)
    public boolean testQuestion3(String s)
	{
    String t = "почему";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion3(String name)
	{
    String[] s = {"Потому что, гладиолус!","Потому!","Всё тебе расскажи, да покажи, да дай попробовать!"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // Тест на вопрос (4)
    public boolean testQuestion4(String s)
    {
    String t = "?;когда;зачем;где;куда;кого;кому;кто;чем";
    return adm.test(s,t.split(";"));
    }
    public String getQuestion4(String name)
    {
    String[] s = {"Лишних вопросов не задавай!","Много будешь знать, плохо будешь спать","Я воздержусь от ответа"};
    return name + " " + s[adm.getRND(s.length)];
    }

    // Тест на оскорбление админа
    public boolean testSKD(String s)
    {
    String t = "туп;лох;чмо;пид;шалав;шлюх;проститут;сука;мраз;грубиян;твар;пиз;гавн;говн;олен;дура;урод;черт;овца;шлюшка;овечка;свин;коза;каза";
    return adm.test(s,t.split(";"));
    }
    public String getSKD(String name)
    {
    String[] s = {"Сам такой!","И ты не лучше","Ты че ахуел!","Заткнись сука!!! Сейчас полетишь!","Что больше сказать нечего?! Шлюшка","А за щеку возмёшь???"};
    return name + " " + s[adm.getRND(s.length)];
    }
	
    /*ИНФОРМАТОР*/

    public String getInfo(){
    try {
    PreparedStatement pst = adm.srv.us.db.getDb().prepareStatement("select * from inforob ORDER BY RAND( ) LIMIT 0 , 1");
    ResultSet rs = pst.executeQuery();
    if(rs.next()) return rs.getString(2);
    rs.close();
    pst.close();
    } catch (Exception ex) {}
    return "";
    }

	if(adm.srv.getProps().getBooleanProperty("adm.Informer")){
    Object times = Manager.getInstance().getData("times");
    if (times == null || times < System.currentTimeMillis()){
    Manager.getInstance().setData("times", System.currentTimeMillis() + adm.srv.getProps().getIntProperty("adm.Informer.time") *60000);// Интервал вывода информации
    Set rid = new HashSet();
    Enumeration e = adm.srv.cq.uq.keys();
    while(e.hasMoreElements()){
    String i = e.nextElement();
    Users us = adm.srv.us.getUser(i);
    if(us.state==adm.srv.us.STATE_CHAT)
    rid.add(us.room);
    }
    for (int i:rid)
    {
    adm.say(getInfo(), i); // Оповещение будет во все комнаты
    }
    }
    }	
	
    if (adm.mq.isEmpty()) return;
    MsgElement ms = adm.mq.poll();
    if(adm.srv.getProps().getBooleanProperty("adm.useMatFilter") &&
    adm.testMat1(adm.changeChar(ms.msg))&& 
	ms.room != adm.srv.getProps().getIntProperty("room.tyrma") && 
	!adm.srv.getProps().testAdmin(ms.uin)) {
    int i=0;
	//ПОНИЖЕНИЕ РЕЙТИНГА ЗА МАТ
	if(adm.srv.getProps().getBooleanProperty("minus.ball.mat.on.off"))
	adm.srv.us.getUser(ms.uin).ball -= adm.srv.getProps().getIntProperty("minus.ball.mat");
	adm.srv.us.updateUser(adm.srv.us.getUser(ms.uin));
	// ОПОВЕЩЕНИЕ(ПРЕДУПРЕЖДЕНИЕ) ЗА МАТ
	boolean test = true; // ВКЛЮЧИТЬ/ВЫКЛЮЧИТЬ
	String nick_m = adm.srv.us.getUser(ms.uin).localnick;
	String id_m = "|" + Integer.toString(adm.srv.us.getUser(ms.uin).id) + "|";
	String msg = "off mat :)";
	if(test){
	adm.say(id_m + nick_m + " " + msg, ms.room);
	}
    if(!adm.uins.containsKey(ms.uin))
	{
    adm.uins.put(ms.uin,i);
    } 
	else 
	{
    i=adm.uins.get(ms.uin);
    i++;
    adm.uins.put(ms.uin,i);
    }
    
	if(i>=3) 
	{ 
	adm.close(ms.uin);
    }	
    return;
    }

    if(!adm.srv.getProps().getBooleanProperty("adm.useSayAdmin"))
    return;  
	
	// АДМИН НЕ ДОЛЖЕН РЕАГИРОВАТЬ В КОМНАТЕ ГДЕ ПРОХОДИТ ВИКТОРИНА
	/*
	 * Проверяем включена викторина или нет
	 */
    if( adm.srv.getProps().getBooleanProperty("vic.on.off"))
	{
	/*
	 * Проверяем комнату
	 */
	if(((ChatCommandProc)adm.srv.cmd).Quiz.TestRoom(ms.room))
	{
	return;// Если комната викторины
	} 
    }	
	
	// Тест на приветствие
    if(adm.testName(ms.msg) && testHi(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getHi(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
    // Тест на прощание
    if(adm.testName(ms.msg) && testBYE(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getBYE(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
    
	// Тест на оскорбление админа
    if(adm.testName(ms.msg) && testSKD(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getSKD(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
	// Тест на вопрос (1)
	if(adm.testName(ms.msg) && testQuestion1(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion1(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
	// Тест на вопрос (2)
    if(adm.testName(ms.msg) && testQuestion2(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion2(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }

	// Тест на вопрос (3)
    if(adm.testName(ms.msg) && testQuestion3(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion3(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
    
	// Тест на вопрос (4)
    if(adm.testName(ms.msg) && testQuestion4(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    adm.say(getQuestion4(adm.srv.us.getUser(ms.uin).localnick), ms.room);
    return;
    }
	
	// Тест на стат
    if(adm.testName(ms.msg) && adm.testStat(ms.msg))
	{
    if(!adm.srv.us.authorityCheck(ms.uin, "adminstat")) return;
    adm.sayStat(ms.room);
    return;
    }
    
    // Тест на обращение к админу    
    if(adm.testName(ms.msg)){
    if(!adm.srv.us.authorityCheck(ms.uin, "adminsay")) return;
    if(adm.testFlood(ms.uin))
	{
    adm.lastCount++;
    if(adm.lastCount == (adm.srv.getProps().getIntProperty("adm.maxSayAdminCount")-1))
	{
    adm.say("Достали... ща закрою!", ms.room);
    } 
	else if(adm.lastCount >= adm.srv.getProps().getIntProperty("adm.maxSayAdminCount"))
	{
    ((ChatCommandProc)adm.srv.cmd).akick(ms.proc,ms.uin);
    adm.lastCount =0;
    } 
	else 
	{
    adm.say(adm.srv.us.getUser(ms.uin).localnick + " " + adm.getAdmin(), ms.room);
    }
    } 
	else 
	{
    adm.say(adm.srv.us.getUser(ms.uin).localnick + " " + adm.getAdmin(), ms.room);
    }
    }
    return;
