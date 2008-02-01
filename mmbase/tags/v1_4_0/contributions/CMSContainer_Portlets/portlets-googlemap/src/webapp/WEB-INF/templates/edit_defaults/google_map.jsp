<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://finalist.com/cmsc" prefix="cmsc" %>
<%@ taglib uri="http://finalist.com/cmsc-basicmodel" prefix="cmsc-bm" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<portlet:defineObjects/>
<cmsc:portlet-preferences/>
<div class="portlet-config-canvas">
    <form name="<portlet:namespace />form" method="post" target="_parent"
          action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">

        <table class="editcontent">
            <tr>
                <td colspan="3">
                    <h3>
                        <fmt:message key="edit_defaults.title"/>
                    </h3>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <fmt:message key="edit_defaults.address"/>
                    :
                </td>
                <td>
                    <input type="text" name="address" value="${address}"/>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <fmt:message key="edit_defaults.info"/>
                    :
                </td>
                <td>
                    <input type="text" name="info" value="${info}"/>
                </td>
            </tr>
			</tr>
            <tr>
                <td colspan="2">
                    <fmt:message key="edit_defaults.key"/>
                    :
                </td>
                <td>
                    <input type="text" name="key" value="${key}"/>
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    <a href="javascript:document.forms['<portlet:namespace />form'].submit()" class="button">
                        <img src="<cmsc:staticurl page='/editors/gfx/icons/save.png'/>" alt=""/>
                        <fmt:message key="edit_defaults.save"/>
                    </a>
                </td>
            </tr>

        </table>
    </form>

</div>
