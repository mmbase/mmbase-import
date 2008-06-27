<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the edit wizards.

   Author: Nico Klasens
   Created: 16-12-2003
   Version: $Revision: 1.1 $
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/wizard.xsl"/>

  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator();</xsl:variable>

  <xsl:template name="stylehtml">
    <link rel="stylesheet" type="text/css" href="{$referrerdir}{style/layout/wysiwyg.css" />
    <link rel="stylesheet" type="text/css" href="{$referrerdir}style/color/wysiwyg.css" />
  </xsl:template>

  <xsl:template name="javascript-html">
      <script type="text/javascript" src="{$referrerdir}javascript/wysiwyg.js">
        <xsl:comment>help IE</xsl:comment>
      </script>
  </xsl:template>

</xsl:stylesheet>