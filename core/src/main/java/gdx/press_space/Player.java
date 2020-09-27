package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Entity {
	float deltaX, speed, accel, deathTime, timeBetweenJumps;
	Vector2 deathPosition, spawnPosition;
	Sound jump, death;
	boolean dead;
	int numContacts;

	Player(Body b, Vector2 p, TextureRegion t) {
		this.body = b;
		deltaX = 0;
		speed = 8f;
		accel = 0.1f;
		dead = false;
		spawnPosition = p;
		deathTime = 1f;
		jump = Gdx.audio.newSound(Gdx.files.internal("audio/jump.wav"));
		death = Gdx.audio.newSound(Gdx.files.internal("audio/death.wav"));
		sprite = new Sprite(t);
		sprite.setScale(GameScreen.unitScale);
		body.setUserData(this);

		EdgeShape edge = new EdgeShape();
		edge.set(new Vector2(-0.5f, -0.5f), new Vector2(0.5f, -0.5f));

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = edge;
		fixtureDef.density = 1f;
		fixtureDef.friction = 1f;
		fixtureDef.restitution = 0f;
		body.setType(BodyDef.BodyType.DynamicBody);
		body.createFixture(fixtureDef); // bottom edge

		edge.set(new Vector2(-0.5f, -0.5f), new Vector2(-0.5f, 0.5f));
		fixtureDef.shape = edge;
		fixtureDef.density = 0f;
		body.createFixture(fixtureDef);
		edge.set(new Vector2(0.5f, -0.5f), new Vector2(0.5f, 0.5f));
		fixtureDef.shape = edge;
		body.createFixture(fixtureDef);
		edge.set(new Vector2(-0.5f, 0.5f), new Vector2(0.5f, 0.5f));
		fixtureDef.shape = edge;
		body.createFixture(fixtureDef);

		PolygonShape sensor = new PolygonShape();
		Vector2[] vertices = new Vector2[4];
		vertices[0] = new Vector2(-0.3f, -0.45f);
		vertices[1] = new Vector2(0.3f, -0.45f);
		vertices[2] = new Vector2(0.3f, -0.55f);
		vertices[3] = new Vector2(-0.3f, -0.55f);
		sensor.set(vertices);
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.shape = sensor;
		sensorDef.isSensor = true;
		body.createFixture(sensorDef);
		vertices[0] = new Vector2(-0.3f, -0.3f);
		vertices[1] = new Vector2(0.3f, -0.3f);
		vertices[2] = new Vector2(0.3f, 0.3f);
		vertices[3] = new Vector2(-0.3f, 0.3f);
		sensor.set(vertices);
		sensorDef.shape = sensor;
		sensorDef.isSensor = false;
		body.createFixture(sensorDef);

		sensor.dispose();
		edge.dispose();

		timeBetweenJumps = 0.3f;
		numContacts = 0;

		initializeBody(p);
	}

	@Override
	void update(SpriteBatch batch, float delta) {
		super.update(batch, delta);

		body.applyForce(1e-20f, 0, position.x, position.y, true);
		// above line fixes a bug where walking off a platform means you stay in the air

		deathTime -= delta;
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && deathTime < 0f) { die(); }

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

		timeBetweenJumps -= delta;
		if (numContacts > 0 && Gdx.input.isKeyPressed(Input.Keys.W) && timeBetweenJumps < 0) {
			timeBetweenJumps = 0.3f;
			jump.play(Main.VOLUME - 0.1f);
			body.setLinearVelocity(body.getLinearVelocity().x, 12f);
		}

		deltaX = MathUtils.clamp(deltaX, -1.0f, 1.0f);
		body.setTransform(position.x + speed * delta * deltaX, position.y, 0);
	}

	void updateSpawnpoint(Vector2 p) {
		Vector2 v = new Vector2(p.x, p.y + 1f);
		if (spawnPosition.x != v.x || spawnPosition.y != v.y) {
			spawnPosition = v;
		}
	}

	void die() {
		if (!dead) {
			dead = true;
			death.play(Main.VOLUME);
			deathTime = 1f;
			deathPosition = new Vector2(position.x, position.y); // gotta make a new instance
			sprite.setAlpha(0f);
			deltaX = 0;
		}
	}

	void initializeBody(Vector2 p) {
		body.setActive(true);
		body.setTransform(p, 0);
		body.setLinearVelocity(0, 0);
		position = body.getPosition();

		body.setLinearDamping(2f);
		sprite.setAlpha(1f);
		body.setFixedRotation(true);
		timeBetweenJumps = 0.3f;
		numContacts = 0;
	}

	@Override
	void dispose() {
		jump.dispose();
		super.dispose();
	}

	@Override
	public String toString() { return "Player"; }
}
