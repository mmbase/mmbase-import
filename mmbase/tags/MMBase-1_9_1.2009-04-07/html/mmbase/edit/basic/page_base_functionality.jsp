<%-- uncomment this, if your JSP-engine is JSP2.0 (see release-notes) --%><%--@page isELIgnored="true"  
--%><%@page session="true" language="java" contentType="text/html; charset=utf-8"  import="java.util.Stack,org.mmbase.bridge.*,org.mmbase.util.xml.UtilReader"
%><%!

// stack stuff (for the bread-crumb). Might appear a tag for this sometime.

void push(Stack stack, String id,  String url) {
   stack.push(new String[] {id, url});
}
void push(Stack stack, String id, HttpServletRequest request) {
   String qs = request.getQueryString();
   push(stack, id, request.getServletPath() + (qs != null ? ("?" + qs) : ""));
}
String peek(Stack stack) {
    if (stack.size() > 0) {
        return ((String []) stack.peek())[1];
    } else {
        return ".";
    }
}

String toHtml(Stack stack, HttpServletRequest request) {
  StringBuffer buf = new StringBuffer();
  if (stack.size() < 1) return "";
  String[] info = (String []) stack.get(0);
  buf.append("<a href='" + (info[1].startsWith("/") ? request.getContextPath() : "")+  info[1] +  "'>" + info[0] + "</a>");
  for (int i = 1; i < stack.size(); i++) {
     info = (String []) stack.get(i);
     buf.append("-&gt; <a href='" + (info[1].startsWith("/") ? request.getContextPath() : "")+  info[1] + "&amp;pop=" + (stack.size() - i) + "'>" + info[0] + "</a>");
  }
  return buf.toString();
}
%><% 

Stack urlStack = (Stack) session.getAttribute("editor_stack");

if (urlStack == null) {
   urlStack = new Stack();
   session.setAttribute("editor_stack", urlStack);
}


%><mm:content postprocessor="swallow">
<mm:import externid="pop" />
<mm:import externid="push" />
<mm:import externid="nopush" />
<mm:import externid="clearstack" />
<mm:present referid="clearstack">
  <% urlStack.clear(); %>
</mm:present>

<mm:notpresent referid="nopush">
  <mm:present referid="push">
    <mm:write referid="push" jspvar="v" vartype="string">
      <% push(urlStack, v, request); %>
    </mm:write>
  </mm:present>
</mm:notpresent>
<mm:present referid="pop">
  <mm:write referid="pop" jspvar="pop" vartype="integer">
    <% for (int i = 0; i < pop.intValue() && urlStack.size() > 0; i++) urlStack.pop(); %>
  </mm:write>
</mm:present>

<mm:import id="config" externid="mmeditors_config" from="session" />

<mm:context id="config" referid="config">
  <mm:import id="page_size"   externid="mmjspeditors_page_size"   from="parameters,cookie,this">20</mm:import>
  <mm:import id="indexoffset" externid="mmjspeditors_indexoffset" from="parameters,cookie,this">1</mm:import>
  <mm:import id="style_sheet" externid="mmjspeditors_style"     from="parameters,cookie,this">mmbase.css</mm:import>
  <mm:import id="liststyle"   externid="mmjspeditors_liststyle" from="parameters,cookie,this">short</mm:import>  
  <mm:write cookie="mmjspeditors_liststyle" referid="liststyle"   />
  <mm:import id="lang"        externid="mmjspeditors_language"  from="parameters,cookie,this" ><%=LocalContext.getCloudContext().getDefaultLocale().getLanguage()%></mm:import>
  <mm:import id="country"     externid="mmjspeditors_country"   from="parameters,cookie,this" />
  <mm:import id="session"     externid="mmjspeditors_session"   from="parameters,cookie,this">mmbase_editors_cloud</mm:import>
  <mm:import id="xmlmode"     externid="mmjspeditors_xmlmode"   from="parameters,cookie,this">flat</mm:import>

  <mm:import externid="batches" from="parameters,this" >30</mm:import>
</mm:context>

<mm:import id="rank"><%= UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>


<mm:write referid="config" session="mmeditors_config" />
</mm:content><% 
java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
%><mm:locale language="$config.lang" country="$config.country" jspvar="locale"><%
  m = java.util.ResourceBundle.getBundle("org.mmbase.jspeditors.editors", locale);
%></mm:locale>