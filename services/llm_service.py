import json
import os
from google import genai
from pydantic import BaseModel
from models.schema import GeneratedExam
from dotenv import load_dotenv

load_dotenv()
api_key = os.getenv("GEMINI_API_KEY")

# Khởi tạo client theo chuẩn mới
client = genai.Client(api_key=api_key)

async def generate_questions_with_llm(text: str, topic: str, num_questions: int, filename: str) -> GeneratedExam:
    system_prompt = """
    Bạn là một trợ giảng thiết kế đề thi AI/Machine Learning. Từ nội dung bài giảng được cung cấp,
    hãy tạo câu hỏi thi theo format "chained sub-questions"... (giữ nguyên prompt cũ của bạn)
    """
    
    user_prompt = f"CHỦ ĐỀ CẦN RA ĐỀ: {topic}\nSỐ CÂU LỚN YÊU CẦU: {num_questions}\nNỘI DUNG TÀI LIỆU GỐC:\n{text}"
    
    # Gọi API bằng thư viện mới
    response = client.models.generate_content(
        model='gemini-3.5-flash',
        contents=user_prompt,
        config=genai.types.GenerateContentConfig(
            system_instruction=system_prompt,
            response_mime_type="application/json",
            response_schema=GeneratedExam,
            temperature=0.2,
        ),
    )
    
    raw_text = response.text
    exam_data = json.loads(raw_text)
    
    exam_data["source_material"] = filename
    if "questions" not in exam_data or exam_data["questions"] is None:
        exam_data["questions"] = []
        
    exam_result = GeneratedExam(**exam_data)
    return exam_result