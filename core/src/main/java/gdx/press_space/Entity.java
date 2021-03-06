package gdx.press_space;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class Entity {
	Sprite sprite;
	Body body;
	Vector2 position;

	void update(SpriteBatch batch, float delta) {
		// static bodies are already drawn on the map render, like ground and spikes
		if (body.getType() != BodyDef.BodyType.StaticBody) {
			position = body.getPosition();
			float spriteX = position.x - sprite.getWidth() / 2;
			float spriteY = position.y - sprite.getHeight() / 2;
			sprite.setPosition(spriteX, spriteY);
			sprite.setRotation(MathUtils.radiansToDegrees * (body.getAngle()));
			sprite.draw(batch);
		}
	}

	void dispose() {
		if (sprite != null) {
			sprite.getTexture().dispose();
		}
	}

	public String toString() { return "Entity"; }
}
