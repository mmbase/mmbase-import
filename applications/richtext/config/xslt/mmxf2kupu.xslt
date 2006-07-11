<!--
  This translates mmbase XML, normally containing an objects tag. The XML related to this XSL is generated by
  org.mmbase.bridge.util.Generator, and the XSL is invoked by FormatterTag.

  @author:  Michiel Meeuwissen
  @version: $Id: mmxf2kupu.xslt,v 1.6 2006-07-11 18:46:57 michiel Exp $
  @since:   MMBase-1.6
-->
<xsl:stylesheet
  xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
  xmlns:node ="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:o="http://www.mmbase.org/xmlns/objects"
  xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns=""
  exclude-result-prefixes="node o mmxf html"
  version = "1.0"
>
  <xsl:import href="2xhtml.xslt" />   <!-- dealing with mmxf is done there -->

  <xsl:output method="xml"
    omit-xml-declaration="yes" /><!-- xhtml is a form of xml -->


  <xsl:param name="client">msie</xsl:param>

   <!-- If objects is the entrance to this XML, then only handle the root child of it -->
  <xsl:template match="o:objects">
    <xsl:apply-templates select="o:object[1]" />
  </xsl:template>

  <xsl:template match="mmxf:h" mode="h1"><xsl:if test=". != ''"><h1><xsl:apply-templates select="node()" /></h1></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h2"><xsl:if test=". != ''"><h2><xsl:apply-templates select="node()" /></h2></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h3"><xsl:if test=". != ''"><h3><xsl:apply-templates select="node()" /></h3></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h4"><xsl:if test=". != ''"><h4><xsl:apply-templates select="node()" /></h4></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h5"><xsl:if test=". != ''"><h5><xsl:apply-templates select="node()" /></h5></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h6"><xsl:if test=". != ''"><h6><xsl:apply-templates select="node()" /></h6></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h7"><xsl:if test=". != ''"><h7><xsl:apply-templates select="node()" /></h7></xsl:if></xsl:template>
  <xsl:template match="mmxf:h" mode="h8"><xsl:if test=". != ''"><h8><xsl:apply-templates select="node()" /></h8></xsl:if></xsl:template>


  <xsl:template match="/o:objects">
    <xsl:apply-templates select="o:object[1]" />
  </xsl:template>


  <!-- how to present a node -->
  <xsl:template match="o:object">
    <xsl:choose>
      <xsl:when test="o:field[@format='xml'][1]/mmxf:mmxf">
        <xsl:apply-templates select="o:field[@format='xml'][1]/mmxf:mmxf" />
      </xsl:when>
      <xsl:otherwise><!-- should present _something_, FF may hang otherwise -->
        <body>
          <xsl:text>&#xA;</xsl:text>
          <xsl:apply-templates />
          <xsl:text>&#xA;</xsl:text>
        </body>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="o:field[@format='xml']">
    <xsl:choose>
      <xsl:when test="mmxf:mmxf">
        <xsl:apply-templates  />
      </xsl:when>
      <xsl:otherwise><!-- null -->
        <p>.</p>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="mmxf:ul|mmxf:ol">
    <xsl:element name="{name()}">
      <xsl:if test="@type">
        <xsl:attribute name="type"><xsl:value-of select="@type" /></xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="node()" />
    </xsl:element>
  </xsl:template>


  <xsl:template match = "mmxf:mmxf" >
    <body>
      <xsl:text>&#xA;</xsl:text>
      <xsl:apply-templates select="mmxf:p|mmxf:table|mmxf:section|mmxf:ul|mmxf:ol|mmxf:table" />
      <xsl:text>&#xA;</xsl:text>
    </body>
    <xsl:text>&#xA;</xsl:text>
  </xsl:template>

  <xsl:template match="mmxf:td">
    <xsl:element name="{name()}">
      <xsl:copy-of select="@*" />
      <xsl:choose>
        <xsl:when test="@class = 'align-right'">
          <xsl:attribute name="align">right</xsl:attribute>
        </xsl:when>
        <xsl:when test="@class = 'align-left'">
          <xsl:attribute name="align">left</xsl:attribute>
        </xsl:when>
        <xsl:when test="@class = 'align-center'">
          <xsl:attribute name="align">center</xsl:attribute>
        </xsl:when>
      </xsl:choose>
      <xsl:apply-templates select="node()" />
    </xsl:element>
  </xsl:template>

  <xsl:template match="mmxf:em|mmxf:strong">
    <!-- hackery, firefox sends b/i and IE sends em/strong
         Sends in the way the browers likes, because ff does not succeed to remove em/strong
    -->
    <xsl:choose>
      <xsl:when test="$client = 'gecko'">
        <xsl:apply-templates select="." mode="gecko" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="msie" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="mmxf:em" mode="gecko">    
    <xsl:if test=". != ''">
      <xsl:element name="i">
        <xsl:copy-of select="@*" />
        <xsl:apply-templates select="node()" />
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template match="mmxf:strong" mode="gecko">    
    <xsl:if test=". != ''">
      <xsl:element name="b">
        <xsl:copy-of select="@*" />
        <xsl:apply-templates select="node()" />
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template match="mmxf:em|mmxf:strong" mode="msie">    
    <xsl:if test=". != ''">
      <xsl:element name="{name()}">
        <xsl:copy-of select="@*" />
        <xsl:apply-templates select="node()" />
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template match="o:object[@type = 'blocks']" mode="quote">
    <xsl:apply-templates select="o:field[@name = 'body']" />
  </xsl:template>



  <!-- don't want clickable images, and hope the id can survive in the title -->
  <xsl:template match="o:object[@type = 'images']" mode="inline">
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:variable name="icache" select="node:nodeFunction(., $cloud, string(./@id), 'cachednode', 's(100x100&gt;)')" />
    <img>
      <xsl:attribute name="src"><xsl:apply-templates select="$icache" mode="url" /></xsl:attribute>
      <xsl:attribute name="alt"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="title"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="class"><xsl:value-of select="$relation/o:field[@name='class']"  /></xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$relation/o:field[@name='id']" />
        <xsl:if test="$position &gt; 1">bla<xsl:value-of select="$position" /></xsl:if>
      </xsl:attribute>
      <xsl:if test="$icache/o:field[@name='width']">
        <xsl:attribute name="height"><xsl:value-of select="$icache/o:field[@name='height']" /></xsl:attribute>
        <xsl:attribute name="width"><xsl:value-of select="$icache/o:field[@name='width']" /></xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>

  <!--
       Produces output for one o:object of type urls
       params: relation, position, last
  -->
  <xsl:template match="o:object[@type = 'urls' or @type='segments']" mode="inline">
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:param name="last" />
    <a>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$relation/o:field[@name='id']" />
        <xsl:if test="$position &gt; 1"><xsl:value-of select="$position" /></xsl:if>
      </xsl:attribute>
      <xsl:attribute name="class">generated</xsl:attribute>
      <xsl:apply-templates select="." mode="title" />
    </a>
    <xsl:if test="$position != $last">,</xsl:if>
  </xsl:template>


  <xsl:template match="o:object[@type = 'attachments']" mode="inline">
    <xsl:param name="relation" />
    <xsl:param name="position" />
    <xsl:param name="last" />
    <a>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:attribute name="class">generated</xsl:attribute>
      <xsl:text>[</xsl:text>
      <xsl:value-of select="./o:field[@name='mimetype']" />
      <xsl:text>:</xsl:text>
      <xsl:apply-templates select="." mode="title" />
      <xsl:text>]</xsl:text>
    </a>
    <xsl:if test="$position != $last">,</xsl:if>
  </xsl:template>


  <xsl:template match="o:object[@type='segments']" mode="inline_body">
    <xsl:param name="relation" />
    <xsl:param name="body" />
    <a>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:attribute name="alt"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="title"><xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:if test="$body = ''">
        <xsl:attribute name="class">generated</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="$body"  />
    </a>
  </xsl:template>

  <xsl:template match="o:object[@type = 'urls']" mode="inline_body">
    <xsl:param name="relation" />
    <xsl:param name="body" />
    <a>
      <xsl:attribute name="href"><xsl:apply-templates select="." mode="url" /></xsl:attribute>
      <xsl:attribute name="id"><xsl:value-of select="$relation/o:field[@name = 'id']" /></xsl:attribute>
      <xsl:attribute name="alt">External: <xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:attribute name="title">External: <xsl:apply-templates select="." mode="title" /></xsl:attribute>
      <xsl:if test="$body = ''">
        <xsl:attribute name="class">generated</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="$body"  />
    </a>
  </xsl:template>

  <xsl:template match="o:object[@type = 'blocks']" mode="id">
    <xsl:param name="relation" />
    <xsl:text>block_</xsl:text><xsl:value-of select="@id" /><xsl:text>_</xsl:text><xsl:value-of select="$relation/o:field[@name='id']" />
  </xsl:template>


  <xsl:template match="o:object[@type != 'images' and @type != 'icaches' and @type != 'urls' and @type != 'blocks']" mode="url" priority="1">
    <xsl:text>mmbase://</xsl:text><xsl:value-of select="@type" /><xsl:text>/</xsl:text><xsl:value-of select="@id" />
  </xsl:template>

</xsl:stylesheet>
