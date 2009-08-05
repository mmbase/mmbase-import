<%@page import="java.util.*"%><%@page import="java.io.*"%><%!
   public static class Value{
      long time;
      Object value;
      public Value(Object value){
       time = System.currentTimeMillis();
       this.value = value;
      }
      public Object getValue(){
        return value;
      }
      public long getTime(){
        return time;
      }
   }

 static class Stats implements Runnable{
   private static Stats statsInstace = null;
   List list = new ArrayList();



   private Stats(){
     Thread t = new Thread(this);
     t.setDaemon(true);
     t.start();
   }

   public static Stats getInstance(){
    if (statsInstace == null){
       statsInstace = new Stats();
    }
    return statsInstace;
   }

   public Double getServerLoad(){
    String ret = "";
    BufferedReader br = null;
    File file;
    try {
      file = new File("/proc/loadavg");
      br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String line= br.readLine();
      StringTokenizer st = new StringTokenizer(line," ");
      if (st.hasMoreTokens()){
         String minute = st.nextToken();
         return Double.valueOf(minute);
      }
    } catch (Throwable t){
    } finally {
      if (br != null){
       try {
       br.close();
       } catch (Throwable t){}
       
      }
      file = null;
    }
    return null;
   }
   public void run(){
    while(true){
       list.add(new Value(getServerLoad()));
       if (list.size() > 10000){
           for (int x =0 ;x < 10; x++){
            list.remove(0);
           }
       }
       try {
           Thread.sleep(15 * 1000);
       } catch (Throwable t){
         return;
       }
    }
       
   }
   public List getList(){
      return list;
   }
 }
%><% List list =  Stats.getInstance().getList(); %><stats>
<% for (int x =0 ; x < list.size() ; x++){
   Value value = (Value)list.get(x); 
   double v = ((Double)value.getValue()).doubleValue(); %><value time="<%= value.getTime() %>" value="<%= v * 100 %>"/>
<% } %></stats>
