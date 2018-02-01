/*
 * Detect style keywords from lexicon in sentences
 * *(replace ' t in lemmatized text file)
 * *(pre-process lexicons, remove -, lower/upper case etc): dont care to lemmatize dict entries, just read all: Inflected entries will simply not be detected in the text
 * *for each text: keep track of num sentences per style
 * 
 * WHEN PROCESSING LEMMAFILES : also to replace -, /
 */

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.io.*;

public class DictStyleDetector {
	
	String growthLexiconFile ="C:\\Users\\ULG\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\LM and GI dictionaries Lexicons\\Style lexicons\\Growth Lexicon.txt";
	String valueLexiconFile = "C:\\Users\\ULG\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\LM and GI dictionaries Lexicons\\Style lexicons\\Value Lexicon.txt";
	
	ArrayList<String> growthLexiconList = new ArrayList<String>();
	ArrayList<String> valueLexiconList = new ArrayList<String>();
	
	StanfordCoreNLP pipeline;
	
	int growthLexCount = 0; 
		
	public DictStyleDetector(String fileDir){
		
		Properties props  =	 new Properties();
		 props.put("annotators", "tokenize,  ssplit");
		 
		 this.pipeline = new StanfordCoreNLP(props);
		
		populateLexiconList(growthLexiconFile, 1); //param 1 for growth lexicon, -
		//System.out.println("******");
		populateLexiconList(valueLexiconFile, 2); //param 2 for value lexicon
		
				
		processText(fileDir, 1); //fileName: file with lemmatized text, passed from main(); param 1 or 2: growth or value lexicon
		
		
	} //constructor
	
	public void processText(String fileDir, int a){ //MAYBE REA SOME FALSE FILES WITH KNOWN EXAMPLES OF LEX Terms, eg. higher roe
		
		int textCnt = 0 ; //for testing purpose, break after maxCnt
		int maxCnt = 50000;
		File fileDirFile = new File(fileDir);
		
		ArrayList<String> tempLex = new ArrayList<String>();
		if(a==1){
			tempLex = this.growthLexiconList;
		}
		else if (a==2){
			tempLex = this.valueLexiconList;
		}
		 for (final File fileEntry : fileDirFile.listFiles()) {
			 textCnt++;
			 System.out.println("\n\nFile "+ textCnt+": "+fileEntry.getName());
			 try{
				
				 
				 BufferedReader br = new BufferedReader(new FileReader(fileEntry));
				 
				 String ln = br.readLine();
				 
				 //ALL THESE PROCESSING SHOULD BE DONE AFTER DOING SENTENCE SPLIT....NOT EVEN NEED TO READ FILE ETC
				 while(ln !=null){ //EACH LN is in fact one complete doc, the file contents by itself
					 ln = ln.trim().toLowerCase();
					 ln = ln.replaceAll("-", " ");
					 ln = ln.replaceAll("/", " ");
					 ln =ln.replaceAll("' t" , "not");
					 ln = ln.replaceAll("[^\\x00-\\x7F]", ""); //replace non ascii
					 					 
					// System.out.println(ln);
					 //SENTENCE SPLIT
					 //APPLY REGEX , REGEX PAT created from lex entry//MAYBE NO NEED FOR REG
					 Annotation doc = new Annotation(ln);
					 this.pipeline.annotate(doc); //AN entire doc
				        // Iterate over all of the sentences found
				        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
				        for(CoreMap sentence: sentences) {
				        	//System.out.println("sentence is "+ sentence.toString());
				        	
				        	
				        	//Difficult to build regexes for each one of the lex entries
				        	/* 
				        	String pattern = "p\\s+e";
				        	 Pattern r = Pattern.compile(pattern);
				        	 Matcher m = r.matcher(sentence.toString().trim());
				        	 if(m.find()){
				        		 System.out.println("found in "+ sentence.toString());
				        	 }
				        	 */
				        	
				        	/*
				        	 * 2 methods to do lexicon entry check occurence in text
				        	 * weak one: if only one word from multi-word entrey of lex appears in sentence: sentence is ok
				        	 * strict one: all the words needs to be present
				        	 */
				        	
				        	//weakLexOccurrenceMatch(sentence.toString(), 1);
				        	/*
				        	 * Not idea: sentence contains "return" would match lex entry "high returns on equity", and would be incorrectly labeled as growth
				        	 * better to check for each word, but to split the lexword and sentence words and compare elem by elem (word by word)
				        	
				        	Another issue (not linked to weak/strong checking but to lex entries defn is stopwords (e.g. on, to). Many  general sentences contain these words (not necessarity abt growth ,value stocks)
				        	 *
				        	 */
				        	
				        	String lemSent = sentence.toString();
				        	if( lemSent.startsWith("*date") || lemSent.startsWith("*title") || lemSent.startsWith("*author")){
				        		
				        	}
				        	
				        	else{
				        		lemSent = lemSent.replaceAll("\\.", " "); //replace the . that appears end of sentence and prevents lex matching, e.g. "...equity."
				        		lemSent = lemSent.trim();
				        		System.out.println("Sentence "+lemSent);
				        		strongLexOccurrenceMatch(lemSent, 1);
				        	//THE CHECKING FOR LEX CONTENT IN SENTENCES TO BE DONE HERE
				        	}//if body
				        } //for CoreMap sentence
				        
					 ln = br.readLine(); 
				 } //while each line in a file
			 
			 } //try
			 catch(Exception e){
				 e.printStackTrace();
			 } //catch
			 
			 if(textCnt == maxCnt){
				 break;
			 }
		 }//for each file in Dir
		System.out.println("Total Growth Lex Matches:"+this.growthLexCount) ;
	} //processText
	
	
	
	public void populateLexiconList(String lexFileName, int a){
		//int a: param to determine whether growth or value lexicon
			try{
				BufferedReader br = new BufferedReader(new FileReader(new File(lexFileName)));
				String ln = br.readLine();
				while(ln != null){
					ln = ln.trim();
					String []toks = ln.split(","); //each array elem from split is an entry from the lexicon
					processToks(toks,a); //remove all the / - and set to lower case, and add to arraylist
					//System.out.println("LEX: "+ lexFileName+" : "+ln);
					break; //lexicon txt files should contain only 1 line separated by , --> no need to read further
						//ln = br.readLine();
				} //while
				
			} //try
			catch(Exception e){
				e.printStackTrace();
			}
			
	} //populateLex
	
	private void processToks(String [] toks, int a){
		for(String s:toks){
			s = s.trim().toLowerCase();
			s=s.replaceAll("-", " ");
			s=s.replaceAll("/", " ");
			s=s.replaceAll("'", " ");
			s = s.trim();
			if(a==1){
				this.growthLexiconList.add(s);
			}
			else if(a==2){
				this.valueLexiconList.add(s);
			}
		
		}
		
		
	}

	public void weakLexOccurrenceMatch(String lemSen, int param){
	//for multi-word lex entries: if only 1 word from the entry appears in the sentence --> sentence is taken
		//param 1: growth lex, 2: value lex
		/*
		 * read lex
		 */
		ArrayList<String> tempLex = new ArrayList<String>();
		tempLex = this.growthLexiconList;
		if(param == 2){
			tempLex = this.valueLexiconList;
		}
		
		for(String s:tempLex){
			String [] lexEntry = s.split(" ");
			for(String lexWord: lexEntry){ //for each word in multi-word lex entry (or the entry itself if single word)
				if(lemSen.contains(lexWord) && !lexWord.trim().equals("to") && !lexWord.equals("on")){
					System.out.println("-->LEXMATCH "+lexWord + " ( " + s + " ) in "+ lemSen);
					//need to "break" here, once a word is matched (for weakLexMatch)
				} 
			}  //for each lexWord
		}//end for each lex entry s
	
	}//end weakLex
	
	public void strongLexOccurrenceMatch(String lemSem, int param){
		//System.out.println("\n\nSENTENCE: " + lemSem);
		ArrayList<String> tempLex = new ArrayList<String>();
		tempLex = this.growthLexiconList;
		if(param == 2){
			tempLex = this.valueLexiconList;
		}
		
		for(String s:tempLex){ //read lexicon entries
			//System.out.println("LEXICON ENTRY: " + s);
			String [] lexEntry = s.split(" "); //split the lex enty, into individual words
			/*
			 * use hashes?
			 * check of of lex entry /sentence word: contains.key, but to ensure order, the lex entry words should be in array
			 * for each arrayelemet: check if sentence hashcontans lexntry element as key,
			 * break as soon as an arrayelemen (lexentry) not found as hashkey
			 */
			
			//dump the sentence into a hashet
			String [] sentSplit = lemSem.split(" ");
			HashSet<String> sentWordHash = new HashSet<String>(); //Important: order of words not maintained in hashset
			for(String sWord : sentSplit){
				sentWordHash.add(sWord.trim());
			} //end for
			
			boolean contains = true;
			for(String lexWord: lexEntry){
				//System.out.println("*** Lex Word" + lexWord);
				if(! sentWordHash.contains(lexWord)){
					contains  = false;
				}
			}
			
			if(contains){
				//System.out.println("  --> Found lex entry " + s +" in " + lemSem);
				System.out.println("  --> LEXMATCH " + s + "|"+lemSem );
				
				growthLexCount++;
			}
			

			/*System.out.println("org sentence: "+lemSem);
			for(String t: sentWordHash){
				System.out.print (" * " + t);
			}
			*/
			
			/*for(String lexWord: lexEntry){
				//System.out.println(s +" splitted " + lexWord );
				
			} //lexEntry/lexWord
			 */
		
		} //end for s: for each lex entry
		
	} //end strongLexOccurrenceMatch
	
	private void writeLexSentence(String lex, String sent){
		/*
		 * helper method, to write only that sentence matching a lex entry to a temp file. 
		 * This output useful to get all lex matches by algo o
		 */
	}
	public static void main(String[] args) {
		
		//Path to file containing lemmatized doc (magazine articles)
		//String lemmTextFile = "C:\\Users\\ULG\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\Lemmatized";
		//String lemmTextDir = "D:\\HEC\\Research\\PhD\\Gillain\\testtext";
		String lemmTextDir = "C:\\Users\\ULG\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\Lemmatized";
		DictStyleDetector dSD = new DictStyleDetector(lemmTextDir);

	}

}
