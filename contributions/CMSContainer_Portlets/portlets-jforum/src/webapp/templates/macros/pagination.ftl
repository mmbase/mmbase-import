<#macro littlePostPagination topicId postsPerPage totalReplies>
	[ <img class="icon_latest_reply" src="${contextPath}/images/transp.gif" alt="" /> ${I18n.getMessage("goToPage")}:

	<#assign totalPostPages = ((totalReplies + 1) / postsPerPage)?int/>

	<#if (((totalReplies + 1) % postsPerPage) > 0)>
		<#assign totalPostPages = (totalPostPages + 1)/>
	</#if>

	<#if (totalPostPages > 6)>
		<#assign minTotal = 3/>
	<#else>
		<#assign minTotal = totalPostPages/>
	</#if>

	<#-- ----------- -->
	<#-- First pages -->
	<#-- ----------- -->
	<#assign link = ""/>

	<#list 1 .. minTotal as page>
		<#assign start = postsPerPage * (page - 1)/>

		<#assign link>${link}<a href='${JForumContext.encodeURL("/posts/list<#if (start>0)>/${start}</#if>/${topicId}")}'>${page}</a></#assign>
		<#if (page < minTotal)><#assign link>${link}, </#assign></#if>
	</#list>

	${link}

	<#assign link = ""/>
	<#if (totalPostPages > 6)>
		&nbsp;...&nbsp;
		<#list totalPostPages - 2 .. totalPostPages as page>
			<#assign start = postsPerPage * (page - 1)/>

			<#assign link>${link}<a href='${JForumContext.encodeURL("/posts/list<#if (start>0)>/${start}</#if>/${topicId}")}'>${page}</a></#assign>
			<#if (page_index + 1 < 3)><#assign link>${link}, </#assign></#if>
		</#list>

		${link}
	</#if>

	]
</#macro>

<#-- ------------------------------------------------------------------------------- -->
<#-- Pagination macro base code inspired from PHPBB's generate_pagination() function -->
<#-- ------------------------------------------------------------------------------- -->
<#macro doPagination action id=-1>
	<#if (totalRecords > recordsPerPage)>
		<span class="gensmall"><b>${I18n.getMessage("goToPage")}:

		<#assign link = ""/>

		<#-- ------------- -->
		<#-- Previous page -->
		<#-- ------------- -->
		<#if (thisPage > 1)>
			<#assign start = (thisPage - 2) * recordsPerPage/>
			<a href='${JForumContext.encodeURL("/${moduleName}/${action}<#if (start > 0)>/${start}</#if><#if (id > -1)>/${id}</#if>")}'>${I18n.getMessage("previous")}</a>&nbsp;
		</#if>

		<#if (totalPages > 10)>
			<#-- ------------------------------ -->
			<#-- Always write the first 3 links -->
			<#-- ------------------------------ -->
			<#list 1 .. 3 as page>
				<@pageLink page, id, (page_index + 1 < 3)/>
			</#list>

			<#-- ------------------ -->
			<#-- Intermediate links -->
			<#-- ------------------ -->
			<#if (thisPage > 1 && thisPage < totalPages)>
				<#if (thisPage > 5)> ... <#else>, </#if>

				<#if (thisPage > 4)>
					<#assign min = thisPage - 1/>
				<#else>
					<#assign min = 4/>
				</#if>

				<#if (thisPage < totalPages - 4)>
					<#assign max = thisPage + 2/>
				<#else>
					<#assign max = totalPages - 2/>
				</#if>

				<#if (max >= min + 1)>
					<#list min .. max - 1 as page>
						<@pageLink page, id, (page + 1 < max)/>
					</#list>
				</#if>

				<#if (thisPage < totalPages - 4)> ... <#else>, </#if>
			<#else>
				&nbsp;...&nbsp;
			</#if>

			<#-- ---------------------- -->
			<#-- Write the last 3 links -->
			<#-- ---------------------- -->
			<#list totalPages - 2 .. totalPages as page>
				<@pageLink page, id, (page < totalPages)/>
			</#list>
		<#else>
			<#list 1 .. totalPages as page>
				<@pageLink page, id, (page < totalPages)/>
			</#list>
		</#if>

		<#-- ------------- -->
		<#-- Next page -->
		<#-- ------------- -->
		<#if (thisPage < totalPages)>
			<#assign start = thisPage * recordsPerPage/>
			<a href="${contextPath}/${moduleName}/${action}<#if (start > 0)>/${start}</#if><#if (id > -1)>/${id}</#if>${extension}">${I18n.getMessage("next")}</a>&nbsp;
		</#if>

		</span>
	</#if>
</#macro>

<#macro pageLink page id commaExpression>
	<#assign start = recordsPerPage * (page - 1)/>
	<#if page != thisPage>
		<#assign link><a href='${JForumContext.encodeURL("${moduleName}/${action}<#if (start > 0)>/${start}</#if><#if (id > -1)>/${id}</#if>")}'>${page}</a></#assign>
	<#else>
		<#assign link>${page}</#assign>
	</#if>

	<#if commaExpression><#assign link = link + ", "/></#if>
	${link}
</#macro>
