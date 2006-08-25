<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="xml" omit-xml-declaration="yes" />
  
  <xsl:variable name="spaces" select="'                                '" />
  
  <xsl:template match="*">
    <xsl:variable name="indent" select="substring($spaces, 0, count(ancestor::*) * 3)" />
    <xsl:text>
</xsl:text>    
    <xsl:value-of select="$indent" />
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
      <xsl:if test="count(child::*) > 0">
        <xsl:text>
</xsl:text><xsl:value-of select="$indent" />           
      </xsl:if>
    </xsl:copy>
  </xsl:template>
  
  
  
  <xsl:template match="text()">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates mode="text" />
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="comment()|processing-instruction()">
    <xsl:copy />
  </xsl:template>
  
</xsl:stylesheet>
