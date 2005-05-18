<!--
  This translates a mmbase XML field to enriched ASCII

  @author: Michiel Meeuwissen
  @version: $Id: mmxf2rich.xslt,v 1.4 2005-05-18 15:35:17 michiel Exp $
  @since:  MMBase-1.6   
-->
<xsl:stylesheet 
    xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" 
    xmlns:mmxf="http://www.mmbase.org/mmxf"
    version = "1.0" >
  <xsl:output method = "text" />
  
  <xsl:template match = "mmxf:mmxf" >
    <xsl:apply-templates select="mmxf:p" />
    <xsl:apply-templates select="mmxf:section">
      <xsl:with-param name="depth">$</xsl:with-param>
    </xsl:apply-templates>   
  </xsl:template>
  
  <xsl:template match = "mmxf:p|mmxf:ul" >
    <xsl:apply-templates select="mmxf:a|mmxf:em|text()|mmxf:ul" />
    <xsl:text>
      
</xsl:text>
  </xsl:template>
  
  <xsl:template match = "mmxf:section" >
    <xsl:param name="depth" />
    <xsl:value-of select="$depth" /><xsl:text> </xsl:text><xsl:value-of select="mmxf:h" />
    <xsl:text>
      
</xsl:text>
    <xsl:apply-templates select = "mmxf:section">
      <xsl:with-param name="depth">$<xsl:value-of select="$depth" /></xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select = "mmxf:p|mmxf:ul" />
  </xsl:template>
  
  <xsl:template match="mmxf:em" >
    <xsl:text>_</xsl:text><xsl:value-of select = "." /><xsl:text>_</xsl:text>
  </xsl:template>
  
  <xsl:template match="mmxf:ul" >
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select = "mmxf:li" />
  </xsl:template>
  
  <xsl:template match="mmxf:li" >
    <xsl:text>- </xsl:text><xsl:apply-templates /><xsl:text>
</xsl:text>
  </xsl:template>
  
  
  <xsl:template match="text()">
    <xsl:value-of select="." />
  </xsl:template>
  
</xsl:stylesheet>
