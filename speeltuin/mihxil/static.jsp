<%-- 
   Included this as the first line of 'non-personalized' jsp-pages.
   Session will be disabled and an (short) expire time will be set. 
--%><%@ page session="false"
%><%!
long EXPIRES = 20 * 1000; // 20 secs.

// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%><% 
long ct = System.currentTimeMillis();
response.setDateHeader("Expires", ct + EXPIRES); 
// response.setDateHeader("Last-Modified", ct);
%> 
