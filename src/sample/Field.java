package sample;

public class Field {
    private Character type;
    private Boolean covered;
    private Integer x;
    private Integer y;

    public Field(Character type, Boolean covered, Integer x, Integer y) {
        this.type = type;
        this.covered = covered;
        this.x = x;
        this.y = y;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public void uncover()
    {
        covered = false;
    }

    public Character getType() {
        return type;
    }

    public Boolean getCovered() {
        return covered;
    }
}
