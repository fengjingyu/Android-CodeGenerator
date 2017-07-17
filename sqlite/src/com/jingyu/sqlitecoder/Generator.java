package com.jingyu.sqlitecoder;

import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * @author fengjingyu@foxmail.com
 * @description
 * 1 model中的static的字段不会生成在db类中
 * 2 @ignore可以忽略model的某一个字段不生成在db类中
 * 3 默认会生成 insert（存入一个） inserts(批量存入) deleteAll(删除所有) queryCount(查询所有数量) queryAll（查询所有） queryPage（分页）方法
 * 4 model的字段之上的注释会生成在db类中（仅多行注释）
 * 5 通过配置@query @update @delete可以生成对应的删查改方法(已删除 @Deprecated)
 * <p>
 */
public class Generator {

    private Parser parser;

    public Generator(Parser parser) {
        this.parser = parser;
    }

    public String getResultString() {
        return getClassNameStatement()
                + "{" + Parser.LINE
                + Parser.LINE
                + getDbNameFieldStatement()
                + Parser.LINE
                + getDbVersionFieldStatement()
                + Parser.LINE
                //+ getTableFieldStatement()
                + Parser.LINE
                + Parser.LINE
                + getSortFieldStatement()
                + getSqlFieldsString()
                + Parser.LINE
                //+ getSingleInstance()
                + getConstructMethodStatement()
                + Parser.LINE
                + getOnCreateMethodStatement()
                + Parser.LINE
                + getUpdateMethodStatement()
                //+ getReflectConstructMethodStatement()
                + getContentValueMethodStatement()
                + Parser.LINE
                + createModel()
                + Parser.LINE
                // 至少会有这些默认的方法
                + insert()
                + inserts()
                + deleteAll()
                + queryCount()
                + queryAllByIdDesc()
                + queryAllByIdAsc()
                + queryPageByIdAsc()
                + queryPageByIdDesc()
                + "}";
    }

    public String isSync() {
        if (true) {
            return " synchronized ";
        } else {
            return " ";
        }
    }

    private String getReflectConstructMethodStatement() {
        return "    public static "
                + parser.getDbName()
                + " instanceDb(Context context, Class<? extends SQLiteOpenHelper> dbClazz, String dbName" + Parser.LINE
                +
                "                                           ) {" + Parser.LINE
                + "        try {" + Parser.LINE
                +
                "            // Logger.i(\"dbClazz----instanceDb()\");" + Parser.LINE
                +
                "            Constructor constructor = dbClazz.getConstructor(Context.class, String.class);" + Parser.LINE
                +
                "            Object o = constructor.newInstance(context, dbName);" + Parser.LINE
                + "            return (" + parser.getDbName() + ") o;" + Parser.LINE
                + "        } catch (Exception e) {" + Parser.LINE
                + "            e.printStackTrace();" + Parser.LINE
                + "            // Logger.e(context, \"\", e);" + Parser.LINE
                + "            return null;" + Parser.LINE + "        }" + Parser.LINE + "    }" + Parser.LINE;
    }

    private String getOnCreateMethodStatement() {
        return "    @Override" + Parser.LINE
                + "    public void onCreate(SQLiteDatabase db) {" + Parser.LINE
                + Parser.LINE
                + createTableSql()
                + Parser.LINE
                + "    }" + Parser.LINE;
    }

    private String getUpdateMethodStatement() {
        return "    @Override" + Parser.LINE
                + "    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {" + Parser.LINE + Parser.LINE
                + "    }" + Parser.LINE;
    }

    private String getConstructMethodStatement() {
        return "    public " + parser.getDbName() + " (Context context) {" + Parser.LINE
                + "        super(context, DB_NAME, null, VERSION);" + Parser.LINE
                + "    }" + Parser.LINE;
    }

    private String getConstructMethodStatement2() {
        return "    private " + parser.getDbName() + " (Context context,String dbName) {" + Parser.LINE
                + "        super(context, dbName, null, mVersion);" + Parser.LINE
                + "    }" + Parser.LINE;
    }

    private String getSingleInstance() {

        return "  /**" + Parser.LINE +
                "     * 装db集合的" + Parser.LINE +
                "     */" + Parser.LINE +
                "    public static Map<String," + parser.getDbName() + "> map = new LinkedHashMap<String," + parser.getDbName() + ">();" + Parser.LINE +
                Parser.LINE +
                "    public static " + parser.getDbName() + " getInstance(Context context) {" + Parser.LINE +
                Parser.LINE +
                "       return getInstance(context,DB_NAME);" +
                Parser.LINE +
                "    }" + Parser.LINE + Parser.LINE +
                "    public static " + parser.getDbName() + " getInstance(Context context, String dbName) {" + Parser.LINE +
                Parser.LINE +
                "        " + parser.getDbName() + " db = map.get(dbName);" + Parser.LINE +
                Parser.LINE +
                "        if (db != null) {" + Parser.LINE +
                "            return db;" + Parser.LINE +
                "        }" + Parser.LINE +
                Parser.LINE +
                "        synchronized (" + parser.getDbName() + ".class) {" + Parser.LINE +
                "            if (map.get(dbName) == null) {" + Parser.LINE +
                "                map.put(dbName, new " + parser.getDbName() + "(context, dbName));" + Parser.LINE +
                "            }" + Parser.LINE +
                "            return map.get(dbName);" + Parser.LINE +
                "        }" + Parser.LINE +
                Parser.LINE +
                "    }"
                + Parser.LINE + Parser.LINE;
    }

    private String getClassNameStatement() {
        return "public class "
                + parser.getDbName()
                + " extends SQLiteOpenHelper";
    }

    private String getSortFieldStatement() {
        return "	   /** 排序常量 */ " + Parser.LINE
                + "	   public static String SORT_DESC = \" DESC\";// 有个空格符号，勿删" + Parser.LINE
                + "	   public static String SORT_ASC = \" ASC\";// 有个空格符号，勿删";
    }

    private String getTableFieldStatement() {
        return "    public static String mOperatorTableName = \"" + StringUtil.setFirstLetterSmall(parser.getTableName()) + "Table\";";
    }

    private String getDbVersionFieldStatement() {
        return "    public static final int VERSION = " + 1 + ";";
    }

    private String getDbNameFieldStatement() {
        return "    public static final String DB_NAME = \"" + StringUtil.setFirstLetterSmall(parser.getTableName()) + ".db\";";
    }

    private String queryPageByIdDesc() {

        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("/** 分页查找 */ " + Parser.LINE);
        sb.append(" public" + isSync() + "List<" + parser.getTableName()
                + "> queryPageByIdDesc(int pageNum, int capacity){" + Parser.LINE);
        sb.append("String offset = (pageNum - 1) * capacity + \"\"; // 偏移量" + Parser.LINE);
        sb.append(" String len = capacity + \"\"; // 个数" + Parser.LINE);
        sb.append("SQLiteDatabase db = getReadableDatabase();" + Parser.LINE);
        sb.append("Cursor c = db.query(mOperatorTableName, null, null, null, null, null, _ID + SORT_DESC , offset + \",\" + len);" + Parser.LINE);
        sb.append("List<" + parser.getTableName() + "> beans = new ArrayList<"
                + parser.getTableName() + ">();" + Parser.LINE);
        sb.append("while (c.moveToNext()) {" + Parser.LINE);
        sb.append(parser.getTableName() + " bean = createModel(c);" + Parser.LINE);
        sb.append("beans.add(bean);" + Parser.LINE);
        sb.append("}" + Parser.LINE);
        sb.append("c.close();" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return beans;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();

    }

    private String queryPageByIdAsc() {

        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("/** 分页查找 */ " + Parser.LINE);
        sb.append(" public" + isSync() + "List<" + parser.getTableName()
                + "> queryPageByIdAsc(int pageNum, int capacity){" + Parser.LINE);
        sb.append("String offset = (pageNum - 1) * capacity + \"\"; // 偏移量" + Parser.LINE);
        sb.append(" String len = capacity + \"\"; // 个数" + Parser.LINE);
        sb.append("SQLiteDatabase db = getReadableDatabase();" + Parser.LINE);
        sb.append("Cursor c = db.query(mOperatorTableName, null, null, null, null, null, _ID + SORT_ASC , offset + \",\" + len);" + Parser.LINE);
        sb.append("List<" + parser.getTableName() + "> beans = new ArrayList<"
                + parser.getTableName() + ">();" + Parser.LINE);
        sb.append("while (c.moveToNext()) {" + Parser.LINE);
        sb.append(parser.getTableName() + " bean = createModel(c);" + Parser.LINE);
        sb.append("beans.add(bean);" + Parser.LINE);
        sb.append("}" + Parser.LINE);
        sb.append("c.close();" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return beans;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();

    }

    private String queryAllByIdAsc() {

        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("/** 查询所有*/ " + Parser.LINE);
        sb.append(" public" + isSync() + "List<" + parser.getTableName() + "> queryAllByIdAsc() {" + Parser.LINE);
        sb.append("SQLiteDatabase db = getReadableDatabase();" + Parser.LINE);
        sb.append("Cursor c = db.query(mOperatorTableName, null, null, null, null, null,_ID + SORT_ASC);" + Parser.LINE);
        sb.append("List<" + parser.getTableName() + "> beans = new ArrayList<"
                + parser.getTableName() + ">();" + Parser.LINE);

        sb.append("while (c.moveToNext()) {" + Parser.LINE);
        sb.append(parser.getTableName() + " bean = createModel(c);" + Parser.LINE);
        sb.append("beans.add(bean);" + Parser.LINE);
        sb.append("}" + Parser.LINE);
        sb.append("c.close();" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return beans;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();
    }

    private String queryAllByIdDesc() {

        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("/** 查询所有*/ " + Parser.LINE);
        sb.append(" public" + isSync() + "List<" + parser.getTableName() + "> queryAllByIdDesc() {" + Parser.LINE);
        sb.append("SQLiteDatabase db = getReadableDatabase();" + Parser.LINE);
        sb.append("Cursor c = db.query(mOperatorTableName, null, null, null, null, null,_ID + SORT_DESC); // 条件为null可以查询所有" + Parser.LINE);
        sb.append("List<" + parser.getTableName() + "> beans = new ArrayList<"
                + parser.getTableName() + ">();" + Parser.LINE);

        sb.append("while (c.moveToNext()) {" + Parser.LINE);
        sb.append(parser.getTableName() + " bean = createModel(c);" + Parser.LINE);
        sb.append("beans.add(bean);" + Parser.LINE);
        sb.append("}" + Parser.LINE);
        sb.append("c.close();" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return beans;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();
    }

    private String queryCount() {

        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("/** 查询共有多少条记录 */ " + Parser.LINE);
        sb.append("public" + isSync() + "int queryCount() {" + Parser.LINE);
        sb.append("SQLiteDatabase db = getReadableDatabase();" + Parser.LINE);
        sb.append("Cursor c = db.query(mOperatorTableName, new String[]{\"COUNT(*)\"},null, null, null, null, null, null);" + Parser.LINE);
        sb.append("c.moveToNext();" + Parser.LINE);
        sb.append("int count = c.getInt(0);" + Parser.LINE);
        sb.append("c.close();" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return count;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();

    }

    private String deleteAll() {
        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("/** 删除所有记录 */ " + Parser.LINE);
        sb.append("public" + isSync() + "int deleteAll() {" + Parser.LINE);
        sb.append("SQLiteDatabase db = getWritableDatabase();" + Parser.LINE);
        sb.append("int raw = db.delete(mOperatorTableName, null, null);" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return raw;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();
    }

    private String insert() {
        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("/** 插入一条记录 */ " + Parser.LINE);
        sb.append("public" + isSync() + " long insert(" + parser.getTableName() + " model) {" + Parser.LINE);
        sb.append("SQLiteDatabase db = getWritableDatabase();" + Parser.LINE);
        sb.append("ContentValues values = createContentValue(model);" + Parser.LINE);
        sb.append("long id = db.insert(mOperatorTableName, _ID, values);" + Parser.LINE);
        sb.append("	//Logger.i(\"insert()插入的记录的id是: \" + id);" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return id;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();
    }

    private String inserts() {

        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("public" + isSync() + " long inserts(List<" + parser.getTableName() + "> list) {" + Parser.LINE);
        sb.append("int count = 0;" + Parser.LINE);
        sb.append("SQLiteDatabase db = getWritableDatabase();" + Parser.LINE);

        sb.append("for(" + parser.getTableName() + " model : list){" + Parser.LINE);

        sb.append("ContentValues values = createContentValue(model);" + Parser.LINE);
        sb.append("long id = db.insert(mOperatorTableName, _ID, values);" + Parser.LINE);
        sb.append("	//Logger.i(\"insert()插入的记录的id是: \" + id);" + Parser.LINE);
        sb.append("	count++;" + Parser.LINE);
        sb.append("}" + Parser.LINE);
        sb.append("db.close();" + Parser.LINE);
        sb.append("return count;" + Parser.LINE + "}" + Parser.LINE + Parser.LINE);
        return sb.toString();
    }

    private String createModel() {

        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("	public " + parser.getTableName() + " createModel(Cursor c){" + Parser.LINE);
        sb.append("	" + parser.getTableName() + " model = new " + parser.getTableName()
                + "();" + Parser.LINE);
        for (Entry<String, String> entry : parser.getFields().entrySet()) {
            sb.append("	model.set" + StringUtil.setFirstLetterBig(entry.getValue())
                    + "(c.getString(c.getColumnIndex("
                    + entry.getKey().toUpperCase() + ")));" + Parser.LINE);
        }
        sb.append("	return model;" + Parser.LINE + "}");

        return sb.toString();
    }

    private String getContentValueMethodStatement() {

        ArrayList<String> list = getPutContentValues();
        StringBuilder sb = new StringBuilder(Parser.LINE);
        sb.append("	public ContentValues createContentValue(" + parser.getTableName()
                + " model) {" + Parser.LINE);
        sb.append("	ContentValues values = new ContentValues();" + Parser.LINE);

        for (String str : list) {

            sb.append("	values.put(" + str + ");" + Parser.LINE);
        }
        return sb.append("	return values;" + Parser.LINE + "	}").toString();

    }

    private ArrayList<String> getPutContentValues() {
        ArrayList<String> list = new ArrayList<String>();
        for (Entry<String, String> entry : parser.getFields().entrySet()) {
            list.add(entry.getKey().toUpperCase() + ", model." + "get"
                    + StringUtil.setFirstLetterBig(entry.getValue()) + "()");
        }
        return list;
    }

    private String createTableSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("db.execSQL(");
        sb.append("\"" + "CREATE TABLE " + "\" + " + parser.getTableName()
                + Parser.LINE + "+ \"" + "(" + "\"" + "+" + "_ID " + "+" + "\""
                + " integer primary key autoincrement," + "\"" + Parser.LINE);
        for (Entry<String, String> entry : parser.getFields().entrySet()) {
            sb.append(" + ").append(entry.getKey().toUpperCase())
                    .append(" + ").append("\" text, \"" + Parser.LINE);
        }

        String result = StringUtil.getBeforeLastSimbolString(sb.toString(), ",");
        result = result + ")\"";

        return result + ");";
    }

    private String getSqlFieldsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Parser.LINE);
        sb.append(Parser.LINE).append("	").append("/").append("**").append("以下是表字段")
                .append("*").append("/").append(Parser.LINE);
        sb.append("public static final String _ID = \"_id\";" + Parser.LINE);
        for (Entry<String, String> entry : parser.getFields().entrySet()) {
            // 获取注释
            if (parser.getComments().get(entry.getValue()) != null) {
                //sb.append("/**").append(Parser.LINE);
                sb.append(" " + parser.getComments().get(entry.getValue())).append(Parser.LINE);
                //sb.append("*/").append(Parser.LINE);
            }
            // 常量语句
            sb.append("	public static final String ")
                    .append(entry.getKey().toUpperCase())
                    .append(" = ").append("\"")
                    .append(entry.getValue().toString()).append("\"")
                    .append(";").append(Parser.LINE);
        }
        return sb.toString();
    }

}
