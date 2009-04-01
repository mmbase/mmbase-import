<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="field[@guitype = 'data' or @guitype = 'bolddata']">
		<tr>
			<td>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:choose>
					<xsl:when test="@guitype = 'bolddata'"><b><xsl:value-of select="@value" disable-output-escaping="yes"/></b></xsl:when>
					<xsl:otherwise><xsl:value-of select="@value" disable-output-escaping="yes"/></xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
		<tr>
			<td class="field" colspan="2">
					<xsl:value-of select="description" disable-output-escaping="yes"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="field[@guitype = 'datatext' or @guitype = 'text' ]" mode="readonly">
		<tr>
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
				</label>
				<xsl:choose>
					<xsl:when test="$errorMsg != ''">
						<div class="redtext">
							<xsl:value-of select="$errorMsg"/>
						</div>
					</xsl:when>
				</xsl:choose>
			</td>
			<td/>
		</tr>
	</xsl:template>

	<xsl:template match="field[@guitype = 'data' or @guitype = 'checkbox' or @guitype = 'leftcheckbox' ]" mode="readonly">
		<tr>
			<td>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:choose>
					<xsl:when test="@guitype = 'bolddata'"><b><xsl:value-of select="@value" disable-output-escaping="yes"/></b></xsl:when>
					<xsl:otherwise><xsl:value-of select="@value" disable-output-escaping="yes"/></xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
  
  	<xsl:template match="field[@guitype = 'edit' ]" mode="readonly">
		<tr>
			<td>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
				<xsl:choose>
					<xsl:when test="@guitype = 'bolddata'"><b><xsl:value-of select="@value" disable-output-escaping="yes"/></b></xsl:when>
					<xsl:otherwise><xsl:value-of select="@value" disable-output-escaping="yes"/></xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="field[@guitype = 'dataselect' ]" mode="readonly">
		<tr>
			<td>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
        <xsl:variable name="fieldvalue" select="@value"/>
				<xsl:choose>
          <xsl:when test="@value = '0'"></xsl:when>
					<xsl:when test="@guitype = 'bolddata'">
            <b>
              <xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
            </b>
          </xsl:when>
					<xsl:otherwise>
            <xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
          </xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="field[@guitype = 'horizontalradio' ]" mode="readonly">
		<tr>
			<td>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
			<td class="field">
        <xsl:variable name="fieldvalue" select="@value"/>
				<xsl:choose>
          <xsl:when test="@value = '0'"></xsl:when>
					<xsl:when test="@guitype = 'bolddata'">
            <b>
              <xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
            </b>
          </xsl:when>
					<xsl:otherwise>
            <xsl:value-of select="optionlist/option[@value = $fieldvalue]/text()" disable-output-escaping="yes"/>
          </xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>


  <xsl:template match="field[@guitype = 'datagroupedcheckbox']" mode="readonly">
    <tr>
			<td>
				<label for="{@name}">
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</label>
			</td>
      <td class="field">
        <xsl:for-each select="optionlist/option[contains(@selected,'true')]">
          <xsl:value-of select="text()"/><br />
        </xsl:for-each>
      </td>
		</tr>
  </xsl:template>

</xsl:stylesheet>
