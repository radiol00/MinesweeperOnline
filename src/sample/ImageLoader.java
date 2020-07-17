package sample;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class ImageLoader {
    public Background loadImage(String s, int size)
    {
        Image image = new Image(getClass().getResourceAsStream(s), size, size, false, true);

        BackgroundImage bImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(size, size, true, true, true, false));

        Background bg = new Background(bImage);

        return bg;
    }
}
