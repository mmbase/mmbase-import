<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'checkbox' ]">
		<tr>
			<td class="label-checkbox">
				<xsl:call-template name="field_checkbox_label"/>
			</td>
			<td class="checkbox">
				<xsl:call-template name="field_checkbox_input"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:if test="description">
				<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="field[@guitype = 'leftcheckbox' ]">
		<tr>
			<td class="checkbox">
				<xsl:call-template name="field_checkbox_input"/>
			</td>
			<td class="label-checkbox">
				<xsl:call-template name="field_checkbox_label"/>
			</td>
		</tr>
		<tr>
			<td>
				<xsl:if test="description">
				<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template name="field_checkbox_label">
		<xsl:variable name="errorMsg" select="@error"/>
		<xsl:choose>
			<xsl:when test="$errorMsg != ''">
			<xsl:attribute name="class">redtext</xsl:attribute>
			<img src="{$URLCONTEXT}gfx/dynamicforms/i_alert.gif" alt="Fout" width="14" height="14" border="0" align="absmiddle"/>&#160;
		</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="class">label-checkbox</xsl:attribute>					
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
	</xsl:template>
	
	<xsl:template name="field_checkbox_input">
		<input type="checkbox" id="{@name}" name="{@name}" class="{@class} checkbox" value="true">
				<xsl:if test="'true' = @value">
				<xsl:attribute name="checked">true</xsl:attribute>
				</xsl:if>
		</input>
	</xsl:template>
</xsl:stylesheet>
