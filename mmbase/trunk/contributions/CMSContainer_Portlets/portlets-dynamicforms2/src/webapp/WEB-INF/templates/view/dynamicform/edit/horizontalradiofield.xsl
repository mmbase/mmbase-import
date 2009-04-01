<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'horizontalradio' ]">
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
					<xsl:if test="@required = 'true' and @title != '&#160;' and @title !='' ">
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
			<xsl:variable name="fieldvalue" select="@value"/>
			<xsl:variable name="fielddefaultvalue" select="value"/>
			<xsl:variable name="fieldname" select="@name"/>
			<xsl:variable name="fieldtitle" select="@title"/>
			<td class="field" align="center">
			<xsl:for-each select="optionlist/option">
				<input name="{$fieldname}" id="{$fieldname}" type="radio" value="{@value}"  class="{@class} radio">
					<xsl:if test="position() = last()">
						<xsl:attribute name="align">right</xsl:attribute>
					</xsl:if>
          <xsl:choose>
            <xsl:when test="@value=$fieldvalue">
              <xsl:attribute name="checked">checked</xsl:attribute>
            </xsl:when>
            <xsl:when test="$fieldvalue='' and @value=$fielddefaultvalue">
              <xsl:attribute name="checked">checked</xsl:attribute>              
            </xsl:when>
          </xsl:choose>
					<xsl:if test="@value=$fieldvalue or @value=$fielddefaultvalue">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
				</input>
				<xsl:value-of select="."/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
			</xsl:for-each>
			<xsl:if test="@required = 'true' and @title = '' ">
				<xsl:text> </xsl:text>*
			</xsl:if>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
