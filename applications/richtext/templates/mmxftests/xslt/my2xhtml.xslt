<!--                                                                                                                                                                                  
     This is a very common way to override an xslt.

  @version: $Id: my2xhtml.xslt,v 1.2 2007-06-20 14:29:28 michiel Exp $                                                                                                               
-->
<xsl:stylesheet
  xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
  xmlns:node ="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:o="http://www.mmbase.org/xmlns/objects"
  xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns=""
  exclude-result-prefixes="node o mmxf html"
  version = "1.0"
>
  <xsl:import href="mm:xslt/2xhtml.xslt" />

  <xsl:output method="xml" omit-xml-declaration="yes" /><!-- xhtml is a form of xml -->

  <xsl:template match="mmxf:h" mode="h1"><xsl:if test=". != ''"><h1 class="my"><xsl:apply-templates select="node()" /></h1></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h2"><xsl:if test=". != ''"><h2 class="my"><xsl:apply-templates select="node()" /></h2></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h3"><xsl:if test=". != ''"><h3 class="my"><xsl:apply-templates select="node()" /></h3></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h4"><xsl:if test=". != ''"><h4 class="my"><xsl:apply-templates select="node()" /></h4></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h5"><xsl:if test=". != ''"><h5 class="my"><xsl:apply-templates select="node()" /></h5></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h6"><xsl:if test=". != ''"><h6 class="my"><xsl:apply-templates select="node()" /></h6></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h7"><xsl:if test=". != ''"><h7 class="my"><xsl:apply-templates select="node()" /></h7></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h8"><xsl:if test=". != ''"><h8 class="my"><xsl:apply-templates select="node()" /></h8></xsl:if></xsl:template>

   <xsl:template match="o:object[@type = 'urls']" mode="inline_body">
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:param name="last" />
    <xsl:param name="body" />
    <a>
      <xsl:attribute name="onclick">window.open(this.href, '_blank'); return false;</xsl:attribute>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:apply-templates select="$body">
        <xsl:with-param name="in_a">yes</xsl:with-param>
      </xsl:apply-templates>
    </a>
    <xsl:if test="$position != $last">,</xsl:if>
   </xsl:template>

</xsl:stylesheet>

