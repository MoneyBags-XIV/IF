import java.util.ArrayList;
import java.io.Console;
import java.util.Objects;
import java.util.Arrays;


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
                try {
                    player.handleInput(action);
                } catch (Exception e) {
                    System.out.println("no verb executed");
                }
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