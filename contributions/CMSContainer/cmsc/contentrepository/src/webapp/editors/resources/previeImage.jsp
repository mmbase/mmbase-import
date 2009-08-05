<%@ page language="java" contentType="text/html;charset=utf-8" 
%><%@ include file="globals.jsp" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="content.searchResult">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath }/editors/editwizards_new/style/extra/wizard.css"">
    <script type="text/javascript" src="imageUtil.js"> </script>
	 <script type="text/javascript" src="content.js"> </script>
</cmscedit:head>
  
  <body>
        <div class="editor">
            <br/>
            <div class="ruler_green">
                <div>
                    IMAGE 
                </div>
            </div>
            <div class="body">
            </p>
            <p>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="SELECT IMAGES" onclick="return onSelect('${pageContext.request.contextPath }');" name="ANDERE" class="button"/>
				<br/>
                &nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="CREATE NEW IMAGE" onclick="fn(0);" name="ANDERE" class="button"/>
            </p>
            <p>
                <table width="93%" border="0" align="center">
                    <tr>
                        <td width="2%" rowspan="5">
                            &nbsp;
                        </td>
                        <td width="23%" rowspan="4">
                            <div class="alignPreview" onMouseOver="this.style.backgroundColor='#F0F0F0';" onmouseout="this.style.backgroundColor='#Ffffff';" onclick="window.close();">
                                <img id="alignSampleImg" src="/**/${pageContext.request.contextPath }/mmbase/images/${imageId}/${imageBame}" alt="" />
								/${imageId}/${imageBame}
                            </div>
                        </td>
                        <td height="33" colspan="3" class="images">
                            Invoegeigen
                        </td>
                    </tr>
                    <tr>
                        <td width="12%">
                            Tooltip:
                        </td>
                        <td colspan="2">
                            <input type="text" style="width: 50%;" value="" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Alt-text
                        </td>
                        <td colspan="2">
                            <input type="text" style="width: 50%;" value="" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Aligment
                        </td>
                        <td width="31%">
                            <select id="align" name="align" style="width: 50%;" onchange="ImageDialog.updateStyle('align');ImageDialog.changeAppearance();">
                                <option value="">Left </option>
                                <option value="baseline">advimage_dlg.align_baseline </option>
                                <option value="top">advimage_dlg.align_top </option>
                                <option value="middle">advimage_dlg.align_middle </option>
                                <option value="bottom">advimage_dlg.align_bottom </option>
                                <option value="text-top">advimage_dlg.align_texttop </option>
                                <option value="text-bottom">advimage_dlg.align_textbottom </option>
                                <option value="left">advimage_dlg.align_left </option>
                                <option value="right">advimage_dlg.align_right </option>
                            </select>
                        </td>
                        <td>
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td>
                            ${imageTitle}
                        </td>
                        <td class="images">
                            Border
                        </td>
                        <td class="images">
                            <select id="border" name="border" style="width: 50%;">
                                <option value="">None</option>
                            </select>
                        </td>
                        <td class="images">
                            &nbsp;
                        </td>
                    </tr>
                </table>
                 <c:if test="${size + 3> 0}">
                <%@ include file="previewImageList.jspf" %>
				</c:if>
            </p>
        </div>
		</div>
		
		<table width="100%" border="0" align="left">
		 <div id="commandbuttonbar" class="buttonscontent">
            <div class="page_buttons_seperator">
               <div></div>
            </div>
            <div class="page_buttons">
                <div class="button">
                    <div class="button_body">
                        <a id="bottombutton-save" class="bottombutton" title="Store all changes." href="javascript:doSave();" unselectable="on" titlesave="Store all changes." titlenosave="The changes cannot be saved, since some data is not filled in correctly." inactive="false">ok</a>
                    </div>
                </div>
               
                <div class="button">
                    <div class="button_body">
                        <a id="bottombutton-cancel" class="bottombutton" href="javascript:doCancel();" title="Cancel this task, changes will NOT be saved.">cancel</a>
                    </div>
                </div>
                <div class="begin">
                </div>
            </div>
        </div>
		</table>
    </body>
</html:html>
</mm:content>
