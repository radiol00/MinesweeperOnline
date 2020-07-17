package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.io.BufferedWriter;
import java.io.IOException;

public class MineSlot extends Button {
        Background original;
        Boolean flagged;
    public MineSlot(Button faceButton, TextField flagCounter, BufferedWriter w, int i, int j) {
        flagged = false;

        ImageLoader iLoader = new ImageLoader();
        Background idleFace = iLoader.loadImage("/img/idleFace.png",75);
        Background clickFace = iLoader.loadImage("/img/clickedFace.png", 75);

        setPrefSize(25, 25);
        setMaxSize(25, 25);

        Background idle = iLoader.loadImage("/img/idle.png",25);
        Background hover = iLoader.loadImage("/img/hover.png",25);
        Background click = iLoader.loadImage("/img/clicked.png",25);
        Background flag = iLoader.loadImage("/img/flag.png",25);

        original = idle;

        setBackground(idle);
        setAlignment(Pos.CENTER);

        addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            if(getBackground().equals(idle)) {
                setBackground(hover);
            }
        });

        addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            if(getBackground().equals(hover)) {
                setBackground(original);
            }
        });

        setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY)
            {
                if (!isDisabled() && flagged == false) {
                    faceButton.setBackground(clickFace);
                    setBackground(click);
                }
            }
        });

        setOnMouseReleased(mouseEvent -> {
            if(!isDisabled() && flagged == false && mouseEvent.getButton() == MouseButton.PRIMARY) {
                try {
                    w.write("Cl1ck3d:x" + j + "x" + i);
                    w.newLine();
                    w.flush();
                } catch (IOException e) {
                    System.out.println("Cant write!");
                }

                faceButton.setBackground(idleFace);
                setBackground(idle);
            }
            else if (mouseEvent.getButton() == MouseButton.SECONDARY && (flagged == true || original == idle)){
                if (flagged == true)
                {
                    flagCounter.setText(""+(Integer.valueOf(flagCounter.getText()).intValue()+1));
                    flagged = false;
                    original = idle;
                    setBackground(idle);
                }else
                {
                    flagCounter.setText(""+(Integer.valueOf(flagCounter.getText()).intValue()-1));
                    flagged = true;
                    original = flag;
                    setBackground(flag);
                }
            }
        });


    }

    public void uncoverWith(Character c)
    {
        ImageLoader iLoader = new ImageLoader();
        switch(c)
        {
            case 'X':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty.png",25); break;
            case '1':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_1.png",25); break;
            case '2':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_2.png",25); break;
            case '3':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_3.png",25); break;
            case '4':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_4.png",25); break;
            case '5':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_5.png",25); break;
            case '6':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_6.png",25); break;
            case '7':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_7.png",25); break;
            case '8':
                setDisabled(true);
                original = iLoader.loadImage("/img/empty_8.png",25); break;
            case 'B':
                setDisabled(true);
                original = iLoader.loadImage("/img/bomb.png",25); break;
        }
        setBackground(original);
    }

}
