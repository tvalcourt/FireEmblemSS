package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class MainController {

    @FXML Tab characterTab, itemTab;

    /**
     * Temporary function when loading a new tab
     */
    public void loadTab(){
        if(characterTab.isSelected()){
            System.out.println("Selecting Characters");
        }
        else if(itemTab.isSelected()){
            System.out.println("Selecting Items");
        }
    }

    /**
     * Exits the program
     */
    public void closeProgram(){
        System.exit(0);
    }
}
