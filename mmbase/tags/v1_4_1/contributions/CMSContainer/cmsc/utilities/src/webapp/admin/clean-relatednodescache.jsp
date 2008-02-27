<%@page import="java.util.*" %>
<%!
static CacheClear clear;

public class CacheClear {
    
    CacheClearTask task = null;
    
    public CacheClear() {
        if (task == null) {
            task = new CacheClearTask();
            new Timer().schedule(task, 0, 10*60*1000);
        }
    }
    
    class CacheClearTask extends TimerTask {

		public Date last;

        public void run() {
            double percentage = getPercentage();
            if (percentage < 15) {
	        	last = new Date();
                org.mmbase.cache.RelatedNodesCache.getCache().clear();
            }
        }

        public double getPercentage() {
            long free = Runtime.getRuntime().freeMemory();
            long total = Runtime.getRuntime().totalMemory();
            double percentage = ((double) free / total) * 100;
            return percentage;
        }
    }
}
%>

<%
	if (clear == null) {
	    	clear = new CacheClear();
	}
%>
Memory free: <%= clear.task.getPercentage() %><br />
Last run: <%= clear.task.last %>
