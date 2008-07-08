<%-- check whether the thememanager has been installed --%>
<mm:import id="thememanager">true</mm:import>
<%-- use thememanager --%>
<mm:present referid="thememanager">
<%-- get the context of the thememanger, this is used to create urls --%>
<mm:import id="context"><mm:url page="/mmbase/thememanager"/></mm:import>
<%-- set the imagecontext, this can be used if images are stored somewhere else --%>
<mm:import id="imagecontext"><mm:write referid="context"/>/images</mm:import>
<mm:import id="themeid">ThemeManager</mm:import>
<mm:import id="style_default"><mm:function set="thememanager" name="getStyleSheet" referids="context,themeid" /></mm:import>
<mm:import id="imageid" reset="true">arrowright</mm:import>
<mm:import id="image_arrowright"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
<mm:import id="imageid" reset="true">arrowleft</mm:import>
<mm:import id="image_arrowleft"><mm:function set="thememanager" name="getThemeImage" referids="imagecontext,themeid,imageid" /></mm:import>
</mm:present>
<%-- if thememanager hasn't been installed use defaults --%>
<mm:present referid="thememanager" inverse="true">
<mm:import id="style_default">css/mmbase-dev.css</mm:import>
<mm:import id="image_arrowright">images/arrow-right.gif</mm:import>
<mm:import id="image_arrowleft">images/arrow-left.gif</mm:import>
</mm:present>
