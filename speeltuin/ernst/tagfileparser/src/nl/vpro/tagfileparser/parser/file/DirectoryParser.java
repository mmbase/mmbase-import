package nl.vpro.tagfileparser.parser.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import nl.vpro.tagfileparser.model.NameSpace;
import nl.vpro.tagfileparser.util.IllegalPathException;

/**
 * This class takes a path to a directory as input, and starts building a
 * structure of NameSpace and TagInfo objects from it.
 * 
 * @author Ernst Bunders
 * 
 */
public class DirectoryParser {
	private NameSpace rootNameSpace;
	private Map<String, TagParser> registeredParsers = new HashMap<String, TagParser>();

	/**
	 * @param rootPath
	 *            a valid path to a directory that contains tag files and
	 *            directories.
	 * @throws IllegalPathException
	 */
	public void parse(File tagDir, NameSpace nameSpace) {
		if (!tagDir.exists()) {
			throw new IllegalPathException("The given path dous not exist on the file system.");
		}
		if (!tagDir.isDirectory()) {
			throw new IllegalPathException("The given path dous not point to a directory.");
		}

		// create the root namespace
		if (nameSpace == null) {
			nameSpace = new NameSpace("/");
			rootNameSpace = nameSpace;
		} else {
			nameSpace = nameSpace.addChildNamespace(new NameSpace(tagDir.getName()));
		}

		// iterate over all the tag file types we have handlers for
		for (String extension : registeredParsers.keySet()) {
			final String thisExstension = extension;

			// iterate over all the tag files in this directory with the current
			// extension and parse them
			File[] files = tagDir.listFiles(new FilenameFilter() {
				public boolean accept(File file, String name) {
					if (file.getName().endsWith("." + thisExstension)) {
						return true;
					}
					return false;
				}
			});

			for (File tagFile : files) {
				nameSpace.addTagInfo(registeredParsers.get(extension).parseTag(tagFile));
			}
		}

		// iterate over all the directories in this directory, and parse them
		File[] dirs = tagDir.listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		
		for(File dir : dirs){
			parse(dir, nameSpace);
		}
	}
	
	public NameSpace getRootNameSpace(){
		return rootNameSpace;
	}

}
