package org.apdplat.qa.model;

/**
 * 医疗问题的候选答案
 */
public class CandidateEvidence implements Comparable<CandidateEvidence>  {

    private String questionStr;

    private String answer;

    private double score = 1.0;

    @Override
    public int compareTo(CandidateEvidence o) {
        if (o != null && o instanceof CandidateEvidence) {
            CandidateEvidence a = (CandidateEvidence) o;
            if (this.score < a.score) {
                return -1;
            }
            if (this.score > a.score) {
                return 1;
            }
            if (this.score == a.score) {
                return 0;
            }
        }
        throw new RuntimeException("无法比较大小");
    }

    @Override
    public int hashCode() {
        return this.answer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CandidateEvidence)) {
            return false;
        }
        CandidateEvidence a = (CandidateEvidence) obj;
        return this.answer.equals(a.answer);
    }



    public String getQuestionStr() {
        return questionStr;
    }

    public void setQuestionStr(String questionStr) {
        this.questionStr = questionStr;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
