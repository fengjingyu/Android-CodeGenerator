package com.jingyu.sqlitecoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * @author fengjingyu@foxmail.com
 */
public class Launcher {

    public static JFrame frame;
    public static JButton button;
    public static JTextArea area;
    public static JTextField area_title;
    public static JScrollPane scrollPane;

    public static void main(String[] args) {
        initUI();
    }

    private static void initUI() {
        frame = new JFrame("生成android sql文件");
        button = new JButton("开始");
        area = new JTextArea(50, 100);
        area_title = new JTextField("请输入Model的绝对路径,该model必须有一个无参的构造器, androidStudio为ctrl+shift+c", 100);
        scrollPane = new JScrollPane(area);
        frame.setBounds(150, 50, 1050, 650);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(button, BorderLayout.EAST);
        frame.add(area_title, BorderLayout.NORTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        area.setText("输出结果");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Parser parser = new Parser();
                parser.parse(new File(area_title.getText().trim()));

                Generator generator = new Generator(parser);

                String result = generator.getResultString();

                area.setText("");

                area.setText(result);

                area.updateUI();
            }
        });
    }

}
