<%!
 // Need to know the real servername sometimes
 String  getHost() { 
     return org.mmbase.applications.media.urlcomposers.Config.host; 
 }

 String thisServer(javax.servlet.http.HttpServletRequest request, String url) { 
    return "http://" + getHost() + request.getContextPath() + url;
 } 
%><mm:context id="config"
><mm:import id="configsubmitted" externid="config" from="parameters" 
/> <mm:present referid="configsubmitted">
    <%-- for config-page --%>
    <mm:import id="lang"    externid="lang"   from="parameters">en</mm:import>
    <mm:import id="quality" externid="quality" from="parameters">any</mm:import>
    <mm:import id="player"  externid="player" from="parameters">any</mm:import>
 </mm:present>
 <mm:notpresent referid="configsubmitted">
    <%-- get config from cookies --%>
    <mm:import id="lang"    externid="mmjspeditors_language"   from="cookie">nl</mm:import>
    <mm:import id="quality" externid="mediaeditors_quality"  from="cookie">any</mm:import>
    <mm:import id="player"  externid="mediaeditors_player"   from="cookie">any</mm:import>
  </mm:notpresent>

  <%-- always write cookies --%>
  <mm:write  referid="lang"       cookie="mmjspeditors_language" />
  <mm:write  referid="quality"    cookie="mediaeditors_quality" />
  <mm:write  referid="player"     cookie="mediaeditors_player" />

  <mm:import id="editwizards"><%=org.mmbase.applications.media.urlcomposers.Config.editwizardsDir == null ? "/mmapps/editwizard" : org.mmbase.applications.media.urlcomposers.Config.editwizardsDir%></mm:import>
</mm:context><% 
   java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; 
   java.util.Map options = null;
%><mm:write referid="config.lang" jspvar="lang" vartype="string" write="false"
><%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.applications.media.resources.mediaedit", locale);
  options = new java.util.HashMap();
  options.put("locale", locale);
  options.put("host", getHost());

%></mm:write>