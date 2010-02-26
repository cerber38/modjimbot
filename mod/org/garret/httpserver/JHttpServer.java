package org.garret.httpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

public class JHttpServer {     
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 

    static String input(String prompt) {
	while (true) { 
	    try { 
		System.out.print(prompt);
		String answer = in.readLine().trim();
		if (answer.length() != 0) {
		    return answer;
		}
	    } catch (IOException x) {}
	}
    }


    public static final String JHTTP_SERVER_PROPS_FILE_NAME = "jhttpserver.properties";

    public static void main(String args[]) throws Exception { 
	if (args.length == 0) { 
	    System.err.println("Java Http Server\nUsage:\n\tjava [-Dconfig=CONFIG_FILE] [-Dport=PORT] [-DmaxThreads=NNN] [-DauthType=(jaas|anonymous|everyone)] [-Ddebug=BOOL] [-Dssl-support=BOOL] [-Dssl-port=PORT] [-Dssl-authenticate=BOOL] [-Dssl-cert-file=FILE] [-Dssl-cert-password=password] org.garret.httpserver.JHttpServer (servlet-path servlet-class)+");
	    return;
	}
	File propsFile;
	String cfgFileName = System.getProperty("config");
	if (cfgFileName != null) { 
	    propsFile = new File(cfgFileName);
	    if (!propsFile.exists()) { 
		System.err.println("Configuration file " + propsFile + " doesn't exit");
		System.exit(1);
	    }
	} else { 
	    propsFile = new File(JHTTP_SERVER_PROPS_FILE_NAME);
	}
	Properties serverProperties;
	if (propsFile.exists()) { 
	    serverProperties = new Properties();
	    FileInputStream stream = new FileInputStream(propsFile);
	    serverProperties.load(stream);
	    stream.close();
	} else { 
	    serverProperties = System.getProperties();
	}
	Hashtable servletMapping = new Hashtable();
	for (int i = 0; i < args.length; i += 2) { 
	    String path = args[i];
	    if (!path.startsWith("/")) { 
		path = "/" + path;
	    } 
	    if (path.endsWith("/")) { 
		path = path.substring(0, path.length()-1);
	    }
	    servletMapping.put(path, args[i+1]);
	}
	JHttpServer server = new JHttpServer(servletMapping, serverProperties);

	while (true) {
	    String cmd = input("> ");
	    if (cmd.equalsIgnoreCase("exit")) { 
		server.shutdown();	 
		break;
	    } else if (cmd.equalsIgnoreCase("info")) { 
		System.out.println("Number of active sessions: " + server.nActiveSessions);
	    } else if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("?")) { 
		System.out.println("Commands: exit, info, help");
	    } else { 
		System.out.println("Invalid command " + cmd);
		System.out.println("Commands: exit, info, help");
	    }
	}
    }
    
    String getProperty(String name, String defaultValue) { 
	String prop = System.getProperty(name);
	if (prop == null) { 
	    prop = serverProperties.getProperty(name, defaultValue);
	} 
	return prop;
    }

    public JHttpServer(Hashtable servletMapping, Properties serverProperties)
      throws Exception 
    { 
	this.servletMapping = servletMapping;
	this.serverProperties = serverProperties;

	maxThreads = Integer.parseInt(getProperty("maxThreads", "64"), 10);
	int lingerTime = Integer.parseInt(getProperty("linger", "10"), 10);
	debug = "true".equalsIgnoreCase(getProperty("debug", "false"));
	String authType= getProperty("authType", "everyone");
	authenticator = "jaas".equalsIgnoreCase(authType)
 	      ? (Authenticator)new BasicAuthenticator() : "anonymous".equalsIgnoreCase(authType)
	      ? (Authenticator)new AnonymousAuthenticator() 
	      : (Authenticator)new EveryoneAuthenticator();

	sessionTimeout = Integer.parseInt(getProperty("sessionTimeout", "0"), 10);
	listeners = new ArrayList();
	int port = Integer.parseInt(getProperty("port", "8080"), 10);
	if (port > 0) {
	    try {
		SocketListener httpListener = new SocketListener(port, lingerTime, false);
		listeners.add(httpListener);
		httpListener.start();
	    } catch(Exception x) {
		System.err.println("Can't start http server: " + x);
	    }
	}
	if ("true".equalsIgnoreCase(getProperty("ssl-support", "false"))) {
	    port = Integer.parseInt(getProperty("ssl-port", "443"), 10);
	    if (port > 0) {
		try {
		    SocketListener httpsListener = new SocketListener(port, lingerTime, true);
		    listeners.add(httpsListener);
		    httpsListener.start();
		} catch(Exception x) {
		    System.err.println("Can't start https server: " + x);
		}
	    }
	}
    }

    public synchronized void shutdown() 
    { 
	for (int i = 0; i < listeners.size(); i++) {
	    ((SocketListener)listeners.get(i)).shutdown();
	}
	Enumeration enum1 = servletMapping.elements();
	while (enum1.hasMoreElements()) { 
	    Object obj = enum1.nextElement();
	    if (obj instanceof Servlet) {
		((Servlet)obj).destroy();
	    }
	}
    }

    protected synchronized boolean runSession(SocketListener listener, Socket s) {
	while (sessions == null) { 
	    if (nActiveSessions == maxThreads) { 
		return false;
	    } else { 
		sessions = createSession(listener);
		sessions.start();
	    }
	}		
	JHttpSession session = sessions;
	sessions = session.next;
	nActiveSessions += 1;
	if (debug) { 
	    System.out.println("Start session");
	}
	session.assignSocket(s);
	return true;
    }

    protected JHttpSession createSession(SocketListener listener) { 
	return new JHttpSession(this, listener);
    }
    

    public synchronized void release(JHttpSession session)
    {
	session.next = sessions;
	sessions = session;
	if (nActiveSessions == maxThreads) { 
	    notifyAll();
	}
	nActiveSessions -= 1;
    }
    
    public Hashtable getServletMapping() 
    {
	return servletMapping;
    }
    
    static class ServletDescription { 
	HttpServlet servlet;
	String      servletName;
	String      servletPath;
    }
    
    synchronized  ServletDescription getServlet(String url) throws Exception 
    { 
	String servletPath = url;
        int queryPrefix = servletPath.indexOf('?');
        if (queryPrefix >= 0){ 
            servletPath  = servletPath.substring(0, queryPrefix);
        }
	Object servlet = servletMapping.get(servletPath);
	while (servlet == null) { 
	    int end = servletPath.lastIndexOf('/');
	    if (end < 0) { 
		break;
	    }
	    servletPath = servletPath.substring(0, end);
	    servlet = servletMapping.get(servletPath);
	}
	if (servlet == null) { 
	    return null;
	}
	String servletName = servletPath.substring(servletPath.lastIndexOf('/')+1);
	if (servlet instanceof String) { 
	    Class servletClass = Class.forName((String)servlet);
	    servlet = servletClass.newInstance();
	    JServletConfig config = new JServletConfig(this, servletName);
	    ((Servlet)servlet).init(config); 
	    servletMapping.put(servletPath, servlet);
	}
	ServletDescription desc = new ServletDescription();
	desc.servlet = (HttpServlet)servlet;
	desc.servletPath = servletPath;
	desc.servletName = servletName;
	return desc;
    }
    
    Hashtable      servletMapping;
    Authenticator  authenticator;
    ArrayList      listeners;
    JHttpSession   sessions;
    int            nActiveSessions;		 
    boolean        debug;
    int            maxThreads;
    int            sessionTimeout;
    Properties     serverProperties;

    class SocketListener extends Thread {
	ServerSocket   socket;
	int            lingerTime; 
	boolean        running;
	int            port;
	String         scheme;

	public SocketListener(int port, int lingerTime, boolean sslSocket) throws Exception {
	    this.port = port;
	    this.lingerTime = lingerTime;
	    ServerSocketFactory ssf = getServerSocketFactory(sslSocket);
	    socket = ssf.createServerSocket(port);
	    if("true".equalsIgnoreCase(getProperty("ssl-authenticate", "false")) 
	       && socket instanceof SSLServerSocket) 
	    {
		((SSLServerSocket)socket).setNeedClientAuth(true);
	    }
	    scheme = (sslSocket) ? "https" : "http";
	    running = true;
	}

	public String getScheme() {
	    return scheme;
	}

	public void run() 
	{ 
	    try { 
		while (running) { 
		    Socket s = socket.accept();			
		    if (!running) { 
			break;
		    }
		    try { 
			s.setTcpNoDelay(true);
		    } catch (NoSuchMethodError er) {}
		    if (lingerTime != 0) { 
			try {
			    s.setSoLinger(true, lingerTime);
			} catch (NoSuchMethodError er) {}
		    }
		    while (!runSession(this, s)) {
			wait();
		    }
		}
	    } catch(Exception x) {
		x.printStackTrace();
	    }		 
	}
    
	public synchronized void shutdown() 
	{ 
	    running = false;
	    try { 
		Socket s = new Socket("localhost", port); // wakeup accept thread
		s.close();
	    } catch (IOException x) {}
	}
    
	private ServerSocketFactory getServerSocketFactory(boolean sslSocket) throws Exception {
	    if (sslSocket) {
		SSLServerSocketFactory ssf = null;
		// set up key manager to do server authentication
		SSLContext ctx;
		KeyManagerFactory kmf;
		KeyStore ks;
		char[] passphrase = getProperty("ssl-cert-password", "password").toCharArray();
		
		ctx = SSLContext.getInstance("SSL");
		kmf = KeyManagerFactory.getInstance("SunX509");
		ks = KeyStore.getInstance("JKS");
		
		ks.load(new FileInputStream(getProperty("ssl-cert-file", "keystore")), passphrase);
		kmf.init(ks, passphrase);
		ctx.init(kmf.getKeyManagers(), null, null);
		
		ssf = ctx.getServerSocketFactory();
		return ssf;
	    } else {
		return ServerSocketFactory.getDefault();
	    }
	}
    }
}



