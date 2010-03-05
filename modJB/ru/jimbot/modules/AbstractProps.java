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

import java.util.Properties;

import ru.jimbot.table.UserPreference;

/**
 * 
 * @author Prolubnikov Dmitry
 *
 */
public interface AbstractProps {
	public void setDefault();
	public UserPreference[] getUserPreference();
	public UserPreference[] getUINPreference();
	public Properties getProps();
	public void load();
	public void save();
	public void registerProperties(Properties _appProps);
	public String getProperty(String key);
	public String getStringProperty(String key);
	public void setStringProperty(String key, String val);
	public void setIntProperty(String key, int val);
	public void setBooleanProperty(String key, boolean val);
	public int getIntProperty(String key);
	public boolean getBooleanProperty(String key);
	public int uinCount();
	public String getUin(int i);
	public String getPass(int i);
	public int addUin(String uin, String pass);
	public void delUin(int c);
	public void setUin(int i, String uin, String pass);
	public boolean isAutoStart();
	public String[] getAdmins();
    public UserPreference[] OtherUserPreference();
    public void AddXmlConfig(String ServiceName);
}
