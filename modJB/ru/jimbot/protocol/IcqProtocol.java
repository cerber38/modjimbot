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

package ru.jimbot.protocol;

import java.util.Observable;
import java.util.Observer;

import ru.caffeineim.protocols.icq.contacts.ContactList;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.ContactListEvent;
import ru.caffeineim.protocols.icq.integration.events.IncomingMessageEvent;
import ru.caffeineim.protocols.icq.integration.events.IncomingUrlEvent;
import ru.caffeineim.protocols.icq.integration.events.IncomingUserEvent;
import ru.caffeineim.protocols.icq.integration.events.LoginErrorEvent;
import ru.caffeineim.protocols.icq.integration.events.MessageAckEvent;
import ru.caffeineim.protocols.icq.integration.events.MessageErrorEvent;
import ru.caffeineim.protocols.icq.integration.events.MessageMissedEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaAffilationsUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaBasicUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaEmailUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaInterestsUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaMoreUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaNoteUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaShortUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaWorkUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.OffgoingUserEvent;
import ru.caffeineim.protocols.icq.integration.events.OfflineMessageEvent;
import ru.caffeineim.protocols.icq.integration.events.SsiAuthReplyEvent;
import ru.caffeineim.protocols.icq.integration.events.SsiAuthRequestEvent;
import ru.caffeineim.protocols.icq.integration.events.SsiFutureAuthGrantEvent;
import ru.caffeineim.protocols.icq.integration.events.SsiModifyingAckEvent;
import ru.caffeineim.protocols.icq.integration.events.StatusEvent;
import ru.caffeineim.protocols.icq.integration.events.UINRegistrationFailedEvent;
import ru.caffeineim.protocols.icq.integration.events.UINRegistrationSuccessEvent;
import ru.caffeineim.protocols.icq.integration.events.XStatusRequestEvent;
import ru.caffeineim.protocols.icq.integration.events.XStatusResponseEvent;
import ru.caffeineim.protocols.icq.integration.listeners.ContactListListener;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;
import ru.caffeineim.protocols.icq.integration.listeners.MetaInfoListener;
import ru.caffeineim.protocols.icq.integration.listeners.StatusListener;
import ru.caffeineim.protocols.icq.integration.listeners.XStatusListener;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.XStatusModeEnum;
import ru.caffeineim.protocols.icq.tool.OscarInterface;
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.modules.MsgOutQueue;
import ru.jimbot.modules.chat.Users;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/** 
 * @author Prolubnikov Dmitry
 */

public class IcqProtocol extends AbstractProtocol 
implements MessagingListener, 
           StatusListener,
           XStatusListener,
           ContactListListener,
           MetaInfoListener,
           Observer {
private OscarConnection con = null;
private AbstractProps props;
private String lastInfo = "";
int xStatusId;
String xStatusText;
	
public IcqProtocol(AbstractProps props)
{
this.props = props;
server = MainProps.getServer();
port = MainProps.getPort();
mq = new MsgOutQueue(this, props.getIntProperty("bot.pauseOut"), 
props.getIntProperty("bot.pauseRestart"), 
props.getIntProperty("bot.msgOutLimit"));
}
	
public AbstractProps getProps()
{
return props;
}
	
public int getOuteqSize()
{
return mq.size();
}
	
public void connect() {
mq.start();
con = new OscarConnection(server, port, screenName, password);
con.getPacketAnalyser().setDebug(false);
con.getPacketAnalyser().setDump(false);
con.addMessagingListener(this);
con.addStatusListener(this);
con.addXStatusListener(this);
con.addContactListListener(this);
con.addMetaInfoListener(this);        
con.addObserver(this);
}
	
public void reConnect(){
try {
con.close();
con.removeContactListListener(this);
con.removeMessagingListener(this);
con.removeStatusListener(this);
con.removeXStatusListener(this);
con.deleteObservers();
con = null;
}
catch (Exception e)
{
e.printStackTrace();
}
con = new OscarConnection(server, port, screenName, password);
con.getPacketAnalyser().setDebug(false);
con.getPacketAnalyser().setDump(false);
con.addMessagingListener(this);
con.addStatusListener(this);
con.addXStatusListener(this);
con.addContactListListener(this);
con.addObserver(this);
}

public void disconnect() {
mq.stop();
try {
con.close();
con.removeContactListListener(this);
con.removeMessagingListener(this);
con.removeStatusListener(this);
con.removeXStatusListener(this);
con.deleteObservers();
}
catch (Exception ex)
{
ex.printStackTrace();
}
}


public void getMsg(String sendSN, String recivSN, String msg,
boolean isOffline)
{
protList.getMsg(sendSN, recivSN, msg, isOffline);
}


public boolean isOnLine()
{
if(con==null) return false;
return con.isLogged();
}

public void sendMsg(String sn, String msg)
{
try
{
OscarInterface.sendBasicMessage(con, sn, msg);
}
catch (ConvertStringException e)
{
Log.getLogger(serviceName).info("ERROR send message: " + msg);
e.printStackTrace();
}
}

	
public boolean userInfoRequest(String sn, String rsn)
{
lastInfo = sn;
OscarInterface.requestShortUserInfo(con, sn);
return true;
}

public boolean isNoAuthUin(String uin)
{
return props.getBooleanProperty("chat.isAuthRequest");
}
	
public void authRequest(String uin, String msg){
try {
ContactList.sendAuthRequestMessage(con, uin, msg);
}
catch (ConvertStringException e)
{
e.printStackTrace();
}
}
	

public void onIncomingMessage(IncomingMessageEvent e)
{
if(MainProps.isIgnor(e.getSenderID()))
{
Log.getLogger(serviceName).flood2("IGNORE LIST: " + e.getMessageId() + "->" + screenName + ": " + e.getMessage());
return;
}
if(e.getSenderID().equals("1"))
{
Log.getLogger(serviceName).error("Ошибка совместимости клиента ICQ. Будет произведена попытка переподключения...");
try{
con.close();
}
catch (Exception ex) {ex.printStackTrace();
}
return;
}
protList.getMsg(e.getSenderID(), screenName, e.getMessage(), false);
}


public void onAuthorizationFailed(LoginErrorEvent e)
{
Log.getLogger(serviceName).error("Авторизация с сервером ICQ не удалась. Причина: " +  e.getErrorMessage());
}

public void onIncomingUser(IncomingUserEvent e) {
Log.getDefault().debug(e.getIncomingUserId() + " has just signed on.");
protList.getStatus(e.getIncomingUserId(), 0);
}

public void onLogout()
{
Log.getLogger(serviceName).error("Разрыв соединения: " + screenName + " - " + server + ":" + port);
}

public void onOffgoingUser(OffgoingUserEvent e)
{
protList.getStatus(e.getOffgoingUserId(), -1);
}

public void onStatusChange(StatusEvent e)
{
Log.getLogger(serviceName).debug("StatusEvent: " + e.getStatusMode());
}


public void onXStatusRequest(XStatusRequestEvent e) {
try {
OscarInterface.sendXStatus(con, new XStatusModeEnum(props.getIntProperty("icq.xstatus")), 
props.getStringProperty("icq.STATUS_MESSAGE1"), 
props.getStringProperty("icq.STATUS_MESSAGE2"), e.getTime(), e.getMsgID(), e.getSenderID(), e.getSenderTcpVersion());
}
catch(ConvertStringException ex)
{
System.err.println(ex.getMessage());
}
}

public void update(Observable arg0, Object arg1)
{
OscarInterface.changeStatus(con, new StatusModeEnum(props.getIntProperty("icq.status")));
OscarInterface.changeXStatus(con, new XStatusModeEnum(props.getIntProperty("icq.xstatus")));
}


public void onBasicUserInfo(MetaBasicUserInfoEvent e) {
Users u = new Users();
u.sn = lastInfo;
u.nick = e.getNickName();
u.fname = e.getFirstName();
u.lname = e.getLastName();
u.email = e.getEmail();
u.city = e.getHomeCity();
u.country = e.getHomeCountry().getCountry();
protList.getInfo(u, 1);
}

public void onShortUserInfo(MetaShortUserInfoEvent e) {
Users u = new Users();
u.sn = lastInfo;
u.nick = e.getNickName();
u.fname = e.getFirstName();
u.lname = e.getLastName();
u.email = e.getEmail();
protList.getInfo(u, 1);
}

public void ListStatus(IcqProtocol proc, String uin){
String s = "Список XStatus'ов\n" ;
s+="1 - Сердитый\n";
s+="2 - Купаюсь\n";
s+="3 - Уставший\n";
s+="4 - Вечеринка\n";
s+="5 - Пью пиво\n";
s+="6 - Думаю\n";
s+="7 - Кушаю\n";
s+="8 - Телевизор\n";
s+="9 - Друзья\n";
s+="10 - Пью чай/кофе\n";
s+="11 - Слушаю музыку\n";
s+="12 - Дела\n";
s+="13 - В кино\n";
s+="14 - Развлекаюсь\n";
s+="15 - Телефон\n";
s+="16 - Играю \n";
s+="17 - Учёба \n";
s+="18 - Магазин \n";
s+="19 - Болею \n";
s+="20 - Сплю \n";
s+="21 - Отрываюсь \n";
s+="22 - В интернете \n";
s+="23 - На работе \n";
s+="24 - Печатаю \n";
s+="25 - Пикник \n";
s+="26 - КПК \n";
s+="27 - Мобильник \n";
s+="28 - Засыпаю \n";
s+="29 - Туалет \n";
s+="30 - Вопрос \n";
s+="31 - Дорога \n";
s+="32 - Сердце \n";
s+="33 - Поиск \n";
s+="34 - Дневник\n";
proc.mq.add(uin, s);
}

/**
 * @param n - номер х-статуса
 *
 * @param text - текст х-статуса
 */

public void setXStatus( int n, String text ){
if ( n >= 0 && n <= 37 )
{
xStatusId = n;
props.setIntProperty( "icq.xstatus", xStatusId );
}
if ( !text.equals( "" ) )
{
xStatusText = text;
props.setStringProperty("icq.STATUS_MESSAGE2", xStatusText);
}
OscarInterface.changeXStatus(con, new XStatusModeEnum(xStatusId));
}

/**
 * Для авто-смены статуса
 * @param number
 */

public void setXStatusNumber( int number ){
OscarInterface.changeXStatus(con, new XStatusModeEnum(number));
}

public void onEmailUserInfo(MetaEmailUserInfoEvent e) {}
public void onInterestsUserInfo(MetaInterestsUserInfoEvent e) {}
public void onMoreUserInfo(MetaMoreUserInfoEvent e) {}
public void onNotesUserInfo(MetaNoteUserInfoEvent e) {}
public void onRegisterNewUINFailed(UINRegistrationFailedEvent e) {}
public void onRegisterNewUINSuccess(UINRegistrationSuccessEvent e) {}
public void onWorkUserInfo(MetaWorkUserInfoEvent e) {}
public void onSsiAuthReply(SsiAuthReplyEvent arg0) {}
public void onSsiAuthRequest(SsiAuthRequestEvent arg0) {}
public void onSsiFutureAuthGrant(SsiFutureAuthGrantEvent arg0) {}
public void onSsiModifyingAck(SsiModifyingAckEvent arg0) {}
public void updateContactList(ContactListEvent arg0) {}
public void onAffilationsUserInfo(MetaAffilationsUserInfoEvent e) {}
public void onXStatusChange(XStatusResponseEvent arg0) {}
public void onIncomingUrl(IncomingUrlEvent e) {}
public void onMessageAck(MessageAckEvent e) {}
public void onMessageError(MessageErrorEvent e) {}
public void onMessageMissed(MessageMissedEvent e) {}
public void onOfflineMessage(OfflineMessageEvent e) {}
public void getStatus(String sn, int status) {}
public void RemoveContactList(String sn) {}
public void addContactList(String sn) {}
public void setStatus(int status) {}

}
