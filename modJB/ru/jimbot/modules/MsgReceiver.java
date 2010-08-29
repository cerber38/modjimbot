/**
 * JimBot - Java IM Bot
 * Copyright (C) 2006-2009 JimBot project
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.jimbot.modules;

import ru.caffeineim.protocols.icq.integration.OscarInterface;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.protocol.ProtocolListener;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;


/**
 * Слушатель входящих сообщений. Помещает их в очередь
 * @author Prolubnikov Dmitry
 */
public class MsgReceiver implements ProtocolListener {
    MsgInQueue iq;
    IcqProtocol iprot;
    
    public MsgReceiver( MsgInQueue q, IcqProtocol ip ){
    iq = q;
    iprot = ip;
    ip.addListener( this );
    }
    public void getMsg( String sendSN, String recivSN, String msg, boolean isOffline ){
        String[] mmsg = msg.split(" ");
        if( mmsg[ 0 ].equalsIgnoreCase( "!about" ) || mmsg[ 0 ].equalsIgnoreCase( "!оботе" ) ){
        if( !iq.testFlood( sendSN ) ){
        iprot.sendMsg( sendSN, MainProps.VERSION +
        "\nВерсия icq библиотеки: " + OscarInterface.getVersion() +
        "\nПоддержка проекта: http://jimbot.ru" +
        "\nПоддержка мода - www.toch72.ru");
        Log.getLogger(iprot.serviceName).info( "CHAT COM_LOG: " + sendSN + ">> " + mmsg[ 0 ] );
        }else{
        Log.getLogger(iprot.serviceName).flood("FLOOD from " + sendSN + ">> " + msg);
        }
        return;
        }
    iq.addMsg( iprot, sendSN, msg, isOffline );
    }


    public void getStatus(String sn, int status){
    iq.addStatus(iprot,sn,String.valueOf(status));
    }    
    
}
