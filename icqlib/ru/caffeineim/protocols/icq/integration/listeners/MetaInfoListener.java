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
import ru.caffeineim.protocols.icq.integration.events.IncomingUserEvent;

import ru.caffeineim.protocols.icq.integration.events.MetaAffilationsUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaBasicUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaEmailUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaInterestsUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaMoreUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaNoteUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaShortUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.MetaWorkUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.events.UINRegistrationFailedEvent;
import ru.caffeineim.protocols.icq.integration.events.UINRegistrationSuccessEvent;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel
 *   @author Egor Baranov 
 */
public interface MetaInfoListener extends EventListener {	
	
	public void onShortUserInfo(MetaShortUserInfoEvent e);
	
	public void onBasicUserInfo(MetaBasicUserInfoEvent e);
	
	public void onEmailUserInfo(MetaEmailUserInfoEvent e);
	
	public void onWorkUserInfo(MetaWorkUserInfoEvent e);

	public void onInterestsUserInfo(MetaInterestsUserInfoEvent e);
	
	public void onMoreUserInfo(MetaMoreUserInfoEvent e);
	
	public void onNotesUserInfo(MetaNoteUserInfoEvent e);
	
	public void onAffilationsUserInfo(MetaAffilationsUserInfoEvent e);		
	
	public void onRegisterNewUINSuccess(UINRegistrationSuccessEvent e);
	
	public void onRegisterNewUINFailed(UINRegistrationFailedEvent e);

        public void IncomingUserEvent(IncomingUserEvent e);
}