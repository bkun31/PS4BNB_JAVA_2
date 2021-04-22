package blockchain;


import java.text.DateFormat;
import java.util.Date;
import java.util.Arrays;

public class Block {
	private String hashPrecedent;
	private Transactions[] listeTransaction;
	private int nbTransaction;
	private int Nonce;
	private String merkel;
	private String hashBlock;
	private Date date;
	private User user;
	private int numBlock;
	
	/**
	 * @param String hashPrecedent
	 * @param Transactions[] listeTransaction
	 * @param nbTransaction : nombre de transaction
	 * @param numBlock Numero du block dans la chain
	 * @param User mineur du block
	 */
	public Block(String hashPrecedent, Transactions[] listeTransaction, int nbTransaction, int numBlock, User user) {
		this.hashPrecedent = hashPrecedent;
		this.listeTransaction = listeTransaction;
		this.nbTransaction = nbTransaction;
		this.numBlock = numBlock;
		this.user = user;
		this.Nonce = 0;
		this.date = new Date();
		// faire le merkel des transaction
		this.merkel = merkel();
	}

	/**
	 * Hash le block en incrementant le nonce jusqu'a que le block commence par X fois "0"
	 * @param BLock a hash
	 * @return String Hash du block
	 */
	public String hashBlock(int difficulte) {
		String dif = "";
		for (int i=0; i<difficulte; i++) {
			dif = dif +"0";
		}
		hashBlock = HashUtil.applySha256(hashPrecedent + merkel + dateToString(date) + Nonce); //Concatenation des info + hash
		while (!hashBlock.substring(0, difficulte).equals(dif)) { //Verifie que les 4 premier caracter du hash soit "0" x difficulte
			Nonce++;
			hashBlock = HashUtil.applySha256(hashPrecedent + merkel + dateToString(date) + Nonce + numBlock + stringListeTx() + user.getNom());
		}
		return hashBlock;
	}
	
	/**
	 * lance merkel hash avec les info du block
	 * @see merkelhash()
	 * @return String merkel
	 */
	public String merkel() {
		String tab[] = new String[3];
		String in = "";
		String out = "";
		for (int i = 0; i<nbTransaction; i++){
			for (int j=0; j<listeTransaction[i].inList.size(); j++){
				in = in + listeTransaction[i].inList.get(j).toString();
			}
			for (int j=0; j<listeTransaction[i].outList.size(); j++){
				out = out + listeTransaction[i].outList.get(j).toString();
			}
			tab[i]= dateToString(listeTransaction[i].timestamp) + listeTransaction[i].inCount + in + out;
			in = "";
			out = "";
		}
		if (nbTransaction == 1){
			return HashUtil.applySha256(tab[0]);
		}
		return merkelHash(tab, nbTransaction);
	}
	
	/**
	 * @return String merkel
	 */
	public String merkelHash(String tabTx[], int nbTx) {
		int i=0;
		int nbTxHash=0;
		String tab[] = Arrays.copyOf(tabTx, nbTx); //crée une copie de tabTx[] de longeur nbTx
		String [] tabHash = tab;
		if (nbTx == 1) { //condition de fin de recursion 
			return tab[0];
		}
		while (i<nbTx-1) {
			tabHash[nbTxHash]= HashUtil.applySha256(tab[i] + tab[i+1]); //concatene et hash 2 transaction 
			nbTxHash++;
			i+=2;
		}
		if (nbTx % 2 != 0) {
			tabHash[nbTxHash]= HashUtil.applySha256(tab[i] + tab[i]); // Si nbTx impaire concatene et hash 2 fois la derniere transaction
			nbTxHash++;
		}
		return merkelHash(tabHash, nbTxHash); //appel récursif de merkelhash()
	}
	
	/**
	 * @return String concatenation de toute les transaction
	 */
	public String stringListeTx() {
		String liste = "";
		for (int i=0; i<nbTransaction;i++) {
			liste = liste + listeTransaction[i] + "\n";
		}
		return liste;
	}
	
	/**
	 * Print toutes les info du block
	 */
	public void printBlock() {
		System.out.println("Block numero (Index) : " + numBlock);
		System.out.println("Previous hash : " + hashPrecedent);
		System.out.println("Current hash : " + hashBlock);
		System.out.println("Timestamp : " + dateToString(date));
		System.out.println("Nb transaction : " + nbTransaction);
		System.out.println("\nListe transaction {");
		for (int i = 0; i<nbTransaction; i++)
		{
			listeTransaction[i].txDump();
		}
		System.out.println("\nMerkel tree root : " + merkel);
		System.out.println("Miner : " + user.getNom());
		System.out.println("Nonce : " + Nonce);
	}
	
	/**
	 * @return true si le merkel est valide
	 */
	public boolean verifMerkel() {
		//refait le merkel des transactions et verfie si il corespond au merkel du block
		return true;
	}
	
	/**
	 * @return the nonce
	 */
	public int getNonce() {
		return Nonce;
	}
	
	/**
	 * @return the hashBlock
	 */
	public String getHashBlock() {
		return hashBlock;
	}

	/**
	 * @return the hashPrecedent
	 */
	public String getHashPrecedent() {
		return hashPrecedent;
	}

	/**
	 * @return the listeTransaction
	 */
	public Transactions[] getListeTransaction() {
		return listeTransaction;
	}

	/**
	 * @return the nbTransaction
	 */
	public int getNbTransaction() {
		return nbTransaction;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
	    return date;
	  }
	
	/**
	 * @return the date
	 */
	public static String dateToString(Date date) {
		DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM);
	    return shortDateFormat.format(date);
	  }

	/**
	 * @return the merkel
	 */
	public String getMerkel() {
		return merkel;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
}	
	
	

