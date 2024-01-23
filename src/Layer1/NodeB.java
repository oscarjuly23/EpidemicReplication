package Layer1;

import Shared.Client;
import Shared.ReadCallback;
import Shared.Server;

import javax.sound.sampled.Port;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class NodeB extends Thread {
    // LAYER 1 --> PASSIVE, PRIMARY BACKUP
    private Server ServerNB;
    private Client ClientNA, ClientNC1, ClientNC2;
    private int id, PORT_NB, PORT_NA, PORT_NC1, PORT_NC2;
    private static File f;
    private int[] arr = new int[20];
    private int version = -1;
    public NodeB(int id, int PORT_NB, int PORT_NA, int PORT_NC1, int PORT_NC2) {
        this.id = id;
        this.PORT_NB = PORT_NB;
        this.PORT_NA = PORT_NA;
        this.PORT_NC1 = PORT_NC1;
        this.PORT_NC2 = PORT_NC2;
    }

    private final ReadCallback readCallbackServer = (String msg) -> {
        if (msg.contains("replic")) {       // Rebem UPDATE de part de la LAYER0
            String[] line = msg.split("-");
            String toArr = line[1].substring(1, line[1].length()-1); // para quitar los corchetes
            String[] numeros = toArr.split(",");
            arr = new int[numeros.length];
            for (int i = 0; i < numeros.length; i++) {
                arr[i] = Integer.parseInt(numeros[i].trim());
            }
            fileWrite();            // Actualitzem el nostre fitcher local

        } else {
            //System.out.println(msg);
            String[] line = msg.split(",");
            for (String word : line) {

                if (word.contains("r")) {           // Si la transacció es tracta de un READ
                    int posR = Integer.parseInt((word.replaceAll("(r\\()(\\d+)(\\))", "$2")).trim());
                    System.out.println("L1 NB" + id + ": READ pos-" + posR + " --> " + arr[posR]);
                }
            }
        }
    };
    private final ReadCallback readCallbackClient = (String msg) -> {
    };
    public void run() {
        //Conexió entre nodes
        ClientNA = new Client(PORT_NA, readCallbackClient);
        ServerNB = new Server(PORT_NB, readCallbackServer);
        if (id == 2){
            ClientNC1 = new Client(PORT_NC1, readCallbackClient);
            ClientNC2 = new Client(PORT_NC2, readCallbackClient);
            updateLayer2();
        }

        Arrays.fill(arr, 0);

        fileWrite();
    }

    public void fileWrite() {
        try {
            version++;
            String path = "src\\Layer1\\Versions1\\VB_N"+id+".txt";
            FileWriter fw = new FileWriter(path, true);
            fw.write("V" + version + ": " + Arrays.toString(arr) + "\n");

            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLayer2() {
        new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        ClientNC1.write("re2-"+ Arrays.toString(arr));
                        ClientNC2.write("re2-"+ Arrays.toString(arr));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
        }).start();
    }
}