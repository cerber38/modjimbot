package org.garret.httpserver;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

public interface Authenticator { 
    String securityLoginConfig = System.getProperty("java.security.auth.login.config");

    String    getAuthType(HttpServletRequest req);
    
    Principal authenticate(HttpServletRequest req);
}

