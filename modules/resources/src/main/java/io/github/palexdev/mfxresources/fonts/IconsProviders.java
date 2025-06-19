/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxresources.fonts;

import java.io.InputStream;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;

import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeBrands;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeRegular;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.scene.text.Font;

/**
 * This enum contains all the "officially" supported icon fonts.
 */
public enum IconsProviders implements IconProvider {
    FONTAWESOME_BRANDS("FontAwesome/brands/FontAwesomeBrands.ttf", FontAwesomeBrands::toCode),
    FONTAWESOME_REGULAR("FontAwesome/regular/FontAwesomeRegular.ttf", FontAwesomeRegular::toCode),
    FONTAWESOME_SOLID("FontAwesome/solid/FontAwesomeSolid.ttf", FontAwesomeSolid::toCode),
    ;

    private static final NavigableMap<String, IconProvider> PROVIDERS = new TreeMap<>();

    static {
        registerProvider("fab-", FONTAWESOME_BRANDS);
        registerProvider("far-", FONTAWESOME_REGULAR);
        registerProvider("fas-", FONTAWESOME_SOLID);
    }

    private final String font;
    private final Function<String, Character> converter;

    IconsProviders(String font, Function<String, Character> converter) {
        this.font = font;
        this.converter = converter;
    }

    @Override
    public String getFontPath() {
        return font;
    }

    @Override
    public Function<String, Character> getConverter() {
        return converter;
    }

    @Override
    public InputStream load() {
        return MFXResources.loadFont(font);
    }

    /**
     * Registers the given {@link IconProvider} to the given prefix.
     * <p>
     * When {@link MFXFontIcon} is going to receive a description with such prefix, it's automatically going to use this
     * provider.
     * <p></p>
     * If a provider for a prefix is already present, it will be replaced with this new one.
     */
    public static void registerProvider(String prefix, IconProvider provider) {
        PROVIDERS.put(prefix, provider);
    }

    /**
     * Creates an anonymous {@link IconProvider} implementation that returns the given converter and font.
     * <p>
     * Delegates to {@link #registerProvider(String, IconProvider)}.
     */
    public static void registerProvider(String prefix, Font font, Function<String, Character> converter) {
        registerProvider(prefix, new IconProvider() {
            @Override
            public String getFontPath() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Function<String, Character> getConverter() {
                return converter;
            }

            @Override
            public InputStream load() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Font loadFont() {
                return font;
            }

            @Override
            public Font loadFont(double size) {
                return font;
            }
        });
    }

    /**
     * Given an icon descriptor as a String, attempts to return an {@link IconProvider} for its prefix.
     * <p>
     * If none is found returns {@code null}.
     */
    public static IconProvider getProvider(String description) {
        String prefix = PROVIDERS.floorKey(description);
        if (prefix != null && description.startsWith(prefix))
            return PROVIDERS.get(prefix);
        return null;
    }

    /**
     * Delegates to {@link #getProvider(String)}.
     */
    public static IconProvider getProvider(IconDescriptor descriptor) {
        return getProvider(descriptor.getDescription());
    }
}
