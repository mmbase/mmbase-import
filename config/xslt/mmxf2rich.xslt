<!--
  This translates a mmbase XML field to enriched ASCII

  @author: Michiel Meeuwissen
  @version: $Id: mmxf2rich.xslt,v 1.3 2004-03-02 16:50:41 michiel Exp $
  @since:  MMBase-1.6   
-->
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >
  <xsl:output method = "text" />
  
    <xsl:template match = "mmxf" >
    <xsl:apply-templates select="p" />
    <xsl:apply-templates select="section">
      <xsl:with-param name="depth">$</xsl:with-param>
    </xsl:apply-templates>   
  </xsl:template>
  
  <xsl:template match = "p|ul" >
	  <xsl:apply-templates select="a|em|text()|ul" />
    <xsl:text>

</xsl:text>
  </xsl:template>
  
  <xsl:template match = "section" >
    <xsl:param name="depth" />
    <xsl:value-of select="$depth" /><xsl:text> </xsl:text><xsl:value-of select="h" />
    <xsl:text>

</xsl:text>
 	  <xsl:apply-templates select = "section">
	    <xsl:with-param name="depth">$<xsl:value-of select="$depth" /></xsl:with-param>
	  </xsl:apply-templates>
 	  <xsl:apply-templates select = "p|ul" />
  </xsl:template>

  <xsl:template match="em" >
    <xsl:text>_</xsl:text><xsl:value-of select = "." /><xsl:text>_</xsl:text>
  </xsl:template>

  <xsl:template match="ul" >
    <xsl:text>
</xsl:text>
	  <xsl:apply-templates select = "li" />
  </xsl:template>

  <xsl:template match="li" >
    <xsl:text>- </xsl:text><xsl:apply-templates /><xsl:text>
</xsl:text>
  </xsl:template>


  <xsl:template match="text()">
    <xsl:value-of select="." />
  </xsl:template>
  
</xsl:stylesheet>
