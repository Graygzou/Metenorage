package Engine.System.Component.Messaging;

import Engine.GameEngine;
import Engine.System.Component.Component;

import java.util.ArrayDeque;

/**
 * @author Florian VIDAL <florianvidals@gmail.com>
 * @author Gregoire Boiron <gregoire.boiron@gmail.com>
 */

public class MessageQueue {

    private ArrayDeque<Message> queue;

    /**
     * The message queue will gather all the messages sent by the components and dispatch them to the correct receiver
     */
    public MessageQueue() {
        this.queue = new ArrayDeque<>();
    }

    /**
     * Add a message to the end of the queue
     * @param message message to be delivered to another component
     */
    public void add(Message message){
        this.queue.addLast(message);
    }

    /**
     * Dispatch all the messages in the queue at that time. The queue follows a FIFO behaviour.
     */
    public void dispatch(){
        while (!this.queue.isEmpty()){
            // Remove the element from the queue
            Message message = (Message)this.queue.poll();
            int receiverID = message.getReceiver();
            // Find the component that have this ID
            Component receiver = GameEngine.componentManager.getComponentFromID(receiverID);
            if (receiver != null){
                receiver.onMessage(message);
            }
            message = null;
        }
    }
}
