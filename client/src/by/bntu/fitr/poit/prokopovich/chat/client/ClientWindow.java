package by.bntu.fitr.poit.prokopovich.chat.client;

import by.bntu.fitr.poit.prokopovich.chat.network.TCPConnection;
import by.bntu.fitr.poit.prokopovich.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    // цвета
    public static final Color TURQUOISE = new Color(168,228,160);
    public static final Color SEA_GREEN = new Color(159,226,191);
    public static final Color SCREAMING_GREEN = new Color(118,255,122);
    public static final Color FOREST_WOLF = new Color(233,231,230);
    public static final Color WHITE_GREEN = new Color(189,236,182);
    public static final Color SMOKY_WHITE = new Color(245,245,245);

    //private static final String IP_ADDR = "192.168.56.1";
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 3345;
    private static final int WIDTH = 550;
    private static final int HEIGHT = 630;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea textArea = new JTextArea();
    private final JFrame frame = new JFrame("Network chat");
    private final JTextField fieldNickname = new JTextField();
    private final JTextField fieldInput = new JTextField();
    private final JButton btnEnter = new JButton("Enter");
    private final JLabel label = new JLabel("Welcome to the chat!");
    private final JButton buttonTime = new JButton();

    private JMenu settings, help;
    private JMenu theme, font, profile;
    private JMenuItem contact, version, about;
    private JMenuItem lightTheme, darkTheme, greenTheme;
    private JMenuItem timesRoman, arial, mvBoli, comicSansMs, lucidaConsole;
    private JMenuItem nickname;
    private JMenuBar menuBar;
    private TCPConnection connection;


    private  ClientWindow() {
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // всегда по середине
        frame.setAlwaysOnTop(true);        // поверх других окон
        frame.setVisible(true);
        GridBagLayout layout = new GridBagLayout();
        frame.setLayout(layout);
        frame.setFont(new Font("TimesRoman", Font.BOLD, 14));
        Image icon = Toolkit.getDefaultToolkit().getImage("D:\\univer\\ксис\\kr\\chat.png");
        frame.setIconImage(icon);

        textArea.setEditable(false); // запрет редактирования
        textArea.setLineWrap(true);  // перенос слов
        fieldNickname.setEditable(false);
        fieldNickname.setToolTipText("Enter your nickname through settings");
        btnEnter.setToolTipText("Click to send message");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);

        // поток для времени
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int hours = 0;
                    int minutes = 0;
                    int seconds = 0;
                    String timeString = "";

                    while (true) {
                        Calendar calendar = Calendar.getInstance();
                        hours = calendar.get(Calendar.HOUR_OF_DAY) ;
                        if (hours > 12) hours -= 12;
                        minutes = calendar.get(Calendar.MINUTE);
                        seconds = calendar.get(Calendar.SECOND);

                        // kk вместо hh ля 24-часового формата
                        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss");
                        Date date = calendar.getTime();
                        timeString = formatter.format(date);

                        buttonTime.setText(timeString);
                    }
                } catch (Exception e) {
                    printMsg("Digital watch: " + e);
                }
            }
        });
        thread.start();

        addMenuBar();
        addElements();
        defaultTheme(); // тема по умолчанию светлая

        btnEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = fieldNickname.getText();
                if(nickname.equals("")) {
                    JOptionPane.showMessageDialog(frame, "Enter your nickname!", "Warning", JOptionPane.WARNING_MESSAGE);
                    fieldInput.setText(null);
                }
                String msg = fieldInput.getText();
                if (msg.equals("")) return;
                fieldInput.setText(null);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                connection.sendString(formatter.format(date)+ " " + fieldNickname.getText() + ": " + msg);

            }
        });

        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void OnConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void OnReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void OnDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close");
    }

    @Override
    public void OnException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(msg + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nickname) {
            String nickname = JOptionPane.showInputDialog(frame, "Enter nickname");
            fieldNickname.setText(nickname);
        }
        if (e.getSource() == lightTheme) {
            fieldNickname.setBackground(FOREST_WOLF);
            fieldNickname.setForeground(Color.BLACK);

            label.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);

            buttonTime.setBackground(Color.WHITE);
            buttonTime.setForeground(Color.DARK_GRAY);

            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);

            fieldInput.setBackground(FOREST_WOLF);
            fieldInput.setForeground(Color.BLACK);

            btnEnter.setBackground(Color.WHITE);
            btnEnter.setForeground(Color.DARK_GRAY);

            menuBar.setBackground(SMOKY_WHITE);
            settings.setForeground(Color.BLACK);
            help.setForeground(Color.BLACK);

        }
        if (e.getSource() == darkTheme) {
            fieldNickname.setBackground(Color.DARK_GRAY);
            fieldNickname.setForeground(Color.WHITE);

            label.setBackground(Color.GRAY);
            label.setForeground(Color.BLACK);

            buttonTime.setBackground(Color.LIGHT_GRAY);
            buttonTime.setForeground(Color.WHITE);

            textArea.setBackground(Color.GRAY);
            textArea.setForeground(Color.WHITE);

            fieldInput.setBackground(Color.DARK_GRAY);
            fieldInput.setForeground(Color.WHITE);

            btnEnter.setBackground(Color.LIGHT_GRAY);
            btnEnter.setForeground(Color.BLACK);

            menuBar.setBackground(Color.GRAY);
            settings.setForeground(Color.WHITE);
            help.setForeground(Color.WHITE);

        }
        if (e.getSource() == greenTheme) {
            fieldNickname.setBackground(TURQUOISE);
            fieldNickname.setForeground(Color.BLACK);

            label.setBackground(SCREAMING_GREEN);
            label.setForeground(Color.BLACK);

            buttonTime.setBackground(SEA_GREEN);
            buttonTime.setForeground(Color.BLACK);

            textArea.setBackground(WHITE_GREEN);
            textArea.setForeground(Color.BLACK);

            fieldInput.setBackground(SCREAMING_GREEN);
            fieldInput.setForeground(Color.BLACK);

            btnEnter.setBackground(SEA_GREEN);
            btnEnter.setForeground(Color.BLACK);

            menuBar.setBackground(SEA_GREEN);
            settings.setForeground(Color.BLACK);
            help.setForeground(Color.BLACK);
        }

        if (e.getSource() == timesRoman) {
            Font newFont = new Font("TimesRoman", Font.BOLD, 12);

            fieldNickname.setFont(new Font("TimesRoman", Font.ITALIC + Font.BOLD, 14));
            label.setFont(new Font("TimesRoman", Font.BOLD, 12));
            buttonTime.setFont(new Font("TimesRoman", Font.BOLD, 14));
            textArea.setFont(new Font("TimesRoman", Font.PLAIN, 14));
            fieldInput.setFont(new Font("TimesRoman", Font.BOLD, 14));
            btnEnter.setFont(new Font("TimesRoman", Font.BOLD, 14));

            settings.setFont(newFont);
            help.setFont(newFont);
            theme.setFont(newFont);
            font.setFont(newFont);
            profile.setFont(newFont);
            nickname.setFont(newFont);
            lightTheme.setFont(newFont);
            darkTheme.setFont(newFont);
            greenTheme.setFont(newFont);
            contact.setFont(newFont);
            version.setFont(newFont);
            about.setFont(newFont);
        }
        if (e.getSource() == arial) {
            Font newFont = new Font("Arial", Font.BOLD, 12);

            fieldNickname.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 14));
            label.setFont(new Font("Arial", Font.BOLD, 12));
            buttonTime.setFont(new Font("Arial", Font.BOLD, 14));
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            fieldInput.setFont(new Font("Arial", Font.BOLD, 14));
            btnEnter.setFont(new Font("Arial", Font.BOLD, 14));

            settings.setFont(newFont);
            help.setFont(newFont);
            theme.setFont(newFont);
            font.setFont(newFont);
            profile.setFont(newFont);
            nickname.setFont(newFont);
            lightTheme.setFont(newFont);
            darkTheme.setFont(newFont);
            greenTheme.setFont(newFont);
            contact.setFont(newFont);
            version.setFont(newFont);
            about.setFont(newFont);

        }
        if (e.getSource() == comicSansMs) {
            Font newFont = new Font("Comic Sans Ms", Font.BOLD, 12);

            fieldNickname.setFont(new Font("Comic Sans Ms", Font.ITALIC + Font.BOLD, 14));
            label.setFont(new Font("Comic Sans Ms", Font.BOLD, 12));
            buttonTime.setFont(new Font("Comic Sans Ms", Font.BOLD, 14));
            textArea.setFont(new Font("Comic Sans Ms", Font.PLAIN, 14));
            fieldInput.setFont(new Font("Comic Sans Ms", Font.BOLD, 14));
            btnEnter.setFont(new Font("Comic Sans Ms", Font.BOLD, 14));

            settings.setFont(newFont);
            help.setFont(newFont);
            theme.setFont(newFont);
            font.setFont(newFont);
            profile.setFont(newFont);
            nickname.setFont(newFont);
            lightTheme.setFont(newFont);
            darkTheme.setFont(newFont);
            greenTheme.setFont(newFont);
            contact.setFont(newFont);
            version.setFont(newFont);
            about.setFont(newFont);

        }
        if (e.getSource() == mvBoli) {
            Font newFont = new Font("MV Boli", Font.BOLD, 12);

            fieldNickname.setFont(new Font("MV Boli", Font.ITALIC + Font.BOLD, 14));
            label.setFont(new Font("MV Boli", Font.BOLD, 12));
            buttonTime.setFont(new Font("MV Boli", Font.BOLD, 14));
            textArea.setFont(new Font("MV Boli", Font.PLAIN, 14));
            fieldInput.setFont(new Font("MV Boli", Font.BOLD, 14));
            btnEnter.setFont(new Font("MV Boli", Font.BOLD, 14));

            settings.setFont(newFont);
            help.setFont(newFont);
            theme.setFont(newFont);
            font.setFont(newFont);
            profile.setFont(newFont);
            nickname.setFont(newFont);
            lightTheme.setFont(newFont);
            darkTheme.setFont(newFont);
            greenTheme.setFont(newFont);
            contact.setFont(newFont);
            version.setFont(newFont);
            about.setFont(newFont);
        }
        if (e.getSource() == lucidaConsole) {
            Font newFont = new Font("Lucida Console", Font.BOLD, 12);

            fieldNickname.setFont(new Font("Lucida Console", Font.ITALIC + Font.BOLD, 14));
            label.setFont(new Font("Lucida Console", Font.BOLD, 12));
            buttonTime.setFont(new Font("Lucida Console", Font.BOLD, 14));
            textArea.setFont(new Font("Lucida Console", Font.PLAIN, 14));
            fieldInput.setFont(new Font("Lucida Console", Font.BOLD, 14));
            btnEnter.setFont(new Font("Lucida Console", Font.BOLD, 14));

            settings.setFont(newFont);
            help.setFont(newFont);
            theme.setFont(newFont);
            font.setFont(newFont);
            profile.setFont(newFont);
            nickname.setFont(newFont);
            lightTheme.setFont(newFont);
            darkTheme.setFont(newFont);
            greenTheme.setFont(newFont);
            contact.setFont(newFont);
            version.setFont(newFont);
            about.setFont(newFont);
        }

        if (e.getSource() == contact) {
            String message = "Developer: Sofya Prokopovich"
                            + "\nEmail: sofya0804@gmail.com"
                            + "\nPhone: +375298420880";
            JOptionPane.showMessageDialog(frame, message, "Contacts", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == version) {
            String message = "Version: 1.3"
                            + "\nDevelopment data: 01.05.2020";
            JOptionPane.showMessageDialog(frame, message, "Version", JOptionPane.INFORMATION_MESSAGE);
        }
        if (e.getSource() == about) {
            String mesage = "This network chat was developed by me as a course project "
                            + "in the discipline of 'Computer system and networks'."
                            + "\nThis chat provides features such as messaging with "
                            + "other users, changing themes and fonts."
                            + "\nIt is planned to further develop the chat and add new features.";
            JOptionPane.showMessageDialog(frame, mesage, "About", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addMenuBar() {
        settings = new JMenu("Settings");
            theme = new JMenu("Theme");
                lightTheme = new JMenuItem("Light");
                darkTheme = new JMenuItem("Dark");
                greenTheme = new JMenuItem("Green");
            font = new JMenu("Font");
                timesRoman = new JMenuItem("TimesRoman");
                arial = new JMenuItem("Arial");
                comicSansMs = new JMenuItem("Comic Sans Ms");
                mvBoli = new JMenuItem("MV Boli");
                lucidaConsole = new JMenuItem("Lucida Console");
            profile = new JMenu("Profile");
                nickname = new JMenuItem("Nickname");
        help = new JMenu("Help");
            contact = new JMenuItem("Contacts");
            version = new JMenuItem("Version");
            about = new JMenuItem("About");

        menuBar = new JMenuBar();

        nickname.addActionListener(this);

        lightTheme.addActionListener(this);
        darkTheme.addActionListener(this);
        greenTheme.addActionListener(this);

        timesRoman.addActionListener(this);
        arial.addActionListener(this);
        comicSansMs.addActionListener(this);
        mvBoli.addActionListener(this);
        lucidaConsole.addActionListener(this);

        contact.addActionListener(this);
        version.addActionListener(this);
        about.addActionListener(this);

        theme.add(lightTheme); theme.add(darkTheme); theme.add(greenTheme);
        font.add(timesRoman); font.add(arial); font.add(comicSansMs);
        font.add(mvBoli); font.add(lucidaConsole);
        profile.add(nickname);
        settings.add(theme); settings.add(font); settings.add(profile);
        help.add(contact); help.add(version); help.add(about);
        menuBar.add(settings); menuBar.add(help);
        frame.setJMenuBar(menuBar);

        timesRoman.setFont(new Font("TimesRoman", Font.BOLD, 12));
        arial.setFont(new Font("Arial", Font.BOLD, 12));
        comicSansMs.setFont(new Font("Comic Sans Ms", Font.BOLD, 12));
        mvBoli.setFont(new Font("MV Boli", Font.BOLD, 12));
        lucidaConsole.setFont(new Font("Lucida Console", Font.BOLD, 12));
    }

    private void addElements() {
        GridBagConstraints gbc = new GridBagConstraints();

        // добавление поля с никнеймом
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 30;
        frame.add(fieldNickname, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(label, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        frame.add(buttonTime, gbc);

        // добавление главного текстового поля
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 400;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        frame.add(textArea, gbc);

        // поле для ввода сообщений
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipady = 40;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.PAGE_END;
        frame.add(fieldInput, gbc);

        // кнопка "Enter"
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 35;
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        frame.add(btnEnter, gbc);
    }

    private void defaultTheme() {
        fieldNickname.setBackground(FOREST_WOLF);
        fieldNickname.setForeground(Color.BLACK);
        fieldNickname.setFont(new Font("TimesRoman", Font.ITALIC + Font.BOLD, 14));

        label.setBackground(Color.WHITE);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("TimesRoman", Font.BOLD, 12));

        buttonTime.setBackground(Color.WHITE);
        buttonTime.setForeground(Color.DARK_GRAY);
        buttonTime.setFont(new Font("TimesRoman", Font.BOLD, 14));

        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.BLACK);
        textArea.setFont(new Font("TimesRoman", Font.PLAIN, 14));

        fieldInput.setBackground(FOREST_WOLF);
        fieldInput.setForeground(Color.BLACK);
        fieldInput.setFont(new Font("TimesRoman", Font.BOLD, 14));

        btnEnter.setBackground(Color.WHITE);
        btnEnter.setForeground(Color.DARK_GRAY);
        btnEnter.setFont(new Font("TimesRoman", Font.BOLD, 14));

        menuBar.setBackground(SMOKY_WHITE);
    }
}
