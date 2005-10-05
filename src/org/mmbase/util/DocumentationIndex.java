/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;
import javax.servlet.ServletContext;

/**
 * Creates the index page for the documentation.
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id: DocumentationIndex.java,v 1.6 2005-10-05 10:02:55 michiel Exp $
 */
public class DocumentationIndex {

    /**
     * The header of the generated XHTML.
     */
    public static final String DOCUMENTATION_HEADER =
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
        "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>" +
        "<title>Documentation Overview</title>" +
        "<link href=\"./style/documentation.css\" rel=\"stylesheet\" type=\"text/css\" />" +
        "<link type=\"image/x-icon\" href=\"./style/favicon.ico\" rel=\"icon\" />" +
        "<link type=\"image/x-icon\" href=\"./style/favicon.ico\" rel=\"shortcut icon\" />" +
        "<meta content=\"text/html; charset=utf-8\" http-equiv=\"content-type\" />" +
        "</head><body>";

    /**
     * The title and license reference tekst of the generated XHTML.
     */
    public static final String DOCUMENTATION_TITLE =
        "<div class=\"article\" lang=\"en\">" +
        "<div class=\"titlepage\">" +
        "<div><h1 class=\"title\">Documentation Overview</h1></div>" +
        "<div class=\"legalnotice\">"+
        "<p xmlns=\"http://www.w3.org/1999/xhtml\">This software is OSI Certified Open Source Software. " +
        "OSI Certified is a certification mark of the Open Source Initiative.</p>" +
        "<p xmlns=\"http://www.w3.org/1999/xhtml\">The license (Mozilla version 1.0) can be read at the MMBase site. "+
        "See <a =\"http://www.mmbase.org/license\" target=\"_top\">http://www.mmbase.org/license</a>"+
        "</p></div></div><div /><hr /></div>";

    /**
     * The header of the introduction section.
     */
    public static final String DOCUMENTATION_INTRODUCTION = "Introduction";

    /**
     * The footer of the generated XHTML.
     */
    public static final String DOCUMENTATION_FOOTER =
        "<hr /><p>This is part of the <a href=\"http://www.mmbase.org\">MMBase</a> documentation.</p>" +
        "<p>For questions and remarks about this documentation mail to: " +
        "<a href=\"mailto:documentation@mmbase.org\">documentation@mmbase.org</a>" +
        "</p></body></html>";

    /**
     * If set, the documentationRoot should be a path to a resource relative to this context.
     */
    protected ServletContext servletContext = null;

    /**
     * The path to the documentation root directory. This can be a File object, or, if the class was
     * instantiated with a ServletContexct, a path to a resource in that context.
     */
    protected Object documentationRoot = null;

    /**
     * Contains documentation sections belonging to this inatance's documentationRoot.
     * The map contains name - value pairs, where name is the directory name, and value the (gui) title.
     * The first entry in this sorted map is the 'root' documentation.
     * @see #determineSections
     */
    protected static Map sections = null;

    /**
     * Instantiates a class for a specific documentation root.
     * @param documentationRoot the path to the documentation directory. This can be a File object, or,
     *      if the class was instantiated with a ServletContexct, a path to a resource in that context.
     */
    public DocumentationIndex(Object documentationRoot) throws IOException {
        this(null, documentationRoot);
    }

    /**
     * Instantiates a class for a specific documentation root.
     * @param servletContext the servletContext from the calling servlet/jsp, or <code>null</code>
     *        if documentationRoot refers a path in a file system.
     * @param documentationRoot the path to the documentation directory. This can be a File object, or,
     *      if the class was instantiated with a ServletContexct, a path to a resource in that context.
     */
    public DocumentationIndex(ServletContext servletContext, Object documentationRoot) throws IOException {
        this.documentationRoot = documentationRoot;
        this.servletContext = servletContext;
        determineSections();
    }

    /**
     * Retrieves a Reader to a resource.
     * @param resource The resource to get the Reader from
     * @return a <code>java.io.Reader</code> object
     * @throws IOException if the resource does not exist
     */
    protected Reader getReader(Object resource) throws IOException  {
        if (resource instanceof File) {
            return new FileReader((File)resource);
        } else {
            return new InputStreamReader(servletContext.getResourceAsStream(resource.toString()));
        }
    }

    /**
     * Determines whether a resource exists.
     * @param resource The resource to check
     * @return <code>true</code> if the resource exists.
     */
    protected boolean exists(Object resource) {
        if (resource instanceof File) {
            return ((File)resource).exists();
        } else {
            try {
                return servletContext.getResource(resource.toString()) != null;
            } catch (java.net.MalformedURLException mue) {
                return false;
            }
        }
    }

    /**
     * Determines whether a resource is a directory.
     * @param resource The resource to check
     * @return <code>true</code> if the resource is a directory.
     */
    private boolean isDirectory(Object resource) {
        if (resource instanceof File) {
            return ((File)resource).isDirectory();
        } else {
            return ((String)resource).endsWith("/");
        }
    }

    /**
     * Create a new resource that is a child (subdirectory or file) of this resource (assumed to be a directory).
     * @param resource The resource to check
     * @param child a subpath indicating teh child resource
     * @return a resource
     */
    protected Object getChild(Object resource, String child) {
        if (resource instanceof File) {
            return new File(resource + File.separator + child);
        } else {
            return resource + child;
        }
    }

    /**
     * Create a new resource that is a child directory (subdirectory or file) of this resource (assumed to be a directory).
     * @param resource The resource to check
     * @param child a subpath indicating teh child resource
     * @return a resource
     */
    protected Object getChildDir(Object resource, String child) {
        if (resource instanceof File) {
            return new File(resource + File.separator + child);
        } else {
            return resource + child + "/";
        }
    }

    /**
     * Returns a List of all resources under this resource.
     * If the resource is not a directory or does not contain other resources, the result is an empty list.
     * @param resource The resource to check
     * @return a List of resources
     */
    private List getList(Object resource) {
        List result = new ArrayList();
        if (resource instanceof File) {
            File dir = (File)resource;
            if (dir.isDirectory()) {
                result = Arrays.asList(dir.listFiles());
            }
        } else {
            Set paths = servletContext.getResourcePaths((String)resource);
            if (paths != null) {
                result = new ArrayList(paths);
            }
        }
        return result;
    }

    /**
     * Returns the last part of the path to the indicated resource (essentially the filename).
     * @param resource The resource to check
     * @return the past as a String
     */
    private String getLastOfPath(Object resource) {
        if (resource instanceof File) {
            return ((File)resource).getName();
        } else {
            String path = resource.toString();
            if (path.endsWith("/")) {
                path = path.substring(0, path.length()-1);
            }
            int pos = path.lastIndexOf('/', path.length()-1);
            if (pos>-1) {
                return path.substring(pos+1);
            } else {
                return path;
            }
        }
    }

    /**
     * Determines the title for a resource.
     * If the resource is a html file, the title is determined from the title tag of that file.
     * if the resource is a directory, the title is determined from the title tag of the index.html file
     * in that directory (if any).
     * Otherwise it returns <code>null</code>.
     * @param resource the resource to test
     * @return the title of the resource, or <code>null</code> is none can be found
     */
    protected String getTitle(Object resource) throws IOException {
        String name = null;
        String path = getLastOfPath(resource);
        if (path.endsWith(".txt")) {
            if (exists(resource)) {
                name = path;
                BufferedReader indexReader = new BufferedReader(getReader(resource));
                String line = indexReader.readLine();
                if (line!=null && line.startsWith("===")) {
                    line = indexReader.readLine();
                    if (line !=null) {
                        name = line.trim();
                    }
                }
                indexReader.close();
            }
        } else {
            if (!path.endsWith(".html")) { // assume a directory
                resource = getChild(resource, "index.html");
                path = getLastOfPath(resource);
            }
            if (exists(resource) && path.endsWith(".html")) {
                BufferedReader indexReader = new BufferedReader(getReader(resource));
                String line = indexReader.readLine();
                while (line !=null) {
                    String lineLC = line.toLowerCase();
                    int pos = lineLC.indexOf("<title>");
                    if (pos>=0) {
                        int pos2 = lineLC.indexOf("</title>",pos+7);
                        while (pos2==-1) {
                            String temp = indexReader.readLine();
                            if (temp == null) break;
                            line = line + temp;
                            lineLC = lineLC + temp.toLowerCase();
                            pos2 = lineLC.indexOf("</title>",pos+7);
                        }
                        if (pos2 > -1) {
                            name = line.substring(pos+7, pos2);
                        } else {
                            name = line.substring(pos+7);
                        }
                        line = null;
                    } else {
                        line = indexReader.readLine();
                    }
                }
                indexReader.close();
            }
        }
        return name;
    }

    /**
     * Determines which directories in the documentation needs to be included as a 'section' in the documentation,
     * and what titles they should have. To be included, a directory should contain an index.html file.
     * The title of that file is used as the title for that section.
     * Results are stored in  the {@link #sections} member.
     * Note that files in the 'root' documentation are contained in a separate 'introduction' section.
     * This section is added as the first entry in this sorted map.
     */
    protected void determineSections() throws IOException {
        sections = new TreeMap();
        sections.put("0ROOT",DOCUMENTATION_INTRODUCTION);
        List documentationDirectories = getList(documentationRoot);
        java.util.Collections.sort(documentationDirectories);
        for (Iterator i = documentationDirectories.iterator(); i.hasNext();) {
            Object section = i.next();
            if (isDirectory(section)) {
                String name = getTitle(section);
                if (name!=null) {
                    String sectionPath = getLastOfPath(section);
                    sections.put(sectionPath,name);
                }
            }
        }
    }

    /**
     * Adds a link to an alternate resource for a givenhtml file (i.e. a pdf or jsp version)
     * if such a file exists.
     * @param resource the orufuinbal resource
     * @param parentPath the path to use as a parent path when generating documentation links
     * @param fullPath the full path to the original resource
     * @param extension the alternate extension of the resource
     * @param label the label to use in the link
     * @param out the Writer to send output to
     */
    protected void addAdditionalPath(Object resource, String parentPath, String fullPath,
                String extension, String label, java.io.Writer out) throws IOException {
        // get path and check if there is a pdf version of the html file
        if (fullPath.endsWith(".html")) {
            Object alternateResource = fullPath.substring(0,fullPath.length()-5)+extension;
            if (resource instanceof File) {
                alternateResource = new File((String)alternateResource);
            }
            if (exists(alternateResource)) {
                String shortPath = getLastOfPath(alternateResource);
                if (parentPath != null) {
                    shortPath = parentPath + "/" + shortPath;
                }
                out.write(", <a href=\""+shortPath+"\" target=\"_top\">" + label + "</a>");
            }
        }
    }

    /**
     * Creates a bulleted list of documentation references for one section in the documentation index.
     * @param resourceList list of resources to output
     * @param parentPath the path to use as a parent path when generating documentation links
     * @param maxDepth maximum depth to which to recurse through this section
     * @param out the Writer to send output to
     */
    protected void listResources(List resourceList, String parentPath, int maxDepth, java.io.Writer out) throws IOException {
         if (resourceList.size() > 0) {
             out.write("<ul type=\"disc\">");
             java.util.Collections.sort(resourceList);
             for (Iterator i = resourceList.iterator(); i.hasNext();) {
                 Object resource = i.next();
                 if (maxDepth > 0 || !isDirectory(resource)) {
                     String name = getTitle(resource);
                     String path = getLastOfPath(resource);
                     if (name != null && !("index.html".equals(path))) {
                         if (parentPath != null) {
                             path = parentPath + "/" + path;
                         }
                         out.write("<li><p>"+name+" (");
                         if (path.endsWith(".txt")) { // text file
                             out.write("<a href=\""+path+"\" target=\"_top\">Text</a>");
                         } else if (path.endsWith(".html")) {  // html file
                             out.write("<a href=\""+path+"\" target=\"_top\">HTML</a>");
                             String fullPath = resource.toString();
                            addAdditionalPath(resource, parentPath, fullPath,".pdf", "PDF", out);
                         } else { // directory with index.html file
                             out.write("<a href=\"" + path+"/index.html\" target=\"_top\">HTML</a>");
                             String fullPath = getChild(resource, "index.html").toString();
                             addAdditionalPath(resource, path, fullPath,".pdf", "PDF", out);
                         }
                         out.write(")</p>");
                         if (maxDepth > 1) {
                             listResources(getList(resource), path, maxDepth-1, out);
                         }
                         out.write("</li>");
                     }
                 }
             }
             out.write("</ul>");
         }
    }

    /**
     * Creates a bulleted list of documentation references for all sections in the documentation index.
     * @param out the Writer to send output to
     */
   protected void createContents(java.io.Writer out) throws IOException {
        out.write("<div class=\"section\" lang=\"en\">");
        boolean first = true;
        for (Iterator i = sections.entrySet().iterator(); i.hasNext();) {
            Map.Entry section = (Map.Entry)i.next();
            String sectionPath = (String)section.getKey();
            String sectionTitle = (String)section.getValue();
            // the first entry is the root
            Object resourceDir;
            if (first) {
                resourceDir = documentationRoot;
            } else {
                resourceDir =getChildDir(documentationRoot, sectionPath);
            }
            if (exists(resourceDir)) {
                List resourceList = getList(resourceDir);
                if (resourceList.size() > 1) { // should contain more than just index.html
                    out.write("<div class=\"titlepage\"><h2 class=\"title\" style=\"clear: both\">");
                    out.write("<a id=\"" + sectionPath + "\" />"+sectionTitle);
                    out.write("</h2></div><div class=\"itemizedlist\">");
                    if (first) { // introduction: show only html files
                        listResources(resourceList, null, 0, out);
                    } else if (sectionPath.startsWith("javadoc")) {  // only show top directories in javadoc
                        listResources(resourceList, sectionPath, 1, out);
                    } else { // other documentation: show up to two directories deep
                        listResources(resourceList, sectionPath, 2, out);
                    }
                    out.write("</div>");
                }
            }
            first = false;
        }
        out.write("</div>");
    }

    /**
     * Creates a table of contents for the documentation index.
     * @param out the Writer to send output to
     */
    protected void createTableOfContents(java.io.Writer out) throws IOException {
        out.write("<div class=\"toc\"><p><b>Table of Contents</b><p><dl>");
        for (Iterator i = sections.entrySet().iterator(); i.hasNext();) {
            Map.Entry section = (Map.Entry)i.next();
            String sectionPath = (String)section.getKey();
            String sectionTitle = (String)section.getValue();
            out.write("<dt><span class=\"section\"><a href=\"#" + sectionPath + "\">" + sectionTitle+"</a></span></dt>");
        }
        out.write("</dl></div>");
    }

    /**
     * Creates a navigation section for the documentation index.
     * @param out the Writer to send output to
     */
    protected void createNavigation(java.io.Writer out) throws IOException {
        out.write("<div class=\"navigation\"><img alt=\"mmbase logo\" src=\"./style/logo.png\" />");
        out.write("<h2>Documentation Overview</h2>");
        for (Iterator i = sections.entrySet().iterator(); i.hasNext();) {
            Map.Entry section = (Map.Entry)i.next();
            String sectionPath = (String)section.getKey();
            String sectionTitle = (String)section.getValue();
            out.write("<p><a href=\"#" + sectionPath + "\">" + sectionTitle+"</a></p>");
        }
        out.write("<hr /><p><a href=\"http://www.mmbase.org\">MMBase</a></p>");
        out.write("<p><a href=\"./index.html\">Home</a></p></div>");
    }

    /**
     * Creates a documentation index (in XHTML text) and sends the result to the provided writer.
     * @param out the Writer to send output to
     */
    public void createIndexPage(java.io.Writer out) throws IOException {
        out.write(DOCUMENTATION_HEADER);
        createNavigation(out);
        out.write(DOCUMENTATION_TITLE);
        createTableOfContents(out);
        createContents(out);
        out.write(DOCUMENTATION_FOOTER);
        out.close();
    }

    /**
     * Creates a documentation index (in XHTML text) and sends the result to System.out.
     */
    public void createIndexPage() throws IOException  {
        createIndexPage(new BufferedWriter(new PrintWriter(System.out)));
    }

    /**
     * Main method, to be called from Ant.
     * Creates the documentation index based on the passed argument (file directory), and sends the
     * resulting XHTML text to System.out.
     * @param args The first comamnd line argument should be the path to the documentation directory
     */
    public static void main(String[] args) throws java.io.UnsupportedEncodingException, IOException {
        if (args.length != 0) {
            String docDir = args[0];
            DocumentationIndex index = new DocumentationIndex(new File(docDir));
            index.createIndexPage();
        } else {
            System.out.println("usage: java DocumentationIndex <basedocumentationdir>");
        }
    }
}
