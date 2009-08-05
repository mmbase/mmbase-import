<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="dynamicform/readonly/section.xsl"/>
	<xsl:template match="item" mode="readonly">
		<xsl:apply-templates select="section" mode="readonly"/>
	</xsl:template>
</xsl:stylesheet>
