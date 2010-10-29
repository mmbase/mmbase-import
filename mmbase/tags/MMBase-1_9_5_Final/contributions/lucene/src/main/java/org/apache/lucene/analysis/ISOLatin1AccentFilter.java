package org.apache.lucene.analysis;

/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A filter that replaces accented characters in the ISO Latin 1 character set by
 * their unaccented equivalent. The case will not be altered.
 * <p>
 * For instance, '&agrave;' will be replaced by 'a'.
 * <p>
 */
public class ISOLatin1AccentFilter extends TokenFilter {
    public ISOLatin1AccentFilter(TokenStream input) {
        super(input);
    }

    public final Token next() throws java.io.IOException {
        final Token t = input.next();
        if (t == null)
            return null;
        // Return a token with filtered characters.
        return new Token(removeAccents(t.termText()), t.startOffset(), t.endOffset(), t.type());
    }

    /**
     * To replace accented characters in a String by unaccented equivalents.
     */
    public final static String removeAccents(String input) {
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            switch (input.charAt(i)) {
            case '\u00C0' : // \`A
            case '\u00C1' : // \'A
            case '\u00C2' : // \^A
            case '\u00C3' : // \~A
            case '\u00C4' : // \"A
            case '\u00C5' : // \AA{}
                output.append("A");
                break;
            case '\u00C6' : // \AE{}
                output.append("AE");
                break;
            case '\u00C7' : // \c{C}
                output.append("C");
                break;
            case '\u00C8' : // \`E
            case '\u00C9' : // \'E
            case '\u00CA' : // \^E
            case '\u00CB' : // \"E
                output.append("E");
                break;
            case '\u00CC' : // \`I
            case '\u00CD' : // \'I
            case '\u00CE' : // \^I
            case '\u00CF' : // \"I
                output.append("I");
                break;
            case '\u00D0' : // \DH{}
                output.append("D");
                break;
            case '\u00D1' : // \~N
                output.append("N");
                break;
            case '\u00D2' : // \`O
            case '\u00D3' : // \'O
            case '\u00D4' : // \^O
            case '\u00D5' : // \~O
            case '\u00D6' : // \"O
            case '\u00D8' : // \O{}
                output.append("O");
                break;
            case '\u0152' : // OE
                output.append("OE");
                break;
            case '\u00DE' : // \TH{}
                output.append("TH");
                break;
            case '\u00D9' : // \`U
            case '\u00DA' : // \'U
            case '\u00DB' : // \^U
            case '\u00DC' : // \"U
                output.append("U");
                break;
            case '\u00DD' : // \'Y
            case '\u0178' : // Y
                output.append("Y");
                break;
            case '\u00E0' : // \`a
            case '\u00E1' : // \'a
            case '\u00E2' : // \^a
            case '\u00E3' : // \~a
            case '\u00E4' : // \"a
            case '\u00E5' : // \aa{}
                output.append("a");
                break;
            case '\u00E6' : // \ae{}
                output.append("ae");
                break;
            case '\u00E7' : // \c{c}
                output.append("c");
                break;
            case '\u00E8' : // \`e
            case '\u00E9' : // \'e
            case '\u00EA' : // \^e
            case '\u00EB' : // \"e
                output.append("e");
                break;
            case '\u00EC' : // \`\i{}
            case '\u00ED' : // \'\i{}
            case '\u00EE' : // \^\i{}
            case '\u00EF' : // \"\i{}
                output.append("i");
                break;
            case '\u00F0' : // \dh{}
                output.append("d");
                break;
            case '\u00F1' : // \~n
                output.append("n");
                break;
            case '\u00F2' : // \`o
            case '\u00F3' : // \'o
            case '\u00F4' : // \^o
            case '\u00F5' : // \~o
            case '\u00F6' : // \"o
            case '\u00F8' : // \o{}
                output.append("o");
                break;
            case '\u0153' : // oe
                output.append("oe");
                break;
            case '\u00DF' : // \ss{}
                output.append("ss");
                break;
            case '\u00FE' : // \th{}
                output.append("th");
                break;
            case '\u00F9' : // \`u
            case '\u00FA' : // \'u
            case '\u00FB' : // \^u
            case '\u00FC' : // \"u
                output.append("u");
                break;
            case '\u00FD' : // \'y
            case '\u00FF' : // \"y
                output.append("y");
                break;
            default :
                output.append(input.charAt(i));
                break;
            }
        }
        return output.toString();
    }
}
