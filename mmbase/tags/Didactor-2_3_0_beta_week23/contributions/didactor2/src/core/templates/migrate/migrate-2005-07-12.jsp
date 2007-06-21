<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/wizards/roles_defs.jsp" %>

<% 
  String editcontextName = "";
  String editcontextID = "";
  String constraints = "";  
  String allEditcontexts = "";  
%>

<% editcontextName = "cursuseditor"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "componenten"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "rollen"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "competentie"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "metadata"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "contentelementen"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "filemanagement"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "toetsen"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "opleidingen"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>
<% editcontextName = "docent schermen"; %>
<%@include file="migrate-2005-07-12p1.jsp" %>



<% constraints = "roles.name='student'"; %>
<%@include file="migrate-2005-07-12p2.jsp" %>
<mm:list path="roles" constraints="<%= constraints %>">
   <mm:import id="roleID" reset="true"><mm:field name="roles.number"/></mm:import>
   <mm:createrelation role="posrel" source="roleID" destination="ec_cursuseditor">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_componenten">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_rollen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_competentie">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_metadata">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_contentelementen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_filemanagement">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_toetsen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_opleidingen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_docent_schermen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
</mm:list>
<% constraints = "roles.name='coach'"; %>
<%@include file="migrate-2005-07-12p2.jsp" %>
<mm:list path="roles" constraints="<%= constraints %>">
   <mm:import id="roleID" reset="true"><mm:field name="roles.number"/></mm:import>
   <mm:createrelation role="posrel" source="roleID" destination="ec_cursuseditor">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_componenten">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_rollen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_competentie">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_metadata">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_contentelementen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_filemanagement">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_toetsen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_opleidingen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_docent_schermen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
</mm:list>
<% constraints = "roles.name='docent'"; %>
<%@include file="migrate-2005-07-12p2.jsp" %>
<mm:list path="roles" constraints="<%= constraints %>">
   <mm:import id="roleID" reset="true"><mm:field name="roles.number"/></mm:import>
   <mm:createrelation role="posrel" source="roleID" destination="ec_cursuseditor">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_componenten">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_rollen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_competentie">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_metadata">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_contentelementen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_filemanagement">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_toetsen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_opleidingen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_docent_schermen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RW"/></mm:setfield>
   </mm:createrelation> 
</mm:list>
<% constraints = "roles.name='contenteditor'"; %>
<%@include file="migrate-2005-07-12p2.jsp" %>
<mm:list path="roles" constraints="<%= constraints %>">
   <mm:import id="roleID" reset="true"><mm:field name="roles.number"/></mm:import>
   <mm:createrelation role="posrel" source="roleID" destination="ec_cursuseditor">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_componenten">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_rollen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_competentie">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_metadata">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_contentelementen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_filemanagement">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_toetsen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_opleidingen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_docent_schermen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_NO"/></mm:setfield>
   </mm:createrelation> 
</mm:list>
<% constraints = "roles.name='systemadministrator'"; %>
<%@include file="migrate-2005-07-12p2.jsp" %>
<mm:list path="roles" constraints="<%= constraints %>">
   <mm:import id="roleID" reset="true"><mm:field name="roles.number"/></mm:import>
   <mm:createrelation role="posrel" source="roleID" destination="ec_cursuseditor">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_componenten">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_rollen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_competentie">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_metadata">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_contentelementen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_filemanagement">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_toetsen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_opleidingen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
   <mm:createrelation role="posrel" source="roleID" destination="ec_docent_schermen">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
</mm:list>
<% constraints = "roles.name='filemanager'"; %>
<%@include file="migrate-2005-07-12p2.jsp" %>
<mm:list path="roles" constraints="<%= constraints %>">
   <mm:import id="roleID" reset="true"><mm:field name="roles.number"/></mm:import>
   <mm:createrelation role="posrel" source="roleID" destination="ec_filemanagement">
      <mm:setfield name="pos"><mm:write referid="RIGHTS_RWD"/></mm:setfield>
   </mm:createrelation> 
</mm:list>

done.
</mm:cloud>
</mm:content>

