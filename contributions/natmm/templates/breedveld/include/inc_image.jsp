<%-- the following variables should be defined in the context:
    thisImage, thisImageLayout, previousImage, nextImage, windowWidth, pageHref, languageConstraint --%>

<%  int marginWidth = (windowWidth/4); 
    int marginHeight = (windowWidth/40);
    int buttonHeight = (8*windowWidth)/16;
    int imageSpaceWidth =  windowWidth-(2*marginWidth);
    if(thisImageLayout.equals("Staand")) {  imageSpaceWidth = windowWidth-((11*marginWidth)/4); }
    // make imageWidth the highest multiple of 100 <= imageSpaceWidth
    int imageWidth = 100*(imageSpaceWidth/100);
%>

<!-- <%= imageSpaceWidth %>  -> <%= imageWidth %> -->
<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0" ALIGN="center">
  <tr>
    <td class="background" colspan="3"><img src="media/spacer.gif" width="1" height="<%= marginHeight %>" border="0"></td>
  </tr>
  <tr>
    <td align="right" valign="top" rowspan="2">
            <img src="media/spacer.gif" width="<%= marginWidth %>" height="<%= buttonHeight %>" border="0"><br>
            <% if(!previousImage.equals("-1")) { %>
            <A onmouseover="changeImages('pijl_previous', 'media/double_arrow_left_dr.gif'); window.status=''; return true;"
                 onmouseout="changeImages('pijl_previous', 'media/double_arrow_left_dg.gif'); window.status=''; return true;"
                 class="light_boldlink" href="<%= pageHref %><%= previousImage %>">
              <IMG src="media/double_arrow_left_dg.gif" width=16 height=12 hspace=4 align=absMiddle border=0 name=pijl_previous title='<%= lan(language,"vorige") %>'>
            </A>
            <% } %>
    </td>
    <td align="center" valign="center">
        <mm:node number="<%= thisImage %>">
            <% String imageTemplate = "s(" + imageWidth + ")"; %>
            <img src=<mm:image template="<%= imageTemplate %>" /> border="0"><br><br><br>
        </mm:node>
        <img src="media/spacer.gif" width="<%= imageSpaceWidth %>" height="1"  border="0"><br>
        <% boolean hasComment = false; %>
        <mm:list nodes="<%= thisImage %>" path="images,posrel,items"
            fields="items.titel,items.number,items.year,items.piecesize,items.material">
            <span class="bold">
            <mm:field name="items.titel" jspvar="piece_title" vartype="String" write="false">
                <% if(piece_title.indexOf("Zonder titel")>-1) { %>
                    <%= lan(language,"Zonder titel") %> <%= piece_title.substring(12) %><br>
                <% } else { %>
                    <%= piece_title %><br>
                <% } %>
            </mm:field>
            <mm:field name="items.piecesize" /> cm<br>          
            <mm:field name="items.material" jspvar="piece_material" vartype="String" write="false">
                <% if(piece_material.indexOf("olieverf op linnen")>-1) { %>
                        <%= lan(language,"olieverf op linnen") %><br>
                <% } else if(piece_material.indexOf("tempera op papier")>-1) { %>
                        <%= lan(language,"tempera op papier") %><br>
                <% } else { %>
                        <%= piece_material %><br>
                <% } %>
            </mm:field>
        </span>
        <% hasComment = true; %>
        </mm:list>
        <% if(!hasComment) { %>
        <mm:list nodes="<%= thisImage %>" path="images,posrel,projects,posrel,projecttypes">
                <span class="bold">
                <% String project_type = ""; %>
                <mm:field name="projecttypes.naam" jspvar="dummy05" vartype="String" write="false">
                   <% project_type = dummy05; %>
                </mm:field>
					 <mm:node element="projects">
                <%@include file="../include/project_titel.jsp" %> 
                <% String project_embargo = ""; %>
	                <mm:field name="embargo" jspvar="dummy08" vartype="String" write="false">
                    <% project_embargo = dummy08; %>
   	             </mm:field>
                <% String project_verloopdatum = ""; %>
      	          <mm:field name="verloopdatum" jspvar="dummy09" vartype="String" write="false">
                    <% project_verloopdatum = dummy09; %>
         	       </mm:field>
                <%= lan(language,project_type) %><br>
                <%= project_title %><br>
                <% if(project_type.indexOf("tentoonstelling")>0) { %>
                    <% timeStamp = project_embargo; %>
                    <%@ include file="inc_date.jsp" %> 
                    <%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %>
                    <% timeStamp = project_verloopdatum; %>
                    <%@ include file="inc_date.jsp" %> 
                    <%= lan(language,"t/m") %> <%= thisDay %>&nbsp;<%= monthsStr[thisMonth] %>&nbsp;<%= thisYear %>
                <% } else { %>
                    <% timeStamp = project_embargo; %>
                    <%@ include file="inc_date.jsp" %> 
                    <% int fromYear = thisYear; %>      
                    <% timeStamp = project_verloopdatum; %>
                    <%@ include file="inc_date.jsp" %> 
                    <% int toYear = thisYear; %>
                    <%= fromYear %>
                    <% if( fromYear < toYear) { %>
                            - <%= toYear %>
                    <% } %>
                <% } %>
					 </mm:node>
                </span>
                <br><br>
        <% hasComment = true; %>
        </mm:list>
        <% } %>
        <% if(!hasComment) { %>
            <mm:list nodes="<%= thisImage %>" path="images,posrel,organisatie,contentrel,pagina" 
                fields="organisatie.naam" constraints="pagina.titel='Links'">
                <span class="bold">
                    <mm:field name="organisatie.naam"/>
                </span>
                <br><br>
            </mm:list>
        <% } %>
    </td>
    <td align="left" valign="top" rowspan="2">
            <img src="media/spacer.gif" width="<%= marginWidth %>" height="<%= buttonHeight %>"  border="0"><br>
            <% if(!nextImage.equals("-1")) { %>
            <A onmouseover="changeImages('pijl_next', 'media/double_arrow_right_dr.gif'); window.status=''; return true;"
                   onmouseout="changeImages('pijl_next', 'media/double_arrow_right_dg.gif'); window.status=''; return true;"
                   class="light_boldlink" href="<%= pageHref %><%= nextImage %>">
             <IMG src="media/double_arrow_right_dg.gif"  height=12 hspace=4 width=16 align=absMiddle border=0 name=pijl_next title='<%= lan(language,"volgende") %>'>
            </A>
            <% } %>
    </td>
  </TR>
  <tr>
  <td align="center" class="background">
    <A class="light_boldlink" HREF="javascript:self.close()" onClick="self.close(); return false;">
        <%= lan(language,"sluit dit venster") %>
    </A>
  </td>
  </tr>
</TABLE>
