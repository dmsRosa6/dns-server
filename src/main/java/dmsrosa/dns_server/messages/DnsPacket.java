package dmsrosa.dns_server.messages;

import dmsrosa.dns_server.BytePacketBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DnsPacket {
    private DnsHeader header;
    private List<DnsQuestion> questions;
    private List<DnsRecord> answers;
    private List<DnsRecord> authorities;
    private List<DnsRecord> resources;

    public DnsPacket(DnsHeader header, List<DnsQuestion> questions, List<DnsRecord> answers,
                     List<DnsRecord> authorities, List<DnsRecord> resources) {
        this.header = header;
        this.questions = questions;
        this.answers = answers;
        this.authorities = authorities;
        this.resources = resources;
    }

    public DnsPacket(){
        this.header = new DnsHeader();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        authorities = new ArrayList<>();
        resources = new ArrayList<>();
    }

    public DnsHeader getHeader() {
        return header;
    }

    public List<DnsQuestion> getQuestions() {
        return questions;
    }

    public List<DnsRecord> getAnswers() {
        return answers;
    }

    public List<DnsRecord> getAuthorities() {
        return authorities;
    }

    public List<DnsRecord> getResources() {
        return resources;
    }

    public static DnsPacket read(BytePacketBuffer reader) throws IOException {
        DnsHeader header = DnsHeader.read(reader);
        List<DnsQuestion> questions = new ArrayList<>();
        List<DnsRecord> answers = new ArrayList<>();
        List<DnsRecord> authorities = new ArrayList<>();
        List<DnsRecord> resources = new ArrayList<>();

        for (int i = 0; i < header.getQDCOUNT(); i++) {
            questions.add(DnsQuestion.read(reader));
        }
        for (int i = 0; i < header.getANCOUNT(); i++) {
            answers.add(DnsRecord.read(reader));
        }
        for (int i = 0; i < header.getNSCOUNT(); i++) {
            authorities.add(DnsRecord.read(reader));
        }
        for (int i = 0; i < header.getARCOUNT(); i++) {
            resources.add(DnsRecord.read(reader));
        }
        return new DnsPacket(header, questions, answers, authorities, resources);
    }

    public void write(BytePacketBuffer writer) {
        header.setQDCOUNT((short) questions.size() );
        header.setANCOUNT((short) answers.size() );
        header.setNSCOUNT((short) authorities.size() );
        header.setARCOUNT((short) resources.size() );

        header.write(writer);

        for (DnsQuestion question : questions) {
            question.write(writer);
        }
        for (DnsRecord rec : answers) {
            rec.write(writer);
        }
        for (DnsRecord rec : authorities) {
            rec.write(writer);
        }
        for (DnsRecord rec : resources) {
            rec.write(writer);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DnsPacket {");
        sb.append("\n  header: ").append(header);

        sb.append("\n  questions: ");
        if (questions.isEmpty()) {
            sb.append("[]");
        } else {
            sb.append("[");
            for (int i = 0; i < questions.size(); i++) {
                sb.append(questions.get(i));
                if (i < questions.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }

        sb.append("\n  answers: ");
        if (answers.isEmpty()) {
            sb.append("[]");
        } else {
            sb.append("[");
            for (int i = 0; i < answers.size(); i++) {
                sb.append(answers.get(i));
                if (i < answers.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }

        sb.append("\n  authorities: ");
        if (authorities.isEmpty()) {
            sb.append("[]");
        } else {
            sb.append("[");
            for (int i = 0; i < authorities.size(); i++) {
                sb.append(authorities.get(i));
                if (i < authorities.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }

        sb.append("\n  resources: ");
        if (resources.isEmpty()) {
            sb.append("[]");
        } else {
            sb.append("[");
            for (int i = 0; i < resources.size(); i++) {
                sb.append(resources.get(i));
                if (i < resources.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        }

        sb.append("\n}");
        return sb.toString();
    }

}
