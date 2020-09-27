package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StoryScreen implements Screen {
	private final Main game;
	private final Stage stage;
	Sound click;
	Array<Label> dialogue;
	int dialogueOption;
	Image dialogueBox, storyBackground;
	float coolDown;
	Array<String> strings;
	boolean faded, fading;
	float fadeConstant;

	StoryScreen(final Main game) {
		this.game = game;
		Camera camera = new OrthographicCamera();
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		stage = new Stage(new FitViewport(w, h, camera));
		Gdx.input.setInputProcessor(stage);
		click = Gdx.audio.newSound(Gdx.files.internal("audio/click.wav"));
		storyBackground = new Image(new Texture("images/story.png"));
		dialogueBox = new Image(new Texture("images/dialogue_box.png"));
		storyBackground.setWidth(w);
		storyBackground.setHeight(h);
		storyBackground.setScaling(Scaling.fill);
		dialogueBox.setWidth(w * 0.6f);
		dialogueBox.setHeight(h * 0.2f);
		dialogueBox.setScaling(Scaling.fill);
		stage.addActor(storyBackground);
		stage.addActor(dialogueBox);
		dialogueBox.setPosition(w / 2f - dialogueBox.getWidth() / 2, h * 0.78f);
		storyBackground.setPosition(0, 0);
		dialogue = new Array<>();
		strings = new Array<>();
		getStrings();

		for (String s : strings) {
			Label dialog = new TextLabel(s, 1, 0.63f, Align.left);
			dialog.setPosition(w * 0.208f, h * 0.82f);
			stage.addActor(dialog);
			dialogue.add(dialog);
		}

		dialogueOption = -1;
		this.setDialogVisible(dialogueOption);

		Label skip = new TextLabel("skip", 2, 0.5f, Align.center);
		skip.setPosition(w - 10 - skip.getWidth(), skip.getHeight() / 2);
		stage.addActor(skip);
		skip.addListener(new LabelListener(skip) {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				click.play(Main.VOLUME);
				startGame();
				return true;
			}
		});
		coolDown = 0f;
		stage.addAction(Actions.fadeOut(0));
		faded = true;
		fading = true;
		fadeConstant = 2;
	}

	void setDialogVisible(int n) { for (int i = 0; i < dialogue.size; i++) { dialogue.get(i).setVisible(i == n); } }

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f); // BFCCE6 is normal background color
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();

		if (fading && faded && stage.getRoot().getActions().isEmpty()) {
			stage.addAction(Actions.fadeIn(fadeConstant));
			faded = false;

		} else if (fading && stage.getRoot().getActions().isEmpty()) {
			stage.addAction(Actions.fadeOut(fadeConstant));
			faded = true;
		}
		coolDown -= delta;
		if (coolDown < 0 && Gdx.input.justTouched()) {
			coolDown = 0.3f;
			if (dialogueOption == 2) {
				stage.getRoot().clearActions();
				stage.addAction(Actions.fadeIn(1f));
				fading = false;
			}
			if (dialogueOption == 28) {
				fading = true;
				fadeConstant = 0.3f;
				coolDown = 2f;
			}
			if (dialogueOption == 29) {
				fading = false;
				stage.getRoot().clearActions();
				stage.addAction(Actions.fadeIn(3f));
				coolDown = 2f;
			}
			if (dialogueOption == 30) { coolDown = 0.6f; }
			dialogueOption++;
			if (dialogueOption >= 43) startGame();
		}
		this.setDialogVisible(dialogueOption);
	}

	private void startGame() {
		stage.getRoot().clearActions();
		game.currentScreen = new GameScreen(game, 1);
		game.setScreen(game.currentScreen);
	}

	@Override
	public void dispose() { stage.dispose(); }

	private void getStrings() {
		strings.add("... [click anywhere]");
		strings.add("Well, it's not my problem they made an \nanthropomorphic zebra. That project belongs \nto Lab 7, not Lab 2.");
		strings.add("...");
		strings.add("So? Again, it's not our project. I don't care that \nhe... she... it got into the Library!");
		strings.add("...");
		strings.add("Okay... yes, commander...");
		strings.add("...");
		strings.add("Huh?");
		strings.add("Wait... hold on... it's waking up! I gotta go!");
		strings.add("*click*");
		strings.add("...uh...ahem... Hello, subject.");
		strings.add("You're probably wondering who you are and \nwhy you're here. Don't worry, I can give you \nsome answers.");
		strings.add("I am Dr. Hex, director of Lab 2. Our assigned \nproject deals with parallel worlds.");
		strings.add("You, are our beautiful creation. We've spent \nyears working towards this moment.");
		strings.add("You see, there's this huge theory about \nthe multiverse, and how there's infinite parallel \nworlds, blah blah blah...");
		strings.add("With our work, we've discovered that the \nmultiverse does exist, and there are, in fact, \ninfinite parallel universes.");
		strings.add("So, what makes you and me different from \nany other parallel world? Very little, actually. \nAn infinitely low difference.");
		strings.add("This means there are infinite copies of \nyou across infinite parallel worlds. \nExciting, right?");
		strings.add("The implications of this are huge. What is \nlife and death if we all exist as an infinite? \nInfinite lives are made and infinite are ended.");
		strings.add("Sorry, I'm getting sidetracked here. This is \njust so groundbreaking, I can barely keep \nmyself together!");
		strings.add("We created you to see if we could bring life \nfrom other worlds over to our world.");
		strings.add("If we created you, there must be infinite \nparallel worlds in which you are created there \nas well, by their version of Lab 2.");
		strings.add("We've connected the dots, and we can \nscientifically bring a parallel copy of you into \nour world. One of infinitely many.");
		strings.add("But that brings up all sorts of questions \nphilosophically. How can there be two... yous? \nWho is the real you?");
		strings.add("I'm sure you get what I'm implying.");
		strings.add("For us to bring in another you, \nwe must first kill you, our world's you.");
		strings.add("Then, there's only one you for our \nphilosophers and our universe to worry about. \nSimple, right?");
		strings.add("Ah, don't worry, your death will be \npainless. It's all in the name of science!");
		strings.add("Let me just hit this button... and...\n");
		strings.add("Boom!");
		strings.add("..."); // 30
		strings.add("Wow... I think we did it.");
		strings.add("How was the trip? Probably didn't feel much, \nright? Maybe a little ringing in the ears. \nHappens when you travel across dimensions.");
		strings.add("How impressive. We achieved immortality. \nOr something like that.");
		strings.add("Hold on... the commander's calling me again. \nHe's been in such a bad mood ever since that \nhuman-like zebra tore up the Library.");
		strings.add("I'll just call him back later. Not like I can \ndo anything about it. Wait... what if... maybe \nthat could work...");
		strings.add("Okay, it's time for your first mission. This \nzebra... thing... has scattered the sacred \nartifacts of our Library across our world.");
		strings.add("We need someone to retrieve them. It's an \ninsanely dangerous mission, and normally \nanyone in their right mind would not go.");
		strings.add("But you are different. You do not fear death. \nYou are the only thing that can die to succeed. \nOur greatest creation.");
		strings.add("In case your parallel world's controls are \ndifferent than ours, remember you can \nmove using WASD.");
		strings.add("I've installed our, uh, painless-death-gadget \nto your character. You can activate it by \npressing Space.");
		strings.add("So, what do you say? What have you got \nto lose, right? Ethics and philosophy don't \nmean anything any more!");
		strings.add("I'll send you off now, and let the commander \nknow. Go forth and conquer the unknown."); // 42
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