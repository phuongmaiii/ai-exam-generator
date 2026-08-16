import { useState, useEffect } from 'react';
import { Card, Descriptions, Button, message, Spin } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeftOutlined } from '@ant-design/icons';
import api from '../api';

export default function CandidateDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [candidate, setCandidate] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCandidateDetails = async () => {
      try {
        // Lưu ý: Đảm bảo Spring Boot của bạn có viết API GET /candidates/{id} nhé
        const res = await api.get(`/candidates/${id}`);
        setCandidate(res.data);
      } catch (error) {
        message.error('Không thể tải thông tin chi tiết ứng viên.');
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchCandidateDetails();
    }
  }, [id]);

  if (loading) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  return (
    <Card
      title="Hồ Sơ Chi Tiết Ứng Viên"
      style={{ maxWidth: 800, margin: '40px auto' }}
      extra={
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>
          Quay lại Bảng Match
        </Button>
      }
    >
      {candidate ? (
        <Descriptions bordered column={1}>
          <Descriptions.Item label="Mã Ứng Viên">{candidate.id}</Descriptions.Item>
          <Descriptions.Item label="Họ và Tên"><b>{candidate.fullName}</b></Descriptions.Item>
          <Descriptions.Item label="Email">{candidate.email}</Descriptions.Item>
          <Descriptions.Item label="Ngành nghề AI Phân Loại">
            {candidate.industry || 'Chưa phân loại'}
          </Descriptions.Item>
          <Descriptions.Item label="Vị trí AI Phân Loại">
            {candidate.position || 'Chưa phân loại'}
          </Descriptions.Item>
          <Descriptions.Item label="Nội dung CV (Đã trích xuất)">
            {/* Thẻ pre giúp giữ nguyên định dạng xuống dòng của text */}
            <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit' }}>
              {candidate.cvText || 'Không có dữ liệu CV'}
            </pre>
          </Descriptions.Item>
        </Descriptions>
      ) : (
        <p>Không tìm thấy dữ liệu ứng viên này.</p>
      )}
    </Card>
  );
}