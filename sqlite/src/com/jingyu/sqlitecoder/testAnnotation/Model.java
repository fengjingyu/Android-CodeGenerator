package com.jingyu.sqlitecoder.testAnnotation;



import com.jingyu.sqlitecoder.Ignore;
import com.jingyu.sqlitecoder.annotation.*;

import java.io.Serializable;

/**
 * @author fengjingyu@foxmail.com
 *  DbGenerator.jar可以生成该model对应的sqlite数据库的常用方法
 */
@Updates({
        @Update(methodName = "updateOne", where = "uniqueId = 123"),
        @Update(methodName = "updateTwo", where = "uniqueId = ?"),
        @Update(methodName = "updateThree", where = "name = ?"),
        // 如果update的where 为null，则该方法不会生成
        @Update(methodName = "updateFour", where = "null")
})

@Querys({
        @Query(methodName = "queryOne", where = "gender = 0"),
        @Query(methodName = "queryTwo", where = "gender = 1", orderBy = "_id desc"),
        @Query(methodName = "queryThree", where = "name = ?", orderBy = "_id asc"),
        @Query(methodName = "queryFour", where = "score > 123456", orderBy = "_id asc", limit = "1"),
        @Query(methodName = "queryFive", where = "score = ?", limit = "1"),
        @Query(methodName = "querySix", where = "score >= ?", orderBy = "_id asc", limit = "0,10"),
        @Query(methodName = "querySeven", where = "score <= ?", orderBy = "name asc", limit = "10,30"),
        @Query(methodName = "queryEight", where = "score <= 90", orderBy = "name desc", limit = "5,-1"),
        @Query(methodName = "queryNight", where = "age <= 20 and score > 70"),
        @Query(methodName = "queryTen", where = "null"),
        @Query(methodName = "queryEleven", where = "null", orderBy = "age desc"),
})

@Deletes({
        @Delete(methodName = "deleteOne", where = "name = 123"),
        @Delete(methodName = "deleteTwo", where = "name = ?"),
        @Delete(methodName = "deleteThree", where = "gender = 1"),
        @Delete(methodName = "deleteFour", where = "gender = ? and age = ?"),
        @Delete(methodName = "deleteFive", where = "null")
})

public class Model implements Serializable {

    /**
     * 静态字段不会生成在数据库中
     */
    private static final long serialVersionUID = 7806487539561621886L;

    /**
     * 含有ignore注解的字段不会生成在数据库中
     */
    @Ignore
    String testIgnore = "";

    /**
     * 身份证（该字段的注释会生成在db中）
     */
    String uniqueId = "";
    /**
     * 名字（该字段的注释会生成在db中）
     */
    String name = "";
    /**
     * 性别 0 男 ，1 女（该字段的注释会生成在db中）
     */
    String gender = "";
    /**
     * 年龄（该字段的注释会生成在db中）
     */
    String age = "";
    /**
     * 得分（该字段的注释会生成在db中）
     */
    String score = "";
    /**
     * 爱好（该字段的注释会生成在db中）
     */
    String hobby = "";

    public Model() {
    }

    public Model(String uniqueId, String name, String gender, String age, String score, String hobby) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.score = score;
        this.hobby = hobby;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    @Override
    public String toString() {
        return "Model{" +
                "uniqueId='" + uniqueId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", score='" + score + '\'' +
                ", hobby='" + hobby + '\'' +
                '}';
    }
}
