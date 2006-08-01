<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/time.jsp" %>
<mm:cloud jspvar="cloud">
<mm:import externid="link" />
<mm:node number="$link" notfound="skipbody">
<html>
   <head>
      <title><mm:field name="titel" /></title>
   </head>
   <body style="margin:0px;padding:0px;">
   <OBJECT ID="MediaPlayer1" width=212 height=177
      classid="CLSID:22D6F312-B0F6-11D0-94AB-0080C74C7E95"
      CODEBASE="http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,4,5,715"
           standby="Loading Microsoft® Windows® Media Player components..." 
           type="application/x-oleobject">
     <PARAM NAME="AutoStart" VALUE="True">
     <PARAM NAME="FileName" VALUE="<mm:field name="url" />">
     <PARAM NAME="ShowControls" VALUE="True">
     <PARAM NAME="ShowStatusBar" VALUE="True">
     <EMBED type="application/x-mplayer2" 
      pluginspage="http://www.microsoft.com/Windows/MediaPlayer/"
      SRC="<mm:field name="url" />"
      name="MediaPlayer1"
      width=212
      height=177
      autostart=1
      showstatusbar=1
      showcontrols=1>
   </OBJECT>
   </body>
</html>
</mm:node>
</mm:cloud>