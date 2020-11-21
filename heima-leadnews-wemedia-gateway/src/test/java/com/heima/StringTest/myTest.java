package com.heima.StringTest;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class myTest {
    @Test
    public void tt(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");
        System.out.println("strings = " + strings);
        String join = StringUtils.join(strings.stream().map(s -> s.replace(" ", "")).collect(Collectors.toList()), ",");
        String join1 = StringUtils.join(strings.stream().collect(Collectors.toList()), ",");
        System.out.println("join1 = " + join1);
        System.out.println("join = " + join);
    }
}
