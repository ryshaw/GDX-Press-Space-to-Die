package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameScreen implements Screen {
	private final Main game;
	private final World world;
	private final Box2DDebugRenderer debugRenderer;
	private final OrthographicCamera camera;
	Player player;
	Body body;
	private float accumulator = 0;
	Sprite ground;

	GameScreen(final Main game, int level) {
		this.game = game;
		Box2D.init();
		world = new World(new Vector2(0, -250f), true);
		debugRenderer = new Box2DDebugRenderer();
		float aspectRatio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		camera = new OrthographicCamera(800, 800 * (aspectRatio));
		camera.translate(-400, 0);
		camera.update();

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vector2(0, 5));
		Body groundBody = world.createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(camera.viewportWidth, 5.0f);
		groundBody.createFixture(groundBox, 0.0f);
		groundBody.setUserData(GameActor.GROUND);
		groundBox.dispose();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(-750, 100);
		body = world.createBody(bodyDef);
		player = new Player(body);
		world.setContactListener(new Box2DCollision(player));
		ground = new Sprite(new Texture("ground.png"));
		Vector2 p = groundBody.getPosition();
		ground.setPosition(p.x - 800, p.y - 10);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//debugRenderer.render(world, camera.combined);
		float d = (float) MathUtils.clamp(delta, 0, 0.05);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		player.update(game.batch, d);
		ground.draw(game.batch);
		game.batch.end();

		doPhysicsStep(delta);
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
		player.dispose();
		world.dispose();
		ground.getTexture().dispose();
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