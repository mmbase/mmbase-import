<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 25-07-2003
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/wizard.xsl"/>

<!-- PARAMETERS PASSED IN BY THE TRANSFORMER. 
     wE PASSED A MAP WITH KEY VALUE PAIRS TO THE TRANSFORMER
 -->
  <xsl:param name="ACTIVITY">DRAFT</xsl:param>

  <xsl:param name="READONLY">false</xsl:param>
  <!-- Reason why the wizardcontroller made the wizard readonly (currently these are NONE, RIGHTS and WORKFLOW) -->
  <xsl:param name="READONLY-REASON">NONE</xsl:param>
  <xsl:param name="WORKFLOW">false</xsl:param>
  <xsl:param name="WORKFLOW-ACCEPTED-ENABLED">false</xsl:param>
  <xsl:param name="WRITER">true</xsl:param>
  <xsl:param name="EDITOR">false</xsl:param>
  <xsl:param name="CHIEFEDITOR">false</xsl:param>
  <xsl:param name="WEBMASTER">false</xsl:param>
  
  <xsl:variable name="REASON-WORKFLOW">Dit object staat in workflow en is goedgekeurd. U heeft geen publiceer rechten op de rubriek waartoe dit object behoort.</xsl:variable>
  <xsl:variable name="REASON-RIGHTS">U heeft geen rechten op  de rubriek waartoe dit object behoort.</xsl:variable>

<!--
  END
-->



<!-- CMSC-446: fix for date picker, so it won't always popup when pressing enter in an input -->
  <xsl:template name="date-picker">
    <img class="calendar" src="{$mediadir}datepicker/calendar.gif" border="0" onClick="popUpCalendar(this, 'dd-mm-yyyy', - 205 , 5 , document.forms[0], 'internal_{@fieldname}',event);return false;"/>
  </xsl:template>
  


  <xsl:variable name="htmlareadir"><xsl:value-of select="$ew_context" />/mmbase/edit/wizard/xinha/</xsl:variable>

<!-- OVERRIDE PROMPTS.XSL
  The prompts.xsl can not be extended, bacause that will break 
  the i18n prompts support.
-->
  <xsl:template name="beforeform">
    <xsl:if test="$READONLY=&apos;true&apos;">
      <xsl:if test="$READONLY-REASON=&apos;WORKFLOW&apos;">
        <p class="readonly-reason"> <xsl:value-of select="$REASON-WORKFLOW"/> </p>
      </xsl:if>
      <xsl:if test="$READONLY-REASON=&apos;RIGHTS&apos;">
        <p class="readonly-reason"> <xsl:value-of select="$REASON-RIGHTS"/> </p>
      </xsl:if>
      <xsl:if test="$READONLY-REASON=&apos;PUBLISH&apos;">
         <p class="readonly-reason">Dit object staat klaar om gepubliceerd te worden. Zolang dit niet gebeurd is kan dit object niet bewerkt worden.</p>
      </xsl:if>
    </xsl:if>
   </xsl:template>


   <xsl:template name="formhiddenargs">
 		<input type="hidden" name="workflowcommand" value="" id="workflowcommand" />
		<input type="hidden" name="workflowcomment" value="" id="workflowcomment" />
   </xsl:template>

  <!-- prompts for starting a editwizard -->
  <xsl:template name="prompt_edit_wizard">
    <img src="{$mediadir}select.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="alt"><xsl:value-of select="prompt" /></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt"><xsl:value-of select="$tooltip_edit_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>
  
  <xsl:template name="prompt_add_wizard">
    <img src="{$mediadir}new.gif" class="imgbutton">
      <xsl:choose>
        <xsl:when test="prompt">
          <xsl:attribute name="alt"><xsl:value-of select="prompt" /></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="alt"><xsl:value-of select="$tooltip_add_wizard" /></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </img>
  </xsl:template>

<!-- END OVERRIDE PROMPTS.XSL -->

  <xsl:variable name="BodyOnLoad">preLoadButtons(); doOnLoad_ew(); start_validator();  xinha_init(); initPopCalendar();</xsl:variable>

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

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/color/wizard.css" />
  </xsl:template>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/extra/wizard.css" />
  </xsl:template>

  <xsl:template name="extrajavascript">
    <script type="text/javascript" src="{$ew_context}{$templatedir}javascript/override.js"><xsl:comment>help IE</xsl:comment></script>
    <script type="text/javascript">
      var isWebmaster = "<xsl:value-of select="$WEBMASTER"/>";
    </script>
  </xsl:template>

  <xsl:template name="previousbutton" />
  <xsl:template name="nextbutton" />

  <xsl:template name="buttons">
    <table class="buttonscontent">
      <tr>
        <td>
          <nobr>
            <!-- cancel -->
            <xsl:call-template name="cancelbutton" />
            <xsl:if test="$READONLY=&apos;false&apos;">
              <!-- saveonly  -->
              <xsl:call-template name="saveonlybutton" />
              <!-- commit  -->
              <xsl:call-template name="savebutton" />
<!-- EXTRA WORKFLOW KNOWLODGE -->
				<xsl:if test="$WORKFLOW=&apos;true&apos;">
					<xsl:if test="$ACTIVITY=&apos;DRAFT&apos;">
						<xsl:call-template name="finishbutton" />
					</xsl:if>

					<xsl:if test="$EDITOR=&apos;true&apos;">
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						<xsl:if test="$ACTIVITY!=&apos;DRAFT&apos;">
							<xsl:call-template name="rejectbutton" />
						</xsl:if>
						<xsl:if test="($WORKFLOW-ACCEPTED-ENABLED=&apos;true&apos; and $ACTIVITY!=&apos;APPROVED&apos;)">
							<xsl:call-template name="acceptbutton" />
						</xsl:if>
					</xsl:if>

					<xsl:if test="$CHIEFEDITOR=&apos;true&apos;">
						<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
						<xsl:call-template name="publishbutton" />
					</xsl:if>                
				</xsl:if>
<!-- END -->
            </xsl:if>
          </nobr>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="savebutton">
    <img
      id="bottombutton-save"
      onclick="doSave();"
      titlesave="{$tooltip_save}" titlenosave="{$tooltip_no_save}"
      enabledsrc="{$mediadir}save.gif"
      disabledsrc="{$mediadir}save_disabled.gif">
      <xsl:if test="@allowsave=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />save.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_save" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave=&apos;false&apos;">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />save_disabled.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_save" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template name="saveonlybutton">
    <img
        id="bottombutton-saveonly"
        onclick="doSaveOnly();"
        titlesave="{$tooltip_save_only}" titlenosave="{$tooltip_no_save}"
        enabledsrc="{$mediadir}saveonly.gif"
        disabledsrc="{$mediadir}saveonly_disabled.gif">
        <xsl:if test="@allowsave=&apos;true&apos;">
          <xsl:attribute name="class">bottombutton</xsl:attribute>
          <xsl:attribute name="src"><xsl:value-of select="$mediadir" />saveonly.gif</xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="$tooltip_save_only" /></xsl:attribute>
        </xsl:if>
        <xsl:if test="@allowsave=&apos;false&apos;">
      <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
          <xsl:attribute name="src"><xsl:value-of select="$mediadir" />saveonly_disabled.gif</xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_save" /></xsl:attribute>
          <xsl:attribute name="disabled" />
        </xsl:if>
    </img>  
  </xsl:template>


  <xsl:template name="cancelbutton">
    <img
      id="bottombutton-cancel"
      onclick="doCancel();"
      enabledsrc="{$mediadir}cancel.gif"
      disabledsrc="{$mediadir}cancel_disabled.gif">
      <xsl:if test="@allowcancel=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />cancel.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_cancel" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowcancel=&apos;false&apos;">
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />cancel_disabled.gif</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_cancel" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>


  <xsl:variable name="tooltip_finish">Voltooi het object en bied het ter goedkeuring aan</xsl:variable>
  <xsl:variable name="tooltip_no_finish">
  	De wijzigingen kunnen niet bewaard worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:variable name="tooltip_accept">Keur het object goed
</xsl:variable>
  <xsl:variable name="tooltip_no_accept">
  	De wijzigingen kunnen niet goedgekeurd worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:variable name="tooltip_reject">Afwijzen.</xsl:variable>
  <xsl:variable name="tooltip_no_reject">
  	De wijzigingen kunnen niet afgewezen worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:variable name="tooltip_publish">Publiceer het object</xsl:variable>
  <xsl:variable name="tooltip_no_publish">
  	De wijzigingen kunnen niet gepubliceerd worden, omdat nog niet alle data correct is ingevuld.
  </xsl:variable>

  <xsl:template name="finishbutton">
    <img
      id="bottombutton-finish"
      onclick="doFinish();"
      enabledsrc="{$mediadir}finish.gif"
      disabledsrc="{$mediadir}finish_disabled.gif">
      <xsl:if test="@allowsave=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />finish.gif</xsl:attribute>
        <xsl:attribute name="titlefinish"><xsl:value-of select="$tooltip_finish" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave=&apos;false&apos;">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />finish_disabled.gif</xsl:attribute>
        <xsl:attribute name="titlenofinish"><xsl:value-of select="$tooltip_no_finish" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template name="acceptbutton">
    <img
      id="bottombutton-accept"
      onclick="doAccept();"
      enabledsrc="{$mediadir}accept.gif"
      disabledsrc="{$mediadir}accept_disabled.gif">
      <xsl:if test="@allowsave=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />accept.gif</xsl:attribute>
        <xsl:attribute name="titleaccept"><xsl:value-of select="$tooltip_accept" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave=&apos;false&apos;">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />accept_disabled.gif</xsl:attribute>
        <xsl:attribute name="titlenoaccept"><xsl:value-of select="$tooltip_no_accept" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template name="rejectbutton">
    <img
      id="bottombutton-reject"
      onclick="doReject();"
      enabledsrc="{$mediadir}reject.gif"
      disabledsrc="{$mediadir}reject_disabled.gif">
      <xsl:if test="@allowsave=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />reject.gif</xsl:attribute>
        <xsl:attribute name="titlereject"><xsl:value-of select="$tooltip_reject" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave=&apos;false&apos;">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />reject_disabled.gif</xsl:attribute>
        <xsl:attribute name="titlenoreject"><xsl:value-of select="$tooltip_no_reject" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>

  <xsl:template name="publishbutton">
    <img
      id="bottombutton-publish"
      onclick="doPublish();"
      enabledsrc="{$mediadir}publish.gif"
      disabledsrc="{$mediadir}publish_disabled.gif">
      <xsl:if test="@allowsave=&apos;true&apos;">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />publish.gif</xsl:attribute>
        <xsl:attribute name="titlepublish"><xsl:value-of select="$tooltip_publish" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave=&apos;false&apos;">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="src"><xsl:value-of select="$mediadir" />publish_disabled.gif</xsl:attribute>
        <xsl:attribute name="titlenopublish"><xsl:value-of select="$tooltip_no_publish" /></xsl:attribute>
        <xsl:attribute name="disabled" />
      </xsl:if>
    </img>
  </xsl:template>



  <xsl:template match="field">
    <td class="fieldprompt">
      <xsl:call-template name="prompt" />
    </td>
    <td class="field">
      <xsl:call-template name="fieldintern" />
    </td>
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
      <xsl:when test="@ftype=&apos;file&apos;">
        <xsl:call-template name="ftype-file"/>
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
  <xsl:template match="list">
    <table class="listcontent">
      <tr>
        <td colspan="2">
          <table class="listhead">
            <tr>
              <td class="listprompt">
                <xsl:call-template name="listprompt" />
              </td>

              <!-- NEW button -->
<!-- REMOVE NEW AND SEARCH WHEN READONLY -->
    <xsl:if test="$READONLY=&apos;false&apos;">
              <td class="listnewbuttons">
                <xsl:call-template name="listnewbuttons" />
              </td>

              <!-- SEARCH input and button -->
              <td class="listsearch">
                <xsl:call-template name="listsearch" />
              </td>
  <!-- REMOVE NEW AND SEARCH WHEN READONLY -->
    </xsl:if>
<!-- END -->
            </tr>
          </table>
        </td>
      </tr>

      <!-- List of items -->
      <tr class="itemcanvas">
        <td class="itemprefix"/>
        <td>
          <xsl:call-template name="listitems"/>
        </td>
      </tr>
    </table>

    <p>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </p>
  </xsl:template>

  <!-- yeah right don't think so -->
  <xsl:template name="listsearch-fields-default"/>

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
        <td colspan="3">
          <xsl:apply-templates select="." />
        </td>
      </tr>
    </xsl:for-each>
    <xsl:call-template name="itempostfix"/>
  </xsl:template>

  <xsl:template name="item-image">
    <tr>
      <td class="itemfields">
        <xsl:call-template name="itemfields"/>
      </td>
      <td class="itembuttons">
        <xsl:call-template name="itembuttons" />
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="item-other">
    <tr>
      <td colspan="2" class="itemfields">
        <xsl:call-template name="itemfields"/>
      </td>
      <td class="itembuttons">
        <xsl:call-template name="itembuttons" />
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="itempostfix">
    <tr class="itemline">
      <td colspan="3"/>
    </tr>
  </xsl:template>

  <xsl:template name="itembuttons">
    <table>
      <tr>
        <xsl:call-template name="mediaitembuttons" />
    <xsl:call-template name="selectitembuttons" />
<!-- REMOVE WHEN READONLY -->
    <xsl:if test="$READONLY=&apos;false&apos;">
<!-- END -->
        <xsl:call-template name="deleteitembuttons" />
        <xsl:call-template name="positembuttons" />
<!-- REMOVE WHEN READONLY -->
    </xsl:if>
<!-- END -->
      </tr>
    </table>
  </xsl:template>


   <!-- overide this one, because we want to be able to call for a pageselector -->
  <xsl:template name="listnewbuttons">
    <xsl:if test="command[@name=&apos;add-item&apos;]">
      <!-- only if less then maxoccurs -->
      <xsl:if test="not(@maxoccurs) or (@maxoccurs = &apos;*&apos;) or count(item) &lt; @maxoccurs">
        <!-- create action and startwizard command are present. Open the object into the start wizard -->
        <xsl:if test="command[@name=&apos;startwizard&apos;]">
          <xsl:for-each select="command[@name=&apos;startwizard&apos;]">
            <!-- The prompts.xsl adds this as a tooltip -->
            <!-- Moved prompt to the "prompt_add_wizard" template as a tooltip -->
            <xsl:choose>
              <xsl:when test="@wizardjsp">
                <a href="{$ew_context}{@wizardjsp}/?objectnumber=new&amp;origin={@origin}&amp;wizard={@wizardname}">
                  <xsl:call-template name="prompt_add_wizard" />
                </a>
              </xsl:when>
              <xsl:otherwise>
            <xsl:if test="@inline=&apos;true&apos;">
              <a href="javascript:doStartWizard(&apos;{../@fid}&apos;,&apos;{../command[@name=&apos;add-item&apos;]/@value}&apos;,&apos;{@wizardname}&apos;,&apos;{@objectnumber}&apos;,&apos;{@origin}&apos;);">
                <xsl:call-template name="prompt_add_wizard"/>
              </a>
            </xsl:if>
            <xsl:if test="not(@inline=&apos;true&apos;)">
              <a href="{$popuppage}&amp;fid={../@fid}&amp;did={../command[@name=&apos;add-item&apos;]/@value}&amp;popupid={@wizardname}_{@objectnumber}&amp;wizard={@wizardname}&amp;objectnumber={@objectnumber}&amp;origin={@origin}" target="_blank">
                <xsl:call-template name="prompt_add_wizard"/>
              </a>
            </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:if>
        <xsl:if test="command[@name=&apos;insert&apos;]">
          <xsl:for-each select="command[@name=&apos;add-item&apos;]">
            <a href="javascript:doAddInline(&apos;{@cmd}&apos;);">
              <xsl:call-template name="prompt_new"/>
            </a>
          </xsl:for-each>
        </xsl:if>
         <xsl:if test="command[@name=&apos;pageselector&apos;]">
             <xsl:for-each select="command[@name=&apos;pageselector&apos;]">
                  <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';window.open('../../../../editors/site/select/SelectorPage.do', 'pageselector', 'width=350,height=500,status=yes,toolbar=no,titlebar=no,scrollbars=yes,resizable=yes,menubar=no');">
                     <xsl:call-template name="prompt_search"/>
                  </a>
             </xsl:for-each>
         </xsl:if>
         <xsl:if test="command[@name=&apos;channelselector&apos;]">
             <xsl:for-each select="command[@name=&apos;channelselector&apos;]">
                  <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';window.open('../../../../editors/repository/select/SelectorContentChannel.do', 'channelselector', 'width=350,height=500,status=yes,toolbar=no,titlebar=no,scrollbars=yes,resizable=yes,menubar=no');">
                     <xsl:call-template name="prompt_search"/>
                  </a>
             </xsl:for-each>
         </xsl:if>
         <xsl:if test="command[@name=&apos;contentselector&apos;]">
             <xsl:for-each select="command[@name=&apos;contentselector&apos;]">
                <a href="#" onclick="select_fid='{../@fid}';select_did='{../command[@name=&apos;add-item&apos;]/@value}';window.open('../../../../editors/repository/SearchInitAction.do?action=selectforwizard', 'contentselector', 'width=1000,height=550,status=yes,toolbar=no,titlebar=no,scrollbars=yes,resizable=yes,menubar=no');">
                     <xsl:call-template name="prompt_search"/>
                  </a>
             </xsl:for-each>
         </xsl:if>         
      </xsl:if>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>