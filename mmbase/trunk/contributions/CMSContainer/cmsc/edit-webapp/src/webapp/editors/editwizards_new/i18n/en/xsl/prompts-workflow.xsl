<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:variable name="REASON-WORKFLOW">This object is in the workflow and accepted. You do not have permissions to publish.</xsl:variable>
  <xsl:variable name="REASON-PUBLISH">This object is in publication. This object will be locked until it is published.</xsl:variable>

  <xsl:variable name="tooltip_finish">Store all changes for acceptance.</xsl:variable>
  <xsl:variable name="tooltip_no_finish">
    The changes cannot be finished since some data is not filled in correctly.
  </xsl:variable>

  <xsl:variable name="tooltip_accept">Accept all changes.</xsl:variable>
  <xsl:variable name="tooltip_no_accept">
    The changes cannot be accepted since some data is not filled in correctly.
  </xsl:variable>

  <xsl:variable name="tooltip_reject">Reject.</xsl:variable>
  <xsl:variable name="tooltip_no_reject">
    The changes cannot be rejected since some data is not filled in correctly.
  </xsl:variable>

  <xsl:variable name="tooltip_publish">Publish all changes.</xsl:variable>
  <xsl:variable name="tooltip_no_publish">
    The changes cannot be published since some data is not filled in correctly.
  </xsl:variable>

  <xsl:template name="prompt_finish">
    finish
  </xsl:template>

  <xsl:template name="prompt_accept">
    accept
  </xsl:template>

  <xsl:template name="prompt_reject">
    reject
  </xsl:template>
  
  <xsl:template name="prompt_publish">
    publish
  </xsl:template>
</xsl:stylesheet>
