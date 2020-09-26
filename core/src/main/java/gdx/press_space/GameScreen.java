package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;


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
	Music music;
	float respawnDelay;
	Sound respawn;
	TiledMap map;
	OrthogonalTiledMapRenderer renderer;
	static float unitScale = 1/16f;
	static int corpseCounter; // used to fix a bug where the player's jump is halved

	ShapeRenderer shapeRenderer;

	GameScreen(final Main game, int level) {
		this.game = game;
		Box2D.init();
		world = new World(new Vector2(0, -20f), true);
		debugRenderer = new Box2DDebugRenderer();
		float aspectRatio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 40, 40 * aspectRatio);
		camera.translate(0, 10);
		camera.update();

		map = new TmxMapLoader().load("level1.tmx");
		processMap(map);
		renderer = new OrthogonalTiledMapRenderer(map, unitScale);

		world.setContactListener(new Box2DCollision(player));

		bodies = new Array<>();
		toRemove = new Array<>();

		shapeRenderer = new ShapeRenderer();

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/the-lift-by-kevin-macleod.mp3"));
		music.setVolume(0.2f);
		music.setLooping(true);
		music.play();

		respawnDelay = 0;
		respawn = Gdx.audio.newSound(Gdx.files.internal("audio/respawn.wav"));
		corpseCounter = 0;
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (player.dead) {
			new Corpse(world.createBody(playerDef), player.deathPosition);
			corpseCounter += 2;
			player.dead = false;
			player.body.setActive(false);
			respawnDelay = 1f;
		}

		if (respawnDelay > 0) {
			respawnDelay -= delta;
			if (respawnDelay < 0) {
				respawn.play(0.2f);
				player.createBody(player.spawnPosition);
			}
		}

		float d = (float) MathUtils.clamp(delta, 0, 0.05);
		world.getBodies(bodies);
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		for (Body b : bodies) {
			Entity e = (Entity) b.getUserData();
			if (!e.toString().equals("Player") || respawnDelay <= 0) e.update(game.batch, d);
			// above line: don't update the player if the player is dead
			if (checkOutOfBounds(e)) {
				if (e.toString().equals("Player") && respawnDelay <= 0) {
					player.die();
				}
				else if (!e.toString().equals("Player")) toRemove.add(e);
			}
		}
		game.batch.end();

		renderer.setView(camera);
		renderer.render();

		//debugRenderer.render(world, camera.combined);

		for (Entity e : toRemove) {
			world.destroyBody(e.body);
			e.dispose();
		}
		toRemove.clear();

		float lerp = 3f;
		Vector3 cam = camera.position;
		Vector2 pos = new Vector2(player.position.x, player.position.y + 1);
		// points camera above player
		cam.x += (pos.x - cam.x) * lerp * delta;
		cam.y += (pos.y - cam.y) * lerp * delta;
		camera.update();

		doPhysicsStep(delta);
	}

	private boolean checkOutOfBounds(Entity e) {
		Vector2 p = e.position;
		return (Math.abs(p.x) > 60 || Math.abs(p.y) > 50);
	}


	private void doPhysicsStep(float delta) {
		float frameTime = Math.min(delta, 0.25f);
		float timeStep = 1/60f;
		accumulator += frameTime;
		while (accumulator >= timeStep) {
			world.step(timeStep, 6, 2);
			accumulator -= timeStep;
		}
	}

	private void processMap(TiledMap map) {
		BodyDef staticBody = new BodyDef();
		staticBody.type = BodyType.StaticBody;
		playerDef = new BodyDef();
		playerDef.type = BodyType.DynamicBody;

		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int column = 0; column < layer.getWidth(); column++) {
				TiledMapTileLayer.Cell c = layer.getCell(column, row);

				if (c != null) {
					TiledMapTile t = c.getTile();

					if (t.getId() == 1) { // ground
						Body groundBody = world.createBody(staticBody);
						new Ground(groundBody, new Vector2(column + 0.5f, row + 0.5f));
					} else if (t.getId() == 2) { // player
						body = world.createBody(playerDef);
						player = new Player(body, new Vector2(column, row), t.getTextureRegion());
						t.setTextureRegion(new TextureRegion(new Texture("images/blank.png")));
					} else if (t.getId() == 4) { // spikes
						Body spikesBody = world.createBody(staticBody);
						new Spikes(spikesBody, new Vector2(column + 0.5f, row + 0.5f));
					}
				}
			}
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
		music.dispose();
		map.dispose();
		renderer.dispose();
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