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
package ru.caffeineim.protocols.icq;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class Flap extends DataContainer {

	private static final int FLAP_HEADER_SIZE = 6;

	private RawData commandStart;
	private RawData channelId;
	private RawData sequenceNumber;
	private RawData dataFieldLength;

	private byte[] headerByteArray;
	private boolean hasSnac = false;

	/**
	 * This create an empty Flap. Only the command start (first byte)
	 * will be set.
	 */
	public Flap(){
		headerByteArray = new byte[FLAP_HEADER_SIZE];
		commandStart = new RawData(0x2A);
		this.sequenceNumber = null; // set by OscarConnection before sending
	}

	/**
	 * Construct a Flap giving it a channel ID and a sequence number.
	 * If you use this constructor the Flap could not contains a Snac Section.
	 *
	 * @param channelId The channel the Flap belongs.
	 */
	public Flap(int channelId) {
		this();
		this.channelId = new RawData(channelId, RawData.BYTE_LENGHT);
	}

	/**
	 * Construct a Flap containing a Snac section.
	 *
	 * @param channelId The channel the Flap belongs.
	 * @param snac The Flap's Snac.
	 */
	public Flap(int channelId, Snac snac) {
		this(channelId);
		addSnac(snac);
	}

	/**
	 * Returns the Flap's channel ID.
	 *
	 * @return The channel ID.
	 */
	public int getChannelId() {
		return channelId.getValue();
	}

	/**
	 * Defines the channel on which the packet will be sent.
	 *
	 * @param channelId The channel on which the packet is sent.
	 */
	public void setChannelId(int channelId){
		this.channelId = new RawData(channelId, RawData.BYTE_LENGHT);
		headerModified = true;
	}

	/**
	 * Returns the Flap's sequence number.
	 *
	 * @return The sequence number.
	 */
	public int getSequenceNumber() {
		if (sequenceNumber == null)
			return Integer.MAX_VALUE;
  
		return sequenceNumber.getValue();
	}

	/**
	 * Defines the sequence number of the packet to be sent.
	 *
	 * @param sequenceNumber The sequence number of the packet.
	 */
	public void setSequenceNumber(int sequenceNumber){
		this.sequenceNumber = new RawData(sequenceNumber, RawData.WORD_LENGHT);
		headerModified = true;
	}

	/**
	 * This Function determines if the Flap has a Snac section.
	 *
	 * @return True if the Flap contains a Snac
	 */
	public boolean hasSnac(){
		return this.hasSnac;
	}

	/**
	 * @return Snac the snac contained
	 */
	public Snac getSnac() {
		return (Snac) elementAt(0);
	}

	/**
	 * Let's you add a Snac into the Flap.
	 *
	 * @param snac the Snac to be added.
	 */
	public void addSnac(Snac snac) {
		addDataField(snac);
		this.hasSnac = true;
	}

	/**
	 * Let you add a TLV to the Flap.
	 *
	 * @param tlv The TLv to add.
	 */
	public void addTlvToFlap(Tlv tlv) {
		addDataField(tlv);
	}

	/**
	 * Let you add a RawData to the Flap.
	 *
	 * @param rawData The RawData to add.
	 */
	public void addRawDataToFlap(RawData rawData) {
		addDataField(rawData);
	}

	public byte[] getHeaderByteArray() {
		if (headerModified) {
			/* computing dataFieldLength field */
			dataFieldLength = new RawData(getDataFieldByteArray().length,
                                    RawData.WORD_LENGHT);
			int position = 0;
			/*  Adding Header fields */
			System.arraycopy(commandStart.getByteArray(), 0, headerByteArray,
                       position, commandStart.getByteArray().length);
			position += commandStart.getByteArray().length;
			System.arraycopy(channelId.getByteArray(), 0, headerByteArray, position,
                       channelId.getByteArray().length);
			position += channelId.getByteArray().length;
			System.arraycopy(sequenceNumber.getByteArray(), 0, headerByteArray,
                       position, sequenceNumber.getByteArray().length);
			position += sequenceNumber.getByteArray().length;
			System.arraycopy(dataFieldLength.getByteArray(), 0, headerByteArray,
                       position, dataFieldLength.getByteArray().length);
			headerModified = false;
		}
		
		return headerByteArray;
	}
}