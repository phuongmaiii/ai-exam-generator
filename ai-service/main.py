from fastapi import FastAPI,UploadFile, File, HTTPException
from schema import ClassifyRequest, ClassifyResponse, MatchRequest, MatchResponse
import random
import pdfplumber
import io

app = FastAPI(title="CV Recruitment AI Service")

@app.post("/classify/industry", response_model=ClassifyResponse)
def classify_industry(request: ClassifyRequest):
    return ClassifyResponse(predicted_label="INFORMATION-TECHNOLOGY", confidence=0.9429)

@app.post("/classify/position", response_model=ClassifyResponse)
def classify_position(request: ClassifyRequest):
    return ClassifyResponse(predicted_label="Backend Developer", confidence=0.88)

@app.post("/match-score", response_model=MatchResponse)
def calculate_match_score(request: MatchRequest):
    score = round(random.uniform(0.5, 0.99), 2)
    return MatchResponse(match_score=score, match_percent=score * 100)
@app.post("/parse-cv", tags=["Parsing"])
async def parse_cv(file: UploadFile = File(...)):
    if file.content_type != "application/pdf":
        raise HTTPException(status_code=422, detail="Chỉ chấp nhận file PDF")

    content = await file.read()
    if not content:
        raise HTTPException(status_code=422, detail="File rỗng")

    try:
        with pdfplumber.open(io.BytesIO(content)) as pdf:
            text = "\n".join(page.extract_text() or "" for page in pdf.pages)
    except Exception as e:
        raise HTTPException(status_code=422, detail=f"Không đọc được nội dung PDF: {e}")

    if not text.strip():
        raise HTTPException(status_code=422, detail="Không trích xuất được văn bản từ PDF")

    return {
        "text": text,
        "industry": "INFORMATION-TECHNOLOGY", 
        "industry_confidence": 0.95,
        "position": "Backend Developer",     
        "position_confidence": 0.88,
    }