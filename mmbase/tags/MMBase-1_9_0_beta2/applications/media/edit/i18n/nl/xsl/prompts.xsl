<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!-- Stream manager -->

  <xsl:import href="ew:i18n/nl/xsl/prompts.xsl" /> <!-- extend from standard  editwizard xslt -->

  
  <xsl:variable name="button_current" >Huidig</xsl:variable>
  <xsl:variable name="tooltip_current" >Zet dit veld op de huidige positie van de stream in de speler</xsl:variable>

  <xsl:variable name="button_start" >Begin</xsl:variable>
  <xsl:variable name="tooltip_start" >Zet dit veld op het begin van stream (0)</xsl:variable>

  <xsl:variable name="button_end" >Eind</xsl:variable>
  <xsl:variable name="tooltip_end" >Zet dit veld op het eind van stream</xsl:variable>

  <xsl:variable name="button_check" >Check</xsl:variable>
  <xsl:variable name="tooltip_check" >Zet de waarde van dit veld terug naar de speler</xsl:variable>

  <xsl:variable name="button_next" >Volgende</xsl:variable>
  <xsl:variable name="tooltip_next" >Zet dit veld veld gelijk aan het volgende 'positie veld'. Op deze manier kunnen items makkelijk tegen elkaar gelegd worden.</xsl:variable>

  <xsl:variable name="button_previous" >Vorige</xsl:variable>
  <xsl:variable name="tooltip_previous" >Zet dit veld veld gelijk aan het vorige 'positie veld'. Op deze manier kunnen items makkelijk tegen elkaar gelegd worden.</xsl:variable>

</xsl:stylesheet>