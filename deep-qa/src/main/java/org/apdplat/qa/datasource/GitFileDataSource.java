package org.apdplat.qa.datasource;

import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.apdplat.qa.system.DocotorQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystemImpl;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 从git文件目录检索问题及其对应的证据
 */
public class GitFileDataSource implements  DataSource{

    private static final Logger LOG = LoggerFactory.getLogger(GitFileDataSource.class);

    private List<String> files = new ArrayList<>();

    public GitFileDataSource(String file){
        this.files.add(file);
    }

    public GitFileDataSource(List<String> files){
        this.files.addAll(files);
    }


    @Override
    public List<Question> getQuestions() {
        return getAndAnswerQuestions(null);
    }

    @Override
    public Question getQuestion(String questionStr) {
        return getAndAnswerQuestion(questionStr, null);
    }

    @Override
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem) {
        List<Question> questions = new ArrayList<>();

        for (String file : files) {
            BufferedReader reader = null;
            try {
                Path filePath = FileSystems.getDefault().getPath("/data/doctor/", file);
                InputStream in = Files.newInputStream(filePath);
                reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                Question question = null;
                String line = reader.readLine();
                while (line != null) {
                    if (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                        //读下一行
                        line = reader.readLine();
                        continue;
                    }
                    if (line.trim().startsWith("?") || line.indexOf("?") == 1) {
                        //在构造下一个问题之前回答上一个问题（好好体会，结合文件格式）
                        //回答问题
                        if (questionAnsweringSystem != null && question != null) {
                            questionAnsweringSystem.answerQuestion(question);
                        }
                        String qs = line.substring(line.indexOf(".") + 1).trim();

                        String questionStr = null;
                        String expectAnswer = null;
                        String[] attrs = qs.split("[:|：]");
                        if (attrs == null) {
                            questionStr = qs;
                        }
                        if (attrs != null && attrs.length == 1) {
                            questionStr = attrs[0];
                        }
                        if (attrs != null && attrs.length == 2) {
                            questionStr = attrs[0];
                            expectAnswer = attrs[1];
                        }
                        LOG.info("Question:" + questionStr);
                        LOG.info("ExpectAnswer:" + expectAnswer);

                        question = new Question();
                        question.setQuestion(questionStr);
                        question.setExpectAnswer(expectAnswer);
                        questions.add(question);
                        //读下一行
                        line = reader.readLine();
                        continue;
                    }
                    Evidence answer = new Evidence();
                    if (line.startsWith("Title:")) {
                        answer.setTitle(line.substring(6).trim());
                    }
                    //读下一行
                    line = reader.readLine();
                    if (line.startsWith("Snippet:")) {
                        answer.setSnippet(line.substring(8).trim());
                    }

                    line = reader.readLine();
                    if (line.startsWith("ID:")){
                        answer.setId(Integer.parseInt(line.substring(3).trim()));
                    }
                    line = reader.readLine();
                    if(line.startsWith("Prompt:")){
                        answer.setPrompt(line.substring(7).trim());
                    }

                    if (answer.getTitle() != null && answer.getSnippet() != null && question != null) {
                        question.addEvidence(answer);
                    }

                    //读下一行
                    line = reader.readLine();
                }

                //回答最后一个问题
                if (questionAnsweringSystem != null && question != null) {
                    questionAnsweringSystem.answerQuestion(question);
                }
            } catch (FileNotFoundException e) {
                LOG.error("文件找不到", e);
            } catch (UnsupportedEncodingException e) {
                LOG.error("编码错误", e);
            } catch (IOException e) {
                LOG.error("IO错误", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOG.error("关闭文件错误", e);
                    }
                }
            }
        }
        return questions;
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        for (Question question : getQuestions()) {
            String q = question.getQuestion().trim().replace("?", "").replace("？", "");
            questionStr = questionStr.trim().replace("?", "").replace("？", "");
            if (q.equals(questionStr)) {
                //回答问题
                if (questionAnsweringSystem != null) {
                    questionAnsweringSystem.answerQuestion(question);
                }
                return question;
            }
        }
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DataSource dataSource = new GitFileDataSource(FilesConfig.doctorMaterial);

        //Question question = dataSource.getQuestions().get(0);
        //question.setQuestion("今天天气是什么");

        DocotorQuestionAnsweringSystem questionAnsweringSystem = new DocotorQuestionAnsweringSystem();
        //questionAnsweringSystem.answerQuestion(question);
        try{
            testSiample(dataSource, questionAnsweringSystem);

        }catch(Exception e){
            e.printStackTrace();
        };
        //LOG.info("答案:" + question.getExpectAnswer());

    }

    /**
     * 测试样本文件 question_siample.txt
     */
    public static void testSiample(DataSource dataSource, DocotorQuestionAnsweringSystem questionAnsweringSystem) throws Exception {
        Question question = dataSource.getQuestions().get(0);

        String file = "question_siample.txt";
        BufferedReader reader = null;
        Path filePath = FileSystems.getDefault().getPath("/data/doctor/", file);
        InputStream in = Files.newInputStream(filePath);
        reader = new BufferedReader(new InputStreamReader(in, "utf-8"));

        String resultfile = "result.txt";
        Path resultPath = FileSystems.getDefault().getPath("/data/doctor/", resultfile);
        OutputStream out = Files.newOutputStream(resultPath);
        BufferedWriter writer = null;
        writer = new BufferedWriter( new OutputStreamWriter(out, "utf-8"));

        writer.write("问题\t期望答案id" +
                "\t期望答案本次分数\t期望答案问题相关度\t期望答案关键词相关度" +
                "\t备选1id\t备选1分数\t备选1问题相关度\t备选1关键词相关度" +
                "\t备选2id\t备选2分数\t备选2问题相关度\t备选2关键词相关度" +
                "\t备选3id\t备选3分数\t备选3问题相关度\t备选3关键词相关度" +
                "\t算法选择的答案\t选择答案正确是否完美1完美0.5不完美0错误\n");


        String line = reader.readLine();
        while (line !=null){
            if (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                //读下一行
                line = reader.readLine();
                continue;
            }
            line = line.trim();

            String[] terms = line.split("\\|");
            String questionStr = terms[0];
            String expectid = terms[1].trim().toLowerCase();
            String result = "0";

            question.setQuestion(questionStr);

            questionAnsweringSystem.answerQuestion(question);
            List<Evidence> evidences =  question.getEvidences();
            writer.write(questionStr + "\t" + expectid + "\t");

            List<Evidence> expectEvidences =
                    evidences.stream()
                            .filter(e -> Integer.toString(e.getId()).equals(expectid))
                            .collect(Collectors.toList());
            if(expectEvidences.isEmpty()){
                writer.write("0 \t 0\t0\t");
            }else {
                writer.write(Double.toString(expectEvidences.get(0).getScore()) + "\t");
                writer.write(Double.toString(expectEvidences.get(0).getTitleSimilarity()) + "\t");
                writer.write(Double.toString(expectEvidences.get(0).getPromptSimilarity()) + "\t");
            }

            evidences = question.getTopNEvidence(4);

            for(int inx = 0; inx < 3 ;inx++){
                Evidence e = evidences.get(inx);
                if(Integer.toString(e.getId()).equals(expectid)){
                    result = "0.5";
                }
                writer.write( Integer.toString(e.getId()) + "\t" + e.getScore() + "\t");
                writer.write(Double.toString(e.getTitleSimilarity()) + "\t");
                writer.write(Double.toString(e.getPromptSimilarity()) + "\t");
            }

            String answerid = QuestionAnsweringSystemImpl.selectAnswer2(question);


            if(answerid.toLowerCase().equals(expectid)){
                result =  "1";
            }

            writer.write("");
            writer.write(answerid + "\t" + result +"\n");

            question.setExpectAnswer("0");
            question.setQuestion("");
            writer.flush();
            line = reader.readLine();
        }
        writer.flush();
        writer.close();
    }


}
