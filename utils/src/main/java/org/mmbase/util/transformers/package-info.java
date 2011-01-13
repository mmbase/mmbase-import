/**
 * {@link org.mmbase.util.transformers.Transformer}s define tranformations of Strings, byte arrays and streams.
 *
 *  Transformers are devided in three species. {@link org.mmbase.util.transformers.CharTransformer}s which transform some String to
 * another String (or Reader to a Writer). Then there are transformation which take a byte array
 * (or an InputStream) and transform that to a String (or a Writer), these are called
 * {@link org.mmbase.util.transformers.ByteToCharTransformer}s. And the last type are are the {@link org.mmbase.util.transformers.ByteTranformer}s, which tranform a byte
 * array to another byte array (or an {@link java.io.InputStream} to an {@link java.io.OutputStream}).
 *
 * All Transformers can be specialized to {@link org.mmbase.util.transformers.ConfigurableTransformer}s.
 *
 * Several Abstract implemenations are present too. E.g. the {@link org.mmbase.util.transformers.ReaderTransformer} is nearly a
 * complete {@link org.mmbase.util.transformers.CharTransformer}, only the function wich pipes a Reader to a Writer is
 * abstract. {@link org.mmbase.util.transformers.StringTransformer} is a CharTransformer which' only abstract function is the one
 * taking a String argument.
 * @version $Id$
 */
package org.mmbase.util.transformers;