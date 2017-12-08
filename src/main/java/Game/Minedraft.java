package Game;

import Engine.GameEngine;
import Engine.Helper.Loader.OBJLoader;
import Engine.Main.Entity;
import Engine.Main.Light.DirectionalLight;
import Engine.Main.Light.PointLight;
import Engine.Main.Light.SpotLight;
import Engine.Main.Material;
import Engine.Main.ScriptFile;
import Engine.Main.Sound;
import Engine.System.Graphics.Camera;
import Engine.System.Graphics.Component.Mesh3D;
import Engine.System.Physics.Component.BoxRigidBodyComponent;
import Engine.System.Scripting.Script;
import Engine.System.Sound.Component.Source;
import Engine.Utils;
import Game.Input.CameraKeyboard;
import org.joml.Vector3f;

/**
 * @author Matthieu Le Boucher <matt.leboucher@gmail.com>
 * @author Noemy Artigouha
 * @author Grégoire Boiron
 * This is a simplistic implementation of the game Minecraft.
 */

public class Minedraft {
    static int BED_ROCK_DEPTH = -2;

    public static void main(String[] args) {
        try {
            boolean testParser = false;

            if(!testParser) {
                GameEngine gameEngine = new GameEngine("Minedraft", 800, 600);

                // Create a game sound
                Sound son = new Sound("Test", "./resources/Game/Sounds/sonTest.wav");
                gameEngine.addSound(son);

                ScriptFile script = new ScriptFile("ScriptTest");
                gameEngine.addScript(script);

                // Create materials.
                Material grassMaterial = new Material("/Game/Textures/grassblock.png", 1f);
                Material bedRockMaterial = new Material("/Game/Textures/bedrock.png", 1f);
                gameEngine.addMaterial(grassMaterial);
                gameEngine.addMaterial(bedRockMaterial);

                int gridWidth = 8, gridHeight = 8;
                Mesh3D cubeMesh = OBJLoader.loadMesh("/Game/Models/cube.obj");
                cubeMesh.setMaterial(bedRockMaterial);
                Source sourceAudioFAMILY = null;

                for (float i = 0; i < gridWidth; i++) {
                    for (float j = 0; j < gridHeight; j++) {
                        Entity block = new Entity("Block (" + i + ", " + j + ")");

                        if (i == 0 && j == 0) {
                            // Create a new Audio Source
                            sourceAudioFAMILY = new Source(block, son);
                            block.addComponent(sourceAudioFAMILY);

                            Script script1 = new Script(block, script);
                            block.addComponent(script1);
                        }

                        cubeMesh.setEntity(block);
                        block.addComponent(cubeMesh);

                        block.addComponent(new BoxRigidBodyComponent(block, 0, 0.5f,0.5f,0.5f));

                        block.setPosition(i, BED_ROCK_DEPTH, -2f - j);
                        block.setScale(0.5f);
                        gameEngine.addEntity(block);
                    }
                }

                // Grass blocks
                cubeMesh = OBJLoader.loadMesh("/Game/Models/cube.obj");
                cubeMesh.setMaterial(grassMaterial);
                Entity block = new Entity("My block");
                cubeMesh.setEntity(block);
                block.addComponent(cubeMesh);
                block.addComponent(new BoxRigidBodyComponent(block, 1, 0.5f,0.5f,0.5f));
                block.setPosition(3, 0, -2f - 2);
                block.setScale(0.5f);
                gameEngine.addEntity(block);

                block = new Entity("My block");
                cubeMesh.setEntity(block);
                block.addComponent(cubeMesh);
                block.addComponent(new BoxRigidBodyComponent(block, 1, 0.5f,0.5f,0.5f));
                block.setPosition(3.8f, 1f, -2f - 2);
                block.setScale(0.5f);
                gameEngine.addEntity(block);

                // Set lighting.
                Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
                Vector3f lightColor = new Vector3f(1, 0.7f, 0.7f);
                Vector3f lightPosition = new Vector3f(gridWidth / 2, 2f, -2 - gridHeight / 2);
                float lightIntensity = 5.0f;
                PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
                pointLight.setAttenuation(new PointLight.Attenuation(0.0f, 0.0f, 1.0f));

                //gameEngine.addEntity(pointLight);
                gameEngine.setAmbientLight(ambientLight);

                lightPosition = new Vector3f(0, 1f, -2);
                lightColor = new Vector3f(0, 0, 1);
                double angle = Math.toRadians(-60);

                DirectionalLight directionalLight = new DirectionalLight(lightColor, lightPosition, 2f);
                directionalLight.getDirection().x = (float) Math.sin(angle);
                directionalLight.getDirection().y = (float) Math.cos(angle);
                gameEngine.addEntity(directionalLight);

                // Set up a spotlight.
                lightPosition = new Vector3f(0, 8f, -gridHeight / 4);
                pointLight = new PointLight(new Vector3f(0.2f, 1, 0), lightPosition, 1f);
                pointLight.setAttenuation(new PointLight.Attenuation(0.0f, 0.0f, 0.02f));
                Vector3f coneDirection = new Vector3f(0.5f, -1f, -0.5f);
                float cutOff = (float) Math.toRadians(240);
                SpotLight spotLight = new SpotLight(pointLight, coneDirection, cutOff);
                gameEngine.addEntity(spotLight);

                lightPosition = new Vector3f(0, 7.2f, -gridHeight / 4);
                pointLight = new PointLight(new Vector3f(1, 0.2f, 0), lightPosition, 1f);
                pointLight.setAttenuation(new PointLight.Attenuation(0.0f, 0.0f, 0.02f));
                coneDirection = new Vector3f(0.5f, -1f, -0.5f);
                cutOff = (float) Math.toRadians(170);
                spotLight = new SpotLight(pointLight, coneDirection, cutOff);
                gameEngine.addEntity(spotLight);

                // Set the main camera.
                Camera mainCamera = new Camera();
                mainCamera.setName("Caméra principale");
                mainCamera.addComponent(new CameraKeyboard(mainCamera));
                gameEngine.setCamera(mainCamera);

                gameEngine.start();

            } else {

                GameEngine gameEngine = new GameEngine("Minedraft", 800, 600);

                Utils.parser("Game/example.json", gameEngine);

                gameEngine.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
