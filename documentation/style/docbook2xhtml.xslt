<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://nwalsh.com/xsl/documentation/1.0" exclude-result-prefixes="doc" version="1.0">

  <xsl:import href="[BASESTYLESHEET]" />

  <xsl:output 
    method="xml"
    encoding="UTF-8" 
    indent="no" 
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />

  <xsl:template name="user.header.content">
    <div class="navigation">
      <h2><xsl:value-of select="//*/title" /></h2>
      <xsl:for-each select="//section|//sect1">
        <xsl:if test="not(ancestor::section)">
          <p>
            <a>
              <xsl:attribute name="href">#<xsl:if test="@id"><xsl:value-of select="@id" /></xsl:if><xsl:if test="not(@id)"><xsl:value-of select="generate-id()" /></xsl:if></xsl:attribute>
              <xsl:value-of select="title" />
            </a>
          </p>
        </xsl:if>
      </xsl:for-each>
      <hr />
      <p><a href="http://www.mmbase.org">MMBase</a></p>
      <p><a href="/">home</a></p>
    </div>
  </xsl:template>
  
  
  <xsl:template name="user.footer.content">
    <hr />
    <p>
      This is part of the <a href="http://www.mmbase.org">MMBase</a> documentation.
    </p>
  </xsl:template>
</xsl:stylesheet>