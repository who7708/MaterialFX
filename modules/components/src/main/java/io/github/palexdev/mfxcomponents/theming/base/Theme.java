/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcomponents.theming.base;

import io.github.palexdev.mfxcomponents.theming.Deployer;
import io.github.palexdev.mfxcomponents.theming.UserAgentBuilder;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Public API for all MaterialFX themes/stylesheets. The bare minimum for every theme is their content, which should be
 * loaded by {@link #load()}. Depending on the implementation, the content could be raw data or a URL resource
 * (see {@link RawTheme} and {@link StylesheetTheme}).
 * <p>
 * Optionally, themes can be loaded with automatic caching by using {@link #load()} or {@link #loadCached(boolean)}.
 * <p></p>
 * Until JavaFX adds support for Themes and multiple user agent stylesheets, this in combination with {@link UserAgentBuilder},
 * offers a workaround for it. I noticed JavaFX themes were correctly merged but were still missing something: assets.
 * Their themes use images that unfortunately cannot be retrieved after the merge unless...we deploy them.
 * <p>
 * I was thinking about a possible solution and the only idea I came up with was to copy the necessary assets on the disk,
 * and then during post-processing correct the relative paths to point to the resources on the disk.
 * <p>
 * So, for now, the API has been extended to allow themes to deploy any kind of resources they need. The assets should
 * all be contained in a zip file as then the {@link Deployer} class will extract its contents when {@link #deploy()} is
 * invoked. All the deployment methods have been made {@code default}, in other words optional. The {@link #assets()} method
 * by default returns null, this indicates to the {@link Deployer} that there's nothing to do.
 *
 * @see Deployer
 */
public interface Theme {

    /**
     * @return the theme's name
     */
    default String name() {
        return null;
    }

    /**
     * @return the theme's data/css
     */
    String load();

    /**
     * Delegates to {@link #loadCached(boolean)}, does not override.
     */
    default String loadCached() {
        return loadCached(false);
    }

    /**
     * Responsible for loading and caching the theme's css for faster subsequent loading.
     * <p>
     * If the {@code override} parameter is true the cache will be invalidated.
     */
    default String loadCached(boolean override) {
        if (!Helper.isCached(this) || override) {
            return Helper.cacheTheme(this);
        }
        return Helper.getCachedTheme(this);
    }

    /**
     * @return the stream to the theme's assets, these are expected to be contained in a zip file
     */
    default InputStream assets() {
        return null;
    }

    /**
     * Tells the {@link Deployer} to deploy this theme's resources.
     * <p>
     * This operation does not happen if the {@link #deployName()} is {@code null}.
     *
     * @see Deployer#deploy(Theme)
     */
    default void deploy() {
        if (deployName() == null) return;
        try {
            Deployer.instance().deploy(this);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to deploy theme: " + name(), ex);
        }
    }

    /**
     * This is used by the {@link Deployer} to identify the theme in its cache map, and it is also the parent folder
     * in which assets will be extracted on the disk.
     * <p></p>
     * By default, this is the {@link #name()} in lower case.
     */
    default String deployName() {
        return Optional.ofNullable(name()).map(String::toLowerCase).orElse(null);
    }

    /**
     * Tells the {@link Deployer} to delete any deployed files from the disk and memory.
     *
     * @see Deployer#clean(Theme)
     */
    default void clean() {
        Deployer.instance().clean(this);
    }

    /**
     * @return whether the theme has been already deployed before by the {@link Deployer}. Beware, this is just a check
     * to see if the deployment is in the cache map. No checks are done on the file system as it would be too costly.
     * And this is true for {@link #deploy()} too, files will be extracted not matter if they are/are not on the disk
     */
    default boolean isDeployed() {
        return Deployer.instance().getDeployed(this) != null;
    }

    /**
     * Applies the theme as the global user agent stylesheet, see {@link Application#setUserAgentStylesheet(String)}.
     * <p></p>
     * Not implemented by default!!
     */
    default void applyGlobal() {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the loaded theme to the given {@link Scene}.
     * <p></p>
     * Not implemented by default!!
     */
    default void applyOn(Scene scene) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the loaded theme to the given {@link Parent}.
     * <p></p>
     * Not implemented by default!!
     */
    default void applyOn(Parent parent) {
        throw new UnsupportedOperationException();
    }

    //================================================================================
    // Helper
    //================================================================================
    class Helper {
        private static final Map<Theme, String> CACHE = new HashMap<>();

        public static boolean isCached(Theme theme) {
            return CACHE.containsKey(theme);
        }

        public static String cacheTheme(Theme theme) {
            String content = theme.load();
            if (content != null) CACHE.put(theme, content);
            return content;
        }

        public static String getCachedTheme(Theme theme) {
            return CACHE.get(theme);
        }
    }

    //================================================================================
    // Impl
    //================================================================================

    /**
     * A simple implementation of {@link Theme} to be used for raw CSS data.
     * <p>
     * For example, this is ideal when generating themes at runtime.
     */
    class RawTheme implements Theme {
        private final String css;

        public RawTheme(String css) {
            this.css = css;
        }

        public static RawTheme wrap(String css) {
            return new RawTheme(css);
        }

        @Override
        public String load() {
            return css;
        }

        /**
         * Caching is disabled for raw themes, just returns the wrapped css content.
         */
        @Override
        public String loadCached(boolean override) {
            return css;
        }

        @Override
        public void applyGlobal() {
            new CSSFragment(css).setGlobal();
        }

        @Override
        public void applyOn(Scene scene) {
            new CSSFragment(css).applyOn(scene);
        }

        @Override
        public void applyOn(Parent parent) {
            new CSSFragment(css).applyOn(parent);
        }
    }

    /**
     * Implementation of {@link Theme} to be used for themes loaded from URL resources.
     * <p>
     * Specifies all the needed properties for loading and deployment.
     */
    class StylesheetTheme implements Theme {
        private final String name;
        private final URL url;
        private final InputStream assets;

        public StylesheetTheme(String name, URL url) {
            this(name, url, null);
        }

        public StylesheetTheme(String name, URL url, InputStream assets) {
            this.name = name;
            this.url = url;
            this.assets = assets;
        }

        public URL getUrl() {
            return url;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String load() {
            try (InputStream is = url.openStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        public InputStream assets() {
            return assets;
        }

        @Override
        public void applyGlobal() {
            Application.setUserAgentStylesheet(url.toExternalForm());
        }

        @Override
        public void applyOn(Scene scene) {
            scene.getStylesheets().add(url.toExternalForm());
        }

        @Override
        public void applyOn(Parent parent) {
            parent.getStylesheets().add(url.toExternalForm());
        }
    }
}
