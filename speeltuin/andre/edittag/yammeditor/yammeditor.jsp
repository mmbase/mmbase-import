<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html;charset=utf-8"
%><%@ page import="org.mmbase.bridge.*,
                   org.mmbase.util.StringSplitter,
                   java.util.*,
                   java.util.regex.*" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud jspvar="cloud" rank="basic user" method="http">
<html>
<head>
    <title>+++ YAMMeditor (beta) : editor for the mm:edit tag +++</title>
<script src="yammeditor.js" type="text/javascript"></script>
<link rel="stylesheet" rev="stylesheet" href="yammeditor.css" media="screen" />
<%-- 
Breakdown:
1. Toon eerst alle startnodes (bovenin)
2. Toon per startnode alle paden die die node als start hebben
3. Verzamel de nodes die met die paden gevonden worden
4. Toon alleen die nodes en velden die in de pagina zaten (andere vindt je niet in de url)
5. Opties:  a. toon overige gerelateerde nodes
            b. zoekopties e.d. om andere nodes te relateren

#################################################################################### 
--%>

<mm:import externid="nrs"       jspvar="nrs" />
<mm:import externid="nodes"     jspvar="nodes" />
<mm:import externid="fields"    jspvar="fields" />
<mm:import externid="paths"     jspvar="paths" />

<%-- put it all down in a yammeditor session :-P --%>
<mm:write referid="nrs"     session="yammeditnrs" />
<mm:write referid="nodes"   session="yammeditnodes" />
<mm:write referid="fields"  session="yammeditflds" />
<mm:write referid="paths"   session="yammeditpaths" />
<mm:import externid="yammedithistory" id="history"  from="session" />

<%-- user actions --%>
<mm:import externid="change" />
</head>
<body>
<%@ include file="yamme_header.jsp" %>
<!-- content -->
<div id="content">  <!-- first startnode -->
<mm:node number="$nr" notfound="skip">

    <%@ include file="yamme_history.jsp" %>
    
    <%-- change --%>
    <mm:present referid="change">
        <mm:fieldlist type="edit"><mm:fieldinfo type="useinput" /></mm:fieldlist>
        <div class="message"><strong><mm:function name="gui" /></strong> is changed.</div>
    </mm:present>
    <%-- /change --%>

    <form id="startnode" enctype="multipart/form-data" method="post" action="<mm:url referids="nrs,nodes,fields,paths,nr" />">
    <h2>#<mm:field name="number" id="nodenr" /> (<mm:nodeinfo type="type" id="nm" />) <mm:function name="gui" /></h2>

        <mm:import jspvar="nodenr" vartype="String"><mm:write referid="nodenr" /></mm:import>
        <mm:import jspvar="nm" vartype="String"><mm:write referid="nm" /></mm:import>
    
<%--        <mm:import id="hist"><mm:write referid="nodenr" /><mm:write referid="history"><mm:isnotempty>,<mm:write /></mm:isnotempty></mm:write></mm:import>
        <mm:write referid="hist" session="yammedithistory" />
        <mm:remove referid="hist" />
--%>

    <mm:fieldlist type="edit">
        <div class="field"><strong><mm:fieldinfo type="guiname" /></strong>
        <mm:fieldinfo type="name" jspvar="fieldname" vartype="String" write="true"><br />
            <%-- 
            String fieldstring = nm + "." + fieldname;
            Set fs = (Set)fmap.get(sn);
            // shows allways the handle of images & attachments
            if (fs.contains(fieldstring) || fieldname.equals("handle")) { --%>
              <mm:fieldinfo type="input" />
            <%-- } else { %>
              <div class="fieldguivalue"><mm:fieldinfo type="guivalue" />&nbsp;</div>
            <% } --%>
        </mm:fieldinfo>
        </div>
        <mm:remove referid="fieldname" />
    </mm:fieldlist>
    <div class="field"><input type="submit" name="change" value="Change" class="formbutton" /></div>
    </form>
</mm:node>
</div>
<!-- /content -->

<!-- sidebar -->
<div id="sidebar">
<%@ include file="yamme_related.jsp" %>
</div>
<!-- /sidebar -->
<div id="footer"><a href="javascript:closeYAMMeditor();">close window</a></div>
<!--
<p>parameters<br />
<strong>nrs :</strong>  <mm:write referid="nrs" /> <br />
<strong>nodes :</strong> <mm:write referid="nodes" /> <br />
<strong>fields :</strong> <mm:write referid="fields" /> <br />
<strong>paths :</strong> <mm:write referid="paths" /> <br />
</p>
-->
<!--
    <% 
    out.println("<br /><br /><b>Session info</b>");
    String[] names  = session.getValueNames();
    for (int j = 0; j < names.length; j++) {
        out.println("<br /><br />session name : " + names[j]);
        out.println("<br />session value : " + session.getValue(names[j]));
    }
    %>
-->
</body>
</html>
</mm:cloud>
