package com.heima;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class testtestTime {
    @Test
    public void test() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd:hh-mm-ss");
        Date parse = simpleDateFormat.parse("1599491110000");
        System.out.println("parse = " + parse);
    }
}
