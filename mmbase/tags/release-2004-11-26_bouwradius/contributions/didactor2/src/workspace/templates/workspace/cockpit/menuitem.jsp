<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- 'my documents and shared documents' is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
  <mm:compare referid="type" value="div">
    <div class="menuSeperator"> </div>
    <div class="menuItem" id="menuWorkspace">
      <a href="<mm:treefile page="/workspace/index.jsp?typeof=1" objectlist="$includePath" referids="$referids" />" class="menubar"><fmt:message key="MYDOCUMENTSMENUITEM" /></a>
    </div>
    <div class="menuSeperator"> </div>
    <div class="menuItem" id="menuWorkspace">
      <a href="<mm:treefile page="/workspace/index.jsp?typeof=2" objectlist="$includePath" referids="$referids" />" class="menubar"><fmt:message key="SHAREDDOCUMENTSMENUITEM" /></a>
    </div>
  </mm:compare>
  <mm:compare referid="type" value="option">
    <option value="<mm:treefile page="/workspace/index.jsp?typeof=1" objectlist="$includePath" referids="$referids" />" class="menubar">
      <fmt:message key="MYDOCUMENTSMENUITEM" />
    </option>
    <option value="<mm:treefile page="/workspace/index.jsp?typeof=2" objectlist="$includePath" referids="$referids" />" class="menubar">
      <fmt:message key="SHAREDDOCUMENTSMENUITEM" />
    </option>
  </mm:compare>
  </fmt:bundle>
  </mm:cloud>
</mm:compare>