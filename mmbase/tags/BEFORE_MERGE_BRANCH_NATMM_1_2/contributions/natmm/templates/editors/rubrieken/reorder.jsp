<%@page import="com.finalist.tree.*,nl.leocms.util.*,nl.leocms.authorization.*" %>
<%@include file="/taglibs.jsp" %>
<cache:flush scope="application"/>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Rubriekenindeling</title>
</head>
<body onLoad="fillHidden()">
<style>
input.button { width : 100; }
</style>
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<h2>Herschikken :
<%
    RubriekHelper h=new RubriekHelper(cloud);
    out.print( h.getPathToRootString(request.getParameter("parent")));
%>
<h2>
<script language='javascript'>
    <mm:list nodes='<%=request.getParameter("parent")%>' path='rubriek,childrel,rubriek2' fields='rubriek2.number,rubriek2.naam' orderby='childrel.pos' searchdir='DESTINATION'>
    <mm:first>var values = new Array(<mm:size/>);</mm:first>
    values[<mm:index/>-1]=<mm:field name="rubriek2.number"/>;
</mm:list>
</script>
<html:form action="/editors/rubrieken/ReorderAction.eb">
    <table><tr><td width='200px;'>
    <select size='10' style="width:100%" name="rubrieken">
    <mm:list nodes='<%=request.getParameter("parent")%>' path='rubriek,childrel,rubriek2' fields='rubriek2.number,rubriek2.naam' orderby='childrel.pos' searchdir='DESTINATION'>
        <option><mm:field name="rubriek2.naam"/></option>
    </mm:list>
    </select>
    </td>
    <td> 
        <img src="../img/up.gif" onClick="moveUp()" onDblClick="moveUp()" /><br/>
        <img src="../img/down.gif" onClick="moveDown()" onDblClick="moveDown()"/>
    </td>
    </tr>
    </table>
    <input type="hidden" name='ids'/>
    <input type="hidden" name="parent" value="<%=request.getParameter("parent")%>"/>
    <input type='submit' name="submit" value='Opslaan' class="button"/>
    <html:cancel value='Annuleren' styleClass="button"/>
</html:form>
</mm:cloud>
<script language="javascript">
function moveUp() {
    var select = document.forms[0].rubrieken;
    var elementToMove = select.selectedIndex;
    if (elementToMove>0) {
        swap(elementToMove,elementToMove-1);
        select.selectedIndex--;
        fillHidden();
    }
}
function moveDown() {
    var select = document.forms[0].rubrieken;
    var elementToMove = select.selectedIndex;
    if (elementToMove!=-1) {
        if (elementToMove<document.forms[0].rubrieken.options.length-1) {
            swap(elementToMove+1,elementToMove);
            select.selectedIndex++;
            fillHidden();
        }
    }
}

function swap(index1, index2) {
        var select = document.forms[0].rubrieken;
        var options = select.options;

        var oldValue = values[index2];
        values[index2]=values[index1];
        values[index1]=oldValue;
        var t = options[index1].text;
        options[index1].text = options[index2].text;
        options[index2].text = t;
}

function fillHidden() {
    var value="";
    for (var i=0;i<values.length;i++) { 
        value = value +  values[i];
        if (i<values.length-1) {
            value=value+",";
        }
    }
    document.forms[0].ids.value=value;
}

</script>
</body>
