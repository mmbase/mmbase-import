<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'groupedcheckbox' ]">
		<xsl:variable name="fieldvalue" select="@value"/>
		<xsl:variable name="fieldname" select="@name"/>
		<xsl:variable name="fieldtitle" select="@title"/>
		<xsl:variable name="fieldrequired" select="@required"/>
		<xsl:variable name="optioncount" select="count(optionlist/option)"/>
		<xsl:variable name="errorMsg" select="@error"/>
		<xsl:for-each select="optionlist/option">
			<tr>
				<xsl:if test="position() = 1">
					<td class="label" rowspan="{$optioncount}">
						<xsl:choose>
							<xsl:when test="$errorMsg != ''">
								<xsl:attribute name="class">redtext</xsl:attribute>
								<img src="{$URLCONTEXT}gfx/dynamicforms/i_alert.gif" alt="Fout" width="14" height="14" border="0" align="absmiddle"/>&#160;
				</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="class">label</xsl:attribute>
							</xsl:otherwise>
						</xsl:choose>
						<label for="{$fieldname}">
							<xsl:value-of select="$fieldtitle" disable-output-escaping="yes"/>
							<xsl:if test="$fieldrequired = 'true' and $fieldtitle != '&#160;'">
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
				</xsl:if>
				<td class="field">
					<input name="{$fieldname}" id="{@value}" type="checkbox" value="{@value}">
						<xsl:if test="@selected = 'true' ">
							<xsl:attribute name="checked">true</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="."/>
					</input>
					<xsl:text disable-output-escaping="yes"/>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="field[@guitype = 'groupedcheckbox' ]" mode="readonly">
		<xsl:variable name="fieldvalue" select="@value"/>
		<xsl:variable name="fieldname" select="@name"/>
		<xsl:variable name="fieldtitle" select="@title"/>
		<xsl:variable name="fieldrequired" select="@required"/>
		<xsl:variable name="optioncount" select="count(optionlist/option)"/>
		<xsl:variable name="errorMsg" select="@error"/>
		<xsl:for-each select="optionlist/option">
						<xsl:if test="@selected = 'true' ">
<xsl:value-of select="$fieldtitle" disable-output-escaping="yes"/>
						</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
