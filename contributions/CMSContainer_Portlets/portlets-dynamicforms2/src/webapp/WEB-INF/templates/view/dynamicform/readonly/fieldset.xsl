<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="fieldset[@guitype = 'horizontaledit']">
		<tr>
			<td colspan="3">
				<table cellpadding="0" cellspacing="0" border="0">
					<tr>
						<xsl:for-each select="field">
							<td>
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
						</xsl:for-each>
					</tr>
					<tr>
						<xsl:for-each select="field">
							<td>
								<input type="text" maxlength="{@maxlength}" id="{@name}" name="{@name}" style="width: 116px; margin-right: 2px;" value="{@value}"/>
								<span class="greytext">
									<xsl:value-of select="description" disable-output-escaping="yes"/>
								</span>
							</td>
						</xsl:for-each>
					</tr>
				</table>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="fieldset[@guitype = 'readonlyyouraddress']">
			<xsl:value-of disable-output-escaping="no" select="field[contains(@name,'straat')]/@value"/>
			<xsl:text> </xsl:text>
			<xsl:value-of disable-output-escaping="no" select="field[contains(@name,'huisnummer')]/@value"/><xsl:text> </xsl:text>
			<xsl:value-of disable-output-escaping="no" select="field[contains(@name,'toevoeging')]/@value"/>
			<br/>
			<xsl:value-of disable-output-escaping="no" select="field[contains(@name,'postcode')]/@value"/>
			<xsl:text> </xsl:text>
			<xsl:value-of disable-output-escaping="no" select="field[contains(@name,'plaats')]/@value"/>
			<br/>
			<xsl:value-of disable-output-escaping="no" select="field[contains(@name,'land')]/@value"/>				
	</xsl:template>

	<xsl:template match="fieldset[@guitype = 'readonlyextracontactdata']">
			Tel : <xsl:value-of disable-output-escaping="no" select="field[contains(@name,'telefoon')]/@value"/>
			<br/>
			E-mail: <xsl:value-of disable-output-escaping="no" select="field[contains(@name,'email')]/@value"/>
			<br/>	
	</xsl:template>

	<xsl:template match="fieldset[@guitype = 'readonlyclient']">
				<xsl:if test="field[contains(@name,'geslacht')]">
					<xsl:choose>
						<xsl:when test="field[contains(@name,'geslacht')]/@value = 'Dhr'">
						De heer
					</xsl:when>
						<xsl:otherwise>
						Mevrouw
					</xsl:otherwise>
					</xsl:choose>
					<xsl:text> </xsl:text>
				</xsl:if>
				<xsl:value-of disable-output-escaping="yes" select="field[contains(@name,'voorletters')]/@value"/>
				<xsl:text> </xsl:text>
				<xsl:value-of disable-output-escaping="yes" select="field[contains(@name,'tussenvoegsels')]/@value"/>
				<xsl:text> </xsl:text>
				<xsl:value-of disable-output-escaping="yes" select="field[contains(@name,'naam')]/@value"/>
				<xsl:if test="field[contains(@name,'mobiel_nummer')]">
				<br/>Mobiel telefoonnummer: <xsl:value-of disable-output-escaping="yes" select="field[contains(@name,'mobiel_nummer')]/@value"/>
				</xsl:if>
				<xsl:if test="field[contains(@name,'klantnummer')]">
					<br/>Klantnummer: <xsl:value-of disable-output-escaping="yes" select="field[contains(@name,'klantnummer')]/@value"/>
				</xsl:if>
				<xsl:if test="field[contains(@name,'geboortedatum')]">
					<br/>Geboortedatum: <xsl:value-of disable-output-escaping="yes" select="field[contains(@name,'geboortedatum')]/@value"/>
				</xsl:if>
				<xsl:if test="field[contains(@name,'klantsoort')]">
					<br/>
					<xsl:value-of disable-output-escaping="yes" select="field[contains(@name,'klantsoort')]/@value"/>
				</xsl:if>
	</xsl:template>

	<xsl:template match="fieldset[@guitype = 'verticalradioanddate']" mode="readonly">
		<tr>
			<td>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
      <td class="field">
        <xsl:variable name="fieldvalue" select="field[1]/@value"/>
        <xsl:choose>
          <xsl:when test="field[1]/optionlist/option[1]/@value = $fieldvalue">
            <xsl:value-of select="field[1]/optionlist/option[1]//text()" disable-output-escaping="yes"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="field[contains(@name,'date')]/@value" disable-output-escaping="yes"/>
          </xsl:otherwise>
        </xsl:choose>
      </td>
		</tr>
	</xsl:template>
  
</xsl:stylesheet>