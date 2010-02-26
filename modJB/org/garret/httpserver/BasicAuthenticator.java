package org.garret.httpserver;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;



public class BasicAuthenticator implements Authenticator { 

    public String getAuthType(HttpServletRequest req) { 
	return "Basic realm=\"" + req.getServerName() + ":" + req.getServerPort() + "\"";
    }

    public Principal authenticate(HttpServletRequest req) { 
	String auth = req.getHeader("Authorization");
	if (auth == null) {
	    return null;
	}
	auth = auth.trim();
	int sep = auth.indexOf(' ');
	if (sep < 0) { 
	    return null;
	}	    
	String authType = auth.substring(0, sep);
	if (!authType.equalsIgnoreCase("Basic")) {
	    return null;
	}
	String userAndPassword = Codecs.base64Decode(auth.substring(sep+1).trim());

	JHttpSession session = (JHttpSession)req.getSession();
	if (userAndPassword.equals(session.principalLogin)) { 
	    return session.principal;
	}

	sep = userAndPassword.indexOf(':');
	String name = userAndPassword.substring(0, sep);
	String password = userAndPassword.substring(sep+1);
	
	CallbackHandler callback = new BasicCallbackHandler(name, password);

	if (securityLoginConfig != null) {
	    try {
		LoginContext context = new LoginContext("JHttpServer", callback);
		context.login();
		return new JHttpPrincipal(name);
	    }  catch (Exception e) {}
	}
	return null;
    }

    static class BasicCallbackHandler implements CallbackHandler {
	/* User login and password */
	String login;
	String password;
       
	BasicCallbackHandler(String login, String password) {
	    this.login = login;
	    this.password = password;
	}
    
        public void handle(Callback[] callbacks)
	    throws IOException, UnsupportedCallbackException 
	{	   
	    for (int i = 0; i < callbacks.length; i++) {
		if (callbacks[i] instanceof TextOutputCallback) {		   
		    // display the message according to the specified type
		    TextOutputCallback tc = (TextOutputCallback) callbacks[i];
		   
		    String text;
		    switch (tc.getMessageType()) {
		      case TextOutputCallback.INFORMATION:
			text = "";
			break;
		      case TextOutputCallback.WARNING:
			text = "Warning: ";
			break;
		      case TextOutputCallback.ERROR:
			text = "Error: ";
			break;
		      default:
			throw new UnsupportedCallbackException(callbacks[i], "Unrecognized message type");
		    }
		   
		    String message = tc.getMessage();
		    if (message != null) {
			text += message;
		    }
		    if (text != null) {
			System.err.println(text);
		    }
		} else if (callbacks[i] instanceof NameCallback) {
		    // prompt the user for a username
		    NameCallback nc = (NameCallback)callbacks[i];
		    // Ignore default username
		    nc.setName(login);
		} else if (callbacks[i] instanceof PasswordCallback) {
		    // prompt the user for sensitive information
		    PasswordCallback pc = (PasswordCallback)callbacks[i];
		    pc.setPassword(password.toCharArray());
		} else {
		    throw new UnsupportedCallbackException
			(callbacks[i], "Unrecognized Callback");
		}
	    }
	}
    }
}
