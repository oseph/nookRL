package nook.screens;

/**
 * Created by Josh on 2017-01-20.
 */

import nook.Creature;
import nook.Item;

public class ExamineScreen extends InventoryBasedScreen {

    public ExamineScreen(Creature player){
        super(player);
    }

    protected String getVerb(){
        return "look at";
    }

    public boolean isAcceptable(Item item){
        return true;
    }

    protected Screen use(Item item){
        String article = "aeiou".contains(item.name().subSequence(0,1)) ? "an " : "a ";
        player.notify("It's " + article + item.name());
        player.notify(item.description());
        return null;
    }


}
