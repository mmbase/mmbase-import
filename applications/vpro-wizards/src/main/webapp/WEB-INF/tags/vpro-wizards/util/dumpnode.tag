<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@ tag body-content="empty"  %>
<%@ attribute name="nodenr" required="true"  %>
<%@ attribute name="headerwidth"  %>
<%@ attribute name="gui" description="wanneer dit 'true' is, wordt de gui functie aangeroepen op het veld. Default is 'false'"   %>

<%--default value--%>
<c:if test="${empty headerwidth}">
    <c:set var="headerwidth" value="100" />
</c:if>

<mm:cloud method="asis">
    <mm:node number="${nodenr}" id="n">
        <mm:fieldlist type="all" >
             <mm:fieldinfo type="name">
                <c:if test="${_ != 'number' && _ != 'otype' && _ != 'snumber' && _ != 'dnumber' && _ != 'rnumber' && _ != 'owner' && _ != 'dir'}">
                    <div style="float: left; width: ${headerwidth}px; clear:left">
                        <b><mm:field name="${_}" ><mm:fieldinfo type="guiname"/></mm:field></b>
                    </div>
                    <div style="float:left">
                        <c:choose>
                            <c:when test="${gui == 'true'}">
                                <mm:functioncontainer name="gui" >
                                    <mm:param name="field" value="${_}" />
                                    <mm:function />
                                </mm:functioncontainer>
                            </c:when>
                            <c:otherwise> <mm:fieldinfo type="value"/> </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </mm:fieldinfo>
        </mm:fieldlist>
    </mm:node>
</mm:cloud>

