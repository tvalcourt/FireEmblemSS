package ui;

import data.ImagePath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import main.FireEmblemDriver;
import org.sqlite.SQLiteJDBCLoader;
import util.IProxyImage;
import util.ListViewRenderer;
import util.ProxyImage;

import javax.xml.transform.Result;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
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
    @FXML GridPane gridSupports,gridHeaders; // support grid (lower) and headers

    // The Overhaul
    @FXML ProgressBar progressHP, progressStr, progressSkill, progressSpeed, progressLuck, progressDefense, progressResistance, progressMov, progressCon;
    @FXML Label lblHPMax, lblStrengthMax, lblSkillMax, lblSpeedMax, lblLuckMax, lblDefenseMax, lblResistanceMax, lblMovMax, lblConMax;
    @FXML Label lblHP, lblStrength, lblSkill, lblSpeed, lblLuck, lblDefense, lblResistance, lblMov, lblCon;
    @FXML VBox vboxBars;

    HashMap<String, String[]> characterInfo;

    // List of controls for base stats
    ProgressBar[] baseStatBars;
    Label[] maxValues, currentValues;

    @FXML ListView listCharacters;


    /**
     * Initialize components of the form to load an initial character (Ephraim)
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup listview with selection actions and custom cell rendering
        listCharacters.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListViewRenderer();
            }
        });
        listCharacters.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    loadCharacter(newValue);
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        });

        /** Load headers for support grid */
        String header = "Char ";
        Label[] entries = new Label[7];
        for(int i = 0; i < 7; i++){
            entries[i] = new Label(header + (i+1));
            entries[i].setStyle("-fx-font-weight: bold");

            gridHeaders.add(entries[i], i, 0);
            GridPane.setHalignment(entries[i], HPos.CENTER);
        }

        /**
         * Load data into progress bars
         */
        try {
            // Setup style sheets for Progress bars
            vboxBars.getStylesheets().add("/ui/stylesheets/progressBarStyles.css");

            /** Initialize lists of controls for base stats*/
            baseStatBars = new ProgressBar[]{progressHP, progressStr, progressSpeed, progressSkill, progressLuck, progressDefense, progressResistance, progressMov, progressCon};
            maxValues = new Label[]{lblHPMax, lblStrengthMax, lblSpeedMax, lblSkillMax, lblLuckMax, lblDefenseMax, lblResistanceMax, lblMovMax, lblConMax};
            currentValues = new Label[]{lblHP, lblStrength, lblSpeed, lblSkill, lblLuck, lblDefense, lblResistance, lblMov, lblCon};

            /** Load the default character, Ephraim */
            loadCharacter("Ephraim");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads the specified characters list of supports onto the interface from the database
     * @param name The name of the character to load supports for
     */
    public void loadSupports(String name) throws SQLException{
        // Clear old entries
        gridSupports.getChildren().clear();

        Statement statement = db.createStatement();
        String sql = "SELECT name, option_1, option_2, option_3, option_4, option_5, option_6, option_7" +
                " FROM characters, supports WHERE characters.id == supports.char_id";

        ResultSet supportList = statement.executeQuery(sql);
        Label[] supports = new Label[7];
        while(supportList.next()){
            if(supportList.getString("name").equals(name)){
                for(int i = 0; i < 7; i++){
                    supports[i] = new Label(supportList.getString("option_" + (i+1)));
                    supports[i].setWrapText(true);
                    gridSupports.add(supports[i], i, 0);

                    GridPane.setValignment(supports[i], VPos.CENTER);
                    GridPane.setHalignment(supports[i], HPos.CENTER);
                }
            }
        }

        statement.close();
        supportList.close();
    }

    /**
     * Loads a progress bar value based on current result set
     * @param bar The bar to load data into
     * @param max The label corresponding to the maximum the current stat can be
     * @param base The label corresponding to the actual base value
     * @param stat The stat to load from the database
     * @param color The color to set the bar to
     * @param name The name of the character to load
     */
    public void loadBar(ProgressBar bar, Label max, Label base, String stat, String color, String name, ResultSet charStats) throws SQLException{
        // Loads info from the result set
        bar.setProgress((double)charStats.getInt(stat) / charStats.getInt("max_" + stat));
        bar.setStyle("-fx-accent: " + color);

        // Maximum Labels
        max.setText("" + charStats.getInt("max_" + stat));
        base.setText("" + charStats.getInt(stat));
        base.setTextFill(Color.DARKGREY);
    }

    /**
     * Loads the desired character data into the form using the database to access information
     * @param name The name of the character to load
     * @throws SQLException
     */
    public void loadCharacter(String name) throws SQLException {
        Statement stmnt = db.createStatement();
        String sql = "SELECT * FROM class_max_stats, char_base_stats, characters WHERE characters.id == char_base_stats.char_id " +
                "AND characters.start_class == class_max_stats.class_id";
        ResultSet charStats = stmnt.executeQuery(sql);
        ObservableList names = FXCollections.observableArrayList();

        /**
         * Iterates through the result set and loads all information onto the form; also secretly populates listview
         */
        while (charStats.next()) {
            names.add(charStats.getString("name" ));

            if (charStats.getString("name").equals(name)) {
                System.out.println("Matching Character: " + name);

                for (int i = 0; i < baseStatBars.length; i++) { // load progress bars for base stats
                    switch (i) {
                        case 0:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "hp", "pink", name, charStats);
                            break;
                        case 1:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "str", "red", name, charStats);
                            break;
                        case 2:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "skill", "cyan", name, charStats);
                            break;
                        case 3:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "speed", "yellow", name, charStats);
                            break;
                        case 4:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "luck", "grey", name, charStats);
                            break;
                        case 5:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "defense", "blue", name, charStats);
                            break;
                        case 6:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "resistance", "purple", name, charStats);
                            break;
                        case 7:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "mov", "brown", name, charStats);
                            break;
                        case 8:
                            loadBar(baseStatBars[i], maxValues[i], currentValues[i], "con", "black", name, charStats);
                            break;
                    }
                }


                IProxyImage image = new ProxyImage(ImagePath.PLAYER_PORTRAITS + charStats.getString("portrait_img"));
                imagePortrait.setImage(image.getImage(ImagePath.PLAYER_PORTRAITS + charStats.getString("portrait_img")));

                // Load Name
                lblName.setText(name); // name is always the second value
            }
        }

        // Load character names if first iteration
        if(listCharacters.getItems().isEmpty()){
            listCharacters.setItems(names);
        }

        stmnt.close();
        charStats.close();

        // Load support data with new query
        loadSupports(name);
    }
}
