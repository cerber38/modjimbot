package org.garret.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class JServletConfig implements ServletConfig {
    public ServletContext getServletContext()
    {
	if (context == null) { 
	    Properties servletProps = server.serverProperties;
	    try { 
		File servletPropsFile = new File(servletName + ".properties");
		if (servletPropsFile.exists()) { 
		    FileInputStream in = new FileInputStream(servletPropsFile);
		    Properties props = new Properties();
		    props.load(in);
		    in.close();
		    servletProps = props;
		}
	    } catch (IOException x) {}
	    context = new JServletContext(servletProps);
	}
	return context;
    }

    public String getInitParameter(String name)
    {
	return System.getProperty(name);
    }

    public Enumeration getInitParameterNames()
    {
	return System.getProperties().keys();
    }

    public String getServletName() 
    {
	return servletName;
    }

    JServletConfig(JHttpServer server, String name) 
    { 
	this.server = server;
	servletName = name;
    }

    String          servletName;
    JServletContext context;
    JHttpServer     server;
}


