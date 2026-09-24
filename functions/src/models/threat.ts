export type ContentType = "MESSAGE" | "LINK" | "CALL";
export type AppLanguage = "en" | "bn" | "hi";
export type ThreatStatus = "SAFE" | "WARNING" | "DANGER" | "UNKNOWN";

export interface AnalyzeThreatRequest {
  contentType: ContentType;
  content: string;
  appLanguage: AppLanguage;
}

export interface AnalyzeThreatResponse {
  status: ThreatStatus;
  confidence: number;
  scamCategory: string | null;
  simpleExplanation: string;
  recommendedAction: string;
  detectedLanguage: AppLanguage | null;
}
