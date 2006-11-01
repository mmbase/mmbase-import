<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>  
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<jsp:directive.page import="java.util.*,nl.didactor.component.education.utils.*" />
<jsp:scriptlet>
  String imageName = "";
  String sAltText = "";
</jsp:scriptlet>
<mm:cloud rank="basic user">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="../mode.include.jsp" />
  <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
   <% //----------------------- Metadata comes from here ----------------------- %>
   <mm:import id="editcontextname" reset="true">metadata</mm:import>
   <%@include file="/education/wizards/roles_chk.jsp" %>
   <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
     <mm:treeinclude page="/metadata/tree/tree.jsp" objectlist="$includePath" referids="$referids">
       <mm:param name="locale"><%= pageContext.getAttribute("t_locale") %></mm:param>
       <mm:param name="wizardjsp"><mm:write referid="wizardjsp"/></mm:param>
       <mm:param name="listjsp"><mm:write referid="listjsp"/></mm:param>
     </mm:treeinclude>
   </mm:islessthan>
</mm:cloud>

