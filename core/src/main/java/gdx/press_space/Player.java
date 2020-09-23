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

public class Player extends Entity {
	float deltaX, speed, accel, jumpingTime, deathTime;
	boolean jumping, inAir, dead;
	Vector2 deathPosition, spawnPosition;

	Player(Body b, Vector2 p) {
		this.body = b;
		deltaX = 0;
		speed = 150f;
		accel = 0.1f;
		jumping = false;
		inAir = true;
		jumpingTime = 0f;
		dead = false;
		spawnPosition = p;
		createBody(p);
		deathTime = 1.5f;
	}

	@Override
	void update(SpriteBatch batch, float delta) {
		super.update(batch, delta);

		body.applyForce(1e-20f, 0, position.x, position.y, true);
		// above line fixes a bug where walking off a platform means you stay in the air

		deathTime -= delta;
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && deathTime < 0f) {
			die();
		}

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

		if (!inAir && !jumping && Gdx.input.isKeyPressed(Input.Keys.W)) {
			jumping = true;
			jumpingTime = 0.3f;
			inAir = true;
		}

		if (Math.abs(body.getLinearVelocity().y) > 1.0f) { inAir = true; }
		if (Math.abs(body.getLinearVelocity().y) < 0.1f) { inAir = false; }


		if (jumping && jumpingTime > 0) {
			body.applyLinearImpulse(0f, 8e5f, position.x, position.y, true);
			jumpingTime -= delta;
			if (jumpingTime < 0) {
				jumping = false;
			}
		}
		deltaX = MathUtils.clamp(deltaX, -1.0f, 1.0f);
		body.setTransform(position.x + speed * delta * deltaX, position.y, 0);

		if (Math.abs(position.x - spawnPosition.x) > 200 && !inAir) {
			spawnPosition = new Vector2(position.x, position.y + 20);
		}
	}

	void die() {
		dead = true;
		deathTime = 1.5f;
		deathPosition = new Vector2(position.x, position.y); // gotta make a new instance
		createBody(spawnPosition);
	}

	void collisionWithGround() {
		inAir = false;
		jumping = false;
	}

	void offTheGround() { inAir = true; }

	void createBody(Vector2 p) {
		sprite = new Sprite(new Texture("player.png"));
		body.setUserData(this);
		body.setTransform(p, 0);
		position = body.getPosition();
		PolygonShape box = new PolygonShape();
		box.setAsBox(8, 8);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0f;
		body.createFixture(fixtureDef);
		box.dispose();
		body.setLinearDamping(1f);
	}

	@Override
	public String toString() {
		return "Player";
	}
}
