<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards for workflow.

   Author: Nico Klasens
-->
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  xmlns:date="org.mmbase.bridge.util.xml.DateFormat"
  extension-element-prefixes="node date">

   <xsl:include href="templatesi18n:xsl/prompts-workflow.xsl"/>
  
<!-- PARAMETERS PASSED IN BY THE TRANSFORMER. 
     WE PASSED A MAP WITH KEY VALUE PAIRS TO THE TRANSFORMER
 -->

  <xsl:param name="ACTIVITY">DRAFT</xsl:param>
  <xsl:param name="WORKFLOW">false</xsl:param>
  <xsl:param name="WORKFLOW-ACCEPTED-ENABLED">false</xsl:param>
<!--
  END
-->  
   <xsl:template name="formhiddenargs">
 		<input type="hidden" name="workflowcommand" value="" id="workflowcommand" />
		<input type="hidden" name="workflowcomment" value="" id="workflowcomment" />
   </xsl:template>

  <xsl:template name="beforeform">
    <xsl:if test="$READONLY=&apos;true&apos;">
      <xsl:if test="$READONLY-REASON=&apos;WORKFLOW&apos;">
        <p class="readonly-reason"> <xsl:value-of select="$REASON-WORKFLOW"/> </p>
      </xsl:if>
      <xsl:if test="$READONLY-REASON=&apos;PUBLISH&apos;">
         <p class="readonly-reason"> <xsl:value-of select="$REASON-PUBLISH"/> </p>
      </xsl:if>
      <xsl:if test="$READONLY-REASON=&apos;RIGHTS&apos;">
        <p class="readonly-reason"> <xsl:value-of select="$REASON-RIGHTS"/> </p>
      </xsl:if>
    </xsl:if>
   </xsl:template>

  <xsl:template name="buttons-extended">
  <!-- EXTRA WORKFLOW KNOWLODGE -->
	<xsl:if test="$WORKFLOW=&apos;true&apos;">
		<xsl:if test="$CHIEFEDITOR=&apos;true&apos;">
			<xsl:text disable-output-escaping="yes"></xsl:text>
		    <div class="button">
		      <div class="button_body">
			    <xsl:call-template name="publishbutton" />
			  </div>
			</div>
		</xsl:if>                
	
		<xsl:if test="$EDITOR=&apos;true&apos;">
			<xsl:if test="($WORKFLOW-ACCEPTED-ENABLED=&apos;true&apos; and $ACTIVITY!=&apos;APPROVED&apos;)">
			  <div class="button">
			    <div class="button_body">
				  <xsl:call-template name="acceptbutton" />
				</div>
              </div>
			</xsl:if>
			<xsl:if test="$ACTIVITY!=&apos;DRAFT&apos;">
			  <div class="button">
			    <div class="button_body">
				  <xsl:call-template name="rejectbutton" />
				</div>
			  </div>
			</xsl:if>
		</xsl:if>
	
		<xsl:if test="$ACTIVITY=&apos;DRAFT&apos;">
		  <div class="button">
		    <div class="button_body">
		      <xsl:call-template name="finishbutton" />
		    </div>
		  </div>
		</xsl:if>
	</xsl:if>
  </xsl:template>

  <xsl:template name="finishbutton">
    <a href="javascript:doFinish();" id="bottombutton-finish" unselectable="on" titlesave="{$tooltip_finish}" titlenosave="{$tooltip_no_finish}">
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_finish"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_finish"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="prompt_finish"/>
    </a>
  </xsl:template>

  <xsl:template name="acceptbutton">
    <a href="javascript:doAccept();" id="bottombutton-accept" unselectable="on" titlesave="{$tooltip_accept}" titlenosave="{$tooltip_no_accept}">
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_accept"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_accept"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="prompt_accept"/>
    </a>
  </xsl:template>

  <xsl:template name="rejectbutton">
    <a href="javascript:doReject();" id="bottombutton-reject" unselectable="on" titlesave="{$tooltip_reject}" titlenosave="{$tooltip_no_reject}">
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_reject"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_reject"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="prompt_reject"/>
    </a>
  </xsl:template>

  <xsl:template name="publishbutton">
    <a href="javascript:doPublish();" id="bottombutton-publish" unselectable="on" titlesave="{$tooltip_publish}" titlenosave="{$tooltip_no_publish}">
      <xsl:if test="@allowsave='true'">
        <xsl:attribute name="class">bottombutton</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_publish"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@allowsave='false'">
        <xsl:attribute name="class">bottombutton-disabled</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$tooltip_no_publish"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="prompt_publish"/>
    </a>
  </xsl:template>

</xsl:stylesheet>