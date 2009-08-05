<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 25-07-2003
   Version: $Revision: 1.4 $
-->
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  extension-element-prefixes="node">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/searchlist.xsl"/>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/color/searchlist.css" />
  </xsl:template>

</xsl:stylesheet>
