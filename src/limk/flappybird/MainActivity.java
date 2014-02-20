package limk.flappybird;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import com.badlogic.gdx.math.Vector2;

public class MainActivity extends SimpleBaseGameActivity {

	private static int CAMERA_WIDTH;
	private static int CAMERA_HEIGHT;
	public static int GAME_OVER = 0;
	public static int ADD_SCORE = 1;

	private DisplayMetrics metrics;
	private Camera mCamera;
	private boolean isBegin;
	private boolean isDead;

	private BitmapTextureAtlas mBirdBitmapTextureAtlas;
	private TiledTextureRegion mBirdTextureRegion;
	private BitmapTextureAtlas mNumberBitmapTextureAtlas;
	private TiledTextureRegion mNumberTextureRegion;

	private Texture mGroundTexture;
	private TextureRegion mGroundTextureRegion;
	private Texture mReadyTexture;
	private TextureRegion mReadyTextureRegion;
	private Texture mUpperBarTexture;
	private TextureRegion mUpperBarTextureRegion;
	private Texture mLowerBarTexture;
	private TextureRegion mLowerBarTextureRegion;
	private Texture mBackgroundTexture;
	private TextureRegion mBackgroundTextureRegion;
	private Bird bird;
	private Sprite floor;
	private Sprite ready;
	private PhysicsWorld mPhysicsWorld;
	private Scene mScene;
	private Score mScore;
	private BarManager barManager;

	private Handler mHandler;
	private IUpdateHandler updateHandler = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			barManager.update(bird);
			if (floor.collidesWith(bird)) {
				bird.dead(floor);
			}
		}
	};

	private Runnable addBar = new Runnable() {

		@Override
		public void run() {
			barManager.addBar();
			mHandler.postDelayed(addBar, 1250);
		}
	};

	@Override
	public EngineOptions onCreateEngineOptions() {
		Constant.init();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == GAME_OVER) {
					barManager.gameOver();
					mHandler.removeCallbacks(addBar);
					floor.clearEntityModifiers();
					isDead = true;
					isBegin = false;
				}
				if (msg.what == ADD_SCORE) {
					mScore.addScore();
				}
			}
		};
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		CAMERA_WIDTH = 320;
		CAMERA_HEIGHT = 480;
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera);
	}

	@Override
	protected void onCreateResources() throws IOException {

		this.mBirdBitmapTextureAtlas = new BitmapTextureAtlas(getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
		mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBirdBitmapTextureAtlas, this,
				"gfx/bird.png", 0, 0, 3, 1);
		this.mEngine.getTextureManager().loadTexture(mBirdBitmapTextureAtlas);

		this.mNumberBitmapTextureAtlas = new BitmapTextureAtlas(getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
		mNumberTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mNumberBitmapTextureAtlas,
				this, "gfx/score_num.png", 0, 0, 11, 1);
		this.mEngine.getTextureManager().loadTexture(mNumberBitmapTextureAtlas);

		mGroundTexture = new AssetBitmapTexture(getTextureManager(), getAssets(), "gfx/ground.png");
		mGroundTextureRegion = TextureRegionFactory.extractFromTexture(mGroundTexture);
		mGroundTexture.load();

		mReadyTexture = new AssetBitmapTexture(getTextureManager(), getAssets(), "gfx/ready.png");
		mReadyTextureRegion = TextureRegionFactory.extractFromTexture(mReadyTexture);
		mReadyTexture.load();

		mBackgroundTexture = new AssetBitmapTexture(getTextureManager(), getAssets(), "gfx/background.png");
		mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(mBackgroundTexture);
		mBackgroundTexture.load();

		mUpperBarTexture = new AssetBitmapTexture(getTextureManager(), getAssets(), "gfx/up_bar.png");
		mUpperBarTextureRegion = TextureRegionFactory.extractFromTexture(mUpperBarTexture);
		mUpperBarTexture.load();

		mLowerBarTexture = new AssetBitmapTexture(getTextureManager(), getAssets(), "gfx/low_bar.png");
		mLowerBarTextureRegion = TextureRegionFactory.extractFromTexture(mLowerBarTexture);
		mLowerBarTexture.load();

		isBegin = false;
		isDead = false;
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		mScene = new Scene();
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 5);
		background.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2,
				mBackgroundTextureRegion, getVertexBufferObjectManager())));
		// background.attachParallaxEntity(new ParallaxEntity(-10.0f, floor));
		mScene.setBackground(background);

		floor = new Sprite(CAMERA_WIDTH / 2f, mGroundTextureRegion.getHeight() / 2f, mGroundTextureRegion,
				getVertexBufferObjectManager());
		floor.setZIndex(10);

		MoveXModifier floorModifier = new MoveXModifier(3.0f, CAMERA_WIDTH, 6f);
		floor.registerEntityModifier(new LoopEntityModifier(floorModifier));

		mScene.attachChild(floor);

		mPhysicsWorld = new PhysicsWorld(new Vector2(0, Constant.GRAVITY), false);

		bird = new Bird(CAMERA_WIDTH / 4, CAMERA_HEIGHT / 2, mBirdTextureRegion, getVertexBufferObjectManager(), this,
				mPhysicsWorld, mScene, mHandler);
		mScene.attachChild(bird);

		ready = new Sprite(CAMERA_WIDTH / 4 + mReadyTextureRegion.getWidth() / 2, CAMERA_HEIGHT / 2,
				mReadyTextureRegion, getVertexBufferObjectManager());
		mScene.attachChild(ready);

		mScene.setOnSceneTouchListener(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(Scene arg0, TouchEvent arg1) {
				System.out.println("isBegin: " + isBegin);
				System.out.println("isDead: " + isDead);
				if (arg1.getAction() == TouchEvent.ACTION_DOWN) {
					if (isDead) {
						restart();
					} else if (!isBegin) {
						begin();
					}
					bird.flyUp();
				}
				return true;
			}
		});

		barManager = new BarManager(mScene, CAMERA_WIDTH, CAMERA_HEIGHT, mUpperBarTextureRegion,
				mLowerBarTextureRegion, floor, getVertexBufferObjectManager(), mHandler);
		mScore = new Score(CAMERA_WIDTH / 2, CAMERA_HEIGHT - Constant.PADDING, mNumberTextureRegion,
				getVertexBufferObjectManager(), mScene);

		mScene.registerUpdateHandler(mPhysicsWorld); 
		mScene.registerUpdateHandler(updateHandler);
		return mScene;
	}

	void restart() {
		isDead = false;
		isBegin = false;
		bird.restart();
		barManager.restart();
		ready.setAlpha(1.0f);
	}

	void begin() {
		isBegin = true;
		ready.registerEntityModifier(new AlphaModifier(1.0f, 1.0f, 0.0f));
		bird.begin();
		mHandler.postDelayed(addBar, 2000);
	}

	public static class Constant {
		public static float SPEED;
		public static float PADDING;
		public static float INTERVAL;
		public static float GRAVITY;

		public static void init() {
			SPEED = 8f;
			PADDING = 60f;
			INTERVAL = 100f;
			GRAVITY = -SensorManager.GRAVITY_EARTH * 2f;
		}
	}

}
