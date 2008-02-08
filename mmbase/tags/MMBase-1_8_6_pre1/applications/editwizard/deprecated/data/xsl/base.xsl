<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform">
  <!--
  Basic parameters and settings for all xsl's of the editwizards.
       
  @since  MMBase-1.6
  @author Michiel Meeuwissen
  @version $Id: base.xsl,v 1.2 2004-05-24 13:13:23 michiel Exp $
       -->
  <xsl:import href="xsl/prompts.xsl" />

  <xsl:output
    method="xml"
    version="1.0"
    encoding="utf-8"
    omit-xml-declaration="no"
    standalone="yes"
    doctype-public="-//W3C//DTD XHTML 1.0 Transitional//"
    indent="no"
    />

  <xsl:template name="prompt_edit_wizard"><xsl:value-of select="$tooltip_edit_wizard" /></xsl:template>
  <xsl:template name="prompt_add_wizard"><xsl:value-of select="$tooltip_add_wizard" /></xsl:template>
  

  <xsl:param name="ew_context"></xsl:param><!-- The web-application's context -->
  <xsl:param name="ew_path"></xsl:param><!-- The directory in which the editwizards are installed (relative to context root) -->

  <xsl:param name="cloud" />

  <xsl:variable name="rootew_path"><xsl:value-of select="$ew_context" /><xsl:value-of select="$ew_path" /></xsl:variable>
  
  <xsl:param name="username">(unknown)</xsl:param>
  <xsl:param name="language">en</xsl:param>   
  <xsl:param name="sessionid"></xsl:param>
  <xsl:param name="popupid"></xsl:param>

  <xsl:param name="referrer"></xsl:param><!-- name of the file that called list.jsp or default.jsp, can be used for back-buttons (relative to context-root) --> 
  
  <xsl:variable name="rootreferrer"><xsl:value-of select="$ew_context" /><xsl:value-of select="$referrer" /></xsl:variable><!-- relative to root -->


  <xsl:variable name="referrerdir"><xsl:call-template name="getdirpart"><xsl:with-param name="dir" select="$rootreferrer" /></xsl:call-template></xsl:variable><!-- the directory of that file, needed to refer to resources there (when you override), like e.g. images -->


  <!-- Perhaps you want to refer to stuff not relative to the referrer-page, but to the root of the site where it belongs to. 
       This must be given to the jsp's then with the paremeter 'templates' 
       -->
  <xsl:variable name="templatedir"><xsl:value-of select="$referrerdir" /></xsl:variable>

  <xsl:param name="debug">false</xsl:param>

  <xsl:param name="sessionkey">editwizard</xsl:param>
  <xsl:param name="cloudkey">cloud_mmbase</xsl:param><!-- name of variable in session in which is the cloud -->

  <xsl:param name="wizardparams"><xsl:value-of select="$sessionid" />?proceed=true&amp;sessionkey=<xsl:value-of select="$sessionkey" />&amp;language=<xsl:value-of select="$language" />&amp;debug=<xsl:value-of select="$debug" /></xsl:param>

  <xsl:variable name="listpage">list.jsp<xsl:value-of select="$wizardparams" />&amp;popupid=<xsl:value-of select="$popupid" /></xsl:variable>
  <xsl:variable name="wizardpage">wizard.jsp<xsl:value-of select="$wizardparams" />&amp;popupid=<xsl:value-of select="$popupid" /></xsl:variable>
  
  <xsl:variable name="formwizardpage">wizard.jsp<xsl:value-of select="$sessionid" /></xsl:variable>
  <xsl:template name="formwizardargs">
    <input type="hidden" name="proceed"    value="true" />
    <input type="hidden" name="sessionkey" value="{$sessionkey}" />
    <input type="hidden" name="language"   value="{$language}"   />
    <input type="hidden" name="debug"      value="{$debug}"      />
    <input type="hidden" name="popupid"    value="{$popupid}"    />
  </xsl:template>

  <xsl:variable name="popuppage">wizard.jsp<xsl:value-of select="$wizardparams" /></xsl:variable>

  <!--xsl:variable name="popuppage">wizard.jsp<xsl:value-of select="$sessionid" />?referrer=<xsl:value-of select="$referrer" />&amp;language=<xsl:value-of select="$language" /></xsl:variable-->
  <xsl:variable name="deletepage">deletelistitem.jsp<xsl:value-of select="$wizardparams" /></xsl:variable>
  <xsl:variable name="uploadpage">upload.jsp<xsl:value-of select="$wizardparams" /></xsl:variable>
  
  
  <xsl:variable name="javascriptdir">../javascript/</xsl:variable>
  <xsl:variable name="mediadir">../media/</xsl:variable>

  <xsl:variable name="wizardtitle">
    <xsl:call-template name="i18n">
      <xsl:with-param name="nodes" select="/*/title" />
      </xsl:call-template>
    </xsl:variable>
  <xsl:param name="title"><xsl:value-of select="$wizardtitle" /></xsl:param>

  <!-- ================================================================================
       General appearance
       ================================================================================ -->

  <xsl:variable name="imagesize">+s(128x128)</xsl:variable>

  <xsl:template name="extrastyle" />        <!-- If you want to add a cascading stylesheet (to change the appearance), the you can overrride this -->
  <xsl:template name="extrajavascript" />   <!-- If you need extra javascript, then you can override this thing -->





  <!-- ================================================================================ -->

    <!-- utitily function. Takes a file and gets the directory part of it -->
  <xsl:template name="getdirpart">
    <xsl:param name="dir" />
    <xsl:variable name="firstdir" select="substring-before($dir, '/') " />
    <xsl:variable name="restdir" select="substring-after($dir, '/') " />
    <!-- if still a rest then add firstdir to dir -->
    <xsl:if test="$restdir">
      <xsl:value-of select="$firstdir" /><xsl:text>/</xsl:text>
      <xsl:call-template name="getdirpart">
        <xsl:with-param name="dir" select="$restdir" />
      </xsl:call-template>    
    </xsl:if>  
  </xsl:template>



  <!-- 
       xml:lang attribute of prompt, description and title tags
       -->
  <xsl:template name="i18n">
    <xsl:param name="nodes" />
    <xsl:choose>
      <xsl:when test="$nodes[lang($language)]">       
        <xsl:value-of select="$nodes[lang($language)]" />
      </xsl:when>
      <!-- default to english -->
      <xsl:when test="$nodes[lang('en')]">
        <xsl:value-of select="$nodes[lang('en')]" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$nodes[1]" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
  
