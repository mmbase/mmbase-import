<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!--  Stream manager -->
  <xsl:import href="ew:xsl/base.xsl" /> <!-- extend from standard  editwizard xslt -->
  
  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$referrerdir}style/streammanager.css" ></link>
  </xsl:template>
   
</xsl:stylesheet>