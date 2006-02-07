<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="newusername"/>
<mm:import externid="newpassword"/>
  <div class="titlefield">
    <di:translate key="core.logindidactor" />
    <mm:import externid="message"/>
    <mm:isnotempty referid="message">
      <br/>
      Error <mm:write referid="message" escape="none"/>
    </mm:isnotempty>
    <mm:import externid="reason" />
    <mm:isnotempty referid="reason">
      <mm:compare referid="reason" value="failed">
        <br />Error: <di:translate key="core.passwordincorrect" />
      </mm:compare>
    </mm:isnotempty>
  </div>
  <br />
  <script>
     function check_passwords(){
     if((document.getElementById("loginUsername").value.length == 0) && (document.getElementById("loginPassword").value.length == 0)) return false;
     else return true;
     }
  </script>
  <mm:import externid="referrer" required="true" />
  <form method="post" action="<mm:write referid="referrer" />" name="loginForm" onSubmit="return(check_passwords())">
    <input type="hidden" name="authenticate"  value="plain"  />
    <input type="hidden" name="command" value="login" />
    <di:translate key="core.username" /><br />
    <input id="loginUsername" type="text" size="20" name="username" value="<mm:write referid="newusername"/>" /> <br />
    <di:translate key="core.password" /><br />
    <input id="loginPassword" type="password" size="20" name="password" value="<mm:write referid="newpassword"/>" /> <br /><br />
    <input class="formbutton" id="loginSubmit" type="submit" value="<di:translate key="core.login" />" />
  </form>
  <mm:node number="component.register" notfound="skipbody">
    <br />
    <di:translate key="register.noaccountyet" /><br />
    <di:translate key="register.registeryourself" /> <a href="<mm:treefile page="/register/index.jsp" objectlist="$includePath" referids="$referids" />"><di:translate key="register.here" /></a>
  </mm:node>
</mm:cloud>
</mm:content>

