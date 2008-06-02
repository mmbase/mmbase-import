<?xml version="1.0" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
<xsl:template match="/rss/channel">
<p>
<a><xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute>
<xsl:value-of select="title"/></a>
</p>
<p>
<xsl:value-of select="lastBuildDate"/>
</p>
<dl>
<xsl:for-each select="item">
    <dt>
        <a><xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute>
        <xsl:value-of select="title"/></a>
    </dt>
    <dd>
        <xsl:value-of select="description"/> (<xsl:value-of select="pubDate"/>)
    </dd>
</xsl:for-each>
</dl>
<p>
<xsl:value-of select="copyright"/>
</p>
</xsl:template>
</xsl:stylesheet>