<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">

  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->
  
  <xsl:template name="beforeform">
    <xsl:if test="/wizard/curform = 'itemize'">    
    <center>
    <embed 
      src="http://michiel.omroep.nl/mm/mediaedit/display.ram.jsp" 
      width="200" 
      height="200"   
      type="audio/x-pn-realaudio-plugin"
      nojava="false" 
      controls="ImageWindow" 
      console="Clip1" 
      autostart="true" 
      nologo="true"
      nolabels="true"
      name="embededplayer"></embed>
  </center>
  <hr />
  </xsl:if>
  </xsl:template>
   
</xsl:stylesheet>