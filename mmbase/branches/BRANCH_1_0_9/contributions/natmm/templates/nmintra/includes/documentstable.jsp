<table cellpadding="0" cellspacing="0" border="0">
	<mm:list nodes="<%= paginaID %>" path="pagina,posrel,documents" fields="documents.filename"
		orderby="posrel.pos,posrel.number" directions="UP,DOWN" offset="<%= "" + thisOffset*pageSize %>" max="<%= "" +  pageSize %>"
	><mm:field name="posrel.pos" jspvar="posrel_pos" vartype="String" write="false"><%	
   if(posrel_pos==null || posrel_pos.equals("")) posrel_pos = "1";
   if(((Integer.parseInt(posrel_pos)-1) % numberOfColumns) == (colNumber-1)){
            %><mm:node element="documents" jspvar="thisDoc"><%
               String [] fileExtensions = { ".xls", ".doc", ".ppt",".pdf", ".txt" };
               String [] fileImages = { "media/icexcel.gif", "media/icword.gif", "media/icppt.gif","media/icpdf.gif", "media/ictxt.gif" };
               String sUrl = thisDoc.getStringValue("url");
               String sFileName = thisDoc.getStringValue("filename");
               %><tr>
                  <td style="width:60px;padding-left:10px;">
                  <% for(int i=0; i< fileExtensions.length; i++) {
                        if(sFileName.toLowerCase().indexOf(fileExtensions[i])>-1) {
                           int dPos = sUrl.lastIndexOf(".");
                           String siblingUrl = sUrl.substring(0,dPos) + fileExtensions[i];
                           String siblingFileName = siblingUrl.substring(siblingUrl.lastIndexOf("/")+1);
                           %><a href="<%= siblingUrl %>" target="_blank">
                               <img src="<%= fileImages[i] %>" alt="<%= siblingFileName %>" border="0">
                             </a><%
                        }
                     }
                  %>
                  </td>
                  <td style="padding-bottom:10px;">
                  <% for(int i=0; i< fileExtensions.length; i++) {
                        if(sFileName.toLowerCase().indexOf(fileExtensions[i])>-1) {
                           int dPos = sUrl.lastIndexOf(".");
                           String siblingUrl = sUrl.substring(0,dPos) + fileExtensions[i];
                           String siblingFileName = siblingUrl.substring(siblingUrl.lastIndexOf("/")+1);
                           %><a href="<%= siblingUrl %>" target="_blank"><%= siblingFileName %></a><br/><%
                        }
                     }
                  %>
               </td>
            </tr>
   			</mm:node><% 
   } 
	%></mm:field
	></mm:list
></table>
