<%@page import="com.finalist.tree.*,nl.leocms.util.*,nl.leocms.authorization.*,java.util.TreeMap" %>
<%@include file="/taglibs.jsp" %>
<cache:flush scope="application"/>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Pagina-indeling: sorteren van detailpagina's</title>
</head>
<body onLoad="fillHidden()">
<style>
input.button { width : 100; }
</style>
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<h2>Pagina's herschikken :
<%
   String parentID = request.getParameter("parent");
   RubriekHelper h=new RubriekHelper(cloud);
   out.print( h.getPathToRootString(parentID));
   
   TreeMap subObjects = h.getSubObjects(parentID,true);
       
%>
</h2>
<b>Let op: De eerste pagina in de lijst is de hoofdpagina van de rubriek!</b>
<script language='javascript'>
var values = new Array(<%= subObjects.size() %>);
<% TreeMap seI = (TreeMap) subObjects.clone(); 
   int i = 0;
   while(!seI.isEmpty()) {
      Integer nextKey = (Integer) seI.firstKey();
      %>
      values[<%= i %>]=<%= seI.get(nextKey) %>;
      <%
      seI.remove(nextKey);
      i++;
   }
%>
</script>
<html:form action="/editors/paginamanagement/ReorderPaginaAction.eb">
    <table><tr><td width='200px;'>
    <select size='10' style="width:100%" name="paginalijst">
    <% seI = (TreeMap) subObjects.clone(); 
       while(!seI.isEmpty()) {
         Integer nextKey = (Integer) seI.firstKey();
         %>
         <option>
            <mm:node number="<%= "" + seI.get(nextKey) %>">
               <mm:field name="titel">
                  <mm:isempty><mm:field name="naam"/> (rubriek)</mm:isempty>
                  <mm:isnotempty><mm:write /></mm:isnotempty>
               </mm:field>
            </mm:node>
         </option>
         <%
         seI.remove(nextKey);
       }
    %>
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
    var select = document.forms[0].paginalijst;
    var elementToMove = select.selectedIndex;
    if (elementToMove>0) {
        swap(elementToMove,elementToMove-1);
        select.selectedIndex--;
        fillHidden();
    }
}
function moveDown() {
    var select = document.forms[0].paginalijst;
    var elementToMove = select.selectedIndex;
    if (elementToMove!=-1) {
        if (elementToMove<document.forms[0].paginalijst.options.length-1) {
            swap(elementToMove+1,elementToMove);
            select.selectedIndex++;
            fillHidden();
        }
    }
}

function swap(index1, index2) {
        var select = document.forms[0].paginalijst;
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
