<%@page import="java.util.*, java.io.*, java.text.* " session="true"%>

<%! class DirSorter implements Comparator {
     public int compare(Object o1, Object o2) {
           File f2 =(File) o2;
           File f1 = (File) o1;
           return f2.getName().compareTo(f1.getName());
     }
}
	class BuildInfo{
		public String link = null;
		public Date  date = null;
		public String  dateString = null;
		public String  remarks = "";
		public String  recentchangeslink = "";
		public String  docsLink = "";
	}

List  showDirs(File thisDir, String prefix, int max) throws IOException {
  List buildInfos = new Vector();
  File[] content = thisDir.listFiles(); 
  Arrays.sort(content, new DirSorter());
  int found = 0;
  DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
  for (int i=0; i< content.length ; i++) {
    BuildInfo info = new BuildInfo();
    File f = (File) content[i];
    if (f.isDirectory() && (! f.getName().equals("last")) && (new File(f, "messages.log")).exists()) {
	found ++;
	    if (found >= max) break;
       info.link = "/development/download/build_page.jsp?dir=" + prefix + "/" + f.getName();
       Date date = new Date(new File(f, "messages.log").lastModified());
       info.date = date;
       info.dateString = df.format(date);
       File remark = new File(f, "REMARKS.txt");
       if (remark.canRead()) {
           java.io.FileReader reader = new java.io.FileReader(remark);
           java.io.StringWriter string = new java.io.StringWriter();
           int c = reader.read();
           while (c != -1) {
              string.write(c);
              c = reader.read();
           }
          info.remarks = string.toString();
       }
       File changes = new File(f, "RECENTCHANGES.txt");
       if(changes.canRead()) {
         info.recentchangeslink = prefix + f.getName() + "/RECENTCHANGES.txt";
       }
       File docs = new File(f, "mmdocs");
       if(docs.canRead()) {
	 info.docsLink = prefix + f.getName() + "/mmdocs/";
       }
    }
    if (info.link != null){
	buildInfos.add(info);
    }
  }
  return buildInfos;
}

 List getHeadBuilds(int max) throws IOException{
	return showDirs(new File("/home/nightlybuild/builds"), "head", max);
 }

 List getStableBuilds(int max) throws IOException{
	return showDirs(new File("/home/nightlybuild/builds/stable"),"stable", max);
 }

 List getOccasionalBuilds(int max) throws IOException{
	return showDirs(new File("/home/nightlybuild/builds/occasional"),"occasional", max);
 }
%>



