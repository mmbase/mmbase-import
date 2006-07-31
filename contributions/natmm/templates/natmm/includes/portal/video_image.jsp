<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%@include file="../../includes/time.jsp" %>
<mm:cloud jspvar="cloud">

<mm:import externid="video_image_url" />
<mm:import externid="link" />

<html>
   <head>
      <title><mm:field name="titel" /></title>
   </head>
   <body style="margin:0px;padding:0px;">
   <img src="<mm:write referid="video_image_url"/>" align="center"/>
   <div><a href="#" onClick="top.video<mm:write referid="link"/>.href='video.jsp?link=<mm:write referid="link"/>'">Play video file.</a></div>
   </body>
</html>

</mm:cloud>