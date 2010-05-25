<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
  Demonstrating overriding of standard list.xsl

  @version   $Id: list.xsl,v 1.1 2008-08-06 16:17:13 michiel Exp $
  @author    Michiel Meeuwissen
  @since     MMBase-1.6

  -->
  <xsl:import href="ew:xsl/list.xsl"/>

  <xsl:template name="colorstyle">
    <!-- TODO relative resolving with blocks ? -->
    <link rel="stylesheet" type="text/css" href="{$ew_context}/mmbase/components/editwizard/advanced/style/color/list.css" />
  </xsl:template>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css"  href="{$ew_context}/mmbase/components/editwizard/advanced/style/extra/list.css" />
  </xsl:template>

  <xsl:variable name="searchagetype">edit</xsl:variable>

 <!-- extend from standard  editwizard xslt -->

  <xsl:template name="title">
    <td class="mysteps_top">
      <span class="title">
        <nobr>
          <a href="{$listpage}&amp;remove=true" class="tools">Redactietools:</a>
        </nobr>
      </span>
      <span class="titleprompt" ><xsl:value-of select="$title" /></span>
    </td>
    <td width="200" class="gutter">
      <br/>
    </td>
  </xsl:template>

  <xsl:template name="subtitle"/>

  <xsl:template name="dolist"><!-- returns the actual list as a table -->
    <table class="listcontent">
      <xsl:apply-templates select="object[@number&gt;0]" />
    </table>
  </xsl:template>

  <xsl:template match="object">
    <tr>
      <xsl:apply-templates select="field" />
      <xsl:if test="@mayedit='true'">
        <td>
          <nobr>
            <a>
              <xsl:attribute name="href"><xsl:value-of select="$wizardpage" />&amp;wizard=<xsl:value-of select="$wizard" />&amp;objectnumber=<xsl:value-of select="@number" />&amp;origin=<xsl:value-of select="$origin" /></xsl:attribute>
              Bewerk
            </a>
            <xsl:if test="$deletable='true'">
              <xsl:if test="@maydelete='true'">
                /<a href="{$deletepage}&amp;wizard={$wizard}&amp;objectnumber={@number}" title="{$deletedescription}" onmousedown="cancelClick=true;" onclick="return doDelete('{$deleteprompt}');" >Verwijder</a>
              </xsl:if>
            </xsl:if>
          </nobr>
        </td>
      </xsl:if>
      <xsl:if test="@mayedit='false'">
        <td></td>
      </xsl:if>
    </tr>
  </xsl:template>

</xsl:stylesheet>
