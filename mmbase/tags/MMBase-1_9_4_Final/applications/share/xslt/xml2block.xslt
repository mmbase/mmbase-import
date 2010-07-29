<?xml version="1.0"?>
<xsl:stylesheet
    id="xml2block"
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

  <xsl:import href="xml2xhtml-base.xslt" />

  <xsl:output method="xml"
              omit-xml-declaration="yes" /> <!-- xhtml is a form of xml -->

  <xsl:template match="taglib">
    <div>
      <xsl:call-template name="main_body" />
    </div>
  </xsl:template>



</xsl:stylesheet>
