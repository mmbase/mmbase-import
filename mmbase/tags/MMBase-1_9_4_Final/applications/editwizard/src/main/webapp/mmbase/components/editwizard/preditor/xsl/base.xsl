<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">

  <xsl:import href="ew:xsl/base.xsl" /> <!-- extend from standard  editwizard xslt -->

  <xsl:param name="previewbase" />

  <xsl:variable name="mediadir"><xsl:value-of select="$templatedir"/>media/</xsl:variable>	
	
  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$templatedir}style/default.css" />
  </xsl:template>

  <xsl:template name="headcontent" >
		<table class="head" cellspacing="0" cellpadding="0">
			<tr class="headtitle">
				<xsl:call-template name="title" />
			</tr>
			<tr class="headsubtitle">
			</tr>
		</table>
  </xsl:template>

  <xsl:template name="bodycontent" >
    <table class="body" cellspacing="0" cellpadding="0">
      <xsl:call-template name="body" />
    </table><br />
		<div class="Path" onClick="self.scrollTo(0,0);">
			 <a href="#top" onClick="self.scrollTo(0,0); return false"><img src="{$mediadir}button_up.gif" class="Button" title="Omhoog" alt="Omhoog" /></a>
		</div>
	</xsl:template>
	
</xsl:stylesheet>