package nl.didactor.component.education.utils.handlers;

import java.io.*;
import java.net.*;

public class FileSizeHandler
{
  public FileSizeHandler()
  {
  }

  public static Long getData(String URI_string)
  {
    try
    {
      return new Long( (new File(new URI(URI_string))).length() );
    }
    catch(Exception ex1)
    {
      ex1.printStackTrace();
      return  new Long(0);
    }

  }


  public static String getDataType()
  {
    return "java.lang.Long";
  }

  public static String getStyle()
   {
     return "width:150px";
   }

}
