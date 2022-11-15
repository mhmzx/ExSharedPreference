package io.github.sgpublic.compiler;

import org.junit.Test;

import lombok.Builder;
import lombok.Data;

/**
 * @author Madray Haven
 * @date 2022/11/15 14:44
 */
public class BuilderTest {
    @Test
    public void test() {
        OriginPref.builder();
    }

    @Data
    @Builder
    public static class OriginPref {
        @Builder.Default
        private int test = 1;
    }
}
