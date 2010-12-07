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
package ru.caffeineim.protocols.icq.packet.received.icbm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.integration.events.MessageMissedEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageMissedTypeEnum;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class MissedMessage__4_10 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(MissedMessage__4_10.class);

    private Tlv usrClass;
    private Tlv usrStatus;
    private Tlv clientOnlineTime;
    private Tlv offlineTime;

    private String uin;
    private MessageMissedTypeEnum reason;
    private int missedMsgCount;

    public static final short MESSAGE_INVALID       = 0;
    public static final short MESSAGE_TOO_LARGE     = 1;
    public static final short MESSAGE_RATE_EXCEEDED = 2;
    public static final short SENDER_TOO_EVIL       = 3;
    public static final short USER_TOO_EVIL         = 4;

    /**
     * Creates a new instance of MissedMessage__4_10
     */
    public MissedMessage__4_10(byte[] array) {
        super(array, true);

        byte data[] = getSnac().getDataFieldByteArray();
        int index = 0;

        do {
            RawData type = new RawData(data, index, RawData.WORD_LENGHT);
            index += RawData.WORD_LENGHT;
            RawData uinLg = new RawData(data, index, RawData.BYTE_LENGHT);
            index += RawData.BYTE_LENGHT;
            uin = new RawData(data, index, uinLg.getValue()).getStringValue();
            index += uinLg.getValue();
            RawData warningLvl = new RawData(data, index, RawData.WORD_LENGHT);
            index += RawData.WORD_LENGHT;
            RawData nbOfTlv = new RawData(data, index, RawData.WORD_LENGHT);
            index += RawData.WORD_LENGHT;

            usrClass = new Tlv(data, index);
            index += usrClass.getLength() + 4; // header of TLV;

            usrStatus = new Tlv(data, index);
            index += usrStatus.getLength() + 4;

            // пропускаем ненужные TLV
            for (int k = 0; k < nbOfTlv.getValue() - 4; k++) {
                Tlv fakeTlv = new Tlv(data, index);
                index += fakeTlv.getLength() + 4;
            }

            clientOnlineTime = new Tlv(data, index);
            index += clientOnlineTime.getLength() + 4;

            offlineTime = new Tlv(data, index);
            index += offlineTime.getLength() + 4;

            missedMsgCount = new RawData(data, index, RawData.WORD_LENGHT).getValue();
            index += RawData.WORD_LENGHT;

            reason = new MessageMissedTypeEnum(new RawData(data, index, RawData.WORD_LENGHT).getValue());
            index += RawData.WORD_LENGHT;
        }
        while(index < data.length);
    }

    public void execute(OscarConnection connection) throws Exception {
    }

    public void notifyEvent(OscarConnection connection) {
        MessageMissedEvent e = new MessageMissedEvent(this);
        for (int i = 0; i < connection.getMessagingListeners().size(); i++) {
            MessagingListener l = (MessagingListener) connection.getMessagingListeners().get(i);
            log.debug("notify listener " + l.getClass().getName() + " onMessageMissed()");
            l.onMessageMissed(e);
        }
    }

    public int getMissedMsgCount() {
        return missedMsgCount;
    }

    public String getUin() {
        return uin;
    }

    public MessageMissedTypeEnum getReason() {
        return reason;
    }
}
