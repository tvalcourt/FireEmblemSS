package util;

import data.ImagePath;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

/**
 * Renders each weapon cell to have an elemental icon if this is an elemental/status type weapon
 * Created by Trevor on 12/22/2015.
 */
public class ListViewRenderer extends ListCell<String> {

    private ImageView imageView = new ImageView();

    @Override
    public void updateItem(String character, boolean empty){
        super.updateItem(character, empty);

        if (empty) {
            setGraphic(null);
            setText("");
        } else {
            String formattedName = character.toLowerCase();

            IProxyImage image = new ProxyImage(ImagePath.CHARACTER_ICONS + "icon_" + formattedName + ".png");
            imageView.setImage(image.getImage(ImagePath.CHARACTER_ICONS + "icon_" + formattedName + ".png"));

            setText(character);
            setGraphic(imageView);
        }
    }

}