<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:node referid="education">
    <mm:field name="number" jspvar="educationNo">
    <mm:relatednodes type="licensetexts" max="1">
    <% 
        if (session.getAttribute(username+"_has_agreed_to_education_licence_"+educationNo) == null) {
        %>
            <mm:redirect page="/drm/showlicense.jsp" referids="$referids "/>
            <%
        }
    %>
    </mm:relatednodes>
</mm:field>
</mm:node>
</mm:cloud>
</mm:content>
