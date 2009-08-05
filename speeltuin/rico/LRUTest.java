import java.util.*;
import org.mmbase.util.*;

class LRUTest {
   
public static int TREESIZ=1024;
public static int OPERS=1000000;

   public static void main(String[] args) {
      int i=0,j,k,l;
      Object rtn=null;
      Random rnd=new Random();
       LRUHashtable treap;
      long ll1,ll2,ll3,ll4;
      int score[]=new int[TREESIZ];
   
      ll1=System.currentTimeMillis();
      treap=new LRUHashtable(TREESIZ);
      for (i=0;i<TREESIZ;i++) {
         j=i;
         treap.put(""+j,""+j);
      }
      ll2=System.currentTimeMillis();
      System.out.println("Size "+treap.size());
      System.out.println("LRUHashtable initaly "+treap.toString(true));
      ll3=System.currentTimeMillis();
      for (i=0;i<OPERS;i++) {
         // Put and get mixed
         j=Math.abs(rnd.nextInt())%(TREESIZ/2)+(TREESIZ/4);
         k=Math.abs(rnd.nextInt())%2;
         k=1;
         switch (k) {
            case 0:
               rtn=treap.put(""+j,""+j);
               score[j]++;
            break;
            case 1:
               rtn=treap.get(""+j);
               score[j]++;
            break;
         }
         // Only a get
         j=Math.abs(rnd.nextInt())%(TREESIZ);
         rtn=treap.get(""+j);
         score[j]++;
      }
      ll4=System.currentTimeMillis();
      System.out.println("LRUHashtable afterwards "+treap.toString(true));
      for (i=0;i<TREESIZ;i++) {
         System.out.println(""+i+" score "+score[i]);
      }
      System.out.println("Creation "+(ll2-ll1)+"ms");
      System.out.println("Print    "+(ll3-ll2)+"ms");
      System.out.println("Run      "+(ll4-ll3)+"ms");
   }
}
