<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/time.jsp" %>
<mm:cloud jspvar="cloud">

<mm:import externid="video_image_url" />
<mm:import externid="link" />

<html>
   <head>
      <title></title>
   </head>
   <body style="margin:0px;padding:0px;">
   <div onClick="parent.document.getElementById('video<mm:write referid="link"/>').src='includes/portal/video.jsp?link=<mm:write referid="link"/>'">
      <img src="<mm:write referid="video_image_url"/>" align="center"/>
   </div>
   </body>
</html>

</mm:cloud>