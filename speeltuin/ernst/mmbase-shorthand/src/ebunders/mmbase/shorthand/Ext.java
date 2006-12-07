package ebunders.mmbase.shorthand;

public class Ext{
    public String go(String input){
        System.out.println("hallo daar");
        return input;
    }
    
    public void mkdir(String dirName){
        java.io.File dir = new java.io.File(dirName);
        if(! dir.isDirectory()) dir.mkdirs();
    }
}
