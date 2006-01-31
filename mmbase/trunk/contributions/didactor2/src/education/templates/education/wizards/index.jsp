<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp"%>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="education.editwizards" /></title>
    <style type="text/css">
      a {
        font-size: 11px;
      }
      .menu_font{
        font-size: 11px;
      }
      .folderBodyTree {
         width: 20%;
         height: 90%;
         overflow: scroll;
      }
      .contentBody {
        top: 0em;
        left: 0em;
        right: 0em;
        padding: 0em;
      }
    </style>
  </mm:param>
</mm:treeinclude>


<script>
   function browserVersion()
   {
      var browser = new Array ();

//      alert(navigator.appName);
//      alert(navigator.userAgent);
//      alert(navigator.appVersion);

      if (navigator.appName.indexOf("Netscape") != -1)
      {
         //Mozilla or Netscape Navigator
         if (navigator.userAgent.indexOf("Gecko") != -1)
         {
            if (navigator.userAgent.match("Netscape[0-9]") || navigator.userAgent.match("Netscape/[0-9]"))
            {
               //Netscape
               browser[0] = "NN";
               var version = navigator.userAgent.split('/');
               var tmp = version[3];
               version = tmp.split('.');
               browser[1] = version[0];
               browser[2] = version[1].substr(0,1);
            }
            else
            {
               //Mozilla
               browser[0] = "Mozilla";
               var version = navigator.userAgent.split(';');
               var tmp = version[4];
               version = tmp.split(')');
               tmp = version[0];
               version = tmp.split('.');
               version[0] = version[0].substr(4);
               browser[1] = version[0];
               browser[2] = version[1];
            }
         }
         else
         {
            //Old Netscape Nvagator, =< 4.8
            browser[0] = "NN";
            var version = navigator.appVersion;
            var tmp = version.split('.');
            tmp2 = tmp[1].split(' ');
            browser[1] = tmp[0];
            browser[2] = tmp2[0];
         }
      }
      if (navigator.appName.indexOf("Internet Explorer") != -1)
      {
         //IE
         browser[0] = "IE";
         var version = navigator.userAgent.split(';');
         var tmp = version[1].substr(6);
         tmp = tmp.split('.');
         browser[1] = tmp[0];
         browser[2] = tmp[1];
      }
      if (navigator.userAgent.indexOf("Opera") != -1)
      {
         //Opera
         browser[0] = "Opera";
         var version = navigator.userAgent.search("[oO]pera ");
         version = navigator.userAgent.substr(version + 6);
         var tmp = version.search(" ");
         version = version.substr(0,tmp);
         tmp = version.split('.');
         browser[1] = tmp[0];
         browser[2] = tmp[1];
      }

      return browser;
   }
</script>


<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
   <tr class="navigationbar">
      <td colspan="2" class="titlebar">
         <img src="<mm:treefile page="/gfx/icon_agenda.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="education.editwizards" />" alt="<di:translate key="education.editwizards" />" />
         <span class="menu_font">Editwizards:</span> <mm:treeinclude page="/education/wizards/tree_top_menu.jsp" objectlist="$includePath" />
      </td>
   </tr>
   <tr>
      <td style="width:20%">
         <div id="left_menu" style="overflow:scroll; width:100%; height:100%" >
             <mm:treeinclude page="/education/wizards/code.jsp" objectlist="$includePath" />
         </div>
      </td>

      <td width="100%">
         <iframe id="text" name="text" width="99%" height="100%" marginwidth="0" marginheight="0" border="1" src="<mm:treefile page="/education/wizards/ok.jsp" objectlist="$includePath" referids="$referids"/>"></iframe>
      </td>
   </tr>
</table>


<%--
<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
      <img src="<mm:treefile page="/gfx/icon_agenda.gif" objectlist="$includePath" referids="$referids"/>" alt="<di:translate key="education.editwizards" />" />
      Editwizards:  <mm:treeinclude page="/education/wizards/tree_top_menu.jsp" objectlist="$includePath" />
    </div>
  </div>
  <div class="folders">
    <div class="folderBodyTree">
      <mm:treeinclude page="/education/wizards/code.jsp" objectlist="$includePath" />
    </div>
  </div>
  <div class="mainContent">
    <div class="contentBody">
      <iframe id="text" name="text" width="100%" height="90%" marginwidth="0" marginheight="0" border="1" src="<mm:treefile page="/education/wizards/ok.jsp" objectlist="$includePath" referids="$referids"/>"></iframe>
    </div>
  </div>
</div>
--%>


<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids"/>
</mm:cloud>
</mm:content>
