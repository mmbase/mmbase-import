<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <!-- reads the builder sources from an index file and writes them 
       all into one file -->

  <xsl:output
    method="xml"
    version="1.0"
    encoding="utf-8"
    omit-xml-declaration="no"
    indent="yes"
    />

  <!-- default language setting -->
  <xsl:param name="language" select="'en'"/>

  <!-- general -->
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- root node of the index-file -->
  <xsl:template match="builders">
    <builders xml:lang="{$language}">
      <xsl:apply-templates/>
    </builders> 
  </xsl:template>

  <!-- get all builders per buildertype (core, vwms, community, etc)-->
  <xsl:template match="buildertype">
    <buildertype name="{@name}"> 
      <xsl:apply-templates/>
    </buildertype> 
  </xsl:template>

  <xsl:template match="builder">
    <!-- gets the builder source file --> 
    <xsl:variable name="buildersource">
      <xsl:value-of select='@source'/> 
    </xsl:variable>

    <!-- reads the content of the builder source file -->
    <xsl:variable name="buildercontent" select="document($buildersource)"/>

    <!-- copy content of the buildersource file in destination file -->    
    <xsl:copy-of select="$buildercontent/*" />
 
    <xsl:apply-templates/>

  </xsl:template>

</xsl:stylesheet>
  