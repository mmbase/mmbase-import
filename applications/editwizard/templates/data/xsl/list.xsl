<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml"
                version="1.0"
                encoding="utf-8"
                omit-xml-declaration="yes"
                standalone="no"
                doctype-public="-//W3C//DTD HTML 4.0 Transitional//"
                indent="yes"
        />
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
			&gt;&gt;
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
<head><meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8" />
<title><xsl:value-of select="$title" /></title>
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
			-->
		]]>
		</xsl:text>
	</script>
</head>
<body bgcolor="#FFFFFF" 
      leftmargin="0" 
      topmargin="0" 
      marginwidth="0" 
      marginheight="0"
      onload="window.focus();">

<table cellspacing="0" cellpadding="3" border="0" width="615">
    <tr>
		<td valign="bottom" colspan="2" align="center">
			<table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tr>
					<td class="superhead" align="right"><nobr><xsl:value-of select="$title" /></nobr></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td><img width="1" src="media/n.gif" height="5" /></td>
	</tr>
	<tr>
				    <td width="124"></td>
		<td width="558" valign="top" colspan="2" class="listcanvas" align="left">
			<div title="These are the items that you can edit." class="subhead">
				<nobr><xsl:value-of select="$title" />(<xsl:value-of select="@count" /> items)</nobr>
			</div>
			<br />
			

			<table border="0" cellspacing="0" cellpadding="0">
			    <xsl:if test="object[@number&gt;0]">
				<tr>
					<xsl:if test="$deletable='true'"><td></td></xsl:if>
					<td class="tableheader">#</td>
					<xsl:for-each select="object[1]/field">
					<td class="tableheader"><xsl:value-of select="@name" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
					</td><td><img src="media/nix.gif" width="4" height="1" /></td>
					</xsl:for-each>
				</tr>
				</xsl:if>
				
				<xsl:for-each select="object[@number&gt;0]">
					<tr class="itemrow" onmouseover="objMouseOver(this);" onmousedown="objClick(this);" onmouseout="objMouseOut(this);" href="wizard.jsp?wizard={$wizard}&amp;objectnumber={@number}">
						<xsl:if test="$deletable='true'">
							<td class="deletebutton"><a href="deletelistitem.jsp?wizard={$wizard}&amp;objectnumber={@number}"><img src="media/remove.gif" border="0" width="20" height="20" title="{$deletedescription}" onmousedown="cancelClick=true;" onclick="return doDelete('{$deleteprompt}');" /></a><img src="media/nix.gif" width="10" height="1" /></td>
						</xsl:if>
						<td valign="top"><xsl:value-of select="@index" />.<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						</td>
						<xsl:apply-templates select="field" />
					</tr>
					<tr><td><img src="media/nix.gif" width="1" height="3" /></td></tr>
				</xsl:for-each>
			</table>
			<br />
			<div width="100%" align="right">
			<a href="wizard.jsp?wizard={$wizard}&amp;objectnumber=new" title="create new"><img src="media/new.gif" border="0" /></a>
			</div>

		</td>
	</tr>
	<tr>
		<td colspan="20">
			<div>
				<xsl:if test="count(/*/pages/page)>1"><xsl:apply-templates select="/*/pages" /><br /><br /></xsl:if>
			</div>
		</td>
	</tr>
			
	<tr class="itemrow" ><td colspan="2" align="center" ><a href="index.html">( index )</a> <a href="logout.jsp">( logout )</a></td></tr>		
					
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