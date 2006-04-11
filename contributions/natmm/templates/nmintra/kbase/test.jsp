<%!
  static String clipPath(String path){
    while(path.startsWith(",")){
      path=path.substring(1);
    }
    while(path.endsWith(",")){
      path=path.substring(0,path.length()-1);
    }
  return path;
  }
%>

<%
  String path="12,12,12";
  out.write(clipPath(path));
%>
