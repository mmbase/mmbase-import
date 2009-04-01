<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'select' ]">
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
			<xsl:variable name="fieldvalue" select="@value"/>
						<select name="{@name}" id="{@name}" class="{@class}">
							<xsl:for-each select="optionlist/option">
							<option value="{@value}">
								<xsl:if test="@value = $fieldvalue">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if><xsl:value-of select="."/></option>
							</xsl:for-each>
						</select>
				<xsl:if test="description">
				  <span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="field[@guitype = 'clientnumberselect' ]">
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
				<script type="text/javascript">
				  function setcurrentstep(stepvalue) {
					 document.getElementById('activeStep').value=stepvalue;
					 document.esform.submit();
				  }
				</script>
			<xsl:variable name="fieldvalue" select="@value"/>
						<select name="{@name}" id="{@name}" class="{@class}">
<xsl:attribute name="onchange">setcurrentstep('<xsl:value-of select="//formstep/@name"/>')</xsl:attribute>						
							<xsl:for-each select="optionlist/option">
							<option value="{@value}">
								<xsl:if test="@value = $fieldvalue">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if><xsl:value-of select="."/></option>
							</xsl:for-each>
						</select>
				<xsl:if test="description">
				  <span class="greytext"><xsl:value-of select="description" disable-output-escaping="yes"/></span>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
