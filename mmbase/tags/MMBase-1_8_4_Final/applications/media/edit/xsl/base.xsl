<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!--  Stream manager -->
  <xsl:import href="ew:xsl/base.xsl" /> <!-- extend from standard  editwizard xslt -->

  <xsl:variable name="mediadir"><xsl:value-of select="$referrerdir" />media/</xsl:variable>
  

  <xsl:param name="SEARCH_LIST_TYPE">IFRAME</xsl:param>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$referrerdir}style/streammanager.css" ></link>
  </xsl:template>

   <xsl:template name="extrajavascript">
     <script language="javascript" src="{$referrerdir}style/streammanager.js.jsp?dir={$referrerdir}&amp;fragment={/wizard/form/list[@searchdir='source']/item[@role='posrel']/@source}&amp;language={$language}"><xsl:comment>help IE</xsl:comment></script>
  </xsl:template>

   
</xsl:stylesheet>