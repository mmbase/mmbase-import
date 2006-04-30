<%  expireTime =  3600*24; // cache for one day
    if(templateTitle.equals("smoelenboek")
         ||templateTitle.equals("Zoeken")
         ||templateTitle.equals("formulier")
         ||templateTitle.equals("winkelwagen")
         ||templateTitle.equals("project archief")
         ||templateTitle.equals("externe website (popup)")
         ||templateTitle.equals("externe website (nieuw venster)")
         ||!postingStr.equals("|")
         ||!termSearchId.equals("")
         ||!emailId.equals("")
         ||isPreview
      ) { expireTime = 0; }
    
%><%@include file="../includes/cachekey.jsp"
%><!-- <%= new java.util.Date() %> -->