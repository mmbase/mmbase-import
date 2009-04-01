<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="fieldset[@guitype = 'extrainfo']">
		<xsl:apply-templates select="field[@guitype = 'horizontalradio' ]"/>
		<xsl:apply-templates select="field[@guitype = 'text' ]"/>
	</xsl:template>
	<xsl:template match="fieldset[@guitype = 'horizontaldata']" mode="readonly">
		<tr>
			<td>
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</td>
			<td class="field">
				<xsl:for-each select="field">
					<xsl:value-of select="@value" disable-output-escaping="yes"/>
					<xsl:text> </xsl:text>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
