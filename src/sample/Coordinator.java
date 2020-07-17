package sample;

import javafx.scene.control.TextArea;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Coordinator extends Thread {

    Socket s;
    TextArea sLog;


    public Coordinator(Socket s, TextArea sLog) {
        this.s = s;
        this.sLog = sLog;
    }

    @Override
    public void run() {

        String line;
        List<List<Field>> mineField = null;
        String[] winCheck;
        Integer winCondition = 999;
        Integer winCounter = 0;
        MineFieldInfos info = null;
        BufferedWriter writer;
        BufferedReader reader;
        Integer width;
        Integer height;
        Integer bombs;
        Integer x,y;

        try {
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        }catch (IOException e)
        {
            sLog.appendText("Ktoś miał problem z połączeniem!\n");
            return;
        }

        try {
            while ((line = reader.readLine()) != null) {
                if (line.matches("N33D:x\\d*x\\d*x\\d*"))
                {
                    String[] whb = line.split("x",4);

                    width = Integer.valueOf(whb[1]);
                    height = Integer.valueOf(whb[2]);
                    bombs = Integer.valueOf(whb[3]);
                    winCondition = (width*height)-bombs;
                    winCounter = 0;
                    sLog.appendText("Ktoś zażądał planszy "+width+"x"+height+" z "+bombs+" bombami!\n");
                    info = generateMineField(height,width,bombs);
                    mineField = info.getMineField();
                }
                else if (line.matches("Cl1ck3d:x\\d*x\\d*"))
                {
                    String[] xy = line.split("x");

                    x = Integer.valueOf(xy[1]);
                    y = Integer.valueOf(xy[2]);

                    sLog.appendText("Ktoś sprawdza swoje pole "+x+" - "+y+" i to "+mineField.get(y).get(x).getType()+"!\n");
                    String msg = "Unc0v3r:";
                    if (mineField.get(y).get(x).getType().equals('B'))
                    {
                        msg = "UL0ST:"+info.getBombsString();
                        sLog.appendText("Ktoś przegrał!\n");
                    }else
                    {
                        msg += uncoverMineField(mineField,x,y);

                        winCheck = msg.split("/");

                        for (String s: winCheck)
                        {
                            winCounter++;
                        }
                        winCounter--;
                    }
                    writer.write(msg);
                    writer.newLine();
                    writer.flush();

                    if (winCounter == winCondition)
                    {
                        sLog.appendText("Ktoś wygrał!\n");
                        writer.write("UW1N!");
                        writer.newLine();
                        writer.flush();
                    }
                    }
                }

            reader.close();
            writer.close();
            s.close();
            sLog.appendText("Ktoś się rozłączył!\n");
        }catch (IOException e)
        {
           /* e.printStackTrace(); */
        }
    }

    private String uncoverMineField(List<List<Field>> mineField, int x, int y)
    {
        Character c = mineField.get(y).get(x).getType();
        Boolean covered = mineField.get(y).get(x).getCovered();
        String msg = "";
            if (c.equals('X') && covered) {
                mineField.get(y).get(x).uncover();
                msg += "/" + x + "x" + y + "x" + 'X';
                for(int i=-1;i<=1;i++)
                {
                    for(int j=-1;j<=1;j++)
                    {
                        if(x+i<0 || x+i>=mineField.get(0).size() || y+j<0 || y+j>=mineField.size())
                            continue;
                        msg += uncoverMineField(mineField,x+i,y+j);
                    }
                }
            } else if (c.toString().matches("\\d\\d*") && covered)
            {
                mineField.get(y).get(x).uncover();
                return "/" + x + "x" + y + "x" + mineField.get(y).get(x).getType();
            }
            return msg;
    }

    public MineFieldInfos generateMineField(int height, int width, int bombs)
    {
        List<List<Field>> mineField = new ArrayList<List<Field>>();
        List<Pair<Integer,Integer>> board = new ArrayList<Pair<Integer,Integer>>();
        Random generator = new Random();
        String bombsString = "";

        for(int i = 0; i < width; i++)
        {
            mineField.add(new ArrayList<Field>());
            for (int j = 0; j < height; j++)
            {
                board.add(new Pair<>(i,j));
                mineField.get(i).add(new Field('X',true,i,j));
            }
        }

        int rand;
        Integer x,a;
        Integer y,b;
        Pair<Integer,Integer> pair;
        for(int i = 0; i < bombs; i++)
        {
            rand = generator.nextInt(board.size());
            x = board.get(rand).getKey();
            y = board.get(rand).getValue();

            mineField.get(x).get(y).setType('B');
            bombsString+="/"+x+"x"+y;

                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            a = x + k;
                            b = y + l;
                            if (a < 0 || a == width || b < 0 || b == height || (a == 0 && b == 0)) continue;
                            switch (mineField.get(a).get(b).getType())
                            {
                                case 'X': mineField.get(a).get(b).setType('1'); break;
                                case '1': mineField.get(a).get(b).setType('2'); break;
                                case '2': mineField.get(a).get(b).setType('3'); break;
                                case '3': mineField.get(a).get(b).setType('4'); break;
                                case '4': mineField.get(a).get(b).setType('5'); break;
                                case '5': mineField.get(a).get(b).setType('6'); break;
                                case '6': mineField.get(a).get(b).setType('7'); break;
                                case '7': mineField.get(a).get(b).setType('8'); break;
                            }
                        }
                    }

            board.remove(rand);
        }

        for(int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                System.out.printf("%c ",mineField.get(j).get(i).getType());
            }
            System.out.println("");
        }

        System.out.println("-------------------");
        return new MineFieldInfos(mineField,bombsString);
    }
}
