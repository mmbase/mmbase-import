<mm:context>
<%
int maxShorties = 1;
int shortyCnt = 0; 
String[] shortyID = new String[maxShorties];
String pannoConstraint = "(panno.embargo < '" + (nowSec+quarterOfAnHour) + "') AND (panno.reageer='0' OR panno.verloopdatum > '" + nowSec + "' )";
if(!natuurgebiedID.equals("-1")){%>
   <mm:list nodes="<%=natuurgebiedID%>" path="natuurgebieden,posrel,panno" fields="panno.number"
      max="1" constraints="<%= pannoConstraint %>" orderby="panno.embargo" directions="DOWN">
   <mm:field name="panno.number" jspvar="panno_number" vartype="String" write="false">
      <% shortyID[shortyCnt]= panno_number; shortyCnt= 1; %>
   </mm:field>
   </mm:list>
<% } 
if(shortyCnt!=1) { %>
   <mm:list nodes="<%= paginaID %>" path="pagina,posrel,panno" fields="panno.number"
      max="1" constraints="<%= pannoConstraint %>" orderby="panno.embargo" directions="DOWN">
   <mm:field name="panno.number" jspvar="panno_number" vartype="String" write="false">
      <% shortyID[shortyCnt]= panno_number; shortyCnt= 1; %>
   </mm:field>
   </mm:list>
<% }
if(shortyCnt!=1) { 
   int r= 0;
   while(shortyCnt!=1&&r<breadcrumbs.size()) { %>
      <mm:list nodes="<%= (String) breadcrumbs.get(r) %>" path="rubriek,posrel,panno" fields="panno.number"
         max="1" constraints="<%= pannoConstraint %>" orderby="panno.embargo" directions="DOWN">
      <mm:field name="panno.number" jspvar="panno_number" vartype="String" write="false">
         <% shortyID[shortyCnt]= panno_number;  shortyCnt= 1; %>
      </mm:field>
      </mm:list>
      <%   
      r++;
   }
} 
// tX, tY, bX, bY
String panoTemplate = "s(744)+part(0,0,744,138)";  // only part(..) gives problem with lots of small images
int pHeight = 138;
if(path.equals("newsletter.jsp")) {
   panoTemplate = "s(564)+part(0,0,564,52)"; 
   pHeight = 52;
} else if(iRubriekLayout==NatMMConfig.SUBSITE3_LAYOUT) {
   if(path.equals("homepage.jsp")) { 
      panoTemplate =""; // panoTemplate = "part(0,0,744,398)";
      pHeight = 398;
   } else {
      panoTemplate = "";
      pHeight = 171;
   }
} else if( iRubriekLayout==NatMMConfig.SUBSITE1_LAYOUT
            || iRubriekLayout==NatMMConfig.SUBSITE2_LAYOUT ) {
   panoTemplate = "s(744)+part(0,0,744,75)";
   pHeight = 75;
} else if (iRubriekLayout==NatMMConfig.DEMO_LAYOUT) {
  if(path.equals("portal.jsp")) { 
    panoTemplate = "s(789)+part(0,0,789,52)";
  } else {
    panoTemplate = "s(744)+part(0,0,744,52)";  
  }
  pHeight = 52;
} else if(path.equals("index.jsp")) {
   panoTemplate = "part(0,0,744,157)"; 
   pHeight = 157;
} 
%>
<tr>
   <td style="width:48%"></td>
   <td style="text-align:left; vertical-align:top; width:744;height:<%= pHeight %>px;padding-bottom:1px;">
   <% 
   for (int i =0; i<shortyCnt;i++){ %>
      <%@include file="../includes/shorty_logic_2.jsp" %>
      <mm:node number="<%= shortyID[i] %>">
      <mm:field name="titel_zichtbaar">
      <mm:compare value="1">
      <div style="position:relative;left:10px;top:<%= (iRubriekLayout==NatMMConfig.SUBSITE1_LAYOUT ? "8" : "85") %>px;">
         <div style="visibility:visible;position:absolute;top:0px;left:0px;">
            <table width="169" cellspacing="0" cellpadding="0">
            <tr>
               <td align="center" class="pano_shorty-header"><mm:field name="titel" /></td>
            </tr>
            <tr>
               <td align="center" height="45" class="pano_shorty-body"><mm:field name="omschrijving" /></td>
            </tr>
            </table>
         </div>
         <% 
         if(validLink){ // *** to make entire table clickable ***  
            %>
            <div style="visibility:visible;position:absolute;top:0px;left:0px;">
               <a href="<%= readmoreURL %>"<%
                  if(!readmoreTarget.equals("")){ %> target="<%= readmoreTarget %>"<% }
                  %>><img src="media/trans.gif" width="169" height="61" border="0"<% 
                        if(!altTXT.equals("")){ %> alt="<%= altTXT %>"<% }
                        %>"></a>
            </div>
            <% 
         } 
         %>
      </div>
      </mm:compare>
      </mm:field>
      <mm:related path="posrel,images" max="1">
         <mm:node element="images"><% 
            if(validLink){ %><a href="<%= readmoreURL %>" <% if(!readmoreTarget.equals("")){ %> target="<%= readmoreTarget %>"<% } %>><% }
            %><img src="<mm:image template="<%= panoTemplate %>" />"<% if(!altTXT.equals("")){%> alt="<%= altTXT %>"<% } %>" border="0"><% 
            if(validLink){ %></a><% } 
         %></mm:node
      ></mm:related
      ></mm:node><% 
      readmoreURL = "";
   } 
   %></td>
   <td style="width:48%" rowspan="2" valign="top">
   <img src="media/natmm_logo_rgb1.gif" width="159" height="216" style="padding:0;">
   </td>
</tr>
</mm:context>
<tr>
   <td style="width:48%"></td>
   <td style="width:744px;padding-bottom:1px;">