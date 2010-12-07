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
package ru.caffeineim.protocols.icq.packet.sent.icbm;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class MessageType2Ack extends Flap {

	private static final byte[] capability = {
                                           0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                           0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                           0x00, 0x00};

	private static final byte[] unknown12 = {
                                          0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                          0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

	private static final byte[] msgTypeFooter = {
                                              0x00, 0x00, 0x00, 0x00,
                                              (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                                              (byte) 0x00};

	public MessageType2Ack(RawData Time, RawData msgID,
			String UIN, RawData senderTcpVer, RawData msgSeqNum,
			RawData msgType, RawData msgFlags,
			String requestedMessage) {

		super(2);

		Snac snac = new Snac(0x04, 0x0B, 0x00, 0x00, 0x00);

		/* Adding a copy of the time */
		snac.addRawDataToSnac(Time);

		/* Adding the message ID we want to ack */
		snac.addRawDataToSnac(msgID);

		/* Adding the message type */
		snac.addRawDataToSnac(new RawData(0x02, RawData.WORD_LENGHT));

		/* Adding the length of the UIN [i.e : B-UIN] */
		snac.addRawDataToSnac(new RawData(UIN.length(), RawData.BYTE_LENGHT));

		/* Adding the UIN itself */
		snac.addRawDataToSnac(new RawData(UIN));

		/* Unknown */
		snac.addRawDataToSnac(new RawData(0x03, RawData.WORD_LENGHT));

		/* Adding len of a chunk [i don't represent it here ] */
		snac.addRawDataToSnac(new RawData(0x1B00));

		/* Adding TCP version */
		snac.addRawDataToSnac(senderTcpVer);

		/* adding fake capability */
		snac.addRawDataToSnac(new RawData(capability));

		/* Unknown */
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));

		/* Unknown */
		snac.addRawDataToSnac(new RawData(0x03000000, RawData.DWORD_LENGHT));

		/* Unknown */
		snac.addRawDataToSnac(new RawData(0x00, RawData.BYTE_LENGHT));

		/* Adding the acknowledged packet sequence */
		snac.addRawDataToSnac(msgSeqNum);

		/* Unknown */
		snac.addRawDataToSnac(new RawData(0x0E00, RawData.WORD_LENGHT));

		/* Adding the acknowledged packet sequence */
		snac.addRawDataToSnac(msgSeqNum);

		/* Unknown */
		snac.addRawDataToSnac(new RawData(unknown12));

		/* Adding message type */
		snac.addRawDataToSnac(msgType);

		/* Adding message flag */
		snac.addRawDataToSnac(msgFlags);

		/* Status */
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));

		/* Priority */
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));

		/* requested message (i.e DND, away etc...) */
		RawData rLen = new RawData(requestedMessage.length() + 1,
                   RawData.WORD_LENGHT);
		rLen.invertIndianness();
		snac.addRawDataToSnac(rLen);
		snac.addRawDataToSnac(new RawData(requestedMessage));
		snac.addRawDataToSnac(new RawData(0x00, RawData.BYTE_LENGHT));

		/* Adding text message's footer */
		snac.addRawDataToSnac(new RawData(msgTypeFooter));

		this.addSnac(snac);
	}

	public MessageType2Ack(RawData time, RawData msgID,
			String UIN, RawData senderTcpVer, RawData msgSeqNum,
			RawData msgType, RawData msgFlags) {

		this(time, msgID, UIN, senderTcpVer, msgSeqNum, msgType, msgFlags, "");
	}
}