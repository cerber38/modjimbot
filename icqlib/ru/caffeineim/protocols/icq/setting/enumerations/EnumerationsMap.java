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
package ru.caffeineim.protocols.icq.setting.enumerations;

import java.util.TreeMap;

/**
 * <p>Created by 08.01.2010
 *   @author Samolisov Pavel
 */
public class EnumerationsMap extends TreeMap {

    private static final long serialVersionUID = -3553407280131250204L;

    public void put(int key, Object value) {
        super.put(new Integer(key), value);
    }

    public boolean containsKey(int key) {
        return super.containsKey(new Integer(key));
    }

    public Object get(int key) {
        return super.get(new Integer(key));
    }
}
