package gdx.press_space;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Ground extends Entity {

	Ground(Body b, Vector2 p) {
		this.body = b;
		createBody(p);
	}

	void createBody(Vector2 p) {
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(0.5f, 0.5f);
		body.setTransform(p, 0);
		body.createFixture(groundBox, 0.0f);
		body.setUserData(this);
		groundBox.dispose();
		position = body.getPosition();
	}

	@Override
	public String toString() { return "Ground"; }
}
