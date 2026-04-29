package org.abdev.rtfm.util;

import org.springframework.ai.chat.prompt.PromptTemplate;

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

    public static String getPrompt1() {
        return """
            You are a helpful assistant that answers the user's question using only the provided context.
    
            Guidelines:
            - Provide clear, concise, and well-structured answers.
            - Only use information from the given context.
            - Do not invent or assume information if it is not present in the context.
            """;
    }

    public static PromptTemplate promptTemplate() {
        return new PromptTemplate("""
                        Given the context information and no prior knowledge, answer the query.
                        User question : {message}
                
                        You must:
                
                        1. Answer the user's question normally and helpfully.
                        2. Extract any useful long-term memory from the user message.  
                        You may ask follow-up questions to the user only when necessary to clarify or improve the answer.
                        These questions must be minimal, relevant, and non-intrusive. Avoid being intrusive or overwhelming the user.
                
                        Memory types:
                
                        - EPISODIC_INFO: events or experiences tied to time
                        - SEMANTIC_MEMORY: stable facts about the user
                        - PROCEDURAL_MEMORY: habits, preferences, rules
                
                        Rules for memory extraction:
                        - Only extract useful, reusable information
                        - Do NOT extract generic conversation
                        - Keep memory short and atomic
                        - If nothing relevant, return empty list
                
                        Return ONLY valid JSON:
                
                        answer: string
                        memories: array of objects with:
                        - type: EPISODIC_INFO | SEMANTIC_MEMORY | PROCEDURAL_MEMORY
                        - content: string
                """);
    }
}
