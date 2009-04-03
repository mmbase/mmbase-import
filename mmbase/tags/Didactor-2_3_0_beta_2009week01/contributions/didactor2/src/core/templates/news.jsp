<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate">
<jsp:directive.include file="/shared/setImports.jsp" />

<mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
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
    <img src="<mm:treefile write="true" page="/gfx/logo_didactor.gif" objectlist="$includePath" />" width="100%" height="106" border="0" title="Didactor logo " alt="Didactor logo" />

    <div class="titlefield">
      <di:translate key="core.followededucation" />
    </div>

    <div class="ListLeft">
      <jsp:directive.include file="cockpit/index/listleft.jsp" />
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
      <di:translate key="core.teacherheader" />
    </div>
    <div class="ListTeachers">
      <mm:treeinclude write="true" page="/users/teach_sel.jsp" objectlist="$includePath"
                      referids="$referids" notfound="skip" />
<%--
       <img src="<mm:treefile write="true" page="/gfx/icon_offline.gif" objectlist="$includePath" />" width="6" height="12" alt="offline" />
      <a href="" class="users">Beheerder (test)</a><br />
--%>
    </div>
   <%-- list of all students that are online for a specific class --%>
    <div class="titlefield">
      <di:translate key="core.studentheader" />
    </div>
    <div class="ListStudents">
      <mm:treeinclude write="true" page="/users/stud_sel.jsp" objectlist="$includePath"
                      referids="$referids" notfound="skip" />
    </div>
  </div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath"  referids="$referids" />
</mm:cloud>
</mm:content>
