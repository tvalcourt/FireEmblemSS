package util;

import javafx.scene.image.Image;

/**
 * Reduces the memory footprint of any image to load
 * Created by Gatrie on 12/15/2015.
 */
public class ProxyImage implements IProxyImage{

    private RealImage realImage;
    private String fileName;

    public ProxyImage(String fileName){
        this.fileName = fileName;
    }

    /**
     * Loads the image into memory for the first time if not done, else simply retrieves the data since it has already
     * been loaded in before
     * @param fileName The name of the image to load
     * @return The actual image data for displaying
     */
    @Override
    public Image getImage(String fileName){
        if(realImage == null){
            realImage = new RealImage(fileName);
        }
        return realImage.getImage(fileName);
    }
}
