package nl.vpro.tagfileparser.parser.file;

import java.io.File;

import nl.vpro.tagfileparser.model.TagInfo;

/**
 * This interface represents a tagfile parser of some kind. different implementations
 * are necessary for different kind of tag files (.tag, .tagx). They will be mapped
 * to a file extension. To create a new type, extend {@link AbstractTagParser}.
 * @author Ernst Bunders
 *
 */
public interface TagParser {
	public TagInfo parseTag(File tagFile);
}
