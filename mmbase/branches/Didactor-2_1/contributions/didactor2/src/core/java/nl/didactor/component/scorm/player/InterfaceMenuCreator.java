package nl.didactor.component.scorm.player;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface InterfaceMenuCreator
{
   public String[] parse(boolean useRelativePaths, String sPackageName, String sSubPath);
}
