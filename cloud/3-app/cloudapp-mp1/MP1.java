import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.TreeMap;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
	    String[] ret = new String[20];

        class ValueComparator implements Comparator {
            Map map;

            public ValueComparator(Map map) {
                this.map = map; 
            }

            public int compare(Object keyA, Object keyB) {
                Comparable valueA = (Comparable) map.get(keyA);
                Comparable valueB = (Comparable) map.get(keyB);
                
                if(valueB.compareTo(valueA) == 0)
                    return ((String)keyA).compareTo((String)keyB);
                else
                    return valueB.compareTo(valueA);
            }
        }
       
	    try(BufferedReader br = new BufferedReader(new FileReader(new File(this.inputFileName)))) {
            String token;
            StringTokenizer st;        
            Integer count;
            int ctr = 0;
            int lineCtr = 0;
            HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
            HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
            Integer[] indexes = getIndexes();
            for(Integer idx : indexes) {
                count = indexMap.get(idx);
                if(count == null)
                    indexMap.put(idx, 1);
                else
                    indexMap.put(idx, count + 1);
            }

            List stopWordsList = Arrays.asList(stopWordsArray);
	        for(String line; (line = br.readLine()) != null; ) {
                if(indexMap.containsKey(lineCtr)) {
                    ctr = indexMap.get(lineCtr);
                    while(0 < ctr--) {
                        st = new StringTokenizer(line, delimiters);
                        while(st.hasMoreTokens()) {
                            token = st.nextToken().toLowerCase().trim(); 
                            if(stopWordsList.contains(token))
                                continue;
                            
                            count = wordCount.get(token);
                            if(count == null)
                                wordCount.put(token, 1);
                            else
                                wordCount.put(token, count + 1);
                        }
                    }
                }
                lineCtr++;
            }	

            ValueComparator vc = new ValueComparator(wordCount);
            TreeMap<String, Integer> sortedWordCount = new TreeMap<String, Integer>(vc); 
            sortedWordCount.putAll(wordCount);

            ctr = 0;
            for(Map.Entry<String, Integer> entry : sortedWordCount.entrySet()) {
                ret[ctr++] = entry.getKey();

                if(ctr >= 20)
                    break;
            }
	    }

        return ret;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
