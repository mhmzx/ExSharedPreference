package io.github.sgpublic.compiler;

import org.junit.Assert;
import org.junit.Test;

public class ClassNameTest {
    @Test
    public void test() {
        Assert.assertEquals("io.github.sgpublic.compiler", ClassNameTest.class.getPackage().getName());
    }
}
