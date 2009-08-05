package ebunders.mmbase.shorthand;


import java.io.*;
import java.util.*;

public class Indenter {
    public static void main(String[] args) throws Exception {
        //System.out.println("indenting files in dir: "+args[0]);
        
        String dirname = args[0];
        File dir = new File(dirname);
        if(dir.exists() && dir.isDirectory()){
            //System.out.println(dirname+" is not a directory");
            String[] files = dir.list(new FilenameFilter(){
                public boolean accept(File dir, String name) {
                    return name.endsWith("xml");
                }
            });
            
            for (int i = 0; i < files.length; i++) {
                File xmlFile = new File(dir, files[i]);
                
                //System.out.println("processing file"+ xmlFile.getAbsolutePath());
                
                //first read the file
                StringBuffer contents = new StringBuffer();
                BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
                String line = reader.readLine();
                while(line != null){
                    contents.append(line);
                    contents.append("\n");
                    line = reader.readLine();
                }
                reader.close();
                
                //now write the file
                SimpleIndentWriter wr = new SimpleIndentWriter(xmlFile);
                
                char[] chars = contents.toString().toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    int c = (int) chars[j];
                    wr.write(c);
                }
                wr.close();
                
            }
        }else{
            //System.out.println("dir "+dir.getAbsolutePath()+" is not a valid directory");
        }
    }

}
