package gdx.press_space;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Corpse extends Entity {

	Corpse(Body b, Vector2 p) {
		this.body = b;
		createBody(p);
	}

	void createBody(Vector2 p) {
		sprite = new Sprite(new Texture("images/corpse.png"));
		sprite.setScale(GameScreen.unitScale);
		body.setUserData(this);
		body.setTransform(p, 0);
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.5f, 0.5f);
		position = body.getPosition();

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = 2f;
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0f;
		body.createFixture(fixtureDef);
		box.dispose();
		body.setLinearDamping(2f);
	}

	@Override
	public String toString() { return "Corpse"; }
}
