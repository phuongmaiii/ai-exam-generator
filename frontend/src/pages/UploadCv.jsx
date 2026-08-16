import { useState } from 'react';
import { Upload, Button, Form, Input, message, Card } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import api from '../api';
import { useNavigate } from 'react-router-dom';

export default function UploadCv() {
  const [fileList, setFileList] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values) => {
    if (fileList.length === 0) {
      message.error('Vui lòng chọn file CV');
      return;
    }

    const formData = new FormData();
    formData.append('file', fileList[0]);
    formData.append('fullName', values.fullName);
    formData.append('email', values.email);

    setLoading(true);
    try {
      // Gọi API 1 lần với đúng endpoint (bạn tự điều chỉnh lại '/api/candidates...' hoặc '/candidates...' cho khớp backend nhé)
      const res = await api.post('/candidates/upload-cv', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      
      // Thông báo thành công và chuyển trang
      message.success(`Đã lưu CV: ngành ${res.data.industry || 'N/A'}, vị trí ${res.data.position || 'N/A'}`);
      navigate(`/candidate/matches/${res.data.id}`);
      
    } catch (err) {
      message.error(err.response?.data?.error || 'Có lỗi xảy ra khi upload CV');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="Upload CV" style={{ maxWidth: 500, margin: '40px auto' }}>
      <Form layout="vertical" onFinish={onFinish}>
        <Form.Item label="Họ tên" name="fullName" rules={[{ required: true, message: 'Vui lòng nhập họ tên!' }]}>
          <Input />
        </Form.Item>
        <Form.Item label="Email" name="email" rules={[{ required: true, type: 'email', message: 'Vui lòng nhập email hợp lệ!' }]}>
          <Input />
        </Form.Item>
        <Form.Item label="File CV (PDF)">
          <Upload
            beforeUpload={(file) => { 
              setFileList([file]); 
              return false; // Ngăn chặn AntD tự động upload file
            }}
            maxCount={1}
            accept=".pdf"
            fileList={fileList}
            onRemove={() => setFileList([])}
          >
            <Button icon={<UploadOutlined />}>Chọn file</Button>
          </Upload>
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={loading} block>
          Tải lên
        </Button>
      </Form>
    </Card>
  );
}