package blockchain;

import java.util.Arrays;

/**
 * @author Bastien Canto bastien.canto@univ-tlse3.fr
 * @author Omar Kired omar.kired@univ-tlse3.fr
 * @author Bilel Besseghieur bilel.besseghieur@univ-tlse3.fr
 */
public class TxInputs {

    public String hash; /* hash de l´UTXO */
    public int height; /* numéro du bloc contenant la Tx pour payer */
    public long amount; /* montant de l´UTXO */
    public int unlockSize; /* nombre d´items dans le unlocking script */
    public String[] unlochking; /* script */
    public String comment; /* commentaire de la transaction */

    /**
     * @param hash Hash de l´UTXO.
     * @param height Numéro du bloc contenant la Tx pour payer.
     * @param amount Montant de l´UTXO.
     * @param unlockSize Nombre d´items dans le unlocking script.
     * @param unlochking Script de deverrouillage.
     * @param comment Commentaire de la transaction.
     */
    public TxInputs(String hash, int height, long amount, int unlockSize, String[] unlochking, String comment) {
        this.hash = hash;
        this.height = height;
        this.amount = amount;
        this.unlockSize = unlockSize;
        this.unlochking = unlochking;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Hash : " + this.hash + "; height : " + this.height + "; unlock size : " + this.unlockSize
                + "; unlocking script : " + Arrays.toString(this.unlochking) + "; Comment : " + this.comment
                + "; amount : " + this.amount;
    }

}
