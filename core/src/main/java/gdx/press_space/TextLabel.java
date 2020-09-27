package gdx.press_space;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TextLabel extends Label {
	BitmapFont font1 = new BitmapFont(Gdx.files.internal("andina.fnt"));
	BitmapFont font2 = new BitmapFont(Gdx.files.internal("courier.fnt"));

	TextLabel(CharSequence text, int s, float scale, int align) {
		super(text, new LabelStyle(new BitmapFont(), Color.BLACK));

		int w = Gdx.graphics.getWidth();
		float trueScale = (w * scale) / 1920;

		this.setStyle(getStyle(s));
		GlyphLayout layout = new GlyphLayout(this.getStyle().font, this.getText());
		this.setSize(layout.width * trueScale, layout.height * trueScale);
		this.setFontScale(trueScale);
		this.setAlignment(align);
		this.setBounds(0, 0, this.getWidth(), this.getHeight());
	}

	private LabelStyle getStyle(int s) {
		LabelStyle labelStyle = new Label.LabelStyle();
		switch (s) {
			case 1: // normal text
				labelStyle.font = font1;
				labelStyle.fontColor = Color.BLACK;
				break;
			case 2: // level complete
				labelStyle.font = font1;
				labelStyle.fontColor = Color.WHITE;
				break;
			case 3: // credits
				labelStyle.font = font2;
				labelStyle.fontColor = Color.BLACK;
				break;
		}
		return labelStyle;
	}
}
