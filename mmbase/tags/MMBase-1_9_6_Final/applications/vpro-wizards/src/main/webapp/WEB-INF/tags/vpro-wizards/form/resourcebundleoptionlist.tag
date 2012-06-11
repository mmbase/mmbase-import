<%@ tag import="java.util.*, org.mmbase.applications.vprowizards.spring.util.*" body-content="empty" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="bundle" description="a fully qualified resourcename pointing to a resource bundle. when empty the value of the fields 'guitype' is assumed to be a pointer to a resourcebundle"  %>

<c:if test="${empty bundle}">
        <c:set var="bundle" value="${requestScope.guitype}"/>
</c:if>

<%
    OptionlistBean b = new OptionlistBean();
    jspContext.setAttribute("_options", b, PageContext.REQUEST_SCOPE);

    String bundle = (String) jspContext.getAttribute("bundle");
    java.util.PropertyResourceBundle bun = (java.util.PropertyResourceBundle) java.util.ResourceBundle.getBundle(bundle);
    java.util.Enumeration iter = bun.getKeys();

    while (iter.hasMoreElements()) {
        String value = (String)iter.nextElement();
        String label = (String)bun.handleGetObject(value);
        b.setValue(value);
        b.setLabel(label);
    }

%>
