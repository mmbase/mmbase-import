<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>
<%@ page import = "java.util.HashMap" %>
<%@ page import = "java.util.HashSet" %>
<%@ page import = "java.util.SortedMap" %>
<%@ page import = "java.util.TreeMap" %>

<%@ page import = "nl.didactor.component.education.utils.EducationPeopleConnector" %>

<mm:content postprocessor="reducespace" expires="0" type="text/html" encoding="UTF-8" escaper="entities">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<% //education-people connector
   EducationPeopleConnector educationPeopleConnector = new EducationPeopleConnector(cloud);
%>


<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
   <meta name="description" content="Didactor is een open source E-learning omgeving, ontwikkeld door The Mediator Group. Deze elektronische leeromgeving is 100% webbased en gebaseerd op didactische principes. Didactor is ontwikkeld mbv MMbase, Java en XML en maakt blended learning mogelijk" />
   <meta name="keywords" content="didactor, mediator, didactiek, didactisch, webbased, platformonafhankelijk, group, elo, lms, lcms, leeromgeving, on-line, java, sun, mmbase, opleidingen, opleiding, events, e-learning, blended, learning, educatie, training, brakel, becking, hof, puntedu, leren, kennisoverdracht, open, source, standaarden, scorm, eml, cursus, bedrijfsopleiding, universiteit, digitaal, digitale, onderwijs, overheid, zorg, school, congres, bijeenkomst, event, kennis, congres, leeromgeving, didactiek, IEEE-lom, EML, scorm, vraaggestuurd, leerobjecten, netg" />
   <meta name="copyright" content="" />
   <meta name="author" content="The Mediator Group" />
   <meta name="rating" content="General" />
   <meta name="robots" content="all" />
    <title>Didactor</title>
  </mm:param>
</mm:treeinclude>



<%
   String sUserSettings_PathBaseDirectory = getServletContext().getInitParameter("filemanagementBaseDirectory");
   String sUserSettings_BaseURL = getServletContext().getInitParameter("filemanagementBaseUrl");

   if (sUserSettings_PathBaseDirectory == null || sUserSettings_BaseURL == null)
   {
       throw new ServletException("Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
   }
%>


<div class="columns">

  <div class="columnLeft">

    <img src="<mm:treefile write="true" page="/gfx/logo_didactor.gif" objectlist="$includePath" />" width="100%" height="106" border="0" title="Didactor logo" alt="Didactor logo" />

    <div class="titlefield">
      <di:hasrole role="teacher">
        <di:translate key="core.giveneducation" />
      </di:hasrole>
      <di:hasrole role="teacher" inverse="true">
          <di:translate key="core.followededucation" />
      </di:hasrole>
    </div>

    <div class="ListLeft">
       <%@include file="listleft.jsp"%>
    </div>
  </div>

  <div class="columnMiddle">
     <mm:node number="$provider" notfound="skipbody">
        <mm:treeinclude page="/welcome.jsp" objectlist="$includePath" />
     </mm:node>

     <%-- only show link to public portfolios for guests --%>
     <mm:node number="component.portfolio" notfound="skipbody">
       <mm:compare referid="user" value="0">
         <div>
           <a href="<mm:treefile write="true" page="/portfolio/listall.jsp" objectlist="$includePath" />"><di:translate key="core.listallportfolios" /></a>
         </div>
       </mm:compare>
     </mm:node>
  </div>

  <div class="columnRight">
   <%-- list of all teachers that are online for a specific class --%>
    <div class="titlefield2">
      <di:translate key="core.teacherheader" />
    </div>
    <div class="ListTeachers">
      <mm:treeinclude write="true" page="/users/users.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="mode">teachers</mm:param>
      </mm:treeinclude>
      <mm:treeinclude write="true" page="/users/users.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="mode">coaches</mm:param>
      </mm:treeinclude>
<%--
       <img src="<mm:treefile write="true" page="/gfx/icon_offline.gif" objectlist="$includePath" />" width="6" height="12" title="offline" alt="offline" />
      <a href="" class="users">Beheerder (test)</a><br />
--%>
    </div>
   <%-- list of all students that are online for a specific class --%>
    <div class="titlefield">
      <di:translate key="core.studentheader" />
    </div>
    <div class="ListStudents">
      <mm:treeinclude write="true" page="/users/users.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="mode">students</mm:param>
      </mm:treeinclude>
    </div>
  </div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath"  referids="$referids" />
</mm:cloud>
</mm:content>
