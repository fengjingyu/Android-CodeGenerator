//package com.jingyu.sqlitecoder.testAnnotation;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ModelDb extends SQLiteOpenHelper {
//
//    public static String mDefaultDbName = "model.db";
//    public static int mVersion = 1;
//    public static String mOperatorTableName = "modelTable";
//
//    /**
//     * 排序常量
//     */
//    public static String SORT_DESC = " DESC";// 有个空格符号，勿删
//    public static String SORT_ASC = " ASC";// 有个空格符号，勿删
//
//    /**
//     * 以下是表字段
//     */
//    public static final String _ID = "_id";
//    /**
//     * 身份证（该字段的注释会生成在db中）
//     */
//    public static final String UNIQUE_ID = "uniqueId";
//    /**
//     * 名字（该字段的注释会生成在db中）
//     */
//    public static final String NAME = "name";
//    /**
//     * 性别 0 男 ，1 女（该字段的注释会生成在db中）
//     */
//    public static final String GENDER = "gender";
//    /**
//     * 年龄（该字段的注释会生成在db中）
//     */
//    public static final String AGE = "age";
//    /**
//     * 得分（该字段的注释会生成在db中）
//     */
//    public static final String SCORE = "score";
//    /**
//     * 爱好（该字段的注释会生成在db中）
//     */
//    public static final String HOBBY = "hobby";
//
//    /**
//     * 装db集合的
//     */
//    public static Map<String, ModelDb> map = new LinkedHashMap<String, ModelDb>();
//
//    public static ModelDb getInstance(Context context) {
//
//        return getInstance(context, mDefaultDbName);
//    }
//
//    public static ModelDb getInstance(Context context, String dbName) {
//
//        ModelDb db = map.get(dbName);
//
//        if (db != null) {
//            return db;
//        }
//
//        synchronized (ModelDb.class) {
//            if (map.get(dbName) == null) {
//                map.put(dbName, new ModelDb(context, dbName));
//            }
//            return map.get(dbName);
//        }
//
//    }
//
//    private ModelDb(Context context) {
//        super(context, mDefaultDbName, null, mVersion);
//    }
//
//    private ModelDb(Context context, String dbName) {
//        super(context, dbName, null, mVersion);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//        db.execSQL("CREATE TABLE " + mOperatorTableName
//                + "(" + _ID + " integer primary key autoincrement,"
//                + UNIQUE_ID + " text, "
//                + NAME + " text, "
//                + GENDER + " text, "
//                + AGE + " text, "
//                + SCORE + " text, "
//                + HOBBY + " text)");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//
//    }
//
//    public ContentValues createContentValue(Model model) {
//        ContentValues values = new ContentValues();
//        values.put(UNIQUE_ID, model.getUniqueId());
//        values.put(NAME, model.getName());
//        values.put(GENDER, model.getGender());
//        values.put(AGE, model.getAge());
//        values.put(SCORE, model.getScore());
//        values.put(HOBBY, model.getHobby());
//        return values;
//    }
//
//    public Model createModel(Cursor c) {
//        Model model = new Model();
//        model.setUniqueId(c.getString(c.getColumnIndex(UNIQUE_ID)));
//        model.setName(c.getString(c.getColumnIndex(NAME)));
//        model.setGender(c.getString(c.getColumnIndex(GENDER)));
//        model.setAge(c.getString(c.getColumnIndex(AGE)));
//        model.setScore(c.getString(c.getColumnIndex(SCORE)));
//        model.setHobby(c.getString(c.getColumnIndex(HOBBY)));
//        return model;
//    }
//
//    /**
//     * 插入一条记录
//     */
//    public synchronized long insert(Model model) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = createContentValue(model);
//        long id = db.insert(mOperatorTableName, _ID, values);
//        //Logger.d(XCConfig.TAG_DB, "insert()插入的记录的id是: " + id);
//        db.close();
//        return id;
//    }
//
//    public synchronized long inserts(List<Model> list) {
//        int count = 0;
//        SQLiteDatabase db = getWritableDatabase();
//        for (Model model : list) {
//            ContentValues values = createContentValue(model);
//            long id = db.insert(mOperatorTableName, _ID, values);
//            //Logger.d( "insert()插入的记录的id是: " + id);
//            count++;
//        }
//        db.close();
//        return count;
//    }
//
//    /**
//     * 删除所有记录
//     */
//    public synchronized int deleteAll() {
//        SQLiteDatabase db = getWritableDatabase();
//        int raw = db.delete(mOperatorTableName, null, null);
//        db.close();
//        return raw;
//    }
//
//    /**
//     * 查询共有多少条记录
//     */
//    public synchronized int queryCount() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, new String[]{"COUNT(*)"}, null, null, null, null, null, null);
//        c.moveToNext();
//        int count = c.getInt(0);
//        c.close();
//        db.close();
//        return count;
//    }
//
//    /**
//     * 查询所有
//     */
//    public synchronized List<Model> queryAllByIdDesc() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, null, null, null, null, _ID + SORT_DESC); // 条件为null可以查询所有
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    /**
//     * 查询所有
//     */
//    public synchronized List<Model> queryAllByIdAsc() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, null, null, null, null, _ID + SORT_ASC);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    /**
//     * 分页查找
//     */
//    public synchronized List<Model> queryPageByIdAsc(int pageNum, int capacity) {
//        String offset = (pageNum - 1) * capacity + ""; // 偏移量
//        String len = capacity + ""; // 个数
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, null, null, null, null, _ID + SORT_ASC, offset + "," + len);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    /**
//     * 分页查找
//     */
//    public synchronized List<Model> queryPageByIdDesc(int pageNum, int capacity) {
//        String offset = (pageNum - 1) * capacity + ""; // 偏移量
//        String len = capacity + ""; // 个数
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, null, null, null, null, _ID + SORT_DESC, offset + "," + len);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized int deleteOne() {
//        SQLiteDatabase db = getWritableDatabase();
//        int rows = db.delete(mOperatorTableName, NAME + " = 123", null);
//        //Logger.d( "deleteOne()-->" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized int deleteTwo(String value1) {
//        SQLiteDatabase db = getWritableDatabase();
//        int rows = db.delete(mOperatorTableName, NAME + " = ?", new String[]{value1});
//        //Logger.d( "deleteTwo()-->" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized int deleteThree() {
//        SQLiteDatabase db = getWritableDatabase();
//        int rows = db.delete(mOperatorTableName, GENDER + " = 1", null);
//        //Logger.d( "deleteThree()-->" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized int deleteFour(String value1, String value2) {
//        SQLiteDatabase db = getWritableDatabase();
//        int rows = db.delete(mOperatorTableName, GENDER + " = ? and " + AGE + " = ?", new String[]{value1, value2});
//        //Logger.d( "deleteFour()-->" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized int deleteFive() {
//        SQLiteDatabase db = getWritableDatabase();
//        int rows = db.delete(mOperatorTableName, null, null);
//        //Logger.d( "deleteFive()-->" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized int updateOne(Model model) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = createContentValue(model);
//        int rows = db.update(mOperatorTableName, values, UNIQUE_ID + " = 123", null);
//        //Logger.d("updateOne()更新了" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized int updateTwo(Model model, String value1) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = createContentValue(model);
//        int rows = db.update(mOperatorTableName, values, UNIQUE_ID + " = ?", new String[]{value1});
//        //Logger.d("updateTwo()更新了" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized int updateThree(Model model, String value1) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = createContentValue(model);
//        int rows = db.update(mOperatorTableName, values, NAME + " = ?", new String[]{value1});
//        //Logger.d("updateThree()更新了" + rows + "行");
//        db.close();
//        return rows;
//    }
//
//    public synchronized List<Model> queryOne() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, GENDER + " = 0", null, null, null, null, null);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryTwo() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, GENDER + " = 1", null, null, null, _ID + SORT_DESC, null);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryThree(String value1) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, NAME + " = ?", new String[]{value1}, null, null, _ID + SORT_ASC, null);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryFour() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, SCORE + " > 123456", null, null, null, _ID + SORT_ASC, "1");
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryFive(String value1) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, SCORE + " = ?", new String[]{value1}, null, null, null, "1");
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> querySix(String value1) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, SCORE + " >= ?", new String[]{value1}, null, null, _ID + SORT_ASC, "0,10");
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> querySeven(String value1) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, SCORE + " <= ?", new String[]{value1}, null, null, NAME + SORT_ASC, "10,30");
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryEight() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, SCORE + " <= 90", null, null, null, NAME + SORT_DESC, "5,-1");
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryNight() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, AGE + " <= 20 and " + SCORE + " > 70", null, null, null, null, null);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryTen() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, null, null, null, null, null, null);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//    public synchronized List<Model> queryEleven() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(mOperatorTableName, null, null, null, null, null, AGE + SORT_DESC, null);
//        List<Model> beans = new ArrayList<Model>();
//        while (c.moveToNext()) {
//            Model bean = createModel(c);
//            beans.add(bean);
//        }
//        c.close();
//        db.close();
//        return beans;
//    }
//
//}