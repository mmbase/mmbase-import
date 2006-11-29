<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<%@include file="../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String sTeaserLayout = request.getParameter("tl");
   if(sTeaserLayout==null||sTeaserLayout.equals("")) { sTeaserLayout = "default"; }
   String sID = request.getParameter("s");
   String shortyRol = request.getParameter("sr");
   int maxShorties = 20;
   imgFormat = "shorty";
   PaginaHelper ph = new PaginaHelper(cloud);
%>
<%@include file="../includes/getstyle.jsp" %>
<mm:import externid="teasersbypool">false</mm:import>
<mm:compare referid="teasersbypool" value="false">
    <mm:remove referid="teasersbypool" />
</mm:compare>
<mm:import id="showteaser"/>
<mm:import id="divstyle"></mm:import>
<%@include file="../includes/shorty_logic_1.jsp" %>
<table width="100%" cellspacing="<%= (sTeaserLayout.equals("button") ? "1" : "0" ) %>"  cellpadding="<%= (sTeaserLayout.equals("button") ? "1" : "0" ) %>" border="0">
<tr>
<%
int numOfColum = 2; // *** maximum number of columns in row
int maxColumnsCntr = numOfColum;
int lastColumnNum = 0;

for (int i =0; i<shortyCnt;i++){

	%><%@include file="../includes/shorty_logic_2.jsp" %>
	<mm:node number="<%= shortyID[i]%>">
		<mm:field name="size" write="false" jspvar="teaser_size" vartype="String">
		<mm:field name="titel" write="false" jspvar="teaser_titel" vartype="String">
		<mm:field name="titel_zichtbaar" write="false" jspvar="teaser_tz" vartype="String">
		<mm:field name="omschrijving" write="false" jspvar="teaser_omschrijving" vartype="String">
		<mm:field name="omschrijving_eng" write="false" jspvar="teaser_javascript" vartype="String">
		<mm:field name="reageer" write="false" jspvar="showline" vartype="String">
		<% 
   	if(teaser_size==null) teaser_size = "1";
	   int colNum = 0;
   	if(teaser_size.equals("0")||teaser_size.equals("2")){
         colNum = 1;
	   } else {
	      colNum = 2;
	   }
	   if(teaser_size.equals("0")){
	      imgFormat = "half_shorty";
	   } else if(teaser_size.equals("1")){
	      imgFormat = "shorty";
   	} else {
	      imgFormat = "orgsize_shorty";
	   } 
      boolean isWarning = false;
   	lastColumnNum = numOfColum-maxColumnsCntr;
   	if (maxColumnsCntr==0) { // *** maxColumnsCntr is reset, start newline
   	   %></tr><tr><%
   	   maxColumnsCntr = numOfColum;
   	   lastColumnNum = 0;
   	}
      // *** make this block fit on present line
   	if (maxColumnsCntr-colNum>=0) {
   		maxColumnsCntr-=colNum; 
   	} else { 
   	   colNum = maxColumnsCntr;
   		maxColumnsCntr = 0;
   		isWarning = true;
   	}
     %>
			<td style="vertical-align:top;<%= (sTeaserLayout.equals("button")&&(maxColumnsCntr == 0) ? "text-align:right;" : "" ) %>" colspan="<%= colNum %>">
     		<% 
			if(!sTeaserLayout.equals("button")) {
            %><%@include file="../includes/image_logic.jsp" %><%
            
            if(!teaser_titel.equals("")&&!teaser_tz.equals("0")){ 
               %><span class="colortitle"><%=teaser_titel%></span><br><%
            }
				
            String description = HtmlCleaner.cleanBRs(HtmlCleaner.cleanPs(teaser_omschrijving)).trim();
            if(!description.equals("")){
               if(sTeaserLayout.equals("asis")) {
                  %><%=teaser_omschrijving%><%
               } else {
                  %><%=description%><%
               }
            } 
            %><mm:import id="hrefclass">teaser</mm:import><%
            linkTXT = readmoreTXT; 
            %><%@include file="../includes/validlink.jsp" 
            %><mm:remove referid="hrefclass" 
            /><%= (teaser_javascript!=null && !teaser_javascript.trim().equals("") ? teaser_javascript : "" )
            %><%= (showline.equals("1") ? "<div class='rule' style='margin-top:6px;margin-bottom:3px;'></div>" : "" ) %><%
         } else {
            int iImageWidth = 354;
            if(teaser_size.equals("0")||teaser_size.equals("2")){
               iImageWidth = 165;
            }            
            
            %><table style="width:<%=iImageWidth+2%>px;margin-bottom:1px;" cellspacing="0"  cellpadding="0" border="0">
               <tr><td colspan="3" style="width:<%=iImageWidth+2%>px;"><table class="dotline"><tr><td style="height:3px;"></td></tr></table></td></tr>
               <tr><td colspan="3" style="width:<%=iImageWidth+2%>px;height:6px;text-align:right;"><%
                  if(validLink){ 
                     %><div style="position:relative;left:-20px;top:6px;"><%
								%><div style="visibility:visible;position:absolute;top:0px;left:0px;"><%
									%><a href="<%= readmoreURL %>"><img src="media/submit_<%= NatMMConfig.style1[iRubriekStyle] %>.gif" border="0" alt="<%= altTXT %>" /></a><%
								%></div><%
							%></div><%
                  }
                  %></td></tr>
               <tr><td colspan="3" class="maincolor" style="height:1px;"></td></tr>
               <tr><td class="maincolor" style="height:1px;width:1px;"></td>
                  <td style="width:<%= iImageWidth %>px;"><%
                  
                  if(validLink){ 
                     %><a href="<%= readmoreURL %>" <%= (!readmoreTarget.equals("") ? " target=\"" + readmoreTarget + "\"" : "" ) %>><%
                  }
                  %><mm:relatednodes path="posrel,images" max="1"
                     ><img src="<mm:image template="<%= "s(" + iImageWidth + "x106!)" %>" />" alt="<%= altTXT %>"  border="0"></mm:relatednodes><%                   
                  if(validLink){ 
                     %></a><% 
                  } 
                  
                  %></td><td class="maincolor" style="height:1px;width:1px;"></td></tr>
               <tr><td colspan="3" class="maincolor" style="height:1px;width:<%=iImageWidth+2%>px;"></td></tr>
               <tr><td colspan="3" style="height:1px;width:<%=iImageWidth+2%>px;"></td></tr>
               <tr><td colspan="3" class="maincolor" style="width:<%=iImageWidth+2%>px;padding-left:5px;padding-right:5px;padding-bottom:1px;">
                     <%
                     // *** capitalize first word and make it clickable
                     teaser_titel = teaser_titel.substring(0,1).toUpperCase() + teaser_titel.substring(1);
                     int sPos = teaser_titel.indexOf(" ");
                     String teaserSubstring = "";
                     if(sPos>0) {
                        teaserSubstring = teaser_titel.substring(sPos);
                        teaser_titel = teaser_titel.substring(0,sPos);
                     }
                     if(validLink){ 
                        %><a href="<%= readmoreURL %>" <%= (!readmoreTarget.equals("") ? " target=\"" + readmoreTarget + "\"" : "" ) %> style="font-weight:bold;color:#FFFFFF;" title="<%= altTXT %>"><%
                     }
                     %><%= teaser_titel %><%
                     if(validLink){ 
                        %></a><% 
                     } %><%= teaserSubstring %></td></tr>
               <% if(i>=shortyCnt-2) { // only works when the last two teasers are on 50% width %>
                  <tr><td colspan="3" style="width:<%=iImageWidth+2%>px;padding-top:4px;"><table class="dotline"><tr><td style="height:3px;"></td></tr></table></td></tr>
               <% } %>
            </table>
            <%
         } %>
			</td>
		</mm:field>
    </mm:field>
		</mm:field>
		</mm:field>
		</mm:field>
		</mm:field>
	</mm:node>
<% }
if (maxColumnsCntr>0) { %>
	<td colspan="<%= maxColumnsCntr %>">&nbsp;</td>
<% } %>
</tr>
</table>
<mm:remove referid="showteaser"/>
<mm:remove referid="divstyle" />
</mm:cloud>
