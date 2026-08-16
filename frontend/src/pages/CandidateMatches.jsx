import { useParams } from 'react-router-dom';
import { Card, Typography } from 'antd';
import MatchTable from '../components/MatchTable';

const { Title } = Typography;

export default function CandidateMatches() {
  const { candidateId } = useParams();

  const columns = [
    { title: 'Vị trí', dataIndex: 'jobTitle' },
    { title: 'Công ty', dataIndex: 'companyName' },
  ];

  return (
    <Card style={{ maxWidth: 900, margin: '40px auto' }}>
      <Title level={3}>CV của bạn phù hợp với các vị trí sau</Title>
      <MatchTable apiPath={`/match/candidate/${candidateId}`} columnsConfig={columns} />
    </Card>
  );
}