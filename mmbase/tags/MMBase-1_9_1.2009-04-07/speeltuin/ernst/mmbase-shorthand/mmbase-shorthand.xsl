<!-- elements in namespace, but attributes not-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:ms="http://www.dynasol.nl/xmlns/mmbase-shorthand"
	xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect" 
    xmlns:dt="http://www.mmbase.org/xmlns/datatypes"
    xmlns:ext="xalan://ebunders.mmbase.shorthand.Ext"
	extension-element-prefixes="redirect" version="1.0">
    


	<!-- reads the builder sources from an index file and writes them 
		all into one file -->

	<xsl:output method="xml" version="1.0" encoding="utf-8" omit-xml-declaration="no" indent="yes" />

	<xsl:variable name="lang" select="/ms:application/@defaultlanguage" />
	<xsl:variable name="defaultmaintainer" select="/ms:application/@maintainer" />
	<xsl:variable name="defaultbuilderclass" select="/ms:application/@class" />
	<xsl:variable name="defaultbuildersearchage" select="/ms:application/@searchage" />
	<xsl:variable name="applicationname" select="/ms:application/@name" />

	<xsl:key name="roles" match="ms:relation" use="@role" />
	<xsl:key name="reldefbuilders" match="ms:reldef" use="@builder" />


	<!-- general -->
	<xsl:template match="/">
        <!--create the application dir-->
        <xsl:variable name="b"><xsl:value-of select="$applicationname"/>/builders</xsl:variable>
        <xsl:value-of select="ext:mkdir($b)"/>
		<xsl:apply-templates mode="application" />
		<xsl:apply-templates mode="builders" />
	</xsl:template>



	<!-- ***********************************************************
		**    Applicatin templates                                    **
		***********************************************************  -->


	<xsl:template match="ms:application" mode="application">
		<redirect:write file="generated/{$applicationname}.xml">
			<xsl:text disable-output-escaping="yes">
				&lt;!DOCTYPE application PUBLIC "-//MMBase//DTD application config 1.1//EN"
				"http://www.mmbase.org/dtd/application_1_1.dtd">
			</xsl:text>
			<!-- version -->
			<xsl:variable name="version">
				<xsl:choose>
					<xsl:when test="@version">
						<xsl:value-of select="@version" />
					</xsl:when>
					<xsl:otherwise>1</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!--  auto-deploy -->
			<xsl:variable name="autodep">
				<xsl:choose>
					<xsl:when test="@autodeploy">
						<xsl:value-of select="@autodeploy" />
					</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!--  maintainer-->
			<xsl:variable name="maintainer">
				<xsl:choose>
					<xsl:when test="@maintainer">
						<xsl:value-of select="@maintainer" />
					</xsl:when>
					<xsl:otherwise>mmbase.org</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:call-template name="blankline" />

			<application name="{@name}" maintainer="{$maintainer}" version="{$version}" auto-deploy="{$autodep}">

				<xsl:call-template name="blankline" />

				<neededbuilderlist>
					<xsl:apply-templates select="ms:builder" mode="application" />
				</neededbuilderlist>

				<xsl:call-template name="blankline" />

				<neededreldeflist>
                    <xsl:if test="not(//reldef[@name='related'])">
                        <reldef guitargetname="related" guisourcename="related" builder="insrel" direction="bidirectional" target="related" source="related"/>
                    </xsl:if>
					<xsl:for-each select="ms:builder/ms:relation[generate-id() =generate-id(key('roles', @role)[1])]">
						<xsl:call-template name="reldefs" />
					</xsl:for-each>
				</neededreldeflist>

				<xsl:call-template name="blankline" />

				<allowedrelationlist>
					<xsl:for-each select="ms:builder/ms:relation">
						<relation from="{../@name}" to="{@target}" type="{@role}" />
					</xsl:for-each>
				</allowedrelationlist>

				<xsl:call-template name="blankline" />

				<datasourcelist>
					<xsl:for-each select="ms:builder[not(@name=//ms:relation/@role)]">
						<datasource builder="{@name}" path="{$applicationname}/{@name}.xml" />
					</xsl:for-each>
				</datasourcelist>

				<xsl:call-template name="blankline" />
                <!--
                    the reldef builders are all the unique values in the '//reldef/@builder' element node set.
                    If there is a //relation[@role='related'] and there is no //reldef[@builder='insrel', add that by default
                    If there is a  //relation[@role='posrel'] but no //reldef[@builder='posrel', add that too.
                -->
				<relationsourcelist>
					<xsl:if test="//ms:relation[@role='related'] and not(//ms:reldef[@builder='insrel'])">
						<relationsource builder="insrel" path="{$applicationname}/insrel.xml" />
					</xsl:if>
                    <xsl:if test="//ms:relation[@role='posrel'] and not(//ms:reldef[@builder='posrel'])">
						<relationsource builder="posrel" path="{$applicationname}/posrel.xml" />
					</xsl:if>
					<xsl:for-each select="//ms:reldef[generate-id() = generate-id(key('reldefbuilders', @builder)[1])]">

						<xsl:variable name="role">
							<xsl:value-of select="@role" />
						</xsl:variable>

						<xsl:variable name="buildername">
							<xsl:choose>
								<xsl:when test="/ms:application/ms:reldef[@role = $role]/@builder">
									<xsl:value-of select="/ms:application/ms:reldef[@role = $role]/@builder" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@role" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>

						<relationsource builder="{$buildername}" path="{$applicationname}/{$buildername}.xml" />
					</xsl:for-each>
				</relationsourcelist>

				<xsl:call-template name="blankline" />

				<contextsourcelist>
					<contextsource path="{$applicationname}/backup.xml" type="depth" goal="backup" />
				</contextsourcelist>

				<xsl:call-template name="blankline" />

				<description>
					<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
					<xsl:choose>
						<xsl:when test="@description">
							<xsl:value-of select="@description" />
						</xsl:when>
						<xsl:otherwise>
							Install application
							<xsl:value-of select="@name" />
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
				</description>

				<xsl:call-template name="blankline" />

				<install-notice>
					<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
					application
					<xsl:value-of select="@name" />
					installed ok.
					<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
				</install-notice>

			</application>
		</redirect:write>
	</xsl:template>


	<!--  
		*** callback template distinct. create a reldef line in an application file
	-->
	<xsl:template name="reldefs">
		<xsl:variable name="role">
			<xsl:value-of select="@role" />
		</xsl:variable>

		<!--  determin builder
            if there is a reldef for this role, and this reldef has a builder attribute, that is the builder.
            otherwise it is insrel
        -->
		<xsl:variable name="builder">
			<xsl:choose>
				<xsl:when test="//ms:reldef[@role=$role and @builder]">
					<xsl:value-of select="//ms:reldef[@role=$role]/@builder" />
				</xsl:when>
				<xsl:otherwise>insrel</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!--  determin the directionality -->
		<xsl:variable name="directionality">
			<xsl:choose>
				<xsl:when test="/ms:application/ms:reldef[@role=$role and @unidirectional='true']">
					unidirectional
				</xsl:when>
				<xsl:otherwise>bidirectional</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- determin the guiname-->
		<xsl:variable name="guiname">
			<xsl:choose>
				<xsl:when test="/ms:application/ms:reldef[@role=$role and @guiname]">
					<xsl:value-of select="/ms:application/ms:reldef[@role=$role]/@guiname" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$role" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<reldef source="{$role}" target="{$role}" direction="{$directionality}" builder="{$builder}"
			guisourcename="{$guiname}" guitargetname="{$guiname}" />
	</xsl:template>




	<!--
		*** create a builder line in an application file
	-->
	<xsl:template match="ms:builder" mode="application">
		<xsl:variable name="version">
			<xsl:call-template name="getbuilderversion" />
		</xsl:variable>
		<xsl:variable name="maintainer">
			<xsl:call-template name="getbuildermaintainer" />
		</xsl:variable>
		<builder maintainer="{$maintainer}" version="{$version}">
			<xsl:value-of select="@name" />
		</builder>
	</xsl:template>



	<!-- ***********************************************************
		**    builder templates                                       **
		***********************************************************  -->

	<xsl:template match="ms:builder" mode="builders">
		<xsl:if test="not(@create='false')">
			<redirect:write file="generated/{$applicationname}/builders/{@name}.xml">
				<xsl:variable name="version">
					<xsl:call-template name="getbuilderversion" />
				</xsl:variable>

				<xsl:variable name="maintainer">
					<xsl:call-template name="getbuildermaintainer" />
				</xsl:variable>

				<xsl:variable name="extends">
					<xsl:choose>
						<xsl:when test="@extends">
							<xsl:value-of select="@extends" />
						</xsl:when>
						<xsl:otherwise>object</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<builder name="{@name}" maintainer="{$maintainer}" version="{$version}" extends="{$extends}"
					xmlns="http://www.mmbase.org/xmlns/builder" xmlns:dt="http://www.mmbase.org/xmlns/datatypes"
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
					xsi:schemaLocation="http://www.mmbase.org/xmlns/builder  http://www.mmbase.org/xmlns/builder.xsd">

					<xsl:if test="@status">
						<status>
							<xsl:value-of select="@status" />
						</status>
					</xsl:if>

					<xsl:variable name="builderclass">
						<xsl:choose>
							<xsl:when test="@class">
								<xsl:value-of select="@class" />
							</xsl:when>
							<xsl:when test="$defaultbuilderclass">
								<xsl:value-of select="$defaultbuilderclass" />
							</xsl:when>
						</xsl:choose>
					</xsl:variable>

					<xsl:if test="not($builderclass='')">
						<class>
							<xsl:value-of select="$builderclass" />
						</class>
						<xsl:call-template name="blankline" />
					</xsl:if>

					<xsl:variable name="searchage">
						<xsl:choose>
							<xsl:when test="@searchage">
								<xsl:value-of select="@searchage" />
							</xsl:when>
							<xsl:when test="$defaultbuildersearchage">
								<xsl:value-of select="$defaultbuildersearchage" />
							</xsl:when>
							<xsl:otherwise>3000</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<searchage>
						<xsl:value-of select="$searchage" />
					</searchage>
					<xsl:call-template name="blankline" />

					<xsl:variable name="sname">
						<xsl:choose>
							<xsl:when test="@sname">
								<xsl:value-of select="@sname" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="pname">
						<xsl:choose>
							<xsl:when test="@pname">
								<xsl:value-of select="@pname" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="@name" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<names>
						<singular xml:lang="{$lang}">
							<xsl:value-of select="$sname" />
						</singular>
						<plural xml:lang="{$lang}">
							<xsl:value-of select="$pname" />
						</plural>
					</names>

					<xsl:if test="@description">
						<descriptions>
							<descrption>
								<xsl:value-of select="@description" />
							</descrption>
						</descriptions>
					</xsl:if>

					<xsl:call-template name="blankline" />

					<fieldlist>
						<xsl:apply-templates select="ms:field" mode="builders" />
					</fieldlist>

					<xsl:call-template name="blankline" />

					<xsl:if test="function">
						<functions>
							<xsl:for-each select="function">
								<function name="{@name}" key="{@key}">
									<class>
										<xsl:value-of select="@class" />
									</class>
								</function>
							</xsl:for-each>
						</functions>
					</xsl:if>

				</builder>
			</redirect:write>
		</xsl:if>
	</xsl:template>

	<!--
		*** a field in a builder ***
	-->
	<xsl:template match="ms:field" mode="builders">
		<field xmlns="http://www.mmbase.org/xmlns/builder">
			<xsl:if test="@description">
				<descriptions>
					<description>
						<xsl:value-of select="@description" />
					</description>
				</descriptions>
			</xsl:if>

			<xsl:variable name="guiname">
				<xsl:choose>
					<xsl:when test="@guiname">
						<xsl:value-of select="@guiname" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<gui>
				<guiname xml:lang="{$lang}">
					<xsl:value-of select="$guiname" />
				</guiname>
			</gui>

			<editor>
				<positions>
					<input>
						<xsl:call-template name="position">
							<xsl:with-param name="flag">i</xsl:with-param>
						</xsl:call-template>
					</input>
					<list>
						<xsl:call-template name="position">
							<xsl:with-param name="flag">l</xsl:with-param>
						</xsl:call-template>
					</list>
					<search>
						<xsl:call-template name="position">
							<xsl:with-param name="flag">s</xsl:with-param>
						</xsl:call-template>
					</search>
				</positions>
			</editor>

			<xsl:choose>
				<xsl:when test="./dt:datatype">
					<xsl:copy-of select="./dt:datatype" />
				</xsl:when>
				<xsl:otherwise>
					<dt:datatype base="{@datatype}" />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:variable name="state">
				<xsl:choose>
					<xsl:when test="@state">
						<xsl:value-of select="@state" />
					</xsl:when>
					<xsl:otherwise>persistent</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="notnull">
				<xsl:choose>
					<xsl:when test="@flags">
						<xsl:call-template name="stringcontains">
							<xsl:with-param name="string">
								<xsl:value-of select="@flags" />
							</xsl:with-param>
							<xsl:with-param name="flag">n</xsl:with-param>
							<xsl:with-param name="found">true</xsl:with-param>
							<xsl:with-param name="notfound">false</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="key">
				<xsl:choose>
					<xsl:when test="@flags">
						<xsl:call-template name="stringcontains">
							<xsl:with-param name="string">
								<xsl:value-of select="@flags" />
							</xsl:with-param>
							<xsl:with-param name="flag">k</xsl:with-param>
							<xsl:with-param name="found">true</xsl:with-param>
							<xsl:with-param name="notfound">false</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="type">
				<xsl:choose>
					<xsl:when test="@type">
						<xsl:value-of select="@type" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="@datatype='text' or @datatype='line' or @datatype='string' or @datatype='eline' or @datatype='field' or @datatype='md5password' or @datatype='confirmpassword' or @datatype='dutch-zipcode' or @datatype='emailaddress'">STRING</xsl:if>
						<xsl:if test="@datatype='datetime' or @datatype='created' or @datatype='lastmodified' or @datatype='time' or @datatype='date' or @datatype='20th-century' or @datatype='20th-century-pedantic' or @datatype='birthdate' or @datatype='living-birthdate' or @datatype='historical' or @datatype='weeknumbers' or @datatype='eventtime'"> DATETIME </xsl:if> <xsl:if test="@datatype='long' or @datatype='duration'">LONG</xsl:if> <xsl:if test="@datatype='boolean' or @datatype='yesno' or @datatype='onoff'">BOOLEAN</xsl:if>
                         <xsl:if test="@datatype='integer' or @datatype='filesize' or @datatype='weekday' or @datatype='byte' or @datatype='hour_of_day' or @datatype='minute_of_hour'">INTEGER</xsl:if>
                         <xsl:if test="@datatype='binary'">BINARY</xsl:if> <xsl:if test="@datatype='node' or @datatype='nodenumber'">BINARY</xsl:if>
						<xsl:if test="@datatype='xml' or @datatype='html' or @datatype='xmlfield'">XML</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="size">
				<xsl:choose>
					<xsl:when test="@size">
						<xsl:value-of select="@size" />
					</xsl:when>
					<xsl:when
						test="@datatype='string' or @datatype='field' or @datatype='html' or @datatype='xml' or @datatype='xmlfield'">3000</xsl:when>
					<xsl:when test="@datatype='line' or @datatype='eline' or @datatype='trimmedline'">300</xsl:when>
					<xsl:when test="@datatype='boolean' or @datatype='yesno' or @datatype='onoff'">1</xsl:when>
					<xsl:when test="$type='BINARY'">16777215</xsl:when>
					<xsl:otherwise>50</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<db>
				<name>
					<xsl:value-of select="@name" />
				</name>
				<type state="{$state}" size="{$size}" notnull="{$notnull}" key="{$key}">
					<xsl:value-of select="$type" />
				</type>
			</db>
		</field>
		<xsl:call-template name="blankline" />
	</xsl:template>

	<!-- ***********************************************************
		**    Global Templates                                        **
		***********************************************************  -->


	<xsl:template name="blankline">
		<xsl:text disable-output-escaping="yes"></xsl:text>
	</xsl:template>


	<xsl:template name="getbuildermaintainer">
		<xsl:choose>
			<xsl:when test="@maintainer">
				<xsl:value-of select="@maintainer" />
			</xsl:when>
			<xsl:when test="$defaultmaintainer">
				<xsl:value-of select="$defaultmaintainer" />
			</xsl:when>
			<xsl:otherwise>mmbase.org</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template name="getbuilderversion">
		<xsl:choose>
			<xsl:when test="@version">
				<xsl:value-of select="@version" />
			</xsl:when>
			<xsl:otherwise>1</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- obtain the field position for a type of field list (input, seach, list)-->
	<xsl:template name="position">
		<xsl:param name="flag" />
		<xsl:choose>
			<xsl:when test="@input and $flag='i'">
				<xsl:value-of select="@input" />
			</xsl:when>
			<xsl:when test="@search and $flag='s'">
				<xsl:value-of select="@search" />
			</xsl:when>
			<xsl:when test="@list and $flag='l'">
				<xsl:value-of select="@list" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="@editor">
						<xsl:call-template name="stringcontains">
							<xsl:with-param name="flag">
								<xsl:value-of select="$flag" />
							</xsl:with-param>
							<xsl:with-param name="string">
								<xsl:value-of select="@editor" />
							</xsl:with-param>
							<xsl:with-param name="notfound">-1</xsl:with-param>
							<xsl:with-param name="found">
								<xsl:value-of select="position()" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="position()" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		find a single character in a string (flag)
		when it is found, the value of the 'found' param is returned.
		when it is not found, the value of the 'notfound' param is returned.
	-->
	<xsl:template name="stringcontains">
		<xsl:param name="flag" />
		<xsl:param name="string" />
		<xsl:param name="notfound" />
		<xsl:param name="found" />
		<xsl:choose>
			<xsl:when test="starts-with($string, $flag)">
				<xsl:value-of select="$found" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length($string) > 1">
						<xsl:call-template name="stringcontains">
							<xsl:with-param name="flag">
								<xsl:value-of select="$flag" />
							</xsl:with-param>
							<xsl:with-param name="string">
								<xsl:value-of select="substring($string, 2)" />
							</xsl:with-param>
							<xsl:with-param name="notfound">
								<xsl:value-of select="$notfound" />
							</xsl:with-param>
							<xsl:with-param name="found">
								<xsl:value-of select="$found" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$notfound" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
