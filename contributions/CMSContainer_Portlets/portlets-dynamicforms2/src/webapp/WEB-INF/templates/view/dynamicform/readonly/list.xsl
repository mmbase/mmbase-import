<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="dynamicform/readonly/item.xsl"/>
	<xsl:template match="list" mode="readonly">
	<p>
		<xsl:apply-templates select="item" mode="readonly"/>
	</p>
	</xsl:template>
</xsl:stylesheet>
