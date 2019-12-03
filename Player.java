import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Player {
    
    int serverPort = 12345;
    int portaServidor;
    InetAddress endereco;
    
    static String word;
    
    char ID = ' ';
    
    ServerSocket server;
    ObjectInputStream input;
    Socket client_socket;
    
    String Input()
    {
        Scanner reader = new Scanner(System.in);
        String s = reader.next(); 
        
        return s;
    }
    

    void DrawGame( char vidasRestantes,String tentativasAnagramas ){

        System.out.println("################################################################################");
        System.out.println(" _______  ____ __  _______  _______  ______  _______  ___ ___  _______  _______ ");
        System.out.println("|   _   ||    |  ||   _   ||   ____||   _  )|   _   ||   |   ||   _   ||     __|");
        System.out.println("|   -   ||       ||   -   ||  |__| ||     < |   -   ||       ||   -   ||__     |");
        System.out.println("|___|___||__|____||___|___||_______||___|__||___|___||__|_|__||___|___||_______|");
        System.out.println("################################################################################");
        System.out.println(" ");
        System.out.println("ID JOGADOR: " + ID );
        System.out.println(" ");
        System.out.println("VIDAS: " + vidasRestantes);
        System.out.println(" ");
        System.out.println("CARACTERES: " + word);
        System.out.println(" ");
        System.out.print("TENTATIVAS: ");
        String l = tentativasAnagramas;
        
        System.out.println(l);
    }
    
    void fazerJogada(){   
        System.out.println("");
        System.out.println("SUA VEZ");
        System.out.print("INSIRA UM ANAGRAMA PARA A SEQUENCIA DE CARACTERES: ");
        String s = "J";//String correspondente a fazer uma jogada
        s += Input();
        
        SendMessage(s);
    }
    
    void SendMessage(String msg){
        try
        {
            Socket c = new Socket(endereco,serverPort);
            ObjectOutputStream o = new ObjectOutputStream(c .getOutputStream());
            o.writeObject(msg);

        }
        catch(Exception e) {
          System.out.println("Error: " + e.getMessage());
        }
        
    }
    
    String WaitForMessage()
    {
        try
        {
            Socket c = server.accept();
            input = new ObjectInputStream(c.getInputStream());
            String s = (String)input.readObject();
            return s;
        }
        catch(Exception e) {
          System.out.println("Error: " + e.getMessage());
        }
        return "ERROR";
    }
    
    
    
    public static void main(String[] args) {
        if (args.length != 2){
            System.out.println("USAGE: Player <IP-SERVIDOR> <PORTA>");
            System.exit(0);
        }
        Player p = new Player();
        p.Play(args);
    }

    public void Play(String[] args)
    {
        try {
            endereco = InetAddress.getByName(args[0]);
            portaServidor = Integer.parseInt(args[1]);
            server = new ServerSocket(portaServidor);
            
            Boolean jogoEmAndamento = false;
            
            int wordSize = 0;///acho que nao precisa
            
            SendMessage("C" + portaServidor);

            String msg = WaitForMessage();//Aguarda mensagem do servidor

            if(msg.startsWith("C"))  //Conectado
            {
                jogoEmAndamento = true;
                ID = msg.charAt(1);
                
               
                System.out.println("################################################################################");
                System.out.println(" _______  ____ __  _______  _______  ______  _______  ___ ___  _______  _______ ");
                System.out.println("|   _   ||    |  ||   _   ||   ____||   _  )|   _   ||   |   ||   _   ||     __|");
                System.out.println("|   -   ||       ||   -   ||  |__| ||     < |   -   ||       ||   -   ||__     |");
                System.out.println("|___|___||__|____||___|___||_______||___|__||___|___||__|_|__||___|___||_______|");
                System.out.println("################################################################################");
                System.out.println(" ");
                System.out.println("ID JOGADOR: " + ID);
                System.out.println(" ");
                System.out.println("AGUARDANDO O INICIO DO JOGO");

            }
            else
            {
                System.exit(0);
            }


            while(jogoEmAndamento == true)
            {
                msg = new String();
                msg = WaitForMessage();

                if(msg.startsWith("R"))  //RESPOSTA
                {
                    word = msg.substring(3, 3 + word.length());
                    DrawGame(msg.charAt(2), msg.substring(3 + word.length()));

                    if(msg.charAt(1) == ID)
                    {
                        fazerJogada();
                    }
                    else
                    {
                        System.out.println("\nESPERE A SUA VEZ");
                    }
                }
                else if(msg.startsWith("I"))  //MENSAGEM INVALIDA
                {
                    System.out.println("\n" + msg.substring(1) + "\n");
                    fazerJogada();
                }
                else if(msg.startsWith("F")) //FIM DO JOGO
                {
                    System.out.println("\n\n");
                    if(msg.charAt(1) == 'V')
                    {
                        System.out.println("VITORIA");
                    }
                    else
                    {
                        System.out.println("DERROTA");
                    }
                    System.out.println("A PALAVRA ERA: " + msg.substring(2));
                    break;
                }
                else if(msg.startsWith("S"))  //START GAME
                {
                    
                    //System.out.println("JOGO INICIADO");
                    word = msg.substring(3);
                    DrawGame(msg.charAt(2),"");
                    if(msg.charAt(1) == ID)
                    {
                        fazerJogada();
                    }
                    else
                    {
                        System.out.println("\nESPERE A SUA VEZ");
                    }
                }
                
                else if(msg.startsWith("W"))  //"WAIT" ESPERA PELO TURNO
                {
                    System.out.println("\nESPERE A SUA VEZ");
                }
                else if(msg.startsWith("ERROR"))  //ERROR
                {
                    System.out.println("\nERRO");
                }
            }



        }
        catch(Exception e) {
          System.out.println("Error: " + e.getMessage());
        }
    }
}