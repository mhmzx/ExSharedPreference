package io.github.sgpublic.compiler;

import org.junit.Test;

import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.collections.SetsKt;

/**
 * @author Madray Haven
 * @date 2022/11/15 14:06
 */
public class LazyJavaTest {
    private final Lazy<Integer> lazy = LazyKt.lazy(() -> {
        System.out.println("initialized!");
        return 114514;
    });

    @Test
    public void test() {
        System.out.println("is initialized: " + lazy.isInitialized());
        System.out.println("value: " + lazy.getValue());
        System.out.println("is initialized: " + lazy.isInitialized());
    }
}
