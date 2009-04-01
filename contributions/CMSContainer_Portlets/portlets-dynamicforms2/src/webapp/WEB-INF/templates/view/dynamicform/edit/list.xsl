<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="dynamicform/edit/item.xsl"/>
	
	<xsl:template match="list[@guitype = '']">
		<div class="cform">
			<xsl:apply-templates select="item"/>
		</div>
	</xsl:template>
</xsl:stylesheet>
