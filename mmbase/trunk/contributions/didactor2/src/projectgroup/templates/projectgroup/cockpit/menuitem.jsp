<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
    <mm:node referid="education">
        <mm:relatednodes type="components" max="1" constraints="components.name='projectgroup'">
         <div class="menuSeperator"> </div>
        <div class="menuItem" id="menuWorkspace">
          <a href="<mm:treefile page="/projectgroup/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><fmt:message key="PROJECTGROUPS" /></a>
        </div>
            
        </mm:relatednodes>
    </mm:node>

</fmt:bundle>
</mm:cloud>

