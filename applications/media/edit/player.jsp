<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
<html>
<mm:import externid="language">nl</mm:import>
<mm:import externid="fragment" required="true" />
<mm:locale language="$language">
<head>
   <title>Media viewer</title>
</head>
<body>
<script>

</script>

    <embed 
      src="<mm:url referids="fragment" page="display.ram.jsp" />" 
                width="200" 
                height="165"   
                type="audio/x-pn-realaudio-plugin"
                nojava="false" 
                controls="ImageWindow" 
                console="Clip1" 
                autostart="true" 
                nologo="true"
                nolabels="true"
                name="embeddedplayer"></embed>
              <p>
                <a href="JavaScript:self.close();">Sluit de player</a>                
              </p>  
          <p>
          <a href="<mm:url referids="fragment" page="display.ram.jsp" />">ach ach</a>
          </p>
</body>
</mm:locale>
</html>
