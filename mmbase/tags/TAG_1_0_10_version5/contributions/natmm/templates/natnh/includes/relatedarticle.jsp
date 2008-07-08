<mm:node element="artikel">
	<%@include file="newsteaser.jsp" %>

   <mm:field name="titel_zichtbaar"
      ><mm:compare value="0" inverse="true"
        ><div class="pageheader" style="margin-top:10px;"><mm:field name="titel"/></div
      ></mm:compare
   ></mm:field>

	<table width="100%">
   <tr>
      <td>
	<%@include file="image_logic.jsp" %>
  <b><mm:field name="intro"/></b><br/>
	<mm:field name="tekst"/><br/>
	  </td>
	</tr>
	</table>
	<mm:related path="posrel,paragraaf"
			  orderby="posrel.pos" directions="UP"
			  fields="paragraaf.number,paragraaf.titel,paragraaf.omschrijving">

         <mm:field name="paragraaf.titel_zichtbaar"
               ><mm:compare value="0" inverse="true"
                 ><div class="pageheader" style="margin-top:10px;"><mm:field name="paragraaf.titel" /></div
               ></mm:compare
            ></mm:field
            >
		<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tr>
			<td>
			<mm:node element="paragraaf">
				<%@include file="image_logic.jsp" %>
			</mm:node>
			<mm:field name="paragraaf.number" jspvar="paragraaf_number" vartype="String" write="false">
            <mm:field name="paragraaf.tekst" 
				 /><mm:node number="<%= paragraaf_number %>" 
					  ><%@include file="attachment.jsp" 
				 %></mm:node
				 ><mm:list nodes="<%= paragraaf_number %>" path="paragraaf,posrel,link" 
					  ><mm:field name="link.omschrijving" />
					  <a target="_blank" href="<mm:field name="link.url" />"><mm:field name="link.titel" /></a>
				  </mm:list
				 ><br/>
			 </mm:field>
			 </td>
		</tr>
		</table>
	</mm:related
></mm:node>