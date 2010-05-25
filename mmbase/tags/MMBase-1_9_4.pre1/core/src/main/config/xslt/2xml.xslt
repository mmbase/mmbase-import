<!--
  This translates XML to HTML, put this in a <pre>

  @version: $Id: 2xml.xslt,v 1.2 2008-02-20 18:10:05 michiel Exp $
  @author:  Michiel Meeuwissen
  @todo:    It needs some work still....
-->
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >


<xsl:variable name="tagcolor">red</xsl:variable>
<xsl:variable name="stringcolor">gray</xsl:variable>
<xsl:variable name="attrcolor">green</xsl:variable>


<xsl:output method="xml"
    version="1.0"
    encoding="utf-8"
    omit-xml-declaration="yes"
    standalone="no"
    indent="no"
    />

  <xsl:template match="*">
    <xsl:param name="ident" />
    <xsl:text>
    </xsl:text>
    <xsl:value-of select="$ident" /><xsl:text>&lt;</xsl:text><font color="{$tagcolor}"><xsl:value-of select="name()" /></font>
    <xsl:apply-templates select="@*" />
    <xsl:choose>

      <xsl:when test="not(*) and text()=''">
        <xsl:text>/&gt;</xsl:text>
      </xsl:when>

      <xsl:otherwise>
        <xsl:text>&gt;</xsl:text>
			<xsl:apply-templates select="*|text()">
          <xsl:with-param name="ident" select="concat($ident, '   ')" />
        </xsl:apply-templates>
        <xsl:text>&lt;/</xsl:text>
		    <font color="{$tagcolor}"><xsl:value-of select="name()" /></font>
        <xsl:text>&gt;</xsl:text><br />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:text> </xsl:text>
    <font color="{$attrcolor}"><xsl:value-of select="name()" /></font><xsl:text>="</xsl:text><font color="{$stringcolor}"><xsl:value-of select="." />
    </font>
    <xsl:text>"</xsl:text>
  </xsl:template>

  <xsl:template match="text()"><span class="text"><xsl:value-of select="." /></span></xsl:template>

</xsl:stylesheet>
