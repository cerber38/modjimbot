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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;

import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;

/**
 *   @author Samolisov Pavel
 *   @author Andreas Rossbacher
 *   @author Artyomov Denis
 *   @author Dmitry Tunin
 *   @since  15.08.07
 */
public class StringTools {

	public static final String UTF8_ENCODING = "UTF-8";

	/**
	 * Convert String to Array of bytes in UTF-8 encoding
	 *
	 * @param s source string
	 * @return byte array for sended
	 */
	public static byte[] stringToByteArray(String s) throws ConvertStringException {
		try {
			return s.getBytes(UTF8_ENCODING);
		}
		catch (UnsupportedEncodingException e) {
			throw new ConvertStringException(UTF8_ENCODING + " not supported in your system");
		}
	}

	/**
	 * Create String from UTF-8 byte-array
	 *
	 * @param arr
	 * @param off
	 * @param len
	 * @return
	 * @throws ConvertStringException
	 */
	public static String utf8ByteArrayToString(byte[] arr, int off, int len)
			throws ConvertStringException {
    	// Length check
        if (off + len > arr.length) {
            return "";
        }

        // Remove \0's at the end
        while ((len > 0) && (arr[off + len - 1] == 0x00))
        {
            len--;
        }

        try {
        	return new String(arr, off, len, UTF8_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
        	throw new ConvertStringException(UTF8_ENCODING + " not supported in your system");
		}
	}

	/**
	 * Convert String to bytes in UCS2 BE encoding
	 *
	 * @param s source string
	 * @return byte array for sended
	 */
	public static byte[] stringToUcs2beByteArray(String s) {
		String str = restoreCrLf(s);
        byte[] ucs2be = new byte[str.length() * 2];
        for (int i = 0; i < str.length(); i++) {
           ucs2be[i*2] = (byte) (((int) str.charAt(i) >> 8) & 0x000000FF);
           ucs2be[i*2+1] = (byte) (((int) str.charAt(i)) & 0x000000FF);
        }

        return ucs2be;
    }

	/**
	 * Extract a UCS-2BE string from the Array of bytes
	 *
	 * @param arr
	 * @param off
	 * @param len
	 * @return
	 */
    public static String ucs2beByteArrayToString(byte[] arr, int off, int len) {
    	// Length check
        if ((off + len > arr.length) || (len % 2 != 0)) {
            return "";
        }

        // Convert from byte array to string
        StringBuffer sb = new StringBuffer();

        for (int i = off; i < off+len; i += 2)
        {
        	char ch = (char) ((((int) arr[i]) << 8) & 0x0000FF00 | (((int) arr[i+1])) & 0x000000FF);
            sb.append(ch);
        }

        return sb.toString();
    }

    /**
     * Convert not UCS-2BE and not UTF-8 byte array to UTF-8 String
     *
     * @param arr
     * @param off
     * @param len
     * @return
     * @throws ConvertStringException
     */
    public static String byteArrayToString(byte[] arr, int off, int len) throws ConvertStringException {
    	// Length check
        if (off + len > arr.length) {
            return "";
        }

        // TODO разобраться с определением кодировки. Пока считаем что если не юникод, то cp1251
        return byteArray1251ToString(arr, off, len);
    }

    /**
     * Convert array of CP1251 bytes to UTF-8
     *
     * @param data
     * @param off
     * @param len
     */
    public static String byteArray1251ToString(byte[] data, int off, int len) {
        String s = new String(data, off, len);

        StringBuffer stringbuffer = new StringBuffer(len);
        for(int k = 0; k < len; k++) {
            int l = data[k + off] & 0xff;

            switch (l) {
            	case 168:
            		stringbuffer.append('\u0401');
            		break;
            	case 184:
            		stringbuffer.append('\u0451');
            		break;

            	/* Ukrainian CP1251 chars section */
            	case 165:
            		stringbuffer.append('\u0490');
            		break;
            	case 170:
            		stringbuffer.append('\u0404');
            		break;
            	case 175:
            		stringbuffer.append('\u0407');
            		break;
            	case 178:
            		stringbuffer.append('\u0406');
            		break;
            	case 179:
            		stringbuffer.append('\u0456');
            		break;
            	case 180:
            		stringbuffer.append('\u0491');
            		break;
            	case 186:
            		stringbuffer.append('\u0454');
            		break;
            	case 191:
            		stringbuffer.append('\u0457');
            		break;
            	/* end of section */

            	default:
            		if (l >= 192 && l <= 255) {
            			stringbuffer.append((char) ((1040 + l) - 192));
            		}
            		else {
            			stringbuffer.append(s.charAt(k));
            		}
            		break;
            }
        }

        return stringbuffer.toString();
    }

	/**
	 * Restore the simbols of string end.
	 *
	 * @param s source string
	 * @returne
	 */
	public static String restoreCrLf(String s) {
        StringBuffer result = new StringBuffer();

        int size = s.length();
        for (int i = 0; i < size; i++) {
            char chr = s.charAt(i);
            if (chr == '\r') continue;
            if (chr == '\n') result.append("\r\n");
            else result.append(chr);
        }
        return result.toString();
    }

	/**
	 * Removes all CR occurences
	 *
	 * @param s source string
	 * @return
	 */
    public static String removeCr(String s) {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            char chr = s.charAt(i);
            if ((chr == 0) || (chr == '\r')) continue;
            result.append(chr);
        }
        return result.toString();
    }

	/**
	 * Convert String from Windows-1251 encoding to UTF8
	 *
	 * @param s source string
	 * @return string in UTF8 Encoding
	 * @throws ConvertStringException
	 */
    public static String stringCP1251ToUTF8(String s) throws ConvertStringException {
        String res = "";

    	try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(s);
            byte[] raw = baos.toByteArray();
            byte[] result = new byte[raw.length-2];
            System.arraycopy(raw, 2, result, 0, raw.length-2);

            String ent = new String(result);
            for (int i = 0; i < ent.length(); i++) {
            	if (Character.getType(ent.charAt(i)) == Character.CONTROL)
            		res += ".";
        		else
        			res += ent.charAt(i);
        	}
    	}
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
        	return new String(res.getBytes(), UTF8_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
        	throw new ConvertStringException(UTF8_ENCODING + " not supported in your system");
		}
    }

    /**
	 * Convert String from UTF8 encoding to Windows-1251
	 *
	 * @param s source string
	 * @return string in Windows-1251 encoding
	 */
    public static String UTF8ToStringCP1251(String s) {
    	byte[] buf = s.getBytes();
    	String res = "";
        try {
            byte[] buf2 = new byte[buf.length + 2];
            // add length in start string
            buf2[0] = (byte) ((buf.length >> 8) & 0x000000FF);
            buf2[1] = (byte) ((buf.length) & 0x000000FF);
            System.arraycopy(buf, 0, buf2, 2, buf.length);

            // convert string
            ByteArrayInputStream bais = new ByteArrayInputStream(buf2);
            DataInputStream dis = new DataInputStream(bais);

            res =  dis.readUTF();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    	return res;
    }

    /**
     * Return <code>true</code> if string <code>string</code> is null or empty
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
    	return string == null || string.length() < 1;
    }
}
