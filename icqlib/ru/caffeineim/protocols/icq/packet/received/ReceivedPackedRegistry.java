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
package ru.caffeineim.protocols.icq.packet.received;

import java.util.Vector;

import ru.caffeineim.protocols.icq.core.IReceivedPacketRegistry;
import ru.caffeineim.protocols.icq.packet.received.authorization.UINRegistrationFailed__23_1;
import ru.caffeineim.protocols.icq.packet.received.authorization.UINRegistrationSuccess__23_5;
import ru.caffeineim.protocols.icq.packet.received.byddylist.BuddyListRightsReply__3_3;
import ru.caffeineim.protocols.icq.packet.received.byddylist.IncomingUser__3_11;
import ru.caffeineim.protocols.icq.packet.received.byddylist.OffgoingUser__3_12;
import ru.caffeineim.protocols.icq.packet.received.byddylist.RefusedContact__3_10;
import ru.caffeineim.protocols.icq.packet.received.generic.BOSMigration__1_18;
import ru.caffeineim.protocols.icq.packet.received.generic.Motd__1_19;
import ru.caffeineim.protocols.icq.packet.received.generic.OnlineInfoResp__1_15;
import ru.caffeineim.protocols.icq.packet.received.generic.PauseReq__1_11;
import ru.caffeineim.protocols.icq.packet.received.generic.RateReply__1_7;
import ru.caffeineim.protocols.icq.packet.received.generic.ServerFamilies__1_24;
import ru.caffeineim.protocols.icq.packet.received.generic.ServerReady__1_3;
import ru.caffeineim.protocols.icq.packet.received.icbm.ICBMParametersReply__4_5;
import ru.caffeineim.protocols.icq.packet.received.icbm.IncomingMessage__4_7;
import ru.caffeineim.protocols.icq.packet.received.icbm.MessageAck__4_12;
import ru.caffeineim.protocols.icq.packet.received.icbm.MessageAutoReply__4_11;
import ru.caffeineim.protocols.icq.packet.received.icbm.MissedMessage__4_10;
import ru.caffeineim.protocols.icq.packet.received.icbm.ServerICBMError__4_1;
import ru.caffeineim.protocols.icq.packet.received.icbm.TypingNotif__4_20;
import ru.caffeineim.protocols.icq.packet.received.location.LocationRightsReply__2_3;
import ru.caffeineim.protocols.icq.packet.received.meta.MetaError__21_1;
import ru.caffeineim.protocols.icq.packet.received.meta.ServerMetaReply__21_3;
import ru.caffeineim.protocols.icq.packet.received.privacy.BosRightReply__9_3;
import ru.caffeineim.protocols.icq.packet.received.ssi.SsiAuthReply__19_27;
import ru.caffeineim.protocols.icq.packet.received.ssi.SsiAuthRequest__19_25;
import ru.caffeineim.protocols.icq.packet.received.ssi.SsiContactListReply__19_6;
import ru.caffeineim.protocols.icq.packet.received.ssi.SsiFutureAuthGranted__19_20;
import ru.caffeineim.protocols.icq.packet.received.ssi.SsiModifyingAck__19_14;
import ru.caffeineim.protocols.icq.packet.received.usagestats.MinReportInterval__11_2;

/**
 * <p>Created by 25.01.2008
 *   @author Samolisov Pavel
 */
public class ReceivedPackedRegistry implements IReceivedPacketRegistry {
    private static Vector registry = new Vector();

    static {
        registry.add(BuddyListRightsReply__3_3.class.getName());
        registry.add(IncomingUser__3_11.class.getName());
        registry.add(OffgoingUser__3_12.class.getName());
        registry.add(RefusedContact__3_10.class.getName());
        registry.add(BOSMigration__1_18.class.getName());
        registry.add(Motd__1_19.class.getName());
        registry.add(OnlineInfoResp__1_15.class.getName());
        registry.add(PauseReq__1_11.class.getName());
        registry.add(RateReply__1_7.class.getName());
        registry.add(ServerFamilies__1_24.class.getName());
        registry.add(ServerReady__1_3.class.getName());
        registry.add(ICBMParametersReply__4_5.class.getName());
        registry.add(IncomingMessage__4_7.class.getName());
        registry.add(MessageAck__4_12.class.getName());
        registry.add(MessageAutoReply__4_11.class.getName());
        registry.add(MissedMessage__4_10.class.getName());
        registry.add(ServerICBMError__4_1.class.getName());
        registry.add(TypingNotif__4_20.class.getName());
        registry.add(LocationRightsReply__2_3.class.getName());
        registry.add(BosRightReply__9_3.class.getName());
        registry.add(SsiAuthReply__19_27.class.getName());
        registry.add(SsiAuthRequest__19_25.class.getName());
        registry.add(SsiContactListReply__19_6.class.getName());
        registry.add(SsiModifyingAck__19_14.class.getName());
        registry.add(SsiFutureAuthGranted__19_20.class.getName());
        registry.add(MinReportInterval__11_2.class.getName());

        registry.add(MetaError__21_1.class.getName());
        registry.add(ServerMetaReply__21_3.class.getName());

        registry.add(UINRegistrationSuccess__23_5.class.getName());
        registry.add(UINRegistrationFailed__23_1.class.getName());
    }

    public Vector getClassNameVector() {
        return registry;
    }
}
