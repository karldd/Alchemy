package alchemy;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

public class AlcAction {
    
    String command;
    AlcObject source;
    Method actionEventMethod;
    
    public AlcAction(AlcObject s, String n) {
        source = s;
        command = n;
    }
    
    public void sendEvent(Object root) {
        ActionEvent guiEvent = new ActionEvent(source, ActionEvent.ACTION_PERFORMED, command);
        try {
            actionEventMethod = root.getClass().getMethod("actionPerformed", new Class[] {
                guiEvent.getClass()
            });
            actionEventMethod.invoke(root, new Object[] {
                guiEvent
            });
        } catch(Exception e) {
            System.out.println("No method named actionPerformed was found in root.");
        }
    }
    
}
