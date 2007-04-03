<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/getstyle.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<mm:node number="<%=paginaID%>">
   <mm:related path="contentrel,persoon" orderby="contentrel.pos,persoon.firstname" fields="persoon.number">
   <mm:node element="persoon">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
   <tr>
      <td style="vertical-align:top">
         <table width="100%" cellpadding="0" cellspacing="0" border="0">
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
              	    <td><mm:field name="titel_de" /></td>
         	    </tr>
         	    </mm:isnotempty>
      	    </mm:field>
      	 </table>
          <mm:field name="omschrijving">
             <mm:isnotempty>
            	 <table width="100%" cellpadding="0" cellspacing="0" border="0">
            	    <tr>
            	       <td>
            	          <a href="people.jsp?p=<%= paginaID %>&pers=<mm:field name="number" />" class="maincolor_link">
                              <mm:field name="titel_fra">
                                 <mm:isnotempty>
                                    <mm:write/>
                                 </mm:isnotempty>
                                 <mm:isempty>
                                    <mm:field name="firstname"/> aan het woord
                                 </mm:isempty>
                              </mm:field>
                           </a>
            	       </td>
            	    </tr>
            	 </table>
      	    </mm:isnotempty>
   	    </mm:field>
      </td>
      <td style="text-align:right;padding-left:10px;vertical-align:top">
   	   <%@include file="image.jsp" %>
      </td>
   </tr>
   </table>   	 	   
   </mm:node>
   <mm:last inverse="true"><table class="dotline"><tr><td height="3"></td></tr></table></mm:last>
   </mm:related>
</mm:node>   
</mm:cloud>
