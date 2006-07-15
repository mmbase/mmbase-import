<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- Stream manager -->

  <xsl:import href="ew:xsl/prompts.xsl" /> <!-- extend from standard  editwizard xslt -->


  <xsl:variable name="button_current" >Current</xsl:variable>
  <xsl:variable name="tooltip_current" >Set this field to the current position of the stream in the player</xsl:variable>
      

  <xsl:variable name="button_start" >Begin</xsl:variable>
  <xsl:variable name="tooltip_start" >Set this field to the beginning of the stream (0)</xsl:variable>

  <xsl:variable name="button_end" >End</xsl:variable>
  <xsl:variable name="tooltip_end" >Set this field to the end of the stream</xsl:variable>

  <xsl:variable name="button_check" >Check</xsl:variable>
  <xsl:variable name="tooltip_check" >Set the value of this field back to the player</xsl:variable>

  <xsl:variable name="button_next" >Next</xsl:variable>
  <xsl:variable name="tooltip_next" >Sets this field equals to the next 'position field'. You can easily link two items with this.</xsl:variable>

  <xsl:variable name="button_previous" >Previous</xsl:variable>
  <xsl:variable name="tooltip_previous" >Sets this field equals to the previus 'position field'. You can easily link two items with this.</xsl:variable>


</xsl:stylesheet>