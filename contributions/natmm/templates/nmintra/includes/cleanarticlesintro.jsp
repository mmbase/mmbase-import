<%
String introText = articles_intro;
introText = HtmlCleaner.replace(introText,"<p>","");
introText = HtmlCleaner.replace(introText,"<P>","");
introText = HtmlCleaner.replace(introText,"</p>","");
introText = HtmlCleaner.replace(introText,"</P>","");
%><%= introText %>