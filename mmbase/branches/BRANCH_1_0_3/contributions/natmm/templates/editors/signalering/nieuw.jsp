<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.util.*,nl.leocms.authorization.*" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Iterator" %>
<%@page import="org.mmbase.bridge.Node" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">

<mm:import externid="pagenumber"/>

<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>

    <title>Nieuwe signalering</title>
    <script language="JavaScript" src="../../calendar/calendar.js"></script>
    <script>
        function popUpCalendarVan() {
            if(document.forms[0].datumVanDag.value != "" && document.forms[0].datumVanMaand.value != "" && document.forms[0].datumVanJaar.value != "")
              document.forms[0]._hiddenDateVan.value = document.forms[0].datumVanDag.value + '-' + document.forms[0].datumVanMaand.value + '-' + document.forms[0].datumVanJaar.value;
            popup_calendar_year("document.forms[0]._hiddenDateVan");
        }
        
        function popUpCalendarTm() {
            if(document.forms[0].datumTmDag.value != "" && document.forms[0].datumTmMaand.value != "" && document.forms[0].datumTmJaar.value != "")
              document.forms[0]._hiddenDateTm.value = document.forms[0].datumTmDag.value + '-' + document.forms[0].datumTmMaand.value + '-' + document.forms[0].datumTmJaar.value;
            popup_calendar_year("document.forms[0]._hiddenDateTm");
        }
        
        function popupDone(resultfield) {
            if(resultfield == document.forms[0]._hiddenDateVan){
              var splitData = document.forms[0]._hiddenDateVan.value.split('-');
              if (splitData.length == 3) {
                 document.forms[0].datumVanDag.value = splitData[0];
                 document.forms[0].datumVanMaand.value = splitData[1];
                 document.forms[0].datumVanJaar.value = splitData[2];
              }
            }
            if(resultfield == document.forms[0]._hiddenDateTm){
              var splitData = document.forms[0]._hiddenDateTm.value.split('-');
              if (splitData.length == 3) {
                 document.forms[0].datumTmDag.value = splitData[0];
                 document.forms[0].datumTmMaand.value = splitData[1];
                 document.forms[0].datumTmJaar.value = splitData[2];
              }
            }
        }
        
        // e-mail notification        
        function showEmailNotification() {
            if (document.forms[0].emailnotificatie.checked) {
                document.all['div1'].style.visibility="visible";
                document.all['div2'].style.visibility="visible";
            }
            else {
                document.all['div1'].style.visibility="hidden";
                document.all['div2'].style.visibility="hidden";
            }
        }
        
        function popupPaginaSelector() {
             var newWindow;
             var urlstring = "../paginamanagement/selector/pagina_selector.jsp?refreshtarget=/editors/signalering/nieuw.jsp&targetframe=bottompane.rightpane.editscreen";    
             newWindow = window.open(urlstring,'_popUpPage','height=400,width=400,left=100,top=100,scrollbars=yes,status=no,toolbar=no,menubar=no,location=no,resizable=no');
         }
    </script>
</head>

<body>
    
<html:form action="/editors/signalering/NieuwSignaleringAction" method="post">
<%
   String username = cloud.getUser().getIdentifier();
   String constraints = "[users.account]='" + username + "'";
%>

<mm:list path="users" constraints="<%= constraints %>" max="1">
   <input type="hidden" name="afzender" value="<mm:field name="users.number"/>">
</mm:list>

<table>
   <tr>
      <td>
         <table width="550" cellpadding="2" cellspacing="1">
            <tr>
               <td colspan="3"><h2>Nieuwe taak</h2></td>
            </tr>
            <tr>
               <td>Pagina</td>
               <td colspan="2">
                  <input type="hidden" name="pagenumber" value="<mm:write referid="pagenumber"/>">
                  <mm:present referid="pagenumber">
                     <mm:node referid="pagenumber">
                        <mm:field name="titel"/>&nbsp;&nbsp;&nbsp;
                     </mm:node>
                  </mm:present>
                  <a href="javascript:popupPaginaSelector();">Kies een pagina</a>
               </td>
            </tr>
            <mm:present referid="pagenumber">
               <tr>
                  <td>Gebruikers:</td>
                  <td>
                     <select name="usernumber">
                        <mm:list nodes="$pagenumber" path="pagina,posrel,rubriek" max="1">
                           <mm:field name="rubriek.number" jspvar="rubriekNodeNumber" vartype="String" write="false">
                           <%
                              RubriekHelper rubriekHelper = new RubriekHelper(cloud);
                              Node rubriekNode = rubriekHelper.getRubriekNode(rubriekNodeNumber);
                              AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud);
                              List list = authorizationHelper.getUsersWithRights(rubriekNode, Roles.SCHRIJVER);
                              if (list != null) {
                                 Iterator usersIterator = list.iterator();
                                 
                                 while (usersIterator.hasNext()) {
                                    Node userNode = (Node) usersIterator.next();
                                    if (userNode != null) {
%>
                                    <option value="<%= userNode.getStringValue("number") %>"><%= userNode.getStringValue("voornaam") %> <%= userNode.getStringValue("tussenvoegsel") %> <%= userNode.getStringValue("achternaam") %></option>
<%
                                    }
                                 }
                              }
                           %>
                           </mm:field>
                        </mm:list>
                     </select>
                  </td>
                  <td>
                     <html:errors bundle="LEOCMS" property="usernumber"/>
                  </td>
               </tr>
               <tr>
                  <td>Content type:</td>
                  <td colspan="2">
                     <html:select property="contentelement">
                        <html:option value="images">Afbeelding</html:option>
                        <html:option value="artikel">Artikel</html:option>
                        <html:option value="attachments">Bijlage</html:option>
                        <html:option value="evenement">Evenement</html:option>
                        <html:option value="flash">Flash</html:option>
                        <html:option value="link">Link</html:option>
                        <html:option value="poll">Opinie</html:option>
                        <html:option value="organisatie">Organisatie</html:option>
                        <html:option value="persoon">Persoon</html:option>
                        <html:option value="vacature">Vacature</html:option>
                        <html:option value="vgv">Veel Gestelde Vraag</html:option>
                     </html:select>
                  </td>
               </tr>
               <tr>
                  <td>Dead line:</td>
                  <td nowrap><html:text property="datumVanDag" maxlength="2" size="2" tabindex="2"/>-<html:text property="datumVanMaand" maxlength="2" size="2" tabindex="3"/>-<html:text property="datumVanJaar" maxlength="4" size="4" tabindex="4"/>
                       <a href="javascript:popUpCalendarVan()" ><img src='../../calendar/show-calendar-on-button.gif' width='24' height='24' align='absmiddle' border='0'></a>
                       <input type=hidden name="_hiddenDateVan" >
                   </td>                  
                  <td>
                      <html:errors bundle="LEOCMS" property="datumVanDag"/>
                   </td>
               </tr>
               <tr>
                  <td>Notitie:</td>
                  <td><html:textarea property="notitie" cols="40" rows="4"/></td>
               </tr>
               <tr>
                  <td>E-mail signalering:</td>
                  <td colspan="2"><input type="checkbox" name="emailnotificatie" value="on" onclick="showEmailNotification()"></td>
               </tr>                              
               <tr>
                  <td>
                     <div style="visibility: hidden;" id="div1">
                        Herhaling signalering:
                     </div>
                  </td>
                  <td colspan="2">
                     <div style="visibility: hidden;" id="div2"><html:text property="herhaling" value="5" maxlength="3" size="3"/> dagen voor de dead line</div>
                  </td>
               </tr>
               <tr>
                  <td colspan="2" align="right"><input type="submit" name="bewaar" value="Bewaar"></td>
                  <td>&nbsp;</td>
               </tr>
            </mm:present>
         </table>
      </td>
   </tr>
</table>

</html:form>

</body>
</html>

</mm:cloud>