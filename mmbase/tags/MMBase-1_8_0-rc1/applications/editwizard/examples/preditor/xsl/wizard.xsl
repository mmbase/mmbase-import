<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">

  <xsl:import href="ew:xsl/wizard.xsl" /> <!-- extend from standard  editwizard xslt -->

  <xsl:template name="style">
    <!-- remove old style -->
  </xsl:template>

  <xsl:template name="colorstyle">
    <!-- remove old style -->
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

  <xsl:template name="subtitle">
    <td colspan="2">
			<span class="valid" >
			  <xsl:call-template name="i18n">
          <xsl:with-param name="nodes" select="//wizard/form[@id=/wizard/curform]/title"/>
        </xsl:call-template>
			</span>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			<span class="field">
				<xsl:call-template name="i18n">
					<xsl:with-param name="nodes" select="//wizard/form[@id=/wizard/curform]/subtitle"/>
				</xsl:call-template>
		  </span>
    </td>
  </xsl:template>
	
 <!-- The first row of the the body's table -->
  <xsl:template name="title">
      <td>        
				<xsl:value-of select="$wizardtitle" />
      </td>
     <td>
        <xsl:if test="$debug='true'"> <a href="debug.jsp{$sessionid}?sessionkey={$sessionkey}&amp;popupid={$popupid}" target="_blank" class="step">[debug]</a></xsl:if>
     </td>
  </xsl:template>
  
  <xsl:template name="formcontent">
    <div id="commandbuttonbar" class="buttonscontent">
      <xsl:for-each select="/*/steps-validator">
        <xsl:call-template name="buttons"/>
      </xsl:for-each>
    </div>
    <div id="stepsbar" class="stepscontentbottom">
      <xsl:apply-templates select="/*/steps-validator"/>
    </div>
		<br />
    <div id="editform" class="editform">
      <table class="formcontent">
				<tr><td colspan="2" class="headerbasis"><xsl:call-template name="htmltitle"/></td></tr>		
        <xsl:apply-templates select="form[@id=/wizard/curform]"/>
      </table>
    </div>
    <div id="stepsbar" class="stepscontentbottom">
      <xsl:apply-templates select="/*/steps-validator"/>
    </div>
    <div id="commandbuttonbar" class="buttonscontent">
      <xsl:for-each select="/*/steps-validator">
        <xsl:call-template name="buttons"/>
      </xsl:for-each>
    </div>
  </xsl:template>
	
  <xsl:template name="extrajavascript" >
	  <script type="text/javascript">
      <xsl:text disable-output-escaping="yes">
        <![CDATA[
      <!--
        // Override, do not resize (silly but needed)
        function resizeEditTable() { 
				}
			// -->
      ]]></xsl:text>
		</script>
	</xsl:template>
	
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
						<xsl:apply-templates select="."/>
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
	
  <xsl:template match="list">
		<td colspan="2" class="related">
			<div class="related_title">
				<xsl:call-template name="listprompt"/>
		  </div>
			<table class="listcontent">
				<tr>
					<td class="itemfields">
            <xsl:call-template name="listitems"/>
				  </td>
				</tr>
			</table>
		</td>
		<td>
      <xsl:if test="command[@name=&apos;add-item&apos; or @name=&apos;search&apos;]">
				<table class="listcontent">
					<tr>
						<td class="listprompt">
							<xsl:call-template name="listnewbuttons"/>
							<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
							<xsl:call-template name="listnewprompt"/>
						</td>
					</tr>
					<tr>
						<td class="listsearch">
							<xsl:call-template name="listsearch"/>
						</td>
					</tr>
				</table>
				<p><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></p>
		  </xsl:if>
		</td>
  </xsl:template>

	<!-- -requires updates in the Wizard.java class, not checked in yet 8/4/2005 -->
  <xsl:template name="listnewprompt">
		<xsl:call-template name="i18n">
			<xsl:with-param name="nodes" select="@title"/>
		</xsl:call-template>
  </xsl:template>

  <xsl:template name="listitems">
    <xsl:if test="item">
			<table class="itemcontent" >
				<xsl:apply-templates select="item"/>
			</table>
    </xsl:if>
  </xsl:template>

  <xsl:template name="itembuttons">
    <table style="float:right;">
      <tr>
        <xsl:call-template name="mediaitembuttons"/>
        <xsl:call-template name="selectitembuttons"/>
        <xsl:call-template name="deleteitembuttons"/>
        <xsl:call-template name="positembuttons"/>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="itemfields-hover" />

  <xsl:template name="listsearch">
    <!-- if 'add-item' command and a search, then make a search util-box -->
    <xsl:if test="command[@name=&apos;add-item&apos;]">
      <xsl:for-each select="command[@name=&apos;search&apos;]">
        <table class="searchcontent">
          <tr>
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
              <span title="{$tooltip_search}" onClick="doSearch(this,&apos;{../command[@name=&apos;add-item&apos;]/@cmd}&apos;,&apos;{$sessionkey}&apos;);">
                <xsl:for-each select="@*">
                  <xsl:copy/>
                </xsl:for-each>
                <xsl:call-template name="prompt_search"/>
              </span>
            </td>
          </tr>
        </table>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>