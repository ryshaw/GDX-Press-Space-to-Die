package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class WinScreen implements Screen {
	private final Stage stage;
	Music music;
	Sound click;

	WinScreen(final Main game) {
		Camera camera = new OrthographicCamera();
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		stage = new Stage(new FitViewport(w, h, camera));
		Gdx.input.setInputProcessor(stage);
		click = Gdx.audio.newSound(Gdx.files.internal("audio/click.wav"));

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("andina.fnt"));
		labelStyle.font = bitmapFont;
		labelStyle.fontColor = Color.BLACK;

		Label title = new Label("game complete!", labelStyle);
		GlyphLayout layout = new GlyphLayout(bitmapFont, title.getText());
		float scale = 2f;
		title.setSize(layout.width * scale, layout.height * scale);
		title.setFontScale(scale);
		title.setPosition(w / 2f - title.getWidth() / 2, h - title.getHeight());
		title.setAlignment(Align.center);
		stage.addActor(title);

		Label menu = new Label("main menu", labelStyle);
		layout = new GlyphLayout(bitmapFont, menu.getText());
		scale = 1f;
		menu.setSize(layout.width * scale, layout.height * scale);
		menu.setFontScale(scale);
		menu.setBounds(0, 0, menu.getWidth(), menu.getHeight());
		menu.setPosition(w / 2f - menu.getWidth() / 2, menu.getHeight() / 2);
		menu.setAlignment(Align.center);
		stage.addActor(menu);
		menu.addListener(new LabelListener(menu) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				music.stop();
				click.play(Main.VOLUME);
				game.currentScreen.dispose();
				game.currentScreen = new StartScreen(game);
				game.setScreen(game.currentScreen);
				return true;
			}
		});

		Label levelScore;
		for (int i = 1; i < 4; i++) {
			levelScore = new Label("level " + i + " time: " + Main.levelTimes[i - 1], labelStyle);
			layout = new GlyphLayout(bitmapFont, levelScore.getText());
			scale = 1.2f;
			levelScore.setSize(layout.width * scale, layout.height * scale);
			levelScore.setFontScale(scale);
			levelScore.setPosition(w / 2f - levelScore.getWidth() / 2, h * (0.85f) - (h * (i / 8f)));
			levelScore.setAlignment(Align.center);
			stage.addActor(levelScore);
		}

		Label totalDeaths = new Label("total deaths: " + Main.totalDeaths, labelStyle);
		layout = new GlyphLayout(bitmapFont, totalDeaths.getText());
		scale = 1.2f;
		totalDeaths.setSize(layout.width * scale, layout.height * scale);
		totalDeaths.setFontScale(scale);
		totalDeaths.setPosition(w / 2f - totalDeaths.getWidth() / 2, h * (1/5f));
		totalDeaths.setAlignment(Align.center);
		stage.addActor(totalDeaths);

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/rhinoceros-by-kevin-macleod.mp3"));
		music.setVolume(Main.VOLUME);
		music.setLooping(true);
		music.play();
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.75f, 0.8f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
		music.dispose();
	}

	@Override
	public void resize(int width, int height) { stage.getViewport().update(width, height, true); }

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