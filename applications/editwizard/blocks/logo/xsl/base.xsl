<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/base.xsl"/>

  <xsl:template name="headcontent" >
    <table class="head">
      <tr class="headtitle">
        <td rowspan="2">
          <!--
          <img src="{$referrerdir}my_logo.gif"/>
          -->
          <img src="{$ew_context}/mmbase/components/editwizard/logo/my_logo.gif"/>
        </td>
        <xsl:call-template name="title" />
      </tr>
      <tr class="headsubtitle">
        <xsl:call-template name="subtitle" />
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
