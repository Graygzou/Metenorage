package Engine;

/*
 * @author Matthieu Le Boucher <matt.leboucher@gmail.com>
 */

import Engine.Helper.Timer;
import Engine.Main.Entity;
import Engine.System.Graphics.Component.Mesh3D;
import Engine.System.Graphics.GraphicsSystem;
import Engine.System.Graphics.Texture;
import Engine.System.Logic.LogicSystem;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class GameEngine implements Runnable {
    private float timePerUpdate = 1f / 50;

    private float timePerRendering = 1f / 30;

    private final Thread gameLoopThread;

    private Window window;

    private Timer timer;

    private List<Entity> entities;

    private LogicSystem logicSystem;

    private GraphicsSystem graphicsSystem;

    public GameEngine(String windowTitle, int windowWidth, int windowHeight) {
        this.gameLoopThread = new Thread(this);

        this.window = new Window(windowTitle, windowWidth, windowHeight, false);
        this.timer = new Timer();

        // Systems setup.
        this.logicSystem = new LogicSystem();
        this.graphicsSystem = new GraphicsSystem(this.window);

        this.entities = new ArrayList<>();
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

        this.graphicsSystem.initialize();
    }

    /**
     * Delegates the input handling to the game logic.
     */
    protected void handleInput() {
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
            float rotation = entity.getRotation().x + 1.5f;
            if (rotation > 360) {
                rotation = 0;
            }

            entity.setRotation(rotation, rotation, rotation);
        }
    }

    /**
     * Delegates the update to the game logic.
     * @param timeStep The theoretical time step between each update.
     */
    protected void update(float timeStep) {
        // Todo: implement this logic.
        logicSystem.iterate(entities);
    }

    /**
     * Delegates the rendering to the game logic, and then updates the window.
     */
    protected void render() {
        // Todo: implement this logic.
        window.update();
        graphicsSystem.iterate(entities);
    }

    private void cleanUp() {
        graphicsSystem.cleanUp();
    }

    /**
     * Core function of the game engine.
     */
    private void gameLoop() {
        double previousLoopTime = Timer.getTime();
        double timeSteps = 0;

        while (true) {
            // Keep track of the elapsed time and time steps.
            double currentLoopStartTime = Timer.getTime();
            double elapsedTime = currentLoopStartTime - previousLoopTime;
            previousLoopTime = currentLoopStartTime;
            timeSteps += elapsedTime;

            handleInput();

            while (timeSteps >= timePerUpdate) {
                update((float) timeSteps);
                timeSteps -= timePerUpdate;
            }

            render();
            synchronizeRenderer(currentLoopStartTime);
        }
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

    public static void main(String[] args) {
        try {
            // GameLogic gameLogic = new Game();
            GameEngine gameEngine = new GameEngine("Metenorage", 800, 600);

            Entity testEntity = new Entity();
            
            // Read from a json file to load Entity and Components attached 
           // Utils.parser("Game/example.json", gameEngine);
            
            
            // Create and active a component
            /*TestComponent test = new TestComponent(testEntity);
            test.setActiveState(true);*/

            float[] positions = new float[] {
                    // V0
                    -0.5f, 0.5f, 0.5f,
                    // V1
                    -0.5f, -0.5f, 0.5f,
                    // V2
                    0.5f, -0.5f, 0.5f,
                    // V3
                    0.5f, 0.5f, 0.5f,
                    // V4
                    -0.5f, 0.5f, -0.5f,
                    // V5
                    0.5f, 0.5f, -0.5f,
                    // V6
                    -0.5f, -0.5f, -0.5f,
                    // V7
                    0.5f, -0.5f, -0.5f,

                    // For text coords in top face
                    // V8: V4 repeated
                    -0.5f, 0.5f, -0.5f,
                    // V9: V5 repeated
                    0.5f, 0.5f, -0.5f,
                    // V10: V0 repeated
                    -0.5f, 0.5f, 0.5f,
                    // V11: V3 repeated
                    0.5f, 0.5f, 0.5f,

                    // For text coords in right face
                    // V12: V3 repeated
                    0.5f, 0.5f, 0.5f,
                    // V13: V2 repeated
                    0.5f, -0.5f, 0.5f,

                    // For text coords in left face
                    // V14: V0 repeated
                    -0.5f, 0.5f, 0.5f,
                    // V15: V1 repeated
                    -0.5f, -0.5f, 0.5f,

                    // For text coords in bottom face
                    // V16: V6 repeated
                    -0.5f, -0.5f, -0.5f,
                    // V17: V7 repeated
                    0.5f, -0.5f, -0.5f,
                    // V18: V1 repeated
                    -0.5f, -0.5f, 0.5f,
                    // V19: V2 repeated
                    0.5f, -0.5f, 0.5f,
            };
            float[] textCoords = new float[]{
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,

                    0.0f, 0.0f,
                    0.5f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,

                    // For text coords in top face
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.0f, 1.0f,
                    0.5f, 1.0f,

                    // For text coords in right face
                    0.0f, 0.0f,
                    0.0f, 0.5f,

                    // For text coords in left face
                    0.5f, 0.0f,
                    0.5f, 0.5f,

                    // For text coords in bottom face
                    0.5f, 0.0f,
                    1.0f, 0.0f,
                    0.5f, 0.5f,
                    1.0f, 0.5f,
            };
            int[] indices = new int[]{
                    // Front face
                    0, 1, 3, 3, 1, 2,
                    // Top Face
                    8, 10, 11, 9, 8, 11,
                    // Right face
                    12, 13, 7, 5, 12, 7,
                    // Left face
                    14, 15, 6, 4, 14, 6,
                    // Bottom face
                    16, 18, 19, 17, 16, 19,
                    // Back face
                    4, 6, 7, 5, 4, 7,};

            Entity testTriangle = new Entity();
            Texture texture = new Texture("/Game/Textures/grassblock.png");
            testTriangle.addComponent(new Mesh3D(testTriangle, positions, indices, textCoords, texture));
            testTriangle.setPosition(0, 0, -4f);
            testTriangle.setRotation(0, 0, 0);
            testTriangle.setScale(1f);

            // Add the component to the entity
            //testEntity.addComponent(test);
            //gameEngine.addEntity(testEntity);
            gameEngine.addEntity(testTriangle);

            gameEngine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
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
    }
}
