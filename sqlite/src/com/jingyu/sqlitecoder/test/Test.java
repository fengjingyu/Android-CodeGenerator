package com.jingyu.sqlitecoder.test;


import com.jingyu.sqlitecoder.Parser;

import java.io.File;

/**
 * Created by jingyu on 2017/2/21.
 */
public class Test {
    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parse(new File("G:\\jingyu\\localsk\\var\\ideintellij\\codegenerator\\sqlite\\src\\com\\jingyu\\sqlitecoder\\test\\Student.java"));
        System.out.println(parser);
    }
}
