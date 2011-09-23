<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">   
<xsl:output omit-xml-declaration = "yes" />

  <xsl:template match = "posting" >
    <xsl:apply-templates/>
  </xsl:template> 

  <xsl:template match="quote">
    <xsl:element name="quote">
      <xsl:attribute name="poster">
        <xsl:value-of select="@poster"/>
      </xsl:attribute>
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template> 

  <xsl:template match = "br" >
<xsl:text>&#13;&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="." />
  </xsl:template>



</xsl:stylesheet>
