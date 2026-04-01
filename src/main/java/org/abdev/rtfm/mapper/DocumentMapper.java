package org.abdev.rtfm.mapper;

import org.springframework.ai.document.Document;

import java.util.List;

public class DocumentMapper {
    public static String mapDocumentListToString(List<Document> documents) {
        StringBuilder context = new StringBuilder();
        for (Document doc : documents) {
            String formattedContent = doc.getFormattedContent();
            context.append(formattedContent);
        }
        return context.toString();
    }
}
