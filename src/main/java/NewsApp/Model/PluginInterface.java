/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NewsApp.Model;

import java.util.List;

/**
 *
 * @author anders
 */
public interface PluginInterface {
    //Time intervals between tasks
    public long getTime();
    //Return headlines
    public List<String> getTitles();
    //return website
    public String getWebsite();
}
