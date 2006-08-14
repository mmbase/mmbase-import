<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">
  <!--
    wizard_tinymce.xsl

    To use the tinymce html editor, place it in the xsl directory of the application calling the wizard.

    You need to download tinymce, and unpack the entire directory in the wizard templates directory (this
    creates a tinymce directory).
    If you have the source, you can use the editwizard build script to download and extract tinymce ('ant tinymce').

    @author Pierre van Rooden
    @version $Id: wizard_tinymce.xsl,v 1.3 2006-08-14 07:54:35 pierre Exp $

    This xsl uses Xalan functionality to call java classes
    to format dates and call functions on nodes
    See the xmlns attributes of the xsl:stylesheet
  -->

  <xsl:import href="xsl/wizard.xsl"/>

  <!-- ================================================================================
    The following things can be overriden to customize the appearance of wizard
    ================================================================================ -->

  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator(); initPopCalendar();</xsl:variable>

  <xsl:template name="javascript-html">
    <script type="text/javascript" src="../tinymce/jscripts/tiny_mce/tiny_mce.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript">
          <xsl:text disable-output-escaping="yes">
            <![CDATA[
tinyMCE.init({
    mode: "textareas",
    editor_selector : "htmlarea",
    theme : "advanced",
    theme_advanced_toolbar_align : "left",
    theme_advanced_path_location : "bottom",
    theme_advanced_toolbar_location : "top",
    theme_advanced_buttons1 : "bold,italic,underline,strikethrough,separator,sub,sup,separator,bullist,numlist,separator,cut,copy,paste,undo,redo,separator,link,unlink,charmap,separator,code,cleanup,removeformat",
    theme_advanced_buttons2 : "",
    theme_advanced_buttons3 : ""
});
          ]]></xsl:text>
    </script>
  </xsl:template>

  <xsl:template name="stylehtml">
  </xsl:template>

</xsl:stylesheet>
