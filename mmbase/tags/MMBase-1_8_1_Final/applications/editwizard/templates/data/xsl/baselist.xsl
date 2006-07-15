<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:date="org.mmbase.bridge.util.xml.DateFormat" extension-element-prefixes="date">
  <!--
    Basic parameters and settings for all list xsl's of the
    editwizards.
    
    @since  MMBase-1.6
    @author Michiel Meeuwissen
    @author Nico Klasens
    @author Martijn Houtman

    @version $Id: baselist.xsl,v 1.8 2004-06-03 08:47:38 michiel Exp $
  -->

  <xsl:import href="xsl/base.xsl" />
  <xsl:param name="wizard" />
  <!-- known list attributes -->
  <xsl:param name="origin" />

  <xsl:template name="writeCurrentField">
    <xsl:param name="val" select="."/>
    <xsl:choose>
      <xsl:when test="@guitype='eventtime'">
        <xsl:value-of select="date:format(string($val), $date-pattern, $timezone, $language, $country)" disable-output-escaping="yes"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$val" disable-output-escaping="yes" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>