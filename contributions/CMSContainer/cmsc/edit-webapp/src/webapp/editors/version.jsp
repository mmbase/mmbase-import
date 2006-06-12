<%@ page import="java.util.regex.*" %><%
    String version = "$Name: not supported by cvs2svn $";
    Matcher m = Pattern.compile("\\$[N]ame: (.*)\\$").matcher(version);
    version = m.find() ? m.group(1).trim() : "dev";
    if (version.length() == 0) {
        version = "dev";
    } %><%= version.replaceAll("_", ".") %>