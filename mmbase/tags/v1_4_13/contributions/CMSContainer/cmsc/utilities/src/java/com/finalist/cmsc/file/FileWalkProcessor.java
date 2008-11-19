package com.finalist.cmsc.file;

import java.io.File;

public interface FileWalkProcessor {

   void processDirectory(File directory);


   void processFile(File file);
}
