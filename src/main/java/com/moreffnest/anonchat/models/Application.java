package com.moreffnest.anonchat.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Application {
    private User applicant;
    private String applicantSex;
    private int applicantAge;
    private String interlocutorSex;
    private IntRange interlocutorAgeRange;
    private long conversationId;
    private String sessionId;

    public Application(String applicantSex, int applicantAge, String interlocutorSex,
                       IntRange interlocutorAgeRange, String sessionId) {
        this.applicantSex = applicantSex;
        this.applicantAge = applicantAge;
        this.interlocutorSex = interlocutorSex;
        this.interlocutorAgeRange = interlocutorAgeRange;
        this.sessionId = sessionId;
    }

    public boolean matches(Application application) {
        return (interlocutorSex.equals("no") || interlocutorSex.equals(application.applicantSex))
                && (interlocutorAgeRange.isInRange(application.applicantAge))
                && (application.interlocutorSex.equals("no") || application.interlocutorSex.equals(applicantSex))
                && (application.interlocutorAgeRange.isInRange(applicantAge))
                && (!sessionId.equals(application.getSessionId()));
    }

    @NoArgsConstructor
    @Setter
    public static class IntRange {
        private int min;
        private int max;

        public IntRange(int a, int b) {
            if (a <= b) {
                min = a;
                max = b;
            } else {
                min = b;
                max = a;
            }
        }
        public boolean isInRange(int number) {
            return (number >= min) && (number <= max);
        }
    }
}
