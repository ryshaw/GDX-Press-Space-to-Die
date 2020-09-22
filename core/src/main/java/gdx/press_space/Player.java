package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Player {
	Sprite sprite;
	Body body;
	float deltaX;
	float speed;
	float accel;
	boolean jumping, inAir;
	float jumpingTime;

	Player(Body b) {
		this.body = b;
		createBody();
		deltaX = 0;
		speed = 150f;
		accel = 0.1f;
		jumping = false;
		inAir = false;
		jumpingTime = 0f;
	}

	void update(SpriteBatch batch, float delta) {
		Vector2 pos = body.getPosition();
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			if (deltaX > 0) {
				deltaX = 0;
			}
			deltaX -= accel;
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			if (deltaX < 0) {
				deltaX = 0;
			}
			deltaX += accel;
		} else {
			if (deltaX > 0) {
				deltaX -= accel;
			} else if (deltaX < 0) {
				deltaX += accel;
			}
			if (Math.abs(deltaX) < 0.2f) {
				deltaX = 0;
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W) && !inAir) {
			jumping = true;
			jumpingTime = 0.3f;
			inAir = true;
		}

		if (jumping && jumpingTime > 0) {
			body.applyLinearImpulse(0f, 8e5f, pos.x, pos.y, true);
			jumpingTime -= delta;
			if (jumpingTime < 0) {
				jumping = false;
			}
		}
		deltaX = MathUtils.clamp(deltaX, -1.0f, 1.0f);
		body.setTransform(pos.x + speed * delta * deltaX, pos.y, 0);
		sprite.setPosition(pos.x - 8, pos.y - 8);
		sprite.draw(batch);
	}

	void collisionWithGround() {
		inAir = false;
	}

	void createBody() {
		sprite = new Sprite(new Texture("player.png"));
		body.setUserData(GameActor.PLAYER);
		PolygonShape box = new PolygonShape();
		box.setAsBox(8, 8);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0.7f;
		fixtureDef.restitution = 0f;
		body.createFixture(fixtureDef);
		box.dispose();
		body.setLinearDamping(0.2f);
	}

	void dispose() {
		sprite.getTexture().dispose();
	}
}
