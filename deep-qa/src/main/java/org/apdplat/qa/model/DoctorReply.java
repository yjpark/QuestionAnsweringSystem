package org.apdplat.qa.model;

import java.util.List;

/**
 * Created by kebo on 2017/1/10.
 */
public class DoctorReply {

    private Evidence answer;
    private List<Evidence> candidateEvidence;

    public Evidence getAnswer() {
        return answer;
    }

    public void setAnswer(Evidence answer) {
        this.answer = answer;
    }

    public List<Evidence> getCandidateEvidence() {
        return candidateEvidence;
    }

    public void setCandidateEvidence(List<Evidence> candidateEvidence) {
        this.candidateEvidence = candidateEvidence;
    }

}
