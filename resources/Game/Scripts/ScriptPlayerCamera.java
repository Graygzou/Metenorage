import Engine.Main.Entity;
import Engine.Main.Material;
import Engine.System.Component.Transform;
import Engine.System.Graphics.Component.Mesh3D;
import Engine.System.Scripting.BaseScript;
import Engine.System.Scripting.Callback;
import Engine.System.Sound.Component.Source;

import java.util.List;

/**
 * @author Grégoire Boiron
 */
public class ScriptPlayerCamera extends BaseScript {

    public void awake() {

        // Get the Component we're interested in
        List<Integer> componentIDs = getComponents(Transform.class);

        //callMethodComponent(componentIDs.get(0), "setLooping", true);

        Callback callback = new Callback() {
            @Override
            public void call(Object result) {
            if(!(boolean)result) {
                // Call a specific method on this component with his id.
                callMethodComponent(componentIDs.get(0), "play", null);
            }
            }
        };

        callReturnMethodComponent(componentIDs.get(0), "isPlaying", null, callback);

        List<Integer> componentIDs2 = getComponents(Mesh3D.class);

        callMethodComponent(componentIDs2.get(0), "setMaterial", new Material("/Game/Textures/grassblock.png", 1f));

    }

    public void start() {
        System.out.println("Methode Start called !!");
    }

    public void update() {
        //System.out.println("Methode Update called !!");
    }

}
