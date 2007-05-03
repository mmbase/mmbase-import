<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:content postprocessor="reducespace">
<mm:cloud>
  <mm:import externid="mode">components</mm:import>
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="roles_defs.jsp" />
  <script type="text/javascript">
    function saveCookie(name,value,days) {
    if (days) {
               var date = new Date();
               date.setTime(date.getTime()+(days*24*60*60*1000))
               var expires = '; expires='+date.toGMTString()
            } else expires = ''
            document.cookie = name+'='+value+expires+'; path=/'
         }
         function readCookie(name) {
            var nameEQ = name + '='
            var ca = document.cookie.split(';')
            for(var i=0;i<ca.length;i++) {
               var c = ca[i];
               while (c.charAt(0)==' ') c = c.substring(1,c.length)
               if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length)
            }
            return null
         }
         function deleteCookie(name) {
            saveCookie(name,'',-1)
         }
         function restoreTree() {
            for(var i=1; i<10; i++) {
               var lastclicknode = readCookie('lastnodepagina'+i);
               if(lastclicknode!=null) { clickNode(lastclicknode); }
            }
         }
         function clickNode(node) {
            var level = node.split('_').length;
            saveCookie('lastnodepagina'+level,node,1);
            el=document.getElementById(node);
            img = document.getElementById('img_' + node);
            img2 = document.getElementById('img2_' + node);
            if (el!=null && img != null)
            {
               if (el.style.display=='none')
               {
                  el.style.display='inline';
                  if (img2 != null) img2.src = 'gfx/folder_open.gif';
                  if (img.src.indexOf('last.gif')!=-1 )
                  {
                     img.src='gfx/tree_minlast.gif';
                  }
                  else
                  {
                     img.src='gfx/tree_min.gif';
                  }
               }
               else
               {
                  el.style.display='none';
                  if (img2 != null) img2.src = 'gfx/folder_closed.gif';
                  if (img.src.indexOf('last.gif')!=-1)
                  {
                     img.src='gfx/tree_pluslast.gif';
                  }
                  else
                  {
                     img.src='gfx/tree_plus.gif';
                  }
               }
            }
         }
       </script>
       <mm:remove from="session" referid="path" />
       <mm:include page="modes/${mode}.jsp" />

     </mm:cloud>
</mm:content>
