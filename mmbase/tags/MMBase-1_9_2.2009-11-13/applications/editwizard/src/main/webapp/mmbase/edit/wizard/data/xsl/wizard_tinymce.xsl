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
    @version $Id: wizard_tinymce.xsl,v 1.7 2007-03-29 12:31:56 pierre Exp $

    This xsl uses Xalan functionality to call java classes
    to format dates and call functions on nodes
    See the xmlns attributes of the xsl:stylesheet
  -->

  <xsl:import href="xsl/wizard.clean.xsl"/>

  <!-- ================================================================================
    The following things can be overriden to customize the appearance of wizard
    ================================================================================ -->

  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator();</xsl:variable>

  <xsl:template name="javascript-html">
    <script type="text/javascript" src="../tinymce/jscripts/tiny_mce/tiny_mce.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript">
          <xsl:text disable-output-escaping="yes">
            <![CDATA[
function mmbaseOnChangeHandler(inst) {
    // preferably you would use:
    //    inst.triggerSave();
    //  but unfortunately that rests the cursor position in the editor too
    if (inst.formElement)
      inst.formElement.value = inst.getBody().innerHTML;
    validator.validate(inst.formElement);
}

tinyMCE.init({
    mode: "textareas",
    editor_selector : "htmlarea",
    theme : "advanced",
    plugins : "table",
    theme_advanced_toolbar_align : "left",
    theme_advanced_path_location : "bottom",
    theme_advanced_toolbar_location : "top",
    theme_advanced_buttons1 : "bold,italic,underline,strikethrough,separator,sub,sup,separator,bullist,numlist,separator,link,unlink,charmap,separator,code,separator,tablecontrols",
    theme_advanced_buttons2 : "",
    theme_advanced_buttons3 : "",
    onchange_callback : "mmbaseOnChangeHandler"

});
          ]]></xsl:text>
    </script>
  </xsl:template>

  <xsl:template name="stylehtml">
  </xsl:template>

  <!-- turn off datepicker (doesn't work properly) -->

  <xsl:template name="javascript-date-picker">
  </xsl:template>

  <xsl:template name="date-picker">
  </xsl:template>

  <!-- show field description as help text -->
  <xsl:template name="fieldintro">
     <xsl:if test="description and @ftype!=&apos;data&apos; and @ftype!=&apos;enumdata&apos;">
       <div class="fieldintro"><xsl:call-template name="i18n">
         <xsl:with-param name="nodes" select="description" />
       </xsl:call-template></div>
     </xsl:if>
  </xsl:template>

</xsl:stylesheet>
