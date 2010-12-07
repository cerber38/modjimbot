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

import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;

/**
 * <p>Created by 23.03.2008
 *   @author Samolisov Pavel
 */
public interface IMetaInfoParser {
	
	/**
	 * Осуществляет разбор сообщения и заполнение полей метаинформации
	 * 
	 * @param data
	 * @param position
	 */
	public void parse(byte[] data, int position) throws ConvertStringException;
	
	/**
	 * Выполняем некоторое действие, предусмотренное логикой ответа на сообщение
	 * 
	 * @param connection
	 */
	public void execute(OscarConnection connection);
	
	/**
	 * Создаем событие и уведомляем о нем слушателей
	 * 
	 * @param connection
	 */
	public void notifyEvent(OscarConnection connection);
}
