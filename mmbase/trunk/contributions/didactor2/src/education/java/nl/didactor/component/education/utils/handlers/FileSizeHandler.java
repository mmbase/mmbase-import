package nl.didactor.component.education.utils.handlers;

import java.io.*;
import java.net.*;

public class FileSizeHandler
{
  public FileSizeHandler()
  {
  }

  public static long fileSize(String URI_string)
  {
    try
    {
      return (long) (new File(new URI(URI_string))).length();
    }
    catch(Exception ex1)
    {
      ex1.printStackTrace();
      return 0;
    }

  }

  public static String getDataType()
  {
    return "INTEGER";
  }

}
