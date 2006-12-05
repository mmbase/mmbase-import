<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    exception.xsl
    
    @since  MMBase-1.6.4
    @author Pierre van Rooden
    @author Nico Klasens
    @version $Id: exception.xsl,v 1.1 2006-03-05 21:46:43 henk Exp $
  -->

  <xsl:import href="xsl/base.xsl" />

  <xsl:template name="htmltitle">DON'T PANIC - But Something Went Wrong</xsl:template>

  <xsl:template name="style">
    <link rel="stylesheet" type="text/css" href="{$cssdir}layout/exception.css" />
  </xsl:template>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$cssdir}color/exception.css" />
  </xsl:template>


  <xsl:template name="bodycontent" >
    <xsl:apply-templates select="error" />
  </xsl:template>

  <xsl:template match="error">
        <xsl:call-template name="errormessage" />
        <xsl:if test="$referrer!=&apos;&apos;">
          <p>
            <a href="{$rootreferrer}">Terug naar de startpagina.</a>
          </p>
        </xsl:if>
        <h3 class="error">
          Foutmelding:
          <xsl:value-of disable-output-escaping="yes" select="exception" />
        </h3>
        <h3 class="expandederror">Uitgebreide foutmelding:</h3>
        <small>
          <pre>
            <xsl:value-of select="stacktrace" />
          </pre>
        </small>
  </xsl:template>

  <xsl:template name="errormessage">
    <h2>Onze excuses</h2>
    <h3>Bij het uitvoeren van de laatste bewerking is er iets fout gegaan.</h3>
    <p>
      Helaas is het niet mogelijk om de gegevens, die u nog niet had opgeslagen te herstellen.
    </p>
    <p>
      Zou u de onderstaande foutmelding in een email kunnen doorsturen naar de webmasters? Bij voorbaat dank.
    </p>
  </xsl:template>

</xsl:stylesheet>