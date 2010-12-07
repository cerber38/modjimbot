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
package ru.caffeineim.protocols.icq.packet.received.meta;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.metainfo.IMetaInfoParser;
import ru.caffeineim.protocols.icq.metainfo.MetaInfoParserFactory;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.setting.enumerations.MetaTypeEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel 
 */
public class ServerMetaReply__21_3 extends ReceivedPacket {
	
	private int metaType = 0;
	private int metaSubType = 0;	
	
	private IMetaInfoParser metaInfoParser = null; 

	public ServerMetaReply__21_3(byte[] array) throws ConvertStringException {
		super(array, true);

		int position = 0;
		byte[] data = getSnac().getDataFieldByteArray();

		// Skipping 4 byte of TLV(1) + length field + my uin
		position += 10;

		// Retreiving meta-type command
		RawData rdMetaType = new RawData(data, position, RawData.WORD_LENGHT);
		rdMetaType.invertIndianness();
		metaType = rdMetaType.getValue();
		position += 2;

		// Skip the sequence
		//RawData sequence = new RawData(data, position, RawData.WORD_LENGHT);
		position += 2;
		
		// Retreiving the meta-subtype command
		if (metaType == MetaTypeEnum.SERVER_ADVANCED_META) {
			RawData metaSubTypeRD = new RawData(data, position, RawData.WORD_LENGHT);
			metaSubTypeRD.invertIndianness();
			metaSubType = metaSubTypeRD.getValue();			
		}
		
		// Build parser
		metaInfoParser = MetaInfoParserFactory.buildMetaInfoParser(metaType, metaSubType); 
		
		// Parse metainfo data
		if (metaInfoParser != null)
			metaInfoParser.parse(data, position);
	}

	public void execute(OscarConnection connection) {
		if (metaInfoParser != null)
			metaInfoParser.execute(connection);
	}

	public void notifyEvent(OscarConnection connection) {
		if (metaInfoParser != null)
			metaInfoParser.notifyEvent(connection);
	}	
}