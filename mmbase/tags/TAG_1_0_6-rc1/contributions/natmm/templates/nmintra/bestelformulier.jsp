<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>

<%@include file="includes/header.jsp" %>
<%@include file="includes/calendar.jsp" %>

<SCRIPT LANGUAGE="JavaScript">
<!--
function validationMessage() {
	if((document.BestelForm.naam.value == "") || (document.BestelForm.email.value == "")) {
		alert("Voer uw naam en email in.");
		return false;
	}
	return true;
}
-->
</script>

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
    <%@include file="includes/back_print.jsp" %>





      
      <h3>Mijn bestelling</h3>
      
      <html:form action="/nmintra/BestelAction" method="POST"  onsubmit="return validationMessage()" >
         <b><html:errors bundle="LEOCMS"/></b>
         <table>
         <tr>
         <td>Naam:</td>
         <td><html:text property="naam" size="44"/>
         
         </td>
         </tr>
         <tr>
         <td>E-mail:</td>
         <td><html:text property="email" size="44"/></td>
         </tr>
         <tr>
         <td>Eenheid:</td>
         <td><html:select property="eendheid">
            <html:option value="Noordenveld">Noordenveld</html:option>
            <html:option value="Waddengebied">Waddengebied</html:option>
            <html:option value="Zuid-Drenthe">Zuid-Drenthe</html:option>
            <html:option value="de Wieden">de Wieden</html:option>
            <html:option value="Salland">Salland</html:option>
            <html:option value="Twente">Twente</html:option>
         	</html:select>
         </td>
         </tr>
         <tr>
         <td>Alternatief bezorgadres:</td>
         <td><html:textarea property="bezorgadres" cols="40" rows="5"></html:textarea></td>
         </tr>     
         </table>
    
         <br/>
         
         <html:link 
            page="/nmintra/KaartenInitAction.eb">
            <img border="0" src="media/vastgoed/w_wagentje_op_wit.gif"/>Koop nog een kaart
         </html:link>
         <br/><br/>
         
         
         <table border="0" cellpadding="4" cellspacing="4">
            <tr>
               <td bgcolor="#AAAAAA">kaartsoort</td>
               <td bgcolor="#AAAAAA">natuurgebied,eenheid,regio,coordinaten etc.</td>
               <td bgcolor="#AAAAAA">schaal of formaat</td>
               <td bgcolor="#AAAAAA">aantal</td>
               <td bgcolor="#AAAAAA">gerold of gevouwen</td>
               <td></td>
               <td></td>
            </tr>
           
            <logic:iterate id="item" name="vastgoed_shoppingbasket" type="nl.leocms.vastgoed.KaartenForm" scope="session" 
                           indexId="i" property="items">               
               
               <tr>
                  <td bgcolor="#dddddd">
                  <%
                   	String[] kartNodes = item.getSel_Kaart();
                   	if (kartNodes != null) {
                   	for (int iNodes = 0; iNodes < kartNodes.length; iNodes++) {
                   		String nodeNumber = kartNodes[iNodes];	
                  %>
                  <mm:node number="<%=nodeNumber%>">
                  	<mm:field name="naam"/>
                  </mm:node>
                  <% 			
                  	 if (iNodes != (kartNodes.length - 1)) {
                  	 	out.print(", ");
                  	 }
                    } 
                   }
                   %>
                  </td>
                  
                  <td bgcolor="#dddddd"><%= item.getKaartType()%> - <%= item.getKaartTypeDetail() %></td>
                  <td bgcolor="#dddddd"><%= item.getSchaalOfFormaat()%></td>
                  <td bgcolor="#dddddd"><%= item.getAantal()%></td>
                  <td bgcolor="#dddddd"><%= item.getGevouwenOfOpgerold()%></td>
                  <td>
                     
                     <html:link 
                        page="/nmintra/KaartenInitAction.eb" 
                        paramId="number" paramName="i">
                        <img src="media/vastgoed/refresh.gif" border="0" alt="terug">
                     </html:link>
                     
                  </td>
                  <td>
                     
                     <html:link 
                        page="/nmintra/BestelAction.eb" 
                        paramId="delete" paramName="i">
                        <img src="media/vastgoed/remove.gif" border="0" alt="verwijderen">
                     </html:link>
                     
                  </td>
               </tr>
               
            </logic:iterate>
            
         </table>
         
         <br/>
         <input type="submit" name="send" value="Verzenden" />
          
      </html:form>

<%
if ((request.getParameter("send") != null) && (request.getParameter("send").equals("Verzenden"))
&& (!"".equals(request.getParameter("naam"))) && (!"".equals(request.getParameter("email"))) ) {
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
         
   %><td style="padding-left:10px;">
   <div class="rightcolumn" id="rightcolumn">
   
   <mm:list nodes="<%= paginaID %>" path="pagina,readmore,contentblocks" orderby="readmore.pos">
      <mm:node element="contentblocks">
         <%@include file="includes/contentblockdetails.jsp" %>
      </mm:node>
      <br/>
   </mm:list>
   </div>
   </td><%
} %>



<%@include file="includes/footer.jsp" %>
</mm:cloud>
