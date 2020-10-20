package coma.game.components;

final public class UIBoxModule {

    private final Renderable[] moduleList;

    private boolean isVisible = true;

    public UIBoxModule(final Renderable ...moduleList) {
        this.moduleList = moduleList;
    }

    public void SetVisibility(final boolean value) {
        for (final Renderable canvas : this.moduleList) {
            canvas.isVisible = value;
        }

        this.isVisible = value;
    }

    public boolean IsVisible() {
        return this.isVisible;
    }
}
