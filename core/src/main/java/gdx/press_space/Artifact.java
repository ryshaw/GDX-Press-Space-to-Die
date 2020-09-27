package gdx.press_space;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Artifact extends Entity {
	float offset, yPosition;
	boolean collected;

	Artifact(Body b, Vector2 p) {
		this.body = b;
		createBody(p);
		sprite = new Sprite(new Texture("images/artifact.png"));
		sprite.setScale(GameScreen.unitScale);
		offset = 0f;
		yPosition = p.y;
		collected = false;
	}

	void createBody(Vector2 p) {
		PolygonShape box = new PolygonShape();
		box.setAsBox(0.5f, 0.5f);
		body.setTransform(p, 0);
		body.createFixture(box, 0.0f);
		body.setUserData(this);
		box.dispose();
		body.getFixtureList().get(0).setSensor(true);
		position = body.getPosition();
	}

	@Override
	void update(SpriteBatch batch, float delta) {
		offset += delta * 1.5f;
		body.setTransform(position.x, yPosition + MathUtils.sin(offset) / 3, 0);
		position = body.getPosition();
		float spriteX = position.x - sprite.getWidth() / 2;
		float spriteY = position.y - sprite.getHeight() / 2;
		sprite.setPosition(spriteX, spriteY);
		sprite.draw(batch);
	}

	@Override
	public String toString() { return "Artifact"; }
}
