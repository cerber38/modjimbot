package org.garret.httpserver;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class JServletContext implements ServletContext 
{
    public ServletContext getContext(String uripath)
    {
	return this;
    }

    public int getMajorVersion()
    {
	return 2;
    }

    public int getMinorVersion()    
    {
	return 2;
    }

    public String getMimeType(String file)
    {
	return "text/html";
    }

    public URL getResource(String path) throws MalformedURLException
    {
	return null;
    }

    public InputStream getResourceAsStream(String path)
    {
	return null;
    }

    public RequestDispatcher getRequestDispatcher(String path)
    {
	return null;
    }

    public RequestDispatcher getNamedDispatcher(String name)
    {
	return null;
    }

    public Servlet getServlet(String name) throws ServletException
    {
	return null;
    }

    public Enumeration getServlets()
    {
	return new Hashtable().keys();
    }

    public Enumeration getServletNames()
    {
	return new Hashtable().keys();
    }

    public void log(String msg)
    {
	System.err.println(msg);
    }    

    public void log(Exception exception, String msg)
    {
	System.err.println(exception.toString() + msg);
    }

    public void log(String message, Throwable throwable)    
    {
	System.err.println(throwable.toString() + message);
    }

    public String getRealPath(String path)
    {
	return path;
    }

    public String getServerInfo()
    {
	return "Java Http Server";
    }

    public String getInitParameter(String name)
    {
	return props.getProperty(name);
    }

    public Enumeration getInitParameterNames()
    {
	return props.keys();
    }

    public Object getAttribute(String name)
    {
	return attrHash.get(name);
    }

    public Enumeration getAttributeNames()
    {
	return attrHash.keys();
    }

    public void setAttribute(String name, Object object)
    {
	attrHash.put(name, object);
    }

    public void removeAttribute(String name)
    {
	attrHash.remove(name);
    }

    public JServletContext(Properties props) { 
	this.props = props;
    }

    Hashtable  attrHash = new Hashtable();
    Properties props;
}
