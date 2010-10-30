package org.garret.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class JHttpSession extends Thread implements HttpSession 
{
    static final int HDR_BUF_SIZE = 4096;

    public JHttpServer getServer() 
    {
	return server;
    }
    
    public Socket getSocket() 
    {
	return socket;
    }

    public JHttpSession(JHttpServer server, JHttpServer.SocketListener listener)
    {
	this.server = server;
	this.listener = listener;
	attr = new Hashtable();
	setDaemon(true);
	activate = new Object();
    }

    public void assignSocket(Socket s)
    {
	synchronized (activate) { 
	    this.socket = s;
	    activated = true;
	    activate.notify();
	}
    }

    protected HttpServletResponse createServletResponse() { 
	return new JHttpServletResponse(this);	
    }

    protected HttpServletRequest createServletRequest(String url, String servletPath, String header, ServletInputStream stream) {
	return new JHttpServletRequest(this, url, servletPath, header, stream);
    }

    @Override
    public void run()
    {
	try { 
	    synchronized (activate) { 
		while (true) {		
		    if (!activated) { 
			activate.wait();
		    }
		    activated = false;
		    if (socket != null) {
			session();
		    }
		}
	    }
	} catch (InterruptedException x) {}
    }
    


    void session() 
    { 
	int beg, end, size, p;
	attr.clear();
	creationTime = System.currentTimeMillis();

	byte buf[] = new byte[HDR_BUF_SIZE];
	try { 
	    if (server.sessionTimeout != 0) { 
		socket.setSoTimeout(server.sessionTimeout);
	    }
	    in = socket.getInputStream();
	    out = socket.getOutputStream();	    
	    principal = null;
	    principalLogin = null;
	    newSession = true;
	    size = 0;
	  session:
	    while (true) { 
		byte ch;
		HttpServletResponse response = createServletResponse();		
		p = 0;
		do {
		    if (p == size) { 
			if (size == buf.length) { 
			    byte[] newBuf = new byte[buf.length*2];
			    System.arraycopy(buf, 0, newBuf, 0, size);
			    buf = newBuf;
			}
			int rc = in.read(buf, size, buf.length - size);
			if (rc < 0) { 
			    break session;
			}
			if (rc == 0) { 
			    response.sendError(200, "OK"); // connection closed due to timeout expiration
			    break session;
			}
			size += rc;
		    }
		    while (p < size 
			   && (p < 2 
			       || !(buf[p] == '\n' 
				    && (buf[p-1] == '\n' 
					|| (buf[p-1] == '\r' && buf[p-2] == '\n' && buf[p-3] == '\r')))))
		    {
			p += 1;
		    }
		} while (p == size);
		
		p += 1; // p now points to the message body
		lastAccessedTime = System.currentTimeMillis();
		beg = 0;
		while (beg < p && (buf[beg] & 0xFF) <= ' ') {
		    beg += 1;
		}
		String header = new String(buf, beg, p - beg); 
		if (server.debug) { 
		    System.out.println("++++++++ REQUEST HEADER +++++++++++++");
		    System.out.print(header);
		    System.out.println("+++++++++++++++++++++++++++++++++++++");
		}
		
		JHttpServletRequest.ServletInputPipe stream =
                    new JHttpServletRequest.ServletInputPipe(in, buf, p, size);
		size = 0;
		beg = header.indexOf(' ') + 1;
		end = header.lastIndexOf(' ', header.indexOf('\n', beg));
		String url = JHttpServletRequest.unpack(header.substring(beg, end).trim()); 
		beg = url.indexOf('/');
		if (beg >= 0 && url.length() > beg+1 && url.charAt(beg+1) == '/') { 
		    beg = url.indexOf('/', beg+2);
		}
		if (beg > 0) { 
		    url = url.substring(beg);
		}
		JHttpServer.ServletDescription servletDesc = server.getServlet(url);
		if (servletDesc != null) { 
		    HttpServletRequest request = createServletRequest(url, servletDesc.servletPath, header, stream);
		    if ("100-continue".equalsIgnoreCase(request.getHeader("expect"))) { 
			response.sendError(100, "CONTINUE");
		    }
		    servletDesc.servlet.service(request, response);
		    if (!"keep-alive".equalsIgnoreCase(request.getHeader("connection"))) {
			break;
		    }
                    int contentLength = request.getContentLength();
                    if (contentLength > 0) { 
                        stream.eatRequestBody(contentLength);
                    }
		} else { 
		    response.sendError(404, "NOT FOUND");		    
		}
	    }
	} catch(IOException x) { 
	    // do noting in case of IOException
	} catch(Throwable x) { 
	    x.printStackTrace();
	    try { 
		HttpServletResponse response = createServletResponse();		
		response.sendError(500, "INTERNAL SERVER ERROR");
	    } catch (Exception ex) {}				   
	}
	try { 
	    socket.close();
	} catch (IOException x) {}
	server.release(this);
    }

    public long getCreationTime() 
    {
        return creationTime;
    }
    
    public String getIdd() 
    {
	return toString();
    }
    
    public long getLastAccessedTime()
    {
	return lastAccessedTime;
    }
    
    public void setMaxInactiveInterval(int interval) 
    {
	inactiveInterval = interval;
    }

    public int getMaxInactiveInterval()
    {
	return inactiveInterval;
    }
   
    public HttpSessionContext getSessionContext()
    {
	return null;
    }
    
    public Object getAttribute(String name)
    {
	return attr.get(name);
    }
    
    public Object getValue(String name)
    {
	return attr.get(name);
    }
    
    public Enumeration getAttributeNames()
    {
	return attr.keys();
    }
    
    public String[] getValueNames()
    {
	Enumeration enum1 = attr.keys();
	String names[] = new String[attr.size()];
	for (int i = 0; enum1.hasMoreElements(); i++) { 
	    names[i] = (String)enum1.nextElement();
	}
	return names;
    }
    
    public void setAttribute(String name, Object value)
    {
	attr.put(name, value);
    }

    public void putValue(String name, Object value)
    {
	attr.put(name, value);
    }

    public void removeAttribute(String name)
    {
	attr.remove(name);
    }

    public void removeValue(String name)
    {
	attr.remove(name);
    }

    public void invalidate()
    {
	attr.clear();
    }
    
    
    public boolean isNew()
    {
	boolean isNewSession = newSession;
	newSession = false;
	return isNewSession;
    }


    Hashtable    attr;
    long         creationTime;
    long         lastAccessedTime;
    int          inactiveInterval;
    Socket       socket;
    JHttpServer  server;
    JHttpSession next;
    boolean      newSession;
    JHttpServer.SocketListener listener;
    InputStream  in;
    OutputStream out;
    Object       activate;
    boolean      activated;

    Principal    principal;
    String       principalLogin;
}





