<!--
  This translates a mmbase XML field to XHTML1. So if you have MMXF
  fields in your datbase, this describes how they are presented as XHTML.

  MMXF itself is besides the mmxf tag itself a subset of XHTML2.

  @version $Id: mmxf2xhtml.xslt,v 1.4 2004-03-02 16:48:54 michiel Exp $
  @author Michiel Meeuwissen
-->
<xsl:stylesheet
  version = "1.0"
  xmlns:xsl = "http://www.w3.org/1999/XSL/Transform"

>
  <xsl:output
    method="xml"
    omit-xml-declaration="yes"
    />


    <xsl:template match = "mmxf" >
      <xsl:apply-templates select = "section|p" />
    </xsl:template>


    <xsl:template match ="p|ul|li|em">
      <xsl:copy-of select="." />
    </xsl:template>

    <xsl:template match ="section">
      <xsl:apply-templates select = "section|h|p|ul" />
    </xsl:template>

  <xsl:template match = "h" >
    <xsl:if test="count(ancestor::section)=1"><xsl:if test="string(.)"><h3><xsl:value-of select="." /></h3></xsl:if></xsl:if>
    <xsl:if test="count(ancestor::section)=2"><p><strong><xsl:value-of select="." /></strong></p></xsl:if>
    <xsl:if test="count(ancestor::section)=3"><p><xsl:value-of select="." /></p></xsl:if>
    <xsl:if test="count(ancestor::section)>3"><xsl:value-of select="." /><br /></xsl:if>
  </xsl:template>

</xsl:stylesheet>
