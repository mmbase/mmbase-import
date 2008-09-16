<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/getstyle.jsp" %>
<mm:cloud jspvar="cloud">
<mm:node referid="pers">
   <%@include file="image.jsp" %>
   <table style="width:100%;margin-top:11px;" cellpadding="0" cellspacing="0" border="0">
   <tr>
      <td style="width:10px;vertical-align:top"><strong>Naam</strong></td>
      <td style="width:3px;vertical-align:top">&nbsp;&nbsp;|&nbsp;&nbsp;</td>
      <td><mm:field name="titel" /></td>
   </tr>
   <mm:field name="titel_de">
      <mm:isnotempty>	 
      <tr>
         <td style="width:10px;vertical-align:top"><strong>Citaat</strong></td>
         <td style="width:3px;vertical-align:top">&nbsp;&nbsp;|&nbsp;&nbsp;</td>
         <td><mm:write /></td>
      </tr>
      </mm:isnotempty>
   </mm:field>
   </table>
</mm:node>
</mm:cloud>
