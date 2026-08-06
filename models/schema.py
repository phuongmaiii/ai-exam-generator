# models/schema.py
from pydantic import BaseModel, Field
from typing import List

class SubQuestion(BaseModel):
    id: str = Field(description="Mã câu hỏi con. Ví dụ: '1a', '1b'")
    type: str = Field(description="Loại câu hỏi: 'preprocessing', 'computation', hoặc 'interpretation'")
    prompt: str = Field(description="Nội dung câu hỏi con")
    depends_on: str = Field(description="ID của câu hỏi trước nếu có phụ thuộc dữ liệu. Nếu không có, hãy điền chữ 'none'")
    answer: str = Field(description="Đáp án và lời giải thích chi tiết từng bước")
    points: float

class ExamQuestion(BaseModel):
    id: int
    topic: str
    context: str = Field(description="Đoạn tình huống hoặc dữ liệu chung cho cả chuỗi câu hỏi con")
    sub_questions: List[SubQuestion]

class GeneratedExam(BaseModel):
    title: str
    source_material: str = Field(description="Tên file tài liệu gốc")
    questions: List[ExamQuestion]
    total_points: float