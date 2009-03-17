<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <!-- Import original stylesheet -->
   <xsl:import href="templatesi18n:xsl/prompts-cmsc.xsl"/>
   <xsl:import href="ew:xsl/wizard.xsl"/>
<!-- <xsl:include href="wizard-simple.xsl"/> -->
   <xsl:include href="wizard-workflow.xsl"/>
   <xsl:include href="templatesi18n:xsl/prompts-newsletter.xsl"/>
   <!-- contains templates which can be implemented by customer projects -->
   <xsl:include href="wizard-custom.xsl"/>

<!-- PARAMETERS PASSED IN BY THE TRANSFORMER. 
     WE PASSED A MAP WITH KEY VALUE PAIRS TO THE TRANSFORMER
 -->

  <xsl:param name="READONLY">false</xsl:param>
  <!-- Reason why the wizardcontroller made the wizard readonly (currently these are NONE, RIGHTS and WORKFLOW) -->
  <xsl:param name="READONLY-REASON">NONE</xsl:param>

  <xsl:param name="WRITER">true</xsl:param>
  <xsl:param name="EDITOR">false</xsl:param>
  <xsl:param name="CHIEFEDITOR">false</xsl:param>
  <xsl:param name="WEBMASTER">false</xsl:param>

<!--
  END
-->



<!-- CMSC-446: fix for date picker, so it won't always popup when pressing enter in an input -->
  <xsl:template name="date-picker">
    <img class="calendar" src="{$mediadir}datepicker/calendar.gif" border="0" onclick="popUpCalendar(this, 'dd-mm-yyyy', - 205 , 5 , document.forms[0], 'internal_{@fieldname}',event);return false;"/>
  </xsl:template>
  


  <xsl:variable name="htmlareadir"><xsl:value-of select="$ew_context" />/mmbase/edit/wizard/xinha/</xsl:variable>

  <xsl:variable name="BodyOnLoad">doOnLoad_ew();start_validator();xinha_init();initPopCalendar();inits();</xsl:variable>

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
        //  -->
        ]]></xsl:text>
    </script>
  </xsl:template>

  <xsl:template name="style">  
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/layout/wizard.css"/>  
 
    <xsl:call-template name="stylehtml"/> 
  </xsl:template>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/color/wizard.css" />
  </xsl:template>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/extra/wizard.css" />
    <style type="text/css" xml:space="preserve">
      body { behavior: url(../../../../editors/css/hover.htc);}
    </style>
  </xsl:template>

  <xsl:template name="extrajavascript">
    <script type="text/javascript" src="{$ew_context}/js/prototype.js"><xsl:comment>help IE</xsl:comment></script>
    <script type="text/javascript" src="{$ew_context}{$templatedir}javascript/override.js"><xsl:comment>help IE</xsl:comment></script>
    <script type="text/javascript" src="{$ew_context}{$templatedir}javascript/my-validator.js"><xsl:comment>help IE</xsl:comment></script>
   <script type="text/javascript">
      var isWebmaster = "<xsl:value-of select="$WEBMASTER"/>";
    </script>
    <xsl:call-template name="extrajavascript-custom"/>
  </xsl:template>

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

<!-- OVERRIDE LAYOUT TABS FORM AND COMMANDBUTTONS -->
  <xsl:template name="bodycontent" >
    <div id="stepsbar">
      <xsl:apply-templates select="/*/steps-validator"/>     
    </div>
    <table class="body">
      <xsl:call-template name="body"/>
    </table>
    <div id="commandbuttonbar" class="buttonscontent">
      <div class="page_buttons_seperator"><div></div></div>
      <xsl:for-each select="/*/steps-validator">
        <xsl:call-template name="buttons"/>
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template name="formcontent">
    <div id="editform" class="editform">
      <table class="formcontent">
        <xsl:apply-templates select="form[@id=/wizard/curform]"/>
      </table>
    </div>
  </xsl:template>
<!-- END LAYOUT TABS FORM AND COMMANDBUTTONS -->

<!-- OVERRIDE TABS -->
  <xsl:template name="steps">
    <div class="tabs">
        <xsl:for-each select="step">
          <xsl:call-template name="steptemplate"/>
        </xsl:for-each>            
    </div>
  </xsl:template>
  
  <xsl:template name="steptemplate">
    <div>
      <xsl:variable name="schemaid" select="@form-schema"/>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="@form-schema=/wizard/curform">tab_active</xsl:when>
          <xsl:otherwise>tab</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:call-template name="step"/>
    </div>
  </xsl:template>
  
  <!-- The appearance of one 'step' button -->
  <xsl:template name="step">
    <div class="body">
    <a>
      <xsl:call-template name="stepsattributes"/>
      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="/*/form[@id=current()/@form-schema]/title"/>
      </xsl:call-template>
    </a>
    </div>
  </xsl:template>

  <xsl:template name="previousbutton" />
  <xsl:template name="nextbutton" />
  
<!-- END TABS -->

<!-- OVERRIDE COMMAND BUTTONS -->
  <xsl:template name="buttons">
    <div class="page_buttons">      
      <xsl:if test="$READONLY=&apos;false&apos;">      
        <xsl:call-template name="buttons-extended" />
        <!-- commit  -->
        <div class="button">
          <div class="button_body">
            <xsl:call-template name="savebutton" />
          </div>
        </div>
        <!-- saveonly  -->
        <div class="button">
          <div class="button_body">
            <xsl:call-template name="saveonlybutton" />
          </div>
        </div>
      </xsl:if>
      <!-- cancel -->
      <div class="button">
        <div class="button_body">
          <xsl:call-template name="cancelbutton" />
        </div>
      </div> 
      <div class="begin"><xsl:comment>empty</xsl:comment></div>
    </div>
  </xsl:template>

<!-- END COMMAND BUTTONS -->

  <xsl:template match="form">
    <xsl:for-each select="fieldset|field|list">
      <xsl:choose>
        <xsl:when test="name()='field'">
          <tr class="fieldcanvas">
            <xsl:apply-templates select="."/>
          </tr>
        </xsl:when>
        <xsl:when test="name()='list'">
          <tr class="listcanvas">
            <td colspan="2">
              <div>
                <xsl:call-template name="colordiv"/>
                <div>
                  <xsl:apply-templates select="."/>
                </div>
              </div>
              <table style="width: 100%">
              <xsl:call-template name="listtable"/>
              </table>
            </td>
          </tr>
        </xsl:when>
        <xsl:when test="name()='fieldset'">
          <tr class="fieldsetcanvas">
            <xsl:apply-templates select="."/>
          </tr>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

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

  <xsl:template match="field"> 
      <td class="fieldprompt">
        <xsl:call-template name="prompt" />
      </td>    
      <td class="field">
        <xsl:call-template name="fieldintern" />
        <xsl:call-template name="prompt_errormesg" />
      </td>
  </xsl:template>

  <xsl:template name="prompt_errormesg">
    <br />
    <span id="errormesg_{@fieldname}" class="notvalid"></span>
  </xsl:template>
  
  <!--
    fieldintern is called to draw the values
  -->
  <xsl:template name="fieldintern">
    <xsl:apply-templates select="prefix"/>

    <xsl:choose>
      <xsl:when test="@ftype=&apos;startwizard&apos;">
        <xsl:call-template name="ftype-startwizard"/>
      </xsl:when>
      
      <xsl:when test="@ftype=&apos;function&apos;">
        <xsl:call-template name="ftype-function"/>
      </xsl:when>
<!-- READONLY WIZARDS SHOULD DISABLE ALL INPUT FIELDS -->
      <xsl:when test="@ftype=&apos;data&apos; or $READONLY=&apos;true&apos;">
<!-- END -->
        <xsl:call-template name="ftype-data"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;line&apos;">
        <xsl:call-template name="ftype-line"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;text&apos;">
        <xsl:call-template name="ftype-text"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;mmxf&apos;">
        <xsl:call-template name="ftype-text"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;html&apos;">
        <xsl:call-template name="ftype-html"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;relation&apos;">
        <xsl:call-template name="ftype-relation"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;enum&apos;">
        <xsl:call-template name="ftype-enum"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;enumdata&apos;">
        <xsl:call-template name="ftype-enumdata"/>
      </xsl:when>
      <xsl:when test="(@ftype=&apos;datetime&apos;) or (@ftype=&apos;date&apos;) or (@ftype=&apos;time&apos;) or (@ftype=&apos;duration&apos;)">
        <xsl:call-template name="ftype-datetime"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;image&apos;">
        <xsl:call-template name="ftype-image"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;imagedata&apos;">
        <xsl:call-template name="ftype-imagedata"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;file&apos;">
        <xsl:call-template name="ftype-file"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;filedata&apos;">
        <xsl:call-template name="ftype-filedata"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;radio&apos;">
         <xsl:call-template name="ftype-radio"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;checkbox&apos;">
         <xsl:call-template name="ftype-checkbox"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;boolean&apos;">
         <xsl:call-template name="ftype-checkbox"/>
      </xsl:when>
      <xsl:when test="@ftype=&apos;realposition&apos;">
        <xsl:call-template name="ftype-realposition"/>
      </xsl:when>

      <xsl:otherwise>
        <xsl:call-template name="ftype-unknown"/>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:apply-templates select="postfix"/>
  </xsl:template>

  <!--
    What to do with 'lists'.
  -->
  <xsl:template name="colordiv">
    <xsl:attribute name="class">ruler<xsl:value-of select="(position() mod 4)+1"/></xsl:attribute>
  </xsl:template>
  
  <xsl:template match="list">
     <xsl:call-template name="listprompt" />
  </xsl:template>
  
  <xsl:template name="listtable">
    <tr class="item{(position() mod 4)+1}">
      <!-- 
        I know this really shouldn't be here (the style attribute)
        but since we need to do more fixes anyway I'll leave it
        here until the structure is changed.
      -->
      <td colspan="2" style="padding: 15px;">
    <table class="contentlist">
      <tr>  
        <!-- NEW button -->
        <!-- REMOVE NEW AND SEARCH WHEN READONLY -->
        <xsl:if test="$READONLY=&apos;false&apos;">    
          <xsl:call-template name="listnewbuttons" />
          <!-- SEARCH input and button -->
          <td>
            <xsl:call-template name="listsearch" />
          </td>
          <!-- REMOVE NEW AND SEARCH WHEN READONLY -->
        </xsl:if>
        <!-- END -->
      </tr>
    </table>
      </td>
    </tr>
    
              
    <xsl:if test="@status=&apos;invalid&apos;">
    <tr>
      <td colspan="2">
        <div class="messagebox_red">
          <div class="box">
            <div class="top"><div></div></div>                  
              <div class="body">
                <xsl:call-template name="prompt_invalid_list">
                  <xsl:with-param name="minoccurs" select="@minoccurs" />
                  <xsl:with-param name="maxoccurs" select="@maxoccurs" />
                </xsl:call-template>
              </div>                                    
            <div class="bottom"><div></div></div>
          </div>
        </div>
      </td>
    </tr>
    </xsl:if>
    
    <!-- List of items -->
    <tr class="itemcanvas">
      <td></td>
      <td>       
        <xsl:call-template name="listitems"/>
        <xsl:variable name="INPUTREADONLY" select="'true'" /> 
      </td>
    </tr>
  </xsl:template> 

  <xsl:template name="listprompt">
    <span>
      <xsl:attribute name="title">
        <xsl:call-template name="i18n">
          <xsl:with-param name="nodes" select="description"/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:call-template name="i18n">
        <xsl:with-param name="nodes" select="title"/>
      </xsl:call-template>
    </span>
  </xsl:template>

  <!-- yeah right don't think so -->
  <xsl:template name="listsearch-fields-default"/>

  <xsl:template name="listsearch">
    <!-- if 'add-item' command and a search, then make a search util-box -->
    <xsl:if test="command[@name=&apos;add-item&apos;]">
      <xsl:for-each select="command[@name=&apos;search&apos;]">
        <table class="searchcontent">
          <tr>
            <xsl:if test="prompt">
              <td class="searchprompt"><xsl:call-template name="prompt"/></td>
            </xsl:if>
            <td>
              <xsl:call-template name="listsearch-age"/>
            </td>
            <td>
              <xsl:call-template name="listsearch-fields"/>
            </td>
            <td>
              <input type="text" name="searchterm_{../command[@name=&apos;add-item&apos;]/@cmd}" value="{search-filter[1]/default}" class="search" onChange="var s = form[&apos;searchfields_{../command[@name=&apos;add-item&apos;]/@cmd}&apos;]; s[s.selectedIndex].setAttribute(&apos;default&apos;, this.value);"/>
              <!-- on change the current value is copied back to the option's default, because of that, the user's search is stored between different types of search-actions -->
            </td>
            <td>
              <a href="#" title="{$tooltip_search}" onclick="doSearch(this,&apos;{../command[@name=&apos;add-item&apos;]/@cmd}&apos;,&apos;{$sessionkey}&apos;);" class="button">
                <xsl:for-each select="@*">
                  <xsl:copy/>
                </xsl:for-each>
                <xsl:attribute name="relationOriginNode"><xsl:value-of select="../@number" /></xsl:attribute>
                <xsl:choose>
                  <xsl:when test="../action[@type=&apos;add&apos;]/relation/@role">
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type=&apos;add&apos;]/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type=&apos;add&apos;]/relation/@createdir" /></xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type=&apos;create&apos;]/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type=&apos;create&apos;]/relation/@createdir" /></xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="prompt_search"/>
              </a>
            </td>
          </tr>
        </table>
      </xsl:for-each>
      <xsl:for-each select="command[@name=&apos;assetsselector&apos;]">
        <table class="searchcontent">
          <tr>
            <xsl:if test="prompt">
              <td class="searchprompt"><xsl:call-template name="prompt"/></td>
            </xsl:if>
            <td>
              <xsl:call-template name="listsearch-age"/>
            </td>
            <td>
              <xsl:call-template name="listsearch-fields"/>
            </td>
            <td>
              <input type="text" name="searchterm_{../command[@name=&apos;add-item&apos;]/@cmd}" value="{search-filter[1]/default}" class="search" onChange="selectAssets(this,'{@nodepath}')"/>
              <!-- on change the current value is copied back to the option's default, because of that, the user's search is stored between different types of search-actions -->
            </td>
            <td>
           <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';getAssets('{@nodepath}','current')" class="button">
                <xsl:for-each select="@*">
                  <xsl:copy/>
                </xsl:for-each>
                <xsl:attribute name="relationOriginNode"><xsl:value-of select="../@number" /></xsl:attribute>
                <xsl:choose>
                  <xsl:when test="../action[@type=&apos;add&apos;]/relation/@role">
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type=&apos;add&apos;]/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type=&apos;add&apos;]/relation/@createdir" /></xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type=&apos;create&apos;]/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type=&apos;create&apos;]/relation/@createdir" /></xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="prompt_search"/>
           </a>
            </td>
          </tr>
        </table>
      </xsl:for-each>
      <xsl:for-each select="command[@name=&apos;imagesselector&apos;]">
        <table class="searchcontent">
          <tr>
            <xsl:if test="prompt">
              <td class="searchprompt"><xsl:call-template name="prompt"/></td>
            </xsl:if>
            <td>
              <xsl:call-template name="listsearch-age"/>
            </td>
            <td>
              <xsl:call-template name="listsearch-fields"/>
            </td>
            <td>
              <input type="text" name="searchterm_{../command[@name=&apos;add-item&apos;]/@cmd}" value="{search-filter[1]/default}" class="search" onChange="selectAssets(this,'{@nodepath}')"/>
              <!-- on change the current value is copied back to the option's default, because of that, the user's search is stored between different types of search-actions -->
            </td>
            <td>
           <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';getAssets('{@nodepath}','all')" class="button">
                <xsl:for-each select="@*">
                  <xsl:copy/>
                </xsl:for-each>
                <xsl:attribute name="relationOriginNode"><xsl:value-of select="../@number" /></xsl:attribute>
                <xsl:choose>
                  <xsl:when test="../action[@type=&apos;add&apos;]/relation/@role">
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type=&apos;add&apos;]/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type=&apos;add&apos;]/relation/@createdir" /></xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="relationRole"><xsl:value-of select="../action[@type=&apos;create&apos;]/relation/@role" /></xsl:attribute>
                    <xsl:attribute name="relationCreateDir"><xsl:value-of select="../action[@type=&apos;create&apos;]/relation/@createdir" /></xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="prompt_search"/>
           </a>
            </td>
          </tr>
        </table>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="listitems">
    <xsl:if test="item">
      <table width="100%">
        <xsl:apply-templates select="item"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="item">
  
    <xsl:call-template name="itemprefix"/>
      <!-- here we figure out how to draw this repeated item. It depends on the displaytype -->
    <xsl:choose>
      <xsl:when test="@displaytype='link'">
        <xsl:call-template name="item-link"/>
      </xsl:when>
      <xsl:when test="@displaytype='image'">
        <xsl:call-template name="item-image"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="item-other"/>
      </xsl:otherwise>
    </xsl:choose>
      
    <xsl:for-each select="list">
      <tr class="listcanvas">
        <td colspan="3" style="padding-left: 50px;">
              <div>
                <xsl:call-template name="colordiv"/>
                <div>
                  <xsl:apply-templates select="." />
                </div>
              </div>
        <table width="100%">
          <xsl:call-template name="listtable"/>
          </table>
        </td>
      </tr>
    </xsl:for-each>
    <xsl:call-template name="itempostfix"/>
  </xsl:template>

  <xsl:template name="item-image">
    <tr>
      <td>
        <xsl:call-template name="itemfields"/>
      </td>
      <td class="itembutton">
        <xsl:call-template name="itembuttons" />
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="item-other">
    <tr>
      <td>
        <xsl:call-template name="itemfields"/>
      </td>
      <td class="itembutton">
        <xsl:call-template name="itembuttons" />
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="itemfields">
    <table class="fieldscontent">
      <xsl:call-template name="itemfields-hover"/>
      
      <xsl:for-each select="fieldset|field">
        <!-- Don't show the startwizard field here. -->
        <xsl:if test="not(@ftype=&apos;startwizard&apos;)">
          <tr class="fieldcanvas">
            <xsl:call-template name="itemfields-click"/>
            <xsl:apply-templates select="."/>
          </tr>
        </xsl:if>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="itempostfix">
  </xsl:template>

  <xsl:template name="itembuttons">
    <table>
      <tr>
        <!-- REMOVE WHEN READONLY -->
        <xsl:if test="$READONLY=&apos;false&apos;">
          <!-- END -->
          <xsl:call-template name="deleteitembuttons" />
          <xsl:call-template name="positembuttons" />
          <!-- REMOVE WHEN READONLY -->
        </xsl:if>
        <!-- END -->
        <xsl:call-template name="mediaitembuttons" />
        <xsl:call-template name="selectitembuttons" />
      </tr>
    </table>
  </xsl:template>

   <!-- overide this one, because we want to be able to call for a pageselector -->
  <xsl:template name="listnewbuttons">
    <xsl:choose>
    <xsl:when test="command[@name=&apos;add-item&apos;]">
      <!-- only if less then maxoccurs -->
      <xsl:if test="not(@maxoccurs) or (@maxoccurs = &apos;*&apos;) or count(item) &lt; @maxoccurs">
        <xsl:apply-templates select="command" mode="listnewbuttons"/>
      </xsl:if>
    </xsl:when>
    <xsl:otherwise>
      <td class="listnew"></td>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="listnewselectors-custom">
     <!-- Crash CMSc 1.1 customer wizard-custom.xsl, because we use xsl:apply-templates now -->
  </xsl:template>

  <xsl:template match="command[@name=&apos;add-item&apos;]" mode="listnewbuttons">
     <!-- add-item is used to check for a new action -->
  </xsl:template>

  <xsl:template match="command[@name=&apos;search&apos;]" mode="listnewbuttons">
     <!-- Search is handled by the listsearch template -->
  </xsl:template>
  
  <xsl:template match="command[@name=&apos;assetsselector&apos;]" mode="listnewbuttons">
     <!-- Search is handled by the listsearch template -->
  </xsl:template>
  
  <xsl:template match="command[@name=&apos;imagesselector&apos;]" mode="listnewbuttons">
     <!-- Search is handled by the listsearch template -->
  </xsl:template>

  <xsl:template match="command[@name=&apos;startwizard&apos;]" mode="listnewbuttons">
    <!-- create action and startwizard command are present. Open the object into the start wizard -->
    <!-- The prompts.xsl adds this as a tooltip -->
    <!-- Moved prompt to the "prompt_add_wizard" template as a tooltip -->
    <xsl:choose>
      <xsl:when test="@wizardjsp">
        <td class="listnew">
          <a href="{$ew_context}{@wizardjsp}/?objectnumber=new&amp;origin={@origin}&amp;wizard={@wizardname}"
             class="expand_button">
            <xsl:call-template name="prompt_add_wizard" />
          </a>
        </td>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="@inline=&apos;true&apos;">
          <td class="listnew">
            <a href="javascript:doStartWizard(&apos;{../@fid}&apos;,&apos;{../command[@name=&apos;add-item&apos;]/@value}&apos;,&apos;{@wizardname}&apos;,&apos;{@objectnumber}&apos;,&apos;{@origin}&apos;);"
               class="expand_button">
              <xsl:call-template name="prompt_add_wizard"/>
            </a>
          </td>
        </xsl:if>
        <xsl:if test="not(@inline=&apos;true&apos;)">
          <td class="listnew">
            <a href="{$popuppage}&amp;fid={../@fid}&amp;did={../command[@name=&apos;add-item&apos;]/@value}&amp;popupid={@wizardname}_{@objectnumber}&amp;wizard={@wizardname}&amp;objectnumber={@objectnumber}&amp;origin={@origin}"
               target="_blank" class="expand_button">
              <xsl:call-template name="prompt_add_wizard"/>
            </a>
          </td>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="command[@name=&apos;insert&apos;]" mode="listnewbuttons">
    <xsl:for-each select="../command[@name=&apos;add-item&apos;]">
      <td class="listnew">
        <a href="javascript:doAddInline(&apos;{@cmd}&apos;);" class="expand_button">
          <xsl:call-template name="prompt_new"/>
        </a>
      </td>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="command[@name=&apos;pageselector&apos;]" mode="listnewbuttons">
    <td class="listnew">
      <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';window.open('../../../../editors/site/select/SelectorPage.do', 'pageselector', 'width=350,height=500,status=yes,toolbar=no,titlebar=no,scrollbars=yes,resizable=yes,menubar=no');" class="button">
        <xsl:call-template name="prompt_search"/>
      </a>
    </td>
  </xsl:template>

  <xsl:template match="command[@name=&apos;contentselector&apos;]" mode="listnewbuttons">
    <td class="listnew">
      <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';window.open('../../../../editors/repository/SearchInitAction.do?action=selectforwizard', 'contentselector', 'width=1000,height=550,status=yes,toolbar=no,titlebar=no,scrollbars=yes,resizable=yes,menubar=no');" class="button">
        <xsl:call-template name="prompt_search"/>
      </a>
    </td>
  </xsl:template>

  <xsl:template match="command[@name=&apos;channelselector&apos;]" mode="listnewbuttons">
    <td class="listnew">
      <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';window.open('../../../../editors/repository/select/SelectorContentChannel.do', 'channelselector', 'width=350,height=500,status=yes,toolbar=no,titlebar=no,scrollbars=yes,resizable=yes,menubar=no');" class="button">
        <xsl:call-template name="prompt_search"/>
      </a>
    </td>
  </xsl:template>
  
  <xsl:template match="command" mode="listnewbuttons">
    <td class="listnew">xslt template missing for command <xsl:value-of select="@name"/></td>
  </xsl:template>

  <xsl:template name="deleteitembuttons">
      <xsl:if test="command[@name=&apos;delete-item&apos;]">
        <xsl:if test="@maydelete!=&apos;false&apos;">
         <td>          
          <a href="#" class="imgbuttonremove" title="{$tooltip_remove}" onclick="doRemove(&apos;{command[@name=&apos;delete-item&apos;]/@cmd}&apos;);">
            <xsl:call-template name="prompt_remove"/>
          </a>         
        </td>
        </xsl:if>
      </xsl:if>
  </xsl:template>
  
  <xsl:template name="positembuttons">
      <xsl:choose>
        <xsl:when test="@maywrite=&apos;true&apos; and command[@name=&apos;move-up&apos;]">
          <td>
            <a href="#" class="imgbuttonup" title="{$tooltip_up}" onclick="doMoveUp(&apos;{command[@name=&apos;move-up&apos;]/@cmd}&apos;);">
            <xsl:call-template name="prompt_up"/>
          </a>
          </td>
        </xsl:when>
        <xsl:otherwise>
         
        </xsl:otherwise>
      </xsl:choose>
    
      <xsl:choose>
        <xsl:when test="@maywrite=&apos;true&apos; and command[@name=&apos;move-down&apos;]">
          <td>
            <a href="#" class="imgbuttondown" title="{$tooltip_down}" onclick="doMoveDown(&apos;{command[@name=&apos;move-down&apos;]/@cmd}&apos;);">
              <xsl:call-template name="prompt_down"/>
            </a>
          </td>
        </xsl:when>
        <xsl:otherwise>
          
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template name="selectitembuttons">
  </xsl:template>

<!-- OVERRIDE PROMPTS.XSL
-->

  <xsl:template name="prompt_remove">
  </xsl:template>

  <xsl:template name="prompt_up">
  </xsl:template>

  <xsl:template name="prompt_down">
  </xsl:template>

  <xsl:template name="emptypositembutton">
  </xsl:template>
  
  <xsl:template name="prompt_new">
    <xsl:value-of select="$prompt_new_link" />
  </xsl:template>
  
  <xsl:template name="prompt_search">
    <xsl:value-of select="$prompt_search_link" />
  </xsl:template>

  <!-- prompts for starting a editwizard -->
  <xsl:template name="prompt_edit_wizard">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="alt"><xsl:value-of select="prompt" /></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt"><xsl:value-of select="$tooltip_edit_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$prompt_edit_link" />
  </xsl:template>
  
  <xsl:template name="prompt_add_wizard">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="title"><xsl:value-of select="prompt" /></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="title"><xsl:value-of select="$tooltip_add_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$prompt_new_link" />
  </xsl:template>

<!-- END OVERRIDE PROMPTS.XSL -->
  <xsl:template name="ftype-unknown">
    <xsl:choose>
      <xsl:when test="@ftype=&apos;calendar&apos;">
         <xsl:call-template name="ftype-calendar"/>
      </xsl:when>
      <xsl:otherwise>
          <xsl:call-template name="ftype-other"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="ftype-calendar">
     <nobr><select name="calendar-type" id="calendar-type" onchange="resetCalendar(this.value,'{@fieldname}')">
   <option value="0"><xsl:value-of select="$prompt_newsletter_never" /></option>
   <option value="1"><xsl:value-of select="$prompt_newsletter_once" /></option>
   <option value="2"><xsl:value-of select="$prompt_newsletter_daily" /></option>
   <option value="3"><xsl:value-of select="$prompt_newsletter_weekly" /></option>
   <option value="4"><xsl:value-of select="$prompt_newsletter_monthly" /></option>
      </select> &#x0020;
      <input type="hidden" name="{@fieldname}" value="{value}" title="new-calendar" id="{@fieldname}"/>
      <a href="#" id="calendarSelect" class="button" onclick="javascript:window.open ('calendar.jsp?id={@fieldname}&amp;type='+document.getElementById('calendar-type').value, 'calendar', 'height=400, width=500, top='+eval((window.screen.availHeight - 400)/2)+', left='+eval((window.screen.availWidth - 500)/2)+',toolbar=no, menubar=no, scrollbars=no, location=no, status=no')"><xsl:value-of select="$prompt_newsletter_select" />  </a> <a class="button" href="#" id="calendarDelete" onclick="javascript:document.getElementById('calendar-expression').innerHTML='';document.getElementById('{@fieldname}').value=''"><xsl:value-of select="$prompt_newsletter_delete" /></a></nobr>
      <div id="calendar-expression"></div>      
  </xsl:template>

   <xsl:template name="ftype-image">
   <xsl:if test="@maywrite!=&apos;false&apos;">
      <xsl:choose>
         <xsl:when test="@dttype=&apos;binary&apos; and not(upload)">
            <div class="imageupload">
               <div>
                  <input type="hidden" name="{@fieldname}" value="" dttype="binary" ftype="image" >
                     <xsl:if test="@dtrequired=&apos;true&apos; and @size &lt;= 0">
                        <xsl:attribute name="dtrequired">true</xsl:attribute>
                     </xsl:if>
                  </input>
                  <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}&amp;filetype={'image'}" onclick="return doStartUpload(this);">
                     <xsl:call-template name="prompt_image_upload"/>
                  </a>
                  <br/>
                  <xsl:if test="@size &gt; 0">
                     <img src="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,cache(&apos;, $imagesize, &apos;))&apos;))}" hspace="0" vspace="0" border="0" title="{field[@name=&apos;description&apos;]}"/>
                     <xsl:if test="@size &lt; 2048">
                        (<xsl:value-of select="@size"/><xsl:text>byte</xsl:text>)
                     </xsl:if>
                     <xsl:if test="@size &gt; =2048 and @size &lt; (2*1024*1024)">
                        (<xsl:value-of select="format-number(@size div 1024,'#,##0.0')"/><xsl:text>K</xsl:text>)
                     </xsl:if>
                     <xsl:if test="@size &gt; =(2*1024*1024)">
                        (<xsl:value-of select="format-number(@size div (1024*1024),'#,##0.0')"/><xsl:text>M</xsl:text>)
                     </xsl:if>
                     <br/>
                     <a
                        href="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey,&apos;)&apos;))}"
                        target="_new">
                        <xsl:call-template name="prompt_image_full" />
                     </a>
                     <br/>
                  </xsl:if>
               </div>
            </div>
         </xsl:when>
         <xsl:when test="@dttype=&apos;binary&apos; and upload">
            <div class="imageupload">
               <input type="hidden" name="{@fieldname}" value="YES" dttype="binary" ftype="image" >
                  <xsl:if test="@dtrequired=&apos;true&apos;">
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
                  <xsl:if test="upload/@size &lt; 2048">
                     (<xsl:value-of select="upload/@size"/><xsl:text>byte</xsl:text>)
                  </xsl:if>
                  <xsl:if test="upload/@size &gt; =2048 and upload/@size &lt; (2*1024*1024)">
                     (<xsl:value-of select="format-number(upload/@size div 1024,'#,##0.0')"/><xsl:text>K</xsl:text>)
                  </xsl:if>
                  <xsl:if test="upload/@size &gt; =(2*1024*1024)">
                     (<xsl:value-of select="format-number(upload/@size div (1024*1024),'#,##0.0')"/><xsl:text>M</xsl:text>)
                  </xsl:if>
               </span>
               <br/>
               <a href="{$uploadpage}&amp;popupid={$popupid}&amp;did={@did}&amp;wizard={/wizard/@instance}&amp;maxsize={@dtmaxsize}&amp;filetype={'image'}" onclick="return doStartUpload(this);">
                  <xsl:call-template name="prompt_image_replace"/>
               </a>
            </div>
         </xsl:when>
         <xsl:otherwise>
            <span>
            <img src="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,cache(&apos;, $imagesize, &apos;))&apos;))}" hspace="0" vspace="0" border="0" title="{field[@name=&apos;description&apos;]}"/>
            <br/>
            <a
               href="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey,&apos;)&apos;))}"
               target="_new">
               <xsl:call-template name="prompt_image_full" />
            </a>
            <br/>
            </span>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:if>
   <xsl:if test="@maywrite=&apos;false&apos;">
      <span class="readonly">
         <img src="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,cache(&apos;, $imagesize, &apos;))&apos;))}" hspace="0" vspace="0" border="0"/>
      </span>
   </xsl:if>
   </xsl:template>
   
   <xsl:template name="ftype-imagedata">
      <span class="readonly">
         <img src="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,cache(&apos;, $imagesize, &apos;))&apos;))}" hspace="0" vspace="0" border="0"/>
         <br/>
         <a href="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey,&apos;)&apos;))}" target="_new">
            <xsl:call-template name="prompt_image_full" />
         </a>
      </span>
   </xsl:template>

   <xsl:template name="ftype-file">
   <xsl:choose>
   <xsl:when test="@dttype=&apos;data&apos; or @maywrite=&apos;false&apos;">
      <a  href="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,number)&apos;))}">
         <xsl:call-template name="prompt_do_download"/>
      </a>
   </xsl:when>
   <xsl:otherwise>
      <xsl:choose>
         <xsl:when test="not(upload)">
            <input type="hidden" name="{@fieldname}" value="" dttype="binary" ftype="file" >
               <xsl:if test="@dtrequired=&apos;true&apos; and @size &lt;= 0">
                  <xsl:attribute name="dtrequired">true</xsl:attribute>
               </xsl:if>
            </input>
            <xsl:if test="@size &lt;= 0">
               <xsl:call-template name="prompt_no_file"/><br/>
            </xsl:if>
            <xsl:if test="@size &gt; 0">
               <a href="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,number)&apos;))}">
                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                  <xsl:call-template name="prompt_do_download"/>
                  <xsl:if test="@size &lt; 2048">
                     (<xsl:value-of select="@size"/><xsl:text>byte</xsl:text>)
                  </xsl:if>
                  <xsl:if test="@size &gt; =2048 and @size &lt; (2*1024*1024)">
                     (<xsl:value-of select="format-number(@size div 1024,'#,##0.0')"/><xsl:text>K</xsl:text>)
                  </xsl:if>
                  <xsl:if test="@size &gt; =(2*1024*1024)">
                     (<xsl:value-of select="format-number(@size div (1024*1024),'#,##0.0')"/><xsl:text>M</xsl:text>)
                  </xsl:if>
               </a>
            <br/>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <input type="hidden" name="{@fieldname}" value="YES" dttype="binary" ftype="file" >
               <xsl:if test="@dtrequired=&apos;true&apos;">
                  <xsl:attribute name="dtrequired">true</xsl:attribute>
               </xsl:if>
            </input>
            <xsl:call-template name="prompt_uploaded"/>
            <xsl:value-of select="upload/@name"/>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
               <xsl:if test="upload/@size &lt; 2048">
                  (<xsl:value-of select="upload/@size"/><xsl:text>byte</xsl:text>)
               </xsl:if>
               <xsl:if test="upload/@size &gt; =2048 and upload/@size &lt; (2*1024*1024)">
                  (<xsl:value-of select="format-number(upload/@size div 1024,'#,##0.0')"/><xsl:text>K</xsl:text>)
               </xsl:if>
               <xsl:if test="upload/@size &gt; =(2*1024*1024)">
                  (<xsl:value-of select="format-number(upload/@size div (1024*1024),'#,##0.0')"/><xsl:text>M</xsl:text>)
               </xsl:if>
            <br/>
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
   
   <xsl:template name="ftype-filedata">
      <a href="{node:saxonFunction($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,number)&apos;))}">
         <xsl:call-template name="prompt_do_download"/>
      </a>
   </xsl:template>
   
  <!--CMSC-1049 Time display in article creation author: Rain.Tang-->
  <!--template new-loop-options is new added only used in hours and minutes display-->
  <xsl:template name="new-loop-options">
    <xsl:param name="value">0</xsl:param>
    <xsl:param name="selected"/>
    <xsl:param name="end">0</xsl:param>

    <xsl:call-template name="gen-option">
      <xsl:with-param name="value" select="format-number($value,'00')" />
      <xsl:with-param name="selected" select="$selected" />
      <xsl:with-param name="text" select="$value" />
    </xsl:call-template>

    <xsl:if test="$value &lt; $end">
      <xsl:call-template name="new-loop-options">
        <xsl:with-param name="value" select="format-number($value + 1,'00')" />
        <xsl:with-param name="selected" select="$selected" />
        <xsl:with-param name="end" select="$end" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="ftype-datetime-time">
    <select name="internal_{@fieldname}_hours" super="{@fieldname}">
      <xsl:call-template name="new-loop-options">
        <xsl:with-param name="value">00</xsl:with-param>
        <xsl:with-param name="selected" select="date:getHours(string(value), string($timezone))" />
        <xsl:with-param name="end" select="23" />
      </xsl:call-template>
    </select>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;:&amp;nbsp;</xsl:text>
    <select name="internal_{@fieldname}_minutes" super="{@fieldname}">
      <xsl:call-template name="new-loop-options">
        <xsl:with-param name="value">00</xsl:with-param>
        <xsl:with-param name="selected" select="date:getMinutes(string(value), string($timezone))" />
        <xsl:with-param name="end" select="59" />
      </xsl:call-template>
    </select>
    <xsl:if test="@ftype=&apos;duration&apos;">
      <xsl:text disable-output-escaping="yes">&amp;nbsp;:&amp;nbsp;</xsl:text>
      <select name="internal_{@fieldname}_seconds" super="{@fieldname}">
        <xsl:call-template name="new-loop-options">
          <xsl:with-param name="value">00</xsl:with-param>
          <xsl:with-param name="selected" select="date:getSeconds(string(value), string($timezone))" />
          <xsl:with-param name="end" select="59" />
        </xsl:call-template>
      </select>
    </xsl:if>
  </xsl:template>

   <!--End of CMSC-1049-->
   
</xsl:stylesheet>