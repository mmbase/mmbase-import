<%@ tag body-content="empty"  %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="nodenr" required="true" description="the root node"  %>
<%@ attribute name="desttype" required="true" description="the type of the nodes you want to check the posrels of"  %>
<%@ attribute name="var" required="true" type="java.lang.String" rtexprvalue="false" description="a variable of this name will be created to store the highest posrel number" %>
<%@ variable name-from-attribute="var"  alias="varname" scope="AT_END"%>
<%--
    this tag finds the highest posrel number that connects a node and some other
    type of node
--%>
<mm:cloud >
    <mm:node number="${nodenr}">
        <mm:relatednodes path="posrel,${desttype}" element="posrel" orderby="posrel.pos" directions="down"  max="1">
            <c:set var="varname"><mm:field name="pos" /></c:set>
        </mm:relatednodes>
    </mm:node>
</mm:cloud>
