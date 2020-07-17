package sample;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    public GridPane mineField;
    public TextField flagCounter;
    List<List<MineSlot>> boardButtons;
    public Pane field;
    public Button faceButton;
    public Socket s;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;
    int height;
    int width;
    int bombs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        faceButton.setPrefSize(75,75);

        ImageLoader iLoader = new ImageLoader();

        Background idleFace = iLoader.loadImage("/img/idleFace.png",75);
        Background hoverFace = iLoader.loadImage("/img/hoverFace.png",75);
        Background pressedFace = iLoader.loadImage("/img/pressedFace.png",75);

        faceButton.setBackground(idleFace);

        faceButton.setOnMousePressed(mouseEvent -> {
            faceButton.setBackground(pressedFace);
        });

        faceButton.setOnMouseReleased(mouseEvent -> {
            faceButton.setBackground(idleFace);
            field.getChildren().clear();

            try {
                bufferedWriter.write("N33D:x" + width + "x" + height + "x" + bombs);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                for(int i=0; i<width; i++) {
                    for (int j = 0; j < height; j++) {
                        boardButtons.get(i).set(j,new MineSlot(faceButton, flagCounter, bufferedWriter, i, j));
                    }
                }

                mineField = setupMineField();

                flagCounter.setText(""+bombs);

                field.getChildren().add(mineField);
            }catch (IOException e)
            {
                /*e.printStackTrace();*/
            }


        });

        faceButton.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            faceButton.setBackground(hoverFace);
        });

        faceButton.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            faceButton.setBackground(idleFace);
        });


    }

    public void set(int height, int width, int bombs,Socket socket)
    {
        this.width = width;
        this.height = height;
        this.bombs = bombs;
        this.s = socket;

        flagCounter.setText(""+bombs);

        boardButtons = new ArrayList<List<MineSlot>>();

        bufferedWriter = null;
        bufferedReader = null;

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            bufferedWriter.write("N33D:x" + width + "x" + height + "x" + bombs);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }catch (IOException e)
        {
            System.out.println("Failed to get streams or write!");
            return;
        }

        for(int i=0; i<width; i++) {
            boardButtons.add(new ArrayList<MineSlot>());
            for (int j = 0; j < height; j++) {
                boardButtons.get(i).add(new MineSlot(faceButton, flagCounter,bufferedWriter, i, j));
            }
        }

        mineField = setupMineField();

        ListenClient listenClient = new ListenClient(bufferedReader,boardButtons,faceButton);
        listenClient.start();
        field.getChildren().add(mineField);
    }

    private GridPane setupMineField()
    {
        GridPane newMineField = new GridPane();
        newMineField.setVgap(2);
        newMineField.setHgap(2);
        newMineField.setGridLinesVisible(true);

        ColumnConstraints columnConstraints = new ColumnConstraints(25);
        RowConstraints rowConstraints = new RowConstraints(25);

        int i;
        for(i = 0; i<width-1;i++)
        {
            newMineField.addColumn(i,new Label(""));
            newMineField.getColumnConstraints().add(i,columnConstraints);
        }
        newMineField.getColumnConstraints().add(i,columnConstraints);

        for(i=0; i<height;i++)
        {
            newMineField.addRow(i,new Label(""));
            newMineField.getRowConstraints().add(i,rowConstraints);
        }

        for(i=0; i<width; i++) {
            for (int j = 0; j < height; j++) {
                newMineField.add(boardButtons.get(i).get(j), i, j);
            }
        }

        return newMineField;
    }

    public void shutdown()
    {
        try {
            if(s!=null) {
                s.close();
                s=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
