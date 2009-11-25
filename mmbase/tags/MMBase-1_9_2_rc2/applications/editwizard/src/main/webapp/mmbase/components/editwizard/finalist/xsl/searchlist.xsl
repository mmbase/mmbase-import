<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 25-07-2003
   Version: $Revision: 1.1 $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/searchlist.xsl"/>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$templatedir}/style/color/searchlist.css" />
  </xsl:template>

</xsl:stylesheet>
