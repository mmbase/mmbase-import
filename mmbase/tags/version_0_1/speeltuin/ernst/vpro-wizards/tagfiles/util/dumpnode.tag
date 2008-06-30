<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@ tag body-content="empty"  %>
<%@ attribute name="nodenr" required="true"  %>
<%@ attribute name="headerwidth"  %>

<%--default value--%>
<c:if test="${empty headerwidth}">
    <c:set var="headerwidth" value="100" />
</c:if>

<mm:cloud method="asis">
    <mm:node number="${nodenr}" id="n">
        <mm:fieldlist type="all" >
             <mm:fieldinfo type="guiname">
                <c:if test="${_ != 'Object' && _ != 'Type'}">
                    <div style="float: left; width: ${headerwidth}px; clear:left">
                        <b>${_}</b>
                    </div>
                    <div style="float:left"><mm:fieldinfo type="value"/></div>
                </c:if>
            </mm:fieldinfo>
        </mm:fieldlist>
    </mm:node>
</mm:cloud>

