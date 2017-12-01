package Engine.System.Graphics;

import Engine.Main.Entity;
import Engine.Main.Light.DirectionalLight;
import Engine.Main.Light.PointLight;
import Engine.ShadersHandler;
import Engine.System.BaseSystem;
import Engine.System.Component.Component;
import Engine.System.Graphics.Component.Mesh3D;
import Engine.TransformationUtils;
import Engine.Utils;
import Engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;

/**
 * @author Matthieu Le Boucher <matt.leboucher@gmail.com>
 */
public class GraphicsSystem extends BaseSystem {
    private ShadersHandler shadersHandler;

    private Window window;

    /**
     * Field of View in radians.
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 1f;

    private static final float Z_FAR = 100.f;

    private Matrix4f projectionMatrix;

    private Matrix4f viewMatrix;

    private Camera camera;

    private Vector3f ambientLight;

    private boolean isInitialized = false;

    public GraphicsSystem(Window window) {
        this.window = window;

        this.camera = new Camera();
    }

    public void resetProjectionMatrix() throws Exception {
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio,
                Z_NEAR, Z_FAR);
        System.out.println("Projection matrix reset with aspect ratio: " + aspectRatio + " to:\n" + projectionMatrix);
        shadersHandler.setUniform("projectionMatrix", projectionMatrix);
    }

    @Override
    public void cleanUp() {
        glDisableVertexAttribArray(0);
    }

    @Override
    public void iterate(List<Entity> entities) {
        if (window.isResized()) {
            try {
                resetProjectionMatrix();
            } catch (Exception e) {
                System.out.println("GraphicsEngine: could not reset projection matrix.");
            }
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        // Update the view matrix.
        Matrix4f viewMatrix = TransformationUtils.getViewMatrix(camera);

        for (Entity entity : entities) {
            // Update the model-view matrix for the current entity.
            shadersHandler.setUniform("modelViewMatrix",
                    TransformationUtils.getModelViewMatrix(entity, viewMatrix));

            shadersHandler.bind();

            if(entity instanceof PointLight) {
                PointLight currentPointLight = new PointLight((PointLight) entity);
                Vector3f lightPosition = currentPointLight.getPosition();
                Vector4f viewPosition = new Vector4f(lightPosition, 1);
                viewPosition.mul(viewMatrix);
                lightPosition.x = viewPosition.x;
                lightPosition.y = viewPosition.y;
                lightPosition.z = viewPosition.z;
                shadersHandler.setUniform("pointLight", currentPointLight);
            } else if(entity instanceof DirectionalLight) {
                DirectionalLight currentDirectionalLight = new DirectionalLight((DirectionalLight) entity);
                Vector4f viewDirection = new Vector4f(currentDirectionalLight.getDirection(), 0);
                viewDirection.mul(viewMatrix);
                currentDirectionalLight.setDirection(new Vector3f(viewDirection.x, viewDirection.y, viewDirection.z));
                shadersHandler.setUniform("directionalLight", currentDirectionalLight);
            }

            shadersHandler.setUniform("ambientLight", ambientLight);
            shadersHandler.setUniform("specularPower", 10f);

            for (Component component : getLocalSystemComponentsFor(entity)) {
                if(component instanceof Mesh3D) {
                    shadersHandler.setUniform("material", ((Mesh3D) component).getMaterial());
                }

                component.initialize();
                component.apply();
            }

            shadersHandler.unbind();
        }
    }

    @Override
    public Class<? extends Component> getRecognizedInterface() {
        return GraphicsComponent.class;
    }

    @Override
    public void initialize() throws Exception {
        shadersHandler = new ShadersHandler();
        shadersHandler.createVertexShader(Utils.readTextResource("Shader/basicShader.vs"));
        shadersHandler.createFragmentShader(Utils.readTextResource("Shader/basicShader.fs"));
        shadersHandler.link();

        shadersHandler.createUniform("projectionMatrix");
        shadersHandler.createUniform("modelViewMatrix");
        shadersHandler.createUniform("textureSampler");

        shadersHandler.setUniform("modelViewMatrix", new Matrix4f());
        shadersHandler.setUniform("textureSampler", 0);

        shadersHandler.createUniform("specularPower");
        shadersHandler.createUniform("ambientLight");
        shadersHandler.createMaterialUniform("material");
        shadersHandler.createPointLightUniform("pointLight");
        shadersHandler.createDirectionalLightUniform("directionalLight");
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }
}
