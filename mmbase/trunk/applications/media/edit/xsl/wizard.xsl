<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">

  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->
  
  <xsl:template name="beforeform">
    <xsl:if test="/wizard/curform = 'itemize'">    
    <center>
    <embed 
      src="http://mihxil.komputilo.org/mm/mediaedit/display.ram.jsp?source=7071&amp;fragment=7070" 
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
  </center>
  <hr />
  </xsl:if>
  </xsl:template>
   
</xsl:stylesheet>