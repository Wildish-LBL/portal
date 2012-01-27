package pl.psnc.dl.wf4ever.portal.utils;

/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Represents the concept of an RDF data serialization format. RDF formats are identified
 * by a {@link #getName() name} and can have one or more associated MIME types, zero or
 * more associated file extensions and can specify a (default) character encoding. Some
 * formats are able to encode context information while other are not; this is indicated
 * by the value of {@link #supportsContexts}.
 * 
 * @author Arjohn Kampman
 */
public class RDFFormat
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5372582127420247056L;

	/*-----------*
	 * Constants *
	 *-----------*/

	/**
	 * The RDF/XML file format.
	 */
	public static final RDFFormat RDFXML = new RDFFormat("RDF/XML", Arrays.asList("application/rdf+xml",
		"application/xml"), Charset.forName("UTF-8"), Arrays.asList("rdf", "rdfs", "owl", "xml"), true, false);

	/**
	 * The N-Triples file format.
	 */
	public static final RDFFormat NTRIPLES = new RDFFormat("N-Triples", "text/plain", Charset.forName("US-ASCII"),
			"nt", false, false);

	/**
	 * The Turtle file format.
	 */
	public static final RDFFormat TURTLE = new RDFFormat("Turtle", "application/x-turtle", Charset.forName("UTF-8"),
			"ttl", true, false);

	/**
	 * The N3/Notation3 file format.
	 */
	public static final RDFFormat N3 = new RDFFormat("N3", "text/rdf+n3", Charset.forName("UTF-8"), "n3", true, false);

	/**
	 * The TriX file format.
	 */
	public static final RDFFormat TRIX = new RDFFormat("TriX", "application/trix", Charset.forName("UTF-8"),
			Arrays.asList("xml", "trix"), false, true);

	/**
	 * The <a href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriG/Spec/">TriG</a> file
	 * format.
	 */
	public static final RDFFormat TRIG = new RDFFormat("TriG", "application/x-trig", Charset.forName("UTF-8"), "trig",
			true, true);

	public static final RDFFormat RDFA = new RDFFormat("RDFa", "application/xhtml+xml", Charset.forName("UTF-8"),
			"xhtml", false, false);

	/*-----------*
	 * Variables *
	 *-----------*/

	/**
	 * The file format's name.
	 */
	private String name;

	/**
	 * The file format's MIME types. The first item in the list is interpreted as the
	 * default MIME type for the format.
	 */
	private List<String> mimeTypes = new ArrayList<String>(1);

	/**
	 * The file format's (default) charset.
	 */
	private String charset;

	/**
	 * The file format's file extensions. The first item in the list is interpreted as the
	 * default file extension for the format.
	 */
	private List<String> fileExtensions = new ArrayList<String>(1);

	/**
	 * Flag indicating whether the RDFFormat can encode namespace information.
	 */
	private boolean supportsNamespaces = false;

	/**
	 * Flag indicating whether the RDFFormat can encode context information.
	 */
	private boolean supportsContexts = false;


	/*--------------*
	 * Constructors *
	 *--------------*/

	/**
	 * Creates a new RDFFormat object.
	 * 
	 * @param name
	 *            The name of the file format, e.g. "PLAIN TEXT".
	 * @param mimeTypes
	 *            The MIME type(s) of the file format, e.g. <tt>text/plain</tt> for
	 *            theplain text files. The first item in the list is interpreted as the
	 *            default MIME type for the format. The supplied list should contain at
	 *            least one MIME type.
	 * @param charset
	 *            The default character encoding of the file format. Specify <tt>null</tt>
	 *            if not applicable.
	 * @param fileExtensions
	 *            The file format's file extension(s), e.g. <tt>txt</tt> for plain text
	 *            files. The first item in the list is interpreted as the default file
	 *            extension for the format.
	 */
	public RDFFormat(String name, Collection<String> mimeTypes, Charset charset, Collection<String> fileExtensions,
			boolean supportsNamespaces, boolean supportsContexts)
	{
		assert name != null : "name must not be null";
		assert mimeTypes != null : "mimeTypes must not be null";
		assert !mimeTypes.isEmpty() : "mimeTypes must not be empty";
		assert fileExtensions != null : "fileExtensions must not be null";

		this.name = name;
		this.mimeTypes.addAll(mimeTypes);
		this.charset = charset.name();
		this.fileExtensions.addAll(fileExtensions);
		this.supportsNamespaces = supportsNamespaces;
		this.supportsContexts = supportsContexts;
	}


	/**
	 * Creates a new RDFFormat object.
	 * 
	 * @param name
	 *            The name of the RDF file format, e.g. "RDF/XML".
	 * @param mimeType
	 *            The MIME type of the RDF file format, e.g. <tt>application/rdf+xml</tt>
	 *            for the RDF/XML file format.
	 * @param charset
	 *            The default character encoding of the RDF file format. Specify
	 *            <tt>null</tt> if not applicable.
	 * @param fileExtension
	 *            The (default) file extension for the RDF file format, e.g. <tt>rdf</tt>
	 *            for RDF/XML files.
	 */
	public RDFFormat(String name, String mimeType, Charset charset, String fileExtension, boolean supportsNamespaces,
			boolean supportsContexts)
	{
		this(name, Arrays.asList(mimeType), charset, Arrays.asList(fileExtension), supportsNamespaces, supportsContexts);
	}


	/**
	 * Creates a new RDFFormat object.
	 * 
	 * @param name
	 *            The name of the RDF file format, e.g. "RDF/XML".
	 * @param mimeType
	 *            The MIME type of the RDF file format, e.g. <tt>application/rdf+xml</tt>
	 *            for the RDF/XML file format.
	 * @param charset
	 *            The default character encoding of the RDF file format. Specify
	 *            <tt>null</tt> if not applicable.
	 * @param fileExtensions
	 *            The RDF format's file extensions, e.g. <tt>rdf</tt> for RDF/XML files.
	 *            The first item in the list is interpreted as the default file extension
	 *            for the format.
	 */
	public RDFFormat(String name, String mimeType, Charset charset, Collection<String> fileExtensions,
			boolean supportsNamespaces, boolean supportsContexts)
	{
		this(name, Arrays.asList(mimeType), charset, fileExtensions, supportsNamespaces, supportsContexts);
	}


	public RDFFormat()
	{

	}


	/*---------*
	 * Methods *
	 *---------*/

	/**
	 * Gets the name of this file format.
	 * 
	 * @return A human-readable format name, e.g. "PLAIN TEXT".
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * Gets the default MIME type for this file format.
	 * 
	 * @return A MIME type string, e.g. "text/plain".
	 */
	public String getDefaultMIMEType()
	{
		return mimeTypes.get(0);
	}


	/**
	 * Checks if the specified MIME type matches the RDFFormat's default MIME type. The
	 * MIME types are compared ignoring upper/lower-case differences.
	 * 
	 * @param mimeType
	 *            The MIME type to compare to the RDFFormat's default MIME type.
	 * @return <tt>true</tt> if the specified MIME type matches the RDFFormat's default
	 *         MIME type.
	 */
	public boolean hasDefaultMIMEType(String mimeType)
	{
		return getDefaultMIMEType().equalsIgnoreCase(mimeType);
	}


	/**
	 * Gets the file format's MIME types.
	 * 
	 * @return An unmodifiable list of MIME type strings, e.g. "text/plain".
	 */
	public List<String> getMIMETypes()
	{
		return Collections.unmodifiableList(mimeTypes);
	}


	/**
	 * Checks if specified MIME type matches one of the RDFFormat's MIME types. The MIME
	 * types are compared ignoring upper/lower-case differences.
	 * 
	 * @param mimeType
	 *            The MIME type to compare to the RDFFormat's MIME types.
	 * @return <tt>true</tt> if the specified MIME type matches one of the RDFFormat's
	 *         MIME types.
	 */
	public boolean hasMIMEType(String mimeType)
	{
		for (String mt : this.mimeTypes) {
			if (mt.equalsIgnoreCase(mimeType)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Gets the default file name extension for this file format.
	 * 
	 * @return A file name extension (excluding the dot), e.g. "txt", or <tt>null</tt> if
	 *         there is no common file extension for the format.
	 */
	public String getDefaultFileExtension()
	{
		if (fileExtensions.isEmpty()) {
			return null;
		}
		else {
			return fileExtensions.get(0);
		}
	}


	/**
	 * Checks if the specified file name extension matches the RDFFormat's default file
	 * name extension. The file name extension MIME types are compared ignoring
	 * upper/lower-case differences.
	 * 
	 * @param extension
	 *            The file extension to compare to the RDFFormat's file extension.
	 * @return <tt>true</tt> if the file format has a default file name extension and if
	 *         it matches the specified extension, <tt>false</tt> otherwise.
	 */
	public boolean hasDefaultFileExtension(String extension)
	{
		String ext = getDefaultFileExtension();
		return ext != null && ext.equalsIgnoreCase(extension);
	}


	/**
	 * Gets the file format's file extensions.
	 * 
	 * @return An unmodifiable list of file extension strings, e.g. "txt".
	 */
	public List<String> getFileExtensions()
	{
		return Collections.unmodifiableList(fileExtensions);
	}


	/**
	 * Checks if the RDFFormat's file extension is equal to the specified file extension.
	 * The file extensions are compared ignoring upper/lower-case differences.
	 * 
	 * @param extension
	 *            The file extension to compare to the RDFFormat's file extension.
	 * @return <tt>true</tt> if the specified file extension is equal to the RDFFormat's
	 *         file extension.
	 */
	public boolean hasFileExtension(String extension)
	{
		for (String ext : fileExtensions) {
			if (ext.equalsIgnoreCase(extension)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Get the (default) charset for this file format.
	 * 
	 * @return the (default) charset for this file format, or null if this format does not
	 *         have a default charset.
	 */
	public Charset getCharset()
	{
		return Charset.forName(charset);
	}


	/**
	 * Checks if the RDFFormat has a (default) charset.
	 * 
	 * @return <tt>true</tt> if the RDFFormat has a (default) charset.
	 */
	public boolean hasCharset()
	{
		return charset != null;
	}


	/**
	 * Compares RDFFormat objects based on their {@link #getName() name}, ignoring case.
	 */
	@Override
	public boolean equals(Object other)
	{
		if (this == other) {
			return true;
		}

		if (other instanceof RDFFormat) {
			return name.equalsIgnoreCase(((RDFFormat) other).name);
		}

		return false;
	}


	@Override
	public int hashCode()
	{
		return name.toLowerCase(Locale.ENGLISH).hashCode();
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(64);

		sb.append(name);

		sb.append(" (mimeTypes=");
		for (int i = 0; i < mimeTypes.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(mimeTypes.get(i));
		}

		sb.append("; ext=");
		for (int i = 0; i < fileExtensions.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(fileExtensions.get(i));
		}

		sb.append(")");

		return sb.toString();
	}


	/*----------------*
	 * Static methods *
	 *----------------*/

	/**
	 * Tries to match the specified MIME type with the MIME types of the supplied file
	 * formats.
	 * 
	 * @param mimeType
	 *            A MIME type, e.g. "text/plain".
	 * @param RDFFormats
	 *            The file formats to match the MIME type against.
	 * @return A RDFFormat object if the MIME type was recognized, or <tt>null</tt>
	 *         otherwise.
	 * @see #matchMIMEType(String, Iterable, RDFFormat)
	 */
	public static <FF extends RDFFormat> FF matchMIMEType(String mimeType, Iterable<FF> RDFFormats)
	{
		return matchMIMEType(mimeType, RDFFormats, null);
	}


	/**
	 * Tries to match the specified MIME type with the MIME types of the supplied file
	 * formats. The supplied fallback format will be returned when no matching format was
	 * found.
	 * 
	 * @param mimeType
	 *            A MIME type, e.g. "text/plain".
	 * @param RDFFormats
	 *            The file formats to match the MIME type against.
	 * @param fallback
	 *            The file format to return if no matching format can be found.
	 * @return A RDFFormat that matches the MIME type, or the fallback format if the
	 *         extension was not recognized.
	 */
	public static <FF extends RDFFormat> FF matchMIMEType(String mimeType, Iterable<FF> RDFFormats, FF fallback)
	{
		// First try to match with the default MIME type
		for (FF RDFFormat : RDFFormats) {
			if (RDFFormat.hasDefaultMIMEType(mimeType)) {
				return RDFFormat;
			}
		}

		// Try alternative MIME types too
		for (FF RDFFormat : RDFFormats) {
			if (RDFFormat.hasMIMEType(mimeType)) {
				return RDFFormat;
			}
		}

		return fallback;
	}


	/**
	 * Tries to match the specified file name with the file extensions of the supplied
	 * file formats.
	 * 
	 * @param fileName
	 *            A file name.
	 * @param RDFFormats
	 *            The file formats to match the file name extension against.
	 * @return A RDFFormat that matches the file name extension, or <tt>null</tt>
	 *         otherwise.
	 * @see #matchFileName(String, Iterable, RDFFormat)
	 */
	public static <FF extends RDFFormat> FF matchFileName(String fileName, Iterable<FF> RDFFormats)
	{
		return matchFileName(fileName, RDFFormats, null);
	}


	/**
	 * Tries to match the specified file name with the file extensions of the supplied
	 * file formats. This method will try to match "extensions" recursively, allowing it
	 * to find the file type of e.g. compressed files (e.g. "example.rdf.gz"). The
	 * supplied fallback format will be returned when the file name extension was not
	 * recognized.
	 * 
	 * @param fileName
	 *            A file name.
	 * @param RDFFormats
	 *            The file formats to match the file name extension against.
	 * @param fallback
	 *            The file format to return if no matching format can be found.
	 * @return A RDFFormat that matches the file name extension, or the fallback format if
	 *         the extension was not recognized.
	 */
	public static <FF extends RDFFormat> FF matchFileName(String fileName, Iterable<FF> RDFFormats, FF fallback)
	{
		// Strip any directory info from the file name
		int lastPathSepIdx = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (lastPathSepIdx >= 0) {
			fileName = fileName.substring(lastPathSepIdx + 1);
		}

		int dotIdx;
		while ((dotIdx = fileName.lastIndexOf('.')) >= 0) {
			String ext = fileName.substring(dotIdx + 1);

			// First try to match with the default file extension of the formats
			for (FF RDFFormat : RDFFormats) {
				if (RDFFormat.hasDefaultFileExtension(ext)) {
					return RDFFormat;
				}
			}

			// Try alternative file extensions too
			for (FF RDFFormat : RDFFormats) {
				if (RDFFormat.hasFileExtension(ext)) {
					return RDFFormat;
				}
			}

			// No match, check if the file name has more "extensions" (e.g.
			// example.rdf.gz)
			fileName = fileName.substring(0, dotIdx);
		}

		return fallback;
	}

	/*------------------*
	 * Static variables *
	 *------------------*/

	/**
	 * List of known RDF file formats.
	 */
	// FIXME: remove/deprecate this list?
	private static List<RDFFormat> RDF_FORMATS = new ArrayList<RDFFormat>(8);

	/*--------------------*
	 * Static initializer *
	 *--------------------*/

	static {
		// FIXME: base available format on available parsers/writers?
		register(RDFXML);
		register(NTRIPLES);
		register(TURTLE);
		register(N3);
		register(TRIX);
		register(TRIG);
		register(RDFA);
	}


	/*----------------*
	 * Static methods *
	 *----------------*/

	/**
	 * Returns all known/registered RDF formats.
	 */
	public static Collection<RDFFormat> values()
	{
		return Collections.unmodifiableList(RDF_FORMATS);
	}


	/**
	 * Registers the specified RDF file format.
	 * 
	 * @param name
	 *            The name of the RDF file format, e.g. "RDF/XML".
	 * @param mimeType
	 *            The MIME type of the RDF file format, e.g. <tt>application/rdf+xml</tt>
	 *            for the RDF/XML file format.
	 * @param fileExt
	 *            The (default) file extension for the RDF file format, e.g. <tt>rdf</tt>
	 *            for RDF/XML files.
	 */
	public static RDFFormat register(String name, String mimeType, String fileExt, Charset charset)
	{
		RDFFormat rdfFormat = new RDFFormat(name, mimeType, charset, fileExt, false, false);
		register(rdfFormat);
		return rdfFormat;
	}


	/**
	 * Registers the specified RDF file format.
	 */
	public static void register(RDFFormat rdfFormat)
	{
		RDF_FORMATS.add(rdfFormat);
	}


	/**
	 * Tries to determine the appropriate RDF file format based on the a MIME type that
	 * describes the content type.
	 * 
	 * @param mimeType
	 *            A MIME type, e.g. "application/rdf+xml".
	 * @return An RDFFormat object if the MIME type was recognized, or <tt>null</tt>
	 *         otherwise.
	 * @see #forMIMEType(String,RDFFormat)
	 * @see #getMIMETypes()
	 */
	public static RDFFormat forMIMEType(String mimeType)
	{
		return forMIMEType(mimeType, null);
	}


	/**
	 * Tries to determine the appropriate RDF file format based on the a MIME type that
	 * describes the content type. The supplied fallback format will be returned when the
	 * MIME type was not recognized.
	 * 
	 * @param mimeType
	 *            A file name.
	 * @return An RDFFormat that matches the MIME type, or the fallback format if the
	 *         extension was not recognized.
	 * @see #forMIMEType(String)
	 * @see #getMIMETypes()
	 */
	public static RDFFormat forMIMEType(String mimeType, RDFFormat fallback)
	{
		return matchMIMEType(mimeType, RDF_FORMATS, fallback);
	}


	/**
	 * Tries to determine the appropriate RDF file format based on the extension of a file
	 * name.
	 * 
	 * @param fileName
	 *            A file name.
	 * @return An RDFFormat object if the file extension was recognized, or <tt>null</tt>
	 *         otherwise.
	 * @see #forFileName(String,RDFFormat)
	 * @see #getFileExtensions()
	 */
	public static RDFFormat forFileName(String fileName)
	{
		return forFileName(fileName, null);
	}


	/**
	 * Tries to determine the appropriate RDF file format based on the extension of a file
	 * name. The supplied fallback format will be returned when the file name extension
	 * was not recognized.
	 * 
	 * @param fileName
	 *            A file name.
	 * @return An RDFFormat that matches the file name extension, or the fallback format
	 *         if the extension was not recognized.
	 * @see #forFileName(String)
	 * @see #getFileExtensions()
	 */
	public static RDFFormat forFileName(String fileName, RDFFormat fallback)
	{
		return matchFileName(fileName, RDF_FORMATS, fallback);
	}


	/**
	 * Returns the RDF format whose name matches the specified name.
	 * 
	 * @param formatName
	 *            A format name.
	 * @return The RDF format whose name matches the specified name, or <tt>null</tt> if
	 *         there is no such format.
	 */
	public static RDFFormat valueOf(String formatName)
	{
		for (RDFFormat format : RDF_FORMATS) {
			if (format.getName().equalsIgnoreCase(formatName)) {
				return format;
			}
		}

		return null;
	}


	/*---------*
	 * Methods *
	 *---------*/

	/**
	 * @return the mimeTypes
	 */
	public List<String> getMimeTypes()
	{
		return mimeTypes;
	}


	/**
	 * @param mimeTypes
	 *            the mimeTypes to set
	 */
	public void setMimeTypes(List<String> mimeTypes)
	{
		this.mimeTypes = mimeTypes;
	}


	/**
	 * @return the supportsNamespaces
	 */
	public boolean isSupportsNamespaces()
	{
		return supportsNamespaces;
	}


	/**
	 * @param supportsNamespaces
	 *            the supportsNamespaces to set
	 */
	public void setSupportsNamespaces(boolean supportsNamespaces)
	{
		this.supportsNamespaces = supportsNamespaces;
	}


	/**
	 * @return the supportsContexts
	 */
	public boolean isSupportsContexts()
	{
		return supportsContexts;
	}


	/**
	 * @param supportsContexts
	 *            the supportsContexts to set
	 */
	public void setSupportsContexts(boolean supportsContexts)
	{
		this.supportsContexts = supportsContexts;
	}


	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @param charset
	 *            the charset to set
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset.name();
	}


	/**
	 * @param fileExtensions
	 *            the fileExtensions to set
	 */
	public void setFileExtensions(List<String> fileExtensions)
	{
		this.fileExtensions = fileExtensions;
	}
}