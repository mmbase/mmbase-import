<%
// HORRRIBLE
    //System.err.println("Cleaning up '"+text+"'");
        //
        // remove some of the annoying html that messes up the PDFs
        //
        text = text.replaceAll("</?(font|style)[^>]*>","");
        text = text.replaceAll("(?<=[^>]\\s)+(width|height|style|align)=\\s*(\"[^\"]*\"|'[^']*'|\\S+)","");
//        text = text.replaceAll("<(t[dh][^>]*)>","<$1 width=\"100%\">");
        text = text.replaceAll("<table[^>]*>","<table border='1' valign='top' cellpadding='4' width='100%' class='Font'>");
        text = text.replaceAll("<p\\s*/>","");
        text = text.replaceFirst("\\A\\s*","");
        text = text.replaceFirst("\\s*\\z","");
        if (!text.startsWith("<p>")) {
            text = "<p>"+text;
        }
        if (!text.endsWith("</p>")) {
            text = text+"</p>";
        }
    //System.err.println("Result: '"+text+"'");
%><%= text %>
