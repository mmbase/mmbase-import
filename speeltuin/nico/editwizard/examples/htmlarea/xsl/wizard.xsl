<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 16-12-2003
   Version: $Revision: 1.1 $
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/wizard.xsl"/>

  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator(); startHtmlArea();</xsl:variable>

  <xsl:variable name="htmlareadir"><xsl:value-of select="$referrerdir"/>htmlarea/</xsl:variable>

  <xsl:template name="stylehtml">
    <link rel="stylesheet" type="text/css" href="{$htmlareadir}htmlarea.css" />
  </xsl:template>

  <xsl:template name="javascript-html">

    <script type="text/javascript" src="{$htmlareadir}htmlarea.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript" src="{$htmlareadir}lang/en.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript" src="{$htmlareadir}dialog.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript" src="{$htmlareadir}my-htmlarea.js">
      <xsl:comment>help IE</xsl:comment>
    </script>

    <script type="text/javascript">
      <xsl:text disable-output-escaping="yes">
        <![CDATA[
        <!--
          // Store htmlarea names.
          var htmlAreas = new Array();
        ]]></xsl:text>

      <xsl:for-each select="//*[@ftype='html']">
        htmlAreas[htmlAreas.length] = '<xsl:value-of select="@fieldname"/>';
      </xsl:for-each>

      <xsl:text disable-output-escaping="yes">
        <![CDATA[

          function startHtmlArea() {
              // Start the htmlarea's.
              for (var i = 0; i < htmlAreas.length; i++) {
                var editor = new HTMLArea(htmlAreas[i]);
                customize(editor, "]]></xsl:text><xsl:value-of select="$htmlareadir"/><xsl:text disable-output-escaping="yes"><![CDATA[");
                editor.generate();
                htmlAreas[i] = editor;
              }
          }

          -->
        ]]></xsl:text>
    </script>
  </xsl:template>

</xsl:stylesheet>