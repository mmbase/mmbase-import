<%
// This jsp page is a central place in the editors.
// It searches the content with the specified criteria.
%>
<%@page import="org.mmbase.bridge.*,
                 java.util.StringTokenizer,
                 nl.leocms.util.ContentHelper,
                 nl.leocms.util.ContentTypeHelper,
                 java.text.*,
                 java.util.Locale,
                 java.util.ArrayList,
                 nl.leocms.util.PropertiesUtil,
                 nl.leocms.authorization.*,
                 org.mmbase.util.logging.*" %>
<%-- hh org.apache.struts.Globals, --%>
<%@include file="/taglibs.jsp" %>
<mm:import externid="language">nl</mm:import>
<mm:import id="referrer"><%=request.getContextPath() %></mm:import>
<mm:import id="jsps"><%= editwizard_location %>/jsp/</mm:import>
<mm:import id="loginmethod">asis</mm:import>
<mm:import id="debug">false</mm:import>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%@include file="settings.jsp" %>
 <html>
   <head>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
     <title>BeheerBibliotheek</title>
     <style>
     <!--
      .erf         { background-color: #C0C0C0; line-height: 100%; font-size: 8pt }
      .normal      { background-color: #CFA0A0; font-size: 8pt }
      body         { font-family: Verdana; font-size: 8pt; background-color: #9DBDD8 }
      td           { font-family: Verdana; font-size: 8pt }
      td.titel     { background-color: #C0C0C0 }
      th           { background-color: #C0C0C0; font-size: 10pt }
      a.th:link    { color: black }
      a.th:visited { color: black }
      a:link       { color: #0000FF }
      a:visited    { color: #0000FF }
      a:hover      { color: #0000FF; text-decoration: none }
      div          { display: none }
      p.paginatitel { font-family: Verdana; font-size: 10pt; font-weight: bold }
      img.button   { cursor: pointer; cursor: hand; border-width=0px; }
      -->
      </style>
      <script language="JavaScript">
         <%@include file="js.jsp" %>
      </script>
   </head>
   <%
   String contentNodeNumber = (String)session.getAttribute("contentmodus.contentnodenumber");
   String closeWindow = request.getParameter("closewindow");
   boolean bodyNotForward = true;
   if ((closeWindow != null) && (closeWindow.equals("true"))) {
      bodyNotForward = false;
      %><body onload="closeWindow()"><%
   } else if ((contentNodeNumber != null) && (!contentNodeNumber.equals(""))) {
      if (popup) {
         Node contentNode = cloud.getNode(contentNodeNumber);
         String nodeManager = contentNode.getNodeManager().getName();
         boolean isInSelectedTypes = (selectedTypes.indexOf(nodeManager) > -1);
         if (isInSelectedTypes) {
            bodyNotForward = false;
            %><body onload="doForward(<%= contentNodeNumber %>, '<%= nodeManager %>')"><%
         }
      }
   }
   if (bodyNotForward) { 
      %><body onload="this.focus()"><%
   }
   %>
   <h3>Beheerbibliotheek</h3>
   <form method="POST"  name="libform" onKeyPress="if(window.event.keyCode == 13) { document.libform.submit(); }" >
      <%@include file="searchtable.jsp" %>
      <% if("advanced".equals(modus)) { %>
         <script language="Javascript">
            if(modus=='simple') {
               toggleAdvanced();
            }
         </script>
      <% } %>
      <p>
      <a href="#" onClick="doFilter();"><img src="pix/filter.gif" align="absmiddle" border="0"/></a>
      <% if (!disableNewContent) {
            String refreshFrameJs = "";
            if (refreshFrame != null && !"".equals(refreshFrame)) {
               refreshFrameJs = "&refreshFrame=" + refreshFrame;
            }
      %>
      <a href="../content/new.jsp?returnUrl=/editors/beheerbibliotheek/index.jsp<%=refreshFrameJs%>"><img src="<mm:url page="<%= editwizard_location %>"/>/media/new.gif" align="absmiddle" border="0"/></a>
      <% } 
         String checked = request.getParameter("popupEditWizards");
         checked = ((checked != null) && (checked.equals("on"))) ? "checked" : "";
         %>
      &nbsp;&nbsp;<input type="checkbox" name="popupEditWizards" <%=checked%>>Contentelement openen in popup
      </p>
		<% boolean show_unused = false; %>
      <%@include file="searchresults.jsp" %>
   </form>
   <form action="../../editors/WizardInitAction.eb" method="post" name="callEditwizardForm">
      <input type="hidden" name="objectnumber"/>
      <input type="hidden" name="returnurl" value="/editors/beheerbibliotheek/index.jsp">
   </form>
   </body>

   </html>
</mm:cloud>