package com.heima;


import com.heima.utils.common.BCrypt;
import com.mchange.v3.decode.DecodeUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

@RunWith(SpringRunner.class)
public class encryptionTest {
    @Test
    public void Md5Test(){
        String s = DigestUtils.md5DigestAsHex("123".getBytes());
        System.out.println("s = " + s);

        String s1 = RandomStringUtils.randomAlphanumeric(10);
        System.out.println("s1 = " + s1);
        String pwd="123"+s1;
        String s2 = DigestUtils.md5DigestAsHex(pwd.getBytes());
        System.out.println("s2 = " + s2);
    }
    @Test
    public void BCryptTest(){
        String gensalt = BCrypt.gensalt();
        System.out.println("gensalt = " + gensalt);
        String hashpw = BCrypt.hashpw("123", gensalt);
        System.out.println("hashpw = " + hashpw);
    }
}
