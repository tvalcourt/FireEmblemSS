package ui;

import data.ImagePath;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

        loadStats("Ephraim"); // base stats, growth rate, supports
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
                if(growthRate.getInt("char_id") == baseStats.getInt("char_id")){
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
}
