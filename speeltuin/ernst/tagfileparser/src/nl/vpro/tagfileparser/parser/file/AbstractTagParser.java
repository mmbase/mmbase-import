package nl.vpro.tagfileparser.parser.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.*;
import org.apache.log4j.spi.LoggerFactory;

import nl.vpro.tagfileparser.model.TagInfo;
import nl.vpro.tagfileparser.parser.BasicTagParserException;
import nl.vpro.tagfileparser.parser.ElementParser;

/**
 * This abstract Tag parser will has all the plumbing for parsing tag files.
 * Concrete implementations should do the following:
 * <ul>
 * <li>Set a tag file extension type. Tag files are checked for this extension
 * before they are parsed.
 * <li>Set an include file extension type. includes like
 * <code><%@include file="somefile.[includeExtension]" %><code> will be resolved and included. If no include extension is set, no includes will be resolved
 * <li>Register any number of instances of the {@link ElementParser} type. they are used to parse the different elements of the tag file.
 * </ul>
 * @author Ernst Bunders
 *
 */
public class AbstractTagParser implements TagParser {
	private static final Log log = LogFactory.getLog(AbstractTagParser.class);

	private List<ElementParser> elementParsers = new ArrayList<ElementParser>();
	private String includeExtension = null;
	private String tagExtension = null;
	private Pattern includePattern;
	private Pattern includeNamePattern;
	private Matcher matcher, fileNameMatcher;

	protected final void addElementParser(ElementParser elementParser) {
		log.debug("adding element parser: " + elementParser);
		elementParsers.add(elementParser);
	}

	/**
	 * If the extension is prefixed with a dot it is removed.
	 * 
	 * @param extension
	 *            the extension of the type of fragment file this type of tag
	 *            wants to include. if not set, no includes will be made.
	 */
	protected final void setIncludeExtension(String extension) {
		this.includeExtension = extension;
		if (this.includeExtension.startsWith(".")) {
			this.includeExtension = this.includeExtension.substring(1);
		}
		includePattern = Pattern.compile("<%@\\s*include\\s*file\\s*=\\s*\"[\\w\\.-]+\\." + includeExtension
				+ "\"\\s*%>");
		includeNamePattern = Pattern.compile("file\\s*=\\s*\"([\\w\\.-]+)\"");
		log.debug(String.format("setting include pattern: '%s'", includePattern.pattern()));
	}

	protected final void setTagExtension(String tagExtension) {
		this.tagExtension = tagExtension;
	}

	/**
	 * @see nl.vpro.tagfileparser.parser.file.TagParser#parseTag(java.io.File)
	 * @throws BasicTagParserException
	 *             when something goes wrong reading the tag file, or a tagfile
	 *             with the wrong extension is fed into it.
	 */
	public final TagInfo parseTag(File tagFile) {
		TagInfo tagInfo;

		if (tagExtension != null && tagFile.getName().endsWith(tagExtension)) {
			log.info(String.format("Parsing tagfile '%s'", tagFile.getName()));
			tagInfo = new TagInfo(resolveTagName(tagFile));
			// first: create the file reader.
			List<String> tagFileLines = createTagFileLines(tagFile);
			log.debug(String.format("%s lines in tag file %s", tagFileLines.size(), tagFile.getName()));
			// let all the element parsers parse the tag file
			for (ElementParser elementParser : elementParsers) {
				log.debug(String.format("applying element parser '%s'", elementParser.toString()));
				elementParser.parse(tagFileLines.iterator(), tagInfo);
			}
		} else {
			throw new BasicTagParserException("Can not parse tagfile: '" + tagFile.getName()
					+ "', can only parse tag files with extension '" + tagExtension + "'");
		}
		return tagInfo;
	}

	/**
	 * Reads a tag file and creates a collection of lines. When in
	 * 
	 * @include statement is found for a tagfile fragment, it is resolved and
	 *          the lines of the included file are inserted instead. <b>Warning:</b>
	 *          Assumes there is only one include statement in a line. once it
	 *          is found, the code looks no further.
	 * @return
	 * @throws BasicTagParserException
	 *             when something goes wrong reading the tag file.
	 */
	private final List<String> createTagFileLines(File tagFile) {
		try {

			BufferedReader tagFileReader = new BufferedReader(new FileReader(tagFile));
			List<String> lines = new ArrayList<String>();

			String line = tagFileReader.readLine();
			while (line != null) {
				if (includeExtension != null) {
					matcher = includePattern.matcher(line);
					if (matcher.find()) {
						fileNameMatcher = includeNamePattern.matcher(line);
						fileNameMatcher.find();
						String includeName = fileNameMatcher.group(1);
						log.debug(String.format("** include found: '%S'", includeName));
						File include = resolveInclude(tagFile, includeName);
						if (include.isFile() && include.canRead()) {
							lines.addAll(createTagFileLines(include));
						} else {
							throw new BasicTagParserException("include  " + includeName
									+ ", could not be resolved fron tag " + tagFile.getAbsoluteFile());
						}
					} else {
						lines.add(line);
					}
				} else {
					lines.add(line);
				}
				line = tagFileReader.readLine();
			}
//			log.debug(String.format("%s lines found", lines.size()));
			return lines;
		} catch (FileNotFoundException e) {
			throw new BasicTagParserException("something went wrong trying to find file: " + tagFile, e);
		} catch (IOException e) {
			throw new BasicTagParserException("something went wrong trying to read file: " + tagFile, e);
		}
	}

	/**
	 * Try to resolve the include. First we try the include file name as an
	 * absolte path. If that dousn't work, we try it as a relative path from the
	 * parent folder of the tagfile.
	 * 
	 * @param tagFile
	 * @param includeName
	 * @return
	 * @throws BasicTagParserException
	 *             when the include name can not be resolved into a readable
	 *             file.
	 */
	private static final File resolveInclude(File tagFile, String includeName) {
		File include = new File(includeName);
		if (include.isFile()) {
			if (include.canRead()) {
				return include;
			} else {
				throw new BasicTagParserException("fragment '" + includeName + "' included by tag '"
						+ tagFile.getAbsoluteFile()
						+ "' could be resolved to a file (by an absolute path), but the file is not readable");
			}
		} else {
			// try the include as an absolute path
			include = new File(tagFile.getParent(), includeName);
			if (include.isFile()) {
				if (include.canRead()) {
					return include;
				} else {
					throw new BasicTagParserException("fragment '" + includeName + "' included by tag '"
							+ tagFile.getAbsoluteFile()
							+ "' could be resolved to a file (by a relative path), but the file is not readable");
				}
			} else {
				throw new BasicTagParserException("fragment '" + includeName + "' included by tag '"
						+ tagFile.getAbsoluteFile() + "' could not be resolved to a file.");
			}
		}
	}

	/**
	 * cut the path and the file type extension from the filename
	 * 
	 * @param tagFile
	 * @return
	 */
	private static final String resolveTagName(File tagFile) {
		String fileName = tagFile.getName();

		int i;
		// This should not be needed, as File.getName returns the name without
		// the path
		// if((i = fileName.indexOf("/")) != -1){
		// fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		// }

		if ((i = fileName.lastIndexOf(".")) != -1) {
			fileName = fileName.substring(0, i);
		}

		return fileName;
	}

}
