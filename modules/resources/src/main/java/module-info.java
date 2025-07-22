module mfx.resources {
    requires mfx.effects;

    requires transitive javafx.graphics;

    // Root
    exports io.github.palexdev.mfxresources;

    // Builders
    exports io.github.palexdev.mfxresources.builders;

    // Icons
    exports io.github.palexdev.mfxresources.icon;
    exports io.github.palexdev.mfxresources.icon.packs;

    // Utils
    exports io.github.palexdev.mfxresources.utils;
}