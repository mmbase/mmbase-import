package nl.leocms.util.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Henk Hangyi (MMatch)
 */

public class HtmlCleaner {

//   private static final Logger log = Logging.getLoggerInstance(HtmlCleaner.class);

   public static String filterUTFChars(String textStr) {
       int uPos = textStr.indexOf("%u");
       while(uPos>-1&&(uPos+6)<textStr.length()) {
           textStr = textStr.substring(0,uPos) + " " + textStr.substring(uPos+6);
           uPos = textStr.indexOf("%u",uPos);
       }
       return textStr;
   }
   
   public static String insertShy(String sText, int iWrapLength) {
      String sWrappedText = "";
      while(sText.length()>iWrapLength) {
         int iNextSpace = sText.substring(0,iWrapLength).indexOf(" ");
         if(iNextSpace==-1) {
            iNextSpace = sText.substring(0,iWrapLength).indexOf("-");
            if(iNextSpace==-1) {
               sWrappedText += sText.substring(0,iWrapLength) + "&shy;";
               sText = sText.substring(iWrapLength);
            } else {
                sWrappedText += sText.substring(0,iNextSpace+1);
                sText = sText.substring(iNextSpace+1);
            }
         } else {
             sWrappedText += sText.substring(0,iNextSpace+1);
             sText = sText.substring(iNextSpace+1);
         }
      }
      sWrappedText += sText;
      return sWrappedText;
   }

   public static String targetHrefs(String text,String rUrl) {
       // *** remove existing targets ***
       text = replace(text,"target=_blank","");
       text = replace(text,"target=_top","");
       text = replace(text,"target=\"_blank\"","");
       text = replace(text,"target=\"_top\"","");
       
       // find hrefs and see if they are external or internal
       String textLC = text.toLowerCase();
       int aPosOpen = textLC.indexOf("<a ");
       while(aPosOpen>-1) {
           int aPosClose = textLC.indexOf("</a>",aPosOpen);
           if(aPosClose>-1) {
               // *** detect internal urls ***
               int nPos = textLC.substring(aPosOpen,aPosClose).indexOf(rUrl);
              
               // *** urls with http are supposed to be internal ***
               if(textLC.substring(aPosOpen,aPosClose).indexOf("http")==-1) { nPos = 0; }
              
               // *** add target ***
               if(nPos==-1) {
                   text = text.substring(0,aPosOpen+2) + " target=\"_blank\" " +  text.substring(aPosOpen+3);   
               } else {
                   text = text.substring(0,aPosOpen+2) + " target=\"_top\" " +  text.substring(aPosOpen+3);
               }
               textLC = text.toLowerCase();
           }
           aPosOpen = textLC.indexOf("<a ",aPosClose);
       }
       return text;
   }

   public static String fixHrefs(String text) {
      // find hrefs ruined by range.pasteHTML(link.outerHTML); in my-htmlarea.js
      String ewUrl = "/mmbase/edit/wizard/jsp/";
      int ewlPos = text.indexOf(ewUrl);
      while(ewlPos>-1) {
         int httpPos = text.substring(0,ewlPos).lastIndexOf("http://");
         if(httpPos>-1) {
            int barPos = text.indexOf("|",ewlPos);
            if(barPos>-1) { // first time saved local link marked with |
               text = text.substring(0,httpPos) + text.substring(barPos+1);   
            } else { // second time save local link ends with wizard.jsp
               text = text.substring(0,httpPos) + text.substring(ewlPos + (ewUrl + "wizard.jsp").length());
            }
         }
         ewlPos = text.indexOf(ewUrl,ewlPos+1);
      }
      return text;
   }


   public static String replace(String text, String oldStr, String newStr) {
       // replaces oldStr by newStr (e.g. a space important for <br> and <p> )
       int oldStrsPos =  text.indexOf(oldStr); 
       while(oldStrsPos>-1){
           text = text.substring(0,oldStrsPos) + newStr + text.substring(oldStrsPos+oldStr.length());
           oldStrsPos =  text.indexOf(oldStr, oldStrsPos + newStr.length()); 
       }       
       return text;
   }

   public static String cleanText(String text, String tagOpenStr, String tagCloseStr, String replaceStr) {
       // replaces the tag by a replaceStr
       int tagOpen =  text.indexOf(tagOpenStr); 
       int tagClose =  text.indexOf(tagCloseStr,tagOpen);
       while(tagOpen>-1&&tagClose>-1){
           text = text.substring(0,tagOpen) + replaceStr + text.substring(tagClose+tagCloseStr.length());
           tagOpen =  text.indexOf(tagOpenStr,tagOpen+replaceStr.length()); 
           tagClose =  text.indexOf(tagCloseStr,tagOpen);
       }       
       return text;
   }

   public static String cleanText(String text, String tagOpenStr, String tagCloseStr) {
       // replaces the tag by a space (important for <br> and <div>)
       return cleanText(text,tagOpenStr,tagCloseStr," ");
   }

   public static String cleanPs(String text) {
       text = cleanText(text,"<P",">");
       text = cleanText(text,"</P",">");
       text = cleanText(text,"<p",">");
       text = cleanText(text,"</p",">");
       return text;
   }
  
   public static String cleanBRs(String text) {
       text = cleanText(text,"<BR",">");
       text = cleanText(text,"<br",">");
       text = cleanText(text,"<Br",">");
       text = cleanText(text,"<bR",">");
       return text;
   }
   
   public static String cleanEmptyTag(String text, String startTag, String closeTag, String replaceStr) {
       // replaces occurences of <startTag>&nbsp; &nbsp; [= all spaces and &nbsp's] </closeTag> with replaceStr
       int startTagOpen =  text.indexOf(startTag); 
       while(startTagOpen>-1){
           int startTagClose =  text.indexOf(">",startTagOpen);
           int closeTagOpen =  text.indexOf(closeTag,startTagClose); 
           int closeTagClose =  text.indexOf(">",closeTagOpen);        
           if(closeTagOpen>-1){
               String tagedString = text.substring(startTagClose+1,closeTagOpen);
               int nbspPos = tagedString.indexOf("&nbsp;");
               while(nbspPos>-1){
                   tagedString = tagedString.substring(0,nbspPos) + tagedString.substring(nbspPos+6);
                   nbspPos = tagedString.indexOf("&nbsp;");
               }
               if(tagedString.trim().equals("")){
                   text = text.substring(0,startTagOpen) + replaceStr + text.substring(closeTagClose+1);
                   startTagOpen =  text.indexOf(startTag,startTagOpen+replaceStr.length());
               } else {
                   startTagOpen =  text.indexOf(startTag,closeTagClose+1);         
               }
           } else {
               startTagOpen = -1;
           }
       }
       return text;
   }

   public static String cleanEmptyTag(String text, String startTag, String closeTag) {
       // replaces occurences of <startTag>&nbsp; &nbsp; etc; </closeTag> by &nbsp;
       return cleanEmptyTag(text,startTag,closeTag," ");
   }

   public static String cleanTags(String text, String startTag, String closeTag) {
       // replaces occurences of <p>, </p> within the context of <startTag>... </closeTag>
       String textLC = text.toLowerCase();
       int startTagOpen =  textLC.indexOf(startTag); 
       while(startTagOpen>-1){
           int startTagClose =  textLC.indexOf(">",startTagOpen);
           int closeTagOpen =  textLC.indexOf(closeTag,startTagClose); 
           int closeTagClose =  textLC.indexOf(">",closeTagOpen);      
           if(closeTagOpen>-1){
               String tagedString = text.substring(startTagClose+1,closeTagOpen);
               tagedString = cleanText(tagedString,"<P",">");
               tagedString = cleanText(tagedString,"<p",">");
               tagedString = cleanText(tagedString,"</P",">");
               tagedString = cleanText(tagedString,"</p",">");
               text = text.substring(0,startTagClose+1) + tagedString + text.substring(closeTagOpen);
               textLC = text.toLowerCase();
               startTagOpen =  textLC.indexOf(startTag,startTagClose + 1);         
           } else {
               startTagOpen = -1;
           }
       }
       return text;
   }

   public static String cleanParam(String text, String paramStr) {
       // deletes occurences of param in text
       int paramOpen =  text.indexOf(paramStr); 
       while(paramOpen>-1){
           // find the max of the quotation mark and the space before the closing greater than
           int paramClose =  text.indexOf(">",paramOpen)-1; // -1 to leave the tag undamaged
           if(paramClose>-1){ // there is a close tag
               int paramCloseQMark =  text.indexOf("\"",text.indexOf("\"",paramOpen)+1);
                   if((paramCloseQMark==-1)||(paramCloseQMark>paramClose)) paramCloseQMark = paramClose;
               int paramCloseSpace =  text.indexOf(" ",paramOpen);
                   if((paramCloseSpace==-1)||(paramCloseSpace>paramClose)) paramCloseSpace = paramClose;
               if(paramCloseQMark<paramCloseSpace){
                   paramClose = paramCloseSpace;
               } else {
                   paramClose = paramCloseQMark;
               }
           } else { // no close tag, leave unchanged
               paramClose = paramOpen;
           }
           text = text.substring(0,paramOpen) 
               //  + "<!-- qm=" + paramCloseQMark + " s=" + paramCloseSpace + " c=" + paramCloseQMark + " -->"
                   + text.substring(paramClose+1);
           paramOpen =  text.indexOf(paramStr); 
       }       
       return text;
   }

   public static String stripText(String text) {
       // cleans text from & and spaces
       text = text.toLowerCase();
       text = text.replaceAll("&","en");
	    text = text.replaceAll(" ","_");
		 text = text.replaceAll("\'","");
       for(int charPos = 0; charPos < text.length(); charPos++){
           char c = text.charAt(charPos);
           if  (   !(('a'<=c)&&(c<='z'))
               &&  !(('0'<=c)&&(c<='9'))
               &&  !(c=='-')
               &&  !(c=='_') 
               &&  !(c=='.')
               ) { 
                   text = text.substring(0,charPos) + "_" + text.substring(charPos+1);
               }
       }
       return text;
   }

   public static String stripParam(String paramString, String param, String seperator) {
           int from = paramString.indexOf(param); 
           if(from!=-1){
               int to = paramString.indexOf(seperator,from);
               if(to==-1){ to = paramString.length(); }
               paramString = paramString.substring(0,from-1)+paramString.substring(to); ; 
           }
           return paramString;
   }

   public static boolean insideTag(String textStr, int spos, int epos, String startTag, String endTag) {
       // *** checks if the substring spos,epos lies inside the tag defined by startTag, endTag ***
       boolean insideTag = false;
       if(0<=spos&&spos<=epos&&epos<textStr.length()) {
           //*** search last non closed startTag before spos ***
           int tagOpen = textStr.substring(0,spos).lastIndexOf(startTag);
           if(tagOpen>-1) {
               if(textStr.substring(tagOpen,spos).indexOf(endTag)>-1) { 
                   tagOpen = -1;
               }
           }
           //*** search first non opened endTag after epos ***
           String tailString = textStr.substring(epos);
           int tagClose = tailString.indexOf(endTag);
           if(tagClose>-1) {
               if(tailString.substring(0,tagClose).indexOf(startTag)>-1) {
                   tagClose = -1;
               }
           }
           if(tagOpen>-1&&tagClose>-1) { // *** the substring is inside the tag ***
               insideTag = true;
           }
       }
       return insideTag;
   }

   public static boolean insideTag(String textStr, int pos, String startTag, String endTag) {
       // *** checks if pos lies inside the tag defined by startTag, endTag ***
       return insideTag(textStr,pos,pos,startTag,endTag);
   }

   public static String [] rawString() {
      String rawString [] = {
           "&Aacute;","&Acirc;","&Agrave;","&Auml;","&Atilde;","&Aring;",
           "&Eacute;","&Ecirc;","&Egrave;","&Euml;",
           "&Icirc;","&Iuml;","&Igrave;","&Iacute;",
           "&Ocirc;","&Ouml;","&Ograve;","&Oacute;","&Otilde;","&Oslash;",
           "&Uuml;","&Ugrave;","&Uacute;","&Ucirc;",
           "&aacute;","&acirc;","&agrave;","&auml;","&atilde;","&aring;",
           "&eacute;","&ecirc;","&egrave;","&euml;",
           "&icirc;","&iuml;","&igrave;","&iacute;",
           "&ocirc;","&ouml;","&ograve;","&oacute;","&otilde;","&oslash;",
           "&uuml;","&ugrave;","&uacute;","&ucirc;",
           "&aelig;","&ccedil;","&szlig;","&yuml;","&copy;",
           "&pound;","&reg;","&quot;",
           "&eth;","&ntilde;","&divide;","&yacute;",
           "&thorn;","&times;","&nbsp;",
           "&sect;","&cent;","&deg;",
           "&dagger;","&trade;","&euro;",
           "&rsquo;",
           "&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;","&lsquo;",
           "&rsquo;","&rsquo;","&rsquo;","&rsquo;","&rsquo;","&rsquo;","&rsquo;","&rsquo;","&rsquo;","&rsquo;","&rsquo;",
           "&quot;","&quot;","&quot;","&quot;","&quot;","&quot;","&quot;","&quot;","&quot;",
           "&quot;","&quot;","&quot;","&quot;","&quot;","&quot;","&quot;","..","&hellip;",
           "-","-","-","-","-","-","-","-","-","&shy;"};
       return rawString;
   }

   public static char [] translatedChar() {
       // Unicode representation of rawString
       char translatedChar[] = {
           '\u00c1','\u00c2','\u00c0','\u00c4','\u00c3','\u00c5',
           '\u00c9','\u00ca','\u00c8','\u00cb',
           '\u00ce','\u00cf','\u00cc','\u00cd',
           '\u00d4','\u00d6','\u00d2','\u00d3','\u00d5','\u00d8',
           '\u00dc','\u00d9','\u00da','\u00db',        
           '\u00e1','\u00e2','\u00e0','\u00e4','\u00e3','\u00e5',
           '\u00e9','\u00ea','\u00e8','\u00eb',
           '\u00ee','\u00ef','\u00ec','\u00ed',
           '\u00f4','\u00f6','\u00f2','\u00f3','\u00f5','\u00f8',
           '\u00fc','\u00f9','\u00fa','\u00fb',
           '\u00e6','\u00e7','\u00df','\u00ff','\u00a9',
           '\u00a3','\u00ae','\u0022',
           '\u00f0','\u00f1','\u00f7','\u00fd',
           '\u00fe','\u00d7','\u00a0',
           '\u00a7','\u00a2','\u00b0',
           '\u2020','\u2122','\u20AC',
           '\u02C8',
           '\u0060','\u02BB','\u02BD','\u02BF','\u02CB','\u02CE','\u02F4','\u0559','\u055D','\u2035','\u2018','\u201B','\u8216',
           '\u00B4','\u02B9','\u02BC','\u02CA','\u02CF','\u0374','\u055A','\u055B','\u2019','\u2032','\u8217',
           '"','\u0022','\u02BA','\u02DD','\u02F5','\u02F6','\u201C','\u201D','\u201E','\u201F',
           '\u2033','\u2036','\u301D','\u301E','\u301F','\u8220','\u8221','\u2025','\u2026',
           '\u002D','\u2010','\u2011','\u2012','\u2013','\u2014','\u2015','\u2212','\u00AD'};
       return translatedChar;
   }

   public static char [] standardChar() {
      // Unicode representation of rawString
      char standardChar[] = {
           'A','A','A','A','A','A',
           'E','E','E','E',
           'I','I','I','I',
           'O','O','O','O','O','O',
           'U','U','U','U',        
           'a','a','a','a','a','a',
           'e','e','e','e',
           'i','i','i','i',
           'o','o','o','o','o','o',
           'u','u','u','u'};
       return standardChar;
   }

   public static String filterEntities(String text) {
       // translatedChar --> rawString
       // Excluded are:
       // ,"&lt;","&gt;","&amp;"
       // ,'\u003c','\u003e','\u0026'
       String rawString[] = rawString();
       char translatedChar[] = translatedChar();
       for(int c= 0; c<translatedChar.length; c++) {
           int cpos = text.indexOf(translatedChar[c]);
           while(cpos>-1) {
               if(!insideTag(text,cpos,"<",">")) { // *** not inside a tag ***
                   text =  text.substring(0,cpos) + rawString[c] + text.substring(cpos+1);
               }
               cpos = text.indexOf(translatedChar[c],cpos+rawString[c].length());
           }
       }
       text = replace(text, "\'","&rsquo;"); // problem with '\u0027' ;-)
       return text;
   }
   
   public static String filterEntitiesEvents(String text) {
      // translatedChar --> rawString
      // Excluded are:
      // ,"&lt;","&gt;","&amp;"
      // ,'\u003c','\u003e','\u0026'
      String rawString[] = rawString();
      char translatedChar[] = translatedChar();
      for(int c= 0; c<translatedChar.length; c++) {
          int cpos = text.indexOf(translatedChar[c]);
          while(cpos>-1) {
              if(!insideTag(text,cpos,"<",">")) { // *** not inside a tag ***
                  text =  text.substring(0,cpos) + rawString[c] + text.substring(cpos+1);
              }
              cpos = text.indexOf(translatedChar[c],cpos+rawString[c].length());
          }
      }
      text = replace(text, "\'","&rsquo;");
      text = replace(text, "\"","&quot;");
      return text;
  }   

   public static String filterTextEntities(String text) {
       // rawString --> translatedChar
       // Excluded are:
       // ,"&lt;","&gt;","&amp;"
       // ,'\u003c','\u003e','\u0026'
       text = replace(text, "&rsquo;","\'");
       text = replace(text, "&lsquo;","\'");
       String rawString[] = rawString();
       char translatedChar[] = translatedChar();
       for(int c= 0; c<rawString.length; c++) {
           int cpos = text.indexOf(rawString[c]);
           while(cpos>-1) {
               text =  text.substring(0,cpos) + translatedChar[c] + text.substring(cpos+rawString[c].length());
               cpos = text.indexOf(rawString[c],cpos+1);
           }
       }
       return text;
   }


   public static String filterAmps(String text) {
      // special function to replace & by &amp;
      int cpos = text.indexOf("&");
      while(cpos>-1) {
         int scpos = text.indexOf(";",cpos+1);
         if(scpos==-1 || ( text.substring(cpos,scpos).indexOf(" ")>-1 )) { // this is a real & and not the beginning of an entity like &Aacute;
            text =  text.substring(0,cpos) + "&amp;" + text.substring(cpos+1);
         }
         cpos = text.indexOf("&",cpos+1);
       }
       return text;
   }

   public static String showEntities() {
       String entitiesStr = "";
       String rawString [] = rawString(); 
       char translatedChar [] = translatedChar(); 
       char standardChar [] = standardChar(); 
       for(int c= 0; c<translatedChar.length; c++){
           entitiesStr +=  c + " : " + translatedChar[c] + " , " +   rawString[c];
           if(c<standardChar.length) entitiesStr += " , " +   standardChar[c];
           entitiesStr += "<br>";
       }
       return entitiesStr;
   }

   public static String replaceMSChars(String text) {
      // *** replaces imported Microsoft chars ***
      for(int i=0; i<text.length(); i++) {
         char c = text.charAt(i);
         if(Character.getType(c)==15) {
            int hC = (new Character(c)).hashCode();
            String replaceStr = "" + c;
            if(hC==128) { replaceStr = "&euro;"; }
            if(hC==130) { replaceStr = "&sbquo;"; }
            if(hC==131) { replaceStr = "&fnof;"; }
            if(hC==132) { replaceStr = "&bdquo;"; }
            if(hC==133) { replaceStr = "&hellip;"; }
            if(hC==134) { replaceStr = "&dagger;"; }
            if(hC==135) { replaceStr = "&Dagger;"; }
            if(hC==136) { replaceStr = "&circ;"; }
            if(hC==137) { replaceStr = "&permil;"; }
            if(hC==138) { replaceStr = "&Scaron;"; }
            if(hC==139) { replaceStr = "&lsaquo;"; }
            if(hC==140) { replaceStr = "&OElig;"; }
            if(hC==145) { replaceStr = "&lsquo;"; }
            if(hC==146) { replaceStr = "&rsquo;"; }
            if(hC==147) { replaceStr = "&ldquo;"; }
            if(hC==148) { replaceStr = "&rdquo;"; }
            if(hC==149) { replaceStr = "&bull;"; }
            if(hC==150) { replaceStr = "-"; }
            if(hC==151) { replaceStr = "&mdash;"; }
            if(hC==152) { replaceStr = "&tilde;"; }
            if(hC==153) { replaceStr = "&trade;"; }
            if(hC==154) { replaceStr = "&scaron;"; }
            if(hC==155) { replaceStr = "&rsaquo;"; }
            if(hC==156) { replaceStr = "&oelig;"; }
            if(hC==159) { replaceStr = "&Yuml;"; }
            text = text.substring(0,i) + replaceStr + text.substring(i+1);
         }
         
      }
      return text;
   }  

   public static String setTableHeaders(String text) {
       // *** set tds in first tr of each table to th ***
       String textUC = text.toUpperCase();
       int tsPos = textUC.indexOf("<TABLE");
       int tePos = textUC.indexOf("</TABLE",tsPos);
       while(tsPos>-1&&tePos>tsPos) {
           int thPos = textUC.indexOf("<TH",tsPos);
           if(thPos==-1||thPos>tePos) { // *** no th in this table ***
               int trsPos = textUC.indexOf("<TR",tsPos);
               int trePos = textUC.indexOf("</TR",tsPos);
               if(trsPos>-1&&trePos>trsPos) {
                   int tdsPos = textUC.indexOf("<TD",trsPos);
                   while(tdsPos>-1&&tdsPos<trePos) {
                       text = text.substring(0,tdsPos) + "<TH" + text.substring(tdsPos+3); 
                       int tdePos = textUC.indexOf("</TD",tdsPos);
                       if(tdePos>-1) {
                           text = text.substring(0,tdePos) + "</TH" + text.substring(tdePos+4); 
                       }
                       tdsPos = textUC.indexOf("<TD",tdePos);
                   }
               }
           }
           tsPos = textUC.indexOf("<TABLE",tePos);
           tePos = textUC.indexOf("</TABLE",tsPos);
       }
       return text;
   }

   public static String colorTables(String text, String borderColor, String bgColor, String bgColorTh) {
       // *** set tds in first tr of each table to th ***
       String textUC = text.toUpperCase();
       int tsPos = textUC.indexOf("<TABLE");
       int tePos = textUC.indexOf(">",tsPos);
       while(tsPos>-1&&tePos>tsPos) {
           text = text.substring(0,tsPos+6) 
                       + " borderColor=\"" + borderColor
                       + "\" bgColor=\"" + bgColor 
                       + "\" border=\"1\" cellSpacing=\"0\" cellPadding=\"0\"" 
                       + text.substring(tePos);
           textUC = text.toUpperCase();
           tsPos = textUC.indexOf("<TABLE",tePos);
           tePos = textUC.indexOf(">",tsPos);
       }
       tsPos = textUC.indexOf("<TH");
       tePos = textUC.indexOf(">",tsPos);
       while(tsPos>-1&&tePos>tsPos) {
           text = text.substring(0,tsPos+3) 
                       + " bgColor=\"" + bgColorTh 
                       + "\""
                       + text.substring(tePos);
           textUC = text.toUpperCase();
           tsPos = textUC.indexOf("<TH",tePos);
           tePos = textUC.indexOf(">",tsPos);
       }
       return text;
   }

   public static String cleanHtml(String text) {
      if (text == null) return null;
      if (text.equalsIgnoreCase("")) return text;
      
       // *** everything in capitals ***
       text = replace(text,"<div","<DIV"); text = replace(text,"</div","</DIV");
       text = replace(text,"<h1","<H1"); text = replace(text,"</h1","</H1");
       text = replace(text,"<h2","<H2"); text = replace(text,"</h2","</H2");
       text = replace(text,"<h3","<H3"); text = replace(text,"</h3","</H3");
       text = replace(text,"<h4","<H4"); text = replace(text,"</h4","</H4");
       text = replace(text,"<h5","<H5"); text = replace(text,"</h5","</H5");
       text = replace(text,"<h6","<H6"); text = replace(text,"</h6","</H6");
       text = replace(text,"<font","<FONT"); text = replace(text,"</font","</FONT");
       text = replace(text,"<span","<SPAN"); text = replace(text,"</span","</SPAN");
       text = replace(text,"<tbody","<TBODY"); text = replace(text,"</tbody","</TBODY");
       text = replace(text,"<b","<B"); text = replace(text,"</b","</B");
       text = replace(text,"<p","<P"); text = replace(text,"</p","</P");
       text = replace(text,"<strike","<STRIKE"); text = replace(text,"</strike","</STRIKE");

       text = cleanText(text,"<DIV",">"); text = cleanText(text,"</DIV",">");      
       text = cleanText(text,"<H1",">"); text = cleanText(text,"</H1",">");        
       text = cleanText(text,"<H2",">"); text = cleanText(text,"</H2",">");        
       text = cleanText(text,"<H3",">"); text = cleanText(text,"</H3",">");        
       text = cleanText(text,"<H4",">"); text = cleanText(text,"</H4",">");        
       text = cleanText(text,"<H5",">"); text = cleanText(text,"</H5",">");        
       text = cleanText(text,"<H6",">"); text = cleanText(text,"</H6",">");        
       text = cleanText(text,"<FONT",">"); text = cleanText(text,"</FONT",">");        
       text = cleanText(text,"<SPAN",">"); text = cleanText(text,"</SPAN",">");
       text = cleanText(text,"<TBODY",">"); text = cleanText(text,"</TBODY",">");
       text = cleanText(text,"<STRIKE",">"); text = cleanText(text,"</STRIKE",">");
       text = cleanText(text,"<o:p",">"); text = cleanText(text,"</o:p",">");
       text = cleanText(text,"<?",">");
       text = cleanText(text,"<small",">"); text = cleanText(text,"</small",">");
       text = cleanText(text,"<Blockquote",">"); text = cleanText(text,"</Blockquote",">");

       text = cleanParam(text,"class=");
       text = cleanParam(text,"style=");
       text = cleanParam(text,"vAlign=");
       text = cleanParam(text,"width=");
       
       // this looks strange but is intended to remove the crap that can come from the html-area
       text = replace(text,"<P >","<P>");
       text = cleanEmptyTag(text,"<P>","<P>","<P><P>");
       text = replace(text,"<P><P>","<P>");
       text = cleanEmptyTag(text,"</P>","</P>","</P></P>");
       text = replace(text,"</P></P>","</P>");
   
       text = cleanEmptyTag(text,"<B","</B");
       text = cleanEmptyTag(text,"<P","</P");
       text = replace(text,"<P/>","<BR/>");
       text = replace(text,"<P />","<BR/>");
       
       text = fixHrefs(text);

       // paragraphs in Word have margin-bottom: 0px;, so remove &nbsp;'s between paragraph's
       text = cleanEmptyTag(text,"</P","<P","</P><P>");
       // Word add's paragraphs in table cells
       text = cleanTags(text,"<td","</td");
       
       text = filterEntities(text);
         
       text = setTableHeaders(text);
       text = colorTables(text,"#FFFFFF","#E8F4FF","#C4C6C8");
       return text;
   }

   // Escape URLs to Valid format 
   public static String forURL(String aURLFragment){
      String result = null;
      try {
         result = URLEncoder.encode(aURLFragment, "UTF-8");
      }
      catch (UnsupportedEncodingException ex){
         throw new RuntimeException("UTF-8 not supported: problem with forUrl escaping", ex);
      }
      return result;
   }

   public HtmlCleaner() {
   }
}