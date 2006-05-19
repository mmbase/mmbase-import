<%@include file="includes/top0.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<mm:import jspvar="card" externid="card" id="card"></mm:import>
<mm:import jspvar="actie" externid="actie" id="actie"></mm:import>
<mm:import jspvar="toname" externid="toname">-</mm:import>
<mm:import jspvar="toemail" externid="toemail">-</mm:import>
<mm:import jspvar="fromname" externid="fromname">-</mm:import>
<mm:import jspvar="fromemail" externid="fromemail">-</mm:import>
<mm:import jspvar="body" externid="body">-</mm:import>
<% 
boolean viewCard = false; 
String cardID = "-1";
%>
<mm:notpresent referid="actie">
	<mm:present referid="card">
		<mm:listcontainer path="ecard,images,dossier">
		<mm:constraint field="ecard.mailkey" operator="EQUAL" value="<%= card %>" />
		<mm:list>
			<mm:field name="dossier.number" write="false" jspvar="dossierid" vartype="String"><% dossierID = dossierid; %></mm:field>
			<mm:field name="images.number" write="false" jspvar="imageid" vartype="String"><% imgID = imageid; %></mm:field>
         <mm:node element="ecard" jspvar="thisEcard">
			   <% 
            cardID = thisEcard.getStringValue("number"); 
            toemail = thisEcard.getStringValue("toemail"); 
            toname = thisEcard.getStringValue("toname"); 
            fromemail = thisEcard.getStringValue("fromemail"); 
            fromname = thisEcard.getStringValue("fromname"); 
            body = thisEcard.getStringValue("body"); 
            %>
         </mm:node>
			<% viewCard = true; %>
		   </mm:list>
		</mm:listcontainer>
	</mm:present>
</mm:notpresent>
<%@include file="includes/top2_cacheparams.jsp" %>
<% if(!cardID.equals("-1")||!imgID.equals("-1")) { expireTime = 0; } %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/top3_nav.jsp" %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<SCRIPT language=JavaScript> 	
 	var layerNumShowing=<%= (viewCard ? "1" : "2" ) %>;
	function showLayerNumber(layerNumToShow){
 		hideLayer(eval('"layer' + layerNumShowing+'"'));
 		showLayer(eval('"layer' + layerNumToShow+'"'));
 		layerNumShowing=layerNumToShow;
   }	
	function showLayer(layerName){
      var IE=(document.all?true:false);
   	if(IE){ 
   		eval('document.all["'+layerName+'"].style.visibility="visible"');
   	} else {
   		eval('document.getElementById("'+ layerName + '").style.visibility="visible"');
   	}
   } 	
	function hideLayer(layerName){
      var IE=(document.all?true:false);
   	if(IE){ 
   		eval('document.all["' + layerName + '"].style.visibility="hidden"');
   	} else {
   		eval('document.getElementById("'+ layerName + '").style.visibility="hidden"');
   	}
   } 
</SCRIPT>
<br>
   <table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <tr>
   	<td style="vertical-align:top;width:185px;padding:10px;padding-top:0px;">
   	   <%@include file="includes/navleft.jsp" %>
   	   <br>
      	<jsp:include page="includes/teaser.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sr" value="0" />
         </jsp:include>
   	</td>
   	<td style="vertical-align:top;width:559px;padding:10px;padding-top:0px;">   
      <% if(cardID.equals("-1")&&imgID.equals("-1")) { %>
      	<jsp:include page="includes/fun/fun_index.jsp">
   	      <jsp:param name="p" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
   	   </jsp:include>
      <% } else { 
         if(dossierID.equals("-1")) { 
            %><mm:list nodes="<%= imgID %>" path="images,posrel,dossier,posrel,pagina" constraints="<%= "pagina.number == '" + paginaID + "'" %>" max="1">
               <mm:field name="dossier.number" jspvar="dossier_number" vartype="String" write="false">
                  <% dossierID = dossier_number; %>
               </mm:field>
            </mm:list>
            <%
         } %>
         <mm:import id="nopage_description" />
         <%@include file="includes/page_intro.jsp" %>
         <div style="margin:9px 0px 0px 0px">
            <mm:node number="<%=dossierID%>"><strong><mm:field name="naam" /></strong></mm:node>: 
            <mm:node number="<%=imgID%>">
               <mm:field name="titel" jspvar="sTitle" vartype="String" write="false">
                  <%= sTitle.substring(0,1).toUpperCase() + sTitle.substring(1) %>
               </mm:field> 
            </mm:node>
         </div>
         <table class="dotline"><tr><td height="3"></td></tr></table>
         <br>
         <mm:notpresent referid="actie">
            <div id="layer1" style="z-index:0;visibility:<%= (viewCard ? "visible" : "hidden" ) %>;position:absolute;">
            	<table border="0" cellspacing="2" cellpadding="2">
            		<tr bgcolor="#FBD1E7">
            		   <td align="center">
               		<% 
               		 if(!imgID.equals("-1")){%>
               				<mm:node number="<%= imgID %>">
               					<img src="<mm:image template="s(500x330)" />" alt="<mm:field name="titel" />" border="0"></td>
               				</mm:node><%
               		}%>
            		   </td>
            		</tr>
            	</table>
            	<table width="518" border="0" cellspacing="0" cellpadding="0">
            		<tr>
            			<td width="300">
               			<a href="javascript:showLayerNumber(2)" class="subnav">Omdraaien</a>
               			<a href="ecard.jsp?p=<%=paginaID%>&d=<%=dossierID%>" class="subnav">
               			   <% if(cardID.equals("-1")) { 
               			      %>Terug naar kaartoverzicht<% 
               			   } else { 
               			      %>Kies zelf een E-card<% 
               			   } 
               			%></a>
               	   </td>
            			<td align="right" width="218"></td>
            		</tr>
            	</table>
            </div>
            <div id="layer2" style="z-index:0;visibility:<%= (viewCard ? "hidden" : "visible" ) %>;position:absolute;">
               <jsp:include page="includes/fun/ecard_form.jsp">
                  <jsp:param name="p" value="<%= paginaID %>" />
                  <jsp:param name="r" value="<%= rubriekID %>" />
                  <jsp:param name="d" value="<%= dossierID %>" />
                  <jsp:param name="i" value="<%= imgID %>" />
                  <jsp:param name="msc" value="<%= nl.mmatch.NatMMConfig.color1[iRubriekStyle] %>" />
                  <jsp:param name="vc" value="<%= viewCard %>" />
                  <jsp:param name="ie" value="<%= isIE %>" />
                  <jsp:param name="toname" value="<%= toname %>" />
                  <jsp:param name="toemail" value="<%= toemail %>" />
                  <jsp:param name="fromname" value="<%= fromname %>" />
                  <jsp:param name="fromemail" value="<%= fromemail %>" />
                  <jsp:param name="body" value="<%= body %>" />
               </jsp:include>
            </div>
            <br><img src="media/trans.gif" width="1" height="360" border="0"><br>
         </mm:notpresent>
         <mm:present referid="actie">
            <div style="margin:45px 0px 250px 0x">
            <jsp:include page="includes/fun/ecard_send.jsp">
      	      <jsp:param name="i" value="<%= imgID %>" />
               <jsp:param name="toname" value="<%= toname %>" />
      	      <jsp:param name="toemail" value="<%= toemail %>" />
               <jsp:param name="fromname" value="<%= fromname %>" />
      	      <jsp:param name="fromemail" value="<%= fromemail %>" />
               <jsp:param name="body" value="<%= body %>" />
   	      </jsp:include>
            </div>	
         </mm:present>
      <% } %>
      </td>
   </tr>
</table>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
