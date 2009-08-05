<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="../css/main.css" type="text/css" rel="stylesheet" />

   <style type="text/css">
     div.editor div.body  #selectedsite
      {
       background-color: #f1f400;
      }
    div.page_buttons div.button div.button_body {
      background-image: url('../../../gfx/tab_2_right.gif');
      background-repeat: no-repeat;
      background-position: top right;
      height: 39px;
      float: left;
   }

   div.page_buttons div.button div.button_body a {
      float: left;
      padding: 10px;
      color: #313728;
      font-size: 13px;
      display: block;
      text-decoration:none;
   }
   </style>
<script language="javascript">
   var sitenumber = "";
   var sitetitle = "";
   function objClick(number,title,ele) {
      sitenumber = number;
      sitetitle = title;
      if(ele.id=='selectedsite'){
         ele.id ='';
         return;
      }
      var oldSelected = document.getElementById('selectedsite');
      if(oldSelected){
         oldSelected.id="";
      }

      ele.id = "selectedsite";
   }
   function selectSite() {
      if (sitenumber == "") {
         alert("Please select a site first!");
         return;
      }
      if(window.opener) {
         window.opener.document.forms[0].sitetitle.value = sitetitle;
         window.opener.document.forms[0].destination.value = sitenumber;
         window.close();
      }
   }
</script>
</head>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
   <body>
   <div class="editor" style="height:355px">
         <div class="ruler_green">
            <div>
               <fmt:message key="deepcopy.site.select.title" />
            </div>
         </div>
         <mm:listnodescontainer type="site">
      <div class="body" style="max-height:400px;overflow-y:auto; overflow-x:hidden"> 
                <table>
                  <tbody class="hover">
                     <mm:listnodes>
                       <mm:field name="number" write="false" id="number"/>
                        <tr <mm:even inverse="true">class="swap"</mm:even>  onMouseDown="objClick('<mm:field name="number"/>','<mm:field name="title"/>',this);">
                           <td ><mm:field name="title"/></td>
                           <td ><mm:field name="number"/></td>
                        </tr>
                     </mm:listnodes>
                  </tbody>
               </table>
        </div>          
         </mm:listnodescontainer>
</div>
       <div id="commandbuttonbar" class="buttonscontent" style="clear:both">
            <div class="page_buttons_seperator">
               <div></div>
            </div>
            <div class="page_buttons">
                <div class="button">
                    <div class="button_body">

                        <a class="bottombutton" title="Select the attachment." href="javascript:selectSite();"><fmt:message key="deepcopy.button.ok" /></a>
                    </div>
                </div>
                <div class="button">
                    <div class="button_body">

                        <a class="bottombutton" href="javascript:window.close();" title="Cancel this task, attachment will NOT be selected."><fmt:message key="deepcopy.button.cancel" /></a>
                    </div>
                </div>
                <div class="begin"></div>
            </div>
        </div>
   </body>
</mm:cloud>
</html>
</mm:content>