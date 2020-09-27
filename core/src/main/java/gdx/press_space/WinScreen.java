package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
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

		Label title = new TextLabel("game complete!", 1, 2f, Align.center);
		title.setPosition(w / 2f - title.getWidth() / 2, h - title.getHeight());
		stage.addActor(title);

		Label menu = new TextLabel("main menu", 1, 1f, Align.center);
		menu.setPosition(w / 2f - menu.getWidth() / 2, menu.getHeight() / 2);
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
			levelScore = new TextLabel("level " + i + " time: " + Main.levelTimes[i - 1], 1, 1.2f, Align.center);
			levelScore.setPosition(w / 2f - levelScore.getWidth() / 2, h * (0.85f) - (h * (i / 8f)));
			stage.addActor(levelScore);
		}

		float totalTimeToComplete = 0f;
		for (float f : Main.levelTimes) { totalTimeToComplete += f; }
		totalTimeToComplete = MathUtils.round(totalTimeToComplete * 100f) / 100f;
		Label totalTime = new TextLabel("total: " + totalTimeToComplete, 1, 1.2f, Align.center);
		totalTime.setPosition(w / 2f - totalTime.getWidth() / 2, h * 0.3f);
		stage.addActor(totalTime);

		Label totalDeaths = new TextLabel("total deaths: " + Main.totalDeaths, 1, 1.2f, Align.center);
		totalDeaths.setPosition(w / 2f - totalDeaths.getWidth() / 2, h * (1/5f));
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