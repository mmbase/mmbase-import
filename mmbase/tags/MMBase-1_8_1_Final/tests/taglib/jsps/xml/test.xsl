<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:test="http://www.mmbase.org/test"
  >
<xsl:output
  method="xml"
  indent="yes"
  omit-xml-declaration="yes"
  />
  
  <xsl:template match="/test:A">
    <p>A:<xsl:value-of select="test:value" /></p>
    <xsl:apply-templates select="test:B" />
  </xsl:template>

  <xsl:template match="test:B">
    <p>B: <xsl:value-of select="test:value" /></p>
    <xsl:apply-templates select="test:C" />
  </xsl:template>

  <xsl:template match="test:C">
    <p>C: <xsl:value-of select="test:value" /></p>
    <xsl:apply-templates select="test:D" />
  </xsl:template>

  <xsl:template match="test:D">
    <p>D: <xsl:value-of select="test:value" /></p>
  </xsl:template>
</xsl:stylesheet>