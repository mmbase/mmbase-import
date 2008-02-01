<#function moderationParams o>
	<#assign open = o/>
	<#if open?length == 0>
		<#if openModeration>
			<#assign open = "1"/>
		<#else>
			<#assign open = "0"/>
		</#if>
	</#if>
	
	<#assign baseUrl = "openModeration="+ open/>
	
	<#if (forum?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;search_forum="+ forum/></#if>
	<#if (category?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;search_cat="+ category/></#if>
	<#if (kw?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;search_keywords="+ kw/></#if>
	<#if (author?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;search_author="+ author/></#if>
	<#if (terms?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;search_terms="+ terms/></#if>
	<#if (orderBy?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;sort_dir="+ orderBy/></#if>
	<#if (orderField?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;sort_by="+ orderField/></#if>
	<#if (postTime?default("")?length > 0)><#assign baseUrl = baseUrl +"&amp;post_time="+ postTime/></#if>
	
	<#return baseUrl/>
</#function>

<#macro searchPagination kw terms forum category orderField orderBy author postTime>
	<#assign baseUrl = contextPath +"/jforum" + extension + "?module=search&amp;action=search&amp;"/>
	<#assign baseUrl = baseUrl + moderationParams("")/>

	<#if (totalRecords > recordsPerPage)>
		<span class="gensmall"><b>${I18n.getMessage("goToPage")}:

		<#assign numberOfActiveLinks = 7/>
		<#assign numberOfSideLinks = 3/>

		<#if (thisPage > 1)>
			<#assign start = (thisPage - 2) * recordsPerPage/>
			<a href="${baseUrl}&amp;start=${start}">${I18n.getMessage("previous")}</a>&nbsp;
		</#if>

		<#if (totalPages > numberOfActiveLinks)>
			<#if (thisPage <= numberOfSideLinks)>
				<#assign startPageAt = 1/>
				<#assign stopPageAt = numberOfActiveLinks/>
			<#else>
				<#assign startPageAt = (thisPage - numberOfSideLinks) >
				<#if (thisPage >= (totalPages - numberOfSideLinks)) >
					<#assign startPageAt = (totalPages - (numberOfActiveLinks - 1))/>
				</#if>
				
				<#assign stopPageAt = startPageAt + (numberOfActiveLinks - 1)/>
			</#if>

			<#if (startPageAt > 1) >
				...
			</#if>

			<#list startPageAt .. stopPageAt as page >
				<#assign start = recordsPerPage * (page-1) >

				<#if thisPage == page>
					${page}
				<#else>
					<a href="${baseUrl}&amp;start=${start}">${page}</a>
				</#if>
				<#if (page < totalPages) >,</#if>
			</#list>
			
			<#if (stopPageAt < totalPages)>
				...
			</#if>
		<#else>
			<#list 1 .. totalPages as page >
				<#assign start = recordsPerPage * (page - 1)/>

				<#if thisPage == page>
					${page}
				<#else>
					<a href="${baseUrl}&amp;start=${start}">${page}</a>
				</#if>
				<#if (page < totalPages) >,</#if>
			</#list>
		</#if>

		<#if thisPage < totalPages >
			<#assign start = thisPage * recordsPerPage/>
			&nbsp;<a href="${baseUrl}&amp;start=${start}">${I18n.getMessage("next")}</a>
		</#if>
		</b>
	</span>
	</#if>
</#macro>