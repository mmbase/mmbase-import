<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'text' ]">
		<tr>
			<td class="label">
				<xsl:variable name="errorMsg" select="@error"/>
				<xsl:choose>
					<xsl:when test="$errorMsg != ''">
					<xsl:attribute name="class">redtext</xsl:attribute>
					<img src="{$URLCONTEXT}gfx/dynamicforms/i_alert.gif" alt="Fout" width="14" height="14" border="0" align="absmiddle"/>&#160;
				</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">label</xsl:attribute>					
					</xsl:otherwise>
				</xsl:choose>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
					<xsl:if test="@required = 'true' and @title != '&#160;'">
						<xsl:text> </xsl:text>*
					</xsl:if>
				</label>
				<xsl:choose>
					<xsl:when test="$errorMsg != ''">
						<div class="redtext">
							<xsl:value-of select="$errorMsg"/>
						</div>
					</xsl:when>
				</xsl:choose>
			</td>
			<td class="field">
			<textarea rows="{rows}" cols="{cols}" id="{@name}" name="{@name}" class="{@class}">
			    <xsl:value-of select="@value"/>
			</textarea>
				<xsl:if test="description">
				<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
