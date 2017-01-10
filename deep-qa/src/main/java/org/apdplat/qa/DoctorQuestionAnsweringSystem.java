package org.apdplat.qa;

import org.apdplat.qa.datasource.GitFileDataSource;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.model.DoctorReply;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.CommonDoctorQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystemImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用文件系统的孕期医疗数据源共享问题问答系统
 */
public class DoctorQuestionAnsweringSystem {
    private static final QuestionAnsweringSystem QUESTION_ANSWERING_SYSTEM = new CommonDoctorQuestionAnsweringSystem();
    static {
        QUESTION_ANSWERING_SYSTEM.setDataSource(new GitFileDataSource(FilesConfig.doctorMaterial));
    }

    public static QuestionAnsweringSystem getInstance(){
        return QUESTION_ANSWERING_SYSTEM;
    }


    /**
     * 选择候选答案,一般是三个
     * @param question
     * @param topN
     * @return
     */
    public static DoctorReply candidateEvidence(Question question, int topN){
        DoctorReply reply = new DoctorReply();
        Evidence answer = QuestionAnsweringSystemImpl.selectAnswer2(question);
        reply.setAnswer(answer);
        List<Evidence> result = new ArrayList<>();
        if(answer != null){
            result.add(answer);
            List<Evidence> l =  question.getTopNEvidence(topN);
            for (Evidence e: l) {
                if(e.getId() != answer.getId()){
                    result.add(e);
                }
                if(result.size() >= topN) break;
            }
        }else{
            List<Evidence> tmp = question.getTopNEvidence(20);
            tmp = QuestionAnsweringSystemImpl.suggest(question, tmp, topN);
            tmp = tmp.stream().filter(e -> e.getScore() > 0).collect(Collectors.toList());
            result.addAll(tmp);
        }
        reply.setCandidateEvidence(result);
        return reply;
    }

    public static void main(String[] args){

    }

}
