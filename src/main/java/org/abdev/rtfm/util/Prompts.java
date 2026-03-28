package org.abdev.rtfm.util;

public class Prompts {

    public static String getPrompt(String question, String context, String recentMessages) {
        return """
            You are a helpful assistant that answers the user's question using only the provided context.
    
            Guidelines:
            - Provide clear, concise, and well-structured answers.
            - Only use information from the given context.
            - Do not invent or assume information if it is not present in the context.
    
            Context:
            %s
    
            Question:
            %s
    
            Previous Q&A:
            %s
            """.formatted(context, question, recentMessages);
    }
}
