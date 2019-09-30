/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NewsApp.View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import NewsApp.Controller.PluginLoader;
import NewsApp.Controller.NewsFeedController;

/**
 *
 * @author anders
 */
public class GUI extends JFrame {
        
    public GUI() {
        //The GUI was implemented quickly and not treated much
        //It has a few getters and setters for buttons, lists and the clock
        //Other than that pretty much just contains a bunch of Swing stuff slapped
        //into the constructor. Uses invokeLater as it will be affected by multiple
        //threads at the same time.
        
        //Buttons outside of invokeLater to avoid race condition on getters for them.
        update = new JButton();
        cancel = new JButton();
        SwingUtilities.invokeLater(()->{
            
        JPanel background = new JPanel();
        background.setBackground(Color.red);
        Container bg2 = new Container();
        bg2.setLayout(new BorderLayout());
        bg2.setBackground(Color.red);
        background.setLayout(null);
        background.add(bg2);
        listModel = new DefaultListModel();
        downloadModel = new DefaultListModel();
        clock = new JLabel();
        clock.setText(new Date().toString());
        
        
        JList list = new JList(listModel);
        JList downloadList = new JList(downloadModel);
        
        
        cancel.setText("Cancel");
        update.setText("Update");
        JPanel bg3 = new JPanel();
        bg3.add(update);
        bg3.add(cancel);
        bg3.add(clock);
        bg3.setBackground(Color.red);
        
        bg2.add(bg3, BorderLayout.NORTH);
        bg2.add(new JScrollPane(list), BorderLayout.CENTER);
        bg2.add(new JScrollPane(downloadList), BorderLayout.SOUTH);

        
        getContentPane().add(bg2);
        setSize(new Dimension(720, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("supa dupa news");
        setVisible(true);
        
        });
    }
    private PluginLoader pl;
    private NewsFeedController nfc;
    private JLabel clock;
    private JButton cancel;

    public JButton getCancel() {
        return cancel;
    }

    public JButton getUpdate() {
        return update;
    }
    private JButton update;
    private DefaultListModel listModel;
    private DefaultListModel downloadModel;

    public DefaultListModel getListModel() {
        return listModel;
    }

    public DefaultListModel getDownloadModel() {
        return downloadModel;
    }
    
    public void updateClock(String s){
        if(clock != null){
        clock.setText(s);
            //System.out.println(clock.getText());
        }
        else{
            //System.out.println("Det funker ikke");
        }
    }
}
