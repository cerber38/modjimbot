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
package ru.caffeineim.protocols.icq.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class Dumper {

	private static Log log = LogFactory.getLog(Dumper.class);

	private Dumper() {}

	/**
	 * This methods dumps an imcoming packet represented with a byte array
	 * to a more "readable" String version.
	 *
	 * @param packet The received packet to be dumped.
	 * @param stringTranslation Indicates if the byte must be converted to String.
	 * @return A String conversion of the received packet.
	 */
	public static String dump(byte[] packet, boolean stringTranslation) {
		return dump(packet, stringTranslation, 4, 8);
	}

	/**
	 * Actually makes the convertion between the byte array to an understandable
	 * version of the incoming packet.
	 *
	 * @param packet The received packet to be dumped.
	 * @param stringTranslation Indicates if the byte must be converted to String.
	 * @param spacer Indicates the number of characters to write before adding a space character.
	 * @param breaker Indicates the number of characters to write before adding a line break.
	 * @return A String conversion of the received packet.
	 */
	public static String dump(byte[] packet, boolean stringTranslation,
			int spacer, int breaker) {
		String hexa = "";
		int i;
		byte[] hexaChar = new byte[breaker];
		byte[] lastLine;

		for (i = 1; i < packet.length + 1; i++) {
			hexa += toUnsignedHex(packet[i - 1]) + " ";

			if (i % spacer == 0) {
				hexa += " ";
				if (i % breaker == 0) {
					if (stringTranslation) {
						System.arraycopy(packet, i - breaker, hexaChar, 0, breaker);
						hexa += stringTranslation(hexaChar);
					}
					hexa += "\n";
				}
			}
		}
		if (stringTranslation) {
			i--;
			lastLine = new byte[i % breaker];
			System.arraycopy(packet, i - (i % breaker), lastLine, 0, i % breaker);
			hexa += align(i % breaker, breaker);
			hexa += stringTranslation(lastLine);
		}

		return hexa;
	}

	/**
	 * This method returns the String representation of an unsigned byte.
	 *
	 * @param b The byte we want to convert into String.
	 * @return The String representation of an unsigned byte.
	 */
	private static String toUnsignedHex(byte b) {
		/* to get the unsigned value of the byte */
		String hex = Integer.toHexString(b & 0xFF);

		/* Padding to 2 digits. */
		if (hex.length() == 1)
			hex = "0" + hex;

		return hex.toUpperCase();
	}

	/**
	 * This method filters all non-displayable characters and replace them
	 * with a '.' in the resulting String.
	 *
	 * @param array The receive byte array.
	 * @return The representation of all displayable characters.
	 */
	private static String stringTranslation(byte[] array) {
		String ent = new String(array);
		String res = new String();

		for (int i = 0; i < ent.length(); i++) {
			if (Character.getType(ent.charAt(i)) == Character.CONTROL)
				res += ".";
			else
				res += ent.charAt(i);
		}

		return res;
	}

	/**
	 * This method align the outputed String.
	 * This Method hides a buggy dump method. Anyone can seen the bug ?
	 *
	 * @param wrote The number of characters that have been wrote.
	 * @param expected The number of characters that should have been wrote.
	 *
	 * @return A correct aligned String.
	 */
	private static String align(int wrote, int expected) {
		String result = "";
		for (int i = 0; i < expected - wrote; i++) {
			result += "   ";
		}
		result += "  ";

		return result;
	}

	public static void log(byte[] packet, boolean stringTranslation,
			int spacer, int breaker) {
        if (log.isTraceEnabled())
		    log.trace("\n" + dump(packet, stringTranslation, spacer, breaker));
	}
}
