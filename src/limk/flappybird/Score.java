package limk.flappybird;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Score {

	private int score;
	private VertexBufferObjectManager mVertexBufferObjectManager;
	private TiledTextureRegion mNumberTiledTextureRegion;
	private Scene mScene;
	private float x, y;
	private AnimatedSprite hundredNum, tenNum, bitNum;

	public Score(float pX, float pY, TiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, Scene pScene) {
		this.mVertexBufferObjectManager = pVertexBufferObjectManager;
		this.mNumberTiledTextureRegion = pTiledTextureRegion;
		this.mScene = pScene;
		this.score = 0;
		this.x = pX;
		this.y = pY;
		hundredNum = new AnimatedSprite(x - mNumberTiledTextureRegion.getWidth(), y, mNumberTiledTextureRegion,
				mVertexBufferObjectManager);
		tenNum = new AnimatedSprite(x, y, mNumberTiledTextureRegion, mVertexBufferObjectManager);
		bitNum = new AnimatedSprite(x + mNumberTiledTextureRegion.getWidth(), y, mNumberTiledTextureRegion,
				mVertexBufferObjectManager);
		hundredNum.stopAnimation(10);
		tenNum.stopAnimation(10);
		bitNum.stopAnimation(10);

		hundredNum.setZIndex(15);
		tenNum.setZIndex(15);
		bitNum.setZIndex(15);
		mScene.attachChild(hundredNum);
		mScene.attachChild(tenNum);
		mScene.attachChild(bitNum);

		changeScore();
	}

	public int getScore() {
		return score;
	}

	public void addScore() {
		score++;
		changeScore();
	}

	public void resetScore() {
		score = 0;
	}

	public void changeScore() {
		if (score < 10) {
			hundredNum.stopAnimation(10);
			tenNum.stopAnimation(score);
			bitNum.stopAnimation(10);
		} else if (score < 100) {
			int ten = score / 10;
			int bit = score % 10;
			hundredNum.stopAnimation(10);
			tenNum.stopAnimation(ten);
			bitNum.stopAnimation(bit);
		} else if (score < 1000) {
			int hundred = score / 100;
			int ten = score % 100 / 10;
			int bit = score % 100 % 10;
			hundredNum.stopAnimation(hundred);
			tenNum.stopAnimation(ten);
			bitNum.stopAnimation(bit);
		} else {
			hundredNum.stopAnimation(9);
			tenNum.stopAnimation(9);
			bitNum.stopAnimation(9);
		}
	}
}
