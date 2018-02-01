import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 
 * @author ULG
 * reads Reco file (manually annotated recommendations)
 * gets only the Reco for a certain style (based on param: g, v, l, s)
 * output results useful for comparing againts algo detect, e.g. produced by DictStyleDetector
 */


public class ReadReco {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String style = "g";
		String fileName = "C:/Users/ULG/Dropbox/Cédric/Working paper Bibliography - Cedric/Results/5000 texts_Reco.txt";
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String ln = br.readLine();
			while(ln != null){
				ln = ln.trim();
				String [] splitLn = ln.split("\t");
				if(splitLn[0].trim().equals("g")){
					System.out.println(splitLn[1].trim());
				}
				ln = br.readLine();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		

	}

}
