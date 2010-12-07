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
package ru.caffeineim.protocols.icq.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.core.exceptions.LoginException;
import ru.caffeineim.protocols.icq.packet.received.AuthorizationReply;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPackedRegistry;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.packet.sent.generic.AuthorizationRequest;
import ru.caffeineim.protocols.icq.packet.sent.generic.SignonCommand;
import ru.caffeineim.protocols.icq.tool.Dumper;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel
 */
public class OscarPacketAnalyser {

	private static Log log = LogFactory.getLog(OscarPacketAnalyser.class);

	private OscarConnection connection;
    private ReceivedPackedClassLoader receivedClassLoader;
    private int nbPacket = 0;

    public OscarPacketAnalyser(OscarConnection connection) {
        this.connection = connection;
        receivedClassLoader = new ReceivedPackedClassLoader(new ReceivedPackedRegistry());
    }

    /**
     * The function is where we manage the received packets. Recognize them,
     * analyse and give an answer to the server.
     *
     * @param packet The byte array received from the ICQ server.
     * @throws LoginException
     */
    protected void handlePacket(byte[] packet) throws LoginException {
        nbPacket++;
        boolean handled;
        try {
            handled = handleInitialPackets(packet);
        } catch (LoginException E) {
            throw new LoginException(E.getErrorType(), E);
        }

        // "normal" service
        if (!handled) {
            try {
                handleService(packet);
            } catch (Exception e) {
                log.error("failed to service packet (continuing with other packets)", e);
            }
        }
    }

    private boolean handleInitialPackets(byte[] packet) throws LoginException {
        if (nbPacket == 1) {
            connection.sendFlap(new AuthorizationRequest(connection.getUserId(), connection.getPassword()));
        } else if (nbPacket == 2) {
            AuthorizationReply reply = new AuthorizationReply(packet);
            reply.execute(connection);
        } else if (nbPacket == 3) {
            /* BOS hello */
            connection.sendFlap(new SignonCommand(connection.getCookie()));
        } else {
            return false;
        }
        return true;
    }

    protected void handleService(byte[] packet) throws Exception {
        ReceivedPacket receivedFlap = new ReceivedPacket(packet, true);
        int familyId = receivedFlap.getSnac().getFamilyId();
        int subTypeId = receivedFlap.getSnac().getSubTypeId();

        if (log.isDebugEnabled())
            log.debug("Received " + familyId + " - " + subTypeId);

        // tracing received packets. Need set trace level
        // for ru.caffeineim.protocols.icq.tool.Dumper
        Dumper.log(packet, true, 8, 16);

        Class loadedClass = receivedClassLoader.loadClass(familyId, subTypeId);
        if (loadedClass != null) {
            Class[] constructorParam = new Class[] {packet.getClass()};
            Object[] param = new Object[] {packet};

            try {
                receivedFlap = (ReceivedPacket) loadedClass.getConstructor(constructorParam).newInstance(param);

                receivedFlap.execute(connection);
                receivedFlap.notifyEvent(connection);

                log.debug("--> Loaded class: " + loadedClass.getName());
                log.debug("--> Executed method: " + receivedFlap.getClass().getName() + ".execute(connection)");

                packet = null; // clean packet byte array
            }
            catch (Exception ex) {
            	log.error("Could not parse packet", ex);
            }
        }

        receivedFlap.matchRequest(connection);

        // if logged to the Server
        if (familyId == 9 && subTypeId == 3) {
            connection.setAuthorized(true);
        }
    }

    /**
     * This method gives access to the OscarConnection class.
     * @return The filtering engine.
     */
    public OscarConnection getConnection() {
        return connection;
    }
}
