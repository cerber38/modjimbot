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

package ru.jimbot.protocol;

import ru.jimbot.modules.chat.Users;

/**
 * ѕередача и прием сообщений, событи€ смены статуса
 *
 * @author Prolubnikov Dmitry
 */

public interface ProtocolListener {
    public abstract void getMsg(String sendSN, String recivSN, String msg, boolean isoff);
    public abstract void getStatus(String sn, int status);    
    public abstract void getInfo(Users u, int type);
}
