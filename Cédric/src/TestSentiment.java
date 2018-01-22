import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations; 
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;


////https://www.programcreek.com/java-api-examples/index.php?source_dir=sentimentr-release-master/src/sentimentr-service/src/main/java/io/pivotal/fe/sentiment/engine/NLP.java
//https://stackoverflow.com/questions/20359346/executing-and-testing-stanford-core-nlp-example




public class TestSentiment {
	
	StanfordCoreNLP pipeline;
	
	public TestSentiment(String str) {
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize,ssplit, pos, parse, sentiment");
		this.pipeline = new StanfordCoreNLP(props);
		
		int longest = 0;
		int mainSentiment = 0;
		
		Annotation document = new Annotation(str);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        for(CoreMap sentence: sentences) {
        	Tree tree = sentence.get(SentimentAnnotatedTree.class); 
        	int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
        	
        	String partText = sentence.toString(); 
            if (partText.length() > longest) { 
                mainSentiment = sentiment; 
                //longest = partText.length();
                System.out.println(str + " SENTIMENT:"+ mainSentiment);
        	
            }
        	
        } //CoreMap Sentence
		
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TestSentiment("On the other hand, smaller firms don't get the attention they need from investors");
		

	}

}
