<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:j2ee="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:preserve-space elements=""/>
  <xsl:param name="mergeXmlFile" />

  <xsl:variable name="mergeXml" select="document($mergeXmlFile)"/>

  <xsl:template match="/j2ee:web-app">
    <web-app>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="j2ee:description" />
      <xsl:apply-templates select="j2ee:display-param" />
      <xsl:apply-templates select="j2ee:context-param" />
      <xsl:apply-templates select="$mergeXml/j2ee:web-app/j2ee:context-param" />
      <xsl:apply-templates select="j2ee:filter" />
      <xsl:apply-templates select="$mergeXml/j2ee:web-app/j2ee:filter" />
      <xsl:apply-templates select="j2ee:filter-mapping" />
      <xsl:apply-templates select="$mergeXml/j2ee:web-app/j2ee:filter-mapping" />
      <xsl:apply-templates select="j2ee:servlet" />
      <xsl:apply-templates select="$mergeXml/j2ee:web-app/j2ee:servlet" />
      <xsl:apply-templates select="j2ee:servlet-mapping" />
      <xsl:apply-templates select="$mergeXml/j2ee:web-app/j2ee:servlet-mapping" />
      <xsl:apply-templates select="j2ee:mime-mapping" />
      <xsl:apply-templates select="$mergeXml/j2ee:web-app/j2ee:mime-mapping" />
      <xsl:apply-templates select="j2ee:welcome-file-list" />
      <xsl:apply-templates select="j2ee:taglib" />
      <xsl:apply-templates select="$mergeXml/j2ee:web-app/j2ee:taglib" />
      <xsl:apply-templates select="j2ee:error-page" />
    </web-app>
  </xsl:template>


  <xsl:template match="*">
    <xsl:copy-of select="." />
  </xsl:template>

</xsl:stylesheet>