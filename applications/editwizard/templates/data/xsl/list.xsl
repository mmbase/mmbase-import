<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="xml" 
    omit-xml-declaration="no" 
    doctype-public="-//W3C/DTD HTML 4.0 Transitional//EN"
    version="1.0" encoding="utf-8" indent="no" />

<xsl:param name="wizard"></xsl:param>
<xsl:param name="start">1</xsl:param>
<xsl:param name="len">15</xsl:param>
<xsl:param name="url">list.jsp</xsl:param>
<xsl:param name="homeurl">index.html</xsl:param>
<xsl:param name="title"><xsl:value-of select="list/object/@type" /></xsl:param>
<xsl:param name="deletable">false</xsl:param>
<xsl:param name="creatable">true</xsl:param>
<xsl:param name="deleteprompt">Are you sure you want to delete this item?</xsl:param>
<xsl:param name="deletedescription">Delete this item</xsl:param>


<xsl:template match="pages">
	<span class="pagenav">
	<xsl:choose>
		<xsl:when test="page[@previous='true']">
			<a class="pagenav" href="{$url}&amp;start={page[@previous='true']/@start}">&lt;&lt;</a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			&lt;&lt;<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>

	<xsl:apply-templates select="page" />
	
	<xsl:choose>
		<xsl:when test="page[@next='true']">
			<a class="pagenav" href="{$url}&amp;start={page[@next='true']/@start}">&gt;&gt;</a>
		</xsl:when>
		<xsl:otherwise>
			&gt;&gt;<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
	</span>
</xsl:template>

<xsl:template match="page">
	<a class="pagenav" href="{$url}&amp;start={@start}"><xsl:value-of select="position()" /></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
</xsl:template>


<xsl:template match="page[@current='true']">
	<xsl:value-of select="position()" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
</xsl:template>



<xsl:template match="list">

<html>
<head>
<title>Editwizards <xsl:value-of select="$title" /></title>
	<link rel="stylesheet" type="text/css" href="style.css" />
		<script language="javascript">
		<xsl:text disable-output-escaping="yes">
		<![CDATA[
		  <!--
			var cancelClick = false;
			
			function objMouseOver(el) {
				el.className="itemrow-hover";
			}
			
			function objMouseOut(el) {
				el.className="itemrow";
			}
			
			function objClick(el) {
				if (cancelClick) {
					cancelClick=false;
					return;
				}
				var href = el.getAttribute("href")+"";
				if (href.length<10) return;
				document.location=href;
			}
			
			function doDelete(prompt) {
				var conf;
				if (prompt && prompt!="") {
					conf = confirm(prompt);
				} else conf=true;
				cancelClick=true;
				return conf;
			}
			// -->
		]]>
		</xsl:text>
	</script>
</head>
<body link="#008374" vlink="#33CC66" text="#000000" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0">
<table width="702" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table cellspacing="0" cellpadding="3" border="0" width="662">
        <tr class="itemrow" >
          <td width="40"></td>
          <td bgcolor="#F4E6DA" width="653" >
<table width="100%">
<tr>
<td align="center" width="132"><a href="index.html"><span class="step">Terug</span></a></td> 
<td align="center" width="132"><img src="media/nix.gif" width="5" height="1" border="0" /></td> 
<td align="center" width="132"><img src="media/nix.gif" width="5" height="1" border="0" /></td> 
<td align="center" width="132"><img src="media/nix.gif" width="5" height="1" border="0" /></td> 
<td align="center" width="132"><a href="logout.jsp"><span class="step">Uitloggen</span></a> </td>
</tr>
</table>
</td>
        </tr> <tr> 
          <td width="75" rowspan="5" valign="top"><img src="media/nix.gif" border="0" width="63" /></td>
          <td valign="bottom" colspan="2" align="center" bgcolor="#F4E6DA"> 
 <table width="547" cellspacing="0" cellpadding="0" border="0" bgcolor="#F4E6DA">
    <tr>
					<td class="superhead" align="right"><nobr><xsl:value-of select="$title" /></nobr>
<div width="100%" align="left">
			<a href="wizard.jsp?wizard={$wizard}&amp;objectnumber=new" title="create new"><img src="media/new.gif" border="0" hspace="5"/><font color="#000000">voeg toe</font></a>
			</div></td>
</tr>
            </table>
       


 </td>
        </tr>
        <tr> 
          <td bgcolor="#F4E6DA"><img width="1" src="media/n.gif" height="5" /></td>
        </tr>
        <tr> 
          <td valign="top" colspan="2" class="listcanvas" align="left" bgcolor="#FFFFFF"> 
            <div title="These are the items that you can edit." class="subhead"> 
              <nobr><xsl:value-of select="$title" />(<xsl:value-of select="@count" /> 
              items)</nobr> </div>
            <br /><div> <xsl:if test="count(/*/pages/page)>1"><xsl:apply-templates select="/*/pages" /><br />
              <br />
              </xsl:if> </div>

            <table border="0" cellspacing="0" cellpadding="0" bgcolor="#F4E6DA">

              <xsl:if test="object[@number&gt;0]"> 
              <tr> <xsl:if test="$deletable='true'">
                <td></td>
                </xsl:if> 
                <td class="tableheader">#</td>
                <xsl:for-each select="object[1]/field"> 
                <td class="tableheader"><xsl:value-of select="@name" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> 
                </td>
                <td><img src="media/nix.gif" width="4" height="1" /></td>
                </xsl:for-each> </tr>
              </xsl:if>

              <xsl:for-each select="object[@number&gt;0]"> 
              <tr class="itemrow" onMouseOver="objMouseOver(this);" onMouseDown="objClick(this);" onMouseOut="objMouseOut(this);" href="wizard.jsp?wizard={$wizard}&amp;objectnumber={@number}"> 
                <xsl:if test="$deletable='true'"> 
                <td class="deletebutton"><a href="deletelistitem.jsp?wizard={$wizard}&amp;objectnumber={@number}"><img src="media/remove.gif" border="0" width="20" height="20" title="{$deletedescription}" onMouseDown="cancelClick=true;" onClick="return doDelete('{$deleteprompt}');" /></a><img src="media/nix.gif" width="10" height="1" /></td>
                </xsl:if> 
                <td valign="top"><xsl:value-of select="@index" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> 
                </td>
                <xsl:apply-templates select="field" /> </tr>
              <tr>
                <td><img src="media/nix.gif" width="1" height="3" /></td>
              </tr>
              </xsl:for-each> 

            </table>
            <br />
                
          </td>
        </tr>
        <tr> 
          <td colspan="100" bgcolor="#F4E6DA"> 
            <div> <xsl:if test="count(/*/pages/page)>1"><xsl:apply-templates select="/*/pages" /><br />
              <br />
              </xsl:if> </div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template match="field">
	<xsl:if test="position()>1"><td valign="top"><nobr><xsl:value-of select="." /></nobr></td></xsl:if>
	<xsl:if test="position()=1"><td valign="top" width="99%"><xsl:value-of select="." /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></xsl:if>
	<td><img src="media/nix.gif" width="4" height="1" /></td>
	</xsl:template>
</xsl:stylesheet>
