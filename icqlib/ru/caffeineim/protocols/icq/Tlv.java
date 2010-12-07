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
public class Tlv extends DataContainer {

	private static final int TLV_HEADER_SIZE = 4;

	private RawData type;
	private RawData length;

	private byte[] headerByteArray;

	/**
	 * Create a TLV which value isn't defined at creation.
	 * Based on Onix's code.
	 * @param type The Tlv's type.
	 */
	public Tlv(int type) {
		headerByteArray = new byte[TLV_HEADER_SIZE];
		/* Setting type */
		this.type = new RawData(type, RawData.WORD_LENGHT);
	}

	public Tlv(RawData value, int tlvType) {
		this(tlvType);
		/* Setting the value */
		appendRawDataToTlv(value);
	}

	public Tlv(Tlv value, int tlvType){
		this(tlvType);
		/* Setting the value */
		appendTlvToTlv(value);
	}

	/**
	 * Create a TLV containing a String.
	 *
	 * @param string The TLV's String.
	 * @param tlvType The TLV's type.
	 */
	public Tlv(String string, int tlvType) {
		this(new RawData(string), tlvType);
	}

	/**
	 * Create a TLV containing a numeric value.
	 *
	 * @param val The TLV's numeric value.
	 * @param tlvType The TLV's type.
	 */
	public Tlv(int val, int tlvType) {
		this(new RawData(val), tlvType);
	}

	/**
	 *
	 * @param val The TLV's numeric value.
	 * @param forcedLenght The type of variable.
	 * @param tlvType The TLV's type.
	 */
	public Tlv(int val, int forcedLenght, int tlvType) {
		this(new RawData(val, forcedLenght), tlvType);
	}

	public Tlv(byte[] array, int start) {
		headerByteArray = new byte[TLV_HEADER_SIZE];
		headerModified = true;
		byte[] typeArray = new byte[2];
		byte[] lengthArray = new byte[2];
		byte[] valueArray;

		System.arraycopy(array, start, typeArray, 0, 2);
		this.type = new RawData(typeArray);
		System.arraycopy(array, start + 2, lengthArray, 0, 2);
		this.length = new RawData(lengthArray);
		valueArray = new byte[getLength()];
		System.arraycopy(array, start + 4, valueArray, 0, getLength());
		appendRawDataToTlv(new RawData(valueArray));
	}

	public void appendRawDataToTlv(RawData data) {
		addDataField(data);
	}

	public void appendTlvToTlv(Tlv tlv) {
		addDataField(tlv);
	}

	public byte[] getHeaderByteArray() {
		if (headerModified) {
			/* computing dataFieldLength field */
			length = new RawData(getDataFieldByteArray().length, RawData.WORD_LENGHT);
			/* adding type field array */
			System.arraycopy(type.getByteArray(), 0, headerByteArray, 0,
					type.getByteArray().length);
			/* adding lenght field array */
			System.arraycopy(length.getByteArray(), 0, headerByteArray, 2,
					length.getByteArray().length);
			headerModified = false;
		}
		
		return headerByteArray;
	}

	/**
	 * @return The TLV's type.
	 */
	public int getType() {
		return type.getValue();
	}

	/**
	 * @return The TLV's length.
	 */
	public int getLength() {
		return length.getValue();
	}

	/**
	 * @return The TLV's value.
	 */
	public int getValue() {
		return ( (RawData) elementAt(0)).getValue();
	}

	/**
	 * @return The TLV's String value.
	 */
	public String getStringValue() {
		return ( (RawData) elementAt(0)).getStringValue();
	}
}
