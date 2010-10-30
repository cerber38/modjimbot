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

import ru.jimbot.modules.chat.Users;
import ru.jimbot.protocol.IcqProtocol;

/**
 * Элемент очереди сообщений
 * @author Prolubnikov Dmitry
 */
public class MsgQueueElement {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_STATUS = 1;
    public static final int TYPE_INFO = 2;
    public static final int TYPE_FLOOD_NOTICE = 3;
    
    public String sn = "";
    public String to_sn = "";
    public String msg = "";
    public Users u;
    public int info_type = 0;
    public long time = 0;
    public int type = 0;
    public IcqProtocol proc;
    
    /** Creates a new instance of MsgQueueElement */
    public MsgQueueElement(String _sn, String _msg) {
        sn = _sn;
        msg = _msg;
        time = System.currentTimeMillis();
    }
    
    public MsgQueueElement(Users u, int i){
        this.u = u;
        this.info_type = i;
        time = System.currentTimeMillis();
        type = TYPE_INFO;
    }
    
    public MsgQueueElement(String _sn, String fsn, String _msg) {
        sn = _sn;
        to_sn = fsn;
        msg = _msg;
        time = System.currentTimeMillis();
    }

    public MsgQueueElement(String _sn, String _msg, IcqProtocol pr) {
        sn = _sn;
        msg = _msg;
        time = System.currentTimeMillis();
        proc = pr;
    }
}
