<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- field includes -->
	<xsl:include href="dynamicform/edit/datafield.xsl"/>
	<xsl:include href="dynamicform/edit/hiddenfield.xsl"/>
	<xsl:include href="dynamicform/readonly/fieldset.xsl"/>
	<xsl:include href="dynamicform/readonly/sectionnavigation.xsl"/>
	<xsl:template match="section" mode="readonly">
		<div>
			<xsl:attribute name="class"><xsl:choose><xsl:when test="position() mod 2 = 0">roweven</xsl:when><xsl:when test="position() mod 2 = 1">rowodd</xsl:when></xsl:choose></xsl:attribute>
			<div class="spacer">
				<xsl:apply-templates select="navigation" mode="readonly"/>
				<h3><xsl:value-of select="@title"/></h3>
			</div>
			<p>
        <table border="0" cellspacing="0" cellpadding="0">
      		<xsl:apply-templates select="field | fieldset" mode="readonly"/>
        </table>
			</p>
		</div>
	</xsl:template>
</xsl:stylesheet>
