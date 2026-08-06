import pdfplumber
import io

async def extract_text_from_pdf(file_bytes: bytes) -> str:
    text_content = ""
    with pdfplumber.open(io.BytesIO(file_bytes)) as pdf:
        for page in pdf.pages:
            page_text = page.extract_text()
            if page_text:
                text_content += page_text + "\n\n"
                
    cleaned_text = " ".join(text_content.split())
    return cleaned_text