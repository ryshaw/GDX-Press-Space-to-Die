package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class StartScreen implements Screen {
	private final Main game;
	private final Stage stage;
	Music music;
	Sound click;

	StartScreen(final Main game) {
		this.game = game;
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		click = Gdx.audio.newSound(Gdx.files.internal("audio/click.wav"));

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("andina.fnt"));
		labelStyle.font = bitmapFont;
		labelStyle.fontColor = Color.BLACK;

		Label title = new Label("press space to die", labelStyle);
		GlyphLayout layout = new GlyphLayout(bitmapFont, title.getText());
		float scale = 2f;
		title.setSize(layout.width * scale, layout.height * scale);
		title.setFontScale(scale);
		title.setPosition(800 - title.getWidth() / 2, 740);
		title.setAlignment(Align.center);
		stage.addActor(title);

		Label start = new Label("start", labelStyle);
		layout = new GlyphLayout(bitmapFont, start.getText());
		scale = 1.5f;
		start.setSize(layout.width * scale, layout.height * scale);
		start.setFontScale(scale);
		start.setBounds(0, 0, start.getWidth(), start.getHeight());
		start.setPosition(800 - start.getWidth() / 2, 300);
		start.setAlignment(Align.center);
		stage.addActor(start);
		start.addListener(new LabelListener(start) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				music.stop();
				click.play(0.5f);
				game.currentScreen.dispose();
				game.currentScreen = new GameScreen(game, 1);
				game.setScreen(game.currentScreen);
				return true;
			}
		});


		Label quit = new Label("quit", labelStyle);
		layout = new GlyphLayout(bitmapFont, quit.getText());
		quit.setSize(layout.width * scale, layout.height * scale);
		quit.setFontScale(scale);
		quit.setBounds(0, 0, quit.getWidth(), quit.getHeight());
		quit.setPosition(800 - quit.getWidth() / 2, 180);
		quit.setAlignment(Align.center);
		stage.addActor(quit);
		quit.addListener(new LabelListener(quit) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				click.play(0.5f);
				event.getStage().dispose();
				Gdx.app.exit();
				return true;
			}
		});

		Label credit = new Label("a game by Ryan Shaw \n ryshaw.itch.io", labelStyle);
		layout = new GlyphLayout(bitmapFont, credit.getText());
		scale = 0.5f;
		credit.setSize(layout.width * scale, layout.height * scale);
		credit.setFontScale(scale);
		credit.setPosition(1600 - credit.getWidth(), 20);
		credit.setAlignment(Align.center);
		stage.addActor(credit);

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/rhinoceros-by-kevin-macleod.mp3"));
		music.setVolume(0.2f);
		music.setLooping(true);
		music.play();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.begin();
		game.batch.end();
		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
		music.dispose();
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