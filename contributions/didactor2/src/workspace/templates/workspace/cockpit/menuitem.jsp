<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- 'my documents and shared documents' is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
    <div class="menuSeperator"> </div>
    <div class="menuItem" id="menuWorkspace">
      <a href="<mm:treefile page="/workspace/index.jsp?typeof=1" objectlist="$includePath" referids="$referids" />" class="menubar"><fmt:message key="MYDOCUMENTSMENUITEM" /></a>
    </div>
    <div class="menuSeperator"> </div>
    <div class="menuItem" id="menuWorkspace">
      <a href="<mm:treefile page="/workspace/index.jsp?typeof=2" objectlist="$includePath" referids="$referids" />" class="menubar"><fmt:message key="SHAREDDOCUMENTSMENUITEM" /></a>
    </div>
   <mm:node number="$user" notfound="skip">
        <mm:relatednodes type="workgroups" max="1">
        <div class="menuSeperator"> </div>
        <div class="menuItem" id="menuWorkspace">
          <a href="<mm:treefile page="/workspace/index.jsp?typeof=3" objectlist="$includePath" referids="$referids" />" class="menubar"><fmt:message key="WORKGROUPDOCUMENTSMENUITEM" /></a>
        </div>
        </mm:relatednodes>
    </mm:node>
</mm:compare>

</fmt:bundle>
</mm:cloud>

