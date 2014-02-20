package limk.flappybird;

import limk.flappybird.MainActivity.Constant;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Bird extends AnimatedSprite {

	protected Context mContext;
	protected PhysicsWorld mPhysicsWorld;
	protected Body birdBody;
	protected Scene mScene;
	protected Handler mHandler;
	protected PhysicsConnector birdConnector;
	protected boolean isDead;
	protected float x, y;

	public Bird(float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, Context pContext, PhysicsWorld pPhysicsWorld,
			Scene pScene, Handler pHandler) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.mContext = pContext;
		this.mPhysicsWorld = pPhysicsWorld;
		this.mScene = pScene;
		this.mHandler = pHandler;
		this.setZIndex(20);
		this.animate(150);
		this.isDead = false;
		this.x = pX;
		this.y = pY;
	}

	public void flyUp() {
		if (!isDead) {
			birdBody.setLinearVelocity(new Vector2(0, Constant.SPEED));
		}
	}

	public void dead(Sprite floor) {
		isDead = true;
		Message msg = new Message();
		msg.what = MainActivity.GAME_OVER;
		mHandler.sendMessage(msg);

		this.stopAnimation();
		if (floor.collidesWith(this)) {
			mPhysicsWorld.unregisterPhysicsConnector(birdConnector);
		}
	}

	public Vector2 getSpeed() {
		return birdBody.getLinearVelocity();
	}

	public void setAngleSpeed(float speed) {
		birdBody.setAngularVelocity(speed);
	}

	public float getAngle() {
		return birdBody.getAngle();
	}

	public void begin() {
		FixtureDef birFixtureDef = PhysicsFactory.createFixtureDef(1.0f, 0, 0);
		birdBody = PhysicsFactory.createCircleBody(mPhysicsWorld, this, BodyType.DynamicBody, birFixtureDef);
		birdConnector = new PhysicsConnector(this, birdBody);
		mPhysicsWorld.registerPhysicsConnector(birdConnector);
	}

	public void restart() {
		isDead = false;
		this.setPosition(x, y);
		this.animate(150);
	}
}
