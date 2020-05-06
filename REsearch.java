import java.io.*;
import java.util.ArrayList;

/*
Mansill Smith
ID: 1341291

Alex Grant
ID: 

Accepts a Finite State Machine from REcomplile through standard input
and a text file as an argument
The line number of each line of text which contains the regex pattern is outputted
*/
public class REsearch{
    //The number of arguments for the program to accept
    static int numberOfArguments = 1;
    //Stores the characters to match with
    static ArrayList<String> characterMatchList = new ArrayList<String>();
    //Stores the next states to go to
    static ArrayList<Integer> firstNextState = new ArrayList<Integer>();
    static ArrayList<Integer> secondNextState = new ArrayList<Integer>();

    static String characterToSplitFSM = " ";

    public static void main(String[] args){
        //If there isn't 1 argument
        if(args.length != numberOfArguments){
            IncorrectInput();
        }
        else{
            try{
                //Opens the file
                BufferedReader reader = new BufferedReader(new FileReader(args[0]));

                //Get the FSM from standard input
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                
                String line = input.readLine();
                while(line != null){
                    String[] values = line.split(characterToSplitFSM);

                    //Reads the values from the FSM
                    int stateNum = Integer.parseInt(values[0]);
                    String character = values[1];
                    int fState = Integer.parseInt(values[2]);
                    int sState = Integer.parseInt(values[3]);

                    //Adds the values to the list
                    characterMatchList.add(character);
                    firstNextState.add(fState);
                    secondNextState.add(sState);

                    line = input.readLine();
                }
                input.close();

                //PrintList(characterMatchList, firstNextState, secondNextState);

                line = reader.readLine();
                //While there are more lines to read
                while(line != null){
                    System.out.println(line);
                    line = reader.readLine();
                }

                reader.close();
            }
            catch(IOException e){
                IncorrectInput();
            }
        }
    }

    //Error statement
    private static void IncorrectInput(){
        System.err.println("Error: File not found");
    }

    private static void PrintList(ArrayList<String> a, ArrayList<Integer> b, ArrayList<Integer> c){
        for(int i = 0; i < a.size(); i++){
            System.err.println(a.get(i) + "," + b.get(i) + "," + c.get(i));
        }
    }
}