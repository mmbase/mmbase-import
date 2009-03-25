package com.finalist.cmsc.tasks;

import java.util.Comparator;

import org.mmbase.bridge.Node;

/**
* Comparator to order task nodes on urgency. Sorting is not done on alphabetical order but on the value of status where 
* status "init" has position 1; status "notified" position 2 and status "done" position 3. And if the same status, order
* by "deadline" up.
* 
 * @author Marco
 */
public class TaskUrgencyComparator implements Comparator{
   
   private static final String STATUS = "status";
   private static final String DEADLINE = "deadline";
   
   private static final String INIT = "task.status.init"; 
   private static final String NOTIFIED = "task.status.notified";
   private static final String DONE = "task.status.done";
   
   public int compare(Object o1, Object o2) {
      String status1 = ((Node) o1).getStringValue(STATUS); 
      String status2 = ((Node) o2).getStringValue(STATUS);
      if (status1.equals(INIT) && status2.equals(INIT) || 
         status1.equals(NOTIFIED) && status2.equals(NOTIFIED) || 
         status1.equals(DONE) && status2.equals(DONE)) {
         return ((Node) o1).getStringValue(DEADLINE).compareTo(((Node) o2).getStringValue(DEADLINE));
      }
      else if (status1.equals(INIT) && status2.equals(NOTIFIED) ||
             status1.equals(INIT) && status2.equals(DONE) ||
             status1.equals(NOTIFIED) && status2.equals(DONE)) {
         return -1;
      }
      else {
         return 1;
      }
   }
}
