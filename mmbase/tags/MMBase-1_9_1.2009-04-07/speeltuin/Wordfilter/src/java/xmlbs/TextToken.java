/*
 * xmlbs
 *
 * Copyright (C) 2002  R.W. van 't Veer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

package xmlbs;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Token to represent and hold text blocks.  Entity refs are
 * preserved when possible and new are introduced for &lt;, &gt;
 * and &amp;.
 * <P><EM>TODO handle known entities only</EM></P>
 *
 * @see <A href="http://www.w3.org/TR/REC-xml#syntax">XML: Character Data and Markup</A>
 * @author R.W. van 't Veer
 * @version $Revision: 1.1 $
 */
public class TextToken implements Token {
    /** processed text */
    private String txt;
    /** unprocessed text */
    private String data;
    /** document structure this token lives in */
    private DocumentStructure ds = null;

    /**
     * @param data create text block token from given text
     */
    public TextToken(String data, DocumentStructure ds) {
        this.data = data;
        this.ds = ds;
        this.txt = fixText(data, ds);
    }

    /**
     * @return unprocessed text data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data text for this block
     */
    public void setData(String data) {
        this.data = data;
        this.txt = fixText(data, ds);
    }

    /**
     * @return true when block only contains whitespace
     */
    public boolean isWhiteSpace() {
        return data.trim().length() == 0;
    }

    /**
     * @return processed text data
     */
    public String toString() {
        return txt;
    }

    /** regular expression to recognize entity references */
    private static RE entityRefRe;
    /** regular expression to recognize character references */
    private static RE charRefRe;

    static {
        try {
            entityRefRe = new RE("^&([a-zA-Z_:][a-zA-Z0-9._:-]*);");
            charRefRe = new RE("^&#([0-9]+)|(x[0-9a-fA-F]+);");
        }
        catch (RESyntaxException ex) {
            throw new RuntimeException(ex.toString());
        }
    }

    /**
     * Xml escape text while preserving existing entities.
     * @param in text to process
     * @return processing result
     */
    public static final String fixText(String in, DocumentStructure ds) {
        StringBuffer out = new StringBuffer();
        // TODO speedup by using char array insteadof string
        for (int i = 0, l = in.length(); i < l; i++) {
            char c = in.charAt(i);
            switch (c) {
                case '<' :
                    out.append("&lt;");
                    break;
                case '>' :
                    out.append("&gt;");
                    break;
                case '"' :
                    out.append("&#034;");
                    break;
                case '\'' :
                    out.append("&#039;");
                    break;
                case '&' :
                    int j = in.indexOf(';', i);
                    if (j != -1) {
                        String s = in.substring(i);
                        if (entityRefRe.match(s)) {
                            String ent = entityRefRe.getParen(1);
                            if (ent != null) {
                                ent = ds.getEntityRef(ent);
                                if (ent != null) {
                                    out.append('&');
                                    out.append(ds.getEntityRef(ent));
                                    out.append(';');
                                    i = j;
                                }
                                else {
                                    out.append("&amp;");
                                }
                            }
                            else {
                                out.append("&amp;");
                            }
                        }
                        else if (charRefRe.match(s)) {
                            String ent1 = charRefRe.getParen(1);
                            String ent2 = charRefRe.getParen(2);
                            if (ent1 != null || ent2 != null) {
                                out.append("&#");
                                out.append(ent1 != null ? ent1 : ent2);
                                out.append(';');
                                i = j;
                            }
                            else {
                                out.append("&amp;");
                            }
                        }
                        else {
                            out.append("&amp;");
                        }
                    }
                    else {
                        // TODO try to match entity ref with missing semi-colon
                        out.append("&amp;");
                    }
                    break;
                default :
                    out.append(c);
            }
        }
        
        return out.toString();
    }
}
