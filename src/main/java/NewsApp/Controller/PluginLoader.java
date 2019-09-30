/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NewsApp.Controller;

import NewsApp.Model.PluginInterface;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anders
 */
public class PluginLoader {

    private List<ScheduledFuture<PluginInterface>> activePlugins;
    private ScheduledThreadPoolExecutor executor;
    private NewsFeedController nfc;
    private Map<String, Boolean> arePluginsAlive;
    
    public PluginLoader(NewsFeedController nfcin) throws MalformedURLException, ClassNotFoundException {
        //Threadpool size of 10
        executor = new ScheduledThreadPoolExecutor(10);
        activePlugins = Collections.synchronizedList(new ArrayList());
        nfc = nfcin;
        arePluginsAlive = Collections.synchronizedMap(new HashMap());
    }

    //This function loads the plugin jarfiles and returns them in a linked list
    public LinkedList<PluginInterface> loadPlugins(String[] plugins) throws ClassNotFoundException{
        LinkedList<PluginInterface> loadedPlugins = new LinkedList();
        for (String item : plugins) {
            try {
                String cls = "";
                JarFile jar = new JarFile(item);
                Enumeration comps = jar.entries();
                //Start off by getting the jar file plugins
                while (comps.hasMoreElements()) {
                    JarEntry newJar = (JarEntry) comps.nextElement();
                    if (newJar.getName().endsWith(".class")) {
                        cls = newJar.getName();
                    }
                }
                //Load them as classes
                File file = new File(System.getProperty("user.dir") + "/" + item);
                URLClassLoader cloader = new URLClassLoader(new URL[]{file.toURI().toURL()});
                Class newClass = Class.forName(cls.replace(".class", ""), true, cloader);
                PluginInterface newp = (PluginInterface) newClass.newInstance();
                loadedPlugins.add(newp);
            }
             catch (InstantiationException ex) {
                Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
            }catch (IOException ex){
                Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return loadedPlugins;
    }
    //Initiates the plugins upon starting the program. Is called in NewsFeedController's constructor
    public void initPlugins(LinkedList<PluginInterface> pl){
        for(PluginInterface item : pl){
            startPlugin(item);
        }
    }
    //Cancels the threads. Isnt properly done yet, still have to handle it in 
    //stoppedbyinterrupt errorthing in the plugins.
    //Runs when cancel button is pressed
    public void cancelButton(){
        for(ScheduledFuture<PluginInterface> activePlugin : activePlugins){
            activePlugin.cancel(true);
        }
        activePlugins.clear();
    }
    //Schedules the plugins to run now. Runs when update button is pressed
    public void updateButton(){
        for(ScheduledFuture<PluginInterface> item : activePlugins){
            //try {
                //if(!arePluginsAlive.get(item.get().getWebsite())){
                    executor.schedule((Runnable) item, 0, TimeUnit.DAYS);
                    //System.out.println(item.get().getWebsite());
                //}
            //} catch (InterruptedException | ExecutionException ex) {
            //    Logger.getLogger(PluginLoader.class.getName()).log(Level.SEVERE, null, ex);
            //}
        }
    }
    
    //Starts a plugin if it is not already running
    public void startPlugin(PluginInterface plugin) {
        Runnable updatePlugin = () -> {
            //First time run? 
            if(!arePluginsAlive.containsKey(plugin.getWebsite())){
                arePluginsAlive.put(plugin.getWebsite(), Boolean.FALSE);
            }
            //Is it running already?
            if(arePluginsAlive.containsValue(false)){
                nfc.startDownloads(plugin.getWebsite());
                arePluginsAlive.put(plugin.getWebsite(), Boolean.TRUE);
                
                List<String> titleList = plugin.getTitles();
                nfc.addTitles(plugin.getWebsite(), titleList);
                
                arePluginsAlive.put(plugin.getWebsite(), Boolean.FALSE);
                nfc.stopDownloads(plugin.getWebsite());
            }
            
        };

        activePlugins.add((ScheduledFuture<PluginInterface>) executor.scheduleAtFixedRate(updatePlugin, 0, plugin.getTime(), TimeUnit.SECONDS));
    }
}
