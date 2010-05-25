<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 25-07-2003
   Version: $Revision: 1.1 $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/list.xsl"/>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}/mmbase/components/editwizard/finalist/style/color/list.css" />
  </xsl:template>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}/mmbase/components/editwizard/finalist/style/extra/list.css" />
  </xsl:template>

	<!-- yeah right don't think so -->
  <xsl:template name="search-fields-default"/>

</xsl:stylesheet>
