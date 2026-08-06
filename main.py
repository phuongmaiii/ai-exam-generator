from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from models.schema import GeneratedExam
from services.pdf_parser import extract_text_from_pdf
from services.llm_service import generate_questions_with_llm

#Khởi tạo
app= FastAPI(title="AI Exam Generator API")
@app.get("/")
def read_root():
    return {"message":"Backend đã sẵn sàng"}

#Endpoint sinh đề thi 
@app.post("/generate-exam",response_model=GeneratedExam)
async def generate_exam(
    file: UploadFile = File(...),
    topic: str = Form(...),
    num_questions:int = Form(3)
):
    if not file.filename.endswith(".pdf"):
        raise HTTPException(status_code=400, detail="Chỉ hỗ trợ file PDF.")
    
    file_bytes = await file.read()
    
    try:
        extracted_text = await extract_text_from_pdf(file_bytes)
        
        if len(extracted_text.strip()) < 50:
            raise HTTPException(status_code=400, detail="Không tìm thấy nội dung văn bản trong PDF.")
            
        exam_data = await generate_questions_with_llm(
            text=extracted_text, 
            topic=topic, 
            num_questions=num_questions,
            filename=file.filename
        )
        
        return exam_data
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Lỗi hệ thống: {str(e)}")
