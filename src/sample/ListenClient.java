package sample;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;


public class ListenClient extends Thread
{
    private BufferedReader r;
    private List<List<MineSlot>> board;
    private Button faceButton;

    public ListenClient(BufferedReader r, List<List<MineSlot>> board, Button faceButton) {
        this.r = r;
        this.board = board;
        this.faceButton = faceButton;
    }

    @Override
    public void run(){
        try {

            String line;
        while ((line = r.readLine()) != null) {
            if(line.startsWith("Unc0v3r:")) {
                String[] positions;
                String[] coordinates;

                positions = line.split("/");


                for (String s : positions) {
                    if(!s.matches("Unc0v3r:")) {
                        coordinates = s.split("x");
                        Integer x = Integer.valueOf(coordinates[0]);
                        Integer y = Integer.valueOf(coordinates[1]);
                        Character c = coordinates[2].charAt(0);

                        board.get(y).get(x).uncoverWith(c);
                    }
                }
            } else if(line.startsWith("UL0ST:")) {

                faceButton.setBackground(new ImageLoader().loadImage("/img/loseFace.png",75));

                String[] positions;
                String[] coordinates;

                positions = line.split("/");

                for(String s: positions) {
                    if(!s.matches("UL0ST:"))
                    {
                        coordinates = s.split("x");
                        Integer x = Integer.valueOf(coordinates[0]);
                        Integer y = Integer.valueOf(coordinates[1]);

                        board.get(x).get(y).uncoverWith('B');
                    }
                }

                int a = board.size(), b = board.get(0).size();

                for(int i=0;i<a;i++)
                {
                    for (int j=0;j<b;j++)
                    {
                        board.get(i).get(j).setDisable(true);
                    }
                }

                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Saper");
                    alert.setHeaderText(null);
                    alert.setContentText("Przegrałeś, kliknij buźkę by spróbować jeszcze raz!");

                    alert.showAndWait();
                });
            } else if(line.equals("UW1N!"))
            {
                faceButton.setBackground(new ImageLoader().loadImage("/img/winFace.png",75));
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Saper");
                    alert.setHeaderText(null);
                    alert.setContentText("Wygrałeś! Kliknij buźkę by grać dalej!");

                    alert.showAndWait();

                });
            } else if (line.equals("1DC!"))
            {
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Saper");
                    alert.setHeaderText(null);
                    alert.setContentText("Serwer przestał działać!");

                    alert.showAndWait();

                });
                r.close();
            }
        }
        }catch (IOException e) {

        }

    }
}
