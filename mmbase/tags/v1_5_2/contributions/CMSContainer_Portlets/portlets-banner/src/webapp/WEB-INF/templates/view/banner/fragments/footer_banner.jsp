<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
    carrouselBanners.push(banners);
    carrouselPositions.push("banner_${bannerPosition}");
    carrouselIntervals.push(intervals);
    }
-->
</script>
<div id="banner_${bannerPosition}" class="banner1">
</div>

<cmsc:portletmode name="edit">
    <%@include file="/WEB-INF/templates/edit/itemfooter.jsp"%>
</cmsc:portletmode>