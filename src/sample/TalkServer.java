package sample;

import javafx.scene.control.TextArea;

import java.io.BufferedWriter;
import java.io.IOException;


public class TalkServer extends Thread
{
    private BufferedWriter w;
    private TextArea sLog;
    public TalkServer(BufferedWriter w, TextArea sLog)
    {
        this.w = w;
        this.sLog = sLog;
    }

    @Override
    public void run()
    {
        try{



            w.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
