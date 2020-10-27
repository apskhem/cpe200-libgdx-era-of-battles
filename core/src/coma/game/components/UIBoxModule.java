package coma.game.components;

final public class UIBoxModule {

    private final Renderable[] moduleList;

    private boolean isVisible = true;

    public UIBoxModule(final Renderable ...moduleList) {
        this.moduleList = moduleList;
    }

    public void setVisibility(final boolean value) {
        for (final Renderable canvas : this.moduleList) {
            canvas.isVisible = value;
        }

        this.isVisible = value;
    }

    public boolean isVisible() {
        return this.isVisible;
    }
}
