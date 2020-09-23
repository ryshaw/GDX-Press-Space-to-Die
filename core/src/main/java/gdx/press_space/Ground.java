package gdx.press_space;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Ground extends Entity {

	Ground(Body b, Vector2 p) {
		this.body = b;
		createBody(p);
	}

	void createBody(Vector2 p) {
		sprite = new Sprite(new Texture("ground.png"));
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(800, 8.0f);
		body.setTransform(p, 0);
		body.createFixture(groundBox, 0.0f);
		body.setUserData(this);
		groundBox.dispose();
		position = body.getPosition();
		float spriteX = position.x - sprite.getWidth() / 2;
		float spriteY = position.y - sprite.getHeight() / 2;
		sprite.setPosition(spriteX, spriteY);
	}

	@Override
	public String toString() {
		return "Ground";
	}
}
