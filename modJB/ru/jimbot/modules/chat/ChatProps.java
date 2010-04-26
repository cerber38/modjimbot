/**
 * JimBot - Java IM Bot
 * Copyright (C) 2006-2009 JimBot project
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.jimbot.modules.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.table.UserPreference;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 *
 * @author Prolubnikov Dmitry
 */;
public class ChatProps implements AbstractProps {
	public static HashMap<String,ChatProps> props = new HashMap<String,ChatProps>();
    public String PROPS_FILE = "";
    private String PROPS_FOLDER = "";
    public String ENCODING = "windows-1251";
    public Properties appProps;
    public Properties langProps;
    public boolean isLoaded = false;    
    long startTime = System.currentTimeMillis();
    public RobAdmin radm = null;

    /** Creates a new instance of ChatProps */
    public ChatProps() {}
    
    public static ChatProps getInstance(String name){
    	if(props.containsKey(name))
    		return props.get(name);
    	else {
    		ChatProps p = new ChatProps();
    		p.PROPS_FILE = "./services/"+name+"/"+name+".xml";
    		p.PROPS_FOLDER = "./services/"+name;
    		p.setDefault();
    		      /*p.load();*/
    		props.put(name, p);
    		return p;
    	}
    }
    
    public void setDefault() {
        appProps = new Properties();        
        setIntProperty("conn.uinCount",1);
        setStringProperty("conn.uin0","111");
        setStringProperty("conn.pass0","Password");       
        setIntProperty("chat.pauseOut",5000);
        setBooleanProperty("chat.IgnoreOfflineMsg",true);
        setIntProperty("chat.TempKick",10); //Временный кик, минут
        setIntProperty("chat.ChangeStatusTime",60000);
        setIntProperty("chat.ChangeStatusCount",5);
        setIntProperty("chat.MaxMsgSize",150); //Максимальный размер одного сообщения от пользователя
        setIntProperty("chat.MaxOutMsgSize",500);
        setIntProperty("chat.MaxOutMsgCount",5);
        setIntProperty("icq.status",0/*Icq.STATUS_ONLINE*/);
        setIntProperty("icq.xstatus",0);
        setBooleanProperty("main.StartBot",false);
        setIntProperty("bot.pauseIn",3000); //Пауза входящих сообщений
        setIntProperty("bot.pauseOut",500); //Пауза исходящих сообщений
        setIntProperty("bot.msgOutLimit",20); //Ограничение очереди исходящих сообщений
        setIntProperty("bot.pauseRestart",11*60*1000); //Пауза перед запуском упавшего коннекта
        setStringProperty("bot.adminUIN","111111;222222");
        setIntProperty("chat.autoKickTime",60);
        setIntProperty("chat.autoKickTimeWarn",58);
        setIntProperty("icq.AUTORETRY_COUNT",5);
        setStringProperty("icq.STATUS_MESSAGE1","");
        setStringProperty("icq.STATUS_MESSAGE2","");
        setBooleanProperty("chat.ignoreMyMessage", true);
        setBooleanProperty("chat.isAuthRequest", false);
        setStringProperty("chat.badNicks","admin;админ");
        setIntProperty("chat.defaultKickTime",5);
        setIntProperty("chat.maxKickTime",300);
        setIntProperty("chat.maxNickLenght",10);
        setBooleanProperty("chat.showChangeUserStatus",true);
        setBooleanProperty("chat.writeInMsgs",false);
        setBooleanProperty("chat.writeAllMsgs",true);
        setBooleanProperty("adm.useAdmin",true);
        setBooleanProperty("adm.useMatFilter",true);
        setBooleanProperty("adm.useSayAdmin",true);
        setStringProperty("adm.matString","бля;хуй;хуя;хуе;хуё;хуи;хули;пизд;сук;суч;ублюд;сволоч;гандон;ебат;ебет;ибат;ебан;ебал;ибал;пидар;пидор;залуп;муда;муди");
        setStringProperty("adm.noMatString","рубл;нибал;абля;обля;оскорбля;шибал;гибал;хулига;требля;скреба;скребе;страх;стеб;хлеб;скипидар;любля;барсук");
        setIntProperty("adm.getStatTimeout",15);
        setIntProperty("adm.maxSayAdminCount",20);
        setIntProperty("adm.maxSayAdminTimeout",10);
        setIntProperty("adm.sayAloneTime",15);
        setIntProperty("adm.sayAloneProbability",20);
        setStringProperty("auth.groups","user;poweruser;moder;admin");
        setStringProperty("auth.group_user","pmsg;reg;invite;adminsay;adminstat;room;anyroom");
        setStringProperty("auth.group_poweruser","pmsg;reg;invite;adminsay;adminstat;room;anyroom");
        setStringProperty("auth.group_moder","pmsg;reg;invite;adminsay;adminstat;kickone;settheme;exthelp;whouser;room;dblnick;anyroom;wroom");
        setStringProperty("auth.group_admin","pmsg;reg;invite;adminsay;adminstat;kickone;kickall;ban;settheme;info;exthelp;authread;whouser;room;kickhist;whoinv;chgkick;dblnick;anyroom;wroom");
        setIntProperty("chat.MaxInviteTime",24);
        setBooleanProperty("chat.NoDelContactList",false);
        setIntProperty("chat.maxUserOnUin",7);
        setStringProperty("chat.badSymNicks","");
        setStringProperty("chat.goodSymNicks","");
        setStringProperty("chat.delimiter",":");
        setIntProperty("chat.floodCountLimit",5);
        setIntProperty("chat.floodTimeLimit",10);
        setIntProperty("chat.floodTimeLimitNoReg",20);
        setStringProperty("db.host","localhost:3306");
        setStringProperty("db.user","root");
        setStringProperty("db.pass","");
        setStringProperty("db.dbname","botdb");
        setBooleanProperty("chat.isUniqueNick", false);
        setIntProperty("chat.maxNickChanged",99);
        setBooleanProperty("chat.isShowKickReason", false);

        //////////////////////////////////////////////////////
        //ДОПОЛНИТЕЛЬНЫЕ КОМАНДЫ
        //////////////////////////////////////////////////////
        setIntProperty("chat.defaultBanroomTime",300);
        setIntProperty("room.tyrma",5);
        setIntProperty("time.dellog",5);
        setStringProperty("chat.name","Чат");
        setStringProperty("chat.lichnoe","111111;222222");
        setBooleanProperty("lichnoe.on.off", false);
        setStringProperty("vic.room","555;888");
        setBooleanProperty("vic.on.off", false);
        setIntProperty("vic.ball", 1);
        setIntProperty("vic.time", 90000);
        setStringProperty("vic.game.time", "9;21");
        setBooleanProperty("vic.time_game.on.off", false);
        setIntProperty("vic.users.cnt", 10);
        setBooleanProperty("vic.throwout.on.off", true);
        setIntProperty("vic.throwout.room", 0);
        setIntProperty("ball.grant.1",1000);
        setIntProperty("ball.grant.2",1000);
        setIntProperty("ball.grant.3",1000);
        setIntProperty("ball.grant.4",1000);
        setIntProperty("ball.grant.5",1000);
        setIntProperty("ball.grant.6",1000);
        setIntProperty("ball.grant.7",1000);
        setIntProperty("ball.grant.8",3000);
        setIntProperty("ball.grant.9",5000);
        setIntProperty("ball.grant.10",50);
        setIntProperty("ball.grant.11",1000);
        setBooleanProperty("Spisok.Chnick.on.off", true);
        setBooleanProperty("Spisok.Settheme.on.off", true);
        setBooleanProperty("Spisok.Who.on.off", true);
        setBooleanProperty("Spisok.Kickhist.on.off", true);
        setBooleanProperty("Spisok.Anyroom.on.off", true);
        setBooleanProperty("Spisok.Kickone.on.off", true);
        setBooleanProperty("Spisok.Banroom.on.off", true);
        setBooleanProperty("Spisok.Moder.on.off", true);
        setBooleanProperty("Spisok.Admin.on.off", true);
        setBooleanProperty("Spisok.Status.on.off", true);
        setBooleanProperty("Spisok.Modertime.on.off", true);
        setIntProperty("Spisok.Modertime.Day",30);
        setIntProperty("max.chnick",10);
        setBooleanProperty("adm.Informer",false);
        setIntProperty("adm.Informer.time",5);
        setIntProperty("about.user.long",15);
        setIntProperty("about.age.min",10);
        setIntProperty("about.age.max",50);
        setStringProperty("about.user.bad", ">;<;);(;!;`;~;@;#;№");
        setIntProperty("about.user.st",15);
        setBooleanProperty("Priglashenie.on.off", false);
        setStringProperty("chat.Priglashenie","111111;222222");
        setIntProperty("Alisa.Room",0);
        setIntProperty("Alisa.UsageTime",1);
        setIntProperty("Alisa.sayAloneTime",20);
        setBooleanProperty("Alisa.on.off", false);
        setIntProperty("Clan.MaxCount",5);
        setIntProperty("Clan.NameLenght",20);
        setIntProperty("Clan.InfoLenght",60);
        setIntProperty("Clan.Ball_1", 1 );
        setIntProperty("Clan.Ball_2", 1 );
        setIntProperty("Clan.Ball_3", 100 );
        setBooleanProperty("chat.captcha", false);
        setIntProperty("room.igra.bytilochka", 777 );
        setIntProperty("time.igra.bytilochka", 30 );
        setBooleanProperty("id.on.off", false);
        setIntProperty("auto_status.time", 5 );
        setBooleanProperty("auto_status.on.off", false);
        setBooleanProperty("wedding.floor.on.off", true);
        setIntProperty("wedding.room", 7 );
        setBooleanProperty("Questionnaire.on.off", true);
        setBooleanProperty("ball.on.off", true);
        setBooleanProperty("voting.on.off", true);
        setIntProperty("voting.time", 1 );
        setIntProperty("voting.kick.time", 30 );
        setIntProperty("voting.count", 2 );
        setBooleanProperty("inchat.on.off", true);
    }

    public UserPreference[] getUserPreference(){
        UserPreference[] p = {
            new UserPreference(UserPreference.CATEGORY_TYPE,"main", "Основные настройки","",""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"main.StartBot","Запускать чат-бот",getBooleanProperty("main.StartBot"),""),
            new UserPreference(UserPreference.CATEGORY_TYPE,"bot", "Настройки бота","",""),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.status","ICQ статус",getIntProperty("icq.status"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.xstatus","x-статус (0-37)",getIntProperty("icq.xstatus"),""),
            new UserPreference(UserPreference.STRING_TYPE,"icq.STATUS_MESSAGE1","Сообщение x-статуса 1",getStringProperty("icq.STATUS_MESSAGE1"),""),
            new UserPreference(UserPreference.STRING_TYPE,"icq.STATUS_MESSAGE2","Сообщение x-статуса 2",getStringProperty("icq.STATUS_MESSAGE2"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.AUTORETRY_COUNT","Число переподключений движка при обрыве",getIntProperty("icq.AUTORETRY_COUNT"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseIn","Пауза для входящих сообщений",getIntProperty("bot.pauseIn"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseOut","Пауза для исходящих сообщений",getIntProperty("bot.pauseOut"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.msgOutLimit","Ограничение очереди исходящих",getIntProperty("bot.msgOutLimit"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseRestart","Пауза перед перезапуском коннекта",getIntProperty("bot.pauseRestart"),""),
            new UserPreference(UserPreference.STRING_TYPE,"bot.adminUIN","Админские UIN",getStringProperty("bot.adminUIN"),""),
            new UserPreference(UserPreference.CATEGORY_TYPE,"chat", "Настройки чата","",""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.floodCountLimit","Число повторов флуда",getIntProperty("chat.floodCountLimit"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.floodTimeLimit","Период флуда (сек)",getIntProperty("chat.floodTimeLimit"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.floodTimeLimitNoReg","Пауза сообщений для незареганых (сек)",getIntProperty("chat.floodTimeLimitNoReg"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.pauseOut","Задержка очереди чата",getIntProperty("chat.pauseOut"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.IgnoreOfflineMsg","Игнорировать оффлайн сообщения",getBooleanProperty("chat.IgnoreOfflineMsg"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.ignoreMyMessage","Игнорировать собственные сообщения в чате",getBooleanProperty("chat.ignoreMyMessage"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.TempKick","Временный кик (минут)",getIntProperty("chat.TempKick"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.ChangeStatusTime","Период переподключения юзера",getIntProperty("chat.ChangeStatusTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.ChangeStatusCount","Количество переподключений для блокировки юзера",getIntProperty("chat.ChangeStatusCount"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxMsgSize","Максимальный размер одного сообщения",getIntProperty("chat.MaxMsgSize"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxOutMsgSize","Максимальный размер одного исходящего сообщения",getIntProperty("chat.MaxOutMsgSize"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxOutMsgCount","Максимальное число частей исходящего сообщения",getIntProperty("chat.MaxOutMsgCount"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.autoKickTime","Время автокика при молчании (минут)",getIntProperty("chat.autoKickTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.autoKickTimeWarn","Время предупреждения перед автокиком",getIntProperty("chat.autoKickTimeWarn"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.isAuthRequest","Запрашивать авторизацию у пользователей",getBooleanProperty("chat.isAuthRequest"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.badNicks","Запрещенные ники",getStringProperty("chat.badNicks"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxNickChanged","Число смен ника за сутки",getIntProperty("chat.maxNickChanged"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.defaultKickTime","Время кика по умолчанию",getIntProperty("chat.defaultKickTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxKickTime","Максимальное время кика",getIntProperty("chat.maxKickTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxNickLenght","Максимальная длина ника в чате",getIntProperty("chat.maxNickLenght"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.isUniqueNick","Уникальные ники в чате",getBooleanProperty("chat.isUniqueNick"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.showChangeUserStatus","Показывать вход-выход при падении юзеров",getBooleanProperty("chat.showChangeUserStatus"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.writeInMsgs","Записывать все входящие сообщения в БД",getBooleanProperty("chat.writeInMsgs"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.writeAllMsgs","Записывать сообщения в БД (отключит статистику и т.п.)",getBooleanProperty("chat.writeAllMsgs"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.NoDelContactList","Не очищать контакт-лист",getBooleanProperty("chat.NoDelContactList"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxUserOnUin","Максимум юзеров на 1 уин",getIntProperty("chat.maxUserOnUin"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.badSymNicks","Запрещенные символы в никах",getStringProperty("chat.badSymNicks"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.goodSymNicks","Разрешенные символы в никах",getStringProperty("chat.goodSymNicks"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.delimiter","Разделитель после ника",getStringProperty("chat.delimiter"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.isShowKickReason","Выводить нарушителю причину кика",getBooleanProperty("chat.isShowKickReason"),""),
            new UserPreference(UserPreference.CATEGORY_TYPE,"adm", "Настройки Админа","",""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.useAdmin","Использовать Админа в чате",getBooleanProperty("adm.useAdmin"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.useMatFilter","Разрешить реакцию на мат",getBooleanProperty("adm.useMatFilter"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.useSayAdmin","Разрешить админу разговаривать",getBooleanProperty("adm.useSayAdmin"),""),
            new UserPreference(UserPreference.STRING_TYPE,"adm.matString","Слова для мата",getStringProperty("adm.matString"),""),
            new UserPreference(UserPreference.STRING_TYPE,"adm.noMatString","Слова исключения",getStringProperty("adm.noMatString"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.getStatTimeout","Пауза между показами статистики",getIntProperty("adm.getStatTimeout"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.maxSayAdminCount","Максимум обращений к админу для одного человека",getIntProperty("adm.maxSayAdminCount"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.maxSayAdminTimeout","Время сброса статистики обращений",getIntProperty("adm.maxSayAdminTimeout"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.sayAloneTime","Время молчания, через которое админ заговорит",getIntProperty("adm.sayAloneTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.sayAloneProbability","Вероятность разговора админа в тишине (1 к ...)",getIntProperty("adm.sayAloneProbability"),""),

            new UserPreference(UserPreference.CATEGORY_TYPE,"db", "Настройки mySQL","",""),
            new UserPreference(UserPreference.STRING_TYPE,"db.host","Хост БД",getStringProperty("db.host"),""),
            new UserPreference(UserPreference.STRING_TYPE,"db.user","Пользователь",getStringProperty("db.user"),""),
            new UserPreference(UserPreference.PASS_TYPE,"db.pass","Пароль",getStringProperty("db.pass"),""),
            new UserPreference(UserPreference.STRING_TYPE,"db.dbname","Название базы данных",getStringProperty("db.dbname"),"")
        };
        return p;
    }

        public UserPreference[] OtherUserPreference()
        {
        UserPreference[] p = {
        //chat
        new UserPreference(UserPreference.CATEGORY_TYPE,"chat", "Настройки чата", "",""),
        new UserPreference(UserPreference.STRING_TYPE,"chat.name","Название чата",getStringProperty("chat.name"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"max.chnick","Максимальная длина ника при команде !chnick.",getIntProperty("max.chnick"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.captcha","Включить/Выключить капчу",getBooleanProperty("chat.captcha"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"id.on.off","Включить/Выключить id рядом с ником в сообщении",getBooleanProperty("id.on.off"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"auto_status.on.off","Включить/Выключить авто смену x-status`ов",getBooleanProperty("auto_status.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"auto_status.time","Интервал смены x-status`ов",getIntProperty("auto_status.time"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Questionnaire.on.off","Включить/Выключить заполнение анкеты после регистрации",getBooleanProperty("Questionnaire.on.off"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"ball.on.off","Включить/Выключить возможность передачи баллов между пользователями",getBooleanProperty("ball.on.off"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"inchat.on.off","Включить/Выключить интерактивный вход в чат",getBooleanProperty("inchat.on.off"),""),
        //банрум
        new UserPreference(UserPreference.CATEGORY_TYPE,"banroom", "Настройки закрытия в комнате", "",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"room.tyrma","Комната Тюрьма в чате",getIntProperty("room.tyrma"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"chat.defaultBanroomTime","Максимальное время закрытия в комнате",getIntProperty("chat.defaultBanroomTime"), " (минут)"),
        //Informer
        new UserPreference(UserPreference.CATEGORY_TYPE,"Informer", "Настройки информера", "",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.Informer","Включить/Выключить информер",getBooleanProperty("adm.Informer"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"adm.Informer.time","Интервал информера",getIntProperty("adm.Informer.time")," (минут)"),
        //лс && invitation
        new UserPreference(UserPreference.CATEGORY_TYPE,"lich", "Настройки по оповещениям сообщений","",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"lichnoe.on.off","Включить/выключить оповещение админа о привате",getBooleanProperty("lichnoe.on.off"),""),
        new UserPreference(UserPreference.STRING_TYPE,"chat.lichnoe","Куда присылать личные сообщения",getStringProperty("chat.lichnoe"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Priglashenie.on.off","Включить/выключить оповещение приглашений.",getBooleanProperty("Priglashenie.on.off"),""),
        new UserPreference(UserPreference.STRING_TYPE,"chat.Priglashenie","Куда присылать оповешение приглашений.",getStringProperty("chat.Priglashenie"),""),
        //Викторина
        new UserPreference(UserPreference.CATEGORY_TYPE,"victorina", "Игра викторина","",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"vic.on.off","Включить/выключить викторину",getBooleanProperty("vic.on.off"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"vic.time_game.on.off","Включить/выключить игровой интервал",getBooleanProperty("vic.time_game.on.off"),""),
        new UserPreference(UserPreference.STRING_TYPE,"vic.game.time", "Игравой интервал", getStringProperty("vic.game.time"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"vic.throwout.on.off", "Переводить пользователей при окончании игры в другую комнату", getBooleanProperty("vic.throwout.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"vic.throwout.room","Номер комнаты в которую переведет при окончании игры",getIntProperty("vic.throwout.room"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"vic.users.cnt","Максимум чел. в игре",getIntProperty("vic.users.cnt"),""),
        new UserPreference(UserPreference.STRING_TYPE,"vic.room", "Комнаты где будет проходить викторина", getStringProperty("vic.room"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"vic.ball","Количество баллов за правельный ответ",getIntProperty("vic.ball"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"vic.time","Интервал викторины",getIntProperty("vic.time"),""),
        //данные
        new UserPreference(UserPreference.CATEGORY_TYPE,"AboutUser", "Настройки личной информации","",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.user.long","Максимальная длина имени и города.",getIntProperty("about.user.long"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.age.min","Минимальный возраст.",getIntProperty("about.age.min"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.age.max","Максимальный возраст.",getIntProperty("about.age.max"),""),
        new UserPreference(UserPreference.STRING_TYPE,"about.user.bad","Запрещенные символы",getStringProperty("about.user.bad"),""),
        //статус
        new UserPreference(UserPreference.CATEGORY_TYPE,"st", "Настройки статуса пользователя", "",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.user.st","Максимальный статус.",getIntProperty("about.user.st"),""),
        //Магазин
        new UserPreference(UserPreference.CATEGORY_TYPE,"magazin", "Настройки магазина" +"","",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Chnick.on.off","Включить/Выключить продажу полномочия ''chnick''",getBooleanProperty("Spisok.Chnick.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.1","Полномочие ''chnick''",getIntProperty("ball.grant.1")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Settheme.on.off","Включить/Выключить продажу полномочия ''settheme''",getBooleanProperty("Spisok.Settheme.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.2","Полномочие ''settheme''",getIntProperty("ball.grant.2")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Who.on.off","Включить/Выключить продажу полномочия ''who''",getBooleanProperty("Spisok.Who.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.3","Полномочие ''who''",getIntProperty("ball.grant.3")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Kickhist.on.off","Включить/Выключить продажу полномочия ''kickhist''",getBooleanProperty("Spisok.Kickhist.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.4","Полномочие ''kickhist''",getIntProperty("ball.grant.4")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Anyroom.on.off","Включить/Выключить продажу полномочия ''anyroom''",getBooleanProperty("Spisok.Anyroom.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.5","Полномочие ''anyroom''",getIntProperty("ball.grant.5")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Kickone.on.off","Включить/Выключить продажу полномочия ''kickone''",getBooleanProperty("Spisok.Kickone.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.6","Полномочие ''kickone''",getIntProperty("ball.grant.6")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Banroom.on.off","Включить/Выключить продажу полномочия ''banroom''",getBooleanProperty("Spisok.Banroom.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.7","Полномочие ''banroom''",getIntProperty("ball.grant.7")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Moder.on.off","Включить/Выключить продажу группы ''moder''",getBooleanProperty("Spisok.Moder.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.8","Группа ''moder''",getIntProperty("ball.grant.8")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Admin.on.off","Включить/Выключить продажу группы ''admin''",getBooleanProperty("Spisok.Admin.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.9","Группа ''admin''",getIntProperty("ball.grant.9")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Status.on.off","Включить/Выключить продажу полномочия ''status_user''",getBooleanProperty("Spisok.Status.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.10","Полномочие ''status_user''",getIntProperty("ball.grant.10")," баллов"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Modertime.on.off","Включить/Выключить продажу группы ''modertime''",getBooleanProperty("Spisok.Modertime.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.11","Группа ''moder'' (на время)",getIntProperty("ball.grant.11")," баллов"),
        new UserPreference(UserPreference.INTEGER_TYPE,"Spisok.Modertime.Day","Количество дней",getIntProperty("Spisok.Modertime.Day"),""),
        //Настройки кланов
        new UserPreference(UserPreference.CATEGORY_TYPE,"clan", "Настройки кланов" +"","",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.MaxCount","Максимальное количество кланов",getIntProperty("Clan.MaxCount"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.NameLenght","Максимальная длина названия клана",getIntProperty("Clan.NameLenght"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.InfoLenght","Максимальная длина информации клана",getIntProperty("Clan.InfoLenght"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.Ball_1","Рейтинг клана (прибавится/отнимется)  при (вступление/уходе) пользователя",getIntProperty("Clan.Ball_1"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.Ball_2","Максимально число возможных балов клану при команде +кланбал",getIntProperty("Clan.Ball_2"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.Ball_3","Максимально количество баллов для поднятия рейтинга клана",getIntProperty("Clan.Ball_3"),""),
        //Настройки бутылочки
        new UserPreference(UserPreference.CATEGORY_TYPE,"but", "Настройки бутылочки =)" +"","",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"room.igra.bytilochka","Комната игры",getIntProperty("room.igra.bytilochka"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"time.igra.bytilochkat","Интервал игры",getIntProperty("time.igra.bytilochka")," секунд"),
        //Настройки свадьбы
        new UserPreference(UserPreference.CATEGORY_TYPE,"wedding", "Настройки свадьбы" +"","",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"wedding.floor.on.off","Включить/Выключить проверку пола",getBooleanProperty("wedding.floor.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"wedding.room","Комната",getIntProperty("wedding.room")," секунд"),
        //Настройки голосования
        new UserPreference(UserPreference.CATEGORY_TYPE,"voting", "Настройки голосования" +"","",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"voting.on.off","Включить/Выключить голосование в чате",getBooleanProperty("voting.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"voting.time","Время голосования",getIntProperty("voting.time")," минут"),
        new UserPreference(UserPreference.INTEGER_TYPE,"voting.kick.time","Время кик при окончании голосования",getIntProperty("voting.kick.time")," минут"),
        new UserPreference(UserPreference.INTEGER_TYPE,"voting.count","Количество голосований для пользователя в сутки ",getIntProperty("voting.count"),""),
        };
        return p;
        }



    public UserPreference[] getUINPreference(){
        UserPreference[] p = new UserPreference[uinCount()*2+1];
        p[0] = new UserPreference(UserPreference.CATEGORY_TYPE,"conn", "Настройки UINов для подключения","","");
        for(int i=0;i<uinCount();i++){
            p[i*2+1] = new UserPreference(UserPreference.STRING_TYPE,"conn.uin" + i,"UIN" + i,getProperty("conn.uin" + i,""),"");
            p[i*2+2] = new UserPreference(UserPreference.PASS_TYPE,"conn.pass" + i,"Password" + i,getProperty("conn.pass" + i, ""),"");
        }
        return p;
    }
    
    public boolean isAutoStart(){
    	return getBooleanProperty("main.StartBot");
    }
    
    public boolean testAdmin(String uin) {
        if(uin.equals("0")) return true; //Выртуальный админ
        String s = getStringProperty("bot.adminUIN");
        if(s.equals("")) return false;
        String[] ss = s.split(";");
        try{
            for(int i=0;i<ss.length;i++){
                if(ss[i].equalsIgnoreCase(uin)) return true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    public String[] getAdmins(){
    	return getStringProperty("bot.adminUIN").split(";");
    }
    
    public String getChatRules(){
        return loadText("./text/rules.txt");
    }
    
    public String getHelp1(){
        return loadText("./text/help1.txt");
    }
    
    public String getHelp2(){
        return loadText("./text/help2.txt");
    }

    public String loadTextWWW(String fname){
    String s = "";
    try {
    String wwwtext = MainProps.getStringFromHTTP( fname );
    s += wwwtext;
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    }
    return s;
    }

    public String loadText(String fname){
        String s = "";
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(fname),"windows-1251"));
            while (r.ready()) {
                s += r.readLine() + "\n";
            }
            r.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }

    public  void load(  ) {
        File file = new File(PROPS_FILE);
        if(!file.exists()){
        String[] xml = PROPS_FILE.split("/");
        Log.getDefault().error(xml[2] + ".xml не создан!");
        return;
        }
        setDefault();
        try {
            FileInputStream fi = new FileInputStream(file);
            appProps.loadFromXML(fi);
            fi.close();
            Log.getDefault().info("Load preferences ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error opening preferences: ");
        }
    }
    
    public final void save() {
        File file = new File(PROPS_FILE);
        File dir = new File(this.PROPS_FOLDER);
        try {
        	if(!dir.exists())
        		dir.mkdirs();
            FileOutputStream fo = new FileOutputStream(file);
            appProps.storeToXML(fo, "jImBot properties");
            fo.close();
            Log.getDefault().info("Save preferences ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error saving preferences: ");
        }
    }
    
    public int uinCount(){
        return getIntProperty("conn.uinCount");
    }
    
    public String getUin(int i){
        return getStringProperty("conn.uin"+i);
    }
    
    public String getPass(int i){
        return getStringProperty("conn.pass"+i);
    }    
    
    /**
     * Изменение уина
     * @param i
     * @param uin
     * @param pass
     */
    public void setUin(int i, String uin, String pass){
    	setStringProperty("conn.uin"+i, uin);
    	if(!pass.equals("")) setStringProperty("conn.pass"+i, pass);
    }
    
    /**
     * Добавление нового уина в настройки
     * @param uin - уин
     * @param pass - пароль
     * @return - порядковый номер нового уина
     */
    public int addUin(String uin, String pass){
    	int c = uinCount();
    	setIntProperty("conn.uinCount", c+1);
    	setStringProperty("conn.uin"+c, uin);
    	setStringProperty("conn.pass"+c, pass);
    	return c;
    }
    
    /**
     * Удаление уина из настроек
     * @param c
     */
    public void delUin(int c) {
    	// Сдвигаем элементы после удаленного
    	for(int i=0; i<(uinCount()-1); i++){
    		if(i>=c){
    			setStringProperty("conn.uin"+i, getUin(i+1));
    			setStringProperty("conn.pass"+i, getPass(i+1));
    		}
    	}
    	//Удаляем самый последний элемент
    	appProps.remove("conn.uin"+(uinCount()-1));
    	appProps.remove("conn.pass"+(uinCount()-1));
    	setIntProperty("conn.uinCount", uinCount()-1);
    }
    
    public void registerProperties(Properties _appProps) {
        appProps = _appProps;
    }
    
    public String getProperty(String key) {
        return appProps.getProperty(key);
    }
    
    public String getStringProperty(String key) {
        return appProps.getProperty(key);
    }
    
    public String getProperty(String key, String def) {
        return appProps.getProperty(key,def);
    }
    
    public void setProperty(String key, String val) {
        appProps.setProperty(key,val);
    }
    
    public void setStringProperty(String key, String val) {
        appProps.setProperty(key,val);
    }
    
    public void setIntProperty(String key, int val) {
        appProps.setProperty(key,Integer.toString(val));
    }
    
    public void setBooleanProperty(String key, boolean val) {
        appProps.setProperty(key, val ? "true":"false");
    }
    
    public int getIntProperty(String key) {
        return Integer.parseInt(appProps.getProperty(key));
    }
    
    public boolean getBooleanProperty(String key) {
        return Boolean.valueOf(appProps.getProperty(key)).booleanValue();
    }

	public Properties getProps() {
		return appProps;
	}


        //////////////////////////////////////////////////////
        //ДОПОЛНИТЕЛЬНЫЕ КОМАНДЫ
        //////////////////////////////////////////////////////

    public boolean Time()
    {
    return (System.currentTimeMillis()-startTime)>1000*60*60*getIntProperty("time.dellog");
    }

    public void del_logs_time()
    {
    if(Time())
    {
    startTime = System.currentTimeMillis();
    dellogs();
    }

    }


    public void dellogs()
    {
    File log = new File("./log/");
    if(!log.exists()) return;
    if(!log.isDirectory()) return;
    File[] all = log.listFiles();
    if(all.length > 0)
    for(int i = 0; i < all.length; i++)
    {
    if(all[i].isFile())
    del("./log/"+all[i].getName());
    }
    //Log.info("Папка log успешно очищенна");
    //srv.us.db.executeQuery(" TRUNCATE `events` ");
    //srv.us.db.executeQuery(" TRUNCATE `log` ");
    //Log.info("Таблицы log и events успешно очищенны");
    }

    public   void del(String name) {
    File i = new File (name);
      if (i.exists()) i.delete();
    }

    /**
     * Авто создание конфига
     * @param name
     */

        public void AddXmlConfig (String name){
    String Xml = "./services/" + name + "/" + name + ".xml";
    File NEW = new File(Xml);
        try {
            FileOutputStream fo = new FileOutputStream(NEW);
            appProps.storeToXML(fo, "jImBot properties");
            fo.close();
            Log.getLogger(name).info(name + ".xml был создан автоматически!");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error saving preferences: ");
        }
    }


}
