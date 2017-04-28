import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author  fengjingyu@foxmail.com
 * @description
 */
public class SpGenerator {

    public static JFrame frame;
    public static JTextField textfield;
    public static JButton button;
    public static JTextArea area;
    public static JTextField area_title;
    public static JScrollPane scrollPane;
    public static String ENCODING = "utf-8";

    public static void main(String[] args) {

        initUI();

    }


    public static void initUI() {

        frame = new JFrame("生成android sp方法");
        textfield = new JTextField();
        button = new JButton("开始");
        area = new JTextArea(30, 100);
        scrollPane = new JScrollPane(area);
        area_title = new JTextField("userName&userId&token&gender&age&city", 100);

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
                launch();
            }
        });

        area_title.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    launch();
                }
            }
        });
    }

    private static void launch() {
        String result = parse();
        area.setText("");
        area.setText(result);
        area.updateUI();
    }

    private static String parse() {

        String keys = area_title.getText().toString().trim();

        StringBuilder sb = new StringBuilder("");

        String[] split = keys.split("&");

//        for (String key : split) {
//            sb.append(start(key, types));
//        }

        sb.append("----------------------------------------以下是String类型的sp------------------------------------" + LINE);
        for (String key : split) {
            sb.append(start(key, new String[]{STRING}));
        }

        sb.append("----------------------------------------以下是Boolean类型的sp-----------------------------------" + LINE);
        for (String key : split) {
            sb.append(start(key, new String[]{BOOLEAN}));
        }

        sb.append("-----------------------------------------以下是int类型的sp--------------------------------------" + LINE);
        for (String key : split) {
            sb.append(start(key, new String[]{INT}));
        }

        sb.append("-----------------------------------------以下是long类型的sp-------------------------------------" + LINE);
        for (String key : split) {
            sb.append(start(key, new String[]{LONG}));
        }


        sb.append("-----------------------------------------以下是float类型的sp-------------------------------------" + LINE);
        for (String key : split) {
            sb.append(start(key, new String[]{FLOAT}));
        }

        return sb.toString();

    }

    public static final String PUBLIC_STATIC = "public static ";

    public static final String SP_GET = "Sp.get";
    public static final String SP_SET = "Sp.put";

    public static final String GET = "get";
    public static final String SET = "set";

    public static final String FLOAT = "float";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String BOOLEAN = "boolean";
    public static final String STRING = "string";
    public static final String VOID = "void";

    public static final String[] types = new String[]{INT, STRING, BOOLEAN, LONG, FLOAT};

    private static String start(String key) {
        return start(key, types);
    }

    private static String start(String key, String[] types) {
        if (key != null && key.length() > 0) {

            return generate(key, types);

        }
        return "";
    }

    private static String generate(String key) {
        return generate(key, types);
    }

    private static String generate(String key, String[] types) {

        StringBuilder sb = new StringBuilder();
        String newKey = setFirstLetterBig(key);

        for (String type : types) {

            generateFiled(key, sb);

            generateGet(key, sb, newKey, type);

            generateSet(key, sb, newKey, type);

        }
        return sb.toString();
    }

    private static void generateSet(String key, StringBuilder sb, String newKey, String type) {
        sb.append(PUBLIC_STATIC);
        sb.append(VOID + " ");
        sb.append(SET);
        sb.append(newKey);
        if (STRING.equals(type)) {
            sb.append("(" + setFirstLetterBig(type) + " value){");
        } else {
            sb.append("(" + type + " value){");
        }

        sb.append(SP_SET + setFirstLetterBig(type) + "(");
        sb.append(getStaticKey(key));
        sb.append(",value);");
        sb.append("}");
        sb.append(LINE + LINE);
    }

    private static void generateGet(String key, StringBuilder sb, String newKey, String type) {
        sb.append(PUBLIC_STATIC);
        if (STRING.equals(type)) {
            sb.append(setFirstLetterBig(type) + " ");
        } else {
            sb.append(type + " ");
        }
        sb.append(GET);
        sb.append(newKey);
        sb.append("(){");

        sb.append("return " + SP_GET + setFirstLetterBig(type) + "(");
        sb.append(getStaticKey(key));

        if (STRING.equals(type)) {
            sb.append(",\"\");");
        } else if (INT.equals(type)) {
            sb.append(",0);");
        } else if (LONG.equals(type)) {
            sb.append(",0L);");
        } else if (BOOLEAN.equals(type)) {
            sb.append(",false);");
        } else if (FLOAT.equals(type)) {
            sb.append(",0F);");
        }
        sb.append("}");
        sb.append(LINE);
    }

    public static final String LINE = "\r\n";

    private static String generateFiled(String key, StringBuilder sb) {
        return sb.append(PUBLIC_STATIC + " final String " + getStaticKey(key) + " = " + "\"" + key + "\"" + ";" + LINE).toString();
    }

    public static final String symbol = "_";

    private static String getStaticKey(String key) {

        String newKey = setFirstLetterSmall(key);

        if (newKey.contains(symbol)) {

        } else {
            char[] chars = newKey.toCharArray();

            HashSet<String> bigLetter = new HashSet();

            for (int i = 0; i < chars.length; i++) {
                if (65 <= chars[i] && chars[i] <= 90) {
                    bigLetter.add(chars[i] + "");
                }
            }

            for (String bitLetter : bigLetter) {
                newKey = newKey.replace(bitLetter, symbol + bitLetter);
            }
        }

        return newKey.toUpperCase();
    }


    public static String setFirstLetterBig(String origin) {

        char[] chars = origin.toCharArray();

        if (chars[0] >= 97 && chars[0] <= 122) {
            chars[0] = (char) (chars[0] - 32);
        }

        return new String(chars);

    }

    /**
     * 设置第一个字母为小写写
     */
    public static String setFirstLetterSmall(String origin) {

        char[] chars = origin.toCharArray();

        if (chars[0] >= 65 && chars[0] < 90) {
            chars[0] = (char) (chars[0] + 32);
        }

        return new String(chars);

    }


}
