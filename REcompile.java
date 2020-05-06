import java.util.*;

public class REcompile {
    static String input;
    static int pointer = 0;
    static int stateNumber = 1;
    static List<Integer> stateStart = new ArrayList<>();

    static FSM fsm = new FSM();

    public static void main(String[] args) {
        input = args[0];

        stateStart.add(stateNumber);
        
        int start = expression();

        updateState(0, start, start);
        
        printStates();
    }

    private static int expression(){
        int start = term();

        if(pointer <= input.length() - 1){
            expression();
        }
        else if(pointer > input.length() - 1){
            return start;
        }
        
        return start;
    }

    private static int term(){
        int s1 = factor();

        if(pointer > input.length() - 1){
            return s1;
        }
        else if(getChar().equals("*")){
            pointer++;

            if(pointer > input.length() - 1){
                return s1;
            }

            if(pointer <= input.length() - 1 && (getChar().equals("*") || getChar().equals("?"))){
                error();
            }

            setState(stateNumber, "BR", s1, stateNumber + 1);
            stateNumber++;

            return stateNumber - 1;
        }
        else if(getChar().equals("?")){
            pointer++;

            if(pointer <= input.length() - 1 && (getChar().equals("*") || getChar().equals("?"))){
                error();
            }

            setState(stateNumber, "BR", s1, stateNumber + 1);
            stateNumber++;

            updateState(s1, stateNumber, stateNumber);

            return stateNumber - 1;
        }
        else if(getChar().equals("|")){
            pointer++;

            if(pointer > input.length() - 1){
                return s1;
            }

            if(pointer <= input.length() - 1 && (getChar().equals("*") || getChar().equals("?") || getChar().equals("|"))){
                error();
            }

            //Set aside a dummy state that branches to the correct state
            int dummyState = stateNumber;
            //Create the dummy state and assign it values (will be reassigned values later)
            setState(dummyState, "BR", 0, 0);
            //Increment the state number
            stateNumber++;
            int branchState = stateNumber;
            stateNumber++;
            //Make the branching state which points to the first term and the second term
            setState(branchState, "BR", s1, 0);
            //Calling term() increments the stateNumber to the next state to be created, so assign the dummy state the value of the next state to be created
            int term2 = expression();
            
            updateState(branchState, stateStart.get(stateStart.size() - 1), term2);
            if((stateStart.size() - 1 >= 0) && ((stateStart.get(stateStart.size() - 1) - 1) >= 0)){
                updateState(stateStart.get(stateStart.size() - 1) - 1, branchState, branchState);
            }            
            updateState(dummyState, stateNumber, stateNumber);

            return branchState;
        }

        return s1;
    }

    private static int factor(){
        String c = getChar();

        if(c.equals(".")){
            setState(stateNumber, "WC", stateNumber + 1, stateNumber + 1);
            pointer++;
            stateNumber++;
            return stateNumber - 1;
        }
        else if(c.equals("\\")){
            pointer++;

            setState(stateNumber, getChar(), stateNumber + 1, stateNumber + 1);
            stateNumber++;
            pointer++;
            return stateNumber - 1;
        }
        else if(c.equals("(")){
            pointer++;
            stateStart.add(stateNumber);

            int start = expression();
            if(!getChar().equals(")")){
                error();
            }

            stateStart.remove(stateStart.size() - 1);
            pointer++;
            return start;
        }
        else{
            setState(stateNumber, c, stateNumber + 1, stateNumber + 1);
            pointer++;
            stateNumber++;
            return stateNumber - 1;
        }        
    }

    private static boolean isSpecialChar(String c){
        if(c.equals(".") || c.equals("*") || c.equals("(") || c.equals(")") || c.equals("|")){
            return true;
        }else{
            return false;
        }
    }

    private static String getChar(){
        return Character.toString(input.charAt(pointer));
    }

    private static String getSpecificChar(int i){
        return Character.toString(input.charAt(i));
    }

    private static void error(){
        System.err.println("Incorrect Regex Statement");
        System.exit(0);
    }

    private static void printStates(){
        fsm.print();
    }

    private static void setState(int state, String character, int n1, int n2){
        fsm.add(state, character, n1, n2);
    }

    private static void updateState(int state, int n1, int n2){
        fsm.update(state, n1, n2);
    }

    static class FSM{
        List<String> ch = new ArrayList<>();
        List<Integer> next1 = new ArrayList<>();
        List<Integer> next2 = new ArrayList<>();

        FSM(){
            ch.add("BR");
            next1.add(1);
            next2.add(1);
        }

        public void add(int state, String character, int n1, int n2){
            if(state == ch.size()){
                ch.add(character);
                next1.add(n1);
                next2.add(n2);
            }else{
                System.err.println(ch.size());
                System.err.println("Adding Error");
            }
        }

        public void update(int state, int n1, int n2){
            if(state > ch.size()){
                System.err.println("Updating Error");
            }else{
                next1.set(state, n1);
                next2.set(state, n2);
            }            
        }

        public void print(){
            for(int i = 0; i < ch.size(); i++){
                System.out.println(i + " " + ch.get(i) + " " + next1.get(i) + " " + next2.get(i));
            }
        }

        public String viewCharacter(int state){
            return ch.get(state);
        }
    }

}