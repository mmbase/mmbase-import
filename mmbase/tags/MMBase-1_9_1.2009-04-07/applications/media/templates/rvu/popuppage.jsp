<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:import externid="fragment" required="true"  
/><mm:cloud><mm:node  number="$fragment"
><html><head><title><mm:field name="title" /></title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"></head>
<body bgcolor="#102b11" bgproperties="FIXED" background="a_output2.gif">
<div align="center">
  <p>
    <object classid="clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B" width="320" height="260" codebase="http://www.apple.com/qtactivex/qtplugin.cab">
      <param name="SRC" value="<mm:field name="url(mov)" />">
      <param name="AUTOPLAY" value="true">
      <param name="CONTROLLER" value="true">
      <embed src="<mm:field name="url(mov)" />" width="320" height="260" autoplay="true" controller="true" pluginspage="http://www.apple.com/quicktime/download/">  </object>
  </p>
  </div>
<!-- Begin Sitestat4 code -->
<script language="JavaScript1.1">
<!--
function sitestat(ns_l){ns_l+="&ns__t="+new Date().getTime();ns_pixelUrl=ns_l;
ns_0=top.document.referrer;
ns_0=(ns_0.lastIndexOf("/")==ns_0.length-1)?ns_0.substring(ns_0.lastIndexOf("/"),0):ns_0;
if(ns_0.length>0)ns_l+="&ns_referrer="+escape(ns_0);
if(document.images){ns_1=new Image();ns_1.src=ns_l;}else
document.write("<img src="+ns_l+" width=1 height=1>");}
sitestat("http://nl.sitestat.com/klo/rvu/s?rvu.berichten.2001.");
//-->
</script>
<noscript>
<img src="http://nl.sitestat.com/klo/rvu/s?rvu.berichten.2001." width=1 height=1>
</noscript>
<!-- End Sitestat4 code -->


</body></html>
</mm:node>
</mm:cloud>