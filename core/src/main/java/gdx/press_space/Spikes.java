package gdx.press_space;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Spikes extends Entity {
	public Spikes(Body b, Vector2 p) {
		this.body = b;
		createBody(p);
	}

	void createBody(Vector2 p) {
		sprite = new Sprite(new Texture("images/spike.png"));
		PolygonShape triangle = new PolygonShape();
		Vector2[] vertices = new Vector2[3];
		vertices[0] = new Vector2(-8, -8);
		vertices[1] = new Vector2(0, 8);
		vertices[2] = new Vector2(8, -8);
		triangle.set(vertices);
		body.setTransform(p, 0);
		body.createFixture(triangle, 1.0f);
		body.setUserData(this);
		triangle.dispose();
		position = body.getPosition();
		float spriteX = position.x - sprite.getWidth() / 2;
		float spriteY = position.y - sprite.getHeight() / 2;
		sprite.setPosition(spriteX, spriteY);
		sprite.setAlpha(0.1f);
	}

	@Override
	public String toString() { return "Spikes"; }
}
