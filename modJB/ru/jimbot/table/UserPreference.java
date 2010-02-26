/*
 * UserPreference.java
 *
 * Copyright (C) 2002, 2003, 2004, 2005, 2006 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */


package ru.jimbot.table;

//import java.awt.Color;
//import org.executequery.Constants;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1.1 $
 * @date     $Date: 2006/12/15 17:34:59 $
 */
public class UserPreference {

    public static final int STRING_TYPE = 0;
    public static final int BOOLEAN_TYPE = 1;
//    public static final int COLOUR_TYPE = 2;
    public static final int INTEGER_TYPE = 3;
    public static final int CATEGORY_TYPE = 4;
    public static final int FILE_TYPE = 5;
    public static final int PASS_TYPE = 6;

    private boolean collapsed;
    private boolean saveActual;
    private String savedValue;

    private int type;
    private int maxLength;
    private String key;
    private Object value;
    private String displayedKey;
    private String displayedKey2;
    private String[] availableValues;

    public UserPreference() {}





    public UserPreference(int type, int maxLength, String key,
                          String displayedKey, Object value, String displayedKey2) {
        this(type, maxLength, key, displayedKey, value, null, displayedKey2);
    }

    public UserPreference(int type, String key, String displayedKey, Object value, String displayedKey2) {
        this(type, -1, key, displayedKey, value, null, displayedKey2);
    }



    public UserPreference(int type, String key, String displayedKey,
                          Object value, String[] availableValues, String displayedKey2) {
        this(type, -1, key, displayedKey, value, availableValues, displayedKey2);
    }

    public UserPreference(int type, int maxLength, String key, String displayedKey,
                          Object value, String[] availableValues, String displayedKey2) {
        this.type = type;
        this.key = key;
        this.maxLength = maxLength;
        
        if (type == STRING_TYPE) {
            savedValue = value.toString();
            this.value = value.toString();
            if (availableValues != null && availableValues.length > 0) {
                try {
                    int index = Integer.parseInt(savedValue);
                    this.value = availableValues[index];
                } 
                catch (NumberFormatException e) {
                    saveActual = true;
                    // try the value
                    for (int i = 0; i < availableValues.length; i++) {
                        
                        if (availableValues[i].equals(value)) {
                            this.value = availableValues[i];
                            break;
                        }
                        
                    }
                    
                }
            } 
            
        } else {
            this.value = value;
        }

        this.displayedKey = displayedKey;
        this.displayedKey2 = displayedKey2;
        this.availableValues = availableValues;        
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        if(type==PASS_TYPE) return "";
        return value;
    }

    public void reset(Object value) {
        if (type == STRING_TYPE) {

            if (availableValues != null && availableValues.length > 0) {
                
                if (saveActual) {
                    this.value = savedValue;
                }
                
                int index = Integer.parseInt(savedValue);
                this.value = availableValues[index];
            }

        } else {
            this.value = value;
        }
    }

    public void setValue(Object value) {
        if(type==PASS_TYPE){
            if(!value.toString().equals("")) this.value = value;
        } else 
            this.value = value;
    }

    public String getSaveValue() {
        switch (type) {
            case STRING_TYPE:
                
                if (availableValues != null) {

                    if (saveActual && value != null) {
                        return value.toString();
                    }
                    
                    for (int i = 0; i < availableValues.length; i++) {
                        if (value == availableValues[i]) {
                            return Integer.toString(i);
                        }
                    }

                }
                
                if (value == null) {
                    return "";
                }
                return value.toString();
            case INTEGER_TYPE:
                return value.toString();
            case BOOLEAN_TYPE:
                return value.toString();
//            case COLOUR_TYPE:
//                return Integer.toString(((Color)value).getRGB());
            case PASS_TYPE:
                return "";
            default:
                return value.toString();
        }
    }
    
    public String getDisplayedKey() {
        return displayedKey;
    }

    public void setDisplayedKey(String displayedKey) {
        this.displayedKey = displayedKey;
    }

    public String getDisplayedKey2() {
    return displayedKey2;
    }

    public void setDisplayedKey2(String displayedKey2) {
        this.displayedKey2 = displayedKey2;
    }


    public String[] getAvailableValues() {
        return availableValues;
    }

    public void setAvailableValues(String[] availableValues) {
        this.availableValues = availableValues;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

}



