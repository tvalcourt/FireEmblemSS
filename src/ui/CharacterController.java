package ui;

import data.ImagePath;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import main.FireEmblemDriver;
import util.IProxyImage;
import util.ProxyImage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Controller object for the character tab
 * Created by Gatrie on 2/13/2016.
 */
public class CharacterController implements Initializable{

    private Connection db = FireEmblemDriver.dbConnection;

    @FXML Label lblName; // The name of the character
    @FXML ImageView imagePortrait; // stores the portrait of the selected character
    private final int GRID_COL = 7; // number of rows/cols in growth/base grids
    @FXML GridPane gridBase, gridGrowths, gridSupports; // growth rate and base stats
    @FXML ComboBox comboPlayers, comboEnemies; // lists of available characters
    @FXML TextFlow flowGeneral; // general information in the middle of the tab


    /**
     * Initialize components of the form to load an initial character
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the image of the default character
        IProxyImage image = new ProxyImage(ImagePath.PLAYER_PORTRAITS + "portrait_ephraim.png");
        imagePortrait.setImage(image.getImage(ImagePath.PLAYER_PORTRAITS + "portrait_ephraim.png"));

        lblName.setText("Ephraim");

        // Create a list of table headers in parallel with a list of strings containing their text
        Label[] headerLabels = new Label[7];
        String[] headerText = {"HP", "Str/Mag", "Skill", "Speed", "Luck", "Def", "Res"};

        // Add headers into the list with horizontal alignment
        for(int i = 0; i < GRID_COL; i++){
            gridBase.add(headerLabels[i] = new Label(headerText[i]), i, 0);
            headerLabels[i].setStyle("-fx-font-weight: bold");
            GridPane.setHalignment(headerLabels[i], HPos.CENTER);

            gridGrowths.add(headerLabels[i] = new Label(headerText[i]), i, 0);
            headerLabels[i].setStyle("-fx-font-weight: bold");
            GridPane.setHalignment(headerLabels[i], HPos.CENTER);
        }

        // Headers for Supports
        Label[] headerSupports = new Label[7];
        String[] headerSupportsText = {"Char 1", "Char 2", "Char 3", "Char 4", "Char 5", "Char 6", "Char 7"};

        for(int i = 0; i < GRID_COL; i++){
            gridSupports.add(headerSupports[i] = new Label(headerSupportsText[i]), i, 0);
            headerSupports[i].setStyle("-fx-font-weight: bold");
            GridPane.setHalignment(headerSupports[i], HPos.CENTER);
        }

        // Populate combo boxes and set rendering to load characters items with their name
        ObservableList players = FXCollections.observableArrayList("Ephraim", "Eirika", "Franz", "Gilliam");
        comboPlayers.setItems(players);
        comboPlayers.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>(){

                    private ImageView imageView = new ImageView();
                    @Override
                    protected void updateItem(String name, boolean empty){
                        super.updateItem(name, empty);

                        if(name == null || empty){
                            setGraphic(null);
                        } else {
                            String formattedName = name.toLowerCase();

                            IProxyImage image = new ProxyImage(ImagePath.PLAYER_ICONS + "icon_" + formattedName + ".png");
                            imageView.setImage(image.getImage(ImagePath.PLAYER_ICONS + "icon_" + formattedName + ".png"));

                            setText(name);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        ObservableList enemies = FXCollections.observableArrayList("Valter", "Callaech", "Vigarde");
        comboEnemies.setItems(enemies);
        comboEnemies.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>(){

                    private ImageView imageView = new ImageView();
                    @Override
                    protected void updateItem(String name, boolean empty){
                        super.updateItem(name, empty);

                        if(name == null || empty){
                            setGraphic(null);
                        } else {
                            String formattedName = name.toLowerCase();

                            IProxyImage image = new ProxyImage(ImagePath.ENEMY_ICONS + "icon_" + formattedName + ".png");
                            imageView.setImage(image.getImage(ImagePath.ENEMY_ICONS + "icon_" + formattedName + ".png"));

                            setText(name);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        loadStats("Ephraim"); // base stats, growth rate
        loadSupports("Ephraim");
        loadGeneralInfo("Ephraim");
    }

    /**
     * Loads the base stats and the growth rate of the specified unit from the database
     * @param name The name of the unit to load data for
     */
    public void loadStats(String name){
        try {
            Label[] entries = new Label[7];
            String[] map = {"hp", "strength", "skill", "speed", "luck", "defense", "resistance"};
            Statement stmnt = db.createStatement();

            /** Base Stats */
            ResultSet baseStats = stmnt.executeQuery("SELECT * FROM characters_ss, base_stats, growth_rate WHERE " +
                    "characters_ss.id == base_stats.char_id");
            while(baseStats.next()){
                if(baseStats.getString("name").equals(name)) { // if name matches, load that character's data
                    for (int i = 0; i < GRID_COL; i++) {
                        gridBase.add(entries[i] = new Label(baseStats.getString(map[i])), i, 1);
                        GridPane.setHalignment(entries[i], HPos.CENTER);
                    }
                }
            }

            /** Growth Rate */
            ResultSet growthRate = stmnt.executeQuery("SELECT * FROM characters_ss, growth_rate WHERE " +
                    "characters_ss.id == growth_rate.char_id");
            while(growthRate.next()){
                if(growthRate.getString("name").equals(name)){
                    for(int i = 0; i < GRID_COL; i++){
                        gridGrowths.add(entries[i] = new Label(growthRate.getString(map[i])), i, 1);
                        GridPane.setHalignment(entries[i], HPos.CENTER);
                    }
                }
            }

            stmnt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the specified characters list of supports onto the interface from the database
     * @param name The name ofthe character to load supports for
     */
    public void loadSupports(String name){
        try {
            Statement stmnt = db.createStatement();
            String[] dbColumns = {"option_1", "option_2", "option_3", "option_4", "option_5", "option_6", "option_7"};
            Label[] entries = new Label[7];

            ResultSet supports = stmnt.executeQuery("SELECT * FROM supports, characters_ss WHERE " +
                    "characters_ss.id == supports.char_id");
            while(supports.next()){
                if(supports.getString("name").equals(name)){
                    for(int i = 0; i < GRID_COL; i++){
                            gridSupports.add(entries[i] = new Label(supports.getString(dbColumns[i])), i, 1);
                            entries[i].setWrapText(true);
                            GridPane.setHalignment(entries[i], HPos.RIGHT);
                    }
                }
            }

            stmnt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads starting weapons into the general information text flow
     * @param name The name of the character to load information for
     */
    public void loadGeneralInfo(String name){
        try{
            Statement stmnt = db.createStatement();
            ResultSet generalInfo = stmnt.executeQuery("SELECT * FROM characters_ss, weapons_start WHERE " +
                "characters_ss.id == weapons_start.char_id");

            while(generalInfo.next()){
                if(generalInfo.getString("name").equals(name)){
                    Text weaponRow = new Text();
                    Text startClass = new Text();

                    // Load all weapons and remove any null strings
                    weaponRow.setText("Weapon: " + generalInfo.getString("item_1") + "   " + generalInfo.getString("item_2") + "   " +
                            generalInfo.getString("item_3") + "   " + generalInfo.getString("item_4") + "\n");
                    weaponRow.setText(weaponRow.getText().replaceAll("null", ""));

                    // Load starting class information
                    startClass.setText("Starting Class: " + generalInfo.getString("start_class") + "\n");

                    flowGeneral.getChildren().clear();
                    flowGeneral.getChildren().addAll(weaponRow, startClass);
                }
            }

            stmnt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCharacter(){
        // Clear all previous entries


        String characterName = comboPlayers.getValue().toString();

        loadStats(characterName);
        loadSupports(characterName);
        loadGeneralInfo(characterName);
    }
}
