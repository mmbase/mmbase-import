<%-- get footer --%>
<mm:present referid="login">
<div id="footer">
<a href="disclaimer.jsp?login">disclaimer</a> | <a href="privacy.jsp?login">privacy statement</a>
</div>
</mm:present>
<mm:notpresent referid="login">
<div id="footer">
<a href="disclaimer.jsp">disclaimer</a> | <a href="privacy.jsp">privacy statement</a>
</div>
</mm:notpresent>