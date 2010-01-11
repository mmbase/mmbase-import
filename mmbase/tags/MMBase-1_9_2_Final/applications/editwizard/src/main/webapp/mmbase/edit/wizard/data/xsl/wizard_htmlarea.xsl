<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

  <xsl:import href="xsl/wizard.clean.xsl"/>

  <xsl:variable name="htmlareadir">../htmlarea/</xsl:variable>
  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator(); startHtmlArea(); initPopCalendar();</xsl:variable>

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
          var htmlAreas = new Array();
        ]]></xsl:text>
      <xsl:for-each select="//wizard/form[@id=//wizard/curform]/descendant::*[@ftype='html' and @maywrite!='false']">
        htmlAreas[htmlAreas.length] = '<xsl:value-of select="@fieldname"/>';
      </xsl:for-each>
      <xsl:text disable-output-escaping="yes">
        <![CDATA[

          function startHtmlArea() {
            if (HTMLArea.checkSupportedBrowser()) {
              // Start the htmlareas.
              for (var i = 0; i < htmlAreas.length; i++) {
                var editor = new HTMLArea(htmlAreas[i]);
                customize(editor, "]]></xsl:text><xsl:value-of select="$htmlareadir"/><xsl:text disable-output-escaping="yes"><![CDATA[");
                editor.generate();
                htmlAreas[i] = editor;
                setTimeout(function() {
                   HTMLArea._addEvent(editor._doc, "blur", function() {
                     editor._textArea.value = editor.getHTML();
                     validator.validate(editor._textArea);
                   });
                   },
                   1000);
              }
            }
          }

          -->
        ]]></xsl:text>
    </script>
  </xsl:template>

  <xsl:template name="stylehtml" >
    <link rel="stylesheet" type="text/css" href="{$htmlareadir}htmlarea.css"/>
  </xsl:template>

</xsl:stylesheet>
