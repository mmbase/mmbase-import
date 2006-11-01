<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:import externid="extraheader" />
  <mm:import externid="extrabody" />
  <mm:node number="$provider" notfound="skipbody">
    <mm:field name="name">
      <mm:compare value="telecoach">
        <mm:redirect page="/telecoach/portal/"/>
      </mm:compare>
    </mm:field>
  </mm:node>
  <mm:import externid="newusername"/>
  <mm:import externid="newpassword"/>

  <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
  <html>
    <head>
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
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/loginpage.css" objectlist="$includePath" referids="$referids" />" />
    <mm:write referid="extraheader" escape="none" />
    <script language="javascript">
    <!--   
      function setFocusOnFirstInput() {
        var form = document.forms[0];
        for (var i=0; i < form.elements.length; i++) {
          var elem = form.elements[i];
          // find first editable field
          var hidden = elem.getAttribute("type"); //.toLowerCase();
          if (hidden != "hidden") {
            elem.focus();
            break;
          }
        }
	   }
    //-->      
    </script>    
    </head>
   <body onload="setFocusOnFirstInput()">
   <script>
      try
      {<% //Prevent from loading /login.jsp in frame %>
         if (top.frames.length > 0)
         {
            top.location.href = document.location.href;
         }
      }
      catch(e){
      }
   </script>
   <div class="content">
    <div class="applicationMenubarCockpit" style="white-space: nowrap">
      <img src="<mm:treefile write="true" page="/gfx/spacer.gif" objectlist="$includePath" />" width="1" height="15" border="0" title="" alt="" />
    </div>
    <div class="providerMenubar" style="white-space: nowrap">
    </div>
    <div class="educationMenubarCockpit" style="white-space: nowrap">
      <mm:node number="component.faq" notfound="skipbody">
        <mm:treeinclude page="/faq/cockpit/general.jsp" objectlist="$includePath" referids="$referids" />
       </mm:node>
      <mm:node number="component.cmshelp" notfound="skipbody">
        <mm:treeinclude page="/cmshelp/cockpit/general.jsp" objectlist="$includePath" referids="$referids" />
      </mm:node>
    </div>
    <div class="columns">
      <div class="columnLeft">
         <img src="<mm:treefile page="/gfx/logo_didactor.gif" objectlist="$includePath" />" width="100%" height="106" border="0" title="Didactor logo" alt="Didactor logo" />
         <mm:node number="component.portalpages" notfound="skipbody">
            <mm:treeinclude page="/portalpages/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" />
            <mm:import id="hasPortalPages">true</mm:import>
         </mm:node>
         <mm:present referid="hasPortalPages" inverse="true">
           <!--  show login box on the left -->
           <div class="ListLeft">
             <mm:include page="loginbox.jsp" />
           </div>
         </mm:present>
       </div>
       <div class="columnMiddle">
         <iframe width="100%" height="100%" src="<mm:treefile page="/firstcontent.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>" name="content" frameborder="0">
         </iframe>
       </div>
       <mm:treeinclude page="/rightcolumn.jsp" objectlist="$includePath" referids="$referids" >
        <mm:param name="hasPortalPages" value="$hasPortalPages" />
       </mm:treeinclude>
      </div>
    </div>
  </body>
</html>
</mm:cloud>

<mm:cloud method="delegate" jspvar="cloud" authenticate="didactor-logout"/>

</mm:content>
