package gdx.press_space;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Spikes extends Entity {
	public Spikes(Body b, Vector2 p) {
		this.body = b;
		createBody(p);
	}

	void createBody(Vector2 p) {
		PolygonShape triangle = new PolygonShape();
		Vector2[] vertices = new Vector2[3];
		vertices[0] = new Vector2(-0.5f, -0.5f);
		vertices[1] = new Vector2(0, 0.5f);
		vertices[2] = new Vector2(0.5f, -0.5f);
		triangle.set(vertices);
		body.setTransform(p, 0);
		body.createFixture(triangle, 1.0f);
		body.setUserData(this);
		triangle.dispose();
		position = body.getPosition();
	}

	@Override
	public String toString() { return "Spikes"; }
}
