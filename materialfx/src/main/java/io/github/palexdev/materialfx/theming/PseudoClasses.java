package io.github.palexdev.materialfx.theming;


import javafx.css.PseudoClass;
import javafx.scene.Node;

public class PseudoClasses {
    //================================================================================
    // Properties
    //================================================================================
    public static final PseudoClass CURRENT = PseudoClass.getPseudoClass("current");
    public static final PseudoClass EXTRA = PseudoClass.getPseudoClass("extra");
    public static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

    //================================================================================
    // Constructors
    //================================================================================
    private PseudoClasses() {
    }

    //================================================================================
    // Methods
    //================================================================================
    public static void setOn(Node node, PseudoClass pseudoClass, boolean state) {
        node.pseudoClassStateChanged(pseudoClass, state);
    }
}
