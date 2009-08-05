<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="navigation">
		<xsl:for-each select="description">
			<p>
				<h4>
					<xsl:value-of disable-output-escaping="yes" select="@title"/>
				</h4>
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</p>
		</xsl:for-each>
		<fieldset>
			<div class="spacer">
				<xsl:apply-templates select="navitem"/>
			</div>
		</fieldset>
	</xsl:template>
	<xsl:template match="navitem[not(@guitype) or @guitype='']">
		<span class="{@class}">
			<input name="button" type="image" src="{@imageurl}" alt="{@title}">
				<xsl:attribute name="onclick">setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
		</span>
	</xsl:template>
	<xsl:template match="navitem[@guitype='popup']">
		<script type="text/javascript">
		  function settarget() {
			 document.esform.target='_blank';
		  }
		  </script>
		<span class="{@class}">
			<input name="button" type="image" src="{@imageurl}" alt="{@title}">
				<xsl:attribute name="onclick">settarget();setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
		</span>
	</xsl:template>
</xsl:stylesheet>
