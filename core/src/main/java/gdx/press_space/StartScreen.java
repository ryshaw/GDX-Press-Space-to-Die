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

public class StartScreen implements Screen {
	private final Stage stage;
	Music music;
	Sound click;
	boolean credits;

	StartScreen(final Main game) {
		Camera camera = new OrthographicCamera();
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		stage = new Stage(new FitViewport(w, h, camera));
		Gdx.input.setInputProcessor(stage);
		click = Gdx.audio.newSound(Gdx.files.internal("audio/click.wav"));
		credits = false;

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("andina.fnt"));
		labelStyle.font = bitmapFont;
		labelStyle.fontColor = Color.BLACK;

		Label.LabelStyle creditsStyle = new Label.LabelStyle();
		BitmapFont f = new BitmapFont(Gdx.files.internal("courier.fnt"));
		creditsStyle.font = f;
		creditsStyle.fontColor = Color.BLACK;

		Label title = new Label("press space to die", labelStyle);
		GlyphLayout layout = new GlyphLayout(bitmapFont, title.getText());
		float scale = 2f;
		title.setSize(layout.width * scale, layout.height * scale);
		title.setFontScale(scale);
		title.setPosition(w / 2f - title.getWidth() / 2, h * 0.85f);
		title.setAlignment(Align.center);
		stage.addActor(title);

		Label start = new Label("start", labelStyle) {
			public void act(float delta) { this.setVisible(!credits); }
		};
		layout = new GlyphLayout(bitmapFont, start.getText());
		scale = 1.5f;
		start.setSize(layout.width * scale, layout.height * scale);
		start.setFontScale(scale);
		start.setBounds(0, 0, start.getWidth(), start.getHeight());
		start.setPosition(w / 2f - start.getWidth() / 2, h * (0.45f));
		start.setAlignment(Align.center);
		stage.addActor(start);
		start.addListener(new LabelListener(start) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				music.stop();
				click.play(Main.VOLUME);
				game.currentScreen.dispose();
				game.currentScreen = new GameScreen(game, 1);
				game.setScreen(game.currentScreen);
				return true;
			}
		});


		Label quit = new Label("quit", labelStyle) {
			public void act(float delta) { this.setVisible(!credits); }
		};
		layout = new GlyphLayout(bitmapFont, quit.getText());
		quit.setSize(layout.width * scale, layout.height * scale);
		quit.setFontScale(scale);
		quit.setBounds(0, 0, quit.getWidth(), quit.getHeight());
		quit.setPosition(w / 2f - quit.getWidth() / 2, h * (0.15f));
		quit.setAlignment(Align.center);
		stage.addActor(quit);
		quit.addListener(new LabelListener(quit) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				click.play(Main.VOLUME);
				Gdx.app.exit();
				return true;
			}
		});

		Label credit = new Label("a game by Ryan Shaw", labelStyle) {
			public void act(float delta) { this.setVisible(!credits); }
		};
		layout = new GlyphLayout(bitmapFont, credit.getText());
		scale = 0.5f;
		credit.setSize(layout.width * scale, layout.height * scale);
		credit.setFontScale(scale);
		credit.setPosition(w - 5 - credit.getWidth(), credit.getHeight());
		credit.setAlignment(Align.center);
		stage.addActor(credit);


		String attribution = getCredits();
		Label longCredits = new Label(attribution, creditsStyle) {
			public void act(float delta) { this.setVisible(credits); }
		};
		layout = new GlyphLayout(f, longCredits.getText());
		scale = 1f;
		longCredits.setSize(layout.width * scale, layout.height * scale);
		longCredits.setFontScale(scale);
		longCredits.setPosition(w / 2f - longCredits.getWidth() / 2, h * 0.2f);
		longCredits.setAlignment(Align.left);
		stage.addActor(longCredits);


		Label creditButton = new Label("credits", labelStyle) {
			public void act(float delta) { this.setVisible(!credits); }
		};
		scale = 1.5f;
		layout = new GlyphLayout(bitmapFont, creditButton.getText());
		creditButton.setSize(layout.width * scale, layout.height * scale);
		creditButton.setFontScale(scale);
		creditButton.setBounds(0, 0, creditButton.getWidth(), creditButton.getHeight());
		creditButton.setPosition(w / 2f - creditButton.getWidth() / 2, h * (0.3f));
		creditButton.setAlignment(Align.center);
		stage.addActor(creditButton);
		creditButton.addListener(new LabelListener(creditButton) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				click.play(Main.VOLUME);
				credits = true;
				return true;
			}
		});

		Label back = new Label("back", labelStyle) {
			public void act(float delta) { this.setVisible(credits); }
		};
		layout = new GlyphLayout(bitmapFont, back.getText());
		back.setSize(layout.width * scale, layout.height * scale);
		back.setFontScale(scale);
		back.setBounds(0, 0, back.getWidth(), back.getHeight());
		back.setPosition(w / 2f - back.getWidth() / 2, h * 0.05f);
		back.setAlignment(Align.center);
		stage.addActor(back);
		back.addListener(new LabelListener(back) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				click.play(Main.VOLUME);
				credits = false;
				return true;
			}
		});

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/rhinoceros-by-kevin-macleod.mp3"));
		music.setVolume(Main.VOLUME);
		music.setLooping(true);
		music.play();
	}

	private String getCredits() {
		return "Created by Ryan Shaw\n" +
				"ryshaw.itch.io\n" +
				"github.com/ryshaw\n" +
				"\n" +
				"Font: Andina by Federico Abuye\n" +
				"Link: http://www.federicoabuye.com.ar\n" +
				"License: http://creativecommons.org/licenses/by/3.0/\n" +
				"\n" +
				"The Lift by Kevin MacLeod\n" +
				"Link: https://incompetech.filmmusic.io/song/5009-the-lift\n" +
				"License: http://creativecommons.org/licenses/by/4.0/\n" +
				"\n" +
				"Rhinoceros by Kevin MacLeod\n" +
				"Link: https://incompetech.filmmusic.io/song/4284-rhinoceros\n" +
				"License: http://creativecommons.org/licenses/by/4.0/\n" +
				"\n" +
				"Jump sound: https://freesound.org/people/n_audioman/sounds/273566/\n" +
				"Click sound: https://freesound.org/people/Fupicat/sounds/471936/\n" +
				"Collect sound: https://freesound.org/people/Cabeeno%20Rossley/sounds/126413/\n" +
				"Death/respawn sound: https://freesound.org/people/notchfilter/sounds/43696/";
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