<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
  <%@include file="/shared/setImports.jsp" %>

  <mm:import externid="uname"/>
  <mm:import externid="password"/>
  <div class="columns">
    <div class="columnLeft">
    </div>
    <div class="columnMiddle">
      <p>
        <di:translate key="register.thankyou" />
        <ul>
          <li><di:translate key="register.username" />: <mm:write referid="uname" /></li>
          <li><di:translate key="register.password" />: <mm:write referid="password" /></li>
        </ul>
          <a href="<mm:treefile page="/index.jsp" objectlist="$includePath" referids="$referids">
                      <mm:param name="newusername"><mm:write referid="uname" /></mm:param>
                      <mm:param name="newpassword"><mm:write referid="password" /></mm:param>
                   </mm:treefile>"><di:translate key="register.tologin" /></a>
      </p>
    </div>
    <div class="columnRight">
    </div>
  </div>
</mm:cloud>
</mm:content>
