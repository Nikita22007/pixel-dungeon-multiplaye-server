package com.watabou.pixeldungeon.network;

import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.scenes.TitleScene;
import com.watabou.pixeldungeon.utils.GLog;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;


public class Client extends Thread {
    public static final String CHARSET = "UTF-8";

    protected static OutputStreamWriter writeStream;
    protected static BufferedWriter writer;
    protected static InputStreamReader readStream;
    protected static Socket socket = null;
    protected static Client client;
    protected static ParseThread parceThread = null;
    protected static final NetworkPacket packet = new NetworkPacket();
    protected static final int BUFFER_SIZE = 16 * 1024; // bytes

    public static boolean connect(String server, int port) {
        packet.clearData();
        parceThread = new ParseThread();
        try {
            socket = new Socket(server, port);
            writeStream = new OutputStreamWriter(
                    socket.getOutputStream(),
                    Charset.forName(CHARSET).newEncoder()
            );
            readStream = new InputStreamReader(
                    socket.getInputStream(),
                    Charset.forName(CHARSET).newDecoder()
            );
            client = new Client();
            client.start();
            parceThread.start();
            writer = new BufferedWriter(writeStream, BUFFER_SIZE);
            return socket.isConnected();
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static void disconnect() {
        try {
            socket.close();
        } catch (Exception e) {
        }
        socket = null;
        readStream = null;
        writeStream = null;
        if (!(PixelDungeon.scene() instanceof TitleScene)) {
            PixelDungeon.switchScene(TitleScene.class);
        }
    }

    public void run() {
        if (!socket.isConnected()) {
            disconnect();
            return;
        }
        try {
            while (!socket.isClosed()) sleep(1000);
        } catch (Exception e) {
            GLog.n(e.getStackTrace().toString());
        }
        disconnect();
    }

    public static void flush() {
        try {
            synchronized (packet.dataRef) {
                synchronized (writeStream) {
                    writer.write(packet.dataRef.get().toString());
                    writer.write('\n');
                    writer.flush();
                    packet.clearData();
                }
            }
        } catch (IOException e) {
            GLog.h("IOException. Message: {0}", e.getMessage());
            disconnect();
        }
    }

    //methods
    public static void sendHeroClass(HeroClass heroClass) {
        packet.packAndAddHeroClass(heroClass.name().toLowerCase());
        flush();
    }
}
