package Layer2;

import Shared.Client;
import Shared.ReadCallback;
import Shared.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class NodeC extends Thread {
    // LAYER 2 --> PASSIVE & PRIMARY BACKUP
    private Server ServerNC;
    private Client ClientNC;
    private int id,PORT_NC, PORT_NB2;
    private int[] arr = new int[20];
    private int version = -1;

    public NodeC(int id, int PORT_NC, int PORT_NB2) {
        this.id = id;
        this.PORT_NC = PORT_NC;
        this.PORT_NB2 = PORT_NB2;
    }
    private final ReadCallback readCallbackClient = (String msg) -> {
        if (msg.contains("re2")) {
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
                    System.out.println("L2 NB" + id + ": READ pos-" + posR + " --> " + arr[posR]);
                }
            }
        }
    };
    private final ReadCallback readCallbackServer = System.out::println;
    public void run() {
        //Conexió entre nodes
        ServerNC = new Server(PORT_NC, readCallbackClient);
        ClientNC = new Client(PORT_NB2, readCallbackServer);

        Arrays.fill(arr, 0);
        fileWrite();
    }

    public void fileWrite() {
        try {
            version++;
            String path = "src\\Layer2\\Versions2\\VC_N"+id+".txt";
            FileWriter fw = new FileWriter(path, true);
            fw.write("V" + version + ": " + Arrays.toString(arr) + "\n");

            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
