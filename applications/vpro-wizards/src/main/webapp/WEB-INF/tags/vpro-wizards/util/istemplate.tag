<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
    if the (jsp) template the file attribute points to is found, the body is evaluated.
--%>
<%@ tag %>
<%@ attribute name="file" required="true"  description="a full path to a jsp template"  %>
<%@ attribute name="relative" required="true" type="java.lang.Boolean" description="When true this path is treated like a relative path"  %>
<%@ attribute name="inverse"  type="java.lang.Boolean" description="When true, the bodyd is executed when the file dous not exist." %>
<c:if test="${empty inverse}"><c:set var="inverse" value="false" /></c:if>
<%
    String file = (String)jspContext.getAttribute("file");
    Boolean relative = (Boolean)jspContext.getAttribute("relative");
    
    if(relative.booleanValue() == true){
        //put the path to the current jsp before the file
        file = request.getServletPath().substring(0, request.getServletPath().lastIndexOf("/")+1)  + file;
    }
    
    String path = request.getRealPath(file);
    System.out.println("test-path:"+path);
    if(path != null && new java.io.File(path).exists()){
        %><c:if test="${inverse == false}"><jsp:doBody/></c:if><%       
    }else{
        %><c:if test="${inverse == true}"><jsp:doBody/></c:if><%
    }
%>


