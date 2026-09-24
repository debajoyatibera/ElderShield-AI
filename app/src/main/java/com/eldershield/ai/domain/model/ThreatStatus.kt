package com.eldershield.ai.domain.model

/**
 * Represents the threat status of a communication (message, call, link).
 * This model is agnostic of the underlying AI service (Gemini, etc.)
 */
enum class ThreatStatus {
    SAFE,
    WARNING,
    DANGER,
    UNKNOWN // For analyzing state or when status cannot be determined
}
