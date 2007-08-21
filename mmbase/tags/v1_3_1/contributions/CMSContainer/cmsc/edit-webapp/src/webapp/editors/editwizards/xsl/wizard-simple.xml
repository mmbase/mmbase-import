<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards for workflow.

   Author: Nico Klasens
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">
  
  <xsl:template name="beforeform">
    <xsl:if test="$READONLY=&apos;true&apos;">
      <xsl:if test="$READONLY-REASON=&apos;RIGHTS&apos;">
        <p class="readonly-reason"> <xsl:value-of select="$REASON-RIGHTS"/> </p>
      </xsl:if>
    </xsl:if>
   </xsl:template>

  <xsl:template name="buttons-extended">
  </xsl:template>

</xsl:stylesheet>