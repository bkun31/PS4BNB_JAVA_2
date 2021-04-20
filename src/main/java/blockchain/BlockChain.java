package blockchain;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;

public class BlockChain {
	//constante
	private static final int NB_TRANSACTION_MAX = 50;
	
	//attribut 
	private ArrayList<Block> chaine = new ArrayList<Block>();
	private int nbUser;
	private int numBlock;
	private Queue<String> queue = new LinkedList<>();
	private User listeUser [] = new User[100];
	
	//parametre par defaut
	private int blockMax = -1;
	private boolean print = false; //n'affiche pas les block a la fin
	private int difficulte = 4;
	private int nbBlockDiv = 10; //division par deux tout les 10 block
	private long recompence = 50000000000L; //50 bnb
	
	
	/**
	 * @param nbUser
	 */
	public BlockChain(int nbUser) {
		this.nbUser = nbUser;
		this.numBlock = 0;
		// Si plus de 100 User : set a 100
		if (nbUser > 100) { 
			System.out.println("Max User : 100");
			this.nbUser=100;
		}
		//Genere un tableau de User
		for (int i=0; i<nbUser; i++) {
			listeUser[i] = new User("User"+(i+1)); 
		}
	}

	/**
	 * @return Block genesis
	 */
	private Block createGenesis() {
		String [] tab = new String[1]; // Crée un tableau de 1 transaction
		tab[0] = "Coinbase envoie 50 Bnb à creator";
		User user = new User("Creator");
		Block genesis = new Block("0000000000000000000000000000000", tab, 1, 0, user);
		return genesis;
	}
	
	/**
	 * Crée le genesis, fait l'helicopere money, phase de marché et print les block 
	 */
	public void start() {
		System.out.println("Difficulty = " + difficulte);
		System.out.println("Initial reward in satoBnb = " + recompence);
		// --------------- Genesis ----------------
		
		System.out.println("----------------- Genesis -----------------");
		Block block = createGenesis(); //init le block genesis
		block.hashBlock(difficulte);  //hash le genesis
		chaine.add(block); //met le genesis en position 0 dans la blockchaine
		numBlock++;
		System.out.println("Block Mined!!! : " + block.getHashBlock() + " n° : " + numBlock + " Nonce = " + block.getNonce());
		
		// --------------- Helico ----------------
		
		System.out.println("----------------- Hélicoptère -----------------");
		Random random = new Random();
		//Helico pour tout les user
		for (int i = 0; i<nbUser; i++) {
			// Crée un tableau de 1 transaction
			String [] tab = new String[1]; 
			tab[0] = "Coinbase envoie 50 Bnb à " + listeUser[i].getNom();
			//creation et hash block
			block = new Block(chaine.get(numBlock-1).getHashBlock(), tab, 1, numBlock, listeUser[random.nextInt(nbUser)]);
			block.hashBlock(difficulte);
			chaine.add(block);
			numBlock++;
			System.out.println("Block Mined!!! : " + block.getHashBlock() + " n° : " + numBlock + " Nonce = " + block.getNonce());
		}
		
		// --------------- Inflation ----------------
		
		System.out.println("----------------- Marché -----------------");
		while(numBlock < blockMax || blockMax == -1) {
			//div recompence 
			if (recompence != 0) {
				if(numBlock % nbBlockDiv == 0) {
					System.out.println("Reward = " + recompence + " Money supply = " + recompence/2 + " (" + satoToBnb(recompence/2)+" Bnb)");
					recompence/=2; 
				}
			}
			//creation de rng transaction dans la queue
			for (int j=0; j<random.nextInt(NB_TRANSACTION_MAX-1)+1;j++) {
				queue.add(generateTransaction());
			}
			//minage
			 minage(listeUser[random.nextInt(nbUser)]); //Met un utilisateur aleatoire
		}
		// --------------- Print all block ----------------
		if (print) {
			System.out.println("----------------- Liste des block -----------------");
			for (int i=numBlock-1; i>=0 ;i--) {
				chaine.get(i).printBlock();
				System.out.println("-------------------");
			}
		}
	}
	
	/**
	 * Prend des transactions dans la queue, creer un block, mine le block, le met dans la blockChain
	 * @param User mineur du block
	 */
	public void minage(User user) {
		String tabTx[] = new String [NB_TRANSACTION_MAX];
		Random random = new Random();
		int nbTx = random.nextInt(NB_TRANSACTION_MAX-1)+1;
		int i;
		
		//Met dans tabTx les transaction de la queue
		for (i=0; i<nbTx;i++) {
			tabTx[i]=queue.poll(); 
			if (tabTx[i] == null) { //Si la queue est vide : reduire le nombre de transaction
				nbTx=i;
				break;
			}
		}
		//Si en phase d'inflation ajouter a recompence
		if (recompence != 0) {  
			tabTx[i] = "Coinbase envoie 50 Bnb à " + user.getNom();
			nbTx++;
		}
		//creation et hash block
		Block block = new Block(chaine.get(numBlock-1).getHashBlock(), tabTx, nbTx, numBlock, user);	
		block.hashBlock(difficulte);
		chaine.add(block);
		numBlock++;
		System.out.println("Block Mined!!! : " + block.getHashBlock() + " n° : " + numBlock + " Nonce = " + block.getNonce());
	}
	
	/**
	 * @return String transaction de deux user et transaction aleatoire
	 */
	public String generateTransaction() {
		Random random = new Random();
		String name1 = listeUser[random.nextInt(nbUser)].getNom();
		String name2 = listeUser[random.nextInt(nbUser)].getNom();
		int Bnb = random.nextInt(1000)+1; //genere un montant pour la transaction entre 1 et 1000
		
		while (name1 == name2) { //Verifie que les deux nom soit different 
			name2 = listeUser[random.nextInt(nbUser)].getNom();
		}
		return (name1 + " envoie " + Bnb +" Bnb à " + name2);
	}
	
	/**
	 * @return true si toute la chaine est valide
	 */
	public boolean verifChain() {
		//re-Hash tout les block de la chain et verifie si le hash tu block et le hash precedent correspondent 
		return true;
	}
	
	/**
	 * @param long Bnb
	 * @return long satoBnb
	 */
	public long bnbToSato(long bnb) {
		return bnb*100000000;
	}
	
	/**
	 * @param long satoBnb
	 * @return double Bnb
	 */
	public double satoToBnb(long sato) {
		return (double)sato/100000000;
	}

	/**
	 * @param print the print to set
	 */
	public void setPrint(boolean print) {
		this.print = print;
	}

	/**
	 * @param difficulte the difficulte to set
	 */
	public void setDifficulte(int difficulte) {
		if (difficulte > 32 || difficulte < 0) {
			System.out.println("Difficulté invalide");
		}
		else {
			this.difficulte = difficulte;
		}
	}

	/**
	 * @param nbBlockDiv the nbBlockDiv to set
	 */
	public void setNbBlockDiv(int nbBlockDiv) {
		if (nbBlockDiv < 0) {
			System.out.println("Intervale de division de la recompence invalide");
		}
		else {
			this.nbBlockDiv = nbBlockDiv;
		}
	}
	
	/**
	 * @param recompence the recompence to set
	 */
	public void setRecompenceBnb(int recompence) {
		if (recompence < 0) {
			System.out.println("recompence invalide");
		}
		else {
			this.recompence = bnbToSato(recompence);
		}
	}
	
	/**
	 * @param recompence the recompence to set
	 */
	public void setRecompenceSatoBnb(long recompence) {
		if (recompence < 0) {
			System.out.println("recompence invalide");
		}
		else {
			this.recompence = recompence;
		}
	}

	/**
	 * @param blockMax the blockMax to set
	 */
	public void setBlockMax(int blockMax) {
		if (blockMax < -1) {
			System.out.println("Limite de block invalide");
		}
		else {
			this.blockMax = blockMax;
		}
	}
	
}
