<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- jdbc -->
<xsl:template match="jdbc">
<html>
<head><title>MMBASEROOT</title></head>
<body bgcolor="#FFFFFF">
<table border="2">
<tr>
  <td colspan="2"><font size="+1"><b>JDBC</b></font></td>
</tr>
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- url -->
<xsl:template match="url">
<tr>
  <td valign="top"><b>URL:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- driver -->
<xsl:template match="driver">
<tr>
  <td valign="top"><b>Driver:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- url -->
<xsl:template match="url">
<tr>
  <td valign="top"><b>URL:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- database -->
<xsl:template match="database">
<tr>
  <td valign="top"><b>Database:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- user -->
<xsl:template match="user">
<tr>
  <td valign="top"><b>User:</b></td>
  <td>
<xsl:choose>
<xsl:when test=".='url'">
Using URL for connecting (set value is 'url')
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="."/>
</xsl:otherwise>
</xsl:choose>
  </td>
</tr>
</xsl:template>

<!-- password -->
<xsl:template match="password">
<tr>
  <td valign="top"><b>Password:</b></td>
  <td>
<xsl:choose>
<xsl:when test=".='url'">
Using URL for connecting (set value if 'url')
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="."/>
</xsl:otherwise>
</xsl:choose>
  </td>
</tr>
</xsl:template>

<!-- host -->
<xsl:template match="host">
<tr>
  <td valign="top"><b>Database host:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- port -->
<xsl:template match="port">
<tr>
  <td valign="top"><b>Port:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- connections -->
<xsl:template match="connections">
<tr>
  <td valign="top"><b>Connections:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- querys -->
<xsl:template match="querys">
<tr>
  <td valign="top"><b>Queries:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- supportclass -->
<xsl:template match="supportclass">
<tr>
  <td valign="top"><b>Supportclass:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- MULTICASTPORT -->
<xsl:template match="MULTICASTPORT">
<tr>
  <td valign="top"><b>Multicast port:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- DTDBASE -->
<xsl:template match="DTDBASE">
<tr>
 <td valign="top"><b>DTD base:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- AUTHTYPE -->
<xsl:template match="AUTHTYPE">
<tr>
 <td valign="top"><b>Authentication type:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

</xsl:stylesheet>


