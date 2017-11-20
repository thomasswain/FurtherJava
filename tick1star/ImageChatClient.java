package tick1star;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ImageChatClient extends JFrame {

	private String server;
	private int port;
	private FileImageInputStream iStream;
	private ImagePanel imagePanel;
	private InputStream input;

	
	public ImageChatClient(String s, int p){
		super(Strings.IMAGE_CHAT_CLIENT);
		server = s;
		port = p;
		setSize(640,480);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		JComponent receivePanel = createReceivePanel();
		receivePanel.setSize(560, 420);
		add(receivePanel, BorderLayout.NORTH);
		try{
			//"pic.jpg" acts as socket output stream.
			
			BufferedImage originalImage = ImageIO.read(new File("C:/Users/tswain/Documents/pic.jpg"));
			//Below stream represents the server sending an image at its socket output stream.
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", stream);
			stream.flush();
			
			//Client end.
			byte[] imageInByte = stream.toByteArray();
			stream.close();
			
			input = new ByteArrayInputStream(imageInByte);
			//BufferedImage bImageFromConvert = ImageIO.read(input);

			//ImageIO.write(bImageFromConvert, "jpg", new File("C:/Users/tswain/Documents/pic_new.jpg"));
					
		}
		catch(IOException ie){
			ie.printStackTrace();
		}
		
		
		
		
	}	
	
	
	
	
	private JComponent createReceivePanel(){
		Box receiveBox = Box.createVerticalBox();
		receiveBox.add(createImagePanel());
		JButton dnButton = new JButton(Strings.DISPLAY_NEXT);
		dnButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				displayNext();
			}
		});
		receiveBox.add(dnButton);
		return receiveBox;
		
	}
	
	private JComponent createImagePanel(){
		
		imagePanel = new ImagePanel();
		addBorder(imagePanel, Strings.RECEIVE);
		JScrollPane scroller = new JScrollPane(imagePanel, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return scroller;
		
	}
	
	private void displayNext(){
		try{
			BufferedImage bIm = ImageIO.read(input);
			imagePanel.setImage(bIm);
			repaint();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void addBorder(JComponent component, String title) {
		Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border tb = BorderFactory.createTitledBorder(etch,title);
		component.setBorder(tb);
		}
	
	public static void main(String[] args) {
		String s;
		int p;
		try{
			s = args[0];
			p = Integer.parseInt(args[1]);
		}
		catch(ArrayIndexOutOfBoundsException ae){
			System.out.println("Usage: java ImageChatClient <server> <port>");
			return;
		}
		catch(NumberFormatException e){
			System.out.println("Please specify <PORT> as an integer");
			return;
		}
		ImageChatClient iChatClient = new ImageChatClient(s,p);
		iChatClient.setVisible(true);
		
	}

}
