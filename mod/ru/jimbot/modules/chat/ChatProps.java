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
    public ChatProps() {
    }
    
    public static ChatProps getInstance(String name){
    	if(props.containsKey(name))
    		return props.get(name);
    	else {
    		ChatProps p = new ChatProps();
    		p.PROPS_FILE = "./services/"+name+"/"+name+".xml";
    		p.PROPS_FOLDER = "./services/"+name;
    		p.setDefault();
    		p.load();
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
        setIntProperty("chat.TempKick",10); //��������� ���, �����
        setIntProperty("chat.ChangeStatusTime",60000);
        setIntProperty("chat.ChangeStatusCount",5);
        setIntProperty("chat.MaxMsgSize",150); //������������ ������ ������ ��������� �� ������������
        setIntProperty("chat.MaxOutMsgSize",500);
        setIntProperty("chat.MaxOutMsgCount",5);
        setIntProperty("icq.status",0/*Icq.STATUS_ONLINE*/);
        setIntProperty("icq.xstatus",0);
        setBooleanProperty("main.StartBot",false);
        setIntProperty("bot.pauseIn",3000); //����� �������� ���������
        setIntProperty("bot.pauseOut",500); //����� ��������� ���������
        setIntProperty("bot.msgOutLimit",20); //����������� ������� ��������� ���������
        setIntProperty("bot.pauseRestart",11*60*1000); //����� ����� �������� �������� ��������
        setStringProperty("bot.adminUIN","111111;222222");
        setIntProperty("chat.autoKickTime",60);
        setIntProperty("chat.autoKickTimeWarn",58);
        setIntProperty("icq.AUTORETRY_COUNT",5);
        setStringProperty("icq.STATUS_MESSAGE1","");
        setStringProperty("icq.STATUS_MESSAGE2","");
        setBooleanProperty("chat.ignoreMyMessage", true);
        setBooleanProperty("chat.isAuthRequest", false);
        setStringProperty("chat.badNicks","admin;�����");
        setIntProperty("chat.defaultKickTime",5);
        setIntProperty("chat.maxKickTime",300);
        setIntProperty("chat.maxNickLenght",10);
        setBooleanProperty("chat.showChangeUserStatus",true);
        setBooleanProperty("chat.writeInMsgs",false);
        setBooleanProperty("chat.writeAllMsgs",true);
        setBooleanProperty("adm.useAdmin",true);
        setBooleanProperty("adm.useMatFilter",true);
        setBooleanProperty("adm.useSayAdmin",true);
        setStringProperty("adm.matString","���;���;���;���;��;���;����;����;���;���;�����;������;������;����;����;����;����;����;����;�����;�����;�����;����;����");
        setStringProperty("adm.noMatString","����;�����;����;����;��������;�����;�����;������;������;������;������;�����;����;����;��������;�����;������");
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
        //�������������� �������
        //////////////////////////////////////////////////////

        setIntProperty("chat.defaultBanroomTime",300);
        setIntProperty("room.tyrma",5);
        setIntProperty("time.dellog",5);
        setStringProperty("chat.name","���");
        setStringProperty("chat.lichnoe","111111;222222");
        setBooleanProperty("lichnoe.on.off", false);
        setIntProperty("vic.room",555);
        setBooleanProperty("vic.on.off", false);
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
        setIntProperty("max.chnick",10);
        setBooleanProperty("adm.Informer",false);
        setIntProperty("adm.Informer.time",5);
        setIntProperty("about.user.long",15);
        setIntProperty("about.age.min",10);
        setIntProperty("about.age.max",50);
        setStringProperty("about.user.bad", ">;<;);(;!;`;~;@;#;�");
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

    }

    public UserPreference[] getUserPreference(){
        UserPreference[] p = {
            new UserPreference(UserPreference.CATEGORY_TYPE,"main", "�������� ���������","",""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"main.StartBot","��������� ���-���",getBooleanProperty("main.StartBot"),""),
            new UserPreference(UserPreference.CATEGORY_TYPE,"bot", "��������� ����","",""),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.status","ICQ ������",getIntProperty("icq.status"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.xstatus","x-������ (0-36)",getIntProperty("icq.xstatus"),""),
            new UserPreference(UserPreference.STRING_TYPE,"icq.STATUS_MESSAGE1","��������� x-������� 1",getStringProperty("icq.STATUS_MESSAGE1"),""),
            new UserPreference(UserPreference.STRING_TYPE,"icq.STATUS_MESSAGE2","��������� x-������� 2",getStringProperty("icq.STATUS_MESSAGE2"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.AUTORETRY_COUNT","����� ��������������� ������ ��� ������",getIntProperty("icq.AUTORETRY_COUNT"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseIn","����� ��� �������� ���������",getIntProperty("bot.pauseIn"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseOut","����� ��� ��������� ���������",getIntProperty("bot.pauseOut"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.msgOutLimit","����������� ������� ���������",getIntProperty("bot.msgOutLimit"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseRestart","����� ����� ������������ ��������",getIntProperty("bot.pauseRestart"),""),
            new UserPreference(UserPreference.STRING_TYPE,"bot.adminUIN","��������� UIN",getStringProperty("bot.adminUIN"),""),
            new UserPreference(UserPreference.CATEGORY_TYPE,"chat", "��������� ����","",""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.floodCountLimit","����� �������� �����",getIntProperty("chat.floodCountLimit"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.floodTimeLimit","������ ����� (���)",getIntProperty("chat.floodTimeLimit"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.floodTimeLimitNoReg","����� ��������� ��� ����������� (���)",getIntProperty("chat.floodTimeLimitNoReg"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.pauseOut","�������� ������� ����",getIntProperty("chat.pauseOut"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.IgnoreOfflineMsg","������������ ������� ���������",getBooleanProperty("chat.IgnoreOfflineMsg"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.ignoreMyMessage","������������ ����������� ��������� � ����",getBooleanProperty("chat.ignoreMyMessage"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.TempKick","��������� ��� (�����)",getIntProperty("chat.TempKick"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.ChangeStatusTime","������ ��������������� �����",getIntProperty("chat.ChangeStatusTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.ChangeStatusCount","���������� ��������������� ��� ���������� �����",getIntProperty("chat.ChangeStatusCount"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxMsgSize","������������ ������ ������ ���������",getIntProperty("chat.MaxMsgSize"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxOutMsgSize","������������ ������ ������ ���������� ���������",getIntProperty("chat.MaxOutMsgSize"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxOutMsgCount","������������ ����� ������ ���������� ���������",getIntProperty("chat.MaxOutMsgCount"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.autoKickTime","����� �������� ��� �������� (�����)",getIntProperty("chat.autoKickTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.autoKickTimeWarn","����� �������������� ����� ���������",getIntProperty("chat.autoKickTimeWarn"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.isAuthRequest","����������� ����������� � �������������",getBooleanProperty("chat.isAuthRequest"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.badNicks","����������� ����",getStringProperty("chat.badNicks"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxNickChanged","����� ���� ���� �� �����",getIntProperty("chat.maxNickChanged"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.defaultKickTime","����� ���� �� ���������",getIntProperty("chat.defaultKickTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxKickTime","������������ ����� ����",getIntProperty("chat.maxKickTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxNickLenght","������������ ����� ���� � ����",getIntProperty("chat.maxNickLenght"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.isUniqueNick","���������� ���� � ����",getBooleanProperty("chat.isUniqueNick"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.showChangeUserStatus","���������� ����-����� ��� ������� ������",getBooleanProperty("chat.showChangeUserStatus"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.writeInMsgs","���������� ��� �������� ��������� � ��",getBooleanProperty("chat.writeInMsgs"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.writeAllMsgs","���������� ��������� � �� (�������� ���������� � �.�.)",getBooleanProperty("chat.writeAllMsgs"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.NoDelContactList","�� ������� �������-����",getBooleanProperty("chat.NoDelContactList"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.maxUserOnUin","�������� ������ �� 1 ���",getIntProperty("chat.maxUserOnUin"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.badSymNicks","����������� ������� � �����",getStringProperty("chat.badSymNicks"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.goodSymNicks","����������� ������� � �����",getStringProperty("chat.goodSymNicks"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.delimiter","����������� ����� ����",getStringProperty("chat.delimiter"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.isShowKickReason","�������� ���������� ������� ����",getBooleanProperty("chat.isShowKickReason"),""),
            new UserPreference(UserPreference.CATEGORY_TYPE,"adm", "��������� ������","",""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.useAdmin","������������ ������ � ����",getBooleanProperty("adm.useAdmin"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.useMatFilter","��������� ������� �� ���",getBooleanProperty("adm.useMatFilter"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.useSayAdmin","��������� ������ �������������",getBooleanProperty("adm.useSayAdmin"),""),
            new UserPreference(UserPreference.STRING_TYPE,"adm.matString","����� ��� ����",getStringProperty("adm.matString"),""),
            new UserPreference(UserPreference.STRING_TYPE,"adm.noMatString","����� ����������",getStringProperty("adm.noMatString"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.getStatTimeout","����� ����� �������� ����������",getIntProperty("adm.getStatTimeout"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.maxSayAdminCount","�������� ��������� � ������ ��� ������ ��������",getIntProperty("adm.maxSayAdminCount"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.maxSayAdminTimeout","����� ������ ���������� ���������",getIntProperty("adm.maxSayAdminTimeout"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.sayAloneTime","����� ��������, ����� ������� ����� ���������",getIntProperty("adm.sayAloneTime"),""),
            new UserPreference(UserPreference.INTEGER_TYPE,"adm.sayAloneProbability","����������� ��������� ������ � ������ (1 � ...)",getIntProperty("adm.sayAloneProbability"),""),

            new UserPreference(UserPreference.CATEGORY_TYPE,"db", "��������� mySQL","",""),
            new UserPreference(UserPreference.STRING_TYPE,"db.host","���� ��",getStringProperty("db.host"),""),
            new UserPreference(UserPreference.STRING_TYPE,"db.user","������������",getStringProperty("db.user"),""),
            new UserPreference(UserPreference.PASS_TYPE,"db.pass","������",getStringProperty("db.pass"),""),
            new UserPreference(UserPreference.STRING_TYPE,"db.dbname","�������� ���� ������",getStringProperty("db.dbname"),"")
        };
        return p;
    }

        public UserPreference[] OtherUserPreference()
        {
        UserPreference[] p = {
        //chat
        new UserPreference(UserPreference.CATEGORY_TYPE,"chat", "��������� ����", "",""),
        new UserPreference(UserPreference.STRING_TYPE,"chat.name","�������� ����",getStringProperty("chat.name"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"max.chnick","������������ ����� ���� ��� ������� !chnick.",getIntProperty("max.chnick"),""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"chat.captcha","��������/��������� �����",getBooleanProperty("chat.captcha"),""),
        //������
        new UserPreference(UserPreference.CATEGORY_TYPE,"banroom", "��������� �������� � �������", "",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"room.tyrma","������� ������ � ����",getIntProperty("room.tyrma"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"chat.defaultBanroomTime","������������ ����� �������� � �������",getIntProperty("chat.defaultBanroomTime"), " (�����)"),
        //Informer
        new UserPreference(UserPreference.CATEGORY_TYPE,"Informer", "��������� ���������", "",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"adm.Informer","��������/��������� ��������",getBooleanProperty("adm.Informer"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"adm.Informer.time","�������� ���������",getIntProperty("adm.Informer.time")," (�����)"),
        //�� && invitation
/*new UserPreference(UserPreference.CATEGORY_TYPE,"lich", "��������� �� ����������� ���������","",""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"lichnoe.on.off","��������/��������� ���������� ������ � �������",getBooleanProperty("lichnoe.on.off"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.lichnoe","���� ��������� ������ ���������",getStringProperty("chat.lichnoe"),""),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"Priglashenie.on.off","��������/��������� ���������� �����������.",getBooleanProperty("Priglashenie.on.off"),""),
            new UserPreference(UserPreference.STRING_TYPE,"chat.Priglashenie","���� ��������� ���������� �����������.",getStringProperty("chat.Priglashenie"),""),*/
        //���������
        new UserPreference(UserPreference.CATEGORY_TYPE,"victorina", "���� ���������","",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"vic.on.off","��������/��������� ���������",getBooleanProperty("vic.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"vic.room", "������� ��� ����� ��������� ���������", Integer.valueOf(getIntProperty("vic.room")),""),
        //������
        new UserPreference(UserPreference.CATEGORY_TYPE,"AboutUser", "��������� ������ ����������","",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.user.long","������������ ����� ����� � ������.",getIntProperty("about.user.long"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.age.min","����������� �������.",getIntProperty("about.age.min"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.age.max","������������ �������.",getIntProperty("about.age.max"),""),
        new UserPreference(UserPreference.STRING_TYPE,"about.user.bad","����������� �������",getStringProperty("about.user.bad"),""),
        //������
        new UserPreference(UserPreference.CATEGORY_TYPE,"st", "��������� ������� ������������", "",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"about.user.st","������������ ������.",getIntProperty("about.user.st"),""),
        //�������
        new UserPreference(UserPreference.CATEGORY_TYPE,"magazin", "��������� ��������" +"","",""),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Chnick.on.off","��������/��������� ������� ���������� ''chnick''",getBooleanProperty("Spisok.Chnick.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.1","���������� ''chnick''",getIntProperty("ball.grant.1")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Settheme.on.off","��������/��������� ������� ���������� ''settheme''",getBooleanProperty("Spisok.Settheme.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.2","���������� ''settheme''",getIntProperty("ball.grant.2")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Who.on.off","��������/��������� ������� ���������� ''who''",getBooleanProperty("Spisok.Who.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.3","���������� ''who''",getIntProperty("ball.grant.3")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Kickhist.on.off","��������/��������� ������� ���������� ''kickhist''",getBooleanProperty("Spisok.Kickhist.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.4","���������� ''kickhist''",getIntProperty("ball.grant.4")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Anyroom.on.off","��������/��������� ������� ���������� ''anyroom''",getBooleanProperty("Spisok.Anyroom.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.5","���������� ''anyroom''",getIntProperty("ball.grant.5")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Kickone.on.off","��������/��������� ������� ���������� ''kickone''",getBooleanProperty("Spisok.Kickone.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.6","���������� ''kickone''",getIntProperty("ball.grant.6")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Banroom.on.off","��������/��������� ������� ���������� ''banroom''",getBooleanProperty("Spisok.Banroom.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.7","���������� ''banroom''",getIntProperty("ball.grant.7")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Moder.on.off","��������/��������� ������� ������ ''moder''",getBooleanProperty("Spisok.Moder.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.8","������ ''moder''",getIntProperty("ball.grant.8")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Admin.on.off","��������/��������� ������� ������ ''admin''",getBooleanProperty("Spisok.Admin.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.9","������ ''admin''",getIntProperty("ball.grant.9")," ������"),
        new UserPreference(UserPreference.BOOLEAN_TYPE,"Spisok.Status.on.off","��������/��������� ������� ���������� ''status_user''",getBooleanProperty("Spisok.Status.on.off"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"ball.grant.10","���������� ''status_user''",getIntProperty("ball.grant.10")," ������"),
        //��������� ������
        new UserPreference(UserPreference.CATEGORY_TYPE,"clan", "��������� ������" +"","",""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.MaxCount","������������ ���������� ������",getIntProperty("Clan.MaxCount"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.NameLenght","������������ ����� �������� �����",getIntProperty("Clan.NameLenght"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.InfoLenght","������������ ����� ���������� �����",getIntProperty("Clan.InfoLenght"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.Ball_1","������� ����� (����������/���������)  ��� (����������/�����) ������������",getIntProperty("Clan.Ball_1"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.Ball_2","����������� ����� ��������� ����� ����� ��� ������� +�������",getIntProperty("Clan.Ball_2"),""),
        new UserPreference(UserPreference.INTEGER_TYPE,"Clan.Ball_3","����������� ���������� ������ ��� �������� �������� �����",getIntProperty("Clan.Ball_3"),""),
        };
        return p;
        }



    public UserPreference[] getUINPreference(){
        UserPreference[] p = new UserPreference[uinCount()*2+1];
        p[0] = new UserPreference(UserPreference.CATEGORY_TYPE,"conn", "��������� UIN�� ��� �����������","","");
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
        if(uin.equals("0")) return true; //����������� �����
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

    public final void load() {
        File file = new File(PROPS_FILE);
        setDefault();
        try {
            FileInputStream fi = new FileInputStream(file);
//            appProps.load(fi);
            appProps.loadFromXML(fi);
            fi.close();
            Log.info("Load preferences ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.error("Error opening preferences: ");
        }
    }
    
    public final void save() {
        File file = new File(PROPS_FILE);
        File dir = new File(this.PROPS_FOLDER);
        try {
        	if(!dir.exists())
        		dir.mkdirs();
            FileOutputStream fo = new FileOutputStream(file);
//            appProps.store(fo,"jImBot properties");
            appProps.storeToXML(fo, "jImBot properties");
            fo.close();
            Log.info("Save preferences ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.error("Error saving preferences: ");
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
     * ��������� ����
     * @param i
     * @param uin
     * @param pass
     */
    public void setUin(int i, String uin, String pass){
    	setStringProperty("conn.uin"+i, uin);
    	if(!pass.equals("")) setStringProperty("conn.pass"+i, pass);
    }
    
    /**
     * ���������� ������ ���� � ���������
     * @param uin - ���
     * @param pass - ������
     * @return - ���������� ����� ������ ����
     */
    public int addUin(String uin, String pass){
    	int c = uinCount();
    	setIntProperty("conn.uinCount", c+1);
    	setStringProperty("conn.uin"+c, uin);
    	setStringProperty("conn.pass"+c, pass);
    	return c;
    }
    
    /**
     * �������� ���� �� ��������
     * @param c
     */
    public void delUin(int c) {
    	// �������� �������� ����� ����������
    	for(int i=0; i<(uinCount()-1); i++){
    		if(i>=c){
    			setStringProperty("conn.uin"+i, getUin(i+1));
    			setStringProperty("conn.pass"+i, getPass(i+1));
    		}
    	}
    	//������� ����� ��������� �������
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
        //�������������� �������
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
    //Log.info("����� log ������� ��������");
    //srv.us.db.executeQuery(" TRUNCATE `events` ");
    //srv.us.db.executeQuery(" TRUNCATE `log` ");
    //Log.info("������� log � events ������� ��������");
    }

    public   void del(String name) {
    File i = new File (name);
      if (i.exists()) i.delete();
    }


}
