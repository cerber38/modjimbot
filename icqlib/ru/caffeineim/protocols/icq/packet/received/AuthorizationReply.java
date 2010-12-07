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
package ru.caffeineim.protocols.icq.packet.received;

import java.io.IOException;

import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.core.OscarClient;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.core.exceptions.LoginException;
import ru.caffeineim.protocols.icq.setting.enumerations.LoginErrorTypeEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class AuthorizationReply extends ReceivedPacket {

	private Tlv uin;
	private Tlv bosServerAddress;
	private Tlv cookie;
	private Tlv errorCode;
	private Tlv url;
	private boolean readyToConnect = false;

	/**
	 * Constructor of the representation of the Authorization Response packet received from mirabilis.
	 *
	 * @param packet The byte array received with the socket.
	 */
	public AuthorizationReply(byte[] packet) {
		super(packet, false);

		Tlv tempTlv = null;
		for (int position = 0; position < getDataFieldByteArray().length; position += tempTlv.getByteArray().length) {
			tempTlv = new Tlv(getDataFieldByteArray(), position);

			switch(tempTlv.getType()) {
				/* retreiving Uin Field */
				case 1:
					uin = tempTlv;
					break;
				/* The URL */
				case 4:
					url = tempTlv;
					break;
				/* BOS address */
				case 5:
					bosServerAddress = tempTlv;
					break;
				/* The Cookie */
				case 6:
					cookie = tempTlv;
					readyToConnect = true;
					break;
				/* Error code */
				case 8:
					errorCode = tempTlv;
					break;
			}
		}
	}

	public void execute(OscarConnection connection) throws LoginException {
		try {
			if (readyToConnect) {
				connectToBos(connection);
			} else {
				throw new LoginException(getErrorType());
			}
		} catch (IOException IOE) {
			throw new LoginException(IOE.getMessage(), IOE);
		}
	}

	public void connectToBos(OscarConnection connection) throws IOException {
		/* Stop the existing client */
		connection.getClient().disconnect();
		/* Create a new client bounded to the BOS */
		OscarClient bosClient = new OscarClient(getBosServerAddress(),
				getBosServerPort(),
				connection.getPacketAnalyser(), connection);
		/* Set the new server */
		connection.setClient(bosClient);
		/* set the cookie for a further usage */
		connection.setCookie(cookie);
		/* start the client */
		connection.getClient().connect();
	}

	/**
	 * This function returns the Uin used during login.
	 *
	 * @return The Uin.
	 */
	public String getUin() {
		return uin.getStringValue();
	}

	/**
	 * This function returns the IP address of the BOS server.
	 *
	 * @return BOS's IP address.
	 */
	public String getBosServerAddress() {
		int dbpt = bosServerAddress.getStringValue().indexOf(':');
		return bosServerAddress.getStringValue().substring(0, dbpt);
	}

	/**
	 * This function returns the port of the BOS server.
	 *
	 * @return BOS's port number.
	 */
	public int getBosServerPort() {
		int dbpt = bosServerAddress.getStringValue().indexOf(':');
		return Integer.parseInt(bosServerAddress.getStringValue().substring(dbpt +
				1, bosServerAddress.getStringValue().length()));
	}

	/**
	 * This function returns the cookie sent by the BOS server.
	 *
	 * @return User's cookie.
	 */
	public Tlv getCookie() {
		return cookie;
	}

	/**
	 * This function returns the error code sent by the server.
	 *
	 * @return The error-Code.
	 */
	public int getErrorcode() {
		return errorCode.getValue();
	}

	/**
	 * This function returns the Url where the user can find help
	 * in case of troubble.
	 *
	 * @return The Url where to find help.
	 */
	public String getUrl() {
		return url.getStringValue();
	}

	private LoginErrorTypeEnum getErrorType() {
		if (errorCode != null) {
			switch (errorCode.getValue()) {
				case 0x01:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.BAD_UIN_ERROR);

				case 0x04:
				case 0x05:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.PASSWORD_ERROR);

				case 0x07:
				case 0x08:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.NOT_EXISTS_ERROR);

				case 0x16:
				case 0x17:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.MAXIMUM_USERS_IP_ERROR);

				case 0x18:
				case 0x1D:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.LIMIT_EXCEEDED_ERROR);

				case 0x1B:
				case 0x1C:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.OLDER_ICQ_VERSION_ERROR);

				case 0x1E:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.CANT_REGISTER_ERROR);

				default:
					return new LoginErrorTypeEnum(LoginErrorTypeEnum.UNKNOWN_ERROR);
			}
		}
		else {
			return new LoginErrorTypeEnum(LoginErrorTypeEnum.UNKNOWN_ERROR);
		}
	}
}
