<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="nl.leocms.evenementen.forms.SubscribeAction,java.util.*" %>
<%@include file="/taglibs.jsp" %>
<%@include file="calendar.jsp"  %>
<mm:import externid="e" jspvar="nodenr" id="nodenr">-1</mm:import>
<mm:import externid="p" jspvar="parent_number" id="parent_number">-1</mm:import>
<mm:import externid="s" jspvar="snumber" id="snumber">-1</mm:import>
<mm:import externid="d" jspvar="dnumber" id="dnumber">-1</mm:import>
<mm:import externid="a" jspvar="action"></mm:import>

<mm:cloud method="http" jspvar="cloud" rank="basic user">
<mm:node number="$nodenr" jspvar="thisEvent">
<mm:node number="$parent_number" jspvar="thisParent">
<mm:node number="$snumber" jspvar="thisSubscription">
<mm:node number="$dnumber" jspvar="thisParticipant">

<html>
<head>
<title>Bevestingingsbrief Aanmelding</title>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<style rel="stylesheet" type="text/css">
	body, td {
	   color:#000000;
      background-color: #FFFFFF;
      font: 14px;
	}
   table {
      margin-left: -3px;
   }
</style>
<% if(!"doprint".equals(action)) { %>
<script type="text/vbscript" language="VBScript">
   Sub DoWord(theTemplate,AddrLines)
      Dim theApp
      Dim theDoc
      Dim theVBComponent
      Dim theCodeLineNr
      Dim theCodeLine
      Dim theCommandBar 
      Set theApp = CreateObject("Word.Application")
      theApp.Visible=True
      theApp.Activate
   
      Set theDoc = theApp.Application.Documents.Add(theTemplate)
      Set theVBComponent = theApp.VBE.VBProjects("TemplateProject").VBComponents.Item("ModStandaarden")
      theCodeLineNr = theVBComponent.CodeModule.ProcBodyLine("VNM_BriefOpstarten", vbext_pk_Proc)
      theCodeLine = "UsrFrmBrief.txtOrganisatie.MultiLine = True"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 1, theCodeLine
      theCodeLine = "UsrFrmBrief.txtOrganisatie.Text = """ & SmartConcat(AddrLines(1), AddrLines(2)) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 2, theCodeLine
      theCodeLine = "UsrFrmBrief.txtAfdeling.Text = """ & AddrLines(3) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 3, theCodeLine
      theCodeLine = "UsrFrmBrief.txtNaam.Text = """ & AddrLines(4) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 4, theCodeLine
      theCodeLine = "UsrFrmBrief.txtAdres.Text = """ & AddrLines(5) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 5, theCodeLine
      theCodeLine = "UsrFrmBrief.txtHuisnummer.Text = """ & AddrLines(6) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 6, theCodeLine
      theCodeLine = "UsrFrmBrief.txtToevoeging.Text = """ & AddrLines(7) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 7, theCodeLine
      theCodeLine = "UsrFrmBrief.txtPostcode.Text = """ & AddrLines(8) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 8, theCodeLine
      theCodeLine = "UsrFrmBrief.txtPlaats.Text = """ & AddrLines(9) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 9, theCodeLine
      theCodeLine = "UsrFrmBrief.txtLand.Text = """ & AddrLines(10) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 10, theCodeLine
      theCodeLine = "UsrFrmBrief.TxtAanhef.Text = """ & AddrLines(11) & """"
      theVBComponent.CodeModule.InsertLines theCodeLineNr + 11, theCodeLine
      'theCodeLine = "UsrFrmBrief.txtOrganisatie.Height = 48"
      'theVBComponent.CodeModule.InsertLines theCodeLineNr + 12, theCodeLine
      theDoc.AttachedTemplate.Saved = True 
      Set theCommandBar = theApp.CommandBars("Menu Bar").Controls(2).CommandBar
      theCommandBar.Controls(1).Execute
      theApp.Application.Documents("Document1").Close(wdDoNotSaveChanges)
      Set theDoc = Nothing
      Set theVBComponent = Nothing
      Set theCommandBar = Nothing
      Set theApp = Nothing
   End Sub

   Sub wordconnection_OnClick()
      Dim dataToBePasted(11)
      template = "F:\\Cabs\\WrdStartNM\\vnm2006a.dot" '<pad+naam word-sjabloon>
      dataToBePasted(1) =  "" '< organisatie naam boven >
      dataToBePasted(2) =  "" '< organisatie naam > 
      dataToBePasted(3) =  "" '< organisatie naam onder >
      dataToBePasted(4) =  "<%= thisParticipant.getStringValue("titel") %>" '< persoon >
      dataToBePasted(5) =  "<%= thisParticipant.getStringValue("straatnaam") %>" '< straat >
      dataToBePasted(6) =  "<%= thisParticipant.getStringValue("huisnummer") %>" '< huisnummer >
      dataToBePasted(7) =  "" '< huisnummer toevoeging >
      dataToBePasted(8) =  "<%= thisParticipant.getStringValue("postcode") %>" '<postcode >
      dataToBePasted(9) =  "<%= thisParticipant.getStringValue("plaatsnaam") %>" '< plaats >
      dataToBePasted(10) = "<%= thisParticipant.getStringValue("land") %>" '< land >
      dataToBePasted(11) = "<%= SubscribeAction.getMessage(thisEvent,thisParent,thisSubscription,thisParticipant,"", "html").replace('\n',' ').replace('"','\'') %>"  '< aanhef >
      Call DoWord(template,dataToBePasted)
    End Sub

   Function SmartConcat(string1, string2) 
      If string1 = "" Then
        SmartConcat = string2
      ElseIf string2 = "" Then
        SmartConcat = string1
      Else
        SmartConcat = string1 & Chr(11) & string2
      End if
   End Function
</script>
<% } %>
</head>
<body style="overflow:auto;" <%= ("doprint".equals(action) ? "onload='self.print();'": "") %>>
<% if(!"doprint".equals(action)) { %>
<form name="printform" action="">
   <input name="wordconnection" type="button" value="print in huisstijl" style="width:200px;text-align:center;">
   <input name="print" type="submit" value="standaard print" style="width:200px;text-align:center;">
   <input type="hidden" name="e" value="<%= nodenr %>" >
   <input type="hidden" name="p" value="<%= parent_number %>" >
   <input type="hidden" name="s" value="<%= snumber %>" >
   <input type="hidden" name="d" value="<%= dnumber %>" >
   <input type="hidden" name="a" value="doprint" >
</form>
<% } %>
<% 
cal.setTime(new Date());
%>
<% for(int i = 0; i<8; i++) { %><br/><% } %>
's Graveland, <%= cal.get(Calendar.DAY_OF_MONTH) + " " + months_lcase[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR) %>
<% for(int i = 0; i<4; i++) { %><br/><% } %>
<%= thisParticipant.getStringValue("prefix") %><br/>
<%= thisParticipant.getStringValue("straatnaam") %> <%= thisParticipant.getStringValue("huisnummer") %><br/>
<%= thisParticipant.getStringValue("postcode") %> <%= thisParticipant.getStringValue("plaatsnaam") %><br/>
<%= thisParticipant.getStringValue("land") %><br/><br/>

<%= SubscribeAction.getMessage(thisEvent,thisParent,thisSubscription,thisParticipant,"", "html") %>
</body>
</html>
</mm:node>
</mm:node>
</mm:node>
</mm:node>
</mm:cloud>
