import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Controller {

    @FXML
    private Button buttonSend;

    @FXML
    private Button buttonStart;
    
    @FXML
    private Button buttonStop;
    
    @FXML
    private ComboBox<String> menu;
    
    @FXML
    private ComboBox<String> bc;
    
    @FXML
    private ComboBox<String> cells;
    
    @FXML
    private TextField row;

    @FXML
    private TextField column;

    @FXML
    private TextField numberOfGenerations;

    @FXML
    private TextField numberOfCells;

    @FXML
    private TextField rayy;
    
    @FXML
    public Canvas canvas;

    public GraphicsContext gc;

    private static int rows;
    private static int columns;
    private int generations;
    private int numcells;
    private int ray;

    Stage primaryStage;
    private static Cell[][] cell;
    private static double width;
    private static double height;
    Timeline tl;
    int neighbourhood=0;
    int location=0;
    static int boundry_cond=0;

    @FXML
    private void initialize() {
    	menu.getItems().addAll(
                "Moore",
                "Von Neumann",
                "Pentagonal up",
                "Pentagonal down",
                "Pentagonal left",
                "Pentagonal right",
                "Hexagonal 1",
                "Hexagonal 2" 
            );
    	
    	cells.getItems().addAll(
                "Random",
                "Evenly",
                "In ray",
                "Mouse click"
            );
    	
    	bc.getItems().addAll(
                "Periodical",
                "Absorbing"
            );
    }
    
    public void getData() {
        rows = Integer.parseInt(row.getText());
        columns = Integer.parseInt(column.getText());
        generations = Integer.parseInt(numberOfGenerations.getText());
        //numcells=Integer.parseInt(numberOfCells.getText());
        String msg=(String)menu.getValue();
        switch(msg){
        	case "Moore":
        		neighbourhood=1;
        		break;
        	case "Von Neumann":
        		neighbourhood=2;
        		break;
        	case "Pentagonal up":
        		neighbourhood=3;
        		break;
        	case "Pentagonal down":
        		neighbourhood=4;
        		break;
        	case "Pentagonal left":
        		neighbourhood=5;
        		break;
        	case "Pentagonal right":
        		neighbourhood=6;
        		break;
        	case "Hexagonal 1":
        		neighbourhood=7;
        		break;
        	case "Hexagonal 2":
        		neighbourhood=8;
        		break;
        }
        
        String msg3=(String)bc.getValue();
        switch(msg3){
        	case "Periodical":
        		boundry_cond=1;
        		break;
        	case "Absorbing":
        		boundry_cond=2;
        		break;
        }
        
        String msg2=(String)cells.getValue();
        switch(msg2){
        	case "Random":
        		location=1;
        		break;
        	case "Evenly":
        		location=2;
        		break;
        	case "In ray":
        		location=3;
        		break;
        	case "Mouse click":
        		location=4;
        		break;
        }
    }
    

    @FXML
    public void buttonhandleSend(ActionEvent actionEvent) throws InterruptedException {
        getData();
        cell = new Cell[columns][rows];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                cell[i][j] = new Cell();
            }
        }
        gc = canvas.getGraphicsContext2D();
        drawBoard(gc);
        if(location==1){
        	numcells=Integer.parseInt(numberOfCells.getText());
        	random(numcells);
        	colourCells(gc);
    	}
        else if(location==2){
        	//numcells=Integer.parseInt(numberOfCells.getText());
        	evenly();
    	}
        else if(location==3){
        	boolean bool=false;
        	Cell[][] tmpCells = new Cell[columns][rows];
            for (int i = 0; i < columns; i++) {
                for (int j = 0; j < rows; j++) {
                    tmpCells[i][j] = new Cell();
                }
            }
        	numcells=Integer.parseInt(numberOfCells.getText());
        	random(numcells);
        	ray=Integer.parseInt(rayy.getText());
        	for (int i = 0; i < columns; i++) {
                for (int j = 0; j < rows; j++) {
                	tmpCells[i][j]=cell[i][j];
                	if(tmpCells[i][j].getState()){
                		bool=checkArea(tmpCells, i, j, ray);		
                	}
                }
        	}

        	while (bool==false) {
        		random(numcells);
        		System.out.println("LOL");
        	}

            colourCells(gc);
    	}
        
        else if(location==4){
        	pointCells(gc);
    	}
       
    }
    
    public boolean checkArea(Cell[][] tmpCells, int i, int j, int ray) {
    	//Cell[][] tmpCells = new Cell[columns][rows];
    	for (int k = i - ray; k <= i + ray; k++) {
    		for (int l = j - ray; l <= j + ray; l++) {
    			try {
    				if (tmpCells[k][l].getState()) {
    					return false;
    				}
    				else{
    					return true;
    				}
    			} catch (Exception e) {
    			}
    		}
    	}
    	tmpCells=cell;
        return true;
    }
    
    
    @FXML
    public void buttonhandleStart(ActionEvent actionEvent) throws InterruptedException {
    	KeyFrame kf = new KeyFrame(Duration.seconds(0.5), e -> {
            nextGeneration();
            colourCells(gc);
        });

        tl = new Timeline(kf);
        tl.setCycleCount(generations);
        tl.play();
       
    }
    
    @FXML
    public void buttonhandleStop(ActionEvent actionEvent) throws InterruptedException {
    	tl.stop();
    	saveFile();
    }

    static void drawBoard(GraphicsContext gc) {
        width = gc.getCanvas().getWidth() / columns;
        height = gc.getCanvas().getHeight() / rows;
        gc.beginPath();
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);

        for (int i = 0; i < columns; i++) {
            gc.moveTo(width * i, 0);
            gc.lineTo(width * i, gc.getCanvas().getHeight());
        }
        for (int i = 0; i < rows; i++) {
            gc.moveTo(0, height * i);
            gc.lineTo(gc.getCanvas().getWidth(), height * i);
        }
        gc.stroke();
        gc.closePath();
    }

    static void colourCells(GraphicsContext gc) {
        drawBoard(gc);
        gc.beginPath();
        gc.setLineWidth(1);
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (cell[i][j].getState() == true) {
                    gc.setFill(cell[i][j].getColor());
                    gc.fillRect(width * i + 1, height * j + 1, width - 1, height - 1);
                } else {
                    gc.setFill(Color.WHITE);
                    gc.fillRect(width * i + 1, height * j + 1, width - 1, height - 1);
                }
            }
        }
        gc.fill();
        gc.closePath();
    }

    public void pointCells(GraphicsContext gc) {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                printCell(x, y);
            }
        });
    }

    public void printCell(double x_pos, double y_pos) {
        Random random = new Random();
        Cell[][] tmp = new Cell[columns][rows];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                tmp[i][j] = new Cell();
            }
        }
        int index_i = -2;
        int index_j = -2;
        for (int i = 0; i <= columns; i++) {
            //if (i < 0 || i>rows) continue;
            if (x_pos < width * i) {
                for (int j = 0; j <= rows; j++) {
                    //if (j < 0 || j>columns) continue;
                    if (y_pos < height * j) {
                        tmp[i - 1][j - 1].setState(true);
                        tmp[i - 1][j - 1].setColor(Color.color(Math.random(), Math.random(), Math.random()));
                        index_i = i - 1;
                        index_j = j - 1;
                        break;
                    } else {
                        continue;
                    }
                }
                break;
            } else {
                continue;
            }
        }
        if ((index_i > -2) && (index_j > -2)) {
            cell[index_i][index_j] = tmp[index_i][index_j];
            colourCells(gc);
        }
    }
    
    public void random(int number){
		int x;
		int y;
		Random random = new Random();
		for(int i =0; i<number; i++){
			x = random.nextInt(columns);
			y = random.nextInt(rows);
			cell[x][y].setState(true);
			cell[x][y].setColor(Color.color(Math.random(), Math.random(), Math.random()));
	    }
		//colourCells(gc);
	 }

    public void evenly(){
    	numcells=Integer.parseInt(numberOfCells.getText());
        int step = 7;
    	
        for (int i = step; i < columns-3; i += step) {
            for (int j = step; j < rows; j += step) {
            	if(cell[i][j].getState()==false){// && numCells<numcells){
            		cell[i][j].setState(true);
                	cell[i][j].setColor(Color.color(Math.random(), Math.random(), Math.random()));
                	//numCells++;
            	}
            }
        }
        colourCells(gc);
    }
    
    
    public void nextGeneration() {
        //Czysta tablica do ktorej bedziemy zbierac wszystkie wyniki funkcji moore dla calej generacji
        Cell[][] tmpCells = new Cell[columns][rows];
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                tmpCells[i][j] = new Cell();
            }
        }

        for (int i = 0; i < columns; i++) {
            //if(i < 0) continue;
            for (int j = 0; j < rows; j++) {
                //if(j < 0) continue;
                if (cell[i][j].getState() == true) {
                	if(neighbourhood==1){
                		moore(i, j, tmpCells);
                	}
                	else if(neighbourhood==2){
                		vonNeumann(i, j, tmpCells);
                	}
                	else if(neighbourhood==3){
                		pentagonal_up(i, j, tmpCells);
                	}
                	else if(neighbourhood==4){
                		pentagonal_down(i, j, tmpCells);
                	}
                	else if(neighbourhood==5){
                		pentagonal_right(i, j, tmpCells);
                	}
                	else if(neighbourhood==6){
                		pentagonal_left(i, j, tmpCells);
                	}
                	else if(neighbourhood==7){
                		hexagonal1(i, j, tmpCells);
                	}
                	else if(neighbourhood==8){
                		hexagonal2(i, j, tmpCells);
                	}
                }
            }
        }
        cell = tmpCells;
    }


    public void moore(int x, int y, Cell[][] tmp) {
        tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++) {
            //if(i < 0 || i > rows-1) continue;
            for (int j = y - 1; j < (y + 2); j++) {
                //if(j < 0 || j > columns-1) continue;
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                    if (tmp_i == x && tmp_j == y) continue;
                    if (tmp[tmp_i][tmp_j].getState() == false) {
                        tmp[tmp_i][tmp_j].setState(true);
                        tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    public void vonNeumann(int x, int y, Cell[][] tmp) {
    	tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++){
            for (int j = y - 1; j < (y + 2); j++){
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                	 if (tmp_i == x && tmp_j == y) continue;
                    if(i==x+1 && j==y || i==x-1 && j==y || i==x && j==y-1 || i==x && j==y+1){
                    	if (tmp[tmp_i][tmp_j].getState() == false) {
                    		tmp[tmp_i][tmp_j].setState(true);
                    		tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    	}
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    public void pentagonal_down(int x, int y, Cell[][] tmp) {
    	tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++){
            for (int j = y - 1; j < (y + 2); j++){
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                	 if (tmp_i == x && tmp_j == y) continue;
                	 if(i==x+1 && j==y || i==x-1 && j==y || i==x && j==y-1 || i==x+1 && j==y-1 || i==x-1 && j==y-1){
                    	if (tmp[tmp_i][tmp_j].getState() == false) {
                    		tmp[tmp_i][tmp_j].setState(true);
                    		tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    	}
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    public void pentagonal_up(int x, int y, Cell[][] tmp) {
    	tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++){
            for (int j = y - 1; j < (y + 2); j++){
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                	if (tmp_i == x && tmp_j == y) continue;
                    if(i==x+1 && j==y || i==x-1 && j==y || i==x && j==y+1 || i==x+1 && j==y+1 || i==x-1 && j==y+1){
                    	if (tmp[tmp_i][tmp_j].getState() == false) {
                    		tmp[tmp_i][tmp_j].setState(true);
                    		tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    	}
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    public void pentagonal_left(int x, int y, Cell[][] tmp) {
    	tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++){
            for (int j = y - 1; j < (y + 2); j++){
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                	if (tmp_i == x && tmp_j == y) continue;
                    if(i==x-1 && j==y || i==x-1 && j==y-1 || i==x-1 && j==y+1 || i==x && j==y-1 || i==x && j==y+1){
                    	if (tmp[tmp_i][tmp_j].getState() == false) {
                    		tmp[tmp_i][tmp_j].setState(true);
                    		tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    	}
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    public void pentagonal_right(int x, int y, Cell[][] tmp) {
    	tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++){
            for (int j = y - 1; j < (y + 2); j++){
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                	 if (tmp_i == x && tmp_j == y) continue;
                    if(i==x+1 && j==y || i==x+1 && j==y-1 || i==x+1 && j==y+1 || i==x && j==y-1 || i==x && j==y+1){
                    	if (tmp[tmp_i][tmp_j].getState() == false) {
                    		tmp[tmp_i][tmp_j].setState(true);
                    		tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    	}
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    public void hexagonal1(int x, int y, Cell[][] tmp) { // czyli \
    	tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++){
            for (int j = y - 1; j < (y + 2); j++){
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                	if (tmp_i == x && tmp_j == y) continue;
                    if(i==x && j==y-1 || i==x && j==y+1 || i==x+1 && j==y || i==x-1 && j==y || i==x-1 && j==y+1 || i==x+1 && j==y-1){
                    	if (tmp[tmp_i][tmp_j].getState() == false) {
                    		tmp[tmp_i][tmp_j].setState(true);
                    		tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    	}
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    public void hexagonal2(int x, int y, Cell[][] tmp) { // czyli /
    	tmp[x][y].setState(true);
        tmp[x][y].setColor(cell[x][y].getColor());
        int tmp_i, tmp_j;
        for (int i = x - 1; i < (x + 2); i++){
            for (int j = y - 1; j < (y + 2); j++){
            	tmp_i=i;
            	tmp_j=j;
            	if (boundry_cond==1) {
                    if (i == -1) tmp_i = columns - 1;
                    if (i == columns) tmp_i = 0;
                    if (j == -1) tmp_j = rows - 1;
                    if (j == rows) tmp_j = 0;
                } else {
                    if (i == -1) tmp_i = 0;
                    if (i == columns) tmp_i = columns;
                    if (j == -1) tmp_j = 0;
                    if (j == rows) tmp_j = rows;
                }
                try {
                    if (tmp_i == x && tmp_j == y) continue;
                    if(i==x && j==y-1 || i==x && j==y+1 || i==x+1 && j==y || i==x-1 && j==y || i==x-1 && j==y-1 || i==x+1 && j==y+1){
                    	if (tmp[tmp_i][tmp_j].getState() == false) {
                    		tmp[tmp_i][tmp_j].setState(true);
                    		tmp[tmp_i][tmp_j].setColor(tmp[x][y].getColor());
                    	}
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    private void saveFile(){
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("C:/Users/Ula/Desktop/wieloszkalowe"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG","*.png"));
        fc.setTitle("Save Board");
        File file = fc.showSaveDialog(primaryStage);
        if(file != null){
            WritableImage wi = new WritableImage((int)gc.getCanvas().getWidth(),(int)gc.getCanvas().getHeight());
            try {                    
            	ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(null,wi),null),"png",file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
