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

  <xsl:if test="tlibversion">
    <tlibversion><xsl:value-of select="tlibversion"/></tlibversion>
  </xsl:if>
  <xsl:if test="jspversion">
    <jspversion><xsl:value-of select="jspversion"/></jspversion>
  </xsl:if>
  <xsl:if test="shortname">
    <shortname><xsl:value-of select="shortname"/></shortname>
  </xsl:if>
  <xsl:if test="uri">
    <uri><xsl:value-of select="uri"/></uri>
  </xsl:if>
  <xsl:if test="info">
    <info><xsl:value-of select="info"/></info>
  </xsl:if>
  <xsl:apply-templates select="tag"/>

</taglib>
</xsl:template>

<xsl:template match="tag">
  <tag>
     <xsl:if test="name">
       <name><xsl:value-of select="name"/></name>
     </xsl:if>
     <xsl:if test="tagclass">
       <tagclass><xsl:value-of select="tagclass"/></tagclass>
     </xsl:if>
     <xsl:if test="teiclass">
        <teiclass><xsl:value-of select="teiclass"/></teiclass>
     </xsl:if>
     <xsl:if test="bodycontent">
        <bodycontent><xsl:value-of select="bodycontent"/></bodycontent>
     </xsl:if>
     <xsl:if test="info">
        <info>
	  <xsl:value-of select="info"/>
	</info>
     </xsl:if>      
     <xsl:apply-templates select="attribute"/>
  </tag>
</xsl:template>

<xsl:template match="attribute">
    <attribute>      
      <xsl:if test="name">
        <name><xsl:value-of select="name"/></name>
      </xsl:if>
      <xsl:if test="required">
        <required><xsl:value-of select="required"/></required>
      </xsl:if>
      <xsl:if test="rtexprvalue">
        <rtexprvalue><xsl:value-of select="rtexprvalue"/></rtexprvalue>
      </xsl:if>
    </attribute>
</xsl:template>

</xsl:stylesheet>
