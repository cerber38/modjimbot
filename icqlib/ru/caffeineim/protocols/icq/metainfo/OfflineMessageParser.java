/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ru.caffeineim.protocols.icq.metainfo;

import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.OfflineMessageEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageFlagsEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageTypeEnum;
import ru.caffeineim.protocols.icq.tool.DateTools;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 23.03.2008
 *   @author Samolisov Pavel
 */
public class OfflineMessageParser extends BaseMetaInfoParser {

	private static Log log = LogFactory.getLog(OfflineMessageParser.class);

    private String senderUin;
    private Date sendDate;
    private String message;
    private int type;
    private int flag;

    protected EventObject getNewEvent() {
        return new OfflineMessageEvent(this);
    }

    protected void sendMessage(EventListener listener, EventObject e) {
    	log.debug("notify listener " + listener.getClass().getName() + " onOfflineMessage()");
        ((MessagingListener) listener).onOfflineMessage((OfflineMessageEvent) e);
    }

    public void parse(byte[] data, int position) throws ConvertStringException {
        // Retreiving sender uin
        RawData uin = new RawData(data, position, RawData.DWORD_LENGHT);
        uin.invertIndianness();
        senderUin = uin.toStringValue();
        position += RawData.DWORD_LENGHT;

        // Retreiving year
        RawData year = new RawData(data, position, RawData.WORD_LENGHT);
        year.invertIndianness();
        position += RawData.WORD_LENGHT;

        // Retreiving month
        RawData month = new RawData(data, position, RawData.BYTE_LENGHT);
        position += RawData.BYTE_LENGHT;

        // Retreiving day
        RawData day = new RawData(data, position, RawData.BYTE_LENGHT);
        position += RawData.BYTE_LENGHT;

        // Retreiving hour
        RawData hour = new RawData(data, position, RawData.BYTE_LENGHT);
        position += RawData.BYTE_LENGHT;

        // Retreiving minute
        RawData minute = new RawData(data, position, RawData.BYTE_LENGHT);
        position += RawData.BYTE_LENGHT;
        sendDate = DateTools.makeDate(year.getValue(), month.getValue(), day.getValue(), hour.getValue(), minute.getValue());

        // Retreiving message type
        type = new RawData(data, position, RawData.BYTE_LENGHT).getValue();
        position += RawData.BYTE_LENGHT;

        // Retreiving message flag
        flag = new RawData(data, position, RawData.BYTE_LENGHT).getValue();
        position += RawData.BYTE_LENGHT;

        // Retreiving message length
        RawData msgLen = new RawData(data, position, RawData.WORD_LENGHT);
        msgLen.invertIndianness();
        position += RawData.WORD_LENGHT;

        // Retreiving message
        message = StringTools.byteArrayToString(data, position, msgLen.getValue() - 1);
    }

    protected List getListenersList(OscarConnection connection) {
        return connection.getMessagingListeners();
    }

    public String getSenderUin() {
        return senderUin;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public String getMessage() {
        return message;
    }

    public MessageTypeEnum getMessageType() {
          return new MessageTypeEnum(type);
      }

    public MessageFlagsEnum getMessageFlag() {
        return new MessageFlagsEnum(flag);
    }
}
