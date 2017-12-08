package Game.Scripts;

import Engine.Main.Entity;
import Engine.System.Component.Component;
import Engine.System.Scripting.Component.BaseScript;
import Engine.System.Sound.Component.Source;

import java.util.List;

/*
 * @author Grégoire Boiron
 */
public class ScriptTest extends BaseScript {

    public ScriptTest(Entity entity) {
        super(entity);
    }

    public void awake() {
        //getComponent(Source).play();
        List<Component> l = this.getEntity().getComponents();
        for(Component comp : l) {
            if(comp instanceof Source) {
                Source s = (Source) comp;
                s.play();
            }
        }
    }

    public void start() {
        System.out.println("Methode Start called !!");
    }

    public void update() {
        //System.out.println("Methode Update called !!");
    }

}
