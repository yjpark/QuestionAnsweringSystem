package org.apdplat.qa;

import org.apdplat.qa.datasource.GitFileDataSource;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.system.CommonQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystem;

/**
 * 使用文件系统的孕期医疗数据源共享问题问答系统
 */
public class DoctorQuestionAnsweringSystem {
    private static final QuestionAnsweringSystem QUESTION_ANSWERING_SYSTEM = new CommonQuestionAnsweringSystem();
    static {
        QUESTION_ANSWERING_SYSTEM.setDataSource(new GitFileDataSource(FilesConfig.doctorMaterial));
    }

    public  static QuestionAnsweringSystem getInstance(){
        return QUESTION_ANSWERING_SYSTEM;
    }

    public static void main(String[] args){

    }

}
