import java.util.*;

public class REcompileTestDummyStates {

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

        System.err.println(input);
        printStates();
    }

    private static int expression(){
        int start = term();

        if((pointer <= input.length() - 1) && ((getChar().equals("(")) || !(getChar().equals(")")))){
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

            if(!getSpecificChar(pointer - 2).equals(")")){
                fsm.copyToNextIndex(stateNumber - 1);
                updateState(stateNumber - 2, stateNumber, stateNumber, "BR");
            }
            else{
                int temp = stateNumber - 1;
                int target = stateStart.get(stateStart.size() - 1);

                while(temp >= target){
                    fsm.copyToNextIndex(temp);
                    temp--;
                }

                updateState(temp + 1, temp + 2, temp + 2, "BR");
            }

            setState(stateNumber, "BR", s1, stateNumber + 1);
            stateNumber++;

            if(pointer > input.length() - 1){
                return s1;
            }

            if(pointer <= input.length() - 1 && (getChar().equals("*") || getChar().equals("?"))){
                error();
            }

            return stateNumber - 1;
        }
        else if(getChar().equals("?")){
            pointer++;

            if(!getSpecificChar(pointer - 2).equals(")")){
                fsm.copyToNextIndex(stateNumber - 1);
                updateState(stateNumber - 2, stateNumber, stateNumber, "BR");
            }
            else{
                int temp = stateNumber - 1;
                int target = stateStart.get(stateStart.size() - 1);

                while(temp >= target){
                    fsm.copyToNextIndex(temp);
                    temp--;
                }

                updateState(temp + 1, temp + 2, temp + 2, "BR");
            }

            setState(stateNumber, "BR", s1 + 1, stateNumber + 1);
            stateNumber++;

            //updateState(s1, stateNumber, stateNumber);
            if(pointer <= input.length() - 1 && (getChar().equals("*") || getChar().equals("?"))){
                error();
            }

            return stateNumber - 1;
        }
        else if(getChar().equals("|")){
            pointer++;
            int tempPointer = pointer - 1;
            int tempStateNum = stateNumber - 1;
            String s = getSpecificChar(tempPointer);
            int closedBrackets = 0;

            while(true){
                if(tempPointer == 0){
                    tempStateNum = 1;
                    break;
                }
                else if(getSpecificChar(tempPointer - 1).equals("\\")){
                    tempPointer -= 2;
                    tempStateNum--;
                }
                else if(s.equals(")")){
                    closedBrackets++;
                    tempPointer--;
                    
                }
                else if(s.equals("(")){
                    if(closedBrackets == 0){
                        tempStateNum++;
                        break;
                    }
                    else{
                        closedBrackets--;
                        tempPointer--;
                    }
                }
                else if(s.equals("|")){
                    tempPointer--;
                }
                else{
                    tempStateNum--;
                    tempPointer--;
                }

                while(fsm.viewCharacter(tempStateNum).equals("BR")){
                    tempStateNum--;
                }

                s = getSpecificChar(tempPointer);
            }

            int temp = stateNumber - 1;
            int target = tempStateNum;  
            while(temp >= target){
                fsm.copyToNextIndex(temp);
                temp--;
            }
            System.err.println("Temp: " + (temp + 1));
            updateState(temp + 1, temp + 2, temp + 2, "BR");
            //if(stateStart.size() == 1){
            //    stateStart.set(0, stateStart.get(0) + 1);
            //}
            stateStart.set(stateStart.size() - 1, stateStart.get(stateStart.size() - 1) + 1);
            

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
            updateState(temp + 1, branchState, branchState);

            if(pointer > input.length() - 1){
                return s1;
            }

            if(pointer <= input.length() - 1 && (getChar().equals("*") || getChar().equals("?") || getChar().equals("|"))){
                error();
            }

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
            
            if(!isVocab(getChar()) && !isSpecialChar(getChar())){
                error();
            }
            
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

    private static boolean isVocab(String c){
        //return true;

        if(c.matches("[a-zA-Z]")){
            return true;
        }else{
            return false;
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
        System.err.println("Custom Error");
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

    private static void updateState(int state, int n1, int n2, String s){
        fsm.update(state, n1, n2, s);
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

        public void update(int state, int n1, int n2, String s){
            if(state > ch.size()){
                System.err.println("Updating Error");
            }else{
                ch.set(state, s);
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

        public void copyToNextIndex(int atIndex){
            if(atIndex >= (ch.size() - 1)){
                ch.add(ch.get(ch.size() - 1));
                next1.add(next1.get(next1.size() - 1) + 1);
                next2.add(next2.get(next2.size() - 1) + 1);
                stateNumber++;
            }
            else{
                ch.set((atIndex + 1), ch.get(atIndex));
                next1.set((atIndex + 1), (next1.get(atIndex) + 1));
                next2.set((atIndex + 1), (next2.get(atIndex) + 1));
            }
        }
    }
}
