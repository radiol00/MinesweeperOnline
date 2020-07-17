package sample;

import java.util.List;

public class MineFieldInfos {
    List<List<Field>> mineField;
    String bombsString;

    public MineFieldInfos(List<List<Field>> mineField, String bombsString) {
        this.mineField = mineField;
        this.bombsString = bombsString;
    }

    public List<List<Field>> getMineField() {
        return mineField;
    }

    public String getBombsString() {
        return bombsString;
    }
}
