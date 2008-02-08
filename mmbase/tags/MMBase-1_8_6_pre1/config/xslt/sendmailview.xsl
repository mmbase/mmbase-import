<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- sendmail -->
<xsl:template match="sendmail">
<html>
<head><title>Sendmail</title></head>
<body bgcolor="#FFFFFF">
<table border="2">
<tr>
  <td colspan="2"><font size="+1"><b>Sendmail</b></font></td>
</tr>
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- mailhost -->
<xsl:template match="mailhost">
<tr>
  <td valign="top"><b>mailhost:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

</xsl:stylesheet>

