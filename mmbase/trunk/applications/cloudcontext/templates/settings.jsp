<mm:import externid="stylesheet">style/default.css</mm:import>
<mm:import externid="rank">project manager</mm:import>
<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:write referid="language" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.security.implementation.cloudcontext.editorresources.texts", locale);
%>
</mm:write>