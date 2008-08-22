package nl.didactor.component.scorm.exceptions;

public class ImportMetaStandartsException extends Exception
{
   private Exception e;

   public ImportMetaStandartsException(Exception e)
   {
      this.e = e;
   }

   public String toString()
   {
      return "Import MetaStandarts Exception: " + e.toString();
   }
}