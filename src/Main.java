import Layer0.NodeA;
import Layer1.NodeB;
import Layer2.NodeC;
import Shared.Client;
import Shared.Ports;
import Shared.ReadCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


public class Main {
    private static final ReadCallback readCallbackClient = System.out::println;

    public static void main(String[] args) throws IOException {
        int nRand;
        String cadena;
        Random random = new Random();

        // Creem fitxers locals de cada node:
        crearFitxers();

        // Invoquem els nodes i creem els fitxers locals:
        invocaNodes();

        // Com estem simulant que això és el client, ens connectem als sockets dels nodes:
        Client clientA1 = new Client(Ports.L0_A1_PORT, readCallbackClient);
        Client clientA2 = new Client(Ports.L0_A2_PORT, readCallbackClient);
        Client clientA3 = new Client(Ports.L0_A3_PORT, readCallbackClient);
        Client clientB1 = new Client(Ports.L1_B1_PORT, readCallbackClient);
        Client clientB2 = new Client(Ports.L1_B2_PORT, readCallbackClient);
        Client clientC1 = new Client(Ports.L2_C1_PORT, readCallbackClient);
        Client clientC2 = new Client(Ports.L2_C2_PORT, readCallbackClient);

        // Llegim el fitxer local de trasnsaccions
        FileReader f = new FileReader("src/localFile.txt");
        BufferedReader b = new BufferedReader(f);
        while ((cadena = b.readLine())!=null) {
            //System.out.println(cadena);
            String[] line = cadena.split(",");

            for (String word : line) {
                if (word.contains("b")) {                       // Cada linia, depenent de la 'b' enviarem la transacció (o conjunt) a la capa adient
                    if (word.length() == 1) {
                        nRand = random.nextInt(3);       // Generem un nombre aleatòri per a enviar la transacció al node random dins la capa adient
                        switch (nRand) {
                            case 0 -> clientA1.write(cadena);
                            case 1 -> clientA2.write(cadena);
                            case 2 -> clientA3.write(cadena);
                        }
                    } else {
                        if (word.charAt(2) == '0') {
                            nRand = random.nextInt(3);
                            switch (nRand) {
                                case 0 -> clientA1.write(cadena);
                                case 1 -> clientA2.write(cadena);
                                case 2 -> clientA3.write(cadena);
                            }
                        } else if (word.charAt(2) == '1') {
                            nRand = random.nextInt(2);
                            switch (nRand) {
                                case 0 -> clientB1.write(cadena);
                                case 1 -> clientB2.write(cadena);
                            }
                        } else if (word.charAt(2) == '2') {
                            nRand = random.nextInt(2);
                            switch (nRand) {
                                case 0 -> clientC1.write(cadena);
                                case 1 -> clientC2.write(cadena);
                            }
                        }
                    }
                }
            }
        }
        b.close();
    }

    public static void invocaNodes() {

        // Invoquem els processos de Layer 0 (CoreLayer)
        NodeA nodeA1 = new NodeA(1, Ports.L0_A1_PORT, Ports.L0_A2_PORT, Ports.L0_A3_PORT, Ports.L1_B1_PORT);
        NodeA nodeA2 = new NodeA(2, Ports.L0_A2_PORT, Ports.L0_A1_PORT, Ports.L0_A3_PORT, Ports.L1_B1_PORT);
        NodeA nodeA3 = new NodeA(3, Ports.L0_A3_PORT, Ports.L0_A1_PORT, Ports.L0_A2_PORT, Ports.L1_B2_PORT);

        // Invoquem els processos de Layer 1
        NodeB nodeB1 = new NodeB(1, Ports.L1_B1_PORT, Ports.L0_A2_PORT, Ports.L2_C1_PORT, Ports.L2_C2_PORT);
        NodeB nodeB2 = new NodeB(2, Ports.L1_B2_PORT, Ports.L0_A3_PORT, Ports.L2_C1_PORT, Ports.L2_C2_PORT);


        // Invoquem els processos de Layer 2
        NodeC nodeC1 = new NodeC(1, Ports.L2_C1_PORT, Ports.L1_B2_PORT);
        NodeC nodeC2 = new NodeC(2, Ports.L2_C2_PORT, Ports.L1_B2_PORT);

        nodeA1.start();
        nodeA2.start();
        nodeA3.start();

        nodeB1.start();
        nodeB2.start();

        nodeC1.start();
        nodeC2.start();


        // Bloquea el subproceso de llamada hasta que finaliza el subproceso
        try {
            nodeA1.join();
            nodeA2.join();
            nodeA3.join();

            nodeB1.join();
            nodeB2.join();

            nodeC1.join();
            nodeC2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void crearFitxers() {

        // Crearem un fitxer local per a LAYER 0
        for (int i = 1; i < 4; i++) {
            File f = new File("src\\Layer0\\Versions0\\VA_N" + i + ".txt");
            try {
                if (f.exists()) {
                    f.delete();
                }
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Crearem un fitxer local per a LAYER 1 i 2
        for (int i = 1; i < 3; i++) {
            File f1 = new File("src\\Layer1\\Versions1\\VB_N" + i + ".txt");
            File f2 = new File("src\\Layer2\\Versions2\\VC_N" + i + ".txt");
            try {
                if (f1.exists()) {
                    f1.delete();
                }
                if (f2.exists()) {
                    f2.delete();
                }
                f1.createNewFile();
                f2.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
