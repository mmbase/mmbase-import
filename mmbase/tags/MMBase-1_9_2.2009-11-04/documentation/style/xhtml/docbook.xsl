<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0" exclude-result-prefixes="doc" version="1.0">

  <xsl:import
      href="http://docbook.sourceforge.net/release/xsl/current/xhtml/chunk.xsl" />

  <xsl:output
    method="xml"
    encoding="UTF-8"
    indent="no"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>


  <!--
    ============================
    Override some things of nwalsh
  -->

  <xsl:param name="toc.section.depth">5</xsl:param>
  <xsl:param name="section.autolabel">1</xsl:param>
  <xsl:param name="section.autolabel.max.depth">3</xsl:param>

  <!-- some chunking options: -->
  <xsl:param name="base.dir" select="'./'"/>

	<xsl:param name="chunk.section.depth" select="count(/article[@class='specification' or @class='faq'])+1" />
  <xsl:param name="use.id.as.filename" select="'$chunk.section.depth'"/>
  <xsl:param name="chunk.quietly" select="$chunk.section.depth"/>
  <!-- set to 0 if you want to know what's happening -->

  <xsl:param name="glossentry.show.acronym">yes</xsl:param>

  <!-- use a custom css -->
  <!--
  <xsl:param name="html.stylesheet"><xsl:value-of select="$root"/>style/documentation.css</xsl:param>
  -->

  <!-- specify meta-tags for utf-8 'cause IE is too dumb -->
  <!--
  <xsl:template name="user.head.content">
    <link rel="icon" href="{$root}style/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="{$root}style/favicon.ico" type="image/x-icon" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  </xsl:template>
  -->

  <!-- list authors after each other -->
  <xsl:template match="author" mode="titlepage.mode">
    <span xmlns="http://www.w3.org/1999/xhtml" class="{name(.)}">
      <!--
      <xsl:call-template name="person.name"/>
      -->
      <xsl:if test="following-sibling::*">
        <xsl:text>, </xsl:text>
      </xsl:if>
    </span>
  </xsl:template>

  <xsl:template match="authorgroup" mode="titlepage.mode">
    <div xmlns="http://www.w3.org/1999/xhtml" class="{name(.)}">
      <strong class="{name(.)}">
        <xsl:text>Author</xsl:text>
        <xsl:if test="count(./author) != 1"><xsl:text>s</xsl:text></xsl:if>
        <xsl:text>: </xsl:text>
      </strong>
      <xsl:apply-templates mode="titlepage.mode"/>
      <br /><strong class="{name(.)}"><xsl:text>Date: </xsl:text></strong> <xsl:value-of select="//*/date"/>
    </div>
  </xsl:template>

  <!-- do not show revision history -->
  <xsl:template match="revhistory" mode="titlepage.mode" />

  <!-- this is for the floating toc on the right -->
  <xsl:template name="user.header.content">
    <div class="navigation">
      <!--
      <img src="{$root}style/logo.png" alt="mmbase logo"/>
      -->
      <h2>
        <xsl:value-of select="//*/title"/>
      </h2>
      <xsl:for-each select="//section|//sect1">
        <xsl:if test="not(ancestor::section)">
          <p>
            <a>
              <xsl:attribute name="href">
                <xsl:call-template name="href.target">
                  <xsl:with-param name="object" select="."/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:value-of select="title"/>
            </a>
          </p>
        </xsl:if>
      </xsl:for-each>
      <hr/>
      <p>
        <a href="http://www.mmbase.org">MMBase</a>
      </p>

      <xsl:variable name="thisfile"><xsl:call-template name="href.target"><xsl:with-param name="object" select="."/></xsl:call-template></xsl:variable>

      <!--
      <xsl:if test="not(($root = './') and ($thisfile = 'index.html'))">
        <p>
          <a href="{$root}index.html">home</a>
        </p>
      </xsl:if>
      -->
    </div>
  </xsl:template>

  <!-- Sort glossary entries -->
  <xsl:template match="glosslist">
    <div xmlns="http://www.w3.org/1999/xhtml" class="{name(.)}">
      <xsl:call-template name="anchor"/>
      <dl>
        <xsl:apply-templates>
          <xsl:sort select="glossterm" />
        </xsl:apply-templates>
      </dl>
    </div>
  </xsl:template>


  <!-- MMBase specific footer on every page -->
  <xsl:template name="user.footer.content">
    <hr/>
    <p>
      This is part of the <a href="http://www.mmbase.org">MMBase</a> documentation.
    </p>
    <p>
      For questions and remarks about this documentation mail to:
      <a href="mailto:documentation@mmbase.org">documentation@mmbase.org</a>
   </p>
  </xsl:template>

</xsl:stylesheet>
