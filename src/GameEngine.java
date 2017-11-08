/*
 * @author Matthieu Le Boucher <matt.leboucher@gmail.com>
 */

import Helper.Timer;
import Logic.GameLogic;

public class GameEngine implements Runnable {
    private float timePerUpdate = 1f / 50;

    private float timePerRendering = 1f / 30;

    private final Thread gameLoopThread;

    private GameLogic gameLogic;

    private Window window;

    private Timer timer;

    public GameEngine(String windowTitle, int windowWidth, int windowHeight, GameLogic gameLogic) {
        this.gameLoopThread = new Thread(this);
        this.gameLogic = gameLogic;

        this.window = new Window(windowTitle, windowWidth, windowHeight, false);
        this.timer = new Timer();
    }

    public void start() {
        gameLoopThread.start();
    }

    @Override
    public void run() {
        try {
            initialize();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        // Todo: implement this logic.
        // e.g. initialize the renderer (when we'll have one. (:).
    }

    /**
     * Delegates the input handling to the game logic.
     */
    protected void handleInput() {
        gameLogic.handleInput();
    }

    /**
     * Delegates the update to the game logic.
     * @param timeStep The theoretical time step between each update.
     */
    protected void update(float timeStep) {
        gameLogic.update(timeStep);
    }

    /**
     * Delegates the rendering to the game logic, and then updates the window.
     */
    protected void render() {
        gameLogic.render();
        // Todo: should update the window as well.
    }

    /**
     * Core function of the game engine.
     */
    private void gameLoop() {
        double previousLoopTime = Timer.getTime();
        double steps = 0;

        while (true) {
            // Keep track of the elapsed time and time steps.
            double currentLoopStartTime = Timer.getTime();
            double elapsedTime = currentLoopStartTime - previousLoopTime;
            previousLoopTime = currentLoopStartTime;
            steps += elapsedTime;

            handleInput();

            while (steps >= timePerUpdate) {
                update((float) steps);
                steps -= timePerUpdate;
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
            GameEngine gameEngine = new GameEngine("Metenorage", 800, 600, null);
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
}
