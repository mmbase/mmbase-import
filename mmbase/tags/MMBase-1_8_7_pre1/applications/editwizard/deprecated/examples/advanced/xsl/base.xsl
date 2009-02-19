<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- 
  Demonstrating adding a CSS

  @version   $Id: base.xsl,v 1.1 2003-12-19 11:10:06 nico Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/base.xsl" /> <!-- extend from standard  editwizard xslt -->
  
  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$referrerdir}style.css" ></link>
  </xsl:template>
   
</xsl:stylesheet>