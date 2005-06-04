<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:kupu="http://kupu.oscom.org/namespaces/dist"
   xmlns:i18n="http://xml.zope.org/namespaces/i18n" 
   xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
   xmlns:html="http://www.w3.org/1999/xhtml"
   exclude-result-prefixes="kupu"
   version="1.0"
   >
  <xsl:import href="make.xsl" />  

  <xsl:template match="html:*" mode="expand">
    <xsl:choose>
      <xsl:when test="@i18n:translate">
	<xsl:element name="{name()}">
	  <xsl:copy-of select="@html:*" />
	  <fmt:message>
	    <xsl:attribute name="key">
	      <xsl:choose>
		<xsl:when test="@i18n:translate = ''">
		  <xsl:apply-templates select="text()" mode="expand" />
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="@i18n:translate" />
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:attribute>
	  </fmt:message>
	</xsl:element>
      </xsl:when>
      <xsl:when test="@i18n:attributes">
	<xsl:variable name="attributes"><xsl:value-of select="@i18n:attributes" /></xsl:variable>
	<fmt:message var="_">
	  <xsl:attribute name="key">
	    <xsl:value-of select="@title" /><!-- should be @$attributes, but that doesn't work -->
	  </xsl:attribute>
	</fmt:message>
	<xsl:element name="{name()}">
	  <xsl:copy-of select="@html:*" />
	  <xsl:attribute name="{$attributes}">${_}</xsl:attribute>
	  <xsl:apply-templates  mode="expand" />
	</xsl:element>
      </xsl:when>
      <xsl:otherwise>
	  <xsl:element name="{name()}">
	    <xsl:copy-of select="@html:*" />
	    <xsl:apply-templates  mode="expand" />
	  </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>