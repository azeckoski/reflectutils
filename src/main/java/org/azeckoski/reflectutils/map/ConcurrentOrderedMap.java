/**
 * $Id: ConcurrentOrderedMap.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/map/ConcurrentOrderedMap.java $
 * ConcurrentOrderedMap.java - genericdao - May 5, 2008 2:16:35 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2
 * 
 * A copy of the Apache License, Version 2 has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.map;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A map which keeps track of the order the entries are added 
 * and allows retrieval of entries in the order they were entered as well,
 * this is safe for multi-threaded access,
 * this is backed by a {@link ConcurrentHashMap} and {@link Vector}
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ConcurrentOrderedMap<K, V> extends ConcurrentHashMap<K, V> implements OrderedMap<K, V> {
    public static final long serialVersionUID = 1l;

    private Vector<K> list = new Vector<K>(5,5);

    public ConcurrentOrderedMap() {
        this(10);
    }

    public ConcurrentOrderedMap(int initialCapacity) {
        super(initialCapacity);
        list = new Vector<K>(initialCapacity);
    }

    public ConcurrentOrderedMap(Map<K, V> map) {
        this(map.size());
        for (Entry<K, V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    private String name = "entity";
    public String getName() {
        return name;
    }
    /**
     * @param name the name to use when encoding this map of entities
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return a list of all the keys in this map in the order they were entered
     */
    public List<K> getKeys() {
        return new ArrayList<K>(list);
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.map.OrderedMap#getValues()
     */
    public List<V> getValues() {
        return new ArrayList<V>( values() );
    }

    /**
     * @return a list of all the entries in this map in the order they were created
     */
    public List<Entry<K, V>> getEntries() {
        ArrayList<Entry<K, V>> entries = new ArrayList<Entry<K,V>>();
        for (K key : list) {
            Entry<K, V> entry = new SimpleEntry<K,V>(key, this.get(key));
            entries.add(entry);
        }
        return entries;
    }

    /**
     * Get an entry based on the position it is in the map (based on the order entries were created)
     * @param position the position in the map (must be less that the size)
     * @return the entry at that position
     * @throws IllegalArgumentException if the position is greater than the map size
     */
    public Entry<K, V> getEntry(int position) {
        if (position >= list.size()) {
            throw new IllegalArgumentException("Value is too large for the map size: " + list.size());
        }
        K key = list.get(position);
        Entry<K, V> entry = new SimpleEntry<K,V>(key, this.get(key));
        return entry;
    }

    @Override
    public V get(Object key) {
        V value = super.get(key);
        return fixNullOut(value);
    }

    @Override
	public boolean containsKey(Object key) {
    	return super.get(key) != null;
	}

	@Override
    public V put(K key, V value) {
        value = fixNullIn(value);
        V v = super.put(key, value);
        if (v != null) {
            // displaced
            list.remove(key);
        }
        list.add(key);
        return v;
    }

    @Override
	public void putAll(Map<? extends K, ? extends V> m) {
    	for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
    public V putIfAbsent(K key, V value) {
        if (! containsKey(key)) 
            return put(key, value);
        else
            return get(key);
    }

    @Override
    public V remove(Object key) {
        V v = super.remove(key);
        if (v != null) {
            list.remove(key);
        }
        return v;
    }

    @Override
    public boolean remove(Object key, Object value) {
        value = fixNullIn(value);
        boolean removed = super.remove(key, value);
        if (removed) {
            list.remove(key);
        }
        return removed;
    }

    @Override
    public void clear() {
        super.clear();
        list.clear();
    }

    @Override
    public Enumeration<K> keys() {
        return new KeyIterator();
    }

    @Override
    public Enumeration<V> elements() {
        return new ValueIterator();
    }

    transient Set<K> keySet;
    transient Set<Map.Entry<K,V>> entrySet;
    transient Collection<V> values;
    
    /*
    @Override
    public KeySetView <K,V> keySet() {
        //Don't use this method?
        return null;
    }
    */

    //Do not use keySet, you need to use this one
    public Set<K> legacyKeySet() {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet());
    }

    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values());
    }

    @SuppressWarnings("unchecked")
    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es = entrySet;
        return (es != null) ? es : (entrySet = (Set<Map.Entry<K,V>>) (Set) new EntrySet());
    }

    // Iterator support

    abstract class CoreIterator {

        int currentPos = -1;
        Entry<K, V> lastReturned = null;
        private List<Entry<K, V>> entries = ConcurrentOrderedMap.this.getEntries();

        public boolean hasMore() {
            if ((currentPos + 1) < entries.size()) {
                return true;
            }
            return false;
        }

        public Entry<K, V> getNext() {
            currentPos++;
            try {
                lastReturned = entries.get(currentPos);
            } catch (RuntimeException e) {
                throw new NoSuchElementException("There are no more items available to get, the last one was reached");
            }
            return lastReturned;
        }

        public void removeCurrent() {
            if (currentPos < 0) {
                throw new IllegalArgumentException("Have not called next yet, cannot remove from this iterator");
            }
            entries.remove(currentPos);
            ConcurrentOrderedMap.this.remove(lastReturned.getKey());
        }

        // shared methods
        public boolean hasNext() {          return hasMore(); }
        public boolean hasMoreElements() {  return hasMore(); }
        public void remove() {              removeCurrent(); }

    }

    final class KeyIterator extends CoreIterator implements Iterator<K>, Enumeration<K> {
        public K next() {                   return super.getNext().getKey(); }
        public K nextElement() {            return next(); }
    }

    final class ValueIterator extends CoreIterator implements Iterator<V>, Enumeration<V> {
        public V next() {                   return super.getNext().getValue(); }
        public V nextElement() {            return next(); }
    }

    final class EntryIterator extends CoreIterator implements Iterator<Entry<K,V>>, Enumeration<Entry<K,V>> {
        public Entry<K,V> next() {                   return super.getNext(); }
        public Entry<K,V> nextElement() {            return next(); }
    }

    // All below copied from CHM

    final class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        public int size() {
            return ConcurrentOrderedMap.this.size();
        }
        public boolean contains(Object o) {
            return ConcurrentOrderedMap.this.containsKey(o);
        }
        public boolean remove(Object o) {
            return ConcurrentOrderedMap.this.remove(o) != null;
        }
        public void clear() {
            ConcurrentOrderedMap.this.clear();
        }
        public Object[] toArray() {
            Collection<K> c = new ArrayList<K>();
            for (Iterator<K> i = iterator(); i.hasNext(); )
                c.add(i.next());
            return c.toArray();
        }
        public <T> T[] toArray(T[] a) {
            Collection<K> c = new ArrayList<K>();
            for (Iterator<K> i = iterator(); i.hasNext(); )
                c.add(i.next());
            return c.toArray(a);
        }
    }

    final class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        public int size() {
            return ConcurrentOrderedMap.this.size();
        }
        public boolean contains(Object o) {
            return ConcurrentOrderedMap.this.containsValue(o);
        }
        public void clear() {
            ConcurrentOrderedMap.this.clear();
        }
        public Object[] toArray() {
            Collection<V> c = new ArrayList<V>();
            for (Iterator<V> i = iterator(); i.hasNext(); )
                c.add(i.next());
            return c.toArray();
        }
        public <T> T[] toArray(T[] a) {
            Collection<V> c = new ArrayList<V>();
            for (Iterator<V> i = iterator(); i.hasNext(); )
                c.add(i.next());
            return c.toArray(a);
        }
    }

    @SuppressWarnings("unchecked")
    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K,V> e = (Map.Entry<K,V>)o;
            V v = ConcurrentOrderedMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K,V> e = (Map.Entry<K,V>)o;
            return ConcurrentOrderedMap.this.remove(e.getKey(), e.getValue());
        }
        public int size() {
            return ConcurrentOrderedMap.this.size();
        }
        public void clear() {
            ConcurrentOrderedMap.this.clear();
        }
        public Object[] toArray() {
            // Since we don't ordinarily have distinct Entry objects, we
            // must pack elements using exportable SimpleEntry
            Collection<Map.Entry<K,V>> c = new ArrayList<Map.Entry<K,V>>(size());
            for (Iterator<Map.Entry<K,V>> i = iterator(); i.hasNext(); )
                c.add(new SimpleEntry<K,V>(i.next()));
            return c.toArray();
        }
        public <T> T[] toArray(T[] a) {
            Collection<Map.Entry<K,V>> c = new ArrayList<Map.Entry<K,V>>(size());
            for (Iterator<Map.Entry<K,V>> i = iterator(); i.hasNext(); )
                c.add(new SimpleEntry<K,V>(i.next()));
            return c.toArray(a);
        }

    }


    /**
     * This duplicates java.util.AbstractMap.SimpleEntry until this class
     * is made accessible.
     */
    static final class SimpleEntry<K,V> implements Entry<K,V> {
        K key;
        V value;

        public SimpleEntry(K key, V value) {
            this.key   = key;
            this.value = value;
        }

        public SimpleEntry(Entry<K,V> e) {
            this.key   = e.getKey();
            this.value = e.getValue();
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return fixNullOut(value);
        }

        public V setValue(V value) {
            V oldValue = fixNullOut(this.value);
            this.value = value;
            return oldValue;
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            return eq(key, e.getKey()) 
                && eq(value, e.getValue());
        }

        public int hashCode() {
            return ((key   == null)   ? 0 :   key.hashCode()) ^
                   ((value == null)   ? 0 : value.hashCode());
        }

        public String toString() {
            return key + "=" + fixNullOut(value);
        }

        static boolean eq(Object o1, Object o2) {
            return (o1 == null ? o2 == null : o1.equals(o2));
        }
    }

    /**
     * Fixes up the null values so that the map will happily store it,
     * has to be applied to every value going into the map
     */
    @SuppressWarnings("unchecked")
    protected static <T> T fixNullIn(T value) {
        if (value == null) {
            return (T) new Null();
        } else {
            if (value instanceof Null) {
                return null;
            }
        }
        return value;
    }

    /**
     * Fixes up the stored Null objects so they are returned as nulls,
     * has to be applied to every value going out of the map
     */
    protected static <T> T fixNullOut(T value) {
        if (value != null) {
            if (value instanceof Null) {
                return null;
            }
        }
        return value;
    }

    public static class Null {
        @Override
        public boolean equals(Object obj) {
            return true;
        }
        @Override
        public int hashCode() {
            return 0;
        }
    }
}
