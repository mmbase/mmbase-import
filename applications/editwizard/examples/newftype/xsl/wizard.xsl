<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/wizard.xsl"/>

  <xsl:template name="fieldintern">
    <xsl:apply-templates select="prefix" />

    <xsl:choose>
      <xsl:when test="@ftype=&apos;my_ftype&apos;">
      	<xsl:call-template name="ftype-my-ftype"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;function&apos;">
      	<xsl:call-template name="ftype-function"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;data&apos;">
      	<xsl:call-template name="ftype-data"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;line&apos;">
      	<xsl:call-template name="ftype-line"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;text&apos;">
      	<xsl:call-template name="ftype-text"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;html&apos;">
      	<xsl:call-template name="ftype-html"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;relation&apos;">
      	<xsl:call-template name="ftype-relation"/>      	
      </xsl:when>
      <xsl:when test="@ftype=&apos;enum&apos;">
      	<xsl:call-template name="ftype-enum"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;enumdata&apos;">
      	<xsl:call-template name="ftype-enumdata"/>
      </xsl:when>
      <xsl:when test="(@ftype=&apos;datetime&apos;) or (@ftype=&apos;date&apos;) or (@ftype=&apos;time&apos;)">
      	<xsl:call-template name="ftype-datetime"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;image&apos;">
      	<xsl:call-template name="ftype-image"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;file&apos;">
      	<xsl:call-template name="ftype-file"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;realposition&apos;">
      	<xsl:call-template name="ftype-realposition"/>
      </xsl:when>
      <xsl:otherwise>
      	<xsl:call-template name="ftype-other"/>
      </xsl:otherwise>
    </xsl:choose>
    
    <xsl:apply-templates select="postfix" />
  </xsl:template>

  <xsl:template name="ftype-my-ftype">
    <input type="checkbox" name="{@fieldname}" value="{value}" id="{@fieldname}">
      <xsl:apply-templates select="@*" />
    </input>
  </xsl:template>

  <xsl:template name="extrajavascript">
    <script language="javascript" src="{$referrerdir}javascript/override.js"></script>
  </xsl:template>

</xsl:stylesheet>