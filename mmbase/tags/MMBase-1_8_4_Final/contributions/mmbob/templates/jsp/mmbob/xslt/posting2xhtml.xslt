<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >  <xsl:output omit-xml-declaration = "yes" />
  <xsl:template match = "posting" >
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="quote">
    <table width="90%" cellspacing="1" cellpadding="3" border="0" align="center">
      <tr><td align="left"><span class="genmed"><b><xsl:value-of select="@poster"/> wrote:</b></span></td></tr>
      <tr>
        <td class="quote"><xsl:apply-templates /></td>
      </tr>
    </table>
  </xsl:template>    

  <xsl:template match="p|ul|li|a|em|img">
    <xsl:copy-of select="." />
  </xsl:template>



  <xsl:template match="text()">
    <xsl:value-of select="." />
  </xsl:template>

  <xsl:template match="br">
	<br />
  </xsl:template>
  
</xsl:stylesheet>
