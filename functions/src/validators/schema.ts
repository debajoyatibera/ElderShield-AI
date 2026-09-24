import { z } from "zod";

export const analyzeThreatRequestSchema = z.object({
  contentType: z.enum(["MESSAGE", "LINK", "CALL"]),
  content: z.string().min(1).max(1000).trim().refine(s => s.length > 0, "Content cannot be only whitespace"),
  appLanguage: z.enum(["en", "bn", "hi"]),
});

export const analyzeThreatResponseSchema = z.object({
  status: z.enum(["SAFE", "WARNING", "DANGER", "UNKNOWN"]),
  confidence: z.number().min(0.0).max(1.0),
  scamCategory: z.enum([
    "PHISHING",
    "OTP_SCAM",
    "BANKING_SCAM",
    "KYC_SCAM",
    "UPI_SCAM",
    "INVESTMENT_SCAM",
    "JOB_SCAM",
    "PRIZE_SCAM",
    "IMPERSONATION",
    "TECH_SUPPORT_SCAM",
    "DELIVERY_SCAM",
    "ROMANCE_SCAM",
    "MALWARE_LINK",
    "REMOTE_ACCESS_SCAM",
    "OTHER"
  ]).nullable(),
  simpleExplanation: z.string().min(1),
  recommendedAction: z.string().min(1),
  detectedLanguage: z.enum(["en", "bn", "hi"]).nullable(),
});
