<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:kupu="http://kupu.oscom.org/namespaces/dist"
    xmlns:i18n="http://xml.zope.org/namespaces/i18n" 
    xmlns:j18n="http://jakarta.apache.org/taglibs/i18n-1.0"
    xmlns=""
    exclude-result-prefixes="i18n kupu"
    version="1.0"
    >
  <xsl:import href="make.xsl" />  

  <xsl:template match="option|h1|span|button" mode="expand">
    <xsl:element name="{name()}">
      <j18n:message>
	<xsl:attribute name="key">
	  <xsl:apply-templates mode="expand" />
	</xsl:attribute>
      </j18n:message>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
