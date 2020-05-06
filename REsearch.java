import java.io.*;

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
    public static void main(String[] args){
        //If there isn't 1 argument
        if(args.length != numberOfArguments){
            IncorrectInput();
        }
        else{
            try{
                //Opens the file
                BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                String line = reader.readLine();

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
}