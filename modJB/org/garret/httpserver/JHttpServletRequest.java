package org.garret.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class JHttpServletRequest implements HttpServletRequest
{
    public Object getAttribute(String name)
    {
	return attrHash.get(name);
    }

    public Enumeration getAttributeNames()
    {
	return attrHash.keys();
    }

    public String getCharacterEncoding()
    {
	String contentType = getHeader("content-type");
	if (contentType == null) { 
	    return null;
	}
	int beg = contentType.indexOf("charset=\"");
	if (beg >= 0) { 
	    int end =  contentType.indexOf('"', beg+9);
	    return contentType.substring(beg+1, end);
	}
	return null;
    }


    public int getContentLength() 
    {
	return getIntHeader("content-length");
	/*
	try { 
	    return stream.available();
	} catch (IOException x) { 
	    return 0;
	}
	*/
    }

    public String getContentType()
    {
	return getHeader("content-type");
    }


    static class ServletInputByteArrayStream extends ServletInputStream {
	byte[] buf;
	int    pos;
	int    count;
	int    mark;

        public ServletInputByteArrayStream(byte[] ptr) 
        {
	    buf = ptr;
	    pos = 0;
	    count = ptr != null ? ptr.length : 0;
	}

	public int read() throws IOException 
        { 
	    return (pos < count) ? (buf[pos++] & 0xff) : -1;
	}

        public int read(byte b[], int off, int len) 
        {
	    if (b == null) {
		throw new NullPointerException();
	    } else if ((off < 0) || (off > b.length) || (len < 0) ||
		       ((off + len) > b.length) || ((off + len) < 0)) {
		throw new IndexOutOfBoundsException();
	    }
	    if (pos >= count) {
		return -1;
	    }
	    if (pos + len > count) {
		len = count - pos;
	    }
	    if (len <= 0) {
		return 0;
	    }
	    System.arraycopy(buf, pos, b, off, len);
	    pos += len;
	    return len;
	}

        public long skip(long n) {
	    if (pos + n > count) {
		n = count - pos;
	    }
	    if (n < 0) {
		return 0;
	    }
	    pos += n;
	    return n;
	}

        public int available() {
	    return count - pos;
	}
	
        public boolean markSupported() 
	{
	    return true;
	}

        public void mark(int readAheadLimit) 
        {
	    mark = pos;
	}

        public void reset() 
        {
	    pos = mark;
	}

        public void close() {}
    }

    static class ServletInputPipe extends ServletInputStream {
	InputStream stream;
	byte[]      buf;
	int         beg;
	int         end;
        long        pos;

        public ServletInputPipe(InputStream in, byte buf[], int beg, int end) { 
	    stream = in;
	    this.buf = buf;
	    this.beg = beg;
	    this.end = end;
            pos = 0;
	}

	public int read() throws IOException { 
	    if (beg < end) { 
                pos += 1;
		return buf[beg++] & 0xFF;
	    } else { 
		int ch = stream.read();
                if (ch >= 0) { 
                    pos += 1;
                }
                return ch;
	    }
	}

        public int read(byte b[], int off, int len) throws IOException { 
	    if (beg < end) { 
		if (len < end - beg) { 
		    System.arraycopy(buf, beg, b, off, len);
		} else { 
		    len = end - beg;
		    System.arraycopy(buf, beg, b, off, len);
		}
		beg += len;
                pos += len;
		return len;
	    } else { 
		len = stream.read(b, off, len);
                pos += len;
                return len;
	    }
	}

        public long skip(long n) throws IOException {
	    if (beg < end) { 
		if (beg + n <= end) { 
		    beg += n;
                    pos += n;
		    return n;
		} else { 
		    long d = end - beg;
		    beg = end;
                    d += stream.skip(n - d);
                    pos += d;
		    return d;
		}
	    }
	    long d = stream.skip(n);
            pos += d;
            return d;
	}

        public int available() throws IOException {
	    return end - beg + stream.available();
	}
	
        public boolean markSupported() {
	    return stream.markSupported();
	}

        public void mark(int readAheadLimit) {
	    stream.mark(readAheadLimit);
	}

        public void reset() throws IOException {
	    stream.reset();
	}

        public void close() throws IOException {
	    // stream.close();
	}

        public void eatRequestBody(long contentLength) throws IOException { 
            if (pos < contentLength) { 
                skip(contentLength - pos);
            }
        }
    }


    public ServletInputStream getInputStream() throws IOException
    {
	return stream;
    }

    public String getParameter(String name)
    {
	return (String)paramHash.get(name);
    }

    public Enumeration getParameterNames()
    {
	return paramHash.keys();
    }

    public String[] getParameterValues(String name)
    {
	String value = getParameter(name);
	if (value != null) { 
	    String arr[] = new String[1];
	    arr[0] = value;
	    return arr;
	}
	return null;
    }

    public String getProtocol()
    {
	int end = header.indexOf('\n');
	int beg = header.lastIndexOf(' ', end);
	return header.substring(beg, end).trim();
    }

    public String getScheme()
    {
	return session.listener.getScheme();
    }

    public String getServerName()
    {
	InetAddress addr = session.listener.socket.getInetAddress();
	return addr.isAnyLocalAddress() ? "localhost" : addr.getHostAddress();
    }

    public int getServerPort()
    {
	return session.listener.socket.getLocalPort();
    }

    public BufferedReader getReader() throws IOException
    {
	if (reader == null) { 
	    reader = new BufferedReader(new InputStreamReader(getInputStream()));
	}
	return reader;
    }

    public String getRemoteAddr()
    {
	return session.socket.getInetAddress().toString();
    }

    public String getRemoteHost()
    {
	String host = getHeader("host");
	return host != null ? host : getRemoteAddr();
    }

    public void setAttribute(String name, Object o)
    {
	attrHash.put(name, o);
    }

    public void removeAttribute(String name)
    {
	attrHash.remove(name);
    }

    public Locale getLocale()
    {
	return Locale.getDefault();
    }

    public Enumeration getLocales()  
    {
	Hashtable hash = new Hashtable();
	hash.put(Locale.getDefault(), null);
	return hash.keys();
    }

    public boolean isSecure()
    {
	return false;
    }

    public RequestDispatcher getRequestDispatcher(String path)
    {
	return null;
    }

    public String getRealPath(String path)
    {
	return path;
    }

    public String getAuthType()
    {
	return session.server.authenticator.getAuthType(this);
    }

    public Cookie[] getCookies() 
    {
	return null;
    }
    
    public long getDateHeader(String name)
    {
	return -1;
    }
    
    static class HeaderEnumeration implements Enumeration { 
	static class HeaderEnumerationElem { 
	    HeaderEnumerationElem next;
	    Object                value;
	}
	HeaderEnumerationElem curr;

	public boolean hasMoreElements() { 
	    return curr != null;
	}

	public Object nextElement()
	{
	    if (curr == null) { 
		throw new NoSuchElementException();
	    }
	    Object elem = curr.value;
	    curr = curr.next;
	    return elem;
	}

	void add(Object obj) { 
	    HeaderEnumerationElem elem = new HeaderEnumerationElem();
	    elem.next = curr;
	    elem.value = obj;
	    curr = elem;
	}
    }

    public String getHeader(String name)
    {
	Object obj = hdrHash.get(name.toLowerCase());
	if (obj instanceof HeaderEnumeration) { 
	    obj = ((HeaderEnumeration)obj).curr.value;
	}
	return (String)obj;
    }

    public Enumeration getHeaders(String name)
    {
	Object obj = hdrHash.get(name.toLowerCase());
	HeaderEnumeration enum1;
	if (!(obj instanceof HeaderEnumeration)) { 
	    enum1 = new HeaderEnumeration();
	    if (obj != null) { 
		enum1.add(obj);
	    }
	} else { 
	    enum1 = (HeaderEnumeration)obj;
	}
	return enum1;
    }    

    public Enumeration getHeaderNames()
    {
	return hdrHash.keys();
    }
    
    public int getIntHeader(String name) throws  NumberFormatException 
    { 
	String value = getHeader(name);
	if (value != null) { 
	    return Integer.parseInt(value, 10);
	} else { 
	    return -1;
	}
    }
    

    public String getMethod() 
    {
	return header.substring(0, header.indexOf(' '));
    }
    
    public String getPathInfo()
    {
	String pathInfo = url.substring(servletPath.length());
	if (pathInfo.length() == 0) { 
	    pathInfo = "/";
	}
	return pathInfo;
    }

    public String getPathTranslated()
    {
	return servletPath;
    }
    
    public String getContextPath()
    {
	return servletPath;
    }
    
    public String getQueryString()
    {
	return query;
    }
    
    
    
    public String getRemoteUser() 
    {
	return null;
    }
    
    
    public boolean isUserInRole(String role)
    {
	return false;
    }
    
    
   public java.security.Principal getUserPrincipal()
    {
	return principal;
    }

    public String getRequestedSessionId() 
    {
        return session.getIdd();
    }

    public String getRequestURI()
    {
	return url;
    }
    

    public String getServletPath()
    {
	return "";
    }    
    
    
    public HttpSession getSession(boolean create)
    {
	return session;
    }
    
    public HttpSession getSession()
    {
	return session;
    }
    
    public boolean isRequestedSessionIdValid()
    {
	return true;
    }
    
    
    public boolean isRequestedSessionIdFromCookie()
    {
	return false;
    }

    
    public boolean isRequestedSessionIdFromURL()
    {
	return false;
    }
    
    public boolean isRequestedSessionIdFromUrl()
    {
	return false;
    }   

    static String unpack(String s) { 
	int i, j, len = s.length();
	char[] buf = new char[len];
	for (i = 0, j = 0; i < len; i++) { 
	    char ch = s.charAt(i);
	    if (ch == '%') { 
		char c1 = s.charAt(i+1);
		char c2 = s.charAt(i+2);
		ch = (char)(((c1 >= 'A' ? c1 - 'A'+ 10 : c1 - '0') << 4) |
			    (c2 >= 'A' ? c2 - 'A'+ 10 : c2 - '0'));
		i += 2;
	    }
	    buf[j++] = ch;
	}
	return new String(buf, 0, j);
    }

    void unpackQueryParameters(String query)
    {
	int  i = 0;
	int  n = query.length(); 
	char ch;
	char buf[] = new char[n];

	while (i < n) { 
	    int j = i + 1;
	    while (j < n && (ch = query.charAt(j)) != '=' && ch != '&') { 
		j += 1;
	    }
	    if (j < n) { 
		String name = query.substring(i, j);
                if (query.charAt(j) == '&') { 
                    paramHash.put(name, "");
                } else { 
                    int k = 0;
                    while (++j < n && (ch = query.charAt(j)) != '&') { 
                        if (ch == '+') {
                            ch = ' ';
                        } else if (ch == '%' && j+2 < n) {
                            char c1 = query.charAt(j+1);
                            char c2 = query.charAt(j+2);
                            byte b = (byte)(((c1 >= 'A' ? c1 - 'A'+ 10 : c1 - '0') << 4) |
                                        (c2 >= 'A' ? c2 - 'A'+ 10 : c2 - '0'));
                            ByteBuffer bb = ByteBuffer.allocate(1);
                            bb.put(b);
                            bb.flip();
							CharBuffer cc = (Charset.forName("windows-1251").decode(bb));
							ch = cc.charAt(0);
                            j += 2;
                        }
                        buf[k++] = ch;
                    }
                    paramHash.put(name, new String(buf, 0, k));
                }
	    }
	    i = j + 1;
	}
    }    


    JHttpServletRequest(JHttpSession session, String url, String servletPath, String header, ServletInputStream stream)
    {
	this.header = header;
	this.servletPath = servletPath;
	this.session = session;
	this.stream = stream;
	reader = null;
	int beg = header.indexOf('\n');
	int end;
	hdrHash = new Hashtable();
	attrHash = new Hashtable();
	paramHash = new Hashtable();

	while ((end = header.indexOf('\n', beg)) >= 0) { 
	    int column = header.indexOf(':', beg);
	    if (column >= 0 && column < end) {
		String name = header.substring(beg, column).trim().toLowerCase();
		String value = unpack(header.substring(column+1, end).trim());
		Object prevValue = hdrHash.put(name, value);
		if (prevValue != null) { 
		    if (prevValue instanceof HeaderEnumeration) { 
			((HeaderEnumeration)prevValue).add(value);
		    } else { 
			HeaderEnumeration enum1 = new HeaderEnumeration();
			enum1.add(prevValue);
			enum1.add(value);
			hdrHash.put(name, enum1);
		    }
		}
	    }
	    beg = end + 1;
	}
	int p = url.indexOf('?');
	if (p < 0) { 
	    this.url = url;
            if ("POST".equals(getMethod())) { 
                int len = getContentLength();
                if (len >= 0) { 
                    byte[] buf = new byte[len];
                    int offs = 0;
                    try { 
                        while (offs < len) { 
                            int rc = stream.read(buf, offs, len - offs);
                            if (rc > 0) { 
                                offs += rc;
                            }
                        }
                        query = new String(buf);
                    } catch (IOException x) {
                    }
                }
            }                
	} else { 
	    this.url = url.substring(0, p);
	    query = url.substring(p+1);
        }
        if (query != null) { 
	    unpackQueryParameters(query);
	}
	principal = session.server.authenticator.authenticate(this);
    }

    JHttpSession        session;
    String              servletPath;
    String              url;
    String              header;
    String              query;
    Hashtable           hdrHash;
    Hashtable           attrHash;
    Hashtable           paramHash;
    ServletInputStream  stream;
    BufferedReader      reader;
    Principal           principal;
}


