# reflectutils
A set of reflection utilities and miscellaneous utilities related to working with classes and their fields with no dependencies which is compatible with java 1.5 and generics (and the higher versions of Java as well).

# Features:
These are built to be compatible with Apache Commons BeanUtils (http://commons.apache.org/proper/commons-beanutils/) and the nesting structure works the same, refer to the apache BeanUtils? project docs for details. Support for Apache DynaClass? / DynaBean? is included. Current users of beanutils should be able to drop in these utilities and gain the functionality with minimal code changes.

Handles field operations for properties (getters and setters), partial properties (only getter or only setter), and fields. This is configurable to use the fields only, properties only, or the hybrid approach (default). This improves upon the BeanUtils? limitation of handling only properties or the Google utilities limitation of handling only fields.

Getting and setting fields supports simple, nested, indexed, and mapped values

**Simple:** Get/set a field in a bean (or map), Example: "title", "id"  
**Nested:** Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"  
**Indexed:** Get/set a list/array item by index in a bean, Example: "myList1?", "anArray2?"  
**Mapped:** Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"  

Includes support for dealing with annotations and working with fields which have annotations on them. Methods for finding fields with an annotation and finding all annotations in a class or on a fields are included.

Includes support for deep cloning, deep copying, and populating objects using auto-conversion. Also includes support for fuzzy copies where object data can be copied from one object to another without the objects being the same type.

Also includes an extendable conversion system for converting between java types. This system also handles conversions between arrays, maps, collections, enums, and scalars and improves upon the apache system by handling more types and handling object holders. Support for construction of any class and a set of utilities for determining what types of objects you are working with are also included. A method for executing a specific constructor can be used if more control is needed.

Includes transcoders (encoder/decoder) for conversion of class data to and from JSON and XML. The transcoders are clean and simple and work with any type of object. They will default to converting the incoming data into maps of simple java objects but these can be converted to the correct objects using the reflection utilities if desired.

The utilities cache reflection data for high performance operation but uses weak/soft caching to avoid holding open ClassLoaders? and causing the caches to exist in memory permanently. The ability to override the caching mechanism with your own is supported.

The utilities are modular and are meant to be extendable and overridable. All methods are protected or public so that the various utility classes can be easily overridden if needed.

Sample code:
Examples operate on the class at the bottom (TestEntity). There are more samples in the javadocs.

    Getting a value from an object field
    TestEntity thing = new TestEntity();
    Object value = ReflectUtils.getInstance().getFieldValue(thing, "entityId");
    // value will be "33"
    Setting a value on an object field
    TestEntity thing = new TestEntity();
    ReflectUtils.getInstance().setFieldValue(thing, "entityId", 33);
    // value of thing.getEntityId() will be "33", value is autoconverted into the right type
    Setting a nested value on an object field
    Object thing = new HashMap(); // using a hashmap for simplicity here, could easily be nested POJOs
    ReflectUtils.getInstance().setFieldValue(thing, "person.contactInfo.name", "aaronz");
    // the value of the name field which is on the object in the contactInfo field which is on the object in the person field on the thing object is set to "aaronz"
    Constructing classes
    List l = ReflectUtils.getInstance().constructClass(List.class);
    Class<?> clazz = TestEntity.class;
    Object o = ReflectUtils.getInstance().constructClass(clazz);
    // o will be an instance of TestEntity
    TestEntity?.class (comes directly from the test cases)
    
    public class TestEntity {
       private Long id = new Long(3);
       private String entityId = "33";
       @TestAnnote
       private String extra = null;
       private Boolean bool = null;
       private String[] sArray = {"1","2"};
    
       public String getPrefix() {
          return "crud";
       }
       public String createEntity(Object entity) {
          return "1";
       }
       @TestAnnote
       public String getEntityId() {
          return entityId;
       }
       public void setEntityId(String entityId) {
          this.entityId = entityId;
       }
       public Long getId() {
          return id;
       }
       public void setId(Long id) {
          this.id = id;
       }
       @TestAnnoteField1
       public String getExtra() {
          return extra;
       }
       @TestAnnoteField2("TEST")
       public void setExtra(String extra) {
          this.extra = extra;
       }
       public String[] getSArray() {
          return sArray;
       }
       public void setSArray(String[] array) {
          sArray = array;
       }
       public Boolean getBool() {
          return bool;
       }
       public void setBool(Boolean bool) {
          this.bool = bool;
       }
    }
