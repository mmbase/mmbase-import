<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- 
  Demonstrating overriding of standard list.xsl 

  @version   $Id: list.xsl,v 1.1 2002-04-19 14:00:41 michiel Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/list.xsl" /> <!-- extend from standard  editwizard xslt -->
  
  <xsl:template name="style"> <!-- just to test overriding -->
    <title>XXXXX  <xsl:value-of select="$wizardtitle" /> -  <xsl:value-of select="$title" />  XXXXX</title>
    <link rel="stylesheet" type="text/css" href="../style.css" />
  </xsl:template>
   
</xsl:stylesheet>