<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<!-- database -->
<xsl:template match="database">
<html>
<head><title><xsl:value-of select="./name"/></title></head>
<body bgcolor="#FFFFFF">
<table border="2">
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- name -->
<xsl:template match="name">
<tr>
  <td valign="top"><b>Name:</b></td>
  <td>
<font size="+1"><b><xsl:value-of select="."/></b></font>
  </td>
</tr>
</xsl:template>

<!-- mmbasedriver -->
<xsl:template match="mmbasedriver">
<tr>
  <td valign="top"><b>MMBase Driver:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>


<!-- scheme -->
<xsl:template match="scheme">
<tr>
  <td valign="top"><b>Scheme:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<!-- create -->
<xsl:template match="create">
<i>CREATE</i>=<b><xsl:value-of select="."/></b><br/> 
</xsl:template>

<!-- not-null -->
<xsl:template match="not-null">
<i>NOT NULL</i>=<b><xsl:value-of select="."/></b><br/> 
</xsl:template>

<!-- null -->
<xsl:template match="null">
<i>NULL</i>=<b><xsl:value-of select="."/></b><br/> 
</xsl:template>

<!-- primary-key -->
<xsl:template match="primary-key">
<i>PRIMARY KEY</i>=<b><xsl:value-of select="."/></b><br/> 
</xsl:template>


<!-- mapping -->
<xsl:template match="mapping">
<tr>
  <td valign="top"><b>Mapping:</b></td>
  <td>
<table width="100%" border="1">
<tr>
  <td><b>mmbase type</b></td>
  <td><b>min size</b></td>
  <td><b>max size</b></td>
  <td><b>database type</b></td>
</tr>
<xsl:apply-templates/>
</table>
  </td>
</tr>
</xsl:template>

<!-- type-mapping -->
<xsl:template match="type-mapping">
<tr>
  <td><xsl:value-of select="@mmbase-type"/></td>
  <td align="right">
<xsl:choose>
<xsl:when test="string-length(@min-size)=0">
-
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="@min-size"/>
</xsl:otherwise>
</xsl:choose>
</td>
  <td align="right">
<xsl:choose>
<xsl:when test="string-length(@max-size)=0">
-
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="@max-size"/>
</xsl:otherwise>
</xsl:choose>
  </td>
  <td><xsl:value-of select="."/></td>
</tr>
</xsl:template>

<!-- disallowed -->
<xsl:template match="disallowed">
<tr>
  <td valign="top"><b>Renaming:</b></td>
  <td>
<table width="100%" border="1">
<tr>
  <td><b>fieldname</b></td>
  <td><b>rename as</b></td>
</tr>
<xsl:apply-templates/>
</table>
  </td>
</tr>
</xsl:template>

<!-- field -->
<xsl:template match="field">
<tr>
  <td><xsl:value-of select="@name"/></td>
  <td><xsl:value-of select="@replacement"/></td>
</tr>
</xsl:template>

</xsl:stylesheet>


