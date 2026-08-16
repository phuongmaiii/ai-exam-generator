import { useState } from 'react';
import { Card, Form, Input, Button, message } from 'antd';
import api from '../api';
import { useNavigate } from 'react-router-dom';

export default function CreateJob() {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      // 1. Đóng gói dữ liệu gửi xuống Backend
      const jobData = {
        title: values.title,
        description: values.description,
        requirements: values.requirements,
        postedBy: 1, // Tạm thời hardcode ID người đăng
        status: 'OPEN'
      };
      
      // 2. Gọi API 1 lần duy nhất (Nhớ check lại backend của bạn dùng '/api/jobs' hay '/jobs')
      const res = await api.post('/jobs', jobData);
      
      // 3. Thông báo và điều hướng
      message.success('Đã đăng tin tuyển dụng thành công!');
      form.resetFields(); // Xóa trắng form (phòng trường hợp user back lại trang này)
      navigate(`/hr/matches/${res.data.id}`);
      
    } catch (error) {
      message.error('Có lỗi xảy ra khi tạo Job.');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="Tạo Mô Tả Công Việc Mới" style={{ maxWidth: 700, margin: '40px auto' }}>
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <Form.Item 
          label="Tiêu đề công việc" 
          name="title" 
          rules={[{ required: true, message: 'Vui lòng nhập tiêu đề!' }]}
        >
          <Input placeholder="VD: Data Analyst Intern" />
        </Form.Item>

        <Form.Item 
          label="Mô tả công việc" 
          name="description" 
          rules={[{ required: true, message: 'Vui lòng nhập mô tả công việc!' }]}
        >
          <Input.TextArea rows={4} placeholder="Nhập mô tả các đầu việc cần làm..." />
        </Form.Item>

        <Form.Item 
          label="Yêu cầu kỹ năng" 
          name="requirements" 
          rules={[{ required: true, message: 'Vui lòng nhập yêu cầu kỹ năng!' }]}
        >
          <Input.TextArea rows={4} placeholder="VD: Python, SQL, IELTS 6.5+,..." />
        </Form.Item>

        <Button type="primary" htmlType="submit" loading={loading} block>
          Lên Sóng Công Việc Này
        </Button>
      </Form>
    </Card>
  );
}