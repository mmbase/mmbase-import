<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
  version="1.0"
>
  <!--
  list.xls
  @since  MMBase-1.6
  @author Kars Veling
  @author Michiel Meeuwissen
  @version $Id: list.xsl,v 1.26 2003-05-01 17:32:48 pierre Exp $
  -->

  <xsl:import href="xsl/baselist.xsl" />

  <xsl:param name="deletable">false</xsl:param>
  <xsl:param name="creatable">true</xsl:param>

  <xsl:param name="deleteprompt"><xsl:call-template name="prompt_delete_confirmation" /></xsl:param>
  <xsl:param name="deletedescription"><xsl:value-of select="$tooltip_delete" /></xsl:param>

  <xsl:param name="title"><xsl:value-of select="list/object/@guitype" /></xsl:param>

	<xsl:param name="age"></xsl:param>
	<xsl:param name="searchvalue"></xsl:param>
	<xsl:param name="searchtype">like</xsl:param>
	<xsl:param name="start"></xsl:param>
	<xsl:param name="fields"></xsl:param>
	<xsl:param name="searchfields" ></xsl:param>
	<xsl:param name="nodepath"></xsl:param>
	<xsl:param name="startnodes"></xsl:param>
	<xsl:param name="orderby"></xsl:param>
	<xsl:param name="directions"></xsl:param>
	<xsl:param name="searchdir"></xsl:param>
	<xsl:param name="constraints"></xsl:param>
	<xsl:param name="distinct"></xsl:param>

	
  <!-- ================================================================================
       The following things can be overriden to customize the appearance of list
       ================================================================================ -->

  <xsl:template name="style"> <!-- It can be usefull to add a style, change the title -->
    <title><xsl:value-of select="$wizardtitle" /> - <xsl:value-of select="$title" /></title>
    <link rel="stylesheet" type="text/css" href="../style/list.css" />
    <xsl:call-template name="extrastyle" /> <!-- override base.xsl for this -->
  </xsl:template>

  <xsl:template name="body"> <!-- You can put stuff before and after then. Don't forget to call 'bodycontent' -->
    <body onload="window.focus();">
      <xsl:call-template name="bodycontent" />
    </body>
  </xsl:template>

  <xsl:template name="title"><!-- The first row of the the body's table -->
    <tr>
      <th colspan="2">
        <span class="title"><nobr>
        <xsl:value-of select="$wizardtitle" />
        </nobr></span>
      </th>
    </tr>
  </xsl:template>

	<xsl:template name="searchbox">
		<xsl:if test="$searchfields!=''">
			<tr>
				<td class="left"></td>
				<td class="searchcanvas">
					<table>
						<tr>
							<xsl:if test="$creatable='true'">
								<td width="250" valign="top">
									<span class="header">
										<xsl:value-of select="$title"/> :</span>
									<br/>
									<a href="/editwizard/jsp/wizard.jsp?referrer={$referrer}&amp;wizard={$wizard}&amp;objectnumber=new">
										<img src="/editwizard/media/new.gif" width="20" height="20" hspace="2" align="absmiddle" alt="" border="0"/>
									</a>
								</td>
							</xsl:if>
							<td valign="top">
								<form action="list.jsp">
									<span class="header">ZOEK <xsl:value-of select="$title"/>:</span>
									<br/>
									<select name="age" class="input" style="width:80px;">
										<option value="1">
											<xsl:if test="$age='1'">
												<xsl:attribute name="selected">selected</xsl:attribute>
											</xsl:if>1 dag</option>
										<option value="7">
											<xsl:if test="$age='7'">
												<xsl:attribute name="selected">selected</xsl:attribute>
											</xsl:if>1 week</option>
										<option value="31">
											<xsl:if test="$age='31'">
												<xsl:attribute name="selected">selected</xsl:attribute>
											</xsl:if>1 maand</option>
										<option value="365">
											<xsl:if test="$age='365'">
												<xsl:attribute name="selected">selected</xsl:attribute>
											</xsl:if>1 jaar</option>
										<option value="-1">
											<xsl:if test="$age='-1'">
												<xsl:attribute name="selected">selected</xsl:attribute>
											</xsl:if>alles</option>
									</select>
									<input type="hidden" name="proceed" value="true" />
									<input type="hidden" name="sessionkey" value="{$sessionkey}" />
									<input type="hidden" name="language" value="${language}" />
									<input type="text" name="searchvalue" value="{$searchvalue}" class="input" style="width:200px;"/>
									<input type="image" src="/editwizard/media/search.gif" width="20" height="20" align="absmiddle" alt="" hspace="2" border="0"/>
									<br/>
									<span class="onderschrift"> (leeftijd)<img src="/editwizard/media/nix.gif" width="45" height="1" alt="" border="0"/>(titel)</span>
									<br/>
								</form>
							</td>
						</tr>
						<tr>
							<td colspan="2"><hr /></td>
						</tr>
					</table>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
  <xsl:template name="bodycontent">
    <!-- I think all elements must have a class here, then you can customize then the appearance by putting another css -->
    <table class="body">
      <xsl:call-template name="title" />
      <xsl:call-template name="searchbox" />
      <tr>
        <td class="left"></td>
        <td class="listcanvas">
          <div title="{$tooltip_edit_list}" class="subhead">
            <nobr><xsl:call-template name="prompt_edit_list" /></nobr>
          </div>
          <br />
          <xsl:call-template name="dolist" />
					<xsl:if test="$searchfields='' and $creatable='true'">
            <br />
            <div width="100%" align="right">
              <a href="{$wizardpage}&amp;wizard={$wizard}&amp;objectnumber=new&amp;origin={$origin}" title="{$tooltip_new}"><xsl:call-template name="prompt_new" /></a>
            </div>
          </xsl:if>
        </td>
      </tr>
			<xsl:if test="count(/*/pages/page)>1">
      <tr>
        <td class="left" />
        <td>
          <div>
            <xsl:apply-templates select="/*/pages" /><br /><br />
          </div>
        </td>
      </tr>
			</xsl:if>
      <tr >
        <td class="left" />
        <td class="itemrow" >
        <a href="{$listpage}&amp;remove=true" title="{$tooltip_index}"><xsl:call-template name="prompt_index"/></a>
        -
        <a href="{$listpage}&amp;logout=true&amp;remove=true" title="{$tooltip_logout}"><xsl:call-template name="prompt_logout"/></a>
        </td>
      </tr>
    </table>
  </xsl:template>


  <!-- javascript, I don't know if you want to override _that_...  -->
  <xsl:template name="javascript">
    <script language="javascript">
      <xsl:text disable-output-escaping="yes"><![CDATA[<!--
   window.history.forward(1);
   var cancelClick = false;

   function objMouseOver(el) {
   el.className="itemrow-hover";
   }

   function objMouseOut(el) {
   el.className="itemrow";
   }

   function objClick(el) {
   if (cancelClick) {
   cancelClick=false;
   return;
   }
   var href = el.getAttribute("href")+"";
   if (href.length<10) return;
   document.location=href;
   }

   function doDelete(prompt) {
   var conf;
   if (prompt && prompt!="") {
   conf = confirm(prompt);
   } else conf=true;
   cancelClick=true;
   return conf;
   }
   -->
]]></xsl:text>
      </script>
      <xsl:call-template name="extrajavascript" />
  </xsl:template>

  <!-- ================================================================================
       The following is functionality. You probably don't want to override it.
       ================================================================================ -->

  <xsl:template match="pages">
    <span class="pagenav">
      <xsl:choose>
        <xsl:when test="page[@previous='true']">
          <a class="pagenav" title="{$tooltip_previous}{@currentpage-1}" href="{$listpage}&amp;start={page[@previous='true']/@start}"><xsl:call-template name="prompt_previous" /></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="prompt_previous" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:apply-templates select="page" />

      <xsl:choose>
        <xsl:when test="page[@next='true']">
          <a class="pagenav" title="{$tooltip_next}{@currentpage+1}" href="{$listpage}&amp;start={page[@next='true']/@start}"><xsl:call-template name="prompt_next" /></a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="prompt_next" />
        </xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>

  <xsl:template match="page">
    <a class="pagenav" title="{$tooltip_goto}{position()}" href="{$listpage}&amp;start={@start}"><xsl:value-of select="position()" /></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
  </xsl:template>


  <xsl:template match="page[@current='true']">
    <xsl:value-of select="position()" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
  </xsl:template>

  <xsl:template match="object">
     <tr>
         <xsl:if test="@mayedit='true'">
           <xsl:attribute name="class">itemrow</xsl:attribute>
           <xsl:attribute name="onMouseOver">objMouseOver(this);</xsl:attribute>
           <xsl:attribute name="onMouseDown">objClick(this);</xsl:attribute>
           <xsl:attribute name="onMouseOut">objMouseOut(this);</xsl:attribute>
           <xsl:attribute name="href"><xsl:value-of select="$wizardpage" />&amp;wizard=<xsl:value-of select="$wizard" />&amp;objectnumber=<xsl:value-of select="@number" />&amp;origin=<xsl:value-of select="$origin" /></xsl:attribute>
         </xsl:if>
         <xsl:if test="@mayedit='false'">
           <xsl:attribute name="class">itemrow-disabled</xsl:attribute>
         </xsl:if>
         <xsl:if test="$deletable='true'">
           <td class="deletebutton">
           <xsl:if test="@maydelete='true'">
            <a href="{$deletepage}&amp;wizard={$wizard}&amp;objectnumber={@number}" title="{$deletedescription}" onmousedown="cancelClick=true;" onclick="return doDelete('{$deleteprompt}');" ><xsl:call-template name="prompt_delete" /></a>
           </xsl:if>
           </td>
         </xsl:if>
         <td class="number"><xsl:value-of select="@index" />.<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
         </td>
         <xsl:apply-templates select="field" />
     </tr>
  </xsl:template>


  <xsl:template name="dolist"><!-- returns the actual list as a table -->
    <table>
      <xsl:if test="object[@number&gt;0]">
        <tr>
          <xsl:if test="$deletable='true'">
            <th></th>
          </xsl:if>
          <th class="number">#</th>
          <xsl:for-each select="object[1]/field">
            <th>
              <xsl:value-of select="@name" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </th>
          </xsl:for-each>
        </tr>
      </xsl:if>
      <xsl:apply-templates select="object[@number&gt;0]" />
    </table>
  </xsl:template>


  <xsl:template match="list"><!-- this it the entry-point, I suppose -->
    <html>
      <head>
        <meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8" /><!-- We require doing everything in UTF-8 -->
        <xsl:call-template name="style" />
        <xsl:call-template name="javascript" />
      </head>
      <xsl:call-template name="body" />
    </html>
  </xsl:template>

  <xsl:template match="field">
    <xsl:if test="position()>1">
      <td>
        <nobr><xsl:value-of disable-output-escaping="yes" select="." /></nobr>
      </td>
    </xsl:if>
    <xsl:if test="position()=1">
      <td>
        <xsl:value-of select="." /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      </td>
      </xsl:if>
  </xsl:template>

</xsl:stylesheet>
