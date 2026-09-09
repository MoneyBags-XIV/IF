import java.util.ArrayList;
import java.io.Console;
import java.util.Objects;
import java.util.Arrays;


class Thing {
    String[] names;
    String description;
    Item[] contents;
    int capacity;

    public void doTurn() {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null) {
                this.contents[i].doTurn();
            }
        }
    }

    public void addToContents(Item item) {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] == null) {
                this.contents[i] = item;
                return;
            }
        }
    }

    public void removeFromContents(Item item) {
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] == item) {
                this.contents[i] = null;
                return;
            }
        }
    }

    public String listContents() {
        ArrayList<Item> contents = new ArrayList<Item>();
        
        for (int i=0; i<this.capacity; i++) {
            if (this.contents[i] != null) {
                contents.add(this.contents[i]);
            }
        }

        Item[] ans = contents.toArray(new Item[0]);
        
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<ans.length; i++) {
            stringBuilder.append(ans[i].toString());
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return this.names[0];
    }
}


class Room extends Thing {

    Room n;
    Room s;
    Room e;
    Room w;
    Room ne;
    Room se;
    Room sw;
    Room nw;
    Room u;
    Room d;
    Room i;
    Room o;

    public Room(String[] names, String description, int capacity) {
        this.description = description;
        this.names = names;
        this.contents = new Item[capacity];
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return this.names[0] + '\n' + this.description;
    }
}


class Item extends Thing {

    public Item(String[] names, String description, int capacity) {
        this.names = names;
        this.description = description;
        this.contents = new Item[capacity];
        this.capacity = capacity;
    }    
}


class Player extends Thing {
    public Player(Room location, String description, int capacity) {
        this.contents = new Item[capacity];
        this.capacity = capacity;
        this.names = new String[] {"self", "me", "player", "myself"};
        this.description = description;
    }

    public void handleInput(Action action) {


        
        this.doTurn();
        return;
    }
}


class Parser {
    
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
            System.out.println("Pardon?");
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

            String goResponse = this.console.readLine("Which way do you want to go?\n>>> ");
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
            String directResponse = this.console.readLine("What do you want to " + verb.names[verbIndex[1]] + "?\n>>> ");
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

        String indirectResponse = this.console.readLine("What do you want to " + verb.names[verbIndex[1]] + " the " + directs[0].names[0] + " " + verb.indirectIndicator[0] + "?\n>>> ");
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


class Action {

    Verb verb;
    Thing[] direct;
    Thing indirect;

    public Action(Verb verb, Thing[] direct, Thing indirect) {
        this.verb = verb;
        this.direct = direct;
        this.indirect = indirect;
    }
}


class Verb {

    String[] names;
    boolean needsDirect;
    boolean needsIndirect;
    boolean acceptsDirect;
    boolean acceptsIndirect;
    boolean acceptsMultipleDirect;
    String[] indirectIndicator;

    public Verb(String[] names, boolean needsDirect, boolean needsIndirect, boolean acceptsDirect, boolean acceptsIndirect, boolean acceptsMultipleDirect, String[] indirectIndicator) {
        this.names = names;
        this.needsDirect = needsDirect;
        this.needsIndirect = needsIndirect;
        this.acceptsDirect = acceptsDirect;
        this.acceptsIndirect = acceptsIndirect;
        this.acceptsMultipleDirect = acceptsMultipleDirect;
        this.indirectIndicator = indirectIndicator;
    }
}


public class Game {

    Verb[] verbs;
    Thing[] items;
    Room[] rooms;
    Parser parser;
    Player player;

    public Game(Verb[] verbs, Thing[] items, Room[] rooms, Parser parser, Player player) {
        this.verbs = verbs;
        this.items = items;
        this.rooms = rooms;
        this.parser = parser;
        this.player = player;
    }

}


public class InteractiveFiction {
    public static void main(String[] args) {

        Console console = System.console();

        Game game = defineDefaultGame();
        Verb[] verbs = game.verbs;
        Thing[] items = game.items;
        Room[] rooms = game.rooms;
        Player player = game.player;
        Parser parser = game.parser;
        parser.console = console;


        String ans;
        int turn = 0;
        Action action;

        while (true) {

            ans = console.readLine(">>> ");
            action = parser.parse(ans);

            if (action != null) {
                player.handleInput(action);
            }

            for (int i=0; i<rooms.length; i++) {
                rooms[i].doTurn();
            }

            for (int i=1; i<items.length; i++) {    //i=1 so the player doesn't doTurn twice
                items[i].doTurn();
            }

            turn++;
        }
    }

    public static Game defineDefaultGame() {


        //==============================VERBS==============================

        Verb[] verbs = new Verb[]{
            new Verb(new String[]{"hello", "hi", "hey"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"look", "l", "inspect", "examine", "search"}, false, false, true, false, true, new String[]{}),
            new Verb(new String[]{"inventory", "holding"}, false, false, false, false, false, new String[]{}),

            new Verb(new String[]{"go", "move", "walk", "run"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"north", "n"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"south", "s"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"east", "e"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"northeast", "ne"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"southeast", "se"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"southwest", "sw"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"northwest", "nw"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"west", "w"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"up", "u"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"down", "d"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"in", "enter"}, false, false, false, false, false, new String[]{}),
            new Verb(new String[]{"out", "leave"}, false, false, false, false, false, new String[]{}),
            
            new Verb(new String[]{"take", "pick", "pickup", "grab", "hold", "keep"}, true, false, true, false, true, new String[]{}),
            new Verb(new String[]{"drop", "leave", "set", "put", "place", "store"}, true, false, true, true, true, new String[]{"in", "inside", "into"}),
            new Verb(new String[]{"hit", "kill", "attack", "cut"}, true, true, true, true, false, new String[]{"with", "use", "using"}),
            new Verb(new String[]{"throw", "chuck", "fling", "toss", "hurl"}, true, true, true, true, false, new String[]{"at", "towards"}),
            new Verb(new String[]{"smell", "sniff"}, true, false, true, false, true, new String[]{}),
            new Verb(new String[]{"eat"}, true, false, true, false, true, new String[]{}),
        };


        //==============================ROOMS==============================

        Room kitchen = new Room(new String[]{"Kitchen", "kitchen"}, "There are too many cooks here.", 20);
        Room dining = new Room(new String[]{"Dining Room", "dining"}, "Fine dining here.", 20);

        kitchen.n = dining;
        dining.s = kitchen;

        Room[] rooms = new Room[] {
            kitchen,
            dining,
        };


        //==============================ITEMS==============================
        
        Player player = new Player(rooms[0], "You are such a player.", 10);

        Item knife = new Item(new String[]{"knife"}, "This steak knife is a sharp example object.", 0);

        Thing[] items = new Thing[] {
            player,
            knife,
        };

        Parser parser = new Parser(rooms, items, verbs);

        return new Game(verbs, items, rooms, parser, player);
    }
}