package tick1star;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	BufferedImage image;
	
	public ImagePanel(){
		super();
		setPreferredSize(new Dimension( 300, 300 ));
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (image != null) {
	         g.drawImage(image, 0, 0, null);
	      }
		}

	public void setImage(BufferedImage i){
		image = i;
	}
		
}

