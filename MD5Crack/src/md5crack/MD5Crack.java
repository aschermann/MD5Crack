package md5crack;

import helpers.CommonHelper;
import helpers.FileHelper;
import helpers.Reductor;
import java.io.DataInputStream;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 *
 * @author Lauri Kangassalo / lauri.kangassalo@helsinki.fi
 */
public class MD5Crack {

    private String charset;
    private int pwLength;
    private int chainsPerTable;
    private int chainLength;
    private String filename;

    public MD5Crack(String charset, int pwLength, int chainsPerTable, int chainLength, String filename) {
        this.charset = charset;
        this.pwLength = pwLength;
        this.chainsPerTable = chainsPerTable;
        this.chainLength = chainLength;
        this.filename = filename;
    }

    public boolean crackHash(String hashString) {
        FileHelper file = new FileHelper();
        CommonHelper helper = new CommonHelper();
        Reductor reductor = new Reductor(charset, pwLength);
        MessageDigest md = helper.getMD5digester();
        
        System.out.print("Reading table file... ");
        DataInputStream dis = file.openFile(filename);
        HashMap<byte[], byte[]> table = file.readTable(dis, pwLength);
        System.out.println("done.");
        System.out.println("Table size: " + table.size());

        byte[] hash = hashString.getBytes();

        for (int i = chainLength - 1; i >= 0; i--) {
            byte[] possibleEndpoint = hash;
            for (int j = i; j < chainLength - 1; j++) {
                possibleEndpoint = reductor.reduce(hash, j);
                possibleEndpoint = md.digest(possibleEndpoint);
            }
            possibleEndpoint = reductor.reduce(possibleEndpoint, chainLength - 1);
            if (table.containsKey(possibleEndpoint)) {
                System.out.println("Endpoint found.");
                return true;
            }
        }

        return false;
    }
}