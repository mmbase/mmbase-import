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
  @version: $Id: docbook2block.xslt,v 1.8 2009-01-30 21:30:22 michiel Exp $
  @since:   MMBase-1.9
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
    xmlns:fw="org.mmbase.framework.Functions"
    xmlns:o="http://www.mmbase.org/xmlns/objects"
    xmlns:mmxf="http://www.mmbase.org/xmlns/mmxf"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="node mmxf o fw"
    version="1.0" >

  <xsl:output method="xml"
              omit-xml-declaration="yes" /> <!-- xhtml is a form of xml -->

  <xsl:param name="request" />
  <xsl:param name="formatter_requestcontext" />

  <xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
  <xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>

  <xsl:template match="article">
    <div class="mm_docbook" >
      <h1><xsl:value-of select="articleinfo/title" /></h1>
      <xsl:for-each select="articleinfo/authorgroup/author">
        <div class="name">
          <span class="surname">
            <xsl:value-of select="surname" />
          </span>
          <span class="firstname">
            <xsl:text> </xsl:text>
            <xsl:value-of select="firstname" />
          </span>
        </div>
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
      <xsl:apply-templates select="*" />
    </div>
  </xsl:template>

  <xsl:template match="programlisting">
    <pre id="{@id}">
      <xsl:apply-templates  />
    </pre>
  </xsl:template>

  <xsl:template match="para">
    <p>
      <xsl:apply-templates  />
    </p>
  </xsl:template>

  <xsl:template match="itemizedlist">
    <ul>
      <xsl:apply-templates />
    </ul>
  </xsl:template>

  <xsl:template match="orderedlist">
    <ol>
      <xsl:apply-templates />
    </ol>
  </xsl:template>

  <xsl:template match="variablelist">
    <dl class="variable">
      <xsl:apply-templates />
    </dl>
  </xsl:template>

  <xsl:template match="varlistentry">
    <xsl:apply-templates select="*" />
  </xsl:template>

  <xsl:template match="term">
    <dt>
      <xsl:apply-templates  />
    </dt>
  </xsl:template>
  <xsl:template match="varlistentry/listitem">
    <dd>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="*" />
    </dd>
  </xsl:template>

  <xsl:template match="listitem">
    <li>
      <xsl:apply-templates />
    </li>
  </xsl:template>

  <xsl:template match="glosslist">
    <dl class="glossary">
      <xsl:apply-templates />
    </dl>
  </xsl:template>

  <xsl:template match="glosslist">
    <dl>
      <xsl:apply-templates>
        <xsl:sort select="translate(glossterm, $lowercase, $uppercase)" />
      </xsl:apply-templates>
    </dl>
  </xsl:template>


  <xsl:template match="glossentry">
    <di>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="*" />
    </di>
  </xsl:template>

  <xsl:template match="glossterm">
    <dt>
      <xsl:apply-templates  />
    </dt>
  </xsl:template>

  <xsl:template match="glossdef">
    <dd>
      <xsl:apply-templates/>
    </dd>
  </xsl:template>

  <xsl:template match="glossseealso">
    <p class="seealso">
      <xsl:text>See also </xsl:text>
      <xsl:choose>
        <xsl:when test="@otherterm">
          <a>
            <xsl:attribute name="href">#<xsl:value-of select="@otherterm" /></xsl:attribute>
            <xsl:for-each select="id(@otherterm)">
              <xsl:value-of select="glossterm" />
            </xsl:for-each>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates  />
        </xsl:otherwise>
      </xsl:choose>
    </p>
  </xsl:template>

  <xsl:template name="url">
    <xsl:param name="url" />
    <xsl:choose>
      <xsl:when test="starts-with($url, 'http:')">
        <xsl:value-of select="$url" />
      </xsl:when>
      <xsl:when test="starts-with($url, 'https:')">
        <xsl:value-of select="$url" />
      </xsl:when>
      <!-- relative -->
      <!-- only in xpath 2
      <xsl:when test="ends-with($url, '.html')">
      -->
      <xsl:when test="substring($url, string-length($url) - 4) = '.html'">
        <xsl:variable name="docbook">docbook=<xsl:value-of select="substring($url, 0, string-length($url) - 4)" />.xml</xsl:variable>
        <xsl:value-of select="$formatter_requestcontext" />
        <xsl:value-of select="fw:url($request, 'docbook', $docbook, '')" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$request" /><xsl:text>bla bla bla: </xsl:text><xsl:value-of select="$url" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="ulink">
    <a>
      <xsl:attribute name="href">
        <xsl:call-template name="url">
          <xsl:with-param name="url"><xsl:value-of select="@url" /></xsl:with-param>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:apply-templates />
    </a>
  </xsl:template>

  <xsl:template match="graphic">
    <img>
      <xsl:attribute name="src">
        <xsl:call-template name="url">
          <xsl:with-param name="url"><xsl:value-of select="@fileref" /></xsl:with-param>
        </xsl:call-template>
      </xsl:attribute>
    </img>
  </xsl:template>

</xsl:stylesheet>
