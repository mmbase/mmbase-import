<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- scancache -->
<xsl:template match="scancache">
<html>
<head><title>Scancache</title></head>
<body bgcolor="#FFFFFF">
<table border="2">
<tr>
  <td colspan="2"><font size="+1"><b>Scancache</b></font></td>
</tr>
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- status -->
<xsl:template match="status">
<tr>
  <td valign="top"><b>status:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- CacheRoot -->
<xsl:template match="CacheRoot">
<tr>
  <td valign="top"><b>Cache root:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

</xsl:stylesheet>

