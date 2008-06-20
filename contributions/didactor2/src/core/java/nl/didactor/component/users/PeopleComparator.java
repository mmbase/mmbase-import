package nl.didactor.component.users;

import java.util.Comparator;


/**
 * lastname,firstname comparator for people
 * @author azemskov
 * @javadoc
 */
public class PeopleComparator implements Comparator {

   public int compare(Object ob1, Object ob2)    {
      String[] arrstrObj1 = (String[]) ob1;
      String[] arrstrObj2 = (String[]) ob2;

      int iResult = arrstrObj1[1].compareTo(arrstrObj2[1]);
      if (iResult != 0) {
         return iResult;
      } else {
         return arrstrObj1[2].compareTo(arrstrObj2[2]);
      }
   }
}
