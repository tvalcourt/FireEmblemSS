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
import javafx.scene.layout.VBox;
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
import java.util.List;
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

    // The Overhaul
    @FXML ProgressBar progressHP, progressStr, progressSkill, progressSpeed, progressLuck, progressDefense, progressResistance, progressMov, progressCon;
    @FXML Label lblHPMax, lblStrengthMax, lblSkillMax, lblSpeedMax, lblLuckMax, lblDefenseMax, lblResistanceMax, lblMovMax, lblConMax;
    @FXML Label lblHP, lblStrength, lblSkill, lblSpeed, lblLuck, lblDefense, lblResistance, lblMov, lblCon;
    @FXML VBox vboxBars;


    /**
     * Initialize components of the form to load an initial character (Ephraim)
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        /**
         * Load data into progress bars
         */
        try {
            Statement stmnt = db.createStatement();

            // Setup style sheets for Progressbars
            vboxBars.getStylesheets().add("/ui/stylesheets/progressBarStyles.css");

            ResultSet baseStats = stmnt.executeQuery("SELECT * FROM characters, classes, char_base_stats, class_max_stats WHERE characters.start_class == classes.id AND classes.id == class_max_stats.class_id " +
                    "AND characters.id == char_base_stats.char_id");
            ProgressBar[] baseStatBars = {progressHP, progressStr, progressSpeed, progressSkill, progressLuck, progressDefense, progressResistance, progressMov, progressCon};
            Label[] maxValues = {lblHPMax, lblStrengthMax, lblSpeedMax, lblSkillMax, lblLuckMax, lblDefenseMax, lblResistanceMax, lblMovMax, lblConMax};
            Label[] currentValues = {lblHP, lblStrength, lblSpeed, lblSkill, lblLuck, lblDefense, lblResistance, lblMov, lblCon};

            while(baseStats.next()){
                if(baseStats.getString("name").equals("Ephraim")){
                    for(int i = 0; i < baseStatBars.length; i++){ // load progress bars for base stats
                        switch(i) {
                            case 0:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i], "hp", "pink", baseStats);
                                break;
                            case 1:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i],"str", "red", baseStats);
                                break;
                            case 2:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i],"skill", "cyan", baseStats);
                                break;
                            case 3:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i],"speed", "yellow", baseStats);
                                break;
                            case 4:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i],"luck", "grey", baseStats);
                                break;
                            case 5:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i],"defense", "blue", baseStats);
                                break;
                            case 6:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i],"resistance", "purple", baseStats);
                                break;
                            case 7:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i], "mov", "brown", baseStats);
                                break;
                            case 8:
                                loadBar(baseStatBars[i], maxValues[i], currentValues[i], "con", "black", baseStats);
                                break;
                        }

                    }

                    // Load image
                    IProxyImage image = new ProxyImage(ImagePath.PLAYER_PORTRAITS + baseStats.getString("portrait_img"));
                    imagePortrait.setImage(image.getImage(ImagePath.PLAYER_PORTRAITS + baseStats.getString("portrait_img")));

                    // Load Name
                    lblName.setText(baseStats.getString("name"));
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

            ResultSet supports = stmnt.executeQuery("SELECT * FROM supports, characters WHERE " +
                    "characters.id == supports.char_id");
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
     * Loads a progress bar value based on current result set
     * @param bar The bar to load data into
     * @param max The label corresponding to the maximum the current stat can be
     * @param base The label corresponding to the actual base value
     * @param stat The stat to load from the database
     * @param color The color to set the bar to
     * @param query The result set containing the desired character data
     */
    public void loadBar(ProgressBar bar, Label max, Label base, String stat, String color, ResultSet query) throws SQLException{

        // Loads info from the result set
        bar.setProgress((double)query.getInt(stat) / query.getInt("max_" + stat));
        bar.setStyle("-fx-accent: " + color);

        // Maximum Labels
        max.setText("" + query.getInt("max_" + stat));
        base.setText("" + query.getInt(stat));
        base.setTextFill(Color.DARKGREY);
    }

}
