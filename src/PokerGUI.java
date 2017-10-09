package gui_programs;

//Karamel Quitayen
//G Period Java
//Program 28: Poker GUI
//=====================================
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Vector;

public class PokerGUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final Color feltGreen = new Color(0,82,0);
	
	private Timer timer = new Timer(350, new TimerListener());
	private Timer timer2 = new Timer(350, new TimerListener2());
	
	private int amtPlayers, numSelected, currPlayer, cntr;
	private int[][] rank; //0 = handtype, 1 = val of hand
	private boolean[] cardSelected = new boolean[5];
	
	private Dealer dealer;
	private Vector<Player> table;
	private String[] name;
	
	//panels
	private pokerJLabel error;
	private JPanel mainPanel = new JPanel();
	private JPanel titlePanel = new JPanel();
	private JPanel formPanel = new JPanel();
	private JPanel btnPanel = new JPanel();
	
	//buttons
	private pokerJButton[] cardBtn = new pokerJButton[5];
	private pokerJButton go = new pokerJButton("Go");
	private pokerJButton deal = new pokerJButton("Deal");
	private pokerJButton hold = new pokerJButton("Hold");
	private pokerJButton play = new pokerJButton("Play");
	private pokerJButton start = new pokerJButton("Start");
	private pokerJButton endTurn = new pokerJButton("End Turn");
	private pokerJButton done = new pokerJButton("Game Over");

	private JComboBox<String> numPlayers;
	private JPanel[] form = new JPanel[3];
	private JTextField[] names = new JTextField[3];
	
	public static void main(String[] args){
		new PokerGUI();
	}
	
	PokerGUI()
	{
		//set up frame
		setTitle("5-Card Poker");
		setLocation(200,100);
		setBackground(feltGreen);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//set up panels
		formPanel.setSize(500,150);
		formPanel.setLocation(0,100);
		formPanel.setBackground(feltGreen);
		
		btnPanel.setSize(500,70);
		btnPanel.setBackground(feltGreen);
		
		//set up global objects
		error = new pokerJLabel("Each player must have name.", 16, true);
		for (int i = 0; i < 3; i++)
		{
			names[i] = new JTextField("", 15);
			names[i].setFont(new Font("Consolas", Font.PLAIN, 18));
			form[i] = new JPanel();
			form[i].setBackground(feltGreen);
			form[i].add(new pokerJLabel("Player #" + (i+1) + ":", 18, false));
			form[i].add(names[i]);
		}
		
		//set up buttons
		endTurn.setPreferredSize(new Dimension(150,50));
		done.setPreferredSize(new Dimension(175,50));
		endTurn.addActionListener(this);
		deal.addActionListener(this);
		hold.addActionListener(this);
		play.addActionListener(this);
		start.addActionListener(this);
		go.addActionListener(this);
		done.addActionListener(this);
		
		getContentPane().setLayout(null);
		setVisible(true);
		setPlayers();
	}
	
	public void startGame()
	{
		table = new Vector<Player>();
		rank = new int[amtPlayers + 1][2];
		name = new String[amtPlayers+1];
		
		//add players to table and deals each a hand
		dealer = new Dealer();
		dealer.addHand(dealHand());
		name[0] = dealer.getName();
		table.addElement(dealer);
		for (int i = 1; i < amtPlayers+1; i++)
		{
			Player p = new Player(names[i-1].getText());
			p.addHand(dealHand());
			name[i] = p.getName();
			table.addElement(p);
		}
		
		currPlayer = 1;
		turn();
	}

	public void actionPerformed (ActionEvent e)
	{ //get button clicks
		if (e.getSource() == play)
		{
			setVisible(false);
			new PokerGUI();
		}
		else if (e.getSource() == start)
			checkNames();
		else if (e.getSource() == go)
			revealCards();
		else if (e.getSource() == deal)
			newHand();
		else if (e.getSource() == hold)
			finishTurn();
		else if (e.getSource() == endTurn)
		{
			currPlayer++;
			if (currPlayer > amtPlayers)
				dealerTurn();
			else
				turn();
		}
		else if (e.getSource() == done)
			gameOver();
	}
	
	public void cardChosen(ActionEvent e)
	{
		for (int i = 0; i < 5; i++)
			if(e.getSource() == cardBtn[i])
			{
				//if not already selected, select it
				if (!cardBtn[i].isSelected())
				{
					deal.setEnabled(true);
					hold.setEnabled(false);
					cardBtn[i].selectCard();
					numSelected++;
					if (numSelected == 3)
						for (int j = 0; j < 5; j++)
							if (!cardBtn[j].isSelected())
								cardBtn[j].setEnabled(false);
				}
				else //if already selected, deselect it
				{
					cardBtn[i].deselectCard();
					numSelected--;
					if (numSelected < 3)
					{
						for (int j = 0; j < 5; j++)
							cardBtn[j].setEnabled(true);
					}
					if (numSelected == 0)
					{
						deal.setEnabled(false);
						hold.setEnabled(true);
					}
				}
			}
	}
	
	public void setPlayers() //set up title screen with players
	{
		for (int i = 0; i < 3; i++)
			names[i].setText("");
		amtPlayers = 1;
		setSize(500,340);
		getContentPane().removeAll();
		titlePanel.removeAll();
		mainPanel.removeAll();
	
		titlePanel.setLocation(0,0);
		titlePanel.setSize(500,50);
		titlePanel.setBackground(feltGreen);
		titlePanel.add(new pokerJLabel("Five-Card Poker", 25, true));
		
		mainPanel.setSize(500,50);
		mainPanel.setLocation(0,50);
		mainPanel.setBackground(feltGreen);
		mainPanel.add(new pokerJLabel("Amount of Players:", 20, false));
		
		formPanel.removeAll();
		formPanel.add(form[0]);
		
		btnPanel.setLocation(0,250);
		btnPanel.removeAll();
		btnPanel.add(start);
		
		String html = "<html><font face='Consolas' size='5'>";
		String[] amt = {html+"One", html+"Two", html+"Three"};
		numPlayers = new JComboBox<String>(amt);
		numPlayers.setSelectedIndex(0);
		numPlayers.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				editPlayerPanel(numPlayers.getSelectedIndex());
			}
		});
		
		mainPanel.add(numPlayers);
		getContentPane().add(titlePanel);
		getContentPane().add(mainPanel);
		getContentPane().add(formPanel);
		getContentPane().add(btnPanel);
		refresh();
	}
	
	public void editPlayerPanel(int i)
	{
		//removes/add name forms to panel based on # players chosen in cmbobox
		getContentPane().remove(formPanel);
		formPanel.remove(error);
		if (i+1 < amtPlayers)
			formPanel.removeAll();
		
		amtPlayers = i + 1;
		formPanel.add(form[0]);
		if (i >= 1)
		{
			formPanel.add(form[1]);
			if (i == 2)
				formPanel.add(form[2]);
		}
		getContentPane().add(formPanel);
		refresh();
	}
	
	public void checkNames()
	{
		//make sure each player has a name
		boolean good = true;
		if (names[0].getText().compareTo("") == 0)
			good = false;
		else if (amtPlayers > 1)
			if ((names[1].getText().compareTo("") == 0) || (amtPlayers == 3 && names[2].getText().compareTo("") == 0))
				good = false;
		
		if (good)
			startGame();
		else
		{
			getContentPane().remove(formPanel);
			formPanel.add(error);
			getContentPane().add(formPanel);
			refresh();
		}
	}

	public void turn()
	{
		setSize(500,290);
		numSelected = 0;
		getContentPane().removeAll();
		titlePanel.removeAll(); //change title to players name
		titlePanel.add(new pokerJLabel(getName(currPlayer) + "'s Turn", 25, true));
		
		mainPanel.removeAll();
		mainPanel.setSize(500,150);
		for (int j = 0; j < 5; j++) //create new card buttons with images
		{
			cardBtn[j] = new pokerJButton(getCard(currPlayer,j));
			cardBtn[j].hideCard();
			cardBtn[j].setEnabled(false);
			if (currPlayer != 0)//not AI
			{
				cardBtn[j].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						cardChosen(e);
					}});
			}
			mainPanel.add(cardBtn[j]);
		}
		
		btnPanel.removeAll();
		btnPanel.setLocation(0,200);
		if (currPlayer != 0)
			btnPanel.add(go);
		
		getContentPane().add(titlePanel);
		getContentPane().add(mainPanel);
		getContentPane().add(btnPanel);
		refresh();
	}
	 
	public void revealCards() //shows cards
	{
		getContentPane().removeAll();
		getContentPane().add(titlePanel);
		
		for (int i = 0; i < 5; i++)
		{
			cardBtn[i].showCard();
			cardBtn[i].setEnabled(true);
		}
		
		if (currPlayer != 0)
		{
			btnPanel.removeAll();
			btnPanel.add(deal);
			btnPanel.add(hold);
			deal.setEnabled(false);
			hold.setEnabled(true);
		}
		
		getContentPane().add(mainPanel);
		getContentPane().add(btnPanel);
		refresh();
	}
	
	public void newHand()
	{
		int i;
		if (currPlayer != 0)
		{
			for (i = 0; i < 5; i++)
				if (cardBtn[i].isSelected())
					cardSelected[i] = true;
				else
					cardSelected[i] = false;
		}
		
		//temporary hand to hold old cards
		Vector<Card> hand = new Vector<Card>();
		for (i = 0; i < 5; i++)
			hand.addElement(getCard(currPlayer, i));
		
		table.elementAt(currPlayer).getHand().removeAllElements();
		
		for (i = 0; i < 5; i++)
			if (cardSelected[i])
				table.elementAt(currPlayer).addCard(dealCard());
			else
				table.elementAt(currPlayer).addCard(hand.elementAt(i));
	
		table.elementAt(currPlayer).sortHand();
		finishTurn();
	}
	
	public void finishTurn()
	{
		//shows player's new hand after switch
		getContentPane().remove(mainPanel);
		getContentPane().remove(btnPanel);

		mainPanel.removeAll();
		for (int j = 0; j < 5; j++)
		{
			cardBtn[j] = new pokerJButton(getCard(currPlayer,j));
			cardBtn[j].showCard();
			cardBtn[j].deselectCard();
			mainPanel.add(cardBtn[j]);
		}
		
		evaluateHand(table.elementAt(currPlayer).getHand());
		mainPanel.add(new pokerJLabel(printType(rank[currPlayer][0],rank[currPlayer][1]),20, true));
		
		btnPanel.removeAll();
		if (currPlayer != 0)
			btnPanel.add(endTurn);
		else
			btnPanel.add(done);
		
		getContentPane().add(mainPanel);
		getContentPane().add(btnPanel);
		refresh();
	}
	
	public void dealerTurn()
	{
		currPlayer = 0;
		turn();
		revealCards();
		evaluateHand(dealer.getHand());
		
		//decide what to do
		if (rank[currPlayer][0] == 8)
			hasFour();
		else if (rank[currPlayer][0] == 4)
			hasThree();
		else if (rank[currPlayer][0] == 3)
			hasTwoPair();
		else if (rank[currPlayer][0] == 2)
			hasOnePair();
		else if (rank[currPlayer][0] == 1)
			tryFlush();
		
		cntr = 0;
		numSelected = 0;
		timer.start();
	}
	
	public void hasFour() //8
	{
		//either card1 or card5 is != to the other 4 cards
		if (dealer.getVal(0) == dealer.getVal(1))
			cardSelected[4] = true;
		else
			cardSelected[0] = true;
	}
	public void hasThree() //4
	{
		if(dealer.getVal(0)==dealer.getVal(1)) //cards 4&5 different from the rest
			cardSelected = new boolean[]{false,false,false,true,true};
		else if (dealer.getVal(3)==dealer.getVal(4)) //cards 1&2 different from the rest
			cardSelected = new boolean[]{true,true,false,false,false};
		else //cards 1&5 different from the rest
			cardSelected = new boolean[]{true,false,false,false,true};
	}
	public void hasTwoPair() //3
	{
		//either 0,2,4 different has no pair
		if (dealer.getVal(0)==dealer.getVal(1) && dealer.getVal(2)==dealer.getVal(3)) //card5 has no pair
			cardSelected[4] = true;
		else if (dealer.getVal(0)==dealer.getVal(1) && dealer.getVal(3)==dealer.getVal(4)) //card3 has no pair
			cardSelected[2] = true;
		else  //card1 has no pair
			cardSelected[0] = true;
	}
	public void hasOnePair() //2
	{
		if(dealer.getVal(0)==dealer.getVal(1)) //card1 & card2 pair
			cardSelected = new boolean[]{false,false,true,true,true};
		else if (dealer.getVal(1)==dealer.getVal(2)) //card2 & card3 pair
			cardSelected = new boolean[]{true,false,false,true,true};
		else if (dealer.getVal(2)==dealer.getVal(3)) //card3 & card4 pair
			cardSelected = new boolean[]{true,true,false,false,true};
		else //card2 & card3 pair
			cardSelected = new boolean[]{true,true,true,false,false};
	}
	public void hasHigh() //1
	{
		//gets rid of anything not K,Q,J, or Ace
		int count = 0;
		for (int i = 0; i < 5; i++)
		{
			//can only get rid of up to 3 cards
			if (count < 3)
			{
				if (dealer.getVal(i) == 1 || dealer.getVal(i) > 10)
					cardSelected[i] = false;
				else
				{
					cardSelected[i] = true;
					count++;
				}
			}
			else
				cardSelected[i] = false;
		}
	}
	public void tryFlush()
	{
		int h=0, d=0, s=0, c=0; //heart, diamond, spade, club
		//count amt of each suit in hand
		for (int i = 0; i < 5; i++)
		{
			switch (dealer.getSuit(i))
			{
				case 1: h++; break;
				case 2: d++; break;
				case 3: s++; break;
				case 4: c++; break;
			}
		}
		
		//if a suit count = 3, try for a flush
		int trySuit = 0;
		if (h > 2)
			trySuit = 1;
		else if (d > 2)
			trySuit = 2;
		else if (s > 2)
			trySuit = 3;
		else if (c > 2)
			trySuit = 4;
		
		if (trySuit != 0) //if card's suit not one of the four...
		{
			for (int i = 0; i < 5; i++)
			{
				if (dealer.getSuit(i) != trySuit)
					cardSelected[i] = true; //trade card
				else
					cardSelected[i] = false;
			}
		}
		else
			hasHigh();
	}
	
	//timer that "selects" the card
	private class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent Evt)
		{
			cardBtn[cntr].selectCard();
			timer2.start();
		}
	}
	//timer that "keeps" or "deselects" the card
	private class TimerListener2 implements ActionListener
	{
		public void actionPerformed(ActionEvent Evt)
		{
			//if not changing card, deselect
			if (!cardSelected[cntr])
				cardBtn[cntr].deselectCard();
			
			cntr++;
			timer2.stop();
			if (cntr == 5) //at the end of dealer's hand
			{
				timer.stop();
				newHand();
			}
		}
	}
	
	public void highScore()
	{
		//loop control and temp variable
		int i, j;
		int temp;
		String tempStr;
		
		//sort by rank
		for (i = 0; i < table.size(); i++) 
		{
			for (j = 1; j < table.size() - i; j++) 
			{
				if (rank[j - 1][0] < rank[j][0])
				{
					temp = rank[j - 1][0];
					rank[j-1][0] = rank[j][0];
					rank[j][0] = temp;
					temp = rank[j - 1][1];
					rank[j-1][1] = rank[j][1];
					rank[j][1] = temp;
					tempStr = name[j - 1];
					name[j-1] = name[j];
					name[j] = tempStr;
				}
				else if (rank[j - 1][0] == rank[j][0]) //sort by hand val
				{
					if (rank[j - 1][1] < rank[j][1])
					{
						temp = rank[j - 1][0];
						rank[j-1][0] = rank[j][0];
						rank[j][0] = temp;
						temp = rank[j - 1][1];
						rank[j-1][1] = rank[j][1];
						rank[j][1] = temp;
						tempStr = name[j - 1];
						name[j-1] = name[j];
						name[j] = tempStr;
					}
				}
			} 
		}
	}
	
	public void gameOver()
	{
		highScore();
		setSize(400,320);
		getContentPane().removeAll();
		titlePanel.removeAll();
		titlePanel.add(new pokerJLabel("RESULTS", 30, true));
		titlePanel.setLocation(0,0);
		titlePanel.setSize(400,50);
		
		mainPanel.removeAll();
		mainPanel.setSize(400,180);

		for (int i = 0 ; i < table.size(); i++)
		{
			JPanel n = new JPanel();
			n.setSize(500, 50);
			n.setBackground(feltGreen);
			if (i == 0)
				n.add(new pokerJLabel(getName(i) + ": " + printType(rank[i][0],rank[i][1]), 25, true));
			else
				n.add(new pokerJLabel(getName(i) + ": " + printType(rank[i][0],rank[i][1]), 20, true));
			mainPanel.add(n);
		}
		
		btnPanel.removeAll();
		btnPanel.add(play);
		btnPanel.setSize(400,70);
		btnPanel.setLocation(0,230);
		
		getContentPane().add(titlePanel);
		getContentPane().add(btnPanel);
		getContentPane().add(mainPanel);
		refresh();
	}
	
	//Code from old poker program
	public String printType(int type, int val)
	{
		String typeStr = "";
		switch (type)
		{
			case 1: typeStr = "High Card "; break;
			case 2: typeStr = "One Pair "; break;
			case 3: typeStr = "Two Pair "; break;
			case 4: typeStr = "Three of a Kind "; break;
			case 5: typeStr = "Straight "; break;
			case 6: typeStr = "Flush "; break;
			case 7: typeStr = "Full House "; break;
			case 8: typeStr = "Four of a Kind "; break;
			case 9: typeStr = "Straight Flush "; break;
			case 10: typeStr = "Royal Flush "; break;
		}
		if ((type == 10)||(type == 6)) //flush
		{
			switch (val)
			{
				case 1: typeStr += "Hearts"; break;
				case 2: typeStr += "Diamonds"; break;
				case 3: typeStr += "Spades"; break;
				case 4: typeStr += "Clubs"; break;
			}
		}
		else 
			typeStr += (val==14 ? "Ace" : val);
		return typeStr;
	}
	
	public void evaluateHand(Vector<Card> hand)
	{
		int i;
		int[] pairs = new int[2];
		int[] val = new int[5], suit = new int[5];
		for (i = 0; i < 5; i++) //put in array so easier to evaluate
		{
			val[i] = hand.elementAt(i).getVal();
			suit[i] = hand.elementAt(i).getSuit();
		}
		
		boolean flush = true, straight = true;
		for (i = 0; i < 4; i++)
			if (val[i] + 1 != val[i+1]) //doesnt ascend by 1 (already sorted)
			{
				straight = false;
				if (val[0] == 1 && val[1] == 10 && val[2] == 11 && val[3] == 12 && val[4] == 13)
					straight = true;
			}
		for (i = 0; i < 5; i++)
			if (suit[i] != suit[0]) //different suit
				flush = false;
		
		if (flush)
		{
			if (straight)
			{   //check royal flush
				if (val[0] == 1)
					rank[currPlayer][0] = 10;
				else//straight flush
					rank[currPlayer][0] = 9;
			}
			else //regular flush
				rank[currPlayer][0] = 6;
		}
		else if (straight)
			rank[currPlayer][0] = 5;
		else
		{ //8-four, 7-full, 6-flush, 5-straight, 4-three, 3-2pair, 2-1pair, 1-high
			//check for four of the same val
			if ((val[0]==val[3])||(val[1]==val[4]))
				rank[currPlayer][0] = 8;
			//check for full house
			else if (((val[0]==val[2])&&(val[3]==val[4]))||((val[0]==val[1])&&(val[2]==val[4])))
			{
				rank[currPlayer][0] = 7;
				pairs[0] = (val[0]==1 ? 14: val[0]);
				pairs[1] = (val[1]==1 ? 14: val[1]);
				
			}
			//check for triples
			else if ((val[0]==val[2])||(val[1]==val[3])||(val[2]==val[4]))
				rank[currPlayer][0] = 4;
			//check for pairs
			else if (val[0]==val[1])
			{
				if ((val[2]==val[3])||(val[3]==val[4]))
				{
					rank[currPlayer][0] = 3;
					pairs[0] = (val[0]==1 ? 14: val[0]);
					pairs[1] = (val[3]==1 ? 14: val[3]);
				}
				else
				{
					rank[currPlayer][0] = 2;
					rank[currPlayer][1] = (val[0] == 1 ? 14 : val[0]);  //greatest val or ace
				}
			}
			else if (val[1]==val[2])
			{
				if (val[3]==val[4])
				{
					rank[currPlayer][0] = 3;
					pairs[0] = (val[1]==1 ? 14: val[1]);
					pairs[1] = (val[3]==1 ? 14: val[3]);
				}
				else
				{
					rank[currPlayer][0] = 2;
					rank[currPlayer][1] = (val[1] == 1 ? 14 : val[1]);  //greatest val or ace
				}
			}
			else if ((val[2]==val[3])||(val[3]==val[4]))
			{
				rank[currPlayer][0] = 2;
				rank[currPlayer][1] = (val[3] == 1 ? 14 : val[3]);  //greatest val or ace
			}
			else //high card
				rank[currPlayer][0] = 1;
		}
		//get value of hand
		if (rank[currPlayer][0] == 10)     //royal flush
			rank[currPlayer][1] = suit[0]; //heart > diamond > spade > club
		else if (rank[currPlayer][0] == 9) //straight flush
			rank[currPlayer][1] = (val[0] == 1 ? 14 : val[4]);  //greatest val or ace
		else if (rank[currPlayer][0] == 8) //four
			rank[currPlayer][1] = val[2];
		else if (rank[currPlayer][0] == 7) //full house
			rank[currPlayer][1] = (val[0]>val[1] ? val[0]: val[1]);
		else if (rank[currPlayer][0] == 6) //flush
			rank[currPlayer][1] = suit[0]; //heart > diamond > spade > club
		else if (rank[currPlayer][0] == 5) //straight
			rank[currPlayer][1] = (val[0] == 1 ? 14 : val[4]);  //greatest val or ace
		else if (rank[currPlayer][0] == 4) //three
			rank[currPlayer][1] = val[2];  //val of three pair
		else if (rank[currPlayer][0] == 3) //two pair
			rank[currPlayer][1] = (pairs[0]>pairs[1] ? pairs[0]: pairs[1]);
		else if (rank[currPlayer][0] == 1) //high
			rank[currPlayer][1] = (val[0] == 1 ? 14 : val[4]);  //greatest val or ace
	}
	
	public void refresh()
	{
		getContentPane().repaint();
		getContentPane().validate();
	}
	public String getName(int i){
		return name[i];
	}
	public Card dealCard(){
		return dealer.dealCard();
	}
	public Vector<Card> dealHand()
	{
		Vector<Card> hand = new Vector<Card>();
		for (int j = 0; j < 5; j++)
			hand.addElement(dealer.dealCard());
		return hand;
	}
	public Card getCard(int playerIndex, int cardIndex){
		return table.elementAt(playerIndex).getHand().elementAt(cardIndex);
	}
}

class pokerJLabel extends JLabel
{
	private static final long serialVersionUID = 1L;
	pokerJLabel(String str, int fontSize, boolean bold)
	{
		setText("<html><font color = 'white'>" + str);
		if (bold)
			setFont(new Font("Consolas", Font.BOLD, fontSize));
		else
			setFont(new Font("Consolas", Font.PLAIN, fontSize));
	}
}

class pokerJButton extends JButton
{
	private static final long serialVersionUID = 1L;
	private Card card;
	private boolean selected;
	
	pokerJButton(Card c)
	{
		card = c;
		selected = false;
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		Image img = null;
		String path = card.getImage(1);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon image = new ImageIcon(img);
		setDisabledIcon(image);
	}
	public void showCard(){
		Image img = null;
		String path = card.getImage(1);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon image = new ImageIcon(img);
		setDisabledIcon(image);
		setIcon(image);
	}
	public void hideCard(){
		Image img = null;
		String path = card.getImage(2);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon image = new ImageIcon(img);
		setDisabledIcon(image);
		setIcon(image);
	}
	public void selectCard()
	{
		selected = true;
		Image img = null;
		String path = card.getImage(3);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon image = new ImageIcon(img);
		setIcon(image);
	}
	public void deselectCard()
	{
		selected = false;
		Image img = null;
		String path = card.getImage(1);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon icon = new ImageIcon(img);
		setIcon(icon);
	}
	public boolean isSelected(){
		return selected;
	}
	
	public Card getCard(){
		return card;
	}
	
	pokerJButton(String str)
	{
		setText(str);
		setPreferredSize(new Dimension(100,50));
		setFont(new Font("Consolas", Font.PLAIN, 24));
	}
}