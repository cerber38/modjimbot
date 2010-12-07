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
package ru.caffeineim.protocols.icq.packet.sent.generic;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Tlv;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel
 */
public class AuthorizationRequest extends Flap {

	private AuthorizationRequest() {
		super(1);
	}

	/**
	 * Create the Authorization Request packet giving the sequence number, the
	 * user's UIN and the corresponding password, but this constructor will use
	 * some default settings such as language and country.
	 *
	 * @param Uin The user's UIN.
	 * @param password The password.
	 */
	public AuthorizationRequest(String Uin, String password) {
		// Притворяемся QIPом
		this(Uin, password, "ICQBasic", 0x14, 0x34, 0x00, 0x02, "en", "us");
	}

	/**
	 * Full customiseabble constructor for the Authorization Request packet.
	 *
	 * @param Uin The user's UIN.
	 * @param password The password.
	 * @param clientProfile A string that could identify your client.
	 * @param majorVersion Major version of your client.
	 * @param minorVersion Minor version of your client.
	 * @param lesserVersion Lesser version of your client.
	 * @param buildNumber Build number of your client.
	 * @param language A 2 characters String that identify your language.
	 * @param country A 2 characters String that identify your country.
	 */
	public AuthorizationRequest(String Uin, String password,
			String clientProfile, int majorVersion, int minorVersion,
			int lesserVersion, int buildNumber, String language, String country) {

		this();

		/* -== ADDING FIELDS TO THE PACKET ==- */
		// 4 BYTE 00 00 00 01
		addRawDataToFlap(new RawData(0x00000001, RawData.DWORD_LENGHT));
		// TLV(1) STRING my uin
		addTlvToFlap(new Tlv(Uin, 0x01));
		// TLV(2) STRING encrypted password
		addTlvToFlap(new Tlv(encryptPassword(password), 0x02));
		// TLV(3) STRING client profile
		addTlvToFlap(new Tlv(clientProfile, 0x03));
		// TLV(16) WORD unk, usually 01 0A
		addTlvToFlap(new Tlv(0x010A, 0x16));
		// TLV(17) WORD major version
		addTlvToFlap(new Tlv(majorVersion, RawData.WORD_LENGHT, 0x17));
		// TLV(18) WORD minor version
		addTlvToFlap(new Tlv(minorVersion, RawData.WORD_LENGHT, 0x18));
		// TLV(19) WORD lesser version
		addTlvToFlap(new Tlv(lesserVersion, RawData.WORD_LENGHT, 0x19));
		// TLV(1A) WORD build version
		addTlvToFlap(new Tlv(buildNumber, RawData.WORD_LENGHT, 0x1A));
		// TLV(14) DWORD dunno version
		addTlvToFlap(new Tlv(0x0000043D, RawData.DWORD_LENGHT, 0x14));
		// TLV(0F) STRING language, 2 chars, usually 'en'
		addTlvToFlap(new Tlv(language, 0x0F));
		// TLV(0E) STRING country, 2 chars, usually 'us'
		addTlvToFlap(new Tlv(country, 0x0E));
	}

	/**
	 * This function encrypt the password using the weak :-) icq encryption
	 * scheme.
	 *
	 * @param password The password to be encrypted.
	 * @return Your encrypted password.
	 */
	private String encryptPassword(String password) {
		byte bytePassword[] = password.getBytes();
		char charPass[] = new char[bytePassword.length];
		final byte[] xorValues = { (byte) 0xF3, (byte) 0x26, (byte) 0x81,
				(byte) 0xC4, (byte) 0x39, (byte) 0x86, (byte) 0xDB, (byte) 0x92 };

		for (int i = 0, j = 0; i < bytePassword.length; i++, j++) {
			if (j >= xorValues.length) {
				j = 0;
			}
			charPass[i] = (char) ((char) (bytePassword[i]) ^ (char) (xorValues[j]));
		}

		return new String(charPass);
	}
}
