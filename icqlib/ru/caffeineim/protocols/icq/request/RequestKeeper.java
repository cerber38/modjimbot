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
package ru.caffeineim.protocols.icq.request;

import java.util.ArrayList;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.request.event.RequestListener;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class RequestKeeper extends java.util.TreeMap {

    private static final long serialVersionUID = -5562290489832818366L;

    private ArrayList freeRequestId;

    private int requestId = 0;

    /**
     * Construct an object that keeps track of all monitored packets.
     */
    public RequestKeeper() {
        super();
        freeRequestId = new ArrayList();
    }

    /**
     * This add a request in the monitoring system.
     *
     * @param packet The request packet.
     * @param listener The listener to warn when the answer is received.
     */
    public void addRequest(Flap packet, RequestListener listener) {
        if (packet.hasSnac()) {
            /* Add the new listener to the existing request */
            if (containsRequest(packet.getSnac().getRequestId())) {
                Request request = (Request)this.getRequest(packet.getSnac().getRequestId());
                request.addListener(listener);
            }
            /* Simply create a new request */
            else {
                Request request = new Request(packet, listener);
                this.put(new Integer(packet.getSnac().getRequestId()), request);
            }
        }
        else {
            // TODO Maybe add some not blocking exception  ex : RequestOnNonSnacedFlapException
        }
    }

    public void addRequest(Request request) {
        /* If there is an existing request for the same requestId
         * then we simply add the new requestListeners to the existing
         * request object.
         */
        if (containsRequest(request.getRequestId())) {
            Request existingRequest = (Request)this.getRequest(request.getRequestId());
            for (int i = 0; i < request.getNbListeners(); i++) {
                if (!existingRequest.containsListener(request.getRequestListener(i)))
                    existingRequest.addListener(request.getRequestListener(i));
            }
        }
        /* simply create a new one. */
        else {
            this.put(new Integer(request.getRequestId()), request);
        }
    }

    /**
     * This remove a listener from the list of listener that monitor a particular
     * request packet.<br/>
     * If there is no more listener monitoring the request, then the request is
     * deleted.
     * <p>
     * This is equal to : <code>removeRequestListener(packet.getSnac().getRequestId(), listener);</code>
     * </p>
     *
     * @param packet The packet that is monitored.
     * @param listener The listener that monitor the packet.
     */
    public void removeRequestListener(Flap packet, RequestListener listener) {
        if (packet.hasSnac())
            removeRequestListener(packet.getSnac().getRequestId(), listener);
        else {
            // TODO Maybe add some not blocking exception  ex : RequestOnNonSnacedFlapException
        }
    }

    /**
     * This remove a listener from the list of listener that monitor a particular
     * request packet.<br/>
     * If there is no more listener monitoring the request, then the request is
     * deleted.
     *
     * @param requestId The requestId that is monitored.
     * @param listener The listener that monitor the requestId.
     */
    public void removeRequestListener(int requestId, RequestListener listener) {
        if (containsKey(new Integer(requestId))) {
            Request request = (Request)this.get(new Integer(requestId));
            request.removeListener(listener);
            freeRequestId.add(new Integer(requestId));
            if (request.getNbListeners() == 0)
                this.remove(new Integer(requestId));
        }
        else {
            // TODO Maybe add some not blocking exception ex : UndefinedRequestException
        }
    }

    /**
     * Determines if a particular requestId is already monitored.
     *
     * @param requestId The request Id that is monitored.
     *
     * @return True if requestId is already monitored, False otherwise.
     */
    public boolean containsRequest(int requestId) {
        return containsKey(new Integer(requestId));
    }

    /**
     * Determines if a particular requestId of a packet is already monitored.
     * <p>
     * This is equal to : <code>containsRequest(packet.getSnac().getRequestId());</code>
     * </p>
     *
     * @param packet The packet containing the request Id that is monitored.
     *
     * @return True if the packet is already monitored, False otherwise.
     */
    public boolean containsRequest(Flap packet) {
        if (packet.hasSnac())
            return containsRequest(packet.getSnac().getRequestId());
        else
            return false;
    }

    /**
     * Return the request to which this requestId is mapped.<br/>
     * Returns null if there is no mapping for this request Id.
     *
     * @param requestId The request id we're searching for.
     *
     * @return The request mapped to this requestId.
     */
    public Request getRequest(int requestId) {
        return (Request) get(new Integer(requestId));
    }

    /**
     * Returns the request to which the requestId field of the snac packet is mapped.<br/>
     * Returns null if there is no mapping for this request Id.
     *
     * @param packet The received packet.
     *
     * @return The request mapped to this packet's requestId.
     */
    public Request getRequest(Flap packet) {
        return getRequest(packet.getSnac().getRequestId());
    }

    /**
     * Returns the first available requestId.
     *
     * @return The first available requestId.
     */
    public int nextAvailableRequestId() {
        int rId;

        if (freeRequestId.size() > 0) {
            rId = ( (Integer) freeRequestId.get(0)).intValue();
            freeRequestId.remove(0);
        }
        else
            rId = ++requestId;
        return rId;
    }
}
