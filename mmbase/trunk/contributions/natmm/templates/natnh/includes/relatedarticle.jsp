<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false"
><p><mm:field name="artikel.titel_zichtbaar"
	   ><mm:compare value="0" inverse="true"
    	  ><div class="pageheader"
		  	><mm:field name="artikel.titel"
	   	 /></div
		></mm:compare
	></mm:field
><mm:field name="artikel.intro"
/></p>
<mm:list nodes="<%= artikel_number %>" path="artikel,posrel,paragraaf"
        orderby="posrel.pos" directions="UP"
        fields="paragraaf.number,paragraaf.titel,paragraaf.omschrijving"
><table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr><td><p><mm:field name="paragraaf.number" jspvar="paragraaf_number" vartype="String" write="false"
        ><% // see the types/images_position at the editwizards
            //  <option id="1">rechts</option>
            //  <option id="2">links</option>
            //  <option id="3">rechts klein</option>
            //  <option id="4">links klein</option>
            //  <option id="5">rechts medium</option>
            //  <option id="6">links medium</option>
            //  <option id="7">volle breedte</option>
        %><mm:list nodes="<%= paragraaf_number %>" path="paragraaf,posrel,images"  max="1"
            ><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="Integer" write="false"><%
				
					 int image_position = 3;
					 String imageTemplate ="";
                try { image_position = posrel_pos.intValue(); } catch (Exception e) { } 

                if(image_position==7) { // large image, no spacer between table and text
                    imageTemplate = "+s(500)";
						 %><table cellspacing="0" cellpadding="0" border="0" align="center">
							  <tr><td colspan="3" class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
							  <tr><td class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td>
									<td><img src="<%@include file="../includes/imagessource.jsp" %>" alt="" border="0"></td>
									<td class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
							  <tr><td colspan="3" class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
							  <tr><td colspan="3"><img src="media/spacer.gif" alt="" border="0" width="1" height="5"></td></tr>
							  </table><br><%
							  
                } else { // medium or small image, spacer between table and text
					 
                    if((2<image_position)&&(image_position<5)) { imageTemplate = "+s(150)"; }
                    if((4<image_position)&&(image_position<7)) { imageTemplate = "+s(300)"; }
                    boolean rightAlign = false;
                    if((image_position%2)==1){  rightAlign = true; }
						 %><table cellspacing="0" cellpadding="0" border="0" width="1" <%
							  if(rightAlign){ %> align="right" <% } else { %> align="left" <% } %>>
							  <tr><td colspan="4"><img src="media/spacer.gif" alt="" border="0" width="1" height="4"></td></tr>
							  <tr><% if(rightAlign){ %><td rowspan="4"><img src="media/spacer.gif" alt="" border="0" width="10" height="1"></td><% } %>
									<td colspan="3" class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td>
									<% if(!rightAlign){ %><td rowspan="4"><img src="media/spacer.gif" alt="" border="0" width="10" height="1"></td><% } %>
							  </tr>
							  <tr><td class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td>
									<td><a href="#" onClick="javascript:launchCenter('<%= requestURL %>slideshow.jsp?r=<%= rubriekId 
											  %>&p=<%= paginaID %>&i=<mm:field name="images.number" />', 'center', 550, 740,'resizable=1'); setTimeout('newwin.focus();',250); return false;">
											  <img src="<%@include file="../includes/imagessource.jsp" %>" alt="" border="0"></a></td>
									<td class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
							  <tr><td colspan="3" class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
							  <mm:field name="images.titel_zichtbaar"
								><mm:compare value="0" inverse="true"
									><mm:field name="images.titel" jspvar="images_titel" vartype="String" write="false"
											><% if(images_titel!=null&&!images_titel.equals("")) {
											  %><tr><td colspan="3" class="fototitle"><%= images_titel %></td></tr><%
										  } %></mm:field
								></mm:compare
							></mm:field
						><tr><td colspan="3"><img src="media/spacer.gif" alt="" border="0" width="1" height="5"></td></tr>
                    </table><% 
              }
				  %>
		   </mm:field
    ></mm:list
    ><mm:first
        ><%@include file="newsteaser.jsp" 
    %></mm:first
    ><mm:field name="paragraaf.titel_zichtbaar"
	   ><mm:compare value="0" inverse="true"
    	  ><div class="pageheader"><mm:field name="paragraaf.titel" /></div
	   ></mm:compare
	></mm:field
	><mm:field name="paragraaf.tekst" 
    /><mm:node number="<%= paragraaf_number%>" 
        ><%@include file="attachment.jsp" 
    %></mm:node
    ><mm:list nodes="<%= paragraaf_number %>" path="paragraaf,posrel,link" 
        ><mm:field name="link.omschrijving" />
        <a target="_blank" href="<mm:field name="link.url" />"><mm:field name="link.titel" /></a>
     </mm:list
    ><mm:list nodes="<%= paragraaf_number %>" path="paragraaf,readmore,pagina" 
        fields="pagina.number,pagina.titel,readmore.readmore"
        ><mm:field name="readmore.readmore"
        /> <a href="index.jsp?p=<mm:field name="pagina.number" />" ><mm:field name="pagina.titel" /></a>
    </mm:list>
    <br>
</mm:field
></p></td></tr></table>
</mm:list
></mm:field
>