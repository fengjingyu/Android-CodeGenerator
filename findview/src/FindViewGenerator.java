import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author fengjingyu@foxmail.com
 * @description 以下的getVIewById、注释、成员变量字段、设置监听、switch等代码是用UiGenerator.jar 生成的
 */
public class FindViewGenerator {

    public static String CONMENT_KEY = "CONMENT";
    public static String LAYOUT_MODEL = "xc_id_model_layout";
    public static String LAYOUT_TITLE = "xc_id_model_titlebar";
    public static String LAYOUT_CONTENT = "xc_id_model_content";
    public static String LAYOUT_NONET = "xc_id_model_no_net";
    public static String LINE = "\r\n";

    public static JFrame frame;
    public static JTextField textfield;
    public static JButton button;
    public static JTextArea area;
    public static JTextField area_title;
    public static JScrollPane scrollPane;
    public static String ENCODING = "utf-8";

    public static String AUTHORITY_PRIVATE = "private";
    public static String AUTHORITY_PUBLIC = "public";

    public static String CLICK_ONE = "@c";
    public static String CLICK_TWO = "@ic";
    public static String CLICK_THREE = "@lc";
    public static String CLICK_FOUR = "@cc";

    public static void main(String[] args) {
        initUI();
    }

    public static void initUI() {

        frame = new JFrame("初始化控件");
        textfield = new JTextField();
        button = new JButton("解析");
        area = new JTextArea(30, 100);
        scrollPane = new JScrollPane(area);
        area_title = new JTextField("请输入xml的绝对路径 , androidStudio为ctrl+shift+c", 100);

        frame.setBounds(200, 100, 1000, 600);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(button, BorderLayout.EAST);
        frame.add(area_title, BorderLayout.NORTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        area.setText("输出结果");

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                parse();
            }
        });

        area_title.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    parse();
                }
            }
        });
    }

    private static void parse() {

        String result = parse(area_title.getText().toString().trim());
        area.setText("");
        area.setText(result);
        area.updateUI();

    }

    public static String parse(String path) {

        StringBuilder sb = new StringBuilder();
        try {

            Map<String, String> map = parseXml(path);

            getFieldAndCommentMethod(sb, map);

            sb.append(LINE);

            getViewByIdMethod(sb, map);

            sb.append(LINE);

            findViewByIdMethod(sb, map);
            sb.append(LINE);
            sb.append("---------------------------------以下是adapter中的holder可能用到的--------------------------------");
            sb.append(LINE);
            sb.append(LINE);

            getHolderMethod(sb, map);

            sb.append(LINE);

            getHolderClassMethod(sb, map);

            sb.append(LINE + LINE);

            sb.append("---------------------------------------------------------------以下是click----------------------------------------------------------");
            sb.append(LINE);

            getSetViewListener(sb, map);

            sb.append(LINE + LINE + LINE);

            getSwitchMethod(sb, map);

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void getFieldAndCommentMethod(StringBuilder sb, Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (check(key)) {
                if (key.contains(CONMENT_KEY)) {
                    if (value != null) {
                        value = value.replace(CLICK_ONE, "");
                        value = value.replace(CLICK_TWO, "");
                        value = value.replace(CLICK_THREE, "");
                        value = value.replace(CLICK_FOUR, "");
                        if (!"".equals(value.trim())) {
                            sb.append("/**" + value + "*/" + LINE);
                        }
                    }
                } else {
                    sb.append(AUTHORITY_PRIVATE + " " + value + " " + key
                            + ";" + LINE);
                }
            }
        }
    }

    private static void findViewByIdMethod(StringBuilder sb, Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (check(key)) {
                if (!key.contains(CONMENT_KEY)) {
                    sb.append(key + " = (" + value + ")findViewById(R.id."
                            + key + ");" + LINE);
                }
            }
        }
        sb.append(LINE);
    }

    private static void getViewByIdMethod(StringBuilder sb, Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (check(key)) {
                if (!key.contains(CONMENT_KEY)) {
                    sb.append(key + " = getViewById(R.id." + key + ");"
                            + LINE);
                }
            }
        }
    }

    private static Map<String, String> parseXml(String path) throws XmlPullParserException, IOException {
        Map<String, String> map = new LinkedHashMap<String, String>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(new FileInputStream(path), ENCODING);

        int event;
        int count = 0;
        // 记录上一个文本的key，因为可能有注释，但是无id，那么这个注释不显示
        String recoderLastTextKey = "";
        while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {

            if (event == XmlPullParser.START_TAG) {
                count++;

                String keyId = parser.getAttributeValue(null, "android:id");

                if (keyId != null) {

                    String packageName = parser.getName();
                    int lastIndex = packageName.lastIndexOf(".");

                    if (lastIndex > 0) {
                        packageName = packageName.substring(lastIndex + 1);
                    }

                    if ("include".equals(packageName)) {
                        packageName = "ViewGroup";
                    }

                    map.put(keyId.substring(keyId.lastIndexOf("/") + 1,
                            keyId.length()).trim(), packageName);
                } else {
                    if (recoderLastTextKey != null && recoderLastTextKey.length() > 0) {
                        String index = recoderLastTextKey.substring(recoderLastTextKey.length() - 1, recoderLastTextKey.length());
                        int index_num = Integer.parseInt(index);
                        if (count - index_num == 1) {
                            map.remove(recoderLastTextKey);
                        }
                    }
                }

            } else if (event == XmlPullParser.TEXT) {

                String str = parser.getPositionDescription();
                int start = str.indexOf("<!--");
                int end = str.lastIndexOf("-->");
                if (start > 0 && end > 0 && end > start) {
                    String content = str.substring(start + 4, end);
                    recoderLastTextKey = CONMENT_KEY + "&" + count;
                    map.put(CONMENT_KEY + "&" + count, parseUnicode(content));
                }
            }

            parser.next();
        }
        return map;
    }

    private static void getHolderClassMethod(StringBuilder sb, Map<String, String> map) {
        sb.append("public class NameHolder{" + LINE);
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (check(key)) {
                if (key.contains(CONMENT_KEY)) {
                    if (value != null) {
                        value = value.replace(CLICK_ONE, "");
                        value = value.replace(CLICK_TWO, "");
                        value = value.replace(CLICK_THREE, "");
                        value = value.replace(CLICK_FOUR, "");
                        if (!"".equals(value.trim())) {
                            sb.append("/**" + value + "*/" + LINE);
                        }
                    }
                } else {
                    sb.append("	" + value + " " + key + ";" + LINE);
                }
            }
        }
        sb.append(LINE);
        sb.append("	public NameHolder(View convertView){" + LINE + LINE);

        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (check(key)) {
                if (!key.contains(CONMENT_KEY)) {
                    sb.append("		" + key + " = (" + value
                            + ")convertView.findViewById(R.id." + key
                            + ");" + LINE);
                }
            }
        }

        sb.append(LINE);

        sb.append("	}" + LINE);
        sb.append("}");
    }

    private static void getHolderMethod(StringBuilder sb, Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (check(key)) {
                if (!key.contains(CONMENT_KEY)) {
                    sb.append("holder." + key + " = (" + value
                            + ")convertView.findViewById(R.id." + key
                            + ");" + LINE);
                }
            }
        }
    }

    private static void getSwitchMethod(StringBuilder sb, Map<String, String> map) {
        String viewContainListener = null;
        sb.append("switch(view.getId()){" + LINE);
        String comment = null;
        for (Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();

            if (value.contains("@")) {
                if (value.contains(CLICK_ONE)) {
                    comment = value;
                    viewContainListener = CLICK_ONE;
                } else if (value.contains(CLICK_TWO)) {
                    viewContainListener = CLICK_TWO;
                } else if (value.contains(CLICK_THREE)) {
                    viewContainListener = CLICK_THREE;
                } else if (value.contains(CLICK_FOUR)) {
                    viewContainListener = CLICK_FOUR;
                } else {
                    comment = null;
                    viewContainListener = null;
                }
            } else {

                if (viewContainListener != null) {
                    // 这个switch只包含onClickListener的id
                    if (CLICK_ONE.equals(viewContainListener)) {
                        if (comment != null) {
                            comment = comment.replace(CLICK_ONE, "");
                        }
                        sb.append("// " + comment + LINE);
                        sb.append("case R.id." + key + " :" + LINE + LINE + "    break;" + LINE);
                    } else if (CLICK_TWO.equals(viewContainListener)) {

                    } else if (CLICK_THREE.equals(viewContainListener)) {

                    } else if (CLICK_FOUR.equals(viewContainListener)) {

                    }
                }
                comment = null;
                viewContainListener = null;
            }
        }
        sb.append("default:" + LINE + LINE + "break;" + LINE);
        sb.append(LINE + "}");
    }

    private static String getSetViewListener(StringBuilder sb, Map<String, String> map) {
        String viewContainListener = null;
        for (Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();

            if (value.contains("@")) {
                if (value.contains(CLICK_ONE)) {
                    viewContainListener = CLICK_ONE;
                } else if (value.contains(CLICK_TWO)) {
                    viewContainListener = CLICK_TWO;
                } else if (value.contains(CLICK_THREE)) {
                    viewContainListener = CLICK_THREE;
                } else if (value.contains(CLICK_FOUR)) {
                    viewContainListener = CLICK_FOUR;
                } else {
                    viewContainListener = null;
                }
            } else {

                if (viewContainListener != null) {

                    if (CLICK_ONE.equals(viewContainListener)) {
                        sb.append(key + ".setOnClickListener(this);" + LINE);
                    } else if (CLICK_TWO.equals(viewContainListener)) {
                        sb.append(key + ".setOnItemClickListener(new AdapterView.OnItemClickListener() {\n" +
                                "            @Override\n" +
                                "            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {\n" +
                                "                \n" +
                                "            }\n" +
                                "        });" + LINE);
                    } else if (CLICK_THREE.equals(viewContainListener)) {
                        sb.append(key + ".setOnLongClickListener(this);" + LINE);
                    } else if (CLICK_FOUR.equals(viewContainListener)) {
                        sb.append(key + ".setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {\n" +
                                "            @Override\n" +
                                "            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {\n" +
                                "                \n" +
                                "            }\n" +
                                "        });" + LINE);
                    }
                }
                viewContainListener = null;
            }
        }
        return viewContainListener;
    }

    public static boolean check(String key) {
        if (key.equals(LAYOUT_MODEL) || key.equals(LAYOUT_TITLE)
                || key.equals(LAYOUT_CONTENT) || key.equals(LAYOUT_NONET)) {
            return true;
        } else {
            return true;
        }
    }

    public static String getStringFromLastIndex(String origin, String symbol) {
        int index = origin.lastIndexOf(symbol);
        if (index > 0) {
            return origin.substring(index + 1, origin.length());
        }
        return "";
    }

    public static String parseUnicode(String line) {
        int len = line.length();
        char[] out = new char[len];
        int outLen = 0;
        for (int i = 0; i < len; i++) {
            char aChar = line.charAt(i);
            if (aChar == '\\') {
                aChar = line.charAt(++i);
                if (aChar == 'u') {
                    int value = 0;
                    for (int j = 0; j < 4; j++) {
                        aChar = line.charAt(++i);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char) value;
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = aChar;
            }
        }
        return new String(out, 0, outLen);
    }

}
