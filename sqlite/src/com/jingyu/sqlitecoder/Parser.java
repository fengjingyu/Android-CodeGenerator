package com.jingyu.sqlitecoder;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * @author fengjingyu@foxmail.com
 * @description 1 model中的static的字段不会生成在db类中
 * 2 @ignore可以忽略model的某一个字段不生成在db类中
 * 3 默认会生成 insert（存入一个） inserts(批量存入) deleteAll(删除所有) queryCount(查询所有数量) queryAll（查询所有） queryPage（分页）方法
 * 4 model的字段之上的注释会生成在db类中（仅多行注释）
 * 5 通过配置@query @update @delete可以生成对应的删查改方法
 * <p>
 */
public class Parser {
    public final static String LINE = System.getProperty("line.separator");
    public final static String ENCODING = "utf-8";

    private String dbName = "";
    private String tableName = "";
    private String originFileString = "";

    private LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> comments = new LinkedHashMap<String, String>();

    public void parse(File file) {
        reset();

        tableName = UtilString.getBeforeLastSimbolString(file.getName(), ".");
        dbName = tableName + "Db";
        originFileString = getStringFromFileByUTF(file);

        String content = UtilString.getAfterFirstSimbolString(originFileString.replace(LINE, ""), "class");
        int startIndex = content.indexOf("{");
        int endIndex = content.lastIndexOf("}");
        content = content.substring(startIndex + 1, endIndex);
        String[] statements = content.split(";");
        for (String statement : statements) {

            // 排除静态的字段
            if (statement.contains(" static ")) {
                continue;
            }

            // 可能是构造器的this赋值
            if (statement.contains("this.")) {
                continue;
            }

            // 忽略ignore注解的字段
            if (statement.contains("@Ignore")) {
                continue;
            }

            if (statement.contains("{") || statement.contains("}")) {
                // 是方法语句
            } else {
                // 是字段声明

                // 获取字段
                if (statement.contains("=")) {
                    statement = UtilString.getBeforeLastSimbolString(statement, "=").trim();
                }

                String field = UtilString.getAfterLastSimbolString(statement, " ");
                if (field != null && !"".equals(field)) {
                    //fields.put(getUnderLineConstantFieldKey(field), field);
                    fields.put(field, field);
                }

                // 获取注释
                getComments(field, statement);
            }
        }
    }

    private String getUnderLineConstantFieldKey(String origin) {
        for (int i = 0; i < 26; i++) {
            if (origin.contains(((char) ('A' + i)) + "")) {
                origin = origin.replace(((char) ('A' + i)) + "", "_" + ((char) ('A' + i)));
                origin = origin.replace(((char) ('A' + i)) + "", "_" + ((char) ('A' + i)));
            }
        }
        // 这里没有toUpCase，否则entrySet（）出来后就变成无序的了
        return origin;
    }

    /**
     * 获取注释
     */
    private void getComments(String keyField, String statement) {
        if (statement.contains("/**") && statement.contains("*/")) {
            int start = statement.indexOf("/**");
            int end = statement.lastIndexOf("*/");
            comments.put(keyField, statement.substring(start, end + 2).trim());
        }
    }

    /**
     * 获取文件内容
     */
    private String getStringFromFileByUTF(File file) {
        BufferedReader br = null;
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), ENCODING);
            br = new BufferedReader(reader);
            StringBuilder buffer = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                buffer.append(s).append(LINE);
            }
            return buffer.toString();
            //.replace(LINE, "")
            //.replace("/", "")
            //.replace("*", "")
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void reset() {
        dbName = "";
        tableName = "";
        originFileString = "";
        fields.clear();
        comments.clear();
    }

    public String getDbName() {
        return dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public LinkedHashMap<String, String> getFields() {
        return fields;
    }

    public LinkedHashMap<String, String> getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "Parser{" +
                "dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", originFileString='" + originFileString + '\'' +
                ", fields=" + fields +
                ", comments=" + comments +
                '}';
    }
}
