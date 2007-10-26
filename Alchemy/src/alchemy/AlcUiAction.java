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
    
    public void sendEvent(Object root) {
        ActionEvent guiEvent = new ActionEvent(source, id, command);
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
