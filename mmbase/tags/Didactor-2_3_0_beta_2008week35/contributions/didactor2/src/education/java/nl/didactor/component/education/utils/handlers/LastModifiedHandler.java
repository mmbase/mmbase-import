package nl.didactor.component.education.utils.handlers;

import java.io.*;
import java.net.*;
import java.util.*;

public class LastModifiedHandler
{
  public LastModifiedHandler()
  {
  }

  public static String getData(String URI_string)
 {
   try
   {
     Date dtAuto = new Date((new File(new URI(URI_string))).lastModified() );
     return dtAuto.toString();
   }
   catch(Exception ex1)
   {
     ex1.printStackTrace();
     return  "";
   }

 }

 public static String getDataType()
{
  return "java.lang.String";
}

public static String getStyle()
 {
   return "width:200px";
 }


}
