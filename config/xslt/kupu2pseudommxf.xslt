<!--
  Translates kupu-output to MMXF. This is only 'pseudo' MMXF because links to other objects (images, urls) need
  interaction with the cloud. This will happen afterwards in Java.

  It's quite hard because HTML as produced by browser editors is not 'nested' as desired by MMXF (which is like XHTML-2).

  The implementation idea is that all 'block' level tags (p, table, ul, ol, h1-h8) match their next sibling, and a parameter 'depth' is
  passed every time to keep track of the number of sections currently open.

  If no following sibling is available then all sections needs closing (the 'closeneeded' function).
  
  @author:  Michiel Meeuwissen
  @version: $Id: kupu2pseudommxf.xslt,v 1.4 2005-06-13 17:07:54 michiel Exp $
  @since:   MMBase-1.8
-->
<xsl:stylesheet  
  xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns="http://www.mmbase.org/xmlns/mmxf"
  version = "1.0"
  exclude-result-prefixes="html"
>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
  
  <xsl:template match="html:html">
    <xsl:apply-templates select="html:body" />
  </xsl:template>
  <xsl:template match="html:body">
    <mmxf version="1.1">
      <xsl:apply-templates  select="child::node()[1]" mode="siblings">
        <xsl:with-param name="depth" select="0" />
      </xsl:apply-templates>
    </mmxf>
  </xsl:template>

  <xsl:template match="html:link" mode="siblings"> <!-- help FF -->
    <xsl:param name="depth">0</xsl:param>
    <xsl:apply-templates select="following-sibling::node()[1]" mode="siblings">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="html:table|html:p" mode="siblings">
    <xsl:param name="depth">0</xsl:param>
    <xsl:element name="{name()}">
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:element>
    <xsl:apply-templates select="following-sibling::node()[1]" mode="siblings">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
    <xsl:apply-templates select="."  mode="closeifneeded">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
  </xsl:template>


  <xsl:template match="html:ol|html:ul" mode="siblings">
    <xsl:param name="depth">0</xsl:param>
    <p>
      <xsl:element name="{name()}">
        <xsl:copy-of select="@*" />
        <xsl:apply-templates />
      </xsl:element>
    </p>
    <xsl:apply-templates select="following-sibling::node()[1]" mode="siblings">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
    <xsl:apply-templates select="."  mode="closeifneeded">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
  </xsl:template>


  <xsl:template name="closesections">
    <xsl:param name="depth">0</xsl:param>
    <xsl:param name="to">0</xsl:param>
    <xsl:if test="$depth &gt; $to">
      <xsl:text disable-output-escaping="yes">&lt;/section&gt;</xsl:text>
    </xsl:if>
    <xsl:if test="$depth - 1 &gt; $to">
      <xsl:call-template name="closesections">
        <xsl:with-param name="depth" select="$depth - 1" />
        <xsl:with-param name="to" select="$to" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="opensections">
    <xsl:param name="depth">0</xsl:param>
    <xsl:param name="to">0</xsl:param>
    <xsl:if test="$depth &lt; $to">
      <xsl:text disable-output-escaping="yes">&lt;section&gt;</xsl:text>
    </xsl:if>
    <xsl:if test="$depth +1 &lt; $to">
      <h> </h><!-- nbsp to help IE only -->
      <xsl:call-template name="opensections">
        <xsl:with-param name="depth" select="$depth + 1" />
        <xsl:with-param name="to"    select="$to" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="html:*|text()" mode="closeifneeded">
    <xsl:param name="depth">0</xsl:param>
    <xsl:if  test="not(following-sibling::node())">
      <xsl:call-template name="closesections">
        <xsl:with-param name="depth" select="$depth" />
        <xsl:with-param name="to" select="0" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>


  <xsl:template match="text()" mode="siblings">
    <xsl:param name="depth">0</xsl:param>
    <xsl:if test="normalize-space(.) != ''">      
      <p><xsl:value-of select="." /></p>
    </xsl:if>
    <xsl:apply-templates select="following-sibling::node()[1]" mode="siblings">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
    <xsl:apply-templates select="."  mode="closeifneeded">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="html:h1|html:h2|html:h3|html:h4|html:h5|html:h6|html:h7|html:h8" mode="siblings">
    <xsl:param name="depth">0</xsl:param>
    <xsl:variable name="thisdepth" select="substring(name(), 2)" />
    <xsl:call-template name="closesections">
      <xsl:with-param name="depth" select="$depth" />
      <xsl:with-param name="to" select="$thisdepth - 1" />
    </xsl:call-template>
    <xsl:call-template name="opensections">
      <xsl:with-param name="depth" select="$depth" />
      <xsl:with-param name="to" select="$thisdepth" />
    </xsl:call-template>
    <h><xsl:value-of select="." /></h>
    <xsl:apply-templates select="following-sibling::node()[1]" mode="siblings">
      <xsl:with-param name="depth" select="$thisdepth" />
    </xsl:apply-templates>
    <xsl:apply-templates select="."  mode="closeifneeded">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="html:br" mode="siblings">
    <xsl:param name="depth">0</xsl:param>
    <xsl:apply-templates select="following-sibling::node()[1]" mode="siblings">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
    <xsl:apply-templates select="."  mode="closeifneeded">
      <xsl:with-param name="depth" select="$depth" />
    </xsl:apply-templates>
  </xsl:template>


  <xsl:template match="html:b|html:strong">
    <strong>
      <xsl:apply-templates />
    </strong>
  </xsl:template>
  <xsl:template match="html:i|html:em">
    <em>
      <xsl:apply-templates />
    </em>
  </xsl:template>

  <xsl:template match="html:*" >
    <xsl:element name="{name()}">
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>


</xsl:stylesheet>
