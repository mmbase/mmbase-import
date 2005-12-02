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
  </head>
  <body>
    <script>
      try {<% //Prevent from loading /login.jsp in frame %>
        if (top.frames.length > 0) {
          top.location.href = document.location.href;
        }
      } catch(err) {
      }
    </script>

    <div class="content">
      <div class="applicationMenubarCockpit" style="white-space: nowrap">
        <img src="<mm:treefile write="true" page="/gfx/spacer.gif" objectlist="$includePath" />" width="1" height="15" border="0" alt="" />
      </div>
      <div class="providerMenubar" style="white-space: nowrap">
      </div>
      <div class="educationMenubarCockpit" style="white-space: nowrap">
        <table class="pixellijn" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td nowrap>&nbsp;</td>
          </tr>
        </table>
      </div>
      <div class="columns">
        <div class="columnLeft">
          <img src="<mm:treefile page="/gfx/logo_didactor.gif" objectlist="$includePath" />" width="100%" height="106" border="0" alt="Didactor logo" />
          <div class="titlefield">
            <di:translate key="core.logindidactor" />
            <%-- show login error message if it is available --%>
            <mm:import externid="message" />
            <mm:isnotempty referid="message">
              <br />Error: <mm:write referid="message" />
            </mm:isnotempty>
          </div>
          <div class="ListLeft">
            <br />
            <script>
              function check_passwords() {
                if((document.getElementById("loginUsername").value.length == 0) && (document.getElementById("loginPassword").value.length == 0)) {
                  return false;
                } else { 
                  return true;
                }
              }
            </script>
            <mm:import externid="referrer" required="true" />
            <form method="post" action="<mm:write referid="referrer" />" name="loginForm" onSubmit="return(check_passwords())">
              <input type="hidden" name="authenticate"  value="name/password"  />
              <input type="hidden" name="command" value="login" />
              <di:translate key="core.username" /><br />
              <input id="loginUsername" type="text" size="20" name="username" value="<mm:write referid="newusername"/>" /> <br />
              <di:translate key="core.password" /><br />
              <input id="loginPassword" type="password" size="20" name="password" value="<mm:write referid="newpassword"/>" /> <br /><br />
              <input class="formbutton" id="loginSubmit" type="submit" value="<di:translate key="core.login" />" />
            </form>
            <mm:node number="component.register" notfound="skipbody">
              <br />
              <di:translate key="register.noaccountyet" /> <br />
              <di:translate key="register.registeryourself" /> <a href="<mm:treefile page="/register/index.jsp" objectlist="$includePath" referids="$referids" />"><di:translate key="register.here" /></a>.
            </mm:node>
          </div>
        </div>
        <div class="columnMiddle">
          <p>
            <h1><di:translate key="core.welcome" /></h1>
          </p>
          <br />
          <p>
            <h3>Bij Didactor, de elektronische leeromgeving.</h3>
          </p>
          <br />
          <p>
            Didactor versie 2.0 Beta
          </p>
          <p>
            <mm:node number="component.portfolio" notfound="skipbody">
              <a href="<mm:treefile write="true" page="/portfolio/listall.jsp" objectlist="$includePath" />"><di:translate key="core.listallportfolios" /></a>
            </mm:node>
          </p>
        </div>
        <div class="columnRight">
          <div class="titlefield2">
            <di:translate key="core.news" />
          </div>
          <div class="ListRight">
            <mm:listnodes type="news" orderby="number" directions="DOWN" max="5">
              <b><mm:field name="title"/></b><br/>
              <mm:field name="body"/><br/>
              <p/>
            </mm:listnodes>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
</mm:cloud>
</mm:content>
