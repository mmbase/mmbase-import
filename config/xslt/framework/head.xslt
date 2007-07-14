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

      <!-- links -->
      <xsl:variable name="unique-links"
                    select="link[not(. = ./following::link)]" />         

      <xsl:for-each select="$unique-links">
        <link>
          <xsl:copy-of select="@*" />
          <xsl:text> </xsl:text>
        </link>
      </xsl:for-each>

      <!-- links -->
      <xsl:variable name="unique-style"
                    select="$descendants/style[not(. = ./following::style)]" />

      <xsl:for-each select="$unique-style">
        <style>
          <xsl:copy-of select="@*" />
          <xsl:copy-of select="*|text()" />
        </style>
      </xsl:for-each>

      <!-- meta headers must be handles smartly, (keywords?) -->
      <!-- TODO -->
      
      <xsl:variable name="unique-script"
                    select="$descendants/script[not(. = ./following::script)]" />

      <xsl:for-each select="$unique-script">
        <style>
          <xsl:copy-of select="@*" />
          <xsl:copy-of select="*|text()" />
        </style>
      </xsl:for-each>

    </head>
  </xsl:template>
  
  
</xsl:stylesheet>
