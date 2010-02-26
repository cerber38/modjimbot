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

package ru.jimbot.modules.anek;

import java.sql.Types;

import ru.jimbot.db.DBObject;
/**
 *
 * @author Prolubnikov Dmitry
 */
public class Aneks extends DBObject {
    public int id=0;
    public String text = "";
    
    /** Creates a new instance of Aneks */
    public Aneks() {
        init();
    }
    
    public Aneks(int i, String s) {
       init();
       id=i;
       text=s;
    }
    
    private void init(){
        fields = new String[] {"id","text"};
        types = new int[] {Types.INTEGER, Types.LONGVARCHAR};
        tableName="aneks";        
    }
    
    public String[] getFields(){
        return fields;
    }
    
    public int[] getTypes(){
        return types;
    }
    
    public String getTableName(){
        return this.tableName;
    }
}
