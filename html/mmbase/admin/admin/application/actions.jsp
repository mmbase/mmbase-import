<%@ page import="org.mmbase.module.core.MMBase,org.mmbase.bridge.*,java.util.*"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp">
<div
  class="mm_c mm_c_core mm_c_b_applications ${requestScope.componentClassName}"
  id="${requestScope.componentId}">
<mm:import externid="application" />
<mm:import externid="cmd" jspvar="cmd" />
<mm:import externid="path" />

<h3>Applications results</h3>

<table summary="results" border="0" cellspacing="0" cellpadding="3">
  <caption>
    Results of your ${cmd} action on application <mm:write referid="application" />.
  </caption>
  <tr>
    <th colspan="2">Results</th>
  </tr><tr>
    <td colspan="2">
	  <mm:compare referid="cmd" value="LOAD">
		<mm:nodefunction module="mmadmin" name="LOAD" referids="application">
		  <mm:field name="RESULT" escape="p" />
		</mm:nodefunction>      
		<mm:import externid="app" jspvar="app" />
<%
   // String cmd = request.getParameter("cmd");
   Module mmAdmin = ContextProvider.getDefaultCloudContext().getModule("mmadmin");
   String msg="";
   if (cmd != null) {
    try {
        Hashtable params=new Hashtable();
        params.put("APPLICATION",app);
        mmAdmin.process(cmd,app,params,request,response);
        msg="<p>"+mmAdmin.getInfo("LASTMSG",request,response)+"</p>";
    } catch (java.lang.reflect.UndeclaredThrowableException ute) {
      Throwable t = ute;
      while (t.getCause() != null) {
      t = t.getCause();
      }
      msg="<p> Error: "+ t + "</p>";
      } catch (Throwable e ) {
      msg="<p> Error: " + e + "</p>";
      }

   }
%>
	  </mm:compare>
	  <mm:compare referid="cmd" value="SAVE">
		<mm:nodefunction module="mmadmin" name="SAVE" referids="application,path">
		  <mm:field name="RESULT" escape="p" />
		</mm:nodefunction>      
	  </mm:compare>
    </td>
  </tr>
  </table>
  
  <p>
	<mm:link page="applications">
	  <a href="${_}"><img src="<mm:url page="/mmbase/style/images/back.png" />" alt="back" /></a>
	  <a href="${_}">Return to Applications Administration</a>
	</mm:link>
  </p>
</div>
</mm:cloud>
