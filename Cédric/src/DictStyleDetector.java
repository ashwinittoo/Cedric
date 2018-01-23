/*
 * Detect style keywords from lexicon in sentences
 * *(replace ' t in lemmatized text file)
 * *(pre-process lexicons, remove -, lower/upper case etc): dont care to lemmatize dict entries, just read all: Inflected entries will simply not be detected in the text
 * *for each text: keep track of num sentences per style
 * 
 * WHEN PROCESSING LEMMAFILES : also to replace -, /
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class DictStyleDetector {
	
	String growthLexiconFile ="C:\\Users\\ULG\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\LM and GI dictionaries Lexicons\\Growth Lexicon.txt";
	String valueLexiconFile = "C:\\Users\\ULG\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\LM and GI dictionaries Lexicons\\Value Lexicon.txt";
	
	ArrayList<String> growthLexiconList = new ArrayList<String>();
	ArrayList<String> valueLexiconList = new ArrayList<String>();
		
	public DictStyleDetector(String fileName){
		
		populateLexiconList(growthLexiconFile, 1); //need to also replace /, -
		//System.out.println("******");
		populateLexiconList(valueLexiconFile, 2);
		
		
		
	} //constructor
	
	public void populateLexiconList(String lexFileName, int a){
			try{
				BufferedReader br = new BufferedReader(new FileReader(new File(lexFileName)));
				String ln = br.readLine();
				while(ln != null){
					ln = ln.trim();
					String []toks = ln.split(",");
					processToks(toks,a); //remove all the / - and set to lower case, and add to arraylist
					//System.out.println("LEX: "+ lexFileName+" : "+ln);
					break; //lexicon txt files should contain only 1 line separated by ,
					
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
			if(a==1){
				this.growthLexiconList.add(s);
			}
			else if(a==2){
				this.valueLexiconList.add(s);
			}
		
		}
		
		
	}

	public static void main(String[] args) {
		
		//Path to file containing lemmatized doc (magazine articles)
		String lemmTextFile = "C:\\Users\\ULG\\Dropbox\\Cédric\\Working paper Bibliography - Cedric\\Lemmatized";
		
		DictStyleDetector dSD = new DictStyleDetector(lemmTextFile);

	}

}
