<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!--
    list.xls

    @since  MMBase-1.6
    @author Kars Veling
    @author Michiel Meeuwissen
    @author Nico Klasens
    @version $Id: list.xsl,v 1.1 2005-11-28 10:09:29 nklasens Exp $
  -->

  <xsl:import href="xsl/baselist.xsl" />

  <xsl:param name="deletable">false</xsl:param>
  <xsl:param name="creatable">true</xsl:param>

  <xsl:param name="deleteprompt">
    <xsl:call-template name="prompt_delete_confirmation" />
  </xsl:param>
  <xsl:param name="deletedescription">
    <xsl:value-of select="$tooltip_delete" />
  </xsl:param>

  <xsl:param name="createprompt" />

  <xsl:param name="age" />
  <xsl:param name="searchvalue" />
  <xsl:param name="searchtype">like</xsl:param>
  <xsl:param name="start" />
  <xsl:param name="fields" />
  <xsl:param name="searchfields" />
  <xsl:param name="realsearchfield" />
  <xsl:param name="nodepath" />
  <xsl:param name="startnodes" />
  <xsl:param name="orderby" />
  <xsl:param name="directions" />
  <xsl:param name="searchdir" />
  <xsl:param name="constraints" />
  <xsl:param name="distinct" />
  <xsl:param name="objecttype" />

  <xsl:variable name="BodyOnLoad">window.focus();</xsl:variable>

  <xsl:template name="javascript">
    <script type="text/javascript" src="{$javascriptdir}list.js">
      <xsl:comment>help IE</xsl:comment>
    </script>
  </xsl:template>

  <xsl:template name="htmltitle">
    <xsl:value-of select="$wizardtitle" /> - <xsl:value-of disable-output-escaping="yes" select="$title" />
  </xsl:template>

  <xsl:template name="style">
    <link rel="stylesheet" type="text/css" href="{$cssdir}layout/list.css" />
  </xsl:template>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$cssdir}color/list.css" />
  </xsl:template>


  <xsl:template name="title">
    <td>
      <xsl:value-of select="$wizardtitle" />
    </td>
  </xsl:template>

  <xsl:template name="subtitle">
    <td>
      <div title="{$tooltip_edit_list}">
        <xsl:call-template name="prompt_edit_list" >
          <xsl:with-param name="age" select="$age" />
          <xsl:with-param name="searchvalue" select="$searchvalue" />
        </xsl:call-template>
      </div>
    </td>
  </xsl:template>

  <xsl:template name="body">
    <xsl:apply-templates select="list" />
  </xsl:template>

  <xsl:template match="list">
    <xsl:call-template name="searchbox" />
    <tr class="listcanvas">
      <td>
        <xsl:call-template name="dolist" />
      </td>
    </tr>
    <xsl:if test="count(/*/pages/page) &gt; 1">
      <tr class="pagescanvas">
        <td>
          <div>
            <xsl:apply-templates select="/*/pages" />
            <br />
            <br />
          </div>
        </td>
      </tr>
    </xsl:if>
    <tr class="buttoncanvas">
      <td>
        <xsl:if test="$searchfields=&apos;&apos; and $creatable=&apos;true&apos;">
          <br />
          <div width="100%" align="left">
            <xsl:if test="$createprompt">
              <div style="width: 200px;">
                <xsl:value-of select="$createprompt" />
              </div>
            </xsl:if>
            <a
              href="{$wizardpage}&amp;wizard={$wizard}&amp;objectnumber=new&amp;origin={$origin}"
              title="{$tooltip_new}">
              <xsl:call-template name="prompt_new" />
            </a>
          </div>
        </xsl:if>
      </td>
    </tr>
    <tr class="linkcanvas">
      <td>
        <a href="{$listpage}&amp;remove=true" title="{$tooltip_index}">
          <xsl:call-template name="prompt_index" />
        </a>
        <xsl:text> - </xsl:text>
        <a href="{$listpage}&amp;logout=true&amp;remove=true" title="{$tooltip_logout}">
          <xsl:call-template name="prompt_logout" />
        </a>
      </td>
    </tr>
  </xsl:template>

  <!-- The search-box it the thing which appears on the top of a 'list' page.
    In your extension you must create a tr with a form to list.jsp (so, the form must provide
    list.jsp post arguments)
  -->
  <xsl:template name="searchbox">
    <xsl:if test="$searchfields!=&apos;&apos;">
      <tr class="searchcanvas">
        <td>
          <table class="searchcontent">
            <tr>
              <xsl:if test="$creatable=&apos;true&apos;">
                <td>

                  <xsl:if test="$createprompt">
                    <div style="width: 200px;">
                      <xsl:value-of select="$createprompt" />
                    </div>
                  </xsl:if>
                  <a href="{$wizardpage}&amp;referrer={$referrer_encoded}&amp;wizard={$wizard}&amp;objectnumber=new&amp;origin={$origin}">
                    <xsl:call-template name="prompt_new" />
                  </a>
                </td>
              </xsl:if>
              <td>
                <form>
                  <span class="header">
                    <xsl:call-template name="prompt_search_list" />
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    <xsl:value-of disable-output-escaping="yes" select="$title"/>:
                  </span>
                  <br />

                  <xsl:call-template name="search-age" />

                  <select name="realsearchfield">
                    <option value="{$searchfields}">
                      <xsl:if test="$realsearchfield=$searchfields" >
                        <xsl:attribute name="selected">selected</xsl:attribute>
                      </xsl:if>
                      <xsl:call-template name="prompt_search_title" />
                    </option>
                    <xsl:call-template name="search-fields-default" />
                  </select>

                  <input type="hidden" name="proceed" value="true" />
                  <input type="hidden" name="sessionkey" value="{$sessionkey}" />
                  <input type="hidden" name="language" value="${language}" />
                  <input type="text" name="searchvalue" value="{$searchvalue}" class="search" />

                    <a href="javascript:document.forms[0].submit();">
                      <xsl:call-template name="prompt_search" />
                    </a>
                    <br />
                  <span class="subscript">
                    ( <xsl:call-template name="prompt_age" /> )
                    ( <xsl:call-template name="prompt_search_term" /> )
                  </span>
                </form>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template name="search-age">
    <xsl:choose>
      <xsl:when test="$searchagetype=&apos;none&apos;"></xsl:when>
      <xsl:when test="$searchagetype=&apos;edit&apos;">
        <input
          type="text"
          name="age"
          class="age"
          value="{$age}" />
      </xsl:when>
      <xsl:otherwise>
        <select name="age" class="input">
          <xsl:call-template name="searchageoptions">
            <xsl:with-param name="selectedage" select="$age" />
          </xsl:call-template>
        </select>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="search-fields-default">
    <xsl:if test="$objecttype=&apos;&apos;">
      <option value="number">
        <xsl:if test="$realsearchfield=&apos;number&apos;" >
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <xsl:call-template name="prompt_search_number" />
      </option>
      <option value="owner">
        <xsl:if test="$realsearchfield=&apos;owner&apos;" >
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <xsl:call-template name="prompt_search_owner" />
      </option>
    </xsl:if>
    <xsl:if test="$objecttype!=&apos;&apos;">
      <option value="{$objecttype}.number">
        <xsl:if test="$realsearchfield=concat($objecttype,&apos;.number&apos;)" >
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <xsl:call-template name="prompt_search_number" />
      </option>
      <option value="{$objecttype}.owner">
        <xsl:if test="$realsearchfield=concat($objecttype,&apos;.owner&apos;)" >
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <xsl:call-template name="prompt_search_owner" />
      </option>
    </xsl:if>
  </xsl:template>

  <xsl:template name="dolist">
    <table class="listcontent">
      <xsl:if test="object[@number>0]">
        <tr class="listheader">
          <xsl:if test="$deletable=&apos;true&apos;">
            <th/>
          </xsl:if>
          <th>#</th>
          <xsl:for-each select="object[1]/field">
            <th>
              <xsl:value-of select="@name" />
              <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
              <a class="pagenav"
                 title="{$tooltip_sort_on} {@name} ({$tooltip_sort_up})"
                 href="{$listpage}&amp;orderby={@fieldname}&amp;directions=UP">
               <xsl:call-template name="prompt_sort_up" />
              </a>
              <a class="pagenav"
                 title="{$tooltip_sort_on} {@name} ({$tooltip_sort_down})"
                 href="{$listpage}&amp;orderby={@fieldname}&amp;directions=DOWN">
               <xsl:call-template name="prompt_sort_down" />
             </a>
            </th>
          </xsl:for-each>
        </tr>
      </xsl:if>
      <xsl:apply-templates select="object[@number>0]" />
    </table>
  </xsl:template>

  <xsl:template match="pages">
    <span class="pagenav">
      <xsl:if test="page[@previous=&apos;true&apos;]">
        <a
          class="pagenav"
          title="{$tooltip_previous}{@currentpage-1}"
          href="{$listpage}&amp;start={page[@previous=&apos;true&apos;]/@start}">
          <xsl:call-template name="prompt_previous" />
        </a>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      </xsl:if>

      <xsl:apply-templates select="page" />

      <xsl:if test="page[@next=&apos;true&apos;]">
        <a
          class="pagenav"
          title="{$tooltip_next}{@currentpage+1}"
          href="{$listpage}&amp;start={page[@next=&apos;true&apos;]/@start}">
          <xsl:call-template name="prompt_next" />
        </a>
      </xsl:if>
    </span>
  </xsl:template>

  <xsl:template match="page">
    <a class="pagenav" title="{$tooltip_goto}{position()}" href="{$listpage}&amp;start={@start}">
      <xsl:value-of select="@number" />
    </a>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
  </xsl:template>

  <xsl:template match="page[@current=&apos;true&apos;]">
    <xsl:value-of select="@number" />
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
  </xsl:template>

  <xsl:template match="object">
    <tr>
      <xsl:if test="@mayedit=&apos;true&apos;">
        <xsl:attribute name="class">itemrow</xsl:attribute>
        <xsl:attribute name="onMouseOver">objMouseOver(this);</xsl:attribute>
        <xsl:attribute name="onMouseDown">objClick(this);</xsl:attribute>
        <xsl:attribute name="onMouseOut">objMouseOut(this);</xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="$wizardpage" />&amp;wizard=<xsl:value-of select="$wizard" />&amp;objectnumber=<xsl:value-of select="@number" />&amp;origin=<xsl:value-of select="$origin" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@mayedit=&apos;false&apos;">
        <xsl:attribute name="class">itemrow-disabled</xsl:attribute>
      </xsl:if>
      <xsl:if test="$deletable=&apos;true&apos;">
        <td class="deletebutton">
          <xsl:if test="@maydelete=&apos;true&apos;">
            <a
              href="{$deletepage}&amp;wizard={$wizard}&amp;objectnumber={@number}"
              title="{$deletedescription}"
              onmousedown="cancelClick=true;"
              onclick="return doDelete(&apos;{$deleteprompt}&apos;);">
              <xsl:call-template name="prompt_delete" />
            </a>
          </xsl:if>
        </td>
      </xsl:if>
      <td class="number">
        <xsl:value-of select="@index" />
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      </td>
      <xsl:apply-templates select="field" />
    </tr>
  </xsl:template>

  <xsl:template match="field">
    <td class="field">
      <xsl:if test="position() &gt; 1">
        <nobr>
          <xsl:call-template name="writeCurrentField" />
        </nobr>
      </xsl:if>
      <xsl:if test="position()=1">
        <xsl:call-template name="writeCurrentField" />
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      </xsl:if>
    </td>
  </xsl:template>

</xsl:stylesheet>
