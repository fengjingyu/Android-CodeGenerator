package com.jingyu.sqlitecoder.testParser;

import com.jingyu.sqlitecoder.Parser;

import java.io.File;

/**
 * Created by xtyx_jy on 2017/7/17.
 */
public class TestStudent {
    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parse(new File("G:\\jingyu\\localsk\\var\\ideintellij\\codegenerator\\sqlite\\src\\com\\jingyu\\sqlitecoder\\testParser\\Student.java"));
        System.out.println(parser);
    }
}
