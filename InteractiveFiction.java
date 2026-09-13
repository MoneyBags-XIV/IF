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

            ans = console.readLine("\n>>> ");
            action = parser.parse(ans);

            if (action != null) {
                try {
                    player.handleInput(action);
                } catch (Exception e) {
                    System.out.println(e);
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
            new Verb(new String[]{"chuck", "throw", "fling", "toss", "hurl"}, true, true, true, true, false, new String[]{"at", "towards"}),
            new Verb(new String[]{"smell", "sniff"}, true, false, true, false, true, new String[]{}),
            new Verb(new String[]{"eat"}, true, false, true, false, true, new String[]{}),
            new Verb(new String[]{"open"}, true, false, true, false, true, new String[]{}),
            new Verb(new String[]{"close"}, true, false, true, false, true, new String[]{}),
        };


        //==============================ROOMS==============================

        Room kitchen = new Room(new String[]{"Kitchen", "kitchen"}, "There are too many cooks here. Through a doorway to the north, you can see the Dining Room.", 20) {
            @Override
            public String north(Player player) {
                if (!player.contains(this.keepTrackOf[0])) {
                    return "You can't go into the Dining Room unless you are holding a knife.\n";
                }
                return super.north(player);
            }
        };
        Room dining = new Room(new String[]{"Dining Room", "dining"}, "Fine dining here. A doorway to the south leads back to the Kitchen.", 20);

        kitchen.n = dining;
        dining.s = kitchen;

        Room[] rooms = new Room[] {
            kitchen,
            dining,
        };


        //==============================ITEMS==============================
        
        Player player = new Player(rooms[0], "You are such a player.", 10){
            @Override
            public String getHit(Thing indirect) {
                if (!indirect.deadly) {
                    return "The " + indirect.names[0] + " isn't deadly enough to do any damage.";
                }
                System.out.println("Well, if you insist...\nYOU HAVE DIED");
                System.exit(0);
                return "";
            }
        };

        Item knife = new Item(new String[]{"knife"}, "This steak knife is a sharp example object.", 0);
        knife.deadly = true;

        kitchen.keepTrackOf = new Thing[]{knife};

        Item bag = new Item(new String[]{"bag"}, "This is a normal bag.", 10);
        bag.container = true;
        bag.open = true;
        kitchen.addToContents(bag);

        Item spoon = new Item(new String[]{"spoon"}, "This is a soup spoon. Don't use it for the wrong course.", 0);
        dining.addToContents(spoon);

        kitchen.addToContents(knife);

        // kitchen.addToContents(player);

        Thing[] items = new Thing[] {
            player,
            knife,
            spoon,
            bag,
        };

        Parser parser = new Parser(rooms, items, verbs);

        Game game = new Game(verbs, items, rooms, parser, player);

        player.game = game;
        return game;
    }
}