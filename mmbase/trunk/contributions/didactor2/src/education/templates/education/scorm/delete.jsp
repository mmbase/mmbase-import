<%
   fileStoreDir = new File(Unpack.fixPath(directory + File.separator + requestDeletePackageID));
   fileTempDir  = new File(Unpack.fixPath(directory + File.separator + requestDeletePackageID + "_"));

   try
   {
      Unpack.deleteFolderIncludeSubfolders(fileStoreDir.getAbsolutePath(), false);
      Unpack.deleteFolderIncludeSubfolders(fileStoreDir.getAbsolutePath(), true);

      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), false);
      Unpack.deleteFolderIncludeSubfolders(fileTempDir.getAbsolutePath(), true);
   }
   catch(Exception e)
   {//Internal server error
   }

%>

<mm:node number="<%= requestDeletePackageID %>">
   <mm:deletenode deleterelations="true"/>
</mm:node>