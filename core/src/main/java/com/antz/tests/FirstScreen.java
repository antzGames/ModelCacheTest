package com.antz.tests;

import static net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute.*;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

    public static final int HOW_MANY_MODELS = 100;          // how many models to build in the model cache
    public static float SCENE_REFRESH_INTERVAL = 2f;        // how often to build in seconds
    private float sceneRefreshTimer = 0;
    public final int SHADOW_MAP_SIZE = 4096;

    public SceneManager sceneManager;
    public Scene scene;
    public Array<Scene> allModels = new Array<>();
    public SceneAsset sceneAsset;
    public DirectionalShadowLight light;
    public Cubemap diffuseCubemap;
    public Cubemap environmentCubemap;
    public Cubemap specularCubemap;
    public Texture brdfLUT;
    public SceneSkybox skybox;
    public PerspectiveCamera camera;
    public ModelCache modelCacheScene = new ModelCache();

    @Override
    public void show() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        initGLTF();
        // Prepare your screen here.
    }

    private void initGLTF() {
        sceneManager = new SceneManager();

        camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.001f;
        camera.far = 200;
        camera.position.set(0,40f,0);
        camera.lookAt(0,0,0);
        camera.update();
        sceneManager.setCamera(camera);

        // setup light
        Vector3 lightPosition = new Vector3(0,35,0);    // even though this is a directional light and is "infinitely far away", use this to set the near plane
        float farPlane = 300;
        float nearPlane = 0f;
        float VP_SIZE = 300f;

        light = new DirectionalShadowLight(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE).setViewport(VP_SIZE, VP_SIZE, nearPlane, farPlane);
        light.direction.set(-1, -1, 1);
        light.direction.nor();
        light.color.set(Color.WHITE);
        light.intensity = 2.8f;

        float halfDepth = (nearPlane + farPlane)/2f;
        Vector3 lightCentre = new Vector3();
        lightCentre.set(light.direction).scl(halfDepth).add(lightPosition);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(createSpecularEnv(specularCubemap));
        sceneManager.environment.set(createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);

        sceneAsset = new GLBLoader().load(Gdx.files.internal("models/castle.gltf.glb"));
        scene = new Scene(sceneAsset.scene);

        modelCacheScene.begin();
        for (int x = 0; x < HOW_MANY_MODELS; x++){
            Scene s = new Scene(scene.modelInstance);
            s.modelInstance.transform.setToTranslation(MathUtils.random(-10,10)*2, 0, MathUtils.random(-10,10)*2);
            modelCacheScene.add(s);
            allModels.add(s);
        }
        modelCacheScene.end();

        sceneManager.getRenderableProviders().add(modelCacheScene);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.SKY, true);

        sceneRefreshTimer += delta;
        if (sceneRefreshTimer > SCENE_REFRESH_INTERVAL) {
            long startTime = TimeUtils.millis();
            sceneRefreshTimer = 0;

            modelCacheScene.begin();
            for (Scene s: allModels){
                s.modelInstance.transform.setToTranslation(MathUtils.random(-10,10)*2, 0, MathUtils.random(-10,10)*2);
                modelCacheScene.add(s);
            }
            modelCacheScene.end();

            sceneManager.getRenderableProviders().clear();
            sceneManager.getRenderableProviders().add(modelCacheScene);
            Gdx.app.log("MODEL CACHE","App Type: " + Gdx.app.getType() +  " took: " + TimeUtils.timeSinceMillis(startTime) + "ms to build " + HOW_MANY_MODELS + " tiles!");
        }

        sceneManager.update(delta);
        sceneManager.render();
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();
        modelCacheScene.dispose();
        Gdx.app.log("DISPOSE","Screen disposed");
    }
}
