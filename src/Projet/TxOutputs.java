package Projet;

import java.util.Arrays;

/**
 * @author Bastien Canto bastien.canto@univ-tlse3.fr
 * @author Omar Kired omar.kired@univ-tlse3.fr
 * @author Bilel Besseghieur bilel.besseghieur@univ-tlse3.fr
 */
public class TxOutputs {
    
    public String[] locking; /* script */
    public long amount; /* montant de l´UTXO */
    
    
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

    /**
     * Calcule le hash de l´utxo en fonction de son script de verouillage et son montant.
     * 
     * @return Le hash de l´utxo.
     */
    public String hash(){
        String lock = "";
        for (String str : locking)
        {
            lock = lock + str;
        }
        return HashUtil.applySha256(lock + amount);
    }
}
