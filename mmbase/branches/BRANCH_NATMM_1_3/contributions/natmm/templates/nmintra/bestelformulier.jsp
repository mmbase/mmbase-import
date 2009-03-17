<%@ page buffer="1000kb" autoFlush="false" %> 
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
   <%@include file="includes/templateheader.jsp" %>
   <%@include file="includes/cacheparams.jsp" %>
   
   <%@include file="includes/vastgoed/override_templateparams.jsp" %>
   
   <% (new SimpleStats()).pageCounter(cloud,application,paginaID,request); %>
   <%@include file="includes/getresponse.jsp" %>
   <html>
   <head>
      <base href="<%= javax.servlet.http.HttpUtils.getRequestURL(request) %>" />
      <link rel="stylesheet" type="text/css" href="css/main.css">
      <link rel="stylesheet" type="text/css" href="<%= styleSheet %>" />
      <link rel="stylesheet" type="text/css" href="css/vastgoed.css" />
      <title><% 
         if(isPreview) { %>PREVIEW: <% } 
         %><mm:node number="<%= subsiteID %>" notfound="skipbody"><mm:field name="naam" /></mm:node
         > <mm:node number="<%= paginaID %>" notfound="skipbody">
            <%--<mm:field name="titel" />--%>
      </mm:node></title>
      <meta http-equiv="imagetoolbar" content="no">
      <script language="javascript" src="scripts/launchcenter.js"></script>
      <script language="javascript" src="scripts/cookies.js"></script>
      <script language="javaScript" src="scripts/screensize.js"></script>
      <script type="text/javascript">
      function resizeBlocks() {	
      var MZ=(document.getElementById?true:false); 
      var IE=(document.all?true:false);
      var wHeight = 0;
      var infoPageDiff = 87;
      var navListDiff = 62;
      <mm:notpresent referid="showprogramselect">
        var smoelenBoekDiff = 378;
      </mm:notpresent>
      <mm:present referid="showprogramselect">
        var smoelenBoekDiff = 414;
      </mm:present>
      var linkListDiff = 511;
      var rightColumnDiff = 109;
      var minHeight = 300;
      if(IE){ 
        wHeight = document.body.clientHeight;
        if(wHeight>minHeight) {
          if(document.all['infopage']!=null) { 
            document.all['infopage'].style.height = (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.all['navlist']!=null) { 
            document.all['navlist'].style.height = (wHeight>navListDiff ? wHeight - navListDiff : 0); }
          if(document.all['smoelenboeklist']!=null) {
            document.all['smoelenboeklist'].style.height = (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); }
          if(document.all['rightcolumn']!=null) {
            document.all['rightcolumn'].style.height = (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); }
          if(document.all['linklist']!=null) {
            document.all['linklist'].style.height = (wHeight>linkListDiff ? wHeight - linkListDiff : 0); }
        }
      } else if(MZ){
        wHeight = window.innerHeight;
        if(wHeight>minHeight) {
          if(document.getElementById('infopage')!=null) {
            document.getElementById('infopage').style.height= (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.getElementById('navlist')!=null) {
            document.getElementById('navlist').style.height= (wHeight>navListDiff ? wHeight - navListDiff : 0); } 
          if(document.getElementById('smoelenboeklist')!=null) {
            document.getElementById('smoelenboeklist').style.height= (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); } 
          if(document.getElementById('rightcolumn')!=null) {
            document.getElementById('rightcolumn').style.height= (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); } 
          if(document.getElementById('linklist')!=null) {
            document.getElementById('linklist').style.height= (wHeight>linkListDiff ? wHeight - linkListDiff : 0); } 
        }
      }
      return false;
      }
      </script>
      <% 
      if(printPage) { 
      %>
      <style>
         body {
         overflow: auto;
         background-color: #FFFFFF
         }
      </style>
      <%
      } %>      
      
      <title>kaartenformulier plotopdrachten</title>
      
      
      
      <%@page import="nl.leocms.vastgoed.NelisReader" %>
      <% 
      NelisReader nelis = NelisReader.getInstance();
      request.setAttribute("nelis", nelis);
      %>
      
      <script type="text/javascript">
function validationMessage() {
	if((document.BestelForm.naam.value == "") || (document.BestelForm.email.value == "")) {
		alert("Voer uw naam en email in.");
		return false;
	}
	if((document.BestelForm.email.value.indexOf("@natuurmonumenten.nl") == -1)) {
		alert("Je moet een e-mailadres van natuurmonumenten invullen.");
		return false;
	}
	return true;
}
      </script>
      
   </head>
   
   <body <% 
         if(!printPage) { 
         %>onLoad="javascript:resizeBlocks();<mm:present referid="extraload"><mm:write referid="extraload" /></mm:present
   >" onResize="javascript:resizeBlocks();" onUnLoad="javascript:setScreenSize()"<%
         } else {
   %>onLoad="self.print();"<% 
         }
   %>>
   <%@include file="/editors/paginamanagement/flushlink.jsp" %>
   <table background="media/styles/<%= NMIntraConfig.style1[iRubriekStyle] %>.jpg" cellspacing="0" cellpadding="0" border="0">
   <% 
   if(!printPage) { 
   %>
   <%@include file="includes/searchbar.jsp" %>
   <tr>
      <td class="black"><img src="media/spacer.gif" width="195" height="1"></td>
      <td class="black" style="width:70%;"><img src="media/spacer.gif" width="1" height="1"></td>
      <td class="black"><img src="media/spacer.gif" width="251" height="1"></td>
   </tr>
   <% 
   } 
   %>
   <tr>
      <% 
      if(!printPage) { 
      %><td rowspan="2"><%@include file="includes/nav.jsp" %></td><% 
      } 
      %>
      <%@include file="includes/calendar.jsp" %>   
      
      <% boolean twoColumns = !printPage && ! NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek"); %>
      <td <% if(!twoColumns) { %>colspan="2"<% } %>><%@include file="includes/pagetitle.jsp" %></td>
      <% 
      if(twoColumns) {
         String rightBarTitle = "";
      %><td><%@include file="includes/rightbartitle.jsp" %></td><%
      } %>
   </tr>
   <tr>
   <td class="transperant" <% if(NMIntraConfig.style1[iRubriekStyle].equals("bibliotheek")) { %>colspan="2"<% } %>>
   <div class="<%= infopageClass %>" id="infopage">
   <table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr><td style="padding:10px;padding-top:18px;">
   <a name="top">
   <%--%@include file="includes/back_print.jsp" %>--%>
   
   <html:form action="/nmintra/BestelAction" method="POST"  onsubmit="return validationMessage()" >
      <b><html:errors bundle="LEOCMS"/></b>
      <table border="0" cellpadding="4" cellspacing="4">
         <tr>
            <td><h3>Mijn bestelling</h3></td>
         </tr>
         <tr>
            <td class="vastgoed_medium">Naam:</td>
            <td class="vastgoed_light"><html:text property="naam" size="44"/>
               
            </td>
         </tr>
         <tr>
            <td class="vastgoed_medium">E-mail:</td>
            <td class="vastgoed_light"><html:text property="email" size="44"/></td>
         </tr>
         <tr>
            <td class="vastgoed_medium">Regio of Eenheid:</td>
            <td class="vastgoed_light"><html:select property="eendheid">
                  <html:options name="nelis" property="eenheidListWithDepartments" />
               </html:select>
            </td>
         </tr>
         <tr>
            <td class="vastgoed_medium">Alternatief bezorgadres:</td>
            <td class="vastgoed_light"><html:textarea property="bezorgadres" cols="44" rows="5"></html:textarea></td>
         </tr>     
      </table>
      
      <br/><br/>
      
      <%String buyNewLink = "/nmintra/KaartenInitAction.eb" + rubriekParams; %>
      <html:link 
         page="<%=buyNewLink%>">
         <img border="0" src="media/vastgoed/w_wagentje_op_wit.gif"/>Bestel nog een kaart 
      </html:link>
      <br/><br/>
      
      
      <table border="0" cellpadding="4" cellspacing="4">
         <tr>
            <td class="vastgoed_medium">kaartsoort</td>
            <td class="vastgoed_medium">natuurgebied,eenheid,regio,coordinaten etc.</td>
            <td class="vastgoed_medium">schaal of formaat</td>
            <td class="vastgoed_medium">aantal</td>
            <td class="vastgoed_medium">gerold of gevouwen</td>
            <td></td>
            <td></td>
         </tr>
         
         <logic:iterate id="item" name="vastgoed_shoppingbasket" type="nl.leocms.vastgoed.KaartenForm" scope="session" 
                        indexId="i" property="items">               
            
            <tr>
               <td class="vastgoed_light">
                  <%
                  String[] kartNodes = item.getSel_Kaart();
                  if (kartNodes != null) {
                     for (int iNodes = 0; iNodes < kartNodes.length; iNodes++) {
                        String nodeNumber = kartNodes[iNodes];
                        Node kaartNode = cloud.getNode(nodeNumber);
                        String kaartNaam = "";
                        if (kaartNode != null) {
                           kaartNaam = kaartNode.getStringValue("naam");
                        }
                        
                        if (iNodes != (kartNodes.length - 1)) {
                           out.print(kaartNaam + ", ");
                        }
                        else {
                           out.print(kaartNaam);
                        }
                     }
                  }
                  %>
               </td>
               
               <td class="vastgoed_light">
                  <%= item.getKaartType()%>
                  <%
                     if (!"".equals(item.getKaartTypeDetail())) {
                        out.print(" - " + item.getKaartTypeDetail());
                     }
                  %>
               </td>
               <td class="vastgoed_light"><%= item.getSchaalOfFormaat()%></td>
               <td class="vastgoed_light"><%= item.getAantal()%></td>
               <td class="vastgoed_light"><%= item.getGevouwenOfOpgerold()%></td>
               <td>
                  <% String updateLink = "/nmintra/KaartenInitAction.eb" + rubriekParams + "&randNr=" + java.lang.Math.random(); %>
                  <html:link 
                     page="<%=updateLink%>" 
                     paramId="number" paramName="i">
                     <img src="media/vastgoed/arrowleft_default.gif" border="0" alt="Bestelling wijzigen" title="Bestelling wijzigen"/>
                  </html:link>
                  
               </td>
               <td>
                  <%String deleteLink = "/nmintra/BestelAction.eb" + rubriekParams; %>
                  <html:link 
                     page="<%=deleteLink%>" 
                     paramId="delete" paramName="i">
                     <img src="media/vastgoed/remove.gif" border="0" alt="Verwijderen" title="Verwijderen"/>
                  </html:link>
                  
               </td>
            </tr>
            
         </logic:iterate>
         
      </table>
      
      <br/>
      <html:submit property="send" value="Verzenden" />
      
      <input type="hidden" name="rb" value="<%=iRubriekStyle%>"/>
      <input type="hidden" name="rbid" value="<%=rubriekId%>"/>
      <input type="hidden" name="pgid" value="<%=paginaID%>"/>
      <input type="hidden" name="ssid" value="<%=subsiteID%>"/>
   </html:form>
   
   <%
   if ((request.getParameter("send") != null) && (request.getParameter("send").equals("Verzenden"))
   && (!"".equals(request.getParameter("naam"))) && (!"".equals(request.getParameter("email")))
   && (request.getAttribute("empty") == null) ) {
   %>
   <div style="color:red;"><b><bean:message bundle="LEOCMS" key="shoppingcart.processed" /></b></div>
   <%
   }
   %>
   
   <%
   if(twoColumns) {
                     // *********************************** right bar *******************************
                     String styleClass = "white";
                     String styleClassDark = "white";
   %>
   <td style="padding-left:10px;">
      <div class="rightcolumn" id="rightcolumn">
         
         <mm:list nodes="<%= paginaID %>" path="pagina,readmore,contentblocks" orderby="readmore.pos">
            <mm:node element="contentblocks">
               <%@include file="includes/contentblockdetails.jsp" %>
            </mm:node>
            <br/>
         </mm:list>
      </div>
   </td>
   <%
   }
   %>
   
   <%@include file="includes/footer.jsp" %>
</mm:cloud>
