<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
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
		<fmt:message key="FOLLOWEDEDUCATION" />
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
  <mm:import externid="newsid"/>
  <mm:present referid="newsid">
    <mm:node number="$newsid">
  <div class="columnMiddle">
	 <h2><mm:field name="title"/></h2>
	 <h4><mm:field name="subtitle"/></h4>
	 <p class="intro"><mm:field name="intro"/></p>
	 <p>
	 <mm:field name="body"/>
	  </p>
  </div>
  </mm:node>
  </mm:present>

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
