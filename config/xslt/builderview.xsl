<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">


<!-- application -->
<xsl:template match="builder">
<html>
<head><title>Some builder or other, dunno, name is not in the XML file</title></head>
<body bgcolor="#FFFFFF">
<table border="2">
<tr>
  <td colspan="2"><b>Some builder or other, dunno, name is not in the XML file</b></td>
</tr>
<xsl:apply-templates/>
</table>
</body>
</html>
</xsl:template>

<!-- status -->
<xsl:template match="status">
<tr>
  <td valign="top"><b>Status:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>

<!-- classfile -->
<xsl:template match="classfile">
<tr>
  <td valign="top"><b>Classfile:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>


<!-- searchage -->
<xsl:template match="searchage">
<tr>
  <td valign="top"><b>Search age:</b></td>
  <td>
<xsl:value-of select="."/>
  </td>
</tr>
</xsl:template>


<!-- names -->
<xsl:template match="names">
<tr>
  <td valign="top"><b>Names:</b></td>
  <td>
    <table>
      <tr>
        <td>singular:</td>
        <td>plural:</td>
      </tr>
      <tr>
        <td><xsl:apply-templates select="singular"/></td>
        <td><xsl:apply-templates select="plural"/></td>
      </tr>
    </table>
  </td>
</tr>
</xsl:template>

<!-- singular -->
<xsl:template match="singular">
<i><xsl:value-of select="@xml:lang"/></i>=<b><xsl:value-of select="."/></b><br/> 
</xsl:template>

<!-- plural -->
<xsl:template match="plural">
<i><xsl:value-of select="@xml:lang"/></i>=<b><xsl:value-of select="."/></b><br/>
</xsl:template>


<!-- descriptions -->
<xsl:template match="descriptions">
<tr>
  <td valign="top"><b>Descriptions:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<!-- description -->
<xsl:template match="description">
<i><xsl:value-of select="@xml:lang"/></i>=<b><xsl:value-of select="."/></b><br/> 
</xsl:template>




<!-- fieldlist -->
<xsl:template match="fieldlist">
<tr>
  <td valign="top"><b>Fields:</b></td>
  <td>
<table width="100%" border="0">
<xsl:apply-templates/>
</table>
  </td>
</tr>
</xsl:template>

<!-- field -->
<xsl:template match="field">
<tr> 
  <td colspan="2"><font color="#CC0000"><xsl:value-of select="./db/name"/></font>
  </td>
</tr>
<xsl:apply-templates/>
<xsl:if test="not(position()=last()-1)">
<tr><td colspan="2"><hr size="2"/></td></tr>
</xsl:if>
</xsl:template>

<!-- gui -->
<xsl:template match="gui">
<tr>
  <td valign="top"><b>GUI:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<!-- gui/name or guiname -->
<xsl:template match="gui/name">
<i>name[<xsl:value-of select="@xml:lang"/>]</i>=<b><xsl:value-of select="."/></b>; 
</xsl:template>

<xsl:template match="guiname">
<i>name[<xsl:value-of select="@xml:lang"/>]</i>=<b><xsl:value-of select="."/></b>; 
</xsl:template>



<!-- gui/type or guitype-->
<xsl:template match="gui/type">
<br/>
<i>type</i>=<b><xsl:value-of select="."/></b> 
</xsl:template>

<xsl:template match="guitype">
<br/>
<i>type</i>=<b><xsl:value-of select="."/></b> 
</xsl:template>


<!-- editor -->
<xsl:template match="editor">
<tr>
  <td valign="top"><b>Editor:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<!-- positions -->
<xsl:template match="positions">
<xsl:apply-templates/>
</xsl:template>

<!-- input -->
<xsl:template match="input">
<xsl:choose>
<xsl:when test=".='-1'">
NO <i>input</i><br/>
</xsl:when>
<xsl:otherwise>
<i>input position=</i> <b><xsl:value-of select="."/></b><br/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<!-- list -->
<xsl:template match="list">
<xsl:choose>
<xsl:when test=".='-1'">
NO <i>list</i><br/>
</xsl:when>
<xsl:otherwise>
<i>list position=</i> <b><xsl:value-of select="."/></b><br/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<!-- search -->
<xsl:template match="search">
<xsl:choose>
<xsl:when test=".='-1'">
NO <i>search</i><br/>
</xsl:when>
<xsl:otherwise>
<i>search position=</i> <b><xsl:value-of select="."/></b><br/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<!-- db -->
<xsl:template match="db">
<tr>
  <td valign="top"><b>DB:</b></td>
  <td>
<xsl:apply-templates/>
  </td>
</tr>
</xsl:template>

<!-- name -->
<xsl:template match="db/name">
<i>name=</i> <b><xsl:value-of select="."/></b><br/>
</xsl:template>

<!-- type -->
<xsl:template match="db/type">
<i>type</i>=<b><xsl:value-of select="."/></b>; 
<i>state</i>=<b><xsl:value-of select="@state"/></b>; 
<i>notnull</i>=<b><xsl:value-of select="@notnull"/></b>
</xsl:template>

</xsl:stylesheet>


