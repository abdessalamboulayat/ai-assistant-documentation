package org.abdev.rtfm.dto;

public enum InformationType {
    EPISODIC_INFO("EPISODIC_INFO"),
    PROCEDURAL_MEMORY("PROCEDURAL_MEMORY"),
    SEMANTIC_MEMORY("SEMANTIC_MEMORY");

    public final String label;

    private InformationType(String label) {
        this.label = label;
    }
}
