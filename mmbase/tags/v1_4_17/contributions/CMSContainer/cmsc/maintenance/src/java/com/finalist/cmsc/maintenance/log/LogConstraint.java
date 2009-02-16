package com.finalist.cmsc.maintenance.log;

import java.util.regex.Pattern;

/**
 * @author Jeoffrey Bakker (Finalist IT Group)
 */
public class LogConstraint {
   private Pattern machinePattern;
   private Pattern numberPattern;
   private Pattern builderPattern;
   private Pattern ctypePattern;
   private boolean printStrackTrace;


   public LogConstraint(String machine, String number, String builder, String ctype, boolean printStrackTrace) {
      machinePattern = (machine != null) ? Pattern.compile(machine) : null;
      numberPattern = (number != null) ? Pattern.compile(number) : null;
      builderPattern = (builder != null) ? Pattern.compile(builder) : null;
      ctypePattern = (ctype != null) ? Pattern.compile(ctype) : null;
      this.printStrackTrace = printStrackTrace;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof LogConstraint))
         return false;

      final LogConstraint logConstraint = (LogConstraint) o;

      if (printStrackTrace != logConstraint.printStrackTrace)
         return false;
      String builder = getBuilder();
      if (builder != null ? !builder.equals(logConstraint.getBuilder()) : logConstraint.getBuilder() != null)
         return false;
      String ctype = getCtype();
      if (ctype != null ? !ctype.equals(logConstraint.getCtype()) : logConstraint.getCtype() != null)
         return false;
      String machine = getMachine();
      if (machine != null ? !machine.equals(logConstraint.getMachine()) : logConstraint.getMachine() != null)
         return false;
      String number = getNumber();
      if (number != null ? !number.equals(logConstraint.getNumber()) : logConstraint.getNumber() != null)
         return false;

      return true;
   }


   @Override
   public int hashCode() {
      int result;
      String builder = getBuilder();
      String ctype = getCtype();
      String machine = getMachine();
      String number = getNumber();
      result = (machine != null ? machine.hashCode() : 0);
      result = 29 * result + (number != null ? number.hashCode() : 0);
      result = 29 * result + (builder != null ? builder.hashCode() : 0);
      result = 29 * result + (ctype != null ? ctype.hashCode() : 0);
      result = 29 * result + (printStrackTrace ? 1 : 0);
      return result;
   }


   public String getMachine() {
      return (machinePattern != null) ? machinePattern.pattern() : null;
   }


   public void setMachine(String machine) {
      machinePattern = (machine != null) ? Pattern.compile(machine) : null;
   }


   public String getNumber() {
      return (numberPattern != null) ? numberPattern.pattern() : null;
   }


   public void setNumber(String number) {
      numberPattern = (number != null) ? Pattern.compile(number) : null;
   }


   public String getBuilder() {
      return (builderPattern != null) ? builderPattern.pattern() : null;
   }


   public void setBuilder(String builder) {
      builderPattern = (builder != null) ? Pattern.compile(builder) : null;
   }


   public String getCtype() {
      return (ctypePattern != null) ? ctypePattern.pattern() : null;
   }


   public void setCtype(String ctype) {
      ctypePattern = (ctype != null) ? Pattern.compile(ctype) : null;
   }


   public boolean isPrintStrackTrace() {
      return printStrackTrace;
   }


   public void setPrintStrackTrace(boolean printStrackTrace) {
      this.printStrackTrace = printStrackTrace;
   }


   public boolean matches(String machine, String number, String builder, String ctype) {
      boolean result = false;

      result = (machinePattern == null || (machinePattern.matcher(machine).matches()));
      result &= (numberPattern == null || (numberPattern.matcher(number).matches()));
      result &= (builderPattern == null || (builderPattern.matcher(builder).matches()));
      result &= (ctypePattern == null || (ctypePattern.matcher(ctype).matches()));

      return result;
   }
}
