<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.finalist.cmsc.util.ThreadUtil" %>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="threads.title" />
<body>
  <div class="tabs">
     <div class="tab_active">
        <div class="body">
           <div>
              <a href="#"><fmt:message key="threads.active" /></a>
           </div>
        </div>
     </div>
<%-- 
     <div class="tab">
        <div class="body">
           <div>
              <a href="#"><fmt:message key="threads.all" /></a>
           </div>
        </div>
     </div>
 --%>
  </div>
  <div class="editor">
    <div class="body">


	</div>
<%
Map<String,Map<Thread, StackTraceElement[]>> map = ThreadUtil.getActiveThreadsByApplication();
for (Map.Entry<String, Map<Thread,StackTraceElement[]>> entry : map.entrySet()) {
    String applicationName = entry.getKey();
    %><div class="ruler_green"><div><%=applicationName%></div></div>
    <div id="<%=applicationName%>" class="body"><pre><%
    Map<Thread,StackTraceElement[]> threads = entry.getValue();
    for (Map.Entry<Thread, StackTraceElement[]> thread : threads.entrySet()) {
    	StackTraceElement[] stack = thread.getValue();
    	if (stack != null && stack.length > 0 && !stack[0].getMethodName().equals("dumpThreads")) {
%>
<%= ThreadUtil.printStackTrace(thread.getKey(), stack) %>
<%
		}
    }
    %></pre></div><%
} %>
  </div>
</body>
</html:html>
</mm:content>