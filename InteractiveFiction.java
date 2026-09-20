import java.util.ArrayList;
import java.io.*;
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


        String ans;
        Action action;

        Game tmp;

        while (true) {

            System.out.print("\n>>> ");
            game.updateTopBar();
            ans = console.readLine();
            action = parser.parse(ans, console);


            if (action != null) {

                if (Objects.equals(action.verb.names[0], "save")) {
                    save(game, console);
                    continue;
                }
                else if (Objects.equals(action.verb.names[0], "load")) {
                    tmp = load(console);
                    if (tmp != null) {
                        game = tmp;
                        verbs = game.verbs;
                        items = game.items;
                        rooms = game.rooms;
                        player = game.player;
                        parser = game.parser;
                    }
                    continue;
                }
                else if (Objects.equals(action.verb.names[0], "restart")) {
                    tmp = defineDefaultGame();
                    ans = console.readLine("Confirm restarting game? (yes/no)\n>>> ");
                    if (ans.equalsIgnoreCase("yes") || ans.equalsIgnoreCase("y")) {
                        game = tmp;
                        verbs = game.verbs;
                        items = game.items;
                        rooms = game.rooms;
                        player = game.player;
                        parser = game.parser;
                    }
                    continue;
                }

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

            game.turns++;
        }
    }

    public static void save(Game game, Console console) {
        File folder = new File(".");
        File[] list = folder.listFiles();

        String name;
        String extension;
        String[] splitName;

        int highestSave = 0;
        int num;

        for (int i=0; i<list.length; i++) {
            if (!list[i].isFile()) {continue;}

            name = list[i].getName();

            splitName = name.split("\\.");

            // if (splitName.length < 2) {continue;}

            extension = splitName[splitName.length -1];

            if (!Objects.equals(extension, "dat")) {continue;}

            try {
                num = Character.getNumericValue(splitName[0].charAt(splitName[0].length() -1));
                if (num > highestSave) {
                    highestSave = num;
                }
            } catch(Exception e){}
        }

        highestSave += 1;

        String filename = console.readLine("Name this save (Default is save" + highestSave + ".dat)\n>>> ");
        if (filename.length() == 0) {
            filename = "save" + highestSave + ".dat";
        }
        splitName = filename.split("\\.");
        extension = splitName[splitName.length -1];

        if (!Objects.equals(extension, "dat")) {
            filename += ".dat";
        }

        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(game);
            out.close();
            file.close();
            System.out.println("Saved.");
        } catch(Exception e){}
    }

    public static Game load(Console console) {
        File folder = new File(".");
        File[] list = folder.listFiles();

        String name;
        String extension;
        String[] splitName;
        ArrayList<String> namesList = new ArrayList<String>();

        for (int i=0; i<list.length; i++) {
            if (!list[i].isFile()) {continue;}

            name = list[i].getName();
            splitName = name.split("\\.");
            extension = splitName[splitName.length -1];

            if (!Objects.equals(extension, "dat")) {continue;}

            namesList.add(name);
        }

        String[] names = namesList.toArray(new String[0]);

        String filename;

        if (names.length == 0) {
            System.out.println("No saves found.");
            return null;
        }
        else if (names.length == 1) {
            filename = names[0];
        }

        String input;
        int selection;
        while (true) {
            for (int i=0; i<names.length; i++) {
                System.out.println(i + 1 + ") " + names[i]);
            }
            input = console.readLine("Load which save? (1-" + names.length + ")\n>>> ");
            try {
                selection = Integer.parseInt(input);
                filename = names[selection-1];
                break;
            }
            catch(Exception e){
                System.out.println("Please make a valid selection.");
                continue;
            }
        }

        String confirmation = console.readLine("Confirm reverting to " + filename + "? (yes/no)\n>>> ");
        if (!(confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y"))) {
            return null;
        }

        try {
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);
            Game game = (Game) in.readObject();
            in.close();
            file.close();
            return game;
        } catch(Exception e) {
            return null;
        }
    }

    public static Game defineDefaultGame() {


        //==============================VERBS==============================

        Verb[] verbs = new Verb[]{

            new Verb(new String[]{"save"}, false, false, false, false, false),
            new Verb(new String[]{"load", "restore"}, false, false, false, false, false),
            new Verb(new String[]{"restart"}, false, false, false, false, false),

            new Verb(new String[]{"hello", "hi", "hey"}, false, false, false, false, false),
            new Verb(new String[]{"look", "l", "inspect", "examine", "search", "check"}, false, false, true, false, true),
            new Verb(new String[]{"inventory", "holding", "inv"}, false, false, false, false, false),

            new Verb(new String[]{"go", "move", "walk", "run"}, false, false, false, false, false),
            new Verb(new String[]{"north", "n"}, false, false, false, false, false),
            new Verb(new String[]{"south", "s"}, false, false, false, false, false),
            new Verb(new String[]{"east", "e"}, false, false, false, false, false),
            new Verb(new String[]{"northeast", "ne"}, false, false, false, false, false),
            new Verb(new String[]{"southeast", "se"}, false, false, false, false, false),
            new Verb(new String[]{"southwest", "sw"}, false, false, false, false, false),
            new Verb(new String[]{"northwest", "nw"}, false, false, false, false, false),
            new Verb(new String[]{"west", "w"}, false, false, false, false, false),
            new Verb(new String[]{"up", "u", "climb"}, false, false, false, false, false),
            new Verb(new String[]{"down", "d"}, false, false, false, false, false),
            new Verb(new String[]{"in", "enter"}, false, false, false, false, false),
            new Verb(new String[]{"out", "leave", "exit"}, false, false, false, false, false),
            
            new Verb(new String[]{"take", "pick", "pickup", "grab", "hold", "keep"}, true, false, true, false, true),
            new Verb(new String[]{"drop", "leave", "set", "put", "place", "store"}, true, false, true, true, true){{indirectIndicator = new String[]{"in", "inside", "into"};}},
            new Verb(new String[]{"hit", "kill", "attack", "cut", "fight", "destroy", "break"}, true, true, true, true, false){{indirectIndicator = new String[]{"with", "use", "using"};}},
            new Verb(new String[]{"open"}, true, false, true, false, true),
            new Verb(new String[]{"close", "shut"}, true, false, true, false, true),

            new Verb(new String[]{"chuck", "throw", "fling", "toss", "hurl"}, true, true, true, true, false){{indirectIndicator = new String[]{"at", "towards"};}},
            new Verb(new String[]{"smell", "sniff"}, true, false, true, false, true),
            new Verb(new String[]{"eat"}, true, false, true, false, true),
            new Verb(new String[]{"drink"}, true, false, true, false, true),
            new Verb(new String[]{"read"}, true, false, true, false, true),
            new Verb(new String[]{"jump", "hop", "skip"}, false, false, true, false, false),
            new Verb(new String[]{"kiss"}, true, false, true, false, true),
            new Verb(new String[]{"hug", "embrace"}, true, false, true, false, false),
            new Verb(new String[]{"listen", "hear"}, false, false, true, false, true),
            new Verb(new String[]{"sing", "serenade"}, false, false, false, false, false),
            new Verb(new String[]{"sleep", "nap"}, false, false, false, false, false),
            new Verb(new String[]{"sorry", "apologize", "apologise"}, false, false, true, false, true),
            new Verb(new String[]{"shout", "yell", "scream"}, false, false, false, false, false),
            new Verb(new String[]{"taste", "lick"}, true, false, true, false, true),
            new Verb(new String[]{"touch", "feel", "fondle", "grope"}, true, false, true, false, true),
            new Verb(new String[]{"lock"}, true, true, true, true, false){{indirectIndicator = new String[]{"with", "use", "using"};}},
            new Verb(new String[]{"unlock"}, true, true, true, true, false){{indirectIndicator = new String[]{"with", "use", "using"};}},
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
        
        Player player = new Player(kitchen, "You are such a player.", 10) {
            @Override
            public String getHit(Thing indirect) {
                if (!indirect.deadly) {
                    return "The " + indirect.names[0] + " isn't deadly enough to do any damage.";
                }
                System.out.println("Well, if you insist...\nYOU HAVE DIED");
                System.exit(0);
                return "";
            }

            @Override public String getSmelled() {return "It's not good.";}
            @Override public String getTouched() {return "Don't do that.";}
            @Override public String getRead() {return "Reading people was never your strong suit.";}
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

        Item cook = new Item(new String[]{"cook", "cooks"}, "They look busy.", 0) {
            {
                this.takeable = false;
                this.silent = true;
            }

            @Override
            public String getTouched() {
                return "The cook seems uncomfortable with this.";
            }
            @Override
            public String getTaken(Player player) {
                return "The cooks are happy right where they are.";
            }
        };
        // cook.takeable = false;
        // cook.silent = true;
        kitchen.addToContents(cook);

        // kitchen.addToContents(player);

        Thing[] items = new Thing[] {
            player,
            knife,
            spoon,
            bag,
            cook,
        };

        Parser parser = new Parser();

        Game game = new Game(verbs, items, rooms, parser, player);

        parser.game = game;

        player.game = game;
        return game;
    }
}