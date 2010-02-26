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

package ru.jimbot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс управления локализованными текстовыми ресурсами
 * 
 * @author Prolubnikov Dmitriy
 *
 */
public class Messages {
private String Messages_FOLDER = "/msg/";
private String fileName = "";
private String ServiceName = null;
private static ConcurrentHashMap<String,Messages> instances = new ConcurrentHashMap<String,Messages>();
private static Messages mainInst = new Messages("");

    public Messages(String name)
    {
        ServiceName = name;
    }

     /**
     * Возвращает экземпляр Messages для нужного сервиса
     * @param name
     * @return
     */
    public static Messages getInstance(String name){
        if(name == null) return null;
        if(name.equals("")) return mainInst;
        if(!instances.containsKey(name)) instances.put(name, new Messages(name));
        return instances.get(name);
    }

    public  ResourceBundle getNewBundle(){
    	ResourceBundle bundle = null;
    	InputStream stream = null;
    	try {
            fileName = "./services/" + ServiceName + Messages_FOLDER + "messages.xml";
            stream =  new BufferedInputStream(new FileInputStream(new File(fileName)));
        if (stream != null) {
            BufferedInputStream bis = new BufferedInputStream(stream);
            bundle = new XMLResourceBundle(bis);
            bis.close();
        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return bundle;
    }



    public synchronized String getString(String key) {
        try {
            return getNewBundle().getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    public synchronized String getString(String key, Object[] arg) {
        try {
            return java.text.MessageFormat.format(getNewBundle().getString(key), arg);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
    public class XMLResourceBundle extends ResourceBundle {
        private Properties props;
        XMLResourceBundle(InputStream stream) throws IOException {
            props = new Properties();
            props.loadFromXML(stream);
        }
        protected Object handleGetObject(String key) {
            return props.getProperty(key);
        }
        /* (non-Javadoc)
         * @see java.util.ResourceBundle#getKeys()
         */
        @Override
        public Enumeration<String> getKeys() {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
