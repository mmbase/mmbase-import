<?xml version="1.0"?>

<xsl:stylesheet   
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:rss="http://purl.org/rss/1.0/"
    xmlns:content="http://purl.org/rss/1.0/modules/content/"
    xmlns:annotate="http://purl.org/rss/1.0/modules/annotate/"
    xmlns:admin="http://webns.net/mvcb/"
    xmlns:image="http://purl.org/rss/1.0/modules/image/"
    xmlns:cc="http://web.resource.org/cc/"
    xmlns:reqv="http://purl.org/rss/1.0/modules/richequiv/"
    exclude-result-prefixes="rdf dc dcterms rss im content rss-simple im annotate admin image cc reqv"                   
    version="1.0">

    <xsl:output method="html"/>

    <!-- Setup -->
    <xsl:template match="/">
        <TABLE WIDTH="100%" BORDER="0" CELLPADDING="2" CELLSPACING="0">
            <xsl:apply-templates/>
        </TABLE>
    </xsl:template>

    <!-- Items -->
    <xsl:template match="item">
        <TR><TD>
            - <A TARGET="_popup" STYLE="text-decoration: none;">
	            <xsl:attribute name="HREF">
	                <xsl:value-of select="link"/>
	            </xsl:attribute>
	            <xsl:value-of select="title"/>
            </A>
        </TD></TR>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="text()"/>      
</xsl:stylesheet>