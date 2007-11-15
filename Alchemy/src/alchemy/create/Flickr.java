package alchemy.create;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
//import javax.swing.Icon;
//import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Return an Icon for the first Flickr photo that matches a query string.
 * Typical usage:
 * <pre>
 * Icon image = Flickr.getInstance().search("face");
 * myLabel.setIcon(image);
 * </pre>
 *
 */

public class Flickr {
    
    private static Flickr theInstance = null;
    private final Logger logger;
    private final DocumentBuilder xmlParser;
    
   /* URL format string that specifies a single "medium" sized photo on
    * the Flickr server.  Based on the URL syntax documented here:
    * http://www.flickr.com/services/api/misc.urls.html, i.e.
    * http://farm{farm-id}.static.flickr.com/{server-id}/{id}_{secret}_[mstb].jpg"
    */
    private final String photoURLFormat =
            "http://farm%s.static.flickr.com/%s/%s_%s.jpg";
    
   /* An HTTP get format string for looking up a single "Photo" that matches
    * a query string.  This request is documented on the Yahoo/Flickr
    * site here: http://www.flickr.com/services/api/flickr.photos.search.html
    */
    private final String searchMethodFormat =
            "http://www.flickr.com/services/rest/?method=flickr.photos.search" +
            "&format=rest" +
            "&api_key=3f44e4e680a2a1b89af1b4bb803057ac" +
            "&per_page=1" +        // just send one match back
            //"&sort=interestingness-desc" +
            "&sort=date-posted-asc" +
            "&page=%s" +
            "&text=%s";
    
    public Flickr() throws ParserConfigurationException {
        
        logger = Logger.getLogger(Flickr.class.getName());
        DocumentBuilderFactory dcb = DocumentBuilderFactory.newInstance();
        this.xmlParser = dcb.newDocumentBuilder();
    }
    
    public static Flickr getInstance() {
        if (theInstance == null) {
            try {
                theInstance = new Flickr();
            } catch (ParserConfigurationException e) {
                throw new Error("fatal error", e);
            }
        }
        return theInstance;
    }
    
    private URL newURL(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            logger.log(Level.WARNING, String.format("bad URL \"%s\"", s), e);
            return null;
        }
    }
    
    private Document getPage(URL url) {
        Document doc = null;
        try {
            doc = xmlParser.parse(url.toString());
        } catch (SAXException e) {
            logger.log(Level.WARNING, String.format("can't parse value of  URL \"%s\"", url), e);
        } catch (IOException e) {
            logger.log(Level.WARNING, String.format("can't load value of  URL \"%s\"", url), e);
        }
        return doc;
    }
    
    private List<Element> elementsWithTag(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);
        if ((nodes != null) && (nodes.getLength() > 0)) {
            List<Element> elements = new ArrayList<Element>(nodes.getLength());
            for(int i = 0; i < nodes.getLength(); i++) {
                elements.add((Element)(nodes.item(i)));
            }
            return elements;
        } else {
            logger.warning(String.format("no elements with tag \"%s\" at \"%s\"", tag, doc.getDocumentURI()));
            return null;
        }
    }
    
    private String elementAttribute(Element elt, String attribute) {
        String s = elt.getAttribute(attribute);
        return (s.length() == 0) ? null : s;
    }
    
    
    public Image search(String keyword, String page) {
        URL searchURL = newURL(String.format(searchMethodFormat, page, keyword));
        if (searchURL == null) {
            return null;
        }
        System.out.println(searchURL.toString());
        Document doc = getPage(searchURL);
        if (doc == null) {
            return null;
        }
        List<Element> elts = elementsWithTag(doc, "photo");
        if (elts == null) {
            return null;
        }
        Element elt = elts.get(0);
        String farm = elementAttribute(elt, "farm");
        String server = elementAttribute(elt, "server");
        String id = elementAttribute(elt, "id");
        String secret = elementAttribute(elt, "secret");
        //System.out.println(secret);
        Image image = null;
        if ((farm != null) && (server != null) && (id != null) && (secret != null)) {
            URL imageURL = newURL(String.format(photoURLFormat, farm, server, id, secret));
            System.out.println(imageURL.toString());
            if (imageURL != null) {
                try {
                    image = ImageIO.read(imageURL);
                    //image = ImageIO.read(new URL("http://www.darcy.co.nz/wp/wp-content/thumb-cache/9bf22e5c926b8f72cdd0d83c1449a967.jpg"));
                    System.out.println("read image from imageURL");
                } catch (IOException e) {
                    //System.out.println("Error");
                    logger.log(Level.WARNING, String.format("couldn't load \"%s\"", imageURL), e);
                }
            }
        }
        
        //return image != null ? new ImageIcon(image) : null;
        //return image;
        return image != null ? image : null;
    }
}
