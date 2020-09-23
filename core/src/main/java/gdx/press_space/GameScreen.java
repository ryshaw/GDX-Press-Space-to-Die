package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;


public class GameScreen implements Screen {
	private final Main game;
	private final World world;
	private final Box2DDebugRenderer debugRenderer;
	private final OrthographicCamera camera;
	Player player;
	Body body;
	private float accumulator = 0;
	BodyDef playerDef;
	Array<Body> bodies;
	Array<Entity> toRemove;


	static Vector2[] manifolds;
	ShapeRenderer shapeRenderer;

	GameScreen(final Main game, int level) {
		this.game = game;
		Box2D.init();
		world = new World(new Vector2(0, -200f), true);
		debugRenderer = new Box2DDebugRenderer();
		float aspectRatio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		camera = new OrthographicCamera(800, 800 * (aspectRatio));
		camera.translate(-400, 0);
		camera.update();

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;
		Body groundBody = world.createBody(groundBodyDef);
		Ground g = new Ground(groundBody, new Vector2(0, 8));

		playerDef = new BodyDef();
		playerDef.type = BodyType.DynamicBody;
		body = world.createBody(playerDef);
		player = new Player(body, new Vector2(-750, 100));

		world.setContactListener(new Box2DCollision(player));

		bodies = new Array<>();
		toRemove = new Array<>();

		manifolds = new Vector2[2];
		manifolds[0] = new Vector2(0, 0);
		manifolds[1] = new Vector2(10, 10);
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (player.dead) {
			player.dead = false;
			Corpse c = new Corpse(world.createBody(playerDef), player.deathPosition);
		}

		//debugRenderer.render(world, camera.combined);
		float d = (float) MathUtils.clamp(delta, 0, 0.05);
		world.getBodies(bodies);
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		for (Body b : bodies) {
			Entity e = (Entity) b.getUserData();
			e.update(game.batch, d);
			if (checkOutOfBounds(e)) {
				if (e.toString().equals("Player")) player.die();
				else toRemove.add(e);
			}
		}
		game.batch.end();

		/*manifolds = Box2DCollision.manifold;
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.line(manifolds[0], manifolds[1]);
		shapeRenderer.end();*/

		for (Entity e : toRemove) {
			world.destroyBody(e.body);
			e.dispose();
		}
		toRemove.clear();

		float lerp = 3f;
		Vector3 cam = camera.position;
		Vector2 pos = new Vector2(player.position.x, player.position.y + 20);
		// points camera above player
		cam.x += (pos.x - cam.x) * lerp * delta;
		cam.y += (pos.y - cam.y) * lerp * delta;
		camera.update();

		doPhysicsStep(delta);
	}

	private boolean checkOutOfBounds(Entity e) {
		Vector2 p = e.position;
		return (Math.abs(p.x) > 1000 || Math.abs(p.y) > 600);
	}


	private void doPhysicsStep(float delta) {
		float frameTime = Math.min(delta, 0.25f);
		float timeStep = 1/60f;
		accumulator += frameTime;
		while (accumulator >= timeStep) {
			world.step(timeStep, 10, 10);
			accumulator -= timeStep;
		}
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();
		for (Body b : bodies) {
			Entity e = (Entity) b.getUserData();
			e.dispose();
		}
		world.dispose();

		shapeRenderer.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}