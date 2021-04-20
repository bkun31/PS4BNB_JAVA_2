package blockchain;

import java.util.Arrays;

/**
 * @author Bastien Canto bastien.canto@univ-tlse3.fr
 * @author Omar Kired omar.kired@univ-tlse3.fr
 * @author Bilel Besseghieur bilel.besseghieur@univ-tlse3.fr
 */
public class TxOutputs {
    
    public String[] locking; /* script */
    public long amount; /* mopntant de l´UTXO */
    
    
    /**
     * @param locking Script de verrouillage.
     * @param amount Montant de l´UTXO.
     */
    public TxOutputs(String[] locking, long amount) {
        this.locking = locking;
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "locking script : " + Arrays.toString(locking) + "; amount : " + amount;
    }
}
