<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    debug.xsl

    @since  MMBase-1.6.4
    @author Pierre van Rooden
    @author Michiel Meeuwissen
    @author Nico Klasens
    @version $Id: debug.xsl,v 1.8 2008-01-17 15:12:19 michiel Exp $
  -->

  <xsl:import href="xsl/base.xsl" />

  <xsl:output
    method="html"
    encoding="utf-8"
    omit-xml-declaration="no"
    standalone="no"
    doctype-public="-//W3C//DTD XHTML 1.0 Transitional//"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
    indent="yes"
    media-type="mimetype" />


  <xsl:param name="session_byte_size">(undetermined)</xsl:param>

  <xsl:variable name="BodyOnLoad" >init();</xsl:variable>

  <xsl:template name="htmltitle">Editwizard session inspector (debugging)</xsl:template>

  <xsl:template name="style">
    <link rel="stylesheet" type="text/css" href="{$cssdir}layout/debug.css" />
  </xsl:template>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$cssdir}color/debug.css" />
  </xsl:template>

  <xsl:template name="javascript" >
      <script type="text/javascript" src="{$javascriptdir}tools.js"></script>
      <script type="text/javascript" src="{$javascriptdir}debug.js"></script>
  </xsl:template>

  <xsl:template name="bodycontent" >
    <xsl:apply-templates select="debugdata" />
  </xsl:template>

  <xsl:template match="/debugdata">
    Size of session: <xsl:value-of select="$session_byte_size" /> bytes (approximately)
    <br />
    <br />

    <span id="Wizard_{@id}" class="debugwizard">
      <xsl:for-each select="*[@debugname]">
        <span class="tab" id="tab{position()}"
          onclick="changeVisibility({position()});">

          <xsl:value-of select="name()" />
          <span class="debugname">
            (<xsl:value-of select="@debugname" />)
          </span>
        </span>
        |
      </xsl:for-each>
      <br />
      <br />

      <xsl:for-each select="*[@debugname]">
        <div id="panel{position()}" class="panel_hidden">
          <xsl:apply-templates select="." />
        </div>
      </xsl:for-each>
    </span>
  </xsl:template>

  <xsl:template match="*">
    <div class="node">
      <span class="nodename">
        &lt;<xsl:value-of select="name()" />
        <xsl:text> </xsl:text>
      </span>
      <xsl:apply-templates select="@*" />
      <xsl:choose>
        <xsl:when test="not(*) and text()=&apos;&apos;">
          <span class="nodename">/&gt;</span>
        </xsl:when>
        <xsl:otherwise>
          <span class="nodename">&gt;</span>
          <xsl:apply-templates select="*|text()" />
          <span class="nodename">
            &lt;/<xsl:value-of select="name()" />&gt;
          </span>
          <br />
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:value-of select="name()" />
    <xsl:text>="</xsl:text>
    <span class="attrvalue">
      <xsl:value-of select="." />
    </span>
    <xsl:text>" </xsl:text>
  </xsl:template>

  <xsl:template match="text()">
    <span class="text">
      <xsl:value-of select="." />
    </span>
  </xsl:template>

</xsl:stylesheet>
