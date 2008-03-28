<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:variable name="REASON-RIGHTS">You do not have the required rights on the contentchannel of this object</xsl:variable>

  <xsl:variable name="prompt_search_link" >search</xsl:variable>
  <xsl:variable name="prompt_new_link" >new</xsl:variable>
  <xsl:variable name="prompt_edit_link" >change</xsl:variable>

  <xsl:template name="prompt_invalid_list">
    <xsl:param name="minoccurs">0</xsl:param>
    <xsl:param name="maxoccurs">*</xsl:param>
    <xsl:choose>
	    <xsl:when test="$minoccurs = '1' and $minoccurs = $maxoccurs">Select <xsl:value-of select="$maxoccurs"/> element</xsl:when>
	    <xsl:when test="$minoccurs = $maxoccurs">Select <xsl:value-of select="$maxoccurs"/> elements</xsl:when>
	    <xsl:when test="not($minoccurs = '0') and not($maxoccurs = '*')">At least <xsl:value-of select="minoccurs"/> and at most <xsl:value-of select="maxoccurs"/> elements should be selected</xsl:when>
	    <xsl:when test="not($minoccurs = '0')">At least <xsl:value-of select="minoccurs"/> elements should be selected</xsl:when>
	    <xsl:when test="not($maxoccurs = '*')">At most <xsl:value-of select="maxoccurs"/> elements should be selected</xsl:when>
    </xsl:choose>
  </xsl:template>
	
  <xsl:template name="prompt_new">new</xsl:template>

</xsl:stylesheet>
