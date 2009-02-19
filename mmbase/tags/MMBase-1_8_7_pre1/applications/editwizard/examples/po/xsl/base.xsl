<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">

  <xsl:import href="ew:xsl/base.xsl" /> <!-- extend from standard editwizard xslt -->

  <xsl:variable name="mediadir"><xsl:value-of select="$templatedir"/>media/</xsl:variable>
  <xsl:variable name="cssdir"><xsl:value-of select="$templatedir"/>style/</xsl:variable>

</xsl:stylesheet>
