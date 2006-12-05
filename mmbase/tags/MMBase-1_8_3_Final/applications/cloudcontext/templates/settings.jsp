<mm:import externid="stylesheet">style/default.css</mm:import>
<mm:import id="rank"><%= org.mmbase.util.xml.UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>
<%!
   String getPrompt(java.util.ResourceBundle m, String key) {
     try {
       return m.getString(key);
     } catch (Exception e) {
       return key;
     }
   }
%>
<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:import externid="language" from="this,parameters">en</mm:import>
<mm:write referid="language" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.security.implementation.cloudcontext.editorresources.texts", locale);
%>
</mm:write>