<table cellpadding="0" cellspacing="0" border="0">
	<mm:list nodes="<%= paginaID %>" path="pagina,posrel,attachments" fields="attachments.number"
		orderby="posrel.pos,attachments.title" directions="UP,UP" offset="<%= "" + thisOffset*10 %>" max="10"
	><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"><%	
         if(posrel_pos.equals("")) posrel_pos = "1";
			if(((Integer.parseInt(posrel_pos)-1) % numberOfColumns) == (colNumber-1)){ 
			%><mm:node element="attachments">
         <tr>
            <td style="width:60px;padding-left:10px;">
               <mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false">
               <a href="<mm:attachment />" target="_blank">
                  <% if(attachments_filename.indexOf(".pdf")>-1){ 
                     %><img src="media/pdf.gif" alt="<mm:field name="title" />" border="0"><%
                  } else if(attachments_filename.indexOf(".doc")>-1){ 
                     %><img src="media/word.gif" alt="<mm:field name="title" />" border="0"><%
                  } else if(attachments_filename.indexOf(".xls")>-1){
                     %><img src="media/xls.gif" alt="<mm:field name="title" />" border="0"><%
                  } else if(attachments_filename.indexOf(".ppt")>-1){ 
                     %><img src="media/ppt.gif" alt="<mm:field name="title" />" border="0"><%
                  } else { 
                     %><img src="media/txt.gif" alt="<mm:field name="title" />" border="0"><%
                  } %>
               </a>
               </mm:field>
            </td>
            <td style="padding-bottom:10px;">
               <strong><mm:field name="title" /></strong><br/>
               <span class="black"><mm:field name="description" /></span>
            </td>
         </tr>
			</mm:node
	   ><% } 
	%></mm:field
	></mm:list
></table>
