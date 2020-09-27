package gdx.press_space;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
	SpriteBatch batch;
	Screen currentScreen;
	static float VOLUME = 0.4f;
	static float[] levelTimes = new float[3];
	static int totalDeaths;

	@Override
	public void create() {
		batch = new SpriteBatch();
		levelTimes[0] = 0f;
		levelTimes[1] = 0f;
		levelTimes[2] = 0f;

		this.currentScreen = new StartScreen(this);
		this.setScreen(currentScreen);
	}

	@Override
	public void render() { super.render(); }

	@Override
	public void dispose() {
		batch.dispose();
		currentScreen.dispose();
	}
}