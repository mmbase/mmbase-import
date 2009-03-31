<%

// <option id="2">klein links</option>
// <option id="3">klein rechts</option>
// <option id="8">medium links</option>
// <option id="9">medium rechts</option>
// <option id="4">groot boven artikel</option>

%><mm:related path="posrel,images"  max="1"
	><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="Integer" write="false"><%
	
		 int image_position = 3;
		 String imageTemplate ="";
		 try { image_position = posrel_pos.intValue(); } catch (Exception e) { } 

		 if(image_position==4) { // large image, no spacer between table and text
			  imageTemplate = "+s(500)";
			 %><table cellspacing="0" cellpadding="0" border="0" align="center">
			     <tr><td colspan="3"><img src="media/spacer.gif" alt="" border="0" width="1" height="10px"></td></tr>
				  <tr><td colspan="3" class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
				  <tr><td class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td>
						<td><img src="<%@include file="../includes/imagessource.jsp" %>" alt="" border="0"></td>
						<td class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
				  <tr><td colspan="3" class="black"><img src="media/spacer.gif" alt="" border="0" width="1" height="1"></td></tr>
				  <tr><td colspan="3"><img src="media/spacer.gif" alt="" border="0" width="1" height="10px"></td></tr>
				  </table><br><%
				  
		 } else { // medium or small image, spacer between table and text
		 
			  if((1<image_position)&&(image_position<4)) { imageTemplate = "+s(150)"; }
			  if((7<image_position)&&(image_position<10)) { imageTemplate = "+s(300)"; }
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
				  		<%-- NMCMS-639 --%>
						<td><a href="#" onClick="javascript:launchCenter('<%= requestURL %>slideshow.jsp?r=<%= rubriekId 
								  %>&p=<%= paginaID %>&i=<mm:field name="images.number" />', 'center', 550, 740,'resizable=yes,scrollbars=yes'); setTimeout('newwin.focus();',250); return false;">
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
></mm:related>
