<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 25-07-2003
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

<!-- OVERRIDE PROMPTS.XSL
	The prompts.xsl can not be extended, bacause that will break
	the i18n prompts support.
-->

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

  <xsl:variable name="BodyOnLoad">preLoadButtons(); doOnLoad_ew(); start_validator();  startHtmlArea();</xsl:variable>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}/mmbase/components/editwizard/finalist/style/color/wizard.css" />
  </xsl:template>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}/mmbase/components/editwizard/finalist/style/extra/wizard.css" />
  </xsl:template>

  <xsl:template name="extrajavascript">
    <script language="javascript" src="{$ew_context}/mmbase/components/editwizard/finalist/javascript/override.js"><xsl:comment>help IE</xsl:comment></script>
  </xsl:template>

  <xsl:template name="buttons">
		<table class="buttonscontent">
			<tr>
				<td>
						<nobr>
							<!-- cancel -->
							<xsl:call-template name="cancelbutton" />
							<!-- saveonly  -->
							<xsl:call-template name="saveonlybutton" />
							<!-- commit  -->
							<xsl:call-template name="savebutton" />
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

  <xsl:template match="field">
		<td class="fieldprompt">
			<xsl:call-template name="prompt" />
		</td>
		<td class="field">
			<xsl:call-template name="fieldintern" />
		</td>
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
							<td class="listnewbuttons">
								<xsl:call-template name="listnewbuttons" />
							</td>

							<!-- SEARCH input and button -->
							<td class="listsearch">
								<xsl:call-template name="listsearch" />
							</td>
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
			<td>
				<!-- the image -->
				<img src="{node:function($cloud, string(field/@number), concat('servletpath(', $cloudkey, ',cache(', $imagesize, '))'))}" hspace="0" vspace="0" border="0" title="{field[@name='description']}"/>
			</td>
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

</xsl:stylesheet>
