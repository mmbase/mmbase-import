<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
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
      <mm:node number="$user">
        <mm:relatedcontainer path="classrel,classes,educations">
          <mm:related>
            <img src="<mm:treefile write="true" page="/gfx/icon_course_notdone.gif" objectlist="$includePath" />" width="13" height="11" border="0" alt="" />
            <a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
              <mm:param name="education"><mm:field name="educations.number" /></mm:param>
              <mm:param name="class"><mm:field name="classes.number" /></mm:param>
            </mm:treefile>" class="users" /><mm:field name="educations.name" /></a> <br />
          </mm:related>
        </mm:relatedcontainer>
      </mm:node> 
    </div>
  </div>

  <div class="columnMiddle">
    <mm:node number="$provider" notfound="skipbody">
      <mm:field name="name">
        <mm:compare value="telecoach">
          <mm:listnodes type="news" orderby="number" directions="DOWN" max="5">
            <b><mm:field name="title"/></b><br/>
            <mm:field name="body"/><br/>
            <p/>
          </mm:listnodes>
        </mm:compare>
        <mm:compare value="telecoach" inverse="true">
          <p>
          <h1><fmt:message key="WELCOME" /></h1>
          </p>
          <br />
          <p>
            <h3>Bij Didactor, de elektronische leeromgeving.</h3>
          </p>
          <br />
          <p>
            Didactor versie 2.0 Beta
          </p>
        </mm:compare>
      </mm:field>
    </mm:node>
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