<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the stylesheet of the edit wizards.

   Author: yigal
   Created: 25-07-2003
   Version: $Revision: 1.1 $, $Date: 2003-12-19 11:09:17 $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/base.xsl"/>

  <xsl:variable name="mediadir"><xsl:value-of select="$referrerdir"/>media/</xsl:variable>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$referrerdir}/style/color/base.css" />
  </xsl:template>

  <xsl:variable name="searchagetype">none</xsl:variable>

</xsl:stylesheet>