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

package ru.jimbot.db;

/**
 *
 * @author Prolubnikov Dmitry
 */
public abstract class DBObject {
    protected String[] fields;
    protected int[] types;
    protected String tableName;
    
    /** Creates a new instance of DBObject */
    public DBObject() {
    }
    
    public abstract String[] getFields();
    public abstract int[] getTypes();
    public abstract String getTableName();
}
