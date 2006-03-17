package nl.ou.rdmc.stats.model.comp;

import java.util.Comparator;
import nl.ou.rdmc.stats.model.FSubtrace;


public class FSubtraceComparator implements Comparator {
  public FSubtraceComparator() {
  }

  public int compare(Object o1, Object o2) throws ClassCastException {
    if (!(o1 instanceof FSubtrace)) throw new ClassCastException();
    if (!(o2 instanceof FSubtrace)) throw new ClassCastException();
    FSubtrace t1 = (FSubtrace)o1;
    FSubtrace t2 = (FSubtrace)o2;

    int result = t1.getUser().getName().compareTo(t2.getUser().getName());
    if (result!=0) return result;
    return t1.getStart().compareTo(t2.getStart());
  }

}
