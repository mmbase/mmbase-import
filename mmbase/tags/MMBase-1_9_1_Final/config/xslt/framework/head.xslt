<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes=""
    version = "1.0" >
  <!--
      This xslt can be used in MMBase framework implementations, to merge the result 'head' of head
      renderers, as supplied by blocks on the page, with their own head.

      See http://cvs.mmbase.org/viewcvs/html/mmbase/admin/index.jsp?revision=1.14&view=markup for a usage example.

      @version $Id: head.xslt,v 1.6 2008-12-30 13:58:33 michiel Exp $
      @author Michiel Meeuwissen
      @since MMBase-1.9
  -->
  <xsl:output method="xml"
              version="1.0"
              encoding="utf-8"
              omit-xml-declaration="yes"
              indent="no"
              />

  <xsl:template match="head">
    <head>
      <xsl:copy-of select="@*" />

      <xsl:variable name="descendants" select="./descendant-or-self::*" />

      <title>
        <xsl:for-each select="$descendants/title">
          <xsl:if test="position() &gt; 1 and string-length(text()) &gt; 0"> - </xsl:if>
          <xsl:copy-of select="text()" />
        </xsl:for-each>
      </title>

      <!--
          As you may understand, i'm pretty much starting to hate XSLT.
      -->
      <!-- link -->
      <xsl:variable name="unique-links"
                    select="$descendants/link[not(. = ./following-sibling::link and
                            string(./@rel)  = string(./following-sibling::link/@rel) and
                            string(./@href) = string(./following-sibling::link/@href) and
                            string(./@type) = string(./following-sibling::link/@type) and
                            string(./@target) = string(./following-sibling::link/@target) and
                            string(./@rev)    = string(./following-sibling::link/@rev) and
                            string(./@hreflang) = string(./following-sibling::link/@hreflang) and
                            string(./@target)   = string(./following-sibling::link/@target)
                            )]" />

      <xsl:for-each select="$unique-links">
        <link>
          <xsl:copy-of select="@*" />
          <xsl:text> </xsl:text>
        </link>
      </xsl:for-each>

      <!-- style -->
      <xsl:variable name="unique-style"
                    select="$descendants/style[not(. = ./following-sibling::style and
                            string(./@media)   = string(./following-sibling::style/@media) and
                            string(./@title)   = string(./following-sibling::style/@title) and
                            string(./@type)   = string(./following-sibling::style/@type) and
                            string(./@id)   = string(./following-sibling::style/@id)
                            )]" />

      <xsl:for-each select="$unique-style">
        <style>
          <xsl:copy-of select="@*" />
          <xsl:copy-of select="*|text()" />
        </style>
      </xsl:for-each>

      <!-- script -->
      <xsl:for-each select="$descendants/script">
        <xsl:if test="count(preceding-sibling::script[
                      string(@src)      = string(current()/@src) and
                      string(@charset)  = string(current()/@charset) and
                      string(@defer)    = string(current()/@defer) and
                      string(@language) = string(current()/@language) and
                      string(@type)     = string(current()/@type) and
                      string(@id)       = string(current()/@id)
                      ]) = 0">
          <script>
            <xsl:copy-of select="@*" />
            <xsl:copy-of select="*|text()" />
            <xsl:comment>help</xsl:comment>
          </script>
        </xsl:if>
      </xsl:for-each>

      <xsl:variable name="unique-no-script"
                    select="$descendants/noscript[not(. = ./following-sibling::noscript and
                            string(./@id)   = string(./following-sibling::noscript/@id)
                            )]" />

      <xsl:for-each select="$unique-no-script">
        <noscript>
          <xsl:copy-of select="@*" />
          <xsl:copy-of select="*|text()" />
        </noscript>
      </xsl:for-each>

      <!--
           meta

           support for some of the more common, sensible,  meta-headers
           For most of them, the content can be merged.
           No support for http-equiv meta headers.
           Framework should issue real http headers.
      -->
      <xsl:if test="$descendants/meta[@name = 'author']">
        <meta name="author">
          <xsl:attribute name="content">
            <xsl:for-each select="$descendants/meta[@name='author']">
              <xsl:if test="position() &gt; 1 and string-length(@content) &gt; 0">, </xsl:if>
              <xsl:value-of select="@content" />
            </xsl:for-each>
          </xsl:attribute>
        </meta>
      </xsl:if>
      <xsl:if test="$descendants/meta[@name = 'keywords']">
        <meta name="keywords">
          <xsl:attribute name="content">
            <xsl:for-each select="$descendants/meta[@name='keywords']">
              <xsl:if test="position() &gt; 1 and string-length(@content) &gt; 0">, </xsl:if>
              <xsl:value-of select="@content" />
            </xsl:for-each>
          </xsl:attribute>
        </meta>
      </xsl:if>
      <xsl:if test="$descendants/meta[@name = 'description']">
        <meta name="description">
          <xsl:attribute name="content">
            <xsl:for-each select="$descendants/meta[@name='description']">
              <xsl:if test="position() &gt; 1 and string-length(@content) &gt; 0">, </xsl:if>
              <xsl:value-of select="@content" />
            </xsl:for-each>
          </xsl:attribute>
        </meta>
      </xsl:if>
      <xsl:if test="$descendants/meta[@name = 'revised']">
        <meta name="revised">
          <xsl:attribute name="content">
            <xsl:for-each select="$descendants/meta[@name='revised']">
              <xsl:if test="position() &gt; 1 and string-length(@content) &gt; 0">, </xsl:if>
              <xsl:value-of select="@content" />
            </xsl:for-each>
          </xsl:attribute>
        </meta>
      </xsl:if>

      <meta name="generator" content="MMBase" />

    </head>
  </xsl:template>


</xsl:stylesheet>
