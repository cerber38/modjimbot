package org.garret.httpserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class JHttpServletResponse implements HttpServletResponse
{   /**
     * HTTP Date format pattern (RFC 2068, 822, 1123).
     */
    public static final String DATE_FORMAT = "EEE, d MMM yyyy kk:mm:ss z";

 
    /**
     * Date formatter.
     */
    static final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT, 
							     Locale.ENGLISH);
    
    
    public String getCharacterEncoding() 
    { 
	return "ISO-8859-1";
    }

    class ServletResponseWriter extends Writer {
	ServletOutputStream out;

	ServletResponseWriter(ServletOutputStream out) { 
	    this.out = out;
	}

	public void write(char cbuf[], int off, int len) throws IOException {
	    write(new String(cbuf, off, len), 0, len);
	}

	public void write(String str, int off, int len) throws IOException {
	    byte[] buf = str.substring(off, off+len).getBytes(getCharacterEncoding());
	    out.write(buf, 0, buf.length);
	}

	public void close() throws IOException { 
	    out.close();
	}

	public void flush() throws IOException { 
	    out.flush();
	}
    }


    class ServletOutputByteArrayStream extends ServletOutputStream {
	public void append(ServletOutputByteArrayStream stream)
	{
	    if (stream.count + count > buf.length) { 
		byte newbuf[] = new byte[Math.max(buf.length << 1, (stream.count + count))];
		System.arraycopy(buf, 0, newbuf, 0, count);
		buf = newbuf;
	    }
	    System.arraycopy(stream.buf, 0, buf, count, stream.count);
	    count += stream.count;
	}

	public void write(int b) 
        {
	    int newcount = count + 1;
	    if (newcount > buf.length) {
		byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
		System.arraycopy(buf, 0, newbuf, 0, count);
		buf = newbuf;
	    }
	    buf[count] = (byte)b;
	    count = newcount;
	}

        public void write(byte b[], int off, int len) 
	{
	    if (b == null) {
		throw new NullPointerException();
	    } else if ((off < 0) || (off > b.length) || (len < 0) ||
		       ((off + len) > b.length) || ((off + len) < 0)) {
		throw new IndexOutOfBoundsException();
	    } else if (len == 0) {
		return;
	    }
	    if (count == 0 && off == 0 && len == b.length && len == contentLength) { 
		buf = b;
		count = len;		
	    } else { 
		int newcount = count + len;
		if (newcount > buf.length) {
		    byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
		    System.arraycopy(buf, 0, newbuf, 0, count);
		    buf = newbuf;
		}
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	    }
	}
        
        public void flush() throws IOException {            
            flushBuffer();
        }

        public void close() throws IOException {
            flush();
        }

	ServletOutputByteArrayStream() 
        {
	    buf = new byte[1024];
	    count = 0;
	}

	public void reset() { 
	    count = 0;
	}

	byte[] buf;
	int    count;
    }

    public ServletOutputStream getOutputStream() 
    {
	return body;
    }

    public PrintWriter getWriter()
    {
	return new PrintWriter(new ServletResponseWriter(getOutputStream()));
    }
    
    public void setContentLength(int len)
    {
	setIntHeader("Content-Length", len);
	contentLength = len;
    }
	
    public void setContentType(String type)
    {
	setHeader("Content-Type", type);
    }

    public void setBufferSize(int size)	
    {
	bufSize = size;
    }

    public int getBufferSize()
    {
	return bufSize;
    }

    public void flushBuffer() throws IOException 
    {
	setContentLength(body.count);
	setIntHeader("Status", statusCode);
	Enumeration enum1 = headerHash.keys();
	header.print("HTTP/1.1 ");
	header.print(statusCode);
	header.print(" ");
	header.println(statusText);
	header.println("Connection: Keep-Alive");
	while (enum1.hasMoreElements()) { 
	    String name = (String)enum1.nextElement();
	    Object value = headerHash.get(name);
	    header.print(name);
	    header.print(": ");
	    header.println(value.toString());
	}
	header.append(add);
	header.println();
	session.out.write(header.buf, 0, header.count);
	session.out.write(body.buf, 0, body.count);
	if (session.server.debug) { 
	    System.out.println("--------------- RESPONSE -----------------");
	    System.out.print(new String(header.buf, 0, header.count));
	    //System.out.print(new String(body.buf, 0, body.count));
	    System.out.println("------------------------------------------");
	}
	session.out.flush();
	isCommitted = true;
    }

    public boolean isCommitted()
    {
	return isCommitted;
    }

    public void reset()
    {
	header.reset();
	body.reset();
    }

    public void setLocale(Locale loc)
    {
	locale = loc;
    }

    public Locale getLocale()
    {
	return locale;
    }

    public void addCookie(Cookie cookie) {}

    public boolean containsHeader(String name)
    {
	return headerHash.get(name) != null;
    }

    public String encodeURL(String url)
    {
	return url;
    }
    
    public String encodeRedirectURL(String url)
    {
	return url;
    }

    public String encodeUrl(String url)
    {
	return encodeURL(url);
    }

    public String encodeRedirectUrl(String url)
    {
	return encodeRedirectURL(url);
    }

    public void sendError(int sc, String msg) throws IOException
    {
	String message;
	if (sc >= 400) { 
	    String body = "<HTML><HEAD><TITLE>Invalid request to the server</TITLE>\r\n"
		+ "</HEAD><BODY>\r\n<H1>" + msg + "</H1>\r\n</BODY></HTML>\r\n";
	    String header = "HTTP/1.1 " + sc + " " + msg 
		+ "\r\nConnection: Keep-Alive\r\nContent-Type: text/html\r\nContent-Language: en\r\nContent-Length: " 
		+ body.length() + "\r\n\r\n";
	    message = header + body;
	    if (session.server.debug) { 
		System.out.println("_______ERROR _______");
		System.out.print(message);
		System.out.println("____________________");
	    }
	} else { 
	    message = "HTTP/1.1 " + sc + " " + msg + "\r\nConnection: Keep-Alive\r\n\r\n";
	}
	session.out.write(message.getBytes());
	session.out.flush();
    }

    public void sendError(int sc) throws IOException
    {
	sendError(sc, Integer.toString(sc, 10));
    }

    public void sendRedirect(String location) {}

    public void setDateHeader(String name, long date)
    {
	setHeader(name, formatter.format(new java.util.Date(date)));	
    }

    public void addDateHeader(String name, long date)
    {
	addHeader(name, formatter.format(new java.util.Date(date)));	
    }

    public void setHeader(String name, String value)
    {
	headerHash.put(name, value);
    }

    public void addHeader(String name, String value) 
    {
	try { 
	    add.print(name);
	    add.print(": ");
	    add.println(value);
	} catch(IOException x) {}
    }

    public void setIntHeader(String name, int value)
    {
	setHeader(name, Integer.toString(value, 10));
    }

    public void addIntHeader(String name, int value)
    {
	addHeader(name, Integer.toString(value, 10));
    }

    public void setStatus(int sc)
    {
	String code = Integer.toString(sc, 10);
	String text = (String)statusTextHashtable.get(code);
	setStatus(sc, text != null ? text : code);
    }

    public void setStatus(int sc, String sm)
    {
	statusCode = sc;
	statusText = sm;
    }

    JHttpServletResponse(JHttpSession session)
    {
	this.session = session;
	header = new ServletOutputByteArrayStream();
	add = new ServletOutputByteArrayStream();
	body = new ServletOutputByteArrayStream();
	headerHash = new Hashtable();
	setContentType("text/html");
	//setHeader("Content-Language", "en");
	setHeader("Server", "JDav server 1.0");
        setHeader("Date", formatter.format(new java.util.Date()));
	statusCode = 200;
	statusText = "OK";
	reset();
    }

    ServletOutputByteArrayStream header;
    ServletOutputByteArrayStream add;
    ServletOutputByteArrayStream body;
    
    int          statusCode;
    String       statusText;
    boolean      isCommitted;
    int          bufSize;
    int          contentLength;
    Locale       locale;
    Hashtable    headerHash;
    JHttpSession session;

    static Hashtable statusTextHashtable;
    
    static { 
	statusTextHashtable = new Hashtable();
	statusTextHashtable.put("200", "OK");
	statusTextHashtable.put("201", "CREATED");
	statusTextHashtable.put("202", "ACCEPTED");
	statusTextHashtable.put("204", "NO_CONTENT");
	statusTextHashtable.put("207", "MULTISTATUS");
	statusTextHashtable.put("400", "BAD REQUEST");
	statusTextHashtable.put("401", "UNAUTHORIZED");
	statusTextHashtable.put("403", "FORBIDDEN");
	statusTextHashtable.put("404", "NOT FOUND");
	statusTextHashtable.put("405", "METHOD NOT ALLOWED");
	statusTextHashtable.put("409", "CONFLICT");
	statusTextHashtable.put("412", "PRECONDITION FAILED");
	statusTextHashtable.put("415", "UNSUPPORTED_MEDIA_TYPE");
	statusTextHashtable.put("423", "LOCKED");
	statusTextHashtable.put("500", "INTERNAL SERVER ERROR");
	statusTextHashtable.put("501", "NOT IMPLEMENTED");
	statusTextHashtable.put("503", "SERVICE UNAVAILABLE");
    }
}
