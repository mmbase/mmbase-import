package org.mmbase.util.transformers;
/**
 * {@link Tranformer}s define tranformations of Strings, byte arrays and streams.
 *
 *  Transformers are devided in three species. {@link CharTransformer}s which transform some String to
 * another String (or Reader to a Writer). Then there are transformation which take a byte array
 * (or an InputStream) and transform that to a String (or a Writer), these are called
 * {@link ByteToCharTransformer}s. And the last type are are the {@link ByteTranformer}s, which tranform a byte
 * array to another byte array (or an InputStream to an OutputStream).
 *
 * All Transformers can be specialized to {@link ConfigurableTransformer}s.
 *
 * Several Abstract implemenations are present too. E.g. the {@link ReaderTransformer} is nearly a
 * complete {@link CharTransformer}, only the function wich pipes a Reader to a Writer is
 * abstract. {@link StringTranformer} is a CharTransformer which' only abstract function is the one
 * taking a String argument.
 * @version $Id$
 */
