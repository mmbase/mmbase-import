<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    prompts.xls
    @version $Id: prompts.xsl,v 1.1 2004-01-18 12:10:27 nico Exp $
  -->
  <xsl:import href="ew:xsl/prompts.xsl" />

  <!-- prompts for starting a editwizard -->
  <xsl:template name="prompt_edit_wizard">
    <img src="{$mediadir}select.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="alt"><xsl:value-of select="prompt" /></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt"><xsl:value-of select="$tooltip_edit_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>
  
  <xsl:template name="prompt_add_wizard">
    <img src="{$mediadir}new.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="alt"><xsl:value-of select="prompt" /></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt"><xsl:value-of select="$tooltip_add_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>
</xsl:stylesheet>