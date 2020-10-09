package coma.game;

import com.badlogic.gdx.graphics.Camera;

import java.util.Hashtable;

public class UIController {

    private final Hashtable<Integer, Canvas> canvasHashtable = new Hashtable();
    private final Hashtable<String, UIBoxModule> boxModuleHashtable = new Hashtable();
    private int hashMaxIndex;
    private Camera camera;

    public UIController(Camera refCamera) {
        this.camera = refCamera;
    }

    public void AddComponents(Canvas ...canvases) {
        for (final Canvas canvas : canvases) {
            this.canvasHashtable.put(this.hashMaxIndex, canvas);
            this.hashMaxIndex++;
        }
    }

    public void AddBoxModule(String name, Canvas ...moduleList) {
        this.boxModuleHashtable.put(name, new UIBoxModule(moduleList));
    }

    public UIBoxModule GetBoxModule(String name) {
        return this.boxModuleHashtable.get(name);
    }

    public void Update() {
        float dx = this.camera.position.x - this.camera.viewportWidth / 2;

        for (int i = 0; i < this.hashMaxIndex; i++) {
            final Canvas img = this.canvasHashtable.get(i);

            img.src.setPosition(img.x + dx, img.y);
        }
    }
}

class UIBoxModule {

    private final Canvas[] moduleList;

    public UIBoxModule(Canvas ...moduleList) {
        this.moduleList = moduleList;
    }

    public void SetVisibility(boolean value) {
        for (final Canvas canvas : this.moduleList) {
            canvas.SetVisibility(value);
        }
    }
}
