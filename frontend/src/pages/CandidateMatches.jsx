import { useParams, useNavigate } from 'react-router-dom';
import { Card, Typography, Button } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import MatchTable from '../components/MatchTable';

const { Title } = Typography;

export default function CandidateMatches() {
  const { candidateId } = useParams();
  const navigate = useNavigate(); // ✔️ ĐÚNG: Đặt bên trong function component

  const columns = [
    { title: 'Vị trí', dataIndex: 'jobTitle' },
    { title: 'Công ty', dataIndex: 'companyName' },
  ];

  return (
    <Card 
      title={`Đề xuất việc làm cho Ứng viên ID: ${candidateId}`} 
      style={{ margin: '40px auto', maxWidth: 900 }}
      extra={
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>
          Quay lại
        </Button>
      }
    >
      <MatchTable apiPath={`/match/candidate/${candidateId}`} columnsConfig={columns} />
    </Card>
  );
}