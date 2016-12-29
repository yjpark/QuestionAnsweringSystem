package org.apdplat.qa.score.evidence;



import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.ScoreWeight;
import org.apdplat.word.analysis.SimpleTextSimilarity;
import org.apdplat.word.analysis.TextSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对证据的title和prompt进行相关性评分,用于辅助选择答案，其中titile用余弦形似判断，prompt用简单文本相关性判断
 */
public class SimilarityEvidenceSore implements EvidenceScore {

    private static final Logger LOG = LoggerFactory.getLogger(SkipBigramEvidenceScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void score(Question question, Evidence evidence) {

        TextSimilarity textSimilarity = new org.apdplat.word.analysis.CosineTextSimilarity();
        double score1pk1 = textSimilarity.similarScore(evidence.getTitle(), question.getQuestion());
        evidence.setTitleSimilarity(score1pk1);

        textSimilarity = new SimpleTextSimilarity();
        double score1pk2 = textSimilarity.similarScore(evidence.getPrompt(), question.getQuestion());

        evidence.setPromptSimilarity(score1pk2);

        LOG.debug("Evidence TermMatch评分:" + score1pk2);
        evidence.addScore(score1pk2);

    }

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }
}
