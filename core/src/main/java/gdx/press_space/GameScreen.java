package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class GameScreen implements Screen {
	private final Main game;
	private final World world;
	private final Box2DDebugRenderer debugRenderer;
	private final FitViewport viewport;
	private final Stage stage;
	Player player;
	Body body;
	private float accumulator = 0;
	BodyDef playerDef;
	Array<Body> bodies;
	Array<Entity> toRemove;
	Music music;
	float respawnDelay;
	Sound respawn, collect, click;
	TiledMap map;
	OrthogonalTiledMapRenderer renderer;
	static float unitScale = 1/16f;
	int artifactsCollected;
	int numArtifacts;
	float timeToComplete;
	int deathCounter, lvl;

	ShapeRenderer shapeRenderer;

	GameScreen(final Main game, int level) {
		this.game = game;
		lvl = level;
		Box2D.init();
		world = new World(new Vector2(0, -20f), true);
		debugRenderer = new Box2DDebugRenderer();
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		float aspectRatio = (float) h / w;
		viewport = new FitViewport(30, 30 * aspectRatio, new OrthographicCamera());
		stage = new Stage(new FitViewport(w, h));
		Gdx.input.setInputProcessor(stage);

		createStage(w, h);

		numArtifacts = 0; // will be set when map is processed
		map = new TmxMapLoader().load("level" + lvl + ".tmx");
		processMap(map);
		renderer = new OrthogonalTiledMapRenderer(map, unitScale);

		world.setContactListener(new Box2DCollision(this));

		bodies = new Array<>();
		toRemove = new Array<>();

		shapeRenderer = new ShapeRenderer();

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/the-lift-by-kevin-macleod.mp3"));
		music.setVolume(Main.VOLUME);
		music.setLooping(true);
		music.play();

		respawnDelay = 0;
		respawn = Gdx.audio.newSound(Gdx.files.internal("audio/respawn.wav"));
		collect = Gdx.audio.newSound(Gdx.files.internal("audio/collect.wav"));
		click = Gdx.audio.newSound(Gdx.files.internal("audio/click.wav"));
		artifactsCollected = 0;
		timeToComplete = 0;
		deathCounter = 0;

		Vector3 cam = viewport.getCamera().position;
		Vector2 pos = player.position;
		// points camera above player
		cam.x = pos.x;
		cam.y = pos.y;
		viewport.getCamera().update();
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (artifactsCollected < numArtifacts) timeToComplete += delta;


		if (player.dead) {
			new Corpse(world.createBody(playerDef), player.deathPosition);
			player.dead = false;
			player.body.setActive(false);
			//world.destroyBody(player.body);
			respawnDelay = 1f;
			if (artifactsCollected < numArtifacts) deathCounter += 1;
		}

		if (respawnDelay > 0) {
			respawnDelay -= delta;
			if (respawnDelay < 0) {
				respawn.play(Main.VOLUME);
				player.initializeBody(player.spawnPosition);
			}
		}

		float d = (float) MathUtils.clamp(delta, 0, 0.05);
		world.getBodies(bodies);
		game.batch.setProjectionMatrix(viewport.getCamera().combined);
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

		renderer.setView((OrthographicCamera) viewport.getCamera());
		renderer.render();

		//debugRenderer.render(world, camera.combined);

		for (Entity e : toRemove) {
			world.destroyBody(e.body);
			e.dispose();
		}
		toRemove.clear();

		float lerp = 3f;
		Vector3 cam = viewport.getCamera().position;
		Vector2 pos = new Vector2(player.position.x, player.position.y + 1);
		// points camera above player
		cam.x += (pos.x - cam.x) * lerp * delta;
		cam.y += (pos.y - cam.y) * lerp * delta;
		viewport.getCamera().update();

		stage.getViewport().apply(true);
		stage.act();
		stage.draw();

		doPhysicsStep(delta);
	}

	private boolean checkOutOfBounds(Entity e) {
		Vector2 p = e.position;
		return (Math.abs(p.x) > 60 || p.y < 0);
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

	void collectArtifact(Artifact a) {
		toRemove.add(a);
		artifactsCollected += 1;
		collect.play(Main.VOLUME);
		if (artifactsCollected >= numArtifacts) {
			float t = MathUtils.round(timeToComplete * 100) / 100f;
			Main.levelTimes[lvl - 1] = t;
			Main.totalDeaths += deathCounter;
		}
	}

	private void createStage(int w, int h) {
		Label.LabelStyle labelStyle1 = new Label.LabelStyle();
		BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("andina.fnt"));
		labelStyle1.font = bitmapFont;
		labelStyle1.fontColor = Color.BLACK;

		Label.LabelStyle labelStyle2 = new Label.LabelStyle();
		labelStyle2.font = bitmapFont;
		labelStyle2.fontColor = Color.WHITE;

		Label restart = new Label("restart", labelStyle1);
		GlyphLayout layout = new GlyphLayout(bitmapFont, restart.getText());
		float scale = 0.3f;
		restart.setSize(layout.width * scale, layout.height * scale);
		restart.setFontScale(scale);
		restart.setBounds(0, 0, restart.getWidth(), restart.getHeight());
		restart.setPosition(w - restart.getWidth() - 5, h - restart.getHeight());
		restart.setAlignment(Align.topRight);
		stage.addActor(restart);
		restart.addListener(new LabelListener(restart) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				music.stop();
				click.play(Main.VOLUME);
				game.currentScreen.dispose();
				game.currentScreen = new GameScreen(game, lvl);
				game.setScreen(game.currentScreen);
				return true;
			}
		});

		Label numCollected = new Label("artifacts: " + artifactsCollected, labelStyle1) {
			@Override
			public void act(float delta) {
				super.act(delta);
				this.setText("artifacts: " + artifactsCollected);
			}
		};
		layout = new GlyphLayout(bitmapFont, numCollected.getText());
		scale = 0.3f;
		numCollected.setSize(layout.width * scale, layout.height * scale);
		numCollected.setFontScale(scale);
		numCollected.setBounds(0, 0, numCollected.getWidth(), numCollected.getHeight());
		numCollected.setPosition(5, h - numCollected.getHeight());
		numCollected.setAlignment(Align.topLeft);
		stage.addActor(numCollected);

		Label level = new Label("level " + lvl, labelStyle1);
		layout = new GlyphLayout(bitmapFont, level.getText());
		scale = 0.3f;
		level.setSize(layout.width * scale, layout.height * scale);
		level.setFontScale(scale);
		level.setBounds(0, 0, level.getWidth(), level.getHeight());
		level.setPosition(w / 2f - level.getWidth() / 2, h - level.getHeight());
		level.setAlignment(Align.top);
		stage.addActor(level);

		Label complete = new Label("level complete!", labelStyle2) {
			@Override
			public void act(float delta) { if (artifactsCollected >= numArtifacts) this.setVisible(true); }
		};
		layout = new GlyphLayout(bitmapFont, complete.getText());
		scale = 2f;
		complete.setSize(layout.width * scale, layout.height * scale);
		complete.setFontScale(scale);
		complete.setPosition(w / 2f - complete.getWidth() / 2, h * (4/5f));
		complete.setAlignment(Align.center);
		complete.setVisible(false);
		stage.addActor(complete);

		Label time = new Label("time: ", labelStyle2) {
			@Override
			public void act(float delta) {
				if (artifactsCollected >= numArtifacts) {
					this.setVisible(true);
					float t = MathUtils.round(timeToComplete * 100) / 100f;
					Main.levelTimes[lvl - 1] = t;
					this.setText("time: " + t);
				}
			}
		};
		layout = new GlyphLayout(bitmapFont, time.getText());
		scale = 1.5f;
		time.setSize(layout.width * scale, layout.height * scale);
		time.setFontScale(scale);
		time.setBounds(0, 0, time.getWidth(), time.getHeight());
		time.setPosition(w / 2f - time.getWidth() / 2, h * (3/5f));
		time.setAlignment(Align.bottom);
		time.setVisible(false);
		stage.addActor(time);

		Label deaths = new Label("deaths: ", labelStyle2) {
			@Override
			public void act(float delta) {
				if (artifactsCollected >= numArtifacts) {
					this.setVisible(true);
					this.setText("deaths: " + deathCounter);
				}
			}
		};
		layout = new GlyphLayout(bitmapFont, deaths.getText());
		scale = 1.5f;
		deaths.setSize(layout.width * scale, layout.height * scale);
		deaths.setFontScale(scale);
		deaths.setBounds(0, 0, deaths.getWidth(), deaths.getHeight());
		deaths.setPosition(w / 2f - deaths.getWidth() / 2, h * (2/5f));
		deaths.setAlignment(Align.bottom);
		deaths.setVisible(false);
		stage.addActor(deaths);

		Label nextLevel = new Label("next level", labelStyle2) {
			@Override
			public void act(float delta) {
				if (artifactsCollected >= numArtifacts) this.setVisible(true);
			}
		};
		layout = new GlyphLayout(bitmapFont, nextLevel.getText());
		scale = 1f;
		nextLevel.setSize(layout.width * scale, layout.height * scale);
		nextLevel.setFontScale(scale);
		nextLevel.setBounds(0, 0, nextLevel.getWidth(), nextLevel.getHeight());
		nextLevel.setPosition(w / 2f - nextLevel.getWidth() / 2, h * (1/5f));
		nextLevel.setAlignment(Align.bottom);
		nextLevel.setVisible(false);
		stage.addActor(nextLevel);
		nextLevel.addListener(new LabelListener(nextLevel) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (lvl == 3) {
					music.stop();
					click.play(Main.VOLUME);
					game.currentScreen.dispose();
					game.currentScreen = new WinScreen(game);
					game.setScreen(game.currentScreen);
				} else {
					music.stop();
					click.play(Main.VOLUME);
					game.currentScreen.dispose();
					game.currentScreen = new GameScreen(game, lvl + 1);
					game.setScreen(game.currentScreen);
				}
				return true;
			}
		});

	}

	private void processMap(TiledMap map) {
		BodyDef staticBody = new BodyDef();
		staticBody.type = BodyType.StaticBody;
		playerDef = new BodyDef();
		playerDef.type = BodyType.DynamicBody;
		BodyDef kinematicBody = new BodyDef();
		kinematicBody.type = BodyType.KinematicBody;

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
					} else if (t.getId() == 5) { // artifact
						numArtifacts += 1;
						Body artifactBody = world.createBody(kinematicBody);
						new Artifact(artifactBody, new Vector2(column + 0.5f, row + 0.5f));
						t.setTextureRegion(new TextureRegion(new Texture("images/blank.png")));
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
		respawn.dispose();
		collect.dispose();
		click.dispose();
		stage.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		stage.getViewport().update(width, height, true);
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