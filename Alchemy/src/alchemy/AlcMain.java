package alchemy;

import alchemy.ui.AlcUi;

//import processing.core.*;
//import processing.pdf.*;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import java.awt.geom.GeneralPath;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.Point;

import java.net.URI;
import java.net.URISyntaxException;

// JAVA PLUGIN FRAMEWORK
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;
import org.java.plugin.PluginClassLoader;

public class AlcMain extends JFrame implements MouseMotionListener, MouseListener {
    
    public AlcMain() {
        
        JPanel content = new JPanel();              // Create content panel.
        content.setLayout(new BorderLayout());
        content.addMouseListener(this);
        content.addMouseMotionListener(this);
        
        canvas = new AlcCanvas();
        content.add(canvas, BorderLayout.CENTER);  // Put in expandable center.
        
        this.setContentPane(content);
        //this.setTitle("al.chemy");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();                                // Finalize window layout
        this.setLocationRelativeTo(null);           // Center window on screen.
        
        // set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        loadPlugins();
        
        if(numberOfPlugins > 0){
            addPlugins();
        }
        
        /*
        for (Iterator<AlcModule> it = creates.iterator(); it.hasNext(); ) {
            AlcModule a = it.next();
            println(a.getName());
        }
         
        for (Iterator<AlcModule> it = affects.iterator(); it.hasNext(); ) {
            AlcModule a = it.next();
            println(a.getName());
        }
         */
        
        shapes = new ArrayList<AlcShape>(100);
        shapes.ensureCapacity(100);
        
    }
    
    public static void main(String[] args) {
        //PApplet.main( new String[]{"alchemy.AlcMain"} );
        AlcMain window = new AlcMain();
        window.setVisible(true);
    }
    
    public void setup(){
    }
    
    public void draw(){
    }
    
    private void loadPlugins() {
        
        pluginManager = ObjectFactory.newInstance().createManager();
        
        // Folder of the plugins
        File pluginsDir = new File("plugins");
        
        // Get all plugins that end with .zip
        File[] plugins = pluginsDir.listFiles(new FilenameFilter() {
            
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".zip");
            }
            
        });
        
        try {
            
            PluginLocation[] locations = new PluginLocation[plugins.length];
            
            // Number of plugins minus one for the core plugin
            numberOfPlugins = plugins.length-1;
            
            for (int i = 0; i < plugins.length; i++) {
                locations[i] = StandardPluginLocation.create(plugins[i]);
                //println(plugins[i].getAbsolutePath());
            }
            
            // Registers plug-ins and their locations with this plug-in manager.
            pluginManager.publishPlugins(locations);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    private void addPlugins() {
        
        creates = addExtensionPoint("Create");
        affects = addExtensionPoint("Affect");
        
    }
    
    private ArrayList addExtensionPoint(String pointName){
        
        ArrayList<AlcModule> plugins = new ArrayList<AlcModule>();
        
        try{
            PluginDescriptor core = pluginManager.getRegistry().getPluginDescriptor("alchemy.core");
            
            ExtensionPoint point = pluginManager.getRegistry().getExtensionPoint(core.getId(), pointName);
            
            for (Iterator<Extension> it = point.getConnectedExtensions().iterator(); it.hasNext();) {
                
                Extension ext = it.next();
                PluginDescriptor descr = ext.getDeclaringPluginDescriptor();
                pluginManager.activatePlugin(descr.getId());
                
                ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());
                
                plugins.add( (AlcModule)pluginCls.newInstance() );
                AlcModule currentPlugin = plugins.get(plugins.size()-1);
                
                // GET THE FILE PATH & ICON NAME
                // Return the path of the XML file as a string
                String path = descr.getLocation().getPath();
                // Remove the XML file name after the "!" mark and make a URI
                URI pathUri = new URI(path.substring(0, path.lastIndexOf("!")));
                // Convert it into an abstract file name
                File pathFile = new File(pathUri);
                
                if(pathFile.exists()){
                    currentPlugin.setPluginPath(pathFile);
                    System.out.println("Loaded " + pathFile.getPath());
                }
                
                // Set the icon name and the decription name from the XML
                String descriptionParam = ext.getParameter("description").valueAsString();
                String iconParam = ext.getParameter("icon").valueAsString();
                String nameParam = ext.getParameter("name").valueAsString();
                
                /*
                // Get the ID
                String name = descr.getId();
                name = name.substring(name.lastIndexOf(".")+1);
                println(name);
                 */
                
                currentPlugin.setName(nameParam);
                currentPlugin.setIconName(iconParam);
                currentPlugin.setDescriptionName(descriptionParam);
                currentPlugin.setId(plugins.size()-1);
                
                //textFont(font);
                //text(modules.getName(), 15, 60, -30);
                //println(modules.getName());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return plugins;
    }
    
    public void mouseMoved(MouseEvent e)    { }
    
    public void mousePressed(MouseEvent e)  {
        
        // Create a new shape
        shapes.add( new AlcShape(e.getPoint()) );
        
        //System.out.println("New Shape");
        
    }
    
    public void mouseClicked(MouseEvent e)  { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseExited(MouseEvent e)   { }
    public void mouseReleased(MouseEvent e) { }
    
    public void mouseDragged(MouseEvent e)  {
        
        // Add points to the shape
        (shapes.get(shapes.size()-1)).drag(e.getPoint());
        
        // Do something here to change the shape
        //System.out.println("Drag");
        
        // Pass the shapes to the canvas to be drawn
        canvas.draw(shapes);
        
    }
    
    public void openFileDialog(){
        // create a file chooser
        final JFileChooser fc = new JFileChooser();
        
        // in response to a button click:
        int returnVal = fc.showSaveDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            pdfURL = file.getPath();
            
            saveOneFrame = true;
            //redraw();
            //println(pdfURL);
            
        } else {
            //println("Open command cancelled by user.");
        }
    }
    
    
    // platform IDs
    static final int WINDOWS = 1;
    static final int MACOSX  = 3;
    static final int LINUX   = 4;
    static final int OTHER   = 0;
    
    
    /**
     * Full name of the Java version (i.e. 1.5.0_11).
     */
    public static final String javaVersionName = System.getProperty("java.version");
    
    /**
     * Version of Java that's in use, whether 1.1 or 1.3 or whatever,
     * stored as a float.
     */
    public static final float javaVersion = new Float(javaVersionName.substring(0, 3)).floatValue();
    
    /**
     * Current platform in use.
     * <P>
     * Equivalent to System.getProperty("os.name"), just used internally.
     */
    static public String platformName =
            System.getProperty("os.name");
    
    /**
     * Current platform in use, one of the
     * PConstants WINDOWS, MACOSX, LINUX or OTHER.
     */
    static public int platform;
    
    static {
        if (platformName.indexOf("Mac") != -1) {
            platform = MACOSX;
            
        } else if (platformName.indexOf("Windows") != -1) {
            platform = WINDOWS;
            
        } else if (platformName.equals("Linux")) {  // true for the ibm vm
            platform = LINUX;
            
        } else {
            platform = OTHER;
        }
    }
    
    /**
     * Modifier flags for the shortcut key used to trigger menus.
     * (Cmd on Mac OS X, Ctrl on Linux and Windows)
     */
    static public final int MENU_SHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    
    AlcCanvas canvas;
    
    // PLUGIN
    private PluginManager pluginManager;
    ArrayList<AlcModule> creates = new ArrayList<AlcModule>(10);
    ArrayList<AlcModule> affects = new ArrayList<AlcModule>(10);
    int numberOfPlugins;
    
    // PDF
    boolean saveOneFrame = false;
    String pdfURL;
    
    // SHAPES
    ArrayList<AlcShape> shapes;
    
    boolean firstLoad = true;
    boolean inToolBar = false;
    
}
