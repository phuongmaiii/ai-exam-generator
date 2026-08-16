from pydantic import BaseModel, Field

class ClassifyRequest(BaseModel):
    text: str = Field(..., description="Nội dung CV (văn bản thuần)", min_length=1)

class ClassifyResponse(BaseModel):
    predicted_label: str
    confidence: float

class MatchRequest(BaseModel):
    cv_text: str
    jd_text: str

class MatchResponse(BaseModel):
    match_score: float
    match_percent: float