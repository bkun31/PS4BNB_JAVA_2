package blockchain;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Bastien Canto bastien.canto@univ-tlse3.fr
 * @author Omar Kired omar.kired@univ-tlse3.fr
 * @author Bilel Besseghieur bilel.besseghieur@univ-tlse3.fr
 */

public class BlockChain {
	// constante
	private static final long MIN_TX = 100000000L;
	private static final long MAX_TX = 10000000000L;
	private static final int MAX_USER = 100;

	// attribut
	private ArrayList<Block> chaine;
	private int nbUser;
	private int numBlock;
	private Queue<String> queue;
	private LinkedList<TxOutputs> globalUtxoList;
	private User listeUser[] = new User[MAX_USER];

	// parametre par defaut
	private int blockMax = -1;
	private boolean print = false; // n'affiche pas les block a la fin
	private int difficulte = 4;
	private int nbBlockDiv = 10; // division par deux tout les 10 block
	private long recompence = bnbToSato(50); // 50 bnb
	private float fee = 0.05f;

	/**
	 * @param nbUser
	 */
	public BlockChain(int nbUser) {
		this.nbUser = nbUser;
		this.numBlock = 0;
		this.globalUtxoList = new LinkedList<>();
		this.chaine = new ArrayList<Block>();
		this.queue = new LinkedList<String>();
		// Si plus de MAX_USER User : set a MAX_USER
		if (nbUser > MAX_USER) {
			System.out.println("Max user depasser");
			this.nbUser = MAX_USER - 1;
		}
		// Genere un tableau de User
		for (int i = 0; i < this.nbUser; i++) {
			listeUser[i] = new User("User" + (i + 1));
		}
	}

	/**
	 * @return Block genesis
	 */
	private Block createGenesis() {
		String[] tab = new String[1]; // Crée un tableau de 1 transaction
		tab[0] = "Coinbase envoie 50 Bnb à creator";
		User user = new User("Creator");

		/* On crée la transaction coinbase pour Créator */
		Transactions tx = new Transactions("coinbase");
		TxOutputs utxo = new TxOutputs(
				new String[] { "tx sign " + user.getNom(), "pubKey " + user.getNom(), "DUP", "HASH" },
				this.bnbToSato(50));
		tx.outList.add(utxo);
		tx.inCount = 0;
		tx.outCount = 1;

		this.globalUtxoList.add(utxo); /* on ajoute l´utxo non dépensé dans la liste globale */

		Block genesis = new Block("0000000000000000000000000000000", new Transactions[] { tx }, 1, 0, user);
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
		Block block = createGenesis(); // init le block genesis
		block.hashBlock(difficulte); // hash le genesis
		chaine.add(block); // met le genesis en position 0 dans la blockchaine
		System.out.println("Block Mined!!! : " + block.getHashBlock() + " n° : " + block.getNumBlock() + " Nonce = " + block.getNonce());
		numBlock++;		

		// --------------- Helico ----------------

		System.out.println("----------------- Hélicoptère -----------------");
		Random random = new Random();
		// Helico pour tout les user
		for (int i = 0; i < nbUser && numBlock<blockMax; i++) {
			/* On crée une transaction coinbase */
			Transactions tx = new Transactions("coinbase");
			TxOutputs utxo = new TxOutputs(new String[] { "tx sign " + listeUser[i].getNom(),
					"pubKey " + listeUser[i].getNom(), "DUP", "HASH" }, this.bnbToSato(50));
			tx.outList.add(utxo);
			tx.inCount = 0;
			tx.outCount = 1;

			this.globalUtxoList.add(utxo); /* on ajoute l´utxo non dépensé dans la liste globale */
			// creation et hash block
			block = new Block(chaine.get(numBlock - 1).getHashBlock(), new Transactions[] { tx }, 1, numBlock,
					listeUser[random.nextInt(nbUser)]);
			block.hashBlock(difficulte);
			chaine.add(block);
			numBlock++;
			System.out.println(
					"Block Mined!!! : " + block.getHashBlock() + " n° : " + block.getNumBlock() + " Nonce = " + block.getNonce());
		}

		// --------------- Inflation ----------------

		System.out.println("----------------- Marché -----------------");
		while (numBlock < blockMax || blockMax == -1) {
			// div recompence
			if (recompence != 0) {
				if (numBlock % nbBlockDiv == 0) {
					System.out.println("Reward = " + recompence + " Money supply = " + recompence / 2 + " ("
							+ satoToBnb(recompence / 2) + " Bnb)");
					recompence /= 2;
				}
			}
			// creation de rng transaction dans la queue
			queue.add(generateTransaction());
			// minage
			minage(listeUser[random.nextInt(nbUser)]); // Met un utilisateur aleatoire
		}
		// --------------- Print all block ----------------
		if (print) {
			System.out.println("----------------- Liste des block -----------------");
			for (int i = numBlock - 1; i >= 0; i--) {
				chaine.get(i).printBlock();
				System.out.println("-------------------");
			}
		}
		// --------------- Verif Chain ----------------
		if (verifChain()) {
			System.out.println("OKKKKKKKKK");
		} else {
			System.out.println("KOOOOOOOOO");
		}
	}

	/**
	 * Prend des transactions dans la queue, creer un block, mine le block, le met
	 * dans la blockChain
	 * 
	 * @param User mineur du block
	 */
	public void minage(User user) {
		Transactions[] tabTx = new Transactions[3];
		ArrayList<TxInputs> utxoInputs;
		User[] tabUser;
		String tx;
		long amountTx;
		long amountFees;

		/*
		 * on prend une tx dans la file et on recherche les utxos de l´utilisateur
		 * source du montant de la tx + les frais éventuelle
		 */
		tx = queue.poll();
		tabUser = stringTxToUser(tx);
		amountTx = stringTxToLong(tx);
		amountFees = (long) (amountTx * fee);
		utxoInputs = searchUtxo(tabUser[0], amountTx + amountFees);

		/* si l´utilisateur source à des fonds suffisants, on creé la transaction */
		if (utxoInputs != null) {
			Transactions market = new Transactions("market");
			Transactions fees = new Transactions("fees");
			long change = amountUtxoList(utxoInputs) - (amountTx + amountFees);
			int nbTx = 2;

			/*
			 * on creé l´utxo correspondant au montant de la tx source pour l´utilisateur
			 * destination, on crée aussi un utxo de change si besoin
			 */
			market.inList = utxoInputs;
			market.inCount = utxoInputs.size();
			market.outList.add(new TxOutputs(
					new String[] { "tx sign " + tabUser[1].getNom(), "pubKey " + tabUser[1].getNom(), "DUP", "HASH" },
					amountTx));
			globalUtxoList.add(market.outList.get(0));
			if (change != 0) {
				market.outList.add(new TxOutputs(new String[] { "tx sign " + tabUser[0].getNom(),
						"pubKey " + tabUser[0].getNom(), "DUP", "HASH" }, change));
				globalUtxoList.add(market.outList.get(1));
			}
			market.outCount = market.outList.size();

			/* on crée un utxo de frais pour le mineur */
			fees.inList = utxoInputs;
			fees.inCount = utxoInputs.size();
			fees.outList.add(new TxOutputs(
					new String[] { "tx sign " + user.getNom(), "pubKey " + user.getNom(), "DUP", "HASH" }, amountFees));
			globalUtxoList.add(fees.outList.get(0));
			fees.outCount = 1;

			/*
			 * on ajoute dans la liste transactions du bloc,les tx market, fees et
			 * récompense si phase d´inflation (plus bas)
			 */
			tabTx[0] = market;
			tabTx[1] = fees;

			// Si en phase d'inflation on crée la tx coinbase
			if (recompence != 0) {
				Transactions coinbase = new Transactions("coinbase");
				coinbase.inCount = 0;
				coinbase.outList.add(new TxOutputs(
						new String[] { "tx sign " + user.getNom(), "pubKey " + user.getNom(), "DUP", "HASH" },
						recompence));
				coinbase.outCount = 1;
				globalUtxoList.add(coinbase.outList.get(0));
				coinbase.outCount = 1;
				tabTx[2] = coinbase;
				nbTx++;
			}

			// creation et hash block
			Block block = new Block(chaine.get(numBlock - 1).getHashBlock(), tabTx, nbTx, numBlock, user);
			block.hashBlock(difficulte);
			chaine.add(block);
			numBlock++;
			System.out.println(
					"Block Mined!!! : " + block.getHashBlock() + " n° : " + block.getNumBlock() + " Nonce = " + block.getNonce());
		}
	}

	/**
	 * 
	 * @param txiList liste de txinputs
	 * @return le montant cumule en satobnb de tous les txinputs
	 */
	private long amountUtxoList(ArrayList<TxInputs> txiList) {
		long amount = 0L;
		for (TxInputs utxo : txiList) {
			amount += utxo.amount;
		}
		return amount;
	}

	/**
	 * @return String transaction aléatoire entre 2 utilisateurs
	 */
	public String generateTransaction() {
		Random random = new Random();
		String name1 = listeUser[random.nextInt(nbUser)].getNom();
		String name2 = listeUser[random.nextInt(nbUser)].getNom();
		long satobnb = MIN_TX + (long) (Math.random() * (MAX_TX - MIN_TX)); // genere un montant pour la transaction
																			// entre MIN_TX et MAX_TX
		while (name1 == name2) { // Verifie que les deux nom soit different
			name2 = listeUser[random.nextInt(nbUser)].getNom();
		}
		return (name1 + " envoie " + satobnb + " Bnb à " + name2);
	}

	/**
	 * @return true si toute la chaine est valide
	 */
	public boolean verifChain() {
		for (int i = 0; i < numBlock; i++) {
			if (!chaine.get(i).getHashBlock().equals(chaine.get(i).hashBlockNonce())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param String transaction
	 * @return User[] tableau de User de la transaction en paramétre
	 */
	public User[] stringTxToUser(String tx) {
		User tab[] = new User[2];
		for (int i = 0; i < nbUser; i++) {
			if (tx.split(" ")[0].equals(listeUser[i].getNom())) {
				tab[0] = listeUser[i];
			} else if (tx.split(" ")[5].equals(listeUser[i].getNom())) {
				tab[1] = listeUser[i];
			}
		}
		return tab;
	}

	/**
	 * @param String transaction
	 * @return Long montant de la transaction en paramétre
	 */
	public long stringTxToLong(String tx) {
		return Long.parseLong(tx.split(" ")[2]);
	}

	/**
	 * @param String[] Lock Script
	 * @param String[] Unlock Script
	 * @return Boolean true si le script et valide
	 */
	public static boolean unlockScript(String[] lock, String[] unlock) {
		Stack<String> stack = new Stack<String>();
		// concatenation lock+unlock
		String tab[] = new String[lock.length + unlock.length];
		for (int i = 0; i < lock.length; i++) {
			tab[i] = lock[i];
		}
		for (int i = 0; i < unlock.length; i++) {
			tab[i + lock.length] = unlock[i];
		}
		boolean bool = false;

		// Lecture tableau
		for (int i = 0; i < (tab.length); i++) {

			if (tab[i].equals("DUP")) {
				stack.add(stack.peek());
			} else if (tab[i].equals("HASH")) {
				stack.add(HashUtil.applySha256(stack.pop()));
			} else if (tab[i].equals("EQ")) {
				if (stack.pop().equals(stack.pop())) {
					bool = true;
				} else {
					return false;
				}
			} else if (tab[i].equals("VER")) {
				if (stack.pop().split(" ")[1].equals(stack.pop().split(" ")[2])) {
					bool = true;
				} else {
					return false;
				}
			} else {
				stack.add(tab[i]);
			}
		}
		return bool;
	}

	/**
	 * Renvoie la liste des UTXO (txOutputs) du user ou la somme est superieur au
	 * montant en paramétre et les suprime de la global liste
	 * 
	 * @param User
	 * @param Long montant
	 * @return ArrayListe<TxInputs> d'UTXO (Txoutputs)
	 */
	private ArrayList<TxInputs> searchUtxo(User user, long montant) {
		ArrayList<TxInputs> liste = new ArrayList<TxInputs>();
		int i = 0;
		long solde = 0;
		// Cherche les Txoutputs dans la GlobalList et les ajoute dans une liste
		while (i < globalUtxoList.size() && solde < montant) {
			if (globalUtxoList.get(i).locking[1].substring(7).equals(user.getNom())) {
				TxOutputs utxo = globalUtxoList.get(i);
				TxInputs txIn = new TxInputs(utxo.hash(), 0, utxo.amount, 3,
						new String[] { HashUtil.applySha256("PubK " + user.getNom()), "EQ", "VER" }, "market");
				liste.add(txIn);
				solde += globalUtxoList.get(i).amount;
			}
			i++;
		}
		// Si le user n'a pas assez d'UTXO return null
		if (montant > solde) {
			return null;
		}
		// Supprime les UTXO de la GlobalList
		for (i = 0; i < liste.size(); i++) {
			globalUtxoList.remove(liste.get(i));
		}
		return liste;
	}

	/**
	 * @param Long Bnb
	 * @return Long satoBnb
	 */
	public long bnbToSato(long bnb) {
		return bnb * 100000000;
	}

	/**
	 * @param Long satoBnb
	 * @return double Bnb
	 */
	public double satoToBnb(long sato) {
		return (double) sato / 100000000;
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
		} else {
			this.difficulte = difficulte;
		}
	}

	/**
	 * @param nbBlockDiv the nbBlockDiv to set
	 */
	public void setNbBlockDiv(int nbBlockDiv) {
		if (nbBlockDiv < 0) {
			System.out.println("Intervale de division de la recompence invalide");
		} else {
			this.nbBlockDiv = nbBlockDiv;
		}
	}

	/**
	 * @param recompence the recompence to set
	 */
	public void setRecompenceBnb(int recompence) {
		if (recompence < 0) {
			System.out.println("recompence invalide");
		} else {
			this.recompence = bnbToSato(recompence);
		}
	}

	/**
	 * @param recompence the recompence to set
	 */
	public void setRecompenceSatoBnb(long recompence) {
		if (recompence < 0) {
			System.out.println("recompence invalide");
		} else {
			this.recompence = recompence;
		}
	}

	/**
	 * @param blockMax the blockMax to set
	 */
	public void setBlockMax(int blockMax) {
		if (blockMax < -1) {
			System.out.println("Limite de block invalide");
		} else {
			this.blockMax = blockMax;
		}
	}

	/**
	 * @param fee the fee to set
	 */
	public void setFee(float fee) {
		if (fee > 1 || fee < 0) {
			System.out.println("Frais invalide");
		} else {
			this.fee = fee;
		}
	}

	/**
	 * @return the chaine
	 */
	public ArrayList<Block> getChaine() {
		return chaine;
	}
}
