<!--
    The idea of this XSL is that it can be used in block-definitions like so:


  <block name="Statistics"
         classification="mmbase.documentation"
         mimetype="text/html">
    <body>
      <class name="org.mmbase.framework.ResourceRenderer">
        <param name="resource">documentation/mmstatistics.xml</param>
        <param name="type">config</param>
        <param name="xslt">xslt/docbook2block.xslt</param>
      </class>
    </body>
  </block>

  Like that you can add blocks to your compoennt which are their documentation.

  Could perhaps use nwalsh xslt but that seems a huge overkill. It should be rather simple, we probably use only a small subset of docbook.

  @author:  Michiel Meeuwissen
  @version: $Id: docbook2block.xslt,v 1.1 2008-10-15 14:32:09 michiel Exp $
  @since:   MMBase-1.9
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
    xmlns:taglib="org.mmbase.bridge.jsp.taglib.functions.Functions"
    xmlns:o="http://www.mmbase.org/xmlns/objects"
    xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="node mmxf o taglib"
    version="1.0" >

  <xsl:output method="xml"
              omit-xml-declaration="yes" /> <!-- xhtml is a form of xml -->

  <xsl:template match="article">
    <div class="mm_c" >
      <h1><xsl:value-of select="articleinfo/title" /></h1>
      <xsl:for-each select="articleinfo/authorgroup/author">
        <em>
          <xsl:value-of select="surname" />
          <xsl:text> </xsl:text>
          <xsl:value-of select="firstname" />
        </em>
      </xsl:for-each>
      <xsl:apply-templates select="section" />
    </div>
  </xsl:template>


  <xsl:template match="title">
    <xsl:variable name="depth"><xsl:value-of select="count(ancestor::section)" /></xsl:variable>
    <xsl:if test="$depth=1"><xsl:apply-templates select="." mode="h2" /></xsl:if>
    <xsl:if test="$depth=2"><xsl:apply-templates select="." mode="h3" /></xsl:if>
    <xsl:if test="$depth>2"><xsl:apply-templates select="." mode="deeper" /></xsl:if>
  </xsl:template>

  <xsl:template match="title" mode="h2">
    <h2><xsl:value-of select="text()" /></h2>
  </xsl:template>
  <xsl:template match="title" mode="h3">
    <h3><xsl:value-of select="text()" /></h3>
  </xsl:template>
  <xsl:template match="title" mode="deeper">
    <p><em><xsl:value-of select="text()" /></em></p>
  </xsl:template>


  <xsl:template match="section">
    <div id="{@id}">
      <xsl:apply-templates select="title" />
      <xsl:apply-templates select="para|section" />
    </div>
  </xsl:template>

</xsl:stylesheet>
