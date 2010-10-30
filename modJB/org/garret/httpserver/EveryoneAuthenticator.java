package org.garret.httpserver;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

public class EveryoneAuthenticator implements Authenticator { 

    public String getAuthType(HttpServletRequest req) { 
	return "Basic realm=\"" + req.getServerName() + ":" + req.getServerPort() + "\"";
    }

    public Principal authenticate(HttpServletRequest req) { 
	String auth = req.getHeader("Authorization");
	if (auth != null) {
	    auth = auth.trim();
	    int sep = auth.indexOf(' ');
	    if (sep >= 0) { 
		String authType = auth.substring(0, sep);
		if (authType.equalsIgnoreCase("Basic")) {
		    String userAndPassword = Codecs.base64Decode(auth.substring(sep+1).trim());
		    sep = userAndPassword.indexOf(':');
		    return new JHttpPrincipal(userAndPassword.substring(0, sep));
		}
	    }
	}
	return null;
    }
}

