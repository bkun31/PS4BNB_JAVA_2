package blockchain;
/**
 * @author Bastien Canto bastien.canto@univ-tlse3.fr
 * @author Omar Kired omar.kired@univ-tlse3.fr
 * @author Bilel Besseghieur bilel.besseghieur@univ-tlse3.fr
 */
public class Main {

	
	public static void main(String[] args) {
		
	
		BlockChain chaine = new BlockChain(10);
		chaine.setDifficulte(3);
		chaine.setNbBlockDiv(10); // Intervale de division de la recompence
		chaine.setPrint(false); 
		chaine.setBlockMax(1000); //Si -1 boucle infini (-1 par defaut)
		chaine.setRecompenceBnb(10);
		chaine.setRecompenceSatoBnb(5000000000L); // L a la fin pour long
		chaine.start();
		BCJsonUtils.BCJsonWriter(chaine, "blockchain.json");
		BlockChain test = BCJsonUtils.BCJsonReader("blockchain.json");
		for (Block blk : test.getChaine())
		{
			blk.printBlock();
		}
	}
}


