<%@page import="nl.leocms.authorization.*,
   nl.leocms.workflow.*,
   nl.leocms.util.*,
   java.util.*,
   org.mmbase.bridge.*,
   nl.leocms.servlets.UrlConverter" %>
<%@include file="/taglibs.jsp" %>
<cache:flush scope="application"/>
<% UrlConverter.getCache().flushAll(); %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>

<title>Pagina editor</title>
   <script language="JavaScript" src="../../calendar/calendar.js"></script>
   <script language="JavaScript" src="<mm:url page="<%= editwizard_location %>"/>/javascript/date.js"></script>
   <script>
      function popUpCalendarVerloop() {
         if(document.forms[0].verloopdatumdag.value != "" && document.forms[0].verloopdatummaand.value != "" && document.forms[0].verloopdatumjaar.value != "")
            document.forms[0].verloopdatum.value = document.forms[0].verloopdatumdag.value + '-' + document.forms[0].verloopdatummaand.value + '-' + document.forms[0].verloopdatumjaar.value;
         popup_calendar_year("document.forms[0].verloopdatum");
      }
         
      function popUpCalendarEmbargo() {
         if(document.forms[0].embargodag.value != "" && document.forms[0].embargomaand.value != "" && document.forms[0].embargojaar.value != "")
            document.forms[0].embargo.value = document.forms[0].embargodag.value + '-' + document.forms[0].embargomaand.value + '-' + document.forms[0].embargojaar.value;
         popup_calendar_year("document.forms[0].embargo");
      }
         
      function popupDone(resultfield) {
         if(resultfield == document.forms[0].verloopdatum){
            var splitData = document.forms[0].verloopdatum.value.split('-');
            if (splitData.length == 3) {
               document.forms[0].verloopdatumdag.value = splitData[0];
               document.forms[0].verloopdatummaand.value = splitData[1];
               document.forms[0].verloopdatumjaar.value = splitData[2];
            }
         }
         if(resultfield == document.forms[0].embargo){
            var splitData = document.forms[0].embargo.value.split('-');
            if (splitData.length == 3) {
               document.forms[0].embargodag.value = splitData[0];
               document.forms[0].embargomaand.value = splitData[1];
               document.forms[0].embargojaar.value = splitData[2];
            }
         }
      }
      
      function showTab(tab, hide1, hide2, hide3) {
        document.getElementById(tab).style.display = 'inline';
        document.getElementById(hide1).style.display = 'none';
        document.getElementById(hide2).style.display = 'none';
        document.getElementById(hide3).style.display = 'none';
    }
    
    function setMaxDate(checkbox) {
        var form = document.forms[0];
        if (checkbox.checked) {
            form.elements["verloopdatumdag"].value = "01";
            form.elements["verloopdatummaand"].value = "01";
            form.elements["verloopdatumjaar"].value = "2038";
            form.elements["verloopdatum"].value = "01-01-2038";
        }
        else {
            var d = new Date();
            var mmbasevalue = Math.round(d.getTime()/1000);
            var d = getDate(mmbasevalue);
            form.elements["verloopdatumdag"].value = d.getDate();
            form.elements["verloopdatummaand"].value = d.getMonth();
            var y = d.getFullYear();
            if (y <= 0) y--;
            form.elements["verloopdatumjaar"].value = y;
            form.elements["verloopdatum"].value = d.getDate() + "-" + d.getMonth() + "-" + y;
        }
      }
   </script>
   <style>
    input { width: auto;}
    
    td.stepcurrent { width: 0px}
    td.stepother { width: 0px}
   </style>
</head>

<body>

<mm:cloud method="http" rank="basic user" jspvar="cloud">

<%
   RubriekHelper rubriekHelper = new RubriekHelper(cloud);
   PaginaHelper paginaHelper = new PaginaHelper(cloud);
   ApplicationHelper applicationHelper = new ApplicationHelper(cloud);
   AuthorizationHelper authHelper = new AuthorizationHelper(cloud);
   String username = cloud.getUser().getIdentifier();
   String rubriekNodeNumber = null;
   String paginaNodeNumber = null;
%>

<logic:equal name="PaginaForm" property="node" value="">
   <h1>Pagina toevoegen</h1>
   <bean:define id="parentNodeNumber" property="parent" name="PaginaForm" scope="request" type="java.lang.String"/>
   <%
      rubriekNodeNumber = parentNodeNumber;
   %>
</logic:equal>  
<logic:notEqual name="PaginaForm" property="node" value="">
   <h1>Pagina wijzigen</h1>
   <bean:define id="currentNodeNumber" property="node" name="PaginaForm" scope="request" type="java.lang.String"/>
   <%
      paginaNodeNumber = currentNodeNumber;
      rubriekNodeNumber = rubriekHelper.getRubriekNodeNumberAsString(paginaNodeNumber);
   %>
</logic:notEqual>

Rubriek: 
<b>
<%
    out.print( rubriekHelper.getPathToRootString(rubriekNodeNumber));
   UserRole userRole = authHelper.getRoleForUser(authHelper.getUserNode(username), rubriekHelper.getRubriekNode(rubriekNodeNumber));
%>
</b> 

<html:form action="/editors/paginamanagement/PaginaAction">
<html:hidden property="node"/>
<html:hidden property="parent"/>
<input type="hidden" name="username" value="<%= username %>">
<div id="nl">
<%-- hh   <table class="stepscontent" width="1%">
      <tr>
         <td class="stepcurrent">Nederlands</td>
         <td class="stepother"><a href="javascript:showTab('fra','nl','eng','de')">Frans</a></td>
         <td class="stepother"><a href="javascript:showTab('eng','nl','fra','de')">English</a></td>
         <td class="stepother"><a href="javascript:showTab('de','nl','fra','eng')">Deutsch</a></td>
      </tr>
   </table>
--%>
   <table class="formcontent">
      <tr><td width="200" valign="top">Titel</td><td><html:text property="titel" size='40' maxlength='64'/> <span class="notvalid"><html:errors bundle="LEOCMS" property="titel" /></span></td></tr>
      <%
      if(applicationHelper.isInstalled("NatMM")) {
         %>
         <tr><td valign="top" nowrap>Korte titel</td><td><html:text property="kortetitel" size='40' maxlength='40'/> <span class="notvalid"><html:errors bundle="LEOCMS" property="kortetitel" /></span></td></tr>
         <%
      } else {
         %>
         <html:hidden property="kortetitel" value=' ' />
         <%
      } %>
      <input type="hidden" name="urlfragment" value="dummy" />
      
<%-- hh 
   <tr><td valign="top">Omschrijving</td><td><html:textarea property="omschrijving" cols='40' rows='4'/></td></tr>
   <tr><td valign="top" nowrap>Url-fragment</td><td><html:text property="urlfragment" size='40' maxlength='40'/> <span class="notvalid"><html:errors bundle="LEOCMS" property="urlfragment" /></span></td></tr>
--%>
      <tr>
         <td valign="top">Template voor de pagina</td>
         <td>
            <%
               String paginaTemplateNodeNumber = "";
               if (paginaNodeNumber != null) {
                  Node paginaTemplateNode = paginaHelper.getPaginaTemplate(paginaNodeNumber);
                  if (paginaTemplateNode != null) {
                     paginaTemplateNodeNumber = "" + paginaTemplateNode.getNumber();
                  }
               }
               paginaTemplateNodeNumber = (request.getAttribute("paginatemplate") != null) ? (String) request.getAttribute("paginatemplate") : paginaTemplateNodeNumber;
               // only show templates for which there is a corresponding editwizard
            %>
            <select name="paginatemplate">
               <mm:listnodes type="paginatemplate" orderby="naam,url" constraints="contenttemplate=0">
                  <mm:field name="number" jspvar="number" vartype="String" write="false">
                  <mm:field name="naam" jspvar="naam" vartype="String" write="false">
                     <option value="<%= number %>" <%= paginaTemplateNodeNumber.equals(number) ? "selected" : "" %>><%= naam %></option>
                  </mm:field>
                  </mm:field>
               </mm:listnodes>
            </select>
         </td>
      </tr> 
    <tr><td valign="top">Notitie</td><td><html:textarea property="notitie" cols='40' rows='4'/></td></tr>   
    <tr><td valign="top">Metatags</td><td><html:text property="metatags" size='40' maxlength='40'/></td></tr>
    <tr><td valign="top">Publiceerdatum</td><td><html:text property="embargodag" size='2' maxlength='2'/>-<html:text property="embargomaand" size='2' maxlength='2'/>-<html:text property="embargojaar" size='4' maxlength='4'/>&nbsp;&nbsp;<a href="javascript:popUpCalendarEmbargo()" ><img src='../../calendar/show-calendar-on-button.gif' width='24' height='24' align='absmiddle' border='0'></a><input type="hidden" name="embargo">
        <span class="notvalid"><html:errors bundle="LEOCMS" property="embargo" /></span></td>
    </tr>       
    <tr><td valign="top">Verloopdatum</td><td><html:text property="verloopdatumdag" size='2' maxlength='2'/>-<html:text property="verloopdatummaand" size='2' maxlength='2'/>-<html:text property="verloopdatumjaar" size='4' maxlength='4'/>&nbsp;&nbsp;<a href="javascript:popUpCalendarVerloop()" ><img src='../../calendar/show-calendar-on-button.gif' width='24' height='24' align='absmiddle' border='0'><input type="hidden" name="verloopdatum"></a>
        <input type="checkbox" onClick="setMaxDate(this);" name="onbepaald" <%= (paginaNodeNumber != null) ? "" : "checked=\"true\"" %>/>Onbepaald
        <span class="notvalid"><html:errors bundle="LEOCMS" property="verloopdatum" /></span></td>
    </tr>
  </table>
</div>
<div id="fra" style="display: none">
   <table class="stepscontent">
      <tr>
         <td class="stepother"><a href="javascript:showTab('nl','fra','eng','de')">Nederlands</a></td>
         <td class="stepcurrent">Frans</a></td>
         <td class="stepother"><a href="javascript:showTab('eng','nl','fra','de')">English</a></td>
         <td class="stepother"><a href="javascript:showTab('de','nl','fra','eng')">Deutsch</a></td>
      </tr>
   </table>
   <table class="formcontent">
      <tr><td width="200" valign="top" nowrap>Titel</td><td><html:text property="titel_fra" size='40' maxlength='64'/> </td></tr>
      <tr><td valign="top" nowrap>Korte titel</td><td><html:text property="kortetitel_fra" size='40' maxlength='40'/></td></tr>
      <tr><td valign="top" nowrap>Omschrijving</td><td><html:textarea property="omschrijving_fra" cols='40' rows='4'/> </td></tr>
   </table>
</div>
<div id="eng" style="display: none">
   <table class="stepscontent" width="0">
      <tr>
         <td class="stepother"><a href="javascript:showTab('nl','fra','eng','de')">Nederlands</a></td>
         <td class="stepother"><a href="javascript:showTab('fra','nl','eng','de')">Frans</a></td>
         <td class="stepcurrent">English</td>
         <td class="stepother"><a href="javascript:showTab('de','nl','fra','eng')">Deutsch</a></td>
      </tr>
   </table>
   <table class="formcontent">
      <tr><td width="200" valign="top" nowrap>Titel</td><td><html:text property="titel_eng" size='40' maxlength='64'/> </td></tr>
      <tr><td valign="top" nowrap>Korte titel</td><td><html:text property="kortetitel_eng" size='40' maxlength='40'/></td></tr>
      <tr><td valign="top" nowrap>Omschrijving</td><td><html:textarea property="omschrijving_eng" cols='40' rows='4'/> </td></tr>
   </table>
</div>
<div id="de" style="display: none">
   <table class="stepscontent">
      <tr>
         <td class="stepother"><a href="javascript:showTab('nl','fra','eng','de')">Nederlands</a></td>
         <td class="stepother"><a href="javascript:showTab('fra','nl','eng','de')">Frans</a></td>
         <td class="stepother"><a href="javascript:showTab('eng','nl','fra','de')">English</a></td>
         <td class="stepcurrent">Deutsch</td>
      </tr>
   </table>
   <table class="formcontent">
      <tr><td width="200" valign="top" nowrap>Titel</td><td><html:text property="titel_de" size='40' maxlength='64'/> </td></tr>
      <tr><td valign="top" nowrap>Korte titel</td><td><html:text property="kortetitel_de" size='40' maxlength='40'/></td></tr>
      <tr><td valign="top" nowrap>Omschrijving</td><td><html:textarea property="omschrijving_de" cols='40' rows='4'/> </td></tr>
   </table>
</div>

<table>
   <tr>
      <td width="200">&nbsp;</td>
      <td><a href="frames.jsp"><img name="cancel" src="<mm:url page="<%= editwizard_location %>"/>/media/cancel.gif" border="0" alt="Annuleer"/></a></td>
      <%
         WorkflowController workflowController = new WorkflowController(cloud);
         int workflowStatus = WorkflowController.STATUS_IN_BEWERKING;

         if (paginaNodeNumber != null) {
            Node paginaNode = cloud.getNode(paginaNodeNumber);
            workflowStatus = workflowController.getStatus(paginaNode);
         }
         if (((userRole.getRol() == Roles.SCHRIJVER) && (workflowStatus == WorkflowController.STATUS_IN_BEWERKING)) 
            || ((userRole.getRol() == Roles.REDACTEUR) && (workflowStatus <= WorkflowController.STATUS_TE_KEUREN))
            || (userRole.getRol() >= Roles.EINDREDACTEUR)) {
      %>
      <td><input type="image" name="save" src="<mm:url page="<%= editwizard_location %>"/>/media/save.gif" border="0" alt="Bewaar"/></td>
      <% } %>
      <%-- hh
      <%
         }
         if ((userRole.getRol() >= Roles.SCHRIJVER) && (workflowStatus == WorkflowController.STATUS_IN_BEWERKING)) {
      %>
      <td><input type="image" name="toaccept" src="<mm:url page="<%= editwizard_location %>"/>/media/voltooien.gif" border="0" alt="Accepteer om te Keuren"/></td>
      <%
         }         
         if ((userRole.getRol() >= Roles.REDACTEUR) && (workflowStatus >= WorkflowController.STATUS_TE_KEUREN)) {
      %>
      <td><input type="image" name="reject" src="<mm:url page="<%= editwizard_location %>"/>/media/reject.gif" border="0" alt="Afwijzen"/></td>
      <%
         }
         if ((userRole.getRol() >= Roles.REDACTEUR) && (workflowStatus < WorkflowController.STATUS_GOEDGEKEURD)) {
      %>
      <td><input type="image" name="accept" src="<mm:url page="<%= editwizard_location %>"/>/media/goedkeuren.gif" border="0" alt="Goedkeuren"/></td>
      <%
         }
         if (userRole.getRol() >= Roles.EINDREDACTEUR) {
      %>
      <td><input type="image" name="publish" src="<mm:url page="<%= editwizard_location %>"/>/media/publish.gif" border="0" alt="Publiceer"/></td>
      <%
         }
      %>
      --%>
   </tr>
</table>
</html:form>
</mm:cloud>
</body>
</html>