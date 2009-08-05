<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'verticalradio' ]">
	<xsl:variable name="fieldvalue" select="@value"/>
	<xsl:variable name="fieldname" select="@name"/>
	<xsl:variable name="fieldtitle" select="@title"/>
	<xsl:variable name="fieldrequired" select="@required"/>
	<xsl:variable name="errorMsg" select="@error"/>
	<xsl:for-each select="optionlist/option">
		<tr>
			<td class="label">
			<xsl:if test="position() = 1">
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
				</xsl:if>
			</td>
			<td class="field">
				<input name="{$fieldname}" id="{$fieldname}" type="radio" value="{@value}" >
					<xsl:if test="@value=$fieldvalue">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</input><xsl:text disable-output-escaping="yes"></xsl:text>
			</td>
		</tr>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="field[@guitype = 'foldverticalradio' ]">
	<xsl:variable name="fieldvalue" select="@value"/>
	<xsl:variable name="fieldname" select="@name"/>
	<xsl:variable name="fieldtitle" select="@title"/>
	<xsl:variable name="fieldrequired" select="@required"/>
	<xsl:variable name="errorMsg" select="@error"/>
		<script type="text/javascript">
	function change(state) {
			if(state) {
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').value='';
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').readOnly = true;			
				} else {
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').readOnly = false;			
				document.getElementById('<xsl:value-of select="../field[contains(@name,'date')]/@name"/>').focus();
			}
		}
		</script>
	<xsl:for-each select="optionlist/option">
		<tr>
			<td class="label">
			<xsl:if test="position() = 1">
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
				</xsl:if>
			</td>
			<td class="field">
				<input name="{$fieldname}" id="{$fieldname}" type="radio" value="{@value}" >
						<xsl:choose>
							<xsl:when test="position() = 1"><xsl:attribute name="onclick">change(true)</xsl:attribute>
</xsl:when>
							<xsl:when test="position() = 2"><xsl:attribute name="onclick">change(false)</xsl:attribute>
</xsl:when>
						</xsl:choose>
					<xsl:if test="@value=$fieldvalue">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="."/>
				</input><xsl:text disable-output-escaping="yes"></xsl:text>
			</td>
		</tr>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
