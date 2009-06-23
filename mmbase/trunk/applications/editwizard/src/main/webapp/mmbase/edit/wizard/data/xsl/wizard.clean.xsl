<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">
  <!--
    wizard.xsl

    @since  MMBase-1.6
    @author Kars Veling
    @author Michiel Meeuwissen
    @author Pierre van Rooden
    @author Nico Klasens
    @author Martijn Houtman
    @author Robin van Meteren
    @version $Id: wizard.xsl,v 1.193 2009-02-11 20:44:05 nklasens Exp $

    This xsl uses Xalan functionality to call java classes
    to format dates and call functions on nodes
    See the xmlns attributes of the xsl:stylesheet
  -->

  <xsl:import href="xsl/base.xsl"/>

  <xsl:variable name="htmlareadir"></xsl:variable>

  <xsl:variable name="default-cols">80</xsl:variable>
  <xsl:variable name="default-rows">10</xsl:variable>

  <xsl:param name="objectnumber"/>

  <!-- ================================================================================
    The following things can be overriden to customize the appearance of wizard
    ================================================================================ -->

  <xsl:variable name="BodyOnLoad">doOnLoad_ew(); start_validator(); initPopCalendar();</xsl:variable>
  <xsl:variable name="BodyOnunLoad">doOnUnLoad_ew();</xsl:variable>

  <xsl:template name="javascript">
    <script type="text/javascript" src="{$javascriptdir}tools.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript" src="{$javascriptdir}date.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script type="text/javascript" src="{$javascriptdir}validator.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <xsl:element name="script">
      <xsl:attribute name="type">text/javascript</xsl:attribute>
      <xsl:attribute name="src">
        <xsl:value-of select="$javascriptdir" />
        <xsl:text disable-output-escaping="yes">editwizard.jsp</xsl:text>
        <xsl:value-of select="$sessionid" />
        <xsl:text disable-output-escaping="yes">?language=</xsl:text>
        <xsl:value-of select="$language" />
        <xsl:text disable-output-escaping="yes">&amp;country=</xsl:text>
        <xsl:value-of select="$country" />
        <xsl:text disable-output-escaping="yes">&amp;timezone=</xsl:text>
        <xsl:value-of select="$timezone" />
        <xsl:text disable-output-escaping="yes">&amp;referrer=</xsl:text>
        <xsl:value-of select="$referrer_encoded" />
      </xsl:attribute>
      <xsl:comment>help IE</xsl:comment>
    </xsl:element>
    <script type="text/javascript">
      <xsl:text disable-output-escaping="yes">
        <![CDATA[
      <!--
      // Mozilla does not like this.
      // See also http://www.mmbase.org/jira/browse/MMB-1234
      // May I please drop this nonsense?
      var hist = window.history;
      if (hist) {
         hist.go(1);
      }
      window.onresize = function(e){ resizeEditTable(); }
      -->
      ]]></xsl:text>
    </script>

    <!-- SEARCH_LIST_TYPE is defined in the base.xsl-->
    <xsl:choose>
      <xsl:when test="$SEARCH_LIST_TYPE='IFRAME'">
        <script type="text/javascript">
          <xsl:text disable-output-escaping="yes">
            <![CDATA[
          <!--
            document.writeln('<div id="searchframe" class="searchframe"><iframe onblur="removeModalIFrame();" src="searching.html" id="modaliframe" class="searchframe" scrolling="no"></iframe></div>');
          -->
          ]]></xsl:text>
        </script>
        <script type="text/javascript" src="{$javascriptdir}searchiframe.js">
          <xsl:comment>help IE</xsl:comment>
        </script>
      </xsl:when>
      <xsl:otherwise>
        <script type="text/javascript" src="{$javascriptdir}searchwindow.js">
          <xsl:comment>help IE</xsl:comment>
        </script>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:call-template name="javascript-date-picker"/>
    <xsl:call-template name="javascript-html"/>
  </xsl:template>

  <xsl:template name="javascript-date-picker">
    <script type="text/javascript">
        var gotoString = '<xsl:value-of select="$datepicker_currentmonth"/>';
        var todayString = '<xsl:value-of select="$datepicker_today"/>';
        var weekString = 'Wk';
        var scrollLeftMessage = '<xsl:value-of select="$datepicker_scrollleft"/>';
        var scrollRightMessage = '<xsl:value-of select="$datepicker_scrollright"/>';
        var selectMonthMessage = '<xsl:value-of select="$datepicker_selectmonth"/>';
        var selectYearMessage = '<xsl:value-of select="$datepicker_selectyear"/>';
        var selectDateMessage = '<xsl:value-of select="$datepicker_selectdate"/>'; // do not replace [date], it will be replaced by date.
        var monthName =	new	Array(
                  '<xsl:value-of select="$date_january"/>','<xsl:value-of select="$date_february"/>',
                  '<xsl:value-of select="$date_march"/>','<xsl:value-of select="$date_april"/>',
                  '<xsl:value-of select="$date_may"/>','<xsl:value-of select="$date_june"/>',
                  '<xsl:value-of select="$date_july"/>','<xsl:value-of select="$date_august"/>',
                  '<xsl:value-of select="$date_september"/>','<xsl:value-of select="$date_october"/>',
                  '<xsl:value-of select="$date_november"/>','<xsl:value-of select="$date_december"/>');
    var dayName = new Array	('<xsl:value-of select="$day_sun"/>','<xsl:value-of select="$day_mon"/>',
                '<xsl:value-of select="$day_tue"/>','<xsl:value-of select="$day_wed"/>',
                '<xsl:value-of select="$day_thu"/>','<xsl:value-of select="$day_fri"/>',
                '<xsl:value-of select="$day_sat"/>');
    </script>
    <script type="text/javascript" src="{$javascriptdir}datepicker.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
  </xsl:template>

  <xsl:template name="javascript-html" />


  <xsl:template name="htmltitle">
    <xsl:value-of select="$wizardtitle"/>
    <xsl:text> - </xsl:text>
    <xsl:call-template name="i18n">
      <xsl:with-param name="nodes" select="//wizard/form[@id=/wizard/curform]/title"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="style">
    <link rel="stylesheet" type="text/css" href="{$cssdir}layout/wizard.css"/>

    <xsl:call-template name="stylehtml"/>
  </xsl:template>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$cssdir}color/wizard.css"/>
  </xsl:template>

  <xsl:template name="stylehtml" />

  <xsl:template name="title">
    <td>
      <xsl:value-of select="$wizardtitle"/>
    </td>
    <td>
      <xsl:call-template name="help"/>
      <xsl:call-template name="debug"/>
    </td>
  </xsl:template>

  <xsl:template name="subtitle">
    <td colspan="2">
      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="//wizard/form[@id=/wizard/curform]/subtitle"/>
      </xsl:call-template>
    </td>
  </xsl:template>

  <xsl:template name="help">
    <xsl:if test="/wizard/description">
      <span class="imgbutton" title="{$tooltip_help}" onclick="doHelp();">
        <xsl:call-template name="prompt_help"/>
      </span>
      <div id="help_text" style="position:absolute; display:none;">
        <xsl:call-template name="i18n">
          <xsl:with-param name="nodes" select="/wizard/description"/>
        </xsl:call-template>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template name="debug">
    <xsl:if test="$debug='true'">
      <a href="debug.jsp{$sessionid}?sessionkey={$sessionkey}&amp;popupid={$popupid}" target="_blank">
        <xsl:call-template name="prompt_debug"/>
      </a>
    </xsl:if>
  </xsl:template>

  <xsl:template name="body">
    <xsl:apply-templates select="wizard"/>
  </xsl:template>

  <xsl:template match="wizard">
    <xsl:call-template name="beforeform"/>
    <xsl:call-template name="bodyform"/>
  </xsl:template>

  <xsl:template name="beforeform">
    <!--tr class="beforeformcanvas">
        <td>
        </td>
      </tr--></xsl:template>

  <!-- Every wizard is based on one 'form',
       with a bunch of attributes and a few hidden entries.
       Those are set here -->
  <xsl:template name="bodyform">
    <tr class="formcanvas">
      <td>
        <form name="form" method="post" action="{$formwizardpage}" id="{/wizard/curform}"
          wizardinstance="{/wizard/@instance}"
          message_pattern="{$message_pattern}" message_required="{$message_required}"
          message_minlength="{$message_minlength}" message_maxlength="{$message_maxlength}"
          message_min="{$message_min}" message_max="{$message_max}" message_mindate="{$message_mindate}"
          message_maxdate="{$message_maxdate}" message_dateformat="{$message_dateformat}"
          message_thisnotvalid="{$message_thisnotvalid}" message_notvalid="{$message_notvalid}"
          message_listtooshort="{$message_listtooshort}"
          invalidlist="{/wizard/form[@invalidlist]/@invalidlist}" filter_required="{$filter_required}">
          <xsl:choose>
            <xsl:when test="/*/*/step[@valid='false'][not(@form-schema=/wizard/curform)]">
              <xsl:attribute name="otherforms">invalid</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="otherforms">valid</xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>

          <input type="hidden" name="curform" value="{/wizard/curform}"/>
          <input type="hidden" name="cmd" value="" id="hiddencmdfield"/>

          <xsl:call-template name="formwizardargs"/>
          <xsl:call-template name="formhiddenargs"/>
          <xsl:call-template name="formcontent"/>
        </form>
      </td>
    </tr>
  </xsl:template>

  <!-- Could be used to send customized hidden input fields -->
  <xsl:template name="formhiddenargs"/>

  <xsl:template name="formcontent">
    <div id="stepsbar" class="stepscontent">
      <xsl:apply-templates select="/*/steps-validator"/>
    </div>
    <div id="editform" class="editform">
      <table class="formcontent">
        <xsl:apply-templates select="form[@id=/wizard/curform]"/>
      </table>
    </div>
    <div id="commandbuttonbar" class="buttonscontent">
      <xsl:for-each select="/*/steps-validator">
        <xsl:call-template name="buttons"/>
      </xsl:for-each>
    </div>
  </xsl:template>

  <!--
    Below this comment are the templates which create the navigational element of the
    wizard screen. The navigational elements consists of the steps and buttons.
    A wizard can be split up into multiple forms. Every form is a step in the wizard.
  -->

  <xsl:template match="steps-validator">
    <!-- when multiple steps, otherwise do nothing -->
    <xsl:if test="count(step) &gt; 1">
      <xsl:call-template name="steps"/>
    </xsl:if>
  </xsl:template>

  <!-- The steps buttons are only created (in steps-validator) if there is more than one step -->
  <xsl:template name="steps">
    <table class="stepscontent">
      <tr>
        <td class="stepprevious">
          <xsl:call-template name="previousbutton"/>
        </td>
        <xsl:for-each select="step">
          <xsl:call-template name="steptemplate"/>
        </xsl:for-each>
        <td class="stepnext">
          <xsl:call-template name="nextbutton"/>
        </td>
        <td/>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="steptemplate">
    <td>
      <xsl:variable name="schemaid" select="@form-schema"/>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="@form-schema=/wizard/curform">stepcurrent</xsl:when>
          <xsl:otherwise>stepother</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:call-template name="step"/>
    </td>
  </xsl:template>

  <!-- The appearance of one 'step' button -->
  <xsl:template name="step">
    <a>
      <!-- xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="@form-schema=/wizard/curform">current</xsl:when>
          <xsl:otherwise>other</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute -->
      <xsl:call-template name="stepsattributes"/>

      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="/*/form[@id=current()/@form-schema]/title"/>
      </xsl:call-template>
      <!-- xsl:call-template name="prompt_step" /-->
    </a>
  </xsl:template>

  <xsl:template name="stepsattributes">
    <xsl:attribute name="href">javascript:doGotoForm('<xsl:value-of select="@form-schema"/>');</xsl:attribute>
    <xsl:attribute name="titlevalid"><xsl:value-of select="$tooltip_valid"/></xsl:attribute>
    <xsl:attribute name="id">step-<xsl:value-of select="@form-schema"/></xsl:attribute>
    <xsl:attribute name="titlenotvalid"><xsl:value-of select="$tooltip_not_valid"/></xsl:attribute>
    <xsl:attribute name="title">
      <xsl:value-of select="/*/form[@id=current()/@form-schema]/title"/>
      <xsl:if test="@valid='false'">
        <xsl:value-of select="$tooltip_step_not_valid"/>
      </xsl:if>
    </xsl:attribute>
    <xsl:attribute name="class">
      <xsl:if test="@valid='true'">valid</xsl:if>
      <xsl:if test="@valid='false'">notvalid</xsl:if>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="previousbutton">
    <xsl:choose>
      <xsl:when test="/wizard/form[@id=/wizard/prevform]">
        <a class="step" href="javascript:doGotoForm('{/wizard/prevform}')" title="{$tooltip_previous} '{/wizard/form[@id=/wizard/prevform]/title}'">
          <xsl:call-template name="prompt_previous"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <span class="step-disabled" title="{$tooltip_no_previous}">
          <!-- xsl:call-template name="prompt_previous" / --></span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="nextbutton">
    <xsl:choose>
      <xsl:when test="/wizard/form[@id=/wizard/nextform]">
        <a class="step" href="javascript:doGotoForm('{/wizard/nextform}')" title="{$tooltip_next} '{/wizard/form[@id=/wizard/nextform]/title}'">
          <xsl:call-template name="prompt_next"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <span class="step-disabled" title="{$tooltip_no_next}">
          <!-- xsl:call-template name="prompt_next" / --></span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Buttons are (always) called -->
  <xsl:template name="buttons">
    <table class="buttonscontent">
      <tr>
        <td>
          <!-- cancel -->
          <xsl:call-template name="cancelbutton"/>
          <xsl:text> - </xsl:text>
          <!-- commit  -->
          <xsl:call-template name="savebutton"/>
          <xsl:text> - </xsl:text>
          <!-- Saveonly  -->
          <xsl:call-template name="saveonlybutton"/>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="savebutton">
    <a href="javascript:doSave();" id="bottombutton-save" unselectable="on" titlesave="{$tooltip_save}" titlenosave="{$tooltip_no_save}">
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_save"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_save"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="prompt_save"/>
    </a>
  </xsl:template>

  <!-- don't want the save-only button?. Put this in your wizard.xsl extension
  <xsl:template name="saveonlybutton">
    <a id="bottombutton-saveonly" />
  </xsl:template>
  -->

  <xsl:template name="saveonlybutton">
    <a href="javascript:doSaveOnly();" id="bottombutton-saveonly" unselectable="on" titlesave="{$tooltip_save_only}" titlenosave="{$tooltip_no_save}">
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_save_only"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_save"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="prompt_save_only"/>
    </a>
  </xsl:template>

  <xsl:template name="cancelbutton">
    <a href="javascript:doCancel();" id="bottombutton-cancel" class="bottombutton" title="{$tooltip_cancel}">
      <xsl:call-template name="prompt_cancel"/>
    </a>
  </xsl:template>

  <!--
    Below this comment are the templates which create the edit part of the wizard screen.
    The edit part consists of input fields and list of related objects. The fields can be
    grouped with fieldsets.
  -->

  <xsl:template match="form">
    <xsl:for-each select="listset|fieldset|field|list">
      <xsl:choose>
        <xsl:when test="name()='field'">
          <tr class="fieldcanvas">
            <xsl:apply-templates select="."/>
          </tr>
        </xsl:when>
        <xsl:when test="name()='list'">
          <tr class="listcanvas">
            <td colspan="2">
              <xsl:apply-templates select="."/>
            </td>
          </tr>
        </xsl:when>
        <xsl:when test="name()='fieldset'">
          <xsl:choose>
            <xsl:when test="count(prompt) = 0">
              <xsl:for-each select="field">
                <tr class="fieldcanvas">
                  <xsl:apply-templates select="."/>
                </tr>
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <tr class="fieldsetcanvas">
                <xsl:apply-templates select="."/>
              </tr>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="name()='listset'">
          <xsl:for-each select="./list">
            <tr class="listcanvas">
              <td colspan="2">
                <xsl:apply-templates select="."/>
              </td>
            </tr>
              </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <!-- How should we visualize the grouping of fields? -->
  <xsl:template match="fieldset">
    <td class="fieldprompt">
      <xsl:call-template name="prompt"/>
    </td>
    <td class="field">
      <xsl:call-template name="fieldintro"/>
      <xsl:for-each select="field">
        <xsl:call-template name="fieldintern"/>
        <xsl:text disable-output-escaping="yes"> </xsl:text>
      </xsl:for-each>
    </td>
  </xsl:template>

  <!--
      If you don't use at least one prompt inside a fieldset, there is actually little
      point in using it, besides if you use it only as a grouping
      (e.g. the implemnentation of a 'block').
      So, we simply ignore the 'fieldset' and simply apply the sub-elements if there are no prompts
  -->
  <xsl:template match="fieldset[count(prompt) = 0]">
    <xsl:apply-templates select="field" />
  </xsl:template>

  <xsl:template match="field">
    <td class="fieldprompt">
      <xsl:call-template name="prompt"/>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <td class="field">
      <xsl:call-template name="fieldintro"/>
      <xsl:call-template name="fieldintern"/>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
  </xsl:template>

  <xsl:template name="prompt">
    <span id="prompt_{@fieldname}" class="valid" prompt="{prompt}">
      <xsl:choose>
        <xsl:when test="description">
          <xsl:attribute name="title">
            <xsl:call-template name="i18n">
              <xsl:with-param name="nodes" select="description"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:attribute name="description">
            <xsl:call-template name="i18n">
              <xsl:with-param name="nodes" select="description"/>
            </xsl:call-template>
          </xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="title">
            <xsl:call-template name="i18n">
              <xsl:with-param name="nodes" select="prompt"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:attribute name="description">
            <xsl:call-template name="i18n">
              <xsl:with-param name="nodes" select="prompt"/>
            </xsl:call-template>
          </xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="prompt"/>
      </xsl:call-template>
    </span>
  </xsl:template>

  <!--
    fieldintro can be used to add a leading description to a field or fieldset
  -->
  <xsl:template name="fieldintro">
  </xsl:template>

  <!--
    fieldintern is called to draw the values
  -->
  <xsl:template name="fieldintern">

    <xsl:call-template name="i18n">
      <xsl:with-param name="nodes" select="prefix"/>
    </xsl:call-template>

    <xsl:choose>
      <xsl:when test="@ftype='startwizard'">
        <xsl:call-template name="ftype-startwizard"/>
      </xsl:when>
      <xsl:when test="@ftype='function'">
        <xsl:call-template name="ftype-function"/>
      </xsl:when>
      <xsl:when test="@ftype='data'">
        <xsl:call-template name="ftype-data"/>
      </xsl:when>
      <xsl:when test="@ftype='line'">
        <xsl:call-template name="ftype-line"/>
      </xsl:when>
      <xsl:when test="@ftype='text'">
        <xsl:call-template name="ftype-text"/>
      </xsl:when>
      <xsl:when test="@ftype='mmxf'">
        <xsl:call-template name="ftype-text"/>
      </xsl:when>
      <xsl:when test="@ftype='html'">
        <xsl:call-template name="ftype-html"/>
      </xsl:when>
      <xsl:when test="@ftype='relation'">
        <xsl:call-template name="ftype-relation"/>
      </xsl:when>
      <xsl:when test="@ftype='enum'">
        <xsl:call-template name="ftype-enum"/>
      </xsl:when>
      <xsl:when test="@ftype='enumdata'">
        <xsl:call-template name="ftype-enumdata"/>
      </xsl:when>
      <xsl:when test="(@ftype='datetime') or (@ftype='date') or (@ftype='time') or (@ftype='duration')">
        <xsl:call-template name="ftype-datetime"/>
      </xsl:when>
      <xsl:when test="@ftype='image'">
        <xsl:call-template name="ftype-image"/>
      </xsl:when>
      <xsl:when test="@ftype='flash'">
        <xsl:call-template name="ftype-flash"/>
      </xsl:when>
      <xsl:when test="@ftype='file'">
        <xsl:call-template name="ftype-file"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;imagedata&apos;">
        <xsl:call-template name="ftype-imagedata"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;filedata&apos;">
        <xsl:call-template name="ftype-filedata"/>
      </xsl:when>
      <xsl:when test="@ftype='radio'">
         <xsl:call-template name="ftype-radio"/>
      </xsl:when>
      <xsl:when test="@ftype='checkbox'">
         <xsl:call-template name="ftype-checkbox"/>
      </xsl:when>
      <xsl:when test="@ftype='boolean'">
         <xsl:call-template name="ftype-checkbox"/>
      </xsl:when>
      <xsl:when test="@ftype='realposition'">
        <xsl:call-template name="ftype-realposition"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-unknown"/>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:call-template name="i18n">
      <xsl:with-param name="nodes" select="postfix"/>
    </xsl:call-template>
  </xsl:template>


  <xsl:template name="ftype-startwizard">
    <xsl:if test="@objectnumber!=''">
      <span class="imgbutton">
        <a href="javascript:doStartWizard('{../../../@fid}','{../../../command[@name='add-item']/@value}','{@wizardname}','{@objectnumber}','{@origin}');">
          <xsl:call-template name="prompt_edit_wizard"/>
        </a>
      </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="ftype-function">
    <xsl:if test="not(string(number(@number)) = 'NaN')">
      <xsl:apply-templates select="value" mode="line">
        <xsl:with-param name="val">
          <xsl:value-of select="node:saxonFunction($cloud, string(@number), string(value))" disable-output-escaping="yes"/>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>

  <xsl:template name="ftype-data">
    <xsl:choose>
      <xsl:when test="@dttype='datetime'">
        <xsl:value-of select="date:format(string(value), $date-pattern, $timezone, $language, $country)" disable-output-escaping="yes"/>
      </xsl:when>
      <xsl:when test="@dttype='millisecondsdatetime'">
        <xsl:value-of select="date:format(string(value), $date-pattern, 1, $timezone, $language, $country)" disable-output-escaping="yes"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="value" mode="line"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ftype-line">
    <xsl:choose>
      <xsl:when test="@maywrite!='false'">
        <xsl:apply-templates select="value" mode="inputline"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-data"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ftype-html">
    <xsl:call-template name="ftype-text"/>
  </xsl:template>

  <xsl:template name="ftype-text">
    <xsl:choose>
      <xsl:when test="@maywrite!='false'">
        <span>
          <textarea
              class="input mm_validate mm_f_{../@name} mm_n_{../@number} mm_nm_{../@nodemanager}"
              name="{@fieldname}" id="{@fieldname}"  wrap="soft">
            <xsl:if test="@ftype = 'html'">
              <xsl:attribute name="class">htmlarea</xsl:attribute>
            </xsl:if>
            <xsl:if test="not(@cols)">
              <xsl:attribute name="cols"><xsl:value-of select="$default-cols" /></xsl:attribute>
            </xsl:if>
            <xsl:if test="not(@rows)">
              <xsl:attribute name="rows"><xsl:value-of select="$default-rows" /></xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="@*"/>

            <xsl:value-of select="translate(value,'&#13;','')" />
          </textarea>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-data"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ftype-relation">
    <xsl:call-template name="ftype-enum"/>
  </xsl:template>

  <xsl:template name="ftype-enum">
    <xsl:choose>
      <xsl:when test="@maywrite!='false'">
        <select name="{@fieldname}"
                class="selectinput mm_validate mm_f_{../@name} mm_n_{../@number} mm_nm_{../@nodemanager}">
          <xsl:apply-templates select="@*"/>
          <xsl:choose>
            <xsl:when test="optionlist/option[@selected='true']"/>
            <xsl:when test="@dtrequired='true'"/>
            <xsl:otherwise>
              <option value="">
                <xsl:call-template name="prompt_select"/>
              </option>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:for-each select="optionlist/option">
            <option value="{@id}">
              <xsl:if test="@selected='true'">
                <xsl:attribute name="selected">selected</xsl:attribute>
              </xsl:if>
              <xsl:choose>
                <xsl:when test="prompt">
                  <xsl:call-template name="i18n">
                    <xsl:with-param name="nodes" select="prompt" />
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="." /></xsl:otherwise>
              </xsl:choose>
            </option>
          </xsl:for-each>
        </select>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-enumdata"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ftype-enumdata">
    <xsl:if test="optionlist/option[@id=current()/value]">
      <xsl:apply-templates select="value" mode="line">
        <xsl:with-param name="val">
          <xsl:for-each select="optionlist/option[@id=current()/value]">
            <xsl:choose >
              <xsl:when test="prompt">
                <xsl:call-template name="i18n">
                  <xsl:with-param name="nodes" select="prompt" />
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise><xsl:value-of select="." /></xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>

  <xsl:template name="ftype-datetime">
    <xsl:choose>
      <xsl:when test="@maywrite!='false'">
        <div>
          <input type="hidden" name="{@fieldname}" value="{@ftype}" id="{@fieldname}">
            <xsl:attribute name="new"><xsl:value-of select="value = '' or value = '-1'"/></xsl:attribute>
            <xsl:apply-templates select="@*"/>
          </input>

          <xsl:if test="(@ftype='datetime') or (@ftype='date')">
            <xsl:call-template name="ftype-datetime-date"/>
          </xsl:if>

          <xsl:if test="@ftype='datetime'">
            <span class="time_at">
              <xsl:value-of select="$time_at"/>
            </span>
          </xsl:if>

          <xsl:if test="(@ftype='datetime') or (@ftype='time') or (@ftype='duration')">
            <xsl:call-template name="ftype-datetime-time"/>
          </xsl:if>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="ftype-data"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="gen-option">
    <xsl:param name="value"/>
    <xsl:param name="selected"/>
    <xsl:param name="text"/>
    <xsl:choose>
      <xsl:when test="$value = $selected">
        <option value="{$value}" selected="selected"><xsl:value-of select="$text"/></option>
      </xsl:when>
      <xsl:otherwise>
        <option value="{$value}"><xsl:value-of select="$text"/></option>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="loop-options">
    <xsl:param name="value">0</xsl:param>
    <xsl:param name="selected"/>
    <xsl:param name="end">0</xsl:param>

    <xsl:call-template name="gen-option">
      <xsl:with-param name="value" select="$value" />
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$value" />
    </xsl:call-template>

    <xsl:if test="$value &lt; $end">
      <xsl:call-template name="loop-options">
        <xsl:with-param name="value" select="$value + 1" />
        <xsl:with-param name="selected" select="$selected" />
        <xsl:with-param name="end" select="$end" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="optionlist_months">
    <xsl:param name="selected">1</xsl:param>

    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">1</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_january" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">2</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_february" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">3</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_march" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">4</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_april" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">5</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_may" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">6</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_june" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">7</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_july" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">8</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_august" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">9</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_september" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">10</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_october" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">11</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_november" />
    </xsl:call-template>
    <xsl:call-template name="gen-option">
      <xsl:with-param name="value">12</xsl:with-param>
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$date_december" />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="date-picker">
    <img class="calendar" src="{$mediadir}datepicker/calendar.gif" border="0" onClick="popUpCalendar(this, 'dd-mm-yyyy', - 205 , 5 , document.forms[0], 'internal_{@fieldname}',event);return false;"/>
  </xsl:template>

  <xsl:template name="ftype-datetime-date">
    <select name="internal_{@fieldname}_day" super="{@fieldname}">
      <xsl:call-template name="loop-options">
        <xsl:with-param name="value">1</xsl:with-param>
        <xsl:with-param name="selected" select="date:getDay(string(value), string($timezone))" />
        <xsl:with-param name="end" select="31" />
      </xsl:call-template>
    </select>
    <xsl:value-of select="$time_daymonth"/>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    <select name="internal_{@fieldname}_month" super="{@fieldname}">
      <xsl:call-template name="optionlist_months">
        <xsl:with-param name="selected" select="date:getMonth(string(value), string($timezone))" />
      </xsl:call-template>
    </select>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    <input
        class="date" name="internal_{@fieldname}_year" super="{@fieldname}" type="text" value="{date:getYear(string(value), string($timezone))}" size="5" maxlength="4"/>
    <xsl:call-template name="date-picker" />
  </xsl:template>

  <xsl:template name="ftype-datetime-time">

    <xsl:variable name="tz">
      <xsl:choose>
        <xsl:when test="@ftype = 'duration'">UTC</xsl:when>
        <xsl:otherwise><xsl:value-of select="string($timezone)"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <select name="internal_{@fieldname}_hours" super="{@fieldname}">
      <xsl:call-template name="loop-options">
        <xsl:with-param name="value">0</xsl:with-param>
        <xsl:with-param name="selected" select="date:getHours(string(value), $tz)" />
        <xsl:with-param name="end" select="23" />
      </xsl:call-template>
    </select>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;:&amp;nbsp;</xsl:text>
    <select name="internal_{@fieldname}_minutes" super="{@fieldname}">
      <xsl:call-template name="loop-options">
        <xsl:with-param name="value">0</xsl:with-param>
        <xsl:with-param name="selected" select="date:getMinutes(string(value), $tz)" />
        <xsl:with-param name="end" select="59" />
      </xsl:call-template>
    </select>
    <xsl:if test="@ftype='duration'">
      <xsl:text disable-output-escaping="yes">&amp;nbsp;:&amp;nbsp;</xsl:text>
      <select name="internal_{@fieldname}_seconds" super="{@fieldname}">
        <xsl:call-template name="loop-options">
          <xsl:with-param name="value">0</xsl:with-param>
          <xsl:with-param name="selected" select="date:getSeconds(string(value), $tz)" />
          <xsl:with-param name="end" select="59" />
        </xsl:call-template>
      </select>
    </xsl:if>
  </xsl:template>


  <xsl:template name="ftype-image">
    <xsl:if test="@maywrite!='false'">
      <xsl:choose>
        <xsl:when test="@dttype='binary' and not(upload)">
          <div class="imageupload">
            <div>
              <input type="hidden" name="{@fieldname}" value="" dttype="binary" ftype="image" >
                <xsl:if test="@dtrequired='true' and @size &lt;= 0">
                  <xsl:attribute name="dtrequired">true</xsl:attribute>
                </xsl:if>
              </input>
              <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
                <xsl:call-template name="prompt_image_upload"/>
              </a>
              <br/>
              <xsl:if test="@size &gt; 0">
                <img src="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
                <br/>
                 <a
                   href="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey,')'))}"
                   target="_new">
                   <xsl:call-template name="prompt_image_full" />
                 </a>
                <br/>
              </xsl:if>
            </div>
          </div>
        </xsl:when>
        <xsl:when test="@dttype='binary' and upload">
          <div class="imageupload">
            <input type="hidden" name="{@fieldname}" value="YES" dttype="binary" ftype="image" >
              <xsl:if test="@dtrequired='true'">
                <xsl:attribute name="dtrequired">true</xsl:attribute>
              </xsl:if>
            </input>
            <xsl:if test="contains(upload/path, '/') or contains(upload/path, '\')">
              <img src="{upload/path}" hspace="0" vspace="0" border="0" width="128" height="128"/>
            <br/>
            </xsl:if>
            <span>
              <xsl:value-of select="upload/@name"/>
              <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              (<xsl:value-of select="round((upload/@size) div 100) div 10"/>K)
            </span>
            <br/>
            <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
              <xsl:call-template name="prompt_image_replace"/>
            </a>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <span>
            <img src="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
            <br/>
            <a
              href="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey,')'))}"
              target="_new">
              <xsl:call-template name="prompt_image_full" />
            </a>
            <br/>
          </span>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="@maywrite='false'">
      <span class="readonly">
        <img src="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0"/>
      </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="ftype-flash">
    <xsl:if test="@maywrite!='false'">
      <xsl:choose>
        <xsl:when test="@dttype='binary' and not(upload)">
          <div class="imageupload">
            <div>
              <input type="hidden" name="{@fieldname}" value="" dttype="binary" ftype="flash" >
                <xsl:if test="@dtrequired='true' and @size &lt;= 0">
                  <xsl:attribute name="dtrequired">true</xsl:attribute>
                </xsl:if>
              </input>
              <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
                <xsl:call-template name="prompt_do_upload"/>
              </a>
              <br/>
              <xsl:if test="@size != 0">
                <object src="{node:saxonFunction($cloud, string(@number), 'url')}"
                        data="{node:saxonFunction($cloud, string(@number), 'url')}"
                        type="application/x-shockwave-flash"
                        height="100" width="100"
                         title="{field[@name='description']}">
                  <param name="{node:saxonFunction($cloud, string(@number), 'url')}" />
                </object>
                <br/>
              </xsl:if>
            </div>
          </div>
        </xsl:when>
        <xsl:when test="@dttype='binary' and upload">
          <div class="imageupload">
            <input type="hidden" name="{@fieldname}" value="YES" dttype="binary" ftype="image" >
              <xsl:if test="@dtrequired='true'">
                <xsl:attribute name="dtrequired">true</xsl:attribute>
              </xsl:if>
            </input>
            <xsl:if test="contains(upload/path, '/') or contains(upload/path, '\')">
              <object src="{upload/path}" hspace="0" vspace="0" border="0" width="128" height="128"/>
              <br/>
            </xsl:if>
            <span>
              <xsl:value-of select="upload/@name"/>
              <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              (<xsl:value-of select="round((upload/@size) div 100) div 10"/>K)
            </span>
            <br/>
            <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
              <xsl:call-template name="prompt_uploaded"/>
            </a>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <span>
            <object src="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
            <br/>
            <a
              href="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey,')'))}"
              target="_new">
              <xsl:call-template name="prompt_do_download" />
            </a>
            <br/>
          </span>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="@maywrite='false'">
      <span class="readonly">
        <img src="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0"/>
      </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="ftype-file">
    <xsl:choose>
      <xsl:when test="@dttype='data' or @maywrite='false'">
        <a href="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',number)'))}">
          <xsl:call-template name="prompt_do_download"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="not(upload)">
            <input type="hidden" name="{@fieldname}" value="" dttype="binary" ftype="file" >
              <xsl:if test="@dtrequired='true' and @size &lt;= 0">
                <xsl:attribute name="dtrequired">true</xsl:attribute>
              </xsl:if>
            </input>
            <xsl:if test="@size &lt;= 0">
              <xsl:call-template name="prompt_no_file"/>
              <br/>
            </xsl:if>
            <xsl:if test="@size &gt; 0">
              <a href="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',number)'))}">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <xsl:call-template name="prompt_do_download"/> (<xsl:value-of select="round(@size div 100) div 10"/> K)
              </a>
              <br/>
            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <input type="hidden" name="{@fieldname}" value="YES" dttype="binary" ftype="file" >
              <xsl:if test="@dtrequired='true'">
                <xsl:attribute name="dtrequired">true</xsl:attribute>
              </xsl:if>
            </input>
            <xsl:call-template name="prompt_uploaded"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="upload/@name"/>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            (<xsl:value-of select="round((upload/@size) div 100) div 10"/> K)
            <br/>
            <!--
                target=_blank
                Does not seem to make much sense, because we don't do that for already saved documents either.
                But it seems to be that windows will always open e.g. PDF _in the browser_ otherwise (regardless
                of the setting for that in windows explorer).
            -->
            <a  href="file://{upload/path}">
              <xsl:call-template name="prompt_do_download"/>
            </a>
            <br/>
          </xsl:otherwise>
        </xsl:choose>
        <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
          <xsl:call-template name="prompt_do_upload"/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="ftype-imagedata">
    <span class="readonly">
      <img src="{node:saxonFunction($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0"/>
      <br/>
      <a href="{node:saxonFunction($cloud, string(@number), concat('servletpath', $cloudkey,')'))}" target="_new">
        <xsl:call-template name="prompt_image_full" />
      </a>
    </span>
  </xsl:template>

  <xsl:template name="ftype-filedata">
    <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
      <xsl:call-template name="prompt_do_upload"/>
    </a>
  </xsl:template>

  <xsl:template name="ftype-realposition">
    <xsl:call-template name="realposition"/>
  </xsl:template>

  <xsl:template name="ftype-radio">
     <xsl:for-each select="optionlist/option">
       <input type="radio" name="{../../@fieldname}" value="{@id}" id="{@id}">
         <xsl:apply-templates select="../../@*" />
         <xsl:if test="@selected='true'">
           <xsl:attribute name="checked">true</xsl:attribute>
         </xsl:if>
         <xsl:value-of select="."/>
       </input><br/>
     </xsl:for-each>
  </xsl:template>

  <xsl:template name="ftype-checkbox">
    <input type="hidden" name="{@fieldname}" value="{value}" fid="{@fid}" id="{@fieldname}" dttype="boolean">
      <xsl:apply-templates select="@dtrequired" />
      <xsl:apply-templates select="@dtdepends" />
      <xsl:apply-templates select="@dtunless" />
      <xsl:apply-templates select="@dtaction" />
      <xsl:apply-templates select="@dthasvalues" />
    </input>
    <!-- onChange event is needed to update the hidden input element when the checkbox changes -->
    <input type="checkbox" name="cb_{@fieldname}" id="cb_{@fieldname}" value="1"
      onChange="var hid = document.getElementById(this.getAttribute('id').substr(3)); if (this.checked) hid.value = '1'; else hid.value = '0';">
      <xsl:if test="value=1">
        <xsl:attribute name="checked">true</xsl:attribute>
      </xsl:if>
    </input>
  </xsl:template>

  <xsl:template name="ftype-unknown">
    <xsl:call-template name="ftype-other"/>
  </xsl:template>

  <xsl:template name="ftype-other">
    <xsl:if test="@maywrite!='false'">
      <xsl:apply-templates select="value" mode="inputline"/>
    </xsl:if>
    <xsl:if test="@maywrite='false'">
      <span class="readonly">
        <xsl:apply-templates select="value" mode="line"/>
      </span>
    </xsl:if>
  </xsl:template>

  <!--
    Prints values with 'ftype=data'.
    truncate size
  -->
  <xsl:template match="value" mode="line">
    <xsl:param name="val" select="."/>
    <span>
      <xsl:call-template name="writeCurrentField">
        <xsl:with-param name="val" select="$val"/>
      </xsl:call-template>
    </span>
  </xsl:template>

  <xsl:template match="value" mode="inputline">
    <xsl:param name="val" select="."/>
    <input
        class="input mm_validate mm_f_{../@name} mm_n_{../@number} mm_nm_{../@nodemanager}"
        type="text" name="{../@fieldname}" value="{$val}">
      <xsl:if test="../@dtmaxlength">
        <xsl:attribute name="maxlength"><xsl:value-of select="../@dtmaxlength" /></xsl:attribute>
        <xsl:choose>
          <xsl:when test="../@dtmaxlength &lt; 80">
            <xsl:attribute name="size"><xsl:value-of select="../@dtmaxlength" /></xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="size">80</xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
      <!-- this produces _invalid_ HTML -->
      <xsl:apply-templates select="../@*"/>
    </input>
  </xsl:template>

  <xsl:template name="realposition">
    <span style="width:128">
      <input
          class="input mm_validate mm_f_{../@name} mm_n_{../@number} mm_nm_{../@nodemanager}"
          type="text" name="{@fieldname}" value="{value}">
        <xsl:apply-templates select="@*"/>
      </input>
      <input type="button" value="get" onClick="document.forms['form'].{@fieldname}.value = document.embeddedplayer.GetPosition();"/>
    </span>
  </xsl:template>

  <!-- On default.  All attributes must be copied -->
  <xsl:template match="@*">
    <xsl:variable name="attributeName" select="name(.)"/>
    <xsl:attribute name="{$attributeName}"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!-- but not the name-attribute -->
  <xsl:template match="@name"/>

  <!-- nor the type attribute -->
  <xsl:template match="@type" />

  <!--
    What to do with 'lists'.
  -->
  <xsl:template match="list">
    <table class="listcontent">
      <tr>
        <td class="listprompt">
          <xsl:call-template name="listprompt"/>
        </td>
      </tr>
      <!-- List of items -->
      <tr class="itemcanvas">
        <td>
          <xsl:call-template name="listitems"/>
        </td>
      </tr>
      <tr>
        <!-- SEARCH input and button -->
        <td class="listsearch">
          <xsl:call-template name="listsearch"/>
        </td>
      </tr>
      <tr>
        <!-- NEW button -->
        <td class="listnewbuttons">
          <xsl:call-template name="listnewbuttons"/>
        </td>
      </tr>
    </table>
    <p>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </p>
  </xsl:template>

  <xsl:template name="listprompt">
    <span>
      <xsl:attribute name="title">
        <xsl:call-template name="i18n">
          <xsl:with-param name="nodes" select="description"/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:if test="@status='invalid'">
        <xsl:attribute name="class">notvalid</xsl:attribute>
      </xsl:if>
      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="title"/>
      </xsl:call-template>
    </span>
  </xsl:template>

  <xsl:template name="listnewbuttons">
    <xsl:if test="command[@name='add-item']">
      <!-- only if less then maxoccurs -->
      <xsl:if test="not(@maxoccurs) or (@maxoccurs = '*') or count(item) &lt; @maxoccurs">
        <!-- create action and startwizard command are present. Open the object into the start wizard -->
        <xsl:if test="command[@name='startwizard']">
          <xsl:for-each select="command[@name='startwizard']">
            <!-- The prompts.xsl adds this as a tooltip -->
            <!-- Moved prompt to the "prompt_add_wizard" template as a tooltip -->
            <xsl:choose>
              <xsl:when test="@wizardjsp">
                <a href="{$ew_context}{@wizardjsp}/?objectnumber=new&amp;origin={@origin}&amp;wizard={@wizardname}">
                  <xsl:call-template name="prompt_add_wizard" />
                </a>
              </xsl:when>
              <xsl:otherwise>
            <xsl:if test="@inline='true'">
              <a href="javascript:doStartWizard('{../@fid}','{../command[@name='add-item']/@value}','{@wizardname}','{@objectnumber}','{@origin}');">
                <xsl:call-template name="prompt_add_wizard"/>
              </a>
            </xsl:if>
            <xsl:if test="not(@inline='true')">
              <a href="{$popuppage}&amp;fid={../@fid}&amp;did={../command[@name='add-item']/@value}&amp;popupid={@wizardname}_{@objectnumber}&amp;wizard={@wizardname}&amp;objectnumber={@objectnumber}&amp;origin={@origin}" target="_blank">
                <xsl:call-template name="prompt_add_wizard"/>
              </a>
            </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:if>

        <!-- create action is present, but no startwizard command. Add the object into the wizard -->
        <xsl:if test="command[@name='insert']">
          <xsl:for-each select="command[@name='add-item']">
            <a href="javascript:doAddInline('{@cmd}');">
              <xsl:call-template name="prompt_new"/>
            </a>
          </xsl:for-each>
        </xsl:if>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template name="listsearch">
    <!-- if 'add-item' command and a search, then make a search util-box -->
    <xsl:if test="command[@name='add-item']">
      <xsl:for-each select="command[@name='search']">
        <table class="searchcontent">
          <tr>
            <xsl:if test="prompt">
              <td><xsl:call-template name="prompt"/></td>
            </xsl:if>
            <td>
              <xsl:call-template name="listsearch-age"/>
            </td>
            <td>
              <xsl:call-template name="listsearch-fields"/>
            </td>
            <td>
              <input type="text" name="searchterm_{../command[@name='add-item']/@cmd}" value="{search-filter[1]/default}" class="search" onChange="var s = form['searchfields_{../command[@name='add-item']/@cmd}']; s[s.selectedIndex].setAttribute('default', this.value);"/>
              <!-- on change the current value is copied back to the option's default, because of that, the user's search is stored between different types of search-actions -->
            </td>
            <td>
              <span title="{$tooltip_search}" onClick="doSearch(this,'{../command[@name='add-item']/@cmd}','{$sessionkey}');">
                <xsl:for-each select="@*">
                  <xsl:copy/>
                </xsl:for-each>
                <xsl:attribute name="relationOriginNode"><xsl:value-of select="../@number" /></xsl:attribute>
                <xsl:choose>
                  <xsl:when test="../action[@type='add']/relation/@role">
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type='add']/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type='add']/relation/@createdir" /></xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type='create']/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type='create']/relation/@createdir" /></xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="prompt_search"/>
              </span>
            </td>
          </tr>
        </table>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="listsearch-age">
    <xsl:choose>
      <xsl:when test="$searchagetype='none'"></xsl:when>
      <!-- alway make searching on age possible -->
      <xsl:when test="$searchagetype='edit'">
        <xsl:call-template name="prompt_age"/>
        <input type="text" name="searchage_{../command[@name='add-item']/@cmd}" class="age" value="{@age}"/>
      </xsl:when>
      <xsl:otherwise>
        <select name="searchage_{../command[@name='add-item']/@cmd}" class="age">
          <xsl:call-template name="searchageoptions">
            <xsl:with-param name="selectedage" select="@age"/>
          </xsl:call-template>
        </select>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="listsearch-fields">
    <!-- other search-possibilities are given in the xml -->
    <select name="searchfields_{../command[@name='add-item']/@cmd}" class="searchpossibilities" onChange="form['searchterm_{../command[@name='add-item']/@cmd}'].value = this[this.selectedIndex].getAttribute('default'); form['searchtype_{../command[@name='add-item']/@cmd}'].value = this[this.selectedIndex].getAttribute('searchtype');">
      <xsl:for-each select="search-filter">
        <option value="{search-fields}" default="{default}" searchtype="{search-fields/@search-type}">
          <xsl:call-template name="i18n">
            <xsl:with-param name="nodes" select="name"/>
          </xsl:call-template>
        </option>
      </xsl:for-each>
      <xsl:call-template name="listsearch-fields-default"/>
    </select>
    <input type="hidden" name="searchtype_{../command[@name='add-item']/@cmd}" value="{search-filter[1]/search-fields/@searchtype}"/>
  </xsl:template>

  <xsl:template name="listsearch-fields-default">
    <!-- always search on owner and number too -->
    <option value="number" searchtype="equals">
      <xsl:call-template name="prompt_search_number"/>
    </option>
    <option value="owner" searchtype="like">
      <xsl:call-template name="prompt_search_owner"/>
    </option>
  </xsl:template>

  <xsl:template name="listitems">
    <xsl:if test="item">
      <table class="itemcontent">
        <xsl:apply-templates select="item"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="item">
    <xsl:call-template name="itemprefix"/>
    <!-- here we figure out how to draw this repeated item. It depends on the displaytype -->
    <xsl:choose>
       <xsl:when test="@displaytype='none'">
        <!-- don't show a thing -->
      </xsl:when>
      <xsl:when test="@displaytype='link'">
        <xsl:call-template name="item-link"/>
      </xsl:when>
      <xsl:when test="@displaytype='image'">
        <xsl:call-template name="item-image"/>
      </xsl:when>
      <xsl:when test="@displaytype='onefield'">
        <xsl:call-template name="item-onefield"/>
      </xsl:when>
      <xsl:when test="@displaytype='other'">
         <xsl:call-template name="item-other"/>
       </xsl:when>
      <xsl:when test="count(field|fieldset) &lt; 2">
        <xsl:call-template name="item-onefield"/>
      </xsl:when>
      <xsl:when test="field[@ftype='startwizard'] and count(field[@ftype!='startwizard']|fieldset) &lt; 3">
        <xsl:call-template name="item-onefield"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="item-other"/>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:for-each select="list">
      <tr class="listcanvas">
        <td colspan="3">
          <xsl:apply-templates select="."/>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:call-template name="itempostfix"/>
  </xsl:template>

  <xsl:template name="itemprefix"/>

  <xsl:template name="item-link">
    <!-- simply make the link, there must be a field name and number -->
    <tr>
      <td colspan="3">
        <a href="{$wizardpage}&amp;wizard={@wizardname}&amp;objectnumber={field[@name='number']/value}">- <xsl:value-of select="field[@name='title']/value"/>
        </a>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="item-image">
    <!-- first column is the image, show the fields in the second column -->
    <tr>
      <td colspan="3">
        <xsl:call-template name="itembuttons"/>
      </td>
    </tr>
    <tr style="vertical-align: top;">
      <td style="vertical-align: top; width: 1%;">
        <xsl:choose>
          <!-- handle field exists then it might be a new image -->
          <xsl:when test="field[@name = 'handle']">
            <xsl:if test="field[@name = 'handle' and @size &gt; 0]">
            <!-- the image -->
            <img src="{node:saxonFunction($cloud, string(field/@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <!-- the image -->
            <img src="{node:saxonFunction($cloud, string(field/@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td colspan="2">
        <xsl:call-template name="itemfields"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="item-onefield">
    <tr>
      <td colspan="2" class="itemfields">
        <xsl:call-template name="itemfields"/>
      </td>
      <td class="itembuttons">
        <xsl:call-template name="itembuttons"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="item-other">
    <!-- more fields -->
    <tr>
      <td colspan="3">
        <xsl:call-template name="itembuttons"/>
      </td>
    </tr>
    <tr>
      <td colspan="3">
        <xsl:call-template name="itemfields"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="itemfields">
    <table class="fieldscontent">
      <xsl:call-template name="itemfields-hover"/>

      <xsl:for-each select="fieldset|field">
        <!-- Don't show the startwizard field here. -->
        <xsl:if test="not(@ftype='startwizard')">
          <tr class="fieldcanvas">
            <xsl:call-template name="itemfields-click"/>
            <xsl:apply-templates select="."/>
          </tr>
        </xsl:if>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="itemfields-hover">
    <xsl:if test="field[@ftype='startwizard'] or fieldset/field[@ftype='startwizard']">
      <xsl:attribute name="class">itemrow</xsl:attribute>
      <xsl:attribute name="onMouseOver">objMouseOver(this);</xsl:attribute>
      <xsl:attribute name="onMouseOut">objMouseOut(this);</xsl:attribute>
      <xsl:for-each select="field[@ftype='startwizard']">
        <xsl:choose>
          <xsl:when test="@wizardjsp">
            <xsl:attribute name="href">
              <xsl:value-of select="$ew_context"/><xsl:value-of select="@wizardjsp"/>?wizard=<xsl:value-of select="@wizardname"/>&amp;did=<xsl:value-of select="../../command[@name='add-item']/@value"/>&amp;objectnumber=<xsl:value-of select="@objectnumber"/>&amp;origin=<xsl:value-of select="@origin"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
        <xsl:if test="not(@inline) or @inline='true'">
          <xsl:attribute name="href">
            <xsl:text disable-output-escaping="yes">javascript:doStartWizard('</xsl:text>
            <xsl:value-of select="../../@fid"/>
            <xsl:text disable-output-escaping="yes">','</xsl:text>
            <xsl:value-of select="../../command[@name='add-item']/@value"/>
            <xsl:text disable-output-escaping="yes">','</xsl:text>
            <xsl:value-of select="@wizardname"/>
            <xsl:text disable-output-escaping="yes">','</xsl:text>
            <xsl:value-of select="@objectnumber"/>
            <xsl:text disable-output-escaping="yes">','</xsl:text>
            <xsl:value-of select="@origin"/>
            <xsl:text disable-output-escaping="yes">');</xsl:text>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@inline='false'">
          <xsl:attribute name="href">
            <xsl:value-of select="$popuppage"/>&amp;fid=<xsl:value-of select="../../@fid"/>&amp;did=<xsl:value-of select="../../command[@name='add-item']/@value"/>&amp;popupid=<xsl:value-of select="@wizardname"/>_<xsl:value-of select="@objectnumber"/>&amp;wizard=<xsl:value-of select="@wizardname"/>&amp;objectnumber=<xsl:value-of select="@objectnumber"/>&amp;origin=<xsl:value-of select="@origin"/>
          </xsl:attribute>
          <xsl:attribute name="target">_blank</xsl:attribute>
        </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="itemfields-click">
    <!-- This little choose code makes it possible to click on the
            fieldcanvas to edit the list item. The fieldcanvas shouldn't
            be clickable when the field is editable.
    -->
    <xsl:choose>
      <xsl:when test="(../field[@ftype='startwizard'] or ../../field[@ftype='startwizard']) and @ftype='data'">
        <xsl:attribute name="onMouseDown">objClick(this.offsetParent);</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="class">fieldcanvas-edit</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="itempostfix"/>

  <xsl:template name="itembuttons">
    <table>
      <tr>
        <xsl:call-template name="mediaitembuttons"/>
        <xsl:call-template name="selectitembuttons"/>
        <xsl:call-template name="deleteitembuttons"/>
        <xsl:call-template name="positembuttons"/>
      </tr>
    </table>
  </xsl:template>

  <!-- Media-items must be overridable, because there is no good generic sollution forewards compatible yet -->
  <xsl:template name="mediaitembuttons">
    <td>
      <xsl:if test="@displaytype='audio'">
        <span	class="imgbutton"	title="{$tooltip_audio}">
          <a target="_blank" href="{node:saxonFunction($cloud, string(field/@objectnumber), 'url()')}">
            <xsl:call-template name="prompt_audio"/>
          </a>
        </span>
      </xsl:if>
      <xsl:if test="@displaytype='video'">
        <span	class="imgbutton"	title="{$tooltip_video}">
          <a target="_blank" href="{node:saxonFunction($cloud, string(field/@objectnumber), 'url()')}">
            <xsl:call-template name="prompt_video"/>
          </a>
        </span>
      </xsl:if>
    </td>
  </xsl:template>

  <xsl:template name="selectitembuttons">
    <!-- The item row is the trigger for startwizard at the moment. -->
    <td>
      <xsl:for-each select="field[@ftype='startwizard']">
        <span class="imgbutton">
        <xsl:choose>
          <xsl:when test="starts-with(@objectnumber,'n')">
            <a href="javascript:alert('{$please_save}')" class="inactive"><xsl:call-template name="prompt_edit_wizard"/></a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="@wizardjsp">
              <a href="{$ew_context}{@wizardjsp}/?objectnumber={@objectnumber}&amp;origin={@origin}&amp;wizard={@wizardname}">
                <xsl:call-template name="prompt_edit_wizard" />
              </a>
            </xsl:if>
            <xsl:if test="@inline='true'">
              <a href="javascript:doStartWizard('{../../@fid}','{../../command[@name='add-item']/@value}','{@wizardname}','{@objectnumber}','{@origin}');">
                <xsl:call-template name="prompt_edit_wizard"/>
              </a>
            </xsl:if>
            <xsl:if test="not(@inline='true')">
              <a href="{$popuppage}&amp;fid={../../@fid}&amp;did={../../command[@name='add-item']/@value}&amp;popupid={@wizardname}_{@objectnumber}&amp;wizard={@wizardname}&amp;objectnumber={@objectnumber}&amp;origin={@origin}" target="_blank">
                <xsl:call-template name="prompt_edit_wizard"/>
              </a>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        </span>
      </xsl:for-each>
    </td>
  </xsl:template>

  <xsl:template name="deleteitembuttons">
    <td>
      <xsl:if test="command[@name='delete-item']">
        <xsl:if test="@maydelete!='false'">
          <span class="imgbutton" title="{$tooltip_remove}" onclick="doRemove('{command[@name='delete-item']/@cmd}');">
            <xsl:call-template name="prompt_remove"/>
          </span>
        </xsl:if>
      </xsl:if>
    </td>
  </xsl:template>

  <!-- what must appear on the position of of a pos-item-button, if it should not appear itself -->
  <xsl:template name="emptypositembutton">
    <img src="{$mediadir}nix.gif" class="placeholder" border="0" width="20" />
  </xsl:template>

  <xsl:template name="positembuttons">
    <td>
      <xsl:choose>
        <xsl:when test="@maywrite='true' and command[@name='move-up']">
          <span class="imgbutton" title="{$tooltip_up}" onclick="doMoveUp('{command[@name='move-up']/@cmd}');">
            <xsl:call-template name="prompt_up"/>
          </span>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="emptypositembutton" />
        </xsl:otherwise>
      </xsl:choose>
    </td>
    <td>
      <xsl:choose>
        <xsl:when test="@maywrite='true' and command[@name='move-down']">
          <span class="imgbutton" title="{$tooltip_down}" onclick="doMoveDown('{command[@name='move-down']/@cmd}');">
            <xsl:call-template name="prompt_down"/>
          </span>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="emptypositembutton" />
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </xsl:template>

</xsl:stylesheet>
