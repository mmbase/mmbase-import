<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'image' ]">
	<xsl:variable name="maxlen">
		<xsl:choose>
			<xsl:when test="@maxlength > 1000000">256</xsl:when>
			<xsl:otherwise><xsl:value-of select="@maxlength"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
		<tr>
			<td >
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
			<xsl:if test="not(tooltip and tooltip/@name !='')">
				<xsl:attribute name="colspan">2</xsl:attribute>
			</xsl:if>
				<input type="file" id="{@name}" name="{@name}" class="{@class}" value="{@value}"/>
				<xsl:if test="description">
    				<span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
    			</xsl:if>
			</td>
			<xsl:if test="tooltip and tooltip/@name !='' ">
			<td class="infobtn">
				<img src="{$URLCONTEXT}gfx/dynamicforms/i_moreinfo_grey.gif" width="15" height="15" border="0" alt=""><xsl:attribute name="onmouseover">showTooltip('<xsl:value-of select="tooltip/@name"/>')</xsl:attribute>
				</img>
			<div id="{tooltip/@name}" class="tooltip">
				<div class="tooltipbody">
					<div class="tooltiptext"><xsl:value-of select="tooltip"/></div>
				</div>
			</div>
			</td>
			</xsl:if>
			<xsl:if test="not(tooltip and tooltip/@name !='')">
<!--			<td class="infobtn">&#160;</td>-->
			</xsl:if>
		</tr>
	</xsl:template>
</xsl:stylesheet>
