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
public class Snac extends DataContainer {

	private static final int SNAC_HEADER_SIZE = 10;

	private RawData familyId;
	private RawData subTypeId;
	private RawData flag0;
	private RawData flag1;
	private RawData requestId;
	private byte[] headerByteArray;

	private Snac() {
		headerByteArray = new byte[SNAC_HEADER_SIZE];
	}

	/**
	 * Create a Snac.
	 *
	 * @param familyID Snac's family ID.
	 * @param subTypeID Snac's sub Type ID.
	 * @param flag0 Snac's flag0.
	 * @param flag1 Snac's flag1.
	 * @param requestID Snac's request ID.
	 */
	public Snac(int familyID, int subTypeID, int flag0, int flag1, int requestID) {
		this();
		familyId = new RawData(familyID, RawData.WORD_LENGHT);
		subTypeId = new RawData(subTypeID, RawData.WORD_LENGHT);
		this.flag0 = new RawData(flag0, RawData.BYTE_LENGHT);
		this.flag1 = new RawData(flag1, RawData.BYTE_LENGHT);
		requestId = new RawData(requestID, RawData.DWORD_LENGHT);
	}

	public Snac(byte array[], int start) {
		this();
		byte[] famArray = new byte[2];
		byte[] subFamArray = new byte[2];
		byte[] requestArray = new byte[4];
		byte[] dataFieldArray = new byte[array.length - (start + 10)];

		System.arraycopy(array, start, famArray, 0, 2);
		familyId = new RawData(famArray);
		System.arraycopy(array, start + 2, subFamArray, 0, 2);
		subTypeId = new RawData(subFamArray);

		flag0 = new RawData(array[start + 4]);
		flag1 = new RawData(array[start + 5]);

		System.arraycopy(array, start + 6, requestArray, 0, 4);
		requestId = new RawData(requestArray);

		System.arraycopy(array, start + 10, dataFieldArray, 0,
				array.length - (start + 10));
		this.addRawDataToSnac(new RawData(dataFieldArray));
	}

	/**
	 *
	 * @return The Snac's Family ID.
	 */
	public int getFamilyId() {
		return familyId.getValue();
	}

	/**
	 *
	 * @return The Snac's Sub Type ID.
	 */
	public int getSubTypeId() {
		return subTypeId.getValue();
	}

	/**
	 *
	 * @return The Snac's fisrt flag.
	 */
	public int getFlag0() {
		return flag0.getValue();
	}

	/**
	 *
	 * @return The Snac's second flag.
	 */
	public int getFlag1() {
		return flag1.getValue();
	}

	/**
	 *
	 * @return The Snac's Request ID.
	 */
	public int getRequestId() {
		return requestId.getValue();
	}

	/**
	 * This define the request Id of the Snac.
	 *
	 * @param requestId The request Id of the Snac.
	 */
	public void setRequestId(int requestId) {
		this.requestId = new RawData(requestId, RawData.DWORD_LENGHT);
		headerModified = true;
	}

	/**
	 * This function allow to add a TLV into the Snac.
	 *
	 * @param tlv The TLV to be added.
	 */
	public void addTlvToSnac(Tlv tlv) {
		addDataField(tlv);
	}

	/**
	 * This function add a Raw Data into the Snac.
	 *
	 * @param rawData The Raw Data to be added.
	 */
	public void addRawDataToSnac(RawData rawData) {
		addDataField(rawData);
	}

	public byte[] getHeaderByteArray() {
		if (headerModified) {
			int position = 0;
			/* Adding Header fields */
			System.arraycopy(familyId.getByteArray(), 0, headerByteArray, position,
					familyId.getByteArray().length);
			position += familyId.getByteArray().length;
			System.arraycopy(subTypeId.getByteArray(), 0, headerByteArray, position,
					familyId.getByteArray().length);
			position += familyId.getByteArray().length;
			System.arraycopy(flag0.getByteArray(), 0, headerByteArray, position,
					flag0.getByteArray().length);
			position += flag0.getByteArray().length;
			System.arraycopy(flag1.getByteArray(), 0, headerByteArray, position,
					flag1.getByteArray().length);
			position += flag1.getByteArray().length;
			System.arraycopy(requestId.getByteArray(), 0, headerByteArray, position,
					requestId.getByteArray().length);
			headerModified = false;
		}
		
		return headerByteArray;
	}
}