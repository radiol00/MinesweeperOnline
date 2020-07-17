package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button slistenButton;
    public TextArea sLog;
    public TextField sserverPort;
    public Button cconnectButton;
    public Tab tabServer;
    public Tab tabKlient;
    public Button sstopButton;
    public TextField cserverPort;
    public TextField cserverIP;
    public TextField fieldHeight;
    public TextField fieldWidth;
    public TextField fieldMines;
    private ServerSocket ss;
    private List<Socket> sockets;

    private enum DiffLvl{
        DIFF_CUSTOM,
        DIFF_BEG,
        DIFF_INTER,
        DIFF_EXP
    };

    DiffLvl diffLvl = DiffLvl.DIFF_BEG;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sockets = new ArrayList<>();
    }

    public void customDiff() {
        fieldHeight.setDisable(false);
        fieldWidth.setDisable(false);
        fieldMines.setDisable(false);
        diffLvl = DiffLvl.DIFF_CUSTOM;
    }

    public void diffBeg() {
        fieldHeight.setDisable(true);
        fieldWidth.setDisable(true);
        fieldMines.setDisable(true);
        diffLvl = DiffLvl.DIFF_BEG;
    }

    public void diffInter() {
        fieldHeight.setDisable(true);
        fieldWidth.setDisable(true);
        fieldMines.setDisable(true);
        diffLvl = DiffLvl.DIFF_INTER;
    }

    public void diffExp() {
        fieldHeight.setDisable(true);
        fieldWidth.setDisable(true);
        fieldMines.setDisable(true);
        diffLvl = DiffLvl.DIFF_EXP;
    }

    public void slistenPort() {
        Thread listener = new Thread(() -> {
            Socket s;
            try {
                Integer port = Integer.valueOf(sserverPort.getText());
                ss = new ServerSocket(port);
                sLog.appendText("Słuchanie na porcie: " + port + " ...\n");

                slistenButton.setDisable(true);
                tabKlient.setDisable(true);
                sstopButton.setDisable(false);

                while (true) {
                    s = ss.accept();
                    sLog.appendText("Przyszedł klient!\n");
                    sockets.add(s);
                    Coordinator coordinator = new Coordinator(s,sLog);
                    coordinator.start();
                }
            } catch (IllegalArgumentException e) {
                sLog.appendText("Zły port!\n");

            } catch (IOException e) {
                if (e instanceof SocketException) sLog.appendText("Zaprzestano nasłuchiwania!\n");
                else sLog.appendText("Problem ze słuchaniem!\n");
            }
        });
        listener.start();
    }

    public void shutdown() {

            BufferedWriter bufferedWriter;

            try {
                if (ss != null) {
                    ss.close();
                    ss = null;
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            for(Socket s:sockets) {
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    bufferedWriter.write("1DC!");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    s.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
    }

    public void sstopListen() {
        try {
            BufferedWriter bufferedWriter;

            ss.close();
            ss = null;

            for(Socket s:sockets)
            {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                bufferedWriter.write("1DC!");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                s.close();
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        slistenButton.setDisable(false);
        tabKlient.setDisable(false);
        sstopButton.setDisable(true);
    }

    public void cconnect(ActionEvent actionEvent) {

        try {
            Stage stage = new Stage();
            int height=3,width=3, bombs = 5;
            switch (diffLvl)
            {
                case DIFF_BEG:
                    stage.setTitle("Saper: Poczatkujacy 9 x 9");
                    height = 9;
                    width = 9;
                    bombs = 10;
                    break;
                case DIFF_INTER:
                    stage.setTitle("Saper: Średniozaawansowany 16 x 16");
                    height = 16;
                    width = 16;
                    bombs = 40;
                    break;
                case DIFF_EXP:
                    stage.setTitle("Saper: Zaawansowany 16 x 30");
                    height = 16;
                    width = 30;
                    bombs = 99;
                    break;
                case DIFF_CUSTOM:
                    Integer checkHeight = Integer.valueOf(fieldHeight.getText());
                    Integer checkWidth = Integer.valueOf(fieldWidth.getText());
                    Integer checkBombs = Integer.valueOf(fieldMines.getText());
                    if (checkHeight >= 9 && checkHeight <=24 &&
                        checkWidth >= 9 && checkWidth <=30 &&
                        checkBombs >= 10 && checkBombs <=690 &&
                        checkBombs < checkHeight*checkWidth) {
                        stage.setTitle("Saper: Niestandardowe " + fieldHeight.getText() + " x " + fieldWidth.getText());
                        height = Integer.valueOf(fieldHeight.getText()).intValue();
                        width = Integer.valueOf(fieldWidth.getText()).intValue();
                        bombs = Integer.valueOf(fieldMines.getText()).intValue();
                        break;
                    }
                    else
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Błąd!");
                    alert.setHeaderText(null);
                    alert.setContentText("Zła konfiguracja!");
                    alert.showAndWait();
                    return;
                }
            }

            Integer port = Integer.valueOf(cserverPort.getText());
            Socket s = new Socket(cserverIP.getText(), port);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("gameSaper.fxml"));
            Parent game = (Parent)loader.load();
            GameController gameController = loader.getController();
            gameController.set(height,width,bombs,s);
            stage.setScene(new Scene(game,width*27+100,height*27+50+50));
            stage.setOnHidden(e -> gameController.shutdown());
            stage.setResizable(false);
            stage.show();
            ((Node)actionEvent.getSource()).getScene().getWindow().hide();

        }catch (IOException | IllegalArgumentException e)
        {
            if (e instanceof IllegalArgumentException)
            {
                System.out.println("Zły port!");
            }
            else if (e instanceof SocketException)
            {
                System.out.println("Nie mozna utworzyc socketu!");
            }
            else
            {
                e.printStackTrace();
            }
        }
    }


}




