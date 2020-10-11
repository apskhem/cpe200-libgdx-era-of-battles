package coma.game;

import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class UIController {

    private final ArrayList<Canvas> canvasObjects = new ArrayList();
    private final ArrayList<TextBox> textBoxes = new ArrayList();
    private final Hashtable<String, UIBoxModule> boxModuleHashtable = new Hashtable();
    private Camera camera;

    public UIController(Camera refCamera) {
        this.camera = refCamera;
    }

    public void AddComponents(Canvas ...canvases) {
        Collections.addAll(this.canvasObjects, canvases);
    }

    public void AddComponents(TextBox ...textBoxes) {
        Collections.addAll(this.textBoxes, textBoxes);
    }

    public ArrayList<Object> GetComponents() {
        final ArrayList<Object> outArr =  new ArrayList<Object>();
        outArr.addAll(this.canvasObjects);
        outArr.addAll(this.textBoxes);
        return outArr;
    }

    public void AddBoxModule(String name, Renderable ...moduleList) {
        this.boxModuleHashtable.put(name, new UIBoxModule(moduleList));
    }

    public UIBoxModule GetBoxModule(String name) {
        return this.boxModuleHashtable.get(name);
    }

    public void Update() {
        float dx = this.camera.position.x - this.camera.viewportWidth / 2;

        for (final Canvas img : this.canvasObjects) {
            img.src.setPosition(img.x + dx, img.y);
        }

        for (final TextBox textBox : this.textBoxes) {
            textBox.translateX = dx;
        }
    }
}

class UIBoxModule {

    private final Renderable[] moduleList;

    public UIBoxModule(Renderable ...moduleList) {
        this.moduleList = moduleList;
    }

    public void SetVisibility(boolean value) {
        for (final Renderable canvas : this.moduleList) {
            canvas.isVisible = value;
        }
    }
}
