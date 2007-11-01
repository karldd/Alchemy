package alchemy;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

public class AlcUiAction {
    
    String command;
    AlcUiObject source;
    Method actionEventMethod;
    int id;
    
    public AlcUiAction(AlcUiObject s, int i, String n) {
        source = s;
        id = i;
        command = n;
    }
    
    public void sendEvent(Object caller) {
        ActionEvent guiEvent = new ActionEvent(source, id, command);
        
        try {
            actionEventMethod = caller.getClass().getMethod("actionPerformed", new Class[] {
                guiEvent.getClass()
            });
            actionEventMethod.invoke(caller, new Object[] {
                guiEvent
            });
        } catch(Exception e) {
            System.out.println("No method named actionPerformed was found in root.");
        }
    }
    
}

/*
 Adding Your Own Library Events

So that your library can notify the host applet that something 
interesting has happened, this is how you implement an event 
method in the style of serialEvent, serverEvent, etc.


public class FancyLibrary {
  Method fancyEventMethod;

  public YourLibrary(PApplet parent) {
    // your library init code here...

    // check to see if the host applet implements
    // public void fancyEvent(FancyLibrary f)
    try {
      fancyEventMethod =
        parent.getClass().getMethod("fancyEvent",
                                    new Class[] { FancyLibrary.class });
    } catch (Exception e) {
      // no such method, or an error.. which is fine, just ignore
    }
  }

  // then later, to fire that event
  public void makeEvent() {
    if (fancyEventMethod != null) {
    try {
      fancyEventMethod.invoke(parent, new Object[] { this });
    } catch (Exception e) {
      System.err.println("Disabling fancyEvent() for " + name +
                         " because of an error.");
      e.printStackTrace();
      fancyEventMethod = null;
    }
  }
}
 */