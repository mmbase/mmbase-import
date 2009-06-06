<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    Basic parameters and settings for all xsl's of the editwizards.

    @since  MMBase-1.6
    @author Michiel Meeuwissen
    @author Nico Klasens
    @author Martijn Houtman
    @version $Id: base.xsl,v 1.37 2007-04-23 14:08:32 michiel Exp $
  -->
  <xsl:import href="xsl/prompts.xsl" />

  <!--
    For the people who are wondering why we don't use an DOCTYPE.
    The editwizards are using extra attributes to do validation and
    other dynamic stuff. So the editwizards don't comply to the DOCTYPE standards

    MM: It could comply, when using namespaces.

    The xsl:output will generate
    <META http-equiv="Content-Type" content="text/html; charset=utf-8">

      btw, output method "xml" which looks nicer (<br />) breaks empty text-area's
  -->
  <xsl:output
    method="html"
    encoding="utf-8"
    omit-xml-declaration="yes"
    standalone="yes"
    indent="no" />

  <xsl:param name="cloud" />
  <xsl:param name="username">(unknown)</xsl:param>
  <xsl:param name="language">en</xsl:param>
  <xsl:param name="country"></xsl:param>
  <xsl:param name="sessionid" />
  <xsl:param name="popupid" />
  <xsl:param name="loginmethod" />

  <xsl:param name="debug">false</xsl:param>
  <xsl:param name="sessionkey">editwizard</xsl:param>
  <!-- name of variable in session in which is the cloud -->
  <xsl:param name="cloudkey">cloud_mmbase</xsl:param>

  <!-- timezone, empty is server timezone -->
<!-- example
  <xsl:param name="timezone">GMT</xsl:param>
-->
  <xsl:param name="timezone" />
  <xsl:variable name="date-pattern">:LONG.SHORT</xsl:variable>

  <!-- Maximum length for fields (approximation) -->
  <xsl:variable name="MAX_LENGTH" select="30" />

  <!-- The searchlist can be a WINDOW or IFRAME -->
  <xsl:param name="SEARCH_LIST_TYPE">WINDOW</xsl:param>

  <xsl:variable name="cssdir">../style/</xsl:variable>
  <xsl:variable name="javascriptdir">../javascript/</xsl:variable>
  <xsl:variable name="mediadir">../media/</xsl:variable>

  <!-- name of the file that called list.jsp or default.jsp, can be used for back-buttons (relative to context-root) -->
  <xsl:param name="referrer" />
  <xsl:param name="referrer_encoded" />
  <!-- Perhaps you want to refer to stuff not relative to the referrer-page, but to the root of the site where it belongs to.
    This must be given to the jsp's then with the paremeter 'templates'
  -->
  <xsl:param name="templatedir"><xsl:value-of select="$referrerdir" /></xsl:param>

  <!-- ================================================================================
    Directory and page settings
    ================================================================================ -->

  <!-- The web-application's context -->
  <xsl:param name="ew_context" />
  <!-- The directory in which the editwizards are installed (relative to context root) -->
  <xsl:param name="ew_path" />
  <xsl:variable name="rootew_path">
    <xsl:value-of select="$ew_context" /><xsl:value-of select="$ew_path" />
  </xsl:variable>
  <!-- relative to root -->
  <xsl:variable name="rootreferrer">
    <xsl:choose>
      <xsl:when test="substring($referrer, 1, 6) = 'https:'">
        <xsl:value-of select="$referrer" />
      </xsl:when>
      <xsl:when test="substring($referrer, 1, 5) = 'http:'">
        <xsl:value-of select="$referrer" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$ew_context" /><xsl:value-of select="$referrer" />
      </xsl:otherwise>

    </xsl:choose>
  </xsl:variable>

  <!-- the directory of that file, needed to refer to resources there (when you override), like e.g. images -->
  <xsl:variable name="referrerdir">
    <xsl:call-template name="getdirpart">
      <xsl:with-param name="dir" select="$rootreferrer" />
    </xsl:call-template>
  </xsl:variable>

  <xsl:param name="wizardjsp">wizard.jsp</xsl:param>
  <xsl:variable name="wizardamp">
    <xsl:choose>
      <xsl:when test="contains($wizardjsp, '?')">
        <xsl:text>&amp;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>?</xsl:text>
      </xsl:otherwise>      
    </xsl:choose>    
  </xsl:variable>

  <xsl:param name="wizardparams"><xsl:value-of select="$sessionid" />proceed=true&amp;sessionkey=<xsl:value-of select="$sessionkey" />&amp;loginmethod=<xsl:value-of select="$loginmethod" />&amp;language=<xsl:value-of select="$language" />&amp;country=<xsl:value-of select="$country" />&amp;debug=<xsl:value-of select="$debug" /></xsl:param>

  <xsl:variable name="listpage">list.jsp?<xsl:value-of select="$wizardparams" />&amp;popupid=<xsl:value-of select="$popupid" />&amp;loginmethod=<xsl:value-of select="$loginmethod" /></xsl:variable>
  


  <xsl:variable name="wizardpage"><xsl:value-of select="$wizardjsp" /><xsl:value-of select="$wizardamp" /><xsl:value-of select="$wizardparams" />&amp;popupid=<xsl:value-of select="$popupid" /></xsl:variable>
  <xsl:variable name="formwizardpage"><xsl:value-of select="$wizardjsp" /><xsl:value-of select="$sessionid" /></xsl:variable>

  <xsl:template name="formwizardargs">
    <input type="hidden" name="proceed" value="true" />
    <input type="hidden" name="loginmethod" value="{$loginmethod}" />
    <input type="hidden" name="sessionkey" value="{$sessionkey}" />
    <input type="hidden" name="language" value="{$language}" />
    <input type="hidden" name="country" value="{$country}" />
    <input type="hidden" name="debug" value="{$debug}" />
    <input type="hidden" name="popupid" value="{$popupid}" />

  </xsl:template>

  <xsl:variable name="popuppage">wizard.jsp?<xsl:value-of select="$wizardparams" /></xsl:variable>

  <!--xsl:variable name="popuppage">wizard.jsp<xsl:value-of select="$sessionid" />?referrer=<xsl:value-of select="$referrer_encoded" />&amp;language=<xsl:value-of select="$language" /></xsl:variable-->
  <xsl:variable name="deletepage">deletelistitem.jsp?<xsl:value-of select="$wizardparams" /></xsl:variable>
  <xsl:variable name="unlinkpage">unlinklistitem.jsp?<xsl:value-of select="$wizardparams" /></xsl:variable>
  <xsl:variable name="uploadpage">upload.jsp?<xsl:value-of select="$wizardparams" /></xsl:variable>

  <xsl:variable name="wizardtitle">
    <xsl:call-template name="i18n">
      <xsl:with-param name="nodes" select="/*/title" />
    </xsl:call-template>
    <!-- We need this xsl:text, because some browsers don't understand <title/> (IE) -->
    <xsl:text><!-- help IE --></xsl:text>
  </xsl:variable>
  <!-- No Clue why we need this. It throws an exception when it is not here -->
  <xsl:param name="title"><xsl:value-of select="$wizardtitle" /></xsl:param>

  <!-- ================================================================================
    General appearance
    ================================================================================ -->

  <xsl:variable name="imagesize">+s(128x128)</xsl:variable>

  <!-- ================================================================================ -->

  <!-- this is the entry-point, I suppose -->
  <xsl:template match="/">
    <html>
      <head>
        <title><xsl:call-template name="htmltitle" /></title>
        <xsl:call-template name="extrameta" />
        <xsl:call-template name="style" />
        <xsl:call-template name="colorstyle" />
        <xsl:call-template name="extrastyle" />
        <xsl:call-template name="javascript" />
        <xsl:call-template name="extrajavascript" />
      </head>
      <body>
        <xsl:if test="not($BodyOnLoad=&apos;&apos;)">
          <xsl:attribute name="onload"><xsl:value-of select="$BodyOnLoad" /></xsl:attribute>
        </xsl:if>
        <xsl:if test="not($BodyOnunLoad=&apos;&apos;)">
          <xsl:attribute name="onunload"><xsl:value-of select="$BodyOnunLoad" /></xsl:attribute>
        </xsl:if>

        <xsl:call-template name="htmlbody" />
      </body>
    </html>
  </xsl:template>

  <!-- html::title. -->
  <xsl:template name="htmltitle" />
  <!-- html::body onLoad attribute. Default is no onLoad attribute -->
  <xsl:variable name="BodyOnLoad" />
  <!-- html::body onunLoad attribute. Default is no onunLoad attribute -->
  <xsl:variable name="BodyOnunLoad" />

  <!-- Sub xsl's will use this to define a default css -->
  <xsl:template name="style">
    <link rel="stylesheet" type="text/css" href="{$cssdir}layout/base.css" />
  </xsl:template>
  <!-- Sub xsl's will use this to define a default css -->
  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$cssdir}color/base.css" />
  </xsl:template>
  <!-- Sub xsl's will use this to define javascript -->
  <xsl:template name="javascript" />

  <!-- If you need extra meta, then you can override this thing -->
  <xsl:template name="extrameta" />
  <!-- If you need a cascading stylesheet (to change the appearance),
    the you can overrride this -->
  <xsl:template name="extrastyle" />
  <!-- If you need extra javascript, then you can override this thing -->
  <xsl:template name="extrajavascript" />

  <!-- If you need a different htmlbody, then you can override this thing -->
  <xsl:template name="htmlbody">
    <xsl:call-template name="headcontent" />
    <xsl:call-template name="bodycontent" />
  </xsl:template>

  <!-- If you need a different headcontent, then you can override this thing -->
  <xsl:template name="headcontent" >
    <table class="head">
      <tr class="headtitle">
        <xsl:call-template name="title" />
      </tr>
      <tr class="headsubtitle">
        <xsl:call-template name="subtitle" />
      </tr>
    </table>
  </xsl:template>

  <!-- If you need a different bodycontent, then you can override this thing -->
  <xsl:template name="bodycontent" >
    <table class="body">
      <xsl:call-template name="body" />
    </table>
  </xsl:template>

  <!-- If you need a title, then you can override this thing -->
  <xsl:template name="title" />
  <!-- If you need a subtitle, then you can override this thing -->
  <xsl:template name="subtitle" />
  <!-- If you need a body, then you can override this thing -->
  <xsl:template name="body" />

  <xsl:variable name="defaultsearchage">7</xsl:variable>
  <xsl:variable name="searchagetype">combobox</xsl:variable>

  <!-- The age search options of a search item -->
  <xsl:template name="searchageoptions">
    <xsl:param name="selectedage" />
    <option value="0">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="&apos;0&apos;" />
        <xsl:with-param name="pref" select="$selectedage" />
      </xsl:call-template>
      <xsl:call-template name="age_now" />
    </option>
    <option value="1">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="&apos;1&apos;" />
        <xsl:with-param name="pref" select="$selectedage" />
      </xsl:call-template>
      <xsl:call-template name="age_day" />
    </option>
    <option value="7">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="&apos;7&apos;" />
        <xsl:with-param name="pref" select="$selectedage" />
      </xsl:call-template>
      <xsl:call-template name="age_week" />
    </option>
    <option value="31">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="&apos;31&apos;" />
        <xsl:with-param name="pref" select="$selectedage" />
      </xsl:call-template>
      <xsl:call-template name="age_month" />
    </option>
    <option value="365">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="&apos;365&apos;" />
        <xsl:with-param name="pref" select="$selectedage" />
      </xsl:call-template>
      <xsl:call-template name="age_year" />
    </option>
    <option value="-1">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="&apos;-1&apos;" />
        <xsl:with-param name="pref" select="$selectedage" />
      </xsl:call-template>
      <xsl:call-template name="age_any" />
    </option>
  </xsl:template>

  <xsl:template name="searchage">
    <xsl:param name="real" />
    <xsl:param name="pref" />
    <xsl:if test="($real=$pref) or (not($pref) and $defaultsearchage=$real)">
      <xsl:attribute name="selected">true</xsl:attribute>
    </xsl:if>
  </xsl:template>

  <!--
    This template truncates fields that are too long.
  -->
  <xsl:template name="writeCurrentField">
	<xsl:param name="val" select="."/>
    <!-- Disabled at the moment, because fields with html in it
      screw up the full page when truncated.
      This will work if we can filter the html out of the text before
      truncating. -->

    <!--xsl:choose>
      <xsl:when test="string-length($val) > $MAX_LENGTH">
        <xsl:value-of select="substring(normalize-space($val), 0, $MAX_LENGTH)" disable-output-escaping="yes" />
        ...
      </xsl:when>
      <xsl:otherwise-->
        <xsl:value-of select="$val" disable-output-escaping="yes" />
      <!--/xsl:otherwise>
    </xsl:choose-->
  </xsl:template>

  <!-- utitily function. Takes a file and gets the directory part of it -->
  <xsl:template name="getdirpart">
    <xsl:param name="dir" />
    <xsl:variable name="filename" select="substring-before(concat($dir, &apos;?&apos;), &apos;?&apos;) " />
    <xsl:variable name="firstdir" select="substring-before($filename, &apos;/&apos;) " />
    <xsl:variable name="restdir" select="substring-after($filename, &apos;/&apos;) " />
    <!-- if still a rest then add firstdir to dir -->
    <xsl:if test="$restdir">
      <xsl:value-of select="$firstdir" />
      <xsl:text>/</xsl:text>
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
        <xsl:value-of select="$nodes[lang($language)]" disable-output-escaping="yes" />
      </xsl:when>
      <!-- default to english -->
      <xsl:when test="$nodes[lang(&apos;en&apos;)]">
        <xsl:value-of select="$nodes[lang(&apos;en&apos;)]" disable-output-escaping="yes" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$nodes[1]" disable-output-escaping="yes" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>