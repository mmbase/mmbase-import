<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:import externid="message"/>


<mm:cloud>

   <di:translate key="core.validatelogin_any_error_header" />

   <br/>
   <br/>

   <b><mm:write referid="message"/></b>

   <br/>
   <br/>


   <a href="login.jsp"><di:translate key="core.login"/></a>
</mm:cloud>


