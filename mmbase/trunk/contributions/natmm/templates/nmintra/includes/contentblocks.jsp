<% 
int numOfColum = 3;
int maxColumnsCntr = numOfColum;
int lastColumnNum = 0;
%><mm:related path="readmore,contentblocks" orderby="readmore.pos" directions="UP" searchdir="destination">
   <mm:first>
      <table border="0" cellpadding="0" cellspacing="0" width="100%">
   	<tr><td width="33%"></td>
   		<td width="33%"></td>
   		<td width="34%"></td>
   	</tr>
   	<tr><td colspan="3" height="1" style="padding-left:10px"><img src="media/1x1green.gif" height="1" width="100%"></td></tr>
   	<tr>
   </mm:first>
   <%	
   	int colNum = 0;
   	%><mm:field name="readmore.readmore" jspvar="dummy" vartype="String" write="false"><% 
   	   try {
   	      colNum = Integer.parseInt(dummy);
   	   } catch(Exception e) {
   		   colNum = 1; 
   		}
   	%></mm:field><%
   	boolean isWarning = false;
   	lastColumnNum = numOfColum-maxColumnsCntr;
   	if (maxColumnsCntr==0) { // *** maxColumnsCntr is reset, start newline
   	   %></tr><tr><%
   	   maxColumnsCntr = numOfColum;
   	   lastColumnNum = 0;
   	}
      // *** make this block fit on present line
   	if (maxColumnsCntr-colNum>=0) {
   		maxColumnsCntr -= colNum; 
   	} else { 
   	   colNum = maxColumnsCntr;
   		maxColumnsCntr = 0;
   		isWarning = true;
   	}
   	%>
   	<td style="padding:10px;" colspan="<%= colNum %>">
      	<mm:node element="contentblocks">
                <%
                String styleClass = "black";
                String styleClassDark = "dark";
                %>
                <%@include file="../includes/contentblockdetails.jsp" %>
             </mm:node>
      	<% if (isWarning) { 
            %><p>Waarschuwing: blok <b><mm:field name="contentblocks.title"/></b> start op kolom <%= lastColumnNum %>
      			en zou <mm:field name="readmore.readmore" /> kolommen breed moeten zijn. Dit is niet mogelijk, daarom zal dit blok worden getoond
      			vanaf kolom <%= lastColumnNum %> tot en met de derde kolom.</p>
      	<% } %>
      </td>
   <mm:last>
      	<%
         if (maxColumnsCntr>0) { 
            %>
      		<td colspan="<%= maxColumnsCntr %>">&nbsp;</td>
      	<% } %>
      	</tr>
      </table>
   </mm:last>
</mm:related>