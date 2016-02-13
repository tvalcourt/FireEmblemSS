package util;

import java.io.File;
import javafx.scene.image.Image;


/**
 * Real Buffered Image to store image data without also rendering
 * Created by Gatrie on 12/15/2015.
 */
public class RealImage implements IProxyImage {

    private Image image;

    protected RealImage(String fileName){
        loadIn(fileName);
    }

    @Override
    public Image getImage(String path){
        return image;
    }

    /**
     * Loads the image byte data into memory and stores it within the class for future use.
     * @param fileName Relative path to the desired image
     */
    private void loadIn(String fileName){
        File imageFile = new File(fileName);
        image = new Image(imageFile.toURI().toString());
    }
}