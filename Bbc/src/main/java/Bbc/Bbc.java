import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import NewsApp.Model.PluginInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anders
 */
public class Bbc implements PluginInterface{
    
    public Bbc() throws MalformedURLException{
    }
    //Regex pattern for all three websites. 
    //private static final Pattern TITLE_REGEX = Pattern.compile("<h1 class=\"heading\"><a.*?>(.*?)<\\/a><\\/h1>"); //ARS
    private static final Pattern TITLE_REGEX = Pattern.compile("<h3.*?promo-heading__title.*?>(.*?)<\\/h3>"); //BBC
    //private static final Pattern TITLE_REGEX = Pattern.compile("<h2 class=\"story-heading\"><a.*?>(.*?)<\\/a><\\/h2>"); //NYT
        
    //Method that returns the titles.
    //Starts off by downloading all of the HTML, then it parses the patterns
    //with the regex and then tries to replace a few encoding stuff. Some of them
    //get through.
    @Override
    public List<String> getTitles(){
        //String that will store titles
        String s = "";
        
        URL url = null;
        try 
        {
            //url = new URL("https://arstechnica.com/"); // Download source
            url = new URL("http://www.bbc.com/news"); // Download source
            //url = new URL("https://www.nytimes.com/"); // Download source
        } 
        catch (MalformedURLException ex) 
        {
            Logger.getLogger(Bbc.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Download the websites html and stuff
        try (ReadableByteChannel chan = Channels.newChannel(url.openStream())) {

            ByteBuffer buf = ByteBuffer.allocate(65536);
            byte[] array = buf.array();
            
            int bytesRead = chan.read(buf); // Read a chunk of data.
            while (bytesRead != -1) {
                //Put chunks into string
                s +=  new String(array);
                buf.clear();
                bytesRead = chan.read(buf);
            }
 
        } 
        catch (ClosedByInterruptException e) 
        {
            System.out.println("Closed");
        } // Thread.interrupt().
        
        catch (IOException e) {
            System.out.println("IO");
        } // An error
        
        List<String> headlines = parseTitles(s);
        for(String item : headlines){
            //Replace some of the encoding stuff
            item = item.replace("&#x27;", "'");
            item = item.replace("&amp;", "&");
            item = item.replace("<em>", "");
            item = item.replace("</em>", "");
            item = item.replace("<br>", "");
        }
        return headlines;
    }
    //The parser
    private static List<String> parseTitles(final String str) {
        final List<String> titles = new ArrayList<String>();
        final Matcher matcher = TITLE_REGEX.matcher(str);
        while (matcher.find()) {
            titles.add(matcher.group(1));
        }
        
        return titles;
    } 
    //Interval time for threads
    @Override
    public long getTime() {
        return 76;
    }

    @Override
    public String getWebsite() {
        return "www.BBC.com/news";
    }
}
