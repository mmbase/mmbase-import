<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%@ page import="org.mmbase.module.core.MMBase,
                 org.mmbase.module.core.MMObjectBuilder,
                 org.mmbase.module.core.MMBaseObserver,
                 com.finalist.cmsc.maintenance.log.LoggingMMBaseObserver,
                 java.util.Iterator,
                 com.finalist.cmsc.maintenance.log.LogConstraint"%>
<html>
  <head><title>Simple jsp page</title></head>
  <body>
  <mm:cloud jspvar="cloud">

  <%
     LoggingMMBaseObserver loggingMMBaseObserver = LoggingMMBaseObserver.getInstance();

     String action = request.getParameter("action");
     String machine = request.getParameter("machine");
     if ("".equals(machine)) {
        machine = null;
     }
     String number = request.getParameter("number");
     if ("".equals(number)) {
        number = null;
     }
     String builder = request.getParameter("builder");
     if ("".equals(builder)) {
        builder = null;
     }
     String ctype = request.getParameter("ctype");
     if ("".equals(ctype)) {
        ctype = null;
     }
     boolean showStacktrace = "yes".equals(request.getParameter("stacktrace"));
     boolean local = "local".equals(request.getParameter("local"));
     boolean remote = "remote".equals(request.getParameter("remote"));


     LogConstraint logConstraint = new LogConstraint(machine, number, builder, ctype, showStacktrace);

     if (action != null && action.equals("add")) {
        loggingMMBaseObserver.addLogConstraints(logConstraint, local, remote);
     }
     else if (action != null && action.equals("remove")) {
        loggingMMBaseObserver.removeLogConstraints(logConstraint, local, remote);
     }

  %>
   <h3>Localconstraints</h3>
   <table>
      <tr>
        <th>Machine</th>
        <th>Number</th>
        <th>Builder</th>
        <th>Ctype</th>
        <th>showstacktrace</th>
      </tr>

   <%
      Iterator logLocalIterator = loggingMMBaseObserver.getLogLocalConstraints().iterator();

      while (logLocalIterator.hasNext()) {
         logConstraint = (LogConstraint) logLocalIterator.next();
         machine = logConstraint.getMachine();
         number = logConstraint.getNumber();
         builder = logConstraint.getBuilder();
         ctype = logConstraint.getCtype();
   %>
      <tr>
         <td><%=machine != null ? machine : "<i>empty</i>"%></td>
         <td><%=number != null ? number : "<i>empty</i>"%></td>
         <td><%=builder != null ? builder : "<i>empty</i>"%></td>
         <td><%=ctype != null ? ctype : "<i>empty</i>"%></td>
         <td><%=logConstraint.isPrintStrackTrace()%></td>
         <td><a href="<mm:url page="observerslogging.jsp" >
               <mm:param name="action" value="remove"/>
               <% if (machine != null) { %>
               <mm:param name="machine" value="<%=machine%>"/>
               <% } else if (number != null) { %>
               <mm:param name="number" value="<%=number%>"/>
               <% } else if (builder != null) { %>
               <mm:param name="builder" value="<%=builder%>"/>
               <% } else if (ctype != null) { %>
               <mm:param name="ctype" value="<%=ctype%>"/>
               <% } %>
               <mm:param name="stacktrace" value='<%=""+logConstraint.isPrintStrackTrace()%>'/>
               <mm:param name="local" value="local"/>
            </mm:url>">remove</a>

         </td>

      </tr>
   <%
      }
   %>
   </table>
   <h3>Remoteconstraints</h3>
   <table>
      <tr>
         <th>Machine</th>
         <th>Number</th>
         <th>Builder</th>
         <th>Ctype</th>
         <th>showstacktrace</th>
      </tr>

   <%
      Iterator logRemoteIterator = loggingMMBaseObserver.getLogRemoteConstraints().iterator();
      while (logRemoteIterator.hasNext()) {
         logConstraint = (LogConstraint) logRemoteIterator.next();
   %>
      <tr>
         <td><%=machine != null ? machine : "<i>empty</i>"%></td>
         <td><%=number != null ? number : "<i>empty</i>"%></td>
         <td><%=builder != null ? builder : "<i>empty</i>"%></td>
         <td><%=ctype != null ? ctype : "<i>empty</i>"%></td>
         <td><%=logConstraint.isPrintStrackTrace()%></td>
         <td><a href="<mm:url page="observerslogging.jsp" >
               <mm:param name="action" value="remove"/>
               <% if (machine != null) { %>
               <mm:param name="machine" value="<%=machine%>"/>
               <% } else if (number != null) { %>
               <mm:param name="number" value="<%=number%>"/>
               <% } else if (builder != null) { %>
               <mm:param name="builder" value="<%=builder%>"/>
               <% } else if (ctype != null) { %>
               <mm:param name="ctype" value="<%=ctype%>"/>
               <% } %>
               <mm:param name="stacktrace" value='<%=""+logConstraint.isPrintStrackTrace()%>'/>
               <mm:param name="remote" value="remote"/>
            </mm:url>">remove</a>
         </td>
      </tr>
   <%
      }
   %>
   </table>

   <form action="observerslogging.jsp" method="post" >
   <table>
      <tr>
         <td>Machine: </td><td><input type="field" size="60" name="machine"/></td>
      </tr>
      <tr>
         <td>Number: </td><td><input type="field" size="60" name="number"/></td>
      </tr>
      <tr>
         <td>Builder: </td><td><input type="field" size="60" name="builder"/></td>
      </tr>
      <tr>
         <td>ctype: </td><td><input type="field" size="60" name="ctype"/></td>
      </tr>
      <tr>
         <td>&nbsp;</td>
         <td>
            <input type="checkbox" size="60" name="local" value="local" id="local"/><label for="local">Local</label><br/>
            <input type="checkbox" size="60" name="remote" value="remote" id="remote"/><label for="remote">Remote</label>
         </td>
      </tr>
      <tr>
         <td>printStacktrace</td>
         <td>
            <input type="radio" name="stacktrace" value="yes" id="stacktrace_yes"/><label for="stacktrace_yes">Yes </label><br/>
            <input type="radio" name="stacktrace" value="no" id="stacktrace_no"/><label for="stacktrace_no">No </label>
         </td>
      </tr>
      <tr><td>&nbsp;</td><td><input type="submit" /></td></tr>
   </table>
      <input type="hidden" name="action" value="add"/>
   </form>
  </mm:cloud>
  </body>
</html>