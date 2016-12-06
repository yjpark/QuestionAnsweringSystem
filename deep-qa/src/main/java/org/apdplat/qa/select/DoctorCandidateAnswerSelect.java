package org.apdplat.qa.select;

import org.apdplat.qa.model.CandidateAnswer;
import org.apdplat.qa.model.CandidateAnswerCollection;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kebo on 2016/12/5.
 */
public class DoctorCandidateAnswerSelect implements  CandidateAnswerSelect {

    private static final Logger LOG = LoggerFactory.getLogger(CommonCandidateAnswerSelect.class);

    @Override
    public void select(Question question, Evidence evidence) {
        CandidateAnswerCollection candidateAnswerCollection = new CandidateAnswerCollection();

        CandidateAnswer answer = new CandidateAnswer();
        answer.setAnswer(evidence.getSnippet());
        answer.setEvidence(evidence);
        candidateAnswerCollection.addAnswer(answer);
        LOG.info(evidence.getSnippet());
        evidence.setCandidateAnswerCollection(candidateAnswerCollection);
    }


}
