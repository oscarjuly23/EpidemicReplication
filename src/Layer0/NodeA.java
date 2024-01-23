package Layer0;

import Shared.Client;
import Shared.ReadCallback;
import Shared.Server;

import java.io.*;
import java.util.Arrays;

public class NodeA extends Thread {
    // LAYER 0 ( CORE LAYER ) --> UPDATE EVERYWHERE, ACTIVE & EAGER REPLICATION
    private Server ServerNA;
    private Client ClientNA1, ClientNA2, ClientNB;
    private int[] arr = new int[20];
    private int id, PORT_NA1, PORT_NA2, PORT_NA3, PORT_NB;
    private int version = -1;
    private int nUpdate = -1;

    public NodeA(int id, int PORT_NA1, int PORT_NA2, int PORT_NA3, int PORT_NB) {
        this.id = id;
        this.PORT_NA1 = PORT_NA1;
        this.PORT_NA2 = PORT_NA2;
        this.PORT_NA3 = PORT_NA3;
        this.PORT_NB = PORT_NB;
    }
    private final ReadCallback readCallbackServer = (String msg) -> {
        if (msg.contains("replic")) {       // Rebem el WRITE de part de un altre node
            String[] splits = msg.split("_");
            for (String split : splits) {
                //System.out.println(split);
                String[] line = split.split("-");
                String[] place = line[1].split("\\.");
                arr[Integer.parseInt(place[0])] = Integer.parseInt(place[1]);
                fileWrite();            // Actualitzem el nostre fitcher local
                if (nUpdate == 10 && id != 1) {
                    ClientNB.write("replic-"+ Arrays.toString(arr));
                    nUpdate = 0;
                }
            }
            //System.out.println("L0 N" + id + ": WRITE(node) pos-" + place[0]+" --> " + place[1]);
        } else {

            //System.out.println(msg);
            String[] line = msg.split(",");
            for (String word : line) {

                if (word.contains("r")) {           // Si la transacció es tracta de un READ
                    int posR = Integer.parseInt((word.replaceAll("(r\\()(\\d+)(\\))", "$2")).trim());
                    System.out.println("L0 NA" + id + ": READ pos-" + posR + " --> " + arr[posR]);

                } else if (word.contains("w")) {    // Si la transacció es tracta de un WRITE
                    String posW = (word.replaceAll("(w\\()(\\d+.\\d+)(\\))", "$2")).trim();
                    String[] place = posW.split("\\.");

                    //ClientNA1.write("replic-" + posW);  // Avisem als altres nodes d'aquest write
                    //ClientNA2.write("replic-" + posW);  // Avisem als altres nodes d'aquest write
                    ClientNA1.write("replic-" + posW + "_");  // Avisem als altres nodes d'aquest write
                    ClientNA2.write("replic-" + posW + "_");  // Avisem als altres nodes d'aquest write

                    arr[Integer.parseInt(place[0])] = Integer.parseInt(place[1]);       // Actualitzem el nostre Array
                    fileWrite();            // Actualitzem el nostre fitcher local

                    if (nUpdate == 10 && id != 1) {
                        ClientNB.write("replic-"+ Arrays.toString(arr));
                        nUpdate = 0;
                    }

                    //System.out.println("L0 N" + id + ": WRITE(client) pos-" + place[0]+" --> " + place[1]);
                }
            }
        }
    };
    private static final ReadCallback readCallbackClient = System.out::println;

    public void run() {
        //Conexió entre nodes: LAYER 0 (CORE LAYER)
        ServerNA = new Server(PORT_NA1 , readCallbackServer);
        ClientNA1 = new Client(PORT_NA2, readCallbackClient);
        ClientNA2 = new Client(PORT_NA3, readCallbackClient);
        if (id != 1) {
            ClientNB = new Client(PORT_NB, readCallbackClient);
        }

        Arrays.fill(arr, 0);

        fileWrite();
    }

    public void fileWrite() {
        try {
            version++;
            nUpdate++;
            String path = "src\\Layer0\\Versions0\\VA_N"+id+".txt";
            FileWriter fw = new FileWriter(path, true);
            fw.write("V" + version + ": " + Arrays.toString(arr) + "\n");

            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
