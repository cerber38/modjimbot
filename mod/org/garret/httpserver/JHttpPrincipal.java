package org.garret.httpserver;

public class JHttpPrincipal implements java.security.Principal {
    String name;

    public boolean equals(Object another) {
	if (another instanceof JHttpPrincipal) { 
	    return name.equals(((JHttpPrincipal)another).name);
	}
	return false;
    }

    public String toString() { 
	return name;
    }
    
    public int hashCode() { 
	return name.hashCode();
    }

    public String getName() { 
	return name;
    }

    JHttpPrincipal(String name) { 
	this.name = name;
    }
}
