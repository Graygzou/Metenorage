package Engine.System.Physics.Component;

import Engine.GameEngine;
import Engine.Main.Entity;
import Engine.System.Component.Messaging.Message;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;

import javax.vecmath.Vector3f;

/*
 * @author Matthieu Le Boucher <matt.leboucher@gmail.com>
 * @author Noemy Artigouha
 */

public class BoxRigidBodyComponent extends RigidBodyComponent {
    public BoxRigidBodyComponent(Entity entity, float mass, float dx, float dy, float dz) {
        super(entity, mass);

        this.collisionShape = new BoxShape(new Vector3f(dx, dy, dz));
    }

    public void apply() {

    }

    @Override
    public void onMessage(Message message) {
        Message<Object[]> returnMessage = null;
        switch (message.getInstruction()) {
            case "initialize":
                initialize();
                break;
            case "translate":
                this.getRigidBody().translate((Vector3f)message.getData());
                break;
            case "getRigidbody":
                Object[] returnRigidbody = {RigidBody.class, this.getRigidBody()};
                returnMessage =
                        new Message<>(getID(), message.getSender(), "return", returnRigidbody);
                GameEngine.messageQueue.add(returnMessage);
                break;
            case "detectCollision":
                Object[] returnBool = {Boolean.class, this.getRigidBody().checkCollideWith((RigidBody)message.getData())};
                returnMessage =
                        new Message<>(getID(), message.getSender(), "return", returnBool);
                GameEngine.messageQueue.add(returnMessage);
                break;
            default:
                System.out.println(message.getInstruction() + ": Corresponding method can't be found");
                break;
        }
    }

    @Override
    public void initialize() {
        super.initialize();
    }
}
