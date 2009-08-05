package nl.ou.rdmc.stats.model.comp;

import java.util.Comparator;
import nl.ou.rdmc.stats.model.FUser;

public class FUserComparator implements Comparator {

  public FUserComparator() {
  }

  public int compare(Object o1, Object o2) throws ClassCastException {
    if (!(o1 instanceof FUser)) throw new ClassCastException();
    if (!(o2 instanceof FUser)) throw new ClassCastException();
    FUser u1 = (FUser)o1;
    FUser u2 = (FUser)o2;
    return u1.getName().compareTo(u1.getName());
  }

}
