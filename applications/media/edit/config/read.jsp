<%!
 // Need to know the real servername sometimes
 String  getHost() { 
     return org.mmbase.applications.media.urlcomposers.Config.host; 
 }

 String thisServer(javax.servlet.http.HttpServletRequest request, String url) { 
    return "http://" + getHost() + request.getContextPath() + url;
 } 
%>
<mm:import externid="logout" />
<mm:present referid="logout">
  <mm:cloud method="logout" />
</mm:present>

<mm:context id="config">
<mm:cloud method="asis">
  <%-- get config from cookies --%>
  <mm:import id="lang"    externid="mmjspeditors_language"   from="parameters,cookie">nl</mm:import>
  <mm:import id="quality" externid="mediaeditors_quality"    from="parameters,cookie">any</mm:import>
  <mm:import id="player"  externid="mediaeditors_player"     from="parameters,cookie">any</mm:import>
  <mm:import              externid="mediaeditors_origin"     from="parameters,cookie" /> <%-- no default, will be ask on first entry --%>
  
  <mm:present referid="mediaeditors_origin">
    <mm:isempty referid="mediaeditors_origin">
      <mm:import id="mediaeditors_origin_set">yes</mm:import>
      <mm:write  referid="mediaeditors_origin"  cookie="mediaeditors_origin" />
    </mm:isempty>
    <mm:node number="$mediaeditors_origin" notfound="skip">
      <mm:import id="mediaeditors_origin_set">yes</mm:import>
      <mm:write  referid="mediaeditors_origin"  cookie="mediaeditors_origin" />
    </mm:node>
  </mm:present>
  
  <mm:write  referid="lang"    cookie="mmjspeditors_language" />
  <mm:write  referid="quality" cookie="mediaeditors_quality" />
  <mm:write  referid="player"  cookie="mediaeditors_player" />

  <mm:import id="editwizards"><%=org.mmbase.applications.media.urlcomposers.Config.editwizardsDir == null ? "/mmapps/editwizard" : org.mmbase.applications.media.urlcomposers.Config.editwizardsDir%></mm:import>
</mm:cloud>
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
