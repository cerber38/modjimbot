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
package ru.caffeineim.protocols.icq.metainfo;

import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.MetaAckEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MetaAckListener;

/**
 * <p>Created by 30.03.2008
 *   @author Samolisov Pavel
 */
public class MetaAckParser extends BaseMetaInfoParser {

	private static Log log = LogFactory.getLog(MetaAckParser.class);

    private boolean isOk;

    protected EventObject getNewEvent() {
        return new MetaAckEvent(this);
    }

    protected void sendMessage(EventListener listener, EventObject e) {
    	log.debug("notify listener " + listener.getClass().getName() + " onMetaAck()");
        ((MetaAckListener) listener).onMetaAck((MetaAckEvent) e);
    }

    public void parse(byte[] data, int position) throws ConvertStringException {
        // scip data lenght
        position += 2;

        // code ack
        int code = new RawData(data, position, RawData.BYTE_LENGHT).getValue();
        isOk = (code == 0x0A);
    }

    protected List getListenersList(OscarConnection connection) {
        return connection.getMetaAckListeners();
    }

    public boolean isOk() {
        return isOk;
    }
}
