<%@ tag import="java.lang.reflect.*" body-content="empty"  %>
<%@ attribute name="message" description="the message that goes into the exception constructor"  %>
<%
    String className = "java.lang.Exception";
    String message = (String) jspContext.getAttribute("message", PageContext.PAGE_SCOPE);
    if(message == null){
        message = "";
    }
    Class c;
    Exception exc;
    c = Class.forName(className);
    try {
        Constructor co = c.getConstructor(new Class[] {String.class});
        exc = (Exception) co.newInstance(new Object[] {message});
    }catch(Exception e){
        exc = e;
    }

    //all done
    if(exc != null){
        throw exc;
    }
%>
