package imzjm.league.ui;

import java.awt.*;

public class ImageView extends Component {

    private Image image;

    public ImageView(Image image){
        this.image = image;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.drawRect(0, 0, getWidth()-1, getHeight()-1);

    }

    public void setImage(Image image) {
        this.image = image;
    }
}
