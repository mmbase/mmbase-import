<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- linkchecker -->
<xsl:template match="linkchecker">
<html>
<head><title>Linkchecker</title></head>
<body bgcolor="#FFFFFF">
<table border="2">
<tr>
  <td colspan="2"><font size="+1"><b>Linkchecker</b></font></td>
</tr>
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- from -->
<xsl:template match="from">
<tr>
  <td valign="top"><b>From:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- to -->
<xsl:template match="to">
<tr>
  <td valign="top"><b>To:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

</xsl:stylesheet>

