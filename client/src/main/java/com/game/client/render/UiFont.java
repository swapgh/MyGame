package com.game.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Loads and holds the shared UI fonts at multiple sizes.
 *
 * @since 0.1.0
 */
public final class UiFont {
    private static final String EXTRA_UI_CHARS =
            " \n\r\t"
                    + "0123456789"
                    + ".,;:!?\"'`´^~"
                    + "+-*/=_<>|\\/@#$%&()[]{}"
                    + "áéíóúÁÉÍÓÚàèìòùÀÈÌÒÙ"
                    + "äëïöüÄËÏÖÜâêîôûÂÊÎÔÛ"
                    + "ñÑçÇ¿¡";

    /** Small: labels, muted hints, info lines. */
    public final BitmapFont small;

    /** Body: field values, list rows, status lines. */
    public final BitmapFont body;

    /** Title: screen headings — decorative font. */
    public final BitmapFont title;

    private UiFont(BitmapFont small, BitmapFont body, BitmapFont title) {
        this.small = small;
        this.body = body;
        this.title = title;
    }

    /**
     * Loads fonts from a single TTF file and generates the sizes used by the UI.
     *
     * @param fontPath path to the font used by the UI
     * @return a loaded UiFont instance
     */
    public static UiFont load(String fontPath) {

        String chars = FreeTypeFontGenerator.DEFAULT_CHARS + EXTRA_UI_CHARS;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal(fontPath)
        );
        FreeTypeFontParameter params = new FreeTypeFontParameter();
        params.characters = chars;
        params.packer = new PixmapPacker(
                1024, 1024, Pixmap.Format.RGBA8888, 2, false
        );

        params.size = 15;
        BitmapFont small = generator.generateFont(params);

        params.size = 19;
        BitmapFont body = generator.generateFont(params);

        params.size = 32;
        BitmapFont title = generator.generateFont(params);

        params.packer.dispose();
        generator.dispose();

        return new UiFont(small, body, title);
    }

    /**
     * Disposes all generated fonts. Call from GameClient#dispose().
     */
    public void dispose() {
        small.dispose();
        body.dispose();
        title.dispose();
    }
}
