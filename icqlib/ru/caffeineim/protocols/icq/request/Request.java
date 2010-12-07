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
public class Request {

    private Flap monitoredFlap;
    private ArrayList listeners;

    /**
     * Construct a request that contains the Flap we want to monitor
     * and a listener to warn when an answer to the request is received.
     *
     * @param packet The packet we're goint to monitor.
     * @param listener The listener to warn.
     */
    public Request(Flap packet, RequestListener listener) {
        this.monitoredFlap = packet;
        this.listeners = new ArrayList();
        listeners.add(listener);
    }

    /**
     * Returns true if this request contains the specified listener.
     *
     * @param listener The listener whose presence is to be tested.
     *
     * @return true if the specified listener is present; false otherwise.
     */
    public boolean containsListener(RequestListener listener) {
        return listeners.contains(listener);
    }

    /**
     * Add a RequestListener to the list if not already present.
     *
     * @param listener The RequestListener to be added.
     */
    public void addListener(RequestListener listener) {
        if (!containsListener(listener))
            listeners.add(listener);
    }

    /**
     * Remove a RequestListener from the list.
     *
     * @param listener The RequestListener to be removed.
     */
    public void removeListener(RequestListener listener) {
        if (containsListener(listener))
            listeners.remove(listener);
    }

    /**
     * Removes all listeners from the list.
     */
    public void removeAllListener() {
        listeners.clear();
    }

    /**
     * Returns the requestId of the monitored paquet.
     *
     * @return The requestId of the monitored paquet.
     */
    public int getRequestId() {
        return monitoredFlap.getSnac().getRequestId();
    }

    /**
     * Returns the monitored packet.
     *
     * @return The monitored paquet.
     */
    public Flap getMonitoredFlap() {
        return this.monitoredFlap;
    }

    /**
     * This returns how many listener(s) are monitoring this packet.
     *
     * @return The number of RequestListeners for this packet.
     */
    public int getNbListeners() {
        return listeners.size();
    }

    /**
     * Returns the RequestListener at the specified position in the list.
     *
     * @param index Index of the RequestListener to return.
     * @return The RequestListener at the specified position in the list.
     * @throws IndexOutOfBoundsException If index is out of range (index < 0 || index >= getNbListeners())
     */
    public RequestListener getRequestListener(int index) throws IndexOutOfBoundsException {
        return (RequestListener) listeners.get(index);
    }
}
