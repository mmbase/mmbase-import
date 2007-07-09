<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">
  <!--
    wizard_xinha.xsl

	Use the Xinha editors

    @since  MMBase-1.8
    @author Nico Klasens

    This xsl uses Xalan functionality to call java classes
    to format dates and call functions on nodes
    See the xmlns attributes of the xsl:stylesheet
  -->

  <xsl:import href="ew:xsl/wizard.xsl"/>

  <xsl:variable name="htmlareadir">../xinha/</xsl:variable>

  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator(); xinha_init(); initPopCalendar();</xsl:variable>

  <xsl:template name="javascript-html">
    <script type="text/javascript">
      _editor_url = '<xsl:value-of select="$htmlareadir"/>';
      _editor_lang = '<xsl:value-of select="$language" />';
    </script>
    <script type="text/javascript" src="{$htmlareadir}htmlarea.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript" src="{$htmlareadir}my-htmlarea.js">
      <xsl:comment>help IE</xsl:comment>
    </script>

    <script type="text/javascript" src="{$htmlareadir}my-lang/{$language}.js">
      <xsl:comment>help IE</xsl:comment>
    </script>

    <script type="text/javascript">
      <xsl:text disable-output-escaping="yes">
        <![CDATA[
        <!--
          // Store htmlarea names.
          var xinha_editors = new Array();
        ]]></xsl:text>

      <xsl:for-each select="//wizard/form[@id=//wizard/curform]/descendant::*[@ftype=&apos;html&apos; and @maywrite!=&apos;false&apos;]">
        xinha_editors[xinha_editors.length] = '<xsl:value-of select="@fieldname"/>';
      </xsl:for-each>

      <xsl:text disable-output-escaping="yes">
        <![CDATA[

         // -->
        ]]></xsl:text>
    </script>
  </xsl:template>

</xsl:stylesheet>
