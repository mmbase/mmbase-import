<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@tag import="java.util.*"%>
<table style="width: 300px; overflow: hidden;">
    <tr>
        <th><h3>env</h3></th>
    </tr>
    <tr>
        <td>
            <table>
                <tr>
                    <th colspan="2"><b>session</b></th>
                </tr>
                <%
                     Enumeration e = jspContext.getAttributeNamesInScope(PageContext.SESSION_SCOPE);
                    jspContext.setAttribute("_e",e);
                %>
                <c:forEach items="${_e}" var="name">
                    <tr>
                        <td>${name}</td>
                        <td><%String name = (String)jspContext.getAttribute("name");
                            Object value=(Object)jspContext.findAttribute(name);
                            out.write(value.toString());%></td>
                    </tr>
                </c:forEach>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <table>
                <tr>
                    <th colspan="2"><b>request</b></th>
                </tr>
                <%
                     e = jspContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE);
                    jspContext.setAttribute("_e",e);
                %>
                <c:forEach items="${_e}" var="name">
                    <tr>
                        <td>${name}</td>
                        <td><%String name = (String)jspContext.getAttribute("name");
                            Object value=(Object)jspContext.findAttribute(name);
                            out.write(value.toString());%></td>
                    </tr>
                </c:forEach>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <table>
                <tr>
                    <th colspan="2"><b>page</b></th>
                </tr>
                <%
                     e = jspContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);
                    jspContext.setAttribute("_e",e);
                %>
                <c:forEach items="${_e}" var="name">
                    <tr>
                        <td>${name}</td>
                        <td><%String name = (String)jspContext.getAttribute("name");
                            Object value=(Object)jspContext.findAttribute(name);
                            out.write(value.toString());%></td>
                    </tr>
                </c:forEach>
            </table>
        </td>
    </tr>
</table>
