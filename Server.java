import java.net.*;
import java.io.*;

public class Server {

    private static final int qtdJogadores = 2;

    int port = 12345;

    InetAddress[] playerIps = new InetAddress[qtdJogadores];
    int[] playerPorts = new int[qtdJogadores];

    String anagramasCorretos = "";
    String sequenciaCaracteres = "A - S - A - O - O - R - S - M";
    String solucao = "";
    int pontos=0;


    void SendMessage(int id, String msg) {

        try {
            Socket client = new Socket(playerIps[id], playerPorts[id]);
            ObjectOutputStream o = new ObjectOutputStream(client.getOutputStream());
            o.flush();
            o.writeObject(msg);
            o.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    void SendMessage(String adress, int port, String msg) {
        try {
            Socket client = new Socket(adress, port);
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            output.flush();
            output.writeObject(msg);
            output.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    void SendMessage(String msg) {
        try {
            for (int i = 0; i < qtdJogadores; i++) {
                Socket client = new Socket(playerIps[i], playerPorts[i]);
                ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
                output.flush();
                output.writeObject(msg);
                output.close();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    Boolean IsValid(char letter) {
        for (int i = 65; i < 91; i++) {
            if (letter == (char) i)
                return true;
        }
        return false;
    }

    Boolean tentativaRepetida(String palavra) {
        if (!tentativas.contains(palavra)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {// METODO PRINCIPAL

        if (args.length != 1) {
            System.out.println("USAGE: Server <NUMERO DA SEQUENCIA DE CHAR>");
            System.exit(0);
        }
        String solucao = "AMA - AMO - AOS - ARO - ASA - MAO - MAR - MAS - MOR - MOS - ORA -"
                    + " RAS - SAO - SAS - SOM - SOS - AMAR - AMAS - AMOR - AMOS - ARMA - AROS - ASMA - MAOS - "
                    + "ORAS - OSSO - RAMO - RASA - RASO - ROMA - ROSA - SOAR - SORO - AMORA - ARMAS - AROMA - ASSAR - MASSA - MORSA"
                    + "RAMOS - RASAS - RASOS - ROMAS - ROSAS - SOMAR - SOROS - AMAROS - AMORAS - AROMAS"
                    + "ASSOAR - MORSAS - AMOROSA - MOROSAS - AMOROSAS - ASSOMAR";
        Server g = new Server();
        g.StartServer(solucao);
    }

    public void StartServer(String arg) {
        try {

            // Inicia o server na porta 12345
            ServerSocket server = new ServerSocket(12345);
            int contadorJogadores = 0;
            int jogadorAtual = 0;
            int vidas = 9;
            Boolean jogoEmAndamento = false;

            System.out.println("\nSERVIDOR INICIALIZADO");

            while (true) {
                // Espera por conexão
                System.out.println("\nESPERANDO MENSAGEM");
                Socket client_socket = server.accept();
                ObjectInputStream input = new ObjectInputStream(client_socket.getInputStream());
                String msg = (String) input.readObject();// Recebe a mensagem

                System.out.println("MENSAGEM RECEBIDA: " + msg);

                if (msg.startsWith("C")) // CONECTAR SE O PRIMEIRO CHAR FOR C
                {
                    System.out.println("CONECTANDO JOGADOR");
                    if (contadorJogadores == qtdJogadores) {
                        SendMessage(client_socket.getLocalAddress().toString(), client_socket.getLocalPort(), "X");
                        System.out.println("NUMERO MAXIMO DE JOGADORES ATINGIDO"); // nao permite adicionar novo jogador
                                                                                   // se a sala esta cheia
                    } else {
                        playerIps[contadorJogadores] = client_socket.getInetAddress();
                        playerPorts[contadorJogadores] = Integer.parseInt(msg.substring(1));

                        String s = "C" + contadorJogadores;
                        SendMessage(contadorJogadores, s);
                        contadorJogadores++;
                        System.out.println("JOGADOR ADICIONADO: " + contadorJogadores + "/" + qtdJogadores);
                        if (contadorJogadores == qtdJogadores) {
                            s = "S" + String.valueOf(jogadorAtual) + String.valueOf(vidas) + anagramasCorretos;
                            SendMessage(s);
                            jogoEmAndamento = true;// inicia o jogo ja que possui todos os jogadores aguardando
                            System.out.println("\nINICIANDO PARTIDA");
                        }
                    }

                } 
                
                else if (msg.startsWith("J")){ // JOGADA SE O PRIMEIRO CHAR FOR J
                
                    System.out.println("JOGADOR " + jogadorAtual + " ESTA TENTANDO ADIVINHAR UM ANAGRAMA");
                    String s = msg.substring(1).toUpperCase();
                        System.out.println("TENTATIVA: " + s);
                        if (tentativaRepetida(s)) {
                            SendMessage(jogadorAtual, "TENTATIVA REPETIDA");
                            System.out.println("TENTATIVA REPETIDA");
                        }
                            System.out.println("POSICAO NA STRING:"+arg.indexOf(s));
                            if (arg.indexOf(s)>=0) {
                                anagramasCorretos += s + " - ";
                                tentativas += s + " - ";
                                pontos++;
                            } else {
                                tentativas += s + " - ";
                                System.out.println("JOGADOR ERROU O CHUTE");
                                vidas--;
                            }
                
                            System.out.println("VIDAS: " + vidas);
                            if (vidas <= 0)// fim de jogo - derrota
                            {
                                SendMessage("FD" + arg);
                                System.out.println("ACABARAM AS VIDAS");
                                System.out.println("\nOS JOGADORES PERDERAM A PARTIDA");
                                break;
                            }else if(pontos>=10){//fim de jogo - vitoria
                                SendMessage("FV" + arg);
                                System.out.println("\nOS JOGADORES GANHARAM A PARTIDA");
                                break;
                            }                             
                            else {
                                jogadorAtual++;
                                if (jogadorAtual == (qtdJogadores))
                                    jogadorAtual = 0;
                                String newMsg = "R" + String.valueOf(jogadorAtual) + String.valueOf(vidas)
                                        + sequenciaCaracteres + anagramasCorretos ;
                                SendMessage(newMsg);// envia mensagem com cada posição representando uma informação
                                System.out.println("JOGADOR ACERTOU UM ANAGRAMA");
                                System.out.println("PONTUACAO: "+pontos);
                            }

                }

                input.close();
                client_socket.close();

                if (msg.equals("over")) {
                    server.close();
                    break;
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
