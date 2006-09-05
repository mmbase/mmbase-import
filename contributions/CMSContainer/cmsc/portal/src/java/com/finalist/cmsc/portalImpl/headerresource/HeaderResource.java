/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portalImpl.headerresource;

import java.util.*;

import net.sf.mmapps.commons.util.XmlUtil;

public class HeaderResource {
    
    private Set headerInfoSet = new LinkedHashSet();
    

    private Set getHeaderInfoSet() {
        return headerInfoSet;
    }
    
    public String toString() {
        return toString(true, true, true, true);
    }

    public String toString(boolean meta, boolean javascript, boolean style, boolean other) {

        Set headerInfoSet = getHeaderInfoSet();
        StringBuffer header = new StringBuffer();
        for (Iterator ite = headerInfoSet.iterator(); ite.hasNext();) {
            HeaderInfo info = (HeaderInfo) ite.next();
            String elementName = info.getElementName();
            if ("script".equalsIgnoreCase(elementName)) {
                if (javascript) {
                    header.append(info.toString());
                    header.append("\n");
                }
                continue;
            }
            if (("link".equalsIgnoreCase(elementName) ||
                "style".equalsIgnoreCase(elementName))) {
                if (style) {
                    header.append(info.toString());
                    header.append("\n");
                }
                continue;
            }
            if ("meta".equalsIgnoreCase(elementName)) {
                if (meta) {
                    header.append(info.toString());
                    header.append("\n");
                }
                continue;
            }
            if (other) {
                header.append(info.toString());
                header.append("\n");
            }
        }
        return header.toString();
    }

    public void addHeaderInfo(String elementName, Map attributes, String text) {
        HeaderInfo headerInfo = new HeaderInfo(elementName, attributes, text);
        if (!containsHeaderInfo(headerInfo)) {
            Set headerInfoSet = getHeaderInfoSet();
            headerInfoSet.add(headerInfo);
        }
    }

    private boolean containsHeaderInfo(HeaderInfo headerInfo) {
        Set headerInfoSet = getHeaderInfoSet();
        for (Iterator ite = headerInfoSet.iterator(); ite.hasNext();) {
            HeaderInfo hInfo = (HeaderInfo) ite.next();
            if (headerInfo.equals(hInfo)) { return true; }
        }
        return false;
    }

    public void addJavaScript(String path, boolean defer) {
        if (path != null) {
            HashMap attrs = new HashMap();
            attrs.put("src", path);
            attrs.put("type", "text/javascript");
            if (defer) {
                attrs.put("defer", "true");
            }
            addHeaderInfo("script", attrs, "");
        }
    }

    public void addJavaScript(String path) {
        addJavaScript(path, false);
    }

    public void addStyleSheet(String path) {
        if (path != null) {
            HashMap attrs = new HashMap();
            attrs.put("rel", "stylesheet");
            attrs.put("href", path);
            attrs.put("type", "text/css");
            addHeaderInfo("link", attrs, null);
        }
    }

    public void addMeta(String name, String value) {
        addMeta(name, value, null, null);
    }
    
    public void addMeta(String name, String value, String lang) {
        addMeta(name, value, lang, null);
    }
    
    public void addMeta(String name, String value, String lang, String header) {
        if (value != null) {
            HashMap attrs = new HashMap();
            attrs.put("name", name);
            attrs.put("content", value);
            if (lang != null) {
                attrs.put("lang", lang);    
            }
            if (header != null) {
                attrs.put("http-equiv", header);    
            }
            addHeaderInfo("meta", attrs, null);
        }
    }
    
    /**
     * This class represents tag information for HeaderResouce component
     */
    private class HeaderInfo {

        /**
         * Tag's name
         */
        private String elementName;

        /**
         * Tag's attributes
         */
        private Map attributes;

        /**
         * Tag's content
         */
        private String text;

        public HeaderInfo(String elementName) {
            this(elementName, new HashMap());
        }

        public HeaderInfo(String elementName, Map attr) {
            this(elementName, attr, null);
        }

        public HeaderInfo(String elementName, Map attr, String text) {
            setElementName(elementName);
            setAttributes(attr);
            setText(text);
        }

        public void addAttribute(String key, String value) {
            attributes.put(key, value);
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("<");
            buf.append(getElementName());
            buf.append(" ");

            Set keySet = getAttributes().keySet();
            for (Iterator ite = keySet.iterator(); ite.hasNext();) {
                String key = (String) ite.next();
                buf.append(key);
                buf.append("=\"");
                String value = XmlUtil.xmlEscape((String) getAttributes().get(key));
                buf.append(value);
                buf.append("\" ");
            }

            if (getText() != null) {
                buf.append(">" + getText() + "</" + getElementName() + ">");
            }
            else {
                buf.append("/>");
            }

            return buf.toString();
        }

        public boolean equals(Object o) {
            if (o instanceof HeaderInfo) {
                HeaderInfo headerInfo = (HeaderInfo) o;
                if (headerInfo.getElementName().equalsIgnoreCase(getElementName())
                        && compareString(headerInfo.getText(), getText())
                        && headerInfo.getAttributes().equals(getAttributes())) {
                    return true; 
                }
            }
            return false;
        }

        private boolean compareString(String str0, String str1) {
            if (str0 == null) {
                if (str1 == null) {
                    return true;
                }
            }
            else {
                if (str0.equals(str1)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @return Returns the attributes.
         */
        public Map getAttributes() {
            return attributes;
        }

        /**
         * @param attributes The attributes to set.
         */
        public void setAttributes(Map attributes) {
            this.attributes = attributes;
        }

        /**
         * @return Returns the elementName.
         */
        public String getElementName() {
            return elementName;
        }

        /**
         * @param elementName The elementName to set.
         */
        public void setElementName(String elementName) {
            this.elementName = elementName;
        }

        /**
         * @return Returns the text.
         */
        public String getText() {
            return text;
        }

        /**
         * @param text The text to set.
         */
        public void setText(String text) {
            this.text = text;
        }
    }

}
