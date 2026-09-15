import java.io.*;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;


public class Parser implements Serializable {
    
    Room[] rooms;
    Thing[] items;
    Verb[] verbs;
    Thing it;

    public Parser(Room[] rooms, Thing[] items, Verb[] verbs) {
        this.rooms = rooms;
        this.items = items;
        this.verbs = verbs;
        this.it = null;
    }

    public Action parse(String input, Console console) {

        String[] inputList = cleanInput(input);
        int[] verbIndex = checkVerb(inputList);

        if (Objects.equals(input, "i")) {
            for (int i=0; i<this.verbs.length; i++) {
                if (Objects.equals(this.verbs[i].names[0], "inventory")) {
                    this.it = null;
                    return new Action(this.verbs[i], null, null);
                }
            }
        }

        if (Objects.equals(input, "")) {
            System.out.println("I beg your pardon?");
            this.it = null;
            return null;
        }

        for (int i=0; i<inputList.length; i++) {
            if (Objects.equals(inputList[i], "asdfghjkl")) {
                System.out.println("qwertyuiop");
                this.it = null;
                return null;
            }
            if (Objects.equals(inputList[i], "qwertyuiop")) {
                System.out.println("asdfghjkl");
                this.it = null;
                return null;
            }
        }

        String[] curseList = new String[]{"fuck", "shit", "bitch"};
        for (int i=0; i<inputList.length; i++) {
            for (int j=0; j<curseList.length; j++) {
                if (inputList[i].contains(curseList[j])) {
                    System.out.println("You kiss your mother with that mouth??");
                    this.it = null;
                    return null;
                }
            }
        }

        if (verbIndex == null) {
            System.out.println("There's no verb in that sentence!");
            this.it = null;
            return null;
        }

        Verb verb = this.verbs[verbIndex[0]];

        if (Objects.equals(verb.names[0], "go")) {

            inputList = remove(inputList, verb.names[verbIndex[1]]);
            verbIndex = checkVerb(inputList);

            String[] directions = new String[]{"north", "south", "east", "west", "northeast", "southeast", "southwest", "northwest", "up", "down", "in", "out"};

            if (verbIndex != null) {
                verb = this.verbs[verbIndex[0]];
                for (int i=0; i<directions.length; i++) {
                    if (Objects.equals(verb.names[0], directions[i])) {
                        this.it = null;
                        return new Action(verb, null, null);
                    }
                }
            }

            String goResponse = console.readLine("Which way do you want to go?\n\n>>> ");
            String[] goResponseList = cleanInput(goResponse);
            verbIndex = checkVerb(goResponseList);
            if (verbIndex == null) {
                System.out.println("I don't know which way you want to go!");
                this.it = null;
                return null;
            }

            verb = this.verbs[verbIndex[0]];

            for (int i=0; i<directions.length; i++) {
                if (Objects.equals(directions[i], verb.names[0])) {
                    this.it = null;
                    return new Action(verb, null, null);
                }
            }
            System.out.println("I don't know which way you want to go!");
            this.it = null;
            return null;
        }
        
        Thing indirect = null;

        if (verb.acceptsIndirect) {
            indirect = checkIndirect(inputList, verb.indirectIndicator);

            if (indirect != null) {
                for (int i=0; i<indirect.names.length; i++) {
                    inputList = remove(inputList, indirect.names[i]);
                }
            }
        }

        if (!verb.acceptsDirect) {
            this.it = null;
            return new Action(verb, null, null);
        }

        Thing[] directs = checkDirect(inputList);

        if (directs.length == 0) {
            if (!verb.needsDirect) {
                this.it = null;
                return new Action(verb, null, null);
            }
            String directResponse = console.readLine("What do you want to " + verb.names[verbIndex[1]] + "?\n\n>>> ");
            String[] directResponseList = cleanInput(directResponse);
            directs = checkDirect(directResponseList);
            if (directs.length == 0) {
                System.out.println("I don't understand what you're trying to do!");
                this.it = null;
                return null;
            }
        }

        if (directs.length > 1 && !verb.acceptsMultipleDirect) {
            System.out.println("You can't " + verb.names[verbIndex[1]] + " multiple things at once.");
            this.it = null;
            return null;
        }

        if (directs.length == 1) {
            this.it = directs[0];
        } else {
            this.it = null;
        }

        if (indirect == null && !verb.needsIndirect) {
            return new Action(verb, directs, null);
        }

        if (indirect != null) {

            return new Action(verb, directs, indirect);
        }

        String indirectResponse = console.readLine("What do you want to " + verb.names[verbIndex[1]] + " the " + directs[0].names[0] + " " + verb.indirectIndicator[0] + "?\n\n>>> ");
        String[] indirectResponseList = cleanInput(indirectResponse);
        Thing[] indirectList = checkDirect(indirectResponseList);

        if (indirectList.length == 0) {
            System.out.println("I don't understand what you're trying to do!");
            return null;
        }

        return new Action(verb, directs, indirectList[0]);
    }

    public int[] checkVerb(String[] input) {
        for (int k=0; k<input.length; k++) {
            for (int i=0; i<this.verbs.length; i++) {
                for (int j=0; j<this.verbs[i].names.length; j++) {
                    if (Objects.equals(input[k], this.verbs[i].names[j])) {
                        return new int[]{i,j};
                    }
                }
            }
        }
        return null;
    }

    public Thing checkIndirect(String[] input, String indicator[]) {
        boolean foundIndicator = false;
        for (int i=0; i<input.length; i++) {
            if (!foundIndicator) {
                for (int j=0; j<indicator.length; j++) {
                    if (Objects.equals(input[i], indicator[j])) {
                        foundIndicator = true;
                    }
                }
            } else {
                for (int j=0; j<this.items.length; j++) {
                    for (int k=0; k<this.items[j].names.length; k++) {
                        if (Objects.equals(this.items[j].names[k], input[i])) {
                            return this.items[j];
                        }
                    }
                }
            }
        }
        return null;
    }

    public Thing[] checkDirect(String[] input) {
        ArrayList<Thing> ans = new ArrayList<Thing>();

        for (int i=0; i<input.length; i++) {
            for (int j=0; j<this.items.length; j++) {
                for (int k=0; k<this.items[j].names.length; k++) {
                    if (Objects.equals(input[i], this.items[j].names[k])) {
                        ans.add(this.items[j]);
                    }
                }
            }
        }

        Thing[] directs = ans.toArray(new Thing[0]);
        
        if (directs.length == 0) {
            for (int i=0; i<input.length; i++) {
                if (Objects.equals(input[i], "it") && !(this.it == null)) {
                    directs = new Thing[]{this.it};
                }
            }
        }

        return directs;
    }

    public String[] remove(String[] phrase, String word) {
        ArrayList<String> phraseList = new ArrayList<String>(Arrays.asList(phrase));
        phraseList.remove(word);
        return phraseList.toArray(new String[0]);
    }

    public String[] cleanInput(String input) {
        return input.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split(" ");
    }
}