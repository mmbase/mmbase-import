package nl.didactor.component.scorm.exceptions;

public class ImportMetaDataException extends Exception
{
   private Exception e;

   public ImportMetaDataException(Exception e)
   {
      this.e = e;
   }

   public String toString()
   {
      return "Import MetaData Exception: " + e.toString();
   }
}