<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:preserve-space elements="application"/>

<!-- application -->
<xsl:template match="application">
<html>
<head><title><xsl:value-of select="@name"/></title></head>
<body bgcolor="#FFFFFF">
<table border="2">
  <tr>
    <td><b>Name:</b></td>
    <td><font size="+1"><b><xsl:value-of select="@name"/></b></font></td>
  </tr>
  <tr>
    <td><b>Maintainer:</b></td>
    <td>
<xsl:choose>
<xsl:when test="string-length(@maintainer)=0">
-
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="@maintainer"/>
</xsl:otherwise>
</xsl:choose>
    </td>
  </tr>
  <tr>
    <td><b>Version:</b></td>
    <td>
<xsl:choose>
<xsl:when test="string-length(@version)=0">
-
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="@version"/>
</xsl:otherwise>
</xsl:choose>
    </td>
  </tr>
  <tr>
    <td><b>Auto-deploy:</b></td>
    <td>
<xsl:choose>
<xsl:when test="string-length(@auto-deploy)=0">
false
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="@auto-deploy"/>
</xsl:otherwise>
</xsl:choose>
    </td>
  </tr>
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- builders -->
<xsl:template match="neededbuilderlist">
<tr>
  <td valign="top"><b>Needed builders:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<xsl:template match="builder">
- <b><xsl:value-of select="."/></b> (<i>maintainer</i>=<xsl:value-of select="@maintainer"/>;<i>version</i>=<xsl:value-of select="@version"/>)<br/>
</xsl:template>

<!-- reldefs -->
<xsl:template match="neededreldeflist">
<tr>
  <td valign="top"><b>Needed reldefs:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<xsl:template match="reldef">
- 
<i>source</i>=<b><xsl:value-of select="@source"/></b>; 
<i>target</i>=<b><xsl:value-of select="@target"/></b>; 
<i>direction</i>=<b><xsl:value-of select="@direction"/></b>; 
<i>guiSourceName</i>=<b><xsl:value-of select="@guisourcename"/></b>; 
<i>guiTargetName</i>=<b><xsl:value-of select="@guitargetname"/></b>
<br/>
</xsl:template>


<!-- allowed relations -->
<xsl:template match="allowedrelationlist">
<tr>
  <td valign="top"><b>Allowed relations:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<xsl:template match="relation">
- 
<i>from</i>=<b><xsl:value-of select="@from"/></b>;
<i>to</i>=<b><xsl:value-of select="@to"/></b>; 
<i>type</i>=<b><xsl:value-of select="@type"/></b>
<br/>
</xsl:template>

<!-- datasource list -->
<xsl:template match="datasourcelist">
<tr>
  <td valign="top"><b>Data sources:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<xsl:template match="datasource">
- <i>path</i>=<b><xsl:value-of select="@path"/></b><br/>
</xsl:template>


<!-- datasource list -->
<xsl:template match="relationsourcelist">
<tr>
  <td valign="top"><b>Relation sources:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<xsl:template match="relationsource">
- <i>path</i>=<b><xsl:value-of select="@path"/></b><br/>
</xsl:template>

</xsl:stylesheet>


