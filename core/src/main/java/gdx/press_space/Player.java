package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Player extends Entity {
	float deltaX, speed, accel, deathTime;
	boolean jumping, inAir, dead;
	Vector2 deathPosition, spawnPosition;
	Sound jump, death;

	Player(Body b, Vector2 p, TextureRegion t) {
		this.body = b;
		deltaX = 0;
		speed = 8f;
		accel = 0.1f;
		jumping = true;
		inAir = true;
		dead = false;
		spawnPosition = p;
		deathTime = 1.5f;
		jump = Gdx.audio.newSound(Gdx.files.internal("audio/jump.wav"));
		death = Gdx.audio.newSound(Gdx.files.internal("audio/death.wav"));
		sprite = new Sprite(t);
		sprite.setScale(GameScreen.unitScale);
		body.setUserData(this);

		PolygonShape box = new PolygonShape();
		box.setAsBox(0.5f, 0.5f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = box;
		fixtureDef.density = 1f;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0f;
		body.setType(BodyDef.BodyType.DynamicBody);
		body.createFixture(fixtureDef);
		box.dispose();

		PolygonShape sensor = new PolygonShape();
		Vector2[] vertices = new Vector2[4];
		vertices[0] = new Vector2(-0.15f, -0.45f);
		vertices[1] = new Vector2(0.15f, -0.45f);
		vertices[2] = new Vector2(0.15f, -0.55f);
		vertices[3] = new Vector2(-0.15f, -0.55f);
		sensor.set(vertices);
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.shape = sensor;
		sensorDef.isSensor = true;
		body.createFixture(sensorDef);
		sensor.dispose();

		initializeBody(p);

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

		if (Math.abs(body.getLinearVelocity().y) > 1f) { inAir = true; }
		if (Math.abs(body.getLinearVelocity().y) < 0.1f) { inAir = false; }

		if (!inAir && !jumping && Gdx.input.isKeyPressed(Input.Keys.W)) {
			jumping = true;
			inAir = true;
			jump.play(Main.VOLUME);
			body.applyLinearImpulse(0f, 12f, position.x, position.y, true);
			//if (Math.abs(body.getLinearVelocity().y) > 12f) System.out.println(body.getLinearVelocity().y);

		}

		deltaX = MathUtils.clamp(deltaX, -1.0f, 1.0f);
		body.setTransform(position.x + speed * delta * deltaX, position.y, 0);

		if (Math.abs(position.x - spawnPosition.x) > 200 && !inAir) {
			spawnPosition = new Vector2(position.x, position.y + 20);
		}
	}

	void die() {
		if (!dead) {
			dead = true;
			death.play(Main.VOLUME);
			deathTime = 1f;
			deathPosition = new Vector2(position.x, position.y); // gotta make a new instance
			sprite.setAlpha(0f);
			jumping = true;
		}
	}

	void collisionWithGround() {
		inAir = false;
		jumping = false;
	}

	void offTheGround() {
		inAir = true;
		jumping = true;
	}

	void initializeBody(Vector2 p) {
		body.setActive(true);
		body.setTransform(p, 0);
		body.setLinearVelocity(0, 0);
		position = body.getPosition();

		body.setLinearDamping(2f);
		sprite.setAlpha(1f);
		body.setFixedRotation(true);
	}

	@Override
	void dispose() {
		jump.dispose();
		super.dispose();
	}

	@Override
	public String toString() { return "Player"; }
}
