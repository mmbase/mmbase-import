<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<mm:import externid="oldPosrel" required="true"/>
<mm:import externid="newContainer" required="true"/>
<mm:import externid="nodenr" required="true"/>

<mm:cloud method="asis" loginpage="/edit/login.jsp">
    <mm:node number="${nodenr}" id="nodenrid"/>
    <mm:node number="${newContainer}" id="newContainerid"/>
    <mm:maycreaterelation role="posrel" source="newContainerid" destination="nodenrid">
        <mm:deletenode number="$oldPosrel"/>

        <mm:createrelation role="posrel" source="newContainerid" destination="nodenrid" >
            <mm:setfield name="pos">-1</mm:setfield>
        </mm:createrelation>

        <mm:listnodes nodes="$newContainer" path="object,posrel,categories"  orderby="posrel.pos" element="posrel">
            <mm:setfield name="pos"><mm:index/></mm:setfield>
        </mm:listnodes>

        <c:redirect url="${cookie.referer.value}" context="/" />
    </mm:maycreaterelation>

    <mm:maycreaterelation role="posrel" source="newContainerid" destination="nodenrid" inverse="true">
        <% response.sendRedirect("/edit/system/error.jsp?error=2"); %>
    </mm:maycreaterelation>
</mm:cloud>