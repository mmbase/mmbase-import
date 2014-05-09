<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform" >

  <xsl:import href="ew:i18n/nl/xsl/prompts.xsl" />

  <xsl:variable name="tooltip_edit">Bekijk</xsl:variable>
  <xsl:template name="prompt_edit" ><img src="{$mediadir}bekijk.gif" hspace="3" alt="bekijk" /></xsl:template>
  
  <xsl:variable name="tooltip_preview">Preview</xsl:variable>
	<xsl:template name="prompt_preview" ><img src="{$mediadir}preview.gif" hspace="3" alt="preview" /></xsl:template>
  
  <xsl:variable name="tooltip_reload">Reload</xsl:variable>
	<xsl:template name="prompt_reload" ><img src="{$mediadir}reload.gif" hspace="3" alt="reload" /></xsl:template>

  <xsl:variable name="tooltip_sort_down">down</xsl:variable>
  <xsl:template name="prompt_sort_up">
    <img src="{$mediadir}sortup.png" alt="{$tooltip_up}" height="15" width="15" />
  </xsl:template>
  <xsl:template name="prompt_sort_down">
    <img src="{$mediadir}sortdown.png" alt="{$tooltip_up}" height="15" width="15" />
  </xsl:template>

	<xsl:template name="prompt_age">
    <xsl:param name="age" />
		<xsl:if test="$age=1"> van de laatste dag</xsl:if>
		<xsl:if test="$age=7"> van de laatste 7 dagen</xsl:if>
		<xsl:if test="$age=31"> van de afgelopen maand</xsl:if>
		<xsl:if test="$age=356"> van het afgelopen jaar</xsl:if>
		<xsl:if test="$age=-1"> over de hele cloud</xsl:if>
  </xsl:template>

	<xsl:template name="prompt_edit_list">
    <xsl:param name="age" />
    <xsl:value-of select="$title" disable-output-escaping="yes"  />
		<xsl:call-template name="prompt_age" >
			<xsl:with-param name="age" select="$age" />
		</xsl:call-template>
    (items <xsl:value-of select="/list/@offsetstart"/>-<xsl:value-of select="/list/@offsetend"/>/<xsl:value-of select="/list/@totalcount" />, pages <xsl:value-of select="/list/pages/@currentpage" />/<xsl:value-of select="/list/pages/@count" />)
  </xsl:template>
  
  <xsl:template name="prompt_edit_wizard">
	  <img alt="Wijzig" src="{$mediadir}edit.gif" class="imgbutton" >
		  <xsl:choose>
			  <xsl:when test="prompt">
				  <xsl:attribute name="title">
						<xsl:call-template name="i18n">
							<xsl:with-param name="nodes" select="prompt"/>
						</xsl:call-template>
					</xsl:attribute> 
				</xsl:when>
				<xsl:otherwise>
				  <xsl:attribute name="title">Bewerk dit item</xsl:attribute> 
				</xsl:otherwise>
			</xsl:choose>
		</img>
	</xsl:template>

  <xsl:template name="prompt_add_wizard">
    <img alt="Nieuw" src="{$mediadir}new.gif" class="imgbutton" >
		  <xsl:choose>
			  <xsl:when test="prompt">
				  <xsl:attribute name="title">
						<xsl:call-template name="i18n">
							<xsl:with-param name="nodes" select="prompt"/>
						</xsl:call-template>
					</xsl:attribute> 
				</xsl:when>
				<xsl:otherwise>
				  <xsl:attribute name="title">Maak een nieuw item en voeg het toe aan de lijst</xsl:attribute> 
				</xsl:otherwise>
			</xsl:choose>
		</img>
  </xsl:template>
	
  <xsl:template name="prompt_remove">
	  <img alt="Verwijder relatie" src="{$mediadir}relations_delete.gif" >
		  <xsl:choose>
			  <xsl:when test="../action[@type=&apos;delete&apos;]/prompt">
				  <xsl:attribute name="title">
						<xsl:call-template name="i18n">
							<xsl:with-param name="nodes" select="../action[@type=&apos;delete&apos;]/prompt"/>
						</xsl:call-template>
					</xsl:attribute> 
				</xsl:when>
				<xsl:otherwise>
				  <xsl:attribute name="title">Verwijder dit item uit de lijst</xsl:attribute> 
				</xsl:otherwise>
			</xsl:choose>
		</img>
	</xsl:template>

</xsl:stylesheet>
