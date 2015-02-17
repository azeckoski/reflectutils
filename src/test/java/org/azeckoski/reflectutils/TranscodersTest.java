/**
 * $Id: TranscodersTest.java 110 2013-05-03 13:30:13Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/TranscodersTest.java $
 * TranscodersTest.java - genericdao - Sep 16, 2008 11:20:27 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.azeckoski.reflectutils.classes.TestBaseOne;
import org.azeckoski.reflectutils.classes.TestBean;
import org.azeckoski.reflectutils.classes.TestCompound;
import org.azeckoski.reflectutils.classes.TestHibernateLikeBean;
import org.azeckoski.reflectutils.classes.TestNesting;
import org.azeckoski.reflectutils.classes.TestPea;
import org.azeckoski.reflectutils.classes.TestUltraNested;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;
import org.azeckoski.reflectutils.transcoders.HTMLTranscoder;
import org.azeckoski.reflectutils.transcoders.JSONTranscoder;
import org.azeckoski.reflectutils.transcoders.JSONTranscoder.JsonReader;
import org.azeckoski.reflectutils.transcoders.Transcoder;
import org.azeckoski.reflectutils.transcoders.XMLTranscoder;


/**
 * Testing the transcoders
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class TranscodersTest extends TestCase {

    // testing the internal encoder
    public void testJSONEncode() {
        Transcoder transcoder = new JSONTranscoder(true, true, false);
        String encoded = null;

        // test simple cases
        encoded = transcoder.encode(null, null, null);
        assertNotNull(encoded);
        assertEquals("null", encoded); // json

        encoded = transcoder.encode("AaronZ", null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AaronZ"));

        encoded = transcoder.encode(1234, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("1234"));

        encoded = transcoder.encode(true, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("true"));

        // test arrays
        TestBean[] array = new TestBean[0];
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertEquals("[]", encoded); //json

        array = new TestBean[] {new TestBean(888), new TestBean(777)};
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));

        // test collections
        List<TestPea> list = new ArrayList<TestPea>();
        encoded = transcoder.encode(list, null, null);
        assertNotNull(encoded);
        assertEquals("[]", encoded); //json

        list.add( new TestPea("AZ","AaronZ"));
        list.add( new TestPea("BZ","BeckyZ"));
        encoded = transcoder.encode(list, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BZ"));
        assertTrue(encoded.contains("BeckyZ"));

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("id", 123);
        m.put("thing", "AZ");
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));

        Map<String, Object> m2 = new ArrayOrderedMap<String, Object>();
        m2.put("name", "aaron");
        m2.put("num", 456);
        m2.put("array", new String[] {"A","B","C"});
        m.put("map", m2);
        m.put("date", new Date(1255129200000l));
        m.put("timestamp", new Timestamp(1255129200000l));
        //m.put("calendar", new GregorianCalendar(2009,9,10));

        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));

        m.put("az", new TestBean());
        m.put("bz", new TestPea());
        m.put("oList", list);
        m.put("oArray", array);
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        assertTrue(encoded.contains("az"));
        assertTrue(encoded.contains("bz"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BeckyZ"));
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));

        // test beans
        TestCompound tc = new TestCompound();
        encoded = transcoder.encode(tc, "az-root", null);
        assertNotNull(encoded);
//        assertTrue(encoded.contains("az-root"));
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(encoded.contains("myField"));
        assertTrue(encoded.contains("myString"));

        // test excluding nulls
        Transcoder transcoder2 = new JSONTranscoder(false, false, false);
        encoded = transcoder2.encode(tc, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(! encoded.contains("myField"));
        assertTrue(! encoded.contains("myString"));

        TestUltraNested tun = new TestUltraNested("aztitle", new TestNesting(999, "bztitle", new String[] {"ZZ","YY","XX"}) );
        encoded = transcoder.encode(tun, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("aztitle"));
        assertTrue(encoded.contains("testNesting"));
        assertTrue(encoded.contains("sMap"));
        assertTrue(encoded.contains("B2"));
        assertTrue(encoded.contains("TWO"));
        assertTrue(encoded.contains("testEntity"));
        assertTrue(encoded.contains("prefix"));
        assertTrue(encoded.contains("crud"));
        assertTrue(encoded.contains("sArray"));
        assertTrue(encoded.contains("entityId"));
        assertTrue(encoded.contains("sList"));
        assertTrue(encoded.contains("myArray"));
        assertTrue(encoded.contains("extra"));
        assertTrue(encoded.contains("testPea"));
        assertTrue(encoded.contains("testBean"));
        assertTrue(encoded.contains("999"));
        assertTrue(encoded.contains("bztitle"));
        assertTrue(encoded.contains("ZZ"));

    }

    public void testJSONEncodeInvalid() {
        JSONTranscoder transcoder = new JSONTranscoder();
        JsonReader reader = transcoder.new JsonReader();
        Map<?,?> map;
        Object read;

        // invalid (becomes null)
        read = reader.read("");
        assertNull(read);
        read = reader.read("       ");
        assertNull(read);
        read = reader.read(":");
        assertNull(read);
        read = reader.read("{");
        assertNull(read);
        read = reader.read("[");
        assertNull(read);
        read = reader.read("{\"nonterminating string}");
        assertNull(read);
        read = reader.read("{{{{");
        assertNull(read);

        // empty
        read = reader.read("{ }");
        assertNotNull(read);
        assertTrue(read instanceof Map);

        read = reader.read("{ \"key\" : \"string\" }");
        assertNotNull(read);
        assertTrue(read instanceof Map);
        map = (Map<?,?>) read;
        assertEquals(1, map.size());

        map = (Map<?,?>) reader.read("{ \"number\": 12345 }");
        assertEquals(12345, map.get("number"));

        map = (Map<?,?>) reader.read("{ \"number\": -1.212345 }");
        // I think this should be a double really according to the JSON spec
        assertEquals(-1.212345f, map.get("number"));

        map = (Map<?,?>) reader.read("{ \"object\": { \"key\": 5} }");
        assertEquals(5, ((Map<?,?>) map.get("object")).get("key"));

        map = (Map<?,?>) reader.read("{ \"key1\": 1, \"key2\": 2 }");
        assertEquals(1, map.get("key1"));
        assertEquals(2, map.get("key2"));

        List<?> list = (List<?>) reader.read("[ 0,1,2,3,4,5 ]");
        assertEquals(1, list.get(1));
        assertEquals(5, list.get(5));

        String rnd = generateString(new Random(),
                "abcdefghijklmnopqrstuvwxyz012344567789{}[]'\";;&<>\\!?",
                10000000);
        reader.read(rnd);
    }

    public static String generateString(Random rng, String characters,
            int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    public void testJSONEncodeProperties() {
        Transcoder transcoder = new JSONTranscoder(true, true, false);
        String encoded = null;

        Map<String, Object> properties = new ArrayOrderedMap<String, Object>();
        properties.put("prop1", 999999);
        properties.put("prop2", "Zeckoski");
        properties.put("prop3", new TestPea("AZ","azeckoski"));
        properties.put("prop4", null);

        // test simple cases (ignored)
        encoded = transcoder.encode(null, null, properties);
        assertNotNull(encoded);
        assertEquals("null", encoded); // json
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        encoded = transcoder.encode("AaronZ", null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AaronZ"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        encoded = transcoder.encode(1234, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("1234"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        encoded = transcoder.encode(true, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("true"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        // test arrays
        TestBean[] array = new TestBean[0];
        encoded = transcoder.encode(array, null, properties);
        assertNotNull(encoded);
        assertEquals("[]", encoded); //json
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        array = new TestBean[] {new TestBean(888), new TestBean(777)};
        encoded = transcoder.encode(array, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        // test collections
        List<TestPea> list = new ArrayList<TestPea>();
        encoded = transcoder.encode(list, null, properties);
        assertNotNull(encoded);
        assertEquals("[]", encoded); //json
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        list.add( new TestPea("AZ","AaronZ"));
        list.add( new TestPea("BZ","BeckyZ"));
        encoded = transcoder.encode(list, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BZ"));
        assertTrue(encoded.contains("BeckyZ"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("id", 123);
        m.put("thing", "AZ");
        encoded = transcoder.encode(m, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        Map<String, Object> m2 = new ArrayOrderedMap<String, Object>();
        m2.put("name", "aaron");
        m2.put("date", new Date());
        m2.put("num", 456);
        m2.put("array", new String[] {"A","B","C"});
        m.put("map", m2);

        encoded = transcoder.encode(m, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        m.put("az", new TestBean());
        m.put("bz", new TestPea());
        m.put("oList", list);
        m.put("oArray", array);
        encoded = transcoder.encode(m, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        assertTrue(encoded.contains("az"));
        assertTrue(encoded.contains("bz"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BeckyZ"));
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        // test beans
        TestCompound tc = new TestCompound();
        encoded = transcoder.encode(tc, "az-root", properties);
        assertNotNull(encoded);
//        assertTrue(encoded.contains("az-root"));
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(encoded.contains("myField"));
        assertTrue(encoded.contains("myString"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        // test excluding nulls
        Transcoder transcoder2 = new JSONTranscoder(false, false, false);
        encoded = transcoder2.encode(tc, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(! encoded.contains("myField"));
        assertTrue(! encoded.contains("myString"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        TestUltraNested tun = new TestUltraNested("aztitle", new TestNesting(999, "bztitle", new String[] {"ZZ","YY","XX"}) );
        encoded = transcoder.encode(tun, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("aztitle"));
        assertTrue(encoded.contains("testNesting"));
        assertTrue(encoded.contains("sMap"));
        assertTrue(encoded.contains("B2"));
        assertTrue(encoded.contains("TWO"));
        assertTrue(encoded.contains("testEntity"));
        assertTrue(encoded.contains("prefix"));
        assertTrue(encoded.contains("crud"));
        assertTrue(encoded.contains("sArray"));
        assertTrue(encoded.contains("entityId"));
        assertTrue(encoded.contains("sList"));
        assertTrue(encoded.contains("myArray"));
        assertTrue(encoded.contains("extra"));
        assertTrue(encoded.contains("testPea"));
        assertTrue(encoded.contains("testBean"));
        assertTrue(encoded.contains("999"));
        assertTrue(encoded.contains("bztitle"));
        assertTrue(encoded.contains("ZZ"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

    }

    // testing the internal decoder
    @SuppressWarnings("unchecked")
    public void testJSONDecode() {
        Transcoder transcoder = new JSONTranscoder();
        Map<String, Object> decoded = null;

        // TEST a few json test strings
        String json = "{\"id\":123,\"thing\":\"AZ\"}";
        decoded = transcoder.decode(json);
        assertNotNull(decoded);
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));

        json = "{\"id\":123,\"thing\":\"AZ\",\"map\":{\"name\":\"aaron\",\"date\":1221493247004,\"num\":456,\"array\":[\"A\",\"B\",\"C\"]}}";
        decoded = transcoder.decode(json);
        assertNotNull(decoded);
        assertEquals(3, decoded.size());
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));
        Map<String, Object> m2d = (Map<String, Object>) decoded.get("map");
        assertEquals(4, m2d.size());
        assertEquals("aaron", m2d.get("name"));
        assertEquals(456, m2d.get("num"));

        // TEST in and out conversion
        String encoded = null;

        // test simple cases
        encoded = transcoder.encode(null, null, null);
        assertNotNull(encoded);
        assertEquals("null", encoded);
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(null, decoded.get(Transcoder.DATA_KEY));

        encoded = transcoder.encode("AaronZ", null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AaronZ"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals("AaronZ", decoded.get(Transcoder.DATA_KEY));

        encoded = transcoder.encode(1234, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("1234"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(1234, decoded.get(Transcoder.DATA_KEY));

        encoded = transcoder.encode(true, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("true"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(true, decoded.get(Transcoder.DATA_KEY));

        // test arrays
        TestBean[] array = new TestBean[0];
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertEquals("[]", encoded); //json
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(0, ((List<?>)decoded.get(Transcoder.DATA_KEY)).size());

        array = new TestBean[] {new TestBean(888), new TestBean(777)};
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        List<Map<String, Object>> decodeArray = ((List<Map<String, Object>>)decoded.get(Transcoder.DATA_KEY));
        assertEquals(2, decodeArray.size());
        assertEquals(array[0].getMyInt(), decodeArray.get(0).get("myInt"));
        assertEquals(array[1].getMyString(), decodeArray.get(1).get("myString"));

        List<TestPea> list = new ArrayList<TestPea>();
        list.add( new TestPea("AZ","AaronZ"));
        list.add( new TestPea("BZ","BeckyZ"));

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("id", 123);
        m.put("thing", "AZ");
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(2, decoded.size());
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));

        Map<String, Object> m2 = new ArrayOrderedMap<String, Object>();
        m2.put("name", "aaron");
        m2.put("date", new Date());
        m2.put("num", 456);
        m2.put("array", new String[] {"A","B","C"});
        m.put("map", m2);

        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(3, decoded.size());
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));
        m2d = (Map<String, Object>) decoded.get("map");
        assertEquals(4, m2d.size());
        assertEquals("aaron", m2d.get("name"));
        assertEquals(456, m2d.get("num"));

        // test beans
        TestCompound tc = new TestCompound();
        encoded = transcoder.encode(tc, "az-root", null);
        assertNotNull(encoded);
//        assertTrue(encoded.contains("az-root"));
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(encoded.contains("myField"));
        assertTrue(encoded.contains("myString"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(4, decoded.size());
        assertEquals(5, decoded.get("fieldInt"));
        assertEquals(8, decoded.get("myInt"));
        assertEquals(null, decoded.get("myField"));
        assertEquals(null, decoded.get("myString"));

    }

    public void testXMLEncode() {
        Transcoder transcoder = new XMLTranscoder(false, true, false);
        String encoded = null;

        // test simple cases
        encoded = transcoder.encode(null, null, null);
        assertNotNull(encoded);
        assertTrue( encoded.startsWith("<null/>") ); // XML

        encoded = transcoder.encode("AaronZ", null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AaronZ"));

        encoded = transcoder.encode(1234, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("1234"));

        encoded = transcoder.encode(true, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("true"));

        // test arrays
        TestBean[] array = new TestBean[0];
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("type='array'")); // XML

        array = new TestBean[] {new TestBean(888), new TestBean(777)};
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));

        // test collections
        List<TestPea> list = new ArrayList<TestPea>();
        encoded = transcoder.encode(list, null, null);
        assertNotNull(encoded);
        //assertEquals("[]", encoded); //json

        list.add( new TestPea("AZ","AaronZ"));
        list.add( new TestPea("BZ","BeckyZ"));
        encoded = transcoder.encode(list, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BZ"));
        assertTrue(encoded.contains("BeckyZ"));

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("id", 123);
        m.put("thing", "AZ");
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));

        Map<String, Object> m2 = new ArrayOrderedMap<String, Object>();
        m2.put("name", "aaron");
        m2.put("date", new Date());
        m2.put("num", 456);
        m2.put("array", new String[] {"A","B","C"});
        m.put("map", m2);

        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));

        m.put("az", new TestBean());
        m.put("bz", new TestPea());
        m.put("oList", list);
        m.put("oArray", array);
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        assertTrue(encoded.contains("az"));
        assertTrue(encoded.contains("bz"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BeckyZ"));
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));

        // test beans
        TestCompound tc = new TestCompound();
        encoded = transcoder.encode(tc, "az-root", null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("az-root")); // XML
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(encoded.contains("myField"));
        assertTrue(encoded.contains("myString"));

        // test excluding nulls
        Transcoder transcoder2 = new XMLTranscoder(false, false, false);
        encoded = transcoder2.encode(tc, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(! encoded.contains("myField"));
        assertTrue(! encoded.contains("myString"));

        TestUltraNested tun = new TestUltraNested("aztitle", new TestNesting(999, "bztitle", new String[] {"ZZ","YY","XX"}) );
        encoded = transcoder.encode(tun, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("aztitle"));
        assertTrue(encoded.contains("testNesting"));
        assertTrue(encoded.contains("sMap"));
        assertTrue(encoded.contains("B2"));
        assertTrue(encoded.contains("TWO"));
        assertTrue(encoded.contains("testEntity"));
        assertTrue(encoded.contains("prefix"));
        assertTrue(encoded.contains("crud"));
        assertTrue(encoded.contains("sArray"));
        assertTrue(encoded.contains("entityId"));
        assertTrue(encoded.contains("sList"));
        assertTrue(encoded.contains("myArray"));
        assertTrue(encoded.contains("extra"));
        assertTrue(encoded.contains("testPea"));
        assertTrue(encoded.contains("testBean"));
        assertTrue(encoded.contains("999"));
        assertTrue(encoded.contains("bztitle"));
        assertTrue(encoded.contains("ZZ"));

        // test names with illegal chars in them
        /*
        XML elements must follow these naming rules:
            Names can contain letters, numbers, and other characters
            Names cannot start with a number or punctuation character (and some others)
            Names cannot start with the letters xml (or XML, or Xml, etc)
            Names cannot contain spaces

            See http://www.w3.org/TR/REC-xml/#sec-common-syn
            Names beginning with the string "xml", or with any string which would match (('X'|'x') ('M'|'m') ('L'|'l')), 
            are reserved for standardization in this or future versions of this specification.
        */
        Map<String, Object> m3 = new ArrayOrderedMap<String, Object>();
        m3.put("totally-valid_name.1", "VALUE");
        m3.put("valid1with.num-andSTUFF", "VALUE");
        m3.put("1numberStart", "VALUE");
        m3.put(".periodStart", "VALUE");
        m3.put("xAtStart", "VALUE");
        m3.put("xmlAtStart", "VALUE");
        m3.put("XMLUpAtStart", "VALUE");
        m3.put("has spaces in it", "VALUE");
        m3.put("no=or'or\"or<or>or&allowed", "VALUE");
        m3.put("#HashStart", "VALUE");
        m3.put("-DashStart", "VALUE");
        m3.put("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+-=<>,.:;\"'[]{}|\\~`01234567890", "VALUE");
        encoded = transcoder.encode(m3, "root", null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("totally-valid_name.1"));
        assertTrue(encoded.contains("valid1with.num-andSTUFF"));
        assertTrue(encoded.contains("_numberStart"));
        assertTrue(encoded.contains("_periodStart"));
        assertTrue(encoded.contains("xAtStart"));
        assertTrue(encoded.contains("_AtStart"));
        assertTrue(encoded.contains("_UpAtStart"));
        assertTrue(encoded.contains("has_spaces_in_it"));
        assertTrue(encoded.contains("no_or_or_or_or_or_allowed"));
        assertTrue(encoded.contains("_DashStart"));
        assertTrue(encoded.contains("_UpAtStart"));
        assertTrue(encoded.contains("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ____________-____.:___________01234567890"));

        // test with auto-correct off
        XMLTranscoder transcoder3 = new XMLTranscoder(false, true, false, false);
        transcoder3.setFixTags(false);
        
        Map<String, Object> m4 = new ArrayOrderedMap<String, Object>();
        m4.clear();
        m4.put("totally-valid_name.1", "VALUE");
        encoded = transcoder3.encode(m4, "root", null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("totally-valid_name.1"));

        try {
            m4.clear();
            m4.put("1numberStart", "VALUE");
            encoded = transcoder3.encode(m4, "root", null);
            fail("Should have caused exception");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
        try {
            m4.clear();
            m4.put("xmlAtStart", "VALUE");
            encoded = transcoder3.encode(m4, "root", null);
            fail("Should have caused exception");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
        try {
            m4.clear();
            m4.put("has spaces in it", "VALUE");
            encoded = transcoder3.encode(m4, "root", null);
            fail("Should have caused exception");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
        try {
            m4.clear();
            m4.put("no=or'or\"or<or>or&allowed", "VALUE");
            encoded = transcoder3.encode(m3, "root", null);
            fail("Should have caused exception");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testXMLEncodeSpecial() {
        Transcoder transcoder = new XMLTranscoder(false, true, false);
        String encoded = null;

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("A_1", 111);
        m.put("B 2", 222);
        m.put("C&3", 333);
        m.put("D\"4", 444);
        m.put("E'5", 555);
        m.put("F<6", 666);
        m.put("G>7", 777);
        m.put("H=8", 888);
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("111"));
        assertTrue(encoded.contains("222"));
        assertTrue(encoded.contains("333"));
        assertTrue(encoded.contains("444"));
        assertTrue(encoded.contains("555"));
        assertTrue(encoded.contains("666"));
        assertTrue(encoded.contains("777"));
        assertTrue(encoded.contains("888"));
    }

    public void testXMLEncodeProperties() {
        Transcoder transcoder = new XMLTranscoder(true, true, false);
        String encoded = null;

        Map<String, Object> properties = new ArrayOrderedMap<String, Object>();
        properties.put("prop1", 999999);
        properties.put("prop2", "Zeckoski");
        properties.put("prop3", new TestPea("AZ","azeckoski"));
        properties.put("prop4", null);

        // test simple cases
        encoded = transcoder.encode(null, null, properties);
        assertNotNull(encoded);
        assertTrue( encoded.startsWith("<null/>") ); // XML
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        encoded = transcoder.encode("AaronZ", null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AaronZ"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        encoded = transcoder.encode(1234, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("1234"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        encoded = transcoder.encode(true, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("true"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        // test arrays
        TestBean[] array = new TestBean[0];
        encoded = transcoder.encode(array, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("type='array'")); // XML
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        array = new TestBean[] {new TestBean(888), new TestBean(777)};
        encoded = transcoder.encode(array, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        // test collections
        List<TestPea> list = new ArrayList<TestPea>();
        encoded = transcoder.encode(list, null, properties);
        assertNotNull(encoded);
        //assertEquals("[]", encoded); //json
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        list.add( new TestPea("AZ","AaronZ"));
        list.add( new TestPea("BZ","BeckyZ"));
        encoded = transcoder.encode(list, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BZ"));
        assertTrue(encoded.contains("BeckyZ"));
        assertFalse(encoded.contains("999999"));
        assertFalse(encoded.contains("Zeckoski"));
        assertFalse(encoded.contains("azeckoski"));

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("id", 123);
        m.put("thing", "AZ");
        encoded = transcoder.encode(m, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        Map<String, Object> m2 = new ArrayOrderedMap<String, Object>();
        m2.put("name", "aaron");
        m2.put("date", new Date());
        m2.put("num", 456);
        m2.put("array", new String[] {"A","B","C"});
        m.put("map", m2);

        encoded = transcoder.encode(m, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        m.put("az", new TestBean());
        m.put("bz", new TestPea());
        m.put("oList", list);
        m.put("oArray", array);
        encoded = transcoder.encode(m, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        assertTrue(encoded.contains("az"));
        assertTrue(encoded.contains("bz"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BeckyZ"));
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        // test beans
        TestCompound tc = new TestCompound();
        encoded = transcoder.encode(tc, "az-root", properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("az-root")); // XML
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(encoded.contains("myField"));
        assertTrue(encoded.contains("myString"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        // test excluding nulls
        Transcoder transcoder2 = new XMLTranscoder(false, false, false);
        encoded = transcoder2.encode(tc, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(! encoded.contains("myField"));
        assertTrue(! encoded.contains("myString"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

        TestUltraNested tun = new TestUltraNested("aztitle", new TestNesting(999, "bztitle", new String[] {"ZZ","YY","XX"}) );
        encoded = transcoder.encode(tun, null, properties);
        assertNotNull(encoded);
        assertTrue(encoded.contains("aztitle"));
        assertTrue(encoded.contains("testNesting"));
        assertTrue(encoded.contains("sMap"));
        assertTrue(encoded.contains("B2"));
        assertTrue(encoded.contains("TWO"));
        assertTrue(encoded.contains("testEntity"));
        assertTrue(encoded.contains("prefix"));
        assertTrue(encoded.contains("crud"));
        assertTrue(encoded.contains("sArray"));
        assertTrue(encoded.contains("entityId"));
        assertTrue(encoded.contains("sList"));
        assertTrue(encoded.contains("myArray"));
        assertTrue(encoded.contains("extra"));
        assertTrue(encoded.contains("testPea"));
        assertTrue(encoded.contains("testBean"));
        assertTrue(encoded.contains("999"));
        assertTrue(encoded.contains("bztitle"));
        assertTrue(encoded.contains("ZZ"));
        assertTrue(encoded.contains("999999"));
        assertTrue(encoded.contains("Zeckoski"));
        assertTrue(encoded.contains("azeckoski"));

    }

    // testing the internal decoder
    @SuppressWarnings("unchecked")
    public void testXMLDecode() {
        Transcoder transcoder = new XMLTranscoder(true, true, false);
        Map<String, Object> decoded = null;

        // simple
        String xml = "<data><id type='number'>123</id><thing>AZ</thing></data>";
        decoded = transcoder.decode(xml);
        assertNotNull(decoded);
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));

        // complete
        xml = "<data type='map' size='3' class='org.azeckoski.reflectutils.map.ArrayOrderedMap'><id type='number' class='java.lang.Integer'>123</id><thing>AZ</thing><map type='map' size='4' class='org.azeckoski.reflectutils.map.ArrayOrderedMap'><name>aaron</name><date type='date' date='2008-09-17T14:47:18+01:00'>1221659238015</date><num type='number' class='java.lang.Integer'>456</num><array type='array' length='3' component='java.lang.String'><string>A</string><string>B</string><string>C</string></array></map></data>";
        decoded = transcoder.decode(xml);
        assertNotNull(decoded);
        assertEquals(3, decoded.size());
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));
        Map<String, Object> m2d = (Map<String, Object>) decoded.get("map");
        assertEquals(4, m2d.size());
        assertEquals("aaron", m2d.get("name"));
        assertEquals(456, m2d.get("num"));

        // TEST in and out conversion
        String encoded = null;

        // test simple cases
/*** TODO currently we always trash the root node(s)
        encoded = transcoder.encode(null, null);
        assertNotNull(encoded);
        assertTrue( encoded.startsWith("<null/>") ); // XML
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(null, decoded.get(Transcoder.DATA_KEY));

        encoded = transcoder.encode("AaronZ", null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AaronZ"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals("AaronZ", decoded.get(Transcoder.DATA_KEY));

        encoded = transcoder.encode(1234, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("1234"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(1234, decoded.get(Transcoder.DATA_KEY));

        encoded = transcoder.encode(true, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("true"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(true, decoded.get(Transcoder.DATA_KEY));

        // test arrays
        TestBean[] array = new TestBean[0];
        encoded = transcoder.encode(array, null);
        assertNotNull(encoded);
        //assertEquals("[]", encoded); //json
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(0, ((List)decoded.get(Transcoder.DATA_KEY)).size());

        array = new TestBean[] {new TestBean(888), new TestBean(777)};
        encoded = transcoder.encode(array, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        List<Map<String, Object>> decodeArray = ((List)decoded.get(Transcoder.DATA_KEY));
        assertEquals(2, decodeArray.size());
        assertEquals(array[0].getMyInt(), decodeArray.get(0).get("myInt"));
        assertEquals(array[1].getMyString(), decodeArray.get(1).get("myString"));

****/

        List<TestPea> list = new ArrayList<TestPea>();
        list.add( new TestPea("AZ","AaronZ"));
        list.add( new TestPea("BZ","BeckyZ"));

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("id", 123);
        m.put("thing", "AZ");
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(2, decoded.size());
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));

        Map<String, Object> m2 = new ArrayOrderedMap<String, Object>();
        m2.put("name", "aaron");
        m2.put("date", new Date());
        m2.put("num", 456);
        m2.put("array", new String[] {"A","B","C"});
        m.put("map", m2);

        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(3, decoded.size());
        assertEquals(123, decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));
        m2d = (Map<String, Object>) decoded.get("map");
        assertEquals(4, m2d.size());
        assertEquals("aaron", m2d.get("name"));
        assertEquals(456, m2d.get("num"));

        // test beans
        TestCompound tc = new TestCompound();
        encoded = transcoder.encode(tc, "az-root", null);
        assertNotNull(encoded);
//        assertTrue(encoded.contains("az-root"));
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(encoded.contains("myField"));
        assertTrue(encoded.contains("myString"));
        decoded = transcoder.decode(encoded);
        assertNotNull(decoded);
        assertEquals(4, decoded.size());
        assertEquals(5, decoded.get("fieldInt"));
        assertEquals(8, decoded.get("myInt"));
        assertEquals(null, decoded.get("myField"));
        assertEquals(null, decoded.get("myString"));
    }

    @SuppressWarnings("unchecked")
    public void testXMLDecodeWithoutTypes() {
        Transcoder transcoder = new XMLTranscoder(true, true, false);
        Map<String, Object> decoded = null;

        // simple
        String xml = "<data><id>123</id><thing>AZ</thing></data>";
        decoded = transcoder.decode(xml);
        assertNotNull(decoded);
        assertEquals("123", decoded.get("id"));
        assertEquals("AZ", decoded.get("thing"));

        // nested
        xml = "<data><people><person><name>aaron</name><email>az@vt.edu</email></person><person><name>becky</name><email>bz@vt.edu</email></person></people><country>USA</country></data>";
        decoded = transcoder.decode(xml);
        assertNotNull(decoded);
        assertEquals(2, decoded.size());
        assertEquals("USA", decoded.get("country"));
        List<Map<String,Object>> people = (List<Map<String, Object>>) decoded.get("people");
        assertEquals(2, people.size());
        assertEquals("aaron", people.get(0).get("name"));
        assertEquals("becky", people.get(1).get("name"));
        assertEquals("az@vt.edu", people.get(0).get("email"));
        assertEquals("bz@vt.edu", people.get(1).get("email"));

        xml = "<data><people><person><name>aaron</name><emails><email>az@vt.edu</email><email>aaron@vt.edu</email><email>azeckoski@gmail.com</email></emails></person><person><name>becky</name><emails><email>bz@vt.edu</email><email>becky@vt.edu</email></emails></person></people><country>USA</country></data>";
        decoded = transcoder.decode(xml);
        assertNotNull(decoded);
        assertEquals(2, decoded.size());
        assertEquals("USA", decoded.get("country"));
        people = (List<Map<String, Object>>) decoded.get("people");
        assertEquals(2, people.size());
        assertEquals("aaron", people.get(0).get("name"));
        assertEquals("becky", people.get(1).get("name"));
        List<String> emails = (List<String>) people.get(0).get("emails");
        assertEquals(3, emails.size());
        assertEquals("az@vt.edu", emails.get(0));
        assertEquals("aaron@vt.edu", emails.get(1));
        assertEquals("azeckoski@gmail.com", emails.get(2));
        emails = (List<String>) people.get(1).get("emails");
        assertEquals("bz@vt.edu", emails.get(0));
        assertEquals("becky@vt.edu", emails.get(1));

        xml = "<data><people><person><name>aaron</name><emails><email>az@vt.edu</email><email>aaron@vt.edu</email></emails><pets><pet><name>minerva</name><type>cat</type></pet><pet><name>rocky</name><type>rock</type></pet><pet><name>puter</name><type>laptop</type></pet></pets></person><person><name>becky</name><emails><email>bz@vt.edu</email><email>becky@vt.edu</email></emails><pets/></person></people><country>USA</country></data>";
        decoded = transcoder.decode(xml);
        assertNotNull(decoded);
        assertEquals(2, decoded.size());
        assertEquals("USA", decoded.get("country"));
        people = (List<Map<String, Object>>) decoded.get("people");
        assertEquals(2, people.size());
        assertEquals("aaron", people.get(0).get("name"));
        assertEquals("becky", people.get(1).get("name"));
        emails = (List<String>) people.get(0).get("emails");
        assertEquals("az@vt.edu", emails.get(0));
        assertEquals("aaron@vt.edu", emails.get(1));
        emails = (List<String>) people.get(1).get("emails");
        assertEquals("bz@vt.edu", emails.get(0));
        assertEquals("becky@vt.edu", emails.get(1));
        List<Map<String,Object>> pets = (List<Map<String, Object>>) people.get(0).get("pets");
        assertNotNull(pets);
        assertEquals(3, pets.size());
        assertEquals("minerva", pets.get(0).get("name"));
        assertEquals("cat", pets.get(0).get("type"));
        assertEquals("rocky", pets.get(1).get("name"));
        assertEquals("rock", pets.get(1).get("type"));
        assertEquals("puter", pets.get(2).get("name"));
        assertEquals("laptop", pets.get(2).get("type"));
        assertNull( people.get(1).get("pets") );
    }

    public void testHTMLEncode() {
        Transcoder transcoder = new HTMLTranscoder(true, true, false);
        String encoded = null;

        // test simple cases
        encoded = transcoder.encode(null, null, null);
        assertNotNull(encoded);
        assertTrue( encoded.contains("<i>NULL</i>") ); // HTML

        encoded = transcoder.encode("AaronZ", null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AaronZ"));

        encoded = transcoder.encode(1234, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("1234"));

        encoded = transcoder.encode(true, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("true"));

        // test arrays
        TestBean[] array = new TestBean[0];
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("type=array")); // HTML

        array = new TestBean[] {new TestBean(888), new TestBean(777)};
        encoded = transcoder.encode(array, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));

        // test collections
        List<TestPea> list = new ArrayList<TestPea>();
        encoded = transcoder.encode(list, null, null);
        assertNotNull(encoded);
        //assertEquals("[]", encoded); //json

        list.add( new TestPea("AZ","AaronZ"));
        list.add( new TestPea("BZ","BeckyZ"));
        encoded = transcoder.encode(list, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BZ"));
        assertTrue(encoded.contains("BeckyZ"));

        // test maps
        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("id", 123);
        m.put("thing", "AZ");
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));

        Map<String, Object> m2 = new ArrayOrderedMap<String, Object>();
        m2.put("name", "aaron");
        m2.put("date", new Date());
        m2.put("num", 456);
        m2.put("array", new String[] {"A","B","C"});
        m.put("map", m2);

        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));

        m.put("az", new TestBean());
        m.put("bz", new TestPea());
        m.put("oList", list);
        m.put("oArray", array);
        encoded = transcoder.encode(m, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("123"));
        assertTrue(encoded.contains("AZ"));
        assertTrue(encoded.contains("aaron"));
        assertTrue(encoded.contains("456"));
        assertTrue(encoded.contains("az"));
        assertTrue(encoded.contains("bz"));
        assertTrue(encoded.contains("AaronZ"));
        assertTrue(encoded.contains("BeckyZ"));
        assertTrue(encoded.contains("888"));
        assertTrue(encoded.contains("777"));

        // test beans
        TestCompound tc = new TestCompound();
        encoded = transcoder.encode(tc, "az-root", null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("az-root")); // XML
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(encoded.contains("myField"));
        assertTrue(encoded.contains("myString"));

        // test excluding nulls
        Transcoder transcoder2 = new HTMLTranscoder(false, false, false);
        encoded = transcoder2.encode(tc, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("fieldInt"));
        assertTrue(encoded.contains("5"));
        assertTrue(encoded.contains("myInt"));
        assertTrue(encoded.contains("8"));
        assertTrue(! encoded.contains("myField"));
        assertTrue(! encoded.contains("myString"));

        TestUltraNested tun = new TestUltraNested("aztitle", new TestNesting(999, "bztitle", new String[] {"ZZ","YY","XX"}) );
        encoded = transcoder.encode(tun, null, null);
        assertNotNull(encoded);
        assertTrue(encoded.contains("aztitle"));
        assertTrue(encoded.contains("testNesting"));
        assertTrue(encoded.contains("sMap"));
        assertTrue(encoded.contains("B2"));
        assertTrue(encoded.contains("TWO"));
        assertTrue(encoded.contains("testEntity"));
        assertTrue(encoded.contains("prefix"));
        assertTrue(encoded.contains("crud"));
        assertTrue(encoded.contains("sArray"));
        assertTrue(encoded.contains("entityId"));
        assertTrue(encoded.contains("sList"));
        assertTrue(encoded.contains("myArray"));
        assertTrue(encoded.contains("extra"));
        assertTrue(encoded.contains("testPea"));
        assertTrue(encoded.contains("testBean"));
        assertTrue(encoded.contains("999"));
        assertTrue(encoded.contains("bztitle"));
        assertTrue(encoded.contains("ZZ"));

    }

    public void testLoopStoppingJSON() {
        Transcoder transcoder = new JSONTranscoder(true, true, false);
        String encoded = null;

        TestBaseOne tbo = new TestBaseOne();
        encoded = transcoder.encode(tbo, null, null);
        int size = encoded.length();
        assertNotNull(encoded);
        assertTrue(size > 1000);
        assertTrue(size < 200000);
        
    }

    public void testLoopStoppingXML() {
        Transcoder transcoder = new XMLTranscoder(true, true, false);
        String encoded = null;

        TestBaseOne tbo = new TestBaseOne();
        encoded = transcoder.encode(tbo, null, null);
        int size = encoded.length();
        assertNotNull(encoded);
        assertTrue(size > 1000);
        assertTrue(size < 200000);
        
    }

    public void testCrazyClassesJSON() {
        TestHibernateLikeBean crazy = new TestHibernateLikeBean();

        Transcoder transcoder = new JSONTranscoder(true, true, false);
        String encoded = null;

        encoded = transcoder.encode(crazy, null, null);
        int size = encoded.length();
        assertNotNull(encoded);
        assertTrue(size > 100);
        assertTrue(size < 1000);
    }

    public void testCrazyClassesXML() {
        TestHibernateLikeBean crazy = new TestHibernateLikeBean();

        Transcoder transcoder = new XMLTranscoder(true, true, false);
        String encoded = null;

        encoded = transcoder.encode(crazy, null, null);
        int size = encoded.length();
        assertNotNull(encoded);
        assertTrue(size > 100);
        assertTrue(size < 1000);
    }

    public void testDateEncoding() {
        Transcoder transcoder = new JSONTranscoder(false, true, false);
        String encoded = null;

        Date d = new Date(1255129200000l);
        encoded = transcoder.encode(d, "date", null);
        assertNotNull(encoded);
        assertEquals("1255129200000", encoded);

        Timestamp t = new Timestamp(1255129200000l);
        encoded = transcoder.encode(t, "date", null);
        assertNotNull(encoded);
        assertEquals("1255129200000", encoded);

        /* sql date is not immutable
        java.sql.Date sd = new java.sql.Date(1255129200000l);
        encoded = transcoder.encode(sd, "date", null);
        assertNotNull(encoded);
        assertEquals("1255129200000", encoded);
        */

    }

}
