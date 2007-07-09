<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:node="org.mmbase.bridge.util.xml.NodeFunction">
  <!--
  wizard.xsl

  @since  MMBase-1.6
  @author Kars Veling
  @author Michiel Meeuwissen
  @author Pierre van Rooden
  @author Martijn Houtman
  @version $Id: wizard.xsl,v 1.6 2005-05-10 16:19:59 michiel Exp $
  -->

  <xsl:import href="xsl/base.xsl"/>

  <xsl:variable name="defaultsearchage">7</xsl:variable>
  <xsl:variable name="searchagetype">combobox</xsl:variable>
  <xsl:param name="objectnumber"></xsl:param>

  <!-- ================================================================================
       The following things can be overriden to customize the appearance of wizard
       ================================================================================ -->

  <!-- It can be usefull to add a style, change the title -->
  <xsl:template name="style">
    <title>
      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="title"/>
      </xsl:call-template> - <xsl:call-template name="i18n"><xsl:with-param name="nodes" select="form[@id=/wizard/curform]/title"/>
      </xsl:call-template>
    </title>
    <link rel="stylesheet" type="text/css" href="../style/wizard.css"/>
    <xsl:call-template name="extrastyle"/>
<!-- see base.xsl -->
  </xsl:template>

  <!-- You can put stuff before and after then, or change the attributes of body itself. Don't forget to call 'bodycontent'
       and to add the on(un)load attributes.
       It is probably handier to override beforeform of formcontent.
       -->
  <xsl:template name="body">
    <body onload="doOnLoad_ew();" onunload="doOnUnLoad_ew();">
      <xsl:call-template name="bodycontent"/>
    </body>
  </xsl:template>

  <!-- The first row of the the body's table -->
  <xsl:template name="title">
    <tr>
      <th colspan="2">
        <span class="title">
          <nobr>
            <xsl:call-template name="i18n">
              <xsl:with-param name="nodes" select="title"/>
            </xsl:call-template>
          </nobr>
        </span>
        <span class="form">
          <nobr>
            <xsl:if test="$debug='true'">
              <a href="debug.jsp{$sessionid}?sessionkey={$sessionkey}&amp;popupid={$popupid}" target="_blank">[debug]</a>
              <br/>
            </xsl:if>
            <xsl:call-template name="i18n">
              <xsl:with-param name="nodes" select="form[@id=/wizard/curform]/title"/>
            </xsl:call-template>
          </nobr>
        </span>
      </th>
    </tr>
  </xsl:template>

  <!-- The second row of the the body's table -->
  <xsl:template name="subtitle">
    <tr class="subtitle">
      <th colspan="2">
        <xsl:call-template name="i18n">
          <xsl:with-param name="nodes" select="form[@id=/wizard/curform]/subtitle"/>
        </xsl:call-template>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      </th>
    </tr>
  </xsl:template>

  <!-- can be handy to add something to the top of your page -->
  <xsl:template name="beforeform"/>

  <!-- The body itself, probably no need overriding this
       Can use beforeform, and formcontent
       -->
  <xsl:template name="bodycontent">
    <xsl:call-template name="beforeform"/>
    <xsl:call-template name="bodyform"/>
  </xsl:template>


  <!--
       The form-content is the lay-out of the page. You can make this different, but don't forget to add the elements present in this one,
       especially /*/steps-validator and form[..]
       -->
  <xsl:template name="formcontent">
    <table class="body">
      <xsl:call-template name="title"/>
    <!-- see above -->
      <xsl:call-template name="subtitle"/>
 <!-- see above -->
      <xsl:apply-templates select="form[@id=/wizard/curform]"/>
<!-- produces <tr />'s -->
      <xsl:apply-templates select="/*/steps-validator"/>
       <!-- produces <tr />'s -->
    </table>
  </xsl:template>


  <!--
       Besides the form, there are the 'steps, navigation and cancel/safe buttons
       -->
  <xsl:template match="steps-validator">
    <!-- when multiple steps, otherwise do nothing -->
    <xsl:if test="count(step) &gt; 1">
      <xsl:call-template name="steps"/>
      <xsl:call-template name="nav-buttons"/>
    </xsl:if>
    <xsl:call-template name="buttons"/>
  </xsl:template>



  <!-- Buttons are (always) called in the steps-validator
       A <tr> is expected.
       -->
  <xsl:template name="buttons">
    <!-- our buttons -->
    <tr>
      <td colspan="2" align="center">
        <hr color="#005A4A" size="1" noshade="true"/>
        <p>
          <!-- cancel -->
          <xsl:call-template name="cancelbutton"/>
          <xsl:text disable-output-escaping="yes">&amp;nbsp;-&amp;nbsp;</xsl:text>
          <!-- commit  -->
          <xsl:call-template name="savebutton"/>
          <xsl:text disable-output-escaping="yes">&amp;nbsp;-&amp;nbsp;</xsl:text>
          <!-- Saveonly  -->
          <xsl:call-template name="saveonlybutton"/>
        </p>
        <hr color="#005A4A" size="1" noshade="true"/>
      </td>
    </tr>
  </xsl:template>


  <!-- The navigation buttons are only created (in steps-validator) if there is more than one step -->
  <xsl:template name="nav-buttons">
    <tr>
      <td colspan="2" align="center">
        <xsl:call-template name="previousbutton"/>
        <!-- previous -->
        <xsl:call-template name="nextbutton"/>
        <!-- next -->
      </td>
    </tr>
  </xsl:template>

  <!-- The steps buttons are only created (in steps-validator) if there is more than one step -->
  <xsl:template name="steps">
    <tr>
      <td colspan="2" align="center">
        <hr color="#005A4A" size="1" noshade="true"/>
      </td>
    </tr>
    <tr>
      <td colspan="2" class="steps">
        <!-- all steps -->
        <p class="step">
          <xsl:for-each select="step">
            <xsl:call-template name="steptemplate"/>
            <!-- steptemplate calls 'step', and surrounds it with some information about if the step is valid/current  -->
            <br/>
          </xsl:for-each>
        </p>
      </td>
    </tr>
  </xsl:template>

  <!-- The appearance of one 'step' button -->
  <xsl:template name="step">
    <a>
      <xsl:call-template name="stepaattributes"/>
      <xsl:call-template name="prompt_step"/>
    </a>
    <xsl:call-template name="i18n">
      <xsl:with-param name="nodes" select="/*/form[@id=current()/@form-schema]/title"/>
    </xsl:call-template>
  </xsl:template>


  <!-- Media-items must be overridable, because there is no good generic sollution forewards compatible yet -->
  <xsl:template name="mediaitembuttons">
    <xsl:if test="@displaytype='audio'">
      <a href="{$ew_context}/rastreams.db?{field/@number}" title="{$tooltip_audio}">
        <xsl:call-template name="prompt_audio"/>
      </a>
    </xsl:if>
    <xsl:if test="@displaytype='video'">
      <a href="{$ew_context}/rmstreams.db?{field/@number}" title="{$tooltip_video}">
        <xsl:call-template name="prompt_video"/>
      </a>
    </xsl:if>
  </xsl:template>


  <!-- ================================================================================
       The following is functionality. You probably don't want to override it.
       ================================================================================ -->


  <xsl:template name="javascript">
    <script language="javascript" src="{$javascriptdir}tools.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script language="javascript" src="{$javascriptdir}validator.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script language="javascript" src="{$javascriptdir}editwizard.jsp{$sessionid}?referrer={$referrer}&amp;language={$language}">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script language="javascript" src="../htmlarea/htmlarea.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script language="javascript" src="../htmlarea/lang/en.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script language="javascript" src="../htmlarea/dialog.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <style type="text/css">@import url(../htmlarea/htmlarea.css);</style>
    <script language="javascript" src="../htmlarea/my-htmlarea.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
    <script language="javascript">
      <xsl:text disable-output-escaping="yes">
        <![CDATA[<!--
          window.history.forward(1);
        ]]></xsl:text>

      // Store htmlarea names.
      var htmlAreas = new Array();
      <xsl:for-each select="//form[@id=/wizard/curform]//*[@ftype='html' and @maywrite!='false']">
        htmlAreas[htmlAreas.length] = '<xsl:value-of select="@fieldname"/>';
      </xsl:for-each>

      <xsl:text disable-output-escaping="yes">
        <![CDATA[
          -->
        ]]></xsl:text>
    </script>
    <xsl:call-template name="extrajavascript"/>
  </xsl:template>



  <!-- Every wizard is based on one 'form', with a bunch of attributes and a few hidden entries.
       Those are set here -->
  <xsl:template name="bodyform">
    <form name="form" method="post" action="{$formwizardpage}" id="{/wizard/curform}" message_pattern="{$message_pattern}" message_required="{$message_required}" message_minlength="{$message_minlength}" message_maxlength="{$message_maxlength}" message_min="{$message_min}" message_max="{$message_max}" message_mindate="{$message_mindate}" message_maxdate="{$message_maxdate}" message_dateformat="{$message_dateformat}" message_thisnotvalid="{$message_thisnotvalid}" message_notvalid="{$message_notvalid}" message_listtooshort="{$message_listtooshort}" invalidlist="{/wizard/form[@invalidlist]/@invalidlist}" filter_required="{$filter_required}">
      <input type="hidden" name="curform" value="{/wizard/curform}"/>
      <input type="hidden" name="cmd" value="" id="hiddencmdfield"/>
      <xsl:call-template name="formwizardargs"/>
      <xsl:call-template name="formcontent"/>
    </form>
  </xsl:template>


  <!-- On default.  All attributes must be copied -->
  <xsl:template match="@*">
    <!-- THIS GOES WRONG IN RESIN -->
    <xsl:copy>
      <xsl:value-of select="."/>
    </xsl:copy>
  </xsl:template>

  <!-- but not the name-attribute? -->
  <xsl:template match="@name"></xsl:template>

  <!-- Wizard is the entry template-->
  <xsl:template match="/">
    <xsl:apply-templates select="wizard"/>
  </xsl:template>

  <!-- we produce HTML, this could be overriden, but you can override bodycontent as well -->
  <xsl:template match="wizard">
    <html>
      <head>
        <xsl:call-template name="style"/>
        <xsl:call-template name="javascript"/>
      </head>
      <xsl:call-template name="body"/>
    </html>
  </xsl:template>


  <!-- -->
  <xsl:template match="form">
    <xsl:for-each select="fieldset|field|list">
      <tr>
        <xsl:apply-templates select="."/>
      </tr>
    </xsl:for-each>
  </xsl:template>

  <!-- produces 2 or 3 td's -->
  <xsl:template match="field">
    <xsl:param name="colspan">1</xsl:param>
    <td class="fieldprompt">
      <xsl:call-template name="prompt"/>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <td class="field" colspan="{$colspan}">
      <xsl:call-template name="fieldintern"/>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
  </xsl:template>

  <!-- produces 2 or 3 td's -->
  <xsl:template match="fieldset">
    <xsl:param name="colspan">1</xsl:param>
    <td class="fieldprompt">
      <xsl:call-template name="prompt"/>
    </td>
    <td class="field" colspan="{$colspan}">
      <table>
        <tr>
          <xsl:for-each select="field">
            <td>
              <nobr>
                <xsl:call-template name="fieldintern"/>
                <xsl:text disable-output-escaping="yes"></xsl:text>
              </nobr>
            </td>
          </xsl:for-each>
        </tr>
      </table>
    </td>
  </xsl:template>




  <!--
       Prefix and postfix are subtags of 'field', and are put respectively before and after the presentation of the field.
       Useful in fieldsets.
       -->
  <xsl:template match="prefix|postfix">
    <xsl:value-of select="."/>
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

  <xsl:template match="value" mode="line">
    <xsl:param name="val" select="."/>
    <span>
      <xsl:value-of select="$val" disable-output-escaping="yes"/>
    </span>
  </xsl:template>

  <xsl:template match="value" mode="inputline">
    <xsl:param name="val" select="."/>
    <input type="text" size="80" name="{../@fieldname}" value="{$val}" class="input" onkeyup="validate_validator(event);" onblur="validate_validator(event);">

      <xsl:apply-templates select="../@*"/>
    </input>
  </xsl:template>

  <!-- used to convert &amp; occurrences in textareas, so it is possible to edit xml  -->
  <xsl:template name="replace-string">
      <xsl:param name="text"/>
      <xsl:param name="replace"/>
      <xsl:param name="with"/>

      <xsl:choose>
          <xsl:when test="string-length($replace) = 0">
              <xsl:value-of disable-output-escaping="yes" select="$text"/>
          </xsl:when>
          <xsl:when test="contains($text, $replace)">

              <xsl:variable name="before" select="substring-before($text, $replace)"/>
              <xsl:variable name="after" select="substring-after($text, $replace)"/>

              <xsl:value-of disable-output-escaping="yes" select="$before"/>
              <xsl:value-of disable-output-escaping="yes" select="$with"/>
              <xsl:call-template name="replace-string">
                  <xsl:with-param name="text" select="$after"/>
                  <xsl:with-param name="replace" select="$replace"/>
                  <xsl:with-param name="with" select="$with"/>
              </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
              <xsl:value-of disable-output-escaping="yes" select="$text"/>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <!-- ================================================================================
      fieldintern is called from fields and fieldsets

      -->
  <xsl:template name="fieldintern">
    <xsl:apply-templates select="prefix"/>
    <xsl:choose>
      <xsl:when test="@ftype='function'">
        <xsl:if test="not(string(number(@number)) = 'NaN')">
          <xsl:apply-templates select="value" mode="line">
            <xsl:with-param name="val">
              <xsl:value-of select="node:function($cloud, string(@number), string(value))" disable-output-escaping="yes"/>
            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:if>
      </xsl:when>
      <xsl:when test="@ftype='data'">
        <xsl:apply-templates select="value" mode="line"/>
      </xsl:when>
      <xsl:when test="@ftype='line'">
        <xsl:if test="@maywrite!='false'">
          <xsl:apply-templates select="value" mode="inputline"/>
        </xsl:if>
        <xsl:if test="@maywrite='false'">
          <span class="readonly">
            <xsl:apply-templates select="value" mode="value"/>
          </span>
        </xsl:if>
      </xsl:when>
      <xsl:when test="@ftype='text' or @ftype='html'">
        <xsl:if test="@maywrite!='false'">
          <span>
            <xsl:text disable-output-escaping="yes">&lt;textarea
              name="</xsl:text>
            <xsl:value-of select="@fieldname"/>
            <xsl:text>"
              id="</xsl:text>
            <xsl:value-of select="@fieldname"/>
            <xsl:text>"
              dttype="</xsl:text>
            <xsl:value-of select="@dttype"/>
            <xsl:text>"
              ftype="</xsl:text>
            <xsl:value-of select="@ftype"/>
            <xsl:text>"
              dtminlength="</xsl:text>
            <xsl:value-of select="@dtminlength"/>
            <xsl:text>"
              dtmaxlength="</xsl:text>
            <xsl:value-of select="@dtmaxlength"/>
            <xsl:text>"
              class="input" wrap="soft"
                onkeyup="validate_validator(event);"
              onblur="validate_validator(event);" </xsl:text>
            <xsl:choose>
              <xsl:when test="@cols">
                <xsl:text>cols="</xsl:text>
                <xsl:value-of select="@cols"/>
                <xsl:text>"</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>cols="80"</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
              <xsl:when test="@rows">
                <xsl:text>rows="</xsl:text>
                <xsl:value-of select="@rows"/>
                <xsl:text>"</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>rows="10"</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="@*"/>
            <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
            <xsl:choose>
              <xsl:when test="@ftype='text'">
                <xsl:call-template name="replace-string">
                  <xsl:with-param name="text">
                    <xsl:value-of disable-output-escaping="yes" select="value"/>
                  </xsl:with-param>
                  <xsl:with-param name="replace" select="'&amp;'"/>
                  <xsl:with-param name="with" select="'&amp;amp;'"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of disable-output-escaping="yes" select="value"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/textarea&gt;</xsl:text>
            <xsl:apply-templates select="postfix"/>
          </span>
        </xsl:if>
        <xsl:if test="@maywrite='false'">
          <span class="readonly">
            <xsl:value-of select="value"/>
          </span>
        </xsl:if>
      </xsl:when>
      <xsl:when test="@ftype='relation' or @ftype='enum'">
        <select name="{@fieldname}" class="input" onchange="validate_validator(event);" onblur="validate_validator(event);">
          <xsl:apply-templates select="@*"/>
          <xsl:choose>
            <xsl:when test="optionlist/option[@selected='true']"></xsl:when>
            <xsl:when test="@dtrequired='true'"></xsl:when>
            <xsl:otherwise>
              <option value="-">
                <xsl:call-template name="prompt_select"/>
              </option>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:for-each select="optionlist/option">
            <option value="{@id}">
              <xsl:if test="@selected='true'">
                <xsl:attribute name="selected">true</xsl:attribute>
              </xsl:if>
              <xsl:value-of select="."/>
            </option>
          </xsl:for-each>
        </select>
      </xsl:when>
      <xsl:when test="@ftype='enumdata'">
        <xsl:if test="optionlist/option[@id=current()/value]">
          <xsl:apply-templates select="value" mode="line">
            <xsl:with-param name="val">
              <xsl:value-of select="optionlist/option[@id=current()/value]"/>
            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:if>
      </xsl:when>
      <xsl:when test="(@ftype='datetime') or (@ftype='date') or (@ftype='time')">
        <div>
          <input type="hidden" name="{@fieldname}" value="{value}" id="{@fieldname}">
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="type">hidden</xsl:attribute>
          </input>

          <xsl:if test="(@ftype='datetime') or (@ftype='date')">
            <select name="internal_{@fieldname}_day" super="{@fieldname}" onChange="validate_validator(event);">
              <option value="1">1</option>
              <option value="2">2</option>
              <option value="3">3</option>
              <option value="4">4</option>
              <option value="5">5</option>
              <option value="6">6</option>
              <option value="7">7</option>
              <option value="8">8</option>
              <option value="9">9</option>
              <option value="10">10</option>
              <option value="11">11</option>
              <option value="12">12</option>
              <option value="13">13</option>
              <option value="14">14</option>
              <option value="15">15</option>
              <option value="16">16</option>
              <option value="17">17</option>
              <option value="18">18</option>
              <option value="19">19</option>
              <option value="20">20</option>
              <option value="21">21</option>
              <option value="22">22</option>
              <option value="23">23</option>
              <option value="24">24</option>
              <option value="25">25</option>
              <option value="26">26</option>
              <option value="27">27</option>
              <option value="28">28</option>
              <option value="29">29</option>
              <option value="30">30</option>
              <option value="31">31</option>
            </select>
            <xsl:value-of select="$time_daymonth"/>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <select name="internal_{@fieldname}_month" super="{@fieldname}" onchange="validate_validator(event);">
              <xsl:call-template name="optionlist_months"/>
            </select>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <input name="internal_{@fieldname}_year" super="{@fieldname}" type="text" value="" size="5" maxlength="4" onChange="validate_validator(event);"/>
          </xsl:if>

          <xsl:if test="@ftype='datetime'">
            <span class="time_at">
              <xsl:value-of select="$time_at"/>
            </span>
          </xsl:if>

          <xsl:if test="(@ftype='datetime') or (@ftype='time')">
            <select name="internal_{@fieldname}_hours" super="{@fieldname}" onChange="validate_validator(event);">
              <option value="0">0</option>
              <option value="1">1</option>
              <option value="2">2</option>
              <option value="3">3</option>
              <option value="4">4</option>
              <option value="5">5</option>
              <option value="6">6</option>
              <option value="7">7</option>
              <option value="8">8</option>
              <option value="9">9</option>
              <option value="10">10</option>
              <option value="11">11</option>
              <option value="12">12</option>
              <option value="13">13</option>
              <option value="14">14</option>
              <option value="15">15</option>
              <option value="16">16</option>
              <option value="17">17</option>
              <option value="18">18</option>
              <option value="19">19</option>
              <option value="20">20</option>
              <option value="21">21</option>
              <option value="22">22</option>
              <option value="23">23</option>
            </select>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>:<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <select name="internal_{@fieldname}_minutes" super="{@fieldname}" onchange="validate_validator(event);" onblur="validate_validator(event);">
              <option value="0">00</option>
              <option value="1">01</option>
              <option value="2">02</option>
              <option value="3">03</option>
              <option value="4">04</option>
              <option value="5">05</option>
              <option value="6">06</option>
              <option value="7">07</option>
              <option value="8">08</option>
              <option value="9">09</option>
              <option value="10">10</option>
              <option value="11">11</option>
              <option value="12">12</option>
              <option value="13">13</option>
              <option value="14">14</option>
              <option value="15">15</option>
              <option value="16">16</option>
              <option value="17">17</option>
              <option value="18">18</option>
              <option value="19">19</option>
              <option value="20">20</option>
              <option value="21">21</option>
              <option value="22">22</option>
              <option value="23">23</option>
              <option value="24">24</option>
              <option value="25">25</option>
              <option value="26">26</option>
              <option value="27">27</option>
              <option value="28">28</option>
              <option value="29">29</option>
              <option value="30">30</option>
              <option value="31">31</option>
              <option value="32">32</option>
              <option value="33">33</option>
              <option value="34">34</option>
              <option value="35">35</option>
              <option value="36">36</option>
              <option value="37">37</option>
              <option value="38">38</option>
              <option value="39">39</option>
              <option value="40">40</option>
              <option value="41">41</option>
              <option value="42">42</option>
              <option value="43">43</option>
              <option value="44">44</option>
              <option value="45">45</option>
              <option value="46">46</option>
              <option value="47">47</option>
              <option value="48">48</option>
              <option value="49">49</option>
              <option value="50">50</option>
              <option value="51">51</option>
              <option value="52">52</option>
              <option value="53">53</option>
              <option value="54">54</option>
              <option value="55">55</option>
              <option value="56">56</option>
              <option value="57">57</option>
              <option value="58">58</option>
              <option value="59">59</option>
            </select>
          </xsl:if>
        </div>
      </xsl:when>
      <xsl:when test="@ftype='startwizard'">
        <xsl:if test="@maywrite!='false'">
          <nobr>
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
          </nobr>
        </xsl:if>
      </xsl:when>
      <xsl:when test="@ftype='image'">
        <xsl:if test="@maywrite!='false'">
          <xsl:choose>
            <xsl:when test="@dttype='binary' and not(upload)">
              <div class="imageupload">
                <div>
                  <input type="hidden" name="{@fieldname}" value="YES"/>
                  <img src="{node:function($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0"/>
                  <br/>
                  <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
                    <xsl:call-template name="prompt_image_upload"/>
                  </a>
                </div>
              </div>
            </xsl:when>
            <xsl:when test="@dttype='binary' and upload">
              <div class="imageupload">
                <input type="hidden" name="{@fieldname}" value="YES"/>
                <img src="{upload/path}" hspace="0" vspace="0" border="0" width="128" height="128"/>
                <br/>
                <span>
                  <xsl:value-of select="upload/@name"/>
                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> (<xsl:value-of select="round((upload/@size) div 100) div 10"/>K)
                  </span>
                <br/>
                <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
                  <xsl:call-template name="prompt_image_upload"/>
                </a>
              </div>
            </xsl:when>
            <xsl:otherwise>
              <span>
                <img src="{node:function($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
                <br/>
              </span>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
        <xsl:if test="@maywrite='false'">
          <span class="readonly">
            <img src="{node:function($cloud, string(@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0"/>
          </span>
        </xsl:if>
      </xsl:when>
      <xsl:when test="@ftype='file'">
        <xsl:choose>
          <xsl:when test="@dttype='data' or @maywrite='false'">
            <a target="_blank" href="{node:function($cloud, string(@number), concat('servletpath(', $cloudkey, ',number)'))}">
              <xsl:call-template name="prompt_do_download"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <nobr>
              <input type="hidden" name="{@fieldname}" value="YES"/>
              <xsl:choose>
                <xsl:when test="not(upload)">
                  <xsl:call-template name="prompt_no_file"/>
                  <br/>
                  <a target="_blank" href="{node:function($cloud, string(@number), concat('servletpath(', $cloudkey, ',number)'))}">
                    <xsl:call-template name="prompt_do_download"/>
                  </a>
                  <br/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="prompt_uploaded"/>
                  <xsl:value-of select="upload/@name"/>
                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>(<xsl:value-of select="round((upload/@size) div 100) div 10"/>K)<br/>
                </xsl:otherwise>
              </xsl:choose>
              <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}" onclick="return doStartUpload(this);">
                <xsl:call-template name="prompt_do_upload"/>
              </a>
            </nobr>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>

      <xsl:when test="@ftype='realposition'">
        <xsl:call-template name="realposition"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="@maywrite!='false'">
          <xsl:apply-templates select="value" mode="inputline"/>
        </xsl:if>
        <xsl:if test="@maywrite='false'">
          <span class="readonly">
            <xsl:apply-templates select="value" mode="value"/>
          </span>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="postfix"/>
  </xsl:template>

  <xsl:template name="realposition">
    <span style="width:128">
      <nobr>
        <input type="text" name="{@fieldname}" value="{value}" class="input" onkeyaup="validate_validator(event);" onblur="validate_validator(event);">
          <xsl:apply-templates select="@*"/>
        </input>
        <input type="button" value="get" onClick="document.forms['form'].{@fieldname}.value = document.embeddedplayer.GetPosition();"/>
      </nobr>
    </span>
  </xsl:template>

  <!-- produces the <tr> (two columns) for a bunch of fields (used in item) -->
  <xsl:template name="itemfields">
    <xsl:for-each select="field|fieldset">
      <tr>
        <xsl:apply-templates select="."/>
      </tr>
    </xsl:for-each>
  </xsl:template>

  <!--
    ================================================================================
    item, produces 3 column-tr's ( prompt (or image) -   item itself  -  (buttons)
  -->
  <xsl:template match="item">
      <!-- here we figure out how to draw this repeated item. It depends on the displaytype -->
    <xsl:choose>
      <xsl:when test="@displaytype='link'">
        <!-- simply make the link, there must be a field name and number -->
        <tr>
          <td colspan="3">
            <a href="{$wizardpage}&amp;wizard={@wizardname}&amp;objectnumber={field[@name='number']/value}">- <xsl:value-of select="field[@name='title']/value"/>
            </a>
          </td>
        </tr>
      </xsl:when>
      <xsl:when test="@displaytype='image'">
        <!-- first column is the image, show the fields in the second column -->
        <tr>
          <td colspan="3">
            <xsl:call-template name="itembuttons"/>
          </td>
        </tr>
        <tr>
          <td>
              <!-- the image -->
            <img src="{node:function($cloud, string(field/@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
          </td>
          <td colspan="2">
            <xsl:if test="field|fieldset">
              <table>
                <xsl:call-template name="itemfields"/>
              </table>
            </xsl:if>
          </td>
        </tr>
      </xsl:when>
      <xsl:when test="count(field|fieldset) &lt; 2">
        <!-- only one field ?, itembutton right from the item. -->
        <tr>
          <xsl:for-each select="field|fieldset">
            <xsl:apply-templates select="."/>
            <!-- two td's -->
          </xsl:for-each>
          <xsl:if test="not(field|fieldset)">
            <td colspan="2"> </td>
          </xsl:if>
          <td align="right" valign="top">
            <nobr>
              <xsl:call-template name="itembuttons"/>
            </nobr>
          </td>
        </tr>
      </xsl:when>
      <xsl:otherwise>
      <!-- more fields -->
        <tr>
          <td colspan="3">
            <xsl:call-template name="itembuttons"/>
          </td>
        </tr>
          <!-- draw all fields, if there are any for this item -->
        <xsl:for-each select="field|fieldset">
          <tbody class="fieldset">
            <tr>
              <xsl:choose>
                <xsl:when test="position()=1">
                  <xsl:attribute name="class">first</xsl:attribute>
                </xsl:when>
                <xsl:when test="position()=last()">
                  <xsl:attribute name="class">last</xsl:attribute>
                </xsl:when>
              </xsl:choose>
              <xsl:apply-templates select=".">
                <xsl:with-param name="colspan">2</xsl:with-param>
              </xsl:apply-templates>
            </tr>
          </tbody>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:for-each select="list">
      <tr>
        <td class="fieldprompt">&#160;</td>
        <xsl:apply-templates select="."/>
      </tr>
    </xsl:for-each>
  </xsl:template>
  <!-- item -->

  <!-- produces a bunch of links -->
  <xsl:template name="itembuttons">
    <xsl:call-template name="mediaitembuttons"/>
    <xsl:if test="command[@name='delete-item']">
      <xsl:if test="@maydelete!='false'">
        <span class="imagebutton" title="{$tooltip_remove}" onclick="doSendCommand('{command[@name='delete-item']/@cmd}');">
          <xsl:call-template name="prompt_remove"/>
        </span>
      </xsl:if>
    </xsl:if>
    <xsl:if test="@maywrite!='false'">
      <xsl:if test="command[@name='move-up']">
        <span class="imagebutton" title="{$tooltip_up}" onclick="doSendCommand('{command[@name='move-up']/@cmd}');">
          <xsl:call-template name="prompt_up"/>
        </span>
      </xsl:if>
      <xsl:if test="command[@name='move-down']">
        <span class="imagebutton" title="{$tooltip_down}" onclick="doSendCommand('{command[@name='move-down']/@cmd}');">
          <xsl:call-template name="prompt_down"/>
        </span>
      </xsl:if>
    </xsl:if>
  </xsl:template>


  <!-- The age search options of a search item -->
  <xsl:template name="searchoptions">
    <option value="0">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="'0'"/>
        <xsl:with-param name="pref" select="@age"/>
      </xsl:call-template>
      <xsl:call-template name="age_now"/>
    </option>
    <option value="1">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="'1'"/>
        <xsl:with-param name="pref" select="@age"/>
      </xsl:call-template>
      <xsl:call-template name="age_day"/>
    </option>
    <option value="7">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="'7'"/>
        <xsl:with-param name="pref" select="@age"/>
      </xsl:call-template>
      <xsl:call-template name="age_week"/>
    </option>
    <option value="31">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="'31'"/>
        <xsl:with-param name="pref" select="@age"/>
      </xsl:call-template>
      <xsl:call-template name="age_month"/>
    </option>
    <option value="365">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="'365'"/>
        <xsl:with-param name="pref" select="@age"/>
      </xsl:call-template>
      <xsl:call-template name="age_year"/>
    </option>
    <option value="-1">
      <xsl:call-template name="searchage">
        <xsl:with-param name="real" select="'-1'"/>
        <xsl:with-param name="pref" select="@age"/>
      </xsl:call-template>
      <xsl:call-template name="age_any"/>
    </option>
  </xsl:template>

  <!--
       What to do with 'lists'.
       @bad-constant: styles should be in the style-sheet, and are then configurable.
       -->
  <xsl:template match="list">
    <td colspan="2" class="listcanvas">
      <div title="{description}">
        <xsl:choose>
          <xsl:when test="@status='invalid'">
            <xsl:attribute name="class">notvalid</xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="class">subhead</xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <nobr>
          <xsl:call-template name="i18n">
            <xsl:with-param name="nodes" select="title"/>
          </xsl:call-template>
        </nobr>
      </div>
      <xsl:call-template name="listitems"/>
    </td>
  </xsl:template>
  <!-- list -->

  <xsl:template name="listitems">
      <!-- show the item's of the list like that -->
    <xsl:if test="item">
      <table class="itemlist">
        <!-- three columns -->
        <xsl:apply-templates select="item"/>
      </table>
    </xsl:if>

    <!-- if 'add-item' command and a search, then make a search util-box -->
    <xsl:if test="command[@name='add-item']">
      <xsl:for-each select="command[@name='search']">
        <table class="itemadd">
          <tr>
            <td>
              <xsl:call-template name="i18n">
                <xsl:with-param name="nodes" select="prompt"/>
              </xsl:call-template>
              <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              <nobr>
                <!-- the search-tools must not be seperated -->
                <!-- alway make searching on age possible -->
                <xsl:choose>
                  <xsl:when test="$searchagetype='edit'">
                    <xsl:call-template name="prompt_age"/>
                    <input type="text" name="searchage_{../command[@name='add-item']/@cmd}" class="age" value="{@age}"/>
                  </xsl:when>
                  <xsl:when test="$searchagetype='none'"></xsl:when>
                  <xsl:otherwise>
                    <select name="searchage_{../command[@name='add-item']/@cmd}" class="age">
                      <xsl:call-template name="searchoptions"/>
                    </select>
                  </xsl:otherwise>
                </xsl:choose>
                  <!-- other search-possibilities are given in the xml -->
                <select name="searchfields_{../command[@name='add-item']/@cmd}" class="searchpossibilities" onChange="form['searchterm_{../command[@name='add-item']/@cmd}'].value = this[this.selectedIndex].getAttribute('default'); form['searchtype_{../command[@name='add-item']/@cmd}'].value = this[this.selectedIndex].getAttribute('searchtype');">
                    >
                    <xsl:choose>
                      <xsl:when test="search-filter">
                        <xsl:for-each select="search-filter">
                          <option value="{search-fields}" default="{default}" searchtype="{search-fields/@search-type}">
                            <xsl:call-template name="i18n"><xsl:with-param name="nodes" select="name"/>
                          </xsl:call-template>
                        </option>
                      </xsl:for-each>
                    </xsl:when>
                      <!-- if nothing, then search on title -->
                    <xsl:otherwise>
                      <option value="title">
                        <xsl:call-template name="prompt_search_title"/>
                      </option>
                    </xsl:otherwise>
                  </xsl:choose>
                    <!-- always search on owner and number too -->
                  <option value="number" searchtype="equals">
                    <xsl:call-template name="prompt_search_number"/>
                  </option>
                  <option value="owner" searchtype="like">
                    <xsl:call-template name="prompt_search_owner"/>
                  </option>
                </select>
                <input type="hidden" name="searchtype_{../command[@name='add-item']/@cmd}" value="{search-filter[1]/search-fields/@searchtype}"/>
                <input type="text" name="searchterm_{../command[@name='add-item']/@cmd}" value="{search-filter[1]/default}" class="search" onChange="var s = form['searchfields_{../command[@name='add-item']/@cmd}']; s[s.selectedIndex].setAttribute('default', this.value);"/>
                    <!-- on change the current value is copied back to the option's default, because of that, the user's search is stored between different types of search-actions -->

                <span title="{$tooltip_search}" class="imagebutton" onClick="doSearch(this,'{../command[@name='add-item']/@cmd}','{$sessionkey}');">
                  <xsl:for-each select="@*">
                    <xsl:copy/>
                  </xsl:for-each>
                  <xsl:call-template name="prompt_search"/>
                </span>
              </nobr>
            </td>
          </tr>
        </table>
      </xsl:for-each>
    </xsl:if>
    <!-- if add-item -->


      <!--
           Create the '*' (add) buttons.
           -->
    <xsl:for-each select="command[@name='add-item']">
      <xsl:if test="not(../command[@name='startwizard'] and not(../command[@name='search' or @name='insert']))">
          <!-- if there is a start-wizard command and not 'search' or 'insert' command then do not add an extra 'add' button (will be added by 'startwizard' -->
        <table class="itemadd">
          <xsl:for-each select="../command[@name='search' or @name='insert']">
            <xsl:choose>
              <xsl:when test="@name='search'">
                <xsl:attribute name="style">display:inline; visibility:hidden; position:absolute; top:0; left:0;</xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="style">display:inline;</xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <tr>
            <td>
              <nobr>
                <span class="imagebutton" onclick="doSendCommand('{@cmd}');">
                  <xsl:call-template name="prompt_new"/>
                </span>
              </nobr>
            </td>
          </tr>
        </table>
      </xsl:if>
    </xsl:for-each>

    <!--
         Create the add-buttons for the startwizard commands.
         -->

    <!-- only if less then maxoccurs -->
    <xsl:if test="not(@maxoccurs) or (@maxoccurs = '*') or count(item) &lt; @maxoccurs">
      <xsl:if test="command[@name='startwizard']">

        <table class="itemadd">
          <xsl:for-each select="command[@name='startwizard']">
            <tr>
              <td>
                <xsl:if test="prompt">
                  <xsl:value-of select="prompt"/>
                </xsl:if>
              </td>
              <td>
                <nobr>
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
                </nobr>
              </td>
            </tr>
          </xsl:for-each>
        </table>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  <!-- listitems -->


  <xsl:template name="savebutton">
    <a href="javascript:doSave();" id="bottombutton-save" unselectable="on" titlesave="{$tooltip_save}" titlenosave="{$tooltip_no_save}">
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="$tooltip_save"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="$tooltip_no_save"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="step[@valid='false'][not(@form-schema=/wizard/curform)]">
          <xsl:attribute name="otherforms">invalid</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="otherforms">valid</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
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
        <xsl:attribute name="title">
          <xsl:value-of select="$tooltip_save_only"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="$tooltip_no_save"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="step[@valid='false'][not(@form-schema=/wizard/curform)]">
          <xsl:attribute name="otherforms">invalid</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="otherforms">valid</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="prompt_save_only"/>
    </a>
  </xsl:template>

  <xsl:template name="cancelbutton">
    <a href="javascript:doCancel();" id="bottombutton-cancel" class="bottombutton" title="{$tooltip_cancel}">
      <xsl:call-template name="prompt_cancel"/>
    </a>
  </xsl:template>

  <xsl:template name="previousbutton">
    <xsl:choose>
      <xsl:when test="/wizard/form[@id=/wizard/prevform]">
        <a class="step" align="left" width="100%" href="javascript:doGotoForm('{/wizard/prevform}')" title="{$tooltip_previous} '{/wizard/form[@id=/wizard/prevform]/title}'">
          <xsl:call-template name="prompt_previous"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <span class="step-disabled" align="left" width="100%" title="{$tooltip_no_previous}">
          <xsl:call-template name="prompt_previous"/>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="nextbutton">
    <xsl:choose>
      <xsl:when test="/wizard/form[@id=/wizard/nextform]">
        <a class="step" align="left" width="100%" href="javascript:doGotoForm('{/wizard/nextform}')" title="{$tooltip_next} '{/wizard/form[@id=/wizard/nextform]/title}'">
          <xsl:call-template name="prompt_next"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <span class="step-disabled" align="left" width="100%" title="{$tooltip_no_next}">
          <xsl:call-template name="prompt_next"/>
        </span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="stepaattributes">
    <xsl:attribute name="href">javascript:doGotoForm('<xsl:value-of select="@form-schema"/>');</xsl:attribute>
    <xsl:attribute name="titlevalid">
      <xsl:value-of select="$tooltip_valid"/>
    </xsl:attribute>
    <xsl:attribute name="id">step-<xsl:value-of select="@form-schema"/>
    </xsl:attribute>
    <xsl:attribute name="titlenotvalid">
      <xsl:value-of select="$tooltip_not_valid"/>
    </xsl:attribute>
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


  <xsl:template name="steptemplate">
    <xsl:variable name="schemaid" select="@form-schema"/>
    <span>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="@form-schema=/wizard/curform">current</xsl:when>
          <xsl:otherwise>other</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:call-template name="step"/>
    </span>
  </xsl:template>

  <xsl:template name="searchage">
    <xsl:param name="real"/>
    <xsl:param name="pref"/>
    <xsl:if test="($real=$pref) or (not($pref) and $defaultsearchage=$real)">
      <xsl:attribute name="selected">true</xsl:attribute>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
