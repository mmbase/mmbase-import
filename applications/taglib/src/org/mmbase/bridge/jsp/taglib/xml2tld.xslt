<?xml version="1.0"?>
<xsl:stylesheet id="xml2tld" 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes"/>

<!-- main entry point -->
<xsl:template match="taglib">
<xsl:text disable-output-escaping="yes">
&lt;!DOCTYPE taglib
        PUBLIC "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.1//EN"
	"http://java.sun.com/j2ee/dtds/web-jsptaglibrary_1_1.dtd"&gt;
</xsl:text>

<xsl:comment> a tag library descriptor </xsl:comment>

<taglib>
<xsl:comment> after this the default space is
	"http://java.sun.com/j2ee/dtds/jsptaglibrary_1_2.dtd"
</xsl:comment>

  <tlibversion><xsl:value-of select="tlibversion"/></tlibversion>
  
  <jspversion><xsl:value-of select="jspversion"/></jspversion>
  
  <shortname><xsl:value-of select="shortname"/></shortname>
  <uri><xsl:value-of select="uri"/></uri>
  <info><xsl:value-of select="info"/></info>
  <xsl:apply-templates select="tag"/>
</taglib>
</xsl:template>

<xsl:template match="tag">
  <tag>
    <name><xsl:value-of select="name"/></name>
    <tagclass><xsl:value-of select="tagclass"/></tagclass>
    <teiclass><xsl:value-of select="teiclass"/></teiclass>
    <bodycontent><xsl:value-of select="bodycontent"/></bodycontent>
    <info>
	<xsl:value-of select="info"/>
    </info>
    <xsl:apply-templates select="attribute"/>
  </tag>
</xsl:template>

<xsl:template match="attribute">
    <attribute>
      <name><xsl:value-of select="name"/></name>
      <required><xsl:value-of select="required"/></required>
      <rtexprvalue><xsl:value-of select="rtexprvalue"/></rtexprvalue>
    </attribute>
</xsl:template>

</xsl:stylesheet>
