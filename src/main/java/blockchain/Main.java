package blockchain;

public class Main {

	
	public static void main(String[] args) {
		
	
		BlockChain chaine = new BlockChain(10);
		chaine.setDifficulte(3);
		chaine.setNbBlockDiv(10); // Intervale de division de la recompence
		chaine.setPrint(true); 
		chaine.setBlockMax(300); //Si -1 boucle infini (-1 par defaut)
		//chaine.setRecompenceBnb(10);
		chaine.setRecompenceSatoBnb(5000000000L); // L a la fin pour long
		chaine.start();
	}
}


/*
// 1 fonction de convertion string transaction
// 1 fcontion locking decript
- appele inflation 
- faire variable globale pourcentage transaction
// 1 fonction de recherche dans la liste utxo
// (faire le change a soit meme)
- 



*/