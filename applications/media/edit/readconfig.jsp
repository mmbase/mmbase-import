<mm:context id="config">
 <mm:import id="configsubmitted" externid="config" from="parameters" />

 <mm:present referid="configsubmitted">
    <mm:import id="lang"   externid="lang" />
    <mm:import id="format" externid="format" />
    <mm:import id="quality" externid="quality" />
    <mm:import id="player" externid="player" />
 </mm:present>
 <mm:notpresent referid="configsubmitted">
    <mm:import id="lang"   externid="mmjspeditors_language" frpm>nl</mm:import>
    <mm:import id="format" externid="mmmediaeditors_format"></mm:import>
    <mm:import id="quality" externid="mmmediaeditors_quality"></mm:import>
    <mm:import id="player" externid="mmmediaeditors_player"></mm:import>
  </mm:notpresent>

 <mm:write  referid="lang"        cookie="mmjspeditors_language" />
 <mm:write  referid="format"      cookie="mmmediaeditors_format" />
 <mm:write  referid="quality"     cookie="mmmediaeditors_quality" />
 <mm:write  referid="quality"     cookie="mmmediaeditors_player" />
  
</mm:context>
<% 
   java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; 
%>
<mm:write referid="config.lang" jspvar="lang" vartype="string"><%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.util.media.resources.mediaedit", locale);
%>
</mm:write>


