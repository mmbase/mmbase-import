<%@page import="com.finalist.tree.*,nl.leocms.rubrieken.*,nl.leocms.authorization.forms.*,nl.leocms.signalering.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<html>
<head>
  <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/list.css" type="text/css" rel="stylesheet"/>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/list.css" type="text/css" rel="stylesheet"/>
   <title>Signalering</title>
   <style>
        tr.listheader a:link    { color: #FFFFFF; }
        tr.listheader a:visited { color: #FFFFFF; }
        tr.listheader a:hover   { color: #FFFFFF; }
        tr.listheader a:active  { color: #FFFFFF; }
    
    input { width: auto;}
    tr.itemrow { cursor: default; }
    
        input.button  {         
                font-weight: bold;
                text-transform: uppercase;
                border-width: 1px;
                border-style: outset;
                padding: 0px 4px 0px 4px;
                text-align: center;
                word-spacing: normal;
                width: auto;
                margin: 2px 0px 2px 0px;
                cursor: pointer;
                cursor: hand;
        color: #FFFFFF;
        background-color: #4A7BA5;
        border-top-color: #FFFFFF;
        border-right-color: #000000;
        border-bottom-color: #000000;
        border-left-color: #FFFFFF;
        }
   </style>


<SCRIPT language="Javascript">
function checkAllBoxes(id) {
    var i=0;
    while(id.elements[i]) {
        id.elements[i].checked=1;
        i++;
    }
}

function uncheckAllBoxes(id) {
    var j=0;
    while(id.elements[j]) {
        id.elements[j].checked=0;
        j++;
    }
}

    function objMouseOver(el) {
         el.className="itemrow-hover";
    }
    
    function objMouseOut(el) {
         el.className="itemrow";
    }
    
function objClick(el) {
  var href = el.parentElement.getAttribute("href")+"";
   if (href.length<10) return;
   document.location=href;
}
</SCRIPT>

</head>

<mm:import externid="directions"/>
<mm:import externid="orderby"/>

<mm:notpresent referid="directions">
   <mm:remove referid="directions"/>
   <mm:import id="directions">UP</mm:import>
</mm:notpresent>
<mm:notpresent referid="orderby">
   <mm:remove referid="orderby"/>
   <mm:import id="orderby">signalering.creatiedatum</mm:import>
</mm:notpresent>

<mm:compare referid="directions" value="UP" inverse="true">
   <mm:import id="newdirections">UP</mm:import>
</mm:compare>
<mm:compare referid="directions" value="DOWN" inverse="true">
   <mm:import id="newdirections">DOWN</mm:import>
</mm:compare>

<body>
<h2>Takenlijst</h2>
<%
   String username = cloud.getUser().getIdentifier();
   String constraints = "[users.account]='" + username + "'";
%>
<mm:list path="users,workflowitem,contentelement" constraints="<%= constraints + " AND workflowitem.status > 0" %>">
   <mm:first>
      <mm:import id="wokflowitems"><mm:size/></mm:import>   
   </mm:first>
</mm:list>

<mm:present referid="wokflowitems">
   <mm:compare referid="wokflowitems" value="1">
   Er staat momenteel <a href="../../workflow/workflow.jsp"><mm:write referid="wokflowitems"/> item in uw workflow</a> te wachten op goedkeuring of publicatie
   </mm:compare>
   <mm:compare referid="wokflowitems" value="1" inverse="true">
   Er staan momenteel <a href="../../workflow/workflow.jsp"><mm:write referid="wokflowitems"/> items in uw workflow</a> te wachten op goedkeuring of publicatie
   </mm:compare>
</mm:present>
<mm:notpresent referid="wokflowitems">
   Er staan momenteel geen items in uw workflow
</mm:notpresent>

<html:form method="post" action="/editors/signalering/DeleteSignaleringAction" name="<%= username %>" type="nl.leocms.signalering.forms.DeleteSignaleringForm">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr>
      <td>
         <table class="listcontent">
            <tr class="listheader">
               <th></th>
               <th><a href="<mm:url><mm:param name="orderby">signalering.creatiedatum</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Creatie</a></th>
               <th><a href="<mm:url><mm:param name="orderby">signalering.verloopdatum</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Deadline</a></th>
               <th><a href="<mm:url><mm:param name="orderby">signalering.type</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Content type</a></th>
               <th>Afzender</th>
               <th><a href="<mm:url><mm:param name="orderby">contentelement.titel</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Betreft</a></th>
               <th><a href="<mm:url><mm:param name="orderby">signalering.omschrijving</mm:param><mm:param name="directions"><mm:write referid="newdirections"/></mm:param></mm:url>">Omschrijving</a></th>
            </tr>
            <%
               constraints += " AND [signalering.status] != 'remove'";
            %>
            
            <mm:list path="users,aan,signalering,betreft,contentelement" orderby="$orderby" constraints="<%= constraints %>" directions="$directions">
                            <mm:remove referid="builder"/>

                            <mm:node element="contentelement">
                                <mm:import id="builder"><mm:nodeinfo type="type"/></mm:import>
                                <mm:compare referid="builder" value="pagina" inverse="true">
                                    <tr class="itemrow" onMouseOver="objMouseOver(this);" 
                                            onMouseOut="objMouseOut(this);"
                                            href="../WizardInitAction.eb?objectnumber=<mm:field name="number"/>&returnurl=/editors/signalering/takenlijst.jsp">
                                </mm:compare>
                                <mm:compare referid="builder" value="pagina">
                                    <tr class="itemrow" onMouseOver="objMouseOver(this);" 
                                            onMouseOut="objMouseOut(this);"
                                            href="../paginamanagement/frames.jsp?page=<mm:field name="number"/>">
                                </mm:compare>
                            </mm:node>
                
                  <td><input type="checkbox" name="checked[<mm:field name="signalering.number"/>]" value="1"></td>
                  <td   onMouseDown="objClick(this);"><mm:field name="signalering.creatiedatum"><mm:time format="dd-MM-yy"/></mm:field></td>
                  <td   onMouseDown="objClick(this);">
                     <mm:remove referid="verloop"/>
                     <mm:import id="verloop"><mm:field name="signalering.verloopdatum"/></mm:import>
                     <mm:compare referid="verloop" value="-1" inverse="true">
                        <mm:write referid="verloop"><mm:time format="dd-MM-yy"/></mm:write>
                     </mm:compare>
                     <mm:compare referid="verloop" value="-1">
                        nvt
                     </mm:compare>
                  </td>
                  <td   onMouseDown="objClick(this);">
                     <mm:field name="signalering.type" jspvar="type" vartype="Integer" write="false">
                     <%= SignaleringUtil.SIGNALERING_TYPES[type.intValue()] %>
                     </mm:field>
                  </td>
                  <td   onMouseDown="objClick(this);">
                     <mm:node element="signalering">
                        <mm:related path="afzender,users" max="1">
                           <mm:field name="users.voornaam"/> <mm:field name="users.tussenvoegsel"/> <mm:field name="users.achternaam"/>
                        </mm:related>
                     </mm:node>
                  </td>
                  <td   onMouseDown="objClick(this);">
                     <mm:remove referid="buildername"/>
                     <mm:import id="buildername"><mm:field name="signalering.builder"/></mm:import>
                     <mm:compare referid="buildername" value="" inverse="true">
                        <mm:nodeinfo nodetype="$buildername" type="guitype"/>
                     </mm:compare>
                     <mm:compare referid="buildername" value="">
                        <mm:node element="contentelement">
                           <mm:nodeinfo type="guitype"/>: <mm:field name="titel"/>
                        </mm:node>
                     </mm:compare>
                  </td>
                  <td   onMouseDown="objClick(this);"><mm:field name="signalering.omschrijving"/></td>
               </tr>
            </mm:list>
        </table>
      </td>
   </tr>
</table>

<table border="0" cellpadding="0" cellspacing="1" width="100%">
   <tr>
      <td>
         <input class="button" type="button" name="checkAll" onClick="checkAllBoxes(<%= username %>)" value="Alles">
         &nbsp;&nbsp;&nbsp;<input class="button" type="button" name="uncheckAll" onClick="uncheckAllBoxes(<%= username %>)" value="Geen">
         &nbsp;&nbsp;&nbsp;<input class="button" type="submit" name="submit" value="Verwijder Selectie">
      </td>
   </tr>
</table>

</html:form>

</mm:cloud>

</body>
</html>
