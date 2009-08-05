<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<mm:node referid="pers">
   <mm:field name="firstname">
      <mm:import id="pers_first_name" reset="true"><mm:write/></mm:import>
   </mm:field>
   <span class="colortitle">
   <mm:field name="titel_fra">
      <mm:isnotempty>
         <mm:write/>
      </mm:isnotempty>
      <mm:isempty>
         <mm:write referid="pers_first_name"/> aan het woord
      </mm:isempty>
   </mm:field>
   </span>
   <br/>
   <mm:field name="omschrijving"/>
   <table class="dotline"><tr><td height="3"></td></tr></table>
</mm:node>
</mm:cloud>
