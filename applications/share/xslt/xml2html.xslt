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
      <table width="100%" cellpadding="5">
        <tr>
          <td width="30"></td>
          <td>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td width="30"></td>
        </tr>
        <tr>
          <td></td>
          <td><xsl:value-of select="info"/></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <a name="toc"/>
            <xsl:apply-templates select="tag" mode="toc"/>
            <a href="#info">info about the syntax of this document</a>
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <xsl:apply-templates select="tag" mode="full" />
          </td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td></td>
        </tr>
         <tr>
          <td></td>
          <td>
            <a name="info"/>
            This document lists the current tags implemented for MMBase.<br/>
            Attributes in <font color="red">red</font> are required.<br/>
            If a tag definition contains a body section this means that the
            tag might do something with the content of the body.
          </td>
          <td></td>
        </tr>
      </table>
    </body>
  </html>
</xsl:template>

<xsl:template match="tag" mode="toc">
  <a href="#{generate-id()}"><xsl:value-of select="name" /></a>
  <xsl:if test="last()"> | </xsl:if>
</xsl:template>

<xsl:template match="tag" mode="full">
  <table bgcolor="#eeeeee" width="100%" cellpadding="5">
    <tr>
      <td colspan="2" bgcolor="white" align="right">
        <a name="{generate-id()}"/>
        <a href="#toc">toc</a>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <b>&lt;<xsl:value-of select="name"/>&gt;</b>
        <p>
        <xsl:value-of select="info"/>
        </p>
      </td>
    </tr>
    <xsl:if test="attribute">
      <tr>
        <td width="100" valign="top">attributes</td>
        <td>
          <ul><xsl:apply-templates select="attribute"/></ul>
        </td>
      </tr>
    </xsl:if>
    <xsl:if test="bodycontent">
      <tr>
        <td width="100" valign="top">body</td>
        <td>
          <xsl:value-of select="bodycontent"/>
          <br/>
          <xsl:apply-templates select="bodycontentinfo"/>
        </td>
      </tr>
    </xsl:if>       
    <xsl:apply-templates select="example" />
  </table>
</xsl:template>

<xsl:template match="example">
   <tr>
     <td width="100" valign="top">example</td>
     <td>
        <pre><xsl:value-of select="."/></pre>
     </td>
   </tr>
</xsl:template>

<xsl:template match="attribute">
  <li>
    <xsl:choose>
      <xsl:when test="requirednote">
        <xsl:if test="requirednote">
          <font color="ff9900"><xsl:apply-templates select="name"/></font>
          <font color="black" size="-1"> (<xsl:value-of select="requirednote"/>)</font>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="required='true'">
            <font color="red"><xsl:apply-templates select="name"/></font>
          </xsl:when>
          <xsl:otherwise>
            <font color="green"><xsl:apply-templates select="name"/></font>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
    <br/>
    <xsl:if test="info">
      <xsl:value-of select="info"/>
    </xsl:if>
    <xsl:if test="possiblevalue">
      <table bgcolor="#99ccff">
        <xsl:apply-templates select="possiblevalue"/>
      </table>
    </xsl:if>
    <xsl:if test="examplevalue">
      <table bgcolor="#99ffff">
        <xsl:apply-templates select="examplevalue"/>
      </table>
    </xsl:if>
  </li>
</xsl:template>

<xsl:template match="possiblevalue">
  <tr>
    <td valign="top"><b><xsl:value-of select="value"/></b></td>
    <td valign="top"><xsl:value-of select="info"/></td>
  </tr>
</xsl:template>

<xsl:template match="examplevalue">
  <tr>
    <td valign="top"><b><xsl:value-of select="value"/></b></td>
    <td valign="top"><xsl:value-of select="info"/></td>
  </tr>
</xsl:template>

</xsl:stylesheet>
