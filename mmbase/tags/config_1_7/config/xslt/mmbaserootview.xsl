<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- MMBASEROOT -->
<xsl:template match="MMBASEROOT">
<html>
<head><title>MMBASEROOT</title></head>
<body bgcolor="#FFFFFF">
<table border="2">
<tr>
  <td colspan="2"><font size="+1"><b>MMBaseRoot</b></font></td>
</tr>
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- DATABASE -->
<xsl:template match="DATABASE">
<tr>
  <td valign="top"><b>Database server:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- BASENAME -->
<xsl:template match="BASENAME">
<tr>
  <td valign="top"><b>Basename:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- MACHINENAME -->
<xsl:template match="MACHINENAME">
<tr>
  <td valign="top"><b>Machine name:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- HOST -->
<xsl:template match="HOST">
<tr>
  <td valign="top"><b>Host:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- LANGUAGE -->
<xsl:template match="LANGUAGE">
<tr>
  <td valign="top"><b>Language:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- MULTICASTHOST -->
<xsl:template match="MULTICASTHOST">
<tr>
  <td valign="top"><b>Multicast host:</b></td>
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


