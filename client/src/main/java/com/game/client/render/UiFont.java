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
     * Loads fonts from two TTF files — one decorative for titles,
     * one clean for body and small text.
     *
     * @param titleFontPath path to the decorative title font
     * @param bodyFontPath  path to the clean readable font
     * @return a loaded UiFont instance
     */
    public static UiFont load(String titleFontPath, String bodyFontPath) {

        String chars = FreeTypeFontGenerator.DEFAULT_CHARS
                + ".:/ 0123456789";

        // --- title font ---
        FreeTypeFontGenerator titleGenerator = new FreeTypeFontGenerator(
                Gdx.files.internal(titleFontPath)
        );
        FreeTypeFontParameter titleParams = new FreeTypeFontParameter();
        titleParams.characters = chars;
        titleParams.packer = new PixmapPacker(
                1024, 1024, Pixmap.Format.RGBA8888, 2, false
        );
        titleParams.size = 32;
        BitmapFont title = titleGenerator.generateFont(titleParams);
        titleParams.packer.dispose();
        titleGenerator.dispose();

        // --- body font ---
        FreeTypeFontGenerator bodyGenerator = new FreeTypeFontGenerator(
                Gdx.files.internal(bodyFontPath)
        );
        FreeTypeFontParameter bodyParams = new FreeTypeFontParameter();
        bodyParams.characters = chars;
        bodyParams.packer = new PixmapPacker(
                1024, 1024, Pixmap.Format.RGBA8888, 2, false
        );

        bodyParams.size = 15;
        BitmapFont small = bodyGenerator.generateFont(bodyParams);

        bodyParams.size = 19;
        BitmapFont body = bodyGenerator.generateFont(bodyParams);

        bodyParams.packer.dispose();
        bodyGenerator.dispose();

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