<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0" >  <xsl:output omit-xml-declaration = "yes" />

  <xsl:param name="startdark">false</xsl:param>

  <xsl:template match = "posting" >
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="quote">
    <xsl:param name="startdark" />

    <xsl:if test="$startdark=&apos;true&apos; or $startdark=&apos;&apos;">
    
    <table width="90%" cellspacing="1" cellpadding="3" border="0" align="center" class="list">
      <tr><td class="threadpagelistevenq">
          <span>
          <b><xsl:value-of select="@poster"/> wrote:</b></span>
      </td></tr>
      <tr>
        <td class="threadpagelistevenq"><xsl:apply-templates><xsl:with-param name="startdark">false</xsl:with-param></xsl:apply-templates></td>
      </tr>
    </table>
    </xsl:if>

    <xsl:if test="$startdark=&apos;false&apos;">
                                                                                                                             
    <table width="90%" cellspacing="1" cellpadding="3" border="0" align="center" class="list">
      <tr><td class="threadpagelistoddq">
           <span>
           <b><xsl:value-of select="@poster"/> wrote:</b></span>
      </td></tr>
      <tr>
        <td class="threadpagelistoddq"><xsl:apply-templates><xsl:with-param name="startdark">true</xsl:with-param></xsl:apply-templates></td>
      </tr>
    </table>
    </xsl:if>
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
