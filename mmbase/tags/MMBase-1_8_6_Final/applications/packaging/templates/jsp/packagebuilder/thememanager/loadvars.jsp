<%-- check if the thememanager has been installed --%>
<mm:import id="thememanager">true</mm:import>

<%-- if thememanager has been installed, check if the webapp has been restarted --%>
<%-- this check is needed until MMBase can load jars on it's own --%>
<mm:present referid="thememanager">
<%
    //try to find the ThemeManager class
    boolean themeManagerInstalled = true;
    try {
        Class.forName("org.mmbase.applications.thememanager.ThemeManager");
    } catch (ClassNotFoundException cnfe) {
        themeManagerInstalled = false;
    }
%>

<mm:import id="thememanager" reset="true"><%= Boolean.toString(themeManagerInstalled) %></mm:import>

<%-- use thememanager if it has been installed and the jars is loaded --%>
<mm:compare referid="thememanager" value="true">

  <%-- get the context of the thememanger, this is used to create urls --%>
  <mm:import id="context"><mm:url page="/mmbase/thememanager" /></mm:import>
  <%-- set the imagecontext, this can be used if images are stored somewhere else --%>
  <mm:import id="imagecontext"><mm:write referid="context"/>/images</mm:import>
  <mm:import id="themeid">PackageManager</mm:import>
  <mm:import id="style_default"><mm:function set="thememanager" name="getStyleSheet" referids="context,themeid" /></mm:import> 

  <mm:import id="imageid" reset="true">help</mm:import>
  <mm:import id="image_help"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
  <mm:import id="imageid" reset="true">arrowright</mm:import>
  <mm:import id="image_arrowright"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
  <mm:import id="imageid" reset="true">arrowleft</mm:import>
  <mm:import id="image_arrowleft"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
  <mm:import id="imageid" reset="true">error</mm:import>
  <mm:import id="image_error"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
  <mm:import id="imageid" reset="true">warning</mm:import>
  <mm:import id="image_warning"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>

</mm:compare>

<%-- use defaults if the thememanger has been installed but the jar isn't loaded --%>
<mm:compare referid="thememanager" value="false">
  <mm:import id="style_default">css/default.css</mm:import>
  <mm:import id="image_arrowright">images/arrow-right.gif</mm:import>
  <mm:import id="image_arrowleft">images/arrow-left.gif</mm:import>
  <mm:import id="image_warning">images/warning.png</mm:import>
  <mm:import id="image_error">images/error.png</mm:import>
  <mm:import id="image_help">images/help.gif</mm:import>
</mm:compare>

</mm:present>

<%-- if thememanager hasn't been installed use defaults --%>      
<mm:present referid="thememanager" inverse="true">
  <mm:import id="style_default">css/default.css</mm:import>
  <mm:import id="image_arrowright">images/arrow-right.gif</mm:import>
  <mm:import id="image_arrowleft">images/arrow-left.gif</mm:import>
  <mm:import id="image_warning">images/warning.png</mm:import>
  <mm:import id="image_error">images/error.png</mm:import>
  <mm:import id="image_help">images/help.gif</mm:import>
</mm:present>
