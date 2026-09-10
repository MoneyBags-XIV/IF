import java.io.Console;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;


public class Parser {
    
    Room[] rooms;
    Thing[] items;
    Verb[] verbs;
    Console console;
    Thing it;

    public Parser(Room[] rooms, Thing[] items, Verb[] verbs) {
        this.rooms = rooms;
        this.items = items;
        this.verbs = verbs;
        this.console = null;
        this.it = null;         // TODO keep track of last item interacted with
    }

    public Action parse(String input) {
        String[] inputList = cleanInput(input);
        int[] verbIndex = checkVerb(inputList);

        if (Objects.equals(input, "i")) {
            for (int i=0; i<this.verbs.length; i++) {
                if (Objects.equals(this.verbs[i].names[0], "inventory")) {
                    return new Action(this.verbs[i], null, null);
                }
            }
        }

        if (Objects.equals(input, "")) {
            System.out.println("I beg your pardon?");
            return null;
        }

        String[] curseList = new String[]{"fuck", "shit", "bitch"};
        for (int i=0; i<inputList.length; i++) {
            for (int j=0; j<curseList.length; j++) {
                if (inputList[i].contains(curseList[j])) {
                    System.out.println("You kiss your mother with that mouth??");
                    return null;
                }
            }
        }

        if (verbIndex == null) {
            System.out.println("There's no verb in that sentence!");
            return null;
        }

        Verb verb = this.verbs[verbIndex[0]];

        if (Objects.equals(verb.names[0], "hello")) {
            System.out.println("Hello!");
            return null;
        }

        if (Objects.equals(verb.names[0], "go")) {

            inputList = remove(inputList, verb.names[verbIndex[1]]);
            verbIndex = checkVerb(inputList);

            String[] directions = new String[]{"north", "south", "east", "west", "northeast", "southeast", "southwest", "northwest", "up", "down", "in", "out"};

            if (verbIndex != null) {
                verb = this.verbs[verbIndex[0]];
                for (int i=0; i<directions.length; i++) {
                    if (Objects.equals(verb.names[0], directions[i])) {
                        return new Action(verb, null, null);
                    }
                }
            }

            String goResponse = this.console.readLine("Which way do you want to go?\n\n>>> ");
            String[] goResponseList = cleanInput(goResponse);
            verbIndex = checkVerb(goResponseList);
            if (verbIndex == null) {
                System.out.println("I don't know which way you want to go!");
                return null;
            }

            verb = this.verbs[verbIndex[0]];

            for (int i=0; i<directions.length; i++) {
                if (Objects.equals(directions[i], verb.names[0])) {
                    return new Action(verb, null, null);
                }
            }
            System.out.println("I don't know which way you want to go!");
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
            return new Action(verb, null, null);
        }

        Thing[] directs = checkDirect(inputList);

        if (directs.length == 0) {
            if (!verb.needsDirect) {
                return new Action(verb, null, null);
            }
            String directResponse = this.console.readLine("What do you want to " + verb.names[verbIndex[1]] + "?\n\n>>> ");
            String[] directResponseList = cleanInput(directResponse);
            directs = checkDirect(directResponseList);
            if (directs.length == 0) {
                System.out.println("I don't understand what you're trying to do!");
                return null;
            }
        }

        if (directs.length > 1) {
            if (!verb.acceptsMultipleDirect) {
                System.out.println("You can't " + verb.names[verbIndex[1]] + " multiple things at once.");
                return null;
            }
        }

        if (indirect == null && !verb.needsIndirect) {
            return new Action(verb, directs, null);
        }

        if (indirect != null) {
            return new Action(verb, directs, indirect);
        }

        String indirectResponse = this.console.readLine("What do you want to " + verb.names[verbIndex[1]] + " the " + directs[0].names[0] + " " + verb.indirectIndicator[0] + "?\n\n>>> ");
        String[] indirectResponseList = cleanInput(indirectResponse);
        indirect = checkDirect(indirectResponseList)[0];

        if (indirect == null) {
            System.out.println("I don't understand what you're trying to do!");
            return null;
        }

        return new Action(verb, directs, indirect);
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
            }
            else {
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

        return ans.toArray(new Thing[0]);
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