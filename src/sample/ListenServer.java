package sample;

import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ListenServer extends Thread
{
    private BufferedReader r;
    private TextArea sLog;
    String line;
    public ListenServer(BufferedReader r, TextArea sLog)
    {
        this.r = r;
        this.sLog = sLog;
    }

    @Override
    public void run()
    {
        try{

            while ((line = r.readLine())!=null)
            {
                try {
                    wait();
                }catch (InterruptedException e)
                {

                }
            }

            r.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public String getMsg()
    {
        return line;
    }

}
