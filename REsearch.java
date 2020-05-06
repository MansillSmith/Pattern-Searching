import java.io.*;
import java.util.*;

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
                //Adds the final state
                characterMatchList.add("FI");
                firstNextState.add(-1);
                secondNextState.add(-1);

                input.close();

                PrintList(characterMatchList, firstNextState, secondNextState);

                int lineNumber = 0;
                line = reader.readLine();
                //While there are more lines to read
                while(line != null){
                    //Marks where the start of the substring being read is
                    int mark = 0;
                    //Marks the current character being looked at
                    int pointer = 0;
                    //Records the current and next states
                    Stack<Integer> currentStates = new Stack<Integer>();
                    Stack<Integer> nextStates = new Stack<Integer>();

                    //Initialises with the first state
                    currentStates.push(0);
                    boolean found = false;

                    while(!found && mark + pointer <= line.length()){

                        //Loops through all of the states on the current states stack
                        while(!found && mark + pointer <= line.length()){
                            int s = 0;
                            try{
                                s = currentStates.pop();
                            }
                            catch(EmptyStackException e){
                                break;
                            }
                            //System.err.println(s);
                            //System.err.println(characterMatchList.get(s));
                            //If the current state is a branching state
                            if(characterMatchList.get(s).equals("BR")){
                                //Pushes the next states onto the current states
                                PushToStack(firstNextState.get(s), secondNextState.get(s), currentStates);
                                //System.err.println("BR, " + firstNextState.get(s));

                            }
                            //If the current state is the final state
                            else if(characterMatchList.get(s).equals("FI")){
                                found = true;
                                //System.err.println("FOUND");
                            }
                            else {
                                //Gets the character as a character, as it cannot be a BR
                                char characterToMatch = characterMatchList.get(s).charAt(0);

                                try{
                                    //If the character matches
                                    if(line.charAt(pointer) == characterToMatch){
                                        PushToStack(firstNextState.get(s), secondNextState.get(s), nextStates);
                                    }
                                }
                                catch(Exception e){
                                    break;
                                }
                            }
                        }

                        //Next states is empty
                        if(nextStates.empty()){
                            //Reset the machine
                            mark++;
                            pointer = mark;

                            //Both stacks should be empty
                            currentStates.push(0);
                        }
                        else{
                            //Make all next states current states
                            currentStates = nextStates;
                            nextStates = new Stack<Integer>();
                            pointer ++;
                        }
                    }

                    if(found){
                        //Output the line number
                        System.out.println(lineNumber + ", " + line);
                    }

                    lineNumber++;
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
        System.err.println();
    }

    //Pushes the two next possible states to the stack
    private static void PushToStack(int first, int second, Stack<Integer> st){
        st.push(first);
        if(first != second){
            st.push(second);
        }
    }
}