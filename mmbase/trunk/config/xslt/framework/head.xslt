<xsl:stylesheet 
    xmlns="http://www.w3.org/1999/xhtml" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    exclude-result-prefixes=""
    version = "1.0" >  
  <xsl:output method="xml"
              version="1.0"
              encoding="utf-8"
              omit-xml-declaration="yes"
              indent="no"
              />
  <xsl:template match="head">
    <head>
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
      <!-- links -->
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

      <!-- links -->
      <xsl:variable name="unique-style"
                    select="$descendants/style[not(. = ./following-sibling::style and
                            string(./@media)   = string(./following-sibling::style/@media) and
                            string(./@title)   = string(./following-sibling::style/@title) and
                            string(./@type)   = string(./following-sibling::style/@type)
                            )]" />

      <xsl:for-each select="$unique-style">
        <style>
          <xsl:copy-of select="@*" />
          <xsl:copy-of select="*|text()" />
        </style>
      </xsl:for-each>

      <!-- meta headers must be handles smartly, (keywords?) -->
      <!-- TODO -->
      
      <xsl:variable name="unique-script"
                    select="$descendants/script[not(. = ./following-sibling::script and
                            string(./@charset)   = string(./following-sibling::script/@charset) and
                            string(./@defer)   = string(./following-sibling::script/@charset) and
                            string(./@language)   = string(./following-sibling::script/@language) and
                            string(./@src)   = string(./following-sibling::script/@src) and
                            string(./@type)   = string(./following-sibling::script/@type)
                            )]" />

      <xsl:for-each select="$unique-script">
        <style>
          <xsl:copy-of select="@*" />
          <xsl:copy-of select="*|text()" />
        </style>
      </xsl:for-each>

    </head>
  </xsl:template>
  
  
</xsl:stylesheet>
