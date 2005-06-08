<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@ page import = "java.util.HashMap" %>
<%@ page import = "java.util.Iterator" %>

<%@ page import = "nl.didactor.education.utils.EducationPeopleConnector" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<% //education-people connector
   EducationPeopleConnector educationPeopleConnector = new EducationPeopleConnector(cloud);
%>


<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
   <meta http-equiv="Content-Style-Type" content="text/css" />
   <meta http-equiv="Content-Language" content="nl" />
   <meta name="description" content="Didactor is een open source E-learning omgeving, ontwikkeld door The Mediator Group. Deze elektronische leeromgeving is 100% webbased en gebaseerd op didactische principes. Didactor is ontwikkeld mbv MMbase, Java en XML en maakt blended learning mogelijk" />
   <meta name="keywords" content="didactor, mediator, didactiek, didactisch, webbased, platformonafhankelijk, group, elo, lms, lcms, leeromgeving, on-line, java, sun, mmbase, opleidingen, opleiding, events, e-learning, blended, learning, educatie, training, brakel, becking, hof, puntedu, leren, kennisoverdracht, open, source, standaarden, scorm, eml, cursus, bedrijfsopleiding, universiteit, digitaal, digitale, onderwijs, overheid, zorg, school, congres, bijeenkomst, event, kennis, congres, leeromgeving, didactiek, IEEE-lom, EML, scorm, vraaggestuurd, leerobjecten, netg" />
   <meta name="copyright" content="" />
   <meta name="author" content="The Mediator Group" />
   <meta name="rating" content="General" />
   <meta name="robots" content="all" />
    <title>Didactor</title>
  </mm:param>
</mm:treeinclude>

<div class="columns">

  <div class="columnLeft">

    <img src="<mm:treefile write="true" page="/gfx/logo_didactor.gif" objectlist="$includePath" />" width="100%" height="106" border="0" alt="Didactor logo" />

    <div class="titlefield">
      <di:hasrole role="teacher">
        <fmt:message key="GIVENEDUCATION" />
      </di:hasrole>
      <di:hasrole role="teacher" inverse="true">
          <fmt:message key="FOLLOWEDEDUCATION" />
      </di:hasrole>
    </div>

    <div class="ListLeft">
      <%
         HashMap hmapEducations = new HashMap();
      %>
      <mm:node number="$user" jspvar="nodeUser">
         <mm:related path="classrel,classes">
            <mm:node element="classes">
               <mm:field name="number" jspvar="sClassID" vartype="String">
                  <mm:relatednodes type="educations">
                     <mm:field name="number" jspvar="sEducationID" vartype="String">
                        <%
                           hmapEducations.put(sEducationID, sClassID);
                        %>
                     </mm:field>
                  </mm:relatednodes>
               </mm:field>
            </mm:node>
         </mm:related>
         <%
            for(Iterator it = educationPeopleConnector.relatedEducations("" + nodeUser.getNumber()).iterator(); it.hasNext(); )
            {
               String sEducationID = (String) it.next();
               if(!hmapEducations.containsKey(sEducationID)) hmapEducations.put(sEducationID, null);
            }
         %>
      </mm:node>

      <%
         for(Iterator it = hmapEducations.keySet().iterator(); it.hasNext();)
         {
            String sEducation = (String) it.next();
            %>
               <img src="<mm:treefile write="true" page="/gfx/icon_course_notdone.gif" objectlist="$includePath" />" width="13" height="11" border="0" alt="" />
               <a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="education"><%= sEducation %></mm:param>
                 <mm:param name="class"><%= (String) hmapEducations.get(sEducation) %></mm:param>
               </mm:treefile>" class="users" /><mm:node number="<%= sEducation %>"><mm:field name="name"/></mm:node></a> <br />
            <%
         }
      %>
    </div>
  </div>

  <div class="columnMiddle">
    <mm:node number="$provider" notfound="skipbody">
    <mm:treeinclude page="/welcome.jsp" objectlist="$includePath" />
    </mm:node>
    <p>
   <a href="<mm:treefile write="true" page="/portfolio/listall.jsp" objectlist="$includePath" />"><fmt:message key="LISTALLPORTFOLIOS"/></a>
    </p>
  </div>

  <div class="columnRight">
   <%-- list of all teachers that are online for a specific class --%>
    <div class="titlefield2">
      <fmt:message key="TEACHERHEADER" />
    </div>
    <div class="ListTeachers">
      <mm:treeinclude write="true" page="/users/teach_sel.jsp" objectlist="$includePath" referids="$referids" />
<%--
       <img src="<mm:treefile write="true" page="/gfx/icon_offline.gif" objectlist="$includePath" />" width="6" height="12" alt="offline" />
      <a href="" class="users">Beheerder (test)</a><br />
--%>
    </div>
   <%-- list of all students that are online for a specific class --%>
    <div class="titlefield">
      <fmt:message key="STUDENTHEADER" />
    </div>
    <div class="ListStudents">
      <mm:treeinclude write="true" page="/users/stud_sel.jsp" objectlist="$includePath" referids="$referids" />
    </div>
  </div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath"  referids="$referids" />
</mm:cloud>
</mm:content>
