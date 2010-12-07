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
package ru.caffeineim.protocols.icq.integration.listeners;

import java.util.EventListener;

import ru.caffeineim.protocols.icq.integration.events.IncomingMessageEvent;
import ru.caffeineim.protocols.icq.integration.events.IncomingUrlEvent;
import ru.caffeineim.protocols.icq.integration.events.MessageAckEvent;
import ru.caffeineim.protocols.icq.integration.events.MessageErrorEvent;
import ru.caffeineim.protocols.icq.integration.events.MessageMissedEvent;
import ru.caffeineim.protocols.icq.integration.events.OfflineMessageEvent;

/**
 * <p>Created by
 *   @author Fabrice Michellonet 
 */
public interface MessagingListener extends EventListener {

	public void onIncomingMessage(IncomingMessageEvent e);

	public void onIncomingUrl(IncomingUrlEvent e);

	public void onOfflineMessage(OfflineMessageEvent e);

	public void onMessageAck(final MessageAckEvent e);

	public void onMessageError(MessageErrorEvent e);
	
	public void onMessageMissed(MessageMissedEvent e);
}