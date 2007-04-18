<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="nl.leocms.versioning.PublishManager,
nl.leocms.workflow.WorkflowController,
nl.leocms.signalering.SignaleringUtil,
nl.leocms.util.ContentTypeHelper" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<mm:import externid="objectnumber" required="true"/>
<mm:import externid="delete">false</mm:import>
<mm:import externid="deletebtn"></mm:import>

<mm:cloud method="http" rank="administrator" jspvar="cloud">

<mm:compare referid="delete" value="true">
<mm:compare referid="deletebtn" value="Verwijderen">
   <mm:deletenode deleterelations="true" referid="objectnumber" jspvar="deleteNode">
   <%-- hh
       WorkflowController wc = new WorkflowController(cloud);
       if (wc.hasWorkflow(deleteNode)) {
           wc.getWorkflowNode(deleteNode).delete(true);
       }
       if (PublishManager.isPublished(deleteNode)) {
           if (ContentTypeHelper.isPagina(deleteNode)) {
               SignaleringUtil.createWijziging(deleteNode, cloud);
           }
           else {
               SignaleringUtil.createLinkWijziging(deleteNode, cloud);
           }
       }
       PublishManager.deletePublishedNode(deleteNode);
   --%>
   </mm:deletenode>
</mm:compare>
<html>
   <script language="JavaScript">
   <!--
    opener.doFilter();
    window.close();
    -->
   </script>
</html>

</mm:compare>
<mm:compare referid="delete" value="false">

 <html>
   <head>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
     <title>BeheerBibliotheek</title>
     <style>
     <!--
      body         { font-family: Verdana; font-size: 8pt; background-color: #AFBFDF }
      table        { width: 100%; font-family: Verdana; font-size: 8pt; background-color: #AFBFDF }
      td           { font-family: Verdana; font-size: 8pt }
      td.titel     { font-size: 10pt; font-weight: bold; background-color: #C0C0C0 }
        input.button  { 
                font-size: 8pt;        
                text-transform: uppercase;
                border-width: 1px;
                border-style: outset;
                padding: 0px 4px 0px 4px;
                text-align: center;
                vertical-align: bottom;
                word-spacing: normal;
                width: 100px;
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
      -->
     </style>

   <script language="JavaScript">
function centerWindow() {
    var w = 0;
    var h = 0;
    if (document.all) {
        w = document.body.clientWidth;
        h = document.body.clientHeight;
    }
    else {
        if (document.layers) {
            w = window.innerWidth;
            h = window.innerHeight;
        }
    }
    if (window.moveTo) {
        window.moveTo((screen.width - w)/2,(screen.height - h)/2);
    }
}
</script>    
   </head>
   <body onload="centerWindow();">

    <mm:node referid="objectnumber">
    <table>
    <tr>
      <td class="titel" colspan="2">
        <mm:nodeinfo type="guitype" />
      </td>
    </tr>
    <tr><td>Titel</td><td> <mm:field name="titel"/> </td></tr>
    <tr><td>Omschrijving</td><td> <mm:field name="omschrijving"/> </td></tr>
    <tr><td>Creatiedatum</td><td> <mm:field name="creatiedatum"><mm:time format="dd MMMM yyyy" /></mm:field> </td></tr>
    <tr><td>Laatst gewijzigd</td><td> <mm:field name="datumlaatstewijziging"><mm:time format="dd MMMM yyyy" /></mm:field> </td></tr>
    </table>

    <form>
        <input type="hidden" name="delete" value="true">
        <input type="hidden" name="objectnumber" value="<mm:field name="number"/>">
        <input type="submit" name="deletebtn" value="Verwijderen" class="button" />
        <input type="submit" name="cancelbtn" value="Annuleren" class="button" />
    </form>

    </mm:node>

   </body>
  </html>
</mm:compare>

</mm:cloud>