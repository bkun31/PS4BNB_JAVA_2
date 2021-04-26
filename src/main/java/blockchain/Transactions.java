package blockchain;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Bastien Canto bastien.canto@univ-tlse3.fr
 * @author Omar Kired omar.kired@univ-tlse3.fr
 * @author Bilel Besseghieur bilel.besseghieur@univ-tlse3.fr
 */
public class Transactions {

    public Date timestamp;
    public int inCount;
    public ArrayList<TxInputs> inList = new ArrayList<>();
    public int outCount;
    public ArrayList<TxOutputs> outList = new ArrayList<>();
    public String comment;

    public Transactions() {
        this.timestamp = new Date();
        this.comment = "";
    }

    public Transactions(String comment) {
        this.timestamp = new Date();
        this.comment = comment;
    }

    /**
     * Affiche sur la sortie standard le contenu de la transaction.
     */
    public void txDump() {
        System.out.println("Transaction date : " + Block.dateToString(timestamp) + "\nNumber of transactions inputs : "
                + this.inCount + "\nTransactions inputs list :");
        for (TxInputs txinputs : this.inList) {
            System.out.println(txinputs.toString());
        }
        System.out.println("Number of transactions outputs : " + this.outCount + "\nTransactions outputs list :");
        for (TxOutputs txoutputs : this.outList)
        {
            System.out.println(txoutputs.toString());
        }
        System.out.println(comment);
    }
}
