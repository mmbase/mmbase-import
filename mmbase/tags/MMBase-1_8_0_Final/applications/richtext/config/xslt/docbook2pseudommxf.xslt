<!--
  Converts docbook XML to 'pseudo' MMXF.
  
  Pseudo MMXF is MMXF in which the crosslinks remain unresolved. It must be programmaticly
  postprocessed to generated real MMBase cross-links (relation objects).

  This XSL is limited to features used in MMBase documentation.
    
  @author:  Michiel Meeuwissen
  @version: $Id: docbook2pseudommxf.xslt,v 1.1 2005-10-25 21:16:45 michiel Exp $
  @since:   MMBase-1.8
-->
<xsl:stylesheet  
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.mmbase.org/xmlns/mmxf"
    version="1.0" >

  <xsl:template match="article">
    <section>
      <h><xsl:value-of select="articleinfo/title" /></h>
      <xsl:apply-templates select="section" />
    </section>
  </xsl:template>

  <xsl:template match="section|appendix">
    <section>
      <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
      <h><xsl:value-of select="title" /></h>
      <xsl:apply-templates />
    </section>
  </xsl:template>

  <xsl:template match="para|simpara">
    <p>
      <xsl:apply-templates />
    </p>
  </xsl:template>

  <xsl:template match="formalpara">
    <section>
      <h>
        <xsl:apply-templates select="title" />
      </h>
      <p>
        <xsl:apply-templates select="para" />
      </p>
    </section>
  </xsl:template>
  <xsl:template match="listitem">
    <li>
      <xsl:apply-templates />
    </li>
  </xsl:template>
  <xsl:template match="itemizedlist|orderedlist">
    <ol>
      <xsl:apply-templates />
    </ol>
  </xsl:template>
  <xsl:template match="variablelist">
    <ul>
      <xsl:apply-templates />
    </ul>
  </xsl:template>
  <xsl:template match="varlistentry">
    <li>
      <em><xsl:apply-templates select="term/*" /></em>:
      <xsl:apply-templates select="listitem/*" />
    </li>
  </xsl:template>

  <xsl:template match="ulink">
    <a>
      <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
      <xsl:apply-templates />
    </a>
  </xsl:template>

  <xsl:template match="programlisting">
    <pre>
      <xsl:apply-templates />
    </pre>
  </xsl:template>

  <xsl:template match="emphasis">
    <em>
      <xsl:apply-templates />
    </em>
  </xsl:template>
  <xsl:template match="graphic">
    <img>
      <xsl:attribute name="src"><xsl:value-of select="@fileref" /></xsl:attribute>
      <xsl:apply-templates />
    </img>
  </xsl:template>
  <xsl:template match="anchor">
    <a>
      <xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
      <xsl:apply-templates />
    </a>
  </xsl:template>
  <xsl:template match="example">
    <div>
      <xsl:attribute name="class">example</xsl:attribute>
      <section>
        <h><xsl:value-of select="title" /></h>
        <xsl:apply-templates />
      </section>
    </div>
  </xsl:template>

  <xsl:template match="title">
  </xsl:template>

  <xsl:template match="*">
    <xsl:apply-templates />
  </xsl:template>

</xsl:stylesheet>
