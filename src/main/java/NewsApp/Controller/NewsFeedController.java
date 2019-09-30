/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NewsApp.Controller;

import NewsApp.Model.PluginInterface;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import NewsApp.View.GUI;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author anders
 */
public class NewsFeedController{

    private GUI gui;
    private LinkedList<PluginInterface> loadedPlugins = null;
    private List<String> headlineList = null;
    private PluginLoader pl;

    //The constructor starts the clock and creates an instance of PluginLoader.
    //It then calls a function that loads the plugins from the PluginLoader class.
    //It also takes the actionevents from the two buttons "cancel" and "update"
    public NewsFeedController(GUI g, String[] args) {
        startClock(g);
        pl = null;
        headlineList = Collections.synchronizedList(new ArrayList());
        try {
            pl = new PluginLoader(this);
            try {
                loadedPlugins = pl.loadPlugins(args);
                pl.initPlugins(loadedPlugins);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(NewsFeedController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(NewsFeedController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NewsFeedController.class.getName()).log(Level.SEVERE, null, ex);
        }

        gui = g;
        //PluginLoader pluginLoader = pl;
        if(gui.getUpdate() == null){
            System.out.println("DETTE ER PROBLEMET");
        }
        gui.getUpdate().addActionListener((ActionEvent e) -> {
            pl.updateButton();
        });
        gui.getCancel().addActionListener((ActionEvent e) -> {
            pl.cancelButton();
        });
    }

    public void startClock(GUI gui) {
        //Lambda clock
        ////////////////////////////////////////////////////////////////////////////
        Thread clockThread = new Thread(()
                -> {
            while (true) {                                                 //define thread task
                gui.updateClock(new Date().toString());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Thread failed");
                }

            }
        }
        );
        clockThread.start();;
        ////////////////////////////////////////////////////////////////////////////
    }

    //The addTitles functioneceives the titles from the plugins and
    //checks if they've already been added. It also checks the length to prevent
    //faulty html to show long strings
    public void addTitles(String website, List<String> titles) {
        SwingUtilities.invokeLater(() -> {
            for (String item : titles) {
                if(!headlineList.contains(item) && item.length() < 100){
                    Date date = new Date();
                    gui.getListModel().add(0, website + ": " + item + " (" + date.toString() + ")");
                    headlineList.add(item);
                }
            }
        });
    }
    
    //The start and stop Downloads are called when a thread starts and ends respectively.
    //They're there to show in the gui when the plugins are downloading.
    public void startDownloads(String website) {
        SwingUtilities.invokeLater(() -> {
            gui.getDownloadModel().add(0, website);
        }
        );

    }
    public void stopDownloads(String website) {
        SwingUtilities.invokeLater(() -> {
            gui.getDownloadModel().removeElement(website);
        }
        );
    }
    
    
}
