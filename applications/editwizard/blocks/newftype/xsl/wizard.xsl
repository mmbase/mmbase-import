<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/wizard.xsl"/>

  <xsl:template name="ftype-unknown">
    <xsl:choose>
      <xsl:when test="@ftype='my_ftype'">
      	<xsl:call-template name="ftype-my-ftype"/>
      </xsl:when>
      <xsl:otherwise>
      	<xsl:call-template name="ftype-other"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ftype-my-ftype">
    <input type="checkbox" name="{@fieldname}" value="{value}" id="{@fieldname}">
      <xsl:apply-templates select="@*" />
    </input>
  </xsl:template>

  <xsl:template name="extrajavascript">
    <script language="javascript" src="{$ew_context}/mmbase/components/editwizards/newftype/javascript/override.js"></script>
  </xsl:template>

</xsl:stylesheet>
