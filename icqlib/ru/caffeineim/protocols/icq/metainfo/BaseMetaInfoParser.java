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

import ru.caffeineim.protocols.icq.core.OscarConnection;

/**
 * <p>Created by 23.03.2008
 *   @author Samolisov Pavel
 */
public abstract class BaseMetaInfoParser implements IMetaInfoParser {


    public void notifyEvent(OscarConnection connection) {
        EventObject e = getNewEvent();

        for (int i = 0; i < getListenersList(connection).size(); i++) {
            EventListener l = (EventListener) getListenersList(connection).get(i);
            sendMessage(l, e);
        }
    }


    public void execute(OscarConnection connection) {
    }

    /**
     * Создаем новый объект-событие заданного типа
     * @return
     */
    protected abstract EventObject getNewEvent();

    /**
     * Уведомляем слушателя о сообщении, вызывая нужный метод
     *
     * @param listener
     * @param e
     */
    protected abstract void sendMessage(EventListener listener, EventObject e);

    protected List getListenersList(OscarConnection connection) {
        return connection.getMetaInfoListeners();
    }
}
