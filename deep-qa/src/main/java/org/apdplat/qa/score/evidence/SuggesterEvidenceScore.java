package org.apdplat.qa.score.evidence;

import com.hankcs.hanlp.suggest.Suggester;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.ScoreWeight;
import org.apdplat.word.analysis.SimpleTextSimilarity;
import org.apdplat.word.analysis.TextSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by kebo on 2017/1/10.
 */
public class SuggesterEvidenceScore implements EvidenceScore  {

    private static final Logger LOG = LoggerFactory.getLogger(SkipBigramEvidenceScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void score(Question question, Evidence evidence) {

        Suggester suggester = new Suggester();

        suggester.addSentence(evidence.getTitle());
        System.out.println("--------------------------");
        System.out.println(evidence.getTitle());
        System.out.println(question.getQuestion());
        List suggestlist =  suggester.suggest(question.getQuestion(), 1);
        System.out.println(suggestlist);
        if(suggestlist.isEmpty()){
            evidence.addScore(0);
        }else{
            evidence.addScore(2.50);
        }
    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }


}
