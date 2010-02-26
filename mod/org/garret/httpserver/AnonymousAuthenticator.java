package org.garret.httpserver;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

public class AnonymousAuthenticator implements Authenticator { 

    public String getAuthType(HttpServletRequest req) { 
	return "Basic realm=\"" + req.getServerName() + ":" + req.getServerPort() + "\"";
    }

    public Principal authenticate(HttpServletRequest req) { 
	String auth = req.getHeader("Authorization");
	String name = null;
	if (auth != null) {
	    auth = auth.trim();
	    int sep = auth.indexOf(' ');
	    if (sep >= 0) { 
		String authType = auth.substring(0, sep);
		if (authType.equalsIgnoreCase("Basic")) {
		    String userAndPassword = Codecs.base64Decode(auth.substring(sep+1).trim());
		    sep = userAndPassword.indexOf(':');
		    name = userAndPassword.substring(0, sep);
		}
	    }
	}
	if (name == null) { 
	    name = req.getRemoteUser();
	    if (name == null) { 
		name = req.getRemoteHost();
	    }
	}
	return new JHttpPrincipal(name);
    }
}
