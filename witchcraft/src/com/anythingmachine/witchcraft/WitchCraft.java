package com.anythingmachine.witchcraft;

import static com.anythingmachine.witchcraft.Util.Util.DEV_MODE;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.anythingmachine.LuaEngine.LoadScript;
import com.anythingmachine.assets.AssetManager;
import com.anythingmachine.collisionEngine.MyContactListener;
import com.anythingmachine.gdxwrapper.PolygonSpriteBatchWrap;
import com.anythingmachine.physicsEngine.RK4Integrator;
import com.anythingmachine.physicsEngine.particleEngine.ParticleSystem;
import com.anythingmachine.tiledMaps.Camera;
import com.anythingmachine.tiledMaps.TiledMapHelper;
import com.anythingmachine.witchcraft.Util.Util;
import com.anythingmachine.witchcraft.agents.Archer;
import com.anythingmachine.witchcraft.agents.Knight;
import com.anythingmachine.witchcraft.agents.NonPlayer;
import com.anythingmachine.witchcraft.agents.player.Player;
import com.anythingmachine.witchcraft.ground.Ground;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class WitchCraft implements ApplicationListener {
	// the time the last frame was rendered, used for throttling framerate
	private long lastRender;
	private TiledMapHelper tiledMapHelper;
	public static AssetManager assetManager;
	private NonPlayer npc1;
	private NonPlayer npc2;
	private NonPlayer npc3;
	private NonPlayer npc4;
	private NonPlayer npc5;
	public static Ground ground;
	private Box2DDebugRenderer debugRenderer;
	private PolygonSpriteBatchWrap polygonBatch;
	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;
	public static Player player;
	public static World world;
	public static Camera cam;
	private static RK4Integrator rk4;
	public static ParticleSystem rk4System;
	private float xGrid;
	private int camWorldSize;
	private Calendar cal;
	private float dawnDuskProgress = 0;
	private LoadScript script;
	public static float dt = 1f / 30f;
	private MyContactListener contactListener;
	private int screenWidth;
	private int screenHeight;

	public WitchCraft() {
		super();

		// Defer until create() when Gdx is initialized.
		screenWidth = -1;
		screenHeight = -1;
	}

	public WitchCraft(int width, int height) {
		super();

		screenWidth = width;
		screenHeight = height;
	}

	@Override
	public void create() {
		Date date = new Date();
		cal = GregorianCalendar.getInstance();
		cal.setTime(date);

		rk4 = new RK4Integrator();
		rk4System = new ParticleSystem(rk4);

		contactListener = new MyContactListener();
		world = new World(new Vector2(0.0f, -10.0f), false);
		world.setContactListener(contactListener);

		loadAssets();
		/**
		 * If the viewport's size is not yet known, determine it here.
		 */
		if (screenWidth == -1) {
			screenWidth = Gdx.graphics.getWidth();
			screenHeight = Gdx.graphics.getHeight();
		}

		tiledMapHelper = new TiledMapHelper();
		tiledMapHelper.setPackerDirectory("data/packer");
		tiledMapHelper.loadMap("data/world/level1/level.tmx");
		cam = new Camera(screenWidth, screenHeight);
		tiledMapHelper.prepareCamera(screenWidth, screenHeight);

		// overallTexture = new
		// Texture(Gdx.files.internal("data/tileSheet.png"));
		// overallTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		spriteBatch = new SpriteBatch();
		polygonBatch = new PolygonSpriteBatchWrap();
		shapeRenderer = new ShapeRenderer();

		script = new LoadScript("helloworld.lua");

		debugRenderer = new Box2DDebugRenderer();
		ground = new Ground(world);
		ground.readCurveFile("data/groundcurves.txt", -240, -250);

		player = new Player(rk4);
		npc1 = new Knight("knight2", "characters", new Vector2(354.0f, 3.0f),
				new Vector2(0.6f, 0.7f));
		npc2 = new Knight("knight1", "characters", new Vector2(800.0f, 3.0f),
				new Vector2(0.6f, 0.7f));
		npc3 = new Archer("archer", "characters", new Vector2(300.0f, 3.0f),
				new Vector2(0.6f, 0.7f));
		npc4 = new NonPlayer("civmalebrown", "characters", new Vector2(300.0f,
				3.0f), new Vector2(0.6f, 0.7f));
		npc5 = new NonPlayer("civfemaleblack-hood", "characters", new Vector2(
				300.0f, 3.0f), new Vector2(0.6f, 0.7f));
		tiledMapHelper.loadCollisions("data/collisions.txt", world,
				Util.PIXELS_PER_METER);

		lastRender = System.nanoTime();
	}

	@Override
	public void resume() {
	}

	@Override
	public void render() {
		long now = System.nanoTime();
		float dT = Gdx.graphics.getDeltaTime();

		world.step(dT, 1, 1);

		rk4.step(dt);

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Color c = getTimeOfDay();
		Gdx.gl.glClearColor((float) c.getRed() / 255f,
				(float) c.getGreen() / 255f, (float) c.getBlue() / 255f, 1f);

		player.update(dT);
		npc1.update(dT);
		npc2.update(dT);
		npc3.update(dT);
		npc4.update(dT);
		npc5.update(dT);

		Vector3 playerPos = player.getPosPixels();
		xGrid = Camera.camera.position.x = playerPos.x;
		float yGrid = Camera.camera.position.y = playerPos.y;
		if (xGrid < Gdx.graphics.getWidth() / 2) {
			xGrid = Camera.camera.position.x = Gdx.graphics.getWidth() / 2;
		}
		// else if (xGrid >= TiledMapHelper.getWidth()
		// - Gdx.graphics.getWidth() / 2) {
		// xGrid = TiledMapHelper.camera.position.x =
		// TiledMapHelper.getWidth()
		// - Gdx.graphics.getWidth() / 2;
		// }

		if (yGrid < Gdx.graphics.getHeight() / 2) {
			Camera.camera.position.y = Gdx.graphics.getHeight() / 2;
		}
		// if (TiledMapHelper.camera.position.y >=
		// TiledMapHelper.getHeight()
		// - Gdx.graphics.getHeight() / 2) {
		// TiledMapHelper.camera.position.y = TiledMapHelper.getHeight();
		// }

		camWorldSize = (int) (Gdx.graphics.getWidth() * Camera.camera.zoom);

		cam.update();

		tiledMapHelper.render(this);

		debugRenderer.render(world, Camera.camera.combined.scale(
				Util.PIXELS_PER_METER, Util.PIXELS_PER_METER,
				Util.PIXELS_PER_METER));

		now = System.nanoTime();
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}

		lastRender = now;
	}

	public Color getTimeOfDay() {
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour >= 20 || hour < 5) {
			return Color.DARK_GRAY;
		} else if (hour >= 6 && hour < 19) {
			return Color.LIGHT_GRAY;
		} else {
			if (hour < 6) {
				int min = cal.get(Calendar.MINUTE);
				if (min == 0) {
					dawnDuskProgress = 0.25f;
				} else {
					dawnDuskProgress = ((float) min / 60.f * .5f);
				}
				return new Color(dawnDuskProgress, dawnDuskProgress,
						dawnDuskProgress);
			} else {
				int min = cal.get(Calendar.MINUTE);
				if (min == 0) {
					dawnDuskProgress = 1f;
				} else {
					dawnDuskProgress = ((1f - (float) min / 60.f) * .5f);
				}
				return new Color(dawnDuskProgress, dawnDuskProgress,
						dawnDuskProgress);
			}
		}
	}

	public void drawPlayerLayer() {
		Matrix4 combined = Camera.camera.combined;
		polygonBatch.setProjectionMatrix(combined);
		polygonBatch.begin();

		ground.draw(polygonBatch,
				(int) (xGrid - (Gdx.graphics.getWidth() / 2.f))
						/ Util.curveLength, camWorldSize / Util.curveLength);

		polygonBatch.end();

		spriteBatch.setProjectionMatrix(combined);
		spriteBatch.begin();
		ground.drawGroundElems(spriteBatch,
				(int) (xGrid - (Gdx.graphics.getWidth() / 2.f))
						/ Util.curveLength, camWorldSize / Util.curveLength);
		if (DEV_MODE) {
			ground.drawDebugCurve(shapeRenderer);
		}

		npc2.draw(spriteBatch);
		npc1.draw(spriteBatch);
		npc3.draw(spriteBatch);
		npc4.draw(spriteBatch);
		npc5.draw(spriteBatch);

		player.draw(spriteBatch, combined);

		rk4System.draw(spriteBatch);

		spriteBatch.end();
		player.drawCape(Camera.camera.combined);


		shapeRenderer.setProjectionMatrix(combined);
		shapeRenderer.begin(ShapeType.FilledRectangle);
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.filledRect((cam.camera.position.x-cam.camera.viewportWidth*0.5f)
				+ ((cam.camera.viewportWidth * .1f) * player.getPower()),
				(cam.camera.position.y+cam.camera.viewportHeight*0.5f)-20, (cam.camera.viewportWidth * .1f), 20);
		shapeRenderer.end();
		//
		// player.drawCape(shapeRenderer);

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void dispose() {
	}

	public void loadAssets() {
		assetManager = new AssetManager();
		assetManager.addAtlas(
				"characters",
				new TextureAtlas(Gdx.files
						.internal("data/spine/character.atlas")));
		assetManager.addTexture("dust",
				new Texture(Gdx.files.internal("data/dust.png")));
		assetManager.addTexture("spiro",
				new Texture(Gdx.files.internal("data/spiro.png")));

	}
}
