package Engine;

/*
 * @author Matthieu Le Boucher <matt.leboucher@gmail.com>
 * @author Noemy Artigouha
 */

import Engine.Helper.Timer;
import Engine.Main.Entity;
import Engine.Main.Material;
import Engine.Main.Sound;
import Engine.System.Component.Messaging.MessageQueue;
import Engine.System.Graphics.Camera;
import Engine.System.Graphics.GraphicsSystem;
import Engine.System.Input.InputSystem;
import Engine.System.Logic.LogicSystem;
import Engine.System.Sound.SoundSystem;
import Engine.System.Sound.Component.Source;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.alDeleteBuffers;

public class GameEngine implements Runnable {
    private float timePerUpdate = 1f / 50;

    private float timePerRendering = 1f / 30;

    private final Thread gameLoopThread;

    private Window window;

    private Timer timer;

    private List<Entity> entities;

    private List<Material> materials;

    private List<Sound> sounds;

    private LogicSystem logicSystem;

    private GraphicsSystem graphicsSystem;

    private InputSystem inputSystem;

    private SoundSystem soundSystem;

    public MessageQueue messageQueue;

    public GameEngine(String windowTitle, int windowWidth, int windowHeight) {
        this.gameLoopThread = new Thread(this);

        this.window = new Window(windowTitle, windowWidth, windowHeight, true);
        this.timer = new Timer();
        this.messageQueue = new MessageQueue();

        // Systems setup.
        this.logicSystem = new LogicSystem();
        this.graphicsSystem = new GraphicsSystem(this.window);
        this.inputSystem = new InputSystem(window, messageQueue);
        this.soundSystem = new SoundSystem();

        this.entities = new ArrayList<>();
        this.materials = new ArrayList<>();
        this.sounds = new ArrayList<>();
    }

    public void start() {
        gameLoopThread.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Metenorage game engine started...");

            initialize();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private void initialize() throws Exception {
        // Todo: implement this logic.
        // e.g. initialize the renderer (when we'll have one. (:).

        window.initialize();
        timer.initialize();

        for (Material material : materials) {
            material.initialize();
        }

        this.inputSystem.initialize();
        this.graphicsSystem.initialize();
        this.soundSystem.initialize();
    }

    /**
     * Delegates the input handling to the game logic.
     */
    protected void handleInput() {
        inputSystem.iterate(entities);

        /*float CAMERA_POS_STEP = 0.1f;
        graphicsSystem.getCamera().movePosition(CAMERA_POS_STEP, CAMERA_POS_STEP, 0);

        // Todo: implement this logic.
        float xIncrement = 0;
        float yIncrement = 0;
        float zIncrement = 0;
        float scaleIncrement = 0;

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            xIncrement = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            xIncrement = -1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            yIncrement = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            yIncrement = 1;
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            zIncrement = -1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            zIncrement = 1;
        } else if (window.isKeyPressed(GLFW_KEY_Z)) {
            scaleIncrement = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            scaleIncrement = 1;
        }

        for(Entity entity: entities) {
            // Update position
            Vector3f entityPosition = entity.getPosition();
            float newXPosition = entityPosition.x + yIncrement * 0.01f;
            float newYPosition = entityPosition.y + xIncrement * 0.01f;
            float newZPosition = entityPosition.z + zIncrement * 0.01f;
            entity.setPosition(newXPosition, newYPosition, newZPosition);

            // Update scale
            float scale = entity.getScale().x;
            scale += scaleIncrement * 0.05f;
            if (scale < 0) {
                scale = 0;
            }
            entity.setScale(scale);

            // Update rotation angle
            *//*
            float rotation = entity.getRotation().x + 1.5f;
            if (rotation > 360) {
                rotation = 0;
            }

            entity.setRotation(rotation, rotation, rotation);
            *//*
        }*/
    }

    /**
     * Delegates the update to the game logic.
     * @param timeStep The theoretical time step between each update.
     */
    protected void update(float timeStep) {
        // Todo: implement this logic.
        logicSystem.iterate(entities);
        messageQueue.dispatch();
    }

    /**
     * Delegates the rendering to the game logic, and then updates the window.
     */
    protected void render() {
        // Todo: implement this logic.
        window.update();
        graphicsSystem.iterate(entities);
    }

    /**
     * Delegates the control of the sounds to the sound system.
     */
    protected void playSounds() {
        soundSystem.iterate(entities);
    }

    private void cleanUp() {
        // TODO REMETTRE :
        // this.graphicsSystem.cleanUp();
        // Clean up song from the engine
        for (Sound s : this.sounds) {
            alDeleteBuffers(s.getId());
        }
        this.soundSystem.cleanUp();
    }

    /**
     * Core function of the game engine.
     */
    private void gameLoop() {
        double previousLoopTime = Timer.getTime();
        double timeSteps = 0;

        while (!window.windowShouldClose()) {
            // Keep track of the elapsed time and time steps.
            double currentLoopStartTime = Timer.getTime();
            double elapsedTime = currentLoopStartTime - previousLoopTime;
            previousLoopTime = currentLoopStartTime;
            timeSteps += elapsedTime;

            handleInput();

            while (timeSteps >= timePerUpdate) {
                update((float) timeSteps);
                playSounds();
                timeSteps -= timePerUpdate;
            }
            render();
            synchronizeRenderer(currentLoopStartTime);
        }

        cleanUp();
    }

    /**
     * Avoids exhausting the system resources by controlling the rendering rate independently..
     *
     * @param currentLoopStartTime The time at with the current loop started.
     */
    private void synchronizeRenderer(double currentLoopStartTime) {
        while(Timer.getTime() < currentLoopStartTime + this.timePerRendering) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }

    public void setUpdatesPerSecond(int updatesPerSecond) {
        this.timePerUpdate = 1f / updatesPerSecond;
    }

    public void setRenderingsPerSecond(int renderingsPerSecond) {
        this.timePerRendering = 1f / renderingsPerSecond;
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
        System.out.println("Added entity " + entity);
    }

    public void addMaterial(Material material) {
        this.materials.add(material);
    }

    public void addSound(Sound sound) { this.sounds.add(sound); }

    public void setCamera(Camera camera) {
        if(this.graphicsSystem != null)
            this.graphicsSystem.setCamera(camera);

        this.addEntity(camera);
    }

    public void setAmbientLight(Vector3f ambientLight) {
        graphicsSystem.setAmbientLight(ambientLight);
    }
}
