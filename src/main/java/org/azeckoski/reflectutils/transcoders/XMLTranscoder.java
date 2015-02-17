/**
 * $Id: XMLTranscoder.java 106 2012-02-09 21:12:51Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/transcoders/XMLTranscoder.java $
 * XMLEncoder.java - entity-broker - Sep 15, 2008 6:36:42 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.transcoders;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.azeckoski.reflectutils.ArrayUtils;
import org.azeckoski.reflectutils.ConstructorUtils;
import org.azeckoski.reflectutils.DateUtils;
import org.azeckoski.reflectutils.FieldUtils;
import org.azeckoski.reflectutils.ReflectUtils;
import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Provides methods for encoding and decoding XML <br/>
 * Note that the XML parser always trashes the root node currently
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class XMLTranscoder implements Transcoder {

    private static final String ELEMENT = "element";

    public String getHandledFormat() {
        return "xml";
    }

    public String encode(Object object, String name, Map<String, Object> properties) {
    	return encode(object, name, properties, this.maxLevel);
    }
    
    public String encode(Object object, String name, Map<String, Object> properties, int maxDepth) {
        String encoded = "";
        if (object != null) {
            // only set the name if this is not null to preserve the "null" tag
            if (name == null || "".equals(name)) {
                name = DATA_KEY;
            }
        }
        encoded = XMLTranscoder.makeXML(object, name, properties, this.humanOutput, this.includeNulls, 
                this.includeClass, this.includeClassField, maxDepth, this.fixTags, this.encoders);
        return encoded;
    }

    public Map<String, Object> decode(String string) {
//        Object decode = new XMLparser(string).getObject();
//        if (decode instanceof Map) {
//            decoded = (Map<String, Object>) decode;
//        } else {
//            // if the result is not a map then simply put the result into a map
//            decoded = new ArrayOrderedMap<String, Object>();
//            decoded.put(Transcoder.DATA_KEY, decode);
//        }
        return new XMLparser(string).getMap();
    }

    /**
     * Default constructor:
     * See other constructors for options
     */
    public XMLTranscoder() {}

    private List<ObjectEncoder> encoders = null;    
    public void setEncoders(List<ObjectEncoder> encoders) {
        this.encoders = encoders;
    }
    public List<ObjectEncoder> getEncoders() {
        return encoders;
    }
    public void addEncoder(ObjectEncoder objectEncoder) {
        if (this.encoders == null) {
            this.encoders = new ArrayList<ObjectEncoder>();
        }
        this.encoders.add(objectEncoder);
    }

    private boolean humanOutput = false;
    private boolean includeNulls = true;
    private boolean includeClass = false;
    private boolean includeClassField = false;
    private boolean fixTags = true;

    /**
     * @param humanOutput if true then enable human readable output (includes indentation and line breaks)
     * @param includeNulls if true then create output tags for null values
     * @param includeClassField if true then include the value from the "getClass()" method as "class" when encoding beans and maps
     */
    public XMLTranscoder(boolean humanOutput, boolean includeNulls, boolean includeClassField) {
        this.humanOutput = humanOutput;
        this.includeNulls = includeNulls;
        this.includeClassField = includeClassField;
    }

    /**
     * @param humanOutput if true then enable human readable output (includes indentation and line breaks)
     * @param includeNulls if true then create output tags for null values
     * @param includeClassField if true then include the value from the "getClass()" method as "class" when encoding beans and maps
     * @param includeClass if true then add in class tips to the XML output
     */
    public XMLTranscoder(boolean humanOutput, boolean includeNulls, boolean includeClassField, boolean includeClass) {
        this.humanOutput = humanOutput;
        this.includeNulls = includeNulls;
        this.includeClassField = includeClassField;
        this.includeClass = includeClass;
    }

    private int maxLevel = 7;
    /**
     * @param maxLevel the number of objects to follow when traveling through the object,
     * 0 means only the fields in the initial object, default is 7
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * @param fixTags if true then fix up any invalid xml tag names, else just throw exception
     */
    public void setFixTags(boolean fixTags) {
        this.fixTags = fixTags;
    }

    public boolean isHumanOutput() {
        return humanOutput;
    }

    public void setHumanOutput(boolean humanOutput) {
        this.humanOutput = humanOutput;
    }

    public boolean isIncludeNulls() {
        return includeNulls;
    }

    public void setIncludeNulls(boolean includeNulls) {
        this.includeNulls = includeNulls;
    }

    public boolean isIncludeClass() {
        return includeClass;
    }

    public void setIncludeClass(boolean includeClass) {
        this.includeClass = includeClass;
    }

    public boolean isIncludeClassField() {
        return includeClassField;
    }

    public void setIncludeClassField(boolean includeClassField) {
        this.includeClassField = includeClassField;
    }

    public boolean isFixTags() {
        return fixTags;
    }

    public int getMaxLevel() {
        return maxLevel;
    }    



    // STATICS

    public static final char SPACE = ' ';
    public static final char AMP   = '&';
    /**
     * single quote (')
     */
    public static final char APOS  = '\'';
    public static final char BANG  = '!';
    public static final char EQ    = '=';
    public static final char GT    = '>';
    public static final char LT    = '<';
    public static final char QUEST = '?';
    public static final char QUOT  = '"';
    public static final char SLASH = '/';
    public static final char EOL   = '\n';


    /**
     * Convert an object into a well-formed, element-normal XML string.
     * @param object any object
     * @return the XML string version of the object
     */
    public static String makeXML(Object object) {
        return makeXML(object, null, null, false, true, false, false, 7, true, null);
    }

    /**
     * Convert an object into a well-formed, element-normal XML string.
     * @param object any object
     * @param tagName (optional) enclosing root tag
     * @param properties (optional) optional properties to add into the encoded data
     * @param humanOutput true of human readable output
     * @param includeNulls true to include null values when generating tags
     * @param maxLevel the maximum number of levels of objects to encode before stopping
     * @return the XML string version of the object
     */
    public static String makeXML(Object object, String tagName, Map<String, Object> properties, boolean humanOutput, boolean includeNulls, boolean includeClass, boolean includeClassField, int maxLevel, List<ObjectEncoder> encoders) {
        return toXML(object, tagName, 0, maxLevel, humanOutput, includeNulls, includeClass, includeClassField, true, properties, encoders);
    }

    /**
     * Convert an object into a well-formed, element-normal XML string.
     * @param object any object
     * @param tagName (optional) enclosing root tag
     * @param properties (optional) optional properties to add into the encoded data
     * @param humanOutput true of human readable output
     * @param includeNulls true to include null values when generating tags
     * @param maxLevel the maximum number of levels of objects to encode before stopping
     * @param fixTags fix up tag names (instead of throwing an exception)
     * @return the XML string version of the object
     */
    public static String makeXML(Object object, String tagName, Map<String, Object> properties, boolean humanOutput, boolean includeNulls, boolean includeClass, boolean includeClassField, int maxLevel, boolean fixTags, List<ObjectEncoder> encoders) {
        return toXML(object, tagName, 0, maxLevel, humanOutput, includeNulls, includeClass, includeClassField, fixTags, properties, encoders);
    }

    protected static String toXML(Object object, String tagName, int level, int maxLevel, boolean humanOutput, boolean includeNulls, boolean includeClass, boolean includeClassField, boolean fixTags, Map<String, Object> properties, List<ObjectEncoder> encoders) {
        StringBuilder sb = new StringBuilder();

        if (object == null) {
            if (includeNulls) {
                // nulls are empty tags always
                tagName = validate(tagName == null ? "null" : tagName, fixTags);
                makeLevelSpaces(sb, level, humanOutput);
                sb.append(LT);
                sb.append(tagName);
                sb.append(SLASH);
                sb.append(GT);
                makeEOL(sb, humanOutput);
            }
        } else {
            Class<?> type = ConstructorUtils.getWrapper(object.getClass());
            if ( ConstructorUtils.isClassSimple(type) ) {
                // Simple (String, Number, etc.)
                tagName = validate(tagName == null ? makeElementName(type) : tagName, fixTags);
                String value = "";
                makeLevelSpaces(sb, level, humanOutput);
                sb.append(LT);
                sb.append(tagName);
                if (Date.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)) {
                    // date
                    Date d = (Date) object;
                    value = d.getTime()+"";
                    sb.append(" type='date' date='");
                    sb.append( DateUtils.makeDateISO8601(d) );
                    sb.append(APOS);
                } else if (Number.class.isAssignableFrom(type)) {
                    // number
                    sb.append(" type='number'");
                    if (includeClass) {
                        makeClassName(sb, type);
                    }
                    value = object.toString();
                } else if (Boolean.class.isAssignableFrom(type)) {
                    // boolean
                    value = object.toString();
                    sb.append(" type='boolean'");
                } else {
                    value = escapeForXML( object.toString() );
                }
                sb.append(GT);
                sb.append(value);
                sb.append(LT);
                sb.append(SLASH);
                sb.append(tagName);
                sb.append(GT);
                makeEOL(sb, humanOutput);
            } else if ( ConstructorUtils.isClassArray(type) ) {
                // ARRAY
                tagName = validate(tagName == null ? "array" : tagName, fixTags);
                int length = ArrayUtils.size((Object[])object);
                Class<?> elementType = ArrayUtils.type((Object[])object);
                makeLevelSpaces(sb, level, humanOutput);
                sb.append(LT);
                sb.append(tagName);
                sb.append(" type='array' length='");
                sb.append(length);
                sb.append(APOS);
                if (includeClass) {
                    sb.append(" component='");
                    sb.append( ConstructorUtils.getTypeFromInnerCollection(elementType).getName() );
                    sb.append(APOS);
                }
                sb.append(GT);
                makeEOL(sb, humanOutput);
                for (int i = 0; i < length; ++i) {
                    sb.append( toXML(Array.get(object, i), makeElementName(elementType), level+1, maxLevel, humanOutput, includeNulls, includeClass, includeClassField, fixTags, properties, encoders) );
                }
                makeLevelSpaces(sb, level, humanOutput);
                sb.append(LT);
                sb.append(SLASH);
                sb.append(tagName);
                sb.append(GT);
                makeEOL(sb, humanOutput);
            } else if ( ConstructorUtils.isClassCollection(type) ) {
                // COLLECTION
                tagName = validate(tagName == null ? "collection" : tagName, fixTags);
                Collection<Object> collection = (Collection) object;
                makeLevelSpaces(sb, level, humanOutput);
                sb.append(LT);
                sb.append(tagName);
                sb.append(" type='collection' size='");
                sb.append(collection.size());
                sb.append(APOS);
                if (includeClass) {
                    makeClassName(sb, ConstructorUtils.getTypeFromInnerCollection(type));
                }
                sb.append(GT);
                makeEOL(sb, humanOutput);
                for (Object element : collection) {
                    Class<?> elementType = null;
                    if (element != null) {
                        elementType = element.getClass();
                    }
                    sb.append( toXML(element, makeElementName(elementType), level+1, maxLevel, humanOutput, includeNulls, includeClass, includeClassField, fixTags, properties, encoders) );
                }
                makeLevelSpaces(sb, level, humanOutput);
                sb.append(LT);
                sb.append(SLASH);
                sb.append(tagName);
                sb.append(GT);
                makeEOL(sb, humanOutput);
            } else {
                // must be a bean or map, make sure it is a map
                tagName = validate(tagName == null ? makeElementName(type) : tagName, fixTags);
                // special handling for certain object types
                String special = TranscoderUtils.handleObjectEncoding(object, encoders);
                if (special != null) {
                    if ("".equals(special)) {
                        // skip this one entirely
                    } else {
                        // just use the value in special to represent this
                        makeLevelSpaces(sb, level, humanOutput);
                        sb.append(LT);
                        sb.append(tagName);
                        sb.append(GT);
                        sb.append( escapeForXML(special) );
                        sb.append(LT);
                        sb.append(SLASH);
                        sb.append(tagName);
                        sb.append(GT);
                        makeEOL(sb, humanOutput);
                    }
                } else {
                    // normal handling
                    if (maxLevel <= level) {
                        // if the max level was reached then stop
                        sb.append(LT);
                        sb.append(tagName);
                        sb.append(GT);
                        sb.append( "MAX level reached (" );
                        sb.append( level );
                        sb.append( "):" );
                        sb.append( escapeForXML(object.toString()) );
                        sb.append(LT);
                        sb.append(SLASH);
                        sb.append(tagName);
                        sb.append(GT);
                        makeEOL(sb, humanOutput);
                    } else {
                        String xmlType = "bean";
                        Map<String, Object> map = null;
                        if (Map.class.isAssignableFrom(type)) {
                            xmlType = "map";
                            map = (Map<String, Object>) object;
                        } else {
                            // reflect over objects
                            map = ReflectUtils.getInstance().getObjectValues(object, FieldsFilter.SERIALIZABLE, false);
                        }
                        // add in the optional properties if it makes sense to do so
                        if (level == 0 && properties != null && ! properties.isEmpty()) {
                            map.putAll(properties);
                        }
                        makeLevelSpaces(sb, level, humanOutput);
                        sb.append(LT);
                        sb.append(tagName);
                        sb.append(" type='");
                        sb.append(xmlType);
                        sb.append(APOS);
                        sb.append(" size='");
                        sb.append(map.size());
                        sb.append(APOS);
                        if (includeClass) {
                            makeClassName(sb, ConstructorUtils.getTypeFromInnerCollection(type));
                        }
                        sb.append(GT);
                        makeEOL(sb, humanOutput);
                        for (Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey() != null) {
                                sb.append( toXML(entry.getValue(), entry.getKey().toString(), level+1, maxLevel, humanOutput, includeNulls, includeClass, includeClassField, fixTags, properties, encoders) );
                            }
                        }
                        makeLevelSpaces(sb, level, humanOutput);
                        sb.append(LT);
                        sb.append(SLASH);
                        sb.append(tagName);
                        sb.append(GT);
                        makeEOL(sb, humanOutput);
                    }
                }
            }
        }
        return sb.toString();
    }

    protected static String makeElementName(Class<?> type) {
        String name = ELEMENT;
        if (type != null) {
            if (Map.class.isAssignableFrom(type)) {
                // use the default "element"
            } else {
                String simpleName = type.getSimpleName().toLowerCase();
                // strip off the [] for arrays
                int index = simpleName.indexOf('[');
                if (index == 0) {
                    // weird to have [] at the beginning so just use default
                } else if (index > 0) {
                    name = simpleName.substring(0, index);
                } else {
                    // not array so just use the class name
                    // TODO maybe handle this prettier with by adding in "-" and stuff?
                    name = simpleName;
                }
            }
        }
        return name;
    }

    protected static void makeClassName(StringBuilder sb, Class<?> type) {
        if (type != null) {
            sb.append(" class='");
            sb.append( type.getName() );
            sb.append(APOS);
        }
    }

    protected static void makeEOL(StringBuilder sb, boolean includeEOL) {
        if (includeEOL) {
            sb.append(EOL);
        }
    }

    protected static final String SPACES = "  ";
    protected static void makeLevelSpaces(StringBuilder sb, int level, boolean includeEOL) {
        if (includeEOL) {
            for (int i = 0; i < level; i++) {
                sb.append(SPACES);
            }
        }
    }

    /**
     * Escape a string for XML encoding: replace special characters with XML escapes:
     * <pre>
     * &amp; <small>(ampersand)</small> is replaced by &amp;amp;
     * &lt; <small>(less than)</small> is replaced by &amp;lt;
     * &gt; <small>(greater than)</small> is replaced by &amp;gt;
     * &quot; <small>(double quote)</small> is replaced by &amp;quot;
     * </pre>
     * @param string The string to be escaped.
     * @return The escaped string.
     */
    public static String escapeForXML(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = string.length(); i < len; i++) {
            char c = string.charAt(i);
            switch (c) {
            case AMP:
                sb.append("&amp;");
                break;
            case LT:
                sb.append("&lt;");
                break;
            case GT:
                sb.append("&gt;");
                break;
            case QUOT:
                sb.append("&quot;");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Validates that a string is a valid tag or attribute name
     * i.e. it contains no spaces and is non-null/non-empty
     * Whitespace is not allowed in tagNames and attributes.
     * 
     * XML elements must follow these naming rules:
     *    Names can contain letters, numbers, and other characters
     *    Names cannot start with a number or punctuation character (and a few others)
     *    Names cannot start with the letters xml (or XML, or Xml, etc)
     *    Names cannot contain spaces
     * 
     * See http://www.w3.org/TR/REC-xml/#sec-common-syn
     * Names beginning with the string "xml", or with any string which would match (('X'|'x') ('M'|'m') ('L'|'l')), 
     *      are reserved for standardization in this or future versions of this specification.
     * 
     * @param string any string
     * @param correct if true then correct any errors found (if possible)
     * @return the valid string
     * @throws IllegalArgumentException if the string is invalid (and cannot be corrected)
     */
    public static String validate(String string, boolean correct) {
        if (string == null) {
            throw new IllegalArgumentException("string is NULL");
        }
        int i, length = string.length();
        if (length == 0) {
            throw new IllegalArgumentException("Empty string.");
        }
        StringBuilder sb = new StringBuilder();
        for (i = 0; i < length; i += 1) {
            char c = string.charAt(i);
            if (i==0) {
                // check for invalid start chars
                if (!Character.isLetter(c) && '_' != c && ':' != c) {
                    // XML names MUST start with a letter OR _ or :, anything else is invalid
                    // technically: ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
                    if (correct) {
                        sb.append('_');
                    } else {
                        throw new IllegalArgumentException("'" + string + "' starts with a non-letter ("+c+") which is also not an underscore (_) or colon (:).");
                    }
                    continue; // skip ahead
                } else if ('x' == c || 'X' == c) {
                    // XML names special case - cannot start with "xml" (in any case)
                    if (string.toLowerCase().startsWith("xml")) {
                        if (correct) {
                            sb.append('_');
                            i += 2; // skip chars
                        } else {
                            throw new IllegalArgumentException("'" + string + "' starts with 'xml' or 'XML'.");
                        }
                        continue; // skip ahead
                    }
                }
            }
            if (Character.isWhitespace(c)) {
                if (correct) {
                    sb.append('_');
                } else {
                    throw new IllegalArgumentException("'" + string + "' contains a whitespace character.");
                }
            } else if (!Character.isLetterOrDigit(c) && ':' != c && '-' != c && '.' != c && '_' != c) {
                // technically: ":" | [A-Z] | "_" | [a-z] | "-" | "." | [0-9] plus #xB7 | [#x0300-#x036F] | [#x203F-#x2040] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
                // old check - if ('=' == c || '\'' == c || '\"' == c || '>' == c || '<' == c || '&' == c)
                if (correct) {
                    sb.append('_');
                } else {
                    throw new IllegalArgumentException("'" + string + "' contains an illegal xml character ("+c+") such as (=,',\",>,<,&), valid chars are (A-Za-z0-9:._-).");
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }



    // DECODER

    protected SAXParserFactory parserFactory = null;
    protected SAXParser parser = null;
    protected SAXParser getParser() {
        if (parserFactory == null) {
            parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating(true);
            parserFactory.setNamespaceAware(true);
        }
        if (parser != null) {
            try {
                parser.reset();
            } catch (UnsupportedOperationException e) {
                // could not reset it so we have to make a new one
                parser = null;
            }
        }
        if (parser == null) {
            try {
                parser = parserFactory.newSAXParser();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException("Failed to get XML parser from factory: " + parserFactory, e);
            } catch (SAXException e) {
                throw new RuntimeException("Failed to get XML parser from factory: " + parserFactory, e);
            }
        }
        return parser;
    }

    /**
     * Use SAX to process the XML document
     */
    public class XMLparser extends DefaultHandler {
        private String xml = null;
        private Map<String, Object> map = null;
        public XMLparser(String xml) {
            if (xml == null || "".equals(xml)) {
                throw new IllegalArgumentException("xml cannot be null or empty");
            }
            this.xml = xml;
            this.map = new ArrayOrderedMap<String, Object>();
            containerStack.push( new Container(this.map) ); // init the holder stack (causes root node to be trashed)
            parseXML(xml);
        }

        /**
         * @return the map which contains the data parsed out of the xml string
         */
        public Map<String, Object> getMap() {
            return map;
        }

        protected void parseXML(String xml) {
            try {
                getParser().parse( new ByteArrayInputStream(xml.getBytes()), this );
            } catch (SAXException se) {
                throw new IllegalArgumentException("Failed to parse xml ("+xml+"): " + se.getMessage(), se);
            } catch (IOException ie) {
                throw new RuntimeException("Failed to convert XML string ("+xml+") into inputstream", ie);
            }
        }

        // handle the XML parsing

        /**
         * Adds the value to the container using the given key,
         * if the key already exists in the container then the container needs to be switched
         * over to a collection and its contents moved, then the stack needs to be updated,
         * and finally the parent container needs to have it's value replaced
         */
        protected void add(Container container, String key, Object value) {
            // first we need to make sure this container is on the stack
//            if (containerStack.peek() != container) {
//                containerStack.push( new Container(container.getContainer(), key, value) );
//            }
            // now do the add
            Class<?> type = container.getContainer().getClass();
            if ( ConstructorUtils.isClassMap(type)) {
                Map<String, Object> m = (Map)container.getContainer();
                if (m.containsKey(key)) {
                    // this should have been a collection so replace the map and move elements over to collection
                    Collection collection = (Collection) makeContainerObject(Types.COLLECTION);
                    for (Entry entry : m.entrySet()) {
                        collection.add( entry.getValue());
                    }
                    collection.add(value);
                    // now replace the container in the stack
                    int endPosition = containerStack.size()-1;
                    int containerPosition = endPosition;
                    if (container != containerStack.peek() && containerPosition != 0) {
                        containerPosition--;
                    }
                    Container current = containerStack.get(containerPosition);
                    current.replaceContainer(collection); // update container and replace the value in the parent object in the container
                    // finally we need to get the next thing in the stack to point back at the new parent
                    if (containerPosition < endPosition) {
                        // there is another container on the stack which needs to be replaced
                        containerStack.set(endPosition, new Container(collection, 1, value) );
                    }
                } else {
                    m.put(key, value);
                }
            } else if ( ConstructorUtils.isClassCollection(type)) {
                Collection collection = ((Collection)container.getContainer());
                collection.add(value);
                // make sure the parent index is correct
                if (container != containerStack.peek()) {
                    containerStack.peek().updateIndex(collection.size() - 1);
                }
            } else {
                // bean or something we hope
                try {
                    ReflectUtils.getInstance().setFieldValue(container.getContainer(), key, value);
                } catch (RuntimeException e) {
                    throw new RuntimeException("Unknown container type ("+type+") and could not set field on container: " + container, e);
                }
            }
        }

        private Stack<String> tagStack = new Stack<String>();
        private Stack<Container> containerStack = new Stack<Container>();

        private CharArrayWriter contents = new CharArrayWriter();
        private Types currentType = null;
        // this should be false when there are no contents to read
        private boolean currentContents = false;

        // Event Handlers
        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            contents.reset();
            tagStack.push(localName);
            if (tagStack.size() > containerStack.size() + 1) {
                // add a new container to the stack, use the types info from the parent
                Container lastContainer = containerStack.peek();
                Object newContainerObject = makeContainerObject(currentType);
                String parentName = ( tagStack.size() > 1 ? tagStack.get(tagStack.size()-2) : tagStack.peek() );
                containerStack.push( new Container(lastContainer.getContainer(), parentName, newContainerObject) );
                add(lastContainer, parentName, newContainerObject);
            }
            currentType = getDataType(attributes);
            currentContents = false;
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if (tagStack.size() > containerStack.size()) {
                // only add data when we are above a container
                Object val = null;
                if (currentContents) {
                    String content = unescapeXML(contents.toString().trim());
                    val = content;
                    if (Types.BOOLEAN.equals(currentType)) {
                        val = Boolean.valueOf(content);
                    } else if (Types.NUMBER.equals(currentType)) {
                        try {
                            val = number(content);
                        } catch (NumberFormatException e) {
                            val = content;
                        }
                    } else if (Types.DATE.equals(currentType)) {
                        try {
                            val = new Date(Long.valueOf(content));
                        } catch (NumberFormatException e) {
                            val = content;
                        }
                    }
                }
                // put the value into the current container
                add(containerStack.peek(), localName, val);
            }
            if (tagStack.isEmpty()) {
                throw new IllegalStateException("tag stack is out of sync, empty while still processing tags: " + localName);
            } else {
                tagStack.pop();
            }
            // now we need to remove the current container if we are done with it
            while (tagStack.size() < containerStack.size()) {
                if (containerStack.size() <= 1) break;
                containerStack.pop();
            }
            contents.reset();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            // get the text out of the element
            contents.write(ch, start, length);
            currentContents = true;
        }

        @Override
        public String toString() {
            return "parser: " + xml + " => " + map;
        }
    }

    public static String unescapeXML(String string) {
        if (string != null && string.length() > 0) {
            string = string.replace("&lt;","<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&").replace("&apos;","'");
        }
        return string;
    }

    /**
     * This will force a tag or attribute to be valid in XML by replacing the invalid chars with "_",
     * invalid chars are ' ' (space), =, ', ", >, <, &
     * @param string any string
     * @return a valid string
     */
    public static String convertInvalidChars(String string) {
        if (string != null && string.length() > 0) {
            string = string.replace(' ','_').replace('=','_').replace('"','_').replace('\'','_').replace('<','_').replace('>','_').replace('&','_');
        }
        return string;
    }

    private static enum Types {STRING,NUMBER,BOOLEAN,DATE,ARRAY,COLLECTION,MAP,BEAN}; 

    protected static Types getDataType(Attributes attributes) {
        Types elementType = Types.STRING;
        String value = attributes.getValue("", "type");
        if (value != null) {
            if ("boolean".equals(value)) {
                elementType = Types.BOOLEAN;
            } else if ("number".equals(value)) {
                elementType = Types.NUMBER;
            } else if ("date".equals(value)) {
                elementType = Types.DATE;
            } else if ("array".equals(value)) {
                elementType = Types.ARRAY;
            } else if ("collection".equals(value)) {
                elementType = Types.COLLECTION;
            } else if ("map".equals(value)) {
                elementType = Types.MAP;
            } else if ("bean".equals(value)) {
                elementType = Types.BEAN;
            }
        }
        return elementType;
    }

    protected static Class<?> getDataClass(Attributes attributes) {
        Class<?> type = String.class;
        String value = attributes.getValue("", "type");
        if (value != null) {
            if (value.startsWith("class ")) {
                value = value.substring(6);
            }
            // TODO handle the classes?
        }
        return type;
    }

    protected static Object makeContainerObject(Types type) {
        Object newContainer = null;
        if (Types.ARRAY.equals(type) 
                || Types.COLLECTION.equals(type)) {
            newContainer = new Vector<Object>();
        } else {
            // bean, map, unknown
            newContainer = new ArrayOrderedMap<String, Object>();
        }
        return newContainer;
    }

    /**
     * Converts a string into a number
     * @param s the string
     * @return the number
     * @throws NumberFormatException if the string is not a number
     */
    @SuppressWarnings("fallthrough")
    protected static Number number(String s) {
        int length = s.length();
        boolean isFloatingPoint = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
            case '.':
            case 'e':
            case 'E':
                isFloatingPoint = true;
            case '-':
            case '+':
                length--;
            }
        }

        // more friendly handling of numbers
        Number num = null;
        if (isFloatingPoint) {
            if (length < 10) {
                num = Float.valueOf(s);
            } else if (length < 17) {
                num = Double.valueOf(s);
            } else {
                num = new BigDecimal(s);
            }
        } else {
            if (length < 10) {
                num = Integer.valueOf(s);
            } else if (length < 19) {
                num = Long.valueOf(s);
            } else {
                num = new BigInteger(s);
            }
        }
        return num;
    }

    protected static class Container {
        private boolean root = false;
        public void setRoot(boolean root) {
            this.root = root;
        }
        public boolean isRoot() {
            return root;
        }
        public Object parent;
        public Object getParent() {
            return parent;
        }
        public String key;
        public String getKey() {
            return key;
        }
        public int index;
        public int getIndex() {
            return index;
        }
        public Object container;
        public Object getContainer() {
            return container;
        }
        public void updateIndex(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("invalid index: " + index);
            }
            this.index = index;
            key = null;
        }
        /**
         * Replace the container with a new one based on the parent and settings in this Container
         */
        public void replaceContainer(Object container) {
            if (container == null) {
                throw new IllegalArgumentException("No null params allowed");
            }
            if (key != null) {
                FieldUtils.getInstance().setFieldValue(parent, key, container);
            } else if (index >= 0) {
                FieldUtils.getInstance().setIndexedValue(parent, index, container);
            }
            // if not key or index then do nothing except replacing the value
            this.container = container;
        }
        /**
         * Use if parent is non-existent (i.e. this is the root)
         */
        public Container(Object container) {
            if (container == null) {
                throw new IllegalArgumentException("No null params allowed");
            }
            this.container = container;
            this.root = true;
        }
        /**
         * Use if parent is keyed
         */
        public Container(Object parent, String key, Object container) {
            if (parent == null || key == null || container == null) {
                throw new IllegalArgumentException("No null params allowed");
            }
            this.container = container;
            this.key = key;
            this.parent = parent;
        }
        /**
         * Use if parent is indexed
         */
        public Container(Object parent, int index, Object container) {
            if (parent == null || index < 0 || container == null) {
                throw new IllegalArgumentException("No null params or index < 0 allowed");
            }
            this.container = container;
            this.index = index;
            this.parent = parent;
        }
        @Override
        public String toString() {
            return "C:root="+root+":parent="+(parent==null?parent:parent.getClass().getSimpleName())+":key="+key+":index="+index+":container="+(container==null?container:container.getClass().getSimpleName());
        }
    }

}
