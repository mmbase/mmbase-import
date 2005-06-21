<!--
  This translates a mmbase XML field to XHTML1. So if you have MMXF
  fields in your datbase, this describes how they are presented as XHTML.

  MMXF itself is besides the mmxf tag itself a subset of XHTML2.

  @version $Id: mmxf2xhtml.xslt,v 1.15 2005-06-21 09:29:12 michiel Exp $
  @author Michiel Meeuwissen
-->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
  exclude-result-prefixes="mmxf"
  version = "1.0"
>
  <xsl:output
    method="xml"
    omit-xml-declaration="yes"
    />
  
  <xsl:template match = "mmxf:mmxf" >
    <div class="mmxf">
      <xsl:apply-templates select = "mmxf:section|mmxf:p|mmxf:table|mmxf:ul|mmxf:ol" />
    </div>
  </xsl:template>
  
  
  <xsl:template match="mmxf:p|mmxf:li|mmxf:ul|mmxf:ol|mmxf:a|mmxf:em|mmxf:table|mmxf:tr|mmxf:th|mmxf:td|mmxf:caption|mmxf:br|mmxf:sub|mmxf:sup">
    <xsl:element name="{name()}">
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="node()" />
    </xsl:element>
  </xsl:template>

  <xsl:template match ="text()">
    <xsl:copy-of select="." />
  </xsl:template>
  
  
  <xsl:template match ="mmxf:section">
    <xsl:apply-templates select="mmxf:section|mmxf:h|mmxf:p|mmxf:ul|mmxf:ol|mmxf:table|mmxf:sub|mmxf:sup" />
  </xsl:template>

  <xsl:template match="mmxf:h" mode="h1"><xsl:if test="string(.)"><h3><xsl:apply-templates select="node()" /></h3></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h2"><p><strong><xsl:apply-templates select="node()" /></strong></p></xsl:template>
  <xsl:template match="mmxf:h" mode="h3"><p><xsl:value-of select="node()" /></p></xsl:template>
  
  <xsl:template match="mmxf:h" mode="h4"><xsl:apply-templates select="." mode="deeper" /></xsl:template>
  <xsl:template match="mmxf:h" mode="h5"><xsl:apply-templates select="." mode="deeper" /></xsl:template>
  <xsl:template match="mmxf:h" mode="h6"><xsl:apply-templates select="." mode="deeper" /></xsl:template>
  <xsl:template match="mmxf:h" mode="h7"><xsl:apply-templates select="." mode="deeper" /></xsl:template>
  <xsl:template match="mmxf:h" mode="h8"><xsl:apply-templates select="." mode="deeper" /></xsl:template>

  <xsl:template match="mmxf:h" mode="deeper">
    <xsl:apply-templates select="node()" /><br />
  </xsl:template>

  <xsl:template match = "mmxf:h" >
    <xsl:variable name="depth"><xsl:value-of select="count(ancestor::mmxf:section)" /></xsl:variable>
    <xsl:if test="$depth=1"><xsl:apply-templates select="." mode="h1" /></xsl:if>
    <xsl:if test="$depth=2"><xsl:apply-templates select="." mode="h2" /></xsl:if>
    <xsl:if test="$depth=3"><xsl:apply-templates select="." mode="h3" /></xsl:if>
    <xsl:if test="$depth=4"><xsl:apply-templates select="." mode="h4" /></xsl:if>
    <xsl:if test="$depth=5"><xsl:apply-templates select="." mode="h5" /></xsl:if>
    <xsl:if test="$depth=6"><xsl:apply-templates select="." mode="h6" /></xsl:if>
    <xsl:if test="$depth=7"><xsl:apply-templates select="." mode="h7" /></xsl:if>
    <xsl:if test="$depth=8"><xsl:apply-templates select="." mode="h8" /></xsl:if>
    <xsl:if test="$depth>8"><xsl:apply-templates select="." mode="deeper" /></xsl:if>
  </xsl:template>



</xsl:stylesheet>
