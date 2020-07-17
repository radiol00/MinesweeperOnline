package sample;

import javafx.scene.control.TextArea;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class TalkClient extends Thread
{
    private BufferedWriter w;
    public TalkClient(BufferedWriter w)
    {
        this.w = w;
    }

    @Override
    public void run() {
            System.out.println("Client: pisze!");
            write("elo!");
            System.out.println("Client: napisalem elo!");
    }

    private void write(String msg)
    {
        try {
            w.write(msg);
            w.newLine();
            w.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
