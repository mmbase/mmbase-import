<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="ew:xsl/list.xsl"/>

  <xsl:template name="style">
    <!-- remove old style -->
  </xsl:template>

  <xsl:template name="colorstyle">
    <!-- remove old style -->
  </xsl:template>

  <xsl:template name="title">
    <td>
			<xsl:value-of select="$wizardtitle" /><br />
			<a href="{$listpage}&amp;remove=true" title="Terug naar de startpagina"><img src="{$mediadir}button_right_small.gif" /> Terug naar de startpagina</a>
    </td>
  </xsl:template>
	
  <xsl:template name="searchbox">
    <xsl:if test="$searchfields!=&apos;&apos;">
			<tr class="searchcanvas">
				<td>
					<table class="searchcontent" cellspacing="0" cellpadding="0">
						<tr class="header">
								<td class="header">
                    <xsl:call-template name="prompt_search_list" />
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    <xsl:value-of disable-output-escaping="yes" select="$title"/>:
								</td>
						</tr>
						<tr class="options">
							<td class="options">
                <form>
									<xsl:if test="$creatable=&apos;true&apos;">
										<a href="{$wizardpage}&amp;referrer={$referrer_encoded}&amp;wizard={$wizard}&amp;objectnumber=new&amp;origin={$origin}">
											<xsl:call-template name="prompt_new" />
										</a>
									</xsl:if>
									
                  leeftijd: <xsl:call-template name="search-age" />
                  <select name="realsearchfield">
                    <option value="{$searchfields}">
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
                </form>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </xsl:if>
		<tr class="headsubtitle">
			<xsl:call-template name="subtitle" />
		</tr>	
  </xsl:template>

  <xsl:template name="subtitle">
    <td>
      <div title="{$tooltip_edit_list}" class="spacer">
        <xsl:call-template name="prompt_edit_list" >
				  <xsl:with-param name="age" select="$age" />
				</xsl:call-template>
      </div>
    </td>
  </xsl:template>
	
  <xsl:template match="object">
    <tr>
      <xsl:if test="@mayedit=&apos;true&apos;">
        <xsl:attribute name="class">itemrow</xsl:attribute>
      </xsl:if>
      <xsl:if test="@mayedit=&apos;false&apos;">
        <xsl:attribute name="class">itemrow-disabled</xsl:attribute>
      </xsl:if>
			<td class="buttons">
      	<xsl:if test="@mayedit=&apos;true&apos;">
            <a
              href="{$wizardpage}&amp;wizard={$wizard}&amp;objectnumber={@number}&amp;origin={@origin}"
              title="{$tooltip_edit}">
              <xsl:call-template name="prompt_edit" />
            </a>
						<xsl:if test="$previewbase">
							<a href="{$previewbase}preview"
								 title="{$tooltip_preview}"> 
								 <xsl:call-template name="prompt_preview" /></a>
							<a href="{$previewbase}reload"
								 title="{$tooltip_reload}">
								 <xsl:call-template name="prompt_reload" /></a>
						</xsl:if>
			  </xsl:if>
        <xsl:if test="$deletable=&apos;true&apos;">
            <a
              href="{$deletepage}&amp;wizard={$wizard}&amp;objectnumber={@number}"
              title="{$tooltip_delete}"
              onmousedown="cancelClick=true;"
              onclick="return doDelete(&apos;{$deleteprompt}&apos;);">
              <xsl:call-template name="prompt_delete" />
            </a>
				</xsl:if>
			</td>
      <td class="number">
        <xsl:value-of select="@index" />
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      </td>
      <xsl:apply-templates select="field" />
    </tr>
  </xsl:template>
	
  <xsl:template name="dolist">
    <table class="listcontent" cellspacing="0" cellpadding="0">
      <xsl:if test="object[@number>0]">
        <tr class="listheader">
					<th></th>
          <th class="listheader">#</th>
          <xsl:for-each select="object[1]/field">
            <th class="listheader">
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

</xsl:stylesheet> 
