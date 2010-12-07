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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.core.queue.OscarQueue;
import ru.caffeineim.protocols.icq.core.queue.Queue;
import ru.caffeineim.protocols.icq.tool.Dumper;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel
 *   @author Prolubnikov Dmitry
 */
public class OscarClient implements Runnable {

	private static Log log = LogFactory.getLog(OscarClient.class);

    public static final String THREAD_NAME = "OscarClientThread";

    private OscarPacketAnalyser analyser;
    private OscarPacketHandler handler;
    private String host;
    private int port;
    private Socket socketClient;
    private InputStream in;
    private OutputStream out;
    private Thread runner;
    private boolean running = true;
    private Queue messagesQueue = null;
    private SocketFactory factory;
    private OscarConnection connection;
    private int timeCounter = 0;       //by Пушкожук
    private int timeThreshold;

    /**
     * This create a socket that will be connected to an Oscar Server.
     *
     * @param host The address of the server. <i>[i.e: <b>login.icq.com</b> for ICQ]</i>
     * @param port The port on which the server is awaiting connection.
     * @param analyser The instance of the analyser that should be called
     * to parse the incoming packets.
     */
    public OscarClient(String host, int port, OscarPacketAnalyser analyser, OscarConnection connection) {
        this(host, port, analyser, null, connection);
    }

    /**
     * This create a socket that will be connected to an Oscar Server.
     *
     * @param host The address of the server. <i>[i.e: <b>login.icq.com</b> for ICQ]</i>
     * @param port The port on which the server is awaiting connection.
     * @param analyser The instance of the analyser that should be called
     * to parse the incoming packets.
     * @param factory The instance of socket factory that make socket for
     * connection to ICQ server
     */
    public OscarClient(String host, int port, OscarPacketAnalyser analyser, SocketFactory factory, OscarConnection connection) {
        this.analyser = analyser;
        this.host = host;
        this.port = port;
        this.factory = factory;
        runner = new Thread(this, THREAD_NAME);
        messagesQueue = new OscarQueue();
        this.connection=connection;
        try {
            timeThreshold=Integer.parseInt(OscarConfiguration.get(OscarConfiguration.PING_TIMEOUT))/200;
        } catch (Exception ex) {
            timeThreshold=150;
        }
    }

    /**
     * Make socket for connection to ICQ server and connect to it:<ul>
     * 	<li> if <code>factory</code> is null then make socket with <code>new Socket(host, port)</code>
     *  <li> if <code>factory</code> is not null then make socket with {@link SocketFactory#createSocket(String, int)}
     *  </ul>
     */
    public synchronized void connect() throws IOException {
    	// make the socket
    	if (factory == null) {
    		socketClient = new Socket(host, port);
    	} else {
    		socketClient = factory.createSocket(host, port);
    	}

    	out = new DataOutputStream(socketClient.getOutputStream());
    	in = socketClient.getInputStream();

    	// starting our thread
        runner.start();

        // starting packet handler thread
        handler = new OscarPacketHandler(this);
    }

    /**
     * Stoping the client
     * @throws IOException
     */
    public synchronized void disconnect() throws IOException {
        try {
        	if (socketClient != null)
        		socketClient.shutdownInput();
        }
        finally {
        	try {
        		if (socketClient != null)
        			socketClient.shutdownOutput();
        	}
        	finally {
        		try {
        			socketClient.close();
        		}
        		finally {
        			running = false;
        			handler.stop();
        		}
        	}
        }
    }

    /**
     * This function is ran by the Thread.
     * It keeps reading the socket until 6 bytes at least are present.
     * Those 6 bytes represents the <code>packet header</code>, from which we
     * can determine the lenght of the packet.
     * Then, the thread wait for <code>packetLen</code> bytes to be present on
     * the socket, this is the <code>packet body</code>.
     * Finaly, the <code>packet header</code> and the <code>packet body</code> are merged and sent to
     * the packet analyser.
     */
    public void run() {
        byte header[] = new byte[6];
        byte packet[] = null;
        int packetLen = 0;
        boolean waitData = false;

        try {
            while (running) {
            	if (!waitData) {
                    // waiting for at least 6 bytes
                    if (in.available() >= 6) {
                        in.read(header, 0, 6);
                        // retreiving flap datafield len
                        packetLen = getPacketLen(header);
                        packet = new byte[packetLen + 6];
                        waitData = true;
                        timeCounter=0;   //Соединение активно
                    }
                } else {
                    // waiting for the entiere datafield
                    if (in.available() >= packetLen) {
                        in.read(packet, 6, packetLen);
                        // adding the header to the packet
                        System.arraycopy(header, 0, packet, 0, 6);
                        getMessageQueue().push(packet);
                        waitData = false;
                        timeCounter=0;   //Соединение активно
                    }
                }

            	if (in.available() == 0) {
            		Thread.sleep(200);
                    timeCounter++;             //by Пушкожук
                    if (timeCounter>=timeThreshold) connection.pingNeeded();
                    //timeCounter обнулится в sendPacket
                }
            }
        }
        catch (IOException ex) {
        	log.error(ex.getMessage(), ex);
        }
        catch (InterruptedException ex) {
        	log.error(ex.getMessage(), ex);
        }
    }

    /**
     * Return the Inet address of the socket that is connected to
     * the Oscar Server.
     * @return The Inet address of the the socket.
     */
    public byte[] getInetaddress() {
        return socketClient.getLocalAddress().getAddress();
    }

    public Queue getMessageQueue() {
        return messagesQueue;
    }

    public OscarPacketAnalyser getAnalyser() {
        return analyser;
    }

    /**
     * @param header
     * @return
     */
    private static int getPacketLen(byte[] header) {
        int result;
        result = (header[4] << 8) & 0xFF00;
        result |= header[5] & 0xFF;
        return result;
    }

    /**
     * This function gives you the possibility to send
     * packets throught the socket.
     *
     * @param packet The byte array representation of the packet to be sent.
     */
    public void sendPacket(byte[] packet) throws IOException {
        // tracing sended packets. Need set trace level
        // for ru.caffeineim.protocols.icq.tool.Dumper
    	Dumper.log(packet, true, 8, 16);
        timeCounter=0;
        out.write(packet);
        out.flush();
    }
}
