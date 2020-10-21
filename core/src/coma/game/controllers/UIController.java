package coma.game.controllers;

import coma.game.components.*;
import coma.game.MainGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

final public class UIController {

    private static final ArrayList<Canvas> canvasObjects = new ArrayList<>();
    private static final ArrayList<TextBox> textBoxes = new ArrayList<>();
    private static final Hashtable<String, UIBoxModule> boxModuleHashtable = new Hashtable<>();

    public static void AddComponents(final Canvas ...canvases) {
        Collections.addAll(UIController.canvasObjects, canvases);
    }

    public static void AddComponents(final TextBox ...textBoxes) {
        Collections.addAll(UIController.textBoxes, textBoxes);
    }

    public static ArrayList<Object> GetComponents() {
        final ArrayList<Object> outArr =  new ArrayList<>();
        outArr.addAll(UIController.canvasObjects);
        outArr.addAll(UIController.textBoxes);
        return outArr;
    }

    public static void AddBoxModule(final String name, final Renderable...moduleList) {
        UIController.boxModuleHashtable.put(name, new UIBoxModule(moduleList));
    }

    public static UIBoxModule GetBoxModule(final String name) {
        return UIController.boxModuleHashtable.get(name);
    }

    public static void Update() {
        float dx = MainGame.camera.position.x - MainGame.camera.viewportWidth / 2;

        for (final Canvas img : UIController.canvasObjects) {
            img.SetImagePosition(img.x + dx, img.y);
        }

        for (final TextBox textBox : UIController.textBoxes) {
            textBox.translateX = dx;
        }
    }
}
