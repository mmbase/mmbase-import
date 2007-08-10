<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards for buttons as image.

   Author: Nico Klasens
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">
  
  <xsl:variable name="BodyOnLoad">preLoadButtons(); doOnLoad_ew(); start_validator();  xinha_init(); initPopCalendar();</xsl:variable>
  
  <xsl:template name="savebutton">
    <img
      id="bottombutton-save"
      onclick="doSave();"
      titlesave="{$tooltip_save}" titlenosave="{$tooltip_no_save}"
      enabledsrc="{$mediadir}save.gif"
      disabledsrc="{$mediadir}save_disabled.gif">
      <xsl:if test="@allowsave=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />save.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_save" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave=&apos;false&apos;">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />save_disabled.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_save" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template name="saveonlybutton">
    <img
        id="bottombutton-saveonly"
        onclick="doSaveOnly();"
        titlesave="{$tooltip_save_only}" titlenosave="{$tooltip_no_save}"
        enabledsrc="{$mediadir}saveonly.gif"
        disabledsrc="{$mediadir}saveonly_disabled.gif">
        <xsl:if test="@allowsave=&apos;true&apos;">
          <xsl:attribute name="class">bottombutton</xsl:attribute>
          <xsl:attribute name="src"><xsl:value-of select="$mediadir" />saveonly.gif</xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="$tooltip_save_only" /></xsl:attribute>
        </xsl:if>
        <xsl:if test="@allowsave=&apos;false&apos;">
      <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
          <xsl:attribute name="src"><xsl:value-of select="$mediadir" />saveonly_disabled.gif</xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_save" /></xsl:attribute>
          <xsl:attribute name="disabled" />
        </xsl:if>
    </img>  
  </xsl:template>

  <xsl:template name="cancelbutton">
    <img
      id="bottombutton-cancel"
      onclick="doCancel();"
      enabledsrc="{$mediadir}cancel.gif"
      disabledsrc="{$mediadir}cancel_disabled.gif">
      <xsl:if test="@allowcancel=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />cancel.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_cancel" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowcancel=&apos;false&apos;">
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />cancel_disabled.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_cancel" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>
  
</xsl:stylesheet>