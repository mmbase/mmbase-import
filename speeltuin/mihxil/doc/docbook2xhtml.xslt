<?xml version="1.0"?>
<xsl:stylesheet id="docbook2xhtml"   version="1.1"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

  <!-- 
       Ok, I'm crazy creating our own docbook2xhtml...

       This is a very straight-forward one, only supporting what I used.
       Makes it xhtml. Style must be in style.css
   -->

  <xsl:output method="xml"
    version="1.0"
    encoding="UTF-8"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="DTD/xhtml1-strict.dtd"
    indent="yes"
    />

    <xsl:template match="book">      
      <html>
        <head>
          <title><xsl:value-of select="bookinfo/title" /></title>
          <link rel="stylesheet" title="default" type="text/css" href="style.css" />
        </head>
      <body>
        <h1><xsl:value-of select="bookinfo/title" /></h1>
        <div class="toc">
          <h1>Table of contents</h1>
          <xsl:for-each select="chapter|appendix">
            <p>
            <a>
              <xsl:attribute name="href">
                #<xsl:value-of select="./@id" />
              </xsl:attribute>
              <xsl:value-of select="./title" />
            </a>
          </p>
          </xsl:for-each>
        </div>
        <xsl:apply-templates select="chapter|appendix" />
      </body>
    </html>
    </xsl:template>
      
    <xsl:template match="chapter|appendix">
      <h2>
        <xsl:attribute name="class">
          <xsl:value-of select="name()" />
        </xsl:attribute>
        <a>
          <xsl:attribute name="id">
            <xsl:value-of select="@id" />
          </xsl:attribute>
          <xsl:value-of select="title" />
        </a>
        </h2>
      <xsl:apply-templates select="para|sect1|example" />
    </xsl:template>

    <xsl:template match="para">
      <p>
        <xsl:apply-templates select="text()|*" />
      </p>      
    </xsl:template>

    <xsl:template match="sect1">
      <h3><xsl:value-of select="title" /></h3>
      <xsl:apply-templates select="para|sect2" />      
    </xsl:template>
    
    <xsl:template match="sect2">
      <p>
        <em><xsl:value-of select="title" /></em>
      </p>
      <xsl:apply-templates select="para|text()" />      
    </xsl:template>

    <xsl:template match="programlisting">
      <pre>
        <xsl:value-of select="." />
      </pre>      
    </xsl:template>

    <xsl:template match="variablelist|orderedlist">
      <ul>
        <xsl:apply-templates select="*" />
      </ul>      
    </xsl:template>

    <xsl:template match="varlistentry|orderedlist/listitem">
      <li>
        <xsl:apply-templates select="*|text()" />
      </li>      
    </xsl:template>

    <xsl:template match="term">
      <em>
        <xsl:apply-templates select="*|text()" />
        </em>:      
    </xsl:template>

    <xsl:template match="ulink">     
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="@url" />
      </xsl:attribute>
      <xsl:apply-templates select="*|text()" />
    </a>
    </xsl:template>


    <xsl:template match="text()">      
      <xsl:value-of select="." />
    </xsl:template>

  </xsl:stylesheet>