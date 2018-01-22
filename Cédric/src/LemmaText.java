//VERSION 22/1
//TESTONE

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormatSymbols;

import edu.stanford.nlp.coref.docreader.CoNLLDocumentReader.NamedEntityAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

public class LemmaText {
	
	
	//NER: http://www.informit.com/articles/article.aspx?p=2265404
	
	StanfordCoreNLP pipeline;
	//ArrayList<String> monthList;
	
	public LemmaText() {
		
		//setDateFormat();
			
		 Properties props;
		 props = new Properties();
		 props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		 
		 this.pipeline = new StanfordCoreNLP(props);
		
	}
	
	 public List<String> lemmatize(String documentText)
	    {
	        List<String> lemmas = new LinkedList<String>();
	        // Create an empty Annotation just with the given text
	        Annotation document = new Annotation(documentText);
	        // run all Annotators on this text
	        this.pipeline.annotate(document);
	        // Iterate over all of the sentences found
	        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	        for(CoreMap sentence: sentences) {
	            // Iterate over all tokens in a sentence
	            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	                // Retrieve and add the lemma for each word into the
	                // list of lemmas
	            	
	            	//NER can become useful later: we can replace all occurences of PERSON, DATE, ORGANIZATION with NER Class
	            	//System.out.println("TESTING NER:"+token+" NER:"+token.get(NamedEntityTagAnnotation.class));
	                String lemTxt = token.get(LemmaAnnotation.class);
	            	
	            	/*System.out.println("LEMMA:" + lemTxt); 
	                if(lemTxt.trim().equals(("' t"))){ ////EASIER NOT TO DO ANYTHING HERE. AS "'"  and "T" ARE 2 SEPARATE LEMMAS
	            		System.out.println(token+" FOUND TOK::"+ lemTxt );
	            		lemTxt =" not ";
	            	}*/
	                
	            	lemmas.add(lemTxt);
	            }
	        }
	        return lemmas;
	    }
	 
	 public void lemmatizeDir(File folder, File outDirFile) {
		 
		 int countLimit = 5000;
		 int fileCount=0;
		 for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		            lemmatizeDir(fileEntry, outDirFile);
		        } else {
		            //System.out.println(fileEntry.getName());
		            
		            //ArrayList <String> contentFiles = new ArrayList<String>();
		            
		            //BETTER TO READ LINE BY LINE
		            	           
		            try {
		            	
		            	//https://www.mkyong.com/java8/java-8-stream-read-a-file-line-by-line/
		            	//Stream<String>contentStream= Files.lines(Paths.get(folder.getPath()+"\\"+fileEntry.getName()));
		            			            	
		            	StringBuffer outLemmStr = new StringBuffer(); //to store lemmatized tokens and later dump to outfile
		            	
		            	
		            	System.out.println("\n\n***PROCESSING " + folder.getPath()+"\\"+fileEntry.getName()) ;
		            	
		            	BufferedReader br= new BufferedReader(new FileReader(new File(folder.getPath()+"\\"+fileEntry.getName())));
		            	
           		        String outputPath=outDirFile.getPath()+"\\"+ fileEntry.getName();
           		        
		            	BufferedWriter brWrt = new BufferedWriter(new FileWriter(new File(outputPath)));
		            	
		            	System.out.println("***OUTPUTTING TO:"+outputPath);
		            	
		            	String lnRead = br.readLine();
		            	lnRead = lnRead.trim();
		            	
		            	int lnCnt =0; //to keep track of line count and detect date, title , author and main text
		            	
		            	while( lnRead != null ) { //
		            		lnRead = lnRead.trim();
		            		//no need to check for dates, authors read etc
		            		
		            		/*
		            		 * Lemmatize
		            		 * Dump to a stringbuffer
		            		 * Write to newfile
		            		 */
		            		
		            		
		            		if(lnRead.length() > 0 && !lnRead.equals("")) {
		            			lnCnt++;
		            			System.out.println("ORG:"+lnRead);
		            			//String lemmString = String.join(" ", this.lemmatize(lnRead));
		            			
		            			String textTag = getTag(lnCnt);
		            					            			
		            			
		            			brWrt.write(textTag+String.join(" ", this.lemmatize(lnRead))+"\n");
		            			brWrt.flush();
		            			
		            			System.out.println("Writing:"+textTag+String.join(" ", this.lemmatize(lnRead))+"\n");
		            					            			
		            			
		            		}
		            		
		            		
		            		lnRead = br.readLine();	            		
		            		
		            	} //end whileBufferedReader
		            	
		            	
		            	//System.out.println("FIN BUFF: " + outLemmStr );
		            	
		            	//writeToFile(outLemmStr, outDirFile, fileEntry.getName()); //dump the lemmatized content to file
		            	
		            	fileCount++;
		            	if(fileCount > countLimit) {
		            		break;
		            	}
		            	
		            	
		            	
		            } //try
		            catch(Exception e) {
		            	e.printStackTrace();
		            }
		           
		            
		        } //else
		    }
	 }
	 
	 	 
	 private String getTag(int lnCnt) {
		 //returns a tag added to each line (e.g. author, date) as meta-data
		 String tag ="";
		 
		 if(lnCnt == 1) {
			 tag = "*DATE*";
		 }
		 else if(lnCnt == 2) {
			 tag = "*TITLE*";
		 }
		 else if(lnCnt == 3) {
			 tag = "*AUTHOR*";
		 }
		 else {
			 tag  = "*BODY*";
		 }
		 
		 return tag;
		 
	 }
	 
	 
	
	public static void main (String [] args) {
		
		 System.out.println("Starting Stanford Lemmatizer");
	        String text = "How could you be seeing into my eyes like open doors? \n"+
	                "You led me down into my core where I've became so numb \n"+
	                "Without a soul my spirit's sleeping somewhere cold \n"+
	                "Until you find it there and led it back home \n"+
	                "You woke me up inside \n"+
	                "Called my name and saved me from the dark \n"+
	                "You have bidden my blood and it ran \n"+
	                "Before I would become undone \n"+
	                "You saved me from the nothing I've almost become \n"+
	                "You were bringing me to life \n"+
	                "Now that I knew what I'm without \n"+
	                "You can've just left me \n"+
	                "You breathed into me and made me real \n"+
	                "Frozen inside without your touch \n"+
	                "Without your love, darling \n"+
	                "Only you are the life among the dead \n"+
	                "I've been living a lie, there's nothing inside \n"+
	                "You were bringing me to life.";
	        LemmaText slem = new LemmaText();
	        //System.out.println(slem.lemmatize(text));
	        
	        
	      
	        
	        
	        //NEED TO CREATE A FOLDER WITH THE TEXT FILE AND PASS IT TO THE METHOD TO LEMMATIZE HERE.
	        //String dirName = "C:\\Users\\Ashwin\\TestCode\\txtfile";
	        
	        String dirName = "C:\\Users\\Ashwin\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\5000 texts sample\\Sample 2012-2016";
	        
	        String outDirName = "C:\\Users\\Ashwin\\Documents\\Research\\Cédric\\LemmatizedFiles";
	        
	      //https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
	        
	        try {
	        	File dirFile = new File(dirName);
	        	File outDirFile = new File(outDirName);
	        	slem.lemmatizeDir(dirFile, outDirFile);
	        	
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
		
	}
	

}
