<?xml version="1.0"?>
<xsl:stylesheet id="xml2html" 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- main entry point -->
<xsl:template match="taglib">
  <html>
    <head>
      <title>MMBase taglib documentation</title>
      <xsl:if test="@author"><meta name="Author" value="{@author}"/></xsl:if>
    </head>
    <body marginwidth="0" marginheight="0" leftmargin="0" rightmargin="0" topmargin="0" bgcolor="#FFFFFF" text="#336699" link="#336699" vlink="#336699" alink="#336699">
      <p><xsl:value-of select="info"/></p>
      <table>
        <tr valign="top">
          <td colspan="2">
            <a name="toc"/>
            <xsl:apply-templates select="tag" mode="toc"/>
            <a href="#info">info about the syntax of this document</a>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
        </tr>
        <xsl:apply-templates select="tag" mode="full" />
      </table>
      <p>
        <a name="info"/>
        This document lists the current tags implemented for MMBase.<br/>
        Attributes in <font color="red">red</font> are required.<br/>
        If a tag definition contains a body section this means that the tag might do something with the content of the body.
      </p>
    </body>
  </html>
</xsl:template>

<xsl:template match="tag" mode="toc">
  <a href="#{generate-id()}"><xsl:value-of select="name" /></a>
  <xsl:if test="last()"> | </xsl:if>
</xsl:template>

<xsl:template match="tag" mode="full">
  <tr>
    <td class="name" valign="top">
      <a name="{generate-id()}"/>
      <b><xsl:value-of select="name"/></b>
      <br/>
      <a href="#toc">toc</a>
    </td>
    <td valign="top">
      <table bgcolor="#eeeeee">
        <tr>
          <td valign="top">info</td>
          <td><xsl:apply-templates select="info"/></td>
        </tr>
        <xsl:if test="attribute">
          <tr>
            <td valign="top">attributes</td>
            <td>
              <ul><xsl:apply-templates select="attribute"/></ul>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="bodycontent">
          <tr>
            <td valign="top">body</td>
            <td>
              <xsl:value-of select="bodycontent"/>
              <br/>
              <xsl:apply-templates select="bodycontentinfo"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="example">
          <tr>
            <td valign="top">example</td>
            <td>
<pre><xsl:apply-templates select="example"/></pre>
            </td>
          </tr>
        </xsl:if>
      </table>
    </td>
  </tr>
</xsl:template>

<xsl:template match="attribute">
  <li>
    <xsl:choose>
      <xsl:when test="required='true'">
        <font color="red"><xsl:apply-templates select="name"/></font>
      </xsl:when>
      <xsl:otherwise>
        <font color="green"><xsl:apply-templates select="name"/></font>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="info">
      <br/>
      <xsl:apply-templates select="info"/>
    </xsl:if>
  </li>
</xsl:template>

</xsl:stylesheet>
