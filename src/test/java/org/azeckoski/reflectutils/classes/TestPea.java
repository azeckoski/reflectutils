/**
 * $Id: TestPea.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestPea.java $
 * TestPea.java - genericdao - May 18, 2008 10:18:18 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.classes;

import org.azeckoski.reflectutils.annotations.TestAnnote;
import org.azeckoski.reflectutils.annotations.TestAnnoteClass2;
import org.azeckoski.reflectutils.annotations.TestAnnoteField1;

/**
 * {@link TestAnnoteClass2} on the class <br/>
 * (f) String id = "id" <br/>
 * (f) String entityId = "EID" ({@link TestAnnote}) <br/>
 * {@link TestAnnoteField1} on the protected and private fields
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
@TestAnnoteClass2("TEST")
public class TestPea {
    public String id = "id";
    @TestAnnote
    public String entityId = "EID";
    @TestAnnoteField1
    protected String prot = "prot";
    @SuppressWarnings("unused")
    @TestAnnoteField1
    private String priv = "priv";

    /**
     * defaults: id="id", entityId="EID"
     */
    public TestPea() {}
    public TestPea(String id,String entityId) {
        this.entityId = entityId;
        this.id = id;
    }
}