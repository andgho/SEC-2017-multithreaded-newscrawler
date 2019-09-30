/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NewsApp;

import NewsApp.Controller.NewsFeedController;
import NewsApp.View.GUI;

/**
 *
 * @author anders
 */
public class NewsApp {
    public static void main(String[] args) {
        
        //hello works as a replacement to args for testing purposes.
        //String[] hello = new String[]{"Bbc.jar", "Arstechnica.jar", "Nytimes.jar"};
        
        //Creating the GUI and NewsFeedController
        GUI gui = new GUI();
        NewsFeedController nfc = new NewsFeedController(gui, args);
    }

}
