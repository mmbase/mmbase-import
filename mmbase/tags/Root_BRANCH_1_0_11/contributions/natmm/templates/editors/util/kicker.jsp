<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.vastgoed.NelisReader, nl.leocms.util.ApplicationHelper"%>
<mm:cloud name="mmbase" method="http" rank="administrator" jspvar="cloud">
   <mm:import externid="a">none</mm:import>
   <html>
      <head>
         <title>MMBase kicker</title>
         <link rel="stylesheet" type="text/css" href="../css/editorstyle.css">
      </head>
      <body style="overflow:auto;padding:5px;">
         <mm:compare referid="a" value="none">
            Please select the process you want to run:<br/>
            <a href="kicker.jsp?a=unuseditems">UpdateUnusedElements</a><br/>
            <a href="kicker.jsp?a=csvreader">CSVReader</a><br/>
            <a href="kicker.jsp?a=eventnotifier">EventNotifier</a><br/>
            <a href="kicker.jsp?a=google">SiteMapGenerator</a><br/>
            <a href="kicker.jsp?a=dirreader">dirReader</a><br/>
            <a href="kicker.jsp?a=defaultrel">addDefaultRelations</a><br/>
            <%
            ApplicationHelper ap = new ApplicationHelper(cloud);
            if(ap.isInstalled("NMIntra")) {
            %>
            <a href="kicker.jsp?a=updatenelis">Import new Nelis CSV</a><br/>
            <%
            }
            %>
         </mm:compare>
         <mm:compare referid="a" value="unuseditems">
            <% (new nl.leocms.content.UpdateUnusedElements()).run(); %>
         </mm:compare>
         <mm:compare referid="a" value="csvreader">
            <% (new nl.mmatch.CSVReader()).run(); %>
         </mm:compare>
         <mm:compare referid="a" value="eventnotifier">
            <% (new nl.leocms.evenementen.EventNotifier()).run(); %>
         </mm:compare>
         <mm:compare referid="a" value="google">
            <% (new nl.leocms.connectors.Google.output.sitemap.SiteMapGenerator()).run(); %>
         </mm:compare>
         <mm:compare referid="a" value="dirreader">
            <% (new nl.leocms.util.tools.documents.DirReader()).run(); %>
            De documenten zijn ingelezen van <%= NMIntraConfig.getSDocumentsRoot() %><br/>
         </mm:compare>
         <mm:compare referid="a" value="defaultrel">
            <% (new nl.leocms.util.MMBaseHelper(cloud)).addDefaultRelations(); %>
         </mm:compare>
         <mm:compare referid="a" value="updatenelis">
            <% NelisReader.getInstance().readData(); %>
         </mm:compare>
      </body>
   </html>
</mm:cloud>
