<html:hidden property="contenttypes" value="customer" />
<html:hidden property="allPositions"/>
<table border="0">
    <tr>
        <td style="width: 100px"><fmt:message key="banner.search.field.page" /></td>
        <td align="right">
            <a href="<c:url value='/editors/site/select/SelectorPage.do?channel=${page}' />"
                target="selectpage1" onclick="openPopupWindow('selectpage1', 340, 400)"> 
                    <img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="edit_defaults.channelselect" />"/></a>
            <a href="javascript:erase('page');erase('pagepath');eraseList('window')">
                <img src="<cmsc:staticurl page='/editors/gfx/icons/erase.png'/>" alt="<fmt:message key="edit_defaults.erase"/>"/></a>
        </td>
        <td><input type="hidden" name="page"/><html:text style="width: 400px" property="pagepath" /></td>
        <%--
        <td><input type="hidden" name="page"/><html:text style="width: 300px" property="pagepath" /></td>
        --%>
    </tr>
    <tr>
        <td colspan="2"><fmt:message key="banner.search.field.position" /></td>
        <td>
            <html:select property="position" size="1">
                <html:option value=""> - </html:option>
                <c:forTokens var="position" items="${allPositions}" delims=";">
                    <html:option value="${position}" />
                </c:forTokens>
            </html:select>
        </td>
        <%--
        <td><html:text style="width: 300px" property="position" /></td>
        --%>
    </tr>
    <tr>
      <td colspan="2"><fmt:message key="banner.search.field.name" /></td>
      <td>
            <html:select property="name" size="1">
                <html:option value=""> - </html:option>
                <c:forEach var="customer" items="${customers}">
                    <html:option value="${customer['name']}" />
                </c:forEach>
            </html:select>
      </td>
    </tr>
            <jsp:useBean id="now" class="java.util.Date" />
            <c:set var="currentYear">
                <fmt:formatDate value="${now}" pattern="yyyy"/>
            </c:set>
            <c:set var="currentMonth">
                <fmt:formatDate value="${now}" pattern="yyyy"/>
            </c:set>
            <c:set var="currentDay">
                <fmt:formatDate value="${now}" pattern="yyyy"/>
            </c:set>
    <tr>
        <td colspan="2"><fmt:message key="banner.search.field.period.from" /></td>
        <td>
            <fmt:message key="banner.search.field.period.day" />
            <html:select property="fromDay" size="1">
                <c:forEach var="day" begin="1" end="31">
                    <html:option value="${day}">${day}</html:option>
                </c:forEach>
            </html:select>
            <fmt:message key="banner.search.field.period.month" />
            <html:select property="fromMonth" size="1">
                <c:forEach var="month" begin="1" end="12">
                    <html:option value="${month}">${month}</html:option>
                </c:forEach>
            </html:select>
            <fmt:message key="banner.search.field.period.year" />
            <html:select property="fromYear" size="1">
                <c:forEach var="year" begin="${currentYear - 5}" end="${currentYear}">
                    <html:option value="${year}">${year}</html:option>
                </c:forEach>
            </html:select>
        </td>
    </tr>
    <tr>
        <td colspan="2"><fmt:message key="banner.search.field.period.until" /></td>
        <td>
            <fmt:message key="banner.search.field.period.day" />
            <html:select property="toDay" size="1">
                <c:forEach var="day" begin="1" end="31">
                    <html:option value="${day}">${day}</html:option>
                </c:forEach>
            </html:select>
            <fmt:message key="banner.search.field.period.month" />
            <html:select property="toMonth" size="1">
                <c:forEach var="month" begin="1" end="12">
                    <html:option value="${month}">${month}</html:option>
                </c:forEach>
            </html:select>
            <fmt:message key="banner.search.field.period.year" />
            <html:select property="toYear" size="1">
                <c:forEach var="year" begin="${currentYear - 5}" end="${currentYear}">
                    <html:option value="${year}">${year}</html:option>
                </c:forEach>
            </html:select>
        </td>
    </tr>
    <%--
    <tr>
        <td colspan="2"><fmt:message key="banner.search.field.period" /></td>
        <td><html:select property="period" size="1">
            <html:option value="0"> - </html:option>
            <html:option value="-1">
                <fmt:message key="banner.search.option.pastday" />
            </html:option>
            <html:option value="-7">
                <fmt:message key="banner.search.option.pastweek" />
            </html:option>
            <html:option value="-31">
                <fmt:message key="banner.search.option.pastmonth" />
            </html:option>
            <html:option value="-120">
                <fmt:message key="banner.search.option.pastquarter" />
            </html:option>
            <html:option value="-365">
                <fmt:message key="banner.search.option.pastyear" />
            </html:option>
        </html:select></td>
    </tr>
    --%>
    <%--
    <tr>
        <td><fmt:message key="banner.search.field.remote" /></td>
        <td><html:checkbox property="remote" /></td>
    </tr>
    --%>
    <html:hidden property="remote" value="true" />
    <%--
    <tr>
        <td colspan="2"><fmt:message key="banner.search.field.export" /></td>
        <td><html:checkbox property="export" /></td>
    </tr>
    --%>

    <tr>
        <td colspan="2"></td>
        <td><input type="submit" name="submitButton" onclick="setOffset(0);" value="<fmt:message key="banner.search.submit" />" />
        <input type="submit" name="exportButton" onclick="setOffset(0);" value="<fmt:message key="banner.search.submit.export" />" /></td>
    </tr>
</table>
