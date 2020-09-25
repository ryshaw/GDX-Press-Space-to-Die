package gdx.press_space;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

public abstract class Entity {
	Sprite sprite;
	Body body;
	Vector2 position;

	void update(SpriteBatch batch, float delta) {
		if (body.getType() == BodyDef.BodyType.DynamicBody) {
			position = body.getPosition();
			float spriteX = position.x - sprite.getWidth() / 2;
			float spriteY = position.y - sprite.getHeight() / 2;
			sprite.setPosition(spriteX, spriteY);
			sprite.draw(batch);
		} else if (body.getType() == BodyDef.BodyType.StaticBody) {
			sprite.draw(batch); // can do less work since it's not moving
		}

	}

	void dispose() { sprite.getTexture().dispose(); }

	public String toString() { return "Entity"; }
}
