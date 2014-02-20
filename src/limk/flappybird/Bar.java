package limk.flappybird;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bar extends Sprite {

	public static final int UPPER_BAR = 0;
	public static final int LOWER_BAR = 1;
	public boolean isGoal;
	private int type;
	
	public Bar(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager, int type) {
		super(pX, pY, pTextureRegion, vertexBufferObjectManager);
		this.type = type;
		this.isGoal = false;
	}
	
	public int getType(){
		return type;
	}
}
