<?xml version="1.0"?>
<xsl:stylesheet id="xml2html"
  version="1.0"
  xmlns:dt="http://www.mmbase.org/xmlns/datatypes"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="org.apache.xalan.xslt.extensions.Redirect"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  extension-element-prefixes="xalan"
  >

  <xsl:output method="xml"
    version="1.0"
    omit-xml-declaration="yes"
    indent="no"
    />

  <xsl:template match="/dt:datatypes">
    <section id="datatypes">
      <title>MMBase default datatypes</title>
      <xsl:apply-templates select="dt:datatype" />
    </section>
  </xsl:template>

  <xsl:template match="dt:datatype">
    <section id="{@id}">
      <title><xsl:value-of select="dt:description[@xml:lang = 'en']" /></title>
      <para>Class: <xsl:value-of select="dt:class" /></para>
      <variablelist>
        <xsl:apply-templates select="dt:datatype|dt:specialization" mode="sub" />
      </variablelist>
    </section>
  </xsl:template>

  <xsl:template match="dt:datatype|dt:specialization" mode="sub">
      <varlistentry>
        <term><xsl:value-of select="@id" /></term>
        <listitem>
          <para><xsl:value-of select="dt:description[@xml:lang = 'en']" /></para>
          <xsl:if test="dt:class">
            <para>Class: <xsl:value-of select="dt:class" /></para>
          </xsl:if>
          <xsl:if test="dt:default">
            <para>Default: <xsl:value-of select="dt:default/@value" /></para>
          </xsl:if>
          <xsl:if test="dt:minInclusive">
            <para>Minimal value (inclusive): <xsl:value-of select="dt:minInclusive/@value" /></para>
          </xsl:if>
          <xsl:if test="dt:minExclusive">
            <para>Minimal value (exclusive): <xsl:value-of select="dt:minExclusive/@value" /></para>
          </xsl:if>
          <xsl:if test="dt:maxInclusive">
            <para>Maximal value (inclusive): <xsl:value-of select="dt:maxInclusive/@value" /></para>
          </xsl:if>
          <xsl:if test="dt:maxExclusive">
            <para>Maximal value (exclusive): <xsl:value-of select="dt:maxExclusive/@value" /></para>
          </xsl:if>
          <xsl:if test="dt:password/@value = 'true'">
            <para>This type is designated for passwords</para>
          </xsl:if>
          <xsl:if test="dt:pattern">
            <para>
              Patterns are assigned. 
              Example pattern (for english): 
              <xsl:value-of select="dt:pattern[@xml:lang = 'en']" />
            </para>
          </xsl:if>
          <xsl:if test="dt:enumeration">
            <para>
              This is an enumeration.
            </para>
          </xsl:if>
        </listitem>
      </varlistentry>
      <xsl:apply-templates select="dt:datatype|dt:specialization" mode="sub" />
  </xsl:template>
</xsl:stylesheet>

