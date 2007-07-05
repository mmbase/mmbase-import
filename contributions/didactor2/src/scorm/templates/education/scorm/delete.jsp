<%
   fileStoreDir  = new File(directory, requestDeletePackageID);
   fileTempDir   = new File(directory, requestDeletePackageID + "_");
   filePlayerDir = new File(directory, requestDeletePackageID + "_player");

   try
   {
      Unpack.deleteFolderIncludeSubfolders(fileStoreDir.getAbsolutePath(), false);
      Unpack.deleteFolderIncludeSubfolders(fileStoreDir.getAbsolutePath(), true);

      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), true);

      Unpack.deleteFolderIncludeSubfolders(filePlayerDir.getAbsolutePath(), false);
      Unpack.deleteFolderIncludeSubfolders(filePlayerDir.getAbsolutePath(), true);
   }
   catch(Exception e)
   {//Internal server error
   }

%>

<mm:node number="<%= requestDeletePackageID %>">
   <mm:deletenode deleterelations="true"/>
</mm:node>
