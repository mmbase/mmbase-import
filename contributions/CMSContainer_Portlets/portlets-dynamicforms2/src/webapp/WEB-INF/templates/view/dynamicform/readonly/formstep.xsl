<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="dynamicform/readonly/section.xsl"/>
	<xsl:include href="dynamicform/readonly/list.xsl"/>
	<xsl:include href="dynamicform/navigation.xsl"/>
	
	<xsl:template match="formstep[@guitype='readonly']">
			<xsl:if test="stepinfo/stepimages">
			<p>
				<xsl:for-each select="stepinfo/stepimages/image">
					<xsl:choose>
						<xsl:when test="@stepstatus = 'active' ">
							<img alt="" src="{$URLCONTEXT}gfx/dynamicforms/i_step_active.gif"/>
						</xsl:when>
						<xsl:when test="@stepstatus = 'finished' ">
							<img alt="" src="{$URLCONTEXT}gfx/dynamicforms/i_step_finished.gif"/>
						</xsl:when>
						<xsl:when test="@stepstatus = 'future' ">
							<img alt="" src="{$URLCONTEXT}gfx/dynamicforms/i_step_future.gif"/>
						</xsl:when>
						<xsl:when test="@stepstatus = 'lastactive' ">
							<img alt="" src="{$URLCONTEXT}gfx/dynamicforms/i_step_last_active.gif"/>
						</xsl:when>
						<xsl:when test="@stepstatus = 'lastfinished' ">
							<img alt="" src="{$URLCONTEXT}gfx/dynamicforms/i_step_last_finished.gif"/>
						</xsl:when>
						<xsl:when test="@stepstatus = 'lastfuture' ">
							<img alt="" src="{$URLCONTEXT}gfx/dynamicforms/i_step_last_future.gif"/>
						</xsl:when>
					</xsl:choose>
					<img alt="Stap 1" src="{@steptitleurl}"/>
					<xsl:if test="@stepstatus = 'active' or @stepstatus = 'finished' or @stepstatus = 'future' ">
						<br/>
					</xsl:if>
				</xsl:for-each>
			</p>
		</xsl:if>
		<p>
		</p>
		<xsl:for-each select="stepinfo/description">
			<p>
				<h4>
					<xsl:value-of disable-output-escaping="yes" select="@title"/>
				</h4>
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</p>
		</xsl:for-each>
		<p> </p>
		<xsl:choose>
			<xsl:when test="list and not(section)">
				<form name="{$NAMESPACE}form"  action="{$ACTIONURL}" method="post">
					<xsl:value-of select="//default-form-params" disable-output-escaping="yes"/>
          <input type="hidden" name="currentStep" id="currentStep" value="{@name}"/>
          <input type="hidden" name="sequence" id="sequence" value="{@sequence}"/>
					<xsl:apply-templates select="list" mode="readonly"/>
					<div class="cform">
						<xsl:apply-templates select="navigation"/>
					</div>
				</form>
			</xsl:when>
			<xsl:when test="list and section">
				<xsl:if test="stepinfo/@title and stepinfo/@title !='' ">
					<div class="ctabletitle">
						<xsl:value-of disable-output-escaping="yes" select="stepinfo/@title"/>
					</div>
				</xsl:if>
				<div class="cform">
					<form name="{$NAMESPACE}form" action="{$ACTIONURL}" method="post">
						<xsl:value-of select="//default-form-params" disable-output-escaping="yes"/>
            <input type="hidden" name="currentStep" id="currentStep" value="{@name}"/>
            <input type="hidden" name="sequence" id="sequence" value="{@sequence}"/>
						<xsl:apply-templates select="section | list" mode="readonly"/>
						<xsl:apply-templates select="navigation"/>
					</form>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="stepinfo/@title and stepinfo/@title !='' ">
					<div class="ctabletitle">
						<xsl:value-of disable-output-escaping="yes" select="stepinfo/@title"/>
					</div>
				</xsl:if>
				<div class="cform">
					<form name="{$NAMESPACE}fomr" action="{$ACTIONURL}" method="post">
						<xsl:value-of select="//default-form-params" disable-output-escaping="yes"/>
            <input type="hidden" name="currentStep" id="currentStep" value="{@name}"/>
            <input type="hidden" name="sequence" id="sequence" value="{@sequence}"/>
						<fieldset>
						<legend><span><xsl:value-of select="stepinfo/@subtitle"/></span></legend>
						<xsl:apply-templates select="section" mode="readonly"/>
						</fieldset>
						<xsl:apply-templates select="navigation"/>
					</form>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
