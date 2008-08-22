<jsp:root
    version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0">

  <mm:content>
    <mm:cloud method="asis">
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
      <mm:import externid="referrer">/index.jsp</mm:import>


      <form method="post" action="${mm:link(referrer)}" name="loginForm" onSubmit="return check_passwords()">
        <input type="hidden" name="authenticate"  value="plain"  />
        <input type="hidden" name="command" value="login" />
        <di:translate key="core.username" /><br />
        <input id="loginUsername" type="text" size="20" name="username" value="${newusername}" /> <br />
        <di:translate key="core.password" /><br />
        <input id="loginPassword" type="password" size="20" name="password" value="${newpassword}" /> <br /><br />
        <input class="formbutton" id="loginSubmit" type="submit" value="${di:translate('core.login')}" />
      </form>

      <mm:hasnode number="component.register">
        <br />
        <di:translate key="register.noaccountyet" /><br />
        <di:translate key="register.registeryourself" />
        <mm:treefile page="/register/index.jsp"
                     objectlist="$includePath" referids="$referids" write="false">
          <a href="${_}"><di:translate key="register.here" /></a>
        </mm:treefile>

      </mm:hasnode>
    </mm:cloud>
  </mm:content>
</jsp:root>
