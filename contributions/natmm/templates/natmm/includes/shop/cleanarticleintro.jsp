<mm:field name="intro" jspvar="intro" vartype="String" write="false"><%
   if(intro!=null&&!HtmlCleaner.cleanText(intro,"<",">","").trim().equals("")) {
   	intro = HtmlCleaner.replace(intro,"<p>","");
   	intro = HtmlCleaner.replace(intro,"<P>","");
   	intro = HtmlCleaner.replace(intro,"</p>","");
   	intro = HtmlCleaner.replace(intro,"</P>","");
   	%><%= intro %><%
   } 
%></mm:field>