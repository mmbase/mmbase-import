<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- field includes -->
	<xsl:include href="dynamicform/edit/editfield.xsl"/>
	<xsl:include href="dynamicform/edit/checkboxfield.xsl"/>
	<xsl:include href="dynamicform/edit/datafield.xsl"/>
	<xsl:include href="dynamicform/edit/hiddenfield.xsl"/>
	<xsl:include href="dynamicform/edit/textfield.xsl"/>
	<xsl:include href="dynamicform/edit/selectfield.xsl"/>
	<xsl:include href="dynamicform/edit/horizontalradiofield.xsl"/>
	<xsl:include href="dynamicform/edit/verticalradiofield.xsl"/>
	<xsl:include href="dynamicform/edit/groupedcheckboxfield.xsl"/>
	<xsl:include href="dynamicform/edit/imagefield.xsl"/>
	
	<!-- fieldset includes -->
	<xsl:include href="dynamicform/edit/fieldset.xsl"/>	
	<xsl:include href="dynamicform/edit/sectionnavigation.xsl"/>

	<xsl:template match="section">
		<fieldset>
			<legend>
				<span>
					<xsl:value-of select="@title"/>
				</span>
			</legend>
			<table border="0" cellspacing="0" cellpadding="0">
				<xsl:apply-templates select="field | fieldset"/>
			</table>
		</fieldset>
		<xsl:apply-templates select="navigation"/>
	</xsl:template>
</xsl:stylesheet>
