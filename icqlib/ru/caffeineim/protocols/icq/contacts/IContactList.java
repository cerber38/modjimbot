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
package ru.caffeineim.protocols.icq.contacts;

import ru.caffeineim.protocols.icq.exceptions.ContactListOperationException;

/**
 * <p>Created by 21.01.2010
 *   @author Samolisov Pavel
 */
public interface IContactList {

	/**
	 * Добавляем новый контакт с UIN'ом <code>userId</code> в
	 * группу с названием <code>groupName</code>
	 *
	 * @param userId идентификатор добавляемого контакта
	 * @param groupName название группы, в которую добавляется контакт
	 * @throws ContactListOperationException
	 */
	public void addContact(String userId, String groupName)
			throws ContactListOperationException;

	/**
	 * Добавляем новый контакт с UIN'ом <code>userId</code> в
	 * группу <code>group</code>
	 *
	 * @param userId идентификатор добавляемого контакта
	 * @param group группа, в которую добавляется контакт
	 * @throws ContactListOperationException
	 */
	public void addContact(String userId, Group group)
			throws ContactListOperationException;

	/**
	 * Добавляем новый контакт <code>contact</code> в группу <code>group</code>
	 *
	 * @param contact добавляемый контакт
	 * @param group группа, в которую добавляется контакт
	 * @throws ContactListOperationException
	 */
	public void addContact(Contact contact, Group group)
			throws ContactListOperationException;

	/**
	 * Удаляем контакт с UIN'ом <code>userId</code> из контакт-листа
	 *
	 * @param userId идентификатор удаляемого контакта
	 * @throws ContactListOperationException
	 */
	public void removeContact(String userId) throws ContactListOperationException;

	/**
	 * Удаляем контакт <code>contact</code> из контакт-листа
	 *
	 * @param contact удаляемый контакт
	 * @throws ContactListOperationException
	 */
	public void removeContact(Contact contact) throws ContactListOperationException;

	/**
	 * Добавляем группу с именем <code>group</code> в контакт-лист
	 *
	 * @param group имя добавляемой группы
	 * @throws ContactListOperationException
	 */
	public void addGroup(String group) throws ContactListOperationException;

	/**
	 * Добавляем группу <code>group</code> в контакт-лист
	 *
	 * @param group добавляемая группа
	 * @throws ContactListOperationException
	 */
	public void addGroup(Group group) throws ContactListOperationException;

	/**
	 * Удаляем группу <code>group</code> из контакт-листа
	 *
	 * @param group удаляемая группа
	 * @throws ContactListOperationException
	 */
	public void removeGroup(Group group) throws ContactListOperationException;

	/**
	 * Удаляем себя из контакт-листа пользователя с UIN <code>userId</code>
	 *
	 * @param userId уин пользователя, из листа которого мы себя удаляем
	 */
	public void removeYourself(String userId);

	/**
	 * Отправляем контакту с UIN <code>userId</code> запрос авторизации <code>request</code>
	 *
	 * @param userId UIN контакт, которому отправляем запрос авторизации
	 * @param request текст запроса авторизации
	 * @throws ContactListOperationException
	 */
	public void sendAuthRequestMessage(String userId, String request)
			throws ContactListOperationException;

	/**
	 * Отправляем ответ <code>auth</code> с текстом <code>reply</code> на запрос авторизации от пользователя
	 * с UIN <code>userId</code>
	 *
	 * @param userId UIN пользователя, который запросил авторизацию
	 * @param reply текст ответа
	 * @param auth результат авторизации, если <code>true</code>, то пользователь считается авторизованым
	 * @throws ContactListOperationException
	 */
	public void sendAuthReplyMessage(String userId, String reply, boolean auth)
			throws ContactListOperationException;

	/**
	 * Отправляем пользователю с UIN <code>userId</code> сообщение <i>"You were added"</i>
	 *
	 * @param userId получатель сообщения о добавлении
	 */
	public void sendYouWereAdded(String userId);

	/**
	 * Возвращаем корневую группу
	 * @return корневая группа
	 */
	public Group getRootGroup();
}
