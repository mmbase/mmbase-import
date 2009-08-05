<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="navigation" mode="readonly">
			<xsl:apply-templates select="navitem" mode="readonly"/>
	</xsl:template>
	<xsl:template match="navitem" mode="readonly">
		<div class="{@class}">
					<a class="tablelink" >
				<xsl:attribute name="href">javascript:setstepvalue('');readonlysubmit();</xsl:attribute><xsl:value-of select="@title"/></a>
		</div>
	</xsl:template>
</xsl:stylesheet>
