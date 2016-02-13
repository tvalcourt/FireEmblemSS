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
import javafx.util.Callback;
import util.IProxyImage;
import util.ProxyImage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller object for the character tab
 * Created by Gatrie on 2/13/2016.
 */
public class CharacterController implements Initializable{

    @FXML Label lblName; // The name of the character
    @FXML ImageView imagePortrait; // stores the portrait of the selected character

    private final int GRID_COL = 7; // number of rows/cols in growth/base grids
    @FXML GridPane gridBase, gridGrowths; // growth rate and base stats

    @FXML ComboBox comboPlayers, comboEnemies; // lists of available characters


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

    }
}
