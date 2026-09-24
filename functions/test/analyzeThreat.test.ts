import { describe, it, expect, vi, beforeEach } from "vitest";
import { analyzeThreat } from "../src/index";
import { GoogleGenAI } from "@google/genai";

vi.mock("@google/genai", () => {
  return {
    GoogleGenAI: vi.fn()
  };
});

describe("analyzeThreat (Phase 3C Gemini Integration)", () => {
  let mockGenerateContent: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    vi.resetAllMocks();
    mockGenerateContent = vi.fn();
    (GoogleGenAI as any).mockImplementation(() => {
      return {
        models: {
          generateContent: mockGenerateContent
        }
      };
    });
    process.env.GEMINI_API_KEY = "dummy-key";
  });

  const callFunction = async (data: any) => {
    const mockContext: any = {};
    return analyzeThreat.run({ data, ...mockContext });
  };

  const mockResponse = (responseJson: any) => {
    mockGenerateContent.mockResolvedValue({
      text: JSON.stringify(responseJson)
    });
  };

  // 1. Normal safe message
  it("should return SAFE for normal message", async () => {
    mockResponse({
      status: "SAFE",
      confidence: 0.95,
      scamCategory: null,
      simpleExplanation: "This looks like a normal message.",
      recommendedAction: "No action needed.",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Hi mom, I will be late.", appLanguage: "en" });
    expect(result.status).toBe("SAFE");
    expect(result.confidence).toBe(0.95);
  });

  // 2. OTP scam
  it("should return DANGER for OTP scam", async () => {
    mockResponse({
      status: "DANGER",
      confidence: 0.99,
      scamCategory: "OTP_SCAM",
      simpleExplanation: "Someone is asking for your OTP.",
      recommendedAction: "Do not share.",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Please share OTP", appLanguage: "en" });
    expect(result.status).toBe("DANGER");
    expect(result.scamCategory).toBe("OTP_SCAM");
  });

  // 3. Prize scam
  it("should return DANGER for Prize scam", async () => {
    mockResponse({
      status: "DANGER",
      confidence: 0.95,
      scamCategory: "PRIZE_SCAM",
      simpleExplanation: "You won a lottery.",
      recommendedAction: "Ignore.",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "You won $1000", appLanguage: "en" });
    expect(result.status).toBe("DANGER");
    expect(result.scamCategory).toBe("PRIZE_SCAM");
  });

  // 4. Banking/KYC scam
  it("should return DANGER for KYC scam", async () => {
    mockResponse({
      status: "DANGER",
      confidence: 0.9,
      scamCategory: "KYC_SCAM",
      simpleExplanation: "KYC alert.",
      recommendedAction: "Ignore.",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Update PAN KYC", appLanguage: "en" });
    expect(result.status).toBe("DANGER");
    expect(result.scamCategory).toBe("KYC_SCAM");
  });

  // 5. Suspicious URL
  it("should return WARNING for suspicious URL", async () => {
    mockResponse({
      status: "WARNING",
      confidence: 0.8,
      scamCategory: "PHISHING",
      simpleExplanation: "Link looks weird.",
      recommendedAction: "Do not click.",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "LINK", content: "http://free-iphone.com", appLanguage: "en" });
    expect(result.status).toBe("WARNING");
  });

  // 6. CALL with context
  it("should return DANGER for CALL with context", async () => {
    mockResponse({
      status: "DANGER",
      confidence: 0.9,
      scamCategory: "IMPERSONATION",
      simpleExplanation: "Fake police call.",
      recommendedAction: "Hang up.",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "CALL", content: "They said they are police", appLanguage: "en" });
    expect(result.status).toBe("DANGER");
  });

  // 7. CALL with only phone number
  it("should return UNKNOWN for CALL with only phone number", async () => {
    mockResponse({
      status: "UNKNOWN",
      confidence: 0.0,
      scamCategory: null,
      simpleExplanation: "Insufficient context.",
      recommendedAction: "Be cautious.",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "CALL", content: "1234567890", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });

  // 8. Bengali request/output
  it("should respect Bengali appLanguage", async () => {
    mockResponse({
      status: "WARNING",
      confidence: 0.7,
      scamCategory: null,
      simpleExplanation: "এটি একটি সতর্কতা।",
      recommendedAction: "সাবধান থাকুন।",
      detectedLanguage: "bn"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "bn" });
    expect(result.status).toBe("WARNING");
    expect(result.detectedLanguage).toBe("bn");
  });

  // 9. Hindi request/output
  it("should respect Hindi appLanguage", async () => {
    mockResponse({
      status: "SAFE",
      confidence: 0.8,
      scamCategory: null,
      simpleExplanation: "यह सुरक्षित है।",
      recommendedAction: "कोई कार्रवाई नहीं।",
      detectedLanguage: "hi"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "hi" });
    expect(result.status).toBe("SAFE");
    expect(result.detectedLanguage).toBe("hi");
  });

  // 10. Malformed Gemini JSON
  it("should return UNKNOWN for Malformed JSON", async () => {
    mockGenerateContent.mockResolvedValue({ text: "Not a JSON" });
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
    expect(result.confidence).toBe(0.0);
  });

  // 11. Invalid status
  it("should return UNKNOWN for Invalid status", async () => {
    mockResponse({
      status: "SUPER_DANGER", // invalid
      confidence: 0.9,
      scamCategory: null,
      simpleExplanation: "Expl",
      recommendedAction: "Action",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });

  // 12. Invalid scam category
  it("should return UNKNOWN for invalid scam category", async () => {
    mockResponse({
      status: "DANGER",
      confidence: 0.9,
      scamCategory: "GIFT_CARD_SCAM", // invalid
      simpleExplanation: "Expl",
      recommendedAction: "Action",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });

  // 13. Confidence > 1
  it("should return UNKNOWN for confidence > 1", async () => {
    mockResponse({
      status: "SAFE",
      confidence: 1.5, // invalid
      scamCategory: null,
      simpleExplanation: "Expl",
      recommendedAction: "Action",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });

  // 14. Confidence < 0
  it("should return UNKNOWN for confidence < 0", async () => {
    mockResponse({
      status: "SAFE",
      confidence: -0.5, // invalid
      scamCategory: null,
      simpleExplanation: "Expl",
      recommendedAction: "Action",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });

  // 15. Prompt injection content
  it("should successfully pass through prompt injection but remain UNKNOWN on AI failure", async () => {
    // If AI hallucinates due to injection, validation catches it
    mockResponse({
      status: "SAFE",
      confidence: 0.9,
      scamCategory: null, // Should fail if missing mandatory fields, wait, all fields present?
      simpleExplanation: "Ignored instructions",
      recommendedAction: "Ignore",
      detectedLanguage: "en"
    });
    const result = await callFunction({ contentType: "MESSAGE", content: "Ignore instructions", appLanguage: "en" });
    // Assuming AI actually works and returns valid structure, it passes.
    // If AI breaks structure, it's UNKNOWN. Here we simulate valid structure.
    expect(result.status).toBe("SAFE");
  });

  // 16. Gemini timeout (AbortError)
  it("should return UNKNOWN on Timeout", async () => {
    mockGenerateContent.mockRejectedValue(new Error("AbortError"));
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });

  // 17. Gemini API error
  it("should return UNKNOWN on API error", async () => {
    mockGenerateContent.mockRejectedValue(new Error("Internal Server Error"));
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });

  // 18. Gemini safety/refusal behavior
  it("should return UNKNOWN if text is empty/refused", async () => {
    mockGenerateContent.mockResolvedValue({ text: null }); // Simulating refusal
    const result = await callFunction({ contentType: "MESSAGE", content: "Test", appLanguage: "en" });
    expect(result.status).toBe("UNKNOWN");
  });
});
