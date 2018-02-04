package com.match.ods.ci;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.match.ods.dcf.CompanyHPIPredicate;
import com.match.ods.dcf.Doc;

public class ObjectArrayTest {

    @Test
    public void objectArrayTest() {
        Object[] objs = new Object[5];

        objs[0] = "String";
        objs[1] = new Doc("123");
        objs[2] = new CompanyHPIPredicate();

        takeObjects(objs);
        assertTrue(this instanceof Object);
    }

    private void takeObjects(Object[] objs) {
        for (Object o : objs) {
            System.out.println(o);
        }

        Doc d = (Doc) objs[1];
        System.out.println(objs[2]);
    }
}
