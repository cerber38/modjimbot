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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.setting.enumerations.MetaSubTypeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MetaTypeEnum;

/**
 * <p>Created by 24.03.2008
 *   @author Samolisov Pavel
 */
public class MetaInfoParserFactory {

	private static Log log = LogFactory.getLog(MetaInfoParserFactory.class);

	/**
	 * Строим конкретную реализацию интерфейса IMetaInfoParser для обработки
	 * того или иного типа метаинформации
	 *
	 * @param metaType
	 * @param metaSubType
	 * @return
	 */
	public static IMetaInfoParser buildMetaInfoParser(int metaType, int metaSubType) {
		IMetaInfoParser parser = null;

		switch (metaType) {
			case MetaTypeEnum.SERVER_OFFLINE_MESSAGE:
				parser = new OfflineMessageParser();
				break;

			case MetaTypeEnum.SERVER_END_OF_OFFLINE_MESSAGES:
				parser = new ServerEndOfOflineMessageParser();
				break;

			case MetaTypeEnum.SERVER_ADVANCED_META:
				switch (metaSubType) {
					case MetaSubTypeEnum.SERVER_USER_FOUND:
					case MetaSubTypeEnum.SERVER_LAST_USER_FOUND:
						// TODO сюда засунем поиск
						break;

					case MetaSubTypeEnum.SERVER_SHORT_USER_INFO_REPLY:
						parser = new ShortUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_BASIC_USER_INFO_REPLY:
						parser = new BasicUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_EXTENDED_EMAIL_USER_INFO_REPLY:
						parser = new EmailUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_WORK_USER_INFO_REPLY:
						parser = new WorkUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_INTERESTS_USER_INFO_REPLY:
						parser = new InterestsUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_MORE_USER_INFO_REPLY:
						parser = new MoreUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_ABOUT_USER_INFO_REPLY:
						parser = new NotesUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_AFFILATIONS_USER_INFO_REPLY:
						parser = new AffilationsUserInfoParser();
						break;

					case MetaSubTypeEnum.SERVER_SET_USER_PASSWORD:
						parser = new MetaAckParser();
						break;
				}
				break;
		}

		log.debug("parser " + parser.getClass().getName() + " has been built");
		return parser;
	}
}
