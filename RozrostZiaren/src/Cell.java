import javafx.scene.paint.Color;

public class Cell {
	private Color color;
    private boolean state;
    
    public Cell(){
        this.color = Color.WHITE;
        this.state = false;
    }

    
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
