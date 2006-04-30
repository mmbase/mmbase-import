<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    exception.xsl
    
    @since  MMBase-1.6.4
    @author Pierre van Rooden
    @author Nico Klasens
    @version $Id: exception.xsl,v 1.6 2003-12-19 11:09:35 nico Exp $
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
            <a href="{$rootreferrer}">Return Home.</a>
          </p>
        </xsl:if>
        <h3 class="error">
          Error:
          <xsl:value-of disable-output-escaping="yes" select="exception" />
        </h3>
        <h3 class="expandederror">Expanded error:</h3>
        <small>
          <pre>
            <xsl:value-of select="stacktrace" />
          </pre>
        </small>
  </xsl:template>

  <xsl:template name="errormessage">
    <h2>DON'T PANIC!</h2>
    <h3>But Something Went Wrong</h3>
    <p>
      An error occurred in the editwizards. This may be caused because you have insufficient rigths to make
      changes, because your edit-session expired, or because the editwizard definition has a bug.
    </p>
    <p>
      When reporting the error, pass the error message (in red, below) and if so requested the expanded message
      to the responsible party.
    </p>
  </xsl:template>

</xsl:stylesheet>