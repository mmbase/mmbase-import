<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:variable name="REASON-RIGHTS">U heeft geen rechten op  de rubriek waartoe dit object behoort.</xsl:variable>

  <xsl:variable name="prompt_search_link" >zoeken</xsl:variable>
  <xsl:variable name="prompt_new_link" >nieuw</xsl:variable>
  <xsl:variable name="prompt_edit_link" >bewerken</xsl:variable>

  <xsl:template name="prompt_invalid_list">
    <xsl:param name="minoccurs">0</xsl:param>
    <xsl:param name="maxoccurs">*</xsl:param>
    <xsl:choose>
	    <xsl:when test="$minoccurs = '1' and $minoccurs = $maxoccurs">Selecteer <xsl:value-of select="$maxoccurs"/> element</xsl:when>
	    <xsl:when test="$minoccurs = $maxoccurs">Selecteer <xsl:value-of select="$maxoccurs"/> elementen</xsl:when>
	    <xsl:when test="not($minoccurs = '0') and not($maxoccurs = '*')">Selecteer tenminste <xsl:value-of select="$minoccurs"/> en ten hoogste <xsl:value-of select="$maxoccurs"/> elementen</xsl:when>
	    <xsl:when test="not($minoccurs = '0')">Selecteer tenminste <xsl:value-of select="$minoccurs"/> elementen</xsl:when>
	    <xsl:when test="not($maxoccurs = '*')">Selecteer ten hoogste <xsl:value-of select="$maxoccurs"/> elementen</xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="prompt_new">nieuw</xsl:template>

</xsl:stylesheet>
