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


import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.OscarInterface;
import ru.caffeineim.protocols.icq.integration.events.*;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;
import ru.caffeineim.protocols.icq.integration.listeners.OurStatusListener;
import ru.caffeineim.protocols.icq.integration.listeners.XStatusListener;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.XStatusModeEnum;
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.modules.MsgOutQueue;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 * @author Prolubnikov Dmitry
 */

public class IcqProtocol extends AbstractProtocol
implements OurStatusListener,
           MessagingListener,
           XStatusListener{
private OscarConnection con = null;
private AbstractProps props;
int xStatusId;
String xStatusText;
private boolean connected = false;

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
con.addOurStatusListener(this);
con.addMessagingListener(this);
con.addXStatusListener(this);
con.connect();
connected = true;
}

public void reConnect(){
try {
con.close();
con.removeOurStatusListener(this);
con.removeMessagingListener(this);
con.removeXStatusListener(this);
con = null;
connected = false;
}
catch (Exception e)
{
e.printStackTrace();
}
con = new OscarConnection(server, port, screenName, password);
con.addOurStatusListener(this);
con.addMessagingListener(this);
con.addXStatusListener(this);
con.connect();
connected = true;
}

public void disconnect() {
if(con == null) return;
try {
mq.stop();
con.close();
con.removeOurStatusListener(this);
con.removeMessagingListener(this);
con.removeXStatusListener(this);
con = null;
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


public boolean isOnLine() {
if(con == null) return false;
return connected;
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


public boolean isNoAuthUin(String uin)
{
return props.getBooleanProperty("chat.isAuthRequest");
}

public void authRequest(String uin, String msg){}


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
connected = false;
}
catch (Exception ex) {ex.printStackTrace();
}
return;
}
protList.getMsg(e.getSenderID(), screenName, e.getMessage(), false);
}

/**
 UNKNOWN_ERROR           = 0; "Unknown Error"
 BAD_UIN_ERROR           = 1; "Bad UIN.";
 PASSWORD_ERROR          = 2; "Password incorrect.";
 NOT_EXISTS_ERROR        = 3; "This ICQ number does not exist.";
 LIMIT_EXCEEDED_ERROR    = 4; "Rate limit exceeded. Please try to reconnect in a few minutes."
 MAXIMUM_USERS_IP_ERROR  = 5; "The amount of users connected from this IP has reached the maximum."
 OLDER_ICQ_VERSION_ERROR = 6; "You are using an older version of ICQ. Please upgrade."
 CANT_REGISTER_ERROR     = 7; "Can't register on the ICQ network. Reconnect in a few minutes."
 */

public void onAuthorizationFailed(LoginErrorEvent e)
{
Log.getLogger(serviceName).error("Авторизация с сервером ICQ не удалась. Причина: " +  e.getErrorMessage());
connected = false;
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
catch(ConvertStringException ex) {
System.err.println(ex.getMessage());
}
}

public void onLogin() {
OscarInterface.changeStatus(con, new StatusModeEnum(props.getIntProperty("icq.status")));
OscarInterface.changeXStatus(con, new XStatusModeEnum(props.getIntProperty("icq.xstatus")));
}

public void onLogout(Exception excptn) {
Log.getLogger(serviceName).error("Разрыв соединения: " + screenName + " - " + server + ":" + port);
connected = false;
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

    public void onMessageMissed(MessageMissedEvent e) {
    Log.getLogger(serviceName).debug("Message from " + e.getUin() + " can't be recieved because " + e.getReason()  +
                                " count="+e.getMissedMsgCount());
    }

    public void onMessageError(MessageErrorEvent e) {
    Log.getLogger(serviceName).error("Message error " + e.getError().toString());
    }

    public void onStatusResponse(StatusEvent se) {}
    public void setStatus(int status) {}
    public void getStatus(String sn, int status) {}
    public void addContactList(String sn) {}
    public void RemoveContactList(String sn) {}
    public void onIncomingUrl(IncomingUrlEvent iue) {}
    public void onOfflineMessage(OfflineMessageEvent ome) {}
    public void onMessageAck(MessageAckEvent mae) {}
    public void onXStatusResponse(XStatusResponseEvent xsre) {}



}
