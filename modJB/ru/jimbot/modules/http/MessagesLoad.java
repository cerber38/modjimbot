/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import ru.jimbot.util.Log;

/**
 * @author fraer72
 */

public class MessagesLoad {
    public static HashMap<String,MessagesLoad> props = new HashMap<String,MessagesLoad>();
    public String fileName = "";
    public Properties appProps;

    public MessagesLoad(){
        
    }

    public static MessagesLoad getInstance(String name){
    	if(props.containsKey(name))
    		return props.get(name);
    	else {
    		MessagesLoad p = new MessagesLoad();
                p.fileName = "./services/" + name + "/msg/messages.xml";
                p.appProps = new Properties();
                p.load();
    		props.put(name, p);
    		return p;
    	}
    }

	public Set<Object> getKeys() {
	return appProps.keySet();
	}
        
        public  void load(  ) {
        File file = new File(fileName);
        try {
        FileInputStream fi = new FileInputStream(file);
        appProps.loadFromXML(fi);
        fi.close();
        Log.getDefault().info("Load Messages ok");
        } catch (Exception ex) {
        ex.printStackTrace();
        Log.getDefault().error("Error opening Messages: ");
        }
        }

        public final void save() {
        File file = new File(fileName);
        try {
        FileOutputStream fo = new FileOutputStream(file);
        appProps.storeToXML(fo, "Messages");
        fo.close();
        Log.getDefault().info("Save Messages ok");
        } catch (Exception ex) {
        ex.printStackTrace();
        Log.getDefault().error("Error saving Messages: ");
        }
        }

        public void setStringProperty(String key, String val) {
        appProps.setProperty(key,val);
        }

        public String getStringProperty(String key) {
        return appProps.getProperty(key);
        }

}
