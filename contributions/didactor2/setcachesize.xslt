<xsl:stylesheet 
    xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >


  <xsl:output method="xml"
              version="1.0"
              encoding="utf-8"
              standalone="yes"
              indent="yes"
              doctype-public="-//MMBase//DTD cache config 1.0//EN" 
              doctype-system="http://www.mmbase.org/dtd/caches_1_0.dtd"
              />

  <xsl:param name="cache">Nodes</xsl:param>
  <xsl:param name="size" />

  <xsl:template match="caches">
    <caches>
      <xsl:apply-templates select="releaseStrategies" />
      <xsl:apply-templates select="cache" />
    </caches>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy-of select="." />
  </xsl:template>

  <xsl:template match="cache">
    <xsl:choose>
      <xsl:when test="@name = $cache">
        <cache name="{@name}">
          <xsl:apply-templates select="status" />
          <size><xsl:value-of select="$size" /></size>
          <xsl:apply-templates select="maxEntrySize" />
        </cache>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="." />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
