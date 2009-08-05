<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:date="http://exslt.org/dates-and-times"
    extension-element-prefixes="date">
    
  <xsl:output encoding="UTF-8" indent="yes" method="xml" />
  <xsl:strip-space elements="omroep" />
  <xsl:variable name="now" select="date:date-time()" />
  <xsl:variable name="lc">abcdefghijklmnopqrstuvwxyz</xsl:variable>
  <xsl:variable name="uc">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
  
  <xsl:template match="nebo_stream_export">
    <xsl:element name="streams">
      <xsl:comment> 
        Created by MMBase on <xsl:value-of select="$now" /> :-) 
      </xsl:comment>
      <xsl:apply-templates />   
    </xsl:element>
  </xsl:template>
      
  <xsl:template match="aflevering">
    <xsl:for-each select="omroepen/omroep">
      <xsl:variable name="omroep" select="translate(., $uc, $lc)" /> <!-- trick to convert to lowercase in xsl 1.0 -->
      <xsl:if test="$omroep = 'teleac/not'"><xsl:call-template name="streams" /></xsl:if>
      <xsl:if test="$omroep = 'teleacnot'" ><xsl:call-template name="streams" /></xsl:if>
      <xsl:if test="$omroep = 'teleac not'"><xsl:call-template name="streams" /></xsl:if>
      <xsl:if test="$omroep = 'teleac'"    ><xsl:call-template name="streams" /></xsl:if>
      <xsl:if test="$omroep = 'schooltv'"  ><xsl:call-template name="streams" /></xsl:if>
      <xsl:if test="$omroep = 'peutertv'"  ><xsl:call-template name="streams" /></xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="streams">
    <xsl:element name="stream">
      <xsl:variable name="medium" select="../../medium" />
      <xsl:attribute name="aflid"><xsl:value-of select="../../@id" /></xsl:attribute>
      <xsl:element name="title"><xsl:value-of select="../../tite" /></xsl:element>
      <xsl:element name="intro"><xsl:value-of select="../../inhk" /></xsl:element>
      <xsl:element name="body"><xsl:value-of select="../../gids_tekst" /></xsl:element>
      <xsl:choose>
        <xsl:when test="$medium = 'Radio'">
          <xsl:element name="pattern">pattern_audio_nebo</xsl:element>
        </xsl:when>
        <xsl:when test="$medium = 'radio'">
          <xsl:element name="pattern">pattern_audio_nebo</xsl:element>
        </xsl:when>
        <xsl:otherwise>
          <xsl:element name="pattern">pattern_video_nebo</xsl:element>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:element name="neboid"><xsl:value-of select="../../@id" /></xsl:element>
      <xsl:element name="fragment"><xsl:value-of select="../../streams/player_url" /></xsl:element>
      <xsl:element name="availabilty">ongebruikt</xsl:element>
      <xsl:element name="begin"><xsl:value-of select="../../streams/@publicatie_start" /></xsl:element>
      <xsl:element name="end"><xsl:value-of select="../../streams/@publicatie_eind" /></xsl:element>
      <xsl:element name="showit"><xsl:value-of select="../../streams/@webcast_toegestaan" /></xsl:element>
    </xsl:element>  
    </xsl:template>
    
</xsl:stylesheet>
