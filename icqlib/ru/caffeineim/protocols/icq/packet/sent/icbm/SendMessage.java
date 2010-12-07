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
import ru.caffeineim.protocols.icq.setting.enumerations.MessageChannelEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel
 * 	 @author Andreas Rossbacher
 *   @author Artyomov Denis
 *   @author Dmitry Tunin
 */
public abstract class SendMessage extends Flap {

	protected Snac snac;
	protected RawData channel;

	public SendMessage(String userId, MessageChannelEnum messageChannel) {
		super(2);
		
		snac = new Snac(0x04, 0x06, 0x00, 0x00, 0x00);
    
		// Time
		snac.addRawDataToSnac(new RawData(0x00000000, RawData.DWORD_LENGHT));
    
		// Message id     
		snac.addRawDataToSnac(new RawData(0x00000000, RawData.DWORD_LENGHT));
    
		// channel of the message, 2 by default
		channel = new RawData(messageChannel.getChannelNumber(), RawData.WORD_LENGHT);
		snac.addRawDataToSnac(channel);
    
		// UIN
		snac.addRawDataToSnac(new RawData(userId.length(), RawData.BYTE_LENGHT));
		snac.addRawDataToSnac(new RawData(userId));
	}
}